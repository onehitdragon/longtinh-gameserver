package hero.map.clienthandler;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.map.Area;
import hero.map.Map;
import hero.map.message.ResponseAreaImage;
import hero.map.message.ResponseMapMiniImage;
import hero.map.service.AreaDict;
import hero.map.service.MapRelationDict;
import hero.map.service.MapServiceImpl;
import hero.map.service.MiniMapImageDict;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RequestMicroMap.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-27 上午10:00:12
 * @描述 ：请求小地图或区域地图
 */

public class RequestMicroMap extends AbsClientProcess
{
    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte imageType = yis.readByte();
            short mapID = yis.readShort();

            if (TYPE_OF_MICRO == imageType)
            {
                Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);

                if (null != map)
                {
                    byte[] miniMapImage = MiniMapImageDict.getInstance()
                            .getImageBytes(map.getMiniImageID());

                    if (null != miniMapImage)
                    {
                       /* OutMsgQ
                                .getInstance()
                                .put(
                                        player.getMsgQueueIndex(),
                                        new ResponseMapMiniImage(player
                                                .getLoginInfo().clientType, map
                                                .getMiniImageID(), miniMapImage));*/
                    }
                }
            }
            else if (TYPE_OF_AREA == imageType)
            {
                Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);

                Area area;

                if (null != map)
                {
                    area = map.getArea();
                }
                else
                {
                    short[] relation = MapRelationDict.getInstance()
                            .getRelationByMapID(mapID);

                    area = AreaDict.getInstance().getAreaByID(relation[4]);
                }

                if (null != area)
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseAreaImage(
                                    player.getLoginInfo().clientType, area));
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * 地图微缩地图
     */
    private static final byte TYPE_OF_MICRO = 0;

    /**
     * 区域微缩区域
     */
    private static final byte TYPE_OF_AREA  = 1;
}
