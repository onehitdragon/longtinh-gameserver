package hero.map.broadcast;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 IBroadcastThread.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-9-23 上午11:40:15
 * @描述 ：广播线程基类
 */

public abstract class IBroadcastThread implements Runnable
{
    /**
     * 等待启动时间
     * 
     * @return
     */
    public abstract long getStartTime ();

    /**
     * 执行间隔时间
     * 
     * @return
     */
    public abstract long getExcuteInterval ();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run ()
    {
        try
        {
            Thread.sleep(getStartTime());
        }
        catch (InterruptedException e)
        {

        }
        while (true)
        {
            try
            {
                broadcast();
            }
            catch (Exception e)
            {

            }
            try
            {
                Thread.sleep(getExcuteInterval());
            }
            catch (InterruptedException e)
            {

            }
        }
    }

    /**
     * 广播任务类需要实现具体逻辑
     */
    public abstract void broadcast ();
}
