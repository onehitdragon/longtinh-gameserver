package hero.npc.ai.data;

import hero.skill.ActiveSkill;
import hero.skill.service.SkillServiceImpl;
import hero.npc.Monster;
import hero.npc.ai.SkillAI;
import hero.npc.ai.SpecialAI;
import hero.share.EMagic;
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
 * @文件 AIDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-16 下午03:56:55
 * @描述 ：
 */

public class AIDataDict
{
     private static Logger log = Logger.getLogger(AIDataDict.class);
    /**
     * 召唤数据列表
     */
    private FastMap<Integer, Shout>         shoutDataTable;

    /**
     * 召唤数据列表
     */
    private FastMap<Integer, Call>          callDataTable;

    /**
     * 变身数据列表
     */
    private FastMap<Integer, Changes>       changesDataTable;

    /**
     * 消失数据列表
     */
    private FastMap<Integer, Disappear>     disappearDataTable;

    /**
     * 逃跑数据列表
     */
    private FastMap<Integer, RunAway>       runAwayDataTable;

    /**
     * 特殊AI数据列表（引用召唤、变身、消失、逃跑），与技能AI并列
     */
    private FastMap<Integer, SpecialAIData> specialAIDataTable;

    /**
     * 技能AI数据列表（引用技能），与特殊AI并列
     */
    private FastMap<Integer, SkillAIData>   skillAIDataTable;

    /**
     * 战斗AI列表，为怪物直接引用，内含特殊AI和技能AI
     */
    private FastMap<Integer, FightAIData>   fightAIDataTable;

