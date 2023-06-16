package hero.share.service;

import hero.gm.service.GmServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.share.EVocation;
import hero.share.Inotice;
import hero.share.RankInfo;
import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-6
 * Time: 上午10:05
 */
public class ShareDAO {
    private static Logger log = Logger.getLogger(ShareDAO.class);

    /**
     * 全职业等级排行
     */
    private static final String RANK_LEVEL = "select user_id,nickname,vocation,lvl from player order by lvl desc,total_play_time desc limit 20";
    /**
     * 单独职业等级排行
     */
    private static final String RANK_LEVEL_SINGLE_VOCATION = "select user_id,nickname,vocation,lvl from player where vocation=? order by lvl desc,total_play_time desc limit 20";
    /**
     * 两个职业的等级排行
     */
    private static final String RANK_LEVEL_TWO_VOCATION = "select user_id,nickname,vocation,lvl from player where vocation=? or vocation=? order by lvl desc,total_play_time desc limit 20";

    /**
     * 全职业财富排行
     */
    private static final String RANK_MONEY = "select user_id,nickname,vocation,money from player order by money desc,total_play_time desc limit 20";
    /**
     * 单独职业财富排行
     */
    private static final String RANK_MONEY_VOCATION = "select user_id,nickname,vocation,money from player where vocation=? order by money desc,total_play_time desc limit 20";
    /**
     * 两个职业财富排行
     */
    private static final String RANK_MONEY_TWO_VOCATION = "select user_id,nickname,vocation,money from player where vocation=? or vocation=? order by money desc,total_play_time desc limit 20";

    /**
     * 全职业爱情值排行
     */
    private static final String RANK_LOVER_VALUE = "select user_id,nickname,vocation,lover_value from player where lover_value>0 order by lover_value desc,total_play_time desc limit 20";
    /**
     * 单独职业爱情排行
     */
    private static final String RANK_LOVER_VALUE_VOCATION = "select user_id,nickname,vocation,lover_value from player where vocation=? and lover_value>0 order by lover_value desc,total_play_time desc limit 20";
    /**
     * 两个职业爱情排行
     */
    private static final String RANK_LOVER_VALUE_TWO_VOCATION = "select user_id,nickname,vocation,lover_value from player where (vocation=? or vocation=?) and lover_value>0 order by lover_value desc,total_play_time desc limit 20";

    /**
     * 查询杀敌排行的时间段：本周内
     */
    private static String SUB_DATE_SQL = "t.create_time>=DATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) DAY) " +
            "AND t.create_time<DATE_ADD(DATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) DAY),INTERVAL 1 WEEK)";
    /**
     * 杀敌排行
     */
    private static String RANK_KILLER = "select p.user_id,p.num,t.nickname,t.vocation from" +
            "(select count(*) as num,winner_user_id as user_id from pvp t where "+SUB_DATE_SQL+" group by winner_user_id) p," +
            "player t where p.user_id=t.user_id order by p.num desc,t.total_play_time desc limit 30";
    /**
     * 单独职业杀敌排行
     */
    private static String RANK_KILLER_VOCATION = "select p.user_id,p.num,t.nickname,t.vocation from" +
            "(select count(*) as num,winner_user_id as user_id from pvp t where winner_vocation=? and "+SUB_DATE_SQL+" group by winner_user_id) p," +
            "player t where p.user_id=t.user_id order by p.num desc,t.total_play_time desc limit 30";
    /**
     * 两个职业杀敌排行
     */
    private static String RANK_KILLER_TWO_VOCATION = "select p.user_id,p.num,t.nickname,t.vocation from" +
            "(select count(*) as num,winner_user_id as user_id from pvp t where (winner_vocation=? or winner_vocation=?) and "+SUB_DATE_SQL+" group by winner_user_id) p," +
            "player t where p.user_id=t.user_id order by p.num desc,t.total_play_time desc limit 30";
    /**
     * 玩家实力排行榜
     * 先查找玩家的USERID，名称和职业
     * 再加载玩家的其它属性，然后再排序
     * 排序每天更新两次
     * 全职业排序
     */
//    private static final String SELECT_PLAYER_USERID_SQL = "select user_id,nickname,vocation from player order by lvl desc,vocation desc limit 500";

