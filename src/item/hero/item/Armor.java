package hero.item;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;

import hero.item.dictionary.SuiteEquipmentDataDict;
import hero.item.dictionary.SuiteEquipmentDataDict.SuiteEData;
import hero.share.CharacterDefine;
import hero.share.EMagic;
import hero.share.EVocation;
import hero.share.EquipmentPropertyDefine;
import hero.share.MagicFastnessList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Armor.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-6 下午01:54:03
 * @描述 ：防具：包括帽子、衣服、手套、鞋子、腰带、腰带、戒指、项链
 */

public class Armor extends Equipment
{
    /**
     * @author DC 防具类型枚举
     */
    public static enum ArmorType
    {
        /**
         * 布甲
         */
        BU_JIA(1, "布甲"),
        /**
         * 轻甲
         */
        QING_JIA(2, "轻甲"),
        /**
         * 重甲
         */
        ZHONG_JIA(3, "重甲"),
        /**
         * 戒指
         */
        RING(4, "戒指"),
        /**
         * 项链
         */
        NECKLACE(5, "项链"),
        /**
         * 腰带
         */
        BRACELETE(6, "腰带");

        /**
         * 类型
         */
        private int    typeValue;

        /**
         * 描述
         */
        private String desc;

        /**
         * 构造
         * 
         * @param _desc
         */
        ArmorType(int _typeValue, String _desc)
        {
            typeValue = _typeValue;
            desc = _desc;
        }

        /**
         * 获取类型值
         * 
         * @return
         */
        public int getTypeValue ()
        {
            return typeValue;
        }

        /**
         * 获取描述
         * 
         * @return
         */
        public String getDesc ()
        {
            return desc;
        }
    }

    /**
     * 护甲类型
     */
    private ArmorType armorType;

    /**
     * 套装编号
     */
    private short     suiteID;
    
    /**
     * 该装备是否因种族差异而展示不同外观
     */
    private byte   isDistinguish;

    /**
     * 构造
     */
    public Armor()
    {
        durabilityConvertRate = Equipment.DURA_REDUCE_PARA_OF_ARMOR;
    }

    /**
     * 获取护甲类型
     * 
     * @return
     */
    public ArmorType getArmorType ()
    {
        return armorType;
    }

    /**
     * 设置护甲类型，主要是初始化护甲对象时调用
     * 
     * @param _desc
     */
    public void setArmorType (String _desc)
    {
        for (ArmorType type : ArmorType.values())
        {
            if (_desc.equals(type.getDesc()))
            {
                armorType = type;
            }
        }
    }

    /**
     * 设置套装编号
     * 
     * @param _suiteID
     */
    public void setSuiteID (short _suiteID)
    {
        suiteID = _suiteID;
    }

    /**
     * 获取套装编号
     * 
     * @return
     */
    public short getSuiteID ()
    {
        return suiteID;
    }

    @Override
    public int getEquipmentType ()
    {
        // TODO Auto-generated method stub
        return Equipment.TYPE_ARMOR;
    }

