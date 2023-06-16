//package hero.gm.tools.clienthandler;
//
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerServiceImpl;
//
//import java.io.IOException;
//
//import me2.service.basic.session.SessionServiceImpl;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.KickOfflineResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 KickOfflineSinglePlayerHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午14:33:56
// * @描述 ：处理上行报文ID(short):115(踢玩家下线)
// */
//public class KickOfflineSinglePlayerHandler extends Handler
//{
//
//    public KickOfflineSinglePlayerHandler()
//    {
//        super(GMProtocolID.REQUEST_KICK_OFFLINE);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new KickOfflineSinglePlayerHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String nickname = messageDataInputStream.readUTF();
//        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(
//                nickname);
//        if (player != null && player.isEnable())
//        {
//            player.getLoginInfo().logoutCause = "被踢下线";
//            SessionServiceImpl.getInstance().fireSessionFree(
//                    player.getSessionID());
//
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new KickOfflineResponse(_sid));
//        }
//        else
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new KickOfflineResponse(_sid, "该玩家不在线上"));
//        }
//    }
//}
