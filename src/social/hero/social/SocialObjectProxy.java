package hero.social;

import hero.player.define.ESex;
import hero.share.EVocation;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SocialObjectProxy.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-9 上午10:08:23
 * @描述 ：社交对象代理，用于显示名称、职业、性别、等级、在线状态（包括好友、仇人、屏蔽三种关系对象）
 */

public class SocialObjectProxy
{
    /**
     * 社交个人关系类型
     */
    public ESocialRelationType socialRelationType;

    /**
     * 编号
     */
    public int                 userID;

    /**
     * 名称
     */
    public String              name;

    /**
     * 职业
     */
    public EVocation           vocation;

    /**
     * 性别
     */
    public ESex                sex;

    /**
     * 等级
     */
    public short               level;

    /**
     * 是否在线
     */
    public boolean             isOnline;

    /**
     * 构造
     * 
     * @param _userID
     * @param _name
     * @param _isOnline
     */
    public SocialObjectProxy(ESocialRelationType _socialRelationType,
            int _userID, String _name)
    {
        socialRelationType = _socialRelationType;
        userID = _userID;
        name = _name;
    }

    /**
     * 构造
     * 
     * @param _userID
     * @param _name
     * @param _isOnline
     */
    public SocialObjectProxy(byte _socialRelationType, int _userID, String _name)
    {
        userID = _userID;
        name = _name;
        socialRelationType = ESocialRelationType
                .getSocialRelationType(_socialRelationType);
    }
    


    /**
     * 设置在线状态
     * 
     * @param _level
     * @param _vocation
     */
    public void setOnlineStatus (short _level, EVocation _vocation, ESex _sex)
    {
        isOnline = true;
        level = _level;
        vocation = _vocation;
        sex = _sex;
    }
}
