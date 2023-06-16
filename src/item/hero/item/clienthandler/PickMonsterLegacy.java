package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.item.legacy.MonsterLegacyManager;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PickMonsterLegacy.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-27 下午02:51:20
 * @描述 ：拾取怪物掉落物品
 */

public class PickMonsterLegacy extends AbsClientProcess
{
    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            int boxID = yis.readInt();

            MonsterLegacyManager.getInstance().playerPickBox(player, boxID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
