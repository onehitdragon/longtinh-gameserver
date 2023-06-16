package hero.charge.clienthandler;

import hero.charge.message.SendChargeList;
import hero.charge.service.ChargeServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-25
 * Time: 下午11:32
 * 0x3b16 请求充值信息列表
 */
public class RequestChargeList extends AbsClientProcess{
    private static Logger log = Logger.getLogger(RequestChargeList.class);
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        		new SendChargeList(ChargeServiceImpl.getInstance().getFpListForRecharge(),
                        ChargeServiceImpl.getInstance().getFeeTypeListForRecharge()));
        log.info("player RequestChargeList send charge list message ....");

    }
}
