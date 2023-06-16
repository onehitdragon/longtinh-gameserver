package hero.ui.data;

import hero.item.SingleGoods;
import hero.item.expand.ExpandGoods;
import hero.item.expand.SellGoods;
import hero.share.CharacterDefine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SingleGoodsListData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-5 下午02:17:17
 * @描述 ：
 */

public class SingleGoodsListData
{
	private static Entry<String, ArrayList<ExpandGoods>>[] getSortedHashtableByKey(
			Hashtable<String, ArrayList<ExpandGoods>> h) {

		Set<Entry<String, ArrayList<ExpandGoods>>> set = h.entrySet();
		Entry<String, ArrayList<ExpandGoods>>[] entries = (Entry<String, ArrayList<ExpandGoods>>[]) set
				.toArray(new Entry[set.size()]);

		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Object key1 = ((Map.Entry) arg0).getKey();
				Object key2 = ((Map.Entry) arg1).getKey();
				return ((Comparable) key1).compareTo(key2);
			}

		});
		return entries;
	}
	
    public static byte[] getData (Hashtable<String, ArrayList<ExpandGoods>> _singleGoodsList,
            int _spareGoodsType)
    {
        /**
         * edit:	zhengl
         * date:	2011-03-22
         * note:	NPC出售数据修改,增加页签下发,但是药水现在不分页签.
         * 
         */
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(GridDataTypeDefine.SYSTEM_SINGLE_GOODS_LIST);//区分是装备包,还是其他物品包
            output.writeByte(_spareGoodsType); //剩余物品的种类

            Entry<String, ArrayList<ExpandGoods>>[] set = getSortedHashtableByKey(_singleGoodsList);

            output.writeByte(set.length); //有多少个页签
            for (int i = 0; i < set.length; i++) {
            	output.writeUTF( set[i].getKey() ); //页签名字
            	ArrayList<ExpandGoods> goodsList = set[i].getValue();
            	output.writeByte(goodsList.size()); //每个页签下有多少个物品
            	int index = 0;
            	for (ExpandGoods expandGoods : goodsList) {
                    switch (expandGoods.getType())
                    {
                        case ExpandGoods.SELL_GOODS:
                        {
                            SellGoods sellGoods = (SellGoods)expandGoods;

                            if (sellGoods.getTraceSellGoodsNums() != 0)
                            {
                                SingleGoods goods = (SingleGoods) expandGoods.getGoodeModel();
                                output.writeByte(index++);// 物品在背包中的位置
                                output.writeInt(goods.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                                output.writeShort(goods.getIconID());// 物品图标
                                output.writeUTF(goods.getName());// 物品名称
                                output.writeByte(goods.getTrait().value());// 物品品质
                                output.writeShort(sellGoods.getTraceSellGoodsNums());// 物品数量
                                //add by zhengl; date: 2011-04-01; note: 下发物品价格
                                output.writeInt(goods.getSellPrice()); //物品价格
                                //add by zhengl; date: 2011-04-25; note: 物品的使用等级.
                                output.writeShort(goods.getNeedLevel());

                                if (-1 == sellGoods.getTraceSellGoodsNums())
                                {
                                    output.writeByte(goods.getMaxStackNums());// 物品可操作的最大数量
                                }
                                else
                                {
                                    output.writeByte(sellGoods
                                            .getTraceSellGoodsNums());// 物品可操作的最大数量
                                }

                                output.writeUTF(goods.getDescription()
                                        + CharacterDefine.DESC_NEW_LINE_CHAR
                                        + "价格：" + goods.getSellPrice());// 物品描述
                            }

                            break;
                        }
                    }
            	}
            }

            output.flush();
            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
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
    }
}
