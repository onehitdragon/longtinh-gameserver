package hero.player.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.ServiceManager;
import yoyo.service.base.session.SessionServiceImpl;
import hero.player.HeroPlayer;
import hero.player.message.ClearRoleSuccNotify;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ReturnToRoleList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-1 上午09:20:32
 * @描述 ：
 */

public class ReturnToRoleList extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        if (null != player)
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ClearRoleSuccNotify());
        }
    }
}
