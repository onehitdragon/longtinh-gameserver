package hero.share.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.ShareServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-22
 * Time: 下午3:11
 * 0x5d27 请求处理离线经验，只在处理刚进入游戏时，在公告界面返回时
 */
public class RequestSendOffLineHookExp extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);

        ShareServiceImpl.getInstance().offLineHook(player);//离线挂机处理
    }
}
