package hero.gather.clientHandler;

import hero.gather.service.GatherServerImpl;
import hero.npc.Monster;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;


/**
 * 对怪物使用葫芦上行 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class UseGourdClientHandler extends AbsClientProcess
{

    public void read () throws Exception
    {
        try
        {
            int _monsterID = yis.readInt();
            HeroPlayer _player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            if (_player != null)
            {
                Monster _monster = _player.where().getMonster(_monsterID);

                if (_monster != null)
                {
                    GatherServerImpl.getInstance().useGourd(_player, _monster);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
