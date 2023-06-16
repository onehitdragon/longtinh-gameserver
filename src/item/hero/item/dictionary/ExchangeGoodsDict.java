package hero.item.dictionary;

import hero.share.service.LogWriter;

import java.io.File;
import java.util.ArrayList;
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
 * @文件 ExchangeGoodsDict.java
 * @创建者 LiuJie
 * @版本 1.0
 * @时间 2009-2-25 下午02:09:18
 * @描述 ：交换物品字典
 */

public class ExchangeGoodsDict
{
    /**
     * 单例
     */
    private static ExchangeGoodsDict           instance;

    /**
     * 数据容器（物品编号：材料列表<材料编号：数量>）
     */
    private FastMap<Integer, ArrayList<int[]>> dictionory;

    /**
     * 获取单例
     * 
     * @return
     */
    public static ExchangeGoodsDict getInstance ()
    {
        if (instance == null)
        {
            instance = new ExchangeGoodsDict();
        }
        return instance;
    }

    /**
     * 获取交换物品需要的材料列表
     * 
     * @param _goodsID
     * @return
     */
    public ArrayList<int[]> getMaterialList (int _goodsID)
    {
        return dictionory.get(_goodsID);
    }

    /**
     * 构造
     */
    private ExchangeGoodsDict()
    {
        dictionory = new FastMap<Integer, ArrayList<int[]>>();
    }

    /**
     * 加载交换物品数据
     * 
     * @param _dataPath
     */
    public void load (String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
        }
        catch (Exception e)
        {
            LogWriter.println(INVALIDATE_PATH + _dataPath);

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
                        int goodsID = Integer.parseInt(subE
                                .elementTextTrim("itemID"));

                        ArrayList<int[]> materialList = new ArrayList<int[]>();

                        int materialType = Integer.parseInt(subE
                                .elementTextTrim("materialType"));

                        if (materialType > 0)
                        {
                            for (int i = 1; i <= materialType; i++)
                            {
                                materialList.add(new int[]{
                                        Integer.parseInt(subE
                                                .elementTextTrim("material" + i
                                                        + "ID")),
                                        Integer.parseInt(subE
                                                .elementTextTrim("material" + i
                                                        + "Number")) });
                            }

                            if (materialList.size() > 0)
                            {
                                dictionory.put(goodsID, materialList);
                            }
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

    /**
     * 需要后续工作
     * 数据文件未找到提示
     */
    private static final String INVALIDATE_PATH = "未找到指定的目录：";
}
