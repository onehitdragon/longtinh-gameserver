package hero.map.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.group.service.GroupServiceImpl;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.map.message.PlayerRefreshNotify;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-2-11
 * Time: 下午5:10
 * 脱离卡死，跳转到所在地图出生点
 * 0xa18
 */
public class BreakAwayStuck extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        Map map = player.where();
        player.setCellX(map.getBornX());
        player.setCellY(map.getBornY());

        MapSynchronousInfoBroadcast.getInstance().put(map,
                            new DisappearNotify(player.getObjectType().value(), player.getID(),player.getHp(),
                                    player.getBaseProperty().getHpMax(),
                                    player.getMp(),player.getBaseProperty().getMpMax()),
                            false, 0);

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResponseMapBottomData(player, map, map));

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseMapGameObjectList(player.getLoginInfo().clientType,
                        map));

        MapSynchronousInfoBroadcast.getInstance().put(map,
                        new PlayerRefreshNotify(player), true, player.getID());

        GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);

    }
}
