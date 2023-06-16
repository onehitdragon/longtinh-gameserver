package hero.item.expand;

import hero.item.Goods;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExpandGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-5 上午11:29:58
 * @描述 ：物品扩展，主要用于除了物品基本信息外还携带了其他特殊信息的包装物品
 */

public abstract class ExpandGoods
{
    /**
     * 拍卖物品
     */
    public static final byte ROUP_GOODS     = 1;

    /**
     * 交换物品
     */
    public static final byte EXCHANGE_GOODS = 2;

    /**
     * 出售物品
     */
    public static final byte SELL_GOODS     = 3;

    /**
     * 物品模型
     */
    private Goods            goods;

    /**
     * 扩展描述
     */
    private String           expandDescription;

    /**
     * 获取类型
     * 
     * @return
     */
    public abstract byte getType ();

    /**
     * 构造
     * 
     * @param _goods
     */
    public ExpandGoods(Goods _goods)
    {
        goods = _goods;
    }

    /**
     * 获取物品模板
     * 
     * @return
     */
    public Goods getGoodeModel ()
    {
        return goods;
    }

    public String getExpandDesc ()
    {
        return expandDescription;
    }
}
