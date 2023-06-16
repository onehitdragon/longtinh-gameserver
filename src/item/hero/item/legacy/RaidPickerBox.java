package hero.item.legacy;

import hero.item.Goods;
import hero.item.TaskTool;
import hero.item.detail.EGoodsTrait;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.map.Map;
import hero.player.HeroPlayer;
import hero.task.service.TaskServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RaidLegacy.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-27 上午11:47:21
 * @描述 ：归团队分配的怪物掉落的箱子，继承自MonsterLegacyBox
 */

public class RaidPickerBox extends MonsterLegacyBox
{
    /**
     * 归属队伍编号
     */
    private int                        groupID;

    /**
     * 可见掉落箱子列表
     */
    private ArrayList<HeroPlayer>      boxVisiblePlayerList;

    /**
     * 箱子拾取状态映射（玩家userID：状态）
     */
    private HashMap<Integer, Boolean>  boxStatusTable;

    /**
     * 分配物品列表（御制、圣器物品）
     */
    private ArrayList<DistributeGoods> distributeGoodsList;

    /**
     * 高品质物品是否已经弹出分配框
     */
    private boolean                    distributeUIShowed;

    /**
     * 是否包含高品质物品
     */
    private boolean                    containsHighTraitGoods;

    /**
     * 构造
     * 
     * @param _group 队伍
     * @param _pickerUserID 普通物品（兵制、将制等级物品）拾取者编号
     * @param _monsterID 怪物编号
     * @param _money 掉落的金钱
     * @param _legacyList 非任务物品掉落清单
     * @param _mapID 怪物所在地图编号
     * @param _locationX 箱子X坐标
     * @param _locationY 箱子Y坐标
     */
    public RaidPickerBox(int _groupID, int _pickerUserID,
            ArrayList<HeroPlayer> _boxVisiblePlayerList, int _monsterID,
            Map _map, ArrayList<int[]> _legacyList,
            ArrayList<TaskGoodsLegacyInfo> _taskGoodsList, short _locationX,
            short _locationY)
    {
        super(_pickerUserID, _monsterID, _map, null, _taskGoodsList,
                _locationX, _locationY);

        groupID = _groupID;
        boxVisiblePlayerList = _boxVisiblePlayerList;
        boxStatusTable = new HashMap<Integer, Boolean>();

        if (null != _legacyList && _legacyList.size() > 0)
        {
            distributeGoodsList = new ArrayList<DistributeGoods>(_legacyList
                    .size());

            DistributeGoods distributeGoods;
            Goods goods;

            for (int[] legacy : _legacyList)
            {
                goods = GoodsServiceImpl.getInstance().getGoodsByID(legacy[0]);

                if (null != goods)
                {
                    distributeGoods = new DistributeGoods();
                    distributeGoods.box = this;
                    distributeGoods.goods = goods;
                    distributeGoods.number = (byte) legacy[1];

                    if (distributeGoods.goods.getTrait() == EGoodsTrait.SHI_QI
                            || distributeGoods.goods.getTrait() == EGoodsTrait.BING_ZHI
                            || distributeGoods.goods.getTrait() == EGoodsTrait.JIANG_ZHI)
                    {
                        distributeGoods.pickerUserID = _pickerUserID;
                    }
                    else
                    {
                        containsHighTraitGoods = true;
                    }

                    distributeGoodsList.add(distributeGoods);
                }
            }
        }

        setStateOfPicking();
    }

