package hero.dcnbbs.message;

import hero.dcnbbs.Topic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;


public class TopicListResult extends AbsResponseMessage {

	private List<Topic> topicList = new ArrayList<Topic>();
	private short pageno = 1;
	public TopicListResult(List<Topic> topicList, short pageno) {
		this.topicList = topicList;
		this.pageno = pageno;
	}
	
	@Override
	protected void write() throws IOException {
		yos.writeShort(pageno);
		yos.writeByte(topicList.size());
		for(Topic topic : topicList) {
			yos.writeUTF(topic.getPostId());
			yos.writeUTF(topic.getCreatedByInfo());
			yos.writeUTF(topic.getTitle());
			yos.writeUTF(topic.getDateTime());
		}
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}
}
