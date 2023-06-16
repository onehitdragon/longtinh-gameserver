package hero.share.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import hero.charge.service.ChargeServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.item.special.HookExp;
import hero.log.service.ServiceType;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.*;
import hero.share.message.ResponseIndexNoticeList;
import hero.share.message.Warning;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

import hero.player.HeroPlayer;
import hero.share.cd.CDTimeDAO;
import hero.share.cd.CDTimer;
import hero.share.cd.CDTimerTask;
import hero.share.cd.CDUnit;
import hero.share.exchange.ExchangeDict;
import hero.share.letter.LetterService;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ShareServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-4 下午02:41:49
 * @描述 ：
 */

public class ShareServiceImpl extends AbsServiceAdaptor<ShareConfig>
{
	private static Logger log = Logger.getLogger(ShareServiceImpl.class);
    private static ShareServiceImpl instance;

    /**
     * 挂机提示，需要客户端返回的报文号
     */
    private static final short REQUEST_OFFLINE_HOOK_COMMAND = 0x5d22;

    /**
     * 数据库里默认的
     */
    private static final String DEFAULT_LOGOUT_TIME = "1980-01-01 00:00:00";
    /**
     * 离线 1 小时以上可以离线挂机
     */
    private static final int OFFLINE_INTERVATE = 60 * 60 * 1000;
    
    /**
     * 请求交易的玩家列表
     * <发起者USERID,交易另一方USERID>
     */
    private static Map<Integer,Integer> requestExchangePlayerList;

    /**
     * 玩家全职业实力排行列表
     */
    private static List<RankInfo> playerPowerRankAllVocation;
    /**
     * 玩家单个职业实力排行
     */
    private static Map<Byte,List<RankInfo>> playerPowerRankSingleVocation;

    private static List<Inotice> inoticeList;

//    private Timer flushPlayerPowerRankTimer;

    private ShareServiceImpl()
    {
        config = new ShareConfig();
        requestExchangePlayerList = new HashMap<Integer,Integer>();

//        flushPlayerPowerRankTimer = new Timer();
//
//        flushPlayerPowerRankTimer.schedule(new ReflushPowerRankTask(), 12*60*60*1000, 12*60*60*1000);  //每12个小时执行一次
    }

    @Override
    protected void start ()
    {
        LetterService.getInstance();

//        loadPlayerPowerRank();//加载玩家实力排行
        AllPictureDataDict.getInstance();
    }

    @Override
    public void sessionFree (Session _session)
    {
        ExchangeDict.getInstance().playerOutline(_session.nickName);
    }

    public static ShareServiceImpl getInstance ()
    {
        if (null == instance)
        {
            instance = new ShareServiceImpl();
        }

        return instance;
    }

    /**
     * 获取排行榜主菜单项列表
     * @return
     */
    public Map<Byte,RankMenuField> getRankTypeMap(){
        return config.rankTypeMap;
    }

    /**
     * 获取排行榜二、三级菜单项列表
     * @return
     */
    /*public Map<Byte,RankMenuField> getShowMenuTypes(){
        return config.showMenuTypes;
    }*/

    /**
     * 是否要以发起交易请求
     * 当 requestExchangePlayer 里没有该userid时才可以
     * @param userID
     * @return
     */
    public boolean canRequest(int userID){
    	log.debug("can Request exchange = " + requestExchangePlayerList.get(userID));
    	return requestExchangePlayerList.get(userID) == null;
    }
    /**
     * 当有玩家发起交易请求时加到 requestExchangePlayer里
     * @param userID
     */
    public void addRequestExchangePlayer(int userID,int targetUserID){
    	if(requestExchangePlayerList.get(userID) == null){
    		log.debug("requestExchangePlayerList add userid = " + userID);
    		requestExchangePlayerList.put(userID, targetUserID);
    	}
    }
    /**
     * 根据发起者USERID从requestExchangePlayerList里移除玩家
     * 当对方没有同意或交易取消时
     * @param userID
     */
    public void removePlayerFromRequestExchangeList(int userID){
    	for(Iterator<Integer> it = requestExchangePlayerList.keySet().iterator();it.hasNext();){
    		if(it.next() == userID){
    			log.debug("removePlayerFromRequestExchangeList userid= " + userID);
    			it.remove();
    			return;
    		}
    	}
    }
    
