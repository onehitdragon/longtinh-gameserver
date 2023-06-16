//package hero.gm.tools.clienthandler;
//
//import hero.gm.EBlackType;
//import hero.gm.service.GmBlackListManager;
//
//import java.io.IOException;
//import java.util.List;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.Black;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.ChatBlackListResponse;
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
// * @描述 ：处理上行报文ID(short):120(请求聊天黑名单列表)
// */
//public class BlackChatListHandler extends Handler
//{
//
//    public BlackChatListHandler()
//    {
//        super(GMProtocolID.REQUEST_CHAT_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new BlackChatListHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//
//        List<Black> _chatBlackList = GmBlackListManager.getInstance()
//                .getContainer(EBlackType.ROLE_CHAT).getList();
//
//        Broadcast.getInstance().send(message.getSessionID(),
//                new ChatBlackListResponse(_sid, _chatBlackList));
//    }
//
//}
