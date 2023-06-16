package hero.share.service;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GlobalTimer.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-16 上午09:47:19
 * @描述 ：
 */

public class GlobalTimer
{
    /**
     * 计时器
     */
    private Timer              timer;

    /**
     * 单例
     */
    private static GlobalTimer instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static GlobalTimer getInstance ()
    {
        if (null == instance)
        {
            instance = new GlobalTimer();
        }

        return instance;
    }

    /**
     * 私有构造
     */
    private GlobalTimer()
    {
        timer = new Timer();
    }

    /**
     * 注册任务
     * 
     * @param _newTask
     * @param _delay
     * @param _period
     */
    public void registe (TimerTask _newTask, long _delay, long _period)
    {
        if (null == timer)
        {
            timer = new Timer();
        }

        timer.schedule(_newTask, _delay, _period);
    }

    /**
     * 关闭
     */
    public void close ()
    {
        timer.cancel();
        timer = null;
    }
}
