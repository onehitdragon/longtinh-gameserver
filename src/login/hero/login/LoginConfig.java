package hero.login;


import org.dom4j.Element;

import yoyo.service.base.AbsConfig;

public class LoginConfig extends AbsConfig
{
    public String rmi_url;
    public int    rmi_port;
    public String rmi_object;

    @Override
    public void init (Element _xmlNode) throws Exception
    {
        Element element = _xmlNode.element("login");
        rmi_port = Integer.parseInt(element.elementTextTrim("port"));
        rmi_url = new StringBuffer("//")
                .append(element.elementTextTrim("host")).append(":").append(
                        rmi_port).append("/").toString();
        rmi_object = element.elementTextTrim("rmi_object_name");
    }
}
