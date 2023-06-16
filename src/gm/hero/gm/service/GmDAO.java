package hero.gm.service;

import hero.chat.service.GmNotice;
import hero.gm.EBlackType;
import hero.item.Equipment;
import hero.item.Material;
import hero.item.Medicament;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.TaskTool;
import hero.item.bag.EquipmentContainer;
import hero.item.dictionary.MaterialDict;
import hero.item.dictionary.MedicamentDict;
import hero.item.dictionary.SpecialGoodsDict;
import hero.item.dictionary.TaskGoodsDict;
import hero.item.service.EquipmentFactory;
import hero.map.Map;
import hero.player.define.EClan;
import hero.player.define.ESex;
import hero.share.EVocation;
import hero.share.letter.Letter;
import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;


public class GmDAO
{
	private static Logger log = Logger.getLogger(GmDAO.class);
    /**
     * 加载玩家装备
     */
    private static final String SQL_QUERY_EQUIPMENT                = "SELECT equipment_id,"
                                                                           + "container_type "
                                                                           + "FROM equipment_instance "
                                                                           + "JOIN player_carry_equipment "
                                                                           + "ON user_id=? AND "
                                                                           + "player_carry_equipment.instance_id="
                                                                           + "equipment_instance.instance_id LIMIT 100";

    /**
     * 获取所有非装备物品
     */
    private static final String SQL_QUERY_SINGLE_GOODS             = "SELECT goods_id,goods_type,goods_number "
                                                                           + "from player_single_goods "
                                                                           + "where user_id=? LIMIT 280";

    /**
     * 获取账号黑名单
     */
    private static final String SQL_QUERY_BLACKLIST_ACCOUNT        = "select * from account_black;";

    /**
     * 获取角色黑民单
     */
    private static final String SQL_QUERY_BLACKLIST_ROLE           = "select * from role_black;";

    /**
     * 获取聊天黑名单
     */
    private static final String SQL_QUERY_BLACKLIST_CHAT           = "select * from chat_black;";

    /**
     * 查找GM用户名
     */
    private static final String SQL_QUERY_GM_NAME                  = "select name from gm where name=? and pwd=? limit 1";

    /**
     * 修改GM密码
     */
    private static final String SQL_MODIFY_GM_PWD                  = "update gm set pwd=? where name=? limit 1";

    /**
     * 查找GM的用户名
     */
    private static final String SQL_QUERY_GM_USERNAME              = "select username from account where account_id=? limit 1";

    /**
     * 查询账号黑名单的account_id
     */
    private static final String SQL_QUERY_GM_ACCOUNT_ID            = "select account_id from account_black where account_id=? limit 1";

    /**
     * 加入账号黑名单
     */
    private static final String SQL_ADD_ACCOUNT_BLACK              = "insert into account_black values(?,?,?,?,?)";

    /**
     * 从account表中获取accountID
     */
    private static final String SQL_QUERY_ACCOUNTID_FROM_ACCOUNT   = "select account_id from account where username=? limit 1";

    /**
     * 从role表中获取accountID
     */
    private static final String SQL_QUERY_ACCOUNTID_FROM_ROLE      = "select account_id from role where nickname=? limit 1";

    /**
     * 从角色黑名单中获取userID
     */
    private static final String SQL_QUERY_USERID_FROM_ROLEBACK     = "select user_id from role_black where nickname=? limit 1";

    /**
     * 从account表中获取username
     */
    private static final String SQL_QUERY_USERNAME_FROM_ACCOUNT    = "select username from account where account_id=? limit 1";

    /**
     * 查找roleblack中的userID
     */
    private static final String SQL_QUERY_USERID_FROM_ROLEBLACK    = "select user_id from role_black where user_id=? limit 1";

    /**
     * 加入角色黑名单
     */
    private static final String SQL_ADD_ROLE_BLACK                 = "insert into role_black values(?,?,?,?,?)";

