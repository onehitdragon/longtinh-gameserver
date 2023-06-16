package hero.ui;

import hero.item.Goods;
import hero.item.bag.SingleGoodsBag;
import hero.item.expand.ExchangeGoods;
import hero.share.CharacterDefine;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_ExchangeItemList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-2 下午02:50:38
 * @描述 ：交换物品列表界面
 */

public class UI_ExchangeItemList
{
    public static byte[] getBytes (String[] _menuList,
            ArrayList<ExchangeGoods> _exchangeGoodsList,
            SingleGoodsBag _materialBag)
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeByte(_exchangeGoodsList.size());

            Goods goods;
            ArrayList<int[]> materialList;

            for (ExchangeGoods exchangeGoods : _exchangeGoodsList)
            {
                goods = exchangeGoods.getGoodeModel();

                output.writeInt(goods.getID());
                output.writeShort(goods.getIconID());
                output.writeUTF(goods.getName());
                output.writeByte(goods.getTrait().value());

                materialList = exchangeGoods.getMaterialList();

                int singleMaterialExchangeNumber, goodsMaxExchangeNumber;

                goodsMaxExchangeNumber = _materialBag
                        .getGoodsNumber(materialList.get(0)[0])
                        / materialList.get(0)[1];

                for (int i = 1; i < materialList.size(); i++)
                {
                    singleMaterialExchangeNumber = _materialBag
                            .getGoodsNumber(materialList.get(i)[0])
                            / materialList.get(i)[1];

                    if (goodsMaxExchangeNumber < singleMaterialExchangeNumber)
                    {
                        goodsMaxExchangeNumber = singleMaterialExchangeNumber;
                    }
                }

                output.writeShort(goodsMaxExchangeNumber);
                output.writeUTF("价格：" + goods.getSellPrice()
                        + CharacterDefine.DESC_NEW_LINE_CHAR
                        + goods.getDescription());
            }

            output.writeByte(_menuList.length);

            for (int i = 0; i < _menuList.length; i++)
            {
                output.writeUTF(_menuList[i]);
                output.writeByte(0);
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
        return EUIType.EXCHANGE_ITEM_LIST;
    }
}
