package hero.gm.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.gm.message.TestResponseUpAndDownTime;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * 用来测试上下行时间
 * @author jiaodongjie
 * 0x3c05
 */
public class TestRequestUpAndDownTime extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        int key = yis.readInt();
        long uptime = yis.readLong();

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new TestResponseUpAndDownTime(key,uptime,System.currentTimeMillis()-uptime));
		
	}

}
