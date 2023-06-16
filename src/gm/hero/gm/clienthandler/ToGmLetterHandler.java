package hero.gm.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.gm.message.GmQuestionSubmitFeedback;
import hero.gm.service.GmServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * 给GM 发邮件，客户端有选择问题类型选项(type)
 * @author jiaodongjie
 * 0x5004
 */
public class ToGmLetterHandler extends AbsClientProcess
{
	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance()
        							.getPlayerBySessionID(contextData.sessionID);
		byte type = yis.readByte();//问题类型
		String content = yis.readUTF();//内容
		
		boolean succ = GmServiceImpl.addGMLetter(player.getUserID(), content, type);
		byte submit = 0;
		if(succ){
            submit = 1;
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_SHARE_MAIL_SEND_SUCCESSFULLY));
        }
		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new GmQuestionSubmitFeedback(submit));
	}

}
