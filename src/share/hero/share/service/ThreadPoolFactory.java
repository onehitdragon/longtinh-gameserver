package hero.share.service;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ThreadPoolFactory.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-16 下午04:52:24
 * @描述 ：
 */

public class ThreadPoolFactory
{
    private static ScheduledThreadPoolExecutor aiScheduledThreadPool = null;

    private static ThreadPoolFactory           instance;

    public static ThreadPoolFactory getInstance ()
    {
        if (null == instance)
        {
            instance = new ThreadPoolFactory();
        }

        return instance;
    }

    private ThreadPoolFactory()
    {
        aiScheduledThreadPool = new ScheduledThreadPoolExecutor(3);
    }

    /**
     * @param _run 线程类及其扩展
     * @param _delay 启动延时
     * @param _period 执行间隔
     */
    public Future excuteAI (Runnable _run, long _delay, long _period)
    {
        return aiScheduledThreadPool.scheduleAtFixedRate(_run, _delay, _period,
                TimeUnit.MILLISECONDS);
    }

    public void schedule (Runnable _run, long _delay)
    {
        aiScheduledThreadPool.schedule(_run, _delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 移除AI
     * 
     * @param _run
     * @return
     */
    public boolean removeAI (Runnable _run)
    {
        return aiScheduledThreadPool.remove(_run);
    }

    /**
     * 线程池状态描述
     * 
     * @return
     */
    public String[] getAIPoolStatus ()
    {
        return new String[]{
                "  ActiveThreads:   " + aiScheduledThreadPool.getActiveCount(),
                "  getCorePoolSize: " + aiScheduledThreadPool.getCorePoolSize(),
                "  MaximumPoolSize: "
                        + aiScheduledThreadPool.getMaximumPoolSize(),
                "  LargestPoolSize: "
                        + aiScheduledThreadPool.getLargestPoolSize(),
                "  PoolSize:        " + aiScheduledThreadPool.getPoolSize(),
                "  CompletedTasks:  "
                        + aiScheduledThreadPool.getCompletedTaskCount(),
                "  QueuedTasks:     " + aiScheduledThreadPool.getQueue().size() };
    }
}
