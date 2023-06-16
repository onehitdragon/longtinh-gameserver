package hero.npc.dict;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.npc.dict.AnimalDataDict.AnimalData;
import hero.share.service.LogWriter;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GearDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-13 下午06:11:14
 * @描述 ：
 */

public class GearDataDict
{
    private static Logger log = Logger.getLogger(GearDataDict.class);
    /**
     * 字典容器
     */
    private FastMap<String, GearData> gearDataDict;

    /**
     * 单例
     */
    private static GearDataDict       instance;

    /**
     * 私有构造
     */
    private GearDataDict()
    {
        gearDataDict = new FastMap<String, GearData>();
    }

    /**
     * * 单例模式
     * 
     * @return 字典单例
     */
    public static GearDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new GearDataDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public GearData getGearData (String _gearModelID)
    {
        return gearDataDict.get(_gearModelID);
    }

    /**
     * 加载机关模板数据
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
                        GearData gearData = new GearData();

                        try
                        {
                            gearData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();
                            gearData.name = subE.elementTextTrim("name");
                            gearData.taskID = Integer.parseInt(subE
                                    .elementTextTrim("taskID"));
                            gearData.optionDesc = subE
                                    .elementTextTrim("optionDesc");
                            gearData.imageID = Short.parseShort(subE
                                    .elementTextTrim("imageID"));
                            gearData.description = subE
                                    .elementTextTrim("description");

                            if (!gearDataDict.containsKey(gearData.modelID))
                            {
                                gearDataDict.put(gearData.modelID, gearData);
                            }
                            else
                            {
                                log.debug("重复的任务机关数据，编号:"
                                        + gearData.modelID);
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("加载任务机关数据错误，编号:"
                                    + gearData.modelID);
                        }
                    }
                }
                log.debug("gear data dict size = " + gearDataDict.size());
            }
        }
        catch (Exception e)
        {
            log.error("加载任务机关 error：", e);
        }
    }

    /**
     * @文件 GearData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2009-5-13 下午06:12:34
     * @描述 ：
     */
    public static class GearData
    {
        public String modelID;

        public String name;

        public String optionDesc;

        public int    taskID;

        public short  imageID;

        public String description;
    }
}
