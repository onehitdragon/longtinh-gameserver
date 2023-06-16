package yoyo.service.base.session;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import yoyo.service.base.AbsConfig;

public class SessionConfig extends AbsConfig
{
    public int checkInterval   = 5000;
    public int maxUnActiveTime = 45000;

    @Override
    public void init (Element element) throws Exception
    {
        String sCheckInterval = element.valueOf("//checkinterval");

        if (!StringUtils.isBlank(sCheckInterval))
        {
            checkInterval = Integer.valueOf(sCheckInterval);
        }

        String sMaxUnActiveTime = element.valueOf("//maxunactivetime");

        if (!StringUtils.isBlank(sMaxUnActiveTime))
        {
            maxUnActiveTime = Integer.valueOf(sMaxUnActiveTime);
        }
    }

}
