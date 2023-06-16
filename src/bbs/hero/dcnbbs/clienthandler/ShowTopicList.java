package hero.dcnbbs.clienthandler;

import hero.dcnbbs.Topic;
import hero.dcnbbs.message.TopicListResult;
import hero.dcnbbs.service.DCNService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

import java.util.List;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


public class ShowTopicList extends AbsClientProcess {

	@Override
	public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		String channelId = yis.readUTF();
		short pageno = yis.readShort();
		if(channelId != null && channelId.length() > 0) {
			List<Topic> topicList = DCNService.getForumList(pageno, "");
			if(topicList != null && topicList.size() > 0) {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new TopicListResult(topicList,pageno));
				return;
			} else {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("已无帖显示。"));
				return;
			}
		}
	}
}
