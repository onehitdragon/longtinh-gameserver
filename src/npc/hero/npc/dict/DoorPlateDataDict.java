package hero.npc.dict;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.share.service.LogWriter;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DoorPlateDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-27 下午02:39:34
 * @描述 ：
 */

public class DoorPlateDataDict
{
    /**
     * 字典容器
     */
    private FastMap<String, DoorPlateData> doorPlateDataDict;

    /**
     * 单例
     */
    private static DoorPlateDataDict       instance;

    /**
     * 私有构造
     */
    private DoorPlateDataDict()
    {
        doorPlateDataDict = new FastMap<String, DoorPlateData>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static DoorPlateDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new DoorPlateDataDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public DoorPlateData getDoorPlateData (String _doorPlateModelID)
    {
        return doorPlateDataDict.get(_doorPlateModelID);
    }

    /**
     * 加载室内大厅门牌模板对象
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
                        DoorPlateData doorPlateData = new DoorPlateData();
                        try
                        {
                            doorPlateData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();
                            doorPlateData.tip = subE.elementTextTrim("tip");

                            if (!doorPlateDataDict
                                    .containsKey(doorPlateData.modelID))
                            {
                                doorPlateDataDict.put(doorPlateData.modelID,
                                        doorPlateData);
                            }
                            else
                            {
                                LogWriter.println("重复的室内门牌数据，编号:"
                                        + doorPlateData.modelID);
                            }
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载路室内门牌据错误，编号:"
                                    + doorPlateData.modelID);
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

    /**
     * @文件 DoorPlateData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2009-5-20 下午02:53:11
     * @描述 ：
     */
    public static class DoorPlateData
    {
        public String modelID;

        public String tip;
    }
}
