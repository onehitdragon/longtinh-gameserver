package hero.player.service;

import hero.expressions.service.CEService;
import hero.gm.service.GmServiceImpl;
import hero.item.Armor;
import hero.item.EquipmentInstance;
import hero.item.SingleGoods;
import hero.item.bag.EquipmentBag;
import hero.item.bag.Inventory;
import hero.item.detail.EGoodsTrait;
import hero.item.service.EquipmentFactory;
import hero.log.service.LogServiceImpl;
import hero.map.Map;
import hero.map.service.MapRelationDict;
import hero.map.service.MapServiceImpl;
import hero.novice.service.NoviceServiceImpl;
import hero.pet.PetPK;
import hero.player.HeroPlayer;
import hero.player.HeroRoleView;
import hero.player.define.EClan;
import hero.player.define.ESex;
import hero.share.Constant;
import hero.share.EObjectLevel;
import hero.share.EVocation;
import hero.share.EVocationType;
import hero.share.service.LogWriter;
import hero.share.service.ShareServiceImpl;
import hero.skill.Skill;
import hero.skill.dict.SkillDict;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import yoyo.service.base.player.IPlayer;
import yoyo.service.base.player.IPlayerDAO;
import yoyo.service.tools.database.DBServiceImpl;

import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PlayerDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-18 下午04:17:45
 * @描述 ：玩家基本信息数据库操作
 */

public class PlayerDAO implements IPlayerDAO
{
	private static Logger log = Logger.getLogger(PlayerDAO.class);
    private static final String INSERT_PLAYER_SQL                    = "INSERT INTO player(user_id,account_id,"
                                                                             + "server_id,nickname,sex,clan,"
                                                                             + "vocation,lvl,money,"
                                                                             + "hp,mp,where_id,where_x,where_y,home_id,"
                                                                             + "surplus_skill_point,clan_chat_time,world_chat_time)"
                                                                             + "VALUES (?,?,?,?,?,?,?,?,?,?,"
                                                                             + "?,?,?,?,?,?,?,?)";

    private static final String UPDATE_PLAYER_SQL                    = "UPDATE player SET vocation=?,lvl=?,money=?,exp=?,"
                                                                             + "hp=?,mp=?,where_id=?,where_x=?,where_y=?,"
                                                                             + "total_play_time=?,last_logout_time=?,"
                                                                             + "surplus_skill_point=?,lover_value=?,last_login_time=?,"
                                                                             + "power_value=?,receive_repeate_task_times=? "
                                                                             + "where user_id=? LIMIT 1";
    
    private static final String UPDATE_NOVICE_SQL                    = "UPDATE player SET novice=0 where user_id=? LIMIT 1";
    

    private static final String UPDATE_HEAVEN_BOOK  = "UPDATE player t SET t.heaven_book_1=?,t.heaven_book_2=?,t.heaven_book_3=?,t.surplus_skill_point=? " +
                                                            " WHERE t.user_id=?";

    private static final String UPDATE_AUTO_SELL_TRAIT_SQL           = "UPDATE player SET auto_sell_trait=? where user_id=? LIMIT 1";

    /**
     * 插入角色身上穿戴装备SQL脚本
     */
    private static final String INSERT_PLAYER_BODY_EQUIPMENT_SQL     = "INSERT INTO player_carry_equipment(instance_id,"
                                                                             + "user_id,container_type) "
                                                                             + "VALUES (?,?,?)";

    /**
     * 插入装备实例装备SQL脚本
     */
    private static final String INSERT_EQUIPMENT_INSTANCE_SQL        = "INSERT INTO equipment_instance(instance_id,"
                                                                             + "equipment_id,creator_user_id,owner_user_id,"
                                                                             + "current_durability, bind) "
                                                                             + "VALUES (?,?,?,?,?,?)";

    private static final String DELETE_PLAYER_SQL                    = "DELETE FROM player WHERE user_id=? limit 1";

    /**
     * 删除角色携带的所有装备
     */
    private static final String DELETE_PLAYER_EQUIPMENT_SQL          = "DELETE FROM player_carry_equipment WHERE user_id=?";

    /**
     * 删除角色所有装备实例
     */
    private static final String DELETE_PLAYER_EQUIPMENT_INSTANCE_SQL = "DELETE FROM equipment_instance WHERE owner_user_id=?";
    

    /**
     * 查询玩家基础信息
     * edit: 2010-11-09 修改了该表,添加surplus_skill_point字段  by zhengl
     * edit: 2011-01-17 加了 left_master_time 和 lover_value 两个字段，加载时需要加载数据
     */
    private static final String SELECT_PLAYER_SQL                    = "SELECT account_id,nickname,sex,clan,vocation,"
                                                                             + "lvl,money,exp,hp,mp,novice,where_id,where_x,"
                                                                             + "where_y,home_id,bag_size,auto_sell_trait,"
                                                                             + "total_play_time,last_logout_time,msisdn, "
                                                                             + "surplus_skill_point,left_master_time,lover_value, "
                                                                             + "last_receive_gift, "
                                                                             + "heaven_book_1,heaven_book_2,heaven_book_3,server_id,"
                                                                             +  "receive_repeate_task_times "
                                                                             + "FROM player WHERE user_id=? LIMIT 1";
    
    private static final String SELECT_PLAYER_LAST_GIFT_SQL          = "SELECT last_receive_gift FROM player"
        																	 + " WHERE user_id=? LIMIT 1";
    
    /**
     * 更新玩家最后一次所领取的礼包
     */
    private static final String UPDATE_PLAYER_LAST_GIFT_SQL          = "UPDATE player set last_receive_gift=? "
    																		 + " WHERE user_id=? LIMIT 1";
    /**
     * 查询玩家简单信息供GM工具显示
     */
    private static final String SELECT_PLAYER_GMTOOL_SQL                    = "SELECT account_id,nickname,sex,clan,vocation,"
                                                                             + "lvl,money,exp,last_login_time,last_logout_time FROM player WHERE user_id=? LIMIT 1";

    private static final String SELECT_PLAYER_OFF_LINE_SQL = "select user_id,sex,clan,vocation,lvl,account_id,money,exp from player where nickname=? limit 1";

    /**
     * 插入玩家初始药水
     */
    private static final String INSERT_MEDICAMENT_SQL                = "INSERT INTO player_single_goods"
                                                                             + "(user_id,goods_type,goods_id,"
                                                                             + "goods_number,package_index) VALUES (?,?,?,?,?)";

    /**
     * 插入快捷键脚本
     */
    private static final String INSERT_SHORTCUT_KEY_SQL              = "INSERT INTO player_shortcut_key(user_id,shortcut_key) VALUES (?,?)";

    /**
     * 更新快捷键脚本
     */
    private static final String UPDATE_SHORTCUT_KEY_SQL              = "UPDATE player_shortcut_key SET shortcut_key = ? WHERE user_id = ? LIMIT 1";

    /**
     * 加载快捷键脚本
     */
    private static final String SELECT_SHORTCUT_KEY_SQL              = "SELECT * FROM player_shortcut_key WHERE user_id = ? LIMIT 1";

    /**
     * 插入初始技能
     */
    private final static String INSERT_SKILL_SQL                     = "INSERT INTO player_skill (user_id,skill_id) VALUES (?,?)";

    /**
     * 根据ID查询玩家等级
     */
    private static final String SELECT_PLAYER_LEVEL_SQL              = "SELECT lvl FROM player WHERE user_id=? LIMIT 1";

    /**
     * 更改玩家属性
     */
    private static final String UPDATE_PLAYER_PROPERTY_SQL           = "UPDATE player SET lvl=?,vocation=?,money=?,exp=?"
                                                                             + " WHERE user_id=? LIMIT 1";

    /**
     * 根据玩家 account_id 查询 username
     */
    private static final String SELECT_USERNAME_FROM_ACCOUNTID         = "SELECT username,curr_publisher,msisdn,client_version,agent,client_jar_type,bind_msisdn,password FROM account WHERE account_id=? LIMIT 1";

    /**
     * 更新玩家脱离师傅的时间
     */
    private static final String UDPATE_PLAYER_LEFT_MASTER_TIME = "UPDATE player t SET t.left_master_time=? where t.user_id=?";
	/**
	 * 用于更新最后世界聊天
	 */
	private static final String UPDATE_WORLD_CHAT = "update player set world_chat_time = ? " 
		+ " where user_id = ?";
	
	/**
	 * 用于更新最后一次阵营聊天
	 */
	private static final String UPDATE_CLAN_CHAT = "update player set clan_chat_time = ? " 
		+ " where user_id = ?";
	/**
	 * 查询最后一次阵营聊天
	 */
	private static final String SELECT_CLAN_CHAT = "select clan_chat_time from player where user_id = ?";
	/**
	 * 查询最后一次世界聊天
	 */
	private static final String SELECT_WORLD_CHAT = "select world_chat_time from player where user_id = ?";

