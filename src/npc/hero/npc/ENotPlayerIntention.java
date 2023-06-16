package hero.npc;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ENotPlayerIntention.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-21 上午11:35:18
 * @描述 ：NPC意图
 */

public enum ENotPlayerIntention
{
    /**
     * 静止状态
     */
    AI_INTENTION_IDLE,

    /**
     * 随机行走，扫描是否有可攻击的目标
     */
    AI_INTENTION_ACTIVE,

    /**
     * 拥有攻击目标，并攻击目标
     */
    AI_INTENTION_ATTACK,

    /**
     * 检查目标是否移动，并跟随目标
     */
    AI_INTENTION_FOLLOW,
}
