package yoyo.service.base;

import yoyo.core.event.AbsEvent;
import yoyo.service.MonitorEvent;
import yoyo.service.base.session.Session;

public abstract class AbsServiceAdaptor<T extends AbsConfig> extends AbsService<T>
{
    @Override
    public AbsEvent montior ()
    {
        MonitorEvent event = new MonitorEvent(getName());
        return event;
    }
    @Override
    public void onEvent (AbsEvent event)
    {

    }
    @Override
    protected void start ()
    {
    }
    @Override
    public void createSession (Session seesion)
    {
    }
    @Override
    public void sessionFree (Session seesion)
    {
    }
    @Override
    public void dbUpdate (int userID)
    {
    }
    @Override
    public void clean (int userID)
    {
    }
}
