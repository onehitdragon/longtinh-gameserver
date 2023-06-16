package hero.map.message;

import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.share.Constant;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseSceneElement.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-1 下午04:34:43
 * @描述 ：响应地图场景数据
 */

public class ResponseSceneElement extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(ResponseSceneElement.class);
    /**
     * 地图
     */
    private Map   map;

    /**
     * 客户端类型（高、中、低端）
     */
    private short clientType;

    /**
     * 地图的场景数据(也就是遮盖层,像房子,树之类的)
     * 
     * @param _map
     */
    public ResponseSceneElement(short _clientType, Map _map)
    {
        clientType = _clientType;
        map = _map;
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
        yos.writeByte(map.elementImageIDList.size());
        log.info("map.elementImageIDList:" + map.elementImageIDList.size());
        for (short imageID : map.elementImageIDList)
        {
        	if(map.getID() == 120){
        		
        		log.info("120 map 图片ID:" + imageID);
        	}
            yos.writeShort(imageID);

            if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
            {
                byte[] imageBytes = MapServiceImpl.getInstance()
                        .getElementImageBytes(imageID);
                yos.writeShort(imageBytes.length);
                yos.writeBytes(imageBytes);
            }
        }

        short[] elementCanvasData = map.elementCanvasData;
        log.debug("mapid="+map.getID()+",name="+map.getName()+"，map.elementCanvasData="+map.elementCanvasData);
        yos.writeShort(elementCanvasData.length / 3); //x,y,遮盖层图片数量 刚好是3种东西,所以刚好可被3整除
        //edit by zhengl ; date: 2010-12-17 ; note: byte数据类型已经不够用了
//        output.writeBytes(elementCanvasData);
        for (int i = 0; i < elementCanvasData.length; i++) {
        	yos.writeShort(elementCanvasData[i]);
		}
        
        if(Map.IS_NEW_MAP){//遮盖层旋转角度
            log.debug("mapid="+map.getID()+",name="+map.getName()+",map.transformData2="+map.transformData2);
            if(null != map.transformData2){
                yos.writeShort(map.transformData2.length);
                yos.writeBytes(map.transformData2);
            }
        	//del:	zhengl
        	//date:	2010-10-27
        	//note:	不需要for,所以删除
//        	for(int i=0; i<map.transformData2.length; i++){
//        		output.writeByte(map.transformData2[i]);
//        	}
        	//end
        }
    }
}
