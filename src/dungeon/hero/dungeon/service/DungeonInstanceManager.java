package hero.dungeon.service;

import java.util.ArrayList;
import java.util.TimerTask;

import hero.dungeon.Dungeon;
import hero.player.HeroPlayer;
import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonInstanceManager.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-9 上午10:24:07
 * @描述 ：
 */

public class DungeonInstanceManager extends TimerTask
{
    /**
     * 简单难度副本实例列表
     */
    private FastList<Dungeon>         noneHistoryDungeonList;

    /**
     * 进度类副副本实例列表
     */
    private FastList<Dungeon>         historyDungeonList;

    /**
     * 进度类副本实例表（进度编号：副本实例）
     */
    private FastMap<Integer, Dungeon> historyDungeonTable;

    /**
     * 即将被从内存中清除的副本列表
     */
    private FastList<Dungeon>         dungeonListThatWillClear;

    /**
     * 私有构造
     */
    private DungeonInstanceManager()
    {
        noneHistoryDungeonList = new FastList<Dungeon>();
        historyDungeonList = new FastList<Dungeon>();
        historyDungeonTable = new FastMap<Integer, Dungeon>();
        dungeonListThatWillClear = new FastList<Dungeon>();
    }

    /**
     * 增加新实例
     * 
     * @param _dungeon
     * @return 是否成功添加
     */
    public void add (Dungeon _dungeon)
    {
        if (!_dungeon.needSaveHistory())
        {
            noneHistoryDungeonList.add(_dungeon);
        }
        else
        {
            historyDungeonTable.put(_dungeon.getHistoryID(), _dungeon);
            historyDungeonList.add(_dungeon);
        }
    }

    /**
     * 根据进度编号获取副本实例
     * 
     * @param _historyID
     * @return
     */
    public Dungeon getHistoryDungeon (int _historyID)
    {
        return historyDungeonTable.get(_historyID);
    }

    /**
     * 根据归属队伍编号、副本编号、难度模式条件获取副本实例
     * 
     * @param _groupID 队伍编号
     * @param _dungeonID 副本编号
     * @param _pattern 难度模式
     * @return
     */
    public Dungeon getHistoryDungeon (int _groupID, int _dungeonID,
            byte _pattern)
    {
        synchronized (historyDungeonList)
        {
        	//edit by zhengl; date: 2011-04-17; note: 解决副本BUG
        	Dungeon dungeon = null;
        	for (int i = 0; i < historyDungeonList.size(); i++) 
        	{
                dungeon = historyDungeonList.get(i);

                if (dungeon.getGroupID() == _groupID
                        && dungeon.getID() == _dungeonID
                        && dungeon.getPattern() == _pattern)
                {
                    return dungeon;
                }
			}
        }

        return null;
    }

    /**
     * 根据归属队伍编号、副本编号条件获取副本实例
     * 
     * @param _groupID 队伍编号
     * @param _dungeonID 副本编号
     * @param _pattern 难度模式
     * @return
     */
    public Dungeon getNoneHistoryDungeon (int _groupID, int _dungeonID)
    {
        synchronized (noneHistoryDungeonList)
        {
            for (Dungeon dungeon : noneHistoryDungeonList)
            {
                if (dungeon.getGroupID() == _groupID
                        && dungeon.getID() == _dungeonID)
                {
                    return dungeon;
                }
            }
        }

        return null;
    }
    
    /**
     * 根据副本id和historyID获取副本
     * @param _dungeonID  配置的ID
     * @param _historyID  生成的id
     * @return
     */
    public Dungeon getMarryDungeon(int _dungeonID, int _historyID){
        synchronized (noneHistoryDungeonList)
        {
            for (Dungeon dungeon : noneHistoryDungeonList)
            {
                if (dungeon.getID() == _dungeonID && dungeon.getHistoryID() == _historyID)
                {
                    return dungeon;
                }
            }
        }

        return null;
    }

