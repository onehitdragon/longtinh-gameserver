package hero.map.clienthandler;

import hero.item.service.GoodsServiceImpl;
import hero.map.Map;
import hero.map.message.ResponseAnimalInfoList;
import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseMapDecorateData;
import hero.map.message.ResponseMapElementList;
import hero.map.message.ResponseMapMiniImage;
import hero.map.message.ResponsePetInfoList;
import hero.map.message.ResponseSceneElement;
import hero.map.service.MiniMapImageDict;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.task.service.TaskServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RequestMapTraceInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-24 下午05:36:50
 * @描述 ：请求地图其他信息
 */

public class RequestMapTraceInfo extends AbsClientProcess
{
     private static Logger log = Logger.getLogger(RequestMapTraceInfo.class);
    @Override
    public void read () throws Exception
    {
        log.info("@@@@@@@ RequestMapTraceInfo ...........");
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        Map where = player.where();

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseSceneElement(player.getLoginInfo().clientType,
                        where));
        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseMapElementList(player.getLoginInfo().clientType,
                        where));
        /*OutMsgQ.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseMapMiniImage(player.getLoginInfo().clientType,
                        where.getMiniImageID(), MiniMapImageDict.getInstance()
                                .getImageBytes(where.getMiniImageID())));*/
        if(Map.IS_NEW_MAP)
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            		new ResponseMapDecorateData(where, player.getLoginInfo().clientType));

        if (where.getAnimalList().size() > 0)
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResponseAnimalInfoList(where));
           
        }

        if (where.getBoxList().size() > 0)
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResponseBoxList(where.getBoxList()));
        }

        GoodsServiceImpl.getInstance().sendLegacyBoxList(where, player);
        TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player, where);
        TaskServiceImpl.getInstance().notifyMapGearOperateMark(player, where);
        TaskServiceImpl.getInstance().notifyGroundTaskGoodsOperateMark(player,
                where);
        
//        OutMsgQ.getInstance().put(player.getMsgQueueIndex(), new ResponsePetInfoList(player));
    }

}
