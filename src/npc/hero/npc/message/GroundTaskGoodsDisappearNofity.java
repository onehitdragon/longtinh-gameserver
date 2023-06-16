package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroundTaskGoodsDisappearNofity.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-31 下午03:26:23
 * @描述 ：
 */

public class GroundTaskGoodsDisappearNofity extends AbsResponseMessage
{
    /**
     * 地上任务物品编号
     */
    private int   taskGoodsID;

    /**
     * 地上任务物品位置Y坐标
     */
    private short taskGoodsIDLocationY;

    public GroundTaskGoodsDisappearNofity(int _taskGoodsID,
            short _taskGoodsIDLocationY)
    {
        taskGoodsID = _taskGoodsID;
        taskGoodsIDLocationY = _taskGoodsIDLocationY;
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
        yos.writeInt(taskGoodsID);
        yos.writeByte(taskGoodsIDLocationY);
    }
}
