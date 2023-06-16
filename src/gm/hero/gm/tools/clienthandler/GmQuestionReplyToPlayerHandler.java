//package hero.gm.tools.clienthandler;
//
//import hero.gm.message.GmReplyResponse;
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerDAO;
//import hero.player.service.PlayerServiceImpl;
//import hero.share.letter.Letter;
//import hero.share.letter.LetterService;
//
//import java.io.IOException;
//
//import me2.core.queue.OutMsgQ;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.QuestionEachMessage;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 GmQuestionReplyToPlayerHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-11 11:08:22
// * @描述 ：处理上行报文ID(short):202(GM和玩家交互)
// */
//public class GmQuestionReplyToPlayerHandler extends Handler
//{
//
//    public GmQuestionReplyToPlayerHandler()
//    {
//        super(GMProtocolID.REQUEST_EACH_QUESTION);
//    }
//
//    @Override
//    protected GmQuestionReplyToPlayerHandler newInstance ()
//    {
//        return new GmQuestionReplyToPlayerHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        int questionID = messageDataInputStream.readInt();
//        String nickname = messageDataInputStream.readUTF();
//        String gmName = messageDataInputStream.readUTF();
//        String content = messageDataInputStream.readUTF();
//
//        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(
//                nickname);
//
//        if (null != player && player.isEnable())
//        {
//            // 玩家在线
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                    new GmReplyResponse(gmName, sid, questionID, content));
//        }
//        else
//        {
//            Broadcast.getInstance().send(
//                    this.message.getSessionID(),
//                    new QuestionEachMessage(sid, questionID, "玩家不在线，已通过系统信件留言给玩家"));
//
//            int userID = PlayerDAO.getUserIDByName(nickname);
//
//            Letter letter = new Letter(LetterService.getInstance()
//                    .getUseableLetterID(), "问题回复", gmName, userID, nickname,
//                    content);
//            LetterService.getInstance().addNewLetter(letter);
//        }
//    }
//}
