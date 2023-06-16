package hero.charge.net;

import hero.charge.net.handler.ChargeHttpHandler;
import hero.charge.service.ChargeServiceImpl;
import hero.share.service.LogWriter;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ServerSocket.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-22 上午10:22:05
 * @描述 ：
 */

public class ServerSocket
{
    private static ServerSocket instance;

    /**
     * 继承于mina的处理器
     */
    protected SocketAcceptor    acceptor;

    /**
     * 继承于mina的逻辑处理接口
     */
    protected IoHandler         handler;

    /**
     * 获取唯一实例
     * 
     * @return
     */
    public static ServerSocket getInstance ()
    {
        if (instance == null)
        {
            instance = new ServerSocket();
        }
        return instance;
    }

    public void start ()
    {
        try
        {
            int port = ChargeServiceImpl.getInstance().getConfig().port_callback;
            handler = new ChargeHttpHandler();
            acceptor = new NioSocketAcceptor(Runtime.getRuntime()
                    .availableProcessors() + 1);
            acceptor.getFilterChain().addLast("protocol",
                    new ProtocolCodecFilter(new ChargeCodeFactory()));
            acceptor.setHandler(handler);
            acceptor.getSessionConfig().setSoLinger(-1);
            acceptor.bind(new InetSocketAddress(port));

            LogWriter.println("Charge callback httpserver is listenig at port " + port);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
