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
// * @文件 ChangeAccountPwHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午14:25:56
// * @描述 ：处理上行报文ID(short):101(世界频道喊话)
// */
//public class ChatWorldCastHandler extends Handler
//{
//
//    public ChatWorldCastHandler()
//    {
//        super(GMProtocolID.REQUEST_WORLD_CHAT);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ChatWorldCastHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        /* int sid = */messageDataInputStream.readInt();
//        String gmName = messageDataInputStream.readUTF(); // GM自己的名字
//        String content = messageDataInputStream.readUTF(); // 聊天内容
//
//        ChatServiceImpl.getInstance().sendWorldGM(gmName, /* "[GM] " + gmName + " : " + */
//                content);
//
//    }
//
//}
