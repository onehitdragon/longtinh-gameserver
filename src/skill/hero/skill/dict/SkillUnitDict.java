package hero.skill.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.effect.Effect.EffectFeature;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.skill.service.SkillConfig;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.unit.ChangePropertyUnit;
import hero.skill.unit.EnhanceSkillUnit;
import hero.skill.unit.SkillUnit;
import hero.skill.unit.TouchUnit;
import hero.skill.unit.EnhanceSkillUnit.SkillDataField;
import hero.skill.unit.EnhanceSkillUnit.EnhanceDataType;
import hero.skill.unit.EnhanceSkillUnit.EnhanceUnit;
import hero.share.EMagic;
import hero.share.service.LogWriter;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillUnitDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-14 下午01:05:54
 * @描述 ：技能单元
 */

public class SkillUnitDict
{
     private static Logger log = Logger.getLogger(SkillUnitDict.class);
    /**
     * 主动技能单元容器
     */
    private FastMap<Integer, SkillUnit> skillUnitTable;

    /**
     * 单例
     */
    private static SkillUnitDict        instance;

    /**
     * 私有构造
     */
    private SkillUnitDict()
    {
        skillUnitTable = new FastMap<Integer, SkillUnit>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static SkillUnitDict getInstance ()
    {
        if (null == instance)
        {
            instance = new SkillUnitDict();
        }

        return instance;
    }

    /**
     * 获取技能单元
     * 
     * @param _skillUnitID
     * @return
     */
    public SkillUnit getSkillUnitRef (int _skillUnitID)
    {
        if (_skillUnitID > 0)
        {
            return skillUnitTable.get(_skillUnitID);
        }

        return null;
    }

    /**
     * 获取技能单元新实例
     * 
     * @param _skillUnitID
     * @return
     */
    public SkillUnit getSkillUnitInstance (int _skillUnitID)
    {
    	SkillUnit skillUnit = null;
        if (_skillUnitID > 0)
        {
            try
            {
            	skillUnit = skillUnitTable.get(_skillUnitID);
            	if (skillUnit != null)
            	{
            		skillUnit = skillUnit.clone();
				}
            }
            catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
            }
        }

        return skillUnit;
    }

    /**
     * 加载
     * 
     * @param _config
     */
    public void load (SkillConfig _config)
    {
        loadActiveSkillUnit(_config.active_skill_data_path);
        loadTouchPassiveSkillUnit(_config.touch_skill_data_path);
        loadEnhanceSkillUnit(_config.enhance_skill_data_path);
        loadPropertySkillUnit(_config.change_property_skill_data_path);
    }

    public static void main (String[] args)
    {
        getInstance();
        // instance.loadActiveSkillUnit(System.getProperty("user.dir")
        // + "/res/data/skill/unit/active");
        // instance.loadTouchPassiveSkillUnit(System.getProperty("user.dir")
        // + "/res/data/skill/unit/touch");
        // instance.loadEnhanceSkillUnit(System.getProperty("user.dir")
        // + "/res/data/skill/unit/enhance");
        instance.loadPropertySkillUnit(System.getProperty("user.dir")
                + "/res/data/skill/unit/property");
    }

    /**
     * 加载主动技能单元
     * 
     * @param _dataPath
     */
    public void loadActiveSkillUnit (String _dataPath)
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
                ActiveSkillUnit skillUnit;
                ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = new ArrayList<AdditionalActionUnit>();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    additionalOddsActionUnitList.clear();

