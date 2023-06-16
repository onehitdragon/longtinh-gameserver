package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.item.message.ResponseItemInfo;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ViewItem.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-11 上午10:12:09
 * @描述 ：根据物品编号查看物品
 */

public class ViewItem extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        int goodsID = yis.readInt();

        Goods goods = GoodsContents.getGoods(goodsID);

        if (null != goods)
        {
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseItemInfo(goods));
        }
    }
}
