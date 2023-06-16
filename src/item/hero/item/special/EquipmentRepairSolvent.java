package hero.item.special;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;

import hero.log.service.LogServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.SpecialGoods;
import hero.item.message.RefreshEquipmentDurabilityPoint;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EquipmentRepairSolvent.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-11 上午11:40:12
 * @描述 ：装备耐久修复溶剂
 */

public class EquipmentRepairSolvent extends SpecialGoods
{

    public EquipmentRepairSolvent(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        boolean needRefreshPlayerProperty = false;

        ArrayList<EquipmentInstance> needRepairList = new ArrayList<EquipmentInstance>();

        int currentDurabilityPoint;
        int money = 0;
        for (EquipmentInstance ei : _player.getBodyWear().getEquipmentList())
        {
            if (null != ei)
            {
            	money += ei.getRepairCharge();
            }
        }
        if (money > _player.getMoney())
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning("金钱不够"));
            return false;
        }

        for (EquipmentInstance ei : _player.getBodyWear().getEquipmentList())
        {
            if (null != ei)
            {
                currentDurabilityPoint = ei.getCurrentDurabilityPoint();

                if (currentDurabilityPoint < ei.getArchetype()
                        .getMaxDurabilityPoint())
                {
                    needRepairList.add(ei);

                    if (0 == currentDurabilityPoint)
                    {
                        needRefreshPlayerProperty = true;
                    }
                }
            }
        }

        if (0 == needRepairList.size())
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_NOT_BAD, Warning.UI_STRING_TIP));

            return false;
        }

        GoodsServiceImpl.getInstance().restoreEquipmentDurability(
                needRepairList);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new RefreshEquipmentDurabilityPoint(_player.getBodyWear()));

        if (needRefreshPlayerProperty)
        {
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
        }
        PlayerServiceImpl.getInstance().addMoney(_player,
                -money, 1,
                PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE, "修理装备花费");

        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new Warning(Tip.TIP_ITEM_REPAIR.replaceAll("%fmoney", String.valueOf(money))));

        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.EQUIPMENT_REPAIR;
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
