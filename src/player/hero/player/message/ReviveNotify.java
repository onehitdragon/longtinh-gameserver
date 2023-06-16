package hero.player.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ReviveNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-17 上午10:06:42
 * @描述 ：复活通知
 */

public class ReviveNotify extends AbsResponseMessage
{
    /**
     * 复活的玩家对象编号
     */
    private int  playrID;

    /**
     * 当前生命值
     */
    private int  hp;

    /**
     * 最大生命值
     */
    private int  maxHp;

    /**
     * 当前魔法值（力值）
     */
    private int  mpOrForceQuantity;

    /**
     * 最大魔法值（物理能量总值）
     */
    private int  maxMpOrPhisicsQuantity;

    /**
     * 复活的位置X坐标
     */
    private byte locationX;

    /**
     * 复活的位置Y坐标
     */
    private byte locationY;

    /**
     * 构造
     * 
     * @param playrID
     * @param hp
     * @param maxHp
     * @param mpOrForceQuantity
     * @param maxMpOrPhisicsQuantity
     * @param locationX
     * @param locationY
     */
    public ReviveNotify(int _playrID, int _hp, int _maxHp,
            int _mpOrForceQuantity, int _maxMpOrPhisicsQuantity,
            byte _locationX, byte _locationY)
    {
        playrID = _playrID;
        hp = _hp;
        maxHp = _maxHp;
        mpOrForceQuantity = _mpOrForceQuantity;
        maxMpOrPhisicsQuantity = _maxMpOrPhisicsQuantity;
        locationX = _locationX;
        locationY = _locationY;
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
        yos.writeInt(playrID);
        yos.writeInt(hp);
        yos.writeInt(maxHp);
        yos.writeInt(mpOrForceQuantity);
        yos.writeInt(maxMpOrPhisicsQuantity);
        yos.writeByte(locationX);
        yos.writeByte(locationY);
    }
}
