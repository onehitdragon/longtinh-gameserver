package hero.item.dictionary;

import hero.item.Material;
import hero.item.detail.EGoodsTrait;
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
 * @文件 MaterialDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-18 下午04:17:45
 * @描述 ：材料字典
 */

public class MaterialDict
{
    /**
     * 单例
     */
    private static MaterialDict        instance;

    /**
     * 模板容器
     */
    private FastMap<Integer, Material> dictionary;

    /**
     * 私有构造
     */
    private MaterialDict()
    {
        dictionary = new FastMap<Integer, Material>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MaterialDict getInstance ()
    {
        if (instance == null)
        {
            instance = new MaterialDict();
        }

        return instance;
    }

    public Object[] getMaterialList ()
    {
        return dictionary.values().toArray();
    }

    /**
     * 根据编号获取材料
     * 
     * @param _materialID
     * @return
     */
    public Material getMaterial (int _materialID)
    {
        return dictionary.get(_materialID);
    }

    /**
     * 加载材料模板
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
                        String data = null;

                        Material material = new Material(Short.parseShort(subE
                                .elementTextTrim("stackNums")));

                        try
                        {
                            if ((data = subE.elementTextTrim("id")) != null)
                            {
                                material.setID(Integer.parseInt(data));
                            }

                            if ((data = subE.elementTextTrim("name")) != null)
                            {
                                material.setName(data);
                            }

                            if ((data = subE.elementTextTrim("trait")) != null)
                            {
                                material.setTrait(EGoodsTrait.getTrait(data));
                            }

                            if ((data = subE.elementTextTrim("exchangeable")) != null)
                            {
                                if (data.equals("可交易"))
                                {
                                    material.setExchangeable();
                                }
                            }

                            if ((data = subE.elementTextTrim("price")) != null)
                            {
                                material.setPrice(Integer.parseInt(data));
                            }

                            if ((data = subE.elementTextTrim("icon")) != null)
                            {
                                material.setIconID(Short.parseShort(data));
                            }

                            if ((data = subE.elementTextTrim("description")) != null)
                            {
                                material.appendDescription(data);
                            }

                            dictionary.put(material.getID(), material);
                        }
                        catch (Exception ex)
                        {
                            LogWriter
                                    .println("加载材料数据出错，编号:" + material.getID());
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
