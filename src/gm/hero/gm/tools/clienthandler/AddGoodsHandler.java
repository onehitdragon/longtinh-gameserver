//package hero.gm.tools.clienthandler;
//
//import hero.item.EquipmentInstance;
//import hero.item.Goods;
//import hero.item.detail.EGoodsType;
//import hero.item.dictionary.GoodsContents;
//import hero.item.service.GoodsServiceImpl;
//import hero.log.service.LogServiceImpl;
//import hero.npc.function.system.PostBox;
//import hero.npc.function.system.postbox.Mail;
//import hero.npc.function.system.postbox.MailService;
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerDAO;
//import hero.player.service.PlayerServiceImpl;
//import hero.share.message.MailStatusChanges;
//import hero.share.message.Warning;
//
//import java.io.IOException;
//
//import me2.core.queue.OutMsgQ;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.AddItemToRoleResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 ChangeAccountGMHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午13:37:56
// * @描述 ：处理上行报文ID(short):165(给玩家添加物品)
// */
//public class AddGoodsHandler extends Handler
//{
//
//    public AddGoodsHandler()
//    {
//        super(GMProtocolID.REQUEST_ADD_ITEM_TO_ROLE);
//    }
//
//    @Override
//    protected AddGoodsHandler newInstance ()
//    {
//        return new AddGoodsHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String nickname = messageDataInputStream.readUTF();
//        int itemID = messageDataInputStream.readInt();
//        short itemNum = messageDataInputStream.readShort();
//
//        Goods goods = GoodsContents.getGoods(itemID);
//
//        if (goods != null)
//        {
//            HeroPlayer player = PlayerServiceImpl.getInstance()
//                    .getPlayerByName(nickname);
//
//            if (null != player) // 不在线
//            {
//                // Add or remove Goods
//                Mail mail = null;
//
//                if (goods.getGoodsType() == EGoodsType.EQUIPMENT)
//                {
//                    EquipmentInstance ei = GoodsServiceImpl
//                            .buildEquipmentInstance(player.getUserID(), itemID);
//                    if (ei != null)
//                    {
//                        mail = new Mail(MailService.getInstance()
//                                .getUseableMailID(), player.getUserID(),
//                                nickname, "系统", Mail.TYPE_OF_EQUIPMENT, itemID,
//                                itemNum, ei);
//                    }
//                }
//                else
//                {
//                    mail = new Mail(MailService.getInstance()
//                            .getUseableMailID(), player.getUserID(), nickname,
//                            "系统", Mail.TYPE_OF_SINGLE_GOODS, itemID, itemNum,
//                            null);
//                }
//
//                if (mail != null)
//                {
//                    MailService.getInstance().addMail(mail, true);
//
//                    // 邮件发送日志
//                    LogServiceImpl.getInstance().mailLog(
//                            0,
//                            0,
//                            "系统",
//                            "",
//                            mail.getID(),
//                            player.getUserID(),
//                            "系统",
//                            0,
//                            0,
//                            goods.getID() + "," + goods.getName() + ","
//                                    + itemNum);
//
//                    if (player.isEnable())
//                    {
//                        OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                                new Warning(PostBox.GET_NEW_MAIL));
//
//                        OutMsgQ.getInstance().put(
//                                player.getMsgQueueIndex(),
//                                new MailStatusChanges(
//                                        MailStatusChanges.TYPE_OF_POST_BOX,
//                                        true));
//                    }
//                }
//
//                Broadcast.getInstance().send(message.getSessionID(),
//                        new AddItemToRoleResponse(sid));
//            }
//            else
//            {
//                int userID = PlayerDAO.getUserIDByName(nickname);
//
//                if (userID == 0)
//                {
//                    Broadcast.getInstance().send(message.getSessionID(),
//                            new AddItemToRoleResponse(sid, "添加物品失败，玩家不存在"));
//                }
//                else
//                {
//                    Mail mail = null;
//
//                    if (goods.getGoodsType() == EGoodsType.EQUIPMENT)
//                    {
//                        EquipmentInstance ei = GoodsServiceImpl
//                                .buildEquipmentInstance(userID, itemID);
//                        if (ei != null)
//                        {
//                            mail = new Mail(MailService.getInstance()
//                                    .getUseableMailID(), userID, nickname,
//                                    "系统", Mail.TYPE_OF_EQUIPMENT, itemID,
//                                    itemNum, ei);
//                        }
//                    }
//                    else
//                    {
//                        mail = new Mail(MailService.getInstance()
//                                .getUseableMailID(), userID, nickname, "系统",
//                                Mail.TYPE_OF_SINGLE_GOODS, itemID, itemNum,
//                                null);
//                    }
//
//                    if (mail != null)
//                    {
//                        MailService.getInstance().addMail(mail, true);
//
//                        Broadcast.getInstance().send(message.getSessionID(),
//                                new AddItemToRoleResponse(sid));
//
//                        // 邮件发送日志
//                        LogServiceImpl.getInstance().mailLog(
//                                0,
//                                0,
//                                "系统",
//                                "",
//                                mail.getID(),
//                                player.getUserID(),
//                                "系统",
//                                0,
//                                0,
//                                goods.getID() + "," + goods.getName() + ","
//                                        + itemNum);
//                    }
//                }
//            }
//        }
//        else
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new AddItemToRoleResponse(sid, "添加物品失败，物品不存在"));
//
//        }
//    }
//}
