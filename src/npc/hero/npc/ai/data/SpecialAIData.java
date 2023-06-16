package hero.npc.ai.data;

import hero.npc.Monster;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialAI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-12 下午02:51:42
 * @描述 ：
 */

public class SpecialAIData
{
    /**
     * 编号
     */
    public int   id;

    /**
     * 使用次数类型
     */
    public byte  useTimesType;

    /**
     * 间隔使用的条件
     */
    public byte  intervalCondition;

    /**
     * 间隔使用时的间隔时间
     */
    public int   intervalTime;

    /**
     * 间隔使用时的生命每减少的百分比
     */
    public float hpConsumePercent;

    /**
     * 唯一施放条件类型
     */
    public byte  onlyReleaseCondition;

    /**
     * 施放一次的剩余生命值百分比
     */
    public float hpTracePercent;

    /**
     * 施放一次的剩余魔法值百分比
     */
    public float mpTracePercent;

    /**
     * 具体AI类型
     */
    public byte  specialWisdomType;

    /**
     * 具体AI编号
     */
    public int   specialWisdomID;

    public SpecialAIData()
    {

    }
}
