package hero.gm.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.gm.EResponseType;
import hero.gm.ResponseToGmTool;
import hero.gm.message.GmQuestionSubmitFeedback;
import hero.gm.service.GmServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GmQuestionHandler.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-24 下午03:53:33
 * @描述 ：报文（0x5001），玩家发起问题，寻求GM帮助
 */

public class GmQuestionHandler extends AbsClientProcess
{
    protected int getPriority ()
    {
        return 0;
    }

    @Override
    public void read () throws Exception
    {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte type = yis.readByte();
        String content = yis.readUTF();

//        if (IoSessionManager.getInstance().size() > 0)
//        {
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                    new Warning("已提交，GM会尽快回复您"));
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                    new GmQuestionSubmitFeedback(GmQuestionSubmitFeedback.OK));
//
//            /**
//             * 推送GM发起新问题信息，插入队列
//             */
//            ResponseToGmTool rtgt = new ResponseToGmTool(
//                    EResponseType.SEND_NEW_QUEATION, 0);
//            rtgt.setNewQuestion(player.getName(), type, content);
//            GmServiceImpl.addGmToolMsg(rtgt);
//        }
//        else
//        {
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                    new Warning("客服系统维护中，请稍后再试"));
//            OutMsgQ.getInstance()
//                    .put(
//                            player.getMsgQueueIndex(),
//                            new GmQuestionSubmitFeedback(
//                                    GmQuestionSubmitFeedback.FAIL));
//        }
    }
}
