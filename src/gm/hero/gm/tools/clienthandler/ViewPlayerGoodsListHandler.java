//package hero.gm.tools.clienthandler;
//
//import hero.gm.service.GmDAO;
//import hero.item.Equipment;
//import hero.item.EquipmentInstance;
//import hero.item.Goods;
//import hero.item.Material;
//import hero.item.Medicament;
//import hero.item.SpecialGoods;
//import hero.item.TaskTool;
//import hero.item.dictionary.MaterialDict;
//import hero.item.dictionary.MedicamentDict;
//import hero.item.dictionary.SpecialGoodsDict;
//import hero.item.dictionary.TaskGoodsDict;
//import hero.item.service.EquipmentFactory;
//import hero.item.service.GoodsDAO;
//import hero.item.service.GoodsServiceImpl;
//import hero.npc.function.system.storage.WarehouseDict;
//import hero.npc.function.system.storage.WarehouseGoods;
//import hero.pet.Pet;
//import hero.pet.PetList;
//import hero.pet.service.PetDAO;
//import hero.pet.service.PetServiceImpl;
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerDAO;
//import hero.player.service.PlayerServiceImpl;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.core.RoleItem;
//import cn.digifun.gamemanager.message.RoleItemListResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 PlayerGoodsListHander.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午15:22:56
// * @描述 ：上行报文ID(short):162(玩家物品查询，有点杂)
// */
//public class ViewPlayerGoodsListHandler extends Handler
//{
//    public ViewPlayerGoodsListHandler()
//    {
//        super(GMProtocolID.REQUEST_ROLE_ITEM_LIST_SEL);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ViewPlayerGoodsListHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        String nickname = messageDataInputStream.readUTF();
//
//        List<RoleItem> list = new ArrayList<RoleItem>();
//
//        String name = "";
//        short number = 0;
//        String des = "";
//        String insId = "";
//        String location = "";
//        RoleItem roleItem = null;
//        int color = 0x000000;
//        byte index = 0; // 索引
//
//        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(
//                nickname);
//        if (null != player && player.isEnable())
//        {
//            /**
//             * 玩家身上
//             */
//
//            // 身上装备
//            EquipmentInstance[] array = player.getBodyWear().getEquipmentList();
//            for (int i = 0; i < array.length; i++)
//            {
//                if (null != array[i])
//                {
//                    name = array[i].getArchetype().getName();
//                    number = 1;
//                    des = array[i].getArchetype().getDescription();
//                    insId = String.valueOf(array[i].getInstanceID());
//                    color = array[i].getArchetype().getTrait().getViewRGB();
//                    location = "身上装备";
//                    roleItem = new RoleItem(name, number, des, insId, location,
//                            color);
//                    list.add(roleItem);
//                }
//            }
//
//            array = player.getInventory().getEquipmentBag().getEquipmentList();
//            for (int i = 0; i < array.length; i++)
//            {
//                if (null != array[i])
//                {
//                    name = array[i].getArchetype().getName();
//                    number = 1;
//                    des = array[i].getArchetype().getDescription();
//                    insId = String.valueOf(array[i].getInstanceID());
//                    location = "背包装备";
//                    color = array[i].getArchetype().getTrait().getViewRGB();
//                    roleItem = new RoleItem(name, number, des, insId, location,
//                            color);
//                    list.add(roleItem);
//                }
//            }
//
//            // 身上材料
//            int[][] item = player.getInventory().getMaterialBag().getAllItem();
//            for (int j = 0; j < item.length; j++)
//            {
//                int id = item[j][0];
//                int num = item[j][1];
//
//                if (item[j][0] != 0)
//                {
//                    Material m = MaterialDict.getInstance().getMaterial(id);
//                    name = m.getName();
//                    number = (short) num;
//                    des = m.getDescription();
//                    insId = String.valueOf(m.getID());
//                    location = "材料";
//                    color = m.getTrait().getViewRGB();
//                    roleItem = new RoleItem(name, number, des, insId, location,
//                            color);
//                    list.add(roleItem);
//                }
//            }
//
//            // 身上药水
//            item = player.getInventory().getMedicamentBag().getAllItem();
//            for (int j = 0; j < item.length; j++)
//            {
//                int id = item[j][0];
//                int num = item[j][1];
//                if (item[j][0] != 0)
//                {
//                    Medicament m = MedicamentDict.getInstance().getMedicament(
//                            id);
//                    name = m.getName();
//                    number = (short) num;
//                    des = m.getDescription();
//                    insId = String.valueOf(m.getID());
//                    location = "药水";
//                    color = m.getTrait().getViewRGB();
//                    roleItem = new RoleItem(name, number, des, insId, location,
//                            color);
//                    list.add(roleItem);
//                }
//            }
//
//            // 身上任务物品
//            item = player.getInventory().getTaskToolBag().getAllItem();
//            for (int j = 0; j < item.length; j++)
//            {
//                int id = item[j][0];
//                int num = item[j][1];
//                if (item[j][0] != 0)
//                {
//                    TaskTool m = TaskGoodsDict.getInstance().getTaskTool(id);
//                    name = m.getName();
//                    number = (short) num;
//                    des = m.getDescription();
//                    insId = String.valueOf(m.getID());
//                    location = "任务物品";
//                    color = m.getTrait().getViewRGB();
//                    roleItem = new RoleItem(name, number, des, insId, location,
//                            color);
//                    list.add(roleItem);
//                }
//            }
//
//            // 身上特殊物品
//            item = player.getInventory().getSpecialGoodsBag().getAllItem();
//            for (int j = 0; j < item.length; j++)
//            {
//                int id = item[j][0];
//                int num = item[j][1];
//                if (item[j][0] != 0)
//                {
//                    SpecialGoods m = SpecialGoodsDict.getInstance()
//                            .getSpecailGoods(id);
//                    name = m.getName();
//                    number = (short) num;
//                    des = m.getDescription();
//                    insId = String.valueOf(m.getID());
//                    location = "特殊物品";
//                    color = m.getTrait().getViewRGB();
//                    roleItem = new RoleItem(name, number, des, insId, location,
//                            color);
//                    list.add(roleItem);
//                }
//            }
//
//            // 宠物
//            ArrayList<Pet> petList = PetServiceImpl.getInstance().getPetList(
//                    player.getUserID());
//            if (null != petList)
//            {
//                for (Pet pet : petList)
//                {
//                    if (null != pet)
//                    {
//                        name = pet.name;
//                        number = 1;
//                        des = "宠物";
//                        insId = String.valueOf(pet.id);
//                        location = "宠物";
//                        roleItem = new RoleItem(name, number, des, insId,
//                                location, color);
//                        list.add(roleItem);
//                    }
//                }
//            }
//        }
//        else
//        {
//            /**
//             * 玩家不在线时，查询数据库获取
//             */
//            int userID = PlayerDAO.getUserIDByName(nickname);
//            list = GmDAO.getRoleItems(userID);
//
//            // 宠物
//            PetList petList = PetDAO.load(userID);
//            if (null != petList)
//            {
//                ArrayList<Pet> pl = petList.getPetList();
//                for (Pet pet : pl)
//                {
//                    if (null != pet)
//                    {
//                        name = pet.name;
//                        number = 1;
//                        des = "宠物";
//                        insId = String.valueOf(pet.id);
//                        location = "宠物";
//                        roleItem = new RoleItem(name, number, des, insId,
//                                location, color);
//                        list.add(roleItem);
//                    }
//                }
//            }
//        }
//
//        // 仓库
//        ArrayList<WarehouseGoods> storages = WarehouseDict.getInstance()
//                .getWarehouseByNickname(nickname).getGoodsList();
//        index = 0;
//        for (WarehouseGoods wg : storages)
//        {
//            if (null != wg)
//            {
//                if (wg.goodsType == 0)
//                {
//                    EquipmentInstance ei = GoodsDAO
//                            .getEquipmentInstanceFromDB(wg.goodsID);
//                    if (null != ei)
//                    {
//                        Equipment equip = EquipmentFactory.getInstance()
//                                .getEquipmentArchetype(
//                                        ei.getArchetype().getID());
//                        if (null != equip)
//                        {
//                            name = equip.getName();
//                            number = wg.goodsNum;
//                            des = equip.getDescription();
//                            insId = String.valueOf(index);
//                            location = "仓库物品";
//                            color = equip.getTrait().getViewRGB();
//                            roleItem = new RoleItem(name, number, des, insId,
//                                    location, color);
//                            list.add(roleItem);
//                        }
//                    }
//                }
//                else if (wg.goodsType == 1)
//                {
//                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
//                            wg.goodsID);
//                    if (null != goods)
//                    {
//                        name = goods.getName();
//                        number = wg.goodsNum;
//                        des = goods.getDescription();
//                        insId = String.valueOf(index);
//                        location = "仓库物品";
//                        color = goods.getTrait().getViewRGB();
//                        roleItem = new RoleItem(name, number, des, insId,
//                                location, color);
//                        list.add(roleItem);
//                    }
//                }
//            }
//
//            ++index;
//        }
//
//        if (0 == list.size())
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new RoleItemListResponse(sid, "该角色无物品"));
//        }
//        else
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new RoleItemListResponse(sid, list));
//        }
//    }
//
//}
