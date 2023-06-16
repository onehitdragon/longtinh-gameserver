package hero.item.clienthandler;

import hero.item.message.ResponseOthersWearList;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SelectDidtributeGoods.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2009-7-15 下午08:07:30
 * @描述 查看其他玩家装备
 */

public class ViewOthersEquipmentList extends AbsClientProcess
{

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        try
        {
            int objectID = yis.readInt();

            HeroPlayer otherPlayer = (HeroPlayer) player.where().getPlayer(
                    objectID);

            if (otherPlayer != null && otherPlayer.isEnable())
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new ResponseOthersWearList(otherPlayer.getBodyWear()));
            }
            else
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning("无效的目标", Warning.UI_STRING_TIP));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
