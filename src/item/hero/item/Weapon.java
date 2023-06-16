package hero.item;

import hero.share.AccessorialOriginalAttribute;
import hero.share.CharacterDefine;
import hero.share.EMagic;
import hero.share.EVocation;
import hero.share.EquipmentPropertyDefine;
import hero.share.MagicFastnessList;
import hero.share.service.MagicDamage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import yoyo.tools.YOYOOutputStream;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Weapon.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-17 下午03:07:46
 * @描述 ：任务道具
 */

public class Weapon extends Equipment
{
    private static Logger log = Logger.getLogger(Weapon.class);
    /**
     * 随机数生成器
     */
    private final static Random RANDOM = new Random();

    /**
     * @author DC 武器类型枚举
     */
    public static enum EWeaponType
    {
        /**
         * 匕首
         */
        TYPE_BISHOU("匕首", 601),
        /**
         * 锤
         */
        TYPE_CHUI("锤", 602),
        /**
         * 杖
         */
        TTYPE_ZHANG("杖", 603),
        /**
         * 弓
         */
        TYPE_GONG("弓", 604),
        /**
         * 剑
         */
        TYPE_JIAN("剑", 605),
        /**
         * 卷轴
         */
        TYPE_SHU("卷轴", 606);

        /**
         * 编号
         */
        private int    id;

        /**
         * 描述
         */
        private String description;

        /**
         * 构造
         * 
         * @param _desc 描述
         * @param _id 编号
         */
        EWeaponType(String _desc, int _id)
        {
            description = _desc;
            id = _id;
        }

        /**
         * 获取描述
         * 
         * @return
         */
        public String getDesc ()
        {
            return description;
        }

        /**
         * 获取编号
         * 
         * @return
         */
        public int getID ()
        {
            return id;
        }

        public static EWeaponType getType (String _desc)
        {
            for (EWeaponType type : EWeaponType.values())
            {
                if (type.description.equals(_desc))
                {
                    return type;
                }
            }

            return null;
        }
        /**
         * 返回适用武器集合
         * @param _desc
         * @return
         */
        public static ArrayList<EWeaponType> getTypes (String[] _desc)
        {
        	ArrayList<EWeaponType> types = new ArrayList<EWeaponType>();
            for (EWeaponType type : EWeaponType.values())
            {
            	for (int i = 0; i < _desc.length; i++) {
                    if (type.description.equals(_desc[i]))
                    {
                    	types.add(type);
                    }
				}
            }
            return types;
        }
    }

    /**
     * 武器类型
     */
    private EWeaponType        weaponType;

    /**
     * 攻击距离
     */
    private short              attackDistance;

    /**
     * 最小物理攻击力
     */
    private int                minPhysicsAttack;

    /**
     * 最大物理攻击力
     */
    private int                maxPhysicsAttack;

    /**
     * 武器携带的魔法伤害，设定为只会携带一种魔法效果
     */
    private MagicDamage        magicMamage;

    /**
     * 武器携带的技能ID列表（目前只有法术书才会有此属性）
     */
    private ArrayList<Integer> accessorialSkillList;

    /**
     * 屠魔强化数据编号（杀怪物首领）
     */
    private int                pveEnhanceID;

    /**
     * 杀戮强化数据编号（杀对立阵营玩家）
     */
    private int                pvpEnhanceID;
    
    /**
     * 该武器附带的攻击光影特效
     */
    private short              weaponLight;
    
    /**
     * 武器光影动画编号
     */
    private short              lightAnimation;

    /**
     * 构造
     */
    public Weapon()
    {
        durabilityConvertRate = Equipment.DURA_REDUCE_PARA_OF_WEAPON;
    }
    /**
     * 获取装备攻击光影动画
     * 
     * @return
     */
    public short getLightAnimation()
    {
    	return lightAnimation;
    }
    
    /**
     * 设置武器攻击光影动画
     * 
     * @return
     */
    public void setLightAnimation(short _lightAnimation)
    {
    	lightAnimation = _lightAnimation;
    }
    
    /**
     * 获取武器攻击光影特效
     * 
     * @return
     */
    public short getLightID ()
    {
        return weaponLight;
    }
    /**
     * 设置武器攻击光影特效
     * 
     * @return
     */
    public void setLightID (short _lightID)
    {
        weaponLight = _lightID;
    }

    /**
     * 获取屠魔强化数据编号
     * 
     * @return
     */
    public int getPveEnhanceID ()
    {
        return pveEnhanceID;
    }

    /**
     * 设置屠魔强化数据编号
     * 
     * @param _enhanceID 强化对象编号
     */
    public void setPveEnhanceID (int _enhanceID)
    {
        pveEnhanceID = _enhanceID;
    }

    /**
     * 获取杀戮强化编号
     * 
     * @return
     */
    public int getPvpEnhanceID ()
    {
        return pvpEnhanceID;
    }

