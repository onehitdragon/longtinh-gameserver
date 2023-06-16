package hero.charge.net;

import java.util.HashMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 HttpRequest.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-7-1 上午09:20:55
 * @描述 ：
 */

public class HttpRequest
{
    private String                        method;

    private String                        uri;

    public String                         briefURI;
    
    private String                          content;

    private HashMap<String, String> uriParams = new HashMap<String, String>();

    public String getRequestMethod ()
    {
        return method;
    }

    public void setRequestMethod (String method)
    {
        this.method = method;
    }

    public String getRequestURI ()
    {
        return uri;
    }

    public void setRequestURI (String uri)
    {
        this.uri = uri;
    }

    public void setBriefRequestURI (String _briefURI)
    {
        this.briefURI = _briefURI;
    }

    public String getBriefRequestURI ()
    {
        return this.briefURI;
    }

    public final void setURIParam (String key, String value)
    {
        if (key != null)
        {
            if (value == null)
                uriParams.remove(key);
            else
                uriParams.put(key, value);
        }
    }

    public final String getURIParam (String key)
    {
        return key == null ? null : (String) uriParams.get(key);
    }

    /*
     * public final Map getURIParams () { return
     * Collections.unmodifiableMap(uriParams); }
     */

    @Override
    public String toString ()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(method).append(" ");
        buffer.append(uri).append(" ");
        buffer.append(""/* getVersion() */).append("\r\n");
        buffer.append(super.toString());
        return buffer.toString();
    }

    public void addParam (String _name, String _value)
    {
        // TODO Auto-generated method stub
        uriParams.put(_name, _value);
    }

    public String getParam (String _name)
    {
        // TODO Auto-generated method stub
        if (uriParams.containsKey(_name))
        {
            return uriParams.get(_name);
        }
        return null;
    }
    
    public void setContent(String _content)
    {
        content = _content;
    }

    public String getContent ()
    {
        // TODO Auto-generated method stub
        return content;
    }
    
    public HashMap<String, String> getParamsMap()
    {
        return uriParams;
    }
}
