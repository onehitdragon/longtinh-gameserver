package hero.micro.sports;

import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SportsDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-15 下午02:06:54
 * @描述 ：竞技DAO
 */

public class SportsDAO
{
    /**
     * 加载竞技场积分脚本
     */
    private static final String SELECT_POINT_SQL = "SELECT * FROM sports_point"
                                                         + " WHERE user_id = ? LIMIT 1";

    /**
     * 插入竞技场积分脚本
     */
    private static final String INSERT_POINT_SQL = "INSERT INTO sports_point VALUES(?,?,?,?,?)";

    /**
     * 更新竞技场积分脚本
     */
    private static final String UPDATE_POINT_SQL = "UPDATE sports_point SET c_y = ? ,"
                                                         + " y_l = ? , t_y = ? , s_w = ?"
                                                         + " WHERE user_id = ? LIMIT 1";

    /**
     * 加载竞技场等级信息
     * 
     * @param _userID
     * @return
     */
    public static short[] loadSportsPoint (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_POINT_SQL);

            pstm.setInt(1, _userID);
            set = pstm.executeQuery();

            if (set.next())
            {
                short[] sportsPointList = new short[4];

                sportsPointList[ESportsClan.CHI_YOU_MAN_YI.getID() - 1] = set
                        .getShort("c_y");
                sportsPointList[ESportsClan.YAN_LONG_YONG_SHI.getID() - 1] = set
                        .getShort("y_l");
                sportsPointList[ESportsClan.TIAN_YU_ZHI_JUN.getID() - 1] = set
                        .getShort("t_y");
                sportsPointList[ESportsClan.SHUN_WANG_WEI_DUI.getID() - 1] = set
                        .getShort("s_w");

                return sportsPointList;
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

        return null;
    }

    /**
     * 插入竞技场等级信息
     * 
     * @param _userID
     * @return
     */
    public static short[] insertSportsPoint (int _userID,
            short[] _sportsPointList)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_POINT_SQL);

            pstm.setInt(1, _userID);
            pstm.setShort(2, _sportsPointList[ESportsClan.CHI_YOU_MAN_YI
                    .getID() - 1]);
            pstm.setShort(3, _sportsPointList[ESportsClan.YAN_LONG_YONG_SHI
                    .getID() - 1]);
            pstm.setShort(4, _sportsPointList[ESportsClan.TIAN_YU_ZHI_JUN
                    .getID() - 1]);
            pstm.setShort(5, _sportsPointList[ESportsClan.SHUN_WANG_WEI_DUI
                    .getID() - 1]);

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

        return null;
    }

    /**
     * 更新竞技场等级信息
     * 
     * @param _userID 玩家编号
     * @param _sportsPointList 竞技场势力等级列表
     * @return
     */
    public static short[] updateSportsPoint (int _userID,
            short[] _sportsPointList)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_POINT_SQL);

            pstm.setShort(1, _sportsPointList[ESportsClan.CHI_YOU_MAN_YI
                    .getID() - 1]);
            pstm.setShort(2, _sportsPointList[ESportsClan.YAN_LONG_YONG_SHI
                    .getID() - 1]);
            pstm.setShort(3, _sportsPointList[ESportsClan.TIAN_YU_ZHI_JUN
                    .getID() - 1]);
            pstm.setShort(4, _sportsPointList[ESportsClan.SHUN_WANG_WEI_DUI
                    .getID() - 1]);

            pstm.setInt(5, _userID);
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

        return null;
    }
}
