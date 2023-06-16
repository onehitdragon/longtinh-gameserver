package yoyo.service.base.network.fuwu;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.SocketAcceptor;

import yoyo.service.MonitorEvent;
import yoyo.service.base.network.NetworkConfig.ConfigInfo;

public abstract class AbsYOYOServer
{
    public static final byte SOCKET = 1;
    public static final byte HTTP   = 2;


    protected int port;
    protected SocketAcceptor acceptor;
    protected IoHandler ioHandler;

    public void monitor (MonitorEvent event)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("ReadMessages:" + acceptor.getReadMessages() + "\n");
        sb.append("WritternMessage:" + acceptor.getWrittenMessages()+ "\n");
        sb.append("ManagedSessionCount:"+ acceptor.getManagedSessionCount() + "\n");
        sb.append("ReadBytesThroughput:"+ acceptor.getReadBytesThroughput() + "byte/s" + "\n");
        sb.append("WrittenBytesThroughput:"+ acceptor.getWrittenBytesThroughput() + "byte/s" + "\n");
        event.put(getServerName(), sb.toString());
    }
    
    public abstract void start(ConfigInfo info) throws Exception;
    public abstract String getServerName ();

}
