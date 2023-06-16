package hero.charge.service;

import hero.charge.ChargeInfo;
import hero.gm.service.GmServiceImpl;
import hero.item.special.HuntExperienceBook;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.share.service.LogWriter;

import java.sql.*;

import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-5 下午02:34:24
 * @描述 ：
 */

public class ChargeDAO
{
    private static Logger log = Logger.getLogger(ChargeDAO.class);
    /**
     * 加载与计费相关的时间信息
     */
    public static void loadTimeInfo (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            _player.getChargeInfo().offLineTimeTotal = _player.lastLogoutTime > PlayerDAO.DEFAULT_LOGOUT_TIME ? System
                    .currentTimeMillis()
                    - _player.lastLogoutTime
                    : 0;
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_TIME_INFO_SQL);

            pstm.setInt(1, _player.getUserID());
            set = pstm.executeQuery();

            if (set.next())
            {
                long expBookTimeTotal = set.getLong("exp_book_time_total");
                long huntBookTimeTotal = set
                        .getLong("hunt_exp_book_time_total");

                _player.getChargeInfo().expBookTimeTotal = expBookTimeTotal;
                _player.getChargeInfo().huntBookTimeTotal = huntBookTimeTotal;
                if (_player.getChargeInfo().huntBookTimeTotal > 0) 
                {
                	_player.changeExperienceModulus(HuntExperienceBook.EXP_MODULUS);
                	//add by zhengl; date: 2011-05-13; note: 添加经验书启动过程
                	ExperienceBookService.getInstance().put(_player.getChargeInfo());
				}
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
    }

    /**
     * 更新经验书剩余时间信息
     */
    public static void updateExpBookTimeInfo (ChargeInfo _chargeInfo)
    {
//        Connection conn = null;
//        PreparedStatement pstm = null;
//
//        try
//        {
//            conn = DBServiceImpl.getInstance().getConnection();
//            pstm = conn.prepareStatement(UPDATE_EXP_BOOK_TIME_SQL);
//
//            pstm.setLong(1, _chargeInfo.expBookTimeTotal);
//            pstm.setInt(2, _chargeInfo.userID);
//            int exist = pstm.executeUpdate();
//
//            if (exist == 0)
//            {
//                pstm.close();
//                pstm = null;
//
//                pstm = conn.prepareStatement(INSERT_EXP_BOOK_TIME_SQL);
//
//                pstm.setInt(1, _chargeInfo.userID);
//                pstm.setLong(2, _chargeInfo.expBookTimeTotal);
//                pstm.execute();
//            }
//        }
//        catch (Exception e)
//        {
//            LogWriter.error(null, e);
//        }
//        finally
//        {
//            try
//            {
//                if (null != pstm)
//                {
//                    pstm.close();
//                }
//
//                if (null != conn)
//                {
//                    conn.close();
//                }
//            }
//            catch (Exception e)
//            {
//
//            }
//        }
    }

