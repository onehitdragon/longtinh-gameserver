//package hero.gm.tools.clienthandler;
//
//import hero.gm.service.GmDAO;
//import hero.gm.service.GmServiceImpl;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.RoleAccountSelResponse;
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
// * @描述 ：处理上行报文ID(short):150(玩家账号查询，不和谐)
// */
//public class ViewAccountHandler extends Handler
//{
//
//    public ViewAccountHandler()
//    {
//        super(GMProtocolID.REQUEST_ROLE_ACCOUNT_SEL);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ViewAccountHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        byte type = messageDataInputStream.readByte();
//        String mobile = "";
//        String nickname = "";
//        String accountInfo = "";
//        int account_id = 0;
//        String[] roleInfos = new String[]{};
//
//        switch (type)
//        {
//            case 1:
//                mobile = messageDataInputStream.readUTF();
//                accountInfo = GmServiceImpl.getAccountInfoByUserName(mobile);
//                account_id = GmDAO.getAccountIDByUserName(mobile);
//                break;
//            case 2:
//                nickname = messageDataInputStream.readUTF();
//                accountInfo = GmServiceImpl.getAccountInfoByRoleName(nickname);
//                account_id = GmDAO.getAccountIDByRolename(nickname);
//                break;
//            default:
//                return;
//        }
//
//        if (accountInfo.equals(""))
//        {
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new RoleAccountSelResponse(sid, "账户不存在"));
//        }
//        else
//        {
//            // 获取所有角色信息
//            roleInfos = GmServiceImpl.getRoleInfos(account_id);
//
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new RoleAccountSelResponse(sid, accountInfo, roleInfos));
//        }
//    }
//}
