package hero.lover.service;

import hero.lover.service.LoverServiceImpl.LoverStatus;
import hero.lover.service.LoverServiceImpl.MarryStatus;
import hero.player.HeroPlayer;
import hero.share.service.LogWriter;
import hero.share.service.ShareServiceImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;

/**
 * 结婚DAO
 * 
 * @author Luke 陈路
 * @date Jul 30, 2009
 */
public class LoverDAO
{
    private static Logger log = Logger.getLogger(LoverDAO.class);
    /* success字段含义: 0没有关系，1恋人，2夫妻 */

    /**
     * 删除两个人的关系
     * 
     * @param _name 删除名称
     */
    public static void deletePlayer (String _name)
    {
        Connection conn = null;
        Statement stmt = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("delete from lover where roleA='" + _name
                    + "' OR roleB='" + _name + "'");
        }
        catch (Exception ex)
        {
            LogWriter.error("删除两个人的关系", ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null)
                    stmt.close();
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
     * 注册结婚
     * 
     * @param _name1 双方名称
     * @param _name2
     * @return
     */
    public static MarryStatus registerMarriage (String _name1, String _name2)
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        MarryStatus status = MarryStatus.NOT_LOVER;
        short sta = 0;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt
                    .executeQuery("SELECT success FROM lover WHERE ((roleA='"
                            + _name1
                            + "' AND roleB='"
                            + _name2
                            + "') OR (roleA='"
                            + _name2
                            + "' AND roleB='"
                            + _name1 + "')) LIMIT 1");

            if (result.next())
            {
                sta = result.getShort(1);
            }

            if (sta == MarryStatus.LOVED_SUCCESS.getStatus())  //双方成为恋人 可以结婚，把时间修改为现在的结婚时间
            {
                stmt.close();
                stmt = null;

                stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE lover SET success="+MarryStatus.SUCCESS.getStatus()+",register_date='"+ShareServiceImpl .DateTimeToString(new Date())
                                +"' WHERE ((roleA='"+ _name1 + "' AND roleB='"+ _name2 + "') "
                                + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "')) LIMIT 1");
                status = MarryStatus.SUCCESS;
            }else if(sta == MarryStatus.SUCCESS.getStatus()){
                status = MarryStatus.MARRIED;
            }else{
                status = MarryStatus.NOT_LOVER;
            }

        }
        catch (Exception ex)
        {
            LogWriter.error("注册结婚", ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return status;
    }

    /**
     * 离婚
     * @param name1
     * @param name2
     * @return
     */
    public static MarryStatus divorceMarriage(String name){
        MarryStatus status;
        String otherName = whoLoveMe(name);
        if(otherName == null){
            otherName = whoMarriedMe(name);
            if(otherName == null){
                status = MarryStatus.NOT_LOVER; //这里有可能是未订婚且未结婚的状态
            }else{
                updateMarryStatus(name,otherName,MarryStatus.DIVORCE_SUCCESS);
                status = MarryStatus.DIVORCE_SUCCESS; //离婚成功
            }
        }else{
            status = MarryStatus.LOVED_NO_MARRY; //只是恋人,未结婚
        }
        return status;
    }

    /**
     * 结婚进入礼堂失败后，把两人的关系重新设置为恋人
     * @param _name1
     * @param _name2
     */
    public static void marryFaild(String _name1, String _name2){
        Connection conn = null;
        Statement stmt = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE lover SET success="+MarryStatus.LOVED_SUCCESS.getStatus()+",register_date='"+ShareServiceImpl .DateTimeToString(new Date())
                            +"' WHERE ((roleA='"+ _name1 + "' AND roleB='"+ _name2 + "') "
                            + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "')) LIMIT 1");
            stmt.close();
            conn.close();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null)
                    stmt.close();
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
     * 成为恋人
     * @param _name1
     * @param name2
     * @return
     */
    public static MarryStatus propose(String _name1,String _name2){
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT success FROM lover WHERE (roleA='"+ _name1 + "' AND roleB='"+ _name2 + "') " +
                    "OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "') LIMIT 1");
            if(result.next()){
                int success = result.getInt(1);
                log.debug("resutl success = " + success);
                if(success == MarryStatus.LOVED_SUCCESS.getStatus()){
                    return MarryStatus.LOVED_NO_MARRY;
                }else if(success == MarryStatus.SUCCESS.getStatus()){
                    return MarryStatus.MARRIED;
                }else if(success == MarryStatus.BREAK_UP.getStatus()
                        || success == MarryStatus.DIVORCE_SUCCESS.getStatus()){
                    result.close();
                    stmt.close();

                    stmt = conn.createStatement();
                    stmt.executeUpdate("UPDATE lover SET success="+MarryStatus.LOVED_SUCCESS.getStatus()+",register_date='"+ShareServiceImpl.DateTimeToString(new Date())
                            +"' WHERE (roleA='"+ _name1 + "' AND roleB='"+ _name2 + "') "
                            + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "') LIMIT 1");
                    stmt.close();
                    return MarryStatus.LOVED_SUCCESS;
                }
            }else{
                result.close();
                stmt.close();

                stmt = conn.createStatement();
                stmt.executeUpdate("INSERT INTO lover(roleA,roleB,success,register_date) VALUES ('"
                                    + _name1
                                    + "','"
                                    + _name2
                                    + "', "+MarryStatus.LOVED_SUCCESS.getStatus()+", '"
                                    + ShareServiceImpl.DateTimeToString(new Date())
                                    + "')");
                stmt.close();
                return MarryStatus.LOVED_SUCCESS;
            }

        }
        catch (Exception ex)
        {
            log.error("成为恋人 error: ",ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return MarryStatus.NOT_LOVER;
    }

    /**
     * 修改状态
     * @param _name1
     * @param _name2
     * @param status
     */
    public static void updateMarryStatus(String _name1, String _name2, MarryStatus status){
        Connection conn = null;
        Statement stmt = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE lover SET success="+status.getStatus()+",register_date='"+ShareServiceImpl.DateTimeToString(new Date())
                            +"' WHERE ((roleA='"+ _name1 + "' AND roleB='"+ _name2 + "') "
                            + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "')) LIMIT 1");
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }finally
        {
            try
            {
                if (stmt != null)
                    stmt.close();
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
     * 大榕树下的签名
     *  现在不用大榕树了,此方法丢弃 --jiaodongjie
     * @param _name1 双方名称
     * @param _name2
     * @return
     */
    /*public static LoverStatus registerLover (String _name1, String _name2)
    {
        *//* uid1 is inviter *//*

        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        LoverStatus status = LoverStatus.NONE;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt
                    .executeQuery("SELECT success FROM lover WHERE success=0 AND roleA='"
                            + _name1 + "'");
            if (result.next())
            {
                status = LoverStatus.REGISTERED; // 已经登记过了
            }

            if (status != LoverStatus.REGISTERED)
            {
                // 自己已有恋人
                result = stmt
                        .executeQuery("SELECT success FROM lover WHERE success=1 AND (roleA='"
                                + _name1 + "' OR roleB='" + _name1 + "')");
                if (result.next())
                {
                    status = LoverStatus.ME_SUCCESSED;
                }
            }

            if (status != LoverStatus.REGISTERED
                    && status != LoverStatus.ME_SUCCESSED)
            {
                // 对方已有恋人
                result = stmt
                        .executeQuery("SELECT success FROM lover WHERE success=1 AND (roleA='"
                                + _name2 + "' OR roleB='" + _name2 + "')");
                if (result.next())
                {
                    status = LoverStatus.THEM_SUCCESSED;
                }
            }

            if (status != LoverStatus.REGISTERED
                    && status != LoverStatus.ME_SUCCESSED
                    && status != LoverStatus.THEM_SUCCESSED)
            {
                result = stmt
                        .executeQuery("SELECT success FROM lover WHERE success=0 AND (roleA='"
                                + _name2 + "' AND roleB='" + _name1 + "')");
                if (result.next())
                {
                    // 符合恋人条件
                    stmt
                            .executeUpdate("UPDATE lover SET success=1 WHERE roleA='"
                                    + _name2 + "' AND roleB='" + _name1 + "'");
                    status = LoverStatus.SUCCESS;
                }
                else
                {
                    // 新登记
                    stmt
                            .executeUpdate("INSERT INTO lover(roleA,roleB,success,register_date) VALUES ('"
                                    + _name1
                                    + "','"
                                    + _name2
                                    + "', 0, '"
                                    + ShareServiceImpl
                                            .DateTimeToString(new Date())
                                    + "')");
                    status = LoverStatus.REGISTER;
                }
            }
        }
        catch (Exception ex)
        {
            // LogWriter.error(null, ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return status;
    }*/



    /**
     * 删除超过1月的记录
     */
    public static void deleteTimeOut ()
    {
        Connection conn = null;
        Statement stmt = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();

            Calendar calendar = Calendar.getInstance();
            String date = "" + calendar.get(Calendar.YEAR) + "-"
                    + (calendar.get(Calendar.MONTH) + 1) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH);
            stmt.executeUpdate("DELETE FROM wedding WHERE wed_date<'" + date
                    + "'");

            calendar.add(Calendar.DAY_OF_MONTH, -7);
            String before = ShareServiceImpl.DateTimeToString(calendar
                    .getTime());

            stmt
                    .executeUpdate("DELETE FROM lover WHERE success=0 AND register_date < '"
                            + before + "'");
        }
        catch (Exception ex)
        {
            // LogWriter.error(null, ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null)
                    stmt.close();
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
     * 某玩家是否与目标群体中的某一个结婚
     * 
     * @param _src 某玩家
     * @param _player 目标群体
     * @return
     */
    public static String[] hasMarried (String _srcName,
            ArrayList<HeroPlayer> _player)
    {
        if (_player.size() == 0)
            return null;

        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        String str = "SELECT * FROM lover WHERE ";

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            String tmpName = null;
            String[] family = new String[2];

            for (int i = 0; i < _player.size(); i++)
            {
                tmpName = _player.get(i).getName();
                str += "((roleA like '" + _srcName + "' AND roleB like '"
                        + tmpName + "') OR ";
                str += "(roleA like '" + tmpName + "' AND roleB like '"
                        + _srcName + "'))";

                if (i != _player.size() - 1)
                {
                    str += " OR ";
                }
                else
                {
                    str += " LIMIT 1";
                }
            }
            
            result = stmt.executeQuery(str);

            if (result.next())
            {
                family[0] = result.getString("roleA");
                family[1] = result.getString("roleB");

                return family;
            }
        }
        catch (Exception ex)
        {
            LogWriter.error(str, ex);
            // ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 谁与目标结婚
     * 
     * @param _name
     * @return
     */
    public static String whoMarriedMe (String _name)
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM lover WHERE (roleA='"
                    + _name + "' OR roleB='" + _name
                    + "') AND success="+MarryStatus.SUCCESS.getStatus()+" limit 1");
            if (result.next())
            {
                String roleA = result.getString("roleA");
                String roleB = result.getString("roleB");
                return _name.equalsIgnoreCase(roleA) ? roleB : roleA;
            }
        }
        catch (Exception ex)
        {
            // LogWriter.error(null, ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 谁是目标的恋人
     * 
     * @param _name
     * @return
     */
    public static String whoLoveMe (String _name)
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM lover WHERE (roleA='"
                    + _name + "' OR roleB='" + _name
                    + "') AND success="+MarryStatus.LOVED_SUCCESS.getStatus()+" limit 1");
            if (result.next())
            {
                String roleA = result.getString("roleA");
                String roleB = result.getString("roleB");
                return _name.equalsIgnoreCase(roleA) ? roleB : roleA;
            }
        }
        catch (Exception ex)
        {
            // LogWriter.error(null, ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 婚礼预定
     * 
     * @param _src 预定人
     * @param _date 预定日期
     */
    public static boolean registerWedding (String _date, String _name)
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM wedding WHERE wed_date='"
                    + _date + "' limit 1");
            while (result.next())
            {
                return false;
            }
            stmt.executeUpdate("INSERT INTO wedding VALUES('" + _date + "','"
                    + _name + "')");
        }
        catch (Exception ex)
        {
            // LogWriter.error(null, ex);
            ex.printStackTrace();
            return false;
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 某日是否已经被预订
     * 
     * @param _date 预定日期
     * @return
     */
    public static String whoWedding (String _date)
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM wedding WHERE wed_date='"
                    + _date + "' limit 1");
            while (result.next())
            {
                return result.getString("user_name");
            }
        }
        catch (Exception ex)
        {
            // LogWriter.error(null, ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
