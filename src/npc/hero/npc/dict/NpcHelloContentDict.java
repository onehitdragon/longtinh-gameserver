package hero.npc.dict;

import hero.share.service.LogWriter;

import java.io.File;
import java.util.Iterator;

import javolution.util.FastMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NpcHelloContentDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-18 下午04:24:20
 * @描述 ：
 */

public class NpcHelloContentDict
{
    /**
     * 字典容器
     */
    private FastMap<Integer, String>   contentDict;

    /**
     * 单例
     */
    private static NpcHelloContentDict instance;

    /**
     * 私有构造
     */
    private NpcHelloContentDict()
    {
        contentDict = new FastMap<Integer, String>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static NpcHelloContentDict getInstance ()
    {
        if (null == instance)
        {
            instance = new NpcHelloContentDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public String getHelloContent (int _contentID)
    {
        return contentDict.get(_contentID);
    }

    /**
     * 加载问候内容
     * 
     * @param _dataPath
     */
    public void load (String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
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
                        int contentID = Integer.parseInt(subE
                                .elementTextTrim("id"));

                        try
                        {
                            if (!contentDict.containsKey(contentID))
                            {
                                contentDict.put(contentID, subE
                                        .elementTextTrim("content"));
                            }
                            else
                            {
                                LogWriter.println("重复的NPC对话数据，编号:" + contentID);
                            }
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载NPC对话数据错误，编号:" + contentID);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }
    }

    public static void main (String[] args)
    {
        NpcHelloContentDict.getInstance().load(
                System.getProperty("user.dir") + "/" + "res/data/npc/hello/");

        System.out
                .println(NpcHelloContentDict.getInstance().getHelloContent(1));
    }
}
