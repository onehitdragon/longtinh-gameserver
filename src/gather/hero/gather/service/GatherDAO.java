package hero.gather.service;

import hero.gather.Gather;
import hero.gather.MonsterSoul;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * 采集数据库操作类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class GatherDAO
{
    private final static String UPDATE_GATHER_SQL       = "UPDATE gather SET lvl = ?,lvl_point = ? WHERE userid = ? LIMIT 1";

    private final static String SEL_GATHER_SQL          = "SELECT lvl,lvl_point FROM gather WHERE userid = ? LIMIT 1";

    private final static String SEL_GATHER_LIST_SQL     = "SELECT soulid,num FROM gather_list WHERE userid = ?";

    private final static String SEL_GATHER_REFINEDS_SQL = "SELECT refinedid FROM gather_refineds WHERE userid = ?";

    private static final String ADD_GATHER_SQL          = "INSERT INTO gather SET userid = ?,gather_type=?";

    private static final String DEL_GATHER_SQL          = "DELETE FROM gather WHERE userid = ? LIMIT 1";

    private static final String ADD_REFINED_SQL         = "INSERT INTO gather_refineds SET userid = ?,refinedid=?";

    private static final String DEL_REFINED_SQL         = "DELETE FROM gather_refineds WHERE userid = ?";

    private static final String ADD_GATHER_LIST_SQL     = "INSERT INTO gather_list SET userid = ?,ind = ?,soulid=?,num=?";

    private static final String DEL_GATHER_LISTS_SQL    = "DELETE FROM gather_list WHERE userid = ?";

    private static final String DEL_GATHER_LIST_SQL     = "DELETE FROM gather_list WHERE userid = ? AND ind = ?";

    /**
     * 更新指定玩家ID的采集数据
     * 
     * @param _userID
     * @param _gather
     */
    public static void updateGather (int _userID, Gather _gather)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_GATHER_SQL);
            pstm.setByte(1, _gather.getLvl());
            pstm.setInt(2, _gather.getPoint());
            pstm.setInt(3, _userID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 加载指定玩家的制造技能数据
     * 
     * @param _userID
     * @return
     */
    public static Gather loadGatherByUserID (int _userID)
    {
        Gather gather = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SEL_GATHER_SQL);
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next())
            {
                byte lvl = rs.getByte(1);
                int point = rs.getInt(2);

                rs.close();
                rs = null;
                pstm.close();
                pstm = null;

                gather = new Gather();
                gather.setLvl(lvl);
                gather.setPoint(point);
                pstm = conn.prepareStatement(SEL_GATHER_LIST_SQL);
                pstm.setInt(1, _userID);
                rs = pstm.executeQuery();
                while (rs.next())
                {
                    int soulid = rs.getInt(1);
                    byte num = rs.getByte(2);
                    gather.loadMonsterSoul(new MonsterSoul(soulid, num));
                }

                rs.close();
                rs = null;

                pstm.close();
                pstm = null;

                pstm = conn.prepareStatement(SEL_GATHER_REFINEDS_SQL);
                pstm.setInt(1, _userID);
                rs = pstm.executeQuery();

                while (rs.next())
                {
                    int _refinedID = rs.getInt(1);
                    gather.addRefinedID(_refinedID);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                    rs = null;
                }
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
        return gather;
    }

    /**
     * 添加玩家采集技能
     * 
     * @param _userID
     */
    public static void studyGather (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(ADD_GATHER_SQL);
            pstm.setInt(1, _userID);
            pstm.setByte(2, (byte) 0);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 玩家遗忘采集技能
     * 
     * @param _userID
     * @param _type
     */
    public static void forgetGatherByUserID (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_GATHER_SQL);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
            delRefinedByUserID(_userID);
            delGatherByUserID(_userID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    public static void addRefinedID (int _userID, int _refinedID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(ADD_REFINED_SQL);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _refinedID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 删除指定玩家的所有采集数据
     * 
     * @param _userID
     */
    private static void delRefinedByUserID (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_REFINED_SQL);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 保存玩家的采集灵魂数据
     * 
     * @param _userID
     * @param _souls
     */
    public static void saveGahterByUserID (int _userID,
            ArrayList<MonsterSoul> _souls)
    {
        delGatherByUserID(_userID);
        Connection conn = null;
        PreparedStatement pstm = null;

        if (_souls.size() > 0)
        {
            try
            {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(ADD_GATHER_LIST_SQL);

                byte i = 0;

                for (MonsterSoul s : _souls)
                {
                    pstm.setInt(1, _userID);
                    pstm.setByte(2, i++);
                    pstm.setInt(3, s.soulID);
                    pstm.setByte(4, s.num);
                    pstm.addBatch();
                }

                pstm.executeBatch();
                conn.setAutoCommit(true);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    if (pstm != null)
                    {
                        pstm.close();
                        pstm = null;
                    }
                    if (conn != null)
                    {
                        conn.close();
                        conn = null;
                    }
                }
                catch (SQLException e)
                {
                }
            }
        }
    }

    /**
     * 删除指定玩家的所有采集数据
     * 
     * @param _userID
     */
    public static void delGatherByUserID (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_GATHER_LISTS_SQL);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 删除指定玩家指定索引的采集数据
     * 
     * @param _userID
     * @param _index
     */
    public static void delGatherByUserID (int _userID, int _index)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_GATHER_LIST_SQL);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _index);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }
}
