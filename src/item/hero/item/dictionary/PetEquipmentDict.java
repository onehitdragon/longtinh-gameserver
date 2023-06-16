package hero.item.dictionary;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.tools.YOYOOutputStream;

import hero.item.Armor;
import hero.item.PetArmor;
import hero.item.PetWeapon;
import hero.item.Weapon;
import hero.item.detail.EPetBodyPartOfEquipment;
import hero.item.dictionary.SuiteEquipmentDataDict.SuiteEData;
import hero.share.AccessorialOriginalAttribute;
import hero.share.EMagic;
import hero.share.EquipmentPropertyDefine;
import hero.share.MagicFastnessList;
import hero.share.service.LogWriter;
import javolution.util.FastMap;
/**
 * 宠物装备
 * @author jiaodongjie
 *
 */
public class PetEquipmentDict
{

	/**
     * 防具字典容器
     */
    FastMap<Integer, PetArmor>  petArmorDictionary;
    /**
     * 武器字典容器
     */
    FastMap<Integer, PetWeapon>  petWeaponDictionary;
    
    private PetEquipmentDict(){
    	petArmorDictionary = new FastMap<Integer, PetArmor>();
    	petWeaponDictionary = new FastMap<Integer, PetWeapon>();
    }
    
    private static PetEquipmentDict instance;
    
    public static PetEquipmentDict getInstance(){
    	if(instance == null){
    		instance = new PetEquipmentDict();
    	}
    	return instance;
    }
    
    /**
     * 根据宠物防具ID获取
     * @param id
     * @return
     */
    public PetArmor getPetArmor(int id){
    	return petArmorDictionary.get(id);
    }
    /**
     * 根据宠物武器ID获取
     * @param id
     * @return
     */
    public PetWeapon getPetWeapon(int id){
    	return petWeaponDictionary.get(id);
    }
    
