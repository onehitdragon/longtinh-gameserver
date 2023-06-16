
package yoyo.core.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager
{
	private static ThreadPoolManager instance;
	
    public static final int BUFFSIZE         = 10;
    public static final int GENERALSIZE         = 13;
    public static final int GENERALCORESIZE = 4;
    public static final int AISIZE            = 6;
    
    private ScheduledThreadPoolExecutor aiPool;
    private ScheduledThreadPoolExecutor buffPool;
    private ScheduledThreadPoolExecutor generalScheduledPool;
    private ThreadPoolExecutor          generalPool;

    public static ThreadPoolManager getInstance ()
    {
        if (instance == null)
        {
            instance = new ThreadPoolManager();
        }
        return instance;
    }

    private ThreadPoolManager()
    {
        buffPool = new ScheduledThreadPoolExecutor(BUFFSIZE, new PriorityThreadFactory("buffPool",Thread.MIN_PRIORITY));
        generalScheduledPool = new ScheduledThreadPoolExecutor(GENERALSIZE, new PriorityThreadFactory("generalScheduledPool",Thread.NORM_PRIORITY));

        generalPool = new ThreadPoolExecutor(GENERALCORESIZE,GENERALCORESIZE + 2, 5L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("generalPool", Thread.NORM_PRIORITY));

        aiPool = new ScheduledThreadPoolExecutor(AISIZE,new PriorityThreadFactory("aiPool", Thread.NORM_PRIORITY));
    }

    public ScheduledFuture scheduleBuff (Runnable r, long delay)
    {
        try
        {
            if (delay < 0)
            {
                delay = 0;
            }
            return buffPool.schedule(r, delay,TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException e)
        {
        	e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleBuffAtFixedRate (Runnable r,long initialDelay, long interval)
    {
        try
        {
            if (interval < 0)
            {
                interval = 0;
            }
            if (initialDelay < 0)
            {
                initialDelay = 0;
            }
            return buffPool.scheduleAtFixedRate(r,initialDelay, interval, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException e)
        {
        	e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleGeneral (Runnable r, long delay)
    {
        try
        {
            if (delay < 0)
            {
                delay = 0;
            }
            return generalScheduledPool.schedule(r, delay,
                    TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException e)
        {
        	e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleGeneralAtFixedRate (Runnable r,long initialDelay, long interval)
    {
        try
        {
            if (initialDelay < 0)
            {
                initialDelay = 0;
            }
            if (interval < 0)
            {
                interval = 0;
            }
            return generalScheduledPool.scheduleAtFixedRate(r,
                    initialDelay, interval, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException e)
        {
        	e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleAI (Runnable r, long delay)
    {
        try
        {
            if (delay < 0)
            {
                delay = 0;
            }
            return aiPool.schedule(r, delay,TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException e)
        {
        	e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleAIAtFixedRate (Runnable r,long initialDelay, long interval)
    {
        try
        {
            if (interval < 0)
            {
                interval = 0;
            }
            if (initialDelay < 0)
            {
                initialDelay = 0;
            }
            return aiPool.scheduleAtFixedRate(r, initialDelay,interval, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException e)
        {
        	e.printStackTrace();
            return null;
        }
    }
    
    public void purge ()
    {
        buffPool.purge();
        generalScheduledPool.purge();
        aiPool.purge();
        generalPool.purge();
    }

    public void shutdown ()
    {
        try
        {
            buffPool.awaitTermination(1, TimeUnit.SECONDS);
            generalScheduledPool.awaitTermination(1, TimeUnit.SECONDS);
            generalPool.awaitTermination(1, TimeUnit.SECONDS);
            buffPool.shutdown();
            generalScheduledPool.shutdown();
            generalPool.shutdown();

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    public void executeTask (Runnable r)
    {
        generalPool.execute(r);
    }
}