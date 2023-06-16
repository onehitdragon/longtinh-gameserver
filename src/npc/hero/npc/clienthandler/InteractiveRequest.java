package hero.npc.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.npc.Npc;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Interactive.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-12 下午03:24:34
 * @描述 ：
 */

public class InteractiveRequest extends AbsClientProcess
{

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        try
        {
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            int npcID = yis.readInt();

            Npc npc = NotPlayerServiceImpl.getInstance().getNpc(npcID);

            if (null != npc)
            {
                npc.listen(player, yis);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
