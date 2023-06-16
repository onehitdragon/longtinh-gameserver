/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 */

package hero.player.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class OtherWalkNotify extends AbsResponseMessage
{
    /**
     * 内存临时编号
     */
    private int    objectID;

    /**
     * 行走路径
     */
    private byte[] path;

    /**
     * 行走速度
     */
    private byte   speed;
    
    private byte   endX;
    
    private byte   endY;

    /**
     * @param _walkerID
     * @param _speed
     * @param _path
     */
    public OtherWalkNotify(int _walkerID, byte _speed, byte[] _path, byte _endX, byte _endY)
    {
        objectID = _walkerID;
        speed = _speed;
        path = _path;
        endX = _endX;
        endY = _endY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.data.ResultMessage#getPriority()
     */
    @Override
    public int getPriority ()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.data.ResultMessage#write()
     */
    @Override
    protected void write () throws IOException
    {
        yos.writeInt(objectID);
        yos.writeByte(speed);
        yos.writeByte(path.length);
        yos.writeBytes(path);
        //add by zhengl; date: 2011-03-01; note:添加x,y最终移动的坐标
        yos.writeByte(endX);
        yos.writeByte(endY);
    }

}
