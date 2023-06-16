package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LegacyBoxStatusDisappearNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-20 下午05:51:06
 * @描述 ：怪物掉落箱子拾取状态消失通知
 */

public class LegacyBoxStatusDisappearNotify extends AbsResponseMessage
{
    /**
     * 箱子编号
     */
    private int   boxID;

    /**
     * 箱子位置Y坐标
     */
    private short boxLocationY;

    /**
     * 构造
     * 
     * @param _boxID 箱子编号
     * @param _boxLocationY 位置Y坐标
     */
    public LegacyBoxStatusDisappearNotify(int _boxID, short _boxLocationY)
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
