package hero.share.cd;

import java.util.Timer;

/**
 * CD时间执行类
 * 
 * @author Luke chen
 * @date 2009-2-2
 */
public class CDTimer extends Timer
{
    public static final long WAIT_TIME = 1000;

    private static CDTimer   task      = null;

    public static CDTimer getInsctance ()
    {
        if (task == null)
            task = new CDTimer();
        return task;
    }

    public void addTask (CDTimerTask cd)
    {
        cd.unit.start();
        this.schedule(cd, WAIT_TIME, WAIT_TIME);
    }

    // public static void main(String[] str)
    // {
    // for(int i=0; i<8000; i++)
    // CDTimerTask.getInsctance().addTask(new CDTimer(2,10));
    // }
}
