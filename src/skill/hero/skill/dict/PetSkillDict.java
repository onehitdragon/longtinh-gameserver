package hero.skill.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.effect.Effect.EffectFeature;
import hero.pet.Pet;
import hero.pet.PetKind;
import hero.pet.service.PetConfig;
import hero.share.EMagic;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;
import hero.skill.PetSkill;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.PetAdditionEffect;
import javolution.util.FastMap;

/**
 * 宠物技能字典
 * @author jiaodongjie
 *
 */
public class PetSkillDict
{
	
	private static Logger log = Logger.getLogger(PetSkillDict.class);
	
	private FastMap<Integer, PetSkill> petSkillDict;
	private FastMap<Integer, PetAdditionEffect> petAdditionEffectDict;
	private static PetSkillDict instance;
	
	public static PetSkillDict getInstance(){
		if(instance == null){
			instance = new PetSkillDict();
		}
		return instance;
	}
	
	private PetSkillDict(){
		petSkillDict = new FastMap<Integer, PetSkill>();
		petAdditionEffectDict = new FastMap<Integer, PetAdditionEffect>();
	}
	
	/**
	 * 根据技能ID获取技能
	 * @param skillId
	 * @return
	 */
	public PetSkill getPetSkill(int skillId){
		return petSkillDict.get(skillId);
	}
	
	/**
	 * 根据效果ID获取技能持续型效果
	 * @param id
	 * @return
	 */
	public PetAdditionEffect getPetAdditionEffect(int id){
		return petAdditionEffectDict.get(id);
	}
	
	/**
	 * 获取某个宠物当前状态下可以学习的技能列表
	 * 包含自动学习和技能书能学习的
	 * @param pet
	 * @return
	 */
	public List<PetSkill> getPetCanLearnSkillList(Pet pet){
		List<PetSkill> canLearnSkillList = new ArrayList<PetSkill>();
		
		for(Iterator<PetSkill> it = petSkillDict.values().iterator(); it.hasNext();){
			PetSkill skill = it.next();
			if(skill.needLevel == pet.level 
					&& (skill.petKind.getKindID()==pet.pk.getKind() || skill.petKind == PetKind.ALL)){
				canLearnSkillList.add(skill);
			}
		}
		
		return canLearnSkillList;
	}
	
	public void load(PetConfig config){
		log.debug("start loading pet skill data...");
		loadPetSkillData(config.pet_skill_data_path);
//		loadPetSkillEffectData(config.pet_skill_effect_data_path);//暂时不做持续型技能效果
		log.debug("loading pet skill data end ...");
	}
	
