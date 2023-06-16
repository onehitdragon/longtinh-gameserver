package hero.log.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LogConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-2-24 下午03:04:58
 * @描述 ：
 */

public class LogConfig extends AbsConfig
{
    public static HashMap<String, Logger> myLogger = new HashMap<String, Logger>();

    @SuppressWarnings("unchecked")
    @Override
    public void init (Element _root) throws Exception
    {
        List nodeList = _root.element("logservice").selectNodes("log");
        for (int i = 0; i < nodeList.size(); i++)
        {
            Element element = (Element) nodeList.get(i);
            String name = element.attributeValue("name");
            String level = element.elementTextTrim("close");
            String path = YOYOSystem.HOME + element.elementTextTrim("filePath");
            String timepattern = element.elementTextTrim("timePattern");
            String format = element.elementTextTrim("format");

            Logger logger = Logger.getLogger(name.trim());
            if (level.equalsIgnoreCase("no"))
            {
                logger.setLevel(Level.DEBUG);
            }
            else if (level.equalsIgnoreCase("yes"))
            {
                logger.setLevel(Level.OFF);
            }
            else
            {
                logger.setLevel(Level.OFF);
            }
            logger.setAdditivity(false);// 设置不继承
            PatternLayout layout = new PatternLayout();
            layout.setConversionPattern(format);// 设置格式
            try
            {
                File logFile = new File(path);
                if (!logFile.exists())
                {
                    logFile.getParentFile().mkdirs();
                }
                FileAppender fa = null;
                if (timepattern.equals(""))
                {
                    fa = new FileAppender(layout, path, false);// true->文件递增|false->文件覆盖

                }
                else
                {
                    fa = new org.apache.log4j.DailyRollingFileAppender(layout,
                            path, timepattern);
                }
                logger.addAppender(fa);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            myLogger.put(name, logger);
        }
    }

    /**
     * 根据Logger的名字获得Logger
     * 
     * @param name
     * @return
     */
    public Logger getLogger (String name)
    {
        return myLogger.get(name);
    }
}
