package hero.map.message;

import hero.map.Area;
import hero.map.Map;
import hero.share.Constant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import yoyo.core.packet.AbsResponseMessage;

import javolution.util.FastList;
import javolution.util.FastMap;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseAreaImage.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-22 下午06:12:26
 * @描述 ：
 */

public class ResponseAreaImage extends AbsResponseMessage
{
    /**
     * 客户端类型（高、中、低端）
     */
    private short clientType;

    /**
     * 区域
     */
    private Area  area;

    /**
     * 构造
     * 
     * @param _area
     */
    public ResponseAreaImage(short _clientType, Area _area)
    {
        clientType = _clientType;
        area = _area;
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
        yos.writeUTF(area.getName());

        if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
        {
            byte[] imageBytes = area.getImageBytes();

            yos.writeShort(imageBytes.length);
            yos.writeBytes(imageBytes);
        }
        else
        {
            yos.writeShort(area.getID());
        }

        FastList<Map> visibleMapTable = area.getVisibleMapList();

        yos.writeByte(visibleMapTable.size());

        if (0 < visibleMapTable.size())
        {
            for (Map map : visibleMapTable)
            {
                yos.writeShort(area.getVisibleMapTable().get(map)[0]);
                yos.writeShort(area.getVisibleMapTable().get(map)[1]);
                yos.writeShort(map.getID());
                yos.writeUTF(map.getName());
            }
        }
    }

}
