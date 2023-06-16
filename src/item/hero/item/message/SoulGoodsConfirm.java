package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SoulGoodsConfirm.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-11 上午10:06:36
 * @描述 ：使用灵魂相关物品的确认框
 */

public class SoulGoodsConfirm extends AbsResponseMessage
{
    /**
     * 操作类型
     */
    private byte operateType;

    /**
     * 物品在背包中的位置（快捷键使用-1）
     */
    private int  locationOfBag;

    /**
     * 构造
     * 
     * @param _operateType
     */
    public SoulGoodsConfirm(byte _operateType, int _locationOfBag)
    {
        operateType = _operateType;
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
        yos.writeByte(operateType);
        yos.writeByte(locationOfBag);
    }

    /**
     * 灵魂印记
     */
    public static final byte TYPE_OF_MARK    = 1;

    /**
     * 灵魂传送
     */
    public static final byte TYPE_OF_CHANNEL = 2;
}
