package hero.gm.service;

import hero.charge.ChargeInfo;
import hero.charge.FPType;
import hero.charge.FeePointInfo;
import hero.charge.FeeType;
import hero.charge.service.ChargeDAO;
import hero.charge.service.ChargeServiceImpl;
import hero.chat.service.ChatServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.gm.EResponseType;
import hero.gm.ResponseToGmTool;
import hero.gm.message.GmQuestionSubmitFeedback;
import hero.log.service.LogServiceImpl;
import hero.log.service.ServiceType;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.share.message.MailStatusChanges;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.ShareServiceImpl;
import hero.share.service.Tip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import hero.task.service.TaskServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;


public class GmServiceImpl extends AbsServiceAdaptor<GmServiceConfig>
{
	private static Logger log = Logger.getLogger(GmServiceImpl.class);
    /**
     * 默认时间-作为黑名单是否为永久标识
     */
    public static final String            TIME_FOREVER       = "1980-01-01 00:00:00";

    /**
     * 单例
     */
    private static GmServiceImpl          instance           = null;
    
    public static int serverID = 1;
    
    public static int gameID = 1;
    
    public static String addChatContentURL="";

    /**
     * 消息队列
     */
    private static List<ResponseToGmTool> responseGmToolList = null;
    
    

    private GmServiceImpl()
    {
        config = new GmServiceConfig();

        responseGmToolList = new ArrayList<ResponseToGmTool>();

    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static GmServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new GmServiceImpl();
        }
        return instance;
    }

    @Override
    protected void start ()
    {
        try
        {

        	log.info("### GmServiceImpl start..");
//        	inItConnection();
            // 加载黑名单列表
            GmBlackListManager.getInstance().init();
            /**
             * 加载GM公告
             */
            ChatServiceImpl.getInstance().loadGmNotice();
            /**
             * 加载服务器分区ID
             */
            serverID = config.getServerID();
            gameID = config.getGameID();
//            log.debug("GM service start serverID = " + serverID);
            addChatContentURL = config.getAddChatContentURL();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void createSession (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (player != null)
        {
            Date date = new Date(player.loginTime);
            String loginTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .format(date);

//            Role role = new Role(player.getUserID(), player.getName(), player
//                    .getClan().getDesc(), player.getVocation().getDesc(),
//                    (int) player.getLevel(), loginTime);

            /**
             * 推送上线消息，插入队列
             */
            ResponseToGmTool rtgt = new ResponseToGmTool(
                    EResponseType.SEND_ROLE_ONLINE, 0);
//            rtgt.setRoleOnline(role);
            addGmToolMsg(rtgt);
        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (player != null)
        {
            /**
             * 推送下线消息，插入队列
             */
            ResponseToGmTool rtgt = new ResponseToGmTool(
                    EResponseType.SEND_ROLE_OUTLINE, 0);
            rtgt.setRoleOutline(player.getName());
            addGmToolMsg(rtgt);
        }
    }


    /**
     * 根据黑名单endTime获取其对应的字符串
     * 
     * @param _endTime
     * @return
     */
    public static String getBlackEndTimeStr (Timestamp _endTime)
    {
        String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .format(_endTime);
        if (time.equals(TIME_FOREVER))
            time = "永久";

        return time;
    }

    /**
     * 格式化输出对应的时间戳
     * 
     * @param _ts
     * @return
     */
    public static String getTimestampStr (Timestamp _ts)
    {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(_ts);
    }

    /**
     * 获取数据库连接
     * 
     * @return
     */
    public final Connection getConnection ()
    {
        String dbname = config.getAccountDBname();
        Connection conn;
        try
        {
        	String dbName = config.getAccountDBname();
        	log.debug("GM DB dbName = " + dbName +", dbUrl = " + config.getAccountDBurl());
        	String dbUrl = "jdbc:mysql://"+config.getAccountDBurl()+"/"+dbName
                + "?connectTimeout=0&autoReconnect=true&failOverReadOnly=false";
        	log.debug("dbUrl = " + dbUrl);
        	String dbUser = config.getAccountDBusername();
        	String dbPassword = config.getAccountDBpassword();
            conn = DriverManager
                    .getConnection(dbUrl, dbUser, dbPassword);
        }
        catch (Exception ex)
        {
            conn = null;
            ex.printStackTrace();
            LogWriter.error("GmServiceImpl.getConnection error!", ex);
        }

        return conn;
    }
    
    /*public Connection getConnection(){
    	return conn;
    }*/
    
    /**
     * 
     * 玩家发邮件给GM
     * 添加 GM 邮件到数据库
     * @param userID
     * @param content
     * @param type
     */
    public static boolean addGMLetter(int userID,String content,byte type){
    	log.debug("player send to GM letter : " + userID+",["+content+"]");
    	return GmDAO.sendGMLetter(userID, content, type);
    	
    }
    
    /**
     * GM 给玩家回复的邮件
     * @param gmLetterID
     */
    public static void GMReplyLetter(int gmLetterID){
    	int letterID = LetterService.getInstance().getUseableLetterID();
    	
    	Letter letter = new Letter();
    	letter.letterID = letterID;
    	
    	GmDAO.getLetterInfo(gmLetterID, letter);
    	log.debug("GM 给玩家回复的邮件: 玩家 uerid = "+ letter.receiverUserID);
    	HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(letter.receiverUserID);
    	if(player == null){
    		player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(letter.receiverUserID);
    	}
    	log.debug("GM 给玩家回复的邮件: 玩家 player = " + player);
    	letter.receiverName = player.getName();
    	letter.title = "系统 GM 回复";
        letter.type = Letter.SYSTEM_TYPE;
    	
    	LetterService.getInstance().addNewLetter(letter);

		if(player != null && player.isEnable()){
    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_NPC_MAIL_GET_NEW));

            ResponseMessageQueue.getInstance().put(
            		player.getMsgQueueIndex(),
                    new MailStatusChanges(
                            MailStatusChanges.TYPE_OF_POST_BOX, true));

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new GmQuestionSubmitFeedback((byte)0));
		}

    }

    /**
     * 神州付充值回调
     * @param userID  当前充值的角色ID，如果是给其它人充值，则是其它人角色ID
     * @param result  1:支付成功   0:支付失败   如果是多次请求的，计费服务器应该在所有请求都完成后返回成功或失败
     * @param orderID
     * @param point 充值的点数
     * @return  1:成功   0：失败
     */
    public static int szfFeeCallBack(int userID,String transID,byte payresult,String orderID,int point){
        log.info("神州付回调.....");
        String result = payresult==1?"充值成功":"充值失败";
        int res = 1;
        HeroPlayer receiver = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
        log.debug("receiver="+receiver);
        if(receiver == null){
            receiver = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(userID);
            PlayerDAO.loadPlayerAccountInfo(receiver);
        }
        if(receiver != null){

            log.debug("神州付回调 result="+payresult+", orderid="+orderID+",transid="+transID+",point="+point);

//            boolean addpointresult = ChargeServiceImpl.getInstance().addPoint(receiver,transID,point,info.rechargetype,receiver.getLoginInfo().publisher, ServiceType.CHARGE);

            Letter letter = new Letter(Letter.SYSTEM_TYPE,LetterService.getInstance()
                            .getUseableLetterID(), Tip.TIP_SZF_CHARGE_BACK, Tip.TIP_MICRO_OF_SYSTEM, userID,
                            receiver.getName(), "订单号："+orderID+" ,充值结果："+result+",本次充值点数："+point);
            LetterService.getInstance().addNewLetter(letter);

            if(receiver.isEnable()){


                ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_MAIL_GET_NEW, Warning.UI_STRING_TIP));

                ResponseMessageQueue.getInstance().put(
                                    receiver.getMsgQueueIndex(),
                                    new MailStatusChanges(
                                            MailStatusChanges.TYPE_OF_LETTER,
                                            true));

            }

            res = ChargeDAO.updateChargeSZF(payresult,new Timestamp(System.currentTimeMillis()),transID,orderID);

            // 记录日志，要查询充值信息
            ChargeInfo info = ChargeDAO.getChargeInfo(transID,orderID);
            String memo = "";
            if(info.rechargetype == 2){
                log.debug("神州付回调，给其他玩家充点数，发提示信息。。。");
//                HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByUserID(info.other_user_id);

                log.debug("upd before other ponit="+receiver.getChargeInfo().pointAmount);

                ChargeServiceImpl.getInstance().updatePointAmount(receiver,point);

                log.debug("upd after other point ="+receiver.getChargeInfo().pointAmount);

                HeroPlayer _player = PlayerServiceImpl.getInstance().getPlayerByUserID(info.userID);
                if(_player == null){
                   _player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(info.userID);
                }

                if(_player != null){
                    Letter letter_x = new Letter(Letter.SYSTEM_TYPE,LetterService.getInstance()
                                .getUseableLetterID(), Tip.TIP_SZF_CHARGE_BACK, Tip.TIP_MICRO_OF_SYSTEM, info.userID,
                                _player.getName(), "订单号："+orderID+" ,充值结果："+result+",本次给\""+receiver.getName()+"\"充值点数："+point);
                    LetterService.getInstance().addNewLetter(letter_x);

                    if(_player.isEnable()){
                        String desc = "充值失败";
                        if(payresult == 1){
                            desc = "充值成功，给\""+receiver.getName()+"\"加 "+point+" 点";
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning(desc,Warning.UI_TOOLTIP_TIP));

                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                                    new Warning(Tip.TIP_NPC_MAIL_GET_NEW, Warning.UI_STRING_TIP));

                        ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new MailStatusChanges(
                                                    MailStatusChanges.TYPE_OF_LETTER,
                                                    true));
                    }

                }


                memo = "给其它人充值:账号id["+info.other_account_id+"],角色id["+info.other_user_id+"]";
            }else{

                log.debug("神州付回调，修改玩家点数，发提示信息。。。");
                log.debug("upd before player ponit="+receiver.getChargeInfo().pointAmount);

                ChargeServiceImpl.getInstance().updatePointAmount(receiver,point);

                log.debug("upd after palyer point ="+receiver.getChargeInfo().pointAmount);
            }

            LogServiceImpl.getInstance().chargeCardCallBackLog("异步",result,receiver.getLoginInfo().username,userID,receiver.getLoginInfo().loginMsisdn,
                    receiver.getName(),info.rechargetime,receiver.getLoginInfo().accountID,info.rechargetype,receiver.getLoginInfo().publisher,
                    info.paytype,transID,orderID,memo,info.price);

            if(receiver.isEnable() && receiver.buyHookExp){//如果是购买离线经验时充值，则继续购买离线流程
                ShareServiceImpl.getInstance().warnBuyHookExp(receiver);
            }

            if(payresult == 1){//如果充值成功，处理充值赠送的点数和活动期间赠送的点数
                FeePointInfo fpi = ChargeServiceImpl.getInstance().getFpInfoByFpcodeAndPrice(info.fpcode,info.price, FPType.CHARGE);
                if(fpi != null && fpi.presentPoint > 0){
                    ChargeServiceImpl.getInstance().addPoint(receiver,transID,fpi.presentPoint,(byte)3,receiver.getLoginInfo().publisher,ServiceType.PRESENT);
                }
                int presentPoint = GmDAO.getPresentPoint(info.price);
                if(presentPoint>0){
                    ChargeServiceImpl.getInstance().addPoint(receiver,transID,presentPoint,(byte)3,receiver.getLoginInfo().publisher,ServiceType.ACTIVE_PRESENT);
                }
            }
        }else{
            res = 0;
        }
        log.info("神州付回调...end .. res="+res);
        return res;
    }

    /**
     * 短信异步结果回调
     * @param transID  流水号
     * @param result 结果
     * @return
     */
    public static void smsCallBack(String transID,String result){
        boolean res = true;
        TaskServiceImpl.getInstance().asynTaskPushItem(transID,res);
    }

    /**
     * 瞬移玩家
     * @param mapID
     * @param userID
     * @return 0:成功  1:玩家不在线  2:地图ID错误
     */
    public static int gmBlinkPlayer(short mapID,int userID){
    	int res = -1;
    	HeroPlayer _player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
    	if(_player == null || !_player.isEnable()){
//    		return Tip.TIP_DUEL_OF_INVALIDATE_TARGET;
    		res = 1;
    	}
    	Map entranceMap = MapServiceImpl.getInstance().getNormalMapByID(mapID);
    	if(entranceMap == null){
//    		return Tip.TIP_MAP_OF_NONE_EXISTS;
    		res =2;
    	}
        _player.setCellX(entranceMap.getBornX());
        _player.setCellY(entranceMap.getBornY());

        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new ResponseMapBottomData(_player, entranceMap,
                        _player.where()));

        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new ResponseMapGameObjectList(_player
                        .getLoginInfo().clientType, entranceMap));

        _player.gotoMap(entranceMap);
        //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
        EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);
        res = 0;
        return res;
    }

    /**
     * 通过用户名获取账户信息
     * 
     * @param _username
     * @return
     */
    public static String getAccountInfoByUserName (String _username)
    {
        return GmDAO.getAccountInfo(_username);
    }

    /**
     * 通过角色名获取账户信息
     * 
     * @param _rolename
     * @return
     */
    public static String getAccountInfoByRoleName (String _rolename)
    {
        String username = GmDAO.getAccountUserNameByRolename(_rolename);
        return getAccountInfoByUserName(username);
    }

    /**
     * 获取指定账户下的所有角色信息
     * 
     * @param _account_id
     * @return
     */
    public static String[] getRoleInfos (int _account_id)
    {
        return GmDAO.getRoleInfos(_account_id);
    }

    /**
     * 加入发送GM工具的消息
     * 
     * @param _msg
     */
    public static void addGmToolMsg (ResponseToGmTool _response)
    {
        responseGmToolList.add(_response);
    }


}
