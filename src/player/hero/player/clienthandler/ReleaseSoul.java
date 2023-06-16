package hero.player.clienthandler;

import hero.dungeon.Dungeon;
import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.Effect;
import hero.effect.Effect.EKeepTimeType;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.StaticEffect;
import hero.effect.message.AddEffectNotify;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.MpRefreshNotify;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.fight.service.SpecialStatusDefine;
import hero.group.service.GroupServiceImpl;
import hero.item.service.GoodsServiceImpl;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.player.service.PlayerServiceImpl;
import hero.share.EObjectLevel;
import hero.share.EVocationType;
import hero.share.ME2GameObject;
import hero.share.service.LogWriter;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ReleaseSoul.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-14 下午04:48:36
 * @描述 ：
 */

public class ReleaseSoul extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(ReleaseSoul.class);
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        //add by zhengl; date: 2011-05-16; note: 刷新角色属性
        PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
        PlayerServiceImpl.getInstance().refreshRoleProperty(player);
        //edit by zhengl; date: 2011-02-24; note: 添加系数进HP计算公式
        player.setHp(
        		CEService.hpByStamina(
        				CEService.playerBaseAttribute(
        						player.getLevel(), 
        						player.getVocation().getStaminaCalPara()), 
        		player.getLevel(), 
        		player.getObjectLevel().getHpCalPara()));
        //edit by zhengl; date: 2011-02-15; note: 所有职业都使用mp进行战斗
//        if (player.getVocation().getType() == EVocationType.PHYSICS)
//        {
//            player.setForceQuantity(50);
//        }
//        else
//        {
//            player.setMp(CEService.mpByInte(CEService.playerBaseAttribute(
//                    player.getLevel(), player.getVocation().getInteCalcPara()),
//                    player.getLevel(), EObjectLevel.NORMAL.getMpCalPara()));
//        }
        player.setMp(CEService.mpByInte(CEService.playerBaseAttribute(
                player.getLevel(), player.getVocation().getInteCalcPara()),
                player.getLevel(), EObjectLevel.NORMAL.getMpCalPara()));
        //end

        Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(
                player.where().getTargetMapIDAfterDie());

        log.debug("currmapid = " + player.where().getID()+",targetmapid="+player.where().getTargetMapIDAfterDie());
        //如果玩家是魔族，则使用魔族地图 --add by jiaodj 2011-05-16
        if(player.getClan() == EClan.HE_MU_DU){
            short mapid = player.where().getMozuTargetMapIDAfterDie();
            log.debug("魔族 mapid = " + mapid);
            targetMap = MapServiceImpl.getInstance().getNormalMapByID(mapid);
        }

        player.setCellX(targetMap.getBornX());
        player.setCellY(targetMap.getBornY());

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new ResponseMapBottomData(player, targetMap, player.where()));

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseMapGameObjectList(player.getLoginInfo().clientType,
                        targetMap));

        Map currentMap = player.where();

        player.gotoMap(targetMap);
        //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
        EffectServiceImpl.getInstance().sendEffectList(player, targetMap);
        if (EMapType.DUNGEON == currentMap.getMapType())
        {
            DungeonServiceImpl.getInstance().playerLeftDungeon(player);
        }

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new SpecialStatusChangeNotify(player.getObjectType().value(),
                        player.getID(), SpecialStatusDefine.REVIVAL));
        ResponseMessageQueue.getInstance()
                .put(
                        player.getMsgQueueIndex(),
                        new HpRefreshNotify(player.getObjectType().value(),
                                player.getID(), player.getHp(), player.getHp(),
                                false, false));
        //edit by zhengl; date: 2011-03-03; note: 修改BUG,mp按实际值下发
        ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),
                        new MpRefreshNotify(
                                player.getObjectType().value(),
                                player.getID(),
                                player.getMp(), false));

        GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);
        
//        //add by zhengl; date: 2011-03-25; note: 消除客户端BUG而添加
//        DisappearNotify notify = new DisappearNotify(player.getObjectType().value(), player.getID());
//        MapSynchronousInfoBroadcast.getInstance().put(player.where(), notify,
//                true, player.getID());
        //add by zhengl; date: 2011-05-15; note: 修复复活后双倍经验显示消失问题.
        if (player.getChargeInfo().huntBookTimeTotal > 0) 
        {
            StaticEffect sef = new StaticEffect(1, "双倍经验");
            sef.desc = "双倍经验";
            sef.releaser = (ME2GameObject)player;
            sef.trait = EffectTrait.BUFF;
            sef.keepTimeType = EKeepTimeType.LIMITED;
            sef.traceTime = (short)(player.getChargeInfo().huntBookTimeTotal/1000);
            sef.iconID = 
            	GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_icon;
            sef.viewType = 0;
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            		new AddEffectNotify(player, (Effect)sef));
		}
        player.revive(null);
    }
}
