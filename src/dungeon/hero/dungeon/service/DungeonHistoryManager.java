package hero.dungeon.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import hero.dungeon.Dungeon;
import hero.dungeon.DungeonHistory;
import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonHistoryManager.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-7 上午10:03:33
 * @描述 ：副本进度管理器
 */

public class DungeonHistoryManager
{
    /**
     * 可用的进度编号
     */
    private int                              usableHistoryID;

    /**
     * 进度映射（进度编号：进度）
     */
    private FastMap<Integer, DungeonHistory> historyTable;

    /**
     * 进度列表
     */
    private FastList<DungeonHistory>         historyList;

    /**
     * 副本进度重置计时器
     */
    private Timer                            clearTimer;

    /**
     * 团队副本重置时间
     */
    private Calendar                         raidDungeonRefreshTime;

    /**
     * 5人副本重置时间
     */
    private Calendar                         normalDungeonRefreshTime;

    /**
     * 私有构造
     */
    private DungeonHistoryManager()
    {

    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static DungeonHistoryManager getInstance ()
    {
        if (null == instance)
        {
            instance = new DungeonHistoryManager();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init ()
    {
        if (0 == usableHistoryID)
        {
            historyTable = new FastMap<Integer, DungeonHistory>();
            historyList = new FastList<DungeonHistory>();
            checkInvalidateHistoryOfDB();
            usableHistoryID = DungeonDAO.loadDungeonHistory() + 1;

            Calendar calendar = GregorianCalendar.getInstance();
            int startTime = (60 - calendar.get(Calendar.MINUTE) + 5) * 60 * 1000;
            clearTimer = new Timer();
            clearTimer.schedule(new HistoryClearTast(), startTime,
                    HISTORY_CHECK_INTERVAL);
            resetDungeonRefreshTime();
        }
    }

    /**
     * 重置副本刷新时间
     */
    private void resetDungeonRefreshTime ()
    {
        raidDungeonRefreshTime = GregorianCalendar.getInstance();
        raidDungeonRefreshTime.add(GregorianCalendar.DAY_OF_MONTH, 7);
        raidDungeonRefreshTime
                .set(
                        GregorianCalendar.DAY_OF_WEEK,
                        DungeonServiceImpl.getInstance().getConfig().raid_history_refresh_week + 1);
        raidDungeonRefreshTime.set(Calendar.HOUR_OF_DAY, DungeonServiceImpl
                .getInstance().getConfig().history_refresh_time);
        raidDungeonRefreshTime.set(Calendar.MINUTE, 0);
        raidDungeonRefreshTime.set(Calendar.SECOND, 0);

        normalDungeonRefreshTime = GregorianCalendar.getInstance();
        normalDungeonRefreshTime.add(GregorianCalendar.DAY_OF_MONTH, 1);
        normalDungeonRefreshTime.set(Calendar.HOUR_OF_DAY, DungeonServiceImpl
                .getInstance().getConfig().history_refresh_time);
        normalDungeonRefreshTime.set(Calendar.MINUTE, 0);
        normalDungeonRefreshTime.set(Calendar.SECOND, 0);
    }

    /**
     * 检查数据库过期副本进度
     */
    private void checkInvalidateHistoryOfDB ()
    {
        Calendar raidHistoryCalendar = GregorianCalendar.getInstance();
        raidHistoryCalendar.set(Calendar.DAY_OF_WEEK, DungeonServiceImpl
                .getInstance().getConfig().raid_history_refresh_week + 1);
        raidHistoryCalendar.set(Calendar.HOUR_OF_DAY, DungeonServiceImpl
                .getInstance().getConfig().history_refresh_time);
        raidHistoryCalendar.set(Calendar.MINUTE, 0);
        raidHistoryCalendar.set(Calendar.SECOND, 0);

        Calendar normalGroupHistoryCalendar = GregorianCalendar.getInstance();
        normalGroupHistoryCalendar.set(Calendar.HOUR_OF_DAY, 5);
        normalGroupHistoryCalendar.set(Calendar.MINUTE, 0);
        normalGroupHistoryCalendar.set(Calendar.SECOND, 0);

        DungeonDAO.deleteDungeonHistory(new Timestamp(
                normalGroupHistoryCalendar.getTimeInMillis()), new Timestamp(
                raidHistoryCalendar.getTimeInMillis()));
    }

    /**
     * 添加副本进度
     * 
     * @param _history
     */
    public void addDungeonHistory (DungeonHistory _history)
    {
        if (!historyTable.containsKey(_history.getID()))
        {
            historyTable.put(_history.getID(), _history);
            historyList.add(_history);
        }
    }

    /**
     * 获取进度
     * 
     * @param _historyID
     * @return
     */
    public DungeonHistory getHistory (int _historyID)
    {
        return historyTable.get(_historyID);
    }

    /**
     * 获取玩家副本进度列表
     * 
     * @param _userID
     * @return
     */
    public ArrayList<DungeonHistory> getPlayerHistoryList (int _userID)
    {
        ArrayList<DungeonHistory> list = new ArrayList<DungeonHistory>();

        DungeonHistory history;

        for (int i = 0; i < historyList.size(); i++)
        {
            history = historyList.get(i);

            if (history.containsPlayer(_userID))
            {
                list.add(history);
            }
        }

        if (list.size() == 0)
        {
            return null;
        }

        return list;
    }

    /**
     * 创建进度编号
     * 
     * @return
     */
    public int buildHistoryID ()
    {
        return usableHistoryID++;
    }

    class HistoryClearTast extends TimerTask
    {
        public void run ()
        {
            // TODO Auto-generated method stub
            if (DungeonServiceImpl.getInstance().getConfig().history_refresh_time == GregorianCalendar
                    .getInstance().get(Calendar.HOUR_OF_DAY))
            {
                Calendar raidHistoryRefreshCalendar = GregorianCalendar
                        .getInstance();
                raidHistoryRefreshCalendar
                        .set(
                                Calendar.DAY_OF_WEEK,
                                DungeonServiceImpl.getInstance().getConfig().raid_history_refresh_week + 1);
                raidHistoryRefreshCalendar
                        .set(Calendar.HOUR_OF_DAY, DungeonServiceImpl
                                .getInstance().getConfig().history_refresh_time);
                raidHistoryRefreshCalendar.set(Calendar.MINUTE, 0);
                raidHistoryRefreshCalendar.set(Calendar.SECOND, 0);

                Calendar normalGroupHistoryRefreshCalendar = GregorianCalendar
                        .getInstance();
                normalGroupHistoryRefreshCalendar.set(Calendar.HOUR_OF_DAY, 5);
                normalGroupHistoryRefreshCalendar.set(Calendar.MINUTE, 0);
                normalGroupHistoryRefreshCalendar.set(Calendar.SECOND, 0);

                DungeonDAO.deleteDungeonHistory(new Timestamp(
                        normalGroupHistoryRefreshCalendar.getTimeInMillis()),
                        new Timestamp(raidHistoryRefreshCalendar
                                .getTimeInMillis()));

                synchronized (historyList)
                {
                    Calendar raidHistoryCalendar = GregorianCalendar
                            .getInstance();

                    DungeonHistory history;

                    for (int i = 0; i < historyList.size();)
                    {
                        history = historyList.get(i);

                        if (history.getDungeonType() == Dungeon.TYPE_RAID)
                        {
                            raidHistoryCalendar.setTime(history.getBuildTime());

                            if (raidHistoryCalendar
                                    .before(raidHistoryRefreshCalendar))
                            {
                                historyList.remove(i);
                                historyTable.remove(history.getID());
                                DungeonInstanceManager.getInstance()
                                        .clearHistoryDungeon(history.getID());
                            }
                            else
                            {
                                i++;
                            }
                        }
                        else
                        {
                            historyList.remove(i);
                            historyTable.remove(history.getID());
                            DungeonInstanceManager.getInstance()
                                    .clearHistoryDungeon(history.getID());
                        }
                    }
                }
            }

            resetDungeonRefreshTime();
        }
    }

    /**
     * 获取副本重置时间信息
     * 
     * @return 团队副本和普通副本的重置时间描述
     */
    public String[] getDungeonRefreshTimeInfo ()
    {
        String[] refreshTimeInfo = new String[2];

        long nowMill = System.currentTimeMillis();
        long raidDungeonRefreshMill = raidDungeonRefreshTime.getTimeInMillis();
        long normalDungeonRefreshMill = normalDungeonRefreshTime
                .getTimeInMillis();

        int days = (int) ((raidDungeonRefreshMill - nowMill) / DAY);
        int hour = (int) ((raidDungeonRefreshMill - nowMill) % DAY / HOUR);
        int minute = (int) ((raidDungeonRefreshMill - nowMill) % DAY % HOUR / MINUTE);

        refreshTimeInfo[0] = "距离重置" + days + "天" + hour + "小时" + minute + "分";

        days = (int) ((normalDungeonRefreshMill - nowMill) / DAY);
        hour = (int) ((normalDungeonRefreshMill - nowMill) % DAY / HOUR);
        minute = (int) ((normalDungeonRefreshMill - nowMill) % DAY % HOUR / MINUTE);

        refreshTimeInfo[1] = "距离重置" + days + "天" + hour + "小时" + minute + "分";

        return refreshTimeInfo;
    }

    /**
     * 单例
     */
    private static DungeonHistoryManager instance;

    /**
     * 最小的进度编号
     */
    public final static int              BASAL_HISTORY_ID       = 1000;

    /**
     * 副本从内存中清除条件检查间隔时间
     */
    private static final long            HISTORY_CHECK_INTERVAL = 60 * 60 * 1000;

    /**
     * 一天的毫秒
     */
    private static final long            DAY                    = 24 * 60 * 60 * 1000;

    /**
     * 一小时的毫秒
     */
    private static final long            HOUR                   = 60 * 60 * 1000;

    /**
     * 一分钟的毫秒
     */
    private static final long            MINUTE                 = 60 * 1000;
}
