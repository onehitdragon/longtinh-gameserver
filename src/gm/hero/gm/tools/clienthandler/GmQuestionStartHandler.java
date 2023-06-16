//package hero.gm.tools.clienthandler;
//
//import hero.gm.message.GmReplyResponse;
//import hero.gm.service.GmQuestionManager;
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerDAO;
//import hero.player.service.PlayerServiceImpl;
//
//import java.io.IOException;
//
//import me2.core.queue.OutMsgQ;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 ChangeAccountGMHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-11 下午11:26:22
// * @描述 ：处理上行报文ID(short):200(GM开始处理玩家提出的问题)
// */
//public class GmQuestionStartHandler extends Handler
//{
//    public GmQuestionStartHandler()
//    {
//        super(GMProtocolID.REQUEST_HANDERING_QUESTION);
//    }
//
//    @Override
//    protected GmQuestionStartHandler newInstance ()
//    {
//        return new GmQuestionStartHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        int questionID = messageDataInputStream.readInt();
//        String nickname = messageDataInputStream.readUTF();
//        String gmName = messageDataInputStream.readUTF();
//
//        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(
//                nickname);
//
//        if (null != player && player.isEnable()) // 玩家在线
//        {
//            // 提交到问题ID容器中
//            GmQuestionManager.getInstance().putQuestion(player.getUserID(),
//                    questionID);
//
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                    new GmReplyResponse(gmName, sid, questionID, "正在处理您提交的问题"));
//        }
//        else
//        // 玩家不在线
//        {
//            int userID = PlayerDAO.getUserIDByName(nickname);
//            // 提交到问题ID容器中
//            GmQuestionManager.getInstance().putQuestion(userID, questionID);
//        }
//
//    }
//
//}
