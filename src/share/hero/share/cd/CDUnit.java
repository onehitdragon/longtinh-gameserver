package hero.share.cd;

/**
 * cd时间类
 * 
 * @author Luke chen
 * @date 2009-2-2
 */
public class CDUnit
{
    public static final byte SKILL      = 0;

    public static final byte MEDICAMENT = 0;

    final private int        key;

    final private int        maxTime;

    private int              curTime;

    private boolean          isRun      = false;

    // private byte type;

    private Object           lock       = new Object();

    public CDUnit(int key, int curTime, int maxTime)
    {
        this.key = key;
        this.curTime = curTime;
        this.maxTime = maxTime;
        // this.type = _type;
    }

    public void action ()
    {
        // 经过的时间 秒计算
        synchronized (lock)
        {
            if (isRun)
            {
                curTime -= 1;
                if (curTime <= 0)
                {
                    isRun = false;
                }
            }
        }
    }

    public void start ()
    {
        synchronized (lock)
        {
            isRun = true;
            if (curTime <= 0)
                curTime = maxTime;
        }
    }

    public void stop ()
    {
        synchronized (lock)
        {
            isRun = false;
        }
    }

    public void setTime (int time)
    {
        synchronized (lock)
        {
            this.curTime = time;
        }
    }

    public int getKey ()
    {
        return key;
    }

    public int getTimeBySec ()
    {
        return curTime;
    }

    public boolean isRunTD ()
    {
        synchronized (lock)
        {
            return isRun;
        }
    }

    public int getMaxTime ()
    {
        return maxTime;
    }

    // public int getID ()
    // {
    // return id;
    // }
    //
    // public void setID (int id)
    // {
    // this.id = id;
    // }

    // public byte getType()
    // {
    // return type;
    // }
}
