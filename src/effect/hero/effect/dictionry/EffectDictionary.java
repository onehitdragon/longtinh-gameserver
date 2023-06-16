package hero.effect.dictionry;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.effect.Effect;
import hero.effect.Effect.AureoleRadiationRange;
import hero.effect.Effect.EKeepTimeType;
import hero.effect.Effect.EffectFeature;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.DynamicEffect;
import hero.effect.detail.StaticEffect;
import hero.effect.detail.TouchEffect;
import hero.effect.detail.DynamicEffect.ActionTimeType;
import hero.effect.service.EffectConfig;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.share.EMagic;
import hero.share.service.LogWriter;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EffectDictionry.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-13 下午05:53:59
 * @描述 ：
 */

public class EffectDictionary
{
    private static Logger log = Logger.getLogger(EffectDictionary.class);
    /**
     * 效果字典容器
     */
    private FastMap<Integer, Effect> effectTable;

    /**
     * 单例
     */
    private static EffectDictionary  instance;

    /**
     * 私有构造
     */
    private EffectDictionary()
    {
        effectTable = new FastMap<Integer, Effect>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static EffectDictionary getInstance ()
    {
        if (null == instance)
        {
            instance = new EffectDictionary();
        }

        return instance;
    }

    /**
     * 获取效果模板
     * 
     * @param _effectID
     * @return
     */
    public Effect getEffectRef (int _effectID)
    {
        return effectTable.get(_effectID);
    }

    /**
     * 获取效果模板
     * 
     * @param _effectID
     * @return
     */
    public Effect getEffectInstance (int _effectID)
    {
        try
        {
            return effectTable.get(_effectID).clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 加载数据
     * 
     * @param _config
     */
    public void load (EffectConfig _config)
    {
        effectTable.clear();

        loadStaticEffect(_config.static_effect_data_path);
        loadDynamicEffect(_config.dynamic_effect_data_path);
        loadTouchEffect(_config.touch_effect_data_path);
    }

    /**
     * 加载静态效果
     * 
     * @param _dataPath
     */
    private void loadStaticEffect (String _dataPath)
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
                StaticEffect effect;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        effect = new StaticEffect(Integer.parseInt(subE
                                .elementText("id")), subE.elementText("name")
                                .trim());

                        effect.level = Short.parseShort(subE
                                .elementTextTrim("level"));
                        effect.feature = EffectFeature.get(subE
                                .elementTextTrim("featureType"));
                        effect.featureLevel = Byte.parseByte(subE
                                .elementTextTrim("featureLevel"));

                        data = subE.elementText("isbuff");

                        if (null != data && data.equals("是"))
                        {
                            effect.trait = EffectTrait.BUFF;
                        }
                        else
                        {
                            effect.trait = EffectTrait.DEBUFFF;
                        }

                        data = subE.elementText("disAfterDie");

                        if (null != data && data.equals("是"))
                        {
                            effect.isClearAfterDie = true;
                        }
                        //add by zhengl; date: 2011-01-26; note:添加个容错.
                        data = subE.elementText("replaceID");
                        if(data != null) {
                            effect.replaceID = Integer.parseInt(data);
                        }


                        data = subE.elementText("keepTime");

                        if (data.equalsIgnoreCase("N/A"))
                        {
                            effect.keepTimeType = EKeepTimeType.N_A;

                            ETargetType targetType = ETargetType.get(subE
                                    .elementText("targetType"));

                            if (null != targetType)
                            {
                                AureoleRadiationRange actionTarget = new AureoleRadiationRange(
                                        targetType);

                                if (targetType != ETargetType.MYSELF)
                                {
                                    actionTarget.targetRangeType = ETargetRangeType
                                            .get(subE.elementText("rangeType"));
                                    actionTarget.rangeRadiu = Byte
                                            .parseByte(subE
                                                    .elementText("rangeRadiu"));
                                }

                                effect.aureoleRadiationRange = actionTarget;
                            }
                            else
                            {
                                LogWriter.println("光环没有作用对象：" + effect.ID);
                            }
                            //光环类型的时候添加光环展示效果.
                            data = subE.elementText("viewType");
                            if(data != null)
                            {
                            	effect.viewType = Byte.valueOf(data);
                            }
                            data = subE.elementText("imageID");
                            if(data != null)
                            {
                            	effect.imageID = Short.valueOf(data);
                            }
                            data = subE.elementText("animationID");
                            if(data != null)
                            {
                            	effect.animationID = Short.valueOf(data);
                            }
                        }
                        else
                        {
                            effect.keepTimeType = EKeepTimeType.LIMITED;
                            effect.setKeepTime(Short.parseShort(data));

                            data = subE.elementText("isAdditional");

                            if (null != data && data.equals("是"))
                            {
                                effect.totalTimes = Short.parseShort(subE
                                        .elementText("additionalTotal"));
                            }
                        }

                        data = subE.elementText("operator");

                        if (null != data)
                        {
                            EMathCaluOperator caluOperator = EMathCaluOperator
                                    .get(data);

                            if (null != caluOperator)
                            {
                                effect.caluOperator = EMathCaluOperator
                                        .get(data);
                            }
                            else
                            {
                                LogWriter.println("静态效果没有运算操作符号：" + effect.ID);
                            }
                        }

                        data = subE.elementText("defence");

                        if (null != data)
                        {
                            effect.defense = Float.parseFloat(data);
                        }

                        data = subE.elementText("maxHp");

                        if (null != data)
                        {
                            effect.maxHp = Float.parseFloat(data);
                        }

                        data = subE.elementText("maxMp");

                        if (null != data)
                        {
                            effect.maxMp = Float.parseFloat(data);
                        }

                        data = subE.elementText("strength");

                        if (null != data)
                        {
                            effect.strength = Float.parseFloat(data);
                        }

                        data = subE.elementText("agility");

                        if (null != data)
                        {
                            effect.agility = Float.parseFloat(data);
                        }

                        data = subE.elementText("stamina");

                        if (null != data)
                        {
                            effect.stamina = Float.parseFloat(data);
                        }

                        data = subE.elementText("inte");

                        if (null != data)
                        {
                            effect.inte = Float.parseFloat(data);
                        }

                        data = subE.elementText("spirit");

                        if (null != data)
                        {
                            effect.spirit = Float.parseFloat(data);
                        }

                        data = subE.elementText("lucky");

                        if (null != data)
                        {
                            effect.lucky = Float.parseFloat(data);
                        }

                        data = subE.elementText("phsicsDeathblowLevel");

                        if (null != data)
                        {
                            effect.physicsDeathblowLevel = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("magicDeathblowLevel");

                        if (null != data)
                        {
                            effect.magicDeathblowLevel = Float.parseFloat(data);
                        }

                        data = subE.elementText("hitLevel");

                        if (null != data)
                        {
                            effect.hitLevel = Float.parseFloat(data);
                        }

                        data = subE.elementText("phsicsDuckLevel");

                        if (null != data)
                        {
                            effect.physicsDuckLevel = Float.parseFloat(data);
                        }

                        data = subE.elementText("magicFastnessType");

                        EMagic magicType;

                        if (null != data)
                        {
                            if (data.equalsIgnoreCase("ALL"))
                            {
                                effect.magicFastnessValue = Float.parseFloat(subE
                                                .elementText("magicFastnessValue"));
                            }
                            else
                            {
                                magicType = EMagic.getMagic(data);

                                if (null != magicType)
                                {
                                    effect.magicFastnessType = magicType;
                                    effect.magicFastnessValue = Integer
                                            .parseInt(subE
                                                    .elementText("magicFastnessValue"));
                                }
                                else
                                {
                                    log.info("不存在的魔法类型：" + data);
                                }
                            }
                        }

                        data = subE.elementText("hate");

                        if (null != data)
                        {
                            effect.hate = Float.parseFloat(data);
                        }

                        data = subE.elementText("physicsAttackHarmValue");

                        if (null != data)
                        {
                            effect.physicsAttackHarmValue = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("bePhysicsHarmValue");

                        if (null != data)
                        {
                            effect.bePhysicsHarmValue = Float.parseFloat(data);
                        }

                        data = subE.elementText("magicAttackHarmType");

                        if (null != data)
                        {
                            if (data.equalsIgnoreCase("ALL"))
                            {
                                effect.magicHarmValue = Float.parseFloat(subE
                                        .elementText("magicAttackHarmValue"));
                            }
                            else
                            {
                                magicType = EMagic.getMagic(data);

                                if (null != magicType)
                                {
                                    effect.magicHarmValue = Float.parseFloat(
                                    		subE.elementText("magicAttackHarmValue"));
                                    effect.magicHarmType = magicType;
                                }
                                else
                                {
                                    log.info("不存在的魔法类型：" + data);
                                }
                            }
                        }
                        data = subE.elementText("beMagicHarmType");

                        if (null != data)
                        {
                            magicType = EMagic.getMagic(data.replaceAll("ALL", "全部"));

                            if (null != magicType)
                            {
                                effect.magicHarmValueBeAttack = Float
                                        .parseFloat(subE
                                                .elementText("beMagicHarmValue"));
                                effect.magicHarmTypeBeAttack = magicType;
                            }
                        }

                        data = subE.elementText("physicsAttackInterval");

                        if (null != data)
                        {
                            effect.physicsAttackInterval = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("skillReleaseTime");

                        if (null != data)
                        {
                            effect.allSkillReleaseTime = Float.parseFloat(data);
                        }
                        else
                        {
                            data = subE.elementText("specialSkillIDList");

                            if (null != data)
                            {
                                ArrayList<Integer> skillIDList = new ArrayList<Integer>();
                                String[] skillIDDomainList = data.split(",");

                                for (String skillIDomain : skillIDDomainList)
                                {
                                    String[] skillIDDescList = skillIDomain
                                            .split("-");

                                    if (skillIDDescList.length == 1)
                                    {
                                        skillIDList.add(Integer
                                                .parseInt(skillIDDescList[0]));
                                    }
                                    else
                                    {
                                        int minSkillID = Integer
                                                .parseInt(skillIDDescList[0]);
                                        int maxSkillID = Integer
                                                .parseInt(skillIDDescList[skillIDDescList.length - 1]);

                                        while (minSkillID <= maxSkillID)
                                        {
                                            skillIDList.add(minSkillID);
                                            minSkillID++;
                                        }
                                    }
                                }

                                if (skillIDList.size() > 0)
                                {
                                    effect.specialSkillReleaseTimeIDList = skillIDList;
                                    effect.specialSkillReleaseTime = Float
                                            .parseFloat(subE
                                                    .elementText("specialSkillReleaseTime"));
                                }
                            }
                        }

                        data = subE.elementText("reduceHarmType");

                        if (null != data)
                        {
                            effect.reduceHarmType = EHarmType.get(data);

                            if (null != effect.reduceHarmType)
                            {
                                data = subE.elementText("reduceHarmValue");

                                if (data.equals("N/A"))
                                {
                                    effect.isReduceAllHarm = true;
                                }
                                else
                                {
                                    effect.reduceHarm = Integer.parseInt(data);
                                }
                            }
                            else
                            {
                                LogWriter.println("吸收伤害类型错误：" + effect.ID);
                            }
                        }

                        data = subE.elementText("specialEffectType");

                        if (null != data)
                        {
                            effect.specialStatus = ESpecialStatus.get(data);

                            if (null != effect.specialStatus)
                            {
                                effect
                                        .setSpecialStatusLevel(Byte
                                                .parseByte(subE
                                                        .elementText("specialEffectLevel")));
                            }
                            else
                            {
                                LogWriter.println("特殊状态类型错误：" + effect.ID);
                            }
                        }

                        data = subE.elementText("resistType");

                        if (null != data)
                        {
                            effect.resistSpecialStatus = ESpecialStatus
                                    .get(data);

                            if (null != effect.resistSpecialStatus)
                            {
                                effect.resistSpecialStatusOdds = Integer
                                        .parseInt(subE
                                                .elementText("resistOdds")) / 100F;
                            }
                            else
                            {
                                LogWriter.println("抵抗属性类型错误：" + effect.ID);
                            }
                        }

                        data = subE.elementText("additionalEffectNumber");

                        if (null != data)
                        {
                            int additionalEffectNumber = Byte.parseByte(data);
                            effect.additionalOddsActionUnitList = new AdditionalActionUnit[additionalEffectNumber];

                            for (int i = 0; i < additionalEffectNumber; i++)
                            {
                                effect.additionalOddsActionUnitList[i] = new AdditionalActionUnit();
                                effect.additionalOddsActionUnitList[i].skillOrEffectUnitID = Integer
                                        .parseInt(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1) + "ID"));
                                effect.additionalOddsActionUnitList[i].activeTimes = Byte
                                        .parseByte(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1)
                                                        + "ActionTimes"));
                                effect.additionalOddsActionUnitList[i].activeOdds = Integer
                                        .parseInt(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1) + "Odds")) / 100F;
                            }
                        }

