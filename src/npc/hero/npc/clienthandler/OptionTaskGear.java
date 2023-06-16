package hero.npc.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.task.service.TaskServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OptionTaskGear.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-13 下午09:09:45
 * @描述 ：
 */

public class OptionTaskGear extends AbsClientProcess
{

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            int gearID = yis.readInt();

            TaskServiceImpl.getInstance().openGear(player, gearID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
