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
//import cn.digifun.gamemanager.core.BlackType;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.RemoveFromRoleBlackListMessage;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 RemoveBlackAccountHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-04 09:03:56
// * @描述 ：处理上行报文ID(short):142(请求从角色黑名单中移除)
// */
//public class RemoveBlackRoleHandler extends Handler
//{
//
//    public RemoveBlackRoleHandler()
//    {
//        super(GMProtocolID.REQUEST_REMOVE_FROM_ROLE_BLACK_LIST);
//    }
//
//    @Override
//    protected RemoveBlackRoleHandler newInstance ()
//    {
//        return new RemoveBlackRoleHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String nickname = messageDataInputStream.readUTF();
//        byte keepTime = messageDataInputStream.readByte();
//
//        int uid = PlayerDAO.getUserIDByName(nickname);
//
//        if (uid > 0)
//        {
//            boolean flag = GmDAO.deleteBlackRole(uid);
//            if (flag)
//            {
//                GmBlackListManager.getInstance().getContainer(
//                        EBlackType.ROLE_LOGIN).remove(nickname);
//
//                Broadcast.getInstance().send(
//                        message.getSessionID(),
//                        new RemoveFromRoleBlackListMessage(sid, nickname,
//                                BlackType.getBlackType(keepTime)));
//            }
//        }
//    }
//
//}
