package hero.gather.dict;

import hero.share.service.LogWriter;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 灵魂信息字典类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class SoulInfoDict
{
    private static SoulInfoDict        instance;

    private HashMap<Integer, SoulInfo> souls;

    private SoulInfoDict()
    {
        souls = new HashMap<Integer, SoulInfo>();
    }

    @SuppressWarnings("unchecked")
    public void loadSoulInfos (String path)
    {
        File dataPath;

        try
        {
            dataPath = new File(path);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + path);
            return;
        }

        try
        {
            File[] dataFileList = dataPath.listFiles();

            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }

                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();
                    if (null != subE)
                    {
                        int id = Integer.parseInt(subE.elementTextTrim("id"));
                        String name = subE.elementTextTrim("name");
                        short icon = Short.parseShort(subE
                                .elementTextTrim("icon"));
                        String des = subE.elementTextTrim("des");

                        souls.put(id, new SoulInfo(id, name, icon, des));
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static SoulInfoDict getInstance ()
    {
        if (instance == null)
            instance = new SoulInfoDict();
        return instance;
    }

    public SoulInfo getSoulInfoByID (int _soulID)
    {
        return souls.get(_soulID);
    }
}
