package hero.guild.service;

import hero.guild.EGuildMemberRank;
import hero.guild.Guild;
import hero.share.service.LogWriter;

import java.sql.*;
import java.util.HashMap;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-12 下午21:00:45
 * @描述 ：公会服务
 */

public class GuildDAO
{
    /**
     * 新成员入会
     */
    private static final String INSERT_GUILD_MEMBER    = "INSERT INTO guild_member(guild_id, user_id, name,rank,create_time,update_time) VALUES(?,?,?,?,?,?)";

    /**
     * 插入新公会
     */
    private static final String INSERT_GUILD           = "INSERT INTO guild(id, name, president_user_id,update_time) VALUES(?,?,?,?)";

    /**
     * 更新会长
     */
    private static final String UPDATE_GUILD_PRESIDENT = "UPDATE guild SET president_user_id=?,update_time=? WHERE id=? LIMIT 1";
    /**
     * 升级公会
     */
    private static final String UPDATE_GUILD_UP_LEVEL  = "UPDATE guild SET level=? WHERE id=? LIMIT 1";

    /**
     * 会员离开公会
     */
    private static final String DELETE_MEMBER          = "DELETE FROM guild_member WHERE user_id=? LIMIT 1";

    /**
     * 公会解散时清除会员
     */
    private static final String CLEAR_GUILD_MEMBER     = "DELETE FROM guild_member WHERE guild_id=?";

    /**
     * 公会解散时删除公会
     */
    private static final String DELETE_GUILD           = "DELETE FROM guild WHERE id=? LIMIT 1";

    /**
     * 改变成员等级
     */
    private static final String UPDATE_GUILD_MEMBER    = "UPDATE guild_member SET rank=?,update_time=? WHERE user_id=? LIMIT 1";

    /**
     * 查询所有公会
     */
    private static final String SELECT_GUILD           = "SELECT * FROM guild";

    /**
     * 查询所有的公会成员
     */
    private static final String SELECT_MEMBER          = "SELECT * FROM guild_member";

    /**
     * 查询最大公会编号
     */
    private static final String SELECT_MAX_GUILD_ID    = "SELECT MAX(id) as max_guild_id from guild";

    /**
     * 创建一个公会
     * 
     * @param _guild 公会
     */
    public static void create (Guild _guild)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            pstm = conn.prepareStatement(INSERT_GUILD);
            pstm.setInt(1, _guild.getID());
            pstm.setString(2, _guild.getName());
            pstm.setInt(3, _guild.getPresident().userID);
            pstm.setTimestamp(4,new Timestamp(System.currentTimeMillis()));

            pstm.executeUpdate();

            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(INSERT_GUILD_MEMBER);
            pstm.setInt(1, _guild.getID());
            pstm.setInt(2, _guild.getPresident().userID);
            pstm.setString(3, _guild.getPresident().name);
            pstm.setByte(4, EGuildMemberRank.PRESIDENT.value());
            pstm.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
            pstm.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 公会添加一个玩家
     * 
     * @param _guild 公会编号
     * @param _memberUserID 新成员编号
     * @param _memberName 新成员名字
     */
    public static boolean add (int _guild, int _memberUserID, String _memberName)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_GUILD_MEMBER);
            pstm.setInt(1, _guild);
            pstm.setInt(2, _memberUserID);
            pstm.setString(3, _memberName);
            pstm.setByte(4, EGuildMemberRank.NORMAL.value());
            pstm.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
            pstm.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

            pstm.executeUpdate();

            return true;
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 一个玩家离开公会
     * 
     * @param _userID
     */
    public static void removeGuildMember (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_MEMBER);
            pstm.setInt(1, _userID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 公会升级
     * 
     * @param _guildID 公会编号
     * @param _newLevel 新等级
     */
    public static void guildUpLevel (int _guildID, int _newLevel)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_GUILD_UP_LEVEL);
            pstm.setInt(1, _newLevel);
            pstm.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            pstm.setInt(2, _guildID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新会长
     * 
     * @param _guildID 公会编号
     * @param _newPresidentUserID 新会长角色编号
     */
    public static void updatePresident (int _guildID, int _newPresidentUserID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_GUILD_PRESIDENT);
            pstm.setInt(1, _newPresidentUserID);
            pstm.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _guildID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 公会解散
     * 
     * @param _guildID
     */
    public static void distory (int _guildID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            pstm = conn.prepareStatement(CLEAR_GUILD_MEMBER);
            pstm.setInt(1, _guildID);

            pstm.executeUpdate();
            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(DELETE_GUILD);
            pstm.setInt(1, _guildID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 改变公会成员等级
     * 
     * @param _userID
     * @param _memberRank
     */
    public static void changeMemberRank (int _userID,
            EGuildMemberRank _memberRank)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_GUILD_MEMBER);

            pstm.setInt(1, _memberRank.value());
            pstm.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _userID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 改变公会成员等级
     * 
     * @param _guild
     * @param _userID
     * @param _memberRank
     */
    public static void transferPresident (int _guildID,
            int _oldPresidentUserID, int _newPresidentUserID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(UPDATE_GUILD_MEMBER);

            pstm.setInt(1, EGuildMemberRank.PRESIDENT.value());
            pstm.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _newPresidentUserID);

            pstm.addBatch();

            pstm.setInt(1, EGuildMemberRank.OFFICER.value());
            pstm.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _oldPresidentUserID);

            pstm.addBatch();

            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(UPDATE_GUILD_PRESIDENT);
            pstm.setInt(1, _newPresidentUserID);
            pstm.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _guildID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 载入所有的公会
     * 
     * @param _guildTable 公会容器
     */
    public static void load (HashMap<Integer, Guild> _guildTable)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_MAX_GUILD_ID);
            set = pstm.executeQuery();

            if (set.next())
            {
                int maxGuildID = set.getInt("max_guild_id");

                if (maxGuildID > 0)
                {
                    GuildServiceImpl.getInstance().setUseableGuildID(
                            ++maxGuildID);
                }
            }

            set.close();
            set = null;
            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(SELECT_GUILD);
            set = pstm.executeQuery();

            int guildID, gjuildLevel;
            String guildName;

            while (set.next())
            {
                guildID = set.getInt("id");
                guildName = set.getString("name");
                gjuildLevel  =set.getInt("level");
                //edit by zhengl; date: 2011-03-20; note: 添加公会等级
                _guildTable.put(guildID, new Guild(guildID, guildName, gjuildLevel));
            }

            set.close();
            set = null;
            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(SELECT_MEMBER);
            set = pstm.executeQuery();

            Guild guild;
            int memberUserID;
            String memeberName;
            byte rank;

            while (set.next())
            {
                guildID = set.getInt("guild_id");
                memberUserID = set.getInt("user_id");
                memeberName = set.getString("name");
                rank = set.getByte("rank");

                guild = GuildServiceImpl.getInstance().getGuild(guildID);

                if (guild != null)
                {
                    guild.add(memberUserID, memeberName, rank);
                }
            }
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
