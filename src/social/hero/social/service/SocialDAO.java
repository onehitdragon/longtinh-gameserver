package hero.social.service;

import hero.share.service.LogWriter;
import hero.social.ESocialRelationType;
import hero.social.SocialRelationList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SocialDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-9 上午10:08:23
 * @描述 ：社交关系数据库DAO（包括好友、仇人、屏蔽三种关系）
 */

public class SocialDAO
{
    private static Logger log = Logger.getLogger(SocialDAO.class);
    /**
     * 插入社交关系SQL脚本
     */
    public static String SQL_OF_INSERT     = "INSERT INTO social VALUES(?,?,?,?)";

    /**
     * 删除单个社交关系脚本
     */
    public static String SQL_OF_REMOVE_ONE = "DELETE FROM social WHERE user_id=? AND member_user_id=? LIMIT 1";

    /**
     * 删除单个社交关系脚本
     */
    public static String SQL_OF_REMOVE_ALL = "DELETE FROM social WHERE user_id=? LIMIT 200";

    /**
     * 删除被动单个社交关系脚本
     */
    public static String SQL_OF_BE_REMOVED = "DELETE FROM social WHERE member_user_id=?";

    /**
     * 加载所有社交关系脚本
     */
    public static String SQL_OF_LOAD       = "SELECT * FROM social WHERE user_id=? LIMIT 200";

    /**
     * 添加
     * 
     * @param _userID
     * @param _memberUserID
     * @param _memberName
     * @param _type
     */
    public static void add (int _userID, int _memberUserID, String _memberName,
            byte _type, byte _vocation, short _lvl)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_INSERT);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _memberUserID);
            pstm.setString(3, _memberName);
            pstm.setInt(4, _type);
//            pstm.setInt(5, _vocation);
//            pstm.setInt(6, _lvl);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
        	log.error("添加失败:" + SQL_OF_INSERT);
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
     * 删除单个社交关系
     * 
     * @param _userID
     * @param _memberUserID
     */
    public static void removeOne (int _userID, int _memberUserID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_REMOVE_ONE);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _memberUserID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
        	log.error("删除社交关系失败:" + SQL_OF_REMOVE_ONE);
//            LogWriter.error(null, ex);
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
     * 删除某角色的所有社交关系（当角色被删除时发生此操作）
     * 
     * @param _userID
     */
    public static void removeAll (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_REMOVE_ALL);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();

            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(SQL_OF_BE_REMOVED);
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
     * 从数据库获得_beUser是不是_hostUserID的朋友
     * @param _beUser
     * @param _hostUserID
     * @return
     */
	public static boolean beFriend (String _beUser, int _hostUserID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        boolean result = false;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_LOAD);
            pstm.setInt(1, _hostUserID);
            set = pstm.executeQuery();
            byte type;
            String memberName;

            while (set.next())
            {
                memberName = set.getString("member_name");
                type = set.getByte("social_type");
                if(_beUser.equals(memberName))
                {
                	if(ESocialRelationType.FRIEND.value() == type) {
                		result = true;
                	}
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
        return result;
    }

    /**
     * 加载
     * 
     * @param _userID
     * @param _list
     */
    public static void load (int _userID, SocialRelationList _list)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_LOAD);
            pstm.setInt(1, _userID);
            set = pstm.executeQuery();

            int memberUserID;
            byte type;
            String memberName;

            while (set.next())
            {
                memberUserID = set.getInt("member_user_id");
                memberName = set.getString("member_name");
                type = set.getByte("social_type");

                _list.add(type, memberUserID, memberName);
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
