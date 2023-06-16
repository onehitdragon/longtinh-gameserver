//package hero.gm.tools.clienthandler;
//
//import hero.chat.service.ChatServiceImpl;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 ChatSystemCastHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午14:25:56
// * @描述 ：处理上行报文ID(short):100(发送公告)
// */
//public class ChatSystemCastHandler extends Handler
//{
//
//    public ChatSystemCastHandler()
//    {
//        super(GMProtocolID.REQUEST_NOTICE);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ChatSystemCastHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        /* int sid = */messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String content = messageDataInputStream.readUTF(); // 聊天内容
//
//        ChatServiceImpl.getInstance().sendWorldBottomSys(/*
//                                                             * "[系统公告] " +
//                                                             * gmName + " : " +
//                                                             */content);
//    }
//
//}