    /**
     * 从chatblack中获取userid
     */
    private static final String SQL_QUERY_USERID_FROM_CHATBLACK    = "select user_id from chat_black where user_id=? limit 1";

    /**
     * 加入聊天黑名单
     */
    private static final String SQL_ADD_CHAT_BLACK                 = "insert into chat_black values(?,?,?,?,?)";

    /**
     * 移除聊天黑名单
     */
    private static final String SQL_REMOVE_CHAT_BLACK              = "delete from chat_black where nickname=? limit 1";

    /**
     * 移除账户黑名单
     */
    private static final String SQL_REMOVE_ACCOUNT_BLACK           = "delete from account_black where username=? limit 1";

    /**
     * 移除角色黑名单
     */
    private static final String SQL_REMOVE_ROLE_BLACK              = "delete from role_black where user_id=? limit 1";

    /**
     * 更新账户新密码
     */
    private static final String SQL_UPDATE_ACCOUNT_PWD             = "update account set password=? where username=? limit 1";

    /**
     * 更新账户名
     */
    private static final String SQL_UPDATE_ACCOUNT_NAME            = "update account set username=? where username=? limit 1";

    /**
     * 获取ENDTIME从chatblack
     */
    // private static final String SQL_QUERY_ENDTIME_FROM_CHAT_BLACK = "select
    // end_time from chat_black where nickname=? limit 1";
    /**
     * 获取账户信息
     */
    private static final String SQL_QUERY_ACCOUNTINFO_FROM_ACCOUNT = "select * from account where username=? limit 1";

    /**
     * 获取角色信息
     */
    private static final String SQL_QUERY_ROLEINFO                 = "select * from player where account_id=? limit 3";

    /**
     * 改变角色所在地图
     */
    private static final String SQL_MODIFY_ROLE_MAP                = "update player set where_id=? where nickname=? limit 1";

    /**
     * 玩家发邮件给GM
     */
    private static final String INSERT_GM_LETTER = "insert into gm_letter(sender_role_id,content,type,serverID) values(?,?,?,?)";
    
    /**
     * 根据回复邮件的GMid 查询GM名和邮件内容、回复时间
     */
    private static final String SELECT_GM_REPLY_LETTER_INFO = "select t.name,gl.reply_content,gl.reply_time,gl.sender_role_id from gm t,gm_letter gl " +
    		"where t.id=gl.reply_gm_id and gl.id=? limit 1";
    
    /**
     * 查询当前未结束的公告
     *
     */
    private static final String SELECT_GM_NOTICE_SQL = "select * from gm_notice where (serverID=? or serverID=0) and end_time>? order by start_time";

