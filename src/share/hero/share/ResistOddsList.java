package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResistOddsList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-8 下午03:39:40
 * @描述 ： 游戏对象抵抗属性列表
 */

public class ResistOddsList
{
    /**
     * 魔法爆击
     */
    public float magicDeathblowOdds;

    /**
     * 物理爆击
     */
    public float physicsDeathblowOdds;

    /**
     * 晕厥
     */
    public float insensibleOdds;

    /**
     * 沉睡
     */
    public float sleepingOdds;

    /**
     * 禁止施放技能
     */
    public float forbidSpellOdds;

    /**
     * 定身几率（不能移动）
     */
    public float fixBodyOdds;

    /**
     * 被嘲讽几率
     */
    public float provokeOdds;

    /**
     * 清除
     */
    public void clear ()
    {
        magicDeathblowOdds = 0;
        physicsDeathblowOdds = 0;
        insensibleOdds = 0;
        sleepingOdds = 0;
        forbidSpellOdds = 0;
        fixBodyOdds = 0;
        provokeOdds = 0;
    }
}
