package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.item.SpecialGoods;
import hero.item.message.PopHornInputUINotify;
import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WorldHorn.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-29 上午11:15:06
 * @描述 ：世界号角，可以让世界聊天内容显示3次
 */

public class WorldHorn extends SpecialGoods
{
	public static final int WORLD_HORN_ID = 340024;
    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public WorldHorn(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.WORLD_HORN;
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
        return false;
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new PopHornInputUINotify(_location));

        //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }
}
