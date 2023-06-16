package hero.item.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;

import hero.item.enhance.EnhanceService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-21
 * Time: 17:48:55
 * 玩家回复是否同意购买剥离宝石
 * 0x1d32
 */
public class PlayerRespBuyJewel extends AbsClientProcess{
	private static Logger log = Logger.getLogger(PlayerRespBuyJewel.class);
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte yes = yis.readByte();// 0:不同意   1:同意 ，这里应该只有 1 ，如果玩家不同意，则客户端不用向服务端发送了
        log.debug("玩家回复是否同意购买剥离宝石 = " + yes);
        if(yes == 1){
            EnhanceService.getInstance().addJewelForPlayer(player);   
        }
    }
}
