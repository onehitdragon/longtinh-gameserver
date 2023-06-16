//package hero.gm.tools.clienthandler;
//
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerServiceImpl;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javolution.util.FastList;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.core.Role;
//import cn.digifun.gamemanager.message.OnlineRoleListResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 PlayerOnlineListHander.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-02 09:22:23
// * @描述 ：上行报文ID(short):180(在线玩家列表请求)
// */
//public class GetPlayerOnlineListHander extends Handler
//{
//
//    public GetPlayerOnlineListHander()
//    {
//        super(GMProtocolID.REQUEST_ONLINE_ROLE_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new GetPlayerOnlineListHander();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//
//        List<Role> roles = new ArrayList<Role>();
//        FastList<HeroPlayer> list = PlayerServiceImpl.getInstance()
//                .getPlayerList();
//        for (HeroPlayer player : list)
//        {
//            if (player != null && player.isEnable())
//            {
//                Date date = new Date(player.loginTime);
//                String loginTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
//                        .format(date);
//
//                Role role = new Role(player.getUserID(), player.getName(),
//                        player.getClan().getDesc(), player.getVocation()
//                                .getDesc(), (int) player.getLevel(), loginTime);
//
//                roles.add(role);
//            }
//        }
//
//        Broadcast.getInstance().send(this.message.getSessionID(),
//                new OnlineRoleListResponse(sid, roles));
//
//    }
//}
