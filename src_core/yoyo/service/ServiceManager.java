package yoyo.service;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoyo.core.event.AbsEvent;
import yoyo.core.packet.ContextData;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.base.AbsConfig;
import yoyo.service.base.AbsService;
import yoyo.service.base.session.Session;

public class ServiceManager
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static ServiceManager instance;

    private Map<Integer, AbsService> serviceMap;

    private AbsService[] sortServices;

    protected Map<String, Integer> messagMap = new FastMap<String, Integer>();

    private ServiceManager()
    {
        serviceMap = new FastMap<Integer, AbsService>();
    }

    public static ServiceManager getInstance ()
    {
        if (null == instance)
        {
            instance = new ServiceManager();
        }

        return instance;
    }
    
    public int getMsgIdByName (String name) throws Exception
    {
        Integer i = messagMap.get(name);
        if (i == null)
        {
            throw new Exception("Can not find Message by name " + name);
        }
        return i;
    }

    public AbsClientProcess getClientProcess (ContextData data) throws Exception
    {
        if (data == null)
        {
            return new ErrorProcess("ContextData null");
        }
        AbsService service = serviceMap.get(data.serviceID);

        if (service != null)
        {
            AbsClientProcess process = service.getClientProcess(data);
            if (process != null)
            {
                process.init(data);
                return process;
            }
            return new ErrorProcess("can not find ClientProcess by id:" + data.messageType);
        }
        return new ErrorProcess("can not find service by id:" + data.serviceID);
    }

    public void load () throws Exception
    {
        serviceMap.clear();
        String path = YOYOSystem.HOME + "conf/server.xml";
        loadBasic(path);
        List<SortService> srvOrders = getLoadOrder();
        String service_dir = YOYOSystem.HOME + "service/";
        for (SortService order : srvOrders)
        {
            SAXReader reader = new SAXReader();
            Document dom = reader.read(new File(service_dir + order.xml));
            parse(dom.getRootElement());
        }
        logger.info("All Service Size " + serviceMap.size());
        sort();
    }

    private List<SortService> getLoadOrder () throws Exception
    {
        String path = YOYOSystem.HOME + "conf/loadorder.xml";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();
        Iterator<Element> it = root.elementIterator("service");
        List<SortService> list = new ArrayList<SortService>();
        while (it.hasNext())
        {
            Element e = it.next();
            SortService order = new SortService();
            order.id = NumberUtils.createInteger(e.attributeValue("id"));
            order.index = NumberUtils.createInteger(e.attributeValue("index"));
            order.className = e.attributeValue("class");
            order.xml = e.getTextTrim();
            list.add(order);
        }
        Collections.sort(list);
        return list;
    }

    private void sort()
    {
        FastList<AbsService> list = new FastList<AbsService>();
        Iterator it = serviceMap.values().iterator();
        while (list.size() < serviceMap.size())
        {
            while (it.hasNext())
            {
                AbsService service = (AbsService) it.next();
                if (list.contains(service))
                {
                    continue;
                }
                int[] ids = service.getSessionDps();
                if (ids != null)
                {
                    for (int id : ids)
                    {
                        AbsService stmp = serviceMap.get(id);
                        if (!list.contains(stmp))
                        {
                            list.addLast(stmp);
                        }
                    }
                }
                list.addLast(service);
            }
        }
        sortServices = new AbsService[serviceMap.size()];
        list.toArray(sortServices);
    }


    private void loadBasic (String path) throws Exception
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();
        Element eLog = root.element("logservice");
        parse(eLog);
        Element eDB = root.element("dbservice");
        parse(eDB);
        Element eNet = root.element("networkservice");
        parse(eNet);
        Element eSsn = root.element("sessionservice");
        parse(eSsn);
    }

    private AbsService loadClass (String className)
            throws ClassNotFoundException,NoSuchMethodException,Exception
    {
        Class c = getClass().getClassLoader().loadClass(className);
        Method getinstance = c.getMethod("getInstance", null);
        AbsService impl = (AbsService) getinstance.invoke(null, null);
        return impl;
    }

    private void loadMessages (Element element, int srvId)
    {
        Element eMsges = element.element("messages");
        if (eMsges == null)
        {
            return;
        }
        Iterator it = eMsges.elementIterator();
        while (it.hasNext())
        {
            Element eMsg = (Element) it.next();
            String className = eMsg.getTextTrim();
            String sId = eMsg.attributeValue("id");
            int id = NumberUtils.createInteger(sId);
            if (srvId != ((id >> 8) & 0xff))
            {
                throw new RuntimeException("Message " + className + " is not correct!");
            }
            logger.info("load message " + className + " id:" + id);
            messagMap.put(className, id);
        }
    }


    public void onEvent (AbsEvent event) throws Exception
    {
    }

    public MonitorEvent[] monitor ()
    {
        List<MonitorEvent> list = new ArrayList<MonitorEvent>();
        if (sortServices == null)
            return null;
        for (AbsService srvc : sortServices)
        {
            list.add((MonitorEvent) srvc.montior());
        }
        MonitorEvent[] rt = new MonitorEvent[list.size()];
        list.toArray(rt);
        return rt;
    }

    private boolean parse (Element root) throws Exception
    {
        Element eService = root.element("service");
        if (eService == null)
        {
            throw new NullPointerException(root.getName() + " not define <service> tag");
        }
        String sId = eService.elementTextTrim("id");
        if (StringUtils.isBlank(sId))
        {
            throw new RuntimeException("can not find <id> tag");
        }
        int id = NumberUtils.createInteger(sId);
        if (serviceMap.containsKey(id))
        {
            return true;
        }
        String sName = eService.elementTextTrim("name");
        if (StringUtils.isBlank(sName))
        {
            throw new RuntimeException("can not find <name> tag");
        }
        Element eDepends = eService.element("dependencies");
        if (eDepends != null)
        {
            Iterator it = eDepends.elementIterator("load");
            while (it.hasNext())
            {
                Element e = (Element) it.next();
                int dpId = NumberUtils.createInteger(e.attributeValue("id"));
                if (!serviceMap.containsKey(dpId))
                {
                    return false;
                }
            }
        }

        String sClassName = eService.elementTextTrim("class");
        if (StringUtils.isBlank(sClassName))
        {
            throw new RuntimeException("can not find <class> tag");
        }
        AbsService service;
        try
        {
            service = loadClass(sClassName);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(sClassName+ " not extends AbsService");
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(sClassName + "not define getInstance() method");
        }
        if (check(id, sName))
        {
            loadMessages(root, id);
            service.initService(root);
            serviceMap.put(id, service);
            return true;
        }
        else
        {
            throw new RuntimeException(root.getName() + " id=" + sId + " or name=" + sName + " regisgted");
        }
    }

    public void createSession (Session session)
    {
        if (null != session)
        {
            if (null==sortServices)
            {
                logger.error("sortServices null");
                return;
            }
            for (int i = 0; i < sortServices.length; i++)
            {
                sortServices[i].createSession(session);
            }
        }
    }

    public void freeSession (Session session)
    {
        if ( null==sortServices)
        {
            logger.error("sortServices null");

            return;
        }

        if (null != session)
        {
            for (int i = sortServices.length - 1; i >= 0; i--)
            {
                try
                {
                    sortServices[i].sessionFree(session);
                }
                catch (Exception e)
                {
                    logger.error("service free session error ：" + sortServices[i].getName());
                    e.printStackTrace();
                }
            }
        }
        else
        {
            logger.error("free empty session");
        }
    }

    public void clean (int userID)
    {
        for (int i = sortServices.length - 1; i >= 0; i--)
        {
            try
            {
                sortServices[i].clean(userID);
            }
            catch (Exception e)
            {
                logger.error("service free session error ：" + sortServices[i].getName());
                e.printStackTrace();
            }
        }
    }

    public void dbUpdate (int userID)
    {
        for (AbsService<AbsConfig> s : serviceMap.values())
        {
            s.dbUpdate(userID);
        }
    }
    
    private boolean check (int id, String name)
    {
        for (AbsService s : serviceMap.values())
        {
            if (s.getID() == id || s.getName().equals(name))
            {
                return false;
            }
        }
        return true;
    }

    private class SortService implements Comparable<SortService>
    {
        int    id;
        int    index;
        String className;
        String xml;

        public int compareTo (SortService obj)
        {
            if (index < obj.index)
            {
                return -1;
            }
            else if (index > obj.index)
            {
                return 1;
            }
            return 0;
        }

    }
}
