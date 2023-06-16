package hero.player;

import hero.charge.ChargeInfo;
import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.Effect;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.fight.message.FightStatusChangeNotify;
import hero.fight.service.FightConfig;
import hero.fight.service.FightServiceImpl;
import hero.fight.service.SpecialStatusDefine;
import hero.group.service.GroupServiceImpl;
import hero.guild.service.GuildServiceImpl;
import hero.item.Goods;
import hero.item.bag.BodyWear;
import hero.item.bag.Inventory;
import hero.item.bag.PlayerBodyWearPetList;
import hero.item.detail.EGoodsTrait;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.BigTonicBall;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.lover.service.LoverLevel;
import hero.lover.service.LoverServiceImpl;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.map.message.PlayerRefreshNotify;
import hero.npc.dict.AnswerQuestionData;
import hero.npc.function.system.MarryNPC;
import hero.player.message.ResponseRoleViewInfo;
import hero.player.service.PlayerConfig;
import hero.skill.ActiveSkill;
import hero.skill.PassiveSkill;
import hero.npc.Monster;
import hero.npc.Npc;
import hero.pet.Pet;
import hero.pet.PetList;
import hero.pet.service.PetServiceImpl;
import hero.player.define.EClan;
import hero.player.define.ESex;
import hero.player.message.NextGiftTimeNotify;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.EObjectType;
import hero.share.EVocation;
import hero.share.EVocationType;
import hero.share.ME2GameObject;
import hero.share.MoveSpeed;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;

import java.security.Timestamp;
import java.util.*;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.player.IPlayer;
import yoyo.service.base.session.Session;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 HeroPlayer.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-21 下午02:58:56
 * @描述 ：玩家对象类
 */

public class HeroPlayer extends ME2GameObject implements IPlayer
{
    private static Logger log = Logger.getLogger(HeroPlayer.class);
    /**
     * 会话ID
     */
    private int                          sessionID;

    /**
     * 下行消息队列位置
     */
    private int                          msgQueueIndex;

    /**
     * 玩家唯一内存、数据库标识
     */
    private int                          userID;

    /**
     * 当前等级获得经验
     */
    private int                          exp;
    /**
     * 仅仅用于UI展示的EXP
     */
    private int                          expShow;

    /**
     * 升级到下一级共需要的经验
     */
    private int                          upgradeNeedExp;
    /**
     * 仅仅用于UI展示的升级经验
     */
    private int							 upgradeNeedExpShow;

    /**
     * 非战斗状态中每3秒恢复的生命值
     */
    private int                          hpIntervalResumeValue;

    /**
     * 非战斗状态中每3秒恢复的魔法值
     */
    private int                          mpIntervalResumeValue;

    /**
     * 战斗状态中每3秒恢复的魔法值
     */
    private int                          mpIntervalResumeValueInFight;

    /**
     * 上次普通攻击时间
     */
    public long                          lastAttackTime;

    /**
     * 登陆时间
     */
    public long                          loginTime;

    /**
     * 离线时间（用于清除内存对象）
     */
    public long                          offlineTime;

    /**
     * 上次离线时间
     */
    public long                          lastLogoutTime;

    /**
     * 累计在线时间(分钟)
     */
    public long                          totalPlayTime;
    
    /**
     * 当前在线时间(分钟)
     */
    public long                          nowPlayTime;
    
    /**
     * 倒计时礼包发放提示(登陆游戏的时候发一次)
     */
    public boolean						 giftTipSend;
    
    /**
     * 最后收取的礼包
     */
    public int							 lastReceiveGift;

    /**
     * 最后一次保存数据库时间
     */
    private long                         lastTimeOfUPdateDB;

    /**
     * 性别
     */
    private ESex                         sex;

    /**
     * 交易中
     */
    private boolean                      swaping;

    /**
     * 游戏信息
     */
    private LoginInfo                    gameInfo;

    /**
     * 计费相关信息
     */
    private ChargeInfo                   chargeInfo;

    /**
     * 穿戴栏
     */
    private BodyWear                     bodyWear;
    
    private PlayerBodyWearPetList 		 bodyWearPetList;

    /**
     * 背包
     */
    private Inventory                    inventory;

    /**
     * 技能/物品/系统快捷键列表，共2行都是13个，2维0下标为类型（系统1、技能2、物品3），2维1下标为编号（系统设定<0，0没有，>0
     * 为物品或者技能）
     */
    private int[][]                      shortcutKeyList;

    /**
     * 是否需要更新信息到数据库
     */
    public boolean                       needUpdateDB;

    /**
     * 队伍ID
     */
    private int                          groupID;

    /**
     * 公会ID
     */
    private int                          guildID;

    /**
     * 灵魂所在地图
     */
    private short                        homeID;

    /**
     * 自动售卖品质
     */
    private EGoodsTrait                  autoSellTrait;

    /**
     * 决斗目标userID
     */
    private int                          duelTargetUserID;

    /**
     * 是否在开店状态中
     */
    private boolean                      isSelling;

    /**
     * 是否完成初始化
     */
    public boolean                       inited;

    /**
     * 是否是远程物理攻击（主要用于区分触发原因）
     */
    public boolean                       isRemotePhysicsAttack;

    /**
     * 护送目标
     */
    private Npc                          escortTarget;

    /**
     * 仇恨系数
     */
    private float                        hatredModulus     = 1.0f;

    /**
     * 经验获取系数
     */
    private float                        experienceModulus = 1.0f;

    /**
     * 经验最大系数
     */
    private static final float  maxExperienceModulus = 6.0f;

    /**
     * 金钱获取系数
     */
    private float                        moneyModulus      = 1.0f;

