/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 */

package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class MonsterWalkNotify extends AbsResponseMessage
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
    
    /**
     * x坐标
     */
    private byte   x;
    
    /**
     * y坐标
     */
    private byte   y;

    /**
     * @param _walkerID
     * @param _speed
     * @param _path
     * @param _x
     * @param _y
     */
    public MonsterWalkNotify(int _walkerID, byte _speed, byte[] _path, byte _x, byte _y)
    {
        objectID = _walkerID;
        speed = _speed;
        path = _path;
        x = _x;
        y = _y;
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
        //add by zhengl; date: 2011-05-07; note: 添加移动的x,y坐标
        yos.writeByte(x);
        yos.writeByte(y);
    }

}
