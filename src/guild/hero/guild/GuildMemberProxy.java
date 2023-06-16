package hero.guild;

import hero.player.HeroPlayer;
import hero.player.define.ESex;
import hero.share.EVocation;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildMemberProxy.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 上午09:37:02
 * @描述 ：公会成员代理对象
 */

public class GuildMemberProxy
{
    /**
     * 构造
     * 
     * @param _userID
     * @param _player
     * @param _memberRank
     */
    public GuildMemberProxy(int _userID, HeroPlayer _player,
            EGuildMemberRank _memberRank)
    {
        userID = _userID;
        name = _player.getName();
        level = _player.getLevel();
        sex = _player.getSex();
        vocation = _player.getVocation();
        memberRank = _memberRank;
        isOnline = true;
    }

    /**
     * 从数据库加载公会成员
     * 
     * @param _userID
     * @param _name
     * @param _memberRankValue
     */
    public GuildMemberProxy(int _userID, String _name, byte _memberRankValue)
    {
        userID = _userID;
        name = _name;
        memberRank = EGuildMemberRank.getRank(_memberRankValue);
    }

    /**
     * 玩家编号
     */
    public int              userID;

    /**
     * 玩家名字
     */
    public String           name;

    /**
     * 等级
     */
    public short            level;

    /**
     * 性别
     */
    public ESex             sex;

    /**
     * 职业
     */
    public EVocation        vocation;

    /**
     * 是否在线
     */
    public boolean          isOnline;

    /**
     * 公会官阶
     */
    public EGuildMemberRank memberRank;
}