    /**
     * 更新狩猎经验书剩余时间信息
     */
    public static void updateHuntExpBookTimeInfo (ChargeInfo _chargeInfo)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_HUNT_EXP_BOOK_TIME_SQL);

            pstm.setLong(1, _chargeInfo.huntBookTimeTotal);
            pstm.setInt(2, _chargeInfo.userID);
            int exist = pstm.executeUpdate();

            if (exist == 0)
            {
                pstm.close();
                pstm = null;

                pstm = conn.prepareStatement(INSERT_HUNT_EXP_BOOK_TIME_SQL);

                pstm.setInt(1, _chargeInfo.userID);
                pstm.setLong(2, _chargeInfo.huntBookTimeTotal);
                pstm.execute();
            }
        }
        catch (Exception e)
        {
            LogWriter.error("error:更新狩猎经验书失败", e);
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
    }

    /**
     * 更新经验书相关信息
     */
    public static void updateExpBookInfo (ChargeInfo _chargeInfo)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_EXP_BOOK_INFO_SQL);

            pstm.setLong(1, _chargeInfo.huntBookTimeTotal);
            pstm.setInt(2, _chargeInfo.userID);
            pstm.executeUpdate();
        }
        catch (Exception e)
        {
            LogWriter.error("error:更新经验书失败", e);
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
    }

    /**
     * 删除经验书相关信息
     */
    public static void clearExpBookInfo (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_EXP_BOOK_INFO_SQL);

            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        }
        catch (Exception e)
        {
            LogWriter.error("error:删除经验书失败", e);
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
    }



    /**
     *
     * 神州付充值时记录信息
     * 因为是异步的所以结果和结果时间在异步返回时更新
     *
     * @param accountID
     * @param userID
     * @param paytype  1:神州付 2:网游
     * @param rechargetype  1:自己充值 2:给别人充值
     * @param otherAccountID  其它人的账号ID
     * @param otherUserID   其它人的角色ID
     * @param transID   服务器产生的唯一流水号
     * @param statusCode  同步返回的状态码
     * @param price  金钱
     * @param syncRes 同步的结果
     * @param orderID  计费平台返回的订单号
     * @param fpcode 计费伪码
     */
    public static void insertChargeUpSZF(int accountID,int userID,byte paytype,byte rechargetype,int otherAccountID,
                                         int otherUserID,String transID,String statusCode,int price,String orderID,int syncRes,String fpcode){
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_CHARGE_UP_SZF);

            pstm.setInt(1, accountID);
            pstm.setInt(2, userID);
            pstm.setByte(3,paytype);
            pstm.setByte(4,rechargetype);
            pstm.setInt(5,otherAccountID);
            pstm.setInt(6,otherUserID);
            pstm.setString(7,transID);
            pstm.setString(8,statusCode);
            pstm.setInt(9,price);
            pstm.setString(10,orderID);
            pstm.setInt(11,syncRes);
            pstm.setString(12,fpcode);
            pstm.executeUpdate();

            pstm.close();
            conn.close();
        }
        catch (Exception e)
        {
            log.error("神州付充值时记录信息:", e);
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
    }

    /**
     * 充值时在xj_account数据库里的 szf_chargeup 记录信息
     * 在神州付异步返回时，根据服务器和订单号到指定的服务器查找信息
     * @param gameID
     * @param serverID
     * @param accountID  当前充值的账号ID
     * @param userID    当前充值的角色ID
     * @param transID
     * @param orderID
     */
    public static void insertChargeUpSZFAccount(int gameID,int serverID,int accountID,int userID,String transID,String orderID){
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_SZF_CHARGE);

            pstm.setInt(1, GmServiceImpl.gameID);
            pstm.setInt(2, GmServiceImpl.serverID);
            pstm.setInt(3, accountID);
            pstm.setInt(4, userID);
            pstm.setString(5, transID);
            pstm.setString(6,orderID);
            pstm.executeUpdate();

            pstm.close();
            conn.close();
        }
        catch (Exception e)
        {
            log.error("充值时在xj_account数据库里的 szf_chargeup 记录信息 error: ", e);
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
    }

    /**
     *  神州付异步充值结果返回时，更新充值信息的结果和返回结果的时间
     * @param result
     * @param resultTime
     * @param transID
     * @param orderID
     */
    public static int updateChargeSZF(byte result,Timestamp resultTime,String transID,String orderID){
        Connection conn = null;
        PreparedStatement pstm = null;
        int res = 0;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_CHARGE_UP_RESULT);

            pstm.setByte(1,result);
            pstm.setTimestamp(2,resultTime);
            pstm.setString(3,orderID);
            pstm.setString(4,transID);

            res = pstm.executeUpdate();

            pstm.close();
            conn.close();
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
        return res;
    }

    /**
     * 网游充值时记录信息
     * 同步插入结果和结果时间
     * @param accountID
     * @param userID
     * @param paytype
     * @param rechargetype
     * @param otherAccountID
     * @param otherUserID
     * @param transID
     * @param statusCode
     * @param price
     * @param orderID
     * @param result
     * @param resultTime
     */
    public static void insertChargeUpNG(int accountID,int userID,byte paytype,byte rechargetype,int otherAccountID,
                                         int otherUserID,String transID,String statusCode,int price,int syncRes,String fpcode){
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_CHARGE_UP_NG);

            pstm.setInt(1, accountID);
            pstm.setInt(2, userID);
            pstm.setByte(3,paytype);
            pstm.setByte(4,rechargetype);
            pstm.setInt(5,otherAccountID);
            pstm.setInt(6,otherUserID);
            pstm.setString(7,transID);
            pstm.setString(8,statusCode);
            pstm.setInt(9,price);
            pstm.setInt(10,syncRes);
            pstm.setString(11,fpcode);
            pstm.executeUpdate();

            pstm.close();
            conn.close();
        }
        catch (Exception e)
        {
            log.error("网游充值时记录信息 error: ", e);
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
    }

    /**
     * 获取充值信息
     * @param userID
     * @param transID
     * @param orderID
     * @return
     */
    public static ChargeInfo getChargeInfo(String transID,String orderID){
        Connection conn = null;
        PreparedStatement pstm = null;
//        Statement pstm = null;
        ResultSet rs = null;
        ChargeInfo info = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_CHARGE_UP_INFO);
