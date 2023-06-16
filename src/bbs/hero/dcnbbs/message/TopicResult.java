package hero.dcnbbs.message;

import hero.dcnbbs.Topic;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class TopicResult extends AbsResponseMessage {

	private Topic topic = new Topic();
	public TopicResult(Topic topic) {
		this.topic = topic;
	}
	
	@Override
	protected void write() throws IOException {
		yos.writeUTF(topic.getPostId());
		yos.writeUTF(topic.getCreatedByInfo());
		yos.writeUTF(topic.getTitle());
		yos.writeUTF(topic.getDateTime());
		yos.writeUTF(topic.getContent() != null ? topic.getContent().replaceAll("\\[url=.*?\\].*?\\[/url\\]", "") : topic.getContent());
		yos.writeShort(topic.getReplyTopicList().size());
		for(Topic topic2 : topic.getReplyTopicList()) {
			yos.writeUTF(topic2.getCreatedByInfo());
			yos.writeUTF(topic2.getContent());
			yos.writeUTF(topic2.getDateTime());

		}
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}
}
