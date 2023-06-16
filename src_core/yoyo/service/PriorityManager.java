package yoyo.service;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javolution.util.FastMap;

public class PriorityManager
{
    private Map<Integer, Priority> priorityMap = new FastMap<Integer, Priority>();

    private static PriorityManager instance;

    private PriorityManager()
    {
    }

    public static PriorityManager getInstance ()
    {
        if (instance == null)
        {
            instance = new PriorityManager();
        }
        return instance;
    }

    public void load ()
    {
        String pathXml = YOYOSystem.HOME + "conf/priority.xml";

        try
        {
            parse(pathXml);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void parse (String path) throws Exception
    {
    	File file = new File(path);
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element root = document.getRootElement();
        Iterator<Element> it = root.elementIterator("clienthandler");
        while (it.hasNext())
        {
            Element eCh = it.next();
            int mID = NumberUtils.createInteger(eCh.attributeValue("id"));
            String sPriority = eCh.attributeValue("priority");
            Priority priority = Priority.REAL_TIME;
            if (!StringUtils.isBlank(sPriority))
            {
                priority = Priority.getPriority(NumberUtils
                        .createInteger(sPriority));
            }
            priorityMap.put(mID, priority);
        }
    }

    public Priority getPriorityByMsgId (int msgId)
    {
        Priority pro = priorityMap.get(msgId);
        if (pro != null)
        {
            return pro;
        }
        return Priority.REAL_TIME;
    }
    

}
