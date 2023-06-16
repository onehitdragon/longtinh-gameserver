package hero.task.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.task.Push;
import hero.task.message.ResponseTaskPushType;

/**
 * 暂时废弃,暂放于此,勿删.
 * @author zhengl
 *
 */
public class TaskPushGetType extends AbsClientProcess {

	@Override
	public void read() throws Exception {
        HeroPlayer player = 
        	(HeroPlayer) PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		
		//返回客户端可以计费的方式.
		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
				new ResponseTaskPushType(Push.PUSH_TYPE_COMM, 0));
	}

}
