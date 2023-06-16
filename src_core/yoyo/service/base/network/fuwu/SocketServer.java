package yoyo.service.base.network.fuwu;

import java.net.InetSocketAddress;


import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import yoyo.service.base.network.NetworkConfig.ConfigInfo;
import yoyo.service.base.network.wrap.YOYOCodecFactory;

public class SocketServer extends AbsYOYOServer
{

    @Override
    public void start (ConfigInfo config) throws Exception
    {
        port = config.getPort();
        ioHandler = (IoHandler) Class.forName(config.getProcess()).newInstance();
        acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors()+1);
        acceptor.getFilterChain().addLast("protocol",
                new ProtocolCodecFilter(new YOYOCodecFactory(config)));
        acceptor.setHandler(ioHandler);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("SocketServer listen on port " + port);
    }

    @Override
    public String getServerName ()
    {
        return "SocketServer";
    }

}
