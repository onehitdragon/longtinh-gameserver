package hero.ui.data;

import hero.item.EqGoods;
import hero.item.Equipment;
import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.item.expand.ExpandGoods;
import hero.item.expand.SellGoods;
import hero.share.CharacterDefine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
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
 * @文件 EquipmentListData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-5 下午02:12:27
 * @描述 ：
 */

public class EquipmentListData
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
	
    public static byte[] getData (Hashtable<String, ArrayList<ExpandGoods>> _equipmentList,
            int _gridNumsOfExsitsGoods)
    {
        /**
         * edit:	zhengl
         * date:	2011-03-22
         * note:	NPC出售数据修改,增加页签下发,以区分NPC所出售的不同类型的武器,衣服
         * 			武器分为5个页签: 剑,锤,杖,匕首,弓
         * 			衣服分为3个页签: 重甲,轻甲,布甲.
         * 
         */
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(GridDataTypeDefine.SYSTEM_EQUIPMENT_LIST);
            output.writeByte(_gridNumsOfExsitsGoods); //剩余物品种类
            
            Entry<String, ArrayList<ExpandGoods>>[] set = getSortedHashtableByKey(_equipmentList);

            output.writeByte(set.length); //有多少个页签
            for (int i = 0; i < set.length; i++) {
            	output.writeUTF( set[i].getKey() ); //页签名字
            	ArrayList<ExpandGoods> goodsList = set[i].getValue();
            	output.writeByte(goodsList.size()); //每个页签下有多少个装备
            	
            	int index = 0;
            	for (ExpandGoods expandGoods : goodsList) {
                    switch (expandGoods.getType())
                    {
                        case ExpandGoods.SELL_GOODS:
                        {
                            SellGoods sellGoods = (SellGoods) expandGoods;

                            if (sellGoods.getTraceSellGoodsNums() != 0)
                            {
                                EqGoods e = (EqGoods)expandGoods.getGoodeModel();
                                output.writeByte(index++);// 物品在背包中的位置
                                output.writeInt(e.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                                output.writeShort(e.getIconID());// 物品图标
                                output.writeUTF(e.getName());// 物品名称
                                output.writeBytes(e.getFixPropertyBytes());
                                output.writeByte(0);
                                output.writeByte(0);
                                output.writeShort(e.getMaxDurabilityPoint());
                                output.writeShort(sellGoods.getTraceSellGoodsNums());// 剩余物品数量
                                output.writeByte(1);// 物品可操作的最大数量
                                output.writeInt(e.getSellPrice());// 出售价格
                            }

                            break;
                        }
                    }//end switch
				}// end foreach
			}//end for

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
