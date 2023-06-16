package hero.item;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import hero.item.Weapon.EWeaponType;
import hero.share.AccessorialOriginalAttribute;
import hero.share.CharacterDefine;
import hero.share.EMagic;
import hero.share.EVocation;
import hero.share.MagicFastnessList;
import hero.share.service.MagicDamage;

/**
 * 宠物武器(爪部)
 * @author jiaodongjie
 *
 */
public class PetWeapon extends PetEquipment
{
	private static Logger log = Logger.getLogger(PetWeapon.class);
	/**
     * 随机数生成器
     */
    private final static Random RANDOM = new Random();
    
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

	public PetWeapon()
	{
		durabilityConvertRate = Equipment.DURA_REDUCE_PARA_OF_ARMOR;
	}

	@Override
	public int getEquipmentType ()
	{
		// TODO Auto-generated method stub
		return PetEquipment.TYPE_WEAPON;
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
        	log.debug(getName());
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
		StringBuffer buff = new StringBuffer();

        // buff.append(getName()).append(CharacterDefine.DESC_NEW_LINE_CHAR);
        buff.append("武器").append(CharacterDefine.CHINESE_SPACE_ONE).append(
                getName());
        buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(getNeedLevel())
                .append("级");

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
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    magicMamage.magic.getName()).append("魔法伤害：").append(
                    magicMamage.minDamageValue).append("-").append(
                    magicMamage.maxDamageValue);
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
	public boolean canBeUse (EVocation evocation)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public short getAnimationID() {
		// TODO Auto-generated method stub
		return -1;
	}

}
