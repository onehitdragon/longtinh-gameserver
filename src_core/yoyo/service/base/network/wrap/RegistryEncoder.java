package yoyo.service.base.network.wrap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class RegistryEncoder extends ProtocolEncoderAdapter
{

    public void encode (IoSession session, Object message,ProtocolEncoderOutput output) throws Exception
    {
        int id = (Integer) message;
        IoBuffer buffer = IoBuffer.allocate(4, false);
        buffer.putInt(id);
        buffer.flip();
        output.write(buffer);
    }
}
