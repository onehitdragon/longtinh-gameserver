package hero.item.special;

import hero.item.SpecialGoods;
import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Gourd.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-8 下午01:46:44
 * @描述 ：葫芦，盛放采集的灵魂
 *  现在没有这种物品了
 */

public class Gourd extends SpecialGoods
{
    /**
     * 可以装的灵魂种类
     */
    private int animaTypeNumber;

    /**
     * 构造
     * 
     * @param _id 特殊物品编号
     */
    public Gourd(int _id, short _stackNums)
    {
        // TODO Auto-generated constructor stub
        super(_id, _stackNums);
        setMonsterTypeNumber();
    }

    @Override
    public void initDescription ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isIOGoods ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 获取灵魂种类数量
     * 
     * @return
     */
    public int getMonsterTypeNumber ()
    {
        return animaTypeNumber;
    }

    /**
     * 获取每种灵魂的最大存放数量
     * 
     * @return
     */
    public final short getAnimaMaxNumerPerType ()
    {
        return 99;
    }

    /**
     * 设置灵魂种类
     */
    private final void setMonsterTypeNumber ()
    {
        for (int[] monsterTypeNumberTable : ANIMA_TYPE_NUMBER_LIST)
        {
            if (monsterTypeNumberTable[0] == getID())
            {
                animaTypeNumber = monsterTypeNumberTable[1];
            }
        }
    }

    /**
     * 可装的灵魂种类列表
     */
    private static final int[][] ANIMA_TYPE_NUMBER_LIST = {
            {50001, 10 }, {50002, 15 }, {50003, 20 }, {50004, 25 },
            {50005, 30 }, {50006, 50 }                 };

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.GOURD;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
