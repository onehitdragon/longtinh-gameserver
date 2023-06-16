package hero.npc.ai;

import hero.npc.Monster;
import hero.npc.ai.data.AIDataDict;
import hero.npc.ai.data.SpecialAIData;
import hero.npc.ai.data.SpecialWisdom;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialAI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-18 下午03:47:17
 * @描述 ：
 */

public class SpecialAI
{
    /**
     * 支配者
     */
    private Monster       dominator;

    /**
     * 执行一次的技能是否执行过
     */
    private boolean       hasExecuted;

    /**
     * 上次执行时的剩余生命百分比
     */
    private float         lastTraceHpPercent;

    /**
     * 上次执行时的时间
     */
    private long          timeThatLastExecuted;

    /**
     * AI数据
     */
    private SpecialAIData data;

    /**
     * 具体特殊AI逻辑
     */
    private SpecialWisdom wisdom;

    /**
     * 构造
     * 
     * @param _data
     */
    public SpecialAI(Monster _dominator, SpecialAIData _data)
    {
        dominator = _dominator;
        data = _data;
        wisdom = AIDataDict.getInstance().getSpecialWisdom(data);
    }

    /**
     * 执行
     */
    public void execute ()
    {
        if (USE_TIMES_OF_INTERVAL == data.useTimesType)
        {
            if (data.intervalCondition == INTERVAL_CONDITION_OF_TIME)
            {
                long currentTime = System.currentTimeMillis();

                if (currentTime - timeThatLastExecuted >= data.intervalTime)
                {
                    wisdom.think(dominator);
                    timeThatLastExecuted = currentTime;
                }
            }
            else if (data.intervalCondition == INTERVAL_CONDITION_OF_HP_CONSUME)
            {
                if (lastTraceHpPercent - dominator.getHPPercent() >= data.hpConsumePercent)
                {
                    wisdom.think(dominator);
                    lastTraceHpPercent = dominator.getHPPercent();
                }
            }
            else if (data.intervalCondition == INTERVAL_CONDITION_OF_HATRED_TARGET_DIE)
            {
                wisdom.think(dominator);
            }
        }
        else
        {
            if (!hasExecuted)
            {
                if (data.onlyReleaseCondition == ONLY_CONDITION_OF_TRACE_HP)
                {
                    if (dominator.getHPPercent() <= data.hpTracePercent)
                    {
                        wisdom.think(dominator);
                        hasExecuted = true;
                    }
                }
                else if (data.onlyReleaseCondition == ONLY_CONDITION_OF_TRACE_MP)
                {
                    if (dominator.getMPPercent() <= data.mpTracePercent)
                    {
                        wisdom.think(dominator);
                        hasExecuted = true;
                    }
                }
                else
                {
                    hasExecuted = true;
                }
            }
        }
    }

    /**
     * 间隔施放类型
     */
    public static final byte USE_TIMES_OF_INTERVAL                   = 1;

    /**
     * 只施放一次类型
     */
    public static final byte USE_TIMES_OF_ONLY                       = 2;

    /**
     * 间隔施放类型的时间条件类型
     */
    public static final byte INTERVAL_CONDITION_OF_TIME              = 1;

    /**
     * 间隔施放类型的生命消耗百分比类型
     */
    public static final byte INTERVAL_CONDITION_OF_HP_CONSUME        = 2;

    /**
     * 间隔施放类型的仇恨目标死亡时
     */
    public static final byte INTERVAL_CONDITION_OF_HATRED_TARGET_DIE = 3;

    /**
     * 施放一次类型的剩余生命值比例
     */
    public static final byte ONLY_CONDITION_OF_TRACE_HP              = 1;

    /**
     * 施放一次类型的剩余魔法值比例
     */
    public static final byte ONLY_CONDITION_OF_TRACE_MP              = 2;

    /**
     * 具体AI类型－召唤
     */
    public static final byte SUB_AI_TYPE_OF_CALL                     = 1;

    /**
     * 具体AI类型－变身
     */
    public static final byte SUB_AI_TYPE_OF_CHANGES                  = 2;

    /**
     * 具体AI类型－消失
     */
    public static final byte SUB_AI_TYPE_OF_DISAPPEAR                = 3;

    /**
     * 具体AI类型－逃跑
     */
    public static final byte SUB_AI_TYPE_OF_RUN_AWAY                 = 4;

    /**
     * 具体AI类型－喊话
     */
    public static final byte SUB_AI_TYPE_OF_SHOUT                    = 5;
}
