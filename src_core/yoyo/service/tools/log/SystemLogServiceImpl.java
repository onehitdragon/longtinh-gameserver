package yoyo.service.tools.log;

import java.util.Timer;
import java.util.TimerTask;


import org.apache.log4j.Logger;

import yoyo.core.event.AbsEvent;
import yoyo.core.packet.ContextData;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.MonitorEvent;
import yoyo.service.ServiceManager;
import yoyo.service.base.IService;
import yoyo.service.base.AbsServiceAdaptor;

public class SystemLogServiceImpl extends AbsServiceAdaptor<SystemLogManager> implements IService
{

    private static SystemLogServiceImpl instance;

    private Timer timer;

    private SystemLogServiceImpl()
    {
        config = new SystemLogManager();
        timer = new Timer();
    }

    public static SystemLogServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new SystemLogServiceImpl();
        }
        return instance;
    }
    
    public Logger getLoggerByName (String name)
    {
        if (name == null || name == "")
        {
            return config.getLoggerByName("root");
        }
        else
        {
            return config.getLoggerByName(name);
        }
    }

    @Override
    public AbsEvent montior ()
    {
        return null;
    }
    
    @Override
    public AbsClientProcess getClientProcess (ContextData data)
    {
        return null;
    }

    @Override
    public void onEvent (AbsEvent event)
    {
        String logname = ((SystemLogEvent) event).getLogName();
        Logger logger = getLoggerByName(logname);
        logger.debug(event.getContext());
    }

    @Override
    protected void start ()
    {
        timer.schedule(new MonitorTask(), 60000, 60000);
    }

    private class MonitorTask extends TimerTask
    {
        @Override
        public void run ()
        {
            Logger logger = getLoggerByName("monitor");
            logger.info("<--------------");
            MonitorEvent[] events = ServiceManager.getInstance().monitor();
            if (events != null) 
            {
	            for (MonitorEvent evt : events)
	            {
	                if (evt != null)
	                {
	                	logger.info(evt.toString());
	                }
	            }
            }
            logger.info("-------------->");
        }

    }

}
