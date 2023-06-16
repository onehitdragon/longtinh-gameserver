package hero.guild;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EGuildMemberRank.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 上午09:41:45
 * @描述 ：公会成员等级
 */

public enum EGuildMemberRank
{
    NORMAL(1, "帮众"), OFFICER(2, "副帮主"), PRESIDENT(3, "帮主");

    byte   value;

    String description;

    EGuildMemberRank(int _value, String _description)
    {
        value = (byte) _value;
        description = _description;
    }

    /**
     * 获取公会成员等级
     * 
     * @param _value
     * @return
     */
    public static EGuildMemberRank getRank (int _value)
    {
        for (EGuildMemberRank guildMemberRank : EGuildMemberRank.values())
        {
            if (guildMemberRank.value == _value)
            {
                return guildMemberRank;
            }
        }

        return null;
    }

    /**
     * 获取公会成员等级描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return description;
    }

    /**
     * 标识值
     * 
     * @return
     */
    public byte value ()
    {
        return value;
    }
}
