package hero.skill.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EHarmType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-27 上午10:43:03
 * @描述 ：
 */

public enum EHarmType
{
    /**
     * 神圣
     */
    SANCTITY("神圣"),

    /**
     * 暗影
     */
    UMBRA("暗影"),
    /**
     * 冰
     */
    WATER("冰"),
    /**
     * 火
     */
    FIRE("火"),
    /**
     * 自然(原来的'土')
     */
    SOIL("自然"),
    /**
     * 所有魔法伤害
     */
    ALL_MAGIC("魔法"),
    /**
     * 远程物理伤害
     */
    DISTANCE_PHYSICS("远程物理"),
    /**
     * 近程物理伤害
     */
    NEAR_PHYSICS("近程物理"),
    /**
     * 物理伤害
     */
    PHYSICS("物理"),
    /**
     * 一切伤害
     */
    ALL("一切");

    String desc;

    EHarmType(String _desc)
    {
        desc = _desc;
    }

    public static EHarmType get (String _desc)
    {
        for (EHarmType harmType : EHarmType.values())
        {
            if (harmType.desc.equals(_desc))
            {
                return harmType;
            }
        }

        return null;
    }

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return desc;
    }
}
