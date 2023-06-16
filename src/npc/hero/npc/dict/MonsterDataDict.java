package hero.npc.dict;

/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @创建者  DingChu
 *  @版本    1.0
 *  @时间    2008-10-15 下午03:46:16
 *
 *  <pre>
 *      Description:
 *  </pre>
 **/

import hero.share.service.LogWriter;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class MonsterDataDict
{
    private static Logger log = Logger.getLogger(MonsterDataDict.class);
    /**
     * 字典容器
     */
    private FastMap<String, MonsterData> monsterDataDict;

    /**
     * 单例
     */
    private static MonsterDataDict       instance;

    /**
     * 私有构造
     */
    private MonsterDataDict()
    {
        monsterDataDict = new FastMap<String, MonsterData>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static MonsterDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MonsterDataDict();
        }

        return instance;
    }

    /**
     * @return
     */
    public MonsterData getMonsterData (String _monsterModelID)
    {
        return monsterDataDict.get(_monsterModelID);
    }

    /**
     * 加载怪物模板对象
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
                        MonsterData monsterData = new MonsterData();

                        try
                        {
                            monsterData.modelID = subE.elementTextTrim("id")
                                    .toLowerCase();
                            log.debug("monsterData modelID = " + monsterData.modelID);
                            monsterData.name = subE.elementTextTrim("name");

                            monsterData.level = subE.elementTextTrim("level");
                            monsterData.clan = subE.elementTextTrim("clan");
                            monsterData.vocation = subE
                                    .elementTextTrim("vocation");
                            monsterData.type = subE.elementTextTrim("type");
                            monsterData.isActive = subE
                                    .elementTextTrim("activeOrPassive");
                            monsterData.existsTime = subE
                                    .elementTextTrim("existsTime");
                            monsterData.isInDungeon = subE
                                    .elementTextTrim("isInDungeon");
                            monsterData.normalOrBoss = subE
                                    .elementTextTrim("normalOrBoss");
                            monsterData.atkRange = subE
                                    .elementTextTrim("atkRange");
                            monsterData.immobilityTime = subE
                                    .elementTextTrim("immobilityTime");
                            monsterData.assistAttackRange = subE
                                    .elementTextTrim("assistAttackRange");
                            monsterData.assistPara = subE
                                    .elementTextTrim("assistPara");
                            monsterData.strength = subE
                                    .elementTextTrim("strength");
                            monsterData.agility = subE
                                    .elementTextTrim("agility");
                            monsterData.stamina = subE
                                    .elementTextTrim("stamina");
                            monsterData.inte = subE.elementTextTrim("inte");
                            monsterData.spirit = subE.elementTextTrim("spirit");
                            monsterData.lucky = subE.elementTextTrim("lucky");
                            monsterData.defense = subE
                                    .elementTextTrim("defense");
                            monsterData.minPhysicsAttack = subE
                                    .elementTextTrim("minPhysicsAttack");
                            monsterData.maxPhysicsAttack = subE
                                    .elementTextTrim("maxPhysicsAttack");
                            monsterData.sanctity = subE
                                    .elementTextTrim("sanctity");
                            monsterData.umbra = subE.elementTextTrim("umbra");
                            monsterData.fire = subE.elementTextTrim("fire");
                            monsterData.water = subE.elementTextTrim("water");
                            monsterData.soil = subE.elementTextTrim("soil");
                            monsterData.magicType = subE
                                    .elementTextTrim("magic");
                            monsterData.minDamageValue = subE
                                    .elementTextTrim("minDamageValue");
                            monsterData.maxDamageValue = subE
                                    .elementTextTrim("maxDamageValue");
                            monsterData.money = subE.elementTextTrim("money");

                            String soulID;

                            for (int i = 1; i <= 3; i++)
                            {
                                soulID = subE.elementTextTrim("soulID" + i);

                                if (null != soulID)
                                {
                                    if (null == monsterData.soulIDList)
                                    {
                                        monsterData.soulIDList = new Vector<Integer>();
                                    }

                                    monsterData.soulIDList.add(Integer
                                            .parseInt(soulID));
                                }
                                else
                                {
                                    break;
                                }
                            }

                            monsterData.aiID = subE.elementTextTrim("aiID");

                            String legacyNumberStr = subE
                                    .elementTextTrim("legacyTypeNums");

                            if (null != legacyNumberStr)
                            {
                                monsterData.legacyTypeNums = legacyNumberStr;

                                int legacyNumber = Integer
                                        .parseInt(legacyNumberStr);

                                if (legacyNumber > 0 && legacyNumber <= 12)
                                {
                                    legacyNumberStr = subE
                                            .elementTextTrim("legacyTypeSmallestNums");

                                    if (null != legacyNumberStr)
                                    {
                                        legacyNumber = Integer
                                                .parseInt(legacyNumberStr);

                                        if (legacyNumber >= 0
                                                && legacyNumber <= Integer
                                                        .parseInt(monsterData.legacyTypeNums))
                                        {
                                            monsterData.legacyTypeSmallestNums = legacyNumberStr;
                                        }
                                        else
                                        {
                                            log.error("怪物掉落物品最少数量数据错误，编号:"
                                                            + monsterData.modelID);

                                            continue;
                                        }
                                    }
                                    else
                                    {
                                        monsterData.legacyTypeSmallestNums = "0";
                                    }

                                     String legacyTypeMostNums = subE
                                            .elementTextTrim("legacyTypeMostNums");

                                    if(null != legacyTypeMostNums){

                                        legacyNumber = Integer
                                                .parseInt(legacyTypeMostNums);

                                        if (legacyNumber > 0
                                                && legacyNumber <= Integer
                                                        .parseInt(monsterData.legacyTypeNums))
                                        {
                                            monsterData.legacyTypeMostNums = legacyTypeMostNums;
                                        }
                                        else
                                        {
                                            log.error("怪物掉落物品最多数量数据错误，编号:"
                                                    + monsterData.modelID);

                                            continue;
                                        }
                                    }else{
                                    	monsterData.legacyTypeMostNums = "0";
                                    }
                                }
                                else
                                {
                                    monsterData.legacyTypeNums = null;
                                }
                            }

                            monsterData.item1 = subE.elementTextTrim("item1");

                            if (null != monsterData.item1)
                            {
                                monsterData.item1Odds = subE
                                        .elementTextTrim("item1Odds");
                                monsterData.item1nums = subE
                                        .elementTextTrim("item1nums");

                                monsterData.item2 = subE
                                        .elementTextTrim("item2");
                                if (null != monsterData.item2)
                                {
                                    monsterData.item2Odds = subE
                                            .elementTextTrim("item2Odds");
                                    monsterData.item2nums = subE
                                            .elementTextTrim("item2nums");

                                    monsterData.item3 = subE
                                            .elementTextTrim("item3");
                                    if (null != monsterData.item3)
                                    {
                                        monsterData.item3Odds = subE
                                                .elementTextTrim("item3Odds");
                                        monsterData.item3nums = subE
                                                .elementTextTrim("item3nums");
                                        monsterData.item4 = subE
                                                .elementTextTrim("item4");

                                        if (null != monsterData.item4)
                                        {
                                            monsterData.item4Odds = subE
                                                    .elementTextTrim("item4Odds");
                                            monsterData.item4nums = subE
                                                    .elementTextTrim("item4nums");
                                            monsterData.item5 = subE
                                                    .elementTextTrim("item5");

                                            if (null != monsterData.item5)
                                            {
                                                monsterData.item5Odds = subE
                                                        .elementTextTrim("item5Odds");
                                                monsterData.item5nums = subE
                                                        .elementTextTrim("item5nums");
                                                monsterData.item6 = subE
                                                        .elementTextTrim("item6");

                                                if (null != monsterData.item6)
                                                {
                                                    monsterData.item6Odds = subE
                                                            .elementTextTrim("item6Odds");
                                                    monsterData.item6nums = subE
                                                            .elementTextTrim("item6nums");
                                                    monsterData.item7 = subE
                                                            .elementTextTrim("item7");

                                                    if (null != monsterData.item7)
                                                    {
                                                        monsterData.item7Odds = subE
                                                                .elementTextTrim("item7Odds");
                                                        monsterData.item7nums = subE
                                                                .elementTextTrim("item7nums");
                                                        monsterData.item8 = subE
                                                                .elementTextTrim("item8");

                                                        if (null != monsterData.item8)
                                                        {
                                                            monsterData.item8Odds = subE
                                                                    .elementTextTrim("item8Odds");
                                                            monsterData.item8nums = subE
                                                                    .elementTextTrim("item8nums");
                                                            monsterData.item9 = subE
                                                                    .elementTextTrim("item9");

                                                            if (null != monsterData.item9)
                                                            {
                                                                monsterData.item9Odds = subE
                                                                        .elementTextTrim("item9Odds");
                                                                monsterData.item9nums = subE
                                                                        .elementTextTrim("item9nums");
                                                                monsterData.item10 = subE
                                                                        .elementTextTrim("item10");

                                                                if (null != monsterData.item10)
                                                                {
                                                                    monsterData.item10Odds = subE
                                                                            .elementTextTrim("item10Odds");
                                                                    monsterData.item10nums = subE
                                                                            .elementTextTrim("item10nums");
                                                                    monsterData.item11 = subE
                                                                            .elementTextTrim("item11");

                                                                    if (null != monsterData.item11)
                                                                    {
                                                                        monsterData.item11Odds = subE
                                                                                .elementTextTrim("item11Odds");
                                                                        monsterData.item11nums = subE
                                                                                .elementTextTrim("item11nums");
                                                                        monsterData.item12 = subE
                                                                                .elementTextTrim("item12");

                                                                        if (null != monsterData.item12)
                                                                        {
                                                                            monsterData.item12Odds = subE
                                                                                    .elementTextTrim("item12Odds");
                                                                            monsterData.item12nums = subE
                                                                                    .elementTextTrim("item12nums");
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            monsterData.retime = subE.elementTextTrim("retime");
                            //edit by zhengl ; date: 2010-11-18 ; note: 废弃使用imageID,改为透传动画ID
                            monsterData.imageID = subE.elementTextTrim("faceID");
                            monsterData.animationID = subE.elementTextTrim("animationID");
                            //end

                            if (!monsterDataDict.containsKey(monsterData.modelID))
                            {
                                monsterDataDict.put(monsterData.modelID, monsterData);
                            }
                            else
                            {
                                log.error("重复的怪物数据，编号:"
                                        + monsterData.modelID);
                            }

                        }
                        catch (Exception e)
                        {
                            log.error("加载怪物数据出错，编号: " + monsterData.modelID,e);

                        }
                    }
                }
                log.debug("monsterdata dict size = " + monsterDataDict.size());
                /*for(String mid : monsterDataDict.keySet()){
                    log.debug("monsterid = " + mid);
                }*/
            }
        }
        catch (Exception e)
        {
            log.error("load monster error: ",e);
//            LogWriter.error(this, e);
        }
    }

    /**
     * @文件 MonsterData.java
     * @创建者 DingChu
     * @版本 1.0
     * @时间 2008-12-30 下午09:53:32
     * @描述 ：
     */
    public static class MonsterData
    {
        public String          modelID, name, level, clan, vocation, type,
                isActive, existsTime, normalOrBoss, isInDungeon, atkRange,
                immobilityTime, assistAttackRange, assistPara, strength,
                agility, stamina, inte, spirit, lucky, defense,
                minPhysicsAttack, maxPhysicsAttack, sanctity, umbra, fire,
                water, soil, magicType, minDamageValue, maxDamageValue, money,
                aiID, legacyTypeNums, legacyTypeSmallestNums,
                legacyTypeMostNums, retime, imageID, animationID;

        public String          item1, item1Odds, item1nums, item2, item2Odds,
                item2nums, item3, item3Odds, item3nums, item4, item4Odds,
                item4nums, item5, item5Odds, item5nums, item6, item6Odds,
                item6nums, item7, item7Odds, item7nums, item8, item8Odds,
                item8nums, item9, item9Odds, item9nums, item10, item10Odds,
                item10nums, item11, item11Odds, item11nums, item12, item12Odds,
                item12nums;

        public Vector<Integer> soulIDList;
    }
}
