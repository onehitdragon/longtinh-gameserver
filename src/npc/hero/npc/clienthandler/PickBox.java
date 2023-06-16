package hero.npc.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.task.service.TaskServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PickBox.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-26 上午11:56:28
 * @描述 ：
 */

public class PickBox extends AbsClientProcess
{

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            int boxID = yis.readInt();

            NotPlayerServiceImpl.getInstance().pickBox(player, boxID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
