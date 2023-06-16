package hero.item.dictionary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.tools.YOYOOutputStream;

import hero.item.Equipment;
import hero.item.Weapon;
import hero.item.detail.EBodyPartOfEquipment;
import hero.share.AccessorialOriginalAttribute;
import hero.share.EMagic;
import hero.share.EquipmentPropertyDefine;
import hero.share.MagicFastnessList;
import hero.share.service.LogWriter;
import hero.share.service.MagicDamage;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WeaponDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-15 下午03:46:16
 * @描述 ：武器字典
 */

public class WeaponDict
{
    private static Logger log = Logger.getLogger(WeaponDict.class);
    /**
     * 字典容器
     */
    private FastMap<Integer, Weapon> dictionary;

    /**
     * 单例
     */
    private static WeaponDict        instance;

    /**
     * 私有构造
     */
    private WeaponDict()
    {
        dictionary = new FastMap<Integer, Weapon>();
    }

    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static WeaponDict getInstance ()
    {
        if (null == instance)
        {
            instance = new WeaponDict();
        }

        return instance;
    }

    /**
     * 根据编号获取武器原型
     * 
     * @param _weaponID 武器编号
     * @return 武器原型
     */
    public Weapon getWeapon (int _weaponID)
    {
        return dictionary.get(_weaponID);
    }