    /**
     * 查询玩家是否被禁言
     */
    private static final String SELECT_CHAT_BLANK = "select * from chat_black where account_id=? and user_id = ?";

    /**
     * 添加冻结的角色
     */
    private static final String INSERT_ROLE_BLANK = "insert into role_black(user_id,nickname,keep_time,start_time,end_time,memo)" +
            " values(?,?,?,?,?,?)";

    /**
     * 添加冻结的账号
     */
    private static final String INSERT_ACCOUNT_BLANK = "insert into account_black(account_id,username,keep_time,start_time,end_time,memo)" +
            " values(?,?,?,?,?,?)";

    /**
     * 添加禁言的角色
     */
    private static final String INSERT_CHAT_BLANK = "insert into chat_black(user_id,nickname,keep_time,start_time,end_time,memo)" +
            " values(?,?,?,?,?,?)";

    /**
     * 删除禁止登陆的帐号信息SQL脚本
     */
    private static final String DELETE_FORBID_ACCOUNT_SQL = "DELETE FROM account_black WHERE account_id=?";

     /**
     * 删除禁止登陆的角色信息SQL脚本
     */
    private static final String DELETE_FORBID_ROLE_SQL    = "DELETE FROM role_black WHERE user_id=?";

    /**
     * 删除禁言玩家
     */
    private static final String DELETE_CHAT_BLANK_SQL = "delete from chat_black where user_id=?";
    
    /**
     * 爱情值排名
     */
    private static final String PLAYER_LOVER_ORDER = "select t.user_id from player t where t.lover_value>0 order by t.lover_value desc,t.lvl desc";
    /**
     * 不同阵营决斗
     */
    private static final String INERT_PVP_INFO = "insert into pvp(winner_user_id,winner_vocation,failer_user_id,failer_vocation) value(?,?,?,?)";
    /**
     *循环任务道具
     */
    private static final String ADD_REPEATE_TASK_GOODS = "insert into repeate_task_tools(user_id,goods_id,max_times) values(?,?,?)";
    /**
     * 获取循环任务道具增加的次数
     */
    private static final String SELECT_REPEATE_TASK_GOODS_TIMES = "select sum(t.max_times) from repeate_task_tools t where t.user_id=? and DATE_FORMAT(t.create_time,'%Y%m%d')=DATE_FORMAT(NOW(),'%Y%m%d')";
    /**
     * 玩家接收循环任务次数清零
     */
    private static final String CLEAR_PLAYER_RECEIVE_REPEATE_TASK_TIMES = "update player t set t.receive_repeate_task_times=0 where t.receive_repeate_task_times>0";
    /**
     * 删除玩家当天的循环任务道具
     */
    private static final String DELETE_REPEATE_TASK_TOOLS = "delete from repeate_task_tools";

    private static final String UPDATE_PLAYER_LOVER_VALUE = "update player t set t.lover_value=? where t.user_id=?";

    /**
     * 统计玩家杀敌次数
     */
    private static final String COUNT_WINNER_NUMBER = "select count(*) from pvp t where t.winner_user_id=?";
    /**
     * 统计玩家被杀次数
     */
    private static final String COUNT_FAILER_NUMBER = "select count(*) from pvp t where t.failer_user_id=?";

    public PlayerDAO()
    {

    }
    
    /**
     * 初始化各职业的快捷键数据
     * @param _vocation
     */
    private static final void initHotKeyByVocation(EVocation _vocation)
    {
        StringBuffer defaultChar = new StringBuffer();
        int initSkillID = PlayerServiceImpl.getInstance().getConfig().getInitSkill(_vocation);
        FIELD_FIXED_SHORTCUT_KEY[0][2] = initSkillID;
        FIELD_FIXED_SHORTCUT_KEY[1][2] = 
        	PlayerServiceImpl.getInstance().getConfig().default_red_medicament;
        FIELD_FIXED_SHORTCUT_KEY[2][2] = 
        	PlayerServiceImpl.getInstance().getConfig().default_blue_medicament;

        for (int i = 0; i < FIELD_FIXED_SHORTCUT_KEY.length; i++)
        {
            defaultChar.append(FIELD_FIXED_SHORTCUT_KEY[i][0])
                    .append(SHORTCUT_KEY_CONNECTOR).append(
                    		FIELD_FIXED_SHORTCUT_KEY[i][1]).append(
                            SHORTCUT_KEY_CONNECTOR).append(
                            		FIELD_FIXED_SHORTCUT_KEY[i][2]).append(
                            SHORTCUT_KEY_SEPARATOR);
        }

        for (int i = 0; i < KEY_OF_WALKING.length; i++)
        {
            defaultChar.append(KEY_OF_WALKING[i][0]).append(
                    SHORTCUT_KEY_CONNECTOR).append(KEY_OF_WALKING[i][1])
                    .append(SHORTCUT_KEY_CONNECTOR)
                    .append(KEY_OF_WALKING[i][2])
                    .append(SHORTCUT_KEY_SEPARATOR);
        }

        for (int i = 0; i < SECOND_FIELD_FIXED_SHORTCUT_KEY.length; i++)
        {
            defaultChar.append(SECOND_FIELD_FIXED_SHORTCUT_KEY[i][0] + 13)
                    .append(SHORTCUT_KEY_CONNECTOR).append(
                            SECOND_FIELD_FIXED_SHORTCUT_KEY[i][1]).append(
                            SHORTCUT_KEY_CONNECTOR).append(
                            SECOND_FIELD_FIXED_SHORTCUT_KEY[i][2]).append(
                            SHORTCUT_KEY_SEPARATOR);
        }

        for (int i = 0; i < KEY_OF_WALKING.length; i++)
        {
            defaultChar.append(KEY_OF_WALKING[i][0] + 13).append(
                    SHORTCUT_KEY_CONNECTOR).append(KEY_OF_WALKING[i][1])
                    .append(SHORTCUT_KEY_CONNECTOR)
                    .append(KEY_OF_WALKING[i][2])
                    .append(SHORTCUT_KEY_SEPARATOR);
        }

        DEFAULT_SHORTCUT_KEY_DESC = defaultChar.toString();
    }

