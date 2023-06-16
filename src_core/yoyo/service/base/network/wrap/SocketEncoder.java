package yoyo.service.base.network.wrap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import yoyo.core.packet.ResponseData;
import yoyo.service.base.network.NetworkConfig;
import yoyo.service.base.network.NetworkServiceImpl;

public class SocketEncoder extends ProtocolEncoderAdapter
{

    public void encode (IoSession session, Object message,ProtocolEncoderOutput output) throws Exception
    {
        ResponseData rd = (ResponseData) message;
        int capacity = rd.getSize() + 2;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.putShort((short) rd.getSize());
        buffer.put(((NetworkConfig) NetworkServiceImpl.getInstance().getConfig()).getGameId());
        buffer.putInt(rd.getSessionId());
        buffer.put(rd.getContext());
        buffer.flip();
        output.write(buffer);
    }

}
