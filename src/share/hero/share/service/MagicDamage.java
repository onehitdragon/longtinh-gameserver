package hero.share.service;

import java.util.Random;

import hero.share.EMagic;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-14 下午2:54:27
 * 
 * <pre>
 *      Description:魔法伤害
 * </pre>
 */

public class MagicDamage
{
    /**
     * 魔法
     */
    public EMagic magic;

    /**
     * 最小伤害值
     */
    public int    minDamageValue;

    /**
     * 最大伤害值
     */
    public int    maxDamageValue;

    /**
     * 获取最大、最小之间的随机值
     * 
     * @return
     */
    public int getRandomDamageValue ()
    {
        return minDamageValue
                + random.nextInt(maxDamageValue - minDamageValue + 1);
    }

    private static final Random random = new Random();
}