                        effect.traceReduceHarmValue = effect.reduceHarm;
                        effect.iconID = Short.parseShort(subE
                                .elementText("iconID"));

                        data = subE.elementText("description");

                        if (null != data)
                        {
                            effect.desc = data;
                        }

                        effectTable.put(effect.ID, effect);
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
     * 加载动态效果
     * 
     * @param _dataPath
     */
    private void loadDynamicEffect (String _dataPath)
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
                DynamicEffect effect;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        effect = new DynamicEffect(Integer.parseInt(subE
                                .elementText("id")), subE.elementText("name")
                                .trim());
                        log.debug("effect dictionary id="+effect.ID +", name = " + effect.name);
                        effect.level = Short.parseShort(subE
                                .elementTextTrim("level"));
                        effect.feature = EffectFeature.get(subE
                                .elementTextTrim("featureType"));
                        effect.featureLevel = Byte.parseByte(subE
                                .elementTextTrim("featureLevel"));

                        data = subE.elementText("isbuff");

                        if (null != data && data.equals("是"))
                        {
                            effect.trait = EffectTrait.BUFF;
                        }
                        else
                        {
                            effect.trait = EffectTrait.DEBUFFF;
                        }

                        data = subE.elementText("disAfterDie");

                        if (null != data && data.equals("是"))
                        {
                            effect.isClearAfterDie = true;
                        }

