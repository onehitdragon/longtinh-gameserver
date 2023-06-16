package yoyo.service.base.network.wrap;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoyo.core.packet.ResponseData;
import yoyo.service.base.network.NetworkConfig;
import yoyo.service.base.network.NetworkServiceImpl;
import yoyo.tools.Convertor;

public class HttpEncoder extends ProtocolEncoderAdapter
{
    private static final int OFFSET = Integer.SIZE / Byte.SIZE;

    private static final String NEWLINE               = "\r\n";

    private static final String RESPONSE_HTTP_HEAD = "HTTP/1.0 200 OK"
                                                           + NEWLINE
                                                           + "Server: YOYO"
                                                           + NEWLINE
                                                           + "Content-Type: application/octet-stream"
                                                           + NEWLINE
                                                           + "Connection: close"
                                                           + NEWLINE
                                                           + "Cache-Control: no-cache"
                                                           + NEWLINE
                                                           + "Content-Length: ";

    public void encode (IoSession session, Object message,ProtocolEncoderOutput output) throws Exception
    {        
        ResponseData rd = (ResponseData) message;
       
        byte[] data = new byte[rd.getSize() + 2];
        Convertor.short2Bytes((short) rd.getSize(), data, 0);
        data[2] = ((NetworkConfig) NetworkServiceImpl.getInstance().getConfig())
                .getGameId();
        Convertor.int2Bytes(rd.getSessionId(), data, 3);
    
        System.arraycopy(rd.getContext(), 0, data, 7, rd.getContext().length);
        data = addHttpHead(data);
        if (data == null || data.length == 0)
        {
            session.close();
        }
        int capacity = data.length;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.put(data);
   
        buffer.flip();
        output.write(buffer);

    }

    private static byte[] addHttpHead (byte[] data)
    {
    	byte[] ret = null;
        try
        {
            byte[] httpHead = new StringBuffer().append(RESPONSE_HTTP_HEAD)
                    .append(data.length + OFFSET).append(NEWLINE).append(NEWLINE)
                    .toString().getBytes();
            ret = new byte[httpHead.length + data.length];
            System.arraycopy(httpHead, 0, ret, 0, httpHead.length);
            System.arraycopy(data, 0, ret, httpHead.length,data.length);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        return ret;
    }
}