    /**
     * 初始化描述
     * 
     * @param _additionalDescription
     */
    public void initDescription ()
    {
        StringBuffer buff = new StringBuffer();

        buff.append(armorType.getDesc()).append(
                CharacterDefine.CHINESE_SPACE_ONE).append(
                getWearBodyPart().getDesc());
        buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(getNeedLevel())
                .append("级").append(CharacterDefine.CHINESE_SPACE_TWO).append(
                        getTrait().getDesc());

        if (bindType == BIND_TYPE_OF_WEAR)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("装备后绑定");
        }
        else if (bindType == BIND_TYPE_OF_PICK)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("拾取后绑定");
        }

        if (0 < atribute.stamina)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.stamina).append(" 耐力");
        }
        if (0 < atribute.inte)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.inte).append(" 智力");
        }
        if (0 < atribute.strength)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.strength).append(" 力量");
        }
        if (0 < atribute.spirit)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.spirit).append(" 精神");
        }
        if (0 < atribute.agility)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.agility).append(" 敏捷");
        }
        if (0 < atribute.lucky)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.lucky).append(" 幸运");
        }
        if (0 < atribute.hp)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.hp).append(" 生命");
        }
        if (0 < atribute.mp)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.mp).append(" 魔法");
        }

        if (0 < atribute.physicsDeathblowLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.physicsDeathblowLevel).append(" 物理爆击等级");
        }
        if (0 < atribute.magicDeathblowLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.magicDeathblowLevel).append(" 魔法爆击等级");
        }
        if (0 < atribute.hitLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.hitLevel).append(" 命中等级");
        }
        if (0 < atribute.duckLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.duckLevel).append(" 闪避等级");
        }
        if (0 < atribute.defense)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("防御")
                    .append(CharacterDefine.CHINESE_SPACE_TWO).append(
                            CharacterDefine.DESC_COMPART_CHAR).append(
                            atribute.defense);
        }

        MagicFastnessList mfl = getMagicFastnessList();
        if (null != mfl)
        {
            int value = mfl.getEMagicFastnessValue(EMagic.SANCTITY);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.SANCTITY.getName()).append(
                                "抗性");
            }
            value = mfl.getEMagicFastnessValue(EMagic.UMBRA);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.UMBRA.getName())
                        .append("抗性");
            }
            value = mfl.getEMagicFastnessValue(EMagic.FIRE);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.FIRE.getName()).append("抗性");
            }
            value = mfl.getEMagicFastnessValue(EMagic.WATER);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.WATER.getName())
                        .append("抗性");
            }

            value = mfl.getEMagicFastnessValue(EMagic.SOIL);

            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.SOIL.getName()).append("抗性");
            }
        }

        description = buff.toString();
    }

    @Override
    public boolean canBeUse (EVocation _vocation)
    {
        // TODO Auto-generated method stub
    	boolean result = false;
        switch (armorType)
        {
            case BU_JIA:
            {
                if(EVocation.FA_SHI == _vocation
                		|| EVocation.WU_YI == _vocation
                		|| EVocation.MIAO_SHOU_WU_YI == _vocation
                		|| EVocation.LING_QUAN_WU_YI == _vocation
                		|| EVocation.XIE_JI_WU_YI == _vocation
                		|| EVocation.YIN_YANG_WU_YI == _vocation
                		|| EVocation.YU_HUO_FA_SHI == _vocation
                		|| EVocation.TIAN_JI_FA_SHI == _vocation
                		|| EVocation.YAN_MO_FA_SHI == _vocation
                		|| EVocation.XUAN_MING_FA_SHI == _vocation
                		//战士组
                		|| EVocation.LI_SHI == _vocation
                        || EVocation.JIN_GANG_LI_SHI == _vocation
                        || EVocation.QING_TIAN_LI_SHI == _vocation
                        || EVocation.LUO_CHA_LI_SHI == _vocation
                        || EVocation.XIU_LUO_LI_SHI == _vocation
                        //游侠组
                        || EVocation.CHI_HOU == _vocation
                        || EVocation.LI_JIAN_CHI_HOU == _vocation
                        || EVocation.SHEN_JIAN_CHI_HOU == _vocation
                        || EVocation.XIE_REN_CHI_HOU == _vocation
                        || EVocation.GUI_YI_CHI_HOU == _vocation) 
                {
                	result = true;
                }
            }
            case QING_JIA:
            {
                if (EVocation.CHI_HOU == _vocation
                        || EVocation.LI_JIAN_CHI_HOU == _vocation
                        || EVocation.SHEN_JIAN_CHI_HOU == _vocation
                        || EVocation.XIE_REN_CHI_HOU == _vocation
                        || EVocation.GUI_YI_CHI_HOU == _vocation
                		//战士组
                		|| EVocation.LI_SHI == _vocation
                        || EVocation.JIN_GANG_LI_SHI == _vocation
                        || EVocation.QING_TIAN_LI_SHI == _vocation
                        || EVocation.LUO_CHA_LI_SHI == _vocation
                        || EVocation.XIU_LUO_LI_SHI == _vocation)
                {
                	result = true;
                }
            }
            case ZHONG_JIA:
            {
                if (EVocation.LI_SHI == _vocation
                        || EVocation.JIN_GANG_LI_SHI == _vocation
                        || EVocation.QING_TIAN_LI_SHI == _vocation
                        || EVocation.LUO_CHA_LI_SHI == _vocation
                        || EVocation.XIU_LUO_LI_SHI == _vocation)
                {
                	result = true;
                }
            }
            default:
            {
            	result = true;
            }
        }
        return result;
    }

    /**
     * 该装备是否因种族差异而展示不同外观
     * @param isDistinguish
     */
    public void setDistinguish(byte _isDistinguish) {
    	isDistinguish = _isDistinguish;
    }
    
    /**
     * 该装备是否因种族差异而展示不同外观
     * @return
     */
    public byte getDistinguish() {
    	return isDistinguish;
    }
}
