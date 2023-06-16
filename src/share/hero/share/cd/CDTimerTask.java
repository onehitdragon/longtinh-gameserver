package hero.share.cd;

import java.util.TimerTask;

/**
 * CD时间任务类
 * 
 * @author Luke chen
 * @date 2009-1-4
 */
public class CDTimerTask extends TimerTask
{
    protected CDUnit unit;

    public CDTimerTask(CDUnit unit)
    {
        this.unit = unit;
    }

    @Override
    public void run ()
    {
        // TODO Auto-generated method stub
        if (!unit.isRunTD())
            cancel();
        else
            unit.action();
    }
}
