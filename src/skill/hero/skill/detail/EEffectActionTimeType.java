package hero.skill.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EEffectActionTimeType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-27 上午10:46:12
 * @描述 ：
 */

public enum EEffectActionTimeType
{
    PER_THREE_SECOND("三秒"), AT_END("末端"), RANDOM("随机");

    String desc;

    EEffectActionTimeType(String _desc)
    {
        desc = _desc;
    }

    public static EEffectActionTimeType get (String _desc)
    {
        for (EEffectActionTimeType specialStatus : EEffectActionTimeType
                .values())
        {
            if (specialStatus.desc.equals(_desc))
            {
                return specialStatus;
            }
        }

        return null;
    }
}