    /**
     * 查询充值赠点活动期间赠送的点数
     * 根据充值的金额只查询该金额最新的一条记录
     */
    private static final String SELECT_RECHARGE_PRESENT_POINT = "select present_point from recharge_present_point where price=? and (server_id=? or server_id=0) and start_time<? and end_time>? order by update_time desc limit 1";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 查询充值赠点活动期间赠送的点数
     * 根据充值的金额只查询该金额最新的一条记录
     * @param price 充值金额
     * @return
     */
    public static int getPresentPoint(int price){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int presentPoint = 0;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(SELECT_RECHARGE_PRESENT_POINT);
            ps.setInt(1,price);
            ps.setInt(2,GmServiceImpl.serverID);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setString(3,dateFormat.format(now));
            ps.setString(4,dateFormat.format(now));
            rs = ps.executeQuery();
            if(rs.next()){
                presentPoint = rs.getInt("present_point");
            }
            rs.close();
            ps.close();
        }catch (Exception ex)
        {
        	log.error("查询公告列表 :",ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
            	if(rs != null){
            		rs.close();
            	}
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return presentPoint;
    }

    public static java.util.Map<Integer,GmNotice> getGmNoticeList(int serverID){
    	java.util.Map<Integer,GmNotice> gmNoticeMap = new HashMap<Integer,GmNotice>();
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        GmNotice notice = null;
        try{
        	conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SELECT_GM_NOTICE_SQL);
            pstmt.setInt(1, serverID);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            pstmt.setString(2,dateFormat.format(now));
            rs = pstmt.executeQuery();
            while(rs.next()){
            	int id = rs.getInt("id");
            	String content = rs.getString("content");
            	Timestamp startTime = rs.getTimestamp("start_time");
            	Timestamp endTime = rs.getTimestamp("end_time");
            	int intervalTime = rs.getInt("interval_time");
            	int times = rs.getInt("times");
            	
            	notice = new GmNotice();
            	notice.setId(id);
            	notice.setContent(content);
            	notice.setStartTime(startTime);
            	notice.setEndTime(endTime);
            	notice.setIntervalTime(intervalTime);
            	notice.setTimes(times);
            	
            	gmNoticeMap.put(id, notice);
            }
        }catch (Exception ex)
        {
        	log.error("查询公告列表 :",ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
            	if(rs != null){
            		rs.close();
            	}
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return gmNoticeMap;
    }
    
    /**
     * 根据回复邮件的GMid 查询GM名和邮件内容、回复时间
     */
    public static Letter getLetterInfo(int gmLetterID,Letter letter){
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try{
        	conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SELECT_GM_REPLY_LETTER_INFO);
            pstmt.setInt(1, gmLetterID);
            
            rs = pstmt.executeQuery();
            while(rs.next()){
            	String gm = rs.getString(1);
            	String content = rs.getString(2);
            	Timestamp reply_time = rs.getTimestamp(3);
            	int receiverID = rs.getInt(4);
            	
            	letter.senderName = "系统GM:"+gm; //名称里“系统”两个字必须要，客户端需要它来判断图标类型
            	letter.content = content;
            	letter.sendTime = reply_time.getTime();
            	letter.receiverUserID = receiverID;
            }
        }catch (Exception ex)
        {
        	log.error("根据回复邮件的GMid 查询GM名和邮件内容、回复时间 error :",ex);
            ex.printStackTrace();
        }
        finally
        {
            try
            {
            	if(rs != null){
            		rs.close();
            	}
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    	return letter;
    }
    
    /**
     * 玩家发邮件给GM
     * @param userID
     * @param content
     * @param type
     * @return
     */
    public static boolean sendGMLetter(int userID,String content, byte type){
    	Connection conn = null;
        PreparedStatement pstmt = null;
        
        try{
        	conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(INSERT_GM_LETTER);
            pstmt.setInt(1, userID);
            pstmt.setString(2, content);
            pstmt.setInt(3, type);
            pstmt.setInt(4, GmServiceImpl.serverID);
            
            pstmt.execute();
        }catch (Exception ex)
        {
        	log.error("添加 GM 邮件 error :",ex);
            ex.printStackTrace();
            return false;
        }
        finally
        {
            try
            {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return true;
    }
    
    /**
     * 修改GM密码
     * 
     * @param name
     * @param oldPwd
     * @param newPwd
     * @return
     */
    public static boolean changeGMPassword (String name, String oldPwd,
            String newPwd)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet set = null;
        boolean flag = false;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_GM_NAME);
            pstmt.setString(1, name);
            pstmt.setString(2, oldPwd);
            set = pstmt.executeQuery();
            String str = null;
            if (set.next())
                str = set.getString("name");

            if (str != null)
            {
                set.close();
                set = null;
                pstmt.close();
                pstmt = null;

                pstmt = conn.prepareStatement(SQL_MODIFY_GM_PWD);
                pstmt.setString(1, str);
                pstmt.setString(2, newPwd);
                if (pstmt.executeUpdate() > 0)
                    flag = true;
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
                if (set != null)
                    set.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 根据accountID获取手机号码
     * 
     * @param accountID
     * @return
     */
    public static String getMSISDNByAccountID (int accountID)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String msisdn = null;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_GM_USERNAME);
            pstmt.setInt(1, accountID);
            result = pstmt.executeQuery();
            if (result.next())
                msisdn = result.getString(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (result != null)
                    result.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return msisdn;
    }

    /**
     * 获取黑名单列表
     * 
     * @param _type 黑名单类型
     * @return
     */
//    public static List<Black> getBlackList (EBlackType _type)
//    {
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet set = null;
//        String sql = null;
//
//        try
//        {
//            switch (_type)
//            {
//                case ACCOUNT_LOGIN:
//                    conn = GmServiceImpl.getInstance().getConnection();
//                    sql = SQL_QUERY_BLACKLIST_ACCOUNT;
//                    break;
//                case ROLE_LOGIN:
//                    conn = GmServiceImpl.getInstance().getConnection();
//                    sql = SQL_QUERY_BLACKLIST_ROLE;
//                    break;
//                case ROLE_CHAT:
//                    conn = DBServiceImpl.getInstance().getConnection();
//                    sql = SQL_QUERY_BLACKLIST_CHAT;
//                    break;
//                default:
//                    return null;
//            }
//
//            stmt = conn.createStatement();
//            set = stmt.executeQuery(sql);
//            List<Black> list = new ArrayList<Black>();
//            
//            while (set.next())
//            {
//                String name = "";
//                switch (_type)
//                {
//                    case ACCOUNT_LOGIN:
//                        name = set.getString("username");
//                        break;
//                    case ROLE_LOGIN:
//                    case ROLE_CHAT:
//                        name = set.getString("nickname");
//                        break;
//                    default:
//                        return null;
//                }
//
//                byte keep = set.getByte("keep_time");
//                Timestamp st = set.getTimestamp("start_time");
//                Timestamp et = set.getTimestamp("end_time");
//
//                String startTime = GmServiceImpl.getTimestampStr(st);
//                String endTime = GmServiceImpl.getBlackEndTimeStr(et);
//
//                Black black = new Black(name, BlackType.getBlackType(keep),
//                        startTime, endTime);
//                list.add(black);
//            }
//            
//            return list;
//        }
//        catch (SQLException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        finally
//        {
//            try
//            {
//                if (set != null)
//                    set.close();
//                if (stmt != null)
//                    stmt.close();
//                if (conn != null)
//                    conn.close();
//            }
//            catch (SQLException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    /**
     * 将指定用户加入黑名单
     * 
     * @param accountID
     * @param name
     * @param msisdn
     * @param keepTime
     */
//    public static Black addBlackAccount (int accountID, String name,
//            byte keepTime)
//    {
//        Black black = null;
//        Connection conn = null;
//        ResultSet set = null;
//        PreparedStatement pstmt = null;
//
//        try
//        {
//            conn = GmServiceImpl.getInstance().getConnection();
//            pstmt = conn.prepareStatement(SQL_QUERY_GM_ACCOUNT_ID);
//            pstmt.setInt(1, accountID);
//            set = pstmt.executeQuery();
//
//            if (set.next())
//                return null;
//
//            pstmt.close();
//            pstmt = null;
//
//            pstmt = conn.prepareStatement(SQL_ADD_ACCOUNT_BLACK);
//            pstmt.setInt(1, accountID);
//            pstmt.setString(2, name);
//            pstmt.setByte(3, keepTime);
//            Timestamp startTime = new Timestamp(Calendar.getInstance()
//                    .getTimeInMillis());
//            pstmt.setTimestamp(4, startTime);
////            Timestamp endTime = GmServiceImpl.getEndTime(BlackType
////                    .getBlackType(keepTime), startTime);
////            pstmt.setTimestamp(5, endTime);
//            pstmt.executeUpdate();
//
////            black = new Black(name, BlackType.getBlackType(keepTime),
////                    GmServiceImpl.getTimestampStr(startTime), GmServiceImpl
////                            .getBlackEndTimeStr(endTime));
//            return black;
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//        finally
//        {
//            try
//            {
//                if (set != null)
//                    set.close();
//                if (pstmt != null)
//                    pstmt.close();
//                if (conn != null)
//                    conn.close();
//            }
//            catch (SQLException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 根据昵称获取userID
     * 
     * @param name
     * @return
     */
    public static int getRoleUIDByNickname (String name)
    {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;

        int uid = 0;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_USERID_FROM_ROLEBACK);
            pstmt.setString(1, name);
            set = pstmt.executeQuery();

            if (set.next())
                uid = set.getInt("user_id");
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return uid;
    }

    /**
     * 根据账号名获取账号ID
     * 
     * @param _username
     * @return
     */
    public static int getAccountIDByUserName (String _username)
    {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        int account_id = 0;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_ACCOUNTID_FROM_ACCOUNT);
            pstmt.setString(1, _username);
            set = pstmt.executeQuery();
            if (set.next())
                account_id = set.getInt(1);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return account_id;
    }

    /**
     * 根据角色昵称查询账号ID
     * 
     * @param _roleName
     * @return
     */
    public static int getAccountIDByRolename (String _roleName)
    {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        int account_id = 0;

        try
        {
            // 首先在role表中获取account_id
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_ACCOUNTID_FROM_ROLE);
            pstmt.setString(1, _roleName);
            set = pstmt.executeQuery();
            if (set.next())
                account_id = set.getInt(1);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return account_id;
    }

    /**
     * 根据角色昵称查询账号
     * 
     * @param _roleName
     * @return
     */
    public static String getAccountUserNameByRolename (String _roleName)
    {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        int account_id = 0;
        String username = "";

        try
        {
            // 首先在role表中获取account_id
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_ACCOUNTID_FROM_ROLE);
            pstmt.setString(1, _roleName);
            set = pstmt.executeQuery();
            if (set.next())
                account_id = set.getInt(1);
            set.close();
            set = null;
            pstmt.close();
            pstmt = null;

            // 再在account表中获取username
            if (account_id > 0)
            {
                pstmt = conn.prepareStatement(SQL_QUERY_USERNAME_FROM_ACCOUNT);
                pstmt.setInt(1, account_id);
                set = pstmt.executeQuery();
                if (set.next())
                    username = set.getString(1);
                set.close();
                pstmt.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return username;
    }

    /**
     * 添加角色黑名单
     * 
     * @param userid
     * @param name
     * @param keepTime
     * @return
     */
//    public static Black addBlackRole (int userid, String name, byte keepTime)
//    {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet set = null;
////        Black black = null;
//
//        try
//        {
//            conn = GmServiceImpl.getInstance().getConnection();
//            pstmt = conn.prepareStatement(SQL_QUERY_USERID_FROM_ROLEBLACK);
//            pstmt.setInt(1, userid);
//            set = pstmt.executeQuery();
//
//            if (set.next())
//                return null;
//            set.close();
//            set = null;
//            pstmt.close();
//            pstmt = null;
//
//            pstmt = conn.prepareStatement(SQL_ADD_ROLE_BLACK);
//            pstmt.setInt(1, userid);
//            pstmt.setString(2, name);
//            pstmt.setByte(3, keepTime);
//            Timestamp startTime = new Timestamp(Calendar.getInstance()
//                    .getTimeInMillis());
//            pstmt.setTimestamp(4, startTime);
//            Timestamp endTime = GmServiceImpl.getEndTime(BlackType
//                    .getBlackType(keepTime), startTime);
//            pstmt.setTimestamp(5, endTime);
//            pstmt.executeUpdate();
//
//            black = new Black(name, BlackType.getBlackType(keepTime),
//                    GmServiceImpl.getTimestampStr(startTime), GmServiceImpl
//                            .getBlackEndTimeStr(endTime));
//            return black;
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//        finally
//        {
//            try
//            {
//                if (set != null)
//                    set.close();
//                if (pstmt != null)
//                    pstmt.close();
//                if (conn != null)
//                    conn.close();
//            }
//            catch (SQLException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 添加聊天黑名单
     * 
     * @param userid
     * @param name
     * @param keepTime
     * @return
     */
//    public static Black addBlackChat (int userid, String name, byte keepTime)
//    {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet set = null;
//        Black black = null;
//        try
//        {
//            conn = DBServiceImpl.getInstance().getConnection();
//            pstmt = conn.prepareStatement(SQL_QUERY_USERID_FROM_CHATBLACK);
//            pstmt.setInt(1, userid);
//            set = pstmt.executeQuery();
//
//            if (set.next())
//                return null;
//            set.close();
//            set = null;
//            pstmt.close();
//            pstmt = null;
//
//            pstmt = conn.prepareStatement(SQL_ADD_CHAT_BLACK);
//            pstmt.setInt(1, userid);
//            pstmt.setString(2, name);
//            pstmt.setByte(3, keepTime);
//            Timestamp startTime = new Timestamp(Calendar.getInstance()
//                    .getTimeInMillis());
//            pstmt.setTimestamp(4, startTime);
//            Timestamp endTime = GmServiceImpl.getEndTime(BlackType
//                    .getBlackType(keepTime), startTime);
//            pstmt.setTimestamp(5, endTime);
//            pstmt.executeUpdate();
//
//            black = new Black(name, BlackType.getBlackType(keepTime),
//                    GmServiceImpl.getTimestampStr(startTime), GmServiceImpl
//                            .getBlackEndTimeStr(endTime));
//            return black;
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//        finally
//        {
//            try
//            {
//                if (set != null)
//                    set.close();
//                if (pstmt != null)
//                    pstmt.close();
//                if (conn != null)
//                    conn.close();
//            }
//            catch (SQLException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 移除聊天黑名单
     * 
     * @param _nickname
     * @return
     */
    public static boolean deleteBlackChat (String _nickname)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean flag = false;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_REMOVE_CHAT_BLACK);
            pstmt.setString(1, _nickname);
            flag = pstmt.executeUpdate() > 0 ? true : false;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 移除账户黑名单
     * 
     * @param _username
     * @return
     */
    public static boolean deleteBlackAccount (String _username)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean flag = false;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_REMOVE_ACCOUNT_BLACK);
            pstmt.setString(1, _username);
            flag = pstmt.executeUpdate() > 0 ? true : false;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 移除登陆角色黑名单
     * 
     * @param _uid
     * @return
     */
    public static boolean deleteBlackRole (int _uid)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean flag = false;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_REMOVE_ROLE_BLACK);
            pstmt.setInt(1, _uid);
            flag = pstmt.executeUpdate() > 0 ? true : false;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 改账户密码
     * 
     * @param _password
     */
    public static boolean changeAccountPassword (String _username,
            String _password)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        boolean flag = false;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_ACCOUNTID_FROM_ACCOUNT);
            pstmt.setString(1, _username);
            resultSet = pstmt.executeQuery();
            if (resultSet.next())
            {
                flag = true;
            }
            resultSet.close();
            resultSet = null;
            pstmt.close();
            pstmt = null;

            if (flag)
            {
                pstmt = conn.prepareStatement(SQL_UPDATE_ACCOUNT_PWD);
                pstmt.setString(1, _password);
                pstmt.setString(2, _username);
                if (pstmt.executeUpdate() > 0)
                {
                    flag = true;
                }
                else
                {
                    flag = false;
                }
            }
        }
        catch (SQLException e)
        {
            flag = false;
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (resultSet != null)
                    resultSet.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return flag;
    }

    /**
     * 更改手机号，这里就是更改账户
     * 
     * @param _password
     */
    public static boolean changeAccountMobile (String _account, String _mobile)
    {
        boolean flag = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_ACCOUNTID_FROM_ACCOUNT);
            pstmt.setString(1, _account);
            resultSet = pstmt.executeQuery();
            if (!resultSet.next())
            {
                return false;
            }
            resultSet.close();
            resultSet = null;
            pstmt.close();
            pstmt = null;

            pstmt = conn.prepareStatement(SQL_UPDATE_ACCOUNT_NAME);
            pstmt.setString(1, _mobile);
            pstmt.setString(2, _account);
            if (pstmt.executeUpdate() > 0)
            {
                flag = true;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (resultSet != null)
                    resultSet.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 切换地图，指定玩家昵称
     * 
     * @param nickname
     * @param _targetMap
     * @return
     */
    public static boolean changePlayerMap (String nickname, Map _targetMap)
    {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_MODIFY_ROLE_MAP);
            pstmt.setInt(1, _targetMap.getID());
            pstmt.setInt(2, _targetMap.getBornX());
            pstmt.setInt(3, _targetMap.getBornY());
            pstmt.setString(4, nickname);

            if (pstmt.executeUpdate() > 0)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstmt != null)
                    pstmt.close();
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
     * 根据用户名获取账户信息（正式版本手机号码即是用户名）
     * 
     * @param _username
     * @return
     */
    public static String getAccountInfo (String _username)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet set = null;
        String accountInfo = "";

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_ACCOUNTINFO_FROM_ACCOUNT);
            pstmt.setString(1, _username);
            set = pstmt.executeQuery();
            if (set.next())
            {
                accountInfo = "账号ID:" + set.getInt("account_id") + "\n" + "账号:"
                        + set.getString("username") + "\n" + "密码:"
                        + set.getString("password") + "\n" + "登陆手机:"
                        + set.getString("username") + "\n" + "客户端版本:"
                        + set.getString("client_version") + "\n" + "创建时间:"
                        + set.getTimestamp("create_time");
            }
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return accountInfo;
    }

    /**
     * 获取指定账户下的所有角色信息，最多3个
     * 
     * @param _account_id
     * @return
     */
    public static String[] getRoleInfos (int _account_id)
    {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        String[] roleInfos = new String[]{};

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement(SQL_QUERY_ROLEINFO);
            pstmt.setInt(1, _account_id);
            set = pstmt.executeQuery();
            List<String> list = new ArrayList<String>();
            while (set.next())
            {
                list.add("角色ID:"
                        + set.getInt("user_id")
                        + "\n"
                        + "角色名:"
                        + set.getString("nickname")
                        + "\n"
                        + "级别:"
                        + set.getByte("lvl")
                        + "\n"
                        + "性别:"
                        + ESex.getSex(set.getByte("sex")).getDesc()
                        + "\n"
                        + "阵营:"
                        + EClan.getClan(set.getByte("clan")).getDesc()
                        + "\n"
                        + "职业:"
                        + EVocation.getVocationByID(set.getByte("vocation"))
                                .getDesc());
            }

            int len = list.size();
            if (len > 0)
            {
                roleInfos = new String[len];
                for (int i = 0; i < len; ++i)
                {
                    roleInfos[i] = list.get(i);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return roleInfos;
    }

    /**
     * 根据userID查询离线玩家的物品信息
     * 
     * @param _userID
     * @return
     */
//    public static List<RoleItem> getRoleItems (int _userID)
//    {
//        List<RoleItem> list = new ArrayList<RoleItem>();
//
//        String name = "";
//        short number = 0;
//        String des = "";
//        String insId = "";
//        String location = "";
//        int color = 0x000000;
////        RoleItem roleItem = null;
//
//        Connection conn = null;
//        PreparedStatement pstm = null;
//        ResultSet resultSet = null;
//
//        try
//        {
//            conn = DBServiceImpl.getInstance().getConnection();
//            pstm = conn.prepareStatement(SQL_QUERY_EQUIPMENT);
//            pstm.setInt(1, _userID);
//
//            resultSet = pstm.executeQuery();
//
//            int equipmentID;
//            short containerType;
//
//            while (resultSet.next())
//            {
//                equipmentID = resultSet.getInt("equipment_id");
//                containerType = resultSet.getShort("container_type");
//
//                Equipment equip = EquipmentFactory.getInstance()
//                        .getEquipmentArchetype(equipmentID);
//
//                if (null != equip)
//                {
//                    name = equip.getName();
//                    number = 1;
//                    des = equip.getDescription();
//                    insId = String.valueOf(equip.getID());
//                    if (EquipmentContainer.BODY == containerType)
//                    {
//                        location = "身上装备";
//                    }
//                    else if (EquipmentContainer.BAG == containerType)
//                    {
//                        location = "背包装备";
//                    }
//                    color = equip.getTrait().getViewRGB();
//                    roleItem = new RoleItem(name, number, des, insId, location,
//                            color);
//                    list.add(roleItem);
//                }
//            }
//
//            if (null != resultSet)
//            {
//                resultSet.close();
//            }
//            resultSet = null;
//            pstm.close();
//            pstm = null;
//
//            pstm = conn.prepareStatement(SQL_QUERY_SINGLE_GOODS);
//            pstm.setInt(1, _userID);
//
//            resultSet = pstm.executeQuery();
//
//            int goodsID;
//            short num, goodsType;
//
//            while (resultSet.next())
//            {
//                goodsID = resultSet.getInt("goods_id");
//                goodsType = resultSet.getShort("goods_type");
//                num = resultSet.getShort("goods_number");
//
//                switch (goodsType)
//                {
//                    case SingleGoods.TYPE_MATERIAL:
//                    {
//                        Material m = MaterialDict.getInstance().getMaterial(
//                                goodsID);
//                        name = m.getName();
//                        number = num;
//                        des = m.getDescription();
//                        insId = String.valueOf(m.getID());
//                        location = "材料";
//                        color = m.getTrait().getViewRGB();
//                        roleItem = new RoleItem(name, number, des, insId,
//                                location, color);
//                        list.add(roleItem);
//                        break;
//                    }
//                    case SingleGoods.TYPE_MEDICAMENT:
//                    {
//                        Medicament m = MedicamentDict.getInstance()
//                                .getMedicament(goodsID);
//                        name = m.getName();
//                        number = num;
//                        des = m.getDescription();
//                        insId = String.valueOf(m.getID());
//                        location = "药水";
//                        color = m.getTrait().getViewRGB();
//                        roleItem = new RoleItem(name, number, des, insId,
//                                location, color);
//                        list.add(roleItem);
//                        break;
//                    }
//                    case SingleGoods.TYPE_TASK_TOOL:
//                    {
//                        TaskTool t = TaskGoodsDict.getInstance().getTaskTool(
//                                goodsID);
//                        name = t.getName();
//                        number = num;
//                        des = t.getDescription();
//                        insId = String.valueOf(t.getID());
//                        location = "任务物品";
//                        color = t.getTrait().getViewRGB();
//                        roleItem = new RoleItem(name, number, des, insId,
//                                location, color);
//                        list.add(roleItem);
//                        break;
//                    }
//                    case SingleGoods.TYPE_SPECIAL_GOODS:
//                    {
//                        SpecialGoods s = SpecialGoodsDict.getInstance()
//                                .getSpecailGoods(goodsID);
//                        name = s.getName();
//                        number = num;
//                        des = s.getDescription();
//                        insId = String.valueOf(s.getID());
//                        location = "特殊物品";
//                        color = s.getTrait().getViewRGB();
//                        roleItem = new RoleItem(name, number, des, insId,
//                                location, color);
//                        list.add(roleItem);
//                        break;
//                    }
//                }
//            }
//
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//        finally
//        {
//            try
//            {
//                if (null != resultSet)
//                {
//                    resultSet.close();
//                }
//                if (null != pstm)
//                {
//                    pstm.close();
//                }
//                if (null != conn)
//                {
//                    conn.close();
//                }
//            }
//            catch (SQLException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        return list;
//    }
}
