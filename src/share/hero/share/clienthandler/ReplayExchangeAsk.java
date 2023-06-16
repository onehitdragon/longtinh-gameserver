package hero.share.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.micro.teach.TeachService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.exchange.ExchangeDict;
import hero.share.message.Warning;
import hero.share.service.ShareServiceImpl;
import hero.share.service.Tip;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-12-9
 * Time: 16:51:59
 * 询玩家回复是否同意交易
 * 0x3016
 */
public class ReplayExchangeAsk extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        int playerID = yis.readInt();
        int otherID = yis.readInt();
        byte reply = yis.readByte(); //1：同意   0：不同意

        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(playerID);
        HeroPlayer other = null;

        if(reply == 1){
            other = PlayerServiceImpl.getInstance().getPlayerByUserID(otherID);
            if(other.isEnable()){
                if(player.where().getID() != other.where().getID()){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_EXCHANGE_DISTANCE,
            			Warning.UI_TOOLTIP_TIP));
                    return;
                }
            	ExchangeDict.getInstance().startExchange(player,other);
            }else{
            	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("对方已下线！", 
            			Warning.UI_STRING_TIP));
            }
        }else{
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("对方不同意和你交易！", 
            		Warning.UI_STRING_TIP));
            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(playerID);

            other = PlayerServiceImpl.getInstance().getPlayerByUserID(otherID);
            if(other.isEnable()){
                ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),new Warning("已回复对方"));
            }
        }

//        if(other != null){
//            TeachService.cancelWaitingTimer(other);
//        }
    }
}
