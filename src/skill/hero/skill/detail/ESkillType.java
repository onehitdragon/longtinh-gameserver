package hero.skill.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ESkillType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-29 上午09:21:20
 * @描述 ：技能类型：主动技能、被动技能
 */

public enum ESkillType
{
    /**
     * 主动技能
     */
    ACTIVE(1),
    /**
     * 被动技能
     */
    PASSIVE(2);

    byte value;

    ESkillType(int _value)
    {
        value = (byte) _value;
    }

    public byte value ()
    {
        return value;
    }
}
