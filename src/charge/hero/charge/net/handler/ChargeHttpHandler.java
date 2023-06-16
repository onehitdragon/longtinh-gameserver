package hero.charge.net.handler;

import hero.charge.net.parse.detail.ChargeListRefreshParse;
import hero.charge.net.parse.detail.RechargeFeedbackParse;
import hero.log.service.LogServiceImpl;

import java.util.HashMap;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeHttpHandler.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-22 上午10:22:58
 * @描述 ：处理HTTP请求及响应
 */
public class ChargeHttpHandler extends IoHandlerAdapter
{
    public void exceptionCaught (IoSession _session, Throwable ex)
            throws Exception
    {
        _session.close();
    }

    
    @SuppressWarnings("unchecked")
    public void messageReceived (IoSession _ioSession, Object _message)
            throws Exception
    {
        try
        {
            HashMap<String, String> param = (HashMap<String, String>) _message;

            String reqType = param.get("REQ_TYPE");
            String resp = "";
            if (reqType == null || reqType.equals(""))
            {
                LogServiceImpl.getInstance().chargeLog("空的handleType!");
            }
            else
            {
                if (reqType.toLowerCase().equals("shenyu"))
                {
                    String typeValue = param.get("type");

                    if (typeValue != null && !typeValue.isEmpty())
                    {
                        // 充值结果成功与否的消息
                        if (typeValue.equals("1"))
                        {
                            LogServiceImpl.getInstance().chargeLog("充值结果回调");
                            new RechargeFeedbackParse(param);
                        }
                        // 计费列表刷新
                        else if (typeValue.equals("2"))
                        {
                            String result = param.get("result");
                            if (!result.isEmpty())
                            {
                                LogServiceImpl.getInstance().chargeLog("计费列表刷新回调");
                                new ChargeListRefreshParse(result);
                            }
                        }

                        resp = "OK";
                    }
                }
                else
                {
                    // 未能找到对应的报文类型
                    resp = "FAIL";
                }
            }

            _ioSession.write(resp);
            CloseFuture future = _ioSession.closeOnFlush();
            future.addListener(new IoFutureListener()
            {
                public void operationComplete (IoFuture _future)
                {
                    _future.getSession().close();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
}