    /**
     * 根据交易对方USERID从requestExchangePlayerList里移除玩家
     * 有可能对方没确认或拒绝就下线或非正常退出
     * @param targetUserID
     */
    public void removePlayerFromRequestExchangeListByTarget(int targetUserID){
    	for(Iterator<Integer> it = requestExchangePlayerList.values().iterator();it.hasNext();){
    		if(it.next() == targetUserID){
    			log.debug("removePlayerFromRequestExchangeListByTarget targetUserID= " + targetUserID);
    			it.remove();
    			return;
    		}
    	}
    }
    
    public final Connection getResourceConnection ()
    {
        String dbname = config.getResourceDBname();
        Connection conn;
        try
        {
        	String dbName = config.getResourceDBname();
        	String dbUrl = "jdbc:mysql://"+config.getResourceDBurl()+"/"+dbName
                + "?connectTimeout=0&autoReconnect=true&failOverReadOnly=false";
        	String dbUser = config.getResourceDBusername();
        	String dbPassword = config.getResourceDBpassword();
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }
        catch (Exception ex)
        {
            conn = null;
            ex.printStackTrace();
            LogWriter.error("GmServiceImpl.getConnection error!", ex);
        }

        return conn;
    }
  

    /**
     * 保存CD
     */
    public void saveCD (HeroPlayer _player)
    {
        ArrayList<CDUnit> list = new ArrayList<CDUnit>();
        Set<Integer> set = _player.userCDMap.keySet();
        Iterator<Integer> iter = set.iterator();
        CDUnit cd = null;

        while (iter.hasNext())
        {
            cd = _player.userCDMap.get(iter.next());
            if(cd.getTimeBySec() > 10) //只有CD时间大于5分钟的才入库
            {
        	list.add(cd);
            }
            cd.stop();
            iter.remove();
        }

        _player.userCDMap.clear();

        if (list.size() > 0)
        {
            CDTimeDAO.insertCD(_player.getUserID(), list);
        }
    }

    public static String DateTimeToString (Date _date)
    {
        String dateStr = "";
        Date date = _date;
        try
        {
            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateStr = DF.format(date);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
        }
        return dateStr;
    }

    public static String DateToString (Date _date)
    {
        String dateStr = "";
        Date date = _date;
        try
        {
            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = DF.format(date);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 显示公告/活动列表
     * @param player
     */
    public void showIndexNoticeList(HeroPlayer player){
        inoticeList = getInoticeList(0);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseIndexNoticeList(inoticeList));
    }

    /**
     * 获取公告/活动列表
     * @param type
     * @return
     */
    public List<Inotice> getInoticeList(int type){
        return ShareDAO.getInoticeList(type);
    }

    /**
     * 16进制字符串转换成int型值
     * @param hex   16进制字符串  eg:0x8d5a2d
     * @return
     */
    public int hexStr2Int(String hex){
        int res = 0;
        if(hex != null && hex.trim().length()>0){
            String hxs = "0123456789abcdef";

            hex = hex.substring(hex.indexOf("0x")+2);

            String[] ses = hex.toLowerCase().split("");

            int legth = ses.length-1;

            for (int i=legth; i>=0; i--){
                if(ses[i].trim().length()>0){ //ses[0]=""
//                  System.out.println(ses[i]+"--"+hxs.indexOf(ses[i])+"--"+(legth-i));
                    res += hxs.indexOf(ses[i]) * Math.pow(16,legth-i);
                }
            }
        }
        return res;
    }

    /**
     * 离线挂机
     */
    public void offLineHook(HeroPlayer player){
        if(player.getLevel() < PlayerServiceImpl.getInstance().getConfig().max_level){
            long defaultLastLogoutTime = Timestamp.valueOf(DEFAULT_LOGOUT_TIME).getTime();
            if(player.lastLogoutTime > defaultLastLogoutTime){
                long offLineTime = System.currentTimeMillis() - player.lastLogoutTime;
                int offLineHours = (int)offLineTime/OFFLINE_INTERVATE;
                if(offLineHours >= config.hookHours){//离线一小时以上可以离线挂机
                    int offLineDays = offLineHours/24;
                    int hookHours = offLineHours;
                    if(offLineDays >= 1){
                        hookHours = offLineDays * 8;//每天累计8小时，超出不计时
                        if(hookHours > 24){
                            hookHours = 24; //最多累计 24 小时
                        }
                    }else {
                        if(hookHours > 8){
                            hookHours = 8; //每天累计8小时，超出不计时
                        }
                    }

                    player.currHookHours = hookHours;
                    log.debug("player["+player.getName()+"] hook hours="+hookHours);
                    warnBuyHookExp(player);
                }
            }
        }
    }

    public void warnBuyHookExp(HeroPlayer player){
        long offLineTime = System.currentTimeMillis() - player.lastLogoutTime;
        int offLineHours = (int)offLineTime/OFFLINE_INTERVATE;

        if(offLineHours > 8){//离线8小时后再弹出框
            if(player.currHookHours> 0){
                int exp = calHookExp(player.currHookHours,player.getLevel());
                int point = calHookExpPoint(exp,player.getLevel());
                String warnStr = "离线经验奖励: \n" +
                                "尊敬的玩家，您已很久没有登陆游戏了，为了不使您与您的队友落下巨大的等级差距，" +
                                "我们特意为您累积了"+player.currHookHours+"小时的离线经验，合计"+exp+"点经验，您愿意花费"+point+"点数购买这些经验吗？";

                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(warnStr,
                        Warning.UI_TOOLTIP_CONFIM_CANCEL_TIP,REQUEST_OFFLINE_HOOK_COMMAND));
            }else {
                player.buyHookExp = false;
            }
        }
    }

