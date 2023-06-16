package yoyo.service.base.network.fuwu;

import java.io.IOException;
import java.net.InetSocketAddress;


import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import yoyo.service.base.network.NetworkConfig.ConfigInfo;
import yoyo.service.base.network.wrap.YOYOCodecFactory;

public class HttpServer extends AbsYOYOServer
{
    @Override
    public void start (ConfigInfo _config) throws Exception
    {
        port = _config.getPort();
        ioHandler = (IoHandler) Class.forName(_config.getProcess()).newInstance();
        acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors()+1);
        acceptor.getFilterChain().addLast("protocol",new ProtocolCodecFilter(new YOYOCodecFactory(_config)));
        acceptor.setHandler(ioHandler);
        acceptor.getSessionConfig().setSoLinger(-1);
        try
        {
            acceptor.bind(new InetSocketAddress(port));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("HttpServer listen on port " + port);
    }

    @Override
    public String getServerName ()
    {
        return "HttpServer";
    }

}
