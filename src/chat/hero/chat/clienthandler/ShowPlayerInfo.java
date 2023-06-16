package hero.chat.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.message.ResponseOthersWearList;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * 在聊天系统里，查看某个玩家的信息
 * @author jiaodongjie
 * 0xc02 
 */
public class ShowPlayerInfo extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		String playerName = yis.readUTF();
		HeroPlayer player_ = PlayerServiceImpl.getInstance().getPlayerByName(playerName);
		if(null != player_){
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseOthersWearList(player_.getBodyWear()));
		}else
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_CHAT_OF_NOT_TARGET, Warning.UI_STRING_TIP));
        }
	}

}
