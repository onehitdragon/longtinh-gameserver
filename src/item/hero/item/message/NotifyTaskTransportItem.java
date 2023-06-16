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

public class NotifyTaskTransportItem extends AbsResponseMessage
{
    /**
     * 使用的传送道具编号
     */
    private int objectID;

    /**
     * 在背包中的位置
     */
    private int locationOfBag;

    /**
     * 构造
     * 
     */
    public NotifyTaskTransportItem(int _objectID, int _locationOfBag)
    {
    	objectID = _objectID;
    	locationOfBag = _locationOfBag;
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
        yos.writeInt(objectID);
        yos.writeByte(locationOfBag);
    }

}