    public void startBuyHookExp(HeroPlayer player){
        int exp = calHookExp(player.currHookHours,player.getLevel());
        int point = calHookExpPoint(exp,player.getLevel());

        if(player.getChargeInfo().pointAmount >= point){
            HookExp hookExp = (HookExp) GoodsContents.getGoods(config.hookExpGoodsID);
            log.debug("HookExp goods = " + hookExp);
            boolean buyRes = ChargeServiceImpl.getInstance().reducePoint(
            		player,point,hookExp.getID(),hookExp.getName(),1, ServiceType.OFFLINE_HOOK_EXP);
            log.debug("buy hook exp res = " +buyRes);
            if(buyRes) {
                player.buyHookExp = false;
                player.currHookHours = 0;
//                OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("恭喜获得"+exp+"点经验",Warning.UI_TOOLTIP_TIP));
                PlayerServiceImpl.getInstance().addExperience(player,exp,1,
                                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING);
            }
        }else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            		new Warning("你点数不足，确定先充值再购买这些离线经验吗？",
            				Warning.UI_TOOLTIP_AND_EVENT_TIP, Warning.SUBFUNCTION_UI_CHARGEUP));
        }
    }

    /**
     * 计算离线挂机经验
     * 挂机经验=EXP_base*挂机时间(小时)*60
     *  EXP_base = [ 3 + 6 *（ LV — 1）]
     *  lv : 挂机人物等级
     *  exp_base : 标准经验
     * @param hours
     * @param level 玩家等级
     * @return
     */
    private int calHookExp(int hours,int level){
        int expBase = 3 + 6 * (level - 1);
        return expBase * hours * 60;
    }

    /**
     * 计算挂机经验所需点数
     * 挂机经验收费标准:50000经验=150点
     * 商城点数 1点= 300+(level+3)/30*300 经验
     * @param exp 挂机经验
     * @param level 玩家等级
     * @return
     */
    private int calHookExpPoint(int exp,int level){
        int per_point_exp = 300+(level+3)/30*300;
        int point = exp/per_point_exp;
        if(point < 1){
            point = 1;
        }
        return point;
    }

    /**
     *
     * @param type  1：杀敌  2：等级  3：财富  4：实力   5：爱情
     * @param vocation1
     * @param vocation2
     * @param moreVocations 是否查看两个职业的数据，如果是，则 vocation1、vocation2都必须>0,否则 vocation2=0 ,如果是全职业=false且vocation1=0
     * @return
     */
    public List<RankInfo> getRankInfoList(byte type,int vocation1,int vocation2,boolean moreVocations){
        List<RankInfo> rankInfoList = null;
        switch (type){
            case 1:
            {
                rankInfoList = ShareDAO.getKillerRankInfoList(vocation1,vocation2,moreVocations);
                break;
            }
            case 2:
            {
                rankInfoList = ShareDAO.getLevelRankInfoList(vocation1,vocation2,moreVocations);
                break;
            }
            case 3:
            {
                rankInfoList = ShareDAO.getMoneyRankInfoList(vocation1,vocation2,moreVocations);
                break;
            }
            case 4:
            {
               /* if(vocation == 0){
                    rankInfoList = getPlayerPowerRankAllVocation();
                }else {
                    rankInfoList = getPlayerPowerRankSingleVocation((byte)vocation);
                }
                int i=1;
                for (RankInfo ri : rankInfoList){
                    ri.rank = i++;  //重新设置排行名次，不能在排序完后就设置名次，会乱的
                }*/
                rankInfoList = ShareDAO.getPowerRankList(vocation1,vocation2,moreVocations);
                break;
            }
            case 5:
            {
                rankInfoList = ShareDAO.getLoverValueRankInfoList(vocation1,vocation2,moreVocations);
                break;
            }
            case 6:
            {
                rankInfoList = ShareDAO.getGuildRankList();
            }
        }
        return rankInfoList;
    }

    /**
     * 加载实力排行的玩家列表
     */
    /*public void loadPlayerPowerRank(){
        //在这里初始化，把之前的数据清除
        playerPowerRankSingleVocation = new HashMap<Byte,List<RankInfo>>();

        playerPowerRankAllVocation = ShareDAO.getPlayerInfoList(0);
        log.debug("加载全职业实力排行玩家 size="+playerPowerRankAllVocation.size());
        HeroPlayer player;
        List<RankInfo> singleVocationPowerRankList;//单独的职业的玩家列表

        int strength,agility,stamina,inte,spirit,lucky;
        int physicsAttack;
        float magicHarmValue;

        int powerValue;

        for (RankInfo ri : playerPowerRankAllVocation){
            player = PlayerServiceImpl.getInstance().load(ri.userID);
            if(player != null){
                PlayerServiceImpl.getInstance().initProperty(player);

                strength = player.getActualProperty().getStrength();
                agility = player.getActualProperty().getAgility();
                stamina = player.getActualProperty().getStamina();
                inte = player.getActualProperty().getInte();
                spirit = player.getActualProperty().getSpirit();
                lucky = player.getActualProperty().getLucky();

                physicsAttack = player.getActualProperty().getMaxPhysicsAttack();
                magicHarmValue = player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.UMBRA)
                                + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY)
                                + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.FIRE)
                                + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.WATER)
                                + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SOIL);

                powerValue = calPowerValue(ri.vocation,strength,agility,stamina,inte,spirit,lucky,physicsAttack,magicHarmValue);

                ri.value = powerValue;

                singleVocationPowerRankList = playerPowerRankSingleVocation.get(ri.vocation);

                RankInfo ri_x = ri;
                if(singleVocationPowerRankList == null){
                    singleVocationPowerRankList = new ArrayList<RankInfo>();
                    singleVocationPowerRankList.add(ri_x);
                    playerPowerRankSingleVocation.put(ri_x.vocation,singleVocationPowerRankList);
                }else {
                    singleVocationPowerRankList.add(ri_x);
                }
            }
        }

        // 排序
        Collections.sort(playerPowerRankAllVocation,new RankInfoComparator());

        //为每个职业的排行列表排序
        for(List<RankInfo> list : playerPowerRankSingleVocation.values()){
            log.debug("单独职业实力排行玩家 size="+list.size());
            Collections.sort(list,new RankInfoComparator());
        }
    }
*/
    /**
     * 获取全职业实力排行
     * @return
     */
    /*private List<RankInfo> getPlayerPowerRankAllVocation(){
        int size = playerPowerRankAllVocation.size();
        if(size > 20){
            playerPowerRankAllVocation.subList(0,20);
        }
        return playerPowerRankAllVocation;
    }*/

    /**
     * 获取单个职业实力排行
     * @param vocation
     * @return
     */
    /*private List<RankInfo> getPlayerPowerRankSingleVocation(byte vocation){
        int size = playerPowerRankSingleVocation.get(vocation).size();
        if(size > 20){
            return playerPowerRankSingleVocation.get(vocation).subList(0,20);
        }
        return playerPowerRankSingleVocation.get(vocation);
    }*/

    /**
     * 计算玩家实力值
     * @param player
     * @return
     */
    public int calPlayerPower(HeroPlayer player){
        int strength,agility,stamina,inte,spirit,lucky;
        int physicsAttack;
        float magicHarmValue;

        strength = player.getActualProperty().getStrength();
        agility = player.getActualProperty().getAgility();
        stamina = player.getActualProperty().getStamina();
        inte = player.getActualProperty().getInte();
        spirit = player.getActualProperty().getSpirit();
        lucky = player.getActualProperty().getLucky();

        physicsAttack = player.getActualProperty().getMaxPhysicsAttack();
        magicHarmValue = player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.UMBRA)
                        + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY)
                        + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.FIRE)
                        + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.WATER)
                        + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SOIL);

        return calPowerValue(player.getVocation().value(), strength, agility, stamina, inte, spirit, lucky, physicsAttack, magicHarmValue);
    }

    /**
     * 计算各职业实力值
     * 公式如下：
     * 力士及转职职业  力量*0.5+敏捷*0.3+智力*0.1 +精神*0.1+耐力*1+幸运
     * 斥候及转职职业  力量*0.3+敏捷*0.5+智力*0.1 +精神*0.1+耐力*1+幸运
     * 法师及转职职业  力量*0.1+敏捷*0.1+智力*0.5 +精神*0.3+耐力*1+幸运
     * 巫医及转职职业  力量*0.1+敏捷*0.1+智力*0.3 +精神*0.5+耐力*1+幸运
     * @param vocation
     * @param strength
     * @param agility
     * @param stamina
     * @param inte
     * @param spirit
     * @param lucky
     * @param physicsAttack
     * @param magicHarmValue
     * @return
     */
    private int calPowerValue(byte vocation,int strength,int agility,int stamina,int inte,int spirit,int lucky,
                              int physicsAttack,float magicHarmValue){
        int power = 0;
        if(vocation == EVocation.LI_SHI.value() || vocation == EVocation.JIN_GANG_LI_SHI.value()
                || vocation == EVocation.LUO_CHA_LI_SHI.value() || vocation == EVocation.QING_TIAN_LI_SHI.value()
                || vocation == EVocation.XIU_LUO_LI_SHI.value()){
            power = (int)(strength*0.5 + agility*0.3 + inte*0.1 + spirit*0.1 + stamina*1 + lucky);
        }
        if(vocation == EVocation.FA_SHI.value() || vocation == EVocation.TIAN_JI_FA_SHI.value()
                || vocation == EVocation.XUAN_MING_FA_SHI.value() || vocation == EVocation.YAN_MO_FA_SHI.value()
                || vocation == EVocation.YU_HUO_FA_SHI.value()){
            power = (int)(strength*0.1 + agility*0.1 + inte*0.5 + spirit*0.3 + stamina*1 + lucky);
        }
        if(vocation == EVocation.CHI_HOU.value() || vocation == EVocation.GUI_YI_CHI_HOU.value()
                || vocation == EVocation.LI_JIAN_CHI_HOU.value() || vocation == EVocation.SHEN_JIAN_CHI_HOU.value()
                || vocation == EVocation.XIE_REN_CHI_HOU.value()){
            power = (int)(strength*0.3 + agility*0.5 + inte*0.1 + spirit*0.1 + stamina*1 + lucky);
        }
        if(vocation == EVocation.WU_YI.value() || vocation == EVocation.LING_QUAN_WU_YI.value()
                || vocation == EVocation.MIAO_SHOU_WU_YI.value() || vocation == EVocation.XIE_JI_WU_YI.value()
                || vocation == EVocation.YIN_YANG_WU_YI.value()){
            power = (int)(strength*0.1 + agility*0.1 + inte*0.3 + spirit*0.5 + stamina*1 + lucky);
        }
        return power;
    }

    /**
     * 更新玩家实力排行榜任务
     * 每天更新两次
     */
   /* class ReflushPowerRankTask extends TimerTask{
        @Override
        public void run() {
            log.debug("重新加载玩家实力排行榜...");
            loadPlayerPowerRank();
        }
    }*/

    /*class RankInfoComparator implements Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            RankInfo ri1 = (RankInfo)o1;
            RankInfo ri2 = (RankInfo)o2;
            if(ri1.value > ri2.value){
                return -1;
            }else if(ri1.value < ri2.value){
                return 1;
            }
            return 0;
        }
    }*/
}



