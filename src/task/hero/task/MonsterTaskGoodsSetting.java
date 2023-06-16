package hero.task;

import java.util.Random;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterTaskGoodsSetting.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-15 下午02:00:07
 * @描述 ：
 */

public class MonsterTaskGoodsSetting
{
    private static final Random random = new Random();

    /**
     * 任务物品编号
     */
    public int                  goodsID;

    /**
     * 任务编号
     */
    public int                  taskID;

    /**
     * 掉落几率
     */
    public float                odds;

    /**
     * 每次最多掉落的数量
     */
    public short                maxNumberPerTime;

    public MonsterTaskGoodsSetting(int _goodsID, int _taskID, float _odds,
            short _maxNumberPerTime)
    {
        goodsID = _goodsID;
        taskID = _taskID;
        odds = _odds;
        maxNumberPerTime = _maxNumberPerTime;
    }

    /**
     * 计算掉落数量
     * 
     * @return
     */
    public int getDropNumber ()
    {
        float randomOdds = random.nextFloat();

        if (randomOdds <= odds)
        {
            return random.nextInt(maxNumberPerTime) + 1;
        }

        return 0;
    }
}
