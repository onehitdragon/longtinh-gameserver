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
//import cn.digifun.gamemanager.message.RemoveFromAccountBlackListMessage;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 RemoveBlackAccountHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午15:03:56
// * @描述 ：处理上行报文ID(short):132(请求从账号黑名单中移除)
// */
//public class RemoveBlackAccountHandler extends Handler
//{
//
//    public RemoveBlackAccountHandler()
//    {
//        super(GMProtocolID.REQUEST_REMOVE_FROM_ACCOUN_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new RemoveBlackAccountHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String account = messageDataInputStream.readUTF();
//        byte keepTime = messageDataInputStream.readByte();
//
//        boolean flag = false;
//
//        flag = GmDAO.deleteBlackAccount(account);
//
//        if (flag)
//        {
//            GmBlackListManager.getInstance().getContainer(
//                    EBlackType.ACCOUNT_LOGIN).remove(account);
//
//            Broadcast.getInstance().send(
//                    this.message.getSessionID(),
//                    new RemoveFromAccountBlackListMessage(sid, account,
//                            BlackType.getBlackType(keepTime)));
//        }
//
//    }
//
//}
