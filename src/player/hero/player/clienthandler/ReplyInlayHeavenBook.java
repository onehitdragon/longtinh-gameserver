package hero.player.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-10
 * Time: 下午4:21
 * 0x419
 */
public class ReplyInlayHeavenBook extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

        byte type = yis.readByte();
        if(type == 1){
            PlayerServiceImpl.getInstance().startInlayHeavenBook(player,player.currInlayHeavenBookPosition,player.currInlayHeavenBookID);
        }else {
            player.currInlayHeavenBookID = 0;
            player.currInlayHeavenBookPosition = -1;
        }
    }
}
