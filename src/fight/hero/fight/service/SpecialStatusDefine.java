package hero.fight.service;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialStatusDefine.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 上午11:53:37
 * @描述 ：角色特殊状态变化定义
 */

public class SpecialStatusDefine
{
    /**
     * 恢复到正常状态
     */
    public final static byte NORMAL        = 1;

    /**
     * 消失（针对隐身类技能）
     */
    public final static byte DISAPPEAR     = 2;

    /**
     * 复活（用于表现复活的特效和消除地上的尸体）
     */
    public final static byte REVIVAL       = 3;

    /**
     * 死亡（用于弹出释放尸体确认框和表现尸体）
     */
    public final static byte DIE           = 4;

    /**
     * 禁魔
     */
    public final static byte MUTE          = 5;

    /**
     * 晕厥
     */
    public final static byte FAINT         = 6;

    /**
     * 沉睡
     */
    public final static byte SLEEP         = 7;

    /**
     * 定身
     */
    public final static byte STOP          = 8;

    /**
     * 速度变化
     */
    public final static byte SPEED_CHANGER = 9;

    /**
     * 解除禁魔
     */
    public final static byte CAN_MAGIC     = 10;

    /**
     * 清醒（从晕厥中脱离）
     */
    public final static byte SOBER         = 11;

    /**
     * 解除沉睡
     */
    public final static byte WAKE_UP       = 12;

    /**
     * 解除定身
     */
    public final static byte RUN           = 13;

    /**
     * 解除减速
     */
    public final static byte DISABLE_SLOW  = 14;
}
