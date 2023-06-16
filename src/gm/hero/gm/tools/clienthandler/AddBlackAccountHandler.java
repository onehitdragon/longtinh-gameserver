//package hero.gm.tools.clienthandler;
//
//import hero.gm.EBlackType;
//import hero.gm.service.GmBlackListManager;
//import hero.gm.service.GmDAO;
//
//import java.io.IOException;
//
////import cn.digifun.gamemanager.Broadcast;
////import cn.digifun.gamemanager.Handler;
////import cn.digifun.gamemanager.core.Black;
////import cn.digifun.gamemanager.core.GMProtocolID;
////import cn.digifun.gamemanager.message.AddToAccountBlackMessage;
////import cn.digifun.gamemanager.message.AddToAccountBlackResponse;
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
// * @描述 ：处理上行报文ID(short):134(添加到账号黑名单)
// */
//public class AddBlackAccountHandler extends Handler
//{
//
//    public AddBlackAccountHandler()
//    {
//        super(GMProtocolID.REQUEST_ADD_TO_ACCOUNT_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new AddBlackAccountHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String account = messageDataInputStream.readUTF();
//        byte keepTime = messageDataInputStream.readByte(); // 禁止时效
//
//        int accountID = GmDAO.getAccountIDByUserName(account);
//
//        if (accountID > 0)
//        {
//            Black black = GmDAO.addBlackAccount(accountID, account, keepTime);
//
//            if (black != null)
//            {
//                GmBlackListManager.getInstance().getContainer(
//                        EBlackType.ACCOUNT_LOGIN).add(black);
//
//                Broadcast.getInstance().send(this.message.getSessionID(),
//                        new AddToAccountBlackResponse(sid));
//                Broadcast.getInstance().send(this.message.getSessionID(),
//                        new AddToAccountBlackMessage(sid, black));
//            }
//            else
//            {
//                Broadcast.getInstance().send(this.message.getSessionID(),
//                        new AddToAccountBlackResponse(sid, "账号已在黑名单中，请勿重复添加"));
//            }
//        }
//        else
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new AddToAccountBlackResponse(sid, "该账号不存在"));
//        }
//
//    }
//
//}
