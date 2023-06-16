package hero.group;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EGroupMemberRank.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午01:48:18
 * @描述 ：队伍成员权限
 */

public enum EGroupMemberRank
{
    /**
     * 普通成员
     */
    NORMAL((byte) 1),
    /**
     * 队伍助手
     */
    ASSISTANT((byte) 2),
    /**
     * 队长
     */
    LEADER((byte) 3);

    byte value;

    EGroupMemberRank(byte _value)
    {
        value = _value;
    }

    public byte value ()
    {
        return value;
    }
}
