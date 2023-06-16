package hero.charge;

import java.sql.Timestamp;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-4 下午05:44:04
 * @描述 ：与计费相关的玩家信息（使用经验书累计时间、离线累计时间、剩余冲值点数等等）
 */

public class ChargeInfo
{

    public int accountID;
    public int userID;
    public byte paytype;//1:神州付 2:网游
    public byte rechargetype; //1:自己充值 2:给别人充值
    public int other_account_id;//其它人的账号
    public int other_user_id;// 其它人角色ID
    public String trans_id;//流水号
    public Timestamp rechargetime;
    public int price;//充值的金钱

    public String fpcode;//充值时的计费伪码

    public byte result;//充值结果 1：支付成功 0：支付失败
    public Timestamp result_time; //返回结果的时间
    public String order_id; //计费平台返回的订单号
    public String statusCode; //状态态码

    /**
     * 构造
     * 
     * @param _userID
     */
    public ChargeInfo(int _userID)
    {
        userID = _userID;
        //add by zhengl; date: 2011-03-13; note: 测试用.用后删除
        /*if(pointAmount < 1) {
        	pointAmount = 50000;
        }*/
    }

    /**
     * 剩余游戏点数
     */
    public int  pointAmount;

    /**
     * 经验书累计时间
     */
    public long expBookTimeTotal;

    /**
     * 狩猎经验书累计时间
     */
    public long huntBookTimeTotal;

    /**
     * 离线累计时间
     */
    public long offLineTimeTotal;

    /**
     * 添加游戏点数
     * 
     * @param _point
     * @return
     */
    public int addPointAmount (int _point)
    {
        if (_point > 0)
        {
            return pointAmount += _point;
        }

        return -1;
    }

    /**
     * 減少游戏点数
     * 
     * @param _point
     * @return
     */
    public int reducePointAmount (int _point)
    {
        if (pointAmount >= _point)
        {
            return pointAmount -= _point;
        }

        return -1;
    }
}
