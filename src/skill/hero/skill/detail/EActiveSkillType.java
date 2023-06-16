package hero.skill.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EActiveSkillType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-29 上午09:19:52
 * @描述 ：
 */

public enum EActiveSkillType
{
    /**
     * 魔法技能
     */
    MAGIC((byte) 1, "魔法技能"),
    /**
     * 物理技能
     */
    PHYSICS((byte) 2, "物理技能");

    byte   value;

    String desc;

    /**
     * 构造
     * 
     * @param _desc
     */
    EActiveSkillType(byte _value, String _desc)
    {
        value = _value;
        desc = _desc;
    }

    /**
     * 根据描述获取主动技能类型
     * 
     * @param _desc
     * @return
     */
    public static EActiveSkillType getType (String _desc)
    {
        for (EActiveSkillType type : EActiveSkillType.values())
        {
            if (type.desc.equals(_desc))
            {
                return type;
            }
        }

        return null;
    }

    public byte value ()
    {
        return value;
    }
}
