package hero.player.message;

import hero.player.HeroPlayer;
import hero.share.EVocationType;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RoleUpgradeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-14 上午11:04:02
 * @描述 ：
 */

public class RoleUpgradeNotify extends AbsResponseMessage
{
    /**
     * 玩家编号
     */
    private int   playerID;

    /**
     * 职业值
     */
    private short level;

    /**
     * 最大生命值
     */
    private int   maxHp;

    /**
     * 力值或最大魔法值
     */
    private int   forceQuantityOrMaxMp;
    
    private short  skillPoint;

    public RoleUpgradeNotify(int _playerID, short _level, int _maxHp,
            int _forceQuantityOrMaxMp, short _skillPoint)
    {
        playerID = _playerID;
        level = _level;
        maxHp = _maxHp;
        forceQuantityOrMaxMp = _forceQuantityOrMaxMp;
        skillPoint = _skillPoint;
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
        yos.writeShort(level);
        yos.writeInt(maxHp);
        yos.writeInt(forceQuantityOrMaxMp);
        yos.writeShort(skillPoint); //add by zhengl ; date: 2010-11-16 ; note: 添加升级后的技能点.
    }

}
