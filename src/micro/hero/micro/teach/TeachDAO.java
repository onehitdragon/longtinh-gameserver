package hero.micro.teach;

import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MicroDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-19 下午03:57:37
 * @描述 ：
 */

public class TeachDAO
{
    private static Logger log = Logger.getLogger(TeachDAO.class);
    /**
     * 加载师徒关系脚本
     */
    private static final String SELECT_TEACH_SQL         = "SELECT * FROM master_apprentice"
                                                                 + " WHERE apprentice_user_id = ?"
                                                                 + " OR master_user_id = ? LIMIT 6";

    private static final String SELECT_TEACH_BY_NAME_SQL = "select * from master_apprentice where apprentice_name=? " +
            " or master_name=? limit 6";

    /**
     * 插入师徒关系脚本
     */
    private static final String INSERT_MASTER_APPRENTICE = "INSERT INTO master_apprentice(apprentice_user_id,"
                                                                 + "apprentice_name,master_user_id,master_name)"
                                                                 + " VALUES(?,?,?,?)";

    /**
     * 修改师徒关系脚本
     */
    private static final String UPDATE_MASTER_APPRENTICE = "UPDATE master_apprentice SET teach_times"
                                                                 + " = ?,level_of_last_teach = ?"
                                                                 + " WHERE apprentice_user_id = ? LIMIT 1";

    /**
     * 删除师徒关系脚本
     */
    private static final String DELETE_MASTER_APPRENTICE = "DELETE FROM master_apprentice WHERE "
                                                                 + "apprentice_user_id=? LIMIT 1";
    /**
     * 删除师傅的所有师徒关系
     */
    private static final String DELETE_All_MASTER_APPRENTICE_BY_MASTER = "DELETE FROM master_apprentice WHERE "
                                                                 + "master_user_id=? LIMIT 5";

    /**
     * 删除玩家所有师徒关系脚本
     */
    private static final String DELETE_ALL_RELATION_SQL  = "DELETE FROM master_apprentice"
                                                                 + " WHERE apprentice_user_id = ?"
                                                                 + " OR master_user_id = ? LIMIT 6";

