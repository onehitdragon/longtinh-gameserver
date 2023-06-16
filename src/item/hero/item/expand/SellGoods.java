package hero.item.expand;

import hero.item.Goods;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SellGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-5 下午01:40:55
 * @描述 ：销售物品
 */

public class SellGoods extends ExpandGoods
{
    /**
     * 出售物品对应的数量上限,如果为-1,表示无数量限制
     */
    private int originalSellGoodsNums;

    /**
     * 剩余物品数量
     */
    private int traceSellGoodsNums;

    /**
     * 构造
     * 
     * @param _goods
     */
    public SellGoods(Goods _goods)
    {
        super(_goods);
    }

    /**
     * 设置原始数量
     * 
     * @param _goodsNums
     */
    public void setOriginalSellGoodsNums (int _goodsNums)
    {
        originalSellGoodsNums = _goodsNums;
    }

    /**
     * 获取原始数量
     * 
     * @return
     */
    public int getOriginalSellGoodsNums ()
    {
        return originalSellGoodsNums;
    }

    /**
     * 设置剩余数量
     * 
     * @param _goodsNums
     */
    public void setTraceSellGoodsNums (int _goodsNums)
    {
        traceSellGoodsNums = _goodsNums;
    }

    /**
     * 获取剩余数量
     * 
     * @return
     */
    public int getTraceSellGoodsNums ()
    {
        return traceSellGoodsNums;
    }

    @Override
    public byte getType ()
    {
        // TODO Auto-generated method stub
        return ExpandGoods.SELL_GOODS;
    }
}
