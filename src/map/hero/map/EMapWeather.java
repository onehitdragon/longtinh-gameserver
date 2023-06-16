package hero.map;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EMapWeather.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-29 下午03:29:38
 * @描述 ：天气效果
 */

public enum EMapWeather
{
    NONE(0, "无"), RAIN(1, "下雨"), SNOW(2, "下雪"), CLOUDY(3, "多云"), BUBBLE(4, "气泡"), PETAL(
            5, "花瓣");

    /**
     * 类型值
     */
    private int    typeValue;

    /**
     * 气候描述
     */
    private String desc;

    /**
     * 构造
     * 
     * @param _value
     * @param _desc
     */
    EMapWeather(int _value, String _desc)
    {
        typeValue = _value;
        desc = _desc;
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
     * 获取天气描述
     * 
     * @return
     */
    public String getDescription ()
    {
        return desc;
    }

    /**
     * 根据气候值获取气候枚举对象
     * 
     * @param _value
     * @return
     */
    public static EMapWeather get (int _value)
    {
        for (EMapWeather weather : EMapWeather.values())
        {
            if (weather.typeValue == _value)
            {
                return weather;
            }
        }

        return NONE;
    }
}