    /**
     * 加载宠物装备数据
     * 武器和防具
     * @param _dataPath
     */
    public void load (String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            YOYOOutputStream outputStreamTool = new YOYOOutputStream();
            
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
                    	PetArmor petArmor = null;
                    	PetWeapon petWeapon = null;
                    	
                    	outputStreamTool.reset();
                    	String id = subE.elementTextTrim("id");
                    	String name = subE.elementTextTrim("name");
                    	String type = subE.elementTextTrim("type");
                    	
                    	if(!type.equals("爪部")){
                    		petArmor = new PetArmor();
                    		petArmor.setID(Integer.parseInt(id));
                    		petArmor.setName(name);
                    		petArmor.setWearBodyPart(EPetBodyPartOfEquipment.getBodyPart(type));
                    		petArmor.setTrait(subE.elementTextTrim("trait"));
                    		petArmor.setNeedLevel(Integer.parseInt(subE.elementTextTrim("needLevel")));
                    		data = subE.elementTextTrim("exchangeable");
                    		if(data.equals("是")){
                    			petArmor.setExchangeable();
                    		}
                    		data = subE.elementTextTrim("bind");
                    		if(data != null){
                    			petArmor.setBindType((byte)(data.equals("是")?1:0));
                    		}else{
                    			petArmor.setBindType((byte)0);
                    		}
                    		petArmor.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                    		
                    		petArmor.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                    		data = subE.elementTextTrim("repairable");
                    		if(data.equals("是")){
                    			petArmor.setRepairable(true);
                    		}else{
                    			petArmor.setRepairable(false);
                    		}
                    		data = subE.elementTextTrim("immobilityTime");
                    		if(null != data){
                    			petArmor.setImmobilityTime(Float.parseFloat(data));
                    		}
                    		
                    		/*data =  subE.elementTextTrim("defense");
                    		if(null != data){
                    			petArmor.atribute.defense = Integer.parseInt(data);
                    		}
                    		
                            data = subE.elementTextTrim("hp");
                            if (null != data)
                            {
                            	petArmor.atribute.hp = Integer.parseInt(data);
                            }*/
                            data = subE.elementTextTrim("mp");
                            if (null != data)
                            {
                            	petArmor.atribute.mp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("strength");
                            if (null != data)
                            {
                            	petArmor.atribute.strength = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("agility");
                            if (null != data)
                            {
                            	petArmor.atribute.agility = Integer.parseInt(data);
                            }
                           
                            data = subE.elementTextTrim("inte");
                            if (null != data)
                            {
                            	petArmor.atribute.inte = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("spirit");
                            if (null != data)
                            {
                            	petArmor.atribute.spirit = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("luck");
                            if (null != data)
                            {
                            	petArmor.atribute.lucky = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("physicsDeathblowLevel");
                            if (null != data)
                            {
                            	petArmor.atribute.physicsDeathblowLevel = (Short.parseShort(data));
                            }
                            data = subE.elementTextTrim("magicDeathblowLevel");
                            if (null != data)
                            {
                            	petArmor.atribute.magicDeathblowLevel = (Short.parseShort(data));
                            }
                            data = subE.elementTextTrim("hitOdds");
                            if (null != data)
                            {
                            	petArmor.atribute.hitLevel = (Short.parseShort(data));
                            }
                            petArmor.setIconID(Short.parseShort(subE
                                    .elementTextTrim("miniImageID")));

                            data = subE.elementTextTrim("equipmentImageID");

                            if (null != data)
                            {
                            	petArmor.setImageID(Short.parseShort(data));
                            }

                            petArmor.initDescription();

                            data = subE.elementTextTrim("description");

                            if (null != data)
                            {
                                setFixPropertyBytes(petArmor, outputStreamTool,data);
                                petArmor.appendDescription(data);
                            }
                            else
                            {
                                setFixPropertyBytes(petArmor, outputStreamTool, "");
                            }
                            
                            petArmorDictionary.put(Integer.parseInt(id), petArmor);
                    	}else{
                    		petWeapon = new PetWeapon();
                    		
                    		petWeapon.setID(Integer.parseInt(id));
                    		petWeapon.setName(name);
                    		petWeapon.setWearBodyPart(EPetBodyPartOfEquipment.CLAW);
                    		petWeapon.setTrait(subE.elementTextTrim("trait"));
                    		petWeapon.setNeedLevel(Integer.parseInt(subE.elementTextTrim("needLevel")));
                    		data = subE.elementTextTrim("exchangeable");
                    		if(data.equals("是")){
                    			petWeapon.setExchangeable();
                    		}
                    		data = subE.elementTextTrim("bind");
                    		if(data != null){
                    			petWeapon.setBindType((byte)(data.equals("是")?1:0));
                    		}else{
                    			petWeapon.setBindType((byte)0);
                    		}
                    		petWeapon.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                    		
                    		petWeapon.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                    		petWeapon.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                    		data = subE.elementTextTrim("atkRange");
                    		if(data != null){
                    			petWeapon.setAttackDistance(Short.parseShort(data));
                    		}
                    		petWeapon.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                    		data = subE.elementTextTrim("repairable");
                    		if(data.equals("是")){
                    			petWeapon.setRepairable(true);
                    		}else{
                    			petWeapon.setRepairable(false);
                    		}
                    		data = subE.elementTextTrim("immobilityTime");
                    		if(null != data){
                    			petWeapon.setImmobilityTime(Float.parseFloat(data));
                    		}
                    		data = subE.elementTextTrim("minPhysicsAttack");
                    		if(null != data){
                    			petWeapon.setMinPhysicsAttack(Integer.parseInt(data));
                    		}
                    		data = subE.elementTextTrim("maxPhysicsAttack");
                    		if(null != data){
                    			petWeapon.setMaxPhysicsAttack(Integer.parseInt(data));
                    		}
                    		
                    		data =  subE.elementTextTrim("defense");
                    		if(null != data){
                    			petWeapon.atribute.defense = Integer.parseInt(data);
                    		}
                    		if (null != data)
                            {
                    			petWeapon.atribute.defense = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("hp");
                            if (null != data)
                            {
                            	petWeapon.atribute.hp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("mp");
                            if (null != data)
                            {
                            	petWeapon.atribute.mp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("strength");
                            if (null != data)
                            {
                            	petWeapon.atribute.strength = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("agility");
                            if (null != data)
                            {
                            	petWeapon.atribute.agility = Integer.parseInt(data);
                            }
                           
                            data = subE.elementTextTrim("inte");
                            if (null != data)
                            {
                            	petWeapon.atribute.inte = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("spirit");
                            if (null != data)
                            {
                            	petWeapon.atribute.spirit = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("luck");
                            if (null != data)
                            {
                            	petWeapon.atribute.lucky = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("physicsDeathblowLevel");
                            if (null != data)
                            {
                            	petWeapon.atribute.physicsDeathblowLevel = (Short.parseShort(data));
                            }
                            data = subE.elementTextTrim("magicDeathblowLevel");
                            if (null != data)
                            {
                            	petWeapon.atribute.magicDeathblowLevel = (Short.parseShort(data));
                            }
                            data = subE.elementTextTrim("hitOdds");
                            if (null != data)
                            {
                            	petWeapon.atribute.hitLevel = (Short.parseShort(data));
                            }
                            petWeapon.setIconID(Short.parseShort(subE
                                    .elementTextTrim("miniImageID")));

                            data = subE.elementTextTrim("equipmentImageID");

                            if (null != data)
                            {
                            	petWeapon.setImageID(Short.parseShort(data));
                            }

                            petWeapon.initDescription();

                            data = subE.elementTextTrim("description");

                            if (null != data)
                            {
                                setFixPropertyBytes(petWeapon, outputStreamTool,data);
                                petWeapon.appendDescription(data);
                            }
                            else
                            {
                                setFixPropertyBytes(petWeapon, outputStreamTool, "");
                            }
                            petWeaponDictionary.put(Integer.parseInt(id), petWeapon);
                    	}
                    }
                    
                }
                
            }
            
            outputStreamTool.close();
            outputStreamTool = null;
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    /**
     * 设置武器固定属性字节数据
     * 
     * @param _weapon
     * @param _output
     * @param _desc
     * @throws IOException
     */
    private void setFixPropertyBytes (PetWeapon _weapon, YOYOOutputStream _output,
            String _desc) throws IOException{
    	_output.reset();

        _output.writeByte(EquipmentPropertyDefine.WEAPON_TYPE);
        _output.writeUTF(_weapon.getName());

        _output.writeByte(EquipmentPropertyDefine.NEED_LVL);
        _output.writeShort(_weapon.getNeedLevel());

//        _output.writeByte(EquipmentPropertyDefine.TRAIT);
//        _output.writeShort(_weapon.getTrait().value());

        _output.writeByte(EquipmentPropertyDefine.MIN_PHY_ATTK);
        _output.writeShort(_weapon.getMinPhysicsAttack());

        _output.writeByte(EquipmentPropertyDefine.MAX_PHY_ATTK);
        _output.writeShort(_weapon.getMaxPhysicsAttack());

        _output.writeByte(EquipmentPropertyDefine.ATTACK_IMMO);
        _output.writeShort(_weapon.getImmobilityTime() * 1000);

        _output.writeByte(EquipmentPropertyDefine.BIND_TYPE);
        _output.writeShort(_weapon.getBindType());

        _output.writeByte(EquipmentPropertyDefine.MAX_DURABILITY);
        _output.writeShort(_weapon.getMaxDurabilityPoint());

        _output.writeByte(EquipmentPropertyDefine.ATTACK_DISTANCE);
        _output.writeShort(_weapon.getAttackDistance());

        AccessorialOriginalAttribute atribute = _weapon.atribute;

        
        if (0 < atribute.inte)
        {
            _output.writeByte(EquipmentPropertyDefine.INTE);
            _output.writeShort(atribute.inte);
        }
        if (0 < atribute.strength)
        {
            _output.writeByte(EquipmentPropertyDefine.STRENGTH);
            _output.writeShort(atribute.strength);
        }
        if (0 < atribute.spirit)
        {
            _output.writeByte(EquipmentPropertyDefine.SPIRIT);
            _output.writeShort(atribute.spirit);
        }
        if (0 < atribute.agility)
        {
            _output.writeByte(EquipmentPropertyDefine.AGILITY);
            _output.writeShort(atribute.agility);
        }
        if (0 < atribute.lucky)
        {
            _output.writeByte(EquipmentPropertyDefine.LUCKY);
            _output.writeShort(atribute.lucky);
        }
        if (0 < atribute.hp)
        {
            _output.writeByte(EquipmentPropertyDefine.HP);
            _output.writeShort(atribute.hp);
        }
        if (0 < atribute.mp)
        {
            _output.writeByte(EquipmentPropertyDefine.MP);
            _output.writeShort(atribute.mp);
        }

        if (0 < atribute.physicsDeathblowLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.PHY_DEATH_LVL);
            _output.writeShort(atribute.physicsDeathblowLevel);
        }
        if (0 < atribute.magicDeathblowLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.MAG_DEATH_LVL);
            _output.writeShort(atribute.magicDeathblowLevel);
        }
        if (0 < atribute.hitLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.HIT_LVL);
            _output.writeShort(atribute.hitLevel);
        }
        if (0 < atribute.duckLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.DUCK_LVL);
            _output.writeShort(atribute.duckLevel);
        }
        if (0 < atribute.defense)
        {
            _output.writeByte(EquipmentPropertyDefine.DEFENSE);
            _output.writeShort(atribute.defense);
        }
        
        _output.writeByte(-1);
        _output.writeUTF(_desc);
        _output.flush();
        _weapon.setFixPropertyBytes(_output.getBytes());
    }
    
    /**
     * 设置防具固定属性字节数据
     * 
     * @param _armor
     * @param _output
     * @param _desc
     * @throws IOException
     */
    private void setFixPropertyBytes (PetArmor _armor, YOYOOutputStream _output,
            String _desc) throws IOException
    {
        _output.reset();

        _output.writeByte(EquipmentPropertyDefine.ARMOR_TYPE);
        _output.writeShort(_armor.getWearBodyPart().value());// TODO 暂时用部位代替

        _output.writeByte(EquipmentPropertyDefine.WEAR_BODYPART);
        _output.writeShort(_armor.getWearBodyPart().value());

        _output.writeByte(EquipmentPropertyDefine.NEED_LVL);
        _output.writeShort(_armor.getNeedLevel());

//        _output.writeByte(EquipmentPropertyDefine.TRAIT);
//        _output.writeShort(_armor.getTrait().value());

        _output.writeByte(EquipmentPropertyDefine.BIND_TYPE);
        _output.writeShort(_armor.getBindType());

        _output.writeByte(EquipmentPropertyDefine.MAX_DURABILITY);
        _output.writeShort(_armor.getMaxDurabilityPoint());

        AccessorialOriginalAttribute atribute = _armor.atribute;

        if (0 < atribute.stamina)
        {
            _output.writeByte(EquipmentPropertyDefine.STAMINA);
            _output.writeShort(atribute.stamina);
        }
        if (0 < atribute.inte)
        {
            _output.writeByte(EquipmentPropertyDefine.INTE);
            _output.writeShort(atribute.inte);
        }
        if (0 < atribute.strength)
        {
            _output.writeByte(EquipmentPropertyDefine.STRENGTH);
            _output.writeShort(atribute.strength);
        }
        if (0 < atribute.spirit)
        {
            _output.writeByte(EquipmentPropertyDefine.SPIRIT);
            _output.writeShort(atribute.spirit);
        }
        if (0 < atribute.agility)
        {
            _output.writeByte(EquipmentPropertyDefine.AGILITY);
            _output.writeShort(atribute.agility);
        }
        if (0 < atribute.lucky)
        {
            _output.writeByte(EquipmentPropertyDefine.LUCKY);
            _output.writeShort(atribute.lucky);
        }
        if (0 < atribute.hp)
        {
            _output.writeByte(EquipmentPropertyDefine.HP);
            _output.writeShort(atribute.hp);
        }
        if (0 < atribute.mp)
        {
            _output.writeByte(EquipmentPropertyDefine.MP);
            _output.writeShort(atribute.mp);
        }

        if (0 < atribute.physicsDeathblowLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.PHY_DEATH_LVL);
            _output.writeShort(atribute.physicsDeathblowLevel);
        }
        if (0 < atribute.magicDeathblowLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.MAG_DEATH_LVL);
            _output.writeShort(atribute.magicDeathblowLevel);
        }
        if (0 < atribute.hitLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.HIT_LVL);
            _output.writeShort(atribute.hitLevel);
        }
        if (0 < atribute.duckLevel)
        {
            _output.writeByte(EquipmentPropertyDefine.DUCK_LVL);
            _output.writeShort(atribute.duckLevel);
        }
        if (0 < atribute.defense)
        {
            _output.writeByte(EquipmentPropertyDefine.DEFENSE);
            _output.writeShort(atribute.defense);
        }

        MagicFastnessList mfl = _armor.getMagicFastnessList();

        if (null != mfl)
        {
            int value = mfl.getEMagicFastnessValue(EMagic.SANCTITY);
            if (value > 0)
            {
                _output.writeByte(EquipmentPropertyDefine.SANCTITY_FSTS);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.UMBRA);
            if (value > 0)
            {
                _output.writeByte(EquipmentPropertyDefine.UMBRA_FSTS);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.FIRE);
            if (value > 0)
            {
                _output.writeByte(EquipmentPropertyDefine.FIRE_FSTS);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.WATER);
            if (value > 0)
            {
                _output.writeByte(EquipmentPropertyDefine.WATER_FSTS);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.SOIL);
            if (value > 0)
            {
                _output.writeByte(EquipmentPropertyDefine.SOIL_FSTS);
                _output.writeShort(value);
            }
        }

        

        _output.writeByte(-1);
        _output.writeUTF(_desc);
        _output.flush();
        _armor.setFixPropertyBytes(_output.getBytes());
    }
}