    /**
     * 加载师徒关系信息
     * 
     * @param _userID
     * @return
     */
    public static void loadMasterApprenticeRelation (int _userID,
            MasterApprentice _masterApprentice)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_TEACH_SQL);

            pstm.setInt(1, _userID);
            pstm.setInt(2, _userID);
            set = pstm.executeQuery();

            int masterUserID, apprenticeUserID;
            byte teachTimes;
            short levelOfLastTeach;
            String masterName, apprenticeName;

            if (set.next())
            {
                apprenticeUserID = set.getInt(1);
                apprenticeName = set.getString(2);
                masterUserID = set.getInt(3);
                masterName = set.getString(4);
                teachTimes = set.getByte(5);
                levelOfLastTeach = set.getShort(6);

                if (apprenticeUserID == _userID) //userID 是徒弟
                {
                    log.debug("load masterapprentice .. user is apprenticeUser... masterUserID="+masterUserID);
                    _masterApprentice
                            .setMaster(masterUserID, masterName, false);
                }
                else  //userID 是师傅
                {
                    log.debug("load masterapprentice .. user is master... apprenticeUserID="+apprenticeUserID);
                    _masterApprentice.addNewApprenticer(apprenticeUserID,
                            apprenticeName, teachTimes, levelOfLastTeach);
                }

                while (set.next())
                {
                    apprenticeUserID = set.getInt(1);
                    apprenticeName = set.getString(2);
                    masterUserID = set.getInt(3);
                    masterName = set.getString(4);
                    teachTimes = set.getByte(5);
                    levelOfLastTeach = set.getShort(6);

                    if (apprenticeUserID == _userID)
                    {
                        _masterApprentice.setMaster(masterUserID, masterName,
                                false);
                    }
                    else
                    {
                        _masterApprentice.addNewApprenticer(apprenticeUserID,
                                apprenticeName, teachTimes, levelOfLastTeach);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("加载师徒关系信息: ", e);
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
     * 根据昵称获取玩家师徒关系
     * @param userName
     * @param _masterApprentice
     */
    public static MasterApprentice loadMasterApprenticeRelationByName(String userName,MasterApprentice _masterApprentice){
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_TEACH_BY_NAME_SQL);

            pstm.setString(1, userName);
            pstm.setString(2, userName);
            set = pstm.executeQuery();

            int masterUserID, apprenticeUserID;
            byte teachTimes;
            short levelOfLastTeach;
            String masterName, apprenticeName;

            if (set.next())
            {
                apprenticeUserID = set.getInt(1);
                apprenticeName = set.getString(2);
                masterUserID = set.getInt(3);
                masterName = set.getString(4);
                teachTimes = set.getByte(5);
                levelOfLastTeach = set.getShort(6);
                log.debug("load off line masterApprentice : " + apprenticeName +", mastername="+masterName);
                if (apprenticeName.equals(userName)) //userName 是徒弟
                {
                    log.debug("apprenticeName.equals(userName)");
                    _masterApprentice
                            .setMaster(masterUserID, masterName, false);
                }
                else  //userName 是师傅
                {
                    log.debug("!!!! apprenticeName.equals(userName)");
                    _masterApprentice.addNewApprenticer(apprenticeUserID,
                            apprenticeName, teachTimes, levelOfLastTeach);
                }

                while (set.next())
                {
                    apprenticeUserID = set.getInt(1);
                    apprenticeName = set.getString(2);
                    masterUserID = set.getInt(3);
                    masterName = set.getString(4);
                    teachTimes = set.getByte(5);
                    levelOfLastTeach = set.getShort(6);
                    log.debug("load off line masterApprentice 2 : " + apprenticeName +", mastername="+masterName);
                    if (apprenticeName.equals(userName))
                    {
                        _masterApprentice.setMaster(masterUserID, masterName,
                                false);
                    }
                    else
                    {
                        _masterApprentice.addNewApprenticer(apprenticeUserID,
                                apprenticeName, teachTimes, levelOfLastTeach);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("根据昵称获取玩家师徒关系 error: ", e);
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
        return _masterApprentice;
    }

    /**
     * 插入师徒关系
     * 
     * @param _apprenticeUserID 徒弟编号
     * @param _apprenticeName 徒弟名字
     * @param _masterUserID 师傅编号
     * @param _masterName 师傅名字
     * @return
     */
    public static boolean insertMasterApprentice (int _apprenticeUserID,
            String _apprenticeName, int _masterUserID, String _masterName)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_MASTER_APPRENTICE);

            pstm.setInt(1, _apprenticeUserID);
            pstm.setString(2, _apprenticeName);
            pstm.setInt(3, _masterUserID);
            pstm.setString(4, _masterName);

            pstm.executeUpdate();

            return true;
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

        return false;
    }

    /**
     * 修改授予知识内容
     * 
     * @param _apprenticeUserID 徒弟编号
     * @param _times 次数
     * @param _levelOfLastTeach 最后一次授予知识的角色等级
     * @return
     */
    public static boolean changeMasterApprentice (int _apprenticeUserID,
            byte _times, short _levelOfLastTeach)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_MASTER_APPRENTICE);

            pstm.setByte(1, (byte) _times);
            pstm.setShort(2, _levelOfLastTeach);
            pstm.setInt(3, _apprenticeUserID);

            pstm.executeUpdate();

            return true;
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

        return false;
    }

    /**
     * 删除师傅的所有师徒关系
     * @param masterID
     * @return
     */
    public static boolean deleteAllMasterApprenticeRelation(int masterID){
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_All_MASTER_APPRENTICE_BY_MASTER);

            pstm.setInt(1, masterID);
            pstm.executeUpdate();

            return true;
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

        return false;

    }

    /**
     * 删除师徒关系
     * 
     * @param _userID 徒弟编号
     */
    public static boolean deleteMasterApprentice (int _apprenticeUserID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_MASTER_APPRENTICE);

            pstm.setInt(1, _apprenticeUserID);
            pstm.executeUpdate();

            return true;
        }
        catch (Exception e)
        {
            log.error("deleteMasterApprentice error : ", e);
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

        return false;
    }

    /**
     * 删除某玩家所有师徒关系（删除角色时调用）
     * 
     * @param _userID 玩家编号
     */
    public static boolean deleteAll (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_ALL_RELATION_SQL);

            pstm.setInt(1, _userID);
            pstm.setInt(2, _userID);
            pstm.executeUpdate();

            return true;
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

        return false;
    }
}