    /**
     * 根据玩家职业查找玩家的USERID，名称和职业
     * 再加载玩家的其它属性，然后再排序
     * 排序每天更新两次
     * 查找单个职业排序
     */
//    private static final String SELECT_PLAYER_USERID_VOCATION_SQL = "select user_id,nickname,vocation from player where vocation=? order by lvl desc limit 500";

    /**
     * 全职业实力排行
     */
    private static final String RANK_POWER = "select t.user_id,t.nickname,t.vocation,t.power_value from player t order by t.power_value desc,t.total_play_time desc limit 20";
    /**
     * 单独职业实力排行
     */
    private static final String RANK_POWER_VOCATION = "select t.user_id,t.nickname,t.vocation,t.power_value from player t where t.vocation=? order by t.power_value desc,t.total_play_time desc limit 20";
    /**
     * 两个职业实力排行
     */
    private static final String RANK_POWER_TWO_VOCATION = "select t.user_id,t.nickname,t.vocation,t.power_value from player t where t.vocation=? or t.vocation=? order by t.power_value desc,t.total_play_time desc limit 20";

    /**
     * 帮派排行，如果等级相同，则按人数排序
     */
    private static final String RANK_GUILD = "SELECT g.id,g.name,g.level,COUNT(*) AS num FROM guild_member m,(SELECT id,name,level FROM guild) g WHERE m.guild_id=g.id " +
            "GROUP BY g.id,g.level,g.name ORDER BY g.level DESC,num DESC ";

    /**
     *公告/活动
     */
    private static String SELECT_INDEX_NOTICE = "select t.id,t.title,t.content,t.is_top,t.color from index_notice t where (t.server_id=0 or t.server_id=?) " +
            " %ftype and t.is_show=1 order by t.is_top asc,t.sequence asc,t.update_time desc";
    
//    /**
//     * 通用凭证领取奖励的参与资格验证语句 (用户是否领取过) 2
//     */
//    private static String SELECT_EVIDENVE_RECEIVE_IS_JOIN_IT = "SELECT * FROM %ftable WHERE receive_account_id = ?";
//    /**
//     * 通用凭证领取奖励的凭证是否已经使用 3
//     */
//    private static String SELECT_EVIDENVE_RECEIVE_IS_BY_USE = "SELECT * FROM %ftable WHERE ";
//    /**
//     * 通用凭证领取奖励的输入是否正确(暂时支持最大2个参数) 1
//     */
//    private static String SELECT_EVIDENVE_RECEIVE_BY_INPUT = "SELECT * FROM %ftable WHERE ";
//
//    /**
//     * 通用凭证领取奖励后领取更新(暂时支持最大2个参数)
//     */
//    private static String UPDATE_EVIDENVE_RECEIVE = "UPDATE %ftable SET receive_account_id = ?, receive_user_id = ? WHERE ";

