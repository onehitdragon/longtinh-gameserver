package hero.npc.ai.data;

import hero.npc.Monster;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialWisdom.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-17 上午11:14:12
 * @描述 ：特殊智能
 */

public abstract class SpecialWisdom
{
    /**
     * 召唤
     */
    public static final byte CALL      = 1;

    /**
     * 变身
     */
    public static final byte CHANGES   = 2;

    /**
     * 隐身
     */
    public static final byte DISAPPEAR = 3;

    /**
     * 逃跑
     */
    public static final byte RUN_AWAY  = 4;

    /**
     * 喊话
     */
    public static final byte SHOUT     = 5;

    /**
     * 获取智慧类型
     * 
     * @return
     */
    public abstract byte getType ();

    /**
     * 思考
     * 
     * @param _dominator 支配者
     */
    public abstract void think (Monster _dominator);
}
