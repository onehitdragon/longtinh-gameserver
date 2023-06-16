package hero.map.service;

import hero.map.EMapWeather;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.WeatherChangeNotify;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WeatherManager.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-3 上午09:38:30
 * @描述 ：气候管理器，管理所有内存地图的气候变化
 */

public class WeatherManager
{
    /**
     * 下雨地图控制器
     */
    private WeatherControler      rainWeatherControler;

    /**
     * 下雪地图控制器
     */
    private WeatherControler      snowWeatherControler;

    /**
     * 多云地图控制器
     */
    private WeatherControler      cloudyWeatherControler;

    /**
     * 落花瓣地图控制器
     */
    private WeatherControler      petalWeatherControler;

    /**
     * 冒气泡地图控制器
     */
    private WeatherControler      bubbleWeatherControler;

    /**
     * 天气管理任务计时器
     */
    private Timer                 timer;

    /**
     * 单例
     */
    private static WeatherManager instance;

    /**
     * 私有构造
     */
    private WeatherManager()
    {
        rainWeatherControler = new WeatherControler(EMapWeather.RAIN);
        snowWeatherControler = new WeatherControler(EMapWeather.SNOW);
        cloudyWeatherControler = new WeatherControler(EMapWeather.CLOUDY);
        petalWeatherControler = new WeatherControler(EMapWeather.PETAL);
        bubbleWeatherControler = new WeatherControler(EMapWeather.BUBBLE);

        timer = new Timer();
        timer.schedule(rainWeatherControler, WEATHER_CHANGE_RANDOM_BUILDER
                .nextInt(CONTROLER_START_RELAY)
                + CONTROLER_START_REVISE, CHANGE_CHECK_INTERVAL);
        timer.schedule(snowWeatherControler, WEATHER_CHANGE_RANDOM_BUILDER
                .nextInt(CONTROLER_START_RELAY)
                + CONTROLER_START_REVISE, CHANGE_CHECK_INTERVAL);
        timer.schedule(cloudyWeatherControler, WEATHER_CHANGE_RANDOM_BUILDER
                .nextInt(CONTROLER_START_RELAY)
                + CONTROLER_START_REVISE, CHANGE_CHECK_INTERVAL);
        timer.schedule(petalWeatherControler, WEATHER_CHANGE_RANDOM_BUILDER
                .nextInt(CONTROLER_START_RELAY)
                + CONTROLER_START_REVISE, CHANGE_CHECK_INTERVAL);
        timer.schedule(bubbleWeatherControler, WEATHER_CHANGE_RANDOM_BUILDER
                .nextInt(CONTROLER_START_RELAY)
                + CONTROLER_START_REVISE, CHANGE_CHECK_INTERVAL);
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static WeatherManager getInstance ()
    {
        if (null == instance)
        {
            instance = new WeatherManager();
        }

        return instance;
    }

    /**
     * 加入天气地图
     * 
     * @param _map
     */
    public void add (Map _map)
    {
        switch (_map.getWeather())
        {
            case RAIN:
            {
                rainWeatherControler.add(_map);

                break;
            }
            case SNOW:
            {
                snowWeatherControler.add(_map);

                break;
            }
            case CLOUDY:
            {
                cloudyWeatherControler.add(_map);

                break;
            }
            case BUBBLE:
            {
                bubbleWeatherControler.add(_map);

                break;
            }
            case PETAL:
            {
                petalWeatherControler.add(_map);

                break;
            }
        }
    }

    /**
     * 移除地图
     * 
     * @param _map
     */
    public void remove (Map _map)
    {
        switch (_map.getWeather())
        {
            case RAIN:
            {
                rainWeatherControler.remove(_map);

                break;
            }
            case SNOW:
            {
                snowWeatherControler.remove(_map);

                break;
            }
            case CLOUDY:
            {
                cloudyWeatherControler.remove(_map);

                break;
            }
            case BUBBLE:
            {
                bubbleWeatherControler.remove(_map);

                break;
            }
            case PETAL:
            {
                petalWeatherControler.remove(_map);

                break;
            }
        }
    }

    /**
     * 获取气候风向
     * 
     * @param _weather
     */
    public int[] getWeather (EMapWeather _weather)
    {
        switch (_weather)
        {
            case NONE:
            {
                return NONE_WEATHER_DESC;
            }
            case RAIN:
            {
                return rainWeatherControler.getWeatherDesc();
            }
            case SNOW:
            {
                return snowWeatherControler.getWeatherDesc();
            }
            case CLOUDY:
            {
                return cloudyWeatherControler.getWeatherDesc();
            }
            case BUBBLE:
            {
                return bubbleWeatherControler.getWeatherDesc();
            }
            case PETAL:
            {
                return petalWeatherControler.getWeatherDesc();
            }
        }

        return NONE_WEATHER_DESC;
    }

    /**
     * @author DC
     */
    private class WeatherControler extends TimerTask
    {
        /**
         * 地图列表
         */
        private FastList<Map> mapList;

        /**
         * 天气
         */
        private EMapWeather   weather;

        /**
         * 开始时间
         */
        private long          startTime;

        /**
         * 结束时间
         */
        private long          endTime;

        /**
         * 存在天气效果
         */
        private boolean       ing;

        /**
         * 天气效果描述
         */
        private int[]         weatherDesc;

        /**
         * 构造
         * 
         * @param _startTime
         */
        public WeatherControler(EMapWeather _weather)
        {
            weather = _weather;
            weatherDesc = new int[]{0, 0 };
            mapList = new FastList<Map>();
        }

        /**
         * 添加地图
         * 
         * @param _map
         */
        public void add (Map _map)
        {
            mapList.add(_map);
        }

        /**
         * 获取风向
         * 
         * @return
         */
        public int[] getWeatherDesc ()
        {
            return weatherDesc;
        }

        /**
         * 移除地图
         * 
         * @param _map
         */
        public void remove (Map _map)
        {
            mapList.remove(_map);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
            long now = System.currentTimeMillis();

            if (!ing)
            {
                if (now >= startTime)
                {
                    if (now < endTime || 0 == endTime)
                    {
                        endTime = getWeatherRandomKeepTime() + now;
                        weatherDesc[0] = weather.getTypeValue();
                        weatherDesc[1] = WEATHER_CHANGE_RANDOM_BUILDER
                                .nextInt(2) + 1;

                        synchronized (mapList)
                        {
                            for (Map map : mapList)
                            {
                                MapSynchronousInfoBroadcast.getInstance().put(
                                        map,
                                        new WeatherChangeNotify(weather,
                                                weatherDesc[1]), false, 0);
                            }
                        }

                        ing = true;
                    }
                }
            }
            else
            {
                if (now >= endTime)
                {
                    startTime = getNextChangeTime();
                    endTime = 0;

                    synchronized (mapList)
                    {
                        for (Map map : mapList)
                        {
                            MapSynchronousInfoBroadcast.getInstance()
                                    .put(
                                            map,
                                            new WeatherChangeNotify(
                                                    EMapWeather.NONE, 0),
                                            false, 0);
                        }
                    }

                    weatherDesc[0] = 0;
                    weatherDesc[1] = 0;
                    ing = false;
                }
            }
        }
    }

    /**
     * 获取天气随机持续时间
     * 
     * @return
     */
    private static int getWeatherRandomKeepTime ()
    {
        int keepTime = WEATHER_CHANGE_RANDOM_BUILDER
                .nextInt(WEATHER_KEEP_TIME_MAX);

        while (keepTime < WEATHER_KEEP_TIME_MIN)
        {
            keepTime = WEATHER_CHANGE_RANDOM_BUILDER
                    .nextInt(WEATHER_KEEP_TIME_MAX);
        }

        return keepTime;
    }

    /**
     * 获取下次天气变化时间
     * 
     * @return
     */
    private static long getNextChangeTime ()
    {
        int keepTime = WEATHER_CHANGE_RANDOM_BUILDER
                .nextInt(CHANGE_INTERVAL_MAX);

        while (keepTime < CHANGE_INTERVAL_MIN)
        {
            keepTime = WEATHER_CHANGE_RANDOM_BUILDER
                    .nextInt(CHANGE_INTERVAL_MAX);
        }

        return keepTime + System.currentTimeMillis();
    }

    /**
     * 天气变化随机数生成器
     */
    private static final Random WEATHER_CHANGE_RANDOM_BUILDER = new Random();

    /**
     * 天气持续最短时间（5分钟）
     */
    private static final int    WEATHER_KEEP_TIME_MIN         = 5 * 60 * 1000;

    /**
     * 天气持续最长时间（8分钟）
     */
    private static final int    WEATHER_KEEP_TIME_MAX         = 8 * 60 * 1000;

    /**
     * 天气变化间隔最短时间（10分钟）
     */
    private static final int    CHANGE_INTERVAL_MIN           = 10 * 60 * 1000;

    /**
     * 天气变化间隔最长时间（30分钟）
     */
    private static final int    CHANGE_INTERVAL_MAX           = 30 * 60 * 1000;

    /**
     * 天气变化控制器启动最大延时（8分钟）
     */
    private static final int    CONTROLER_START_RELAY         = 8 * 60 * 1000;

    /**
     * 天气变化控制器启动延时修正（3分钟）
     */
    private static final int    CONTROLER_START_REVISE        = 3 * 60 * 1000;

    /**
     * 天气变化检查间隔（30秒）
     */
    private static final int    CHANGE_CHECK_INTERVAL         = 30 * 1000;

    /**
     * 无天气效果的描述
     */
    private static final int[]  NONE_WEATHER_DESC             = new int[]{0, 0 };
}
