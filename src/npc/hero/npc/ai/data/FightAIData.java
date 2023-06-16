package hero.npc.ai.data;

import hero.npc.Monster;
import hero.npc.ai.data.SkillAIData;
import hero.npc.ai.data.SpecialAIData;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 FightAIData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-12 下午03:25:09
 * @描述 ：战斗AI，怪物直接引用唯一
 */

public class FightAIData
{
    /**
     * 编号
     */
    public int             id;

    /**
     * 特殊AI列表
     */
    public SpecialAIData[] specialAIList;

    /**
     * 原始技能AI列表
     */
    public SkillAIData[]   skillAIList;

    /**
     * 当前技能AI列表(变身后将使用与原始技能不一致的技能)
     */
    private SkillAIData[]  currentSkillAIList;

    public void think (Monster _dominator)
    {
        // TODO Auto-generated method stub

    }
}