	/**
	 * 加载宠物技能数据
	 * @param _dataPath 含文件名的完整路径
	 */
	public void loadPetSkillData(String _dataPath){
		File file;
//		PetSkill skill;
		try{
			file = new File(_dataPath);
			if(file.exists()){
				
				SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                String data, id, name;
                while(rootIt.hasNext()){
                	Element subE = rootIt.next();
                	id = subE.elementTextTrim("id");
                	name = subE.elementTextTrim("name");
                	data = subE.elementTextTrim("type");
                	if(data.equals("1") || data.equals("主动")){
                		PetActiveSkill skill = new PetActiveSkill(Integer.parseInt(id), name);
                		data = subE.elementTextTrim("level");
                		skill.level = Integer.parseInt(data);
                		data = subE.elementTextTrim("skillType");
                		if(null != data){
                			skill.skillType = EActiveSkillType.getType(data);
                		}
                		data = subE.elementTextTrim("getFrom");
                		if(data.equals("直接习得"))
                			skill.getFrom = 1;
                		else skill.getFrom = 2;
                		data = subE.elementTextTrim("lev");
                		skill.needLevel = Integer.parseInt(data);
                		data = subE.elementTextTrim("petKind");
                		skill.petKind = PetKind.getPetKind(data);
                		data = subE.elementTextTrim("publicVar");
                		if(null != data)
                			skill.coolPublicVar = Integer.parseInt(data);
                		data = subE.elementTextTrim("time");
                		if(null != data)
                			skill.coolDownTime = Integer.parseInt(data);
                		data = subE.elementTextTrim("useMp");
                		if(null != data)
                			skill.useMp = Integer.parseInt(data);
                		data = subE.elementTextTrim("targetType");
                		if(null != data)
                			skill.targetType = ETargetType.get(data);
                		data = subE.elementTextTrim("range");
                		if(null != data)
                			skill.targetRangeType = ETargetRangeType.get(data);
                		else skill.targetRangeType = ETargetRangeType.SINGLE;
                		
                		data = subE.elementTextTrim("distance");
                		if(null != data)
                			skill.targetDistance = Byte.parseByte(data);
                		data = subE.elementTextTrim("rangeBase");
                		if(null != data)
                			skill.rangeBaseLine = EAOERangeBaseLine.get(data);
                		data = subE.elementTextTrim("rangeMode");
                		if(null != data)
                			skill.rangeMode = EAOERangeType.get(data);
                		data = subE.elementTextTrim("rangeX");
                		if(null != data)
                			skill.rangeX = Byte.parseByte(data);
                		data = subE.elementTextTrim("rangeY");
                		if(null != data)
                			skill.rangeY = Byte.parseByte(data);
                		data = subE.elementTextTrim("atkMult");
                		if(null != data)
                			skill.atkMult = Byte.parseByte(data);
                		data = subE.elementTextTrim("physicsHarmValue");
                		if(null != data)
                			skill.physicsHarmValue = Integer.parseInt(data);
                		data = subE.elementTextTrim("magicHarmType");
                		if(null != data)
                			skill.magicHarmType = EMagic.getMagic(data);
                		data = subE.elementTextTrim("magicAtkValue");
                		if(null != data)
                			skill.magicHarmHpValue = Integer.parseInt(data);
                		data = subE.elementTextTrim("resumeHp");
                		if(null != data)
                			skill.resumeHp = Integer.parseInt(data);
                		data = subE.elementTextTrim("resumeMp");
                		if(null != data)
                			skill.resumeMp = Integer.parseInt(data);
                		data = subE.elementTextTrim("code");
                		if(null != data)
                			skill.effectID = Integer.parseInt(data);
                		data = subE.elementTextTrim("odds");
                		if(null != data)
                			skill.effectOdds = Float.parseFloat(data);
                		data = subE.elementTextTrim("iconID");
                		if(null != data)
                			skill.iconID = Short.parseShort(data);
                		data = subE.elementTextTrim("fireImageId");
                		if(null != data)
                			skill.releaseAnimationID = Short.parseShort(data);
                		data = subE.elementTextTrim("actImageId");
                		if(null != data)
                			skill.activeAnimationID = Short.parseShort(data);
                		data = subE.elementTextTrim("description");
                		if(null != data)
                			skill.description = data;
                		
                		petSkillDict.put(Integer.parseInt(id), skill);
                		
                	}else if(data.equals("2") || data.equals("被动")){
                		PetPassiveSkill skill = new PetPassiveSkill(Integer.parseInt(id), name);
                		data = subE.elementTextTrim("level");
                		skill.level = Integer.parseInt(data);
                		data = subE.elementTextTrim("getFrom");
                		if(data.equals("直接习得"))
                			skill.getFrom = 1;
                		else skill.getFrom = 2;
                		data = subE.elementTextTrim("lev");
                		skill.needLevel = Integer.parseInt(data);
                		data = subE.elementTextTrim("petKind");
                		skill.petKind = PetKind.getPetKind(data);
                		data = subE.elementTextTrim("calMode");
                		if(null != data)
                			skill.caluOperator = EMathCaluOperator.get(data);
                		data = subE.elementTextTrim("targetType");
                		if(null != data)
                			skill.targetType = ETargetType.get(data);
                		data = subE.elementTextTrim("range");
                		if(null != data)
                			skill.targetRangeType = ETargetRangeType.get(data);
                		else skill.targetRangeType = ETargetRangeType.SINGLE;
                		data = subE.elementTextTrim("str");
                		if(null != data)
                			skill.strength = Float.parseFloat(data);
                		data = subE.elementTextTrim("agile");
                		if(null != data)
                			skill.agility = Float.parseFloat(data);
                		data = subE.elementTextTrim("intel");
                		if(null != data)
                			skill.inte = Float.parseFloat(data);
                		data = subE.elementTextTrim("spi");
                		if(null != data)
                			skill.spirit = Float.parseFloat(data);
                		data = subE.elementTextTrim("luck");
                		if(null != data)
                			skill.lucky = Float.parseFloat(data);
                		data = subE.elementTextTrim("maxMp");
                		if(null != data)
                			skill.maxMp = Float.parseFloat(data);
                		data = subE.elementTextTrim("hitLevel");
                		if(null != data)
                			skill.hitLevel = Float.parseFloat(data);
                		data = subE.elementTextTrim("physicsDeathblowLevel");
                		if(null != data)
                			skill.physicsDeathblowLevel = Float.parseFloat(data);
                		data = subE.elementTextTrim("magicDeathblowLevel");
                		if(null != data)
                			skill.magicDeathblowLevel = Float.parseFloat(data);
                		data = subE.elementTextTrim("physicsAttackHarmValue");
                		if(null != data)
                			skill.physicsAttackHarmValue = Float.parseFloat(data);
                		data = subE.elementTextTrim("magicHarmType");
                		if(null != data)
                			skill.magicHarmType = EMagic.getMagic(data);
                		data = subE.elementTextTrim("magicHarmValue");
                		if(null != data)
                			skill.magicHarmValue = Float.parseFloat(data);
                		data = subE.elementTextTrim("physicsAttackInterval");
                		if(null != data)
                			skill.physicsAttackInterval = Float.parseFloat(data);
                		data = subE.elementTextTrim("iconID");
                		if(null != data)
                			skill.iconID = Short.parseShort(data);
                		data = subE.elementTextTrim("description");
                		if(null != data)
                			skill.description = data;
                		
                		petSkillDict.put(Integer.parseInt(id), skill);
                		
                	}
                	log.debug("加载宠物技能数据完成...");
                }
			}
		}catch(Exception e){
			log.error("加载宠物技能数据 error : ",e);
		}
	}
	
