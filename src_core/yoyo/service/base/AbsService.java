package yoyo.service.base;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoyo.core.event.AbsEvent;
import yoyo.core.packet.ContextData;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.ServiceManager;
import yoyo.service.base.session.Session;

public abstract class AbsService<T extends AbsConfig>
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected T config;

    protected AbsPolicy policy;

    protected Map<Integer, Class> cpClassMap = new FastMap<Integer, Class>();

    private ServiceInfo info;

    public T getConfig ()
    {
        return config;
    }

    public void initService (Element element) throws Exception
    {
        defaultConfig(element);
        if (null != config)
        {
            config.init(element);
        }
        start();
        logger.info(info.toString());
    }

    private void defaultConfig (Element element) throws Exception
    {
        Element eService = element.element("service");
        info = new ServiceInfo();
        info.id = NumberUtils.createInteger(eService.elementTextTrim("id"));
        info.name = eService.elementTextTrim("name");
        info.version = eService.elementTextTrim("version");
        info.author = eService.elementTextTrim("author");
        info.description = eService.elementTextTrim("description");
        List list = eService.selectNodes("//dependencies/ssn");
        if (list != null && list.size() > 0)
        {
            info.ssnDp = new int[list.size()];
            for (int i = 0; i < list.size(); i++)
            {
                Element eSsn = (Element) list.get(i);
                info.ssnDp[i] = NumberUtils.createInteger(eSsn.attributeValue("id"));
            }
        }

        Element eChs = element.element("clientprocesses");
        if (eChs == null)
        {
            return;
        }
        Iterator it = eChs.elementIterator();
        cpClassMap = new FastMap<Integer, Class>();
        while (it.hasNext())
        {
            Element eCH = (Element) it.next();
            String className = eCH.getTextTrim();
            String sId = eCH.attributeValue("id");
            ClassLoader loader = ServiceManager.class.getClassLoader();
            int id = NumberUtils.createInteger(sId);
            if (info.id != ((id >> 8) & 0xff))
            {
                throw new RuntimeException("clientProcess id " + className + " is not correct!");
            }
            id = id & 0x00ff;
            Class c = loader.loadClass(className);
            if (c.getSuperclass() == AbsClientProcess.class)
            {
                cpClassMap.put(id, c);
            }
            else
            {
                throw new RuntimeException(className + "not extends ClientProcess");
            }
        }

    }

    public String getName ()
    {
        return info.name;
    }

    public int getID ()
    {
        return info.id;
    }

    public int[] getSessionDps ()
    {
        return info.ssnDp;
    }

    public AbsClientProcess getClientProcess (ContextData data)
            throws InstantiationException,IllegalAccessException
    {
        Class c = cpClassMap.get(data.messageType);
        if (c != null)
        {
            return (AbsClientProcess) c.newInstance();
        }
        return null;
    }

    protected abstract void start ();
    public abstract void onEvent (AbsEvent event);
    public abstract void createSession (Session session);
    public abstract void sessionFree (Session session);
    public abstract void dbUpdate (int userID);
    public abstract AbsEvent montior ();
    public abstract void clean (int userID);

    private class ServiceInfo
    {
        int    id;
        String name;
        String version;
        String description;
        String author;
        /**
         * SessionCreate和SessionFree时的依赖关系
         */
        int[]  ssnDp;

        @Override
        public String toString ()
        {
            StringBuffer buf = new StringBuffer();
            buf.append("id:" + id + "\n");
            buf.append("name:" + name + "\n");
            if (!StringUtils.isBlank(version))
            {
                buf.append("version:" + version + "\n");
            }
            if (!StringUtils.isBlank(author))
            {
                buf.append("author:" + author + "\n");
            }
            if (!StringUtils.isBlank(description))
            {
                buf.append("description:" + description + "\n");
            }
            return buf.toString();
        }

    }
}
