package hero.item.expand;

import java.util.ArrayList;

import hero.item.Goods;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExchangeGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-17 上午11:14:31
 * @描述 ：交换物品
 */

public class ExchangeGoods extends ExpandGoods
{
    /**
     * 所需材料表（0:材料、1：数量）
     */
    private ArrayList<int[]> materialList;

    /**
     * 构造
     * 
     * @param _goods
     */
    public ExchangeGoods(Goods _goods)
    {
        super(_goods);
        materialList = new ArrayList<int[]>();
    }

    /**
     * 添加交换需要的材料
     * 
     * @param _goodID 材料物品编号
     * @param _number 数量
     */
    public void addExchangeMaterial (int _goodsID, int _number)
    {
        materialList.add(new int[]{_goodsID, _number });
    }

    /**
     * 获取兑换物品需要材料列表
     * 
     * @return
     */
    public ArrayList<int[]> getMaterialList ()
    {
        return materialList;
    }

    @Override
    public byte getType ()
    {
        // TODO Auto-generated method stub
        return ExpandGoods.EXCHANGE_GOODS;
    }
}
