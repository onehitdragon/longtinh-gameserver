package hero.gamepoint.service;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class HttpEncoder extends ProtocolEncoderAdapter
{
    private static final int    OFFSET    = Integer.SIZE / Byte.SIZE;

    private static final String CRLN      = "\r\n";

    private static final String HTTP_HEAD = "HTTP/1.1 200 OK "
                                                  // + CRLN
                                                  // +
                                                  // "Date: Wed, 25 Oct 2006 09:11:56 GMT"
                                                  + CRLN
                                                  + "Server: ShenHua Digifun"
                                                  // + CRLN
                                                  // +
                                                  // "Set-Cookie: JSESSIONID=CFF5B39C14DF99ED9BF7705149C8F9B7; Path=/mov"
                                                  + CRLN
                                                  + "Keep-Alive: timeout=15, max=100"
                                                  + CRLN
                                                  + "Connection: Keep-Alive"
                                                  + CRLN
                                                  + "Content-Type: application/octet-stream"
                                                  + CRLN + "Content-Length: ";

    /*
     * (non-Javadoc)
     * @see
     * org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.common
     * .IoSession, java.lang.Object,
     * org.apache.mina.filter.codec.ProtocolEncoderOutput)
     */
    public void encode (IoSession _session, Object _message,
            ProtocolEncoderOutput _out) throws Exception
    {
        String resp = (String) _message;
        byte[] temp = resp.getBytes("UTF8");
        temp = addHttpHead(temp);

        IoBuffer buffer = IoBuffer.allocate(temp.length, false);
        buffer.put(temp);
        buffer.flip();
        _out.write(buffer);
        // _out.flush();
    }

    /**
     * 为下行报文添加Http头
     * 
     * @param _data
     * @return
     */
    private static byte[] addHttpHead (byte[] _data)
    {
        try
        {
            byte[] httpHead = new StringBuffer().append(HTTP_HEAD).append(
                    _data.length).append(CRLN).append(CRLN).toString()
                    .getBytes();
            byte[] returnValue = new byte[httpHead.length + _data.length];
            System.arraycopy(httpHead, 0, returnValue, 0, httpHead.length);
            System.arraycopy(_data, 0, returnValue, httpHead.length,
                    _data.length);
            return returnValue;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
