package hero.skill.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ETargetType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-27 上午10:30:17
 * @描述 ：
 */

public enum ETargetType
{
    /**
     * 自己
     */
    MYSELF((byte) 0, "自身"),
    /**
     * 敌方
     */
    ENEMY((byte) 1, "敌方"),
    /**
     * 友方
     */
    FRIEND((byte) 2, "友方"),
    /**
     * 死者
     */
    DIER((byte) 3, "亡者"),
    /**
     * 
     */
    TARGET((byte) 4, "目标"),
    /**
     * 主人，用于宠物技能
     */
    OWNER((byte)5,"主人");

    byte   value;

    String desc;

    ETargetType(byte _value, String _desc)
    {
        value = _value;
        desc = _desc;
    }

    public static ETargetType get (String _typeDesc)
    {
        for (ETargetType type : ETargetType.values())
        {
            if (type.desc.equals(_typeDesc))
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