    /**
     * 设置箱子拾取状态映射
     */
    private void setStateOfPicking ()
    {
        if (containsHighTraitGoods)
        {
            for (HeroPlayer player : boxVisiblePlayerList)
            {
                boxStatusTable.put(player.getUserID(), true);
            }

            return;
        }

        for (HeroPlayer player : boxVisiblePlayerList)
        {
            boxStatusTable.put(player.getUserID(), false);
        }

        if (null != distributeGoodsList)
        {
            boxStatusTable.put(pickerUserID, true);
        }

        if (null != taskGoodsInfoList && taskGoodsInfoList.size() > 0)
        {
            for (HeroPlayer player : boxVisiblePlayerList)
            {
                if (null == distributeGoodsList
                        || player.getUserID() != pickerUserID)
                {
                    for (TaskGoodsLegacyInfo legacyTaskGoods : taskGoodsInfoList)
                    {
                        if (legacyTaskGoods.canPick(player.getUserID()))
                        {
                            boxStatusTable.put(player.getUserID(), true);

                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取队伍编号
     * 
     * @return
     */
    public int getGroupID ()
    {
        return groupID;
    }

    /**
     * 是否包含分配物品
     * 
     * @return
     */
    public boolean containsHighTraitGoods ()
    {
        return containsHighTraitGoods;
    }

    @Override
    public byte getPickerType ()
    {
        // TODO Auto-generated method stub
        return MonsterLegacyBox.PICKER_TYPE_RAID;
    }

    /**
     * 是否已弹出高品质物品分配界面
     * 
     * @return
     */
    public boolean distributeUIHasShowed ()
    {
        return distributeUIShowed;
    }

    /**
     * 弹出高品质分配界面
     */
    public void showDistributeUI ()
    {
        distributeUIShowed = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.item.legacy.MonsterLegacyBox#isEmpty()
     */
    public boolean isEmpty ()
    {
        if (null != distributeGoodsList)
        {
            if (distributeGoodsList.size() != 0)
            {
                return false;
            }
        }

        if (null != taskGoodsInfoList)
        {
            if (taskGoodsInfoList.size() != 0)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取箱子拾取状态
     * 
     * @param _userID
     * @return
     */
    public boolean getStateOfPicking (int _userID)
    {
        return boxStatusTable.get(_userID);
    }

    /**
     * 可见者中是否包含该玩家
     * 
     * @param _userID
     * @return
     */
    public boolean containsVisibler (int _userID)
    {
        return boxStatusTable.containsKey(_userID);
    }

    /**
     * 处理玩家对箱子的拾取状态
     * 
     * @param _userID
     * @return 可拾取状态是否发生变化（由可拾取是否变为不可拾取）
     */
    public boolean statusIsChanged (int _userID)
    {
        if (boxStatusTable.get(_userID))
        {
            if (null != distributeGoodsList && distributeGoodsList.size() > 0)
            {
                for (DistributeGoods goods : distributeGoodsList)
                {
                    if (goods.pickerUserID == _userID
                            || (goods.hasOperated && goods.pickerUserID == 0))
                    {
                        return false;
                    }
                }
            }

            if (null != taskGoodsInfoList)
            {
                for (TaskGoodsLegacyInfo legacyTaskGoods : taskGoodsInfoList)
                {
                    if (legacyTaskGoods.canPick(_userID))
                    {
                        return false;
                    }
                }
            }

            boxStatusTable.put(_userID, false);

            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean bePicked (HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        boolean changed = false;

        if (null != distributeGoodsList && distributeGoodsList.size() > 0)
        {
            for (int i = 0; i < distributeGoodsList.size();)
            {
                DistributeGoods distributeGoods = distributeGoodsList.get(i);

                if (distributeGoods.goods.getTrait() == EGoodsTrait.JIANG_ZHI
                		|| distributeGoods.goods.getTrait() == EGoodsTrait.YU_ZHI 
                		|| distributeGoods.goods.getTrait() == EGoodsTrait.SHENG_QI)
                {
                    if (!distributeUIShowed)
                    {
                        MonsterLegacyManager.getInstance()
                                .notifyGoodsDistributeUI(distributeGoods);
                    }
                    else
                    {
                        if (distributeGoods.hasOperated)
                        {
                            if (0 == distributeGoods.pickerUserID
                                    || _player.getUserID() == distributeGoods.pickerUserID)
                            {
                                if (null != GoodsServiceImpl.getInstance()
                                        .addGoods2Package(_player,
                                                distributeGoods.goods,
                                                distributeGoods.number,CauseLog.DROP))
                                {
                                    distributeGoodsList.remove(i);
                                    MonsterLegacyManager.getInstance()
                                            .removeDistributeFromMonitor(
                                                    distributeGoods.id);

                                    changed = true;

                                    //玩家获得怪物掉落物品日志
                                    LogServiceImpl.getInstance().getMonsterLegacyGoodsLog(_player.getLoginInfo().accountID,
                                            _player.getLoginInfo().username,_player.getUserID(),_player.getName(),
                                            distributeGoods.goods.getID(),distributeGoods.goods.getName(),distributeGoods.number,
                                            distributeGoods.goods.getTrait().getDesc(),distributeGoods.goods.getGoodsType().getDescription());

                                    continue;
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (_player.getUserID() == distributeGoods.pickerUserID)
                    {
                        if (null != GoodsServiceImpl.getInstance()
                                .addGoods2Package(_player,
                                        distributeGoods.goods,
                                        distributeGoods.number,CauseLog.DROP))
                        {
                            distributeGoodsList.remove(i);
                            MonsterLegacyManager.getInstance()
                                    .removeDistributeFromMonitor(
                                            distributeGoods.id);

                            changed = true;

                            //玩家获得怪物掉落物品日志
                            LogServiceImpl.getInstance().getMonsterLegacyGoodsLog(_player.getLoginInfo().accountID,
                                    _player.getLoginInfo().username,_player.getUserID(),_player.getName(),
                                    distributeGoods.goods.getID(),distributeGoods.goods.getName(),distributeGoods.number,
                                    distributeGoods.goods.getTrait().getDesc(),distributeGoods.goods.getGoodsType().getDescription());

                            continue;
                        }
                    }
                }

                i++;
            }
        }

        if (null != taskGoodsInfoList)
        {
            TaskGoodsLegacyInfo taskGoodsLegacyInfo;

            for (int i = 0; i < taskGoodsInfoList.size();)
            {
                taskGoodsLegacyInfo = taskGoodsInfoList.get(i);

                if (taskGoodsLegacyInfo.canPick(_player.getUserID()))
                {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
                            taskGoodsLegacyInfo.getTaskGoodsID());

                    if (null != goods)
                    {
                        if (null != GoodsServiceImpl.getInstance()
                                .addGoods2Package(_player, goods,
                                        taskGoodsLegacyInfo.getGoodsNumber(),CauseLog.DROP))
                        {
                            taskGoodsLegacyInfo.removePicker(_player
                                    .getUserID());
                            TaskServiceImpl.getInstance().addTaskGoods(_player,
                                    taskGoodsLegacyInfo.getTaskID(), goods,
                                    taskGoodsLegacyInfo.getGoodsNumber());

                            changed = true;

                            if (!((TaskTool) goods).isShare()
                                    || taskGoodsLegacyInfo.pickedByAll())
                            {
                                taskGoodsInfoList.remove(i);

                                continue;
                            }
                            else
                            {
                                i++;
                            }
                        }
                        else
                        {
                            i++;
                        }
                    }
                    else
                    {
                        changed = false;
                    }
                }
                else
                {
                    i++;
                }
            }
        }

        distributeUIShowed = true;

        return changed;
    }

    /**
     * 移除普通掉落物品
     * 
     * @param _distributeGoods
     */
    public void removeNormalGoods (DistributeGoods _distributeGoods)
    {
        distributeGoodsList.remove(_distributeGoods);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.item.legacy.MonsterLegacyBox#removeGoods(int)
     */
    public void removeGoods (int _goodsID)
    {
        return;
    }

    public ArrayList<HeroPlayer> getVisitorList ()
    {
        return boxVisiblePlayerList;
    }
}
