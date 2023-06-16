package hero.item.message;

import hero.item.Equipment;
import hero.item.Goods;
import hero.item.SingleGoods;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseItemInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-21 下午01:48:25
 * @描述 ：响应查看的物品信息
 */

public class ResponseItemInfo extends AbsResponseMessage
{
    /**
     * 物品对象
     */
    private Goods goods;

    /**
     * 构造
     * 
     * @param _weapon
     */
    public ResponseItemInfo(Goods _goods)
    {
        goods = _goods;
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
        yos.writeUTF(goods.getName());

        if (goods instanceof Equipment)
        {
            Equipment equipment = (Equipment) goods;

            yos.writeByte(GOODS_TYPE_OF_EQUIPMENT);
            yos.writeBytes(equipment.getFixPropertyBytes());
            yos.writeByte(0);
            yos.writeByte(equipment.existSeal());
            yos.writeShort(equipment.getMaxDurabilityPoint());
            yos.writeInt(equipment.getRetrievePrice());
        }
        else
        {
            SingleGoods singleGoods = (SingleGoods) goods;

            yos.writeByte(GOODS_TYPE_OF_SINGLE);
            yos.writeByte(singleGoods.getTrait().value());
            yos.writeUTF(singleGoods.getDescription());
            yos.writeInt(singleGoods.getRetrievePrice());
//            output.writeShort(singleGoods.getNeedLevel());//add by zhengl; date: 2011-04-27;
        }
    }

    /**
     * 物品类型-装备
     */
    private static final byte GOODS_TYPE_OF_EQUIPMENT = 1;

    /**
     * 物品类型-非装备
     */
    private static final byte GOODS_TYPE_OF_SINGLE    = 2;
}
