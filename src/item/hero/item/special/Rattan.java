package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.item.service.GoodsServiceImpl;
import hero.item.dictionary.SpecialGoodsDict;
import hero.log.service.LogServiceImpl;
import hero.item.SpecialGoods;
import hero.item.bag.EBagType;
import hero.item.bag.exception.BagException;
import hero.log.service.CauseLog;
import hero.player.HeroPlayer;
import hero.ui.message.NotifyAddGoods2SinglePackage;
import hero.ui.message.ResponseSinglePackageChange;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GourdEnhancer.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-9 上午10:43:01
 * @描述 ：藤条，是葫芦的强化剂
 */

public class Rattan extends SpecialGoods
{
    /**
     * 可强化的目标葫芦编号
     */
    private int targetGourdID;

    /**
     * 幻化出的目标葫芦编号
     */
    private int resultGourdID;

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public Rattan(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        setTargetGourdID();
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
        return false;
    }

    /**
     * 获取幻化目标葫芦编号
     * 
     * @return
     */
    public int getTargetGourdID ()
    {
        return targetGourdID;
    }

    /**
     * 获取幻化结果葫芦编号
     * 
     * @return
     */
    public int getResultGourdID ()
    {
        return resultGourdID;
    }

    private final void setTargetGourdID ()
    {
        for (int[] correspondingGourdTable : CORRESPONDING_TARGET_GOURD_LIST)
        {
            if (correspondingGourdTable[0] == getID())
            {
                targetGourdID = correspondingGourdTable[1];
                resultGourdID = correspondingGourdTable[2];
            }
        }
    }

    /**
     * 可强化的葫芦和幻化后的葫芦对应列表（4种藤条，[藤条编号、源葫芦编号、目标葫芦编号]）
     */
    private static final int[][] CORRESPONDING_TARGET_GOURD_LIST = {{50011, 50001, 50002 }, {50012, 50002, 50003 }, {50013, 50003, 50004 }, {50014, 50004, 50005 } };

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.RATTAN;
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
        try
        {
            if (_player.getInventory().getSpecialGoodsBag().getGoodsNumber(
                    targetGourdID) == 1)
            {
                SpecialGoods resultGoods = SpecialGoodsDict.getInstance()
                        .getSpecailGoods(resultGourdID);

                if (null != resultGoods)
                {
                    int gridIndexTargetGourd = _player.getInventory()
                            .getSpecialGoodsBag().getFirstGridIndex(
                                    targetGourdID);

                    if (GoodsServiceImpl.getInstance().diceSingleGoods(_player,
                            _player.getInventory().getSpecialGoodsBag(),
                            gridIndexTargetGourd, targetGourdID,
                            CauseLog.RATTAN))
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new ResponseSinglePackageChange(
                                                EBagType.SPECIAL_GOODS_BAG
                                                        .getTypeValue(),
                                                new short[]{(short) gridIndexTargetGourd, 0 }));

                        short[] resultGourdInfo = GoodsServiceImpl
                                .getInstance().addGoods2Package(_player,
                                        resultGoods, 1, CauseLog.RATTAN);

                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new NotifyAddGoods2SinglePackage(
                                        EBagType.SPECIAL_GOODS_BAG
                                                .getTypeValue(),
                                        resultGourdInfo, resultGoods, _player
                                                .getShortcutKeyList()));

                        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
                        return true;
                    }
                }
            }
        }
        catch (BagException be)
        {
            be.printStackTrace();
        }

        return false;
    }
}