    /**
     * 加载武器模板对象
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
                        Weapon weapon = new Weapon();
                        outputStreamTool.reset();

                        try
                        {
                            weapon.setWearBodyPart(EBodyPartOfEquipment.WEAPON);
                            weapon.setID(Integer.parseInt(subE
                                    .elementTextTrim("id")));
                            weapon.setName(subE.elementTextTrim("name"));
                            weapon.setWeaponType(subE.elementTextTrim("type"));
                            weapon.setTrait(subE.elementTextTrim("trait"));
                            weapon.setNeedLevel(Integer.parseInt(subE
                                    .elementTextTrim("needLevel")));

                            data = subE.elementTextTrim("existSeal");

                            if (data != null && data.equals("是"))
                            {
                                weapon.setSeal();
                            }

                            data = subE.elementTextTrim("bindType");

                            if (null != data)
                            {
                                if (data.equals("装备绑定"))
                                {
                                    weapon.setBindType(Equipment.BIND_TYPE_OF_WEAR);
                                }
                                else if (data.equals("拾取绑定"))
                                {
                                    weapon.setBindType(Equipment.BIND_TYPE_OF_PICK);
                                }
                            }

                            weapon.setPrice(Integer.parseInt(subE
                                    .elementTextTrim("price")));
                            weapon.setAttackDistance(Short.parseShort(subE
                                    .elementTextTrim("atkRange")));
                            weapon.setMaxDurabilityPoint(Integer.parseInt(subE
                                    .elementTextTrim("durability")));
                            weapon.setRepairable(subE.elementTextTrim(
                                    "repairable").equals("可以"));
                            weapon.setImmobilityTime(Float.parseFloat(subE
                                    .elementTextTrim("immobilityTime")));
                            weapon.setMinPhysicsAttack(Integer.parseInt(subE
                                    .elementTextTrim("minPhysicsAttack")));
                            weapon.setMaxPhysicsAttack(Integer.parseInt(subE
                                    .elementTextTrim("maxPhysicsAttack")));

                            String addMagic = subE.elementTextTrim("magic");

                            if (null != addMagic)
                            {
                        		weapon.setMagicDamage(EMagic.getMagic(addMagic),
                        				Integer.parseInt(subE.elementTextTrim("minDamageValue")),
                        				Integer.parseInt(subE.elementTextTrim("maxDamageValue")));
                            }

                            data = subE.elementTextTrim("skillID1");
                            if (null != data)
                            {
                                weapon.addAccessorialSkill(Integer.parseInt(data));
                            }

                            data = subE.elementTextTrim("skillID2");
                            if (null != data)
                            {
                                weapon.addAccessorialSkill(Integer.parseInt(data));
                            }

                            data = subE.elementTextTrim("skillID3");
                            if (null != data)
                            {
                                weapon.addAccessorialSkill(Integer.parseInt(data));
                            }

                            data = subE.elementTextTrim("pvpEnhanceID");
                            if (null != data)
                            {
                                weapon.setPvpEnhanceID(Integer.parseInt(data));
                            }

                            data = subE.elementTextTrim("pveEnhanceID");
                            if (null != data)
                            {
                                weapon.setPveEnhanceID(Integer.parseInt(data));
                            }

                            data = subE.elementTextTrim("defense");
                            if (null != data)
                            {
                                weapon.atribute.defense = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("hp");
                            if (null != data)
                            {
                                weapon.atribute.hp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("mp");
                            if (null != data)
                            {
                                weapon.atribute.mp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("strength");
                            if (null != data)
                            {
                                weapon.atribute.strength = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("agility");
                            if (null != data)
                            {
                                weapon.atribute.agility = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("stamina");
                            if (null != data)
                            {
                                weapon.atribute.stamina = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("inte");
                            if (null != data)
                            {
                                weapon.atribute.inte = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("spirit");
                            if (null != data)
                            {
                                weapon.atribute.spirit = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("luck");
                            if (null != data)
                            {
                                weapon.atribute.lucky = Integer.parseInt(data);
                            }
                            data = subE
                                    .elementTextTrim("physicsDeathblowLevel");
                            if (null != data)
                            {
                                weapon.atribute.physicsDeathblowLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("magicDeathblowLevel");
                            if (null != data)
                            {
                                weapon.atribute.magicDeathblowLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("hitOdds");
                            if (null != data)
                            {
                                weapon.atribute.hitLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("duckOdds");
                            if (null != data)
                            {
                                weapon.atribute.duckLevel = (Short
                                        .parseShort(data));
                            }

                            data = subE.elementTextTrim("sanctity");
                            if (null != data)
                            {
                                weapon.setMagicFastness(EMagic.SANCTITY,
                                        Integer.parseInt(data));
                            }
                            data = subE.elementTextTrim("umbra");
                            if (null != data)
                            {
                                weapon.setMagicFastness(EMagic.UMBRA, Integer
                                        .parseInt(data));
                            }
                            data = subE.elementTextTrim("fire");
                            if (null != data)
                            {
                                weapon.setMagicFastness(EMagic.FIRE, Integer
                                        .parseInt(data));
                            }
                            data = subE.elementTextTrim("water");
                            if (null != data)
                            {
                                weapon.setMagicFastness(EMagic.WATER, Integer
                                        .parseInt(data));
                            }
                            data = subE.elementTextTrim("soil");
                            if (null != data)
                            {
                                weapon.setMagicFastness(EMagic.SOIL, Integer
                                        .parseInt(data));
                            }

                            weapon.setIconID(Short.parseShort(subE
                                    .elementTextTrim("miniImageID")));
                            
                            //add by zhengl ; date: 2011-01-16 ; note: 添加动画ID读取
                            data = subE.elementTextTrim("animationID");
                            if (null != data)
                            {
                            	weapon.setAnimationID(Short.parseShort(data));
                            }
                            else {
								log.info("warn:武器未填写对应动画文件项,ID:" + weapon.getID());
							}
                            
                            weapon.setImageID(Short.parseShort(subE
                                    .elementTextTrim("equipmentImageID")));
                            //add by zhengl ; date: 2010-11-23 ; note: 添加武器攻击的光影效果ID
                            data = subE.elementTextTrim("weaponLight");
                            if(null != data){
                                weapon.setLightID(Short.parseShort(data));
                            }else{
                                weapon.setLightID((short)605); //测试用
                            }
                            //add by zhengl ; date: 2011-01-17 ; note: 添加武器攻击的光影动画
                            data = subE.elementTextTrim("lightAnimation");
                            if(null != data){
                                weapon.setLightAnimation(Short.parseShort(data));
                            }else{
                                weapon.setLightAnimation((short)605); //测试用
                            }
                            if(weapon.getName().equals("古铜剑.火")){
                            	log.info("");
                            }
                            weapon.initDescription();

                            data = subE.elementTextTrim("description");

                            if (null != data)
                            {
                                if (-1 != data.indexOf("\\n"))
                                {
                                    data = data.replaceAll("\\\\n", "\n");
                                }

                                setFixPropertyBytes(weapon, outputStreamTool,
                                        data);
                                weapon.appendDescription(data);
                            }
                            else
                            {
                                setFixPropertyBytes(weapon, outputStreamTool,
                                        "");
                            }

                            dictionary.put(weapon.getID(), weapon);
//                            log.debug("load weapon end .. id= " + weapon.getID());
                        }
                        catch (Exception e)
                        {
                            log.error("加载武器数据出错，编号:" + weapon.getID(),e);
                            LogWriter.error(this, e);
                        }
                    }
                }

                outputStreamTool.close();
                outputStreamTool = null;
            }
        }
        catch (Exception e)
        {
            log.error("加载武器 errors : ", e);
        }
    }

    /**
     * 获取武器列表
     * 
     * @return
     */
    public Object[] getWeaponList ()
    {
        return dictionary.values().toArray();
    }

