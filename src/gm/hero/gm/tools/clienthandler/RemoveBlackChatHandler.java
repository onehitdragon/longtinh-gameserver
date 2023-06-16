//package hero.gm.tools.clienthandler;
//
//import hero.gm.EBlackType;
//import hero.gm.service.GmBlackListManager;
//import hero.gm.service.GmDAO;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.BlackType;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.RemoveFromChatBlackListMessage;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 RemoveBlackChatHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午15:03:56
// * @描述 ：处理上行报文ID(short):122(请求从聊天黑名单中移除)
// */
//public class RemoveBlackChatHandler extends Handler
//{
//
//    public RemoveBlackChatHandler()
//    {
//        super(GMProtocolID.REQUEST_REMOVE_FROM_CHAT_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new RemoveBlackChatHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String nickname = messageDataInputStream.readUTF();
//        byte keeptype = messageDataInputStream.readByte();
//
//        boolean flag = GmDAO.deleteBlackChat(nickname);
//
//        if (flag)
//        {
//            GmBlackListManager.getInstance().getContainer(EBlackType.ROLE_CHAT)
//                    .remove(nickname);
//
//            Broadcast.getInstance().send(
//                    this.message.getSessionID(),
//                    new RemoveFromChatBlackListMessage(_sid, nickname,
//                            BlackType.getBlackType(keeptype)));
//        }
//    }
//
//}
