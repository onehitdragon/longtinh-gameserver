package hero.map.service;

import hero.map.Area;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AreaDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-22 下午12:35:04
 * @描述 ：
 */

public class AreaDict
{
    /**
     * 区域字典（区域编号：区域）
     */
    private HashMap<Integer, Area> dictionary;

    /**
     * 单例
     */
    private static AreaDict        instance;

    /**
     * 私有构造
     */
    private AreaDict()
    {
        dictionary = new HashMap<Integer, Area>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static AreaDict getInstance ()
    {
        if (null == instance)
        {
            instance = new AreaDict();
        }

        return instance;
    }

    public Set<Entry<Integer, Area>> getAreaSet ()
    {
        return dictionary.entrySet();
    }

    /**
     * 根据区域获取区域
     * 
     * @param _areaID
     * @return
     */
    public Area getAreaByID (int _areaID)
    {
        return dictionary.get(_areaID);
    }

    protected void init (String _dataPath)
    {
        File fileList;

        try
        {
            fileList = new File(_dataPath);
            File[] dataFileList = fileList.listFiles();

            if (dataFileList.length > 0)
            {
                dictionary.clear();
            }

            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }

                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> elementIterator = rootE.elementIterator();

                while (elementIterator.hasNext())
                {
                    Element subE = elementIterator.next();
                    if (null != subE)
                    {
                        int areaID = Integer.parseInt(subE
                                .elementTextTrim("areaID"));
                        try
                        {
                            String name = subE.elementTextTrim("areaName");

                            Area area = new Area(areaID, name, AreaImageDict
                                    .getInstance().getImageBytes(areaID));

                            if (null == dictionary.get(areaID))
                            {
                                dictionary.put(area.getID(), area);
                            }
                            else
                            {
                                LogWriter.println("重复的地图区域数据，编号：" + areaID);
                            }
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载地图区域数据错误，编号：" + areaID);
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
