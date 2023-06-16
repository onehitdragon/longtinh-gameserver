package hero.novice.service;

import hero.player.HeroPlayer;
import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NoviceDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-27 上午11:59:11
 * @描述 ：
 */

public class NoviceDAO
{
    /**
     * 完成新手引导后更新相应数据
     */
    private static final String UPDATE_PLAYER_AFTER_COMPLETE_SQL = "UPDATE player SET lvl=?,money=?,novice=?,"
                                                                         + "where_id=?,where_x=?,where_y=?"
                                                                         + " where user_id=? LIMIT 1";

    /**
     * 推出新手引导后更新相应数据
     */
    private static final String UPDATE_PLAYER_WHEN_EXIT_SQL      = "UPDATE player SET novice=?,where_id=?,"
                                                                         + "where_x=?,where_y=?"
                                                                         + " where user_id=? LIMIT 1";

    /**
     * 完成新手引导后更新数据库
     * 
     * @param _playerUserID
     */
    public static void completeNoviceTeaching (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER_AFTER_COMPLETE_SQL);

            ps.setShort(1, _player.getLevel());
            ps.setInt(2, _player.getMoney());
            ps.setShort(3, (short) 0);
            ps.setInt(4, _player.where().getID());
            ps.setShort(5, _player.getCellX());
            ps.setShort(6, _player.getCellY());
            ps.setInt(7, _player.getUserID());

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (ps != null)
                {
                    ps.close();
                    ps = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 退出新手引导后更新地图信息
     * 
     * @param _playerUserID
     */
    public static void exitNoviceTeaching (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER_WHEN_EXIT_SQL);

            ps.setShort(1, (short) 0);
            ps.setInt(2, _player.where().getID());
            ps.setShort(3, _player.getCellX());
            ps.setShort(4, _player.getCellY());
            ps.setInt(5, _player.getUserID());

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (ps != null)
                {
                    ps.close();
                    ps = null;
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
