package yoyo.service.base.network.wrap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class RegistryDecoder extends CumulativeProtocolDecoder
{
    protected boolean doDecode (IoSession session, IoBuffer in,
            ProtocolDecoderOutput out) throws Exception
    {
        if (in.remaining() >= 4)
        {
            int tag = in.getInt();
            out.write(tag);
            return true;
        }
        return false;
    }
}
