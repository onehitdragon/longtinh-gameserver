package hero.gamepoint.service;

import java.net.InetSocketAddress;

import me2.service.basic.net.NetConfig.Config;
import me2.service.basic.net.codec.ME2CodecFactory;
import me2.service.basic.net.server.ME2Server;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * Description:<br>
 * @author JOJO
 * @version 0.1
 */
public class GamePointServer extends ME2Server
{

    @Override
    public String getServerName ()
    {
        // TODO Auto-generated method stub
        return "GamePointServer";
    }

    @Override
    public void start (Config _config) throws Exception
    {
        port = _config.getPort();
        handler = (IoHandler) Class.forName(_config.getHandler()).newInstance();
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("protocol",
                new ProtocolCodecFilter(new ME2CodecFactory(_config)));
        acceptor.setHandler(handler);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.bind(new InetSocketAddress(port));
        log.info("GamePointServer is listenig at port " + port);
        
    }

}
