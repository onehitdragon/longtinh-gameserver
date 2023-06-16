package hero.map.clienthandler;

import hero.charge.message.SendChargeList;
import hero.charge.service.ChargeServiceImpl;
import hero.effect.message.MoveSpeedChangerNotify;
import hero.effect.service.EffectServiceImpl;
import hero.item.message.SendBodyWearList;
import hero.log.service.LogServiceImpl;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.message.NotifyEnterMap;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapRelationDict;
import hero.map.service.MapServiceImpl;
import hero.micro.store.PersionalStore;
import hero.micro.store.StoreService;
import hero.novice.service.NoviceServiceImpl;
import hero.pet.message.ResponseWearPetGridNumber;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import hero.player.message.ResponseRoleViewInfo;
import hero.share.Direction;
import hero.share.service.ME2ObjectList;

import hero.share.service.ShareServiceImpl;
import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.session.SessionServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RequestEnterGame.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-5 上午11:43:44
 * @描述 ：进入游戏
 */

public class EnterGame extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(EnterGame.class);
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void read () throws Exception
    {
        // 触发SessionCreate方法

        SessionServiceImpl.getInstance().initSession(
                SessionServiceImpl.getInstance().getSession(
                        contextData.sessionID));

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        log.info("#### player name["+player.getName()+"] login....");

        PlayerServiceImpl.getInstance().initProperty(player);

        byte clientType = 0;

        try
        {
            clientType = yis.readByte();
            player.getLoginInfo().clientType = clientType;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Map where;

        if (player.where().getMapType() == EMapType.DUNGEON)
        {
            if (player.getGroupID() == 0)
            {
                short[] relations = MapRelationDict.getInstance().getRelationByMapID(player.where().getID());
                short targetmapid = relations[3];

                if(player.getClan() == EClan.HE_MU_DU && relations[9] > 0){//如果是魔族，则跳转到魔族的回城地图
                    targetmapid = relations[9];
                }
                log.debug("relation map id = " + targetmapid);
                where = MapServiceImpl.getInstance().getNormalMapByID(targetmapid);

                player.live(where);
                player.setCellX(where.getBornX());
                player.setCellY(where.getBornY());
            }
        }

        where = player.where();
        log.info("player entry game where = " + where.getName() );
        //edit by zhengl; date: 2011-02-22; note: 修改为比较合理的方式
//        if( (!PlayerServiceImpl.getInstance().getConfig().useNovice())
//                && where.getID() == NoviceServiceImpl.getInstance().getNoviceMapID() ) {
//            log.debug("no use novice ..." +PlayerServiceImpl.getInstance().getInitBornMapID(
//                    player.getClan()));
//           
//            where = MapServiceImpl.getInstance().getNormalMapByID(
//                PlayerServiceImpl.getInstance().getInitBornMapID(
//                        player.getClan()));
////            PetServiceImpl.getInstance().addPetEgg(player); // 给新玩家加宠物蛋
//
//            NoviceServiceImpl.getInstance().addGoodsForNewPlayer(player); // 给新玩家的新手奖励
//            log.debug("player born map = " + where);
//            player.live(where);
//            PlayerServiceImpl.getInstance().dbUpdate(player);//这里需要更新数据库，保存玩家的where_id,否则，如果服务器直接关闭，玩家的where_id还是新手教程ID
//                                                             // 玩家重新登录就会重新给玩家添加测试物品
//        }
        if( PlayerServiceImpl.getInstance().getNovice(player) )
        {
        	NoviceServiceImpl.getInstance().novicePlayerAward(player);
        }
        //end
        log.info("player entry game mapID = " + where.getID());
        player.setDirection(Direction.DOWN);

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new NotifyEnterMap());
        log.info("player login send notify enter map message.");
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new ResponseMapBottomData(player, where, null));
        log.info("player login send response map bottom data ...");
        //edit by zhengl; date: 2011-02-22; note: 合理化代码结构
        ResponseMessageQueue.getInstance().put( player.getMsgQueueIndex(), new ResponseRoleViewInfo(player) );
//        OutMsgQ.getInstance().put(
//                player.getMsgQueueIndex(),
//                new ResponseRoleViewInfo(
//                		player,
//                		where.getID() == NoviceServiceImpl.getInstance().getNoviceMapID() ? true : false
//                				)
//                );
        //end

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new SendBodyWearList(player.getBodyWear(),player));
        log.info("player login send body wear list message...");
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        		new ResponseWearPetGridNumber(player.getBodyWearPetList()));
        log.info("player login send response wear pet grid number message...");
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new ResponseMapGameObjectList(player.getLoginInfo().clientType, where));
        log.info("player login send response map game object list ..");
        PlayerServiceImpl.getInstance().refreshRoleProperty(player);
        log.info("player login refresh role property ...");
        if (where.getID() != NoviceServiceImpl.getInstance().getNoviceMapID())
        {
            player.born(where);
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new MoveSpeedChangerNotify(
                		player.getObjectType().value(), player.getID(), player.getMoveSpeed()));
        log.info("player send move speed changer notify message ...");
        player.active();
        //del by zhengl; date: 2011-05-04; note: 先删除进地图的时候BUFF加载.
//        EffectServiceImpl.getInstance().sendEffectList(player, player.where());
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        		new SendChargeList(ChargeServiceImpl.getInstance().getFpListForRecharge(), 
        				ChargeServiceImpl.getInstance().getFeeTypeListForRecharge()));
        log.info("player login send charge list message ....");

        LogServiceImpl.getInstance().roleLoginLog(
        					player.getLoginInfo().accountID,
                            player.getUserID(), player.getName(),
                            player.getLoginInfo().loginMsisdn,
                            player.getLoginInfo().userAgent,
                            player.getLoginInfo().clientVersion,
                            player.getLoginInfo().clientType,
                            player.getLoginInfo().communicatePipe,
                            System.currentTimeMillis(),
                            player.getLoginInfo().publisher,
                            player.where().getID(),
                            player.where().getName(),
                            getIp());
        log.info("player login save loginlog end ...");
    }
}
