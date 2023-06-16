package hero.item.special;

import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SealPray.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-27 下午02:05:10
 * @描述 ：封印祝福，用于解除装备上的封印，使之可用
 */

public class SealPray extends SpecialGoods
{
    /**
     * 解封装备等级下限
     */
    private byte equipmentLevelLower;

    /**
     * 解封装备等级上限
     */
    private byte equipmentLevelLimit;

    /**
     * 构造
     * 
     * @param _id 编号
     * @param _stackNums 叠防数量
     */
    public SealPray(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub

        for (int[] equipmentLevelList : PRAY_TARGET_LEVEL_LIST)
        {
            if (equipmentLevelList[0] == getID())
            {
                equipmentLevelLower = (byte) equipmentLevelList[1];
                equipmentLevelLimit = (byte) equipmentLevelList[2];

                break;
            }
        }
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.SEAL_PRAY;
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

    /**
     * 是否有效封印祝福
     * 
     * @param _equipmentLevel
     * @return
     */
    public boolean isValidatePray (int _equipmentLevel)
    {
        if (_equipmentLevel >= equipmentLevelLower
                && _equipmentLevel <= equipmentLevelLimit)
        {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.item.SingleGoods#useable()
     */
    public boolean useable ()
    {
        return false;
    }

    /**
     * 可解封的装备等级区间对应列表（3种封印祝福，[祝福编号、物品等级下限、物品等级上限]）
     */
    private static final int[][] PRAY_TARGET_LEVEL_LIST = {
            {52011, 1, 40 }, {52012, 41, 70 }, {52013, 71, 100 } };

    /**
     * 获取有效祝福之光的物品编号
     * 
     * @param _equipmentLevel 装备等级
     * @return
     */
    public static final int getValidatePrayID (int _equipmentLevel)
    {
        for (int[] equipmentLevelList : PRAY_TARGET_LEVEL_LIST)
        {
            if (_equipmentLevel >= equipmentLevelList[1]
                    && _equipmentLevel <= equipmentLevelList[2])
            {
                return equipmentLevelList[0];
            }
        }

        return 0;
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
        boolean res = true;
        if(res){
        //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        }
        return res;
    }
}
