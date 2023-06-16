package hero.charge.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.message.ResponseQueryResult;
import hero.charge.service.ChargeServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * 查询点数余额
 * @author jiaodongjie
 * 0x3b12
 */
public class QueryPoint extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance()
								.getPlayerBySessionID(contextData.sessionID);
		
		String result = ChargeServiceImpl.getInstance().queryBalancePoint(player.getLoginInfo().accountID);
		if(result != null && result.trim().length()>0){
			String[] res = result.split("#");
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseQueryResult(res[1]));
		}else{
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning(Tip.QUERY_POINT_ERROR_TIP,Warning.UI_TOOLTIP_TIP));
		}
	}

}
