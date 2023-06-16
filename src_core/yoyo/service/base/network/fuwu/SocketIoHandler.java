package yoyo.service.base.network.fuwu;

import java.net.InetSocketAddress;


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

public class SocketIoHandler extends IoHandlerAdapter
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public void messageReceived (IoSession ioSession, Object message)throws Exception
	{
		ContextData[] cds = (ContextData[]) message;
		int sessionID = cds[0].sessionID;
		Session session = SessionServiceImpl.getInstance().getSession(sessionID);
		ResponseData rd = null;
		
		if (null != session)
		{
		    session.refreshTime = System.currentTimeMillis();
			int len = cds.length;	
		    for (ContextData cd : cds)
		    {
		        Priority priority = PriorityManager.getInstance().getPriorityByMsgId(cd.messageID);
		        AbsClientProcess ch = ServiceManager.getInstance().getClientProcess(cd);
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
		    if (rd != null) 
		    {
		        if (rd.isErrorMessage()) 
		        {
		        	logger.warn("sessionID = " + sessionID + "; nickname=" + session.nickName 
		        			+ "的用户被通知掉线,但他的session并不为NULL");
				}
			}
		    else 
		    {
				logger.error("从消息对象get出来的消息为null,应该尽快排除这样的情况:" 
						+ session.index);
			}
		}
		else
		{
			logger.info("session is null sessionid=" + String.valueOf(sessionID));
		    try 
		    {
		    	rd = ResponseMessageQueue.getInstance().getErrorData();
		    	String remoteIP = ((InetSocketAddress)ioSession.getRemoteAddress()).getAddress().getHostAddress();
		    	logger.warn("!!!to yoyo-->通过sessionID获得session为null. sessionID=" + sessionID + "他的IP:" + remoteIP);
		    	ioSession.closeOnFlush();
			} catch (Exception e) {
				logger.error("messageReceived error:", e);
				e.printStackTrace();
			}
		}
		
		rd.setSessionID(sessionID);
		ioSession.write(rd);
	
	}

    public void exceptionCaught (IoSession session, Throwable ex)throws Exception
    {
        session.close();
    }

    public void sessionIdle (IoSession session, IdleStatus status)throws Exception
    {
        session.close();
    }
    
    public void sessionClosed (IoSession session) throws Exception
    {
    	session.close();
    }

    public void sessionCreated (IoSession session) throws Exception
    {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 120);
    }
}
