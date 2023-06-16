package hero.gamepoint.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;

//import navy.util.ME2Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Description:
 * 
 * @author Insunny
 * @version 0.1
 */
public class HttpDecoder extends CumulativeProtocolDecoder
{
    private static final CharsetDecoder decoder = Charset.forName("ISO-8859-1")
                                                        .newDecoder();

    /*
     * (non-Javadoc)
     * @see
     * org.apache.mina.filter.codec.CumulativeProtocolDecoder#doDecode(org.apache
     * .mina.common.IoSession, org.apache.mina.common.IoBuffer,
     * org.apache.mina.filter.codec.ProtocolDecoderOutput)
     */
    @Override
    protected boolean doDecode (IoSession _session, IoBuffer _buffer,
            ProtocolDecoderOutput _out) throws Exception
    {
        try
        {
            String request = _buffer.getString(decoder);
            //ME2Logger.debug(request);
            HashMap<String, String> param = parseHttpRequest(request);
            _out.write(param);
            _out.flush();
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }

    private static final String PARAMETER_CONNECT_CHAR  = "&";

    private static final String PARAMETER_EVALUATE_CHAR = "=";

    public static final String  CRLF                    = "\r\n";

    private static HashMap<String, String> parseHttpRequest (String _requestContent)
    {
        int start, end;
        if (-1 != (start = _requestContent.indexOf("GET /")) && -1 != (end = _requestContent.indexOf("HTTP/")))
        {

            HashMap<String, String> parameterMap = new HashMap<String, String>();

            String coreRequest = _requestContent.substring(start + 5, end).trim();
            int reqIndex = coreRequest.indexOf("?");
            if (reqIndex < 0)
            {// 没有参数
                parameterMap.put(GamePointIOHandler.REQ_TYPE, coreRequest);
            }
            else
            {
                String reqType = coreRequest.substring(0, reqIndex);

                parameterMap.put(GamePointIOHandler.REQ_TYPE, reqType);
                coreRequest = coreRequest.substring(reqIndex + 1);
                String[] parameters = coreRequest.split(PARAMETER_CONNECT_CHAR);

                for (String parameterExprion : parameters)
                {
                    int evaluateCharIndex = parameterExprion.indexOf(PARAMETER_EVALUATE_CHAR);
                    if (-1 != evaluateCharIndex)
                    {
                        String parameterName = parameterExprion.substring(0,evaluateCharIndex).trim();
                        String parameterValue = parameterExprion.substring(evaluateCharIndex + 1).trim();
                        try
                        {
                            parameterValue = java.net.URLDecoder.decode(parameterValue, "UTF-8");
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }
                        parameterMap.put(parameterName, parameterValue);
                    }
                }
            }
            return parameterMap;
        }

        return null;
    }
}
