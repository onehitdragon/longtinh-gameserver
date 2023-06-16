package hero.gather.clientHandler;

import hero.gather.Gather;
import hero.gather.message.SoulMessage;
import hero.gather.service.GatherServerImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * 查看葫芦上行 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ViewGourdClientHandler extends AbsClientProcess
{
    private static final String NOT_GATHER_SKILL = "你还没有学习采集技能";

    public void read () throws Exception
    {
        byte type;
        try
        {
            type = yis.readByte();
            HeroPlayer _player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            if (_player == null)
                return;
            Gather gather = GatherServerImpl.getInstance().getGatherByUserID(
                    _player.getUserID());
            if (gather == null)
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(NOT_GATHER_SKILL));
                return;
            }
            if (type == 0)
            {
                // 查看灵魂列表
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new SoulMessage(gather.getMonsterSoul()));
            }
            else
            {
                // 释放灵魂
                byte index = yis.readByte();
                gather.releaseMonsterSoul(index);
            }
        }
        catch (IOException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
    }

}
