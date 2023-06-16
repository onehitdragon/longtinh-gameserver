package hero.npc.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.npc.function.system.MarryNPC;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-19
 * Time: 上午10:44
 * 设置恋人 type=1, 和恋人分手type=2
 * 0x608
 */
public class RequestLover extends AbsClientProcess{
    @Override
    public void read() throws Exception {

        HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

        byte type = yis.readByte();// 1:结为恋人， 2:分手
        String otherName = yis.readUTF();

        if(type == 1){
            MarryNPC.propose(player,otherName);
        }
        if(type == 2){
           MarryNPC.breakUp(player,otherName);
        }

    }
}
