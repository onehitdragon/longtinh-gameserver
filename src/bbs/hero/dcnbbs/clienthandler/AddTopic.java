package hero.dcnbbs.clienthandler;

import java.util.List;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.dcnbbs.Topic;
import hero.dcnbbs.message.TopicListResult;
import hero.dcnbbs.message.TopicResult;
import hero.dcnbbs.service.DCNService;
import hero.dcnbbs.service.Result;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

public class AddTopic extends AbsClientProcess {

	@Override
	public void read() throws Exception {
		
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		String topicid = yis.readUTF();
		String title = yis.readUTF();
		String content = yis.readUTF();
		short pageno = yis.readShort();
		if(player.getLoginInfo().username == null) {
			PlayerDAO.loadPlayerAccountInfo(player);
		}
		String djtk = (player.getDcndjtk() != null && player.getDcndjtk().length() > 0) ? player.getDcndjtk() : "";
		if("".equals(djtk) && player.getLoginInfo() != null && player.getLoginInfo().username.indexOf(DCNService.CHANNEL_ID + "#") > 0) {
			Result result = DCNService.login(player.getLoginInfo().username.replaceFirst(DCNService.CHANNEL_ID + "#", ""), "", player.getLoginInfo().password);
			if(result.isResult()) {
				djtk = result.getDjtk();
				player.setDcndjtk(djtk);
			}
		}
		if("".equals(djtk)) {
			Result result = DCNService.sys(player.getLoginInfo().accountID + "", player.getName(), player.getLoginInfo().password);
			if(result.isResult()) {
				djtk = result.getDjtk();
				player.setDcndjtk(djtk);
			}
		}
		if("".equals(djtk)) {
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("操作失败，你无法发帖或回帖！"));
			return;
		}
		
		if(topicid == null || content == null) {
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("操作失败，服务器接收数据错误！"));
			return;
		}
		if(topicid.length() == 0 || content.length() == 0) {
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("操作失败，服务器接收数据错误！"));
			return;
		}
		if("0".equals(topicid)) {//发帖
			if(title != null || title.length() > 0) {
				Result result = DCNService.newTopic("", djtk, "", title, content, "", player.getName(), "", "");
				if(result.isResult()) {
					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("发帖成功！"));
					//List<Topic> topicList = DCNService.getForumList(pageno, "");
					//OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new TopicListResult(topicList,pageno));
					return;
				} else {
					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("发帖失败，请尝试重新发帖！"));
					player.setDcndjtk("");
					return;
				}
			} else {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("操作失败，服务器接收数据错误！"));
				return;
			}
		} else {//回帖
			Result result = DCNService.replyTopic(topicid, "", djtk, "", title, content, "", player.getName());
			if(result.isResult()) {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("回帖成功！"));
				//List<Topic> topicList = DCNService.getForumList(pageno, "");
				//OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new TopicListResult(topicList,pageno));
				return;
			} else {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("回帖失败，请重新尝试！"));
				player.setDcndjtk("");
				return;
			}
		}
	}
	
}