                        effect.replaceID = Integer.parseInt(subE
                                .elementText("replaceID"));

                        data = subE.elementText("keepTime");

                        if (data.equalsIgnoreCase("N/A"))
                        {
                            effect.keepTimeType = EKeepTimeType.N_A;

                            ETargetType targetType = ETargetType.get(subE
                                    .elementText("targetType"));

                            if (null != targetType)
                            {
                                AureoleRadiationRange actionTarget = new AureoleRadiationRange(
                                        targetType);
                                actionTarget.targetRangeType = ETargetRangeType
                                        .get(subE.elementText("rangeType"));

                                if (targetType == ETargetType.FRIEND
                                        || (targetType == ETargetType.ENEMY && actionTarget.targetRangeType != ETargetRangeType.SINGLE))
                                {
                                    actionTarget.rangeRadiu = Byte
                                            .parseByte(subE
                                                    .elementText("rangeRadiu"));
                                }
                                
                                effect.aureoleRadiationRange = actionTarget;
                            }
                            else
                            {
                                LogWriter.println("光环目标类型错误：" + effect.ID);
                            }
                        }
                        else
                        {
                            effect.keepTimeType = EKeepTimeType.LIMITED;
                            effect.setKeepTime(Short.parseShort(data));

                            data = subE.elementText("isAdditional");

                            if (null != data && data.equals("是"))
                            {
                                effect.totalTimes = Short.parseShort(subE
                                        .elementText("additionalTotal"));
                            }
                        }

