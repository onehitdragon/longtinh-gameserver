package hero.dcnbbs;

import java.util.List;

public class Topic {
	private String forumId;
	private String postId;
	private String mid;
	private String title;
	private String content;
	private String dateTime;
	private String createdByInfo;
	
	private List<Topic> replyTopicList;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedByInfo() {
		return createdByInfo;
	}

	public void setCreatedByInfo(String createdByInfo) {
		this.createdByInfo = createdByInfo;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public List<Topic> getReplyTopicList() {
		return replyTopicList;
	}

	public void setReplyTopicList(List<Topic> replyTopicList) {
		this.replyTopicList = replyTopicList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	} 

}
