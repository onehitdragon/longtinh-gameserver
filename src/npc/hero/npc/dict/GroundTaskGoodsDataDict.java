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
 * @文件 GroundTaskGoodsDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-26 下午06:13:16
 * @描述 ：
 */

public class GroundTaskGoodsDataDict
{

    /**
     * 字典容器
     */
    private FastMap<String, GroundTaskGoodsData> taskGoodsDataDict;

    /**
     * 单例
     */
    private static GroundTaskGoodsDataDict       instance;

    /**
     * 私有构造
     */
    private GroundTaskGoodsDataDict()
    {
        taskGoodsDataDict = new FastMap<String, GroundTaskGoodsData>();
    }

    /**
     * * 单例模式
     * 
     * @return 字典单例
     */
    public static GroundTaskGoodsDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new GroundTaskGoodsDataDict();
        }

        return instance;
    }

    /**
     * 根据任务物品编号获取详细数据
     * 
     * @return
     */
    public GroundTaskGoodsData getTaskGoodsData (String _taskGoodsModelID)
    {
        return taskGoodsDataDict.get(_taskGoodsModelID);
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
                        GroundTaskGoodsData taskGoodsData = new GroundTaskGoodsData();

                        try
                        {
                            taskGoodsData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();
                            taskGoodsData.name = subE.elementTextTrim("name");
                            taskGoodsData.taskID = Integer.parseInt(subE
                                    .elementTextTrim("taskID"));
                            taskGoodsData.taskToolID = Integer.parseInt(subE
                                    .elementTextTrim("taskGoodsID"));
                            taskGoodsData.imageID = Short.parseShort(subE
                                    .elementTextTrim("imageID"));
                            if (!taskGoodsDataDict
                                    .containsKey(taskGoodsData.modelID))
                            {
                                taskGoodsDataDict.put(taskGoodsData.modelID,
                                        taskGoodsData);
                            }
                            else
                            {
                                LogWriter.println("重复的地上任务物品数据，编号:"
                                        + taskGoodsData.modelID);
                            }
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载地上任务物品数据错误，编号:"
                                    + taskGoodsData.modelID);
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
     * @文件 TaskGoodsData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2009-5-26 下午06:14:03
     * @描述 ：
     */
    public static class GroundTaskGoodsData
    {
        public String modelID;

        public String name;

        public int    taskID;

        public int    taskToolID;

        public short  imageID;
    }

}
