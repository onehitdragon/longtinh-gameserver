package hero.npc.function.system.exchange;

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
 * @文件 TraderExchangeContentDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-17 下午02:35:33
 * @描述 ：物品兑换商人兑换内容字典
 */

public class TraderExchangeContentDict
{

    /**
     * 字典容器(key:NPC编号,value：物品列表[2]--0：物品编号,1：物品数量,)
     */
    private FastMap<String, int[]>           goodsListMap;

    /**
     * 单例
     */
    private static TraderExchangeContentDict instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static TraderExchangeContentDict getInstance ()
    {
        if (null == instance)
        {
            instance = new TraderExchangeContentDict();
        }

        return instance;
    }

    /**
     * 构造
     */
    private TraderExchangeContentDict()
    {
        goodsListMap = new FastMap<String, int[]>();
    }

    /**
     * 获取NPC出售的物品列表
     * 
     * @param _npcModelID NPC模板编号
     * @return
     */
    public int[] getExchangeGoodsList (String _npcModelID)
    {
        return goodsListMap.get(_npcModelID.toLowerCase());
    }

    /**
     * 加载出售物品列表
     * 
     * @param _dataPath
     */
    @SuppressWarnings("unchecked")
    public void load (String _dataPath)
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
                        String npcID = subE.elementTextTrim("npcID")
                                .toLowerCase();
                        int goodsTypeNums = Integer.parseInt(subE
                                .elementTextTrim("goodsTypeNums"));
                        int[] goodsList = new int[goodsTypeNums];

                        if (goodsTypeNums > 0)
                        {
                            for (int i = 0; i < goodsTypeNums; i++)
                            {
                                goodsList[i] = Integer.parseInt(subE
                                        .elementTextTrim("goods" + (i + 1)
                                                + "ID"));
                            }

                            goodsListMap.put(npcID, goodsList);
                        }
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
