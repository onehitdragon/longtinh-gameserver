package yoyo.core.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory implements ThreadFactory
{
    private int priority;
    private String poolName;
    private ThreadGroup threadGroup;

    private AtomicInteger threadCount = new AtomicInteger(1);
    
    public ThreadGroup getThreadGroup ()
    {
        return threadGroup;
    }

    public PriorityThreadFactory(String name, int priority)
    {
        this.priority = priority;
        this.poolName = name;
        threadGroup = new ThreadGroup(poolName);
    }

    public Thread newThread (Runnable r)
    {
        Thread t = new Thread(threadGroup, r);
        t.setName(poolName + "-" + threadCount.getAndIncrement());
        t.setPriority(priority);
        return t;
    }
}
