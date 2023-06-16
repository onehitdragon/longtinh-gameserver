package hero.npc.dict;

import hero.share.service.LogWriter;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonManagerDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-8 下午07:16:51
 * @描述 ：副本管理员管理目录
 */

public class DungeonManagerDict
{
    /**
     * 目录（NPC编号：副本编号）
     */
    private FastMap<String, Integer>  dictionary;

    /**
     * 单例
     */
    private static DungeonManagerDict instance;

    /**
     * 私有构造
     */
    private DungeonManagerDict()
    {
        dictionary = new FastMap<String, Integer>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static DungeonManagerDict getInstance ()
    {
        if (null == instance)
        {
            instance = new DungeonManagerDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public int getDungeonID (String _npcModelID)
    {
        return dictionary.get(_npcModelID);
    }

    /**
     * 加载
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

                String npcModelID;
                int dungeonID;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();
                    if (null != subE)
                    {
                        try
                        {
                            npcModelID = subE.elementTextTrim("npcModelID")
                                    .toLowerCase();
                            dungeonID = Integer.parseInt(subE
                                    .elementTextTrim("dungeonID"));

                            dictionary.put(npcModelID, dungeonID);
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载副本传送管理数据错误");
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
}
