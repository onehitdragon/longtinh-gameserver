package hero.skill;

import hero.skill.detail.ESkillType;
import hero.skill.detail.ETargetType;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ActiveSkill.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 上午09:08:36
 * @描述 ：主动技能基类（造成攻击和恢复的技能、驱散效果型技能）
 */

public class ActiveSkill extends Skill
{
    /**
     * 是否在非战斗状态下使用
     */
    public boolean     onlyNotFightingStatus;

    /**
     * 技能共享的冷却编号
     */
    public int         coolDownID;

    /**
     * 冷却时间（秒）
     */
    public int         coolDownTime;

    /**
     * 剩余的冷却时间（秒）
     */
    public int         reduceCoolDownTime;

    /**
     * 生命值条件判断目标类型（自己、技能目标）
     */
    public ETargetType hpConditionTargetType;

    /**
     * 生命值条件判断比较符（使用值-1（小于等于）、1（大于等于）来表示）
     */
    public byte        hpConditionCompareLine;

    /**
     * 生命值条件判断百分比
     */
    public float       hpConditionPercent;

    /**
     * 需要施放者存在的效果的名称
     */
    public String      releaserExistsEffectName;

    /**
     * 需要施放者不存在效果的名称
     */
    public String      releaserUnexistsEffectName;

    /**
     * 需要目标存在效果的名称
     */
    public String      targetExistsEffectName;

    /**
     * 需要目标不存在效果的名称
     */
    public String      targetUnexistsEffectName;

    /**
     * 构造
     * 
     * @param _id
     * @param _name
     */
    public ActiveSkill(int _id, String _name)
    {
        super(_id, _name);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Skill clone () throws CloneNotSupportedException
    {
        return (Skill) super.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.skill.Skill#getType()
     */
    public ESkillType getType ()
    {
        return ESkillType.ACTIVE;
    }

    /**
     * 减少技能的周期冷却剩余时间，每次减3秒
     */
    public void traceIntervalCDTime ()
    {
        if (reduceCoolDownTime > 0)
        {
            reduceCoolDownTime -= 3;
        }
    }

    /**
     * 使用技能时生命值比例判断标准-大于等于
     */
    public static final byte HP_COND_LINE_OF_GREATER_AND_EQUAL = 1;

    /**
     * 使用技能时生命值比例判断标准-小于
     */
    public static final byte HP_COND_LINE_OF_LESS              = -1;
}
