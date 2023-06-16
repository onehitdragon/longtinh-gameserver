package hero.npc.dict;

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
 * @文件 NpcDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午01:53:29
 * @描述 ：
 */

public class NpcDataDict
{
    private static Logger log = Logger.getLogger(NpcDataDict.class);
    /**
     * 字典容器
     */
    private FastMap<String, NpcData> npcDataDict;

    /**
     * 单例
     */
    private static NpcDataDict       instance;

    /**
     * 私有构造
     */
    private NpcDataDict()
    {
        npcDataDict = new FastMap<String, NpcData>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static NpcDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new NpcDataDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public NpcData getNpcData (String _npcModelID)
    {
        return npcDataDict.get(_npcModelID);
    }

    /**
     * 加载NPC模板对象
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

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        NpcData npcData = new NpcData();
                        try
                        {
                            npcData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();

                            String data = subE.elementTextTrim("name");

                            if (null == data)
                            {
                                npcData.name = "";
                            }
                            else
                            {
                                npcData.name = data;
                            }

                            data = subE.elementTextTrim("title");

                            if (null == data)
                            {
                                npcData.title = "";
                            }
                            else
                            {
                                npcData.title = data;
                            }

                            npcData.exsitTime = subE
                                    .elementTextTrim("exsitTime");
                            npcData.helloContent = NpcHelloContentDict
                                    .getInstance()
                                    .getHelloContent(
                                            Integer.parseInt(subE.elementTextTrim("helloContentID")));
                            npcData.screamContent = subE
                                    .elementTextTrim("screamContent");
                            //edit by zhengl ; date: 2010-11-18 ; note: 废弃使用imageID,改为透传动画ID
                            npcData.imageID = subE.elementTextTrim("imageID");
                            npcData.animationID = subE.elementTextTrim("animationID");
                            //end
                            npcData.imageType = subE.elementTextTrim("imageType");
                            npcData.clanDesc = subE.elementTextTrim("clan");
                            npcData.function1 = subE
                                    .elementTextTrim("function1");
                            npcData.function2 = subE
                                    .elementTextTrim("function2");
                            npcData.function3 = subE
                                    .elementTextTrim("function3");
                            npcData.function4 = subE
                                    .elementTextTrim("function4");
                            npcData.function5 = subE
                                    .elementTextTrim("function5");
                            npcData.skillEducateVocation = subE
                                    .elementTextTrim("skillTrainerVocation");
                            npcData.skillEducateFeature = subE
                                    .elementTextTrim("feature");

                            if (!npcDataDict.containsKey(npcData.modelID))
                            {
                                npcDataDict.put(npcData.modelID, npcData);
                            }
                            else
                            {
                                log.error("重复的NPC数据，编号:"+ npcData.modelID);
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("加载NPC数据错误，编号:" + npcData.modelID,e);
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

    public static void main (String[] args)
    {
        NpcHelloContentDict.getInstance().load(
                System.getProperty("user.dir") + "/" + "res/data/npc/hello/");
        NpcDataDict.getInstance().load(
                System.getProperty("user.dir") + "/" + "res/data/npc");
        NpcData data = NpcDataDict.getInstance().getNpcData("N1");
        System.out.println(data.name);
        System.out.println(data.function1);
        System.out.println(data.helloContent);
        System.out.println(data.imageID);
        System.out.println(data.imageType);
        System.out.println(data.skillEducateVocation);
    }

    /**
     * @文件 NpcData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2009-2-17 下午02:17:07
     * @描述 ：
     */

    public static class NpcData
    {
        public String   modelID, name, clanDesc, title, exsitTime,
                helloContent, screamContent, imageID, imageType, animationID;

        public String   function1, function2, function3, function4, function5,
                skillEducateVocation, skillEducateFeature;

        public byte[][] movePath;
    }
}
