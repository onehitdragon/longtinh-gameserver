package hero.item.dictionary;

import hero.share.AccessorialOriginalAttribute;
import hero.share.EMagic;
import hero.share.MagicFastnessList;
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
 * @文件 SuiteEquipmentDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-3 下午02:50:13
 * @描述 ：套装数据字典
 */

public class SuiteEquipmentDataDict
{
    /**
     * 套装数据表
     */
    private FastMap<Short, SuiteEData>    table;

    /**
     * 单例
     */
    private static SuiteEquipmentDataDict instance;

    /**
     * 私有构造
     */
    private SuiteEquipmentDataDict()
    {
        table = new FastMap<Short, SuiteEData>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static SuiteEquipmentDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new SuiteEquipmentDataDict();
        }

        return instance;
    }

    /**
     * 加载套装数据
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

                String data;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        SuiteEData suiteEData = new SuiteEData();

                        try
                        {
                            suiteEData.id = Short.parseShort(subE
                                    .elementTextTrim("id"));
                            suiteEData.name = subE.elementTextTrim("name");

                            for (int i = 1; i <= 4; i++)
                            {
                                data = subE.elementTextTrim("e" + i);

                                if (null == data)
                                {
                                    return;
                                }
                                else
                                {
                                    suiteEData.idList[i - 1] = Integer
                                            .parseInt(data);
                                }
                            }

                            data = subE.elementTextTrim("defense");

                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.defense = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("hp");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.hp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("mp");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.mp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("strength");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.strength = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("agility");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.agility = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("stamina");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.stamina = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("inte");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.inte = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("spirit");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.spirit = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("luck");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.lucky = Integer
                                        .parseInt(data);
                            }
                            data = subE
                                    .elementTextTrim("physicsDeathblowLevel");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.physicsDeathblowLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("magicDeathblowLevel");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.magicDeathblowLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("hitLevel");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.hitLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("duckLevel");
                            if (null != data)
                            {
                                if (suiteEData.atribute == null)
                                {
                                    suiteEData.atribute = new AccessorialOriginalAttribute();
                                }

                                suiteEData.atribute.duckLevel = (Short
                                        .parseShort(data));
                            }

                            data = subE.elementTextTrim("sanctity");
                            if (null != data)
                            {
                                if (suiteEData.fastnessList == null)
                                {
                                    suiteEData.fastnessList = new MagicFastnessList();
                                }

                                suiteEData.fastnessList.reset(EMagic.SANCTITY,
                                        Integer.parseInt(data));
                            }
                            data = subE.elementTextTrim("umbra");
                            if (null != data)
                            {
                                if (suiteEData.fastnessList == null)
                                {
                                    suiteEData.fastnessList = new MagicFastnessList();
                                }

                                suiteEData.fastnessList.reset(EMagic.UMBRA,
                                        Integer.parseInt(data));
                            }
                            data = subE.elementTextTrim("fire");
                            if (null != data)
                            {
                                if (suiteEData.fastnessList == null)
                                {
                                    suiteEData.fastnessList = new MagicFastnessList();
                                }

                                suiteEData.fastnessList.reset(EMagic.FIRE,
                                        Integer.parseInt(data));
                            }
                            data = subE.elementTextTrim("water");
                            if (null != data)
                            {
                                if (suiteEData.fastnessList == null)
                                {
                                    suiteEData.fastnessList = new MagicFastnessList();
                                }

                                suiteEData.fastnessList.reset(EMagic.WATER,
                                        Integer.parseInt(data));
                            }
                            data = subE.elementTextTrim("soil");
                            if (null != data)
                            {
                                if (suiteEData.fastnessList == null)
                                {
                                    suiteEData.fastnessList = new MagicFastnessList();
                                }

                                suiteEData.fastnessList.reset(EMagic.SOIL,
                                        Integer.parseInt(data));
                            }

                            data = subE.elementTextTrim("description");

                            if (null != data)
                            {
                                if (-1 != data.indexOf("\\n"))
                                {
                                    data = data.replaceAll("\\\\n", "\n");
                                }

                                suiteEData.description = data;
                            }
                            else
                            {
                                suiteEData.description = "";
                            }

                            table.put(suiteEData.id, suiteEData);
                        }
                        catch (Exception e)
                        {
                            LogWriter.println("加载套装装备数据出错");
                            LogWriter.error(null, e);
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
     * 获取套装数据
     * 
     * @param _suiteID 套装编号
     * @return
     */
    public SuiteEData getSuiteData (short _suiteID)
    {
        return table.get(_suiteID);
    }

    /**
     * @author DC 套装数据
     */
    public class SuiteEData
    {
        /**
         * 构造
         */
        public SuiteEData()
        {
            idList = new int[4];
        }

        /**
         * 编号
         */
        public short                        id;

        /**
         * 名称
         */
        public String                       name;

        /**
         * 套装包含的装备编号列表
         */
        public int[]                        idList;

        /**
         * 属性加成
         */
        public AccessorialOriginalAttribute atribute;

        /**
         * 抗性加成
         */
        public MagicFastnessList            fastnessList;

        /**
         * 效果描述
         */
        public String                       description;
    }
}
