package hero.map.service;

import hero.share.service.LogWriter;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MapRelationDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-21 下午02:52:53
 * @描述 ：
 */

public class MapRelationDict
{
    /**
     * 字典（地图编号：{死亡复活后回到的地图编号、使用道具后传往的地图编号、区域地图编号、是否在区域中绘制位置、在区域中位置X坐标、在区域中位置Y坐标}）
     */
    private HashMap<Short, short[]> dictionary;

    /**
     * 单例
     */
    private static MapRelationDict  instance;

    /**
     * 私有构造
     */
    private MapRelationDict()
    {
        dictionary = new HashMap<Short, short[]>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MapRelationDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MapRelationDict();
        }

        return instance;
    }

    /**
     * 根据地图编号获取地图关系数据
     * 
     * @param _mapID
     * @return
     */
    public short[] getRelationByMapID (short _mapID)
    {
        return dictionary.get(_mapID);
    }

    /**
     * 初始化字典数据
     * 
     * @param _dataPath
     */
    public void init (String _dataPath)
    {

        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + _dataPath);

            return;
        }

        try
        {
            File[] dataFileList = dataPath.listFiles();

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
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        short[] relationData = new short[10];

                        short mapID = Short.parseShort(subE
                                .elementTextTrim("mapID"));
                        relationData[0] = mapID;

                        short miniImageID = Short.parseShort(subE
                                .elementTextTrim("miniImageID"));
                        relationData[1] = miniImageID;

                        short targetMapIDAfterDie = Short.parseShort(subE
                                .elementTextTrim("reviveReturnMapID"));
                        relationData[2] = targetMapIDAfterDie;

                        short targetMapIDAfterUseGoods = Short.parseShort(subE
                                .elementTextTrim("toolReturnMapID"));
                        relationData[3] = targetMapIDAfterUseGoods;

                        short areaID = Short.parseShort(subE
                                .elementTextTrim("areaID"));
                        relationData[4] = areaID;

                        if (areaID != 0)
                        {
                            if (subE.elementTextTrim("existsPoint").equals("是"))
                            {
                                relationData[5] = 1;

                                short imageX = Short.parseShort(subE
                                        .elementTextTrim("imageX"));
                                relationData[6] = imageX;

                                short imageY = Short.parseShort(subE
                                        .elementTextTrim("imageY"));
                                relationData[7] = imageY;
                            }
                        }

                        String data = subE.elementTextTrim("mozu_reviveReturnMapID");
                        if(data != null){
                            short moZuTargetMapIDAfterDie = Short.parseShort(data);
                            relationData[8] = moZuTargetMapIDAfterDie;
                        }
                        data = subE.elementTextTrim("mozu_toolReturnMapID");
                        if(data != null){
                            short mozuTargetMapIDAfterUseGoods = Short.parseShort(data);
                            relationData[9] = mozuTargetMapIDAfterUseGoods;
                        }

                        dictionary.put(mapID, relationData);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