//            pstm.setInt(1,userID);
            pstm.setString(1,transID);
            pstm.setString(2,orderID);
            rs = pstm.executeQuery();

//            pstm = conn.createStatement();
//            rs = pstm.executeQuery("select * from charge_up t where t.user_id="+userID+" and t.trans_id='"+transID+"' and t.order_id='"+orderID+"'");


            if(rs.next()){
                int userID = rs.getInt("user_id");
                int accountID = rs.getInt("account_id");
                byte paytype = rs.getByte("paytype");
                byte rechargetype = rs.getByte("rechargetype");

                info = new ChargeInfo(userID);

                if(rechargetype == 2){
                    int otherAccountID = rs.getInt("other_account_id");
                    int otherUserID = rs.getInt("other_user_id");
                    info.other_account_id = otherAccountID;
                    info.other_user_id = otherUserID;
                }
                String statusCode = rs.getString("status_code");
                int price = rs.getInt("price");
                String fpcode = rs.getString("fpcode");
                String transid = rs.getString("trans_id");

                Timestamp rechargeTime = rs.getTimestamp("rechargetime");

                info.accountID = accountID;
                info.paytype = paytype;
                info.rechargetype = rechargetype;
                info.price = price;
                info.statusCode = statusCode;
                info.rechargetime = rechargeTime;
                info.fpcode = fpcode;
                info.trans_id = transid;
            }

        }catch (SQLException e){
            log.error("获取充值信息error :",e);
            e.printStackTrace();
        }finally {
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
        return info;
    }

    /**
     * 保存SMS计费配置信息
     * @param accountID
     * @param userID
     * @param transID
     * @param mobileUserID
     * @param sumPrice
     * @param serverID
     */
    public static void saveSmsFeeIni(int accountID,int userID,String transID,String mobileUserID,int sumPrice,int serverID){
        Connection conn = null;
        PreparedStatement pstm = null;
        try{
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_SMS_FEE_INI);
            pstm.setInt(1,accountID);
            pstm.setInt(2,userID);
            pstm.setString(3,transID);
            pstm.setString(4,mobileUserID);
            pstm.setInt(5,sumPrice);
            pstm.setInt(6,serverID);

            pstm.executeUpdate();

        }catch (SQLException e){
            log.error("保存SMS计费配置信息error :",e);
            e.printStackTrace();
        }finally {
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
    }

    private static final String INSERT_SMS_FEE_INI = "insert into sms_fee_ini(account_id,role_id,trans_id,mobile_user_id,sum_price,server_id) value (?,?,?,?,?,?)";

    /**
     * 神州付充值时记录信息
     * 因为是异步的所以结果和结果时间在异步返回时更新
     */
    private static final String INSERT_CHARGE_UP_SZF = "insert into charge_up(account_id,user_id,paytype,rechargetype,other_account_id,other_user_id,trans_id,status_code,price,order_id,status_result,fpcode) " +
            " values(?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     * 网游充值时记录信息
     * 同步插入结果和结果时间
     */
    private static final String INSERT_CHARGE_UP_NG = "insert into charge_up(account_id,user_id,paytype,rechargetype,other_account_id,other_user_id,trans_id,status_code,price,status_result,fpcode) " +
            " values(?,?,?,?,?,?,?,?,?,?,?)";

    /**
     * 充值时在xj_account数据库里的 szf_chargeup 记录信息
     * 在神州付异步返回时，根据服务器和订单号到指定的服务器查找信息
     */
    private static final String INSERT_SZF_CHARGE = "insert into szf_chargeup(game_id,server_id,account_id,role_id,trans_id,order_id) values(?,?,?,?,?,?)";

    /**
     * 神州付异步充值结果返回时，更新充值信息的结果和返回结果的时间
     */
    private static final String UPDATE_CHARGE_UP_RESULT = "update charge_up t set t.result=?,t.result_time=? where t.order_id=? and t.trans_id=?";

    /**
     * 查询充值信息
     */
    private static final String SELECT_CHARGE_UP_INFO = "select * from charge_up t where t.trans_id=? and t.order_id=?";