    /**
     * 设置杀戮强化编号
     * 
     * @param _enhanceID 强化对象编号
     */
    public void setPvpEnhanceID (int _enhanceID)
    {
        pvpEnhanceID = _enhanceID;
    }

    /**
     * 设置最小物理攻击力
     * 
     * @param _minPhysicsAttack 最小物理攻击力
     */
    public void setMinPhysicsAttack (int _minPhysicsAttack)
    {
        minPhysicsAttack = _minPhysicsAttack;
    }

    /**
     * 获取最小物理攻击力
     * 
     * @return 最小物理攻击力
     */
    public int getMinPhysicsAttack ()
    {
        return minPhysicsAttack;
    }

    /**
     * 设置最大物理攻击力
     * 
     * @param _minPhysicsAttack 最大物理攻击力
     */
    public void setMaxPhysicsAttack (int _maxPhysicsAttack)
    {
        maxPhysicsAttack = _maxPhysicsAttack;
    }

    /**
     * 获取最大物理攻击力
     * 
     * @return 最大物理攻击力
     */
    public int getMaxPhysicsAttack ()
    {
        return maxPhysicsAttack;
    }

    /**
     * 武器物理攻击力，用于计算技能携带的攻击力
     * 
     * @return
     */
    public int getPhysicsAttack ()
    {
        return minPhysicsAttack
                + RANDOM.nextInt(maxPhysicsAttack - minPhysicsAttack + 1);
    }

    /**
     * 设置携带的魔法伤害
     * 
     * @param _magic 类型魔法
     * @param _minValue 最小伤害值
     * @param _maxValue 最大伤害值
     */
    public void setMagicDamage (EMagic _magic, int _minValue, int _maxValue)
    {
        if (0 > _minValue || 0 >= _maxValue)
        {
            return;
        }

        if (null == magicMamage)
        {
            magicMamage = new MagicDamage();
        }

        if (_magic == null)
        {
            log.info(getName());
        }

        magicMamage.magic = _magic;
        magicMamage.minDamageValue = _minValue;
        magicMamage.maxDamageValue = _maxValue;
    }

    /**
     * 获取魔法伤害对象
     * 
     * @return
     */
    public MagicDamage getMagicDamage ()
    {
        return magicMamage;
    }

    @Override
    public int getEquipmentType ()
    {
        // TODO Auto-generated method stub
        return Equipment.TYPE_WEAPON;
    }

    /**
     * 获取武器类型
     * 
     * @return
     */
    public EWeaponType getWeaponType ()
    {
        return weaponType;
    }

    /**
     * 设置武器类型，主要是初始化武器对象时调用
     * 
     * @param _desc 类型描述
     */
    public void setWeaponType (String _desc)
    {
        for (EWeaponType type : EWeaponType.values())
        {
            if (_desc.equals(type.getDesc()))
            {
                weaponType = type;
            }
        }
    }

    /**
     * 获取攻击距离
     * 
     * @return
     */
    public short getAttackDistance ()
    {
        return attackDistance;
    }

    /**
     * 设置攻击距离
     * 
     * @param _distance
     */
    public void setAttackDistance (short _distance)
    {
        attackDistance = _distance;

        if (_distance < 2)
        {
            attackDistance = 2;
        }
    }

    /**
     * 获取附带技能编号列表
     * 
     * @return
     */
    public ArrayList<Integer> getAccessorialSkillList ()
    {
        return accessorialSkillList;
    }

    /**
     * 添加携带的技能ID
     * 
     * @param _skillID
     */
    public void addAccessorialSkill (int _skillID)
    {
        if (0 >= _skillID)
        {
            return;
        }

        if (null == accessorialSkillList)
        {
            accessorialSkillList = new ArrayList<Integer>();
        }

        accessorialSkillList.add(_skillID);
    }

