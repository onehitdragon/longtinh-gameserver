package yoyo.service.tools.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.ServiceManager;
import yoyo.service.base.AbsConfig;

public class SystemLogManager extends AbsConfig
{
    public static HashMap<String, Logger> loggerMap = new HashMap<String, Logger>();
    public Logger getLoggerByName (String name)
    {
        return loggerMap.get(name);
    }
    @Override
    public void init (Element elememt) throws Exception
    {
        List logList = elememt.selectNodes("log");
        for (int i = 0; i < logList.size(); i++)
        {
            Element element = (Element) logList.get(i);
            String name = element.attributeValue("name");
            String level = element.elementTextTrim("close");
            String path = element.elementTextTrim("filePath");
            path = YOYOSystem.HOME + path;
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
            logger.setAdditivity(false);
            PatternLayout layout = new PatternLayout();
            layout.setConversionPattern(format);
            try
            {
                FileAppender fa = null;
                if (timepattern.equals(""))
                {
                    fa = new FileAppender(layout, path, false);
                }
                else
                {
                    fa = new DailyRollingFileAppender(layout, path, timepattern);
                }
                logger.addAppender(fa);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            loggerMap.put(name, logger);
        }
        String lv = elememt.valueOf("//logservice/rootLogger/close");
        String format = elememt.valueOf("//logservice/rootLogger/format");

        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern(format.trim());
        BasicConfigurator.configure(new ConsoleAppender(layout));
        Logger rootLogger = Logger.getRootLogger();
        if (lv.trim().equalsIgnoreCase("no"))
        {
            rootLogger.setLevel(Level.DEBUG);
        }
        else if (lv.equalsIgnoreCase("yes"))
        {
            rootLogger.setLevel(Level.OFF);
        }
        else
        {
            rootLogger.setLevel(Level.OFF);
        }

        loggerMap.put("root", rootLogger);

        System.out.println("SysInfoConfig init(),OK!");
    }
}