	/**
	 * 加载宠物技能持续型效果数据
	 * @param _dataPath 含文件名的完整路径
	 */
	public void loadPetSkillEffectData(String _dataPath){
		File file;
		PetAdditionEffect pef;
		try{
			file = new File(_dataPath);
			if(file.exists()){
				
				SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();
                
                String data;
                while(rootIt.hasNext()){
                	pef = new PetAdditionEffect();
                	Element subE = rootIt.next();
                	data = subE.elementTextTrim("id");
                	pef.id = Integer.parseInt(data);
                	data = subE.elementTextTrim("name");
                	pef.name = data;
                	data = subE.elementTextTrim("needLevel");
                	pef.needLevel = Integer.parseInt(data);
                	data = subE.elementTextTrim("type");
                	if(null != data)
                		pef.additonType = EffectFeature.get(data);
                	data = subE.elementTextTrim("level");
                	if(null != data)
                		pef.additionLevel = Integer.parseInt(data);
                	data = subE.elementTextTrim("disAfterDie");
                	if(data != null)
                		pef.disAfterDie = data.equals("是");
                	else pef.disAfterDie = false;
                	data = subE.elementTextTrim("isBuff");
                	if(null != data)
                		pef.isBuff = data.equals("是");
                	else pef.isBuff = false;
                	data = subE.elementTextTrim("replaceID");
                	if(null != data)
                		pef.replaceID = Integer.parseInt(data);
                	data = subE.elementTextTrim("keepTime");
                	if(null != data)
                		pef.keepTime = Float.parseFloat(data);
                	data = subE.elementTextTrim("hpHarmValue");
                	if(null != data)
                		pef.harmHpValue = Integer.parseInt(data);
                	data = subE.elementTextTrim("harmType");
                	if(null != data)
                		pef.harmType = EHarmType.get(data);
                	data = subE.elementTextTrim("resumeHp");
                	if(null != data)
                		pef.resumeHp = Integer.parseInt(data);
                	data = subE.elementTextTrim("resumeMp");
                	if(null != data)
                		pef.resumeMp = Integer.parseInt(data);
                	data = subE.elementTextTrim("calMode");
                	if(null != data)
                		pef.caluOperator = EMathCaluOperator.get(data);
                	data = subE.elementTextTrim("str");
            		if(null != data)
            			pef.strength = Integer.parseInt(data);
            		data = subE.elementTextTrim("agile");
            		if(null != data)
            			pef.agility = Integer.parseInt(data);
            		data = subE.elementTextTrim("intel");
            		if(null != data)
            			pef.inte = Integer.parseInt(data);
            		data = subE.elementTextTrim("spi");
            		if(null != data)
            			pef.spirit = Integer.parseInt(data);
            		data = subE.elementTextTrim("luck");
            		if(null != data)
            			pef.lucky = Integer.parseInt(data);
            		data = subE.elementTextTrim("maxMp");
            		if(null != data)
            			pef.maxMp = Integer.parseInt(data);
            		data = subE.elementTextTrim("hitLevel");
            		if(null != data)
            			pef.hitLevel = Integer.parseInt(data);
            		data = subE.elementTextTrim("physicsDeathblowLevel");
            		if(null != data)
            			pef.physicsDeathblowLevel = Float.parseFloat(data);
            		data = subE.elementTextTrim("magicDeathblowLevel");
            		if(null != data)
            			pef.magicDeathblowLevel = Float.parseFloat(data);
            		data = subE.elementTextTrim("physicsAttackHarmValue");
            		if(null != data)
            			pef.physicsAttackHarmValue = Integer.parseInt(data);
            		data = subE.elementTextTrim("magicHarmType");
            		if(null != data)
            			pef.magicHarmType = EMagic.getMagic(data);
            		data = subE.elementTextTrim("magicHarmValue");
            		if(null != data)
            			pef.magicHarmValue = Float.parseFloat(data);
            		data = subE.elementTextTrim("physicsAttackInterval");
            		if(null != data)
            			pef.physicsAttackInterval = Float.parseFloat(data);
            		data = subE.elementTextTrim("iconID");
            		if(null != data)
            			pef.iconID = Short.parseShort(data);
            		data = subE.elementTextTrim("description");
            		if(null != data)
            			pef.description = data;
            		
            		petAdditionEffectDict.put(pef.id, pef);
            		
                }
                log.debug("加载宠物技能持续型效果数据完成...");
			}
		}catch(Exception e){
			log.error("加载宠物技能持续型效果数据 error : ",e);
		}
	}
}
