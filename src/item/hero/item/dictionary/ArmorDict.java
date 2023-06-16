package hero.item.dictionary;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.tools.YOYOOutputStream;

import hero.item.Armor;
import hero.item.Equipment;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.dictionary.SuiteEquipmentDataDict.SuiteEData;
import hero.share.AccessorialOriginalAttribute;
import hero.share.EMagic;
import hero.share.EquipmentPropertyDefine;
import hero.share.MagicFastnessList;
import hero.share.service.LogWriter;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ArmorDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-15 下午03:54:53
 * @描述 防具字典
 */

public class ArmorDict
{
    private static Logger log = Logger.getLogger(ArmorDict.class);
    /**
     * 字典容器
     */
    FastMap<Integer, Armor>  dictionary;

    /**
     * 单例
     */
    private static ArmorDict instance;

    /**
     * 私有构造
     */
    private ArmorDict()
    {
        dictionary = new FastMap<Integer, Armor>();
    }

    /**
     * 获取单例
     * 
     * @return 字典单例
     */
    public static ArmorDict getInstance ()
    {
        if (null == instance)
        {
            instance = new ArmorDict();
        }

        return instance;
    }

    public Object[] getArmorList ()
    {
        return dictionary.values().toArray();
    }

    /**
     * 向字典中添加防具
     * 
     * @param _armor 防具
     * @return 防具
     */
    public Armor add (Armor _armor)
    {
        return dictionary.put(_armor.getID(), _armor);
    }

    /**
     * 根据编号获取防具原型
     * 
     * @param _armorID 防具编号
     * @return 防具原型
     */
    public Armor getArmor (int _armorID)
    {
        return dictionary.get(_armorID);
    }

