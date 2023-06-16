package hero.skill;

import hero.share.EMagic;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ESkillType;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;

/**
 * 宠物被动技能
 * @author jiaodongjie
 *
 */
public class PetPassiveSkill extends PetSkill
{
	public PetPassiveSkill(int id, String name)
	{
		super(id, name);
	}

	/**
     * 运算符
     */
    public EMathCaluOperator      caluOperator;
    
    /**
     * 目标类型:自身、敌方、主人
     */
    public ETargetType targetType;
    /**
     * 目标范围类型
     */
    public ETargetRangeType       targetRangeType;
    
    /**
     * 力量
     */
    public float                  strength;

    /**
     * 敏捷
     */
    public float                  agility;

    /**
     * 耐力
     */
    public float                  stamina;

    /**
     * 智力
     */
    public float                  inte;

    /**
     * 精神
     */
    public float                  spirit;

    /**
     * 幸运
     */
    public float                  lucky;
    /**
     * 魔法值（上限）
     */
    public float                  maxMp;
    /**
     * 命中等级
     */
    public float                  hitLevel;

    /**
     * 物理暴击等级
     */
    public float                  physicsDeathblowLevel;

    /**
     * 魔法暴击等级
     */
    public float                  magicDeathblowLevel;
    
    /**
     * 物理攻击伤害
     */
    public float                  physicsAttackHarmValue;
    
    /**
     * 魔法伤害类型
     */
    public EMagic                 magicHarmType;

    /**
     * 魔法伤害改变数值
     */
    public float                  magicHarmValue;
    
    /**
     * 普通物理攻击间隔时间
     */
    public float                  physicsAttackInterval;

	@Override
	public ESkillType getType ()
	{
		return ESkillType.PASSIVE;
	}

}
