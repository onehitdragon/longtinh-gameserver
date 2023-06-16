package hero.player.service;

import hero.charge.service.ChargeServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
//import hero.gm.EResponseType;
//import hero.gm.ResponseToGmTool;
//import hero.gm.service.GmServiceImpl;
import hero.group.Group;
import hero.group.message.ReduceMemberNotify;
import hero.group.service.GroupServiceImpl;
import hero.guild.service.GuildServiceImpl;
import hero.item.*;
import hero.item.bag.Inventory;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsTrait;
import hero.item.dictionary.GoodsContents;
import hero.item.dictionary.SuiteEquipmentDataDict;
import hero.item.dictionary.SuiteEquipmentDataDict.SuiteEData;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.ESpecialGoodsType;
import hero.item.special.HeavenBook;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.item.message.GoodsShortcutKeyChangeNotify;
import hero.item.service.EquipmentFactory;
import hero.log.service.ServiceType;
import hero.lover.service.LoverServiceImpl;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.service.MapRelationDict;
import hero.map.service.MapServiceImpl;
import hero.micro.help.HelpService;
import hero.micro.store.PersionalStore;
import hero.micro.store.StoreDAO;
import hero.micro.store.StoreService;
import hero.micro.teach.TeachService;
import hero.npc.function.system.postbox.MailService;
import hero.skill.Skill;
import hero.skill.service.SkillServiceImpl;
import hero.social.service.SocialServiceImpl;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.CountDownGiftData;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.player.define.ESex;
import hero.player.message.ExperienceAddNotify;
import hero.player.message.HotKeySumByMedicament;
import hero.player.message.MoneyChangeNofity;
import hero.player.message.RefreshRoleProperty;
import hero.player.message.RoleUpgradeNotify;
import hero.player.message.VocationChangeNotify;
import hero.share.Constant;
import hero.share.EMagic;
import hero.share.EObjectLevel;
import hero.share.EVocation;
import hero.share.EVocationType;
import hero.share.cd.CDUnit;
import hero.share.letter.LetterService;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.MagicDamage;
import hero.share.service.ShareServiceImpl;
import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;
import hero.task.service.TaskServiceImpl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.ServiceManager;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.player.IPlayerDAO;
import yoyo.service.base.player.IPlayerService;
import yoyo.service.base.session.Session;
import yoyo.service.base.session.SessionServiceImpl;
import yoyo.tools.YOYOOutputStream;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 *
 * @文件 PlayerServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 上午11:41:36
 * @描述 ：玩家服务类
 */

