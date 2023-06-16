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
 * @文件 BoxDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-25 下午08:16:24
 * @描述 ：
 */

public class BoxDataDict
{
    /**
     * 字典容器
     */
    private FastMap<String, BoxData> boxDataDict;

    /**
     * 单例
     */
    private static BoxDataDict       instance;

    /**
     * 私有构造
     */
    private BoxDataDict()
    {
        boxDataDict = new FastMap<String, BoxData>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static BoxDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new BoxDataDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public BoxData getBoxData (String _boxModelID)
    {
        return boxDataDict.get(_boxModelID);
    }

    /**
     * 加载宝箱模板对象
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

                byte fixedGoodsTypeNumsTotal, randomGoodsTypeNumsTotal;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        BoxData boxData = new BoxData();
                        try
                        {
                            boxData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();
                            boxData.rebirthInterval = Integer.parseInt(subE
                                    .elementTextTrim("rebirthInterval")) * 60000;
                            boxData.fixedGoodsTypeNumsPerTimes = Byte
                                    .parseByte(subE
                                            .elementTextTrim("fixedGoodsTypeNumsPerTime"));
                            fixedGoodsTypeNumsTotal = Byte
                                    .parseByte(subE
                                            .elementTextTrim("fixedGoodsTypeNumsTotal"));
                            boxData.fixedGoodsInfos = new int[fixedGoodsTypeNumsTotal][2];

                            for (int i = 1; i <= fixedGoodsTypeNumsTotal; i++)
                            {
                                boxData.fixedGoodsInfos[i - 1][0] = Integer
                                        .parseInt(subE
                                                .elementTextTrim("fixedGoods"
                                                        + i + "ID"));
                                boxData.fixedGoodsInfos[i - 1][1] = Integer
                                        .parseInt(subE
                                                .elementTextTrim("fixedGoods"
                                                        + i + "Nums"));
                            }

                            randomGoodsTypeNumsTotal = Byte
                                    .parseByte(subE
                                            .elementTextTrim("randomGoodsTypeNumsTotal"));

                            if (randomGoodsTypeNumsTotal > 0)
                            {
                                boxData.randomGoodsTypeNumsPerTimes = Byte
                                        .parseByte(subE
                                                .elementTextTrim("randomGoodsTypeNumsPerTime"));
                                boxData.randomGoodsInfos = new int[randomGoodsTypeNumsTotal][3];

                                for (int i = 1; i <= randomGoodsTypeNumsTotal; i++)
                                {
                                    boxData.randomGoodsInfos[i - 1][0] = Integer
                                            .parseInt(subE
                                                    .elementTextTrim("randomGoods"
                                                            + i + "ID"));

                                    boxData.randomGoodsInfos[i - 1][1] = Integer
                                            .parseInt(subE
                                                    .elementTextTrim("randomGoods"
                                                            + i + "Odds"));
                                    boxData.randomGoodsInfos[i - 1][2] = Integer
                                            .parseInt(subE
                                                    .elementTextTrim("randomGoods"
                                                            + i + "Nums"));
                                }
                            }

                            boxData.pickType = PickType.getPickType(subE.elementTextTrim("pickType"));

                            if (!boxDataDict.containsKey(boxData.modelID))
                            {
                                boxDataDict.put(boxData.modelID, boxData);
                            }
                            else
                            {
                                LogWriter.println("重复的宝箱数据，编号:"
                                        + boxData.modelID);
                            }
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载宝箱数据错误，编号:" + boxData.modelID);
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
     * @文件 BoxData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2009-5-24 下午08:16:34
     * @描述 ：
     */
    public static class BoxData
    {
        /**
         * 编号
         */
        public String  modelID;

        /**
         * 刷新时间
         */
        public int     rebirthInterval;

        /**
         * 固定物品每次掉落最多种类
         */
        public byte    fixedGoodsTypeNumsPerTimes;

        /**
         * 固定物品掉落物品信息（编号、掉落最多数量）
         */
        public int[][] fixedGoodsInfos;

        /**
         * 随机物品每次掉落最多种类
         */
        public byte    randomGoodsTypeNumsPerTimes;

        /**
         * 随机物品掉落物品信息（编号、几率、掉落最多数量）
         */
        public int[][] randomGoodsInfos;

        /**
         * 拾取类型
         */
        public PickType pickType;
    }



}
