package hero.chat.service;

import hero.chat.message.GetGoodsNofity;
//import hero.gm.EBlackType;
//import hero.gm.service.GmBlackListManager;
import hero.gm.service.GmDAO;
import hero.gm.service.GmServiceImpl;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.guild.Guild;
import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;

/**
 * 聊天服务
 * 
 * @author Luke 陈路
 * @date Jul 28, 2009
 */
public class ChatServiceImpl extends AbsServiceAdaptor<MsgQConfig>
{
     private static Logger log = Logger.getLogger(ChatServiceImpl.class);
    /**
     * 玩家私人聊天
     */
    public static final byte       PLAYER_SINGLE       = 0;

    /**
     * 玩家世界聊天
     */
    public static final byte       PLAYER_WORLD        = 1;

    /**
     * 地图
     */
    public static final byte       PLAYER_MAP          = 2;

    /**
     * 队伍聊天
     */
    public static final byte       PLAYER_GROUP        = 3;

    /**
     * 工会聊天
     */
    public static final byte       PLAYER_GUILD        = 4;

    /**
     * 系统，上标签提示
     */
    public static final byte       TOP_SYSTEM_WORLD    = 5;

    /**
     * 系统地图
     */
    public static final byte       BOTTOM_SYSTEM_MAP   = 7;

    /**
     * 队伍系统消息
     */
    public static final byte       BOTTOM_SYSTEM_GROUP = 8;

    /**
     * 工会系统消息
     */
    public static final byte       BOTTOM_SYSTEM_GUILD = 9;

    /**
     * 物品提示
     */
    public static final byte       GOODS_SYSTEM_GROUP  = 20;

    /**
     * 阵营聊天
     */
    public static final byte       CLAN                = 10;
    
    /**
     * 对单个玩家的系统，上标签提示
     */
    public static final byte       TOP_SYSTEM_SINGLE    = 12;

    /**
     * GM回复的聊天内容
     */
    //public static final byte       GM_REPLY            = 11;

    /**
     * 上标签提示内容
     */
    private String[]               tip                 = null;

    /**
     * 上标签提示互斥锁
     */
    private Object                 tipMutex            = new Object();

    /**
     * 随即数
     */
    private static final Random    random              = new Random();

    /**
     * 循环执行上标签提示内容
     */
    private Timer                  timer               = new Timer();
    
    /**
     * 检查加载公告
     */
    private Timer	loadNoticeTimer	= new Timer();
    
    private Timer 	sendNoticeTimer = new Timer();

    /**
     * 单例
     */
    private static ChatServiceImpl instance            = null;
    
    /**
     * 公告列表
     */
    private Map<Integer,GmNotice> gmNoticeMap;
    
    /**
     * 定时发公告定时器列表
     */
    private Map<Integer,Timer> sendNoticeTimerMap = null;

    private Map<Integer,TimerTask> sendNoticeMap = null;

