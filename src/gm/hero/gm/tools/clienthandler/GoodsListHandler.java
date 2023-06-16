//package hero.gm.tools.clienthandler;
//
//import hero.item.Armor;
//import hero.item.Material;
//import hero.item.Medicament;
//import hero.item.SpecialGoods;
//import hero.item.Weapon;
//import hero.item.dictionary.ArmorDict;
//import hero.item.dictionary.MaterialDict;
//import hero.item.dictionary.MedicamentDict;
//import hero.item.dictionary.SpecialGoodsDict;
//import hero.item.dictionary.WeaponDict;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.core.Item;
//import cn.digifun.gamemanager.core.ItemList;
//import cn.digifun.gamemanager.message.ItemListSelResponse;
//import edu.emory.mathcs.backport.java.util.Collections;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 KickOfflineHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午14:33:56
// * @描述 ：上行报文ID(short):160(物品列表查询)
// */
//public class GoodsListHandler extends Handler
//{
//    public GoodsListHandler()
//    {
//        super(GMProtocolID.REQUEST_ITEM_LIST_SEL);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new GoodsListHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        ItemList[] items = new ItemList[5]; // 根据物品类型划分
//
//        int color = 0x000000;
//
//        // 武器
//        {
//            List<Item> list = new ArrayList<Item>();
//            Object[] objects = WeaponDict.getInstance().getWeaponList();
//            for (Object o : objects)
//            {
//                if (o instanceof Weapon)
//                {
//                    Weapon a = (Weapon) o;
//                    color = a.getTrait().getViewRGB();
//                    list.add(new Item(a.getID(), a.getName(), a
//                            .getDescription(), color));
//                }
//            }
//            items[0] = new ItemList("武器", list);
//        }
//
//        // 装备
//        {
//            List<Item> list = new ArrayList<Item>();
//            Object[] objects = ArmorDict.getInstance().getArmorList();
//            for (Object o : objects)
//            {
//                if (o instanceof Armor)
//                {
//                    Armor a = (Armor) o;
//                    color = a.getTrait().getViewRGB();
//                    list.add(new Item(a.getID(), a.getName(), a
//                            .getDescription(), color));
//                }
//            }
//            Collections.sort(list, new GoodsComparator());
//            items[1] = new ItemList("装备", list);
//        }
//
//        // 材料
//        {
//            List<Item> list = new ArrayList<Item>();
//            Object[] objects = MaterialDict.getInstance().getMaterialList();
//            for (Object o : objects)
//            {
//                if (o instanceof Material)
//                {
//                    Material a = (Material) o;
//                    color = a.getTrait().getViewRGB();
//                    list.add(new Item(a.getID(), a.getName(), a
//                            .getDescription(), color));
//                }
//            }
//            Collections.sort(list, new GoodsComparator());
//            items[2] = new ItemList("材料", list);
//        }
//
//        // 药水
//        {
//            List<Item> list = new ArrayList<Item>();
//            Object[] objects = MedicamentDict.getInstance().getMedicamentList();
//            for (Object o : objects)
//            {
//                if (o instanceof Medicament)
//                {
//                    Medicament a = (Medicament) o;
//                    color = a.getTrait().getViewRGB();
//                    list.add(new Item(a.getID(), a.getName(), a
//                            .getDescription(), color));
//                }
//            }
//            Collections.sort(list, new GoodsComparator());
//            items[3] = new ItemList("药水", list);
//        }
//
//        // 特殊材料
//        {
//            List<Item> list = new ArrayList<Item>();
//            Object[] objects = SpecialGoodsDict.getInstance()
//                    .getSpecialGoodsList();
//            for (Object o : objects)
//            {
//                if (o instanceof SpecialGoods)
//                {
//                    SpecialGoods a = (SpecialGoods) o;
//                    color = a.getTrait().getViewRGB();
//                    list.add(new Item(a.getID(), a.getName(), a
//                            .getDescription(), color));
//                }
//            }
//            Collections.sort(list, new GoodsComparator());
//            items[4] = new ItemList("特殊材料", list);
//        }
//
//        Broadcast.getInstance().send(this.message.getSessionID(),
//                new ItemListSelResponse(sid, items));
//    }
//
//    class GoodsComparator implements Comparator<Item>
//    {
//        public int compare (Item t1, Item t2)
//        {
//            if (t1.id > t2.id)
//            {
//                return 1;
//            }
//            return -1;
//        }
//    }
//
//}
