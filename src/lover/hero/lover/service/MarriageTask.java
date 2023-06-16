package hero.lover.service;

import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.npc.function.system.MarryNPC;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;

import java.util.Iterator;
import java.util.TimerTask;

/**
 * 婚礼计划任务
 */
public class MarriageTask extends TimerTask
{
    private short clan;
    public MarriageTask(short _clan){
        this.clan = _clan;
    }
    @Override
    public void run ()
    {
        LoverServiceImpl.getInstance().canMarry = true;

        MarryNPC.removeAllPlayer(clan);
    }
}
