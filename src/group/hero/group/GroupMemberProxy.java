package hero.group;

import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroupMemberProxy.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午01:46:17
 * @描述 ：队伍成员代理
 */

public class GroupMemberProxy
{
    /**
     * 玩家对象
     */
    public HeroPlayer      player;

    /**
     * 成员权限
     */
    public EGroupMemberRank memberRank;

    /**
     * 在队伍中的小队编号
     */
    public int             subGroupID;

    /**
     * 在整个队伍中的位置
     */
//    public int position;

    public byte getPattern() {
        return pattern;
    }

    public void setPattern(byte pattern) {
        this.pattern = pattern;
    }

    /**
     * 队长进入副本的模式
     */
    private byte pattern;

    /**
     * 构造
     * 
     * @param _player
     * @param _subGroupID
     */
    public GroupMemberProxy(HeroPlayer _player, int _subGroupID)
    {
        player = _player;
        subGroupID = _subGroupID;
        memberRank = EGroupMemberRank.NORMAL;
    }

    /**
     * 是否在线
     */
    public boolean isOnline ()
    {
        if (null != player && player.isEnable())
        {
            return true;
        }

        return false;
    }
}
