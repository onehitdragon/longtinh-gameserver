//package hero.gm.tools.clienthandler;
//
//import hero.gm.message.GmCommitResponse;
//import hero.gm.message.GmReplyResponse;
//import hero.gm.service.GmQuestionManager;
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerDAO;
//import hero.player.service.PlayerServiceImpl;
//import hero.share.letter.Letter;
//import hero.share.letter.LetterService;
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
// * @文件 GmQuestionReplyToPlayerHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-11 14:12:22
// * @描述 ：上行报文ID(short):203(GM完成问题)
// */
//public class GmQuestionFinishHandler extends Handler
//{
//
//    public GmQuestionFinishHandler()
//    {
//        super(GMProtocolID.REQUEST_FINISH_QUESTION);
//    }
//
//    @Override
//    protected GmQuestionFinishHandler newInstance ()
//    {
//        return new GmQuestionFinishHandler();
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
//            // 从问题容器中删除
//            GmQuestionManager.getInstance().delQuestion(player.getUserID());
//
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                    new GmReplyResponse(gmName, sid, questionID, "已处理完您提交的问题"));
//
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                    new GmCommitResponse(questionID));
//        }
//        else
//        // 玩家不在线
//        {
//            int userID = PlayerDAO.getUserIDByName(nickname);
//            // 从问题容器中删除
//            GmQuestionManager.getInstance().delQuestion(userID);
//
//            // 发送信件给玩家，提示已完成
//            Letter letter = new Letter(LetterService.getInstance()
//                    .getUseableLetterID(), "问题已完成", gmName, userID, nickname,
//                    "您提交的问题已完成处理");
//            LetterService.getInstance().addNewLetter(letter);
//        }
//    }
//
//}
