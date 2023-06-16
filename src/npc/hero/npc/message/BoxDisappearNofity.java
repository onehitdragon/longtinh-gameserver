package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 BoxDisappearNofity.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-26 上午11:53:35
 * @描述 ：
 */

public class BoxDisappearNofity extends AbsResponseMessage
{
    /**
     * 箱子编号
     */
    private int   boxID;

    /**
     * 箱子位置Y坐标
     */
    private short boxLocationY;

    public BoxDisappearNofity(int _boxID, short _boxLocationY)
    {
        boxID = _boxID;
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
        yos.writeByte(boxLocationY);
    }

}
