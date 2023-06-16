package hero.share.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.system.PostBox;
import hero.npc.message.SpeedyMailResponse;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * User: zhengl
 * Date: 2011-01-25
 * 玩家随身邮.
 * 0x3017
 */
public class PostByPlayer extends AbsClientProcess {

	private PostBox postOperate = null;
	


	/**
	 * 校对功能是否可用
	 */
	private static final byte TYPE_VERIFY = 0;
	
	/**
	 * 使用功能
	 */
	private static final byte TYPE_USE = 1;
	
	/**
	 * 开通功能
	 */
	private static final byte TYPE_OPEN = 2;
	
	public PostByPlayer () {
		int id = NotPlayerServiceImpl.getInstance().getNpcByFunction(
					ENpcFunctionType.POST_BOX.value()).getID();
		postOperate = new PostBox(id);
	}
	
	@Override
	public void read() throws Exception {
		// TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
        	.getPlayerBySessionID(contextData.sessionID);
        byte operate = yis.readByte();
        if(operate == TYPE_VERIFY) {
        	//验证随身邮是否可用
        	if(player.speedyMail - System.currentTimeMillis() < 24*60*60*1000) {
        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SpeedyMailResponse((byte)0));
        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
        				new Warning(Tip.TIP_SHARE_MAIL_FEE_TIP.replace("*name*", player.getName()), 
        						Warning.UI_STRING_TIP));
        	} else {
				int day = (int)(player.speedyMail - System.currentTimeMillis())/(24*60*60*1000);
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SpeedyMailResponse((byte)1));
        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
        				new Warning( Tip.TIP_SHARE_MAIL_BY_UES.replace("*name*", player.getName())
        						.replace("*day*", String.valueOf(day)), Warning.UI_STRING_TIP) );
			}
        } else if(operate == TYPE_USE) {
			if(player.speedyMail - System.currentTimeMillis() >= 24*60*60*1000) {
        		//使用功能
                byte step = yis.readByte();
                int selectIndex = yis.readInt();
        		postOperate.process(player, step, selectIndex, yis);
			}
		} else if(operate == TYPE_OPEN) {
        	//开通随身邮件.
        	//使用功能并且开通.记录邮件消耗日志.
		}
	}

}
