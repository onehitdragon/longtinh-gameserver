package hero.npc.function.system;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import hero.item.EquipmentInstance;
import hero.item.message.RefreshEquipmentDurabilityPoint;
import hero.item.service.GoodsServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Repair.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午05:45:38
 * @描述 ：修理装备（恢复耐久度）
 */

public class Repair extends BaseNpcFunction
{


    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]  mainMenuMarkImageIDList = {1003 };

    public Repair(int _npcID)
    {
        super(_npcID);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.REPAIR;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
        for (int i = 0; i < Tip.FUNCTION_NPC_REPAIR_MAIN.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = Tip.FUNCTION_NPC_REPAIR_MAIN[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        int repairCharge = 0;
        boolean needRefreshPlayerProperty = false;

        ArrayList<EquipmentInstance> needRepairList = new ArrayList<EquipmentInstance>();
        EquipmentInstance[] eiList = _player.getBodyWear().getEquipmentList();
        int money = 0;

        for (EquipmentInstance ei : eiList)
        {
            if (null != ei)
            {
                money = ei.getRepairCharge();

                if (money > 0)
                {
                    needRepairList.add(ei);
                    repairCharge += money;
                }

                if (ei.getCurrentDurabilityPoint() == 0)
                {
                    needRefreshPlayerProperty = true;
                }
            }
        }

        eiList = _player.getInventory().getEquipmentBag().getEquipmentList();

        for (EquipmentInstance ei : eiList)
        {
            if (null != ei)
            {
                money = ei.getRepairCharge();

                if (money > 0)
                {
                    needRepairList.add(ei);
                    repairCharge += money;
                }
            }
        }

        if (repairCharge > 0)
        {
            if (repairCharge > _player.getMoney())
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning("金钱不够"));
            }
            else
            {
                GoodsServiceImpl.getInstance().restoreEquipmentDurability(
                        needRepairList);
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new RefreshEquipmentDurabilityPoint(_player
                                .getBodyWear()));
                PlayerServiceImpl.getInstance().addMoney(_player,
                        -repairCharge, 1,
                        PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE, "修理装备花费");

                ResponseMessageQueue.getInstance().put( _player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_ITEM_REPAIR.replaceAll("%fmoney", String.valueOf(repairCharge))) );

                if (needRefreshPlayerProperty)
                {
                    PlayerServiceImpl.getInstance().reCalculateRoleProperty(
                            _player);
                    PlayerServiceImpl.getInstance()
                            .refreshRoleProperty(_player);
                }
            }
        }
        else
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning("装备未损坏"));
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        return optionList;
    }
}