    /**
     * 移除非进度类型副本（队伍解散时调用）
     * 
     * @param _groupID
     * @param _dungeonID
     */
    public void removeNoneHistoryDungeon (int _groupID)
    {
        synchronized (noneHistoryDungeonList)
        {
            Dungeon dungeon;

            for (int i = 0; i < noneHistoryDungeonList.size();)
            {
                dungeon = noneHistoryDungeonList.get(i);

                if (dungeon.getGroupID() == _groupID
                        && 0 == dungeon.getPlayerNumber())
                {
                    dungeon.destroy();
                    noneHistoryDungeonList.remove(i);
                }
                else
                {
                    i++;
                }
            }
        }
    }

    /**
     * 向监视容器中添加即将从内存中清除的副本（副本没有人时添加）
     * 
     * @param _dungeion
     */
    public boolean addDungeonToMonitor (Dungeon _dungeion)
    {
        if (!dungeonListThatWillClear.contains(_dungeion))
        {
            synchronized (dungeonListThatWillClear)
            {
                return dungeonListThatWillClear.add(_dungeion);
            }
        }

        return false;
    }

    /**
     * 从监视容器中删除即将从内存中清除的副本（副本又有玩家进入时添加）
     * 
     * @param _dungeion
     */
    public boolean removeDungeonFromMonitor (Dungeon _dungeion)
    {
        synchronized (dungeonListThatWillClear)
        {
            return dungeonListThatWillClear.remove(_dungeion);
        }
    }

    /**
     * 单例
     */
    private static DungeonInstanceManager instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static DungeonInstanceManager getInstance ()
    {
        if (null == instance)
        {
            instance = new DungeonInstanceManager();
        }

        return instance;
    }

    /**
     * 清除进度副本
     */
    public void clearHistoryDungeon (int _historyID)
    {
        synchronized (historyDungeonTable)
        {
            Dungeon dungeon = historyDungeonTable.remove(_historyID);

            if (null != dungeon)
            {
                if (0 == dungeon.getPlayerNumber())
                {
                    synchronized (dungeonListThatWillClear)
                    {
                        dungeonListThatWillClear.remove(dungeon);
                    }
                }
                else
                {
                    ArrayList<HeroPlayer> playerList = dungeon.getPlayerList();

                    for (HeroPlayer player : playerList)
                    {
                        // 传送出所在地图设定的地图
                        // 通知
                    }
                }

                dungeon.destroy();

                synchronized (historyDungeonList)
                {
                    historyDungeonList.remove(dungeon);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    public void run ()
    {
        synchronized (dungeonListThatWillClear)
        {
            Dungeon dungeon;

            for (int i = 0; i < dungeonListThatWillClear.size();)
            {
                dungeon = dungeonListThatWillClear.get(i);

                if (System.currentTimeMillis() - dungeon.getTimeOfNobody() >= DUNGEON_EXISTS_TIME_IN_MEMERY)
                {
                    dungeon.destroy();

                    if (dungeon.getPattern() == Dungeon.PATTERN_OF_DIFFICULT
                            || dungeon.getPlayerNumberLimit() == Dungeon.MEMBER_NUMBER_OF_RAID)
                    {
                        historyDungeonList.remove(dungeon);
                        historyDungeonTable.remove(dungeon.getHistoryID());
                    }
                    else
                    {
                        noneHistoryDungeonList.remove(dungeon);
                    }

                    dungeonListThatWillClear.remove(i);
                }
                else
                {
                    i++;
                }
            }
        }
    }

    /**
     * 副本无人时在内存中的存在时间
     */
    private static final long DUNGEON_EXISTS_TIME_IN_MEMERY = 30 * 60 * 1000;

    /**
     * 启动延时
     */
    public static final int   START_RELAY                   = 30 * 1000;

    /**
     * 副本从内存中清除条件检查间隔时间
     */
    public static final long  CHECK_PERIOD                  = 200 * 1000;
}