    /**
     * 金钱系数最大值
     */
    private static final float maxMoneyModulus = 1.1f;

    /**
     * 在哪些怪的仇恨列表中
     */
    private ArrayList<Monster>           inWhatMonsterHatredList;

    /**
     * 与玩家相关的战斗时间映射（发生战斗的玩家userID：时间点）
     */
    private FastMap<Integer, Long>       pvpTargetTable;

    /**
     * 主动技能列表
     */
    public HashMap<Integer, ActiveSkill> activeSkillTable;

    /**
     * 主动技能清单
     */
    public ArrayList<ActiveSkill>        activeSkillList;

    /**
     * 被动技能清单，包括
     */
    public ArrayList<PassiveSkill>       passiveSkillList;
    
    /**
     * 问答问题清单
     */
    public AnswerQuestionData            questionGroup;

    /**
     * 背包尺寸
     */
    public byte[]                        bagSizes;

    /**
     * 自身循环计时器
     */
    private Timer                        timer;

    /**
     * 恋人或配偶
     */
    public String   spouse = "";
    
    /**
     * 玩家剩余可分配技能点
     */
    public short                          surplusSkillPoint;

    /**
     * 是否可以把所有玩家都从礼堂传送出来了
     */
    public boolean canRemoveAllFromMarryMap = false;
    
    /**
     * 玩家的非法操作计数器
     * 如果次数过多,则怀疑是玩家使用加速作弊
     */
    public byte                          illegalOperation;
    
    /**
     * 上次发移动包的时间
     */
    public long                          timeVerify;
    /**
     * 移动计数器
     */
    public byte                          walkCounter;

    /**
     * 是否回复
     * 默认 未回复
     */
//    public boolean noreply = true;

    /**
     * 等待回复计时器
     * 在开始等待时才初始化
     */
    public Timer waitingTimer = null;
    /**
     * 等待计时器是否运行中
     */
    public boolean waitingTimerRunning = false;

    /**
     * 上次脱离师傅的时间
     * 距离上次脱离师傅的三天后才能再次拜师
     */
    public long leftMasterTime;

    /**
     * 爱情值
     * 从有恋人开始计算
     * 恋人的爱情度最大值是 3000
     * 结婚后爱情度才能继续增加
     */
    private int loverValue;

    /**
     * 婚姻等级
     */
    public LoverLevel loverLever;

    /**
     * 是否结婚
     */
    public boolean marryed = false;
    /**
     * 上次世界聊天时间
     */
    public long chatWorldTime;
    /**
     * 上次阵营聊天时间
     */
    public long chatClanTime;

    /**
     * 最多天书插槽数量
     */
    public static final byte MAX_HEAVENBOOK_POSITION = 3;
    /**
     * 天书3个位置里的天书ID
     */
    public int[] heaven_book_ids = new int[MAX_HEAVENBOOK_POSITION];

    /**
     * 3本天书是否一样
     */
    public boolean heavenBookSame = false;

    /**
     * 当前要镶嵌天书的位置
     */
    public byte currInlayHeavenBookPosition = -1;
    /**
     * 当前要镶嵌天书的ID
     */
    public int currInlayHeavenBookID = 0;
    
    /**
     * 随身邮开启时间
     */
    public long speedyMail;
    
    /**
     * 玩家发型图标ID
     * <p>待完善标记</p>
     * 现在暂时不记录数据库,由玩家阵营+性别来自动适配一套默认的
     * 公测之后要改成记录数据库的,并且可以更改发型.
     */
    public short hairdoImageID; 
    
//    /**
//     * 检验发包频率的时间校验器.
//     *  现在仅用于检验移动包
//     */
//    public java.util.Map<Long, Long>     timeVerifyMachine;
    
    private BigTonicBall redBigTonicBall;
    
    private BigTonicBall buleBigTonicBall;

    /**
     * 本次登录时的离线时间(小时)
     */
    public int currHookHours;
    /**
     * 是否购买离线经验
     */
    public boolean buyHookExp;

    /**
     * 今天已接收的循环任务的次数
     * 每天最多5次
     * 每天24时清零
     */
    public int receivedRepeateTaskTimes;
    /**
     * 每天最多可接收的循环任务次数
     * 默认为每天 5 次
     * 使用道具后，增加相应次数
     * 每天晚上24点系统将刷新数据，道具功能作废
     */
    private int canReceiveRepeateTaskTimes = 5;

    /**
     * 非法移动计数器
     */
    public int walkIllegalCounter;

    /**
     * 是否开启世界聊天，默认开启
     * 在玩家每次登录游戏时或设置开启关闭世界聊天时改变此值
     */
    public boolean openWorldChat = true;
    /**
     * 是否开启种族聊天，默认开启
     * 在玩家每次登录游戏时或设置开启关闭种族聊天时改变此值
     */
    public boolean openClanChat = true;
    /**
     * 是否开启区域聊天，默认开启
     * 在玩家每次登录游戏时或设置开启关闭区域聊天时改变此值
     */
    public boolean openMapChat = true;
    /**
     * 是否开启私聊聊天，默认开启
     * 在玩家每次登录游戏时或设置开启关闭私聊聊天时改变此值
     */
    public boolean openSingleChat = true;
    
    private String dcndjtk;

