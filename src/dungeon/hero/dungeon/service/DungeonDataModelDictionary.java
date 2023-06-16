package hero.dungeon.service;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.dungeon.DungeonDataModel;
import hero.map.MapModelData;
import hero.map.service.MapModelDataDict;
import hero.share.service.LogWriter;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonDataModelDictionary.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-8 下午01:51:36
 * @描述 ：
 */

public class DungeonDataModelDictionary
{
    private static Logger log = Logger.getLogger(DungeonDataModelDictionary.class);
    /**
     * 副本模板数据表（副本编号：副本模板数据）
     */
    private FastMap<Integer, DungeonDataModel> dungeonDataTable;

    /**
     * 单例
     */
    private static DungeonDataModelDictionary  instance;

    /**
     * 私有构造
     */
    private DungeonDataModelDictionary()
    {
        dungeonDataTable = new FastMap<Integer, DungeonDataModel>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static DungeonDataModelDictionary getInsatnce ()
    {
        if (null == instance)
        {
            instance = new DungeonDataModelDictionary();
        }

        return instance;
    }

    /**
     * 获取副本模板数据
     * 
     * @param _dungeonID
     * @return
     */
    public DungeonDataModel get (int _dungeonID)
    {
        return dungeonDataTable.get(_dungeonID);
    }

    /**
     * 根据副本里的地图ID获取副本实例
     * 因为一个副本里的地图是不能和其它副本的地图重复的
     * @param entranceMapID
     * @return
     */
    public DungeonDataModel getDungeonDataModelByMapid(short mapID){
        for (DungeonDataModel m : dungeonDataTable.values()) {
            for(MapModelData d : m.mapModelList){
                if (d.id == mapID) {
                    return m;
                }
            }
        }
        return null;
    }
    
    /**
     * 记载副本模板数据
     * 
     * @param _filePath
     */
    protected void loadDungeonModelData (String _filePath)
    {

        File dataPath;

        try
        {
            dataPath = new File(_filePath);

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

                DungeonDataModel dungeonDataModel;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        dungeonDataModel = new DungeonDataModel();
                        dungeonDataModel.id = Integer.parseInt(subE
                                .elementTextTrim("id"));

                        try
                        {
                            dungeonDataModel.name = subE
                                    .elementTextTrim("name");
                            dungeonDataModel.level = Short.parseShort(subE
                                    .elementTextTrim("level"));
                            dungeonDataModel.playerNumberLimit = Byte
                                    .parseByte(subE
                                            .elementTextTrim("limitPlayerNumber"));
                            dungeonDataModel.entranceMapID = Short
                                    .parseShort(subE
                                            .elementTextTrim("entranceMapID"));

                            int number = Integer.parseInt(subE
                                    .elementTextTrim("number"));
                            dungeonDataModel.mapModelList = new MapModelData[number];

                            if (number > 0 && number <= 20)
                            {
                                short mapID = 0;

                                for (int i = 1; i <= number; i++)
                                {
                                    mapID = Short.parseShort(subE
                                            .elementTextTrim("map" + i + "ID"));

                                    if (mapID > 0)
                                    {
                                        dungeonDataModel.mapModelList[i - 1] = MapModelDataDict
                                                .getInstance().getMapModelData(
                                                        mapID);
                                    }
                                    else
                                    {
                                        LogWriter.println("副本地图编号错误，副本编号:"
                                                + dungeonDataModel.id);

                                        return;
                                    }
                                }

                                dungeonDataTable.put(dungeonDataModel.id,
                                        dungeonDataModel);
                            }
                            else
                            {
                                LogWriter.println("副本地图数量错误，副本编号:"
                                        + dungeonDataModel.id);

                                continue;
                            }
                        }
                        catch (Exception ex)
                        {
                            LogWriter.println("加载副本数据出错，副本编号:"
                                    + dungeonDataModel.id);
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
