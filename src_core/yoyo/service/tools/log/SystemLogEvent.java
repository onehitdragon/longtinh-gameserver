package yoyo.service.tools.log;

import yoyo.core.event.AbsEvent;

public class SystemLogEvent extends AbsEvent
{
    private String logName;

    {
        this.setDest("logservice");
    }

    public String getLogName ()
    {
        return logName;
    }

    public void setLogName (String logName)
    {
        this.logName = logName;
    }
}