    /**
     * 设置固定属性字节数据
     * 
     * @param _weapon
     * @param _output
     * @param _desc
     * @throws IOException
     */
    private void setFixPropertyBytes (Weapon _weapon, YOYOOutputStream _output,
            String _desc) throws IOException
    {
        _output.reset();

        _output.writeByte(EquipmentPropertyDefine.WEAPON_TYPE);
        _output.writeShort(_weapon.getWeaponType().getID());
        
        //add by zhengl; date: 2011-02-18; note: 添加武器部位
        _output.writeByte(EquipmentPropertyDefine.WEAR_BODYPART);
        _output.writeShort(EBodyPartOfEquipment.WEAPON.value());

        _output.writeByte(EquipmentPropertyDefine.NEED_LVL);
        _output.writeShort(_weapon.getNeedLevel());

        _output.writeByte(EquipmentPropertyDefine.TRAIT);
        _output.writeShort(_weapon.getTrait().value());

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

        MagicDamage magicMamage = _weapon.getMagicDamage();

        if (null != magicMamage)
        {
            switch (magicMamage.magic)
            {
	            case ALL:
	            {
	            	//add:	zhengl
	            	//date:	2011-03-04
	            	//note:	添加全魔法伤害的客户端UI展示.
	                _output.writeByte(EquipmentPropertyDefine.MIN_ALL_MAGIC_HARM);
	                _output.writeShort(magicMamage.minDamageValue);
	                _output.writeByte(EquipmentPropertyDefine.MAX_ALL_MAGIC_HARM);
	                _output.writeShort(magicMamage.maxDamageValue);
	
	                break;
	            }
                case SANCTITY:
                {
                    _output.writeByte(EquipmentPropertyDefine.MIN_SANCTITY_HARM);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte(EquipmentPropertyDefine.MAX_SANCTITY_HARM);
                    _output.writeShort(magicMamage.maxDamageValue);

                    break;
                }
                case UMBRA:
                {
                    _output.writeByte(EquipmentPropertyDefine.MIN_UMBRA_HARM);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte(EquipmentPropertyDefine.MAX_UMBRA_HARM);
                    _output.writeShort(magicMamage.maxDamageValue);

                    break;
                }
                case FIRE:
                {
                    _output.writeByte(EquipmentPropertyDefine.MIN_FIRE_HARM);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte(EquipmentPropertyDefine.MAX_FIRE_HARM);
                    _output.writeShort(magicMamage.maxDamageValue);

                    break;
                }
                case WATER:
                {
                    _output.writeByte(EquipmentPropertyDefine.MIN_WATER_HARM);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte(EquipmentPropertyDefine.MAX_WATER_HARM);
                    _output.writeShort(magicMamage.maxDamageValue);

                    break;
                }
                case SOIL:
                {
                    _output.writeByte(EquipmentPropertyDefine.MIN_SOIL_HARM);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte(EquipmentPropertyDefine.MAX_SOIL_HARM);
                    _output.writeShort(magicMamage.maxDamageValue);

                    break;
                }
            }
        }

        MagicFastnessList mfl = _weapon.getMagicFastnessList();

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
        _weapon.setFixPropertyBytes(_output.getBytes());
    }
}
