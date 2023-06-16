package hero.player.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.player.IPlayer;
import hero.player.HeroPlayer;
import hero.player.message.ResponsePlayerBaseInfo;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ViewOtherBaseInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-7-3 下午05:14:48
 * @描述 ：
 */

public class ViewOtherBaseInfo extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
    	HeroPlayer other;
    	byte type = yis.readByte();
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        
        if(type == 1) {
        	int otherUserID = yis.readInt();
        	other = PlayerServiceImpl.getInstance().getPlayerByUserID(otherUserID);
        } else {
        	String name = yis.readUTF();
        	other = PlayerServiceImpl.getInstance().getPlayerByName(name);
		}
        

        if (null == other || !other.isEnable())
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new Warning("目标不在线"));

            return;
        }

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new ResponsePlayerBaseInfo(other));
    }
}
