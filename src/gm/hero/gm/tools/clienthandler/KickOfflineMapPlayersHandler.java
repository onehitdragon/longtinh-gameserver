//package hero.gm.tools.clienthandler;
//
//import hero.map.Map;
//import hero.map.service.MapServiceImpl;
//import hero.player.HeroPlayer;
//import hero.share.service.ME2ObjectList;
//
//import java.io.IOException;
//
//import me2.service.basic.session.SessionServiceImpl;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.KickMapOutlineResponse;
//
//public class KickOfflineMapPlayersHandler extends Handler
//{
//
//    public KickOfflineMapPlayersHandler()
//    {
//        super(GMProtocolID.REQUEST_KICK_MAP_OFFLINE);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new KickOfflineMapPlayersHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        int mapID = messageDataInputStream.readInt();
//
//        Map map = MapServiceImpl.getInstance().getNormalMapByID((short) mapID);
//        ME2ObjectList list = map.getPlayerList();
//        int num = 0;
//        HeroPlayer player = null;
//        synchronized (list)
//        {
//            for (int i = 0; i < list.size();)
//            {
//                player = (HeroPlayer) list.get(i);
//                if (player != null && player.isEnable())
//                {
//                    SessionServiceImpl.getInstance().fireSessionFree(
//                            ((HeroPlayer) player).getSessionID());
//                    ++num;
//                }
//                else
//                {
//                    i++;
//                }
//            }
//        }
//
//        Broadcast.getInstance().send(message.getSessionID(),
//                new KickMapOutlineResponse(_sid, num));
//    }
//
//}
