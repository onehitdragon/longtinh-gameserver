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
//import cn.digifun.gamemanager.message.AccountBlackListResponse;
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
// * @描述 ：处理上行报文ID(short):130(得到账号黑名单列表)
// */
//public class BlackAccountListGMHandler extends Handler
//{
//
//    public BlackAccountListGMHandler()
//    {
//        super(GMProtocolID.REQUEST_ACCOUNT_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new BlackAccountListGMHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//
//        List<Black> _accoutBlackList = GmBlackListManager.getInstance()
//                .getContainer(EBlackType.ACCOUNT_LOGIN).getList();
//
//        Broadcast.getInstance().send(message.getSessionID(),
//                new AccountBlackListResponse(_sid, _accoutBlackList));
//    }
//
//}
