package hero.manufacture.service;

import hero.manufacture.Manufacture;
import hero.manufacture.ManufactureType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * 制造数据库操作类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 *
 * 现在因为玩家可以学习两个制造技能，所以不能再以玩家USERID为主键了
 * 已经修改表 manuf,加了 id为主健且自增 ---- 2010-12-22 jiaodongjie
 */
public class ManufactureDAO
{
    private final static String UPDATE_MANUF_SQL    = "UPDATE manuf SET lvl = ?,lvl_point = ? WHERE userid = ? and manuf_type=? LIMIT 1";

    private final static String SEL_MANUF_SQL       = "SELECT manuf_type,lvl,lvl_point FROM manuf WHERE userid = ? LIMIT 2";

    private final static String SEL_MANUF_LIST_SQL  = "SELECT manufid FROM manuf_list WHERE userid = ?";

    private static final String DEL_MANUF_SQL       = "DELETE FROM manuf WHERE userid = ? LIMIT 2";

    private static final String DEL_MANUF_LIST_SQL  = "DELETE FROM manuf_list WHERE userid = ? AND manufid = ?";

    private static final String DEL_MANUF_LISTS_SQL = "DELETE FROM manuf_list WHERE userid = ?";

    private static final String ADD_MANUF_LIST_SQL  = "INSERT INTO manuf_list SET userid = ?,manufid = ?";

    private static final String ADD_MANUF_SQL       = "INSERT INTO manuf SET userid = ?,manuf_type=?";

    /**
     * 更新指定玩家的制造技能数据
     * 
     * @param _userID
     * @param _manuf
     */
    public static void updateManuf (int _userID, Manufacture _manuf)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_MANUF_SQL);
            pstm.setByte(1, _manuf.getLvl());
            pstm.setInt(2, _manuf.getPoint());
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
                    pstm.close();
                if (conn != null)
                    conn.close();
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
    public static List<Manufacture> loadManufByUserID (int _userID)
    {
        List<Manufacture> manufList = new ArrayList<Manufacture>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SEL_MANUF_SQL);
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next())
            {
                byte manuf_type = rs.getByte(1);
                byte lvl = rs.getByte(2);
                int point = rs.getInt(3);

                rs.close();
                rs = null;
                pstm.close();
                pstm = null;

                Manufacture manuf = new Manufacture(ManufactureType.get(manuf_type));
                manuf.setLvl(lvl);
                manuf.setPoint(point);
                pstm = conn.prepareStatement(SEL_MANUF_LIST_SQL);
                pstm.setInt(1, _userID);
                rs = pstm.executeQuery();
                while (rs.next())
                {
                    int manufid = rs.getInt(1);
                    manuf.addManufID(manufid);
                }

                manufList.add(manuf);
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
                    rs.close();
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }
        return manufList;
    }

    /**
     * 删除指定玩家的制造技能
     * 
     * @param _userID
     * @param _type
     */
    public static void forgetManufByUserID (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_MANUF_SQL);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
            delManufListsByUserID(_userID);
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
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 删除指定玩家和制造ID的数据
     * 
     * @param _userID
     */
    public static void delManufListByUserID (int _userID, int _manufID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_MANUF_LIST_SQL);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _manufID);
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
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 删除指定玩家的所有制造ID
     * 
     * @param _userID
     */
    public static void delManufListsByUserID (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_MANUF_LISTS_SQL);
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
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 给玩家添加一个制造ID
     * 
     * @param _userID
     * @param _manufID
     */
    public static void addManufID (int _userID, int _manufID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(ADD_MANUF_LIST_SQL);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _manufID);
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
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 添加玩家制造技能数据
     * 
     * @param _userID
     * @param _type
     */
    public static void studyManuf (int _userID, ManufactureType _type)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(ADD_MANUF_SQL);
            pstm.setInt(1, _userID);
            pstm.setByte(2, _type.getID());
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
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }
    }
}