    /**
     * 加载防具模板对象
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
                        Armor armor = new Armor();
                        outputStreamTool.reset();

                        try
                        {
                            armor.setID(Integer.parseInt(subE
                                    .elementTextTrim("id")));
                            armor.setName(subE.elementTextTrim("name"));
                            armor.setArmorType(subE.elementTextTrim("type"));
                            armor.setTrait(subE.elementTextTrim("trait"));
                            armor.setNeedLevel(Integer.parseInt(subE
                                    .elementTextTrim("needLevel")));

                            data = subE.elementTextTrim("bindType");

                            if (null != data)
                            {
                                if (data.equals("装备绑定"))
                                {
                                    armor.setBindType(Equipment.BIND_TYPE_OF_WEAR);
                                }
                                else if (data.equals("拾取绑定"))
                                {
                                    armor.setBindType(Equipment.BIND_TYPE_OF_PICK);
                                }
                                else if (data.equals("不绑定")) {
                                	armor.setBindType(Equipment.BIND_TYPE_OF_NOT);
								}
                            }

                            data = subE.elementTextTrim("existSeal");

                            if (data != null && data.equals("是"))
                            {
                                armor.setSeal();
                            }

                            data = subE.elementTextTrim("suiteID");

                            if (null != data)
                            {
                                armor.setSuiteID(Short.parseShort(data));
                            }

                            armor.setWearBodyPart(EBodyPartOfEquipment
                                    .getBodyPart(subE
                                            .elementTextTrim("bodyPart")));
                            armor.setPrice(Integer.parseInt(subE
                                    .elementTextTrim("price")));
                            armor.setMaxDurabilityPoint(Integer.parseInt(subE
                                    .elementTextTrim("durability")));
                            armor.setRepairable(subE.elementTextTrim(
                                    "repairable").equals("可以"));

                            data = subE.elementTextTrim("defense");

                            if (null != data)
                            {
                                armor.atribute.defense = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("hp");
                            if (null != data)
                            {
                                armor.atribute.hp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("mp");
                            if (null != data)
                            {
                                armor.atribute.mp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("strength");
                            if (null != data)
                            {
                                armor.atribute.strength = Integer
                                        .parseInt(data);
                            }
                            data = subE.elementTextTrim("agility");
                            if (null != data)
                            {
                                armor.atribute.agility = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("stamina");
                            if (null != data)
                            {
                                armor.atribute.stamina = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("inte");
                            if (null != data)
                            {
                                armor.atribute.inte = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("spirit");
                            if (null != data)
                            {
                                armor.atribute.spirit = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("luck");
                            if (null != data)
                            {
                                armor.atribute.lucky = Integer.parseInt(data);
                            }
                            data = subE
                                    .elementTextTrim("physicsDeathblowLevel");
                            if (null != data)
                            {
                                armor.atribute.physicsDeathblowLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("magicDeathblowLevel");
                            if (null != data)
                            {
                                armor.atribute.magicDeathblowLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("hitOdds");
                            if (null != data)
                            {
                                armor.atribute.hitLevel = (Short
                                        .parseShort(data));
                            }
                            data = subE.elementTextTrim("duckOdds");
                            if (null != data)
                            {
                                armor.atribute.duckLevel = (Short
                                        .parseShort(data));
                            }

                            data = subE.elementTextTrim("sanctity");
                            if (null != data)
                            {
                                armor.setMagicFastness(EMagic.SANCTITY, Integer
                                        .parseInt(data));
                            }
                            data = subE.elementTextTrim("umbra");
                            if (null != data)
                            {
                                armor.setMagicFastness(EMagic.UMBRA, Integer
                                        .parseInt(data));
                            }
                            data = subE.elementTextTrim("fire");
                            if (null != data)
                            {
                                armor.setMagicFastness(EMagic.FIRE, Integer
                                        .parseInt(data));
                            }
                            data = subE.elementTextTrim("water");
                            if (null != data)
                            {
                                armor.setMagicFastness(EMagic.WATER, Integer
                                        .parseInt(data));
                            }
                            data = subE.elementTextTrim("soil");
                            if (null != data)
                            {
                                armor.setMagicFastness(EMagic.SOIL, Integer
                                        .parseInt(data));
                            }

                            armor.setIconID(Short.parseShort(subE
                                    .elementTextTrim("miniImageID")));

                            //add by zhengl ; date: 2011-01-16 ; note: 添加动画ID读取
                            data = subE.elementTextTrim("animationID");
                            if (null != data)
                            {
                                armor.setAnimationID(Short.parseShort(data));
                            }
                            
                            data = subE.elementTextTrim("equipmentImageID");

                            if (null != data)
                            {
                                armor.setImageID(Short.parseShort(data));
                            }

                            armor.initDescription();

                            data = subE.elementTextTrim("description");

                            if (null != data)
                            {
                                setFixPropertyBytes(armor, outputStreamTool,
                                        data);
                                armor.appendDescription(data);
                            }
                            else
                            {
                                setFixPropertyBytes(armor, outputStreamTool, "");
                            }
                            //add by zhengl ; date: 2010-11-24 ;
                            data = subE.elementTextTrim("isDistinguish");
                            if (null != data)
                            {
                            	armor.setDistinguish(Byte.parseByte(data));
                            }
                            else
                            {
                            	if(armor.getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
                                	log.info("warn:获取装备:"+ armor.getID()
                                			+"的isDistinguish字段失败,采取默认值0");
                            	}
//                            	log.info("warn:获取装备:"+ armor.getID()
//                            			+"的isDistinguish字段失败,采取默认值0");
                            	armor.setDistinguish((byte)0);
                            }
                            dictionary.put(armor.getID(), armor);
//                            log.debug("load armor end .. id= " + armor.getID() +"  imageID = " + armor.getImageID());
                        }
                        catch (Exception e)
                        {
                            log.error("加载防具数据出错，编号:" + armor.getID(),e);
                        }
                    }
                }
            }

            outputStreamTool.close();
            outputStreamTool = null;
        }
        catch (Exception e)
        {
            log.error("加载防具 errors : ", e);
        }
    }

    /**
     * 设置防具固定属性字节数据
     * 
     * @param _armor
     * @param _output
     * @param _desc
     * @throws IOException
     */
    private void setFixPropertyBytes (Armor _armor, YOYOOutputStream _output,
            String _desc) throws IOException
    {
        _output.reset();

        _output.writeByte(EquipmentPropertyDefine.ARMOR_TYPE);
        _output.writeShort(_armor.getArmorType().getTypeValue());

        _output.writeByte(EquipmentPropertyDefine.WEAR_BODYPART);
        _output.writeShort(_armor.getWearBodyPart().value());

        _output.writeByte(EquipmentPropertyDefine.NEED_LVL);
        _output.writeShort(_armor.getNeedLevel());

        _output.writeByte(EquipmentPropertyDefine.TRAIT);
        _output.writeShort(_armor.getTrait().value());

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

        if (0 < _armor.getSuiteID())
        {
            SuiteEData suiteEData = SuiteEquipmentDataDict.getInstance()
                    .getSuiteData(_armor.getSuiteID());

            if (null != suiteEData)
            {
                _output.writeByte(EquipmentPropertyDefine.SUITE_NAME);
                _output.writeUTF(suiteEData.name);
                _output.writeByte(EquipmentPropertyDefine.SUITE_DESC);
                _output.writeUTF(suiteEData.description);
            }
        }

        _output.writeByte(-1);
        _output.writeUTF(_desc);
        _output.flush();
        //修改这...
        _armor.setFixPropertyBytes(_output.getBytes());
    }
}