                    if (null != subE)
                    {
                        skillUnit = new ActiveSkillUnit(Integer.parseInt(subE
                                .elementTextTrim("id")));

                        data = subE.elementTextTrim("name");
//                        log.info("skill name:"+data);;

                        if (null == data)
                        {
                            log.info("没有名字：" + skillUnit.id);

                            break;
                        }

                        skillUnit.name = data;
                        skillUnit.activeSkillType = EActiveSkillType
                                .getType(subE.elementTextTrim("type"));

                        if (null == skillUnit.activeSkillType)
                        {
                            log.info("技能类型没有：" + skillUnit.id);

                            break;
                        }

                        data = subE.elementTextTrim("consumeType");

                        if (null != data)
                        {
                            if (data.equals("力槽"))
                            {
                                skillUnit.consumeFp = Short.parseShort(subE
                                        .elementTextTrim("consumeValue"));
                            }
                            else if (data.equals("气槽"))
                            {
                                skillUnit.consumeGp = Short.parseShort(subE
                                        .elementTextTrim("consumeValue"));
                            }
                            else if (data.equals("魔法槽"))
                            {
                                skillUnit.consumeMp = Integer.parseInt(subE
                                        .elementTextTrim("consumeValue"));
                            }
                        }

                        data = subE.elementTextTrim("consumeHp");

                        if (null != data)
                        {
                            skillUnit.consumeHp = Short.parseShort(data);
                        }

                        data = subE.elementTextTrim("targetType");

                        skillUnit.targetType = ETargetType.get(data);

                        if (null == skillUnit.targetType)
                        {
                            log.info("没有目标类型：" + skillUnit.id);
                            break;
                        }

                        data = subE.elementTextTrim("range");

                        skillUnit.targetRangeType = ETargetRangeType.get(data);

                        if (skillUnit.targetRangeType == ETargetRangeType.TEAM
                                || skillUnit.targetRangeType == ETargetRangeType.SOME)
                        {
                            skillUnit.rangeTargetNumber = Byte.parseByte(subE
                                    .elementTextTrim("rangeTargetNumber"));
                            skillUnit.rangeBaseLine = EAOERangeBaseLine
                                    .get(subE.elementTextTrim("rangeBase"));
                            skillUnit.rangeMode = EAOERangeType.get(subE
                                    .elementTextTrim("rangeMode"));
                            skillUnit.rangeX = Byte.parseByte(subE
                                    .elementTextTrim("rangeX"));

                            if (skillUnit.rangeMode == EAOERangeType.FRONT_RECT)
                            {
                                skillUnit.rangeY = Byte.parseByte(subE
                                        .elementTextTrim("rangeY"));
                            }
                            //add by zhengl ; date: 2011-01-20 ; note: 添加群体动画的作用形式
                            data = subE.elementTextTrim("animationModel");
                            if (null != data)
                            {
                                skillUnit.animationModel = Byte.parseByte(data);
                            }

                            data = subE.elementTextTrim("average");

                            if (null != data)
                            {
                                skillUnit.isAverageResult = data.equals("是") ? true
                                        : false;
                            }
                        }

                        data = subE.elementTextTrim("releaseTime");

                        if (null != data)
                        {
                            skillUnit.releaseTime = Float.parseFloat(data);
                        }

                        if (skillUnit.targetType != ETargetType.MYSELF)
                        {
                            if (skillUnit.targetRangeType == ETargetRangeType.SINGLE
                                    || skillUnit.rangeBaseLine == EAOERangeBaseLine.TARGET)
                            {
                            	data = subE.elementTextTrim("distance");
                            	if (data != null) 
                            	{
                            		skillUnit.targetDistance = Byte.parseByte(data);
								}
                            	else 
                            	{
                            		skillUnit.targetDistance = 1;
									log.warn("技能单元:目标距离（格子数）欠缺,请填写:" + skillUnit.id);
								}
                            }
                        }

                        data = subE.elementTextTrim("physicsHarmType");

                        if (null != data)
                        {
                            skillUnit.physicsHarmType = EHarmType.get(data);

                            data = subE.elementTextTrim("weaponHarmMult");

                            if (null != data)
                            {
                                skillUnit.weaponHarmMult = Float
                                        .parseFloat(data);
                            }

                            data = subE.elementTextTrim("physicsHarmValue");

                            if (null != data)
                            {
                                skillUnit.physicsHarmValue = Integer
                                        .parseInt(data);
                            }
//                            log.info("技能"+skillUnit.name);
//                            log.info("附加伤害"+skillUnit.physicsHarmValue );
                        }

                        data = subE.elementTextTrim("needHit");

                        if (null != data && data.equals("是"))
                        {
                            skillUnit.needMagicHit = true;
                            skillUnit.magicHarmType = EMagic.getMagic(subE
                                    .elementTextTrim("magicHarmType"));
                        }

                        data = subE.elementTextTrim("magicHarmValue");

                        if (null != data)
                        {
                            skillUnit.magicHarmHpValue = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("magicMpReduce");

                        if (null != data)
                        {
                            skillUnit.magicHarmMpValue = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("hpResume");

                        if (null != data)
                        {
                            skillUnit.resumeHp = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("mpResume");

                        if (null != data)
                        {
                            skillUnit.resumeMp = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("hateValue");

                        if (null != data)
                        {
                            skillUnit.hateValue = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("effectFeature");

                        if (null != data)
                        {
                            skillUnit.cleanEffectFeature = EffectFeature
                                    .get(data);
                            skillUnit.cleanEffectMaxLevel = Byte.parseByte(subE
                                    .elementTextTrim("featureMaxLevel"));

                            if (subE.elementTextTrim("cleanNumberPerTimes") == null)

                                log.info("skillUnit.id:"
                                        + skillUnit.id);

                            skillUnit.cleanEffectNumberPerTimes = Byte
                                    .parseByte(subE
                                            .elementTextTrim("cleanNumberPerTimes"));
                        }

                        data = subE.elementTextTrim("effectIDs");

                        if (null != data)
                        {
                            String[] detailCleanEffectIDInfo = data.split("-");

                            skillUnit.cleanDetailEffectLowerID = Integer
                                    .parseInt(detailCleanEffectIDInfo[0]);
                            skillUnit.cleandetailEffectUperID = Integer
                                    .parseInt(detailCleanEffectIDInfo[1]);
                        }

                        data = subE.elementTextTrim("odds");

                        if (null != data)
                        {
                            skillUnit.cleanEffectOdds = Float.parseFloat(data) / 100;
                        }

                        data = subE.elementTextTrim("additionalSEID");

                        if (null != data)
                        {
                            skillUnit.additionalSEID = Integer.parseInt(data);
                        }

                        AdditionalActionUnit additionalActionUnit;

                        for (int i = 1; i <= 3; i++)
                        {
                            data = subE.elementTextTrim("additionalSE" + i
                                    + "ID");

                            if (null == data)
                            {
                                break;
                            }

                            additionalActionUnit = new AdditionalActionUnit();
                            additionalActionUnit.skillOrEffectUnitID = Integer
                                    .parseInt(data);
                            additionalActionUnit.activeOdds = Integer
                                    .parseInt(subE
                                            .elementTextTrim("additionalSE" + i
                                                    + "Odds")) / 100F;
                            additionalActionUnit.activeTimes = Byte
                                    .parseByte(subE
                                            .elementTextTrim("additionalSE" + i
                                                    + "ActionTimes"));

                            additionalOddsActionUnitList
                                    .add(additionalActionUnit);
                        }

                        if (additionalOddsActionUnitList.size() > 0)
                        {
                            skillUnit.additionalOddsActionUnitList = new AdditionalActionUnit[additionalOddsActionUnitList
                                    .size()];

                            for (int i = 0; i < additionalOddsActionUnitList
                                    .size(); i++)
                            {
                                skillUnit.additionalOddsActionUnitList[i] = additionalOddsActionUnitList
                                        .get(i);
                            }
                        }
                        //add by zhengl ; date: 2011-01-13 ; note: 添加施法动作动作序列编号 (施展这个法术的时候,人物的动作)
                        data = subE.elementTextTrim("animationAction");
                        if (null != data)
                        {
                            skillUnit.animationAction = Byte.valueOf(data);
                        }

                        data = subE.elementTextTrim("releaseAnimationID");
                        if (null != data)
                        {
                            skillUnit.releaseAnimationID = Short.parseShort(data);
                        }
                        else {
                        	skillUnit.releaseAnimationID = -1;
						}
                        
                        //add by zhengl ; date: 2011-01-19 ; note: 施法动作的相关图片id也需要下发
                        data = subE.elementTextTrim("releaseImageID");
                        if (null != data){
                            skillUnit.releaseImageID = Short.parseShort(data);
                        } else {
                        	skillUnit.releaseImageID = -1;
						}
                        data = subE.elementTextTrim("activeImageID");
                        if (null != data){
                            skillUnit.activeImageID = Short.parseShort(data);
                        }
                        //end
                        
                        //add by zhengl ; date: 2011-01-13 ; note: 添加施放动画特效层级2=下层;1=上层,不填为1
                        data = subE.elementTextTrim("tierRelation");
                        if (null != data)
                        {
                            skillUnit.tierRelation = Byte.valueOf(data);
                        }
                        else
                        {
                        	skillUnit.tierRelation = 1;
                        }
                        
                        //add:	zhengl
                        //date:	2011-02-14
                        //note:	添加施放动画特效高度关系
                        //		1=脚下播放 (力量光环,闪电召唤等)
                        //		2=中心位置播放 (火球术等)
                        //		3=头顶播放 (小天使光圈等)
                        data = subE.elementTextTrim("heightRelation");
                        if (null != data)
                        {
                            skillUnit.heightRelation = Byte.valueOf(data);
                        }
                        data = subE.elementTextTrim("releaseheightRelation");
                        if (null != data)
                        {
                            skillUnit.releaseHeightRelation = Byte.valueOf(data);
                        }
                        
                        //add by zhengl; date: 2011-02-24; note: 施法动作是否区分方向
                        data = subE.elementTextTrim("isDirection");
                        if (data != null) {
                        	skillUnit.isDirection = Byte.parseByte(data);
						}

                        data = subE.elementTextTrim("activeAnimationID");

                        if (null != data)
                        {
                            skillUnit.activeAnimationID = Short
                                    .parseShort(data);
                        }

                        if (skillUnit.activeSkillType == EActiveSkillType.MAGIC)
                        {
                            if (skillUnit.targetType == ETargetType.ENEMY)
                            {
                                skillUnit.activeTouchType = ETouchType.ATTACK_BY_MAGIC;
                                skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_MAGIC;
                            }
                            else
                            {
                                skillUnit.activeTouchType = ETouchType.RESUME_BY_MAGIC;
                            }
                        }
                        else
                        {
                            if (skillUnit.targetType == ETargetType.ENEMY)
                            {
                                if (skillUnit.physicsHarmValue > 0)
                                {
                                    if (skillUnit.physicsHarmType == EHarmType.DISTANCE_PHYSICS)
                                    {
                                        skillUnit.activeTouchType = ETouchType.ATTACK_BY_DISTANCE_PHYSICS;
                                        skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_DISTANCE_PHYSICS;
                                    }
                                    else
                                    {
                                        skillUnit.activeTouchType = ETouchType.ATTACK_BY_NEAR_PHYSICS;
                                        skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_NEAR_PHYSICS;
                                    }
                                }
                                else
                                {
                                    if (skillUnit.targetDistance <= 3)
                                    {
                                        skillUnit.activeTouchType = ETouchType.ATTACK_BY_NEAR_PHYSICS;
                                        skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_NEAR_PHYSICS;
                                    }
                                    else
                                    {
                                        skillUnit.activeTouchType = ETouchType.ATTACK_BY_DISTANCE_PHYSICS;
                                        skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_DISTANCE_PHYSICS;
                                    }
                                }
                            }
                            else
                            {
                                skillUnit.activeTouchType = ETouchType.ACTIVE;
                            }

                        }

                        skillUnitTable.put(skillUnit.id, skillUnit);
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
     * 加载触发被动技能
     * 
     * @param _dataPath
     */
    public void loadTouchPassiveSkillUnit (String _dataPath)
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
                TouchUnit skillUnit;
                ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = new ArrayList<AdditionalActionUnit>();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    additionalOddsActionUnitList.clear();

                    if (null != subE)
                    {
                        skillUnit = new TouchUnit(Integer.parseInt(subE
                                .elementTextTrim("id")));

                        data = subE.elementTextTrim("name");

                        if (null == data)
                        {
                            log.info("没有名字：" + skillUnit.id);

                            break;
                        }

                        skillUnit.name = data;
                        skillUnit.touchType = ETouchType.get(subE
                                .elementTextTrim("touthType"));

                        if (null == skillUnit.touchType)
                        {
                            log.info("没有触发类型：" + skillUnit.id);
                            break;
                        }

                        data = subE.elementTextTrim("targetType");

                        skillUnit.targetType = ETargetType.get(data);

                        if (null == skillUnit.targetType)
                        {
                            log.info("没有目标类型：" + skillUnit.id);
                            break;
                        }

                        data = subE.elementTextTrim("range");

                        skillUnit.targetRangeType = ETargetRangeType.get(data);

                        if (skillUnit.targetRangeType == ETargetRangeType.TEAM
                                || skillUnit.targetRangeType == ETargetRangeType.SOME)
                        {
                            skillUnit.rangeTargetNumber = Byte.parseByte(subE
                                    .elementTextTrim("rangeTargetNumber"));
                            skillUnit.rangeLine = EAOERangeBaseLine.get(subE
                                    .elementTextTrim("rangeBase"));
                            skillUnit.rangeMode = EAOERangeType.get(subE
                                    .elementTextTrim("rangeMode"));
                            skillUnit.rangeX = Byte.parseByte(subE
                                    .elementTextTrim("rangeX"));

                            if (skillUnit.rangeMode == EAOERangeType.FRONT_RECT)
                            {
                                skillUnit.rangeY = Byte.parseByte(subE
                                        .elementTextTrim("rangeY"));
                            }

                            data = subE.elementTextTrim("average");
                            if(data != null)
                            {
                            	skillUnit.isAverageResult = data.equals("是") ? true
                                    : false;
                            }
                            else 
                            {
                            	skillUnit.isAverageResult = false;
							}
                        }

                        data = subE.elementTextTrim("physicsHarmType");

                        if (null != data)
                        {
                            skillUnit.physicsHarmType = EHarmType.get(data);

                            data = subE.elementTextTrim("weaponHarmMult");

                            if (null != data)
                            {
                                skillUnit.weaponHarmMult = Float
                                        .parseFloat(data);
                            }

                            data = subE.elementTextTrim("physicsHarmValue");

                            if (null != data)
                            {
                                skillUnit.physicsHarmValue = Integer
                                        .parseInt(data);
                            }
                        }

                        data = subE.elementTextTrim("magicHarmType");

                        if (null != data)
                        {
                            skillUnit.magicHarmType = EMagic.getMagic(data);
                            skillUnit.magicHarmHpValue = Integer.parseInt(subE
                                    .elementTextTrim("magicHarmValue"));
                        }

                        data = subE.elementTextTrim("magicMpReduce");

                        if (null != data)
                        {
                            skillUnit.magicHarmMpValue = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("hpResume");

                        if (null != data)
                        {
                            skillUnit.resumeHp = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("mpResume");

                        if (null != data)
                        {
                            skillUnit.resumeMp = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("hateValue");

                        if (null != data)
                        {
                            skillUnit.hateValue = Integer.parseInt(data);
                        }

                        data = subE.elementTextTrim("additionalSEID");

                        if (null != data)
                        {
                            skillUnit.additionalSEID = Integer.parseInt(data);
                        }

                        AdditionalActionUnit additionalActionUnit;

                        for (int i = 1; i <= 3; i++)
                        {
                            data = subE.elementTextTrim("additionalSE" + i
                                    + "ID");

                            if (null == data)
                            {
                                break;
                            }

                            additionalActionUnit = new AdditionalActionUnit();
                            additionalActionUnit.skillOrEffectUnitID = Integer
                                    .parseInt(data);
                            additionalActionUnit.activeOdds = Float
                                    .parseFloat(subE
                                            .elementTextTrim("additionalSE" + i
                                                    + "Odds")) / 100;
                            additionalActionUnit.activeTimes = Byte
                                    .parseByte(subE
                                            .elementTextTrim("additionalSE" + i
                                                    + "ActionTimes"));

                            //edit by zhengl; date: 2011-03-06; note: 不应该添加默认对象.
//                            additionalOddsActionUnitList.add(new AdditionalActionUnit());
                            additionalOddsActionUnitList.add(additionalActionUnit);
                            //end
                        }

                        if (additionalOddsActionUnitList.size() > 0)
                        {
                            skillUnit.additionalOddsActionUnitList = 
                            	new AdditionalActionUnit[additionalOddsActionUnitList.size()];

                            for (int i = 0; i < additionalOddsActionUnitList
                                    .size(); i++)
                            {
                                skillUnit.additionalOddsActionUnitList[i] = additionalOddsActionUnitList
                                        .get(i);
                            }
                        }

                        data = subE.elementTextTrim("activeAnimationID");

                        if (null != data)
                        {
                            skillUnit.activeAnimationID = Short
                                    .parseShort(data);
                        }
                        
                        //add by zhengl; date: 2011-02-12; note: 添加图片
                        data = subE.elementTextTrim("activeImageID");
                        if (null != data)
                        {
                            skillUnit.activeImageID = Short
                                    .parseShort(data);
                        }
                        skillUnitTable.put(skillUnit.id, skillUnit);
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
     * 加载强化技能单元
     * 
     * @param _dataPath
     */
    public void loadEnhanceSkillUnit (String _dataPath)
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
                EnhanceSkillUnit skillUnit;
                ArrayList<EnhanceUnit> enhanceUnitList = new ArrayList<EnhanceUnit>();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    enhanceUnitList.clear();

                    if (null != subE)
                    {
                        skillUnit = new EnhanceSkillUnit(Integer.parseInt(subE
                                .elementTextTrim("id")));

                        data = subE.elementTextTrim("name");

                        if (null == data)
                        {
                            break;
                        }

                        skillUnit.name = data;

                        EnhanceUnit enhanceUnit;

                        for (int i = 1; i <= 5; i++)
                        {
                            enhanceUnit = new EnhanceUnit();

                            enhanceUnit.skillDataType = EnhanceDataType
                                    .get(subE.elementTextTrim("target" + i
                                            + "Type"));

                            if (null == enhanceUnit.skillDataType)
                            {
                                break;
                            }

                            enhanceUnit.skillName = subE
                                    .elementTextTrim("target" + i + "Name");

                            if (null == enhanceUnit.skillName)
                            {
                                LogWriter.println("强化单元数据错误-目标技能名称："
                                        + skillUnit.name);

                                break;
                            }

                            enhanceUnit.dataField = SkillDataField.get(subE
                                    .elementTextTrim("target" + i
                                            + "ColumnName"));

                            if (null == enhanceUnit.dataField)
                            {
                                LogWriter.println("强化单元数据错误-目标技能列名："
                                        + skillUnit.name);

                                break;
                            }

                            enhanceUnit.caluOperator = EMathCaluOperator
                                    .get(subE.elementTextTrim("target" + i
                                            + "Operator"));

                            if (null == enhanceUnit.caluOperator)
                            {
                                LogWriter.println("强化单元数据错误-运算符："
                                        + skillUnit.name);

                                break;
                            }

                            data = subE.elementTextTrim("target" + i + "Value");

                            if (null == data)
                            {
                                LogWriter.println("强化单元数据错误-数值："
                                        + skillUnit.name);
                                break;
                            }
                            else
                            {
                            	Pattern pattern = Pattern.compile("[0-9]*");
                            	if(pattern.matcher(data).matches()) {
                                    enhanceUnit.changeMulti = Float.parseFloat(data);
                            	} else {
                            		enhanceUnit.changeString = data
                            			.replace('、', ',').replace('，', ',').split(",");
								}
                            }

                            enhanceUnitList.add(enhanceUnit);
                        }

                        if (enhanceUnitList.size() > 0)
                        {
                            skillUnit.enhanceUnitList = new EnhanceUnit[enhanceUnitList
                                    .size()];

                            for (int i = 0; i < enhanceUnitList.size(); i++)
                            {
                                skillUnit.enhanceUnitList[i] = enhanceUnitList
                                        .get(i);
                            }

                            enhanceUnitList.clear();

                            skillUnitTable.put(skillUnit.id, skillUnit);
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
     * 加载被动强化属性技能单元
     * 
     * @param _dataPath
     */
    public void loadPropertySkillUnit (String _dataPath)
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
                ChangePropertyUnit skillUnit;
                ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = new ArrayList<AdditionalActionUnit>();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    additionalOddsActionUnitList.clear();

                    if (null != subE)
                    {
                        skillUnit = new ChangePropertyUnit(Integer
                                .parseInt(subE.elementTextTrim("id")));

                        data = subE.elementTextTrim("name");

                        if (null == data)
                        {
                            break;
                        }

                        skillUnit.name = data;
                        skillUnit.caluOperator = EMathCaluOperator.get(subE
                                .elementTextTrim("operator"));

                        data = subE.elementText("defence");

                        if (null != data)
                        {
                            skillUnit.defense = Float.parseFloat(data);
                        }

                        data = subE.elementText("maxHp");

                        if (null != data)
                        {
                            skillUnit.maxHp = Float.parseFloat(data);
                        }

                        data = subE.elementText("maxMp");

                        if (null != data)
                        {
                            skillUnit.maxMp = Float.parseFloat(data);
                        }

                        data = subE.elementText("strength");

                        if (null != data)
                        {
                            skillUnit.strength = Float.parseFloat(data);
                        }

                        data = subE.elementText("agility");

                        if (null != data)
                        {
                            skillUnit.agility = Float.parseFloat(data);
                        }

                        data = subE.elementText("stamina");

                        if (null != data)
                        {
                            skillUnit.stamina = Float.parseFloat(data);
                        }

                        data = subE.elementText("inte");

                        if (null != data)
                        {
                            skillUnit.inte = Float.parseFloat(data);
                        }

                        data = subE.elementText("spirit");

                        if (null != data)
                        {
                            skillUnit.spirit = Float.parseFloat(data);
                        }

                        data = subE.elementText("luck");

                        if (null != data)
                        {
                            skillUnit.lucky = Float.parseFloat(data);
                        }

                        data = subE.elementText("phsicsDeathblowLevel");

                        if (null != data)
                        {
                            skillUnit.physicsDeathblowLevel = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("magicDeathblowLevel");

                        if (null != data)
                        {
                            skillUnit.magicDeathblowLevel = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("hitLevel");

                        if (null != data)
                        {
                            skillUnit.hitLevel = Float.parseFloat(data);
                        }

                        data = subE.elementText("phsicsDuckLevel");

                        if (null != data)
                        {
                            skillUnit.physicsDuckLevel = Float.parseFloat(data);
                        }

                        data = subE.elementText("magicFastnessType");

                        EMagic magicType;

                        if (null != data)
                        {
                            if (data.equalsIgnoreCase("ALL"))
                            {
                                skillUnit.magicFastnessValue = Integer
                                        .parseInt(subE
                                                .elementText("magicFastnessValue"));
                            }
                            else
                            {
                                magicType = EMagic.getMagic(data);

                                if (null != magicType)
                                {
                                    skillUnit.magicFastnessType = magicType;
                                    skillUnit.magicFastnessValue = Float.parseFloat(subE
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
                            skillUnit.hate = Float.parseFloat(data);
                        }

                        data = subE.elementText("physicsAttackHarmValue");

                        if (null != data)
                        {
                            skillUnit.physicsAttackHarmValue = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("bePhysicsHarmValue");

                        if (null != data)
                        {
                            skillUnit.bePhysicsHarmValue = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("magicAttackHarmType");

                        if (null != data)
                        {
                            if (data.equalsIgnoreCase("ALL"))
                            {
                                skillUnit.magicHarmValue = Float.parseFloat(
                                		subE.elementText("magicAttackHarmValue"));
                            }
                            else
                            {
                                magicType = EMagic.getMagic(data);

                                if (null != magicType)
                                {
                                    skillUnit.magicHarmValue = Float.parseFloat(
                                    		subE.elementText("magicAttackHarmValue"));
                                    skillUnit.magicHarmType = magicType;
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
                            if (data.equalsIgnoreCase("ALL"))
                            {
                                skillUnit.magicHarmValueBeAttack = Float
                                        .parseFloat(subE
                                                .elementText("beMagicHarmValue"));
                            }
                            else
                            {
                                magicType = EMagic.getMagic(data);

                                if (null != magicType)
                                {
                                    skillUnit.magicHarmValueBeAttack = Float
                                            .parseFloat(subE
                                                    .elementText("beMagicHarmValue"));
                                    skillUnit.magicHarmTypeBeAttack = magicType;
                                }
                            }
                        }

                        data = subE.elementText("physicsAttackInterval");

                        if (null != data)
                        {
                            skillUnit.physicsAttackInterval = Float
                                    .parseFloat(data);
                        }

                        data = subE.elementText("skillReleaseTime");

                        if (null != data)
                        {
                            skillUnit.allSkillReleaseTime = Float
                                    .parseFloat(data);
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
                                    skillUnit.specialSkillReleaseTimeIDList = skillIDList;
                                    skillUnit.specialSkillReleaseTime = Float
                                            .parseFloat(subE
                                                    .elementText("specialSkillReleaseTime"));
                                }
                            }
                        }

                        data = subE.elementText("resistType");

                        if (null != data)
                        {
                            skillUnit.resistSpecialStatus = ESpecialStatus
                                    .get(data);

                            if (null != skillUnit.resistSpecialStatus)
                            {
                                skillUnit.resistSpecialStatusOdds = Integer
                                        .parseInt(subE
                                                .elementText("resistOdds")) / 100F;
                            }
                            else
                            {
                                break;
                            }
                        }

                        data = subE.elementTextTrim("additionalSETouchType");

                        if (null != data)
                        {
                            skillUnit.touchType = ETouchType.get(data);

                            if (null != skillUnit.touchType)
                            {
                                data = subE.elementTextTrim("additionalSEID");

                                if (null != data)
                                {
                                    skillUnit.additionalSEID = Integer
                                            .parseInt(data);
                                }

                                AdditionalActionUnit additionalActionUnit;

                                for (int i = 1; i <= 3; i++)
                                {
                                    data = subE.elementTextTrim("additionalSE"
                                            + i + "ID");

                                    if (null == data)
                                    {
                                        break;
                                    }

                                    additionalActionUnit = new AdditionalActionUnit();
                                    additionalActionUnit.skillOrEffectUnitID = Integer
                                            .parseInt(data);
                                    additionalActionUnit.activeOdds = Float
                                            .parseFloat(subE
                                                    .elementTextTrim("additionalSE"
                                                            + i + "Odds")) / 100;
                                    additionalActionUnit.activeTimes = Byte
                                            .parseByte(subE
                                                    .elementTextTrim("additionalSE"
                                                            + i + "ActionTimes"));

                                    additionalOddsActionUnitList
                                            .add(new AdditionalActionUnit());
                                }

                                if (additionalOddsActionUnitList.size() > 0)
                                {
                                    skillUnit.additionalOddsActionUnitList = new AdditionalActionUnit[additionalOddsActionUnitList
                                            .size()];

                                    for (int i = 0; i < additionalOddsActionUnitList
                                            .size(); i++)
                                    {
                                        skillUnit.additionalOddsActionUnitList[i] = additionalOddsActionUnitList
                                                .get(i);
                                    }
                                }
                            }
                        }

                        skillUnitTable.put(skillUnit.id, skillUnit);
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
