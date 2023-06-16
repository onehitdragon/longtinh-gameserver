package hero.player.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.effect.Effect;
import hero.effect.Effect.EKeepTimeType;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.StaticEffect;
import hero.effect.message.AddEffectNotify;
import hero.item.service.GoodsServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.player.HeroPlayer;
import hero.player.message.ReviveNotify;
import hero.player.service.PlayerServiceImpl;
import hero.share.EVocationType;
import hero.share.ME2GameObject;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AcceptRevive.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-17 上午09:36:46
 * @描述 ：接受复活
 */

public class AcceptRevive extends AbsClientProcess
{

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        try
        {
            HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

            byte reviverLocationX = yis.readByte();
            byte reviverLocationY = yis.readByte();
            int resumeHp = yis.readInt();
            int resumeMp = yis.readInt();

            PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(player);

            if (resumeHp < player.getActualProperty().getHpMax())
            {
                player.setHp(resumeHp);
            }

            if (player.getVocation().getType() == EVocationType.MAGIC)
            {
                if (resumeMp < player.getActualProperty().getMpMax())
                {
                    player.setMp(resumeMp);
                }
            }
            else
            {
                player.setForceQuantity(50);
            }

            player.setCellX(reviverLocationX);
            player.setCellY(reviverLocationY);

            //edit by zhengl; date: 2011-02-14; note: 每个职业都使用mp
            ReviveNotify msg = new ReviveNotify(
                    player.getID(),
                    player.getHp(),
                    player.getActualProperty().getHpMax(),
//                    player.getVocation().getType() == EVocationType.MAGIC ? player.getMp() : 50,
                    player.getMp(), 
//                    player.getVocation().getType() == EVocationType.MAGIC ? player.getActualProperty().getMpMax() : 100, 
                    player.getActualProperty().getMpMax(), 
                    reviverLocationX, reviverLocationY);

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

            MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg,
                    true, player.getID());

            PlayerServiceImpl.getInstance().refreshRoleProperty(player);
//            //add by zhengl; date: 2011-03-25; note: 消除客户端BUG而添加
//            DisappearNotify notify = new DisappearNotify(player.getObjectType().value(), player.getID());
//            MapSynchronousInfoBroadcast.getInstance().put(player.where(), notify,
//                    true, player.getID());
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
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }
    }
}
