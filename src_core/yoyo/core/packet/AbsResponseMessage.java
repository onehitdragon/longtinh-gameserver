package yoyo.core.packet;

import java.io.IOException;

import yoyo.service.ServiceManager;
import yoyo.tools.Convertor;
import yoyo.tools.YOYOOutputStream;

public abstract class AbsResponseMessage
{

    protected YOYOOutputStream yos = new YOYOOutputStream();

    private boolean isCached;
    
    public int getSize()
    {
    	return yos.size();
    }
    
    public int getID () throws Exception
    {
        int id = ServiceManager.getInstance().getMsgIdByName(getName());

        return id;
    }

    protected String getName ()
    {
        return getClass().getName();
    }

    public byte[] getBytes ()
    {
        try
        {
            if (!isCached)
            {
                flush();
            }
            byte[] data = yos.getBytes();
            Convertor.short2Bytes((short) (data.length - 2), data, 0);
            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                yos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void flush ()
    {
        try
        {
            yos.writeShort(0);
            yos.writeShort(getID());
            write();
            yos.flush();
            isCached = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected abstract void write() throws IOException;

    public abstract int getPriority ();
}
