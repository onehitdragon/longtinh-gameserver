package hero.item.legacy;

import hero.item.Goods;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DistributeGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-16 下午02:21:53
 * @描述 ：分配物品，在一个队伍中对御制和圣器的选择，记录着归属掉落箱子、哪些人参与的分配、各自的需求随机数、开始分配时间等信息
 */

public class DistributeGoods
{
    /**
     * 物品分配编号
     */
    private static short distributeGoodsID;

    /**
     * 构造
     */
    public DistributeGoods()
    {
        if (distributeGoodsID >= 30000)
        {
            distributeGoodsID = 0;
        }

        id = ++distributeGoodsID;
    }

    /**
     * 编号，唯一
     */
    public short         id;

    /**
     * 物品
     */
    public Goods         goods;

    /**
     * 所属的箱子
     */
    public RaidPickerBox box;

    /**
     * 数量
     */
    public byte          number;

    /**
     * 已经被分配过了
     */
    public boolean       hasOperated;

    /**
     * 拾取者归属者
     */
    public int           pickerUserID;

    /**
     * 最大随机数
     */
    public int           maxRandom;

    /**
     * 分配时间
     */
    public long          distributeTime;

    /**
     * 已经参与分配的人数
     */
    public int           partnerNumber;

    /**
     * 分配选择
     * 
     * @param _playerUserID
     * @param _random
     * @return
     */
    public int distribute (int _playerUserID, int _random)
    {
        if (_random > maxRandom)
        {
            maxRandom = _random;
            pickerUserID = _playerUserID;
        }

        partnerNumber++;

        if (partnerNumber == box.getVisitorList().size())
        {
            hasOperated = true;
        }

        return _random;
    }
}
