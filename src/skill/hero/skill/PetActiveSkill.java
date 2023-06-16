package hero.skill;

import hero.effect.Effect;
import hero.share.EMagic;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.ESkillType;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.unit.SkillUnit;

/**
 * 宠物主动技能
 * @author jiaodongjie
 *
 */
public class PetActiveSkill extends PetSkill
{
	public PetActiveSkill(int id, String name)
	{
		super(id, name);
	}

	/**
	 * 冷却时间的公共变量
	 */
	public int coolPublicVar;
	/**
	 * 冷却时间(秒)
	 */
	public int coolDownTime;
	/**
     * 剩余的冷却时间（秒）
     */
    public int reduceCoolDownTime;
    /**
     * 最后一次使用时间
     */
    public long lastUseTime;
    /**
     * 目标类型:自身、敌方、主人
     */
    public ETargetType targetType;
    /**
     * 技能类型  物理/法术
     */
    public EActiveSkillType skillType;
    
    /**
     * 附带的必然效果单元
     */
    public Effect         addEffectUnit;
    /**
     * 消耗魔法值
     */
    public int useMp;

    /**
     * 目标范围类型
     */
    public ETargetRangeType       targetRangeType;

    /**
     * 目标范围数量
     */
    public byte                   rangeTargetNumber;
    
    /**
     * 秒
     */
    public float                  releaseTime;

    /**
     * 目标距离（格子数）
     */
    public byte                   targetDistance;

    /**
     * 范围性法术范围基准
     */
    public EAOERangeBaseLine      rangeBaseLine;

    /**
     * 范围性法术范围模式（中心模式、前方矩形模式）
     */
    public EAOERangeType          rangeMode;

    /**
     * 范围法术范围宽度
     */
    public byte                   rangeX;

    /**
     * 范围性法术范围高度
     */
    public byte                   rangeY;
    
    /**
     * 物理攻击倍数
     */
    public byte atkMult;
    
    /**
     * 物理伤害值
     */
    public int physicsHarmValue;
    
    /**
     * 法术伤害属性
     */
    public EMagic                 magicHarmType;

    /**
     * 法术伤害生命值值
     */
    public int                    magicHarmHpValue;
    /**
     * 恢复生命值
     */
    public int                    resumeHp;

    /**
     * 恢复魔法值
     */
    public int                    resumeMp;
    
    /**
     * 附带持续型效果编号
     */
    public int 					effectID;
    
    /**
     * 附带持续型效果的几率
     */
    public float                  effectOdds;
    
    /**
     * 施放动画编号
     * 图片ID
     */
    public short                  releaseAnimationID;

    /**
     * 作用动画编号
     * anuID
     */
    public short                  activeAnimationID;
    
    /**
     * 是否需要目标
     * 
     * @return
     */
    public boolean isNeedTarget ()
    {
        return targetRangeType == ETargetRangeType.SINGLE
                || rangeBaseLine == EAOERangeBaseLine.TARGET;
    }
    
	@Override
	public ESkillType getType ()
	{
		return ESkillType.ACTIVE;
	}

}
