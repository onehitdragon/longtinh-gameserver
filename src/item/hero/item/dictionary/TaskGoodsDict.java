package hero.item.dictionary;

import hero.item.TaskTool;
import hero.item.detail.EGoodsTrait;
import hero.share.service.DataConvertor;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.Iterator;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskGoodsDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-14 上午09:26:27
 * @描述 ：任务道具字典
 */

public class TaskGoodsDict
{
    private static Logger log = Logger.getLogger(TaskGoodsDict.class);
    /**
     * 字典容器
     */
    private FastMap<Integer, TaskTool> dictionary;

    /**
     * 单例
     */
    private static TaskGoodsDict       instance;

    /**
     * 私有构造
     */
    private TaskGoodsDict()
    {
        dictionary = new FastMap<Integer, TaskTool>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static TaskGoodsDict getInstance ()
    {
        if (null == instance)
        {
            instance = new TaskGoodsDict();
        }

        return instance;
    }

    /**
     * 根据编号获取任务物品
     * 
     * @param _taskGoodsID 任务道具编号
     * @return 任务道具
     */
    public TaskTool getTaskTool (int _taskGoodsID)
    {
        return dictionary.get(_taskGoodsID);
    }

    /**
     * 加载任务物品
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
                        log.debug("start load task tool id = " + subE.elementTextTrim("id"));
                        TaskTool goods = new TaskTool(Short.parseShort(subE
                                .elementTextTrim("stackNums")), subE
                                .elementTextTrim("isShare").equals("是"), subE
                                .elementTextTrim("useable").equals("是"));
                        goods.setID(Integer
                                .parseInt(subE.elementTextTrim("id")));

                        goods.setName(subE.elementTextTrim("name"));
                        goods.setTrait(EGoodsTrait.getTrait(subE.elementTextTrim("trait")==null?EGoodsTrait.BING_ZHI.getDesc():subE.elementTextTrim("trait")));

                        if (goods.useable())
                        {
                            if (subE.elementTextTrim("disappear").equals("是"))  
                            {
                                goods.disappearAfterUse();
                            }

                            if (subE.elementTextTrim("limitLocation").equals(
                                    "是"))
                            {
                                goods.limitLocation();
                                goods.setLocationOfUse(Integer.parseInt(subE
                                        .elementTextTrim("limitMapID")), Short
                                        .parseShort(subE
                                                .elementTextTrim("limitMapX")),
                                        Short.parseShort(subE
                                                .elementTextTrim("limitMapY")),
                                        Short.parseShort(subE
                                                .elementTextTrim("range")));
                            }

                            String data = subE.elementTextTrim("getGoodsID");
                            if (null != data)
                            {
                                goods.setGetGoodsAfterUse(Integer
                                        .parseInt(data));
                            }

                            data = subE.elementTextTrim("targetNpcID");
                            if (null != data)
                            {
                                goods.setTargetNpcModelID(data);

                                data = subE.elementTextTrim("hpPercent");

                                if (null != data)
                                {
                                    goods.setTargetTraceHpPercent(DataConvertor
                                            .percentElementsString2Float(data));
                                }

                                if (subE.elementTextTrim("targetNpcDisappear")
                                        .equals("是"))
                                {
                                    goods.targetDisappearAfterUse();
                                }
                            }

                            data = subE.elementTextTrim("refreshNpcID");

                            if (null != data)
                            {
                                goods.setRefreshNpcModelIDAfterUse(data);
                                goods
                                        .setRefreshNpcNumsAfterUse(Short
                                                .parseShort(subE
                                                        .elementTextTrim("refreshNpcNums")));
                            }
                        }

                        goods.setIconID(Short.parseShort(subE
                                .elementTextTrim("icon")));
                        goods.initDescription();
                        goods.appendDescription(subE
                                .elementTextTrim("description"));

                        dictionary.put(goods.getID(), goods);
                        log.debug("任务道具加载成功: "+goods.getID());
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
