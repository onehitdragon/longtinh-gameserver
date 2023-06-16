package yoyo.service.base.network.fuwu;


import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoyo.core.packet.ContextData;
import yoyo.core.packet.ResponseData;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.core.threadpool.TaskThreadPool;
import yoyo.service.PriorityManager;
import yoyo.service.ServiceManager;
import yoyo.service.Priority;
import yoyo.service.base.session.Session;
import yoyo.service.base.session.SessionServiceImpl;

import java.net.InetSocketAddress;

public class HttpIoHandler extends IoHandlerAdapter
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void exceptionCaught (IoSession session, Throwable ex)throws Exception
    {
        session.close();
    }
    public void messageReceived (IoSession ioSession, Object message)throws Exception
    {
        ContextData[] cds = (ContextData[]) message;
        int sessionID = cds[0].sessionID;
        Session session = SessionServiceImpl.getInstance().getSession(sessionID);

        ResponseData rd = null;
        
        long receivedTime = System.currentTimeMillis();
        short key = 0;

        if (null != session)
        {
            session.refreshTime = System.currentTimeMillis();

            for (ContextData cd : cds)
            {
            	receivedTime = cd.recvTime;
            	key = cd.key;
            	
            	Priority priority = PriorityManager.getInstance().getPriorityByMsgId(cd.messageID);

                AbsClientProcess ch = ServiceManager.getInstance()
                        .getClientProcess(cd);

                try
                {
                    InetSocketAddress address = (InetSocketAddress)ioSession.getRemoteAddress();
                    if(address != null)
                    {
                        String ip = address.getAddress().getHostAddress();
                        ch.setIp(ip);
                    }
                }
                catch (Exception e)
                {
                    logger.error("get IP error,accountID="+session.accountID+",nickname="+session.nickName+",userID="+session.userID);
                }

                if (priority == Priority.REAL_TIME)
                {
                    ch.run();
                }
                else
                {
                    TaskThreadPool.getInstance().addTask(ch);
                }
            }

            rd = ResponseMessageQueue.getInstance().get(session.index);
            if (rd.isErrorMessage()) 
            {
            	logger.warn("sessionID = " + sessionID + "; nickname=" + session.nickName + "的用户被通知掉线,但他的session并不为NULL");
			}
        }
        else
        {
        	logger.warn("!!!to yoyo-->通过sessionID获得session为null. sessionID="+sessionID);
            rd = ResponseMessageQueue.getInstance().getErrorData();
        }

        rd.setSessionID(sessionID);
        
        rd.setRecvTime(receivedTime);
        rd.setKey(key);
        
        ioSession.write(rd);
        CloseFuture future = ioSession.closeOnFlush();
        future.addListener(new IoFutureListener()
        {
            public void operationComplete (IoFuture future)
            {
                future.getSession().close();
            }
        });

    }

    public void sessionCreated (IoSession session) throws Exception
    {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
    }

    public void sessionIdle (IoSession session, IdleStatus arg1)
            throws Exception
    {
        session.close();
    }

	@Override
	public void messageSent(IoSession session, Object message) throws Exception 
	{
		ResponseData rd = (ResponseData)message;
		super.messageSent(session, message);	
	}
    
    
}
