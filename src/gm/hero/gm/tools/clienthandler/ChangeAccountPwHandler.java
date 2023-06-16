//package hero.gm.tools.clienthandler;
//
//import hero.gm.service.GmDAO;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.UpdateRoleAccountPwResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 ChangeAccountPwHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午13:37:56
// * @描述 ：处理上行报文ID(short):153(修改账号密码)
// */
//public class ChangeAccountPwHandler extends Handler
//{
//
//    public ChangeAccountPwHandler()
//    {
//        super(GMProtocolID.REQUEST_UPDATE_ACCOUNT_PW);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ChangeAccountPwHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String account = messageDataInputStream.readUTF();
//        String newPassword = messageDataInputStream.readUTF();
//
//        if (GmDAO.changeAccountPassword(account, newPassword))
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new UpdateRoleAccountPwResponse(_sid));
//        }
//        else
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new UpdateRoleAccountPwResponse(_sid, "修改密码失败，账户不存在"));
//        }
//    }
//
//}
