package hero.skill.dict;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.effect.dictionry.EffectDictionary;
import hero.skill.ActiveSkill;
import hero.skill.Skill;
import hero.skill.service.SkillConfig;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.unit.SkillUnit;
import javolution.util.FastMap;

/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件	MonsterSkillDict.java
 *  @创建者	zhengl
 *  @版本	1.0
 *  @时间	2010-12-10 下午01:18:15
 *  @描述:	怪物技能字典
 **/

public class MonsterSkillDict
{
     private static Logger log = Logger.getLogger(MonsterSkillDict.class);
    /**
     * 主动技能单元容器
     */
    private FastMap<Integer, Skill> skillUnitTable;
    
    /**
     * 单例
     */
    private static MonsterSkillDict        instance;
    
    /**
     * 私有构造
     */
    private MonsterSkillDict()
    {
        skillUnitTable = new FastMap<Integer, Skill>();
    }
    
    /**
     * 获取单例
     * 
     * @return
     */
    public static MonsterSkillDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MonsterSkillDict();
        }

        return instance;
    }
    
    /**
     * 获取技能单元
     * 
     * @param _skillUnitID
     * @return
     */
    public Skill getSkillUnitRef (int _skillUnitID)
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
    public Skill getSkillUnitInstance (int _skillUnitID)
    {
        if (_skillUnitID > 0)
        {
            try
            {
                return skillUnitTable.get(_skillUnitID).clone();
            }
            catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }
    
    /**
     * 加载
     * 
     * @param _config
     */
    public void load (SkillConfig _config)
    {
        try
        {
        	File dataPath = new File(_config.monster_skill_data_path);
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
                while (rootIt.hasNext()) {
                	Skill skill;
                	Element subE = rootIt.next();
                    if (null != subE) {
                        skill = new ActiveSkill(Integer.parseInt(
                        		subE.elementTextTrim("skillID")), 
                        		subE.elementTextTrim("name"));
                        //获得新的技能单元对象.
                        int skillID = 0;
                        try 
                        {
                        	skillID = Integer.parseInt(subE.elementTextTrim("effectIdx"));
						} 
                        catch (Exception e) 
                        {
							e.printStackTrace();
							break;
						}
                        SkillUnit skillUnit = SkillUnitDict.getInstance().getSkillUnitInstance(skillID);
                        if(skillUnit == null)
                        {
                        	log.info("怪物技能为NUL,skillID:"+skillID);
                        	break;
                        }
                        
                        if (skillUnit instanceof ActiveSkillUnit)
                        {
                            skill.setSkillUnit((ActiveSkillUnit) skillUnit);
                        }
                        else
                        {
                            log.info("怪物无法加载非主动技能");
                        }
                        //根据技能单元携带的触发效果编号给技能绑定效果/技能
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
                        skillUnitTable.put(skill.id, skill);
                        log.info("加载怪物技能成功:" + skill.id + ";" + skill.name);
                    } 
                } //end while
            }
        } catch (Exception e) {
        	log.info("Error:加载怪物技能失败");
			e.printStackTrace();
		}
    }
    
    
    
}