    /**
     * 单例
     */
    private static AIDataDict               instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static AIDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new AIDataDict();
        }

        return instance;
    }

    /**
     * 私有构造
     */
    private AIDataDict()
    {
        shoutDataTable = new FastMap<Integer, Shout>();
        callDataTable = new FastMap<Integer, Call>();
        changesDataTable = new FastMap<Integer, Changes>();
        disappearDataTable = new FastMap<Integer, Disappear>();
        runAwayDataTable = new FastMap<Integer, RunAway>();
        specialAIDataTable = new FastMap<Integer, SpecialAIData>();
        skillAIDataTable = new FastMap<Integer, SkillAIData>();
        fightAIDataTable = new FastMap<Integer, FightAIData>();
    }

    public void load (String _shoutDataPath, String _callDataPath,
            String _changesDataPath, String _disappearDataPath,
            String _runAwayDataPath, String _specialAIDataPath,
            String _skillAIDataPath, String _fightAIDataPath)
    {
        shoutDataTable.clear();
        callDataTable.clear();
        changesDataTable.clear();
        disappearDataTable.clear();
        runAwayDataTable.clear();
        specialAIDataTable.clear();
        skillAIDataTable.clear();
        fightAIDataTable.clear();

        loadSkillAIData(_skillAIDataPath);
        loadShoutData(_shoutDataPath);
        loadCallData(_callDataPath);
        loadDisappearData(_disappearDataPath);
        loadRunAwayData(_runAwayDataPath);
        loadChangesData(_changesDataPath);
        loadSpecialAIData(_specialAIDataPath);
        loadFightAIData(_fightAIDataPath);
    }

    /**
     * 获取战斗AI数据
     * 
     * @param _fightAIID
     * @return
     */
    public FightAIData getFightAIData (int _fightAIID)
    {
        if (_fightAIID > 0)
        {
            return fightAIDataTable.get(_fightAIID);
        }
        else
        {
            return null;
        }
    }

    /**
     * 获取喊话数据
     * 
     * @param _id
     * @return
     */
    public Shout getShoutData (int _id)
    {
        return shoutDataTable.get(_id);
    }

    /**
     * 获取召唤数据
     * 
     * @param _id
     * @return
     */
    public Call getCallData (int _id)
    {
        return callDataTable.get(_id);
    }

    /**
     * 获取变身数据
     * 
     * @param _id
     * @return
     */
    public Changes getChangesData (int _id)
    {
        return changesDataTable.get(_id);
    }

    /**
     * 获取消失数据
     * 
     * @param _id
     * @return
     */
    public Disappear getDisappearData (int _id)
    {
        return disappearDataTable.get(_id);
    }

    /**
     * 获取逃跑数据
     * 
     * @param _id
     * @return
     */
    public RunAway getRunAwayData (int _id)
    {
        return runAwayDataTable.get(_id);
    }

    /**
     * 创建技能AI数组
     * 
     * @param _monster
     * @param _dataList
     * @return
     */
    public SkillAI[] buildSkillAIList (SkillAIData[] _dataList,
            float _traceHpPercent)
    {
        if (null != _dataList && _dataList.length > 0)
        {
            SkillAI[] skillAIList = new SkillAI[_dataList.length];

            for (int i = 0; i < _dataList.length; i++)
            {
                skillAIList[i] = new SkillAI(_dataList[i], _traceHpPercent);
            }

            return skillAIList;
        }

        return null;
    }

    /**
     * 获取特殊AI
     * 
     * @param _specialAIData 特殊AI数据
     * @return
     */
    public SpecialWisdom getSpecialWisdom (SpecialAIData _specialAIData)
    {
        switch (_specialAIData.specialWisdomType)
        {
            case SpecialWisdom.CALL:
            {
                return callDataTable.get(_specialAIData.specialWisdomID);
            }
            case SpecialWisdom.CHANGES:
            {
                return changesDataTable.get(_specialAIData.specialWisdomID);
            }
            case SpecialWisdom.RUN_AWAY:
            {
                return runAwayDataTable.get(_specialAIData.specialWisdomID);
            }
            case SpecialWisdom.DISAPPEAR:
            {
                return disappearDataTable.get(_specialAIData.specialWisdomID);
            }
            case SpecialWisdom.SHOUT:
            {
                return shoutDataTable.get(_specialAIData.specialWisdomID);
            }
            default:
            {
                return null;
            }
        }
    }

    /**
     * 加载喊话数据
     * 
     * @param _dataPath
     */
    private void loadShoutData (String _dataPath)
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
                        Shout shout = new Shout();

                        shout.id = Integer.parseInt(subE.elementTextTrim("id"));
                        shout.shoutContent = subE
                                .elementTextTrim("shoutContent");

                        shoutDataTable.put(shout.id, shout);
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
     * 加载召唤数据
     * 
     * @param _dataPath
     */
    private void loadCallData (String _dataPath)
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
                        Call call = new Call();

                        call.id = Integer.parseInt(subE.elementTextTrim("id"));
                        call.shoutContent = subE
                                .elementTextTrim("shoutContent");

                        byte npcTypeNumber = Byte.parseByte(subE
                                .elementTextTrim("npcTypeNumber"));

                        if (0 < npcTypeNumber)
                        {
                            call.monsterModelIDs = new String[npcTypeNumber];
                            call.monsterDataArray = new short[npcTypeNumber][4];

                            for (int i = 0; i < npcTypeNumber; i++)
                            {
                                call.monsterModelIDs[i] = subE
                                        .elementTextTrim("npc" + (i + 1) + "Id");
                                call.monsterDataArray[i][Call.MONSTER_DATA_INDEX_OF_NUMBER] = Short
                                        .parseShort(subE.elementTextTrim("npc"
                                                + (i + 1) + "Number"));

                                String locationType = subE
                                        .elementTextTrim("npc" + (i + 1)
                                                + "LocationType");

                                if (locationType.equals("地图固定坐标"))
                                {
                                    call.monsterDataArray[i][Call.MONSTER_DATA_INDEX_OF_LOCATION_TYPE] = Call.LOCATION_TYPE_ABSTRACT_OF_MAP;
                                }
                                else
                                {
                                    call.monsterDataArray[i][Call.MONSTER_DATA_INDEX_OF_LOCATION_TYPE] = Call.LOCATION_TYPE_MONSTER_SELF;
                                }

                                call.monsterDataArray[i][Call.MONSTER_DATA_INDEX_OF_LOCATION_X] = Short
                                        .parseShort(subE.elementTextTrim("npc"
                                                + (i + 1) + "X"));
                                call.monsterDataArray[i][Call.MONSTER_DATA_INDEX_OF_LOCATION_Y] = Short
                                        .parseShort(subE.elementTextTrim("npc"
                                                + (i + 1) + "Y"));
                            }
                        }

                        callDataTable.put(call.id, call);
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
     * 加载消失数据
     * 
     * @param _dataPath
     */
    private void loadDisappearData (String _dataPath)
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
                        Disappear disappear = new Disappear();

                        disappear.id = Integer.parseInt(subE
                                .elementTextTrim("id"));
                        disappear.keepTime = Integer.parseInt(subE
                                .elementTextTrim("keepTime")) * 1000;
                        disappear.shoutContent = subE
                                .elementTextTrim("shoutContent");

                        disappearDataTable.put(disappear.id, disappear);
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
     * 加载逃跑数据
     * 
     * @param _dataPath
     */
    private void loadRunAwayData (String _dataPath)
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
                        RunAway runAway = new RunAway();

                        runAway.id = Integer.parseInt(subE
                                .elementTextTrim("id"));
                        runAway.range = Short.parseShort(subE
                                .elementTextTrim("range"));
                        runAway.shoutContent = subE
                                .elementTextTrim("shoutContent");

                        runAwayDataTable.put(runAway.id, runAway);
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
     * 加载技能AI数据
     * 
     * @param _dataPath
     */
    private void loadSkillAIData (String _dataPath)
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
                        SkillAIData skillAI = new SkillAIData();

                        skillAI.id = Integer.parseInt(subE
                                .elementTextTrim("id"));

                        String useTimesType = subE
                                .elementTextTrim("useTimesType");

                        if (useTimesType.equals("间隔型"))
                        {
                            skillAI.useTimesType = SkillAI.USE_TIMES_OF_INTERVAL;
                        }
                        else
                        {
                            skillAI.useTimesType = SkillAI.USE_TIMES_OF_ONLY;
                        }

                        if (SkillAI.USE_TIMES_OF_INTERVAL == skillAI.useTimesType)
                        {
                            String conditionOfInterval = subE
                                    .elementTextTrim("hpConsumePercent");

                            if (null != conditionOfInterval)
                            {
                                skillAI.intervalCondition = SkillAI.INTERVAL_CONDITION_OF_HP_CONSUME;
                            }
                            else
                            {
                                conditionOfInterval = subE
                                        .elementTextTrim("intervalTime");

                                if (null != conditionOfInterval)
                                {
                                    skillAI.intervalCondition = SkillAI.INTERVAL_CONDITION_OF_TIME;
                                }
                                else
                                {
                                    conditionOfInterval = subE
                                            .elementTextTrim("hatredTargetDie");
                                    //edit by zhengl; date: 2011-02-15; note: 添加 != null的容错
                                    if (conditionOfInterval != null && conditionOfInterval.equals("是"))
                                    {
                                        skillAI.intervalCondition = SkillAI.INTERVAL_CONDITION_OF_HATRED_TARGET_DIE;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                            }

                            skillAI.odds = Integer.parseInt(subE
                                    .elementTextTrim("odds")) / 100F;
                        }
                        else
                        {
                            String conditionOfOnly = subE
                                    .elementTextTrim("timeOfInFighting");

                            if (null != conditionOfOnly)
                            {
                                skillAI.onlyReleaseCondition = SkillAI.ONLY_CONDITION_OF_FIGHT_TIME;
                                skillAI.timeOfFighting = Integer
                                        .parseInt(conditionOfOnly) * 1000;
                            }
                            else
                            {
                                conditionOfOnly = subE
                                        .elementTextTrim("hpTracePercent");

                                if (null != conditionOfOnly)
                                {
                                    skillAI.onlyReleaseCondition = SkillAI.ONLY_CONDITION_OF_TRACE_HP;
                                    skillAI.hpTracePercent = Integer
                                            .parseInt(conditionOfOnly) / 100F;
                                }
                                else
                                {
                                    conditionOfOnly = subE
                                            .elementTextTrim("mpTracePercent");

                                    if (null != conditionOfOnly)
                                    {
                                        skillAI.onlyReleaseCondition = SkillAI.ONLY_CONDITION_OF_TRACE_MP;
                                        skillAI.mpTracePercent = Integer
                                                .parseInt(conditionOfOnly) / 100F;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                            }
                        }

                        String targetSettingCondition = subE
                                .elementTextTrim("targetSettingCondition");

                        if (targetSettingCondition.equals("仇恨值") 
                        		|| targetSettingCondition.equals("仇恨"))
                        {
                            skillAI.targetSettingCondition = SkillAI.TARGET_SET_CONDITION_OF_HATRED;
                        }
                        else if (targetSettingCondition.equals("生命值"))
                        {
                            skillAI.targetSettingCondition = SkillAI.TARGET_SET_CONDITION_OF_HP;
                        }
                        else if (targetSettingCondition.equals("魔法值"))
                        {
                            skillAI.targetSettingCondition = SkillAI.TARGET_SET_CONDITION_OF_MP;
                        }
                        else if (targetSettingCondition.equals("距离"))
                        {
                            skillAI.targetSettingCondition = SkillAI.TARGET_SET_CONDITION_OF_DISTANCE;
                        }
                        else if (targetSettingCondition.equals("自身"))
                        {
                            skillAI.targetSettingCondition = SkillAI.TARGET_SET_CONDITION_OF_SELF_CENTER;
                        }
                        else
                        {
                            continue;
                        }

                        if (SkillAI.TARGET_SET_CONDITION_OF_SELF_CENTER 
                        		!= skillAI.targetSettingCondition)
                        {
                            skillAI.sequenceOfSettingCondition = Byte
                                    .parseByte(subE
                                            .elementTextTrim("conditionSequence"));
                        }

                        String releaseDelay = subE.elementTextTrim("delay");

                        if (null != releaseDelay)
                        {
                            skillAI.releaseDelay = Integer
                                    .parseInt(releaseDelay) * 1000;
                        }
                        int skillId = Integer.parseInt(subE.elementTextTrim("skillId"));
                        skillAI.shoutContentWhenRelease = subE
                                .elementTextTrim("shoutContent");
                        skillAI.skill = 
                        	(ActiveSkill) SkillServiceImpl.getInstance().getMonsterSkillModel(skillId);
                        if(skillAI.skill == null) {
                        	log.warn("wrong:加载的怪物技能为NULL,请检查配置文件");
                        }
                        skillAIDataTable.put(skillAI.id, skillAI);
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
     * 加载变身数据
     * 
     * @param _dataPath
     */
    private void loadChangesData (String _dataPath)
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
                        Changes changes = new Changes();

                        changes.id = Integer.parseInt(subE
                                .elementTextTrim("id"));
                        changes.shoutContent = subE
                                .elementTextTrim("shoutContent");
                        changes.strength = Integer.parseInt(subE
                                .elementTextTrim("strength"));
                        changes.agility = Integer.parseInt(subE
                                .elementTextTrim("agility"));
                        changes.inte = Integer.parseInt(subE
                                .elementTextTrim("inte"));
                        changes.spirit = Integer.parseInt(subE
                                .elementTextTrim("spirit"));
                        changes.lucky = Integer.parseInt(subE
                                .elementTextTrim("lucky"));
                        changes.minAttack = Integer.parseInt(subE
                                .elementTextTrim("minAttack"));
                        changes.maxAttack = Integer.parseInt(subE
                                .elementTextTrim("maxAttack"));

                        String magicType = subE.elementTextTrim("magic");

                        if (null != magicType)
                        {
                            changes.magicType = EMagic.getMagic(magicType);
                            changes.minDamageValue = Integer.parseInt(subE
                                    .elementTextTrim("minDamageValue"));
                            changes.maxDamageValue = Integer.parseInt(subE
                                    .elementTextTrim("maxDamageValue"));
                        }

                        changes.defense = Integer.parseInt(subE
                                .elementTextTrim("defense"));
                        changes.sanctityFastness = Integer.parseInt(subE
                                .elementTextTrim("sanctityFastness"));
                        changes.fireFastness = Integer.parseInt(subE
                                .elementTextTrim("fireFastness"));
                        changes.waterFastness = Integer.parseInt(subE
                                .elementTextTrim("waterFastness"));
                        changes.soilFastness = Integer.parseInt(subE
                                .elementTextTrim("soilFastness"));

                        if (subE.elementTextTrim("useNewHp").equals("是"))
                        {
                            changes.newHp = Integer.parseInt(subE
                                    .elementTextTrim("newHp"));
                        }

                        byte skillAiNumber = Byte.parseByte(subE
                                .elementTextTrim("skillAiNumber"));

                        if (0 < skillAiNumber)
                        {
                            changes.skillAIDataList = new SkillAIData[skillAiNumber];

                            for (int i = 0; i < skillAiNumber; i++)
                            {
                                changes.skillAIDataList[i] = skillAIDataTable
                                        .get(Integer.parseInt(subE
                                                .elementTextTrim("skillAi"
                                                        + (i + 1))));
                            }
                        }

                        changes.imageID = Short.parseShort(subE
                                .elementTextTrim("imageId"));

                        changesDataTable.put(changes.id, changes);
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
     * 加载特殊AI数据
     * 
     * @param _dataPath
     */
    private void loadSpecialAIData (String _dataPath)
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
                        SpecialAIData specialAI = new SpecialAIData();

                        specialAI.id = Integer.parseInt(subE
                                .elementTextTrim("id"));

                        String emergeTimesType = subE
                                .elementTextTrim("emergeTimesType");

                        if (emergeTimesType.equals("间隔型"))
                        {
                            specialAI.useTimesType = SpecialAI.USE_TIMES_OF_INTERVAL;
                        }
                        else
                        {
                            specialAI.useTimesType = SpecialAI.USE_TIMES_OF_ONLY;
                        }

                        if (SpecialAI.USE_TIMES_OF_INTERVAL == specialAI.useTimesType)
                        {
                            String conditionOfInterval = subE
                                    .elementTextTrim("hpConsumePercent");

                            if (null != conditionOfInterval)
                            {
                                specialAI.intervalCondition = SpecialAI.INTERVAL_CONDITION_OF_HP_CONSUME;
                                specialAI.hpConsumePercent = Integer
                                        .parseInt(conditionOfInterval) / 100F;
                            }
                            else
                            {
                                conditionOfInterval = subE
                                        .elementTextTrim("intervalTime");

                                if (null != conditionOfInterval)
                                {
                                    specialAI.intervalCondition = SpecialAI.INTERVAL_CONDITION_OF_TIME;
                                    specialAI.intervalTime = Integer
                                            .parseInt(conditionOfInterval) * 1000;
                                }
                                else
                                {
                                    conditionOfInterval = subE
                                            .elementTextTrim("hatredTargetDie");

                                    if (conditionOfInterval.equals("是"))
                                    {
                                        specialAI.intervalCondition = SpecialAI.INTERVAL_CONDITION_OF_HATRED_TARGET_DIE;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                            }
                        }
                        else
                        {
                            String conditionOfOnly = subE
                                    .elementTextTrim("hpTracePercent");

                            if (null != conditionOfOnly)
                            {
                                specialAI.onlyReleaseCondition = SpecialAI.ONLY_CONDITION_OF_TRACE_HP;
                                specialAI.hpTracePercent = Integer
                                        .parseInt(conditionOfOnly) / 100F;
                            }
                            else
                            {
                                conditionOfOnly = subE
                                        .elementTextTrim("mpTracePercent");

                                if (null != conditionOfOnly)
                                {
                                    specialAI.onlyReleaseCondition = SpecialAI.ONLY_CONDITION_OF_TRACE_MP;
                                    specialAI.mpTracePercent = Integer
                                            .parseInt(conditionOfOnly) / 100F;
                                }
                                else
                                {
                                    continue;
                                }
                            }
                        }

                        String specialWisdomType = subE.elementTextTrim("type");
                        int specialWisdomID = Integer.parseInt(subE
                                .elementTextTrim("subAiId"));

                        specialAI.specialWisdomID = specialWisdomID;

                        if (specialWisdomType.equals("变身"))
                        {
                            specialAI.specialWisdomType = SpecialWisdom.CHANGES;
                        }
                        else if (specialWisdomType.equals("召唤"))
                        {
                            specialAI.specialWisdomType = SpecialWisdom.CALL;
                        }
                        else if (specialWisdomType.equals("消失"))
                        {
                            specialAI.specialWisdomType = SpecialWisdom.DISAPPEAR;
                        }
                        else if (specialWisdomType.equals("逃跑"))
                        {
                            specialAI.specialWisdomType = SpecialWisdom.RUN_AWAY;
                        }
                        else if (specialWisdomType.equals("喊话"))
                        {
                            specialAI.specialWisdomType = SpecialWisdom.SHOUT;
                        }
                        else
                        {
                            continue;
                        }

                        specialAIDataTable.put(specialAI.id, specialAI);
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
     * 加载战斗AI数据
     * 
     * @param _dataPath
     */
    private void loadFightAIData (String _dataPath)
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
                        FightAIData ai = new FightAIData();

                        ai.id = Integer.parseInt(subE.elementTextTrim("id"));

                        byte aiNumber = Byte.parseByte(subE
                                .elementTextTrim("specialAiNumber"));

                        if (0 < aiNumber)
                        {
                            ai.specialAIList = new SpecialAIData[aiNumber];

                            for (int i = 1; i <= aiNumber; i++)
                            {
                                ai.specialAIList[i - 1] = specialAIDataTable
                                        .get(Integer.parseInt(subE
                                                .elementTextTrim("specialAi"
                                                        + i)));
                            }
                        }

                        aiNumber = Byte.parseByte(subE
                                .elementTextTrim("skillAiNumber"));

                        if (0 < aiNumber)
                        {
                            ai.skillAIList = new SkillAIData[aiNumber];
                            int skillAIID = 0;
                            SkillAIData data = null;
                            for (int i = 0; i < aiNumber; i++)
                            {
                            	skillAIID = Integer.parseInt(subE.elementTextTrim("skillAi"+ (i + 1)));
                            	data = skillAIDataTable.get(skillAIID);
                            	if (data != null) 
                            	{
                            		ai.skillAIList[i] = data;
								}
                            	else 
                            	{
									log.error("加载怪物技能AI失败skillAIID="+skillAIID);
								}
                            }
                        }

                        fightAIDataTable.put(ai.id, ai);
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
