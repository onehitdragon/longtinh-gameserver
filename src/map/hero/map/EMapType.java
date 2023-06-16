package hero.map;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EMapType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-12 下午02:03:32
 * @描述 ：地图类型（普通、副本）
 */

public enum EMapType
{
    /**
     * 普通地图
     */
    GENERIC(1),
    /**
     * 副本
     */
    DUNGEON(2);

    /**
     * 类型值
     */
    private int typeValue;

    /**
     * 构造
     * 
     * @param _typeValue
     */
    EMapType(int _typeValue)
    {
        typeValue = _typeValue;
    }

    /**
     * 获取类型值
     * 
     * @return
     */
    public int getTypeValue ()
    {
        return typeValue;
    }

    /**
     * 获取地图类型
     * 
     * @param _value
     * @return
     */
    public static EMapType getMapType (int _value)
    {
        for (EMapType type : EMapType.values())
        {
            if (type.typeValue == _value)
            {
                return type;
            }
        }

        return null;
    }
}
