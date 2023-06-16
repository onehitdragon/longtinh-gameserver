//package hero.gm.tools.clienthandler;
//
//import hero.player.service.PlayerServiceImpl;
//import hero.share.EVocation;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.UpdateRoleInfoResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 UpdatePlayerAttributeHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-21 12:38:16
// * @描述 ：处理上行报文ID(short):213(角色属性修改)
// */
//public class UpdatePlayerAttributeHandler extends Handler
//{
//
//    public UpdatePlayerAttributeHandler()
//    {
//        super(GMProtocolID.REQUEST_UPDATE_ROLE_INFO);
//    }
//
//    @Override
//    protected UpdatePlayerAttributeHandler newInstance ()
//    {
//        return new UpdatePlayerAttributeHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        int userID = messageDataInputStream.readInt();
//        String nickname = messageDataInputStream.readUTF();
//        byte number = messageDataInputStream.readByte();
//
//        if (userID != PlayerServiceImpl.getInstance().getUserIDByNameFromDB(nickname))
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new UpdateRoleInfoResponse(sid, "不存在该角色"));
//            return;
//        }
//        if (number != 4)
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new UpdateRoleInfoResponse(sid, "属性数量错误：" + number));
//            return;
//        }
//
//        String[] attribute = new String[4]; // 属性值
//
//        for (byte i = 0; i < number; ++i)
//        {
//            attribute[i] = messageDataInputStream.readUTF();
//        }
//
//        String failInfo = PlayerServiceImpl.getInstance().changePlayerProperty(
//                userID, Short.parseShort(attribute[0]), // 级别
//                EVocation.getVocationByDesc(attribute[1]).value(), // 职业ID
//                Integer.parseInt(attribute[2]), // 金钱
//                Integer.parseInt(attribute[3]) // 经验
//                );
//
//        if (failInfo == null)
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new UpdateRoleInfoResponse(sid));
//        }
//        else
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new UpdateRoleInfoResponse(sid, failInfo));
//        }
//    }
//}
