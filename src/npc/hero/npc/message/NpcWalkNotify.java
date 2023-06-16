package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NpcWalkNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-7 上午09:54:47
 * @描述 ：
 */

public class NpcWalkNotify extends AbsResponseMessage
{
    /**
     * 内存临时编号
     */
    private int    objectID;

    /**
     * 行走路径
     */
    private byte[] path;
    
    private byte   x;
    
    private byte   y;

    /**
     * @param walker
     */
    public NpcWalkNotify(int _walkerID, byte[] _path, byte _x, byte _y)
    {
        objectID = _walkerID;
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
        yos.writeByte(path.length);
        yos.writeBytes(path);
        //add by zhengl; date: 2011-05-10; note: 添加移动的x,y坐标
        yos.writeByte(x);
        yos.writeByte(y);
    }

}
