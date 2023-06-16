package hero.share.cd;

import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import yoyo.service.tools.database.DBServiceImpl;

public class CDTimeDAO
{
    /**
     * 更新CD时间
     * 
     * @param _uid
     * @param _cdType
     * @param _cdTime
     */
    public static void insertCD (int _uid, ArrayList<CDUnit> _list)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);

            pstm = conn
                    .prepareStatement("insert into cd(user_id,cd_type,cd_time,cd_maxtime) values(?,?,?,?)");

            CDUnit cd = null;
            for (int i = 0; i < _list.size(); i++)
            {
                cd = _list.get(i);
                if (cd.getTimeBySec() > 0)
                {
                    pstm.setInt(1, _uid);
                    pstm.setInt(2, cd.getKey());
                    pstm.setInt(3, cd.getTimeBySec());
                    pstm.setInt(4, cd.getMaxTime());
                    pstm.addBatch();
                    // log.info("save cd" + _uid + " " + cd.getKey() +
                    // " " + cd.getTimeBySec());
                }
            }
            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
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
     * 删除该uid所有cd
     * 
     * @param _uid
     * @return
     */
    public static ArrayList<CDUnit> deleteCD (int _uid)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        Statement stam = null;
        ArrayList<CDUnit> array = new ArrayList<CDUnit>();

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            stam = conn.createStatement();
            ResultSet set = stam
                    .executeQuery("select cd_type, cd_time, cd_maxtime from cd where user_id="
                            + _uid);
            int type = 0;
            int time = 0;
            int maxTime = 0;

            if (set != null)
            {
                while (set.next())
                {
                    type = set.getInt("cd_type");
                    time = set.getInt("cd_time");
                    maxTime = set.getInt("cd_maxtime");
                    array.add(new CDUnit(type, time, maxTime));
//                    log.info("load" + _uid + " " + type + " " + time
//                            + " " + maxTime);
                }
            }

            pstm = conn.prepareStatement("delete from cd where user_id=?");
            pstm.setInt(1, _uid);
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
                if (stam != null)
                    stam.close();
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

        return array;
    }
}
