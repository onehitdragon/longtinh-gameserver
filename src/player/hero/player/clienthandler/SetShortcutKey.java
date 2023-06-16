package hero.player.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SetKeyOfWalking.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-17 上午09:36:46
 * @描述 ：设置快捷键
 */

public class SetShortcutKey extends AbsClientProcess
{
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        try
        {
            HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

            byte shortcutKeyType = yis.readByte();
            int targetID = yis.readInt();
            byte shortcutKey = yis.readByte();
            
            PlayerServiceImpl.getInstance().setShortcutKey(player, shortcutKey,
                    shortcutKeyType, targetID);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
