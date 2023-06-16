package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.item.SpecialGoods;
import hero.item.message.NotifyTaskTransportItem;
import hero.item.message.PopHornInputUINotify;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTransportItem.java
 * @创建者 zhengl
 * @版本 1.0
 * @时间 2011-02-11 上午11:15:06
 * @描述 ：任务传送道具
 */

public class TaskTransportItem extends SpecialGoods
{
	/**
	 * 任务传送道具ID
	 */
	public static int TASK_TRANSPORT_ITEM_ID = GoodsServiceImpl.getInstance()
				.getConfig().getSpecialConfig().number_transport;
    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public TaskTransportItem(int _id, short _stackNums)
    {
        super(_id, _stackNums);
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.TASK_TRANSPORT;
    }

    @Override
    public void initDescription ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isIOGoods ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
    	//任务物品勿需显示物品数量变化通知,因为使用该物品的时候玩家无法查看包裹
//        OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),
//                new NotifyTaskTransportItem(getID(), _location));

        //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }
}