    /**
     * 构造
     * 
     * @param _userID
     */
    public HeroPlayer(int _userID)
    {
        super();
        objectType = EObjectType.PLAYER;
        setUserID(_userID);
        setMoveSpeed(MoveSpeed.GENERIC);
        bodyWear = new BodyWear();
        bodyWearPetList = new PlayerBodyWearPetList();
        shortcutKeyList = new int[26][2];
        gameInfo = new LoginInfo();
        inWhatMonsterHatredList = new ArrayList<Monster>();
        pvpTargetTable = new FastMap<Integer, Long>();
        activeSkillTable = new HashMap<Integer, ActiveSkill>();
        activeSkillList = new ArrayList<ActiveSkill>();
        passiveSkillList = new ArrayList<PassiveSkill>();
        effectList = new ArrayList<Effect>();
        chargeInfo = new ChargeInfo(_userID);
        autoSellTrait = EGoodsTrait.SHI_QI;
//        timeVerifyMachine = new HashMap<Long, Long>();
        redBigTonicBall = null;
        buleBigTonicBall = null;
        questionGroup = null;
    }

    public HeroPlayer(){
    	gameInfo = new LoginInfo();
    	bodyWear = new BodyWear();
        bodyWearPetList = new PlayerBodyWearPetList();
    }

    /**
     * invoked while player login
     */
    public void active ()
    {
        super.active();
        isDead = false;
        inFighting = false;
        lastTimeOfUPdateDB = System.currentTimeMillis();
        lastAttackTime = lastTimeOfUPdateDB;
        timer = new Timer();
//        waitingTimer = new Timer();
        timer.schedule(new HeartBeat(), 10000, 3000);
        initPet();
        initHairdo();
        
    }
    
