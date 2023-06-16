package yoyo.tools;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;

import org.dom4j.Element;

public class BeanCreator
{
    public static Object createBean (Element element, Object upper,String type)
    {
        try
        {
            if (isBaseType(type))
            {
                return createBaseType(element, type);
            }
            else if (type.contains("[]"))
            {
                return createArrayObject(element, upper, type);
            }
            else
            {
                return createObject(element, upper, type);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    private static boolean isBaseType (String type)
    {
        if ("int".equals(type))
        {
            return true;
        }
        else if ("double".equals(type))
        {
            return true;
        }
        else if ("boolean".equals(type))
        {
            return true;
        }
        else if ("String".equals(type))
        {
            return true;
        }
        else if ("float".equals(type))
        {
            return true;
        }
        else if ("short".equals(type))
        {
            return true;
        }
        else if ("byte".equals(type))
        {
            return true;
        }
        return false;
    }

    private static Class getClass (String type) throws Exception
    {
        if ("int".equals(type))
        {
            return int.class;
        }
        else if ("double".equals(type))
        {
            return double.class;
        }
        else if ("boolean".equals(type))
        {
            return boolean.class;
        }
        else if ("String".equals(type))
        {
            return String.class;
        }
        else if ("float".equals(type))
        {
            return float.class;
        }
        else if ("short".equals(type))
        {
            return short.class;
        }
        else if ("byte".equals(type))
        {
            return byte.class;
        }
        return Class.forName(type);
    }

    private static Object createBaseType (Element element, String type)
    {
        String ret = element.getTextTrim();
        if ("int".equals(type))
        {
            return Integer.parseInt(ret);
        }
        else if ("double".equals(type))
        {
            return Double.parseDouble(ret);
        }
        else if ("boolean".equals(type))
        {
            return Boolean.parseBoolean(ret);
        }
        else if ("float".equals(type))
        {
            return Float.parseFloat(ret);
        }
        else if ("short".equals(type))
        {
            return Short.parseShort(ret);
        }
        else if ("byte".equals(type))
        {
            return Byte.parseByte(ret);
        }
        return ret;
    }

    private static Object createArrayObject (Element element, Object upper,String type) throws Exception
    {
        String componentType = type.split("\\[]")[0];
        Object array = Array.newInstance(getClass(componentType), element.elements().size());
        Iterator it = element.elementIterator();
        int i = 0;
        while (it.hasNext())
        {
            Element subElement = (Element) it.next();
            Array.set(array, i++, createBean(subElement, upper, subElement.attributeValue("type")));
        }
        return array;
    }

    private static Object createObject (Element element, Object upper,String type) throws Exception
    {
        Class objc = null;
        Object obj = null;
        if (type.contains("$"))
        {
            Class[] cs = upper.getClass().getClasses();
            for (Class c : cs)
            {
                if (c.getName().equals(type))
                {
                    obj = c.getConstructor(new Class[]{upper.getClass() })
                            .newInstance(new Object[]{upper });
                    objc = c;
                }
            }
        }
        else if (upper != null)
        {
            objc = upper.getClass().getClassLoader().loadClass(type);
        }
        else
        {
            objc = Class.forName(type);
        }
        if (objc.isEnum())
        {
            Object[] objs = objc.getEnumConstants();
            String value = element.getTextTrim();
            int eId = Integer.parseInt(value);
            for (Object b : objs)
            {
                int id = (Integer) objc.getMethod("ordinal", null).invoke(b,null);
                if (id == eId)
                    return b;
            }
            return null;
        }
        obj = objc.newInstance();
        Iterator it = element.elementIterator();
        while (it.hasNext())
        {
            Element subElement = (Element) it.next();
            Object value = createBean(subElement, obj, subElement
                    .attributeValue("type"));
            try
            {
                Field field = objc.getField(subElement.getName());
                field.set(obj, value);
            }
            catch (NoSuchFieldException e)
            {

                BeanInfo bInfo = Introspector.getBeanInfo(objc, Object.class);
                boolean isField = false;
                for (PropertyDescriptor property : bInfo
                        .getPropertyDescriptors())
                {
                    if (property.getName().equalsIgnoreCase(
                            subElement.getName()))
                    {
                        property.getWriteMethod().invoke(obj,
                                new Object[]{value });
                        isField = true;
                        break;
                    }
                }
                if (!isField)
                {
                    throw new NoSuchFieldException(e.toString());
                }
            }
        }
        return obj;
    }
}