//    /**
//     * 加载点数余额SQL教本
//     */
//    private static final String SELECT_POINT_AMOUNT_SQL       = "SELECT game_point_amount FROM account WHERE account_id = ? LIMIT 1";
//
//    /**
//     * 更新游戏点数余额SQL教本
//     */
//    private static final String UPDATE_POINT_AMOUNT_SQL       = "UPDATE account SET game_point_amount = ? WHERE account_id = ? LIMIT 1";

    /**
     * 加载与计费相关的时间信息脚本
     */
    private static final String SELECT_TIME_INFO_SQL          = "SELECT * FROM player_time_info WHERE user_id = ? LIMIT 1";

//    /**
//     * 更新经验书剩余时间脚本
//     */
//    private static final String UPDATE_EXP_BOOK_TIME_SQL      = "UPDATE player_time_info SET exp_book_time_total=?"
//                                                                      + " WHERE user_id = ? LIMIT 1";

    /**
     * 更新狩猎经验书剩余时间脚本
     */
    private static final String UPDATE_HUNT_EXP_BOOK_TIME_SQL = "UPDATE player_time_info SET hunt_exp_book_time_total=?"
                                                                      + " WHERE user_id = ? LIMIT 1";

    /**
     * 插入经验书剩余时间信息脚本
     */
    private static final String INSERT_EXP_BOOK_TIME_SQL      = "INSERT INTO player_time_info(user_id,exp_book_time_total) VALUES(?,?)";

    /**
     * 插入狩猎经验书剩余时间信息脚本
     */
    private static final String INSERT_HUNT_EXP_BOOK_TIME_SQL = "INSERT INTO player_time_info(user_id,hunt_exp_book_time_total) VALUES(?,?)";

    /**
     * 更新时间信息脚本
     */
    private static final String UPDATE_EXP_BOOK_INFO_SQL      = "UPDATE player_time_info SET hunt_exp_book_time_total=? "
                                                                      + " WHERE user_id = ? LIMIT 1";

    /**
     * 删除时间信息脚本
     */
    private static final String DELETE_EXP_BOOK_INFO_SQL      = "DELETE FROM player_time_info"
                                                                      + " WHERE user_id = ? LIMIT 1";
}
