package hero.npc.dict;

import java.io.File;
import java.util.Iterator;

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
 * @文件 RoadPlateDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-20 下午02:52:13
 * @描述 ：
 */

public class RoadPlateDataDict
{
    /**
     * 字典容器
     */
    private FastMap<String, RoadPlateData> roadPlateDataDict;

    /**
     * 单例
     */
    private static RoadPlateDataDict       instance;

    /**
     * 私有构造
     */
    private RoadPlateDataDict()
    {
        roadPlateDataDict = new FastMap<String, RoadPlateData>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static RoadPlateDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new RoadPlateDataDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public RoadPlateData getRoadPlateData (String _roadPlateModelID)
    {
        return roadPlateDataDict.get(_roadPlateModelID);
    }

    /**
     * 加载路牌模板对象
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
                        RoadPlateData roadPlateData = new RoadPlateData();
                        try
                        {
                            roadPlateData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();
                            roadPlateData.instructContent = subE
                                    .elementTextTrim("content");

                            if (!roadPlateDataDict
                                    .containsKey(roadPlateData.modelID))
                            {
                                roadPlateDataDict.put(roadPlateData.modelID,
                                        roadPlateData);
                            }
                            else
                            {
                                LogWriter.println("重复的路牌数据，编号:"
                                        + roadPlateData.modelID);
                            }
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载路牌数据错误，编号:"
                                    + roadPlateData.modelID);
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
     * @文件 RoadPlateData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2009-5-20 下午02:53:11
     * @描述 ：
     */
    public static class RoadPlateData
    {
        public String modelID;

        public String instructContent;
    }
}
