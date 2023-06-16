package hero.npc.dict;

import java.io.File;
import java.util.Iterator;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AnimalDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-11 下午09:03:16
 * @描述 ：
 */

public class AnimalDataDict
{
    private static Logger log = Logger.getLogger(AnimalDataDict.class);
    /**
     * 字典容器
     */
    private FastMap<String, AnimalData> animalDataDict; 

    /**
     * 单例
     */
    private static AnimalDataDict       instance;

    /**
     * 私有构造
     */
    private AnimalDataDict()
    {
        animalDataDict = new FastMap<String, AnimalData>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static AnimalDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new AnimalDataDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public AnimalData getAnimalData (String _animalModelID)
    {
        return animalDataDict.get(_animalModelID);
    }

    /**
     * 加载动物模板对象
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
                        AnimalData animalData = new AnimalData();
                        try
                        {
                            animalData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();
                            animalData.fastestWalkRange = Byte.parseByte(subE
                                    .elementTextTrim("range"));
                            animalData.imageID = Short.parseShort(subE
                                    .elementTextTrim("imageID"));
                            //add by zhengl ; date: 2010-11-12 ; note: 宠物的动作用动画来控制
                            animalData.animationID = Byte.parseByte(subE
                                    .elementTextTrim("animationID"));

                            if (!animalDataDict.containsKey(animalData.modelID))
                            {
                                animalDataDict.put(animalData.modelID,
                                        animalData);
                            }
                            else
                            {
                               log.debug("重复的小动物数据，编号:"
                                        + animalData.modelID);
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("加载小动物数据错误，编号:"
                                    + animalData.modelID,e);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("加载小动物数据 error: ", e);
        }
    }

    /**
     * @文件 AnimalData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2009-5-11 下午09:03:32
     * @描述 ：
     */
    public static class AnimalData
    {
        public String modelID;

        public byte   fastestWalkRange;
        
        public short   animationID;

        public short  imageID;
    }
}
