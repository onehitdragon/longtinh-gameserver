package yoyo.service.base;

import org.dom4j.Element;

public abstract class AbsConfig
{
    public abstract void init (Element _xmlNode) throws Exception;
}
