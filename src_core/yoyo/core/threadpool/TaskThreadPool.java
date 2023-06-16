package yoyo.core.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskThreadPool
{

    private static TaskThreadPool instance = null;

    private int minSize = 5;
    private int maxSize = 10;
    private ThreadPoolExecutor exeor;

    public static TaskThreadPool getInstance ()
    {
        if (instance == null)
        {
            instance = new TaskThreadPool();
        }
        return instance;
    }

    TaskThreadPool()
    {
        exeor = new ThreadPoolExecutor(minSize, maxSize, 0,TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.CallerRunsPolicy());
    }
    
    public synchronized void addTask (Runnable task)
    {
        exeor.execute(task);
    }

    public synchronized void shutdown ()
    {
        exeor.shutdown();
    }

    public void getActiveCount ()
    {
        exeor.getActiveCount();
    }

    public synchronized boolean remove (Runnable task)
    {
        return exeor.remove(task);
    }

}