                        effect.actionTimeType = ActionTimeType.get(subE
                                .elementText("actionTimeType"));

                        data = subE.elementText("hpReduce");

                        if (null != data)
                        {
                            effect.hpHarmTotal = Integer.parseInt(data);
                            effect.harmType = EHarmType.get(subE
                                    .elementText("harmType"));

                            if (effect.harmType != EHarmType.PHYSICS)
                            {
                                effect.harmMagicType = EMagic
                                        .getMagic(effect.harmType.getDesc());
                            }
                        }

                        data = subE.elementText("mpReduce");

                        if (null != data)
                        {
                            effect.mpHarmTotal = Integer.parseInt(data);
                        }

                        data = subE.elementText("hpResume");

                        if (null != data)
                        {
                            effect.hpResumeTotal = Integer.parseInt(data);
                        }

                        data = subE.elementText("mpResume");

                        if (null != data)
                        {
                            effect.mpResumeTotal = Integer.parseInt(data);
                        }

                        data = subE.elementText("additionalEffectNumber");

                        if (null != data)
                        {
                            int additionalEffectNumber = Byte.parseByte(data);
                            effect.additionalOddsActionUnitList = new AdditionalActionUnit[additionalEffectNumber];

                            for (int i = 0; i < additionalEffectNumber; i++)
                            {
                                effect.additionalOddsActionUnitList[i].skillOrEffectUnitID = Integer
                                        .parseInt(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1) + "ID"));
                                effect.additionalOddsActionUnitList[i].activeTimes = Byte
                                        .parseByte(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1)
                                                        + "ActionTimes"));
                                effect.additionalOddsActionUnitList[i].activeOdds = Integer
                                        .parseInt(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1) + "Odds")) / 100F;
                            }
                        }
                        try {
                        	short s = Short.parseShort(subE.elementText("iconID"));
						} catch (Exception e) {
							log.info("effect id = " +effect.ID);
							log.info("effect name = " +effect.name);
						}
                        effect.iconID = Short.parseShort(subE
                                .elementText("iconID"));

                        data = subE.elementText("description");

                        if (null != data)
                        {
                            effect.desc = data;
                        }

                        effectTable.put(effect.ID, effect);
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
     * 加载触发效果
     * 
     * @param _dataPath
     */
    private void loadTouchEffect (String _dataPath)
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
                TouchEffect effect;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        ETouchType touchType = ETouchType.get(subE
                                .elementText("touthType"));

                        if (null == touchType)
                        {
                            LogWriter.println("触发类型错误："
                                    + subE.elementText("id"));

                            continue;
                        }

                        effect = new TouchEffect(Integer.parseInt(subE
                                .elementText("id")), subE.elementText("name")
                                .trim(), touchType, Integer.parseInt(subE
                                .elementText("touthOdds")) / 100F);

                        effect.level = Short.parseShort(subE
                                .elementTextTrim("level"));
                        effect.feature = EffectFeature.get(subE
                                .elementTextTrim("featureType"));
                        effect.featureLevel = Byte.parseByte(subE
                                .elementTextTrim("featureLevel"));

                        data = subE.elementText("isbuff");

                        if (null != data && data.equals("是"))
                        {
                            effect.trait = EffectTrait.BUFF;
                        }
                        else
                        {
                            effect.trait = EffectTrait.DEBUFFF;
                        }

                        data = subE.elementText("disAfterDie");

                        if (null != data && data.equals("是"))
                        {
                            effect.isClearAfterDie = true;
                        }

                        effect.targetType = ETargetType.get(subE
                                .elementText("targetType"));

                        effect.replaceID = Integer.parseInt(subE
                                .elementText("replaceID"));

                        data = subE.elementText("keepTime");

                        if (data.equalsIgnoreCase("N/A"))
                        {
                            effect.keepTimeType = EKeepTimeType.N_A;
                        }
                        else
                        {
                            effect.keepTimeType = EKeepTimeType.LIMITED;
                            effect.setKeepTime(Short.parseShort(data));
                        }

                        data = subE.elementText("hpReduce");

                        if (null != data)
                        {
                            effect.hpHarmValue = Integer.parseInt(data);
                            effect.harmType = EHarmType.get(subE
                                    .elementText("harmType"));

                            if (effect.harmType != EHarmType.PHYSICS)
                            {
                                effect.harmMagicType = EMagic
                                        .getMagic(effect.harmType.getDesc());
                            }
                        }

                        data = subE.elementText("mpReduce");

                        if (null != data)
                        {
                            effect.mpHarmValue = Integer.parseInt(data);
                        }

                        data = subE.elementText("hpResume");

                        if (null != data)
                        {
                            effect.hpResumeValue = Integer.parseInt(data);
                        }

                        data = subE.elementText("mpResume");

                        if (null != data)
                        {
                            effect.mpResumeValue = Integer.parseInt(data);
                        }

                        data = subE.elementText("additionalEffectNumber");

                        if (null != data)
                        {
                            int additionalEffectNumber = Byte.parseByte(data);
                            effect.additionalOddsActionUnitList = new AdditionalActionUnit[additionalEffectNumber];

                            for (int i = 0; i < additionalEffectNumber; i++)
                            {
                                effect.additionalOddsActionUnitList[i] = new AdditionalActionUnit();
                                effect.additionalOddsActionUnitList[i].skillOrEffectUnitID = Integer
                                        .parseInt(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1) + "ID"));
                                effect.additionalOddsActionUnitList[i].activeTimes = Byte
                                        .parseByte(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1)
                                                        + "ActionTimes"));
                                effect.additionalOddsActionUnitList[i].activeOdds = Integer
                                        .parseInt(subE
                                                .elementText("additionalEffect"
                                                        + (i + 1) + "Odds")) / 100F;
                            }
                        }
                        
                        //add by zhengl ; date: 2011-01-21 ; note: 读取持续特效作用展示效果
                        data = subE.elementText("effectImageID");
                        if (null != data)
                        {
                            effect.effectImageID = Short.parseShort(data);
                        }
                        else {
                        	effect.effectImageID = -1;
						}
                        data = subE.elementText("effectAnimationID");
                        if (null != data)
                        {
                            effect.effectAnimationID = Short.parseShort(data);
                        }
                        else {
                        	effect.effectAnimationID = -1;
						}
                        data = subE.elementText("tierRelation");
                        if (null != data)
                        {
                            effect.tierRelation = Short.parseShort(data);
                        }
                        //end

                        effect.iconID = Short.parseShort(subE
                                .elementText("iconID"));

                        data = subE.elementText("description");

                        if (null != data)
                        {
                            effect.desc = data;
                        }

                        effectTable.put(effect.ID, effect);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.println(e);
        }
    }

    public static void main (String[] args)
    {
        getInstance().loadStaticEffect(
                System.getProperty("user.dir") + File.separator
                        + "res/data/effect/static");
        getInstance().loadDynamicEffect(
                System.getProperty("user.dir") + File.separator
                        + "res/data/effect/dynamic");
        getInstance().loadTouchEffect(
                System.getProperty("user.dir") + File.separator
                        + "res/data/effect/touch");
    }
}