    /**
     * 获取公告/活动列表
     * @return
     */
    public static List<Inotice> getInoticeList(int type){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<Inotice> inoticeList = null;

        try{
            if(type > 0){
                SELECT_INDEX_NOTICE = SELECT_INDEX_NOTICE.replaceAll("%ftype"," and type="+type);
            }else {
                SELECT_INDEX_NOTICE = SELECT_INDEX_NOTICE.replaceAll("%ftype","");
            }
            conn = GmServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(SELECT_INDEX_NOTICE);
            ps.setInt(1,GmServiceImpl.serverID);

            rs = ps.executeQuery();
            Inotice inotice;

            inoticeList = new ArrayList<Inotice>();

            while (rs.next()){
                inotice = new Inotice();
                inotice.id=rs.getInt("id");
                inotice.title = rs.getString("title");
                inotice.content = rs.getString("content");
                inotice.top = rs.getInt("is_top");
                inotice.color = ShareServiceImpl.getInstance().hexStr2Int(rs.getString("color"));

                inoticeList.add(inotice);
            }

            rs.close();
            ps.close();
            conn.close();

        }catch (Exception e){
            log.error("获取公告/活动列表 error:",e);
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
        return inoticeList;
    }
    
    /**
     * 是否已经被使用过3
     * @param _tableName
     * @param _columnName
     * @param _key
     * @return
     */
    public static boolean isByUse(String _tableName, String[] _columnName, String[] _key)
    {
    	boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String RECEIVE_IS_BY_USE = "SELECT receive_account_id FROM %ftable WHERE "
        	.replaceAll("%ftable", _tableName);
        try
        {
        	for (int i = 0; i < _columnName.length; i++) 
        	{
        		if (i == 0) 
        		{
        			RECEIVE_IS_BY_USE += " "+ _columnName[i] +" = ? ";
				}
        		else 
        		{
        			RECEIVE_IS_BY_USE += " and "+ _columnName[i] +" = ? ";
				}
			}
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(RECEIVE_IS_BY_USE);
            for (int i = 0; i < _key.length; i++) 
            {
				ps.setString(i +1, _key[i]);
			}
            rs = ps.executeQuery();
            int receAccount = -1;
            while (rs.next()) 
            {
            	//已经被使用过了.
            	receAccount  = rs.getInt("receive_account_id");
            	if (receAccount > 0) 
            	{
            		result = true;
            		break;
				}
			}

            rs.close();
            ps.close();
            conn.close();
		} 
        catch (Exception e) {
			log.error("查询是否已经使用失败", e);
		}finally
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
    	
    	return result;
    }
    
    /**
     * 查询用户时候曾参与过该领取2
     * @param _tableName
     * @param _nowAccountID
     * @return
     */
    public static boolean isJoinIt(String _tableName, int _nowAccountID)
    {
    	boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String RECEIVE_IS_JOIN_IT = "SELECT * FROM %ftable WHERE receive_account_id = ?";
        try 
        {
        	RECEIVE_IS_JOIN_IT = RECEIVE_IS_JOIN_IT.replaceAll("%ftable", _tableName);
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(RECEIVE_IS_JOIN_IT);
            ps.setInt(1, _nowAccountID);
            rs = ps.executeQuery();
            while (rs.next()) 
            {
            	//已经参与过了.
            	result = true;
            	break;
			}
            rs.close();
            ps.close();
            conn.close();
		} 
        catch (Exception e) {
			log.error("查询是否已经领取失败", e);
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
    	
    	return result;
    }
    /**
     * 输入凭证是否存在1
     * @param _tableName
     * @param _columnName
     * @param _key
     * @return
     */
    public static boolean InputVerify(String _tableName, String[] _columnName, String[] _key)
    {
    	boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String RECEIVE_BY_INPUT = "SELECT count(1) as row FROM %ftable WHERE ".replaceAll(
        		"%ftable", _tableName);
        try
        {
        	for (int i = 0; i < _columnName.length; i++) 
        	{
        		if (i == 0) 
        		{
        			RECEIVE_BY_INPUT += " "+ _columnName[i] +" = ? ";
				}
        		else 
        		{
        			RECEIVE_BY_INPUT += " and "+ _columnName[i] +" = ? ";
				}
			}
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(RECEIVE_BY_INPUT);
            for (int i = 0; i < _key.length; i++) 
            {
				ps.setString(i +1, _key[i]);
			}
            rs = ps.executeQuery();
            int receAccount = -1;
            while (rs.next()) 
            {
            	receAccount  = rs.getInt("row");
            	if (receAccount > 0) 
            	{
            		result = true;
            		break;
				}
			}
            rs.close();
            ps.close();
            conn.close();
		} 
        catch (Exception e) {
			log.error("查询是否已经使用失败", e);
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

    	
    	return result;
    }
    
    public static boolean updateEvidenveRece(String _tableName, String[] _columnName, String[] _key, 
    		int _accountID, int _userID)
    {
    	boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String UPDATE_EVIDENVE_RECEIVE = "UPDATE %ftable SET receive_account_id = ?, receive_user_id = ? WHERE "
        	.replaceAll("%ftable", _tableName);
        try
        {
        	for (int i = 0; i < _columnName.length; i++) 
        	{
        		if (i == 0) 
        		{
        			UPDATE_EVIDENVE_RECEIVE += " "+ _columnName[i] +" = ? ";
				}
        		else 
        		{
        			UPDATE_EVIDENVE_RECEIVE += " and "+ _columnName[i] +" = ? ";
				}
			}
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(UPDATE_EVIDENVE_RECEIVE);
            ps.setInt(1, _accountID);
            ps.setInt(2, _userID);
            for (int i = 0; i < _key.length; i++) 
            {
				ps.setString(i +3, _key[i]);
			}
            if (ps.executeUpdate() > 0) {
            	result = true;
			}

            ps.close();
            conn.close();
		} 
        catch (Exception e) {
			log.error("更新已经领取数据失败", e);
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

        return result;
    }
    
    /**
     * 帮派排行
     * @return
     */
    public static List<RankInfo> getGuildRankList(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try{
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(RANK_GUILD);
            rs = ps.executeQuery();

            int i=1,value;
            byte guildLevel;
            String name;

            while (rs.next()){
                name = rs.getString("name");
                guildLevel = rs.getByte("level");
                value  = rs.getInt("num");

                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = 0;
                rankInfo.name = name;
                rankInfo.vocation = guildLevel+"";
                rankInfo.value = value;

                rankInfoList.add(rankInfo);

                i++;
            }
        }catch (Exception e){
            log.error("帮派排行 error: ",e);
        }finally
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

        return rankInfoList;
    }

    /**
     * @param vocaiton1
     * @param vocation2
     * @param moreVocations 是否查看两个职业的数据，如果是，则 vocation1、vocation2都必须>0,否则 vocation2=0 ,如果是全职业,=false且vocation1=0
     * @return
     */
    public static List<RankInfo> getPowerRankList(int vocation1,int vocation2,boolean moreVocations){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;

        try{
        	conn = DBServiceImpl.getInstance().getConnection();
            if(moreVocations){
                ps = conn.prepareStatement(RANK_POWER_TWO_VOCATION);
                ps.setInt(1,vocation1);
                ps.setInt(2,vocation2);
            }else{
                if(vocation1 == 0){
                    ps = conn.prepareStatement(RANK_POWER);
                }else {
                    ps = conn.prepareStatement(RANK_POWER_VOCATION);
                    ps.setInt(1,vocation1);
                }
            }
            rs = ps.executeQuery();

            int i=1,userID,value;
            byte vocationValue;
            String name;
            while (rs.next()){
                name = rs.getString("nickname");
                vocationValue = rs.getByte("vocation");
                userID = rs.getInt("user_id");
                value  = rs.getInt("power_value");

                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfo.value = value;

                rankInfoList.add(rankInfo);

                i++;
            }

        }catch (Exception e){
            log.error("实力排行 error: ",e);
        }finally
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

        return rankInfoList;
    }

    /**
     * 杀敌排行
     * @param vocaiton1
     * @param vocation2
     * @param moreVocations 是否查看两个职业的数据，如果是，则 vocation1、vocation2都必须>0,否则 vocation2=0 ,如果是全职业,=false且vocation1=0
     * @return
     */
    public static List<RankInfo> getKillerRankInfoList(int vocation1,int vocation2,boolean moreVocations){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try{
        	conn = DBServiceImpl.getInstance().getConnection();
            if(moreVocations){
                ps = conn.prepareStatement(RANK_KILLER_TWO_VOCATION);
                ps.setInt(1,vocation1);
                ps.setInt(2,vocation2);
            }else {
                if(vocation1 == 0){
                    ps = conn.prepareStatement(RANK_KILLER);
                }else {
                    ps = conn.prepareStatement(RANK_KILLER_VOCATION);
                    ps.setInt(1,vocation1);
                }
            }
            rs = ps.executeQuery();
            int i=1,value,userID;
            byte vocationValue;
            String name;
            while (rs.next()){
                name = rs.getString("nickname");
                value = rs.getInt("num");
                vocationValue = rs.getByte("vocation");
                userID = rs.getInt("user_id");

                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.value = value;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();

                rankInfoList.add(rankInfo);

                i++;
            }
        }catch (Exception e){
            log.error("杀敌排行 error: ",e);
        }finally
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
        return rankInfoList;
    }

    /**
     * 等级排行
     * @param vocaiton1
     * @param vocation2
     * @param moreVocations 是否查看两个职业的数据，如果是，则 vocation1、vocation2都必须>0,否则 vocation2=0 ,如果是全职业,=false且vocation1=0
     * @return
     */
    public static List<RankInfo> getLevelRankInfoList(int vocation1,int vocation2,boolean moreVocations){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try{
        	conn = DBServiceImpl.getInstance().getConnection();
            if(moreVocations){
                ps = conn.prepareStatement(RANK_LEVEL_TWO_VOCATION);
                ps.setInt(1,vocation1);
                ps.setInt(2,vocation2);
            }else {
                if(vocation1 == 0){
                    ps = conn.prepareStatement(RANK_LEVEL);
                }else {
                    ps = conn.prepareStatement(RANK_LEVEL_SINGLE_VOCATION);
                    ps.setInt(1,vocation1);
                }
            }
            rs = ps.executeQuery();
            int i=1,value,userID;
            byte vocationValue;
            String name;
            while (rs.next()){
                name = rs.getString("nickname");
                value = rs.getInt("lvl");
                vocationValue = rs.getByte("vocation");
                userID = rs.getInt("user_id");

                if(value > PlayerServiceImpl.getInstance().getConfig().max_level){
                    value = PlayerServiceImpl.getInstance().getConfig().max_level;
                }

                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.value = value;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();

                rankInfoList.add(rankInfo);

                i++;
            }
        }catch (Exception e){
            log.error("等级排行 error: ",e);
        }finally
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
        return rankInfoList;
    }

    /**
     * 财富排行
     * @param vocaiton1
     * @param vocation2
     * @param moreVocations 是否查看两个职业的数据，如果是，则 vocation1、vocation2都必须>0,否则 vocation2=0 ,如果是全职业,=false且vocation1=0
     * @return
     */
    public static List<RankInfo> getMoneyRankInfoList(int vocation1,int vocation2,boolean moreVocations){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try{
        	conn = DBServiceImpl.getInstance().getConnection();
            if(moreVocations){
                ps = conn.prepareStatement(RANK_MONEY_TWO_VOCATION);
                ps.setInt(1,vocation1);
                ps.setInt(2,vocation2);
            }else {
                if(vocation1 == 0){
                    ps = conn.prepareStatement(RANK_MONEY);
                }else {
                    ps = conn.prepareStatement(RANK_MONEY_VOCATION);
                    ps.setInt(1,vocation1);
                }
            }
            rs = ps.executeQuery();
            int i=1,value,userID;
            byte vocationValue;
            String name;
            while (rs.next()){
                name = rs.getString("nickname");
                value = rs.getInt("money");
                vocationValue = rs.getByte("vocation");
                userID = rs.getInt("user_id");

                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfo.value = value;

                rankInfoList.add(rankInfo);

                i++;
            }
        }catch (Exception e){
            log.error("财富排行 error: ",e);
        }finally
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
        return rankInfoList;
    }
    /**
     * 爱情排行
     * @param vocaiton1
     * @param vocation2
     * @param moreVocations 是否查看两个职业的数据，如果是，则 vocation1、vocation2都必须>0,否则 vocation2=0 ,如果是全职业,=false且vocation1=0
     * @return
     */
    public static List<RankInfo> getLoverValueRankInfoList(int vocation1,int vocation2,boolean moreVocations){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try{
        	conn = DBServiceImpl.getInstance().getConnection();
            if(moreVocations){
                ps = conn.prepareStatement(RANK_LOVER_VALUE_TWO_VOCATION);
                ps.setInt(1,vocation1);
                ps.setInt(2,vocation2);
            }else {
                if(vocation1 == 0){
                    ps = conn.prepareStatement(RANK_LOVER_VALUE);
                }else {
                    ps = conn.prepareStatement(RANK_LOVER_VALUE_VOCATION);
                    ps.setInt(1,vocation1);
                }
            }
            rs = ps.executeQuery();
            int i=1,value,userID;
            byte vocationValue;
            String name;
            while (rs.next()){
                name = rs.getString("nickname");
                value = rs.getInt("lover_value");
                vocationValue = rs.getByte("vocation");
                userID = rs.getInt("user_id");

                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfo.value = value;

                rankInfoList.add(rankInfo);

                i++;
            }
        }catch (Exception e){
            log.error("爱情排行 error: ",e);
        }finally
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
        return rankInfoList;
    }

}
