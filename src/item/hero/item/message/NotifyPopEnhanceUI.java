package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NotifyPopEnhanceUI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-21 上午11:10:50
 * @描述 ：
 */

public class NotifyPopEnhanceUI extends AbsResponseMessage
{
    /**
     * 使用的水晶编号
     */
    private int crystalID;

    /**
     * 在背包中的位置
     */
    private int crystalLocationOfBag;

    /**
     * 构造
     * 
     * @param _crystalID 水晶编号
     */
    public NotifyPopEnhanceUI(int _crystalID, int _location)
    {
        crystalID = _crystalID;
        crystalLocationOfBag = _location;
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
        yos.writeInt(crystalID);
        yos.writeByte(crystalLocationOfBag);
    }

}
