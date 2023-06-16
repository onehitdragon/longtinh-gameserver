package hero.map.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DoorOpenedNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-8 下午04:52:08
 * @描述 ：通往其他地图的传送点打开通知
 */

public class DoorOpenedNotify extends AbsResponseMessage
{
    /**
     * 当前地图编号
     */
    private short currentMapID;

    /**
     * 目标地图编号
     */
    private short targetMapID;

    /**
     * 构造
     * 
     * @param _currentMapID
     * @param _targetMapID
     */
    public DoorOpenedNotify(short _currentMapID, short _targetMapID)
    {
        currentMapID = _currentMapID;
        targetMapID = _targetMapID;
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
        yos.writeShort(currentMapID);
        yos.writeShort(targetMapID);
    }

}
