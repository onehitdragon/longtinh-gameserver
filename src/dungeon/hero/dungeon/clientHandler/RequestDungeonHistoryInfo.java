package hero.dungeon.clientHandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.dungeon.message.ResponseDungeonHistoryInfo;
import hero.dungeon.service.DungeonServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RequestDungeonHistoryInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-22 上午10:01:27
 * @描述 ：请求副本进度信息
 */

public class RequestDungeonHistoryInfo extends AbsClientProcess
{

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseDungeonHistoryInfo(DungeonServiceImpl.getInstance()
                        .getHistoryList(player.getUserID())));
    }
}
