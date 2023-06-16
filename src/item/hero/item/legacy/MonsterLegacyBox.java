package hero.item.legacy;

import hero.map.Map;
import hero.player.HeroPlayer;
import hero.share.service.IDManager;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterLegacyBox.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-26 下午09:32:28
 * @描述 ：怪物死亡后掉落的物品箱（包括单人打怪掉落箱子和队伍打怪掉落箱子）
 */

public abstract class MonsterLegacyBox
{
    /**
     * 箱子ID
     */
    private int                              id;

    /**
     * 怪物编号
     */
    private int                              monsterID;

    /**
     * 位置X坐标
     */
    private short                            locationX;

    /**
     * 位置Y坐标
     */
    private short                            locationY;

    /**
     * 所在地图
     */
    protected Map                            where;

    /**
     * 普通物品（兵制、将制等级物品）拾取者
     */
    protected int                            pickerUserID;

    /**
     * 普通物品列表,int[]： 物品ID、数量
     */
    protected ArrayList<int[]>               legacyList;

    /**
     * 掉落的任务物品信息列表
     */
    protected ArrayList<TaskGoodsLegacyInfo> taskGoodsInfoList;

    /**
     * 掉落时间
     */
    protected long                           buildTime;

    /**
     * 个人拾取
     */
    public static final byte                 PICKER_TYPE_PERSION                        = 1;

    /**
     * 团队拾取
     */
    public static final byte                 PICKER_TYPE_RAID                           = 2;

    /**
     * 个人拾取的箱子保留时间
     */
    public static final long                 KEEP_TIME_OF_PERSONAL_LEGACY               = 180 * 1000;

    /**
     * 团队拾取的箱子保留时间
     */
    public static final long                 KEEP_TIME_OF_RAID_LEGACY                   = 600 * 1000;

    /**
     * 物品分配操作持续时间
     */
    public static final int                  KEEP_TIME_OF_DISTRIBUTE_OPERATION          = 90 * 1000;

    /**
     * 分配物品的延时修正时间
     */
    public static final int                  DELAY_CORRECT_TIME_OF_DISTRIBUTE_OPERATION = 93 * 1000;

    /**
     * 默认构造
     */
    private MonsterLegacyBox()
    {
        id = IDManager.buildMonsterLegacyBoxID();
        buildTime = System.currentTimeMillis();
    }

    /**
     * 扩展构造
     * 
     * @param _money 金钱
     * @param _legacyList 物品列表
     */
    public MonsterLegacyBox(int _pickerUserID, int _monsterID, Map _map,
            ArrayList<int[]> _legacyList,
            ArrayList<TaskGoodsLegacyInfo> _taskGoodsList, short _locationX,
            short _locationY)
    {
        this();
        pickerUserID = _pickerUserID;
        monsterID = _monsterID;
        legacyList = _legacyList;
        taskGoodsInfoList = _taskGoodsList;
        where = _map;
        locationX = _locationX;
        locationY = _locationY;

        _map.getLegacyBoxList().add(this);
    }

    /**
     * 获取拾取者userID
     * 
     * @return
     */
    public int getPickerUserID ()
    {
        return pickerUserID;
    }

    /**
     * 获取箱子编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    /**
     * 获取怪物编号
     * 
     * @return
     */
    public int getMonsterID ()
    {
        return monsterID;
    }

    /**
     * 在哪里
     * 
     * @return
     */
    public Map where ()
    {
        return where;
    }

    /**
     * 获取箱子位置X坐标
     * 
     * @return
     */
    public short getLocationX ()
    {
        return locationX;
    }

    /**
     * 获取箱子位置Y坐标
     * 
     * @return
     */
    public short getLocationY ()
    {
        return locationY;
    }

    /**
     * 是否是空箱子
     * 
     * @return
     */
    public boolean isEmpty ()
    {
        if (null != legacyList)
        {
            if (legacyList.size() != 0)
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
     * 箱子保留时间是否到期
     * 
     * @return
     */
    public boolean becomeDue ()
    {
        if (PICKER_TYPE_PERSION == getPickerType())
        {
            if (System.currentTimeMillis() - buildTime >= KEEP_TIME_OF_PERSONAL_LEGACY)
            {
                return true;
            }
        }
        else
        {
            if (System.currentTimeMillis() - buildTime >= KEEP_TIME_OF_RAID_LEGACY)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 被拾取
     */
    public abstract boolean bePicked (HeroPlayer _player);

    /**
     * @return
     */
    public abstract byte getPickerType ();
}
