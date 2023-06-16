package yoyo.core.process;

import yoyo.core.packet.ContextData;
import yoyo.tools.YOYOInputStream;

public abstract class AbsClientProcess implements Runnable
{
    protected ContextData contextData;

    protected YOYOInputStream yis;

    private String ip = "";
    
    public String getIp() 
    {
        return ip;
    }

    public void setIp(String ip) 
    {
        this.ip = ip;
    }
    
    public int getMessageType ()
    {
        return contextData.messageType;
    }

    public void init(ContextData data)
    {
        contextData = data;
        yis = new YOYOInputStream(data.context);
    }

    public void run ()
    {
        try
        {
            read();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public abstract void read () throws Exception;
}
