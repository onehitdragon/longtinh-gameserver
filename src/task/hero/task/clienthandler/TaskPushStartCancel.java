package hero.task.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.ShareServiceImpl;
import hero.task.Push;
import hero.task.service.TaskServiceImpl;

public class TaskPushStartCancel extends AbsClientProcess {

	@Override
	public void read() throws Exception {
        HeroPlayer player = 
        	(HeroPlayer) PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
        int pushID = yis.readInt();
        Push push = TaskServiceImpl.getInstance().getTaskPush(pushID);
        int price = push.point/ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
        LogServiceImpl.getInstance().taskPushOption(
        		player.getLoginInfo().accountID, 
        		player.getLoginInfo().username,
        		player.getUserID(),
        		player.getName(), 
        		pushID, 
        		1, 
        		"取消", 
        		price);
	}
	

}
