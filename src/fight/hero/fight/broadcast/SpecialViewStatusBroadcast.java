package hero.fight.broadcast;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.share.service.ME2ObjectList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialViewStatusBroadcast.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-31 上午09:41:31
 * @描述 ：游戏对象特殊可见状态变化广播（玩家、怪物、NPC）
 */

public class SpecialViewStatusBroadcast
{
    /**
     * 即时广播
     * 
     * @param _object
     * @param _stat
     */
    public static void send (ME2GameObject _object, byte _stat)
    {
        AbsResponseMessage message = new SpecialStatusChangeNotify(_object
                .getObjectType().value(), _object.getID(), _stat);

        ME2ObjectList mapPlayerList = _object.where().getPlayerList();

        if (mapPlayerList.size() > 0)
        {
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), message);
            }

            player = null;
        }
    }
}
