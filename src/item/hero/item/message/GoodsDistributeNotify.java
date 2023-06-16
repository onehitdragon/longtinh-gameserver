package hero.item.message;

import hero.item.Equipment;
import hero.item.Goods;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GoodsDistributeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-15 下午06:39:14
 * @描述 ：御制、圣器物品在队伍中的分配界面通知
 */

public class GoodsDistributeNotify extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(GoodsDistributeNotify.class);
    /**
     * 待分配物品编号
     */
    private short id;

    /**
     * 待分配物品
     */
    private Goods goods;

    /**
     * 待分配物品数量
     */
    private byte  number;

    /**
     * 客户端分配界面存在时间
     */
    private int   existsTime;

    /**
     * 构造
     * 
     * @param _id
     * @param _goods
     * @param _number
     * @param _existsTime
     */
    public GoodsDistributeNotify(short _id, Goods _goods, byte _number,
            int _existsTime)
    {
        id = _id;
        goods = _goods;
        number = _number;
        existsTime = _existsTime;
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
        log.debug("通知客户端弹出物品分配框 goods name="+goods.getName()+",trait="+goods.getTrait()+",number="+number);
        yos.writeShort(id);
        yos.writeUTF(goods.getName());
        yos.writeByte(goods.getTrait().value());
        yos.writeByte(number);
        yos.writeShort(goods.getIconID());
        yos.writeUTF(goods.getDescription());

        if (goods instanceof Equipment)
        {
            yos.writeByte(((Equipment) goods).getWearBodyPart().value());
        }
        else
        {
            yos.writeByte(-1);
        }

        yos.writeInt(existsTime);
        //add by zhengl; date: 2011-04-26; note: 加上物品等级
        yos.writeInt(goods.getNeedLevel());
        log.debug("通知客户端弹出物品分配框 end....");
    }
}
