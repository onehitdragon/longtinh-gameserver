package hero.log.service;

import hero.gm.service.GmServiceImpl;
import hero.item.Goods;
import hero.item.detail.EGoodsTrait;
import hero.map.service.IMapService;
import hero.pet.Pet;
import hero.player.HeroPlayer;

import java.sql.Timestamp;
import java.util.Date;


import org.apache.log4j.Logger;

import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.tools.log.SystemLogServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LogServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-2-24 下午03:19:02
 * @描述 ：
 */

public class LogServiceImpl extends AbsServiceAdaptor<LogConfig> implements
        IMapService
{
    private static LogServiceImpl instance;

    /**
     * 私有构造
     */
    private LogServiceImpl()
    {
        config = new LogConfig();
    }

    @Override
    protected void start ()
    {
    }

    private Logger getLogger (String name)
    {
        if (name == null || name.equals(""))
        {
            return SystemLogServiceImpl.getInstance().getLoggerByName(name);
        }
        else
        {
            return config.getLogger(name);
        }
    }

    public static LogServiceImpl getInstance ()
    {
        if (null == instance)
        {
            instance = new LogServiceImpl();
        }

        return instance;
    }

    /**
     * 创建角色日志
     * @param _accountID   账号
     * @param _userID     角色编号
     * @param _nickname   角色昵称
     * @param _clanName     阵营名称
     * @param _vocation   职业名称
     * @param _sex  性别
     * @param _clientType     客户端标识
     * @param success        是否成功
     */
    public void createDelRoleLog(String operate,int _accountID,short _serverID, int _userID, String _nickname,
                              String _clanName,String _vocation, String _sex,
                              short _clientType,boolean success){
        XmlFormatter formatter = new XmlFormatter("createDelRoleLog");
        formatter.append("date",new Date()).append("operate",operate).append("accountID",_accountID)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID).append("userID",_userID)
                .append("nickname",_nickname).append("clan",_clanName)
                .append("vocation",_vocation).append("sex",_sex)
                .append("time",DateTime.getTime(System.currentTimeMillis()))
                .append("clientType",_clientType).append("result",success?"成功":"失败");
        getLogger("createRole").info(formatter.flush());
    }

    /**
     * 角色登录日志
     * @param _userID     角色编号
     * @param _nickname   角色昵称
     * @param _mobile     手机号
     * @param _userAgent  手机型号
     * @param _clientVersion  客户端版本
     * @param _clientType     客户端标识
     * @param _conType        连接类型
     */
    public void roleLoginLog(int _accountID,int _userID, String _nickname, String _mobile, String _userAgent,
                          String _clientVersion, short _clientType, short _conType,long _loginTime,
                          int _publisher,short mapId,String _loginMapName,String ip){
    	XmlFormatter formatter = new XmlFormatter("loginRole");
        /*formatter.append(new Date()).append(_userID).append(
                _nickname).append(_mobile).append(DateTime.getTime(System.currentTimeMillis()))
                .append(_userAgent).append(_clientVersion)
                .append(_clientType).append(_conType);*/
        formatter.append("date",new Date()).append("userid",_accountID).append("role_id",_userID)
        		.append("game_id",GmServiceImpl.gameID).append("server_id",GmServiceImpl.serverID)
        		.append("nickname",_nickname).append("logintime",DateTime.getTime(_loginTime))
        		.append("agent",_userAgent).append("msisdn",_mobile).append("client_version",_clientVersion)
        		.append("publisher",_publisher).append("companyID",0).append("SystemType","")
        		.append("login_where_id",mapId).append("login_where_name",_loginMapName)
                .append("loginIP",ip)
        		.append("logoutIP",ip).append("conType",_conType);
        getLogger("roleLogin").info(formatter.flush());
    }

    /**
     * 玩家下线日志
     * 
     * @param _accountID 账号编号
     * @param _userID 角色编号
     * @param _nickname 角色昵称
     * @param _mobile 手机号
     * @param _loginTime 登陆时间
     * @param _cause 下线原因(正常下线|超时|被踢下线)
     * @param _userAgent 手机型号
     * @param _clientVersion 客户端版本号
     * @param _clientType 客户端标识
     * @param _conType 连接类型
     * @param _logoutMapName 下线时所在地图名
     */
    public void roleOnOffLog (int _accountID, int _userID, String _nickname,
            String _mobile, long _loginTime, String _cause, String _userAgent,
            String _clientVersion, short _clientType, short _conType,
            String _logoutMapName,long _logoutTime,int _publisher,short mapId)
    {
    	XmlFormatter formatter = new XmlFormatter("offOlineRole");
        /*formatter.append(new Date()).append(_accountID).append(_userID).append(
                _nickname).append(_mobile).append(DateTime.getTime(_loginTime))
                .append(_cause).append(_userAgent).append(_clientVersion)
                .append(_clientType).append(_conType).append(_logoutMapName);*/
        
        formatter.append("date",new Date()).append("userid",_accountID).append("role_id",_userID)
        			.append("game_id",GmServiceImpl.gameID).append("server_id",GmServiceImpl.serverID)
        			.append("nickname",_nickname).append("logintime",DateTime.getTime(_loginTime))
        			.append("logouttime",DateTime.getTime(_logoutTime)).append("logoutReason",_cause)
        			.append("agent",_userAgent).append("msisdn",_mobile).append("client_version",_clientVersion)
        			.append("publisher",_publisher).append("companyID",0).append("SystemType","")
        			.append("logout_where_id",mapId).append("logout_where_name",_logoutMapName)
        			.append("logoutIP","").append("conType",_conType);
        
        getLogger("roleOnOff").info(formatter.flush());
    }

    /**
     * 聊天日志
     * 
     * @param _accountID 账号编号
     * @param _userID 角色编号
     * @param _nickname 角色昵称
     * @param _mobile 手机号
     * @param _receiveID 接收者角色编号(除私聊外为0)
     * @param _receiveNickname 接收者角色昵称(除私聊外为"")
     * @param _type 聊天类型
     * @param _mapName 地图名
     * @param _content 聊天内容
     */
    public void talkLog (int _accountID, int _userID, String _nickname,
            String _mobile, int _receiveID, String _receiveNickname,
            String _type, String _mapName, String _content)
    {
        XmlFormatter formatter = new XmlFormatter("talkLog");
        formatter.append("date",new Date()).append("accountID",_accountID)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("userID",_userID).append("nickname",_nickname).append("msisdn",_mobile)
        		.append("receiverID",_receiveID)
        		.append("receiveNickname",_receiveNickname).append("type",_type)
        		.append("mapName",_mapName).append("content",_content.replaceAll("\\[", "(").replaceAll("\\]", ")"));
        getLogger("talk").info(formatter.flush());
    }

    /**
     * 角色升级日志
     * 
     * @param _accountID 账号编号
     * @param _userID 角色编号
     * @param _nickname 角色昵称
     * @param _mobile 手机号
     * @param _mapName 地图名
     * @param _lvl 升级后等级
     */
    public void upgradeLog (int _accountID, int _userID, String _nickname,
            String _mobile, String _mapName, int _lvl)
    {
        XmlFormatter formatter = new XmlFormatter("upgradeLog");
        formatter.append("date",new Date()).append("accountID",_accountID)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("userID",_userID)
        		.append("nickname",_nickname)
        		.append("msisdn",_mobile)
        		.append("mapName",_mapName)
        		.append("level",_lvl);
        getLogger("upgrade").info(formatter.flush());
    }

    /**
     * 在线人数日志
     * 
     * @param _onlineNum 在线人数
     */
    public void onlineNumLog (int _onlineNum)
    {
    	XmlFormatter formatter = new XmlFormatter("onlineNumInfo");
        formatter.append("date",new Date())
        		 .append("game_id",GmServiceImpl.gameID)
        		 .append("server_id",GmServiceImpl.serverID)
        		 .append("logtime",DateTime.getTime(System.currentTimeMillis()))
        		 .append("onlineNum",_onlineNum);
        getLogger("onlineNum").info(formatter.flush());
    }

    /**
     * 交易日志
     * 
     * @param _accountID 发起方账号编号
     * @param _userID 发起方角色编号
     * @param _nickname 发起方角色昵称
     * @param _mobile 发起方手机号
     * @param _money 发起方交易金钱
     * @param _items 发起方交易物品
     * @param _receiveAccountID 接收方账号编号
     * @param _receiveUserID 接收方角色编号
     * @param _receiveNickname 接收方角色昵称
     * @param _receiveMobile 接收方手机号
     * @param _receiveMoney 接收方交易金钱
     * @param _receiveItems 接收方交易物品
     * @param _mapName 交易所在地图
     */
    public void tradeLog (int _accountID, int _userID, String _nickname,
            String _mobile, int _money, String _items, int _receiveAccountID,
            int _receiveUserID, String _receiveNickname, String _receiveMobile,
            int _receiveMoney, String _receiveItems, String _mapName)
    {
        XmlFormatter formatter = new XmlFormatter("tradeLog");
        formatter.append("date",new Date()).append("accountID",_accountID).append("userID",_userID)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("nickname",_nickname).append("msisdn",_mobile).append("money",_money).append("goodsName",_items)
                .append("receiveAccountID",_receiveAccountID).append("receiveUserID",_receiveUserID)
                .append("receiveNickname",_receiveNickname).append("receiveMobile",_receiveMobile)
                .append("receiveMoney",_receiveMoney).append("receiveItems",_receiveItems).append("mapName",_mapName);
        getLogger("trade").info(formatter.flush());
    }

    /**
     * 金币变化日志
     * 
     * @param _accountID 账号编号
     * @param _userID 角色编号
     * @param _nickname 角色昵称
     * @param _mobile 手机号
     * @param _cause 变化原因 (自动出售劣质物品、购买拍卖行物品、拍卖行手续费、训练采集技能花费、升级采集技能花费、
     *            学习采集技能花费、训练制造技能花费
     *            、升级制造技能花费、学习制造技能花费、邮寄金币、提取邮件金币、修理装备花费、升级仓库、购买物品
     *            、出售物品、传送花费、结婚花费、
     *            交易、学习技能、任务奖励、创建公会、新手奖励、个人商店出售、个人商店购买、知识授予、怪物掉落)
     * @param _number 变化数量
     */
    public void moneyChangeLog (int _accountID, int _userID, String _nickname,
            String _mobile, String _cause, int _number)
    {
        XmlFormatter formatter = new XmlFormatter("moneyChangeLog");
        formatter.append("date",new Date()).append("accountID",_accountID).append("userID",_userID)
                .append("game_id",GmServiceImpl.gameID)
                .append("server_id",GmServiceImpl.serverID)
                .append("nickname",_nickname).append("msisdn",_mobile).append("cause",_cause).append("number",_number);
        getLogger("moneyChange").info(formatter.flush());
    }

    /**
     * 仓库变化日志
     * 
     * @param _accountID 账号编号
     * @param _userID 角色编号
     * @param _nickname 角色昵称
     * @param _mobile 手机号
     * @param _itemID 物品编号
     * @param _itemName 物品名称
     * @param _number 物品数量
     * @param _option 操作(取出|存入)
     * @param _mapName 所在地图名
     */
    public void depotChangeLog (int _accountID, int _userID, String _nickname,
            String _mobile, int _itemID, String _itemName, int _number,
            String _option, String _mapName)
    {
        XmlFormatter formatter = new XmlFormatter("depotChangeLog");
        formatter.append("date",new Date()).append("accountID",_accountID).append("userID",_userID)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("nickname",_nickname).append("msisdn",_mobile).append("goodsID",_itemID).append("goodsName",_itemName)
                .append("goodsNumber",_number).append("option",_option).append("mapName",_mapName);
        getLogger("depotChange").info(formatter.flush());
    }

    /**
     * 公会变化日志
     * 
     * @param _userID 角色编号
     * @param _nickname 角色昵称
     * @param _guildID 公会编号
     * @param _guildName 公会名
     * @param _guildNumber 公会会员数
     * @param _leaderID 新公会会长角色编号
     * @param _leaderNickname 新公会会长角色昵称
     * @param _option 公会操作(创建公会|解散工会|转让工会)
     */
    public void guildChangeLog (int _userID, String _nickname, int _guildID,
            String _guildName, int _guildNumber, int _leaderID,
            String _leaderNickname, String _option)
    {
        XmlFormatter formatter = new XmlFormatter("guildMemberChangeLog");
        formatter.append("date",new Date()).append("userID",_userID).append("nickname",_nickname)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("guildID",_guildID).append("guildName",_guildName).append("guildNumber",_guildNumber)
        		.append("lederID",_leaderID).append("leaderNickname",_leaderNickname).append("option",_option);
        getLogger("guildChange").info(formatter.flush());
    }

    /**
     * 公会成员变化日志
     * 
     * @param _userID 角色编号
     * @param _nickname 角色昵称
     * @param _guildID 公会编号
     * @param _guildName 公会名
     * @param _receiveUserID 变化角色编号
     * @param _receiveNickname 变化角色昵称
     * @param _option 操作(添加、开除、退出)
     */
    public void guildMemberChangeLog (int _userID, String _nickname,
            int _guildID, String _guildName, int _receiveUserID,
            String _receiveNickname, String _option)
    {
        XmlFormatter formatter = new XmlFormatter("guildMemberChangeLog");
        formatter.append("date",new Date()).append("userID",_userID).append("nickname",_nickname)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("guildID",_guildID).append("guildName",_guildName).append("receiverUserID",_receiveUserID)
        		.append("receiverNickname",_receiveNickname).append("option",_option);
        getLogger("guildMemberChange").info(formatter.flush());
    }

    /**
     * 玩家物品变更日志
     * 
     * @param _player 玩家
     * @param _goods 物品
     * @param _itemNum 物品数量
     * @param _loction 物品位置(身上、包裹、仓库)
     * @param _flow 物品流向(获得、失去)
     * @param _cause
     *            原因(出售,购买,交易,掉落,拍卖,邮箱,制造,丢弃,任务奖励,仓库,个人商店,采集,炼化,藤条,任务道具,商城,知识授予
     *            ,取消任务,完成任务,世界聊天,转职,解除封印,兑换,快捷键,新手奖励,卸下,穿戴)
     */
    public void goodsChangeLog (HeroPlayer _player, Goods _goods, int _itemNum,
            LoctionLog _loction, FlowLog _flow, CauseLog _cause)
    {
        if (_goods.getTrait().value() >= EGoodsTrait.YU_ZHI.value())
        {
            XmlFormatter formatter = new XmlFormatter("goodsChangeLog");
            formatter.append("date",new Date())
            		.append("game_id",GmServiceImpl.gameID)
        			.append("server_id",GmServiceImpl.serverID)
            		.append("accountID",_player.getLoginInfo().accountID)
            		.append("userID",_player.getUserID())
            		.append("nickname",_player.getName())
            		.append("loginMsisdn",_player.getLoginInfo().loginMsisdn)
            		.append("goodsID",_goods.getID())
                    .append("goodsName",_goods.getName())
                    .append("itemNum",_itemNum)
                    .append("mapName",_player.where().getName())
                    .append("loction",_loction.getName())
                    .append("flow",_flow.getName())
                    .append("cause",_cause.getName());
            getLogger("goodsChange").info(formatter.flush());
        }
    }

    /**
     * 宠物交易日志
     * @param _player
     * @param _pet
     * @param _itemNum
     * @param _loction
     * @param _flow
     * @param _cause
     */
    public void petChangeLog (HeroPlayer _player, Pet _pet, int _itemNum,
            LoctionLog _loction, FlowLog _flow, CauseLog _cause)
    {
//        if (_goods.getTrait().value() >= EGoodsTrait.YU_ZHI.value())
//        {
            Formatter formatter = new Formatter();
            formatter.append(new Date()).append(
                    _player.getLoginInfo().accountID).append(
                    _player.getUserID()).append(_player.getName()).append(
                    _player.getLoginInfo().loginMsisdn).append(_pet.id)
                    .append(_pet.name).append(_itemNum).append(
                            _player.where().getName()).append(
                            _loction.getName()).append(_flow.getName()).append(
                            _cause.getName());
            getLogger("petChange").info(formatter.flush());
//        }
    }


    /**
     * 信件发送日志
     * 
     * @param _nickname 发件人角色昵称
     * @param _letterID 信件编号
     * @param _receiveNickname 接收方角色昵称
     * @param _letterTitle 信件标题
     * @param _letterContent 信件内容
     */
    public void letterLog (String _nickname, int _letterID,
            String _receiveNickname, String _letterTitle, String _letterContent)
    {
        XmlFormatter formatter = new XmlFormatter("letterLog");
        formatter.append("date",new Date()).append("nickname",_nickname).append("letterID",_letterID)
        		 .append("game_id",GmServiceImpl.gameID)
        		 .append("server_id",GmServiceImpl.serverID)
                .append("receiveNickname",_receiveNickname).append("letterTitle",_letterTitle)
                .append("letterContent",_letterContent);
        getLogger("letter").info(formatter.flush());
    }

    /**
     * 邮件发送日志
     * 
     * @param _accountID 账号编号(GM或者系统发的邮件账号编号为0)
     * @param _userID 角色编号 (GM或者系统发的邮件账号编号为0)
     * @param _nickname 角色昵称(GM时为GM昵称，拍卖行发为'拍卖行')
     * @param _mobile 手机号(GM或者系统发的邮件账号编号为'')
     * @param _mailID 邮件编号
     * @param _receiveUserID 接收方角色编号
     * @param _receiveNickname 接收方角色昵称
     * @param _money 金币
     * @param _point 游戏点数
     * @param _items 邮件附件(物品1ID,物品1名称,物品1数量)
     */
    public void mailLog (int _accountID, int _userID, String _nickname,
            String _mobile, int _mailID, int _receiveUserID,
            String _receiveNickname, int _money, int _point, String _items)
    {
    	XmlFormatter formatter = new XmlFormatter("mailLog");
        formatter.append("date",new Date()).append("accountID",_accountID).append("userID",_userID)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("nickname",_nickname).append("msisdn",_mobile).append("mailID",_mailID)
        		.append("receiverUserID", _receiveUserID).append("receiveNickname",_receiveNickname)
        		.append("money",_money).append("point",_point).append("items",_items);
        getLogger("mail").info(formatter.flush());
    }

    /**
     * 计费通用日志
     * 
     * @param _content
     */
    public void chargeLog (String _content)
    {
        XmlFormatter formatter = new XmlFormatter("charge");
        formatter.append("date",new Date()).append("content",_content);
        getLogger("charge").info(formatter.flush());
    }

    /**
     * 充值发起日志
     *  可能一次充值发起多次请求，因为有可能出现某次请求失败的情况，所以每次请求记录一次
     * @param _accountID 账号编号
     * @param _account 账号名
     * @param _userID 角色编号
     * @param _nickname 角色名
     * @param _msisdn 绑定手机号
     * @param _tranID 流水号
     * @param _fpcode 计费编号fpcode
     * @parsm _forother 是否是给他人充值  1:给其它人充值  2:给自己充值
     * @param _otherNickname 他人昵称
     * @param _otherAccountID 他人账号ID
     * @param _rechargeType 充值类型 神州付或网游
     * @param _syncresult 同步返回的结果，如果是网游充值则是充值结果 0成功  1失败
     * @param _status_code 同步返回的状态码
     * @param _status_desc 状态说明
     */
    public void chargeGenLog (int _accountID, String _account, int _userID,
            String _nickname, String _msisdn, String _tranID,String _fpcode,
            byte _forother,String _otherNickname,int _otherAccountID,
            String _rechargeType,String _syncresult,String _status_code,String _status_desc)

    {
        XmlFormatter formatter = new XmlFormatter("chargeGenLog");
        formatter.append("date",new Date()).append("accountID",_accountID).append("username",_account)
                .append("userID",_userID).append("nickname",_nickname).append("msisdn",_msisdn)
                .append("transID", _tranID).append("fpcode",_fpcode)
                .append("forother",_forother).append("otherNickname",_otherNickname)
                .append("otherAccountID", _otherAccountID)
                .append("rechargeType",_rechargeType)
                .append("syncresult",_syncresult)
                .append("statusCode",_status_code)
                .append("statusDesc",_status_desc);
        getLogger("chargeGen").info(formatter.flush());
    }

    /**
     * 充值卡回调结果日志
     *
     * @param _resultType 同步或异步返回
     * @param _resultCode 回调结果值
     * @param _account 账户名
     * @param _userID 角色编号
     * @param _msisdn 手机号码
     * @param _cardID 充值卡ID
     * @param _price 充值金额
     * @parms _accountID 账号ID
     * @param _rechargetype 充值类型：自己或他人
     * @param _paytype 1:神州付  2：网游
     * @param _paytransid 流水号
     * @param _orderid 订单号
     *
     */
    public void chargeCardCallBackLog (String _resultType,String _resultCode, String _account,
            int _userID, String _msisdn, String _nickname,Timestamp rechargetime,
            int _accountID,int _rechargetype,int _publisher,int _paytype,
            String _paytransid,String _orderid,String memo,int _price)
    {
    	XmlFormatter formatter = new XmlFormatter("chargeCardFeedBack");
        /*formatter.append(new Date()).append(_resultCode).append(_account)
                .append(_userID).append(_msisdn).append(_tranID)
                .append(_cardID).append(_price);*/

        formatter.append("date",new Date())
                    .append("resultType",_resultType)
                    .append("accountName",_account)
                    .append("user_id",_accountID)
                    .append("role_id",_userID)
                    .append("role_name",_nickname)
        			.append("rechargetype",_rechargetype)
        			.append("game_id",GmServiceImpl.gameID)
        			.append("server_id",GmServiceImpl.serverID)
        			.append("rechargetime",rechargetime)
        			.append("logtime",DateTime.getTime(System.currentTimeMillis()))
        			.append("msisdn",_msisdn)
        			.append("publisher",_publisher)
        			.append("paytype",_paytype)
        			.append("result",_resultCode)
        			.append("paytransid",_paytransid)
                    .append("orderid",_orderid)
                    .append("price",_price)
        			.append("remark", memo);
        getLogger("chargeCardFeedBack").info(formatter.flush());
    }

    /**
     * 点数修改日志
     * 
     * @param _tranID 流水号
     * @param _accountID 账号编号
     * @param _account 账号名
     * @param _userID 角色编号
     * @param _nickname 角色名
     * @param _operateType 操作类型 add, setzero
     * @param _point 点数
     * @param _des 描述
     */
    public void pointLog (String _tranID, int _accountID, String _account,
            int _userID, String _nickname, String _operateType, int _point,
            String _des,int _publisher,String _goodslist)
    {
    	XmlFormatter formatter = new XmlFormatter("pointLog");
        formatter.append("date",new Date()).append("paytransid",_tranID).append("user_id",_accountID)
//        		.append( _account)
        		.append("role_id",_userID)
        		.append("game_id",GmServiceImpl.gameID)
        		.append("server_id",GmServiceImpl.serverID)
        		.append("deductiontime",DateTime.getTime(System.currentTimeMillis()))
        		.append("logtime",DateTime.getTime(System.currentTimeMillis()))
        		.append("publisher",_publisher)
//        		.append(_nickname).append(_operateType)
                .append("points",_point)
                .append("goodslist",_goodslist)
                .append("result","成功")
                .append("desc",_des)
                .append("accountName",_account)
                .append("nickname",_nickname)
                .append("operateType",_operateType);
        getLogger("chargePoint").info(formatter.flush());
    }
    
    /**
     * 地图跳转日志
     * @param _accountID
     * @param _accountName
     * @param _userID
     * @param nickname
     * @param mapID
     * @param mapName
     * @param targetMapID
     * @param targetMapName
     */
    public void switchMapLog(int _accountID,String _accountName,int _userID, String nickname,
    		short mapID,String mapName,short targetMapID,String targetMapName,String mapType,String targetMapType){
    	XmlFormatter formatter = new XmlFormatter("switchMap");
    	formatter.append("date", new Date()).append("accountID", _accountID)
    				.append("accountName", _accountName)
    				.append("game_id",GmServiceImpl.gameID)
    				.append("server_id",GmServiceImpl.serverID)
    				.append("roleID", _userID)
    				.append("roleName", nickname)
    				.append("mapID", mapID)
    				.append("mapName", mapName)
    				.append("targetMapID", targetMapID)
    				.append("targetMapName", targetMapName)
                    .append("currType",mapType)
                    .append("targetMapType",targetMapType);
    	getLogger("switchMap").info(formatter.flush());
    }

    /**
     * 物品使用日志
     * @param accountID账号ID
     * @param accountName    账号
     * @param userID   角色ID
     * @param nickname  角色名
     * @param goodsID     使用的物品ID
     * @param goodsName    使用的物品名称
     */
    public void goodsUsedLog(int accountID,String accountName, int userID, String nickname,
                             int goodsID,String goodsName,String trait,String type){
        XmlFormatter formatter =  new XmlFormatter("goodsUsed");
        formatter.append("date", new Date())
                .append("game_id",GmServiceImpl.gameID)
                .append("server_id",GmServiceImpl.serverID)
                .append("accountID",accountID)
                .append("accountName",accountName)
                .append("roleID",userID)
                .append("roleName",nickname)
                .append("goodsID",goodsID)
                .append("goodsName",goodsName)
                .append("trait",trait)
                .append("type",type);
        getLogger("goodsUsed").info(formatter.flush());
    }
    
    /**
     * 任务推广玩家选择日志
     * 
     * step:
     * 0,第1个确认
     * 1,第1个取消
     * 2,第2个确认
     * 3,第2个取消
     * 4,获取计费配置失败.
     * 5,计费成功,下发装备
     * 6,用户点数充足,但是扣点失败.任务计费未成功
     * 7,计费失败,强制下发装备
     * 8,计费失败,且未下发装备
     * 9,下发装备未成功
     * <p>
     * date=日期
     * server_id=serverid
     * accountID=账号ID
     * accountName=账号
     * roleID=userid
     * roleName=用户昵称
     * pushID=任务计费点ID
     * step=步骤
     * option=操作描述
     * price=该推广的价格.(step=5的时候统计一次该价格产生的收入)
     * 
     * @param accountID
     * @param accountName
     * @param userID
     * @param nickname
     * @param pushID
     * @param step
     */
    public void taskPushOption(int accountID,String accountName, int userID, String nickname,
            int pushID, int step, String option, int price) {
		XmlFormatter formatter =  new XmlFormatter("taskPushOption");
		formatter.append("date", new Date())
			.append("game_id", GmServiceImpl.gameID)
			.append("server_id", GmServiceImpl.serverID)
			.append("accountID", accountID)
			.append("accountName", accountName)
			.append("roleID", userID)
			.append("roleName", nickname)
			.append("pushID", pushID)
			.append("step", step)
			.append("option", option)
			.append("price", price);
		getLogger("taskPushOption").info(formatter.flush());
    }

    /**
     * 任务完成日志
     * @param accountID
     * @param accountName
     * @param userID
     * @param nickname
     * @param taskID
     * @param taskName
     */
    public void taskFinished(int accountID,String accountName,int userID,String nickname,
                               int taskID,String taskName){
        XmlFormatter formatter = new XmlFormatter("taskFinished");
        formatter.append("date", new Date())
                .append("game_id",GmServiceImpl.gameID)
                .append("server_id",GmServiceImpl.serverID)
                .append("accountID",accountID)
                .append("accountName",accountName)
                .append("roleID",userID)
                .append("roleName",nickname)
                .append("taskID",taskID)
                .append("taskName",taskName);
        getLogger("taskFinishedLogs").info(formatter.flush());
    }

    /**
     * 怪物掉落日志
     * 问过赵雨天不用记录任务物品   2011-03-16 21:00
     * @param monsterID
     * @param monsterName
     * @param killerNum 杀怪人数
     * @param isGroup 是否是组队打怪
     * @param money   掉落的金钱
     * @param kind 掉落物品种类数量
     * @param goodsIDS   怪物掉落的物品ID数组
     * @param goodsNames 怪物掉落的物品名称数组 ，与物品ID对应
     * @param goodsNums  怪物掉落的物品数量数组，与物品ID对应
     * @param goodsTypes 怪物掉落的物品类型数组 ，与物品ID对应
     */
    public void monsterLegacy(String monsterID,String monsterName,int killerNum,boolean isGroup,int money,int kind,int[] goodsIDS,
                              String[] goodsNames,int[] goodsNums,String[] goodsTypes){
        XmlFormatter formatter = new XmlFormatter("monsterLegacy");
        formatter.append("date", new Date())
                .append("monsterID",monsterID)
                .append("monsterName",monsterName)
                .append("killerNum",killerNum)
                .append("game_id",GmServiceImpl.gameID)
                .append("server_id",GmServiceImpl.serverID)
                .append("isGroup",isGroup?"组队":"单人")
                .append("money",money)
                .append("kind",kind);
        StringBuffer sf = new StringBuffer();
        for(int i=0; i<goodsIDS.length; i++){
            sf.append(goodsIDS[i]).append(",");
        }
        sf.deleteCharAt(sf.length()-1);
        formatter.append("goodsIDS",sf.toString());
        sf = new StringBuffer("");
        for(int i=0; i<goodsNames.length; i++){
            sf.append(goodsNames[i]).append(",");
        }
        sf.deleteCharAt(sf.length()-1);
        formatter.append("goodsNames",sf.toString());
        sf = new StringBuffer("");
        for(int i=0; i<goodsNums.length; i++){
            sf.append(goodsNums[i]).append(",");
        }
        sf.deleteCharAt(sf.length()-1);
        formatter.append("goodsNums",sf.toString());
        sf = new StringBuffer("");
        for(int i=0; i<goodsTypes.length; i++){
            sf.append(goodsTypes[i]).append(",");
        }
        sf.deleteCharAt(sf.length()-1);
        formatter.append("goodsTypes",sf.toString());
        getLogger("monsterLegacy").info(formatter.flush());
    }

    /**
     * 玩家拾取怪物掉落物品日志
     * @param accountID
     * @param accountName
     * @param userID
     * @param nickname
     * @param goodsID
     * @param goodsName
     * @param goodsNum
     * @param trait   物品品质
     */
    public void getMonsterLegacyGoodsLog(int accountID, String accountName,int userID,String nickname,
                                         int goodsID,String goodsName,int goodsNum,String trait,String goodsType){
        XmlFormatter formatter = new XmlFormatter("getMonsterLegacy");
        formatter.append("date", new Date())
                .append("game_id",GmServiceImpl.gameID)
                .append("server_id",GmServiceImpl.serverID)
                .append("accountID",accountID)
                .append("accountName",accountName)
                .append("roleID",userID)
                .append("roleName",nickname)
                .append("goodsID",goodsID)
                .append("goodsName",goodsName)
                .append("goodsNum",goodsNum)
                .append("trait",trait)
                .append("goodsType",goodsType);
        getLogger("getMonsterLegacy").info(formatter.flush());
    }

    /**
     * 网游计费日志即充即扣
     * @param ngUrlID  计费接口ID
     * @param _accountID    账号ID
     * @param _toolsID      道具ID
     * @param mobileUserID  移动userid
     * @param userID         角色ID
     * @param publisher      渠道ID
     * @param serviceType
     * @param price         道具单价
     * @param sumPrice      计费总价
     * @param transID       流水号
     * @param resCode       结果代码
     * @param result        结果
     */
    public void feeLog(String ngUrlID, int _accountID, String _toolsID,String mobileUserID,
                                  int userID,int publisher,ServiceType serviceType,int price,
                                  int sumPrice,String transID,String resCode,String result){
        XmlFormatter formatter = new XmlFormatter("fee");
        formatter.append("date", new Date())
                .append("game_id",GmServiceImpl.gameID)
                .append("server_id",GmServiceImpl.serverID)
                .append("accountID",_accountID)
                .append("roleID",userID)
                .append("publisher",publisher)
                .append("servicetype",serviceType.getName())
                .append("toolsID",_toolsID)
                .append("mobileUserID",mobileUserID)
                .append("ngID",ngUrlID)
                .append("price",price)
                .append("sumPrice",sumPrice)
                .append("transID",transID)
                .append("resCode",resCode)
                .append("result",result);
        getLogger("feeLogs").info(formatter.flush());
    }

    /**
     * 数量值错误日志
     * @param accountID
     * @param accountName
     * @param userID
     * @param nickname
     * @param number
     * @param desc
     */
    public void numberErrorLog(int accountID,String accountName,int userID,String nickname,int number,String desc){
        XmlFormatter formatter = new XmlFormatter("numberError");
        formatter.append("data",new Date())
                .append("game_id",GmServiceImpl.gameID)
                .append("server_id",GmServiceImpl.serverID)
                .append("accountID",accountID)
                .append("accountName",accountName)
                .append("roleID",userID)
                .append("nickname",nickname)
                .append("number",number)
                .append("desc",desc);
        getLogger("numberError").info(formatter.flush());
    }

    /*public void log (String logName, String[] strings)
    {
        Formatter formatter = new Formatter();
        formatter.append(new Date());
        for (String s : strings)
        {
            formatter.append(s);
        }
        getLogger(logName).info(formatter.flush());
    }*/
}
