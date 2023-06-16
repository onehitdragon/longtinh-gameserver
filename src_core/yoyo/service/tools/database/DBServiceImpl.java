package yoyo.service.tools.database;

import java.sql.Connection;
import java.sql.DriverManager;

import yoyo.core.event.AbsEvent;
import yoyo.core.packet.ContextData;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.MonitorEvent;
import yoyo.service.base.IService;
import yoyo.service.base.AbsServiceAdaptor;

public class DBServiceImpl extends AbsServiceAdaptor<DBConfig> implements IService
{
    private static DBServiceImpl instance = null;

    private DBServiceImpl()
    {
        config = new DBConfig();
    }

    public static DBServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new DBServiceImpl();
        }
        return instance;
    }

    public void onEvent (AbsEvent event)
    {
    }

    @Override
    public AbsClientProcess getClientProcess (ContextData data)
    {
        return null;
    }

    @Override
    public AbsEvent montior ()
    {
        MonitorEvent event = new MonitorEvent(getName());
        event.put("vinh - Active Connection Count", String.valueOf(config.ds.getNumActive()));
        event.put("vinh - Avaliable Connection Count", String.valueOf(config.ds.getNumIdle()));
        event.put("vinh - Connection Count", String.valueOf(config.ds.getNumActive()));
        event.put("vinh - Maxximum Connection Count", String.valueOf(config.ds.getMaxIdle()));
        
        return event;
    }

    @Override
    protected void start ()
    {
    }

    public final Connection getConnection ()
    {
        Connection con = null;
        try
        {
           con = config.ds.getConnection();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return con;
    }
}
