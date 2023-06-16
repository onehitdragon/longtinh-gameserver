package yoyo.service.base.network.fuwu;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import yoyo.service.base.session.SessionServiceImpl;

public class RegistryIoHandler extends IoHandlerAdapter
{
    private static int id = 1;
    
    public int getID ()
    {
        return id++;
    }
    
    @Override
    public void messageReceived (IoSession session, Object message)
            throws Exception
    {
        int id = (Integer) message;
        if (id == 2008)
        {
            int sid = SessionServiceImpl.getInstance().createSession(getID(), 2);
            session.write(sid);
        }
        session.close();
    }

    @Override
    public void exceptionCaught (IoSession session, Throwable e)
            throws Exception
    {
        e.printStackTrace();
        session.close();
    }

    @Override
    public void sessionClosed (IoSession session) throws Exception
    {
    }
}
