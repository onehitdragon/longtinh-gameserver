package hero.ui;

import hero.item.Goods;
import hero.item.bag.SingleGoodsBag;
import hero.item.dictionary.GoodsContents;
import hero.item.expand.ExchangeGoods;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_ExchangeStuffList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-2 下午02:50:38
 * @描述 ：交换物品所需材料列表界面
 */

public class UI_ExchangeMaterialList
{
    /**
     * 获取兑换物品所需材料UI界面数据
     * 
     * @param _goodsID 兑换的物品编号
     * @param _maxExchangeNumber 背包中的材料最多可以兑换的数量
     * @param _materialList 材料表
     * @param _bag 材料背包
     * @return
     */
    public static byte[] getBytes (ExchangeGoods _exchangeGoods,
            SingleGoodsBag _materialBag)
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeInt(_exchangeGoods.getGoodeModel().getID());
            output.writeByte(_exchangeGoods.getMaterialList().size());

            Goods goods;

            for (int[] materialInfo : _exchangeGoods.getMaterialList())
            {
                goods = GoodsContents.getGoods(materialInfo[0]);

                output.writeShort(goods.getIconID());
                output.writeUTF(goods.getName());
                output.writeShort(materialInfo[1]);
                output.writeByte(goods.getTrait().value());
                output.writeShort(_materialBag.getGoodsNumber(materialInfo[0]));
            }

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }

        return null;
    }

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.EXCHANGE_MATERIAL_LIST;
    }
}
