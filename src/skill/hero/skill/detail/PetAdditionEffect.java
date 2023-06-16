package hero.skill.detail;

import hero.effect.Effect.EffectFeature;
import hero.share.EMagic;

/**
 * 宠物技能持续型效果
 * @author jiaodongjie
 * 暂时不做宠物持续型技能
 *  date:2010-11-3
 */
public class PetAdditionEffect
{
	/**
	 * 编号
	 */
	public int id;
	/**
	 * 名称
	 */
	public String name;
	/**
	 * 所需等级
	 */
	public int needLevel;
	
	/**
	 * 附加效果特质
	 */
	public EffectFeature additonType;
	/**
	 * 附加效果等级
	 */
	public int additionLevel;
	/**
	 * 死亡后是否消失 
	 */
	public boolean disAfterDie;
	/**
	 * 是否为增益效果
	 */
	public boolean isBuff;
	/**
	 * 替代变量
	 */
	public int replaceID;
	/**
	 * 持续时间
	 */
	public float keepTime;
	/**
	 * 伤害生命值
	 */
	public int harmHpValue;
	/**
	 * 伤害属性
	 */
	public EHarmType harmType;
	/**
     * 恢复生命值
     */
    public int                    resumeHp;

    /**
     * 恢复魔法值
     */
    public int                    resumeMp;
    /**
     * 运算符
     */
    public EMathCaluOperator      caluOperator;
    
    /**
     * 力量
     */
    public float                  strength;

    /**
     * 敏捷
     */
    public float                  agility;


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
    /**
     * 图标编号
     */
    public short iconID;
    /**
     * 描述
     */
    public String description;
}
