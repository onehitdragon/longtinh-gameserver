package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

import hero.npc.others.GroundTaskGoods;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroundTaskGoodsEmergeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-31 下午03:37:09
 * @描述 ：
 */

public class GroundTaskGoodsEmergeNotify extends AbsResponseMessage
{
    /**
     * 地上的任务物品
     */
    private GroundTaskGoods groundTaskGoods;

    /**
     * 能否被拾取
     */
    private boolean         canBePick;

    /**
     * 构造
     * 
     * @param _groundTaskGoods
     */
    public GroundTaskGoodsEmergeNotify(short _clientType,
            GroundTaskGoods _groundTaskGoods, boolean _canBePick)
    {
        groundTaskGoods = _groundTaskGoods;
        canBePick = _canBePick;
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
        yos.writeInt(groundTaskGoods.getID());
        yos.writeUTF(groundTaskGoods.getName());
        yos.writeByte(groundTaskGoods.getCellX());
        yos.writeByte(groundTaskGoods.getCellY());
        yos.writeShort(groundTaskGoods.getImageID());
        yos.writeByte(canBePick);
    }
}