    /**
     * 获取玩家被杀次数
     * @param userID
     * @return
     */
    public static int getPlayerFailerNumber(int userID){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int num = 0;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(COUNT_FAILER_NUMBER);
            ps.setInt(1,userID);

            rs = ps.executeQuery();

            if(rs.next()){
                num = rs.getInt(1);
            }

            rs.close();
            ps.close();
            conn.close();

        }catch (Exception e) {
			log.error("获取玩家被杀次数 error: ", e);
		}
		finally
        {
            try
            {
                if(rs != null){
                    rs.close();
                    rs = null;
                }
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
        return num;
    }


    /**
     * 获取玩家杀敌次数
     * @param userID
     * @return
     */
    public static int getPlayerWinnerNumber(int userID){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int num = 0;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(COUNT_WINNER_NUMBER);
            ps.setInt(1,userID);

            rs = ps.executeQuery();

            if(rs.next()){
                num = rs.getInt(1);
            }

            rs.close();
            ps.close();
            conn.close();

        }catch (Exception e) {
			log.error("获取玩家杀敌次数 error: ", e);
		}
		finally
        {
            try
            {
                if(rs != null){
                    rs.close();
                    rs = null;
                }
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
        return num;
    }

    /**
     * 修改玩家爱情值
     * @param userID
     * @param loverValue
     */
    public void updatePlayerLoverValue(int userID,int loverValue){
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER_LOVER_VALUE);
            ps.setInt(1,loverValue);
            ps.setInt(2,userID);

            ps.executeUpdate();

            ps.close();

        }catch (Exception e) {
			log.error("修改玩家爱情值 error: ", e);
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
     * 刷新玩家接收循环任务的次数
     * @return
     */
    public static void clearPlayerReceiveRepeatTaskTimes(){
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(CLEAR_PLAYER_RECEIVE_REPEATE_TASK_TIMES);
            ps.executeUpdate();

            ps.close();

            ps = conn.prepareStatement(DELETE_REPEATE_TASK_TOOLS);
            ps.executeUpdate();

            ps.close();

        }catch (Exception e) {
			log.error("刷新玩家接收循环任务的次数 error: ", e);
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
     * 获取玩家使用循环任务道具添加的可接收次数
     * @param userID
     * @return
     */
    public static int getRepeatTaskGoodsTimes(int userID){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int res = 0;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(SELECT_REPEATE_TASK_GOODS_TIMES);
            ps.setInt(1,userID);

            rs = ps.executeQuery();
            if(rs.next()){
                res = rs.getInt(1);
            }

            rs.close();
            ps.close();
            conn.close();

        }catch (Exception e) {
			log.error("获取玩家使用循环任务道具添加的可接收次数 error: ", e);
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
        return res;
    }

    /**
     * 给玩家添加循环任务道具
     * 这个表每晚24时清除数据
     * @param userID
     * @param goodsID
     * @param maxTimes
     * @return
     */
    public static int insertRepeatTaskGoods(int userID,int goodsID,int maxTimes){
        Connection conn = null;
        PreparedStatement ps = null;
        int res = 0;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(ADD_REPEATE_TASK_GOODS);
            ps.setInt(1,userID);
            ps.setInt(2,goodsID);
            ps.setInt(3,maxTimes);

            res = ps.executeUpdate();

            ps.close();
            conn.close();
        }catch (Exception e) {
			log.error("给玩家添加循环任务道具 error: ", e);
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
        return res;
    }

    /**
     * 保存敌对玩家PK数据
     * @param winnerUserID
     * @param failerUserID
     */
    public static void insertPvpInfo(int winnerUserID,int winnerVocation,int failerUserID,int failerVocation){
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(INERT_PVP_INFO);
            ps.setInt(1,winnerUserID);
            ps.setInt(2,winnerVocation);
            ps.setInt(3,failerUserID);
            ps.setInt(4,failerVocation);

            ps.executeUpdate();

        }catch (Exception e) {
			log.error("插入玩家PK数据 error: ", e);
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
     * 玩家爱情值排行
     * 只对爱情值大于0的玩家进行排名
     * @return
     */
    public java.util.Map<Integer,Integer> loverValueOrderMap(){
    	java.util.Map<Integer,Integer> loverOrderMap = new HashMap<Integer, Integer>();
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
        	conn = DBServiceImpl.getInstance().getConnection();
        	ps = conn.prepareStatement(PLAYER_LOVER_ORDER);
        	rs = ps.executeQuery();
        	int i=1;
        	while(rs.next()){
        		int userID = rs.getInt("user_id");
        		loverOrderMap.put(userID, i);
        		i++;
        	}
        	rs.close();
        	ps.close();
        	conn.close();
        }catch (Exception e) {
            System.out.println("Vinh Eroooooooooooooooooooooo");
			log.error("玩家爱情值排行 error: ", e);
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
		return loverOrderMap;
    }
    
	/**
	 * 更新最后世界聊天时间
	 * @param _userID
	 * @param _newTime
	 */
	public static final void updateWorldChatWait (int _userID, long _newTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
			conn = DBServiceImpl.getInstance().getConnection();
			ps = conn.prepareStatement(UPDATE_WORLD_CHAT);
			ps.setLong(1, _newTime);
			ps.setInt(2, _userID);
			ps.executeUpdate();
		} catch (Exception e) {
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
	 * 更新最后阵营聊天时间
	 * @param _userID
	 * @param _newTime
	 */
	public static final void updateClanChatWait (int _userID, long _newTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
			conn = DBServiceImpl.getInstance().getConnection();
			ps = conn.prepareStatement(UPDATE_CLAN_CHAT);
			ps.setLong(1, _newTime);
			ps.setInt(2, _userID);
			ps.executeUpdate();
		} catch (Exception e) {
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
	 * 初始化玩家上次阵营聊天时间
	 * @param _userID
	 * @return
	 */
    public static final long loadClanChatWait (int _userID)
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        long time = 0;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(SELECT_CLAN_CHAT);
            ps.setInt(1, _userID);
            set = ps.executeQuery();

            if (set.next())
            {
            	time = set.getLong("clan_chat_time");
            }
            else
            {
                set.close();
                set = null;
                ps.close();
                ps = null;
            }
        }
        catch (Exception sqle)
        {
            LogWriter.error(null, sqle);
        }
        finally
        {
            try
            {
                if (set != null)
                {
                    set.close();
                }
                if (ps != null)
                {
                    ps.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return time;
    }
    
	/**
	 * 初始化玩家上次世界聊天间隔
	 * @param _userID
	 * @return
	 */
    public static final long loadWorldChatWait (int _userID)
    {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        long time = 0;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(SELECT_WORLD_CHAT);
            ps.setInt(1, _userID);
            set = ps.executeQuery();

            if (set.next())
            {
            	time = set.getLong("world_chat_time");
            }
            else
            {
                set.close();
                set = null;
                ps.close();
                ps = null;
            }
        }
        catch (Exception sqle)
        {
            LogWriter.error(null, sqle);
        }
        finally
        {
            try
            {
                if (set != null)
                {
                    set.close();
                }
                if (ps != null)
                {
                    ps.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return time;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayerDAO#createRole(int, short,
     *      java.lang.String[]) 创建角色，并返回角色描述信息
     */
    public byte[] createRole (int _accountID, short _serverID, int _userID,
            String[] _paras)
    {
        String _nickname = _paras[0].toString();
        EClan clan = EClan.getClan(Short.parseShort(_paras[1]));
        EVocation vocation = EVocation.getVocationByID(Short
                .parseShort(_paras[2]));

        ESex sex = ESex.getSex(Short.parseShort(_paras[3]));
        short clientType = Short.parseShort(_paras[4]);

        int roleUserID = initPlayerDB(_accountID, _serverID, _userID,
                _nickname, clan, vocation, sex, clientType);

        byte[] roleDesc = null;

        if (roleUserID > 0)
        {
            // 获得角色的描述信息
            roleDesc = HeroRoleView.getInstance().getNewRoleDesc(roleUserID,
                    _nickname, clan, vocation, sex, clientType);
        }

        try {
            LogServiceImpl.getInstance().createDelRoleLog("创建",_accountID,_serverID,_userID,_nickname,clan.getDesc(),
                    vocation.getDesc(),sex.getDesc(),clientType,roleUserID>0);
		} catch (Exception e) {
			e.printStackTrace();
		}


        return roleDesc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayerDAO#deleteRole(int) 删除角色
     */
    public int deleteRole (int _userID)
    {
        Connection conn = null;
        java.sql.PreparedStatement ps = null;
        ResultSet rs = null;


        int rst = 0;
//        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        //edit by zhengl; date: 2011-02-09; note: 删除角色的时候角色并未登陆实际游戏世界,所以无法通过游戏内存获取玩家.
        String name = "";
        int accountID = 0;
        int serverID = -1;
        String clan = "";
        String vocation = "";
        String sex = "";
        short clientType = -1;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            ps = conn.prepareStatement(SELECT_PLAYER_SQL);
            ps.setInt(1, _userID);
            rs = ps.executeQuery();
            if(rs.next()){
                name = rs.getString("nickname");
                accountID = rs.getInt("account_id");
                serverID = rs.getInt("server_id");
                clan = EClan.getClan(rs.getInt("clan")).getDesc();
                vocation = EVocation.getVocationByID(rs.getInt("vocation")).getDesc();
                sex = ESex.getSex(rs.getInt("sex")).getDesc();
            }

            ps = conn.prepareStatement(DELETE_PLAYER_SQL);
            ps.setInt(1, _userID);

            rst = ps.executeUpdate();

            if (1 == rst)
            {
                ps.close();
                ps = null;

                ps = conn.prepareStatement(DELETE_PLAYER_EQUIPMENT_SQL);
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;

                ps = conn
                        .prepareStatement(DELETE_PLAYER_EQUIPMENT_INSTANCE_SQL);
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;

                ps = conn.prepareStatement("DELETE FROM player_skill WHERE user_id = ? LIMIT 200");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;

                ps = conn.prepareStatement("DELETE FROM player_effect WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;

                ps = conn
                        .prepareStatement("DELETE FROM player_shortcut_key WHERE user_id = ? LIMIT 1");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;

                ps = conn
                        .prepareStatement("DELETE FROM player_completed_task WHERE user_id = ? LIMIT 20");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;
                ps = conn
                        .prepareStatement("DELETE FROM player_exsits_task WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;
                ps = conn
                        .prepareStatement("DELETE FROM player_single_goods WHERE user_id = ? LIMIT 300");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;
                ps = conn
                        .prepareStatement("DELETE FROM cd WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;
                ps = conn
                        .prepareStatement("DELETE FROM sports_point WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();

                ps.close();
                ps = null;
                ps = conn
                        .prepareStatement("DELETE FROM lover WHERE roleA =? OR roleB=? LIMIT 1");
                ps.setString(1,name);
                ps.setString(2,name);
                ps.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            LogWriter.error(this, e);
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }

        int flag = (rst == 1) ? 1 : 0;
        //edit:	zhengl
        //date:	2011-01-18
        //note:	必须try catch
        try {
            LogServiceImpl.getInstance().createDelRoleLog("删除",accountID,(short)serverID,_userID,
                    name,clan,vocation,sex,clientType,flag>0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//end
        return flag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayerDAO#listRole(int[]) 获得角色的描述信息
     */
    public byte[] listRole (int[] _userIDList)
    {
        // TODO Auto-generated method stub
        return HeroRoleView.getInstance().get(_userIDList);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayerDAO#load(int) 从数据库加载角色
     */
    public IPlayer load (int _userID)
    {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HeroPlayer player = null;
        try
        {
            con = DBServiceImpl.getInstance().getConnection();
            pstm = con.prepareStatement(SELECT_PLAYER_SQL);
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();

            if (rs.next())
            {
                String bagSizeDesc = rs.getString("bag_size");
                String[] bagSizeList = bagSizeDesc.split(BAG_SIZE_CONNECTOR);
                int bagsize = bagSizeList.length;
                
                byte[] bagSizes = new byte[bagsize];

//                bagSizes[0] = Byte.parseByte(bagSizeList[0]);
//                bagSizes[1] = Byte.parseByte(bagSizeList[1]);
//                bagSizes[2] = Byte.parseByte(bagSizeList[2]);
//                bagSizes[3] = Byte.parseByte(bagSizeList[3]);
                if(bagsize == 6){//没有宠物物品背包
                	bagSizes = new byte[7];
                	bagSizes[6] = Inventory.GENERIC_BAG_GRID_SIZE;
                }
                if(bagsize == 5){//没有宠物列表包,宠物物品背包
                	bagSizes = new byte[7];
                	bagSizes[5] = Inventory.GENERIC_BAG_GRID_SIZE;
                	bagSizes[6] = Inventory.GENERIC_BAG_GRID_SIZE;
                }
                if(bagsize == 4){//没有宠物装备背包,宠物列表包,宠物物品背包
                	bagSizes = new byte[7];
                	bagSizes[4] = Inventory.GENERIC_BAG_GRID_SIZE;
                	bagSizes[5] = Inventory.GENERIC_BAG_GRID_SIZE;
                	bagSizes[6] = Inventory.GENERIC_BAG_GRID_SIZE;
                }
                log.debug("player bag size = " + bagsize);
                for(int i=0; i<bagsize; i++){
                	bagSizes[i] = Byte.parseByte(bagSizeList[i]);
                }
                
                
                player = new HeroPlayer(_userID);
                player.bagSizes = bagSizes;

                int accountID = rs.getInt("account_id");
                String nickName = rs.getString("nickname");
                short sex = rs.getShort("sex");
                int vocation = rs.getInt("vocation");
                int clan = rs.getInt("clan");
                short level = rs.getShort("lvl");

                if (level > PlayerServiceImpl.getInstance().getConfig().max_level)
                {
                    level = PlayerServiceImpl.getInstance().getConfig().max_level;
                }

                int hp = rs.getInt("hp");
                int mp = rs.getInt("mp");
                int exp = rs.getInt("exp");
                int money = rs.getInt("money");

                if (money > Constant.INTEGER_MAX_VALUE)
                {
                    money = Constant.INTEGER_MAX_VALUE;
                }

                short whereMapID = rs.getShort("where_id");
                short isNovice = rs.getShort("novice");
                short whereX = rs.getShort("where_x");
                short whereY = rs.getShort("where_y");
                short homeID = rs.getShort("home_id");
                short autoSellTrait = rs.getShort("auto_sell_trait");
                long totalPlayerTime = rs.getLong("total_play_time");
                Timestamp lastLogoutTime = rs.getTimestamp("last_logout_time");
                short skillPoints = rs.getShort("surplus_skill_point"); //add by zhengl
                Timestamp leftMasterTime = rs.getTimestamp("left_master_time");
                int lastReceiveGift = rs.getInt("last_receive_gift");//add by zhengl
                int loverValue = rs.getInt("lover_value");
                int heaven_book_1 = rs.getInt("heaven_book_1");
                int heaven_book_2 = rs.getInt("heaven_book_2");
                int heaven_book_3 = rs.getInt("heaven_book_3");
                int receive_repeate_task_times = rs.getInt("receive_repeate_task_times");

                player.getLoginInfo().accountID = accountID;
                player.setName(nickName);
                player.setSex(ESex.getSex(sex));
                player.setVocation(EVocation.getVocationByID(vocation));
                player.setClan(EClan.getClan(clan));
                player.setLevel(level);
                player.setExp(exp);
                int nextAdd = 0, nowAdd = 0;
                for (int i = 1; i <= level; i++) 
                {
                	if (i == level) 
                	{
                		nextAdd += CEService.totalUpgradeExp(i);
					}
                	else 
                	{
                		nowAdd += CEService.totalUpgradeExp(i);
                		nextAdd += CEService.totalUpgradeExp(i);
					}
        		}
                nowAdd += exp;
            	player.setUpgradeNeedExp(CEService.totalUpgradeExp(level));
            	player.setUpgradeNeedExpShow(nextAdd);
            	player.setExpShow(nowAdd);
            	
                player.setMoney(money);
                player.setAutoSellTrait(EGoodsTrait.getTrait(autoSellTrait));
                player.totalPlayTime = totalPlayerTime;
                player.lastLogoutTime = lastLogoutTime.getTime();
                player.surplusSkillPoint = skillPoints; //add by zhengl
                player.leftMasterTime = leftMasterTime.getTime();
                player.setLoverValue(loverValue);
                player.heaven_book_ids[0] = heaven_book_1;
                player.heaven_book_ids[1] = heaven_book_2;
                player.heaven_book_ids[2] = heaven_book_3;
                player.lastReceiveGift = lastReceiveGift; //add by zhengl
                player.receivedRepeateTaskTimes = receive_repeate_task_times;// add by jiaodj

                if(heaven_book_1 > 0 && heaven_book_1 == heaven_book_2 && heaven_book_1 == heaven_book_3){
                    player.heavenBookSame = true;
                }

                Map map;

                if (hp == 0)
                {
                	//edit by zhengl; date: 2011-02-24; note: 添加系数进HP计算公式
                    player.setHp(
                    		CEService.hpByStamina(
                    				CEService.playerBaseAttribute(
                    						player.getLevel(), 
                    						player.getVocation().getStaminaCalPara()), 
                    				player.getLevel(), 
                    				player.getObjectLevel().getHpCalPara()));
                    player.setMp(
                    		CEService.mpByInte(
                    				CEService.playerBaseAttribute(player.getLevel(), 
                    				player.getVocation().getInteCalcPara()), 
                    				player.getLevel(), 
                    				EObjectLevel.NORMAL.getMpCalPara()
                    				)
                    		);

                    //如果玩家是魔族，则使用魔族地图 --add by jiaodj 2011-05-16
                    short[] relations = MapRelationDict.getInstance().getRelationByMapID(whereMapID);

                    short mapid = relations[2];
                    if(player.getClan() == EClan.HE_MU_DU && relations[8] > 0){
                        mapid = relations[8];
                    }

                    map = MapServiceImpl.getInstance().getNormalMapByID(mapid);

                    whereX = map.getBornX();
                    whereY = map.getBornY();
                }
                else
                {
                    player.setHp(hp);
                    player.setMp(mp);

                    if (Map.WORLD_MAP_ID_UPPER_LIMIT < whereMapID)
                    {
                        //如果玩家是魔族，则使用魔族地图  --add by jiaodj 2011-05-16
                        short[] relations = MapRelationDict.getInstance().getRelationByMapID(whereMapID);
                        short mapid = relations[3];
                        if(player.getClan() == EClan.HE_MU_DU && relations[9] > 0){
                            mapid = relations[9];
                        }

                        map = MapServiceImpl.getInstance().getNormalMapByID(mapid);

                        whereX = map.getBornX();
                        whereY = map.getBornY();
                    }
                    else
                    {
                    	//edit by zhengl; date: 2011-02-22; note: 合理化新手流程
//                        if (0 == isNovice)
//                        {
//                            map = MapServiceImpl.getInstance()
//                                    .getNormalMapByID(whereMapID);
//                        }
//                        else
//                        {
//                            map = MapServiceImpl.getInstance()
//                                    .getNormalMapByID(
//                                            NoviceServiceImpl.getInstance()
//                                                    .getNoviceMapID());
//                        }
                    	map = MapServiceImpl.getInstance().getNormalMapByID(whereMapID);
                    	//end
                    }
                }

                player.live(map);
                player.setCellX(whereX);
                player.setCellY(whereY);
                player.setHomeID(homeID);

                loadShortcutKeyList(_userID, player.getShortcutKeyList());

                // player.getGameInfo().setLoginMsisdn(rs.getString("msisdn"));

                return player;
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            LogWriter
                    .println("SQLException in PlayerLoader.loadPlayerByUserID() : "
                            + _userID);
            LogWriter.error(null, e);
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
                if (con != null)
                {
                    con.close();
                    con = null;
                }
            }
            catch (SQLException e)
            {
            	e.printStackTrace();
            }
        }

        return null;
    }
    
    
    /**
     * 从数据库加载角色，显示到GMTOOL中
     * @param _userID
     * @return
     */
    public IPlayer loadOffLinePlayerToGmTool (int _userID)
    {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HeroPlayer player = null;
        try
        {
            con = DBServiceImpl.getInstance().getConnection();
            pstm = con.prepareStatement(SELECT_PLAYER_GMTOOL_SQL);
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();

            if (rs.next())
            {
                player = new HeroPlayer(_userID);

                int accountID = rs.getInt("account_id");
                String nickName = rs.getString("nickname");
                short sex = rs.getShort("sex");
                int vocation = rs.getInt("vocation");
                int clan = rs.getInt("clan");
                short level = rs.getShort("lvl");

                if (level > PlayerServiceImpl.getInstance().getConfig().max_level)
                {
                    level = PlayerServiceImpl.getInstance().getConfig().max_level;
                }

                int exp = rs.getInt("exp");
                int money = rs.getInt("money");

                if (money > Constant.INTEGER_MAX_VALUE)
                {
                    money = Constant.INTEGER_MAX_VALUE;
                }
                
                Timestamp lastLoginTime = rs.getTimestamp("last_login_time");
                Timestamp lastLogoutTime = rs.getTimestamp("last_logout_time");

                player.getLoginInfo().accountID = accountID;
                player.setName(nickName);
                player.setSex(ESex.getSex(sex));
                player.setVocation(EVocation.getVocationByID(vocation));
                player.setClan(EClan.getClan(clan));
                player.setLevel(level);
                player.setExp(exp);
                player.setMoney(money);
                player.loginTime = lastLoginTime.getTime();
                player.lastLogoutTime = lastLogoutTime.getTime();

                return player;
            }
        }
        catch (Exception e)
        {
            LogWriter
                    .println("SQLException in PlayerLoader.loadOffLinePlayerToGmTool() : "
                            + _userID);
            LogWriter.error(null, e);
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
                if (con != null)
                {
                    con.close();
                    con = null;
                }
            }
            catch (SQLException e)
            {
            }
        }

        return null;
    }

    /**
     * 根据昵称获取不在线玩家简单信息
     * @param name
     * @return
     */
    public HeroPlayer getOffLinePlayerByName(String name){
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HeroPlayer player = null;
        try
        {
            con = DBServiceImpl.getInstance().getConnection();
            pstm = con.prepareStatement(SELECT_PLAYER_OFF_LINE_SQL);
            pstm.setString(1,name);
            rs = pstm.executeQuery();

            while (rs.next()){
                player = new HeroPlayer();
                player.setName(name);
                player.setUserID(rs.getInt("user_id"));
                player.setSex(ESex.getSex(rs.getShort("sex")));
                player.setVocation(EVocation.getVocationByID(rs.getInt("vocation")));
                player.setClan(EClan.getClan(rs.getInt("clan")));
                player.setLevel(rs.getShort("lvl"));
                player.getLoginInfo().accountID = rs.getInt("account_id");
                player.setMoney(rs.getInt("money"));
                player.setExp(rs.getInt("exp"));
            }

            rs.close();
            pstm.close();
            con.close();
        }catch (SQLException e){
            log.error("get offline player error :　",e);
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
                if (con != null)
                {
                    con.close();
                    con = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
        return player;
    }
    
    /**
     * 更新循环任务
     * @param _player
     */
    public static void updateRepeateTask (IPlayer _player)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        HeroPlayer player = (HeroPlayer) _player;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER_SQL);

            ps.setShort(1, player.getVocation().value());
            ps.setShort(2, player.getLevel());
            ps.setInt(3, player.getMoney());
            ps.setInt(4, player.getExp());
            ps.setInt(5, player.getHp());
            ps.setInt(6, player.getMp());
            ps.setInt(7, player.where().getID());
            ps.setShort(8, player.getCellX());
            ps.setShort(9, player.getCellY());
            player.nowPlayTime = (System.currentTimeMillis() - player.loginTime) / 60000;
            ps.setLong(10, player.totalPlayTime + player.nowPlayTime);
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            ps.setInt(12, player.surplusSkillPoint); //add by zhengl
            ps.setInt(13, player.getLoverValue());
            ps.setTimestamp(14, new Timestamp(player.loginTime));
            //更新玩家当前实力值 add by jiaodongjie
            int power = ShareServiceImpl.getInstance().calPlayerPower(player);
            ps.setInt(15,power);
            ps.setInt(16,player.receivedRepeateTaskTimes);

            ps.setInt(17, player.getUserID());
            

            ps.executeUpdate();

            player.needUpdateDB = false;
        }
        catch (SQLException e)
        {
            LogWriter.error(null, e);
            log.error("DB update player error : ",e);
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
            	log.error("DB update player error : ",e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayerDAO#updateDB(me2.service.basic.player.IPlayer)
     */
    public void updateDB (IPlayer _player)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        HeroPlayer player = (HeroPlayer) _player;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER_SQL);

            ps.setShort(1, player.getVocation().value());
            ps.setShort(2, player.getLevel());
            ps.setInt(3, player.getMoney());
            ps.setInt(4, player.getExp());
            ps.setInt(5, player.getHp());
            ps.setInt(6, player.getMp());
            ps.setInt(7, player.where().getID());
            ps.setShort(8, player.getCellX());
            ps.setShort(9, player.getCellY());
            player.nowPlayTime = (System.currentTimeMillis() - player.loginTime) / 60000;
            ps.setLong(10, player.totalPlayTime + player.nowPlayTime);
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            ps.setInt(12, player.surplusSkillPoint); //add by zhengl
            ps.setInt(13, player.getLoverValue());
            ps.setTimestamp(14, new Timestamp(player.loginTime));
            //更新玩家当前实力值 add by jiaodongjie
            int power = ShareServiceImpl.getInstance().calPlayerPower(player);
            ps.setInt(15,power);
            ps.setInt(16,player.receivedRepeateTaskTimes);

            ps.setInt(17, player.getUserID());
            

            ps.executeUpdate();

            player.needUpdateDB = false;
        }
        catch (SQLException e)
        {
            LogWriter.error(null, e);
            log.error("DB update player error : ",e);
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
            	log.error("DB update player error : ",e);
            }
        }
    }

    /**
     * 更新玩家天书ID
     * @param _player
     */
    public static void updatePlayerHeavenBookID(HeroPlayer _player){
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_HEAVEN_BOOK);

            ps.setInt(1,_player.heaven_book_ids[0]);
            ps.setInt(2,_player.heaven_book_ids[1]);
            ps.setInt(3,_player.heaven_book_ids[2]);
            ps.setInt(4,_player.surplusSkillPoint);
            ps.setInt(5,_player.getUserID());

            ps.executeUpdate();

            ps.close();
            conn.close();
        }catch (SQLException e){
            log.error("update player heaven bookID error ：",e);
        }finally
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
    
    /**
     * 因为等级变化引起的玩家属性更新
     * @param _playerUserID
     * @param _level
     * @param _vocation
     * @param _money
     * @param _exp
     */
    public void updateLevel (int _playerUserID, short _level, EVocation _vocation,
            int _money, int _exp)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER_PROPERTY_SQL);

            ps.setShort(1, _level);
            ps.setByte(2, _vocation.value());
            ps.setInt(3, _money);
            ps.setInt(4, _exp);
            ps.setInt(5, _playerUserID);

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

    public void updateDB (int _playerUserID, short _level, EVocation _vocation,
            int _money, int _exp)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER_PROPERTY_SQL);

            ps.setShort(1, _level);
            ps.setByte(2, _vocation.value());
            ps.setInt(3, _money);
            ps.setInt(4, _exp);
            ps.setInt(5, _playerUserID);

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

    /**
     * 更新玩家脱离师傅时间
     * @param userID
     * @param leftMasterTime
     */
    public void updatePlayerLeftMasterTime(int userID, long leftMasterTime){
        Connection conn = null;
        PreparedStatement ps = null;

        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UDPATE_PLAYER_LEFT_MASTER_TIME);

            ps.setTimestamp(1, new Timestamp(leftMasterTime));
            ps.setInt(2,userID);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            LogWriter.error("update player leftMasterTime error:", e);
            log.error("update player leftMasterTime error:",e);
        }finally
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
                log.error("update player leftMasterTime error: ",e);
            }
        }
    }

    /**
     * 更新宠物自动出售的物品品质
     * 
     * @param _userID
     * @param _newTrait
     */
    public void updateAutoSellTrait (int _userID, EGoodsTrait _newTrait)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_AUTO_SELL_TRAIT_SQL);

            ps.setShort(1, (short) _newTrait.value());
            ps.setInt(2, _userID);

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

    private static final String EXITS_NICKNAME = "SELECT * FROM player WHERE nickname like BINARY ? LIMIT 1";

    public static boolean playerExitsByNickname (String _nickname)
    {
        boolean isExits = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(EXITS_NICKNAME);
            pstm.setString(1, _nickname);
            ResultSet rs = pstm.executeQuery();
            if (rs.next())
            {
                isExits = true;
            }
            rs.close();
            rs = null;
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
        return isExits;
    }

    /**
     * 获取玩家等级
     * 
     * @param _userID
     * @return
     */
    public static short getRoleLevel (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        short level = 0;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_PLAYER_LEVEL_SQL);
            pstm.setInt(1, _userID);

            resultSet = pstm.executeQuery();

            if (resultSet.next())
            {
                level = resultSet.getShort("lvl");
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
                if (null != resultSet)
                {
                    resultSet.close();
                    resultSet = null;
                }
                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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

        return level;
    }
    
    /**
     * 根据玩家nickname获取账户的username
     * 
     * @param _userID
     * @return
     */
/*    public static void loadPlayerAccountInfo (HeroPlayer player)
    {
        int accountID = getAccountIDByNickName(player.getName());

        getUsernameByAccountID(accountID,player);
    }*/

    /**
     * 加载玩家账号信息
     * @param _accountID
     * @return
     */
    public static void loadPlayerAccountInfo(HeroPlayer player){
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_USERNAME_FROM_ACCOUNTID);
            pstm.setInt(1, player.getLoginInfo().accountID);

            resultSet = pstm.executeQuery();
//username,publisher,msisdn,client_version,agent,client_jar_type
            if (resultSet.next())
            {
                player.getLoginInfo().username = resultSet.getString("username");
                player.getLoginInfo().password = resultSet.getString("password");
                player.getLoginInfo().loginMsisdn = resultSet.getString("msisdn");
                player.getLoginInfo().publisher = Integer.parseInt(resultSet.getString("curr_publisher"));
                player.getLoginInfo().clientType = Short.parseShort(resultSet.getString("client_jar_type"));
                player.getLoginInfo().clientVersion = resultSet.getString("client_version");
                player.getLoginInfo().boundMsisdn = resultSet.getString("bind_msisdn");
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
                if (null != resultSet)
                {
                    resultSet.close();
                    resultSet = null;
                }
                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
     * 查询玩家是否被禁言
     * @param _userID
     * @return
     */
    public boolean getChatBlankByUserID(int accountID,int _userID){
        boolean isBlack = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_CHAT_BLANK);
            pstm.setInt(1, accountID);
            pstm.setInt(2,_userID);
            resultSet = pstm.executeQuery();
            if(resultSet.next()){
            	isBlack = true;
            }

        }catch (Exception ex)
        {
            log.error("获取禁言玩家 "+ _userID +"  error : ",ex);
            LogWriter.error("", ex);
        }
        finally
        {
            try
            {
                if (null != resultSet)
                {
                    resultSet.close();
                    resultSet = null;
                }
                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
        return isBlack;
    }

    /**
     * 冻结角色
     * @param _userID
     * @param nickname
     * @param keepTime
     * @param startTime
     * @param endTime
     * @param memo
     * @return
     */
    public boolean setPlayerUserIDBlank(int _userID,String nickname,int keepTime,String startTime,String endTime,String memo){
        Connection conn = null;
        PreparedStatement pstm = null;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_ROLE_BLANK);
            pstm.setInt(1,_userID);
            pstm.setString(2,nickname);
            pstm.setInt(3,keepTime);
            pstm.setTimestamp(4,Timestamp.valueOf(startTime));
            pstm.setTimestamp(5,Timestamp.valueOf(endTime));
            pstm.setString(6,memo);
            pstm.executeQuery();

        }catch (Exception ex)
        {
            log.error("设置角色黑名单 "+ _userID +"  error : ",ex);
            return false;
        }
        finally
        {
            try
            {

                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
        return true;
    }

    /**
     * 冻结账号
     * @param _accountID
     * @param username
     * @param keepTime
     * @param startTime
     * @param endTime
     * @param memo
     * @return
     */
    public boolean setPlayerAccountIDBlank(int _accountID,String username,int keepTime,String startTime,String endTime,String memo){
        Connection conn = null;
        PreparedStatement pstm = null;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_ACCOUNT_BLANK);
            pstm.setInt(1,_accountID);
            pstm.setString(2,username);
            pstm.setInt(3,keepTime);
            pstm.setTimestamp(4,Timestamp.valueOf(startTime));
            pstm.setTimestamp(5,Timestamp.valueOf(endTime));
            pstm.setString(6,memo);
            pstm.executeQuery();
        }catch (Exception ex)
        {
            log.error("设置账号黑名单 "+ _accountID +"  error : ",ex);
            return false;
        }
        finally
        {
            try
            {

                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
        return true;
    }

    /**
     * 角色禁言
     * @param _userID
     * @param nickname
     * @param keepTime
     * @param startTime
     * @param endTime
     * @param memo
     * @return
     */
    public boolean setPlayerChatBlank(int _userID,String nickname,int keepTime,String startTime,String endTime,String memo){
        Connection conn = null;
        PreparedStatement pstm = null;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_CHAT_BLANK);
            pstm.setInt(1,_userID);
            pstm.setString(2,nickname);
            pstm.setInt(3,keepTime);
            pstm.setTimestamp(4,Timestamp.valueOf(startTime));
            pstm.setTimestamp(5,Timestamp.valueOf(endTime));
            pstm.setString(6,memo);
            pstm.executeQuery();
        }catch (Exception ex)
        {
            log.error("设置禁言角色  "+ _userID +" error : ",ex);
            return false;
        }
        finally
        {
            try
            {

                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
        return true;
    }

    /**
     * 解冻角色
     * @param _userID
     * @return
     */
    public boolean deletePlayerUserIDBlack(int _userID){
        Connection conn = null;
        PreparedStatement pstm = null;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_FORBID_ROLE_SQL);
            pstm.setInt(1,_userID);
            pstm.executeUpdate();
        }catch (Exception ex)
        {
            log.error("删除角色黑名单 "+ _userID +"  error : ",ex);
            return false;
        }
        finally
        {
            try
            {

                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
        return true;
    }

    /**
     * 解冻账号
     * @param _accountID
     * @return
     */
    public boolean deletePlayerAccountIDBlack(int _accountID){
        Connection conn = null;
        PreparedStatement pstm = null;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_FORBID_ACCOUNT_SQL);
            pstm.setInt(1,_accountID);
            pstm.executeUpdate();
        }catch (Exception ex)
        {
            log.error("删除账号黑名单 "+ _accountID +"  error : ",ex);
            return false;
        }
        finally
        {
            try
            {

                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
        return true;
    }

    /**
     * 解冻禁言
     * @param _userID
     * @return
     */
    public boolean deletePlayerChatBlack(int _userID){
        Connection conn = null;
        PreparedStatement pstm = null;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_CHAT_BLANK_SQL);
            pstm.executeUpdate();
        }catch (Exception ex)
        {
            log.error("删除角色禁言 "+ _userID +" error : ",ex);
            return false;
        }
        finally
        {
            try
            {

                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
        return true;
    }
    
    /**
     * 设置玩家脱离新手状态
     * @param _player
     */
    public static void updateNovice (int _userID)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_NOVICE_SQL);
            ps.setInt(1, _userID);
            ps.executeUpdate();
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
            }
        }
    }
    /**
     * 获得玩家是否新手状态
     * @param _userID
     * @return
     */
    public static int getNovice (int _userID)
    {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int novice = 1;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state.executeQuery("select novice from player where user_id=" 
            		+ _userID + " limit 1");
            if (result.next())
            	novice = result.getInt("novice");
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (state != null)
                    state.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return novice;
    }
    
    /**
     * 更新玩家基本信息表
     * 
     * @param _onlinePlayerList
     */
    public static void updatePlayerInfo (FastList<HeroPlayer> _playerList)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(UPDATE_PLAYER_SQL);

            boolean needUpdateDB = false;

            HeroPlayer player;

            for (int i = 0; i < _playerList.size(); i++)
            {
                player = _playerList.get(i);

                if (player.isEnable() && player.needUpdateDB)
                {
                    if (System.currentTimeMillis()
                            - player.getLastTimeOfUPdateDB() >= INTERVAL_OF_UPDATE_DB)
                    {
                        pstm.setShort(1, player.getVocation().value());
                        pstm.setShort(2, player.getLevel());
                        pstm.setInt(3, player.getMoney());
                        pstm.setInt(4, player.getExp());
                        pstm.setInt(5, player.getHp());
                        pstm.setInt(6, player.getMp());
                        pstm.setInt(7, player.where().getID());
                        pstm.setShort(8, (short) player.getCellX());
                        pstm.setShort(9, (short) player.getCellY());
                        player.nowPlayTime = (System.currentTimeMillis() - player.loginTime) / 60000;
                        pstm.setLong(10, player.totalPlayTime + player.nowPlayTime);
                        pstm.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
                        pstm.setInt(12, player.surplusSkillPoint);
                        //edit by zhengl; date: 2011-02-11; note: 修改这的BUG,love等级需要添加上.
//                        pstm.setInt(13, player.getUserID());
                        pstm.setInt(13, player.getLoverValue());
                        pstm.setTimestamp(14, new Timestamp(player.loginTime));
                        //更新玩家当前实力值 add by jiaodongjie
                        int power = ShareServiceImpl.getInstance().calPlayerPower(player);
                        pstm.setInt(15,power);
                        pstm.setInt(16,player.receivedRepeateTaskTimes);

                        pstm.setInt(17, player.getUserID());
                        //end

                        pstm.addBatch();
                        needUpdateDB = true;
                        player.needUpdateDB = false;
                        player
                                .setLastTimeOfUPdateDB(System
                                        .currentTimeMillis());
                    }
                }
            }

            if (needUpdateDB)
            {
                pstm.executeBatch();
                conn.commit();
            }

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
                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }
                if (null != conn)
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
     * 初始化玩家数据库
     * 
     * @param _playerUserID
     * @return
     */
    public static int initPlayerDB (int _accountID, short _serverID,
            int _userID, String _nickname, EClan _clan, EVocation _vocation,
            ESex _sex, short _clientType)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_PLAYER_SQL);
            /*add by zhengl; date: 2011-05-18; note: 解决conn.commit()异常*/
            conn.setAutoCommit(false);

            pstm.setInt(1, _userID);
            pstm.setInt(2, _accountID);
            pstm.setShort(3, _serverID);
            pstm.setString(4, _nickname);
            pstm.setShort(5, _sex.value());
            pstm.setShort(6, _clan.getID());
            pstm.setShort(7, _vocation.value());
            pstm.setShort(8, (short) 1);
            pstm.setInt(9, PlayerServiceImpl.getInstance().getConfig().init_money);
            pstm.setInt(10, PlayerServiceImpl.getInstance().getConfig().getInitHp(_vocation));
            pstm.setInt(11, PlayerServiceImpl.getInstance().getConfig().getInitMp(_vocation));
            pstm.setShort(12, PlayerServiceImpl.getInstance().getInitBornMapID(_clan));
            pstm.setShort(13, PlayerServiceImpl.getInstance().getInitBornX(_clan));
            pstm.setShort(14, PlayerServiceImpl.getInstance().getInitBornY(_clan));
            pstm.setShort(15, PlayerServiceImpl.getInstance().getInitBornMapID(_clan));
            pstm.setInt(16, PlayerServiceImpl.getInstance().getConfig().init_surplus_skill_point);
            pstm.setLong(17, System.currentTimeMillis() - 5*60*1000);
            pstm.setLong(18, System.currentTimeMillis() - 5*60*1000);

            pstm.execute();
            pstm.close();
            pstm = null;

            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(INSERT_EQUIPMENT_INSTANCE_SQL);

            int[] armorIDs = PlayerServiceImpl.getInstance().getConfig()
                    .getInitArmorIDs(_vocation.getType());
            log.info("init player armorIDS length = " + armorIDs.length);
            ArrayList<EquipmentInstance> equipmentList = new ArrayList<EquipmentInstance>();

            for (int armorID : armorIDs)
            {
//                log.info("init player armorID = " + armorID);
                //edit by zhengl; date: 2011-02-25; note: 即使是新手也可以不给他送全套装备,添加为0的判断
                if(armorID != 0) {
                	EquipmentInstance armor = EquipmentFactory.getInstance().build(_userID,
                			_userID, armorID);
//                	log.info("armor = " + armor);
                	equipmentList.add(armor);
                }
            }
            
            //add by zhengl; date: 2011-02-27; note: 添加首饰下发
            armorIDs = PlayerServiceImpl.getInstance().getConfig()
            .getInitJewelryIDs(_vocation.getType());
            log.info("init player armorIDS length = " + armorIDs.length);
            for (int armorID : armorIDs)
            {
//                log.info("init player armorID = " + armorID);
                if(armorID != 0) {
                	EquipmentInstance armor = EquipmentFactory.getInstance().build(_userID,
                			_userID, armorID);
//                	log.info("armor = " + armor);
                	equipmentList.add(armor);
                }
            }
            //add end
            

            int initWeaponID = PlayerServiceImpl.getInstance().getConfig().getInitWeaponID(_vocation);
            log.info("initWeaponID = " + initWeaponID);

            EquipmentInstance initWeapon = EquipmentFactory.getInstance().build(
                    _userID,
                    _userID,
                    PlayerServiceImpl.getInstance().getConfig().getInitWeaponID(_vocation));
            log.info("init Weapon = " + initWeapon);
            equipmentList.add(initWeapon);
            log.info("init player equip size = " + equipmentList.size());
            for (EquipmentInstance ei : equipmentList)
            {
//            	log.info("ei.getInstanceID():"+ei.getInstanceID());
                pstm.setInt(1, ei.getInstanceID());
//                log.info("ei.getArchetype().getID():"+ei.getArchetype().getID());
                pstm.setInt(2, ei.getArchetype().getID());
//                log.info("ei.getCreatorUserID():"+ei.getCreatorUserID());
                pstm.setInt(3, ei.getCreatorUserID());
//                log.info("ei.getOwnerUserID():"+ei.getOwnerUserID());
                pstm.setInt(4, ei.getOwnerUserID());
//                log.info("ei.getCurrentDurabilityPoint():"+ei.getCurrentDurabilityPoint());
                pstm.setInt(5, ei.getCurrentDurabilityPoint());
//                log.info("ei.getCurrentDurabilityPoint():"+ei.getCurrentDurabilityPoint());
                //edit by zhengl; date: 2011-02-27; note: 设置绑定
                pstm.setInt(6, 1); //直接设置绑定

                pstm.addBatch();
            }

            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(INSERT_PLAYER_BODY_EQUIPMENT_SQL);

            for (EquipmentInstance ei : equipmentList)
            {
                pstm.setInt(1, ei.getInstanceID());
                pstm.setInt(2, _userID);
                pstm.setShort(3, EquipmentBag.BODY);

                pstm.addBatch();
            }

            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(INSERT_MEDICAMENT_SQL);
            int[][] medicamentDataList = PlayerServiceImpl.getInstance()
                    .getConfig().getInitMedicamentData(_vocation);
            //edit by zhengl; date: 2011-02-18; note: 新手可以不奖励消耗品
            if(medicamentDataList != null) 
            {
                for (int i = 0; i < medicamentDataList.length; i++)
                {
                    pstm.setInt(1, _userID);
                    pstm.setShort(2, SingleGoods.TYPE_MEDICAMENT);
                    pstm.setInt(3, medicamentDataList[i][0]);
                    pstm.setInt(4, medicamentDataList[i][1]);
                    pstm.setInt(5, i);

                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.commit();
                pstm.close();
                pstm = null;
            }
            //end

            pstm = conn.prepareStatement(INSERT_SKILL_SQL);
            //edit: zhengl
            //date:	2010-11-16
            //note:	删除老的技能初始化方法
//            int[] skillList = PlayerServiceImpl.getInstance().getConfig()
//                    .getInitSkillList(_vocation);
            ArrayList<Skill> skills = SkillDict.getInstance().getSkillsByVocation(_vocation);
            //end
            log.debug("新玩家技能初始化:");
            log.debug("新玩家职业:"+_vocation.getDesc());
            log.debug(skills.size() + "个技能被初始化");
            int initSkillID = PlayerServiceImpl.getInstance().getConfig().getInitSkill(_vocation);
            for (Skill skill : skills)
            {
                pstm.setInt(1, _userID);
                if (skill.next != null && skill.next.id == initSkillID) 
                {
                	pstm.setInt(2, initSkillID);
				}
                else 
                {
                	pstm.setInt(2, skill.id);
				}
                

                log.debug("为userid="+_userID+"的"+_vocation.getDesc()+"职业的玩家加载技能:"
                		+skill.name+";技能限制职业:"+skill.learnerVocation[0].getDesc() );
                pstm.addBatch();
            }

            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;

            conn.setAutoCommit(true);
            initHotKeyByVocation(_vocation);
            pstm = conn.prepareStatement(INSERT_SHORTCUT_KEY_SQL);
            pstm.setInt(1, _userID);
            pstm.setString(2, DEFAULT_SHORTCUT_KEY_DESC);

            pstm.executeUpdate();
            pstm.close();
            pstm = null;

            // 设置初始邮件

            return _userID;
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
            log.error("sql cmd error:",ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    if (!conn.getAutoCommit())
                    {
                        conn.setAutoCommit(true);
                    }

                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return -1;
    }

    public static int getAccountIDByNickName (String name)
    {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int accountID = 0;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state
                    .executeQuery("select account_id from player where nickname='"
                            + name + "' limit 1");
            if (result.next())
                accountID = result.getInt("account_id");
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (state != null)
                    state.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return accountID;
    }

    public static int getAccountIDByMSISDN (String number)
    {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int accountID = 0;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state
                    .executeQuery("select account_id from player where msisdn='"
                            + number + "' limit 1");
            if (result.next())
                accountID = result.getInt("account_id");
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (state != null)
                    state.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return accountID;
    }

    public static int getUserIDByName (String name)
    {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int userID = 0;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state
                    .executeQuery("select user_id from player where nickname='"
                            + name + "' limit 1");
            if (result.next())
                userID = result.getInt("user_id");
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (state != null)
                    state.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return userID;
    }

    /**
     * 从数据库获取快捷键列表
     * 
     * @param _userID 角色编号
     * @return
     */
    public static final int[][] loadShortcutKeyList (int _userID,
            int[][] _shortcutKeyList)
    {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(SELECT_SHORTCUT_KEY_SQL);
            ps.setInt(1, _userID);
            set = ps.executeQuery();

            String[] singleShortcutKeyDescList;

            if (set.next())
            {
                String shortcutKeyDesc = set.getString("shortcut_key");

                singleShortcutKeyDescList = shortcutKeyDesc
                        .split(SHORTCUT_KEY_SEPARATOR);
            }
            else
            {
                set.close();
                set = null;
                ps.close();
                ps = null;

                ps = conn.prepareStatement(INSERT_SHORTCUT_KEY_SQL);
                ps.setInt(1, _userID);
                ps.setString(2, DEFAULT_KEY_OF_WALKING_DESC);
                ps.executeUpdate();
                ps.close();
                ps = null;

                singleShortcutKeyDescList = DEFAULT_KEY_OF_WALKING_DESC
                        .split(SHORTCUT_KEY_SEPARATOR);
            }

            if (null != singleShortcutKeyDescList
                    && singleShortcutKeyDescList.length > 0)
            {
                String[] shortcutKeyInfo;
                int shortcutKey;

                for (String singleShortcutKeyDesc : singleShortcutKeyDescList)
                {
                    shortcutKeyInfo = singleShortcutKeyDesc
                            .split(SHORTCUT_KEY_CONNECTOR);

                    shortcutKey = Integer.parseInt(shortcutKeyInfo[0]);

                    _shortcutKeyList[shortcutKey - 1][0] = Integer
                            .parseInt(shortcutKeyInfo[1]);
                    _shortcutKeyList[shortcutKey - 1][1] = Integer
                            .parseInt(shortcutKeyInfo[2]);
                }
            }
        }
        catch (Exception sqle)
        {
            LogWriter.error(null, sqle);
        }
        finally
        {
            try
            {
                if (set != null)
                {
                    set.close();
                }
                if (ps != null)
                {
                    ps.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return _shortcutKeyList;
    }

    /**
     * 更新快捷键
     * 
     * @param _userID
     * @param _shortcutKeyList
     */
    public void updateShortcutKey (int _userID, int[][] _shortcutKeyList)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            String shortcutKeyDesc = formatShortcutKey(_shortcutKeyList);

            if (null != shortcutKeyDesc)
            {
                conn = DBServiceImpl.getInstance().getConnection();
                ps = conn.prepareStatement(UPDATE_SHORTCUT_KEY_SQL);
                ps.setString(1, shortcutKeyDesc);
                ps.setInt(2, _userID);
                ps.executeUpdate();
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
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
            }
        }
    }
    /**
     * 更新玩家最后一次所领取的礼包序列
     * @param _userID
     * @param _giftID
     */
    public static void updateLastGift (int _userID, int _giftID)
    {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	
    	try
    	{
			conn = DBServiceImpl.getInstance().getConnection();
			ps = conn.prepareStatement(UPDATE_PLAYER_LAST_GIFT_SQL);
			ps.setInt(1, _giftID);
			ps.setInt(2, _userID);
			ps.executeUpdate();
    	}
    	catch (Exception e)
    	{
    		LogWriter.error(null, e);
    	}
    	finally
    	{
    		try
    		{
    			if (ps != null)
    				ps.close();
    			if (conn != null)
    				conn.close();
    		}
    		catch (Exception e)
    		{
    		}
    	}
    }

    /**
     * 将快捷键格式化为数据库存储格式
     * 
     * @return
     */
    private static String formatShortcutKey (int[][] _shortcutKeyList)
    {
        if (null != _shortcutKeyList && _shortcutKeyList.length > 0)
        {
            StringBuffer defaultChar = new StringBuffer();

            for (int i = 0; i < _shortcutKeyList.length; i++)
            {
                if (0 != _shortcutKeyList[i][0])
                {
                    defaultChar.append(i + 1).append(SHORTCUT_KEY_CONNECTOR)
                            .append(_shortcutKeyList[i][0]).append(
                                    SHORTCUT_KEY_CONNECTOR).append(
                                    _shortcutKeyList[i][1]).append(
                                    SHORTCUT_KEY_SEPARATOR);
                }
            }

            return defaultChar.toString();
        }

        return null;
    }

    /**
     * 默认初始快捷键
     */
    private static String          DEFAULT_SHORTCUT_KEY_DESC;

    /**
     * 同类元素之间的连接符
     */
    private static final String    BAG_SIZE_CONNECTOR                    = "&";

    /**
     * 保存数据库的间隔时间
     */
    private static int             INTERVAL_OF_UPDATE_DB                 = 60 * 1000;

    /**
     * 多个快捷键设置分隔符号
     */
    private static final String    SHORTCUT_KEY_SEPARATOR                = "#";

    /**
     * 快捷键设置连接符
     */
    private static final String    SHORTCUT_KEY_CONNECTOR                = "&";
    
//    /**
//     * 第二栏固定的快捷键
//     * edit:	zhengl
//     * date:	2011-03-04
//     * note:	应策划需求更改现有快捷键设置.
//     */
//    private static final byte[][]  SECOND_FIELD_FIXED_SHORTCUT_KEY       = {
//            {1, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 14 },
//            {2, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 9 },
//            {3, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 16 }   };
//
//    /**
//     * 行走键
//     */
//    private static final byte[][]  KEY_OF_WALKING                        = {
//            {10, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 1 },
//            {11, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 2 },
//            {12, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 3 },
//            {13, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 4 }      };

    /**
     * 第一栏固定的快捷键
     */
    private static final int[][] FIELD_FIXED_SHORTCUT_KEY  = { 
    	{1, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SKILL, 1 },
    	{2, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS, 0 },
        {3, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS, 0 },
        {4, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 15 }, /*bag*/
        {5, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 14 },
    	{8, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 24 }, 
    };

    /**
     * 第二栏固定的快捷键
     * edit:	zhengl
     * date:	2011-03-04
     * note:	应策划需求更改现有快捷键设置.
     */
    private static final byte[][]  SECOND_FIELD_FIXED_SHORTCUT_KEY       = {
            {1, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 8 },
            {2, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 17 },
            {3, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 9 },
            {4, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 16 }   };

    /**
     * 行走键
     */
    private static final byte[][]  KEY_OF_WALKING                        = {
            {10, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 1 },
            {11, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 2 },
            {12, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 3 },
            {13, PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM, 4 }      };

    /**
     * 缺省行走键数据库描述
     */
    private static final String    DEFAULT_KEY_OF_WALKING_DESC           = "4&1&5#5&1&8#6&1&14#7&1&24#10&1&1#11&1&2#12&1&3#13&1&4#14&1&6#15&1&9#16&1&11#17&1&16#18&1&10#23&1&1#24&1&2#25&1&3#26&1&4#";

    /**
     * 确认登出时间
     */
    public static final long       DEFAULT_LOGOUT_TIME                   = 315504000000L;
}
