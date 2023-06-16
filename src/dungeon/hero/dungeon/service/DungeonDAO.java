package hero.dungeon.service;

import hero.dungeon.DungeonHistory;
import hero.share.service.LogWriter;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-3 下午06:10:13
 * @描述 ：
 */

public class DungeonDAO
{
    /**
     * 查询所有副本进度信息
     */
    private static final String SELECT_HISTORY_SQL = "SELECT * FROM dungeon_history";

    /**
     * 插入副本进度，在进度产生时发生
     */
    private static final String INSERT_HISTORY_SQL = "INSERT INTO dungeon_history(history_id,dungeon_id,pattern,type,death_boss_list,include_player_list) values(?,?,?,?,?,?)";

    /**
     * 修改副本进度，包括：BOSS死亡、有进度的玩家增加时发生
     */
    private static final String UPDATE_HISTORY_SQL = "UPDATE dungeon_history SET death_boss_list=? ,include_player_list=? WHERE history_id=? LIMIT 1";

    /**
     * 删除过期副本进度
     */
    private static final String DELETE_HISTORY_SQL = "DELETE FROM dungeon_history WHERE (type=1 AND build_time<?) OR (type=2 AND build_time<?)";

    /**
     * 加载副本进度记录
     * 
     * @param _historyContainer
     */
    public static int loadDungeonHistory ()
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        int maxDungeonHistoryID = DungeonHistoryManager.BASAL_HISTORY_ID;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_HISTORY_SQL);
            set = pstm.executeQuery();

            DungeonHistory history;

            while (set.next())
            {
                int historyID = set.getInt("history_id");
                int dungeonID = set.getInt("dungeon_id");
                byte pattern = set.getByte("pattern");
                byte type = set.getByte("type");
                String deathBossList = set.getString("death_boss_list");
                String includePlayer = set.getString("include_player_list");
                Date buildTime = set.getTimestamp("build_time");

                history = new DungeonHistory(historyID, dungeonID, pattern,
                        type, buildTime);
                parseDeathBossList(deathBossList, history);
                parsePlayerList(includePlayer, history);

                DungeonHistoryManager.getInstance().addDungeonHistory(history);

                if (historyID > maxDungeonHistoryID)
                {
                    maxDungeonHistoryID = historyID;
                }
            }

            history = null;

            return maxDungeonHistoryID;
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);

            return -1;
        }
        finally
        {
            try
            {
                if (null != set)
                {
                    set.close();
                }

                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    /**
     * 创建副本进度
     * 
     * @param _history
     */
    public static void buildDungeonHistory (DungeonHistory _history)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_HISTORY_SQL);

            pstm.setInt(1, _history.getID());
            pstm.setInt(2, _history.getDungeonID());
            pstm.setByte(3, _history.getPattern());
            pstm.setByte(4, _history.getDungeonType());

            String dataChars = formatDeathBossList(_history.getDeathBossTable());

            if (null != dataChars)
            {
                pstm.setString(5, dataChars);

                dataChars = formatPlayerUserIDList(_history
                        .getIncludePlayerUserIDList());

                if (null != dataChars)
                {
                    pstm.setString(6, dataChars);
                    pstm.executeUpdate();
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    /**
     * 修改副本进度内容
     * 
     * @param _history
     */
    public static void changeDungeonHistoryContent (DungeonHistory _history)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_HISTORY_SQL);

            String dataChars = formatDeathBossList(_history.getDeathBossTable());

            if (null != dataChars)
            {
                pstm.setString(1, dataChars);

                dataChars = formatPlayerUserIDList(_history
                        .getIncludePlayerUserIDList());

                if (null != dataChars)
                {
                    pstm.setString(2, dataChars);
                    pstm.setInt(3, _history.getID());
                    pstm.executeUpdate();
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    /**
     * 删除无效的副本进度
     * 
     * @param _history
     */
    public static void deleteDungeonHistory (Timestamp _normalGroudHistoryTime,
            Timestamp _raidHistoryTime)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_HISTORY_SQL);
            pstm.setTimestamp(1, _normalGroudHistoryTime);
            pstm.setTimestamp(2, _raidHistoryTime);

            pstm.executeUpdate();
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    /**
     * 解析死亡BOSS列表
     * 
     * @param _deathBossListChar
     * @param _history
     */
    private static void parseDeathBossList (String _deathBossListChar,
            DungeonHistory _history)
    {
        String[] bossInfoList = _deathBossListChar
                .split(SAME_ELEMENT_CONNECTOR);

        for (String bossInfo : bossInfoList)
        {
            String[] detail = bossInfo.split(MONSTER_AND_MAP_CONNECTOR);
            _history.addDeathBoss(detail[0], Short.parseShort(detail[1]));
        }
    }

    /**
     * 将角色编号列表格式化为数据库存储格式
     * 
     * @param _includePlayerUserIDList
     * @return
     */
    private static String formatPlayerUserIDList (
            Vector<Integer> _includePlayerUserIDList)
    {
        if (null != _includePlayerUserIDList
                && _includePlayerUserIDList.size() > 0)
        {
            StringBuffer sb = new StringBuffer();

            for (int userID : _includePlayerUserIDList)
            {
                sb.append(userID).append(SAME_ELEMENT_CONNECTOR);
            }

            return sb.toString();
        }

        return null;
    }

    /**
     * 解析进度包含的玩家编号列表
     * 
     * @param _playerListChar
     * @param _history
     */
    private static void parsePlayerList (String _playerListChar,
            DungeonHistory _history)
    {
        String[] playerIDList = _playerListChar.split(SAME_ELEMENT_CONNECTOR);

        for (String userID : playerIDList)
        {
            _history.addPlayer(Integer.parseInt(userID));
        }
    }

    /**
     * 将BOSS死亡数据格式化为数据库存储格式
     * 
     * @return
     */
    private static String formatDeathBossList (
            HashMap<String, Short> _deathBossTable)
    {
        if (null != _deathBossTable && _deathBossTable.size() > 0)
        {
            StringBuffer defaultChar = new StringBuffer();

            Iterator<String> bossModelIDSet = _deathBossTable.keySet()
                    .iterator();
            String monsterModelID;

            while (bossModelIDSet.hasNext())
            {
                monsterModelID = bossModelIDSet.next();
                defaultChar.append(monsterModelID).append(
                        MONSTER_AND_MAP_CONNECTOR).append(
                        _deathBossTable.get(monsterModelID)).append(
                        SAME_ELEMENT_CONNECTOR);
            }

            return defaultChar.toString();
        }

        return null;
    }

    /**
     * 怪物和地图编号的连接符
     */
    private static final String MONSTER_AND_MAP_CONNECTOR = "#";

    /**
     * 同类元素之间的连接符
     */
    private static final String SAME_ELEMENT_CONNECTOR    = "&";
}
