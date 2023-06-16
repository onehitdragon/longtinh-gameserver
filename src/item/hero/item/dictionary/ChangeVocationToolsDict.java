package hero.item.dictionary;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javolution.util.FastMap;
import hero.item.TaskTool;
import hero.npc.dict.AnimalDataDict.AnimalData;
import hero.share.EVocation;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeVocationConditionsList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-13 下午05:30:39
 * @描述 ：转职
 */

public class ChangeVocationToolsDict
{
    private static Logger log = Logger.getLogger(ChangeVocationToolsDict.class);
    /**
     * 转职需要的物品列表
     */
    private FastMap<EVocation, TaskTool>   changeVocationGoodsDict;

    /**
     * 单例
     */
    private static ChangeVocationToolsDict instance;

    /**
     * 私有构造
     */
    private ChangeVocationToolsDict()
    {
        changeVocationGoodsDict = new FastMap<EVocation, TaskTool>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static ChangeVocationToolsDict getInstance ()
    {
        if (null == instance)
        {
            instance = new ChangeVocationToolsDict();
        }

        return instance;
    }

    /**
     * 根据职业获取道具
     * 
     * @param _vocation
     * @return
     */
    public TaskTool getToolByVocation (EVocation _vocation)
    {
        return changeVocationGoodsDict.get(_vocation);
    }

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
                        EVocation vocation;
                        TaskTool tool;

                        try
                        {
                            vocation = EVocation.getVocationByDesc(subE
                                    .elementTextTrim("vocation"));
                            log.debug("change vocation = " + vocation);
                            if (null != vocation)
                            {
                                tool = (TaskTool) GoodsContents
                                        .getGoods(Integer.parseInt(subE
                                                .elementTextTrim("toolID")));
                                log.debug("change vocation tool = " + tool);
                                if (null != tool)
                                {
                                    if (null == changeVocationGoodsDict
                                            .get(vocation))
                                    {
                                        changeVocationGoodsDict.put(vocation,
                                                tool);
                                    }
                                    else
                                    {
                                        log.debug("转职道具表－重复的职业描述数据:"
                                                + vocation.getDesc());
                                    }
                                }
                                else
                                {
                                    log.debug("转职道具表－不存在的道具，编号:"
                                            + vocation.getDesc());
                                }
                            }
                            else
                            {
                                log.debug("转职道具表－不存在的职业描述:"
                                        + vocation.getDesc());
                            }


                        }
                        catch (Exception e)
                        {
                            log.debug("加载转职道具数据错误，编号:"
                                    + subE.elementTextTrim("vocation"),e);
                        }
                    }
                }
            }
            log.debug("changeVocationGoodsDict size = " + changeVocationGoodsDict.size());
        }
        catch (Exception e)
        {
            log.error("加载转职物品 error: ", e);
        }
    }
}