public class PlayerServiceImpl extends AbsServiceAdaptor<PlayerConfig> implements
        IPlayerService
{
	private static Logger log = Logger.getLogger(PlayerServiceImpl.class);
    private static PlayerServiceImpl     instance;

    /**
     * 在线玩家对象列表
     */
    private FastList<HeroPlayer>         playerList;

    /**
     * 以userID为key在线的玩家对象列表
     */
    private FastMap<Integer, HeroPlayer> playerTableByUserID;

    /**
     * 以会话ID为索引的玩家对象列表
     */
    private FastMap<Integer, HeroPlayer> sessionIDKeyPlayerTable;

    /**
     * 玩家可以领取的礼包的容器
     */
    private FastMap<Integer, CountDownGiftData> countDownGiftDict;

    /**
     * 负责操作具体游戏中玩家数据库的接口
     */
    private PlayerDAO                    dao;

    /**
     * 不绘制
     */
    public final static byte             MONEY_DRAW_LOCATION_NONE    = 0;

    /**
     * 在屏幕中间飘出
     */
    public final static byte             MONEY_DRAW_LOCATION_MIDDLE  = 1;

    /**
     * 以红字在中间显示
     */
    public final static byte             MONEY_DRAW_LOCATION_WARNING = 2;

    private Timer                        timer;

    private Timer loverValueTimer;

    private FastMap<Integer,Timer> loverValueTimerMap = new FastMap<Integer,Timer>();

    /**
     * 爱情值排名
     */
    private java.util.Map<Integer,Integer> loverValueOrderMap = new HashMap<Integer,Integer>();
    /**
     * 爱情值排名定时更新器
     */
    private Timer loverValueOrderTimer;

    /**
     * 客户端回复是否覆盖天书的上行报文号
     */
    public static final short REPLY_INLAY_HEAVEN_BOOK_COMMAND_CODE =0x419;

    private PlayerServiceImpl()
    {
        config = new PlayerConfig();
        playerList = new FastList<HeroPlayer>();
        sessionIDKeyPlayerTable = new FastMap<Integer, HeroPlayer>();
        playerTableByUserID = new FastMap<Integer, HeroPlayer>();
        timer = new Timer();
        loverValueOrderTimer = new Timer();
        dao = new PlayerDAO();
        countDownGiftDict = new FastMap<Integer, CountDownGiftData>();
    }

    public static PlayerServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new PlayerServiceImpl();
        }
        return instance;
    }

    public Timer getLoverValueTimerByUserID(int userID){
        return loverValueTimerMap.get(userID);
    }

    public void startLoverValueTimer(HeroPlayer player){
        loverValueTimer = new Timer();
        loverValueTimer.schedule(new PlayerLoverValueTimer(player.getName(),player.spouse),0,60*1000);
        loverValueTimerMap.put(player.getUserID(),loverValueTimer);
    }

    public void removeLoverValueTimer(HeroPlayer player){
        loverValueTimerMap.remove(player.getUserID());
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.AbstractService#sessionCreate(int)
     */
    @Override
    public void createSession (Session _session)
    {
        HeroPlayer player = playerTableByUserID.get(_session.userID);

        if (null == player)
        {
            player = (HeroPlayer) dao.load(_session.userID);

            if (null != player)
            {
                PlayerDAO.loadPlayerAccountInfo(player);
                //add by zhengl ; date: 2011-01-23 ; note: 玩家喊话时间更新
                player.chatClanTime = PlayerDAO.loadClanChatWait(player.getUserID());
                player.chatWorldTime = PlayerDAO.loadWorldChatWait(player.getUserID());
                //end

                /**
                 * 查找配偶，成为恋人后就显示
                 */
                String spouse = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
                if(null != spouse){
                    player.spouse = spouse;

                }else{
                    spouse = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                    if(null != spouse){
                        player.marryed = true;
                        player.spouse = spouse;
                        player.loverLever = LoverServiceImpl.getInstance().getLoverLevel(player.getLoverValue());
                    }
                }
                if(null != spouse){
                    HeroPlayer other = getPlayerByName(spouse);
                    if(other != null && other.isEnable() && (!other.isDead())){
                        startLoverValueTimer(player);
                    }
                }
                log.debug("["+player.getName()+"] spouse:"+spouse);

                //添加玩家使用循环任务道具增加的可接收次数
                int rtt = PlayerDAO.getRepeatTaskGoodsTimes(player.getUserID());
                log.info("rtt ="+rtt+",player.receivedRepeateTaskTimes="+player.receivedRepeateTaskTimes);
                if(rtt > 5 && (rtt+5) < player.receivedRepeateTaskTimes){//这种情况出现可能是因为定时清除任务因为某种原因没有执行
                    player.receivedRepeateTaskTimes = 0;  //
                }
                player.setCanReceiveRepeateTaskTimes(player.getCanReceiveRepeateTaskTimes()+rtt);

                playerList.add(player);
                playerTableByUserID.put(_session.userID, player);

                LogWriter.println("加载角色成功 ：" + player.getName());
            }
            else
            {
                LogWriter.println("加载角色失败 ：" + _session.userID);

                return;
            }
        }
        else
        {
            if (player.isDead())
            {
            	//edit by zhengl; date: 2011-02-24; note: 添加系数进HP计算公式
                player.setHp(
                		CEService.hpByStamina(
                				CEService.playerBaseAttribute(
                						player.getLevel(),
                						player.getVocation().getStaminaCalPara()),
                				player.getLevel(),
                				player.getObjectLevel().getHpCalPara())
                				);
                player.setMp(CEService.mpByInte(CEService.playerBaseAttribute(
                        player.getLevel(), player.getVocation()
                                .getInteCalcPara()), player.getLevel(),
                        EObjectLevel.NORMAL.getMpCalPara()));

                // 如果玩这是魔族，则使用魔族复活地图 --add by jiaodj 2011-05-16
                short[] relations = MapRelationDict.getInstance().getRelationByMapID(player.where().getID());
                short mapid = relations[2];
                if(player.getClan() == EClan.HE_MU_DU && relations[8] > 0){
                    mapid = relations[8];
                }
                Map where = MapServiceImpl.getInstance().getNormalMapByID(mapid);

                player.setCellX(where.getBornX());
                player.setCellY(where.getBornY());
                player.live(where);
            }

            player.offlineTime = 0;
        }

        player.loginTime = System.currentTimeMillis();
        player.setSession(_session);
        player.needUpdateDB = true;

        player.getChargeInfo().pointAmount = ChargeServiceImpl.getInstance().getBalancePoint(player.getLoginInfo().accountID);
        log.debug("login init player point=" + player.getChargeInfo().pointAmount);
        sessionIDKeyPlayerTable.put(_session.ID, player);
    }

    /**
     * 提供一个单独的方法，可以根据USERID加载玩家的基本信息和物品信息
     * @param userID
     * @return
     */
    public HeroPlayer load(int userID){
        HeroPlayer player = (HeroPlayer)dao.load(userID);
        if (player != null && null == player.getInventory())
        {
            player.setInventory(new Inventory(player.getUserID(),
                    player.bagSizes));
            GoodsDAO.loadPlayerGoods(player);
        }
        return player;
    }

    @Override
    public void sessionFree (Session _session)
    {
        // TODO Auto-generated method stub
        if (null != _session)
        {
            try
            {
                HeroPlayer player = sessionIDKeyPlayerTable.remove(_session.ID);

                if (null != player)
                {
                    PlayerServiceImpl.getInstance().removeLoverValueTimer(player);
                    player.free();
                    player.offlineTime = System.currentTimeMillis();


                    LogServiceImpl.getInstance().roleOnOffLog(
                            player.getLoginInfo().accountID,
                            player.getUserID(), player.getName(),
                            player.getLoginInfo().loginMsisdn,
                            player.loginTime,
                            player.getLoginInfo().logoutCause,
                            player.getLoginInfo().userAgent,
                            player.getLoginInfo().clientVersion,
                            player.getLoginInfo().clientType,
                            player.getLoginInfo().communicatePipe,
                            player.where().getName(),
                            player.offlineTime,
                            player.getLoginInfo().publisher,
                            player.where().getID());


        			List<Pet> petlist = PetServiceImpl.getInstance().getPetList(player.getUserID());
        			if (petlist != null && petlist.size() > 0) {
        				for (Pet pet : petlist) {
        					PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);

        				}
        			}

        			// PlayerServiceImpl.getInstance().dbUpdate(player);

        			PersionalStore store = StoreService.get(player.getUserID());
        			if (null != store && (store.opened || player.isSelling())) {
        				log.debug("退出游戏，摆摊状态 = " + store.opened + ", player storestatus = "
        						+ player.isSelling());
        				StoreService.takeOffAll(player);
        				StoreService.clear(player.getUserID()); // 退出游戏时，清除玩家之前的开店状态
        			}

                    GroupServiceImpl.getInstance().clean(player.getUserID());


        			PlayerServiceImpl.getInstance().getPlayerList().remove(player);
        			SessionServiceImpl.getInstance().fireSessionFree(player.getSessionID());
                    //add by zhengl; date: 2011-02-15; note:
                    PlayerServiceImpl.getInstance().getSessionPlayerList().remove(player.getSessionID());
                    PlayerServiceImpl.getInstance().getUserIDPlayerList().remove(player.getUserID());

                    ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(player.getUserID());
                    //end
        			log.info(player.getName()+ "; sessionID=" + _session.ID + "  退出,或者被踢");
                }
            }
            catch (Exception e)
            {
                LogWriter.error(instance, e);
            }
        }
    }

    @Override
    public void clean (int _userID)
    {
        playerTableByUserID.remove(_userID);
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.AbstractService#startService()
     */
    @Override
    protected void start ()
    {
        timer.schedule(new PlayerInfoUpdateTask(), UPDATE_DB_DELAY,
                UPDATE_DB_INTERVAL);
        timer.schedule(new PlayerClearTask(), CLEAR_PLAYER_TASK_DELAY,
                CLEAR_PLAYER_TASK_CHECK_INTERVAL);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
//        calendar.getTimeInMillis();
        timer.schedule(new ClearPlayerReceiveRepeateTaskTimes(),calendar.getTimeInMillis()-System.currentTimeMillis(),24*60*60*1000);
//        timer.schedule(new ClearPlayerReceiveRepeateTaskTimes(),calendar.getTimeInMillis()-System.currentTimeMillis(),10*60*1000);

        loverValueOrderTimer.schedule(
        		new PlayerLoverValueOrderTimer(), 0, UPDATE_LOVER_VALUE_ORDER_INTERVAL);
        loadCountDownGift(config.countdown_gift_data_path);
    }

    private void loadCountDownGift(String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + _dataPath);

            return;
        }
        File[] dataFileList = dataPath.listFiles();

        try
        {
            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();
                CountDownGiftData countDownGift = null;
                while (rootIt.hasNext())
                {
                	Element subE = rootIt.next();

                    if (null != subE)
                    {
                    	countDownGift = new CountDownGiftData();
                    	countDownGift.name = subE.elementTextTrim("name");
                    	countDownGift.giftBagID = Integer.valueOf(subE.elementTextTrim("giftBagID"));
                    	countDownGift.needTime = Integer.valueOf(subE.elementTextTrim("needTime"));
                    	countDownGift.id = Integer.valueOf(subE.elementTextTrim("id"));
                    	countDownGift.content = subE.elementTextTrim("content");
                    	countDownGift.icon = Integer.valueOf(subE.elementTextTrim("icon"));
                    }
                    if(!this.countDownGiftDict.containsKey(countDownGift.id))
                    {
                    	countDownGiftDict.put(countDownGift.id, countDownGift);
                    }
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    /**
     * 获得指定的倒计时礼包
     * @param _id
     * @return
     */
    public CountDownGiftData getCountDownGift (int _id)
    {
    	return countDownGiftDict.get(_id);
    }

    /**
     * 根据玩家userID获取爱情排名
     * @param userID
     * @return
     */
    public int getPlayerLoverValueOrder(int userID){
    	return loverValueOrderMap.get(userID)==null?0:loverValueOrderMap.get(userID);
    }

    /**
     * 获取初始地图编号
     *
     * @param _clan
     * @return
     */
    public short getInitBornMapID (EClan _clan)
    {
        return config.getBornMapID(_clan);
    }

    /**
     * 获取初始地图位置X坐标
     *
     * @param _clan
     * @return
     */
    public short getInitBornX (EClan _clan)
    {
        return config.getBornPoint(_clan)[0];
    }

    /**
     * 获取初始地图位置Y坐标
     *
     * @param _clan
     * @return
     */
    public short getInitBornY (EClan _clan)
    {
        return config.getBornPoint(_clan)[1];
    }

    /**
     * 初始化玩家动态属性
     *
     * @param _player
     */
    public void initProperty (HeroPlayer _player)
    {
        int maxHP = 0, maxMP = 0, stamina = 0, inte = 0, strength = 0;
        int spirit = 0, lucky = 0, defense = 0, agility = 0;
        short physicsDeathblowLevel = 0, magicDeathblowLevel = 0, hitLevel = 0, duckLevel = 0;


        int level = _player.getLevel();
        EVocation vocation = _player.getVocation();

        stamina += CEService.playerBaseAttribute(level, vocation.getStaminaCalPara());
        inte += CEService.playerBaseAttribute(level, vocation.getInteCalcPara());

        strength += CEService.playerBaseAttribute(level, vocation.getStrengthCalcPara());
        spirit += CEService.playerBaseAttribute(level, vocation.getSpiritCalcPara());
        lucky += CEService.playerBaseAttribute(level, vocation.getLuckyCalcPara());
        agility += CEService.playerBaseAttribute(level, vocation.getAgilityCalcPara());
        //edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
        physicsDeathblowLevel = CEService.physicsDeathblowLevel(agility, lucky);
        magicDeathblowLevel = CEService.magicDeathblowLevel(inte, lucky);
        hitLevel = CEService.hitLevel(lucky);
        duckLevel = CEService.duckLevel(agility, lucky);

        EquipmentInstance[] equipmentList = _player.getBodyWear().getEquipmentList();
        _player.getBaseProperty().getMagicFastnessList().clear();

        //
        //武器携带属性加载过程start
        //
        for (EquipmentInstance e : equipmentList)
        {
            if (null != e && e.getCurrentDurabilityPoint() > 0)
            {
                maxHP += e.getArchetype().atribute.hp;
                maxHP += e.getArchetype().atribute.hp * e.getGeneralEnhance().getBasicModulus();
                maxMP += e.getArchetype().atribute.mp;
                maxMP += e.getArchetype().atribute.mp * e.getGeneralEnhance().getBasicModulus();

                stamina += e.getArchetype().atribute.stamina;
                stamina += e.getArchetype().atribute.stamina * e.getGeneralEnhance().getBasicModulus();
                inte += e.getArchetype().atribute.inte;
                inte += e.getArchetype().atribute.inte * e.getGeneralEnhance().getBasicModulus();
                strength += e.getArchetype().atribute.strength;
                strength += e.getArchetype().atribute.strength * e.getGeneralEnhance().getBasicModulus();
                spirit += e.getArchetype().atribute.spirit;
                spirit += e.getArchetype().atribute.spirit * e.getGeneralEnhance().getBasicModulus();
                lucky += e.getArchetype().atribute.lucky;
                lucky += e.getArchetype().atribute.lucky * e.getGeneralEnhance().getBasicModulus();
                agility += e.getArchetype().atribute.agility;
                agility += e.getArchetype().atribute.agility * e.getGeneralEnhance().getBasicModulus();
                defense += e.getArchetype().atribute.defense;
                defense += e.getArchetype().atribute.defense * e.getGeneralEnhance().getDefenseModulus();

                physicsDeathblowLevel += e.getArchetype().atribute.physicsDeathblowLevel;
                physicsDeathblowLevel += e.getArchetype().atribute.physicsDeathblowLevel
                        * e.getGeneralEnhance().getAdjuvantModulus();
                magicDeathblowLevel += e.getArchetype().atribute.magicDeathblowLevel;
                magicDeathblowLevel += e.getArchetype().atribute.magicDeathblowLevel
                        * e.getGeneralEnhance().getAdjuvantModulus();
                hitLevel += e.getArchetype().atribute.hitLevel;
                hitLevel += e.getArchetype().atribute.hitLevel
                        * e.getGeneralEnhance().getAdjuvantModulus();
                duckLevel += e.getArchetype().atribute.duckLevel;
                duckLevel += e.getArchetype().atribute.duckLevel
                        * e.getGeneralEnhance().getDefenseModulus();

                _player.getBaseProperty().getMagicFastnessList().add(
                        e.getArchetype().getMagicFastnessList());
                _player.getBaseProperty().getMagicFastnessList().add(
                        e.getArchetype().getMagicFastnessList(),
                        e.getGeneralEnhance().getDefenseModulus());
            }
        }
        //
        //武器携带属性加载过程end
        //
        short suiteID = _player.getBodyWear().hasSuite();
        //
        //套装携带属性加载过程start
        //
        if (suiteID != 0)
        {
            SuiteEData suiteEData = SuiteEquipmentDataDict.getInstance()
                    .getSuiteData(suiteID);

            if (null != suiteEData)
            {
                if (null != suiteEData.atribute)
                {
                    maxHP += suiteEData.atribute.hp;
                    maxMP += suiteEData.atribute.mp;
                    stamina += suiteEData.atribute.stamina;
                    inte += suiteEData.atribute.inte;
                    strength += suiteEData.atribute.strength;
                    spirit += suiteEData.atribute.spirit;
                    lucky += suiteEData.atribute.lucky;
                    agility += suiteEData.atribute.agility;
                    defense += suiteEData.atribute.defense;
                    physicsDeathblowLevel += suiteEData.atribute.physicsDeathblowLevel;
                    magicDeathblowLevel += suiteEData.atribute.magicDeathblowLevel;
                    hitLevel += suiteEData.atribute.hitLevel;
                    duckLevel += suiteEData.atribute.duckLevel;
                }

                _player.getBaseProperty().getMagicFastnessList().add(
                        suiteEData.fastnessList);
            }
        }
        //
        //套装携带属性加载过程end
        //
        defense += CEService.defenseBySpirit(spirit, vocation.getPhysicsDefenceSpiritPara());
        //
        //基础属性加载过程start
        //
        _player.getBaseProperty().setStamina(stamina);
        _player.getBaseProperty().setInte(inte);
        _player.getBaseProperty().setStrength(strength);
        _player.getBaseProperty().setSpirit(spirit);
        _player.getBaseProperty().setLucky(lucky);
        _player.getBaseProperty().setAgility(agility);
        _player.getBaseProperty().setDefense(defense);
        _player.getBaseProperty().setPhysicsDeathblowLevel(physicsDeathblowLevel);
        _player.getBaseProperty().setMagicDeathblowLevel(magicDeathblowLevel);
        _player.getBaseProperty().setHitLevel(hitLevel);
        _player.getBaseProperty().setPhysicsDuckLevel(duckLevel);
        //
        //基础属性加载过程end
        //
        maxHP += CEService.hpByStamina(stamina, level, _player.getObjectLevel().getHpCalPara());
        maxMP += CEService.mpByInte(inte, level, EObjectLevel.NORMAL.getMpCalPara());
        _player.getBaseProperty().setHpMax(maxHP);
        _player.getBaseProperty().setMpMax(maxMP);

        Weapon weapon = null;
        EquipmentInstance ei = _player.getBodyWear().getWeapon();

        int weaponMinMagicHarm = 0, weaponMaxMagicHarm = 0;
        int maxPhysicsAttack, minPhysicsAttack;

        if (null != ei)
        {
            weapon = (Weapon) ei.getArchetype();

            if (ei.getCurrentDurabilityPoint() > 0)
            {
                _player.setAttackRange(weapon.getAttackDistance());
                _player.setBaseAttackImmobilityTime((int) (weapon.getImmobilityTime() * 1000F));

                if (null != ei.getMagicDamage())
                {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }

                maxPhysicsAttack = CEService.maxPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), ei
                                .getWeaponMaxPhysicsAttack(),
                        weaponMaxMagicHarm, weapon.getImmobilityTime(),
                        EObjectLevel.NORMAL.getPhysicsAttckCalPara());

                minPhysicsAttack = CEService.minPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), ei
                                .getWeaponMinPhysicsAttack(),
                        weaponMinMagicHarm, weapon.getImmobilityTime(),
                        EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            }
            else
            {
                _player.setAttackRange(Constant.DEFAULT_ATTACK_DISTANCE);
                _player.setBaseAttackImmobilityTime(Constant.DEFAULT_IMMOBILITY_TIME * 1000);

                maxPhysicsAttack = CEService.maxPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), 0, 0,
                        Constant.DEFAULT_IMMOBILITY_TIME, EObjectLevel.NORMAL
                                .getPhysicsAttckCalPara());

                minPhysicsAttack = CEService.minPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), 0, 0,
                        Constant.DEFAULT_IMMOBILITY_TIME, EObjectLevel.NORMAL
                                .getPhysicsAttckCalPara());
            }
        }
        else
        {
            _player.setAttackRange(Constant.DEFAULT_ATTACK_DISTANCE);
            _player.setBaseAttackImmobilityTime(Constant.DEFAULT_IMMOBILITY_TIME * 1000);

            maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility,
                    vocation.getPhysicsAttackParaA(),
                    vocation.getPhysicsAttackParaB(),
                    vocation.getPhysicsAttackParaC(), 0, 0,
                    Constant.DEFAULT_IMMOBILITY_TIME,
                    EObjectLevel.NORMAL.getPhysicsAttckCalPara());

            minPhysicsAttack = CEService.minPhysicsAttack(strength, agility,
                    vocation.getPhysicsAttackParaA(),
                    vocation.getPhysicsAttackParaB(),
                    vocation.getPhysicsAttackParaC(), 0, 0,
                    Constant.DEFAULT_IMMOBILITY_TIME,
                    EObjectLevel.NORMAL.getPhysicsAttckCalPara());
        }

        _player.getBaseProperty().setMaxPhysicsAttack(maxPhysicsAttack);
        _player.getBaseProperty().setMinPhysicsAttack(minPhysicsAttack);

        int baseMagicHarmByInte = CEService.magicHarmByInte(inte);

        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SANCTITY, baseMagicHarmByInte);

        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.UMBRA,
                baseMagicHarmByInte);

        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.FIRE,
                baseMagicHarmByInte);

        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.WATER,
                baseMagicHarmByInte);

        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SOIL,
                baseMagicHarmByInte);

        if (null != weapon && ei.getCurrentDurabilityPoint() > 0)
        {
            MagicDamage weaponMagicDamage = ei.getMagicDamage();

            if (null != weaponMagicDamage)
            {
            	int magicHarmValue =
            		(weaponMagicDamage.minDamageValue + weaponMagicDamage.maxDamageValue)/2;
//                int magicHarmValue = CEService.magicHarm(inte,
//                                (weaponMagicDamage.minDamageValue + weaponMagicDamage.maxDamageValue) / 2);
                _player.getBaseProperty().getBaseMagicHarmList().reset(
                        weaponMagicDamage.magic, magicHarmValue);
            }
        }

        _player.clearHatredModulus();
        _player.getActualProperty().clearNoneBaseProperty();
        _player.getResistOddsList().clear();

        // 计算被动技能对基础属性的影响
        SkillServiceImpl.getInstance().changePropertySkillAction(_player, true);

        _player.getActualProperty().getMagicFastnessList().reset(
                _player.getBaseProperty().getMagicFastnessList());
        _player.getActualProperty().getBaseMagicHarmList().reset(
                _player.getBaseProperty().getBaseMagicHarmList());
        _player.getActualProperty().setMaxPhysicsAttack(
                _player.getBaseProperty().getMaxPhysicsAttack());
        _player.getActualProperty().setMinPhysicsAttack(
                _player.getBaseProperty().getMinPhysicsAttack());
        _player.setActualAttackImmobilityTime(_player.getBaseAttackImmobilityTime());

        _player.getActualProperty().setStamina(
                _player.getBaseProperty().getStamina());
        _player.getActualProperty().setInte(_player.getBaseProperty().getInte());
        _player.getActualProperty().setStrength(
                _player.getBaseProperty().getStrength());
        _player.getActualProperty().setSpirit(
                _player.getBaseProperty().getSpirit());
        _player.getActualProperty().setLucky(
                _player.getBaseProperty().getLucky());
        _player.getActualProperty().setAgility(
                _player.getBaseProperty().getAgility());
        _player.getActualProperty().setDefense(
                _player.getBaseProperty().getDefense());
        _player.getActualProperty().setPhysicsDeathblowLevel(
                _player.getBaseProperty().getPhysicsDeathblowLevel());
        _player.getActualProperty().setMagicDeathblowLevel(
                _player.getBaseProperty().getMagicDeathblowLevel());
        _player.getActualProperty().setHitLevel(
                _player.getBaseProperty().getHitLevel());
        _player.getActualProperty().setPhysicsDuckLevel(
                _player.getBaseProperty().getPhysicsDuckLevel());

        _player.getActualProperty().setHpMax(
                _player.getBaseProperty().getHpMax());
        _player.getActualProperty().setMpMax(
                _player.getBaseProperty().getMpMax());

        // 计算静态效果对基础属性的影响
        EffectServiceImpl.getInstance().staticEffectAction(_player);

        if (_player.getHp() > _player.getActualProperty().getHpMax())
        {
            _player.setHp(_player.getActualProperty().getHpMax());
        }

        if (_player.getMp() > _player.getActualProperty().getMpMax())
        {
            _player.setMp(_player.getActualProperty().getMpMax());
        }

        _player.resetHpResumeValue(CEService.hpResumeAuto(_player.getLevel(),
                _player.getActualProperty().getSpirit(),
                _player.getVocation().getStaminaCalPara()));
        _player.resetMpResumeValue(CEService.mpResumeAuto(_player.getLevel(),
                _player.getActualProperty().getSpirit(),
                _player.getVocation().getInteCalcPara()));
        _player.resetMpResumeValueAtFight(CEService.mpResumeAutoInFighting(_player.getMpResumeValue()));

    }

    /**
     * 重新计算玩家属性
     *
     * @param _player
     */
    public void reCalculateRoleProperty (HeroPlayer _player)
    {
        int maxHP = 0, maxMP = 0, stamina = 0, inte = 0, strength = 0;
        int spirit = 0, lucky = 0, defense = 0, agility = 0;
        short physicsDeathblowLevel = 0, magicDeathblowLevel = 0, hitLevel = 0, duckLevel = 0;

        int level = _player.getLevel();
        EVocation vocation = _player.getVocation();

        stamina += CEService.playerBaseAttribute(level, vocation.getStaminaCalPara());
        inte += CEService.playerBaseAttribute(level, vocation.getInteCalcPara());

        strength += CEService.playerBaseAttribute(level, vocation.getStrengthCalcPara());
        spirit += CEService.playerBaseAttribute(level, vocation.getSpiritCalcPara());
        lucky += CEService.playerBaseAttribute(level, vocation.getLuckyCalcPara());
        agility += CEService.playerBaseAttribute(level, vocation.getAgilityCalcPara());
        //edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
        physicsDeathblowLevel = CEService.physicsDeathblowLevel(agility, lucky);
        magicDeathblowLevel = CEService.magicDeathblowLevel(inte, lucky);
        hitLevel = CEService.hitLevel(lucky);
        duckLevel = CEService.duckLevel(agility, lucky);

        EquipmentInstance[] equipmentList = _player.getBodyWear().getEquipmentList();
        _player.getBaseProperty().getMagicFastnessList().clear();

        for (EquipmentInstance e : equipmentList)
        {
            if (null != e && e.getCurrentDurabilityPoint() > 0)
            {
                maxHP += e.getArchetype().atribute.hp;
                maxHP += e.getArchetype().atribute.hp
                        * e.getGeneralEnhance().getBasicModulus();
                maxMP += e.getArchetype().atribute.mp;
                maxMP += e.getArchetype().atribute.mp
                        * e.getGeneralEnhance().getBasicModulus();
                stamina += e.getArchetype().atribute.stamina;
                stamina += e.getArchetype().atribute.stamina
                        * e.getGeneralEnhance().getBasicModulus();
                inte += e.getArchetype().atribute.inte;
                inte += e.getArchetype().atribute.inte
                        * e.getGeneralEnhance().getBasicModulus();
                strength += e.getArchetype().atribute.strength;
                strength += e.getArchetype().atribute.strength
                        * e.getGeneralEnhance().getBasicModulus();
                spirit += e.getArchetype().atribute.spirit;
                spirit += e.getArchetype().atribute.spirit
                        * e.getGeneralEnhance().getBasicModulus();
                lucky += e.getArchetype().atribute.lucky;
                lucky += e.getArchetype().atribute.lucky
                        * e.getGeneralEnhance().getBasicModulus();
                agility += e.getArchetype().atribute.agility;
                agility += e.getArchetype().atribute.agility
                        * e.getGeneralEnhance().getBasicModulus();
                defense += e.getArchetype().atribute.defense;
                defense += e.getArchetype().atribute.defense
                        * e.getGeneralEnhance().getDefenseModulus();

                physicsDeathblowLevel += e.getArchetype().atribute.physicsDeathblowLevel;
                physicsDeathblowLevel += e.getArchetype().atribute.physicsDeathblowLevel
                        * e.getGeneralEnhance().getAdjuvantModulus();
                magicDeathblowLevel += e.getArchetype().atribute.magicDeathblowLevel;
                magicDeathblowLevel += e.getArchetype().atribute.magicDeathblowLevel
                        * e.getGeneralEnhance().getAdjuvantModulus();
                hitLevel += e.getArchetype().atribute.hitLevel;
                hitLevel += e.getArchetype().atribute.hitLevel
                        * e.getGeneralEnhance().getAdjuvantModulus();
                duckLevel += e.getArchetype().atribute.duckLevel;
                duckLevel += e.getArchetype().atribute.duckLevel
                        * e.getGeneralEnhance().getDefenseModulus();

                _player.getBaseProperty().getMagicFastnessList().add(
                        e.getArchetype().getMagicFastnessList());
                _player.getBaseProperty().getMagicFastnessList().add(
                        e.getArchetype().getMagicFastnessList(),
                        e.getGeneralEnhance().getDefenseModulus());
            }
        }

        short suiteID = _player.getBodyWear().hasSuite();

        if (suiteID != 0)
        {
            SuiteEData suiteEData = SuiteEquipmentDataDict.getInstance()
                    .getSuiteData(suiteID);

            if (null != suiteEData)
            {
                if (null != suiteEData.atribute)
                {
                    maxHP += suiteEData.atribute.hp;
                    maxMP += suiteEData.atribute.mp;
                    stamina += suiteEData.atribute.stamina;
                    inte += suiteEData.atribute.inte;
                    strength += suiteEData.atribute.strength;
                    spirit += suiteEData.atribute.spirit;
                    lucky += suiteEData.atribute.lucky;
                    agility += suiteEData.atribute.agility;
                    defense += suiteEData.atribute.defense;
                    physicsDeathblowLevel += suiteEData.atribute.physicsDeathblowLevel;
                    magicDeathblowLevel += suiteEData.atribute.magicDeathblowLevel;
                    hitLevel += suiteEData.atribute.hitLevel;
                    duckLevel += suiteEData.atribute.duckLevel;
                }

                _player.getBaseProperty().getMagicFastnessList().add(
                        suiteEData.fastnessList);
            }
        }

        defense += CEService.defenseBySpirit(spirit, vocation
                .getPhysicsDefenceSpiritPara());

        _player.getBaseProperty().setStamina(stamina);
        _player.getBaseProperty().setInte(inte);
        _player.getBaseProperty().setStrength(strength);
        _player.getBaseProperty().setSpirit(spirit);
        _player.getBaseProperty().setLucky(lucky);
        _player.getBaseProperty().setAgility(agility);
        _player.getBaseProperty().setDefense(defense);
        _player.getBaseProperty().setPhysicsDeathblowLevel(
                physicsDeathblowLevel);
        _player.getBaseProperty().setMagicDeathblowLevel(magicDeathblowLevel);
        _player.getBaseProperty().setHitLevel(hitLevel);
        _player.getBaseProperty().setPhysicsDuckLevel(duckLevel);
        //edit by zhengl; date: 2011-02-24; note: 添加系数进HP计算公式
        maxHP += CEService.hpByStamina(stamina, level, _player.getObjectLevel().getHpCalPara());

        maxMP += CEService.mpByInte(inte, level, EObjectLevel.NORMAL
                .getMpCalPara());

        _player.getBaseProperty().setHpMax(maxHP);
        _player.getBaseProperty().setMpMax(maxMP);

        Weapon weapon = null;
        EquipmentInstance ei = _player.getBodyWear().getWeapon();

        int weaponMinMagicHarm = 0, weaponMaxMagicHarm = 0;
        int maxPhysicsAttack, minPhysicsAttack;

        if (null != ei)
        {
            weapon = (Weapon) ei.getArchetype();

            if (ei.getCurrentDurabilityPoint() > 0)
            {
                _player.setAttackRange(weapon.getAttackDistance());
                _player.setBaseAttackImmobilityTime((int) (weapon.getImmobilityTime() * 1000F));

                if (null != ei.getMagicDamage())
                {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }

                maxPhysicsAttack = CEService.maxPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), ei
                                .getWeaponMaxPhysicsAttack(),
                        weaponMaxMagicHarm, weapon.getImmobilityTime(),
                        EObjectLevel.NORMAL.getPhysicsAttckCalPara());
                minPhysicsAttack = CEService.minPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), ei
                                .getWeaponMinPhysicsAttack(),
                        weaponMinMagicHarm, weapon.getImmobilityTime(),
                        EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            }
            else
            {
                _player.setAttackRange(Constant.DEFAULT_ATTACK_DISTANCE);
                _player
                        .setBaseAttackImmobilityTime(Constant.DEFAULT_IMMOBILITY_TIME * 1000);

                maxPhysicsAttack = CEService.maxPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), 0, 0,
                        Constant.DEFAULT_IMMOBILITY_TIME, EObjectLevel.NORMAL
                                .getPhysicsAttckCalPara());
                minPhysicsAttack = CEService.minPhysicsAttack(strength,
                        agility, vocation.getPhysicsAttackParaA(), vocation
                                .getPhysicsAttackParaB(), vocation
                                .getPhysicsAttackParaC(), 0, 0,
                        Constant.DEFAULT_IMMOBILITY_TIME, EObjectLevel.NORMAL
                                .getPhysicsAttckCalPara());
            }
        }
        else
        {
            _player.setAttackRange(Constant.DEFAULT_ATTACK_DISTANCE);
            _player.setBaseAttackImmobilityTime(Constant.DEFAULT_IMMOBILITY_TIME * 1000);

            maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility,
                    vocation.getPhysicsAttackParaA(), vocation
                            .getPhysicsAttackParaB(), vocation
                            .getPhysicsAttackParaC(), 0, 0,
                    Constant.DEFAULT_IMMOBILITY_TIME, EObjectLevel.NORMAL
                            .getPhysicsAttckCalPara());
            minPhysicsAttack = CEService.minPhysicsAttack(strength, agility,
                    vocation.getPhysicsAttackParaA(), vocation
                            .getPhysicsAttackParaB(), vocation
                            .getPhysicsAttackParaC(), 0, 0,
                    Constant.DEFAULT_IMMOBILITY_TIME, EObjectLevel.NORMAL
                            .getPhysicsAttckCalPara());
        }

        _player.getBaseProperty().setMaxPhysicsAttack(maxPhysicsAttack);
        _player.getBaseProperty().setMinPhysicsAttack(minPhysicsAttack);

        int baseMagicHarmByInte = CEService.magicHarmByInte(inte);

        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SANCTITY,
                baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.UMBRA,
                baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.FIRE,
                baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.WATER,
                baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SOIL,
                baseMagicHarmByInte);

        if (null != weapon && ei.getCurrentDurabilityPoint() > 0)
        {
            MagicDamage weaponMagicDamage = ei.getMagicDamage();

            if (null != weaponMagicDamage)
            {
            	//edit by zhengl; date: 2011-04-27; note: 武器伤害已经设置为ALL类型魔法伤害.
                int magicHarmValue =
                	(weaponMagicDamage.minDamageValue + weaponMagicDamage.maxDamageValue) / 2;
                _player.getBaseProperty().getBaseMagicHarmList().reset(
                        weaponMagicDamage.magic, magicHarmValue);
            }
        }

        _player.clearHatredModulus();
        _player.getActualProperty().clearNoneBaseProperty();
        _player.getResistOddsList().clear();

        // 计算被动技能对基础属性的影响
        SkillServiceImpl.getInstance().changePropertySkillAction(_player, true);

        _player.getActualProperty().getMagicFastnessList().reset(
                _player.getBaseProperty().getMagicFastnessList());
        _player.getActualProperty().getBaseMagicHarmList().reset(
                _player.getBaseProperty().getBaseMagicHarmList());
        _player.getActualProperty().setMaxPhysicsAttack(
                _player.getBaseProperty().getMaxPhysicsAttack());
        _player.getActualProperty().setMinPhysicsAttack(
                _player.getBaseProperty().getMinPhysicsAttack());
        _player.setActualAttackImmobilityTime(_player.getBaseAttackImmobilityTime());

        _player.getActualProperty().setStamina(
                _player.getBaseProperty().getStamina());
        _player.getActualProperty()
                .setInte(_player.getBaseProperty().getInte());
        _player.getActualProperty().setStrength(
                _player.getBaseProperty().getStrength());
        _player.getActualProperty().setSpirit(
                _player.getBaseProperty().getSpirit());
        _player.getActualProperty().setLucky(
                _player.getBaseProperty().getLucky());
        _player.getActualProperty().setAgility(
                _player.getBaseProperty().getAgility());
        _player.getActualProperty().setDefense(
                _player.getBaseProperty().getDefense());
        _player.getActualProperty().setPhysicsDeathblowLevel(
                _player.getBaseProperty().getPhysicsDeathblowLevel());
        _player.getActualProperty().setMagicDeathblowLevel(
                _player.getBaseProperty().getMagicDeathblowLevel());
        _player.getActualProperty().setHitLevel(
                _player.getBaseProperty().getHitLevel());
        _player.getActualProperty().setPhysicsDuckLevel(
                _player.getBaseProperty().getPhysicsDuckLevel());

        _player.getActualProperty().setHpMax(
                _player.getBaseProperty().getHpMax());
        _player.getActualProperty().setMpMax(
                _player.getBaseProperty().getMpMax());

        // 计算静态效果对基础属性的影响
        EffectServiceImpl.getInstance().staticEffectAction(_player);

        if (_player.getHp() > _player.getActualProperty().getHpMax())
        {
            _player.setHp(_player.getActualProperty().getHpMax());
        }

        if (_player.getMp() > _player.getActualProperty().getMpMax())
        {
            _player.setMp(_player.getActualProperty().getMpMax());
        }

        _player.resetHpResumeValue(CEService.hpResumeAuto(_player.getLevel(),
                _player.getActualProperty().getSpirit(), _player.getVocation()
                        .getStaminaCalPara()));
        _player.resetMpResumeValue(CEService.mpResumeAuto(_player.getLevel(),
                _player.getActualProperty().getSpirit(), _player.getVocation()
                        .getInteCalcPara()));
        _player.resetMpResumeValueAtFight(CEService
                .mpResumeAutoInFighting(_player.getMpResumeValue()));

    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.AbstractService#dbUpdate(int)
     */
    @Override
    public void dbUpdate (int _userID)
    {
        HeroPlayer player = playerTableByUserID.get(_userID);

        if (null != player && player.isEnable())
        {
            dao.updateDB(player);
            player.needUpdateDB = false;
        }
    }

    /**
     * 更新玩家数据库基础信息表
     *
     * @param _player
     * @return
     */
    public boolean dbUpdate (HeroPlayer _player)
    {
        if (null != _player)
        {
            dao.updateDB(_player);
            _player.needUpdateDB = false;
            _player.setLastTimeOfUPdateDB(System.currentTimeMillis());

            return true;
        }

        return false;
    }

    /**
     * 玩家是否新手状态
     * @param _player
     * @return
     */
    public boolean getNovice (HeroPlayer _player)
    {
    	boolean result = false;
        if (null != _player)
        {
        	if(PlayerDAO.getNovice(_player.getUserID()) == 1)
        	{
        		result =  true;
        	}
        }
        return result;
    }
    /**
     * 脱离新手状态
     * @param _player
     */
    public void leaveNovice (HeroPlayer _player)
    {
    	PlayerDAO.updateNovice(_player.getUserID());
    }
    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.player.IPlayerService#createRole(int, short,
     *      java.lang.String[])
     */
    public byte[] createRole (int accountID, short serverID, int _userID,
            String[] paras)
    {
        return dao.createRole(accountID, serverID, _userID, paras);
    }

