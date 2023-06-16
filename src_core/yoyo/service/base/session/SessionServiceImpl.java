package yoyo.service.base.session;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoyo.core.event.AbsEvent;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.MonitorEvent;
import yoyo.service.ServiceManager;
import yoyo.service.base.AbsServiceAdaptor;

import javolution.util.FastList;
import javolution.util.FastMap;

public class SessionServiceImpl extends AbsServiceAdaptor<SessionConfig> implements ISessionService
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ReentrantLock countLock;
    
    private static final Random RANDOM = new Random();
    
    private static SessionServiceImpl instance;
    private Timer checkTimer;
    private FastList<Session> sessionList;
    private FastMap<Integer, Session> sessionMap;
    private FastMap<Integer, Session> uIdSessionMap;

    private int count;
    
    private SessionServiceImpl()
    {
        sessionMap = new FastMap<Integer, Session>();
        sessionList = new FastList<Session>();
        uIdSessionMap = new FastMap<Integer, Session>();
        config = new SessionConfig();
        checkTimer = new Timer();
        countLock = new ReentrantLock();
    }

    public static SessionServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new SessionServiceImpl();
        }

        return instance;
    }

    public Session getSession (int sessionID)
    {
        return sessionMap.get(sessionID);
    }

    @Override
    public AbsEvent<Map<String, String>> montior ()
    {
        MonitorEvent event = new MonitorEvent(getName());
        event.put("onlineNumber", String.valueOf(sessionList.size()));

        return event;
    }

    @Override
    protected void start ()
    {
        checkTimer.schedule(new CheckTask(), config.checkInterval, config.checkInterval);
    }
    @Override
    public int createSession (int userID, int accountID)
    {
        int inx = ResponseMessageQueue.getInstance().createItem();

        if (-1 != inx)
        {
            Session session = new Session();
            session.ID = createSessionID();
            session.index = inx;
            session.userID = userID;
            session.accountID = accountID;
            session.refreshTime = System.currentTimeMillis();

            sessionList.add(session);
            sessionMap.put(session.ID, session);
            uIdSessionMap.put(userID, session);

            return session.ID;
        }
        else
        {
            return -1;
        }
    }
    @Override
    public void freeSessionByAccountID (int accountID)
    {
        for (int i = 0; i < sessionList.size(); i++)
        {
            Session session = sessionList.get(i);

            if (session.accountID == accountID)
            {
                freeSession(session);

                break;
            }
        }
    }
    @Override
    public void initSession (Session session)
    {
        ServiceManager.getInstance().createSession(session);
    }
    @Override
    public void freeSession (Session session)
    {
        if (null != session)
        {
            try
            {
                sessionList.remove(session);
                uIdSessionMap.remove(session.userID);
                sessionMap.remove(session.ID);
                ResponseMessageQueue.getInstance().removeItem(session.index);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            ServiceManager.getInstance().dbUpdate(session.userID);
            ServiceManager.getInstance().freeSession(session);
        }
    }

    @Override
    public int getIndexBySessionID (int sessionID)
    {
        Session s = sessionMap.get(sessionID);
        if (null == s)
        {
            return -1;
        }
        return s.index;
    }
    
    @Override
    public int getIndexByUserID (int userID)
    {
        Session s = uIdSessionMap.get(userID);
        if (s == null)
        {
            return -1;
        }
        return s.index;
    }
    
    public Session getSessionByID (int sessionID)
    {
        return sessionMap.get(sessionID);
    }
    
    public void fireSessionFree (int sessionID)
    {
        Session session = sessionMap.get(sessionID);

        if (null != session)
        {
            try
            {
                sessionList.remove(session);
                uIdSessionMap.remove(session.userID);
                sessionMap.remove(session.ID);
                ResponseMessageQueue.getInstance().removeItem(session.index);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            ServiceManager.getInstance().dbUpdate(session.userID);
            ServiceManager.getInstance().freeSession(session);
        }
    }



    private int createSessionID ()
    {
        int newID = 0;
        Calendar calendar = Calendar.getInstance();

        newID |= (calendar.get(Calendar.DAY_OF_MONTH) & 0x3) << 30;
        newID |= (calendar.get(Calendar.HOUR_OF_DAY) & 0x1f) << 25;
        newID |= (calendar.get(Calendar.MINUTE) & 0x3b) << 19;
        newID |= (calendar.get(Calendar.SECOND) & 0x3b) << 13;
        newID |= (count & 0x1f) << 8;
        newID |= RANDOM.nextInt() & 0xff;

        try
        {
            countLock.lock();
            count++;
            if (count == Integer.MAX_VALUE)
            {
                count = 1;
            }
        }
        finally
        {
            countLock.unlock();
        }

        return newID;
    }

    private class CheckTask extends TimerTask
    {
        @Override
        public void run ()
        {
            try
            {
                Session session;
                long now = System.currentTimeMillis();

                for (int i = 0; i < sessionList.size();)
                {
                    session = sessionList.get(i);
                    long time = now - session.refreshTime;
                    if (time > config.maxUnActiveTime)
                    {
                        freeSession(session);
                    }
                    else
                    {
                        i++;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    class MonitorInfo
    {
        int  sessionID;
        long lastCleanTime;
        int  maliceTimes;
    }
}
