package hero.chat.service;

import java.sql.Timestamp;

/**
 * GM公告
 * @author jiaodongjie
 *
 */
public class GmNotice
{
		private int id;
		private int severID;
		private String title;
		private String content;
		private Timestamp create_time;
		private Timestamp update_time;
		/**
		 * 开始时间
		 */
		private Timestamp startTime;
		/**
		 * 结束时间
		 */
		private Timestamp endTime;
		/**
		 * 间隔时间(毫秒)
		 */
		private int intervalTime;
		/**
		 * 公告次数
		 */
		private int times;

		public GmNotice() {
		}
		
		public int getSeverID() {
			return severID;
		}

		public void setSeverID(int severID) {
			this.severID = severID;
		}

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public Timestamp getCreate_time() {
			return create_time;
		}
		public void setCreate_time(Timestamp createTime) {
			create_time = createTime;
		}
		public Timestamp getUpdate_time() {
			return update_time;
		}
		public void setUpdate_time(Timestamp updateTime) {
			update_time = updateTime;
		}

		public Timestamp getStartTime() {
			return startTime;
		}

		public void setStartTime(Timestamp startTime) {
			this.startTime = startTime;
		}

		public Timestamp getEndTime() {
			return endTime;
		}

		public void setEndTime(Timestamp endTime) {
			this.endTime = endTime;
		}

		public int getIntervalTime() {
			return intervalTime;
		}

		public void setIntervalTime(int intervalTime) {
			this.intervalTime = intervalTime;
		}

		public int getTimes() {
			return times;
		}

		public void setTimes(int times) {
			this.times = times;
		}


}