    /**
     * 获取单例
     * 
     * @return
     */
    public static ChatServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new ChatServiceImpl();
        }
        return instance;
    }

    private ChatServiceImpl()
    {
        try
        {
            config = new MsgQConfig();
            gmNoticeMap = new HashMap<Integer,GmNotice>();
            sendNoticeTimerMap = new HashMap<Integer,Timer>();
            sendNoticeMap = new HashMap<Integer,TimerTask>();
            timer.schedule(new SystemTipTask(), 60000, 30000);
            loadNoticeTimer.schedule(new GmNoticeLoadTask(), 120200,301000);
            sendNoticeTimer.schedule(new CheckSendNoticeTask(), 90000, 300000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    

    /**
     * 是否被禁言
     * 
     * @param _speaker
     * @return
     */
    private boolean isChatBlack (HeroPlayer _speaker)
    {
        // 禁言
//        String endTime = GmBlackListManager.getInstance().getBlackEndTime(
//                EBlackType.ROLE_CHAT, _speaker.getName());
//
//        if (!endTime.isEmpty())
//        {
//            String warnStr = null;
//            if (endTime.equals("永久"))
//            {
//                warnStr = "您已被永久禁言";
//            }
//            else
//            {
//                String min = ""
//                        + (Timestamp.valueOf(endTime + ".0").getTime() - System
//                                .currentTimeMillis()) / (60 * 1000);
//
//                warnStr = "禁言结束：" + min + "分";
//            }
//
//            OutMsgQ.getInstance().put(_speaker.getMsgQueueIndex(),
//                    new Warning(warnStr));
//            return true;
//        }

        return PlayerServiceImpl.getInstance().playerChatIsBlank(_speaker.getLoginInfo().accountID,_speaker.getUserID());
    }
    
    public void toGMaddChatContent(String speakerName,String targetName,String content){
    	String addUrl = GmServiceImpl.addChatContentURL;
    	content = "["+speakerName+"]对["+targetName+"]说："+content;
//log.info("send to gm chat : " + content);
    	try{
    		URL url = new URL(addUrl);
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		conn.setDoOutput(true);
    		conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "UTF-8");
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write("serverID="+GmServiceImpl.serverID+"&content="+content);
			writer.flush();
			
			conn.getResponseCode();
			
			conn.disconnect();
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		return;
    	}
    }

    /**
     * 私聊
     * 
     * @param _srcName 发送者名称
     * @param _destName 接受者名称
     * @param _target 接受者
     * @param _content 内容
     */
    public void sendSinglePlayer (HeroPlayer _speaker, String _destName,
            HeroPlayer _target, String _content, boolean _toGm)
    {
        if (_toGm)
        {
            ChatQueue.getInstance().add(PLAYER_SINGLE, _speaker, _destName,
                    _target, _content);
        }
        else
        {
            if (!isChatBlack(_speaker))
            {
                ChatQueue.getInstance().add(PLAYER_SINGLE, _speaker, _destName,
                        _target, _content);
                //这里的向GM发聊天内容在 ChatClientHandler.java 里，因为要重复发两次，GM只记一次就可以 
            }
        }
        
    }
    
    /**
     * 单个玩家系统提示消息
     * @param _dsetName
     * @param _content
     */
    public void sendSinglePlayer (String _dsetName, String _content)
    {
        ChatQueue.getInstance().add(TOP_SYSTEM_SINGLE, null, _dsetName,
        		null, _content);
    }

    /**
     * 地图
     * 
     * @param _target 发送者
     * @param _content 内容
     */
    public void sendMapPlayer (HeroPlayer _sender, String _content)
    {
        if (isChatBlack(_sender))
            return;

        ChatQueue.getInstance().add(PLAYER_MAP, _sender, "", null, _content);
        toGMaddChatContent(_sender.getName(),"同地图玩家",_content);
    }

    /**
     * 世界
     * 
     * @param _name 发送者
     * @param _content 内容
     */
    public void sendWorldPlayer (HeroPlayer _speaker, String _content)
    {
        if (!isChatBlack(_speaker))
        {
            ChatQueue.getInstance().add(PLAYER_WORLD, _speaker, null, null,
                    _content);
            toGMaddChatContent(_speaker.getName(),"所有玩家",_content);
        }
    }
    
    /**
     * 用集结号角向世界发言
     * @param _speaker
     * @param _content
     */
    public void sendWorldPlayerUseMassHorn(HeroPlayer _speaker, String _content){
    	if (!isChatBlack(_speaker))
        {
            ChatQueue.getInstance().add(PLAYER_WORLD, _speaker, null, null,
                    _content, true);
            toGMaddChatContent(_speaker.getName(),"所有玩家",_content);
        }
    }

    public void sendWorldGM (String _gmName, String _content)
    {
        ChatQueue.getInstance().add(PLAYER_WORLD, _gmName, _content);
    }
    /**
     * gm 发公告
     * @param _GMName
     * @param _content
     */
    public void sendNoticeGM(String _GMName,String _content){
    	ChatQueue.getInstance().add(TOP_SYSTEM_WORLD, _GMName, _content,true);
        loadGmNotice();
    }

    /**
     * 队伍聊天
     * 
     * @param _speaker
     * @param _content
     */
    public void sendGroupPlayer (HeroPlayer _speaker, String _content)
    {
        if (!isChatBlack(_speaker))
        {
            ChatQueue.getInstance().add(PLAYER_GROUP, _speaker, null, null,
                    _content);
            toGMaddChatContent(_speaker.getName(),"同队伍玩家",_content);
        }
    }

    /**
     * 公会聊天
     * 
     * @param _speaker
     * @param _content
     */
    public void sendGuildContent (HeroPlayer _speaker, String _content)
    {
        if (!isChatBlack(_speaker))
        {
            ChatQueue.getInstance().add(PLAYER_GUILD, _speaker, null, null,
                    _content);
            toGMaddChatContent(_speaker.getName(),"同工会玩家",_content);
        }
    }

    /**
     * 设置滚动消息
     * 
     * @param str
     */
    public void setTopSys (String[] str)
    {
        synchronized (tipMutex)
        {
            tip = str;
        }
    }

    /**
     * 清除滚动消息
     */
    public void clearTopSys ()
    {
        synchronized (tipMutex)
        {
            tip = null;
        }
    }

    /**
     * 世界系统消息，公告
     * 
     * @param _content 内容
     */
    public void sendWorldBottomSys (String _content)
    {
        ChatQueue.getInstance().add(TOP_SYSTEM_WORLD, null, null, null,
                _content);
    }

    /**
     * 队伍系统消息 1
     * 
     * @param guildID 队伍ID
     * @param _content 内容
     */
    public void sendGroupBottomSys (int groupID, String _content)
    {
        Group group = GroupServiceImpl.getInstance().getGroup(groupID);

        sendGroupBottomSys(group, _content);
    }

    /**
     * 队伍系统消息 2
     * 
     * @param _group 队伍
     * @param _content 内容
     */
    public void sendGroupBottomSys (Group _group, String _content)
    {
        ChatQueue.getInstance().add(BOTTOM_SYSTEM_GROUP, null, null, null,
                _content);
    }

    /**
     * 工会系统消息
     * 
     * @param guildID 工会ID
     * @param _content 内容
     */
    public void sendGuildBottomSys (Guild _guild, String _content)
    {
        ChatQueue.getInstance().addGuildSys(_guild.getID(), _content);
    }

    /**
     * 在队伍中拾东西消息
     * 
     * @param _groupID 队伍ID
     * @param _content 内容
     * @param _goods 物品
     * @param _num 数量
     * @param _needExcludeTrigger
     * @param _playerObjectID
     */
    public void sendGroupGoods (int _groupID, String _content, Goods _goods,
            byte _num, boolean _needExcludeTrigger, int _playerObjectID)
    {
        Group group = GroupServiceImpl.getInstance().getGroup(_groupID);

        if (null != group)
        {
            HeroPlayer player;
            GetGoodsNofity msg = new GetGoodsNofity(_content, _goods.getName(),
                    _goods.getTrait().getViewRGB(), _num);

            ArrayList<HeroPlayer> list = group.getPlayerList();
            for (int i = 0; i < list.size(); i++)
            {
                player = list.get(i);

                if (player != null && player.isEnable())
                {
                    if (_needExcludeTrigger
                            && _playerObjectID == player.getID())
                    {
                        continue;
                    }

                    ChatQueue.getInstance().addGoodsMsg(player, msg);
                }
            }
        }
    }

    /**
     * 发送单体拾取东西消息
     * 
     * @param _player 发送者
     * @param _content 内容
     * @param _goods 物品
     * @param _num 数量
     */
    public void sendSingleGoods (HeroPlayer _player, String _content,
            Goods _goods, byte _num)
    {
        ChatQueue.getInstance().addGoodsMsg(_player, _content,
                _goods.getName(), _goods.getTrait().value(), _num);
    }

    /**
     * 阵营聊天
     * 
     * @param _player
     * @param _content
     */
    public void sendClan (HeroPlayer _speaker, short _clan, String _content)
    {
        if (!isChatBlack(_speaker))
        {
            ChatQueue.getInstance().add(CLAN, _speaker, null, null, _content,
                    _clan);
            toGMaddChatContent(_speaker.getName(),"同阵营玩家",_content);
        }
    }

    @Override
    protected void start ()
    {
        WorldHornService.getInstance().start();
        
    }
    
    public void loadGmNotice(){
        synchronized (gmNoticeMap){
    	    gmNoticeMap = GmDAO.getGmNoticeList(GmServiceImpl.serverID);
log.info("loadGmNotice gmNoticeMap size  = " + gmNoticeMap.size());
        }
    }
    
    /**
     * 根据公告的开始时间和结束时间定时发送公告
     * @author jiaodongjie
     * 如果当前处于公告的开始时间和结束时间之间，则根据公告的间隔和次数发送公告
     * 如果公告的结束时间已经过时，则取消此定时器且从定时器列表里移除
     */
    class CheckSendNoticeTask extends TimerTask{

    	Timer sendNoticeTimer;
        SendNoticeTask sntask;
		@Override
		public void run ()
		{
            synchronized (gmNoticeMap){
                if(gmNoticeMap != null && gmNoticeMap.size()>0){
                    for (Iterator<GmNotice> iterator = gmNoticeMap.values().iterator(); iterator.hasNext();){
//                    for(Integer nid : gmNoticeMap.keySet()){
                        GmNotice notice = iterator.next();
                        if(notice.getStartTime().getTime() <= System.currentTimeMillis()
                                && notice.getEndTime().getTime() >= System.currentTimeMillis()){
                            log.debug("公告期内....");
                            if(sendNoticeTimerMap.get(notice.getId()) == null){
                                log.debug("新加公告任务..");
                                sendNoticeTimer = new Timer();
                                sntask = new SendNoticeTask(notice.getId(),notice.getTimes(),notice.getContent());
                                sendNoticeTimer.schedule(sntask, 0, notice.getIntervalTime());
                                sendNoticeTimerMap.put(notice.getId(), sendNoticeTimer);
                                sendNoticeMap.put(notice.getId(),sntask);
                            }
                        }else{
                            log.info("gm notice title="+notice.getId()+" cancel..");
    //                        if(notice.getEndTime().getTime() <= System.currentTimeMillis()){
                                sendNoticeTimer = sendNoticeTimerMap.get(notice.getId());
                                if(sendNoticeTimer != null){
                                    sendNoticeTimer.cancel();
                                    sendNoticeTimer = null;
                                    sendNoticeTimerMap.remove(notice.getId());

                                }
    //                        }
//                            gmNoticeMap.remove(nid);
                            iterator.remove();
                        }
                    }
                }
            }
		}
    	
    }
    
    /**
     * 发送公告定时器
     * @author jiaodongjie
     *
     */
    class SendNoticeTask extends TimerTask{
        int nid; //公告 ID
    	int times;//发送次数
    	String content;//发送内容
    	SendNoticeTask(int nid,int _times, String _content){
            this.nid = nid;
    		this.times = _times;
    		this.content = _content;
    	}
		@Override
		public void run ()
		{
            GmNotice notice = gmNoticeMap.get(nid);
            if(notice != null){
                if(notice.getStartTime().getTime() <= System.currentTimeMillis()
                        && notice.getEndTime().getTime() >= System.currentTimeMillis()){
                    for(int i=0; i<times; i++){
                        sendNoticeGM("",content);
                    }
                }else {
                    log.debug("cancel notice id="+nid);
                    this.cancel();
                    sendNoticeMap.remove(nid);
                }

            }
		}
    	
    }

    /**
     * 定时重新加载公告列表
     * @author jiaodongjie
     *
     */
    class GmNoticeLoadTask extends TimerTask{

		@Override
		public void run ()
		{
			synchronized(gmNoticeMap)
			{
				loadGmNotice();
			}
			
		}
    	
    }
    
    /**
     * 定时执行滚动提示
     * 
     * @author Luke 陈路
     * @date Jul 28, 2009
     */
    class SystemTipTask extends TimerTask
    {
        @Override
        public void run ()
        {
            // TODO Auto-generated method stub
            synchronized (tipMutex)
            {
                if (tip != null)
                    ChatQueue.getInstance().add(
                            ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,
                            tip[random.nextInt(tip.length)]);
            }
        }
    }

    /**
     * 解析喊物品信息的聊天内容
     * 
     * @param _player
     * @param _content
     * @param _startIndex
     * @param _goodsInfoNumber
     * @return
     */
    public static String parseGoodsInContent (HeroPlayer _player,
            String _content)
    {
        int goodsInfoStartIndex = 0, goodsInfoEndIndex = 0;
        String goodsInfo;
        int goodsInfoNumber = 0;
        StringBuffer sb = new StringBuffer();

        try
        {
            while (-1 != (goodsInfoEndIndex = _content.indexOf("]", 3)))
            {
                goodsInfoStartIndex = goodsInfoEndIndex - 3;
                goodsInfo = _content.substring(goodsInfoStartIndex,
                        goodsInfoEndIndex);

                if (!goodsInfo.startsWith("[") && goodsInfoEndIndex >= 4)
                {
                    goodsInfoStartIndex = goodsInfoEndIndex - 4;
                }

                goodsInfo = _content.substring(goodsInfoStartIndex,
                        goodsInfoEndIndex + 1);

                if (goodsInfo.startsWith("["))
                {
                    Goods goods = parseGoods(_player, goodsInfo.toLowerCase());

                    if (null != goods)
                    {
                        sb.append(_content.substring(0, goodsInfoStartIndex));
                        sb.append("#S");
                        sb.append(goods.getTrait().value());
                        sb.append("F");
                        sb.append(goods.getID());
                        sb.append("[");
                        sb.append("<goodsname_"+goodsInfoNumber+">").append(goods.getName()).append("</goodsname_"+goodsInfoNumber+">");
                        sb.append("]");

                        goodsInfoNumber++;

                        if (goodsInfoEndIndex + 1 == _content.length())
                        {
                            return sb.append("<num>").append(goodsInfoNumber).append("</num>").toString();
                        }

                        if (3 == goodsInfoNumber)
                        {
                            return sb.append(
                                    _content.substring(goodsInfoEndIndex + 1))
                                    .append("<num>").append(goodsInfoNumber).append("</num>")
                                    .toString();
                        }
                    }
                    else
                    {
                        sb.append(_content.substring(0, goodsInfoEndIndex + 1));
                    }

                    _content = _content.substring(goodsInfoEndIndex + 1);
                }
                else
                {
                    if (goodsInfoEndIndex + 1 == _content.length())
                    {
                        return sb.append(_content).append("<num>").append(goodsInfoNumber).append("</num>").toString();
                    }

                    sb.append(_content.substring(0, goodsInfoEndIndex + 1))
                            .toString();
                    _content = _content.substring(goodsInfoEndIndex + 1);
                }
            }

            return sb.append(_content).append("<num>").append(goodsInfoNumber).append("</num>").toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return _content;
    }

    /**
     * 解析聊天内容中携带的物品描述信息
     * 
     * @param _player
     * @param _goodsInfo
     * @return
     */
    private static Goods parseGoods (HeroPlayer _player, String _goodsInfo)
    {
        int bagGridIndex = -1;
        EGoodsType goodsType = null;
        Object bag = null;

        try
        {
            _goodsInfo = _goodsInfo.substring(1, _goodsInfo.length() - 1);
            bagGridIndex = Integer.parseInt(_goodsInfo.substring(1));

            if (_goodsInfo.startsWith("w"))
            {
                goodsType = EGoodsType.EQUIPMENT;
                bag = _player.getBodyWear();
            }
            else if (_goodsInfo.startsWith("z"))
            {
                goodsType = EGoodsType.EQUIPMENT;
                bag = _player.getInventory().getEquipmentBag();
            }
            else if (_goodsInfo.startsWith("c"))
            {
                goodsType = EGoodsType.MATERIAL;
                bag = _player.getInventory().getMaterialBag();
            }
            else if (_goodsInfo.startsWith("t"))
            {
                goodsType = EGoodsType.SPECIAL_GOODS;
                bag = _player.getInventory().getSpecialGoodsBag();
            }
            else if (_goodsInfo.startsWith("y"))
            {
                goodsType = EGoodsType.MEDICAMENT;
                bag = _player.getInventory().getMedicamentBag();
            }
            else if (_goodsInfo.startsWith("r"))
            {
                goodsType = EGoodsType.TASK_TOOL;
                bag = _player.getInventory().getTaskToolBag();
            }

            return GoodsServiceImpl.getInstance().bagGoodsModel(bag, goodsType,
                    bagGridIndex);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
