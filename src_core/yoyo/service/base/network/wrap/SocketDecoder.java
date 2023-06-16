package yoyo.service.base.network.wrap;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoyo.core.packet.ContextData;
import yoyo.service.base.network.fuwu.AbsYOYOServer;

public class SocketDecoder extends CumulativeProtocolDecoder
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final int HEAD_LENGTH = 2;

    @Override
    protected boolean doDecode (IoSession session, IoBuffer input,ProtocolDecoderOutput output) throws Exception
    {
        if (input.prefixedDataAvailable(HEAD_LENGTH))
        {
        	try 
        	{
                short packageLen = input.getShort();
                if (input.remaining() < packageLen)
                {
                    return false;
                }
                byte gameId = input.get();
                int id = input.getInt();

                byte msgcount = input.get();
                ContextData[] cds = new ContextData[msgcount];
                for (int i = 0; i < msgcount; i++)
                {
                    short msgLen = input.getShort();
                    short msgId = input.getShort();

                    byte[] body = new byte[msgLen-2];
                    input.get(body);
                    cds[i] = new ContextData(AbsYOYOServer.SOCKET, id, gameId, msgId,body);
                }
                output.write(cds);
			} 
        	catch (Exception e) {
				logger.error("decode出错", e);
				e.printStackTrace();
			}

            return true;
        }
        return false;
    }

}