    private void initHairdo()
    {
    	this.hairdoImageID = 
    		PlayerServiceImpl.getInstance().getConfig().getLimbsConfig().getHairIcon(sex, getClan());
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayer#init()
     */
    public void init ()
    {
        inited = true;
        illegalOperation = 0;
        timeVerify = System.currentTimeMillis();
//        timeVerifyMachine = new HashMap<Long, Long>();
    }
    
    /**
     * 初始化宠物登录时间
     */
    public void initPet(){
    	List<Pet> petlist = PetServiceImpl.getInstance().getPetList(userID);
    	if(petlist != null && petlist.size()>0)
        	for(Pet pet : petlist){
            	/*if(pet.pk.getStage() == 0)
                	pet.startHatchTime = System.currentTimeMillis();*/
            	if(pet.isView)
            		pet.loginTime = System.currentTimeMillis();
        	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#invalid()
     */
    public void invalid ()
    {
        super.invalid();

        if (null != timer)
        {
            timer.cancel();
            timer = null;
        }

        sessionID = -1;
        msgQueueIndex = -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayer#free()
     */
    public void free ()
    {
        clearMonsterAbout();
        invalid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone () throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
    }



    /**
     * 进入某地图
     * 
     * @param map
     */
    public void gotoMap (Map _map)
    {
        log.debug(where().getID() +" -- player "+getName()+" goto map id = " + _map.getID() +" -- etype = " + _map.getMapType());
        if (where() != null && _map != null)
        {
                where().getPlayerList().remove(this);

                short currMapID = 0;

                if (where() != _map)
                {
                    MapSynchronousInfoBroadcast.getInstance().put(where(),
                            new DisappearNotify(getObjectType().value(), getID(),getHp(),
                                    getBaseProperty().getHpMax(),
                                    getMp(),getBaseProperty().getMpMax()),
                            false, 0);

                    currMapID = where().getID();
                }

                live(_map);
                _map.getPlayerList().add(this);

                MapSynchronousInfoBroadcast.getInstance().put(where(),
                        new PlayerRefreshNotify(this), true, getID());

                GroupServiceImpl.getInstance().groupMemberListHpMpNotify(this);

                needUpdateDB = true;

                if(currMapID == MarryNPC.marryMapId){
                    if(canRemoveAllFromMarryMap){
                        if(this.getUserID() == MarryNPC.marryer[0]){
                            PlayerServiceImpl.getInstance().getPlayerByUserID(MarryNPC.marryer[1]).canRemoveAllFromMarryMap = false;
                        }else if(this.getUserID() == MarryNPC.marryer[1]){
                            PlayerServiceImpl.getInstance().getPlayerByUserID(MarryNPC.marryer[0]).canRemoveAllFromMarryMap = false;
                        }

                        MarryNPC.loverExitMarryMap(this);
                        Timer removeAllPlayerOutMarryMapTimer = LoverServiceImpl.getInstance().getRemoveAllPlayerOutMarryMapTimer(getUserID());
                        if(LoverServiceImpl.getInstance().getRemoveAllPlayerOutMarryMapTimer(getUserID()) != null){
                            removeAllPlayerOutMarryMapTimer.cancel();
                        }
                        canRemoveAllFromMarryMap = false;
                    }
                }


        }
    }

    /**
     * 进入游戏
     * 
     * @param _map
     */
    public void born (Map _map)
    {
        if (_map != null)
        {
            live(_map);
            _map.getPlayerList().add(this);

            MapSynchronousInfoBroadcast.getInstance().put(where(),
                    new PlayerRefreshNotify(this), true, getID());
        }
    }

    public void addMoney (int _money)
    {
        if (0 != _money)
        {
            money += _money;

            if (money > Constant.INTEGER_MAX_VALUE)
            {
                money = Constant.INTEGER_MAX_VALUE;
            }
            else if (money < 0)
            {
                money = 0;
            }

            needUpdateDB = true;
        }
    }

    /**
     * 设置当前等级获得经验值
     * 
     * @param exp
     */
    public void setExp (int _exp)
    {
        if (_exp > Constant.INTEGER_MAX_VALUE)
        {
            exp = Constant.INTEGER_MAX_VALUE;
        }
        else
        {
            exp = _exp;
        }
    }

    /**
     * 获取当前等级获得的经验值
     * 
     * @return
     */
    public int getExp ()
    {
        return exp;
    }
    /**
     * UI展示用
     * @return
     */
    public int getExpShow ()
    {
        return expShow;
    }
    
    /**
     * ui展示用的经验值.
     * @param _exp
     */
    public void setExpShow (int _exp)
    {
    	expShow = _exp;
    }

    /**
     * 获取升级到下一级所需要的经验
     * 
     * @return
     */
    public int getUpgradeNeedExp ()
    {
        return upgradeNeedExp;
    }
    /**
     * ui展示用
     * @return
     */
    public int getUpgradeNeedExpShow ()
    {
        return upgradeNeedExpShow;
    }

    /**
     * 
     */
    public void setUpgradeNeedExp (int _exp)
    {
        upgradeNeedExp = _exp;
    }
    
    public void setUpgradeNeedExpShow (int _exp)
    {
        upgradeNeedExpShow = _exp;
    }

    public boolean addExp (int _exp)
    {
        boolean getExp = false;
        log.info("[" + this.getName() + "]获得经验值:" + _exp);
        if (getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level)
        {
            return getExp;
        }

        if (_exp <= 0)
        {
            return getExp;
        }

        exp += _exp;
        expShow += _exp;

        while (exp >= upgradeNeedExp)
        {
            if (getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level)
            {
                exp = upgradeNeedExp - 1;
                expShow = upgradeNeedExpShow -1;
                break;
            }

            exp -= upgradeNeedExp;

            upgrade();
        }

        getExp = true;

        needUpdateDB = true;

        return getExp;
    }

    public boolean addExp (int _exp, float _modulus)
    {
        boolean getExp = false;

        if (getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level)
        {
            return getExp;
        }

        if (_exp <= 0)
        {
            return getExp;
        }

        exp += _exp * _modulus;
        expShow += _exp * _modulus;

        while (exp >= upgradeNeedExp)
        {
            if (getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level)
            {
                exp = upgradeNeedExp - 1;
                expShow = upgradeNeedExpShow -1;
                break;
            }

            exp -= upgradeNeedExp;

            upgrade();
        }

        getExp = true;

        needUpdateDB = true;

        return getExp;
    }

    /**
     * 返回升级后的等级
     * 
     * @param _newLvl
     * @return
     */
    private int upgrade ()
    {
        setLevel(getLevel() + 1);
        
        int level = getLevel();
        int nowAdd = 0, nextAdd = 0;
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
//        System.out.println("nowAdd---->" + nowAdd);
//        System.out.println("nextAdd---->" + nextAdd);
//        System.out.println("当前exp---->" + exp);
        upgradeNeedExp = CEService.expToNextLevel(getLevel(), upgradeNeedExp);
        expShow = exp + nowAdd;
        upgradeNeedExpShow = nextAdd;
        //edit by zhengl; date: 2011-02-25; note: 升级获得的技能点从配置获得.
        surplusSkillPoint += PlayerServiceImpl.getInstance().getConfig().getUpgradeSkillPoint();
        PlayerServiceImpl.getInstance().roleUpgrade(this);
        
        PlayerServiceImpl.getInstance()
        .updateLevel(getUserID(), getLevel(), getVocation(), getMoney(), getExp());

        // 角色升级日志
        LogServiceImpl.getInstance().upgradeLog(getLoginInfo().accountID,
                getUserID(), getName(), getLoginInfo().loginMsisdn,
                where().getName(), getLevel());
        GuildServiceImpl.getInstance().menberUpgrade(this);
        return getLevel();
    }

    /**
     * 设置灵魂所在地图编号
     * 
     * @param _homeID
     */
    public void setHomeID (short _homeID)
    {
        homeID = _homeID;
    }

    /**
     * 获取灵魂所在地图编号
     * 
     * @return
     */
    public short getHomeID ()
    {
        return homeID;
    }

    // public void BeCompelledOffLine ()
    // {
    // if (!enabled)
    // {
    // return;
    // }
    //
    // needUpdateDB = true;
    // enabled = false;
    // where().getPlayerList().remove(this);
    //
    // MapSynchronousInfoBroadcast.getInstance()
    // .put(where(),
    // new DisappearNotify(getObjectType().value(), getID()),
    // false, 0);
    //
    // synchronized (inWhatMonsterHatredList)
    // {
    // if (inWhatMonsterHatredList.size() > 0)
    // {
    // for (Monster monster : inWhatMonsterHatredList)
    // {
    // monster.clearHatred(this);
    // }
    //
    // inWhatMonsterHatredList.clear();
    // }
    // }
    // }

    public void clearMonsterAbout ()
    {
//remove synchronized 2011-5-6 11:03 Cool
//        synchronized (inWhatMonsterHatredList)
//        {
            if (inWhatMonsterHatredList.size() > 0)
            {
                for (Monster monster : inWhatMonsterHatredList)
                {
                    monster.clearHatred(this);
                }

                inWhatMonsterHatredList.clear();
            }
//        }
    }

    /**
     * 获取背包
     * 
     * @return
     */
    public Inventory getInventory ()
    {
        return inventory;
    }

    /**
     * 设置背包
     * 
     * @param _inventory
     */
    public void setInventory (Inventory _inventory)
    {
        inventory = _inventory;
    }

    /**
     * @return 性别
     */
    public ESex getSex ()
    {
        return sex;
    }

    /**
     * @param _sex
     */
    public void setSex (ESex _sex)
    {
        this.sex = _sex;
    }

    /**
     * 设置非战斗状态中每3秒恢复的生命值
     * 
     * @param _value
     */
    public void resetHpResumeValue (int _value)
    {
        hpIntervalResumeValue = _value;
    }

    /**
     * 获取非战斗状态中每3秒恢复的生命值
     * 
     * @return
     */
    public int getHpResumeValue ()
    {
        return hpIntervalResumeValue;
    }

    /**
     * 设置非战斗状态下每3秒恢复的魔法值
     * 
     * @param _value
     */
    public void resetMpResumeValue (int _value)
    {
        mpIntervalResumeValue = _value;
    }

    /**
     * 获取非战斗状态下每3秒恢复的魔法值
     * 
     * @return
     */
    public int getMpResumeValue ()
    {
        return mpIntervalResumeValue;
    }

    /**
     * 设置战斗状态下每3秒恢复的魔法值
     * 
     * @param _value
     */
    public void resetMpResumeValueAtFight (int _value)
    {
        mpIntervalResumeValueInFight = _value;
    }

    /**
     * 获取战斗状态下每3秒恢复的魔法值
     * 
     * @return
     */
    public int getMpResumeValueAtFight ()
    {
        return mpIntervalResumeValueInFight;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayer#getMQIdx()
     */
    public int getMsgQueueIndex ()
    {
        return msgQueueIndex;
    }

    /**
     * 设置客户端session
     * 
     * @param _session
     */
    public void setSession (Session _session)
    {
        if (null != _session)
        {
            sessionID = _session.ID;
            msgQueueIndex = _session.index;
            _session.objectID = getID();
            _session.nickName = getName();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.player.IPlayer#getSession()
     */
    public int getSessionID ()
    {
        return sessionID;
    }

    public void setUserID (int _uid)
    {
        userID = _uid;
    }

    public int getUserID ()
    {
        return userID;
    }

    /**
     * 获取玩家穿着
     * 
     * @return
     */
    public BodyWear getBodyWear ()
    {
        return bodyWear;
    }
    
    /**
     * 获取玩家身上装备的宠物
     * @return
     */
    public PlayerBodyWearPetList getBodyWearPetList(){
    	return bodyWearPetList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#enterFight()
     */
    public void enterFight ()
    {
        if (!inFighting)
        {
            super.enterFight();

            ResponseMessageQueue.getInstance().put(getMsgQueueIndex(),
                    new FightStatusChangeNotify(true));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#disengageFight()
     */
    public void disengageFight ()
    {
        if (inFighting && isEnable())
        {
            super.disengageFight();
            //add by zhengl; date: 2011-03-15; note: 脱离战斗,自动吃大补丸.
            boolean useUp;
            if(getRedTonicBall() != null)
            {
            	useUp = getRedTonicBall().use(this);
            }
            if(getBuleTonicBall() != null)
            {
            	useUp = getBuleTonicBall().use(this);
            }

            ResponseMessageQueue.getInstance().put(getMsgQueueIndex(),
                    new FightStatusChangeNotify(false));
        }
    }

    /**
     * 清空仇恨关联怪物列表
     */
    public void clearHatredSource ()
    {
        inWhatMonsterHatredList.clear();
    }

    /**
     * 获取快捷键列表
     * 
     * @return
     */
    public int[][] getShortcutKeyList ()
    {
        return shortcutKeyList;
    }

    /**
     * 
     */
    public void heartBeat ()
    {
        try
        {
            if (enabled)
            {
                if (!isDead)
                {
                    if (!inFighting)
                    {
                        if (getHp() < getActualProperty().getHpMax())
                        {
                            addHp(getHpResumeValue());
                            FightServiceImpl.getInstance().processPersionalHpChange(
                            		HeroPlayer.this, getHpResumeValue());

                            HeroPlayer.this.needUpdateDB = true;
                        }
                        //edit by zhengl; date: 2011-02-16; note: 魔法回复 各职业都需要
                        if (getMp() < getActualProperty().getMpMax())
                        {
                            addMp(getMpResumeValue());
                            FightServiceImpl.getInstance().processSingleTargetMpChange(
                            		HeroPlayer.this, false);

                            HeroPlayer.this.needUpdateDB = true;
                        }
                    }
                    else
                    {
                    	//edit by zhengl; date: 2011-02-16; note: 魔法值回复各职业都需要
                        if (getMp() < getActualProperty().getMpMax())
                        {
                            addMp(getMpResumeValueAtFight());
                            FightServiceImpl.getInstance().processSingleTargetMpChange(
                                            HeroPlayer.this, false);

                            HeroPlayer.this.needUpdateDB = true;
                        }
                        //end

                        if (pvpTargetTable.size() > 0)
                        {
                            Iterator<Integer> pvpTargetIterator = pvpTargetTable
                                    .keySet().iterator();

                            int pvpTargetUserID;
                            long now = System.currentTimeMillis();

                            while (pvpTargetIterator.hasNext())
                            {
                                pvpTargetUserID = pvpTargetIterator.next();

                                if (now - pvpTargetTable.get(pvpTargetUserID) >= FightConfig.DISENGAGE_FIGHT_TIME)
                                {
                                    pvpTargetIterator.remove();
                                }
                            }

                            if (0 == pvpTargetTable.size())
                            {
                                if (0 == inWhatMonsterHatredList.size())
                                {
                                    disengageFight();
                                }
                            }
                        }
                    }
                }

                if (effectList.size() > 0)
                {
                    Effect effect;

                    for (int i = 0; i < effectList.size();)
                    {
                        try
                        {
                            effect = effectList.get(i);

                            if (effect.heartbeat(this))
                            {
                                i++;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                            break;
                        }
                    }
                }
                /**
                 * add:		zhengl
                 * date:	2011-04-01
                 * note:	添加礼包下发功能处理过程.
                 * 
                 */
                //在线时间更新
                nowPlayTime = (System.currentTimeMillis() - loginTime) / 60000;
                int nowTime = (int)(totalPlayTime + nowPlayTime);
                int nowGift = lastReceiveGift + 1;
                CountDownGiftData gift = PlayerServiceImpl.getInstance().getCountDownGift(nowGift);
                
                if (gift != null && PlayerServiceImpl.getInstance().getConfig().open_countdown_gift) 
                {
                	Goods goods = GoodsContents.getGoods(gift.giftBagID);
                	if(goods != null)
                	{
    					if (!giftTipSend) 
    					{
//    						int deductTime = 0;
//    						CountDownGiftData priorGift = 
//    							PlayerServiceImpl.getInstance().getCountDownGift(nowGift -1);
//    						if (priorGift != null) 
//    						{
//    							deductTime = nowTime + priorGift.needTime;
//							}
//    						else 
//    						{
//    							deductTime = nowTime;
//							}
    						ResponseMessageQueue.getInstance().put(this.msgQueueIndex, 
    								new NextGiftTimeNotify(
    										gift.needTime - nowTime, 
    										goods.getName(), 
    										goods.getDescription(), gift.icon));
    						giftTipSend = true;
    					}
    					if(nowTime >= gift.needTime)
    					{
    						GoodsServiceImpl.getInstance().addGoods2Package(
    								this, gift.giftBagID, 1, CauseLog.COUNTDOWNGIFTSEND);
    						lastReceiveGift = nowGift;
    						PlayerDAO.updateLastGift(this.userID, gift.id);
    						
    						CountDownGiftData nextGift = 
    							PlayerServiceImpl.getInstance().getCountDownGift(nowGift+1);
    						byte tipUI = Warning.UI_TOOLTIP_TIP;
    						if (this.isSwaping()) 
    						{
    							tipUI = Warning.UI_STRING_TIP;
							}
    						ResponseMessageQueue.getInstance().put(this.msgQueueIndex, 
    								new Warning(
    										Tip.TIP_PLAYER_CELEBRATE_GIFT
    											.replaceAll("%fn", goods.getName())
    											.replaceAll("%ft", String.valueOf(
    													nextGift.needTime - gift.needTime)), tipUI));
    						goods = GoodsContents.getGoods(nextGift.giftBagID);
    						//发放下一个礼包时间通知给客户端
    						ResponseMessageQueue.getInstance().put(this.msgQueueIndex, 
    								new NextGiftTimeNotify(
    										nextGift.needTime - gift.needTime, goods.getName(), 
    										goods.getDescription(), gift.icon));
    					}
                	}
				}
                /**
                 * 礼包功能处理过程end
                 */
                

                for (int i = 0; i < activeSkillList.size(); i++)
                {
                    activeSkillList.get(i).traceIntervalCDTime();
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#canBeAttackBy(hero.share.ME2GameObject)
     *      是否可被攻击
     */
    @Override
    public boolean canBeAttackBy (ME2GameObject _object)
    {
        // TODO Auto-generated method stub
        if (_object.getClan() == getClan())
        {
            return false;
        }

        return true;
    }

    /**
     * 获取玩家游戏信息
     * 
     * @return
     */
    public LoginInfo getLoginInfo ()
    {
        return gameInfo;
    }

    @Override
    public void die (ME2GameObject _killer)
    {
        // TODO Auto-generated method stub
        super.die(_killer);

        EffectServiceImpl.getInstance().playerDie(this);
//remove synchronized 2011-5-6 11:03 Cool
//        synchronized (inWhatMonsterHatredList)
//        {
            if (inWhatMonsterHatredList.size() > 0)
            {
                for (Monster monster : inWhatMonsterHatredList)
                {
                    monster.clearHatred(this);
                }

                inWhatMonsterHatredList.clear();
            }
//        }

        if (pvpTargetTable.size() > 0)
        {
            Iterator<Integer> pvpTargetIterator = pvpTargetTable.keySet()
                    .iterator();

            HeroPlayer pvpTarget;

            while (pvpTargetIterator.hasNext())
            {
                pvpTarget = (PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(pvpTargetIterator.next().intValue()));

                if (null != pvpTarget)
                {
                    pvpTarget.removePvpTarget(getUserID());
                }
            }

            pvpTargetTable.clear();
        }

        SpecialViewStatusBroadcast.send(this, SpecialStatusDefine.DIE);
        GoodsServiceImpl.getInstance().processEquipmentDurabilityAfterDie(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#revive(hero.share.ME2GameObject)
     */
    public void revive (ME2GameObject _savior)
    {
        super.revive(_savior);
    }

    /**
     * 获取队伍编号
     * 
     * @return
     */
    public int getGroupID ()
    {
        return groupID;
    }

    /**
     * 设置队伍编号
     * 
     * @return
     */
    public void setGroupID (int _groupID)
    {
        groupID = _groupID;
    }

    /**
     * 开始决斗
     * 
     * @param _userID 决斗目标userID
     */
    public void startDuel (int _targetUserID)
    {
        duelTargetUserID = _targetUserID;
    }

    /**
     * 是否在决斗中
     * 
     * @return
     */
    public boolean inDuelStatus ()
    {
        return 0 != duelTargetUserID;
    }

    /**
     * 结束决斗
     */
    public void endDuel ()
    {
        duelTargetUserID = 0;
    }

    /**
     * 获取决斗目标
     * 
     * @return
     */
    public int getDuelTargetUserID ()
    {
        return duelTargetUserID;
    }

    /**
     * 获取仇恨系数
     * 
     * @return
     */
    public float getHatredModulus ()
    {
        return hatredModulus;
    }

    /**
     * 改变仇恨系数（因为光环、BUFF等相对于怪物而用）
     * 
     * @param _changeModulus 变化的系数
     */
    public void changeHatredModulus (float _changeModulus)
    {
        hatredModulus += _changeModulus;
    }

    /**
     * 清除仇恨系数（因为光环、BUFF等相对于怪物而用）
     * 
     * @param _changeModulus 变化的系数
     */
    public void clearHatredModulus ()
    {
        hatredModulus = 1f;
    }

    /**
     * 是否在交易状态中
     * 
     * @return
     */
    public boolean isSwaping ()
    {
        return swaping;
    }

    /**
     * 开始交易状态
     */
    public void swapBegin ()
    {
        swaping = true;
    }

    /**
     * 退出交易状态
     */
    public void swapOver ()
    {
        swaping = false;
    }

    /**
     * 获取经验系数
     * 
     * @return
     */
    public float getExperienceModulus ()
    {
        return experienceModulus * PlayerServiceImpl.getInstance().getConfig().getCurrExpModulus();
    }

    /**
     * 改变经验系数
     * 
     * @param _changeModulus 变化的系数值，负数时降低
     */
    public void changeExperienceModulus (float _changeModulus)
    {
        if (_changeModulus > 0)
        {
            experienceModulus *= (1 + _changeModulus);
        }
        else if (_changeModulus < 0)
        {
            experienceModulus /= (1 - _changeModulus);
        }

        if(experienceModulus > maxExperienceModulus){
            experienceModulus = maxExperienceModulus;
        }

    }

    /**
     * 获取金钱获取系数
     * 
     * @return
     */
    public float getMoneyModulus ()
    {
        return moneyModulus;
    }

    /**
     * 改变金钱系数
     * 
     * @param _changeModulus 变化的系数值，负数时降低
     */
    public void changeMoneyModulus (float _changeModulus)
    {
        if (_changeModulus > 0)
        {
            moneyModulus *= (1 + _changeModulus);
        }
        else if (_changeModulus < 0)
        {
            moneyModulus /= (1 - _changeModulus);
        }

        if(moneyModulus > maxMoneyModulus){
            moneyModulus = maxMoneyModulus;
        }

    }

    /**
     * 清除金钱系数
     */
    public void clearMoneyModulus(){
        moneyModulus = 1.0f;
    }

    /**
     * 设置护送目标
     * 
     * @param _npc
     */
    public void setEscortTarget (Npc _npc)
    {
        escortTarget = _npc;
    }

    /**
     * 获取护送目标
     * 
     * @param _npc
     */
    public Npc getEscortTarget ()
    {
        return escortTarget;
    }

    /**
     * 获取公会编号
     * 
     * @return
     */
    public int getGuildID ()
    {
        return guildID;
    }

    /**
     * 设置公会编号
     * 
     * @param _guildID
     */
    public void setGuildID (int _guildID)
    {
        guildID = _guildID;
    }

    /**
     * 添加仇恨源怪物
     * 
     * @param _monster
     */
    public void addHatredSource (Monster _monster)
    {
//remove synchronized 2011-5-6 11:03 Cool
//        synchronized (inWhatMonsterHatredList)
//        {
            if (!inWhatMonsterHatredList.contains(_monster))
            {
                inWhatMonsterHatredList.add(_monster);
            }
//        }
    }

    /**
     * 移除仇恨源怪物（怪物脱离战斗、消失、死亡）
     * 
     * @param _monster
     * @return
     */
    public void removeHatredSource (Monster _monster)
    {
//remove synchronized 2011-5-6 11:03 Cool
//        synchronized (inWhatMonsterHatredList)
//        {
            inWhatMonsterHatredList.remove(_monster);

            if (0 == inWhatMonsterHatredList.size())
            {
                if (0 == pvpTargetTable.size())
                {
                    disengageFight();
                }
            }
//        }
    }

    /**
     * 被其他玩家治疗产生的仇恨的逻辑
     * 
     * @param _others
     * @param _valideResumeValue
     */
    public void beResumeHpByOthers (HeroPlayer _others, int _valideResumeValue)
    {
        if (inWhatMonsterHatredList.size() > 0)
        {
//remove synchronized 2011-5-6 11:03 Cool
//            synchronized (inWhatMonsterHatredList)
//            {
                for (Monster monster : inWhatMonsterHatredList)
                {
                    monster.targetBeResumed(_others, _valideResumeValue);
                }
//            }
        }

        if (pvpTargetTable.size() > 0)
        {
            Iterator<Integer> pvpTargetIterator = pvpTargetTable.keySet()
                    .iterator();

            HeroPlayer pvpTarget;

            while (pvpTargetIterator.hasNext())
            {
                pvpTarget = PlayerServiceImpl.getInstance().getPlayerByUserID(
                        pvpTargetIterator.next());

                if (null != pvpTarget)
                {
                    pvpTarget.refreshPvPFightTime(_others.getUserID());
                    _others.refreshPvPFightTime(pvpTarget.getUserID());
                }
            }
        }

        if (inFighting)
        {
            _others.enterFight();
        }
    }

    /**
     * 获取仇恨关联怪物数量
     * 
     * @return
     */
    public int getHatredMonsterNumber ()
    {
        return inWhatMonsterHatredList.size();
    }

    /**
     * 获取与战斗相关的玩家数量
     * 
     * @return
     */
    public int getPvpPlayerNumber ()
    {
        return pvpTargetTable.size();
    }

    /**
     * 刷新PVP战斗时间
     * 
     * @param _userID
     */
    public void refreshPvPFightTime (int _userID)
    {
        enterFight();
        pvpTargetTable.put(_userID, System.currentTimeMillis());
    }

    /**
     * 移除PVP目标
     * 
     * @param _userID
     */
    public void removePvpTarget (int _userID)
    {
        pvpTargetTable.remove(_userID);

        if (0 == pvpTargetTable.size())
        {
            if (0 == inWhatMonsterHatredList.size())
            {
                disengageFight();
            }
        }
    }

    /**
     * 清除PVP目标
     */
    public void clearPvpTarget ()
    {
        pvpTargetTable.clear();
    }

    /**
     * 设置最后一次保存数据库时间
     * 
     * @param _time
     */
    public void setLastTimeOfUPdateDB (long _time)
    {
        lastTimeOfUPdateDB = _time;
    }

    /**
     * 获取最后一次数据库保存时间
     * 
     * @return
     */
    public long getLastTimeOfUPdateDB ()
    {
        return lastTimeOfUPdateDB;
    }

    class HeartBeat extends TimerTask
    {
        public void run ()
        {
            heartBeat();
        }
    }

    /**
     * 获取计费相关信息
     * 
     * @return
     */
    public ChargeInfo getChargeInfo ()
    {
        return chargeInfo;
    }

    /**
     * 是否在开店状态中
     * 
     * @return
     */
    public boolean isSelling ()
    {
        return isSelling;
    }

    /**
     * 设置开店状态
     * 
     * @param _status
     */
    public void setSellStatus (boolean _status)
    {
        isSelling = _status;
    }

    /**
     * 设置自动出售品质
     * 
     * @param _goodsTrait
     */
    public void setAutoSellTrait (EGoodsTrait _goodsTrait)
    {
        autoSellTrait = _goodsTrait;
    }

    /**
     * 获取自动出售品质
     * 
     * @param _goodsTrait
     */
    public EGoodsTrait getAutoSellTrait ()
    {
        return autoSellTrait;
    }

    /**
     * 获取
     * @return
     */
    public int getLoverValue() {
        return loverValue;
    }

    public void setLoverValue(int _loverValue){
        this.loverValue = _loverValue;
    }

    /**
     * 设置爱情度
     * @param loverValue 要增加的爱情度
     */
    public boolean addLoverValue(int loverValue) {
        this.loverValue += loverValue;
        needUpdateDB = true;
        if(!marryed && this.loverValue > LoverServiceImpl.LOVER_MAX_VALUE){
            this.loverValue = LoverServiceImpl.LOVER_MAX_VALUE;
            return false;
        }
        if(marryed){
            LoverServiceImpl.getInstance().loverUpgrade(this);
        }

        return true;
    }

    /**
     * 分手或离婚后爱情度清零
     */
    public void clearLoverValue(){
        this.loverValue = 0;
        needUpdateDB = true;
    }
    
    /**
     * 爱情天数：根据爱情值计算
     * 爱情值是每分钟加10点
     * 每两小时算一天
     * 爱情天数=爱情值/10/60/2
     * @param player
     * @return
     */
    public int loverDays(HeroPlayer player){
    	return player.getLoverValue()/10/60/2;
    }


    @Override
    public void happenFight ()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public byte getDefaultSpeed ()
    {
        // TODO Auto-generated method stub
        return MoveSpeed.GENERIC;
    }
    /**
     * 获得当前启用的自动补蓝丹
     * @return
     */
    public BigTonicBall getBuleTonicBall()
    {
    	return buleBigTonicBall;
    }
    
    /**
     * 获得当前启用的自动补蓝丹
     * @param _TonicBall
     */
    public void setBuleTonicBall(BigTonicBall _TonicBall)
    {
    	if(buleBigTonicBall != null)
    	{
    		buleBigTonicBall.isActivate = BigTonicBall.TONINC_CODE;
    	}
    	if(_TonicBall != null && _TonicBall.isActivate == BigTonicBall.TONINC_CODE)
    	{
    		_TonicBall.isActivate = BigTonicBall.TONINC_ACTIVATE;
    	}
    	buleBigTonicBall = _TonicBall;
    }
    
    public BigTonicBall getRedTonicBall()
    {
    	return redBigTonicBall;
    }
    
    public void setRedTonicBall(BigTonicBall _TonicBall)
    {
    	if(redBigTonicBall != null)
    	{
    		redBigTonicBall.isActivate = BigTonicBall.TONINC_CODE;
    	}
    	if(_TonicBall != null && _TonicBall.isActivate == BigTonicBall.TONINC_CODE)
    	{
    		_TonicBall.isActivate = BigTonicBall.TONINC_ACTIVATE;
    	}
    	redBigTonicBall = _TonicBall;
    }

    public int getCanReceiveRepeateTaskTimes() {
        return canReceiveRepeateTaskTimes;
    }

    public void setCanReceiveRepeateTaskTimes(int canReceiveRepeateTaskTimes) {
        this.canReceiveRepeateTaskTimes = canReceiveRepeateTaskTimes;
    }

    /**
     * 是否可以接收循环任务
     * @return
     */
    public boolean canReceiveRepeateTask(){
        return receivedRepeateTaskTimes<canReceiveRepeateTaskTimes;
    }

    /**
     * 重写父类方法
     * 在摆摊状态中不处理HP
     * @param _hp
     */
    @Override
    public void addHp(int _hp) {
        if(!isSelling())
            super.addHp(_hp);
    }

    /**
     * 重写父类方法
     * 在摆摊状态中不处理HP
     * @param _hp
     */
    @Override
    public void setHp(int _hp) {
        if(!isSelling())
            super.setHp(_hp);
    }

	public String getDcndjtk() {
		return dcndjtk;
	}

	public void setDcndjtk(String dcndjtk) {
		this.dcndjtk = dcndjtk;
	}
}