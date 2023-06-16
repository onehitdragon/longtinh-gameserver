package hero.charge.net;

//import me2.core.data.ResultData;
//import me2.service.basic.net.NetConfig;
//import me2.service.basic.net.NetServiceImpl;
//import me2.util.BaseDataConvertor;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeEncoder.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-22 上午10:25:04
 * @描述 ：编码类
 */

public class ChargeEncoder extends ProtocolEncoderAdapter
{
    private static final String CRLN      = "\r\n";

    private static final String HTTP_HEAD = "HTTP/1.1 200 OK "
                                                  + CRLN
                                                  + "Server: WangZheYingXiong Digifun"
                                                  + CRLN
                                                  + "Keep-Alive: timeout=15, max=100"
                                                  + CRLN
                                                  + "Connection: Keep-Alive"
                                                  + CRLN
                                                  + "Content-Type: application/octet-stream"
                                                  + CRLN + "Content-Length: ";

    public void encode (IoSession _session, Object _message,
            ProtocolEncoderOutput _out) throws Exception
    {
        // TODO Auto-generated method stub
        String rd = (String) _message;
        byte[] temp = rd.getBytes();
        temp = addHttpHead(temp);

        IoBuffer buffer = IoBuffer.allocate(temp.length, false);
        buffer.put(temp);
        buffer.flip();
        _out.write(buffer);
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
