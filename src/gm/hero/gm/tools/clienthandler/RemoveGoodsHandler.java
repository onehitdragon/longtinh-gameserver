//package hero.gm.tools.clienthandler;
//
//import hero.item.service.GoodsDAO;
//import hero.npc.function.system.storage.WarehouseDict;
//import hero.pet.service.PetDAO;
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerDAO;
//import hero.player.service.PlayerServiceImpl;
//
//import java.io.IOException;
//
//import me2.service.basic.session.SessionServiceImpl;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.DelItemFromRoleResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 RemoveGoodsHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午15:03:57
// * @描述 ：处理上行报文ID(short):168(删除玩家指定物品，功能不完善)
// */
//public class RemoveGoodsHandler extends Handler
//{
//
//    public RemoveGoodsHandler()
//    {
//        super(GMProtocolID.REQUEST_DEL_ITEM_FROM_ROLE);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new RemoveGoodsHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String nickname = messageDataInputStream.readUTF();
//        String insID = messageDataInputStream.readUTF();
//        String location = messageDataInputStream.readUTF();
//
//        Broadcast.getInstance().send(this.message.getSessionID(),
//              new DelItemFromRoleResponse(sid,"此功能暂停使用"));
//    }
//    
////    @Override
////    protected void process () throws IOException
////    {
////        int sid = messageDataInputStream.readInt();
////        /* String gmName = */messageDataInputStream.readUTF();
////        String nickname = messageDataInputStream.readUTF();
////        String insID = messageDataInputStream.readUTF();
////        String location = messageDataInputStream.readUTF();
////
////        int uid = 0;
////        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(
////                nickname);
////
////        if (player != null && player.isEnable())
////        {
////            uid = player.getUserID();
////        }
////        else
////        {
////            uid = PlayerDAO.getUserIDByName(nickname);
////        }
////
////        // 踢玩家下线
////        if (player != null)
////        {
////            SessionServiceImpl.getInstance().fireSessionFree(
////                    player.getSessionID());
////        }
////
////        int itemInsID = -1;
////        try
////        {
////            itemInsID = Integer.parseInt(insID);
////
////        }
////        catch (Exception e)
////        {
////            e.printStackTrace();
////        }
////
////        if (itemInsID < 0)
////        {
////            Broadcast.getInstance().send(this.message.getSessionID(),
////                    new DelItemFromRoleResponse(sid, "物品不存在"));
////            return;
////        }
////
////        if (location.equals("身上装备") || location.equals("背包装备"))
////        {
////            GoodsDAO.diceEquipment(itemInsID);
////            GoodsDAO.removeEquipmentOfBag(itemInsID);
////        }
////        else if (location.equals("材料") || location.equals("药水")
////                || location.equals("任务物品") || location.equals("特殊物品"))
////        {
////            GoodsDAO.removeSingleGoodsFromBag(uid, itemInsID);
////        }
////        else if (location.equals("宠物"))
////        {
////            PetDAO.dice(uid, (short) itemInsID);
////        }
////        else if (location.equals("仓库物品"))
////        {
////            WarehouseDict.getInstance().getWarehouseByNickname(nickname)
////                    .removeWarehouseGoods((byte) itemInsID);
////        }
////
////        Broadcast.getInstance().send(this.message.getSessionID(),
////                new DelItemFromRoleResponse(sid));
////    }
//
//}
