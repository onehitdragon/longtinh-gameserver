package hero.map.clienthandler;

import hero.dungeon.Dungeon;
import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.SwitchMapFailNotify;
import hero.map.service.MapServiceImpl;
import hero.npc.Npc;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SwitchMap.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-27 上午10:00:12
 * @描述 ：切换地图
 */

public class SwitchMap extends AbsClientProcess {
	private static Logger log = Logger.getLogger(SwitchMap.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see me2.core.handler.ClientHandler#read()
	 */
	public void read() throws Exception {

		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(
				contextData.sessionID);

		if (!player.isEnable() || player.isDead()) {
			return;
		}
        if(player.isSelling()){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_EXCHANGEING_NOT_SWITCHMAP));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new SwitchMapFailNotify(Tip.TIP_EXCHANGEING_NOT_SWITCHMAP));
            return;
        }
		log.debug("switch map start ... playe current map id = " + player.where().getID());
		short targetMapID = yis.readShort();
		short switchX = yis.readShort();
		short switchY = yis.readShort();
		log.debug("switch target mapID = " + targetMapID);
		Map currentMap = player.where();
		Map targetMap = null;
		targetMap = MapServiceImpl.getInstance().getNormalMapByID(targetMapID);
		if (targetMap.getMapType() == EMapType.DUNGEON) {
			//如果是副本那么将重新获得副本
			//提示:当该地图是副本的时候,那么用户必然已经进入副本,已经有了相应的副本进度.
			Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(player.getUserID());

			if (null != dungeon) {
				targetMap = dungeon.getMap(targetMapID);
			}
		}

		if (null == targetMap) {
			log.debug(Tip.TIP_MAP_OF_NONE_EXISTS + targetMapID);

			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
					new SwitchMapFailNotify(Tip.TIP_MAP_OF_NONE_EXISTS + targetMapID));
			return;
		}
		if (currentMap.getID() == targetMap.getID()) {
			log.debug(Tip.TIP_MAP_OF_SAME_HEADER + currentMap.getName() + Tip.TIP_MAP_OF_SAME_ENDER
					+ targetMap.getName());
			ResponseMessageQueue.getInstance().put(
					player.getMsgQueueIndex(),
					new SwitchMapFailNotify(Tip.TIP_MAP_OF_SAME_HEADER + currentMap.getName()
							+ Tip.TIP_MAP_OF_SAME_ENDER + targetMap.getName()));
			return;
		}

		switchMap(player, currentMap, targetMap, switchX, switchY);
//		if (targetMap.getMapType() == EMapType.GENERIC) {
//
//		} else if (targetMap.getMapType() == EMapType.DUNGEON) {
//			// add by zhengl; date: 2011-03-03; note:
//			// 修改玩家切换副本地图的时候的BUG以及不在同组却在同一地图的BUG
//			Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(player.getUserID());
//			if (null != dungeon) {
//				// edit by zhengl; date: 2011-04-01; note: 修改进入副本地图的BUG
//				Map oldTargetMap = dungeon.getMap(targetMapID);
//				if (oldTargetMap == null) {
//					// oldTargetMap为空 证明玩家已有进度但是没有当前副本的进度.
//					// 接下来将使用默认副本地图作为地图传递进构造方法从而创建新的地图
//					OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//							new SwitchMapFailNotify(Tip.TIP_MAP_OF_DUNGEON_FAIL));
//				} else {
//					targetMap = oldTargetMap;
//				}
//			}
//			// end
//			short[] targetMapBornPort = currentMap.getTargetMapPoint(targetMap.getID(), switchX,
//					switchY);
//			log.debug("switch goto gungeon map id = " + targetMap.getID());
//			if (currentMap.getMapType() == EMapType.GENERIC) { // 从普通地图进入副本地图
//				log.debug("from generic map to gungeon...");
//				DungeonServiceImpl.getInstance().canGotoDungeonMap(player, targetMap,
//						targetMapBornPort[0], targetMapBornPort[1]);
//			} else if (currentMap.getMapType() == EMapType.DUNGEON) {// 副本地图间的跳转
//				log.debug("bwtten dungeon map switch ...");
//				switchMap(player, currentMap, targetMap, switchX, switchY);
//			}
//		}

		LogServiceImpl.getInstance().switchMapLog(player.getLoginInfo().accountID,
				player.getLoginInfo().username, player.getUserID(), player.getName(),
				currentMap.getID(), currentMap.getName(), targetMap.getID(), targetMap.getName(),
				currentMap.getMapType().name(), targetMap.getMapType().name());
		log.info("switch map end ...");
	}

	private void switchMap(HeroPlayer player, Map currentMap, Map targetMap, short switchX,
			short switchY) {
		short[] targetMapBornPort = currentMap.getTargetMapPoint(targetMap.getID(), switchX,
				switchY);

		if (targetMapBornPort != null) {
			player.setCellX(targetMapBornPort[0]);
			player.setCellY(targetMapBornPort[1]);
		} else {
			// LogWriter.println(TIP_OF_NONE_BORN_HEADER + currentMap.getID()
			// + TIP_OF_NONE_BORN_ENDER + targetMapID);
			log.debug(Tip.TIP_MAP_OF_NONE_BORN_HEADER + currentMap.getID()
					+ Tip.TIP_MAP_OF_NONE_BORN_ENDER + targetMap.getID());

			ResponseMessageQueue.getInstance().put(
					player.getMsgQueueIndex(),
					new SwitchMapFailNotify(Tip.TIP_MAP_OF_NONE_BORN_HEADER + currentMap.getID()
							+ Tip.TIP_MAP_OF_NONE_BORN_ENDER + targetMap.getID()));
			return;
		}

		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
				new ResponseMapBottomData(player, targetMap, currentMap));

		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
				new ResponseMapGameObjectList(player.getLoginInfo().clientType, targetMap));
		log.debug("next player gotoMap id = " + targetMap.getID());
		player.gotoMap(targetMap);
		// add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
		EffectServiceImpl.getInstance().sendEffectList(player, targetMap);
		if (EMapType.DUNGEON == currentMap.getMapType()
				&& EMapType.GENERIC == targetMap.getMapType()) {
			DungeonServiceImpl.getInstance().playerLeftDungeon(player);
		}

		Npc escortNpc = player.getEscortTarget();

		if (null != escortNpc) {
			escortNpc.setCellX(player.getCellX());
			escortNpc.setCellY(player.getCellY());
			escortNpc.gotoMap(targetMap);
		}
	}

}