    @Override
    public void initDescription ()
    {
        // TODO Auto-generated method stub

        StringBuffer buff = new StringBuffer();

        // buff.append(getName()).append(CharacterDefine.DESC_NEW_LINE_CHAR);
        buff.append("武器").append(CharacterDefine.CHINESE_SPACE_ONE).append(
                getWeaponType().getDesc());
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

        AccessorialOriginalAttribute atr = atribute;

        if (0 < atr.stamina)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atr.stamina).append(" 耐力");
        }
        if (0 < atr.inte)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(atr.inte)
                    .append(" 智力");
        }
        if (0 < atr.strength)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atr.strength).append(" 力量");
        }
        if (0 < atr.spirit)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR)
                    .append(atr.spirit).append(" 精神");
        }
        if (0 < atr.agility)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atr.agility).append(" 敏捷");
        }
        if (0 < atr.lucky)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(atr.lucky)
                    .append(" 幸运");
        }
        if (0 < atr.hp)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(atr.hp)
                    .append(" 生命");
        }
        if (0 < atr.mp)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(atr.mp)
                    .append(" 魔法");
        }

        if (0 < atr.physicsDeathblowLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atr.physicsDeathblowLevel).append(" 物理爆击等级");
        }
        if (0 < atr.magicDeathblowLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atr.magicDeathblowLevel).append(" 魔法爆击等级");
        }
        if (0 < atr.hitLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atr.hitLevel).append(" 命中等级");
        }
        if (0 < atr.duckLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atr.duckLevel).append(" 闪避等级");
        }

        buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("攻击力").append(
                CharacterDefine.CHINESE_SPACE_ONE).append(
                CharacterDefine.DESC_COMPART_CHAR)
                .append(getMinPhysicsAttack()).append("－").append(
                        getMaxPhysicsAttack());

        if (null != magicMamage)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR)
            .append(magicMamage.magic.getName())
            .append("魔法伤害：")
            .append(magicMamage.minDamageValue)
            .append("-")
            .append(magicMamage.maxDamageValue);
        }

        if (0 < atr.defense)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("防御")
                    .append(CharacterDefine.CHINESE_SPACE_TWO).append(
                            CharacterDefine.DESC_COMPART_CHAR).append(
                            atr.defense);
        }

        buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("攻击间隔：").append(
                getImmobilityTime());
        buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("攻击距离：").append(
                getAttackDistance());

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
        switch (weaponType) {
			case TYPE_BISHOU:
			{
                if(EVocation.CHI_HOU == _vocation
                		|| EVocation.GUI_YI_CHI_HOU == _vocation
                		|| EVocation.XIE_REN_CHI_HOU == _vocation
                		|| EVocation.SHEN_JIAN_CHI_HOU == _vocation
                		|| EVocation.LI_JIAN_CHI_HOU == _vocation
                		) {
                	return true;
                } else {
                	return false;
                }
			}
			case TYPE_CHUI:
			{
                if(EVocation.LI_SHI == _vocation
                		|| EVocation.JIN_GANG_LI_SHI == _vocation
                		|| EVocation.QING_TIAN_LI_SHI == _vocation
                		|| EVocation.XIU_LUO_LI_SHI == _vocation
                		|| EVocation.LUO_CHA_LI_SHI == _vocation
                		|| EVocation.WU_YI == _vocation
                		|| EVocation.MIAO_SHOU_WU_YI == _vocation
                		|| EVocation.LING_QUAN_WU_YI == _vocation
                		|| EVocation.XIE_JI_WU_YI == _vocation
                		|| EVocation.YIN_YANG_WU_YI == _vocation
                		) {
                	return true;
                } else {
                	return false;
                }
			}
			case TTYPE_ZHANG:
			{
                if(EVocation.FA_SHI == _vocation
                		|| EVocation.YU_HUO_FA_SHI == _vocation
                		|| EVocation.TIAN_JI_FA_SHI == _vocation
                		|| EVocation.YAN_MO_FA_SHI == _vocation
                		|| EVocation.XUAN_MING_FA_SHI == _vocation
                		|| EVocation.WU_YI == _vocation
                		|| EVocation.MIAO_SHOU_WU_YI == _vocation
                		|| EVocation.LING_QUAN_WU_YI == _vocation
                		|| EVocation.XIE_JI_WU_YI == _vocation
                		|| EVocation.YIN_YANG_WU_YI == _vocation
                		) {
                	return true;
                } else {
                	return false;
                }
			}
			case TYPE_GONG:
			{
                if(EVocation.CHI_HOU == _vocation
                		|| EVocation.GUI_YI_CHI_HOU == _vocation
                		|| EVocation.XIE_REN_CHI_HOU == _vocation
                		|| EVocation.SHEN_JIAN_CHI_HOU == _vocation
                		|| EVocation.LI_JIAN_CHI_HOU == _vocation
                		) {
                	return true;
                } else {
                	return false;
                }
			}
			case TYPE_JIAN:
			{
                if(EVocation.LI_SHI == _vocation
                		|| EVocation.JIN_GANG_LI_SHI == _vocation
                		|| EVocation.QING_TIAN_LI_SHI == _vocation
                		|| EVocation.LUO_CHA_LI_SHI == _vocation
                		|| EVocation.XIU_LUO_LI_SHI == _vocation
                		|| EVocation.FA_SHI == _vocation
                		|| EVocation.YU_HUO_FA_SHI == _vocation
                		|| EVocation.TIAN_JI_FA_SHI == _vocation
                		|| EVocation.YAN_MO_FA_SHI == _vocation
                		|| EVocation.XUAN_MING_FA_SHI == _vocation
                		) {
                	return true;
                } else {
                	return false;
                }
			}
            default:
            {
                return true;
            }
		}
    }
}
