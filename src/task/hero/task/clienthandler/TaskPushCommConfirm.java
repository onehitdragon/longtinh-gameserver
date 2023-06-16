package hero.task.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.ShareServiceImpl;
import hero.task.Push;
import hero.task.service.TaskServiceImpl;

public class TaskPushCommConfirm extends AbsClientProcess {

	@Override
	public void read() throws Exception {
		// TODO Auto-generated method stub
        HeroPlayer player = 
        	(HeroPlayer) PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		int pushID = yis.readInt(); //推广id
		byte mode = yis.readByte(); //计费方式
		String transID = yis.readUTF(); //流水号
		String productID = "";
		String userID = "";
		byte proxyID = 0;
		if (mode == Push.PUSH_TYPE_MOBILE_PROXY) {
			productID = yis.readUTF();
			proxyID = yis.readByte();
			userID = yis.readUTF();
		}
		Push push = TaskServiceImpl.getInstance().getTaskPush(pushID);
		int price = push.point/ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
        LogServiceImpl.getInstance().taskPushOption(
        		player.getLoginInfo().accountID, 
        		player.getLoginInfo().username,
        		player.getUserID(),
        		player.getName(), 
        		pushID, 
        		2, 
        		"确认", 
        		price);
		TaskServiceImpl.getInstance().confirmTaskPush(
				player, pushID, mode, productID, proxyID, transID, userID);
	}

}
