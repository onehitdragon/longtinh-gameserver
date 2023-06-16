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
 * 查询充值记录或消费记录,#HH客户端换行
 * @author jiaodongjie
 * 0x3b13
 */
public class QueryRecord extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance()
							.getPlayerBySessionID(contextData.sessionID);
		
		byte type = yis.readByte();//查询类型 1:充值记录   2：消费记录
//		String stime = input.readUTF();//开始时间
//		String etime = input.readUTF();//结束时间
		
		String result = Tip.QUERY_POINT_ERROR_TIP;
		if(type == 1){
			result = ChargeServiceImpl.getInstance().queryChargeUpDetail(player.getLoginInfo().accountID, player.getUserID(),2, null, null);
		}else if(type == 2){
			result = ChargeServiceImpl.getInstance().queryConsumeDetail(player.getLoginInfo().accountID, player.getUserID(),2, null, null);
		}
		
		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseQueryResult(result));
	}

}
