package hero.gm;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EAttriType.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-21 12:52:20
 * @描述 ：修改属性类型枚举
 */
public enum EAttriType
{
    LEVEL("级别"), MONEY("金钱"), VOCATION("职业"), EXP("经验");

    private String desc;

    private EAttriType(String _name)
    {
        desc = _name;
    }

    public String getDesc ()
    {
        return desc;
    }

    public static EAttriType getEType (String _name)
    {
        for (EAttriType et : EAttriType.values())
        {
            if (et.getDesc().equals(_name))
            {
                return et;
            }
        }

        return null;
    }
}
