package hero.gm.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.chat.service.ChatServiceImpl;
import hero.gm.EResponseType;
import hero.gm.ResponseToGmTool;
import hero.gm.service.GmServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ToGmChatHandler.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-24 下午04:15:04
 * @描述 ：报文（0x5002），玩家向GM发送私聊消息
 */

public class ToGmChatHandler extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        String gmName = yis.readUTF();
        int sid = yis.readInt();
        int questionID = yis.readInt();
        String content = yis.readUTF();

        HeroPlayer speaker = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        ChatServiceImpl.getInstance().sendSinglePlayer(speaker, gmName,
                speaker, content, true);

        /**
         * 推送GM问题交互信息，插入队列
         */
        ResponseToGmTool rtgt = new ResponseToGmTool(
                EResponseType.SEND_QUEATION_EACH, 0);
        rtgt.setQuestionEach(sid, questionID, content);
        GmServiceImpl.addGmToolMsg(rtgt);

        // 聊天日志
        LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID,
                speaker.getUserID(), speaker.getName(),
                speaker.getLoginInfo().loginMsisdn, 10000, gmName, "GM私聊",
                speaker.where().getName(), content);
    }

}
