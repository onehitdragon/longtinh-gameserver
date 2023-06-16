//package hero.gm.tools.clienthandler;
//
//import hero.gm.EBlackType;
//import hero.gm.service.GmBlackListManager;
//import hero.gm.service.GmDAO;
//import hero.player.service.PlayerDAO;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.Black;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.AddToChatBlackMessage;
//import cn.digifun.gamemanager.message.AddToChatBlackResponse;
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
// * @描述 ：处理上行报文ID(short):124(添加到聊天黑名单)
// */
//public class AddBlackChatHandler extends Handler
//{
//
//    public AddBlackChatHandler()
//    {
//        super(GMProtocolID.REQUEST_ADD_TO_CHAT_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new AddBlackChatHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String nickname = messageDataInputStream.readUTF();
//        byte keepTime = messageDataInputStream.readByte();
//
//        int uid = PlayerDAO.getUserIDByName(nickname);
//        if (uid > 0)
//        {
//            Black black = GmDAO.addBlackChat(uid, nickname, keepTime);
//
//            if (null != black)
//            {
//                GmBlackListManager.getInstance().getContainer(
//                        EBlackType.ROLE_CHAT).add(black);
//
//                Broadcast.getInstance().send(this.message.getSessionID(),
//                        new AddToChatBlackResponse(_sid));
//                Broadcast.getInstance().send(this.message.getSessionID(),
//                        new AddToChatBlackMessage(_sid, black));
//            }
//            else
//                Broadcast.getInstance().send(this.message.getSessionID(),
//                        new AddToChatBlackResponse(_sid, "角色已在黑名单中，请勿重复添加"));
//        }
//        else
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new AddToChatBlackResponse(_sid, "该角色不存在"));
//        }
//    }
//
//}
