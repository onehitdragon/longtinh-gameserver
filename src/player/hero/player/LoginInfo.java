package hero.player;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LoginInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-20 下午02:26:16
 * @描述 ：
 */

public class LoginInfo
{
    /**
     * 高、中、低端标识
     */
    public short  clientType;

    /**
     * 客户端版本
     */
    public String  clientVersion;

    /**
     * 客户端与服务器端通讯协议（HTTP,TCP）
     */
    public short  communicatePipe;

    /**
     * 账号ID
     */
    public int    accountID;

    /**
     * 用户名
     */
    public String username;

    /**
     * 密码
     */
    public String password;

    /**
     * 登陆手机号码
     */
    public String loginMsisdn="";

    /**
     * 绑定手机号码
     */
    public String boundMsisdn;

    /**
     * 手机型号
     */
    public String userAgent="";

    /**
     * 服务器ID
     */
//    public short  serverID    = 1;

    /**
     * 手机归属地区域ID
     */
    public short  msisdnZoon  = -1;

    /**
     * 下线原因，玩家推出游戏时、GM踢玩家下线时赋值，如果此值为NULL，则为超时下线
     */
    public String logoutCause = "超时";
    /**
     * 发行方标识
     */
    public int publisher;
}
