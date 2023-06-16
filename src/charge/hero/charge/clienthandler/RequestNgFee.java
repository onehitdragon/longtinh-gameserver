package hero.charge.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.service.ChargeServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-14
 * Time: 上午10:39
 * 请求网游计费    0x3b15  测试用
 */
public class RequestNgFee extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

//        String feeUrlID = input.readUTF();
//        String transID = input.readUTF();
//        String toolsID = input.readUTF();

//        boolean ngres = ChargeServiceImpl.getInstance().ngBuyMallTools(feeUrlID,transID,
//                        player.getLoginInfo().accountID,toolsID);
//        if(ngres){
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("此次网游计费成功",Warning.UI_TOOLTIP_TIP));
//        }else {
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("此次网游计费失败",Warning.UI_TOOLTIP_TIP));
//        }
    }
}
