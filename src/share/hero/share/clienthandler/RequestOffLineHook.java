package hero.share.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.ShareServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-8
 * Time: 下午6:08
 * 0x5d22 是否同意购买离线经验
 */
public class RequestOffLineHook extends AbsClientProcess{

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

        byte res = yis.readByte(); //1:同意购买  0:不同意
        if(res == 1){
            player.buyHookExp = true;
            ShareServiceImpl.getInstance().startBuyHookExp(player);
        }else {
            player.buyHookExp = false;
        }

    }
}
