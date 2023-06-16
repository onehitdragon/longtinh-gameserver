package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.effect.Effect;
import hero.effect.Effect.EKeepTimeType;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.StaticEffect;
import hero.effect.message.AddEffectNotify;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.MpRefreshNotify;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.fight.service.SpecialStatusDefine;
import hero.group.service.GroupServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.ReviveStone;
import hero.log.service.CauseLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.player.HeroPlayer;
import hero.player.message.ReviveNotify;
import hero.player.service.PlayerServiceImpl;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.Tip;

public class ItemRevive extends AbsClientProcess {

	@Override
	public void read() throws Exception {
		// TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(
        		contextData.sessionID);
        //add by zhengl; date: 2011-05-16; note: 刷新玩家属性
        PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
        PlayerServiceImpl.getInstance().refreshRoleProperty(player);
		int count = player.getInventory().getSpecialGoodsBag().getGoodsNumber(
				ReviveStone.REVIVE_STONE_ID);
		if(count < 1) {
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_NOT_HAVE_REVIVE, 
							Warning.UI_TOOLTIP_AND_EVENT_TIP, 
							Warning.SUBFUNCTION_UI_POPUP_REVIVE_CHARGE));
		} else {
			ReviveStone stone = (ReviveStone) GoodsContents.getGoods(ReviveStone.REVIVE_STONE_ID);

	        player.setHp(player.getActualProperty().getHpMax());
	        player.setMp(player.getActualProperty().getMpMax());
	        ResponseMessageQueue.getInstance().put(
	                player.getMsgQueueIndex(),
	                new SpecialStatusChangeNotify(player.getObjectType().value(),
	                        player.getID(), SpecialStatusDefine.REVIVAL));
	        ResponseMessageQueue.getInstance().put(
                    player.getMsgQueueIndex(),
                    new HpRefreshNotify(player.getObjectType().value(),
                            player.getID(), player.getHp(), player.getHp(),
                            false, false));
	        ResponseMessageQueue.getInstance().put(
                    player.getMsgQueueIndex(),
                    new MpRefreshNotify(
                            player.getObjectType().value(),
                            player.getID(),
                            player.getMp(), false));
	        
            ReviveNotify msg = new ReviveNotify(
                    player.getID(),
                    player.getHp(),
                    player.getActualProperty().getHpMax(),
                    player.getMp(),  
                    player.getActualProperty().getMpMax(), 
                    (byte)player.getCellX(), (byte)player.getCellY());

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

            MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg,
                    true, player.getID());

	        GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);
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
//            //add by zhengl; date: 2011-03-25; note: 消除客户端BUG而添加
//            DisappearNotify notify = new DisappearNotify(player.getObjectType().value(), player.getID());
//            MapSynchronousInfoBroadcast.getInstance().put(player.where(), notify,
//                    true, player.getID());
			GoodsServiceImpl.getInstance().deleteSingleGoods(
					player,
                    player.getInventory().getSpecialGoodsBag(),
                    stone, 1,
                    CauseLog.USE);
		}
	}

}
