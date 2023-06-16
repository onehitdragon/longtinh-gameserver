package hero.skill.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.effect.dictionry.EffectDictionary;
import hero.item.Weapon.EWeaponType;
import hero.skill.ActiveSkill;
import hero.skill.Skill;
import hero.skill.PassiveSkill;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.ETargetType;
import hero.skill.service.SkillConfig;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.unit.PassiveSkillUnit;
import hero.skill.unit.SkillUnit;
import hero.share.ESystemFeature;
import hero.share.EVocation;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-14 下午01:06:03
 * @描述 ：技能字典，存放玩家技能
 */

public class SkillDict
{
     private static Logger log = Logger.getLogger(SkillDict.class);
    /**
     * 技能容器
     */
    private FastMap<Integer, Skill> skillTable;

    /**
     * 单例
     */
    private static SkillDict        instance;

    /**
     * 私有构造
     */
    private SkillDict()
    {
        skillTable = new FastMap<Integer, Skill>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static SkillDict getInstance ()
    {
        if (null == instance)
        {
            instance = new SkillDict();
        }

        return instance;
    }

    /**
     * 获取技能
     * 
     * @param _skillID
     * @return
     */
    public Skill getSkill (int _skillID)
    {
        return skillTable.get(_skillID);
    }
    
    /**
     * 获得转职职业的技能.
     * @param _educateVocation
     * @return
     */
    public ArrayList<Skill> getChangeVocationSkills (EVocation _educateVocation)
    {
        Iterator<Skill> iterator = skillTable.values().iterator();
        ArrayList<Skill> list = new ArrayList<Skill>();

        Skill skill;

        while (iterator.hasNext())
        {
            skill = iterator.next();
            if(skill.level == 0) {
	            for (int i = 0; i < skill.learnerVocation.length; i++) {
	                if (skill.learnerVocation[i] == _educateVocation)
	                {
	                    list.add(skill);
	                    break;
	                }
				}
            }
        }

        return list;
    }
    
    /**
     * 获取当前职业所有初始化技能
     * 
     * @param _player
     * @return
     */
    public ArrayList<Skill> getSkillsByVocation (EVocation _educateVocation)
    {
        Iterator<Skill> iterator = skillTable.values().iterator();
        ArrayList<Skill> list = new ArrayList<Skill>();

        Skill skill;

        while (iterator.hasNext())
        {
            skill = iterator.next();
            if(skill.level == 0) {
	            for (int i = 0; i < skill.learnerVocation.length; i++) {
	            	EVocation  vocation = skill.learnerVocation[i];
	                if ((vocation == _educateVocation || _educateVocation.baseIs(skill.learnerVocation[i]))
	                        && !skill.getFromSkillBook)
	                {
	                    list.add(skill);
	                    break;
	                }
				}
            }
        }

        return list;
    }

    /**
     * 获取技能训练师处所有技能列表
     * 
     * @param _player
     * @return
     */
    public ArrayList<Skill> getSkillList (EVocation _educateVocation,
            ESystemFeature _feature)
    {
        Iterator<Skill> iterator = skillTable.values().iterator();
        ArrayList<Skill> list = new ArrayList<Skill>();

        Skill skill;

        while (iterator.hasNext())
        {
            skill = iterator.next();
            //edit by zhengl ; date: 2010-11-09 ; note: 由于1个技能可以对应多个职业,所以采取for
            for (int i = 0; i < skill.learnerVocation.length; i++) {
                if ((skill.learnerVocation[i] == _educateVocation 
                		|| _educateVocation.baseIs(skill.learnerVocation[i]))
                        && !skill.getFromSkillBook)
                {
                    list.add(skill);
                    break; //技能已经不从技能训练师那学习,为了避免不必要的NULL异常,该处仅仅回传1个技能.
                }
			}
            //end
        }

        return list;
    }

    /**
     * 加载
     * 
     * @param _config
     */
    public void load (SkillConfig _config)
    {
        skillTable.clear();

        File dataPath;
        Skill skill;

        FastMap<String, ArrayList<Skill>> heroFeatureSkill = new FastMap<String, ArrayList<Skill>>();
        FastMap<String, ArrayList<Skill>> reverFeatureSkill = new FastMap<String, ArrayList<Skill>>();
        FastMap<String, ArrayList<Skill>> noneFeatureSkill = new FastMap<String, ArrayList<Skill>>();
        ArrayList<Skill> skillList = null;

        try
        {
            dataPath = new File(_config.player_skill_data_path);
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
                SkillUnit skillUnit;
                ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = 
                	new ArrayList<AdditionalActionUnit>();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    additionalOddsActionUnitList.clear();

                    if (null != subE)
                    {
                        data = subE.elementTextTrim("skillUnitDataID");

                        try {
                            skillUnit = SkillUnitDict.getInstance()
                            .getSkillUnitInstance(Integer.parseInt(data));
						} catch (Exception e) {
							skillUnit = null;
							log.info("***获取skillUnitDataID失败:" + data);
							continue;
						}


                        if (skillUnit instanceof ActiveSkillUnit)
                        {
                            skill = new ActiveSkill(Integer.parseInt(subE
                                    .elementTextTrim("id")), subE
                                    .elementTextTrim("name"));
                            skill.setSkillUnit((ActiveSkillUnit) skillUnit);
                        }
                        else
                        {
                            skill = new PassiveSkill(Integer.parseInt(subE
                                    .elementTextTrim("id")), subE
                                    .elementTextTrim("name"));
                            skill.setSkillUnit((PassiveSkillUnit) skillUnit);
                        }

                        skill.level = Short.parseShort(subE
                                .elementTextTrim("level"));

                        //del by zhengl ; date: 2010-11-09 ; note: 本版本不需要这个参数
//                        data = subE.elementTextTrim("feature");
//
//                        if (null != data)
//                        {
//                            skill.feature = ESystemFeature
//                                    .getFeatureByDesc(data);
//                        }
                        //end

                        data = subE.elementTextTrim("feature");

                        if (null != data)
                        {
                            skill.getFromSkillBook = data.equals("是");
                        }

                        data = subE.elementTextTrim("learnerLevel");

                        if (null != data)
                        {
                            skill.learnerLevel = Short.parseShort(data);
                        }
                        //add by zhengl; date: 2011-03-22; note: 技能阶级.
                        skill.skillRank = Byte.valueOf(subE.elementTextTrim("skillRank"));
                        
                        //edit by zhengl ; date: 2010-11-09 ; note: 单个技能可以对应多个职业
                        String[] vocations = subE.elementTextTrim("learnerVocation")
                        			.replace('，', ',').replace('、', ',').split(",");
                        skill.learnerVocation = new EVocation[vocations.length];
                        for(int i = 0; i < vocations.length; i++) {
                        	skill.learnerVocation[i] = EVocation.getVocationByDesc(vocations[i]);
                        }

//                        skill.learnerVocation = EVocation
//                                .getVocationByDesc(subE
//                                        .elementTextTrim("learnerVocation"));
                        //end

                        data = subE.elementTextTrim("needMoney");

                        if (null != data)
                        {
                            skill.learnFreight = Integer.parseInt(data);
                        }

                        if (skill instanceof ActiveSkill)
                        {
                            data = subE.elementTextTrim("canUseInFight");

                            if (null != data && data.equals("非战斗"))
                            {
                                ((ActiveSkill) skill).onlyNotFightingStatus = true;
                            }
                        }

                        data = subE.elementTextTrim("needWeaponType");
                        if (null != data)
                        {
                        	//edit by zhengl ; date: 2010-12-05 ; note: 技能适用武器类型有可能多种
                            skill.needWeaponType = EWeaponType.getTypes(
                            		data.replace('，', ',').replace('、', ',').split(",") );
                        }
                        
                        //add by zhengl ; date: 2010-11-11 ; note: 添加1个节点
                        data = subE.elementTextTrim("needSkillPoint");
                        if(data != null)
                        {
                        	skill.skillPoints = Short.parseShort(data);
                        }

                        skill.iconID = Short.parseShort(subE.elementTextTrim("icon"));
                        
//                        skill.description = subE.elementTextTrim("description");
                        
                        if (skillUnit.additionalSEID > 0)
                        {
                            if (skillUnit.additionalSEID > 100000)
                            {
                                skill.addEffectUnit = EffectDictionary
                                        .getInstance().getEffectRef(
                                                skillUnit.additionalSEID);
                            }
                            else
                            {
                                skill.addSkillUnit = SkillUnitDict.getInstance()
                                        .getSkillUnitRef(
                                                skillUnit.additionalSEID);
                            }
                        }
                        data = subE.elementTextTrim("maxLevel");
                        if (data != null) 
                        {
							skill.maxLevel = Short.valueOf(data);
						}
                        if (skill instanceof ActiveSkill)
                        {
                            data = subE.elementTextTrim("coolDownID");

                            if (null != data)
                            {
                                ((ActiveSkill) skill).coolDownID = Integer.parseInt(data);
                                data = subE.elementTextTrim("coolDownTime");
                                if (data != null) 
                                {
                                    ((ActiveSkill) skill).coolDownTime = Integer
                                    	.parseInt(subE.elementTextTrim("coolDownTime"));
								}
                                else 
                                {
									log.warn("该技能CD时间不能为NULL,请检查玩家技能表. skillid=" 
											+ String.valueOf(skill.id));
								}
                            }

                            data = subE.elementTextTrim("hpConditionTargetType");

                            if (null != data)
                            {
                                ((ActiveSkill) skill).hpConditionTargetType = ETargetType
                                        .get(data);

                                data = subE.elementTextTrim("compareLineType");

                                if (data.equals("大于等于"))
                                {
                                    ((ActiveSkill) skill).hpConditionCompareLine = 
                                    	ActiveSkill.HP_COND_LINE_OF_GREATER_AND_EQUAL;
                                }
                                else
                                {
                                    ((ActiveSkill) skill).hpConditionCompareLine = 
                                    	ActiveSkill.HP_COND_LINE_OF_LESS;
                                }

                                ((ActiveSkill) skill).hpConditionPercent = Float
                                        .parseFloat(subE
                                                .elementTextTrim("hpPercent")) / 100;
                            }

                            ((ActiveSkill) skill).releaserExistsEffectName = subE
                                    .elementTextTrim("selfExistsEffect");

                            ((ActiveSkill) skill).releaserUnexistsEffectName = subE
                                    .elementTextTrim("selfUnexistsEffect");

                            ((ActiveSkill) skill).targetUnexistsEffectName = subE
                                    .elementTextTrim("targetExistsEffect");

                            ((ActiveSkill) skill).targetUnexistsEffectName = subE
                                    .elementTextTrim("targetUnexistsEffect");
                            
                            
                            StringBuffer desc = new StringBuffer();
                            if(skill.level == 0) 
                            {
                            	desc.append("技能等级:未学习").append("#HH");
                                desc.append("升级所需技能点:").append(skill.skillPoints).append("#HH");
                                desc.append("升级所需金钱:").append(skill.learnFreight);
                            }
                            else 
                            {
                            	String descTemp = null;
                            	if (skill.maxLevel == skill.level) 
                                {
                            		desc.append("技能等级:").append(skill.level)
                            			.append("(已达最高等级)#HH");
    							}
                            	else 
                            	{
                            		desc.append("技能等级:").append(skill.level).append("#HH");
								}
                                
                                descTemp = subE.elementTextTrim("description_1");
                                desc.append("技能目标:")
                                	.append(descTemp == null ? "" : descTemp).append("#HH");
                                
                                desc.append("技能冷却:")
                                	.append(((ActiveSkill) skill).coolDownTime).append("#HH");
                                
                                descTemp = subE.elementTextTrim("description_2");
                                desc.append("技能距离:")
                                	.append(descTemp == null ? "" : descTemp).append("#HH");
                                
                                descTemp = subE.elementTextTrim("description_3");
                                desc.append("施法时间:")
                                	.append(descTemp == null ? "" : descTemp).append("#HH");
                                
                                descTemp = subE.elementTextTrim("description_4");
                                desc.append("消耗法力:")
                                	.append(descTemp == null ? "" : descTemp).append("#HH");
                                
                                descTemp = subE.elementTextTrim("description_5");
                                desc.append("技能描述:")
                                	.append(descTemp == null ? "" : descTemp).append("#HH");
                                desc.append("所需武器:");
                                if(skill.needWeaponType != null)
                                {
                                    for(EWeaponType wtype : skill.needWeaponType)
                                    {
                                    	desc.append(wtype.getDesc()).append(",");
                                    }
                                    desc.deleteCharAt(desc.length()-1);
                                }
                                desc.append("#HH");
                                desc.append("升级所需技能点:").append(skill.skillPoints).append("#HH");
                                desc.append("升级所需金钱:").append(skill.learnFreight);
							}
                            skill.description = desc.toString();
                            
                        }
                        else 
                        {
                            StringBuffer desc = new StringBuffer();
                            if(skill.level == 0) 
                            {
                            	desc.append("技能等级:未学习").append("#HH");
                                desc.append("升级所需技能点:").append(skill.skillPoints).append("#HH");
                                desc.append("升级所需金钱:").append(skill.learnFreight).append("#HH");
                            }
                            else 
                            {
                            	String descTemp = null;
                            	if (skill.maxLevel == skill.level) 
                                {
                            		desc.append("技能等级:").append(skill.level)
                            			.append("(已达最高等级)#HH");
    							}
                            	else 
                            	{
                            		desc.append("技能等级:").append(skill.level).append("#HH");
								}
                                desc.append("所需武器:");
                                if(skill.needWeaponType != null)
                                {
                                    for(EWeaponType wtype : skill.needWeaponType){
                                    	desc.append(wtype.getDesc()).append(",");
                                    }
                                    desc.deleteCharAt(desc.length()-1);
                                }
                                desc.append("#HH");
                                descTemp = subE.elementTextTrim("description_5");
                                desc.append("技能描述:")
                                	.append(descTemp == null ? "" : descTemp).append("#HH");
                                desc.append("升级所需技能点:").append(skill.skillPoints).append("#HH");
                                desc.append("升级所需金钱:").append(skill.learnFreight).append("#HH");
							}
                            skill.description = desc.toString();
                        }
                        skillTable.put(skill.id, skill);
                        skillList = noneFeatureSkill.get(skill.name);
                        
                        if (null == skillList)
                        {
                        	skillList = new ArrayList<Skill>();
                        	noneFeatureSkill.put(skill.name, skillList);
                        }
                        
                        skillList.add(skill);
                    }
                }
            }

            Iterator<ArrayList<Skill>> iterator = heroFeatureSkill.values()
                    .iterator();

            while (iterator.hasNext())
            {
                skillList = iterator.next();

                if (skillList.size() > 1)
                {
                    for (Skill skill1 : skillList)
                    {
                        for (Skill skill2 : skillList)
                        {
                            if (skill1.level + 1 == skill2.level)
                            {
                                skill1.next = skill2;
                                skill2.prev = skill1;

                                break;
                            }
                        }
                    }
                }
            }

            iterator = reverFeatureSkill.values().iterator();

            while (iterator.hasNext())
            {
                skillList = iterator.next();

                if (skillList.size() > 1)
                {
                    for (Skill skill1 : skillList)
                    {
                        for (Skill skill2 : skillList)
                        {
                            if (skill1.level + 1 == skill2.level)
                            {
                                skill1.next = skill2;
                                skill2.prev = skill1;

                                break;
                            }
                        }
                    }
                }
            }

            iterator = noneFeatureSkill.values().iterator();

            while (iterator.hasNext())
            {
                skillList = iterator.next();

                if (skillList.size() > 1)
                {
                    for (Skill skill1 : skillList)
                    {
                        for (Skill skill2 : skillList)
                        {
                            if (skill1.level + 1 == skill2.level)
                            {
                                skill1.next = skill2;
                                skill2.prev = skill1;

                                break;
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
}
