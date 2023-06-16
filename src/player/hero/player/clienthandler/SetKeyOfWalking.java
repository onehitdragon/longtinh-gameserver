package hero.player.clienthandler;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SetKeyOfWalking.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-17 上午09:36:46
 * @描述 ：设置行走键
 */

public class SetKeyOfWalking extends AbsClientProcess
{

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        try
        {
            HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

            byte[] shortcutKeys = new byte[4];
            yis.readFully(shortcutKeys, 0, 4);

            PlayerServiceImpl.getInstance().setKeyOfWalking(player,
                    shortcutKeys);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
