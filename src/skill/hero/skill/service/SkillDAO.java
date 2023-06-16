package hero.skill.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import hero.player.HeroPlayer;
import hero.share.service.LogWriter;
import hero.skill.ActiveSkill;
import hero.skill.Skill;
import hero.skill.dict.SkillDict;
import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-15 下午03:41:00
 * @描述 ：
 */

public class SkillDAO
{
    private static Logger log = Logger.getLogger(SkillDAO.class);
    /**
     * 加载玩家技能SQL脚本
     */
    private static String SELECT_SKILL_SQL        = "SELECT *FROM player_skill WHERE user_id = ? LIMIT 100";

    /**
     * 增加技能SQL脚本
     */
    private static String INSERT_SKILL_SQL        = "INSERT INTO player_skill (user_id,skill_id) VALUES(?,?) ";

    /**
     * 技能升级SQL脚本
     */
    private static String UPGRADE_SKILL_SQL       = "UPDATE player_skill SET skill_id = ? WHERE user_id = ? AND skill_id = ? LIMIT 1";

    /**
     * 删除遗忘技能列表
     * edit: 2010-11-09 修改为一次性遗忘所有技能 by zhengl 
     *       原始SQL:DELETE FROM player_skill WHERE user_id = ? AND skill_id = ? LIMIT 1
     */
    private static String DELETE_FORGET_SKILL_SQL = "DELETE FROM player_skill WHERE user_id = ?  LIMIT 300";

    /**
     * 删除技能冷却时间SQL脚本
     */
    private static String DELETE_SKILL_CD_SQL     = "UPDATE player_skill SET trace_cd_time = 0 WHERE user_id = ? AND  skill_id = ? LIMIT 1";

    /**
     * 更新技能冷却时间SQL脚本
     */
    private static String UPGRADE_SKILL_CD_SQL    = "UPDATE player_skill SET trace_cd_time = ? WHERE user_id = ? AND skill_id = ?";

    /**
     * 加载玩家技能
     * 
     * @param _userID 玩家编号
     */
    public static ArrayList<int[]> loadPlayerSkill (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_SKILL_SQL);
            pstm.setInt(1, _userID);
            set = pstm.executeQuery();

            ArrayList<Integer> existsCDSkillIDList = null;
            int skillID, traceCoolDownTime;
            ArrayList<int[]> skillInfoList = new ArrayList<int[]>();

            while (set.next())
            {
                skillID = set.getInt("skill_id");
                traceCoolDownTime = set.getInt("trace_cd_time");

                skillInfoList.add(new int[]{skillID, traceCoolDownTime });

                if (traceCoolDownTime > 0)
                {
                    if (null == existsCDSkillIDList)
                    {
                        existsCDSkillIDList = new ArrayList<Integer>();
                    }

                    existsCDSkillIDList.add(skillID);
                }
            }

            if (null != existsCDSkillIDList)
            {
                pstm.close();
                pstm = null;

                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(DELETE_SKILL_CD_SQL);

                for (int id : existsCDSkillIDList)
                {
                    pstm.setInt(1, _userID);
                    pstm.setInt(2, id);

                    pstm.addBatch();
                }

                pstm.executeBatch();
                conn.setAutoCommit(true);
            }

            return skillInfoList;
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
                    if (!conn.getAutoCommit())
                    {
                        conn.setAutoCommit(true);
                    }

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
     * 存储转职之后获得的技能
     * 
     * @param _userID 玩家编号
     * @param skills 新技能
     */
    public static boolean changeCovation (int _userID, ArrayList<Skill> skills)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            /*add by zhengl; date: 2011-05-18; note: 解决conn.commit()异常*/
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(INSERT_SKILL_SQL);
            for (Skill skill : skills) {
                pstm.setInt(1, _userID);
                pstm.setInt(2, skill.id);

                pstm.addBatch();
			}
            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;

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
     * 学习技能
     * 
     * @param _isNew 是否新技能
     * @param _userID 玩家编号
     * @param _skillID 新技能编号
     * @param _lowLevelSkillID 如果不是新学技能，则是该技能的低等级编号
     */
    public static boolean LearnSkill (boolean _isNew, int _userID,
            int _skillID, int _lowLevelSkillID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            if (_isNew)
            {
                pstm = conn.prepareStatement(INSERT_SKILL_SQL);
                pstm.setInt(1, _userID);
                pstm.setInt(2, _skillID);

                if (1 == pstm.executeUpdate())
                {
                    return true;
                }
            }
            else
            {
                pstm = conn.prepareStatement(UPGRADE_SKILL_SQL);
                pstm.setInt(1, _skillID);
                pstm.setInt(2, _userID);
                pstm.setInt(3, _lowLevelSkillID);

                if (1 == pstm.executeUpdate())
                {
                    return true;
                }
                else {
					log.info("执行UPDATE未影响到任何的行,请注意!!!!!");
					log.info(UPGRADE_SKILL_SQL);
					log.info("_skillID:" + _skillID + "_userID:"
                            + _userID + "_lowLevelSkillID:" + _lowLevelSkillID);
				}
            }
        }
        catch (Exception e)
        {
        	log.error("更新技能数据库出错:");
        	e.printStackTrace();
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
     * 当玩家下线时，更新剩余冷却时间超过2分钟的冷却时间信息
     * 
     * @param _player 玩家
     */
    public static void updateSkillTraceCD (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            ArrayList<ActiveSkill> activeSkillList = _player.activeSkillList;
            ArrayList<ActiveSkill> updateSkillList = null;

            for (ActiveSkill skill : activeSkillList)
            {
                if (skill.reduceCoolDownTime > SkillServiceImpl.VALIDATE_CD_TIME)
                {
                    if (null == updateSkillList)
                    {
                        updateSkillList = new ArrayList<ActiveSkill>();
                    }

                    updateSkillList.add(skill);
                }
            }

            if (null != updateSkillList)
            {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(UPGRADE_SKILL_CD_SQL);

                for (ActiveSkill skill : updateSkillList)
                {
                    pstm.setInt(1, skill.reduceCoolDownTime
                            - SkillServiceImpl.VALIDATE_CD_TIME);
                    pstm.setInt(2, _player.getUserID());
                    pstm.setInt(3, skill.id);

                    pstm.addBatch();
                }

                pstm.executeBatch();
                conn.setAutoCommit(true);
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
                    if (!conn.getAutoCommit())
                    {
                        conn.setAutoCommit(true);
                    }

                    conn.close();
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    /**
     * 遗忘技能
     * 
     * @param _player
     */
    public static void forgetSkill (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmInsert = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);

            pstm = conn.prepareStatement(DELETE_FORGET_SKILL_SQL);
            pstm.setInt(1, _player.getUserID());
            pstm.addBatch();

            pstm.executeBatch();
            conn.commit();
            //add by zhengl; date: 2011-03-23; note: 技能删除之后重新插入
            pstmInsert = conn.prepareStatement(INSERT_SKILL_SQL);
            ArrayList<Skill> skills = SkillDict.getInstance().getSkillsByVocation(_player.getVocation());
            for (Skill skill : skills)
            {
            	pstmInsert.setInt(1, _player.getUserID());
            	pstmInsert.setInt(2, skill.id);
            	pstmInsert.addBatch();
            }

            pstmInsert.executeBatch();
            conn.commit();
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
                	pstmInsert.close();
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
            catch (Exception e)
            {

            }
        }
    }
}
