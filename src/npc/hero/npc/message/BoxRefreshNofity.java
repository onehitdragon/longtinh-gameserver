package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 BoxEmergeNofity.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-26 上午11:47:37
 * @描述 ：
 */

public class BoxRefreshNofity extends AbsResponseMessage
{
    /**
     * 箱子编号
     */
    private int   boxID;

    /**
     * 箱子位置X坐标
     */
    private short boxLocationX;

    /**
     * 箱子位置Y坐标
     */
    private short boxLocationY;

    /**
     * 构造
     * 
     * @param _boxID
     * @param _boxLocationX
     * @param _boxLocationY
     */
    public BoxRefreshNofity(int _boxID, short _boxLocationX, short _boxLocationY)
    {
        boxID = _boxID;
        boxLocationX = _boxLocationX;
        boxLocationY = _boxLocationY;
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
        yos.writeInt(boxID);
        yos.writeByte(boxLocationX);
        yos.writeByte(boxLocationY);
    }

}
