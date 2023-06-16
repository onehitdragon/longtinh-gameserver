package hero.npc.function.system.trade;

import hero.share.service.LogWriter;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TraderSellContentDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-25 下午02:09:18
 * @描述 ：销售商品列表字典
 */

public class TraderSellContentDict
{
     private static Logger log = Logger.getLogger(TraderSellContentDict.class);
    /**
     * 字典容器(key:NPC编号,value：物品列表[2]--0：物品编号,1：物品数量,)
     */
    private FastMap<String, int[][]>     goodsListMap;

    /**
     * 单例
     */
    private static TraderSellContentDict instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static TraderSellContentDict getInstance ()
    {
        if (null == instance)
        {
            instance = new TraderSellContentDict();
        }

        return instance;
    }

    /**
     * 构造
     */
    private TraderSellContentDict()
    {
        goodsListMap = new FastMap<String, int[][]>();
    }

    /**
     * 获取NPC出售的物品列表
     * 
     * @param _npcModelID NPC模板编号
     * @return
     */
    public int[][] getSellGoodsList (String _npcModelID)
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
                        int goodsTypeNums = Integer.parseInt(subE.elementTextTrim("goodsTypeNums"));
                        int[][] goodsList = new int[goodsTypeNums][2];

                        if (goodsTypeNums > 0)
                        {
                            for (int i = 0; i < goodsTypeNums; i++)
                            {
                            	try {
									
                            		goodsList[i][0] = Integer.parseInt(subE
                            				.elementTextTrim("goods" + (i + 1)
                            						+ "ID"));
								} catch (Exception e) {
									log.info("npcID=" + npcID + "没有第" + i + "个商品");
								}

                                String goodsNumsDesc = subE
                                        .elementTextTrim("goods" + (i + 1)
                                                + "Nums");

                                if (null != goodsNumsDesc)
                                {
                                    goodsList[i][1] = Integer
                                            .parseInt(goodsNumsDesc);
                                }
                                else
                                {
                                    goodsList[i][1] = -1;
                                }
                            }
                        }

                        goodsListMap.put(npcID, goodsList);
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