//    public byte[] listDefaultRole () {
//    	ME2OutputStream outPipe = new ME2OutputStream();
//    	log.info("触发listDefaultRole:"+this.toString());
//    	try {
//    		PlayerConfig config = PlayerServiceImpl.getInstance().getConfig();
//    		//4职业
//    		outPipe.writeByte(4);
//    		for (int i = 0; i < 4; i++) {
//    			outPipe.writeByte(i+1); //职业
//    			outPipe.writeShort(-1); //头
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //头发
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //头盔
//    			outPipe.writeShort(-1);
//    			outPipe.writeByte(0);
//    			outPipe.writeShort(-1); //衣服
//    			outPipe.writeShort(-1);
//    			outPipe.writeByte(0);
//    			outPipe.writeShort(-1); //腿
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //武器
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //宠物
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //套装
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //武器攻击
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //武器发光
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //死亡
//    			outPipe.writeShort(-1);
//    			outPipe.writeShort(-1); //尾巴
//    			outPipe.writeShort(-1);
//			}
//
//			outPipe.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		byte[] rtnValue = outPipe.getBytes();
//    	return rtnValue;
//    }

    public byte[] listDefaultRole () {
    	YOYOOutputStream outPipe = new YOYOOutputStream();
    	log.info("触发listDefaultRole:"+this.toString());
    	try {
    		PlayerConfig config = PlayerServiceImpl.getInstance().getConfig();
    		outPipe.writeByte(EClan.getValues().length); //循环次数*2
    		for (EClan clan : EClan.getValues()) {
    			for (ESex sex : ESex.values()) {
        			outPipe.writeByte( sex.value() );
        			outPipe.writeByte( clan.getID() );
        			outPipe.writeShort(config.getLimbsConfig().getHeadImage(sex)); //头
        			outPipe.writeShort(config.getLimbsConfig().getHeadAnimation(sex));
        			outPipe.writeShort(config.getLimbsConfig().getHairImage(sex, clan)); //头发
        			outPipe.writeShort(config.getLimbsConfig().getHairAnimation(sex, clan));
        			outPipe.writeShort(config.getLimbsConfig().getLegImage(sex)); //腿
        			outPipe.writeShort(config.getLimbsConfig().getLegAnimation(sex));
        			outPipe.writeShort(config.getLimbsConfig().getTailImage(sex, clan)); //尾巴
        			outPipe.writeShort(config.getLimbsConfig().getTailAnimation(sex, clan));
        			outPipe.writeShort(config.getLimbsConfig().getDieImage(clan)); //死亡
        			outPipe.writeShort(config.getLimbsConfig().getDieAnimation(clan));
				}
			}
    		outPipe.writeByte(EVocationType.values().length); //几个职业
    		for (EVocationType type : EVocationType.values()) {
    			outPipe.writeByte(type.getID()); //职业
    			outPipe.writeShort( config.getInitArmorImageGroup(type)[0] );//头盔
//    			log.info("头盔"+config.getInitArmorImageGroup(type)[0]);
    			outPipe.writeShort( config.getInitArmorImageGroup(type)[1]);
//    			log.info("头盔"+config.getInitArmorImageGroup(type)[1]);
    			outPipe.writeByte( config.getInitArmorImageGroup(type)[2] );
//    			log.info("头盔"+config.getInitArmorImageGroup(type)[2]);
    			outPipe.writeShort( config.getInitArmorImageGroup(type)[3] );//衣服
//    			log.info("衣服"+config.getInitArmorImageGroup(type)[3]);
    			outPipe.writeShort( config.getInitArmorImageGroup(type)[4]);
//    			log.info("衣服"+config.getInitArmorImageGroup(type)[4]);
    			outPipe.writeByte( config.getInitArmorImageGroup(type)[5] );
//    			log.info("衣服"+config.getInitArmorImageGroup(type)[5]);
    			outPipe.writeShort(-1); //初始化角色的时候不可能有衣服强化光效所以下发-1
    			outPipe.writeShort(-1); //初始化角色的时候不可能有衣服强化光效所以下发-1

    			outPipe.writeShort(config.getInitWeaponImageGroup(type)[0]); //武器
//    			log.info("武器"+config.getInitWeaponImageGroup(type)[0]);
    			outPipe.writeShort(config.getInitWeaponImageGroup(type)[1]);
//    			log.info("武器"+config.getInitWeaponImageGroup(type)[1]);
    			outPipe.writeShort(-1); //宠物
    			outPipe.writeShort(-1);
    			outPipe.writeShort(-1); //套装
    			outPipe.writeShort(-1);
    			outPipe.writeShort(config.getInitWeaponImageGroup(type)[2]); //武器攻击
    			outPipe.writeShort(config.getInitWeaponImageGroup(type)[3]);
    			outPipe.writeShort(-1); //武器强化发光
    			outPipe.writeShort(-1);
			}
			outPipe.flush();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("listDefaultRole error : ",e);
		}
//		log.debug("listDefaultRole end ...@# ");
		byte[] rtnValue = outPipe.getBytes();
    	return rtnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.player.IPlayerService#deleteRole(int)
     */
    public int deleteRole (int _userID)
    {
        GuildServiceImpl.getInstance().deleteRole(_userID);
        SocialServiceImpl.getInstance().deleteRole(_userID);
        TeachService.delteRole(_userID);
        LetterService.getInstance().deleteRole(_userID);
        MailService.getInstance().deleteRole(_userID);

        ServiceManager.getInstance().clean(_userID);

        return dao.deleteRole(_userID);
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.player.IPlayerService#listRole(int[])
     */
    public byte[] listRole (int[] userIDs)
    {
    	for(int userid : userIDs){
    		log.debug("listRole userid = " + userid);
    	}
        return dao.listRole(userIDs);
    }

    /**
     * 因为等级变化引起的玩家属性更新
     * @param _playerUserID
     * @param _level
     * @param _vocation
     * @param _money
     * @param _exp
     */
    public void updateLevel (int _playerUserID, short _level, EVocation _vocation,
            int _money, int _exp)
    {
    	dao.updateLevel(_playerUserID, _level, _vocation, _money, _exp);
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.player.IPlayerService#getPlayerByMQIdx(int)
     */
    public HeroPlayer getPlayerBySessionID (int _sessionID)
    {
        return sessionIDKeyPlayerTable.get(_sessionID);
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.player.IPlayerService#getPlayerByUID(int)
     */
    public HeroPlayer getPlayerByUserID (int _userID)
    {
        if (_userID <= 0)
        {
            return null;
        }

        return playerTableByUserID.get(_userID);
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.player.IPlayerService#getPlayerByName(java.lang.String)
     */
    public HeroPlayer getPlayerByName (String _name)
    {
        HeroPlayer player;

        for (int i = 0; i < playerList.size(); i++)
        {
            try
            {
                player = playerList.get(i);
            }
            catch (Exception e)
            {
                return null;
            }

            if (player.isEnable() && player.getName().equals(_name))
            {
                return player;
            }
        }

        return null;
    }

    public FastList<HeroPlayer> getPlayerList ()
    {
        return playerList;
    }

    public FastMap<Integer, HeroPlayer> getUserIDPlayerList ()
    {
        return playerTableByUserID;
    }

    public FastMap<Integer, HeroPlayer> getSessionPlayerList ()
    {
        return sessionIDKeyPlayerTable;
    }
    /**
     * 根据种族返回玩家列表
     * @param clan
     * @return
     */
    public FastList<HeroPlayer> getPlayerListByClan (EClan clan)
    {
    	FastList<HeroPlayer> list = new FastList<HeroPlayer>();
    	HeroPlayer player = null;
    	for (int i = 0; i < playerList.size(); i++) {
    		player = playerList.get(i);
    		if(player.getClan() == clan) {
    			list.add(player);
    		}
		}
        return list;
    }

    /**
     * 获取在线人数
     *
     * @return
     */
    public int getPlayerNumber ()
    {
        return sessionIDKeyPlayerTable.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see me2.service.basic.player.IPlayerService#getDAO()
     */
    public IPlayerDAO getDAO ()
    {
        return dao;
    }

    /**
     * 给角色添加金钱
     *
     * @param _player 角色
     * @param _money 金钱
     * @param _drawLocation 客户端提示绘制位置0：不绘制，1：主界面飘出，2：中间红字
     * @param _cause 变化原因
     */
    public boolean addMoney (HeroPlayer _player, int _money, float _expModulus,
            int _drawLocation, String _cause)
    {
        _money *= _expModulus;

        if (_money > 0)
        {
            if (_player.getMoney() + _money <= Constant.INTEGER_MAX_VALUE)
            {
                _player.addMoney(_money);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new MoneyChangeNofity(_money, _drawLocation));

                // 金币变化日志
                LogServiceImpl.getInstance().moneyChangeLog(
                        _player.getLoginInfo().accountID, _player.getUserID(),
                        _player.getName(), _player.getLoginInfo().loginMsisdn,
                        _cause, _money);

                return true;
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning("金钱超过上限"));

                return false;
            }
        }
        else if (_money < 0)
        {
            if (_player.getMoney() + _money >= 0)
            {
                _player.addMoney(_money);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new MoneyChangeNofity(_money, _drawLocation));

                // 金币变化日志
                LogServiceImpl.getInstance().moneyChangeLog(
                        _player.getLoginInfo().accountID, _player.getUserID(),
                        _player.getName(), _player.getLoginInfo().loginMsisdn,
                        _cause, _money);

                return true;
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning("金钱不夠"));

                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * 给角色添加经验
     *
     * @param _player 角色
     * @param _exp 经验值
     * @param _expModulus 倍数
     * @param _drawLocation 客户端提示绘制位置0：不绘制，1：主界面飘出，2：中间红字
     */
    public void addExperience (HeroPlayer _player, int _exp, float _expModulus,
            int _drawLocation)
    {
        if (_exp > 0)
        {
            _exp *= _expModulus;

            if (_player.addExp(_exp))
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new ExperienceAddNotify(
                        		_exp, _drawLocation, _player.getExp(), _player.getExpShow()));
            }
        }
    }



    /**
     * 角色升级后的处理
     *
     * @param _player
     */
    public void roleUpgrade (HeroPlayer _player)
    {
        //仅恋人或夫妻两人组队练级时，每当有人升级，得到 500 爱情度
        if(_player.getGroupID() > 0 && _player.spouse != null && _player.spouse.trim().length()>0){
            HeroPlayer spouser = getPlayerByName(_player.spouse);
            if(spouser != null && spouser.getGroupID() > 0 && _player.getGroupID() == spouser.getGroupID()){
                Group group = GroupServiceImpl.getInstance().getGroup(_player.getGroupID());
                if(group != null && group.getMemberNumber() == 2){
                    _player.addLoverValue(LoverServiceImpl.ADD_LOVER_VALUE_FOR_UPGRADE);
                    spouser.addLoverValue(LoverServiceImpl.ADD_LOVER_VALUE_FOR_UPGRADE);
                }
            }
        }

        dbUpdate(_player);
        reCalculateRoleProperty(_player);

        _player.setHp(_player.getBaseProperty().getHpMax());

        //edit by zhengl; date: 2011-02-16; note: 仙境物理职业也使用MP
        int maxMp = _player.getActualProperty().getMpMax();
//        if (_player.getVocation().getType() == EVocationType.PHYSICS)
//        {
//            _player.setForceQuantity(50);
//        }
//        else
//        {
//            _player.setMp(_player.getActualProperty().getMpMax());
//        }
        _player.setMp(maxMp);

        refreshRoleProperty(_player);
//        int forceQuantityOrMaxMp =
//        	_player.getVocation().getType() == EVocationType.PHYSICS ? _player.getForceQuantity()
//        			: _player.getActualProperty().getMpMax();
        RoleUpgradeNotify msg = new RoleUpgradeNotify(
        		_player.getID(),
        		_player.getLevel(),
        		_player.getActualProperty().getHpMax(),
        		maxMp,
        		_player.surplusSkillPoint);
        //end

        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);

        MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg,
                true, _player.getID());

        TaskServiceImpl.getInstance().notifyMapNpcTaskMark(_player,
                _player.where());
//        HelpService.helpTip(_player);
//        TeachService.finishedStudy(_player);  不能强制出师
        GroupServiceImpl.getInstance().refreshMemberLevel(_player);

        /**
         * 推送升级信息，插入队列
         */
//        ResponseToGmTool rtgt = new ResponseToGmTool(
//                EResponseType.SEND_ROLE_LVL_UPDATE, 0);
//        rtgt.setRoleLvlUpdate(_player.getName(), _player.getLevel(), _player
//                .getVocation().getDesc());
//        GmServiceImpl.addGmToolMsg(rtgt);
        // GameMasterServiceImpl.getInstance().sendRoleLvlUpdate(
        // _player.getName(), _player.getLevel(),
        // _player.getVocation().getDesc());
    }

    /**
     * 修改玩家属性
     *
     * @param _roleUserID
     * @param _level
     * @param _vocationID
     * @param _money
     * @param _exp
     * @return 失败信息
     */
    public String changePlayerProperty (int _roleUserID, short _level,
            byte _vocationID, int _money, int _exp)
    {
        if (_level > config.max_level)
        {
            return "等级超出最大值";
        }

        EVocation vocation = EVocation.getVocationByID(_vocationID);

        if (null == vocation)
        {
            return "不存在的职业";
        }

        if (_money < 0)
        {
            return "金钱不能为负数";
        }

        if (_exp < 0)
        {
            return "经验值不能为负数";
        }

        HeroPlayer player = getPlayerByUserID(_roleUserID);

        if (null != player)
        {
            if (_level == player.getLevel() && vocation == player.getVocation()
                    && _money == player.getMoney() && _exp == player.getExp())
            {
                return "属性未发生任何变化，无须修改";
            }

            if (_level != player.getLevel())
            {
                player.setLevel(_level);
                player.setUpgradeNeedExp(CEService.totalUpgradeExp(player
                        .getLevel()));

                reCalculateRoleProperty(player);

                if (player.isEnable())
                {
                    refreshRoleProperty(player);

                    RoleUpgradeNotify msg = new RoleUpgradeNotify(
                            player.getID(),
                            player.getLevel(),
                            player.getActualProperty().getHpMax(),
                            player.getActualProperty().getMpMax(),
                            player.surplusSkillPoint);

                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                    MapSynchronousInfoBroadcast.getInstance().put(
                            player.where(), msg, true, player.getID());

                    TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player,
                            player.where());
                    HelpService.helpTip(player);
//                    TeachService.finishedStudy(player);  不能强制出师
                    GroupServiceImpl.getInstance().refreshMemberLevel(player);
                }
            }

            if (vocation != player.getVocation())
            {
                player.setVocation(vocation);
                refreshRoleProperty(player);

                if (player.isEnable())
                {
                    PlayerServiceImpl.getInstance().refreshRoleProperty(player);

                    VocationChangeNotify msg = new VocationChangeNotify(
                            player.getID(),
                            vocation.value(),
                            player.getActualProperty().getHpMax(),
                            player.getActualProperty().getMpMax());

                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

                    MapSynchronousInfoBroadcast.getInstance().put(
                            player.where(), msg, true, player.getID());

                    TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player,
                            player.where());
                    GroupServiceImpl.getInstance()
                            .refreshMemberVocation(player);
                }
            }

            if (_money != player.getMoney())
            {
                int moneyChange = _money - player.getMoney();
                player.addMoney(moneyChange);

                if (player.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new MoneyChangeNofity(moneyChange,
                                    MONEY_DRAW_LOCATION_NONE));
                }

                // 金币变化日志
                LogServiceImpl.getInstance().moneyChangeLog(
                        player.getLoginInfo().accountID, player.getUserID(),
                        player.getName(), player.getLoginInfo().loginMsisdn,
                        "GM修改", _money);
            }

            if (_exp != player.getExp())
            {
                if (_exp >= player.getUpgradeNeedExp())
                {
                    return "经验超出当前等级最大值";
                }

                player.setExp(_exp);

                if (player.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ExperienceAddNotify(_exp,
                                    MONEY_DRAW_LOCATION_NONE, player.getExp(), player.getExpShow()));
                }
            }
            else
            {
                if (player.getExp() >= player.getUpgradeNeedExp())
                {
                    player.setExp(player.getUpgradeNeedExp() - 1);

                    _exp = player.getExp();
                }
            }
        }
        else
        {
            int expOfUpgrade = CEService.totalUpgradeExp(_level);

            if (_exp >= expOfUpgrade)
            {
                _exp = expOfUpgrade - 1;
            }
        }

        dao.updateDB(_roleUserID, _level, vocation, _money, _exp);

        return null;
    }

    /**
     * 设置快捷键
     *
     * @param _userID
     * @param _shortcutKeyList
     */
    public void setShortcutKey (HeroPlayer _player, byte _shortcutKey,
            byte _shortcutKeyType, int _targetID)
    {
        int[][] shortcutKeyList = _player.getShortcutKeyList();

        for (int[] shortcutKey : shortcutKeyList)
        {
            if (shortcutKey[0] == _shortcutKeyType
                    && shortcutKey[1] == _targetID)
            {
                shortcutKey[0] = 0;
                shortcutKey[1] = 0;
            }
        }

        shortcutKeyList[_shortcutKey][0] = _shortcutKeyType;
        shortcutKeyList[_shortcutKey][1] = _targetID;

        dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);

        if (_shortcutKeyType == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS)
        {
            Goods goods = GoodsContents.getGoods(_targetID);
            if (null != goods && goods instanceof Medicament)
            {
                Medicament medicament = (Medicament) goods;
                CDUnit cd = null;
                if (medicament.getMaxCdTime() > 0)
                {
                    cd = _player.userCDMap
                            .get(medicament.getPublicCdVariable());
                    if (cd != null)
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new GoodsShortcutKeyChangeNotify(_shortcutKey,
                                        cd.getTimeBySec(), cd.getMaxTime(),
                                        (short) cd.getKey()));
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new GoodsShortcutKeyChangeNotify(_shortcutKey,
                                        0, medicament.getMaxCdTime(),
                                        (short) medicament
                                                .getPublicCdVariable()));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance()
                            .put(
                                    _player.getMsgQueueIndex(),
                                    new GoodsShortcutKeyChangeNotify(
                                            _shortcutKey, 0, 0,
                                            (short) medicament
                                                    .getPublicCdVariable()));
                }
            }
        }

        if (_shortcutKeyType == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS)
        {
            Goods goods = GoodsContents.getGoods(_targetID);
            if (null != goods && (goods instanceof Medicament || goods instanceof SpecialGoods))
            {
                //add by zhengl; date: 2011-03-08; note: 快捷键上绑定的消耗品的数量下发
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                		new HotKeySumByMedicament(_player));
			}
            if (null != goods && goods instanceof Medicament)
            {
                Medicament medicament = (Medicament) goods;
                CDUnit cd = null;
                if (medicament.getMaxCdTime() > 0)
                {
                    cd = _player.userCDMap
                            .get(medicament.getPublicCdVariable());
                    if (cd != null)
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new GoodsShortcutKeyChangeNotify(_shortcutKey,
                                        cd.getTimeBySec(), cd.getMaxTime(),
                                        (short) cd.getKey()));
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new GoodsShortcutKeyChangeNotify(_shortcutKey,
                                        0, medicament.getMaxCdTime(),
                                        (short) medicament
                                                .getPublicCdVariable()));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance()
                            .put(
                                    _player.getMsgQueueIndex(),
                                    new GoodsShortcutKeyChangeNotify(
                                            _shortcutKey, 0, 0,
                                            (short) medicament
                                                    .getPublicCdVariable()));
                }
            }
        }
    }

    /**
     * 技能快捷键升级
     *
     * @param _userID
     * @param _shortcutKeyList
     */
    public void upgradeShortcutKeySkill (HeroPlayer _player,
            int _lowLevelSkillID, int _highLevelSkillID)
    {
        int[][] shortcutKeyList = _player.getShortcutKeyList();

        for (int[] shortcutKey : shortcutKeyList)
        {
            if (shortcutKey[0] == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SKILL
                    && shortcutKey[1] == _lowLevelSkillID)
            {
                shortcutKey[1] = _highLevelSkillID;

                dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);

                return;
            }
        }
    }

    /**
     * 重设技能快捷键（吸取灵魂除外，编号-50）
     *
     * @param _userID
     * @param _shortcutKeyList
     */
    public void resetSkillShortcutKey (HeroPlayer _player)
    {
        boolean changed = false;

        for (int[] shortcutKey : _player.getShortcutKeyList())
        {
            if (shortcutKey[0] == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SKILL)
            {
                shortcutKey[0] = 0;
                shortcutKey[1] = 0;
                changed = true;
            }
        }

        if (changed)
            dao.updateShortcutKey(_player.getUserID(), _player.getShortcutKeyList());
    }

    /**
     * 设置行走键
     *
     * @param _userID
     * @param _shortcutKeyList
     */
    public void setKeyOfWalking (HeroPlayer _player, byte[] _shortcutKeys)
    {
        int[][] shortcutKeyList = _player.getShortcutKeyList();
        int scan = 0;

        for (int i = 0; i < shortcutKeyList.length; i++)
        {
            if (shortcutKeyList[i][0] == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM
                    && (shortcutKeyList[i][1] == 1
                            || shortcutKeyList[i][1] == 2
                            || shortcutKeyList[i][1] == 3 || shortcutKeyList[i][1] == 4))
            {
                shortcutKeyList[i][0] = 0;
                shortcutKeyList[i][1] = 0;
                shortcutKeyList[i + 13][0] = 0;
                shortcutKeyList[i + 13][1] = 0;

                scan++;

                if (scan == 4)
                {
                    break;
                }
            }
        }

        for (int i = 1; i <= 4; i++)
        {
            shortcutKeyList[_shortcutKeys[i - 1]][0] = PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM;
            shortcutKeyList[_shortcutKeys[i - 1]][1] = i;
            shortcutKeyList[_shortcutKeys[i - 1] + 13][0] = PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM;
            shortcutKeyList[_shortcutKeys[i - 1] + 13][1] = i;
        }

        dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);
    }

    /**
     * 删除快捷键
     *
     * @param _userID
     * @param _shortcutKeyList
     */
    public void deleteShortcutKey (HeroPlayer _player, byte _shortcutKeyType,
            int _targetID)
    {
        int[][] shortcutKeyList = _player.getShortcutKeyList();

        for (int[] shortcutKey : shortcutKeyList)
        {
            if (shortcutKey[0] == _shortcutKeyType
                    && shortcutKey[1] == _targetID)
            {
                shortcutKey[0] = 0;
                shortcutKey[1] = 0;

                dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);
            }
        }
    }

    /**
     * 获得快捷键
     *
     * @param _player
     * @param _skillID
     * @return
     */
    public byte getShortKey (int[][] _shortcutKeyList, byte _shortcutKeyType,
            int _targetID)
    {
        for (byte shortcutKey = 0; shortcutKey < 26; shortcutKey++)
        {
            if (_shortcutKeyList[shortcutKey][0] == _shortcutKeyType
                    && _shortcutKeyList[shortcutKey][1] == _targetID)
            {
                return shortcutKey;
            }
        }

        return -1;
    }

    /**
     * 刷新角色属性
     *
     * @param _player
     */
    public void refreshRoleProperty (HeroPlayer _player)
    {
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new RefreshRoleProperty(_player));
    }

    /**
     * 更新玩家自动出售品质
     *
     * @param _player
     * @param _newTrait
     */
    public void updateAutoSellTrait (HeroPlayer _player, EGoodsTrait _newTrait)
    {
        if (_player.getAutoSellTrait() != _newTrait)
        {
            _player.setAutoSellTrait(_newTrait);
            dao.updateAutoSellTrait(_player.getUserID(), _newTrait);
        }
    }

    /**
     * 根据指定昵称查询userID
     *
     * @param _nickname
     * @return
     */
    public int getUserIDByNameFromDB (String _nickname)
    {
        return PlayerDAO.getUserIDByName(_nickname);
    }

    /**
     * 更新玩家脱离师傅时间
     * @param player
     */
    public void updateLeftMasterTime(HeroPlayer player){
        player.leftMasterTime = System.currentTimeMillis();
        dao.updatePlayerLeftMasterTime(player.getUserID(),player.leftMasterTime);
    }

    /**
     * 加载离线角色的简单信息，供GMTOOL显示
     *
     * @param _userID
     * @return
     */
    public HeroPlayer getOffLinePlayerInfo (int _userID)
    {
        return (HeroPlayer) dao.loadOffLinePlayerToGmTool(_userID);
    }

    /**
     * 给玩家添加物品
     * @param userID
     * @param goodsID
     * @param number
     * @return 0成功  1没有此玩家失败  2:其它原因失败
     */
    public int GMAddGoodsForPlayer(int userID,int goodsID,int number){
        int res = 2;
        try{
            HeroPlayer player = getPlayerByUserID(userID);
            if(player == null){
                player = (HeroPlayer)dao.load(userID);
                if(player != null){
                    player.setInventory(new Inventory(player.getUserID(),
                            player.bagSizes));
                    GoodsDAO.loadPlayerGoods(player);
                }
            }
            if(player != null){
                GoodsServiceImpl.getInstance().addGoods2Package(player,goodsID,number, CauseLog.GMADD);
                res = 0;
            }else {
                res = 1;
            }
        }catch(Exception e){
            res = 2;
            log.error("给玩家添加物品 error: ",e);
        }
        return res;
    }

    /**
     * 给玩家添加点数
     * @param userID
     * @param point
     * @return
     */
    public int GMAddPointForPlayer(int userID,int point){
        int res = 2;
        try{
            HeroPlayer player = getPlayerByUserID(userID);
            if(player == null){
                player = this.getOffLinePlayerInfo(userID);
                PlayerDAO.loadPlayerAccountInfo(player);
            }
            if(player != null){
                if(point > 0){
                    String transID = ChargeServiceImpl.getInstance().getTransIDGen();
                    boolean addres = ChargeServiceImpl.getInstance().addPoint(player,transID,point,(byte)3,player.getLoginInfo().publisher, ServiceType.GM);
                    if(addres) res = 0;
                }
            }else {
                res = 1;
            }
        }catch(Exception e){
            res = 2;
            log.error("给玩家加点 error: ",e);
        }
        return res;
    }

    /**
     * GM修改玩家的等级、金钱、等级
     * @param userID
     * @param money
     * @param levle
     * @param loverValue
     * @return
     */
    public int GMModifyPlayerInfo(int userID,int money,int loverValue,int level,int skillPoint){
         int res = 2;
        try{
            HeroPlayer player = getPlayerByUserID(userID);
            if(player == null){
                player = this.getOffLinePlayerInfo(userID);
                PlayerDAO.loadPlayerAccountInfo(player);
            }
            if(player != null){
                log.debug("gm modify player info money="+money+",levle="+level+",skillpoint="+skillPoint+",lovervalue="+loverValue);

//                player.addMoney(money);
//                player.addLevel(level);
                player.surplusSkillPoint += skillPoint;
                if(player.isEnable()){
                    if(money > 0){
                        addMoney(player,money,1,MONEY_DRAW_LOCATION_NONE,CauseLog.GMADD.getName());
                    }
                    if(level > 0){
                        int currLevel = player.getLevel() + level;
                        if(currLevel > config.max_level){
                            player.addLevel(config.max_level-player.getLevel());
                        }else{
                            player.addLevel(level);
                        }

                        int exp = CEService.expToNextLevel(player.getLevel(), player.getUpgradeNeedExp());
                        player.setUpgradeNeedExp(exp);


                        reCalculateRoleProperty(player);

                        player.setHp(player.getBaseProperty().getHpMax());

                        int maxMp = player.getActualProperty().getMpMax();

                        player.setMp(maxMp);

                        refreshRoleProperty(player);

                        RoleUpgradeNotify msg = new RoleUpgradeNotify(
                                player.getID(),
                                player.getLevel(),
                                player.getActualProperty().getHpMax(),
                                maxMp,
                                player.surplusSkillPoint);

                        //GM给添加的等级，只有自己才能看见升级效果
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                    }
                    if(player.spouse != null && player.spouse.trim().length()>0){
                        player.addLoverValue(loverValue);
                    }
                    player.needUpdateDB = true;
                }else{
                    String spouse = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
                    if(null == spouse){
                        spouse = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                    }
                    if(spouse != null && spouse.trim().length()>0){
                        player.addLoverValue(loverValue);
                    }

                }
                dao.updateDB(player);
                res = 0;
            }else {
                res = 1;
            }
        }catch(Exception e){
            res = 2;
            log.error("GM修改玩家的等级、金钱、等级 error: ",e);
        }
        log.debug("gm modify player info end ...");
        return res;
    }

    /**
     * 根据昵称获取不在线玩家信息
     * @param name
     * @return
     */
    public HeroPlayer getOffLinePlayerInfoByName(String name){
        return dao.getOffLinePlayerByName(name);
    }

    /**
     * 查询玩家是否被禁言
     * 因为禁言是GM处理的，所以每次判断都要从数据库查询
     * @param _userID
     * @return
     */
    public boolean playerChatIsBlank(int accountID,int _userID){
        return dao.getChatBlankByUserID(accountID,_userID);
    }

    /**
     * 冻结角色
     * @param _userID
     * @param nickname
     * @param keepTime
     * @param startTime
     * @param endTime
     * @param memo
     * @return
     */
    public boolean setPlayerUserIDBlack(int _userID,String nickname,int keepTime,String startTime,String endTime,String memo){
        return dao.setPlayerUserIDBlank(_userID,nickname,keepTime,startTime,endTime,memo);
    }

    /**
     * 冻结账号
     * @param _accountID
     * @param username
     * @param keepTime
     * @param startTime
     * @param endTime
     * @param memo
     * @return
     */
    public boolean setPlayerAccountIDBlack(int _accountID,String username,int keepTime,String startTime,String endTime,String memo){
        return dao.setPlayerAccountIDBlank(_accountID,username,keepTime,startTime,endTime,memo);
    }

    /**
     * 角色禁言
     * @param _userID
     * @param nickname
     * @param keepTime
     * @param startTime
     * @param endTime
     * @param memo
     * @return
     */
    public boolean setPlayerChatBlack(int _userID,String nickname,int keepTime,String startTime,String endTime,String memo){
        return dao.setPlayerChatBlank(_userID,nickname,keepTime,startTime,endTime,memo);
    }

    /**
     * 解冻角色
     * @param _userID
     * @return
     */
    public boolean deletePlayerUserIDBlack(int _userID){
        return dao.deletePlayerUserIDBlack(_userID);
    }

    /**
     * 解冻账号
     * @param _accountID
     * @return
     */
    public boolean deletePlayerAccountIDBlack(int _accountID){
        return dao.deletePlayerAccountIDBlack(_accountID);
    }

    /**
     * 解禁禁言
     * @param _userID
     * @return
     */
    public boolean deletePlayerChatBlack(int _userID){
        return dao.deletePlayerChatBlack(_userID);
    }

    /**
     * 玩家基本信息更新任务
     *
     * @author DC
     */
    private class PlayerInfoUpdateTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         *
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
            PlayerDAO.updatePlayerInfo(playerList);
        }
    }

    /**
     * 刷新玩家接收循环任务次数
     * 每晚24时
     */
    private class ClearPlayerReceiveRepeateTaskTimes extends TimerTask{
        @Override
        public void run() {
            log.info("刷新玩家接收循环任务次数 start ...");
            PlayerDAO.clearPlayerReceiveRepeatTaskTimes();

            FastList<HeroPlayer> playerList = getPlayerList();
            for(HeroPlayer player : playerList){
                player.receivedRepeateTaskTimes = 0;
                player.setCanReceiveRepeateTaskTimes(5);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new NotifyPlayerReciveRepeateTaskTimes(player));
            }

            log.info("刷新玩家接收循环任务次数 end ...");
        }
    }

    /**
     * 玩家玩家对象清理任务
     *
     * @author DC
     */
    private class PlayerClearTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         *
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
            LogServiceImpl.getInstance().onlineNumLog(playerList.size());

            if (playerList.size() > 0)
            {
                HeroPlayer player;
                long now = System.currentTimeMillis();

                for (int i = 0; i < playerList.size();)
                {
                    try
                    {
                        player = playerList.get(i);

                        if (!player.isEnable() && player.offlineTime > 0)
                        {
                            if (now - player.offlineTime > PLAYER_OBJECT_KEEP_TIME)
                            {
                                log.info("清除玩家内存:" + player.getName());

                                List<Pet> petlist = PetServiceImpl.getInstance().getPetList(player.getUserID());
                                if(petlist != null){
                                    for(Pet pet : petlist){
                                        log.debug("清除玩家内存时保存宠物信息，petID= " + pet.id);
                                        PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);

                                    }
                                }

                                ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(player.getUserID());
                                ShareServiceImpl.getInstance().removePlayerFromRequestExchangeListByTarget(player.getUserID());

                                ServiceManager.getInstance().clean(player.getUserID());

                                playerList.remove(i);

                                //释放玩家对象资源
                                player.free();
                                player = null;

                                continue;
                            }
                        }

                        i++;
                    }
                    catch (Exception e)
                    {
                    	log.error("清除掉线玩家发生异常:",e);
                    	e.printStackTrace();
                        break;
                    }
                }
                //for end
            }

        }
    }

    /**
     * 爱情值排名每隔两小时更新一次
     * @author jiaodongjie
     *
     */
    private class PlayerLoverValueOrderTimer extends TimerTask{

		@Override
		public void run ()
		{
			loverValueOrderMap = dao.loverValueOrderMap();

		}

    }

    /**
     * 开始处理天书镶嵌
     * @param player
     * @param position
     * @param bookID
     */
    public void startInlayHeavenBook(HeroPlayer player,byte position,int bookID) throws BagException{
        SpecialGoods goods = (SpecialGoods)GoodsServiceImpl.getInstance().getGoodsByID(bookID);
        if(goods.getType() == ESpecialGoodsType.HEAVEN_BOOK){
            HeavenBook book = (HeavenBook)goods;
            book.setPosition(position);
            if(book.beUse(player,null,-1)){
                if(book.disappearImmediatelyAfterUse()){
                    goods.remove(player,(short)-1);
                }
            }
        }else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("你选择的不是天书"));
        }
        player.currInlayHeavenBookPosition = -1;
        player.currInlayHeavenBookID = 0;
    }

    /**
     * 镶嵌天书添加技能点
     * @param player
     * @param position(客户端传的值为0,1,2)
     * @param bookID
     * @return
     */
    public boolean inlayHeavenBook(HeroPlayer player, byte position, int bookID){
        short currentPoint = player.surplusSkillPoint;//当前技能点
        HeavenBook book = (HeavenBook)GoodsServiceImpl.getInstance().getGoodsByID(bookID);
        try{
            if(player.heaven_book_ids[position] > 0){
                HeavenBook oldBook = (HeavenBook)GoodsServiceImpl.getInstance().getGoodsByID(player.heaven_book_ids[position]);
                if(oldBook.getTrait().value() > book.getTrait().value()){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("不能用低品质的覆盖高品质的天书!"));
                    return false;
                }

                //把旧天书的技能点减掉
                player.surplusSkillPoint -= oldBook.getSkillPoint();
                if(player.heavenBookSame){
                    player.surplusSkillPoint -= oldBook.getComBonus();
                    player.heavenBookSame = false;
                }

                //加上新天书的技能点
                player.surplusSkillPoint += book.getSkillPoint();
                player.heaven_book_ids[position] = bookID;
                if(player.heaven_book_ids[0]>0
                            && player.heaven_book_ids[0] == player.heaven_book_ids[1]
                            && player.heaven_book_ids[0] == player.heaven_book_ids[2])
                {
                    player.heavenBookSame = true;
                    player.surplusSkillPoint += book.getComBonus();  //三本天书一样则加上额外奖励技能点
                }

            }else{
                player.surplusSkillPoint += book.getSkillPoint();
                player.heaven_book_ids[position] = bookID;
                if(player.heaven_book_ids[0]>0
                        && player.heaven_book_ids[0] == player.heaven_book_ids[1]
                        && player.heaven_book_ids[0] == player.heaven_book_ids[2])
                {
                    player.heavenBookSame = true;
                    player.surplusSkillPoint += book.getComBonus();
                }
            }

            PlayerDAO.updatePlayerHeavenBookID(player);

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("镶嵌天书成功"));

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new RefreshRoleProperty(player));

            return true;
        }catch(Exception e){
            player.surplusSkillPoint = currentPoint;
            log.error("inlayHeavenBook error : ",e);
            return false;
        }
    }

    /**
     * 更新玩家爱情值
     * @param userID
     * @param loverValue
     */
    public void updatePlayerLoverValue(int userID,int loverValue){
        dao.updatePlayerLoverValue(userID,loverValue);
    }

    /**
     * 获取玩家的天书增加的技能点
     * @param player
     * @return
     */
    public int getPlayerHeavenBookSkillPoint(HeroPlayer player){
        int point = 0;

        HeavenBook book = null;
        for(int i=0; i<player.heaven_book_ids.length; i++){
            if(player.heaven_book_ids[i]>0){
                book = (HeavenBook)GoodsServiceImpl.getInstance().getGoodsByID(player.heaven_book_ids[i]);
                if(book != null){
                    point += book.getSkillPoint();
                }
            }
        }

        if(player.heavenBookSame && book != null){
            point += book.getComBonus();
        }

        return point;
    }

    /**
     * 爱情值排名任务更新间隔
     */
    private final static int 	UPDATE_LOVER_VALUE_ORDER_INTERVAL	= 2 * 60 * 60 * 1000;

    /**
     * 更新玩家基本信息任务启动延时
     */
    private final static int             UPDATE_DB_DELAY                  = 55 * 1000;

    /**
     * 更新玩家基本信息任务检查间隔
     */
    private final static int             UPDATE_DB_INTERVAL               = 1 * 60 * 1000;

    /**
     * 清除玩家内存任务启动延时
     */
    private final static int             CLEAR_PLAYER_TASK_DELAY          = 140 * 1000;

    /**
     * 清除玩家内存任务检查间隔
     */
    private final static int             CLEAR_PLAYER_TASK_CHECK_INTERVAL = 1 * 60 * 1000;

    /**
     * 离线玩家对象在内存中的最长驻留时间
     */
    private final static int             PLAYER_OBJECT_KEEP_TIME          = 1 * 60 * 1000;

    /**
     * 快捷键类型——系统
     */
    public static final byte             SHUTCUT_KEY_TYPE_OF_SYSTEM       = 1;

    /**
     * 快捷键类型——技能
     */
    public static final byte             SHUTCUT_KEY_TYPE_OF_SKILL        = 2;

    /**
     * 快捷键类型——物品
     */
    public static final byte             SHUTCUT_KEY_TYPE_OF_GOODS        = 3;

    /**
     * 日期格式化器
     */
    public static final SimpleDateFormat DATE_FORMATTER                   = (SimpleDateFormat) SimpleDateFormat
                                                                                  .getDateTimeInstance();

    static
    {
        DATE_FORMATTER.applyPattern("yy-MM-dd HH:mm:ss:SS");
    }
}
