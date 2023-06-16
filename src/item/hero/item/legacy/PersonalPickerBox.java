package hero.item.legacy;

import hero.item.Goods;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.Map;
import hero.player.HeroPlayer;
import hero.task.service.TaskServiceImpl;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PersonalPickerBox.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-27 上午11:47:06
 * @描述 ：归个人拾取的怪物掉落的箱子，继承自MonsterLegacyBox
 */

public class PersonalPickerBox extends MonsterLegacyBox
{
    private static Logger log = Logger.getLogger(PersonalPickerBox.class);
    /**
     * 构造
     * 
     * @param _pickerUserID 拾取者编号
     * @param _monsterID 怪物编号
     * @param _money 钱
     * @param _legacyList 掉落物品列表
     * @param _locationX 掉落X坐标
     * @param _locationY 掉落Y坐标
     */
    public PersonalPickerBox(int _pickerUserID, int _monsterID, Map _map,
            ArrayList<int[]> _legacyList,
            ArrayList<TaskGoodsLegacyInfo> _taskGoodsList, short _locationX,
            short _locationY)
    {
        super(_pickerUserID, _monsterID, _map, _legacyList, _taskGoodsList,
                _locationX, _locationY);
    }

    @Override
    public byte getPickerType ()
    {
        // TODO Auto-generated method stub
        return MonsterLegacyBox.PICKER_TYPE_PERSION;
    }

    @Override
    public boolean bePicked (HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        boolean changed = false;

        if (null != legacyList && legacyList.size() > 0)
        {
            log.debug("personal picker picked : null != legacyList && legacyList.size() > 0");
            int[] monsterLegacy;

            for (int i = 0; i < legacyList.size();)
            {
                monsterLegacy = legacyList.get(i);
                log.debug("monsterLegacy goods id = " + monsterLegacy[0]);
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
                        monsterLegacy[0]);

                if (null != goods)
                {
                    log.debug("monsterLegacy goods name = " + goods.getName());
                    if (null != GoodsServiceImpl.getInstance()
                            .addGoods2Package(_player, goods, monsterLegacy[1],CauseLog.DROP))
                    {
                        legacyList.remove(i);

                        changed = true;
                    }
                    else
                    {
                        i++;
                    }
                }
                else
                {
                    legacyList.remove(i);

                    changed = true;
                }
            }
        }

        if (null != taskGoodsInfoList)
        {
            log.debug("null != taskGoodsInfoList ");
            for (int i = 0; i < taskGoodsInfoList.size();)
            {
                TaskGoodsLegacyInfo taskGoodsLegacyInfo = taskGoodsInfoList
                        .get(i);
                log.debug("task goodsID = " + taskGoodsLegacyInfo.getTaskGoodsID());
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
                        taskGoodsLegacyInfo.getTaskGoodsID());

                if (null != goods)
                {
                    log.debug("task goodsname = " + goods.getName());
                    if (null != GoodsServiceImpl.getInstance()
                            .addGoods2Package(_player, goods,
                                    taskGoodsLegacyInfo.getGoodsNumber(),CauseLog.DROP))
                    {
                        TaskServiceImpl.getInstance().addTaskGoods(_player,
                                taskGoodsLegacyInfo.getTaskID(), goods,
                                taskGoodsLegacyInfo.getGoodsNumber());

                        taskGoodsInfoList.remove(i);

                        changed = true;
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        return changed;
    }
}
