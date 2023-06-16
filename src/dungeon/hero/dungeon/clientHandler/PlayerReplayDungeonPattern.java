package hero.dungeon.clientHandler;

import yoyo.core.process.AbsClientProcess;
import hero.dungeon.service.DungeonServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-12-3
 * Time: 14:24:42
 * 0x4202
 */
public class PlayerReplayDungeonPattern extends AbsClientProcess{
    @Override
    public void read() throws Exception {

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        short mapID = yis.readShort();
        short targetX = yis.readShort();
        short targetY = yis.readShort();
        byte replay = yis.readByte(); // 1:简单模式   2：复杂模式

//        DungeonServiceImpl.getInstance().setAsked(true);
//        DungeonServiceImpl.getInstance().gotoDungeonMap(player, mapID, replay,targetX,targetY);
    }
}
