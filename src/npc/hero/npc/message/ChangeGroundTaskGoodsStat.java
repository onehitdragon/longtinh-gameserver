package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeGroundTaskGoodsStat.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-31 下午01:07:08
 * @描述 ：
 */

public class ChangeGroundTaskGoodsStat extends AbsResponseMessage
{
    /**
     * 地上的任务物品编号
     */
    private int     groundTaskGoodsID;

    /**
     * 地上的任务物品位置Y坐标
     */
    private short   locationY;

    /**
     * 能否交互
     */
    private boolean canInteract;

    public ChangeGroundTaskGoodsStat(int _groundTaskGoodsID, short _y,
            boolean _canInteract)
    {
        groundTaskGoodsID = _groundTaskGoodsID;
        locationY = _y;
        canInteract = _canInteract;
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
        yos.writeInt(groundTaskGoodsID);
        yos.writeByte(locationY);
        yos.writeByte(canInteract);
    }
}
