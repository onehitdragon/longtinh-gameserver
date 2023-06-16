package yoyo.service.base.network.wrap;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import yoyo.core.packet.ContextData;
import yoyo.service.base.network.fuwu.AbsYOYOServer;
import yoyo.tools.Convertor;

public class HttpDecoder extends CumulativeProtocolDecoder
{
    private static final CharsetDecoder decoder = Charset.forName("iso8859-1").newDecoder();

    private static String               START    = "datastart:";
    private static String               End     = "dataend";

    @Override
    protected boolean doDecode (IoSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception
    {
        String request = buffer.getString(decoder);
        int dataBegin = request.indexOf(START);
        int dataEnd = request.indexOf(End);
        if (dataBegin != -1 && dataEnd != -1)
        {
            request = request.substring(dataBegin + START.length(), dataEnd).trim();
            if (request != null)
            {
                byte[] data = Decoder.decode(request.getBytes("ISO8859-1"));
                if (data.length >= 12)
                {
                    short packageLen = Convertor.bytes2Short(data, 0);
                    byte gameId = data[2];
                    int id = Convertor.bytes2Int(data, 3);
                    
                    byte mesgNum = data[7];
                    ContextData[] cds = new ContextData[mesgNum];
                    int pos = 8;
                    for (int i = 0; i < mesgNum; i++)
                    {
                        short msgLen = Convertor.bytes2Short(data, pos);
                        short msgId = Convertor.bytes2Short(data,pos + 2);                     
                        byte[] body = new byte[msgLen - 2];
                        System.arraycopy(data, pos + 4, body, 0, body.length);
                        cds[i] = new ContextData(AbsYOYOServer.HTTP, id, gameId,msgId, body);
                        pos += (msgLen + 2);

                    }
                    output.write(cds);
                    return true;
                }
            }
        }
        return false;
    }

}
