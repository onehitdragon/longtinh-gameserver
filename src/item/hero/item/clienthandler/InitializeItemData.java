package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.message.ResponseInitializeItemData;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

public class InitializeItemData extends AbsClientProcess {

	@Override
	public void read() throws Exception {
		// TODO Auto-generated method stub
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(
				contextData.sessionID);
		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
				new ResponseInitializeItemData());
	}

}
