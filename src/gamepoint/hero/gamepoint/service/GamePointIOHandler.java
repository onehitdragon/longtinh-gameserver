package hero.gamepoint.service;

import java.util.Date;
import java.util.HashMap;

//import navy.util.ME2Logger;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Description:<br>
 * 
 * @author JOJO
 * @version 0.1
 */
public class GamePointIOHandler extends IoHandlerAdapter
{
    static final String REQ_TYPE   = "REQ_TYPE";

    static final String SESSION_ID = "SESSION_ID";

    static final String CALL_POINT = "callpoint";

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache
     *      .mina.core.session.IoSession, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught (IoSession _session, Throwable arg1)
            throws Exception
    {
        _session.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache
     *      .mina.core.session.IoSession, java.lang.Object)
     */
    @Override
    public void messageReceived (IoSession _session, Object _message)
            throws Exception
    {
        try
        {
            // ME2Logger.println("计费回调>>MessageReceived");
            HashMap<String, String> param = (HashMap<String, String>) _message;
            String reqType = param.get(REQ_TYPE);
            String resp = "";
            if (reqType == null || reqType.equals(""))
            {
                // ME2Logger.println("错误的报文类型: " + reqType);
                resp = "错误的报文类型:  " + reqType;
            }

            if (reqType.toLowerCase().equals(CALL_POINT))
            {
                // WAP|SMS网关回调
                resp = GamePointService.getInstance().handlerRequest(param);
            }
            else
            {
                // ME2Logger.println("未能找到对应的报文类型: " + reqType);
                resp = "未能找到对应的报文类型:  " + reqType;
            }
            if (resp == null)
            {
                // ME2Logger.println("GamePoint报文处理Handler未有返回内容, handleType: "
                // + reqType);
                resp = "GamePoint报文处理Handler未有返回内容, handleType: " + reqType;
            }
            _session.write(resp);
            CloseFuture future = _session.closeOnFlush();
            future.addListener(new IoFutureListener()
            {
                public void operationComplete (IoFuture _future)
                {
                    // ME2Logger.debug(" 返回报文 "+
                    // _future.getSession().getRemoteAddress());
                    _future.getSession().close();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#sessionCreated(org.apache
     *      .mina.core.session.IoSession)
     */
    @Override
    public void sessionCreated (IoSession session) throws Exception
    {
        // ME2Logger.debug(session.getRemoteAddress() + " connected : " + (new
        // Date()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#sessionIdle(org.apache.
     *      mina.core.session.IoSession,
     *      org.apache.mina.core.session.IdleStatus)
     */
    @Override
    public void sessionIdle (IoSession session, IdleStatus arg1)
            throws Exception
    {
        session.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(org.apache
     *      .mina.core.session.IoSession)
     */
    @Override
    public void sessionClosed (IoSession session) throws Exception
    {
        try
        {
            // log.info(session.getRemoteAddress() + " closed: "
            // + session.hashCode() + "," + (new Date()));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
