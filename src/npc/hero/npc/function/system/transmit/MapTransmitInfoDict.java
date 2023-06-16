package hero.npc.function.system.transmit;

import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;

import hero.map.Map;
import hero.map.service.*;
import hero.share.service.LogWriter;

import org.dom4j.*;
import org.dom4j.io.*;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MapTransmitInfoDict.java
 * @创建者 Liu Jie
 * @版本 1.0
 * @时间 2009-2-17 下午02:33:23
 * @描述 ：地图传送信息字典
 */

public class MapTransmitInfoDict
{
    /**
     * 单例
     */
    private static MapTransmitInfoDict                        instance;

    /**
     * 字典容器（源地图编号：可到达的目标地图列表）
     */
    private HashMap<String, ArrayList<TransmitTargetMapInfo>> dictionary = new HashMap<String, ArrayList<TransmitTargetMapInfo>>();

    /**
     * 获取单例
     * 
     * @return
     */
    public static MapTransmitInfoDict getInstance ()
    {
        if (instance == null)
        {
            instance = new MapTransmitInfoDict();
        }

        return instance;
    }

    /**
     * 私有构造
     */
    private MapTransmitInfoDict()
    {
        load(MapServiceImpl.getInstance().getConfig().getTransmitMapListPath());
    }

    /**
     * @param _npcModelID 传送们NPC模板编号
     * @return
     */
    public ArrayList<TransmitTargetMapInfo> getTargetMapInfoList (
            String _npcModelID)
    {
        return dictionary.get(_npcModelID.toLowerCase());
    }

    /**
     * 加载地图传送数据
     * 
     * @param _dataPath
     */
    private void load (String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
        }
        catch (Exception e)
        {
            LogWriter.println(TIP_OF_INVAILD_PATH + _dataPath);

            return;
        }

        try
        {
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
                        String npcModelID = subE.elementTextTrim("id");

                        ArrayList<TransmitTargetMapInfo> targetMapInfoList = new ArrayList<TransmitTargetMapInfo>();

                        String sDestMapID;
                        Map destMap;

                        for (int i = 1; i <= 5; i++)
                        {
                            sDestMapID = subE.elementTextTrim("destMap" + i
                                    + "ID");

                            if (null != sDestMapID)
                            {
                                destMap = MapServiceImpl.getInstance()
                                        .getNormalMapByID(
                                                Short.parseShort(sDestMapID));

                                if (null != destMap)
                                {
                                    int freight = Integer.parseInt(subE
                                            .elementTextTrim("destMap" + i
                                                    + "Price"));
                                    short needLevel = Short.parseShort(subE
                                            .elementTextTrim("destMap" + i
                                                    + "NeedLevel"));

                                    byte destMapX = Byte.parseByte(subE
                                            .elementTextTrim("destMap" + i
                                                    + "X"));
                                    byte destMapY = Byte.parseByte(subE
                                            .elementTextTrim("destMap" + i
                                                    + "Y"));

                                    targetMapInfoList
                                            .add(new TransmitTargetMapInfo(
                                                    destMap.getID(), destMap
                                                            .getName(), destMap
                                                            .getArea()
                                                            .getName(),
                                                    needLevel, destMapX,
                                                    destMapY, freight));
                                }
                            }
                        }

                        dictionary.put(npcModelID, targetMapInfoList);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * **需后续工作
     * 文件加载错误提示
     */
    private static final String TIP_OF_INVAILD_PATH = "未找到指定的目录：";
}
