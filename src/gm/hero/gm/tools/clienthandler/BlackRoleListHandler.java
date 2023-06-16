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
//import cn.digifun.gamemanager.message.RoleBlackListResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 BlackRoleListHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-02 14:32:50
// * @描述 ：处理上行报文ID(short):140(得到角色黑名单列表)
// */
//public class BlackRoleListHandler extends Handler
//{
//
//    public BlackRoleListHandler()
//    {
//        super(GMProtocolID.REQUEST_ROLE_BLACK_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new BlackRoleListHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        List<Black> roleBlackList = GmBlackListManager.getInstance()
//                .getContainer(EBlackType.ROLE_LOGIN).getList();
//
//        Broadcast.getInstance().send(message.getSessionID(),
//                new RoleBlackListResponse(sid, roleBlackList));
//    }
//
//}
