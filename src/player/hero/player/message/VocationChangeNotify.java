package hero.player.message;

import hero.share.EVocationType;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 VocationChangeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-13 下午07:33:34
 * @描述 ：角色改变通知
 */

public class VocationChangeNotify extends AbsResponseMessage
{
    /**
     * 玩家编号
     */
    private int   playerID;

    /**
     * 职业值
     */
    private short vocationValue;

    /**
     * 最大生命值
     */
    private int   maxHp;

    /**
     * 力值或最大魔法值
     */
    private int   forceQuantityOrMaxMp;

    /**
     * 构造
     * 
     * @param _playerID
     * @param _vocationValue
     */
    public VocationChangeNotify(int _playerID, short _vocationValue,
            int _maxHp, int _forceQuantityOrMaxMp)
    {
        playerID = _playerID;
        vocationValue = _vocationValue;
        maxHp = _maxHp;
        forceQuantityOrMaxMp = _forceQuantityOrMaxMp;
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
        yos.writeInt(playerID);
        yos.writeByte(vocationValue);
        yos.writeInt(maxHp);
        yos.writeInt(forceQuantityOrMaxMp);
    }

}
