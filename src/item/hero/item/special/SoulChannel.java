package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.item.SpecialGoods;
import hero.item.message.SoulGoodsConfirm;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SoulChannel.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-10 下午06:07:31
 * @描述 ：灵魂付文，用于将自己传送回灵魂记录点
 */

public class SoulChannel extends SpecialGoods
{

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public SoulChannel(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        if (_player.where().getID() == _player.getHomeID())
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_EXIST, Warning.UI_STRING_TIP));

            return false;
        }

        if (_player.isInFighting())
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_IN_FIGHTING, Warning.UI_STRING_TIP));

            return false;
        }

        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new SoulGoodsConfirm(SoulGoodsConfirm.TYPE_OF_CHANNEL,
                        _location));

        //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.SOUL_CHANNEL;
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




}
