package hero.item;

import yoyo.core.queue.ResponseMessageQueue;
import hero.item.bag.EBagType;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsDAO;
import hero.item.special.ESpecialGoodsType;
import hero.player.HeroPlayer;
import hero.ui.message.ResponseSinglePackageChange;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-8 下午05:03:23
 * @描述 ：特殊物品类
 */

public abstract class SpecialGoods extends SingleGoods
{
    /**
     * 是否唯一
     */
    private boolean isOnly;

    /**
     * 构造
     * 
     * @param _stackNums
     */
    public SpecialGoods(int _id, short _stackNums)
    {
        super(_stackNums);
        setID(_id);
        // TODO Auto-generated constructor stub
    }

    @Override
    public byte getSingleGoodsType ()
    {
        // TODO Auto-generated method stub
        return SingleGoods.TYPE_SPECIAL_GOODS;
    }

    @Override
    public EGoodsType getGoodsType ()
    {
        // TODO Auto-generated method stub
        return EGoodsType.SPECIAL_GOODS;
    }

    /**
     * 是否在背包中唯一
     * 
     * @return
     */
    public boolean isOnly ()
    {
        return isOnly;
    }

    /**
     * 设置唯一性
     */
    public void setOnly ()
    {
        isOnly = true;
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 被使用
     * 
     * @param _player
     * @param _target
     * @param _location
     * @return
     */
    public abstract boolean beUse (HeroPlayer _player, Object _target,
            int _location);

    /**
     * 获取特殊物品的类型
     * 
     * @return
     */
    public abstract ESpecialGoodsType getType ();

    /**
     * 使用后是否立即消失
     * 
     * @return
     */
    public abstract boolean disappearImmediatelyAfterUse ();

    /**
     * 移除背包中指定位置的一个
     * 
     * @param _gridIndex 在背包中的位置
     */
    public void remove (HeroPlayer _player, short _gridIndex)
            throws BagException
    {
        short[] gridChange = null;

        if (_gridIndex >= 0)
        {
            gridChange = _player.getInventory().getSpecialGoodsBag().remove(
                    _gridIndex, getID(), 1);
        }
        else
        {
            gridChange = _player.getInventory().getSpecialGoodsBag().removeOne(
                    getID());
        }

        if (null != gridChange)
        {
            if (0 == gridChange[1])
            {
                GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(),
                        gridChange[0], getID());
            }
            else
            {
                GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(),
                        getID(), gridChange[1], gridChange[0]);
            }

            if (_gridIndex >= 0)
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new ResponseSinglePackageChange(
                                EBagType.SPECIAL_GOODS_BAG.getTypeValue(),
                                gridChange));
            }
        }
    }
}
