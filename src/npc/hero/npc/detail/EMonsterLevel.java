package hero.npc.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EMonsterLevel.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-22 上午11:23:13
 * @描述 ：
 */

public enum EMonsterLevel
{
    NORMAL(1, "普通"), BOSS(2, "首领");

    EMonsterLevel(int _value, String _desc)
    {
        value = _value;
        desc = _desc;
    }

    /**
     * 编号
     */
    private int    value;

    /**
     * 描述
     */
    private String desc;

    /**
     * 获取编号值
     * 
     * @return
     */
    public int value ()
    {
        return value;
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

    /**
     * 根据描述获取怪物对象等级
     * 
     * @param _desc
     * @return
     */
    public static EMonsterLevel getMonsterLevel (String _desc)
    {
        for (EMonsterLevel type : EMonsterLevel.values())
        {
            if (type.getDesc().equals(_desc))
            {
                return type;
            }
        }

        return null;
    }
}
