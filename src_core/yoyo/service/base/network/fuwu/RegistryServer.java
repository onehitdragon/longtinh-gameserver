package yoyo.service.base.network.fuwu;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import yoyo.service.base.network.NetworkConfig.ConfigInfo;
import yoyo.service.base.network.wrap.YOYOCodecFactory;

public class RegistryServer extends AbsYOYOServer
{
    @Override
    public String getServerName ()
    {
        return "RegistryServer";
    }

    @Override
    public void start (ConfigInfo config) throws Exception
    {
        port = config.getPort();
        ioHandler = (IoHandler) Class.forName(config.getProcess()).newInstance();
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("protocol",
                new ProtocolCodecFilter(new YOYOCodecFactory(config)));
        acceptor.setHandler(ioHandler);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("RegistryServer listen on port " + port);
    }

}
