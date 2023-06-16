package hero.map.message;

import hero.map.Decorater;
import hero.map.Map;
import hero.map.clienthandler.EnterGame;
import hero.map.detail.OtherObjectData;
import hero.map.service.*;
import hero.player.HeroPlayer;
import hero.share.Constant;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseMapBottomData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-23 上午10:46:55
 * @描述 ：
 */

public class ResponseMapBottomData extends AbsResponseMessage
{
	private static Logger log = Logger.getLogger(ResponseMapBottomData.class);
    /**
     * 地图
     */
    private Map        map;

    /**
     * 从哪张地图过来
     */
    private Map        lastMap;

    /**
     * 进入地图的角色
     */
    private HeroPlayer player;

    public ResponseMapBottomData(HeroPlayer _player, Map _map, Map _lastMap)
    {
        player = _player;
        map = _map;
        lastMap = _lastMap;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeShort(map.getID());
        yos.writeByte(MapServiceImpl.getInstance().getPlayerMapWorldType(map.getID(),player));
        yos.writeShort(map.getMiniImageID());

        if (Constant.CLIENT_OF_HIGH_SIDE != player.getLoginInfo().clientType)
        {
            if (null == lastMap || map.getTileID() != lastMap.getTileID())
            {
                yos.writeByte(1);

                char[] tileChars = MapTileDict.getInstance().getMapTileChars(
                        map.getTileID());
                yos.writeShort(tileChars.length);
                yos.writeChars(tileChars);
            }
            else
            {
                yos.writeByte(0);
            }
        }
        else
        {
            yos.writeShort(map.getTileID());
        }

        int[] weatherDesc = WeatherManager.getInstance().getWeather(
                map.getWeather());

        yos.writeByte(weatherDesc[0]);
        yos.writeByte(weatherDesc[1]);
        yos.writeUTF(map.getName());
        yos.writeByte(map.getWidth());
        yos.writeByte(map.getHeight());
        yos.writeByte(map.getPKMark());
        yos.writeByte(map.isModifiable() ? 1 : 0);

        yos.writeByte(player.getCellX());
        yos.writeByte(player.getCellY());

        byte[] bottomCanvasData = map.bottomCanvasData;
        //edit:	zhengl
        //date:	2010-11-08
        //note:	把旋转数据和地图贴片数据集合在一起传输
        int len = bottomCanvasData.length;
        yos.writeShort(len);
        for(int i = 0; i < len; i++) {
        	yos.writeByte(bottomCanvasData[i]);
        	yos.writeByte(map.transformData1[i]);
        }
//        output.writeBytes(bottomCanvasData);
        //end

        yos.writeByte(map.doorList.length);

        for (int i = 0; i < map.doorList.length; i++)
        {
            yos.writeByte(map.doorList[i].x);
            yos.writeByte(map.doorList[i].y);
            yos.writeByte(map.doorList[i].direction);
            yos.writeShort(map.doorList[i].targetMapID);
            yos.writeUTF(map.doorList[i].targetMapName);
            yos.writeByte(map.doorList[i].visible);
        }

        yos.writeByte((byte) map.internalTransportList.length);
        for (int i = 0; i < map.internalTransportList.length; i++)
        {
            yos.writeByte(map.internalTransportList[i][0]);
            yos.writeByte(map.internalTransportList[i][1]);
            yos.writeByte(map.internalTransportList[i][2]);
            yos.writeByte(map.internalTransportList[i][3]);
        }

        yos.writeByte((byte) map.popMessageList.length);
        for (int i = 0; i < map.popMessageList.length; i++)
        {
            yos.writeByte(map.popMessageList[i].x);
            yos.writeByte(map.popMessageList[i].y);
            yos.writeUTF(map.popMessageList[i].msgContent);
        }

        if (map.cartoonList != null)
        {
            yos.writeByte(map.cartoonList.length);

            for (int i = 0; i < map.cartoonList.length; i++)
            {
                yos.writeByte(map.cartoonList[i].firstTileIndex);
                yos.writeByte(map.cartoonList[i].followTileIndexList.length);
            }
        }
        else
        {
            yos.writeByte(0);
        }

        yos.writeShort(map.unpassData.length / 2);
        if (map.unpassData.length > 0)
        {
            yos.writeBytes(map.unpassData);
        }

        yos.writeShort(null == map.getArea() ? 0 : map.getArea().getID());
        yos.writeUTF(null == map.getArea() ? "" : map.getArea().getName());
        byte musicId = MapMusicDict.getInstance().getMapMusicID(map.getID());
        yos.writeByte(musicId);
        
        //del by zhengl; date: 2010-11-08; note:透传该层旋转信息的地方改了,不再这样写
//        if(Map.IS_NEW_MAP){
//        	output.writeShort(map.transformData1.length);
//        	log.info("map.transformData1.length" + map.transformData1.length);
//        	output.writeBytes(map.transformData1);      	
//        }
        //end
        
        log.info("output size = " + String.valueOf(yos.size()));
    }

}
