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
//import cn.digifun.gamemanager.message.AddToRoleBlackMessage;
//import cn.digifun.gamemanager.message.AddToRoleBlackResponse;
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
// * @描述 ：处理上行报文ID(short):144(添加到角色黑名单)
// */
//public class AddBlackRoleHandler extends Handler
//{
//
//    public AddBlackRoleHandler()
//    {
//        super(GMProtocolID.REQUEST_ADD_TO_ROLE_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new AddBlackRoleHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String nickname = messageDataInputStream.readUTF();
//        byte keepTime = messageDataInputStream.readByte();
//        int uid = PlayerDAO.getUserIDByName(nickname);
//        if (uid > 0)
//        {
//            Black black = GmDAO.addBlackRole(uid, nickname, keepTime);
//
//            if (null != black)
//            {
//                GmBlackListManager.getInstance().getContainer(
//                        EBlackType.ROLE_LOGIN).add(black);
//
//                Broadcast.getInstance().send(message.getSessionID(),
//                        new AddToRoleBlackResponse(_sid));
//                Broadcast.getInstance().send(message.getSessionID(),
//                        new AddToRoleBlackMessage(_sid, black));
//            }
//            else
//            {
//                Broadcast.getInstance().send(message.getSessionID(),
//                        new AddToRoleBlackResponse(_sid, "角色已在黑名单中，请勿重复添"));
//            }
//        }
//        else
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new AddToRoleBlackResponse(_sid, "该角色不存在"));
//        }
//    }
//
//}
