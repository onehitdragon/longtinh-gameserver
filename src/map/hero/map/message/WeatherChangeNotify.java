package hero.map.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

import hero.map.EMapWeather;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WeatherChangeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-28 下午02:46:41
 * @描述 ：地图天气变化通知（0：无、1：雨、2：雪、3：花、4：云、5：气泡）
 */

public class WeatherChangeNotify extends AbsResponseMessage
{
    /**
     * 天气
     */
    private EMapWeather weather;

    /**
     * 方向
     */
    private int         direction;

    /**
     * 构造
     * 
     * @param _weather
     */
    public WeatherChangeNotify(EMapWeather _weather, int _direction)
    {
        weather = _weather;
        direction = _direction;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeByte(weather.getTypeValue());
        yos.writeByte(direction);
    }

}
