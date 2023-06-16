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
 * @文件 PickGroundTaskGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-31 下午02:52:25
 * @描述 ：
 */

public class PickGroundTaskGoods extends AbsClientProcess
{

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            int groundTaskGoodsID = yis.readInt();

            TaskServiceImpl.getInstance().pickGroundTaskGoods(player,
                    groundTaskGoodsID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
