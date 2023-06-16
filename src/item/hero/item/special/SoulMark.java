package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.item.SpecialGoods;
import hero.item.message.PopHornInputUINotify;
import hero.item.message.SoulGoodsConfirm;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SoulMark.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-10 下午06:04:07
 * @描述 ：灵魂印记，用于记录设定灵魂记录点
 */

public class SoulMark extends SpecialGoods
{

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public SoulMark(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        if (EMapType.DUNGEON == _player.where().getMapType())
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_CAN_NOT_BIND, Warning.UI_STRING_TIP));

            return false;
        }

        if (_player.where().getID() == _player.getHomeID())
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_SAME_MAP, Warning.UI_STRING_TIP));

            return false;
        }

        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new SoulGoodsConfirm(SoulGoodsConfirm.TYPE_OF_MARK, _location));

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
        return ESpecialGoodsType.SOUL_MARK;
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
