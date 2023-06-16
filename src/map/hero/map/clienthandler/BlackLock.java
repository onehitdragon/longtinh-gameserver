package hero.map.clienthandler;

import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

public class BlackLock extends AbsClientProcess {
    private static Logger log = Logger.getLogger(BlackLock.class);
	@Override
	public void read() throws Exception {
		// TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		short where = yis.readShort(); 
		
    	Map currentMap = player.where();
    	Map targetMap = null;
    	if(currentMap.getID() != where) {
    		log.info("warn:客户端记录自己所在地图和服务器记录玩家所在地图不符:");
    		log.info("client in map=" + where);
    		log.info("server in map=" + currentMap.getID());
    	}
    	if( MapServiceImpl.getInstance().getConfig().use_default_map ) {
    		if(player.getClan() == EClan.LONG_SHAN) {
        		targetMap = MapServiceImpl.getInstance().getNormalMapByID(
        				MapServiceImpl.getInstance().getConfig().break_lock_default_long_map);
    		} else {
        		targetMap = MapServiceImpl.getInstance().getNormalMapByID(
        				MapServiceImpl.getInstance().getConfig().break_lock_default_mo_map);
			}

    	} else {

            //如果玩家是魔族，则使用魔族地图 --add by jiaodj 2011-05-16
            if(player.getClan() == EClan.LONG_SHAN){
                targetMap = MapServiceImpl.getInstance().getNormalMapByID(
                        currentMap.getTargetMapIDAfterDie());
            }
            if(player.getClan() == EClan.HE_MU_DU){
                short mapid = currentMap.getMozuTargetMapIDAfterDie();
                log.debug("curr mapid="+mapid+"，魔族mapid="+mapid);
                targetMap = MapServiceImpl.getInstance().getNormalMapByID(mapid);
            }
		}
    	if(targetMap != null) {
        	player.setCellX(currentMap.getBornX());
        	player.setCellY(currentMap.getBornY());
        	
            ResponseMessageQueue.getInstance().put(
            		player.getMsgQueueIndex(),
                    new ResponseMapBottomData(player, currentMap, currentMap));
            ResponseMessageQueue.getInstance().put(
            		player.getMsgQueueIndex(),
                    new ResponseMapGameObjectList(
                    		player.getLoginInfo().clientType, currentMap));
            player.gotoMap(currentMap);
    	}

	}

}
