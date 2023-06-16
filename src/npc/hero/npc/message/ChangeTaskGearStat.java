package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeTaskGearStat.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-13 下午09:44:21
 * @描述 ：
 */

public class ChangeTaskGearStat extends AbsResponseMessage
{
    /**
     * 任务机关编号
     */
    private int     gearID;

    /**
     * 任务机关位置Y坐标
     */
    private short   locationY;

    /**
     * 能否交互
     */
    private boolean canInteract;

    public ChangeTaskGearStat(int _gearID, short _y, boolean _canInteract)
    {
        gearID = _gearID;
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
        yos.writeInt(gearID);
        yos.writeByte(locationY);
        yos.writeByte(canInteract);
    }

}
