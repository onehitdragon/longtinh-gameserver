package hero.charge.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeDecoder.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-22 上午10:26:07
 * @描述 ：解码类
 */

public class ChargeDecoder extends CumulativeProtocolDecoder
{
    private static final CharsetDecoder decoder                 = Charset
                                                                        .forName(
                                                                                "ISO-8859-1")
                                                                        .newDecoder();

    private static final Pattern        PATTERN                 = Pattern
                                                                        .compile(" ");

    private static final String         PARAMETER_CONNECT_CHAR  = "&";

    private static final String         PARAMETER_EVALUATE_CHAR = "=";

    @Override
    protected boolean doDecode (IoSession _session, IoBuffer _buffer,
            ProtocolDecoderOutput _out) throws Exception
    {
        // TODO Auto-generated method stub
        try
        {
            String request = _buffer.getString(decoder);
            // ME2Logger.debug(request);
            HashMap<String, String> param = parseHttpRequest(request);
            _out.write(param);
            _out.flush();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 将请求的URL批成一一对应的字段存于HashMap中 如：http://www.xxx.com/test?a=1&b=3d&c=k 名 值
     * 索引0: REQ_TYPE test 索引1: a 1 索引2: b 3d 索引3: c k
     * 
     * @param _requestContent
     * @return
     */
    private static HashMap<String, String> parseHttpRequest (
            String _requestContent)
    {
        HttpRequest request = new HttpRequest();
        try
        {
            // 解析uri，如http://221.130.55.82:8003/shenyu，解析出shenyu
            int start, end;
            if (-1 != (start = _requestContent.indexOf(" /"))
                    && -1 != (end = _requestContent.indexOf("HTTP/")))
            {
                String coreRequest = _requestContent.substring(start + 2, end)
                        .trim();
                int reqIndex = coreRequest.indexOf("?");
                if (reqIndex < 0)
                {
                    // 没有参数
                    request.addParam("REQ_TYPE", coreRequest);
                }
                else
                {
                    request.addParam("REQ_TYPE", coreRequest.substring(0,
                            reqIndex));
                }

            }

            // 打印HTTP请求
            //System.out.println(_requestContent);

            // 解析出各参数
            int index = _requestContent.indexOf("\r\n\r\n");
            if (index > 0)
            {
                String[] headers = _requestContent.substring(0, index).split(
                        "\r\n");

                parseHeaders(request, headers);

                String contentLen = request.getParam("Content-Length");

                if (contentLen != null)
                {
                    int len = Integer.parseInt(contentLen);

                    String content = _requestContent.substring(_requestContent
                            .length()
                            - len);
                    request.setContent(content);

                    if (request.getRequestMethod().equals("POST"))
                    {
                        parseParam(request, new String(request.getContent()
                                .getBytes(), "ISO-8859-1"));
                    }
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return request.getParamsMap();
    }

    /**
     * 解析头
     * 
     * @param request
     * @param headers
     */
    private static void parseHeaders (HttpRequest request, String[] headers)
    {
        // every time use String.split will construct a new Pattern object
        String[] s = PATTERN.split(headers[0]);
        request.setRequestMethod(s[0]);
        request.setRequestURI(s[1]);
        parseURI(request, s[1]); // GET请求的

        for (int i = 1; i < headers.length; i++)
        {
            int index = headers[i].indexOf(":");
            // Http头
            request.addParam(headers[i].substring(0, index).trim(), headers[i]
                    .substring(index + 1).trim());
        }
    }

    /**
     * 解析URI，如果是GET提交，则解析GET中的参数
     * 
     * @param _request
     * @param _requestContent
     */
    private static void parseURI (HttpRequest _request, String _requestContent)
    {
        String coreRequest = _requestContent;
        int reqIndex = coreRequest.indexOf("?");
        if (reqIndex < 0)
        {// 没有参数
            _request.setBriefRequestURI(coreRequest);
        }
        else
        {
            String reqType = coreRequest.substring(0, reqIndex);

            _request.setBriefRequestURI(reqType);
            coreRequest = coreRequest.substring(reqIndex + 1);
            parseParam(_request, coreRequest);
        }
    }

    /**
     * 解析参数
     * 
     * @param _request
     * @param _requestContent
     */
    private static void parseParam (HttpRequest _request, String _requestContent)
    {
        String[] parameters = _requestContent.split(PARAMETER_CONNECT_CHAR);
        for (String parameterExprion : parameters)
        {
            int evaluateCharIndex = parameterExprion
                    .indexOf(PARAMETER_EVALUATE_CHAR);
            if (-1 != evaluateCharIndex)
            {
                String parameterName = parameterExprion.substring(0,
                        evaluateCharIndex).trim();
                String parameterValue = parameterExprion.substring(
                        evaluateCharIndex + 1).trim();
                try
                {
                    parameterValue = java.net.URLDecoder.decode(parameterValue,
                            "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                _request.setURIParam(parameterName, parameterValue);

                // 打印参数
                // System.out.println(parameterName + " : " + parameterValue);
            }
        }
    }

    /*
     * public static void main (String[] args) { ChargeDecoder cd = new
     * ChargeDecoder(); String http = "POST /shenyu HTTP/1.1"+"\r\n"+
     * "User-Agent: Jakarta Commons-HttpClient/3.1"+"\r\n"+ "Host:
     * 221.130.55.82:8003"+"\r\n"+ "Content-Length: 1617"+"\r\n"+ "Content-Type:
     * application/x-www-form-urlencoded"+"\r\n"+ "\r\n"+
     * "result=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0D%0A%3CFee%3E%0D%0A%09%3CType+id%3D%220%22+name%3D%22%E6%89%8B%E6%9C%BA%E8%AE%A1%E8%B4%B9%22%3E%0D%0A%09%09%3Cprice+fpcode%3D%22TEST1%22%3E1%3C%2Fprice%3E%0D%0A%09%09%3Cprice+fpcode%3D%22TEST2%22%3E2%3C%2Fprice%3E%0D%0A%09%3C%2FType%3E%0D%0A%09%3CType+id%3D%221%22+name%3D%22%E5%85%85%E5%80%BC%E8%AE%A1%E8%B4%B9%22%3E%0D%0A%09%09%3CCard+id%3D%221%22+name%3D%22%E7%A7%BB%E5%8A%A8%E5%85%85%E5%80%BC%E5%8D%A1%22%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E10%3C%2Fprice%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E20%3C%2Fprice%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E30%3C%2Fprice%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E50%3C%2Fprice%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E100%3C%2Fprice%3E%0D%0A%09%09%3C%2FCard%3E%0D%0A%09%09%3CCard+id%3D%222%22+name%3D%22%E8%81%94%E9%80%9A%E5%85%85%E5%80%BC%E5%8D%A1%22%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E50%3C%2Fprice%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E100%3C%2Fprice%3E%0D%0A%09%09%3C%2FCard%3E%0D%0A%09%09%3CCard+id%3D%223%22+name%3D%22%E7%94%B5%E4%BF%A1%E5%85%85%E5%80%BC%E5%8D%A1%22%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E50%3C%2Fprice%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E100%3C%2Fprice%3E%0D%0A%09%09%3C%2FCard%3E%0D%0A%09%09%3CCard+id%3D%224%22+name%3D%22%E9%AA%8F%E7%BD%91%E4%B8%80%E5%8D%A1%E9%80%9A%E5%85%85%E5%80%BC%22%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E50%3C%2Fprice%3E%0D%0A%09%09%09%3Cprice+fpcode%3D%22ABCD%22%3E100%3C%2Fprice%3E%0D%0A%09%09%3C%2FCard%3E%0D%0A%09%3C%2FType%3E%0D%0A%3C%2FFee%3E%0D%0A&type=2"; }
     */
}
