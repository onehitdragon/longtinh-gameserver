package hero.player.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.player.message.ResponsePlayerBaseInfo;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-24
 * Time: 上午11:01
 * 根据昵称查看不在线玩家信息
 * 0x417
 */
public class ViewOffLinePlayerInfo extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player =  PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        String name = yis.readUTF();

        HeroPlayer other = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(name);
        if(null != other){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponsePlayerBaseInfo(other));
        }else{
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("没有这个玩家"));
        }
    }
}
