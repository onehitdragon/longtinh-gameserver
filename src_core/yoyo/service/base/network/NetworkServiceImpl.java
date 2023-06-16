package yoyo.service.base.network;

import yoyo.core.event.AbsEvent;
import yoyo.core.packet.ContextData;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.MonitorEvent;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.network.NetworkConfig.ConfigInfo;
import yoyo.service.base.network.fuwu.AbsYOYOServer;

public class NetworkServiceImpl extends AbsServiceAdaptor<NetworkConfig>
{

    private static NetworkServiceImpl instance;

    private AbsYOYOServer[] servers;

    private NetworkServiceImpl()
    {
        config = new NetworkConfig();
    }

    public static NetworkServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new NetworkServiceImpl();
        }
        return instance;
    }

    @Override
    protected void start ()
    {
        try
        {
            servers = new AbsYOYOServer[config.getServerCount()];
            
            for (int i = 0; i < servers.length; i++)
            {
                ConfigInfo conf = config.configs[i];
                servers[i] = (AbsYOYOServer) (Class.forName(conf.getServer()).newInstance());
                servers[i].start(conf);
            }
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
        
        for (AbsYOYOServer server : servers)
        {
            server.monitor(event);
        }
        
        return event;
    }

    @Override
    public void onEvent (AbsEvent event)
    {
    }
}
