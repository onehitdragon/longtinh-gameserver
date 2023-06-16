package hero.item.message;

import hero.item.legacy.MonsterLegacyBox;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LegacyBoxEmergeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-27 下午02:04:43
 * @描述 ：怪物掉落箱子出现通知
 */

public class LegacyBoxEmergeNotify extends AbsResponseMessage
{
    /**
     * 箱子编号
     */
    private int     boxID;

    /**
     * 怪物编号
     */
    private int     monsterID;

    /**
     * 是否可以拾取
     */
    private boolean canPick;

    /**
     * 箱子位置X坐标
     */
    private short   boxLocationX;

    /**
     * 箱子位置Y坐标
     */
    private short   boxLocationY;

    /**
     * 构造
     * 
     * @param _box
     * @param _canPick
     */
    public LegacyBoxEmergeNotify(MonsterLegacyBox _box, boolean _canPick)
    {
        boxID = _box.getID();
        monsterID = _box.getMonsterID();
        canPick = _canPick;
        boxLocationX = _box.getLocationX();
        boxLocationY = _box.getLocationY();
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
        yos.writeInt(monsterID);
        yos.writeByte(canPick);
        yos.writeByte(boxLocationX);
        yos.writeByte(boxLocationY);
    }

}
