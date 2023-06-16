package yoyo.tools;


import java.util.Iterator;
import java.util.Map;

import javolution.util.FastMap;
import org.dom4j.Element;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.ServiceManager;

public class CPTool
{
    public static Map<Integer, Class> loadCPMap (Element element) throws Exception
    {
		Element root = element.element("clientprocesses");
		Iterator it = root.elementIterator();
		Map<Integer, Class> map = new FastMap<Integer, Class>();
		while (it.hasNext())
		{
		    Element eCH = (Element) it.next();
		    String className = eCH.getTextTrim();
		    String sId = eCH.attributeValue("id");
		    ClassLoader loader = ServiceManager.class.getClassLoader();
		    int id = Integer.valueOf(sId);
		    Class c = loader.loadClass(className);
		    if (c.getSuperclass() == AbsClientProcess.class)
		    {
		        map.put(id, c);
		    }
		    else
		    {
		        throw new RuntimeException(className + " not extedns ClientProcess");
		    }
		}
		return map;
	}
    
    public static Class loadCP (Element element) throws Exception
    {
        String className = element.valueOf("//clientprocesses/clientprocess");
        if (className == null || className.equals(""))
        {
            throw new Exception(element.getName() + " not define ClientProcess");
        }
        ClassLoader loader = ServiceManager.class.getClassLoader();
        Class c = loader.loadClass(className);

        if (c.getSuperclass() == AbsClientProcess.class)
        {
            return c;
        }
        throw new RuntimeException(className + " not extedns ClientProcess");
    }
}
