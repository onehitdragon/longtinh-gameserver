package hero.task.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.charge.FeeIni;
import hero.charge.service.ChargeServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.ShareServiceImpl;
import hero.share.service.Tip;
import hero.task.Push;
import hero.task.message.ResponseTaskPushType;
import hero.task.service.TaskServiceImpl;

/**
 * 任务推广 
 * @author Administrator
 *
 */
public class TaskPushFeeMode extends AbsClientProcess {

	private static Logger log = Logger.getLogger(TaskPushFeeMode.class);
	
	@Override
	public void read() throws Exception {
        HeroPlayer player = 
        	(HeroPlayer) PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		int pushID = yis.readInt(); //推广id
		String swcode = yis.readUTF(); //客户端版本号
		String pseudoCode = yis.readUTF(); //移动伪码
		byte mode = Push.PUSH_TYPE_COMM;
        Push push = TaskServiceImpl.getInstance().getTaskPush(pushID);
        int price = push.point/ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
        //记录玩家点击第1个确认.
        LogServiceImpl.getInstance().taskPushOption(
        		player.getLoginInfo().accountID, 
        		player.getLoginInfo().username,
        		player.getUserID(),
        		player.getName(), 
        		pushID, 
        		0, 
        		"确认", 
        		price);
        if(player.getChargeInfo().pointAmount <= push.point)
        {
        	int rmb = push.point / ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
        	//调用焦栋杰编写的计费模块获得当前到底使用什么模式进行计费
        	FeeIni fee = ChargeServiceImpl.getInstance().getFeeIniForTask(
        			player.getLoginInfo().accountID, 
        			player.getUserID(), 
        			swcode, 
        			player.getLoginInfo().loginMsisdn, 
        			pseudoCode,
        			player.getLoginInfo().publisher, 
        			rmb);
        	log.info("接收到计费模块返回信息feeCode=" 
        			+ fee.feeCode 
        			+ ";feeType=feeType=" + fee.feeType 
        			+ ";feeUrlID=" + fee.feeUrlID 
        			+ ";price=" + fee.price 
        			+ ";status=" + fee.status 
        			+ ";sumPrice=" + fee.sumPrice 
        			+ ";transID=" + fee.transID );
        	if (fee.status == 0) 
        	{
                log.debug("任务计费请求返回计费信息："+fee.feeType);
				if (fee.feeType.equals(FeeIni.FEE_TYPE_SMS)) 
				{
					mode = Push.PUSH_TYPE_SMS;
					int smsCount = fee.sumPrice / fee.price;
		    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
		    				new ResponseTaskPushType(
		    						mode, 0, fee.feeUrlID, fee.feeCode, fee.transID, smsCount, fee.price));
				}
				else if (fee.feeType.equals(FeeIni.FEE_TYPE_NG)) 
				{
					mode = Push.PUSH_TYPE_MOBILE_PROXY; 
					//暂时只有2种代收费
					if(fee.feeUrlID.equals(FeeIni.FEE_URL_ID_HERO))
					{
						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
								new ResponseTaskPushType(
										mode, 0, fee.feeCode , (byte)Push.PROXY_HERO_ID, fee.transID));
					}
					else if (fee.feeUrlID.equals(FeeIni.FEE_URL_ID_JIUTIAN)) 
					{
						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
								new ResponseTaskPushType(
										mode, 0, fee.feeCode , (byte)Push.PROXY_JTCQ_ID, fee.transID));
					}
					else 
					{
						log.info("接收到无法识别的模式:" + fee.feeUrlID);
					}
				}
				else 
				{
					log.info("接收到无法识别的模式:" + fee.feeType);
				}
			}
        	else 
        	{
        		//没有任何模式能适配,只能跳转到正常模式
        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
        				new ResponseTaskPushType(mode, 0));
				log.warn("进行任务计费点的时候请求计费服务器失败,状态:" + String.valueOf(fee.status));
		        //记录玩家点击第1个确认.
		        LogServiceImpl.getInstance().taskPushOption(
		        		player.getLoginInfo().accountID, 
		        		player.getLoginInfo().username,
		        		player.getUserID(),
		        		player.getName(), 
		        		pushID, 
		        		4, 
		        		"任务计费点获取计费配置失败:"+ String.valueOf(fee.status) 
		        		+ ";移动伪码=" + pseudoCode 
		        		+ ";手机号码=" + player.getLoginInfo().loginMsisdn, 
		        		price);
			}
            TaskServiceImpl.getInstance().enterTaskPush(player, pushID, mode, true);
        }
        else 
        {
            TaskServiceImpl.getInstance().enterTaskPush(player, pushID, mode, true);
    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    				new ResponseTaskPushType(mode, 0));
		}

        
	}

}
