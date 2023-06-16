//package hero.gm.tools.clienthandler;
//
//import hero.gm.service.GmDAO;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.UpdateAccountMobileResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 ChangeAccountMobileHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午13:50:56
// * @描述 ：上行报文ID(short):156(修改账号手机号)
// */
//public class ChangeAccountMobileHandler extends Handler
//{
//
//    public ChangeAccountMobileHandler()
//    {
//        super(GMProtocolID.REQUEST_UPDATE_ACCOUNT_MOBILE);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ChangeAccountMobileHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String account = messageDataInputStream.readUTF();
//        String mobile = messageDataInputStream.readUTF();
//        if (GmDAO.changeAccountMobile(account, mobile))
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new UpdateAccountMobileResponse(_sid));
//        }
//        else
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new UpdateAccountMobileResponse(_sid, "修改手机号码失败，账户不存在"));
//        }
//    }
//
//}
