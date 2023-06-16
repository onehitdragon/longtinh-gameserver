package hero.npc.ai.data;

import hero.skill.ActiveSkill;
import hero.npc.Monster;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillAI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-12 下午01:15:21
 * @描述 ：
 */

public class SkillAIData
{
    /**
     * 编号
     */
    public int         id;

    /**
     * 使用次数类型
     */
    public byte        useTimesType;

    /**
     * 间隔使用的条件
     */
    public byte        intervalCondition;

    /**
     * 间隔使用时的间隔时间
     */
    public int         intervalTime;

    /**
     * 间隔使用时的生命每减少的百分比
     */
    public float       hpConsumePercent;

    /**
     * 间隔使用时的几率
     */
    public float       odds;

    /**
     * 唯一施放条件类型
     */
    public byte        onlyReleaseCondition;

    /**
     * 施放一次的战斗时间
     */
    public int         timeOfFighting;

    /**
     * 施放一次的剩余生命值百分比
     */
    public float       hpTracePercent;

    /**
     * 施放一次的剩余魔法值百分比
     */
    public float       mpTracePercent;

    /**
     * 施放目标判断条件
     */
    public byte        targetSettingCondition;

    /**
     * 目标在判断条件中的次序
     */
    public byte        sequenceOfSettingCondition;

    /**
     * 施放延时
     */
    public int         releaseDelay;

    /**
     * 施放前的呐喊内容
     */
    public String      shoutContentWhenRelease;

    /**
     * 施放的技能
     */
    public ActiveSkill skill;
}
