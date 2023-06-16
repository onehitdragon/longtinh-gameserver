package hero.map.service;

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
 * @文件 MapMusicDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-1-23 上午10:46:45
 * @描述 ：
 */

public class MapMusicDict
{
    /**
     * 字典
     */
    private FastMap<Short, Byte> dictionary;

    /**
     * 单例
     */
    private static MapMusicDict  instance;

    /**
     * 私有构造
     */
    private MapMusicDict()
    {
        dictionary = new FastMap<Short, Byte>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MapMusicDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MapMusicDict();
        }

        return instance;
    }

    /**
     * 根据地图编号获取地图背景音乐编号
     * 
     * @param _mapID
     * @return
     */
    public byte getMapMusicID (short _mapID)
    {
        Byte musicID = dictionary.get(_mapID);

        if (musicID == null)
        {
            return 0;
        }

        return musicID;
    }

    /**
     * 初始化
     * 
     * @param _configPath
     */
    public void init (String _configPath)
    {
        File fileDir;

        try
        {
            fileDir = new File(_configPath);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到地图音乐配置目录：" + _configPath);

            return;
        }

        try
        {
            File[] fileList = fileDir.listFiles();

            if (fileList.length > 0)
            {
                dictionary.clear();
            }
            else
            {
                return;
            }

            SAXReader reader;
            Document document;
            Element rootE;
            Iterator<Element> rootIt;

            for (File dataFile : fileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }

                reader = new SAXReader();
                document = reader.read(dataFile);
                rootE = document.getRootElement();
                rootIt = rootE.elementIterator();

                short mapID;
                byte musicID;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        mapID = Short.parseShort(subE.elementTextTrim("mapID"));

                        musicID = Byte.parseByte(subE
                                .elementTextTrim("musicID"));

                        dictionary.put(mapID, musicID);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
    }
}
