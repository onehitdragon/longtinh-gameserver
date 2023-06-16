package hero.dcnbbs.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.dcnbbs.Topic;
import hero.dcnbbs.message.TopicResult;
import hero.dcnbbs.service.DCNService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;


public class ShowTopic extends AbsClientProcess {

	@Override
	public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		String topicid = yis.readUTF();
		if(topicid != null && topicid.length() > 0) {
			Topic topic = DCNService.getForum(topicid, "");
			if(topic != null) {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new TopicResult(topic));
				return;
			} else {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("该帖子无法显示。"));
				return;
			}
		}
	}
}
