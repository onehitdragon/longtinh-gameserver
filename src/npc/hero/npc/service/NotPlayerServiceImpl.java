/**
 * Copyright: Copyright (c) 2008
 * <br>
 * Company: Digifun
 * <br>
 * Date: 2008-11-6
 */

package hero.npc.service;

import hero.expressions.service.CEService;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.item.Goods;
import hero.item.legacy.MonsterLegacyBox;
import hero.item.legacy.MonsterLegacyManager;
import hero.item.legacy.PersonalPickerBox;
import hero.item.legacy.RaidPickerBox;
import hero.item.legacy.TaskGoodsLegacyInfo;
import hero.item.message.LegacyBoxEmergeNotify;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.manufacture.Manufacture;
import hero.manufacture.ManufactureType;
import hero.manufacture.service.ManufactureServerImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.micro.teach.TeachService;
import hero.npc.ME2NotPlayer;
import hero.npc.Monster;
import hero.npc.Npc;
import hero.npc.ai.data.AIDataDict;
import hero.npc.ai.data.Changes;
import hero.npc.dict.*;
import hero.npc.dict.AnimalDataDict.AnimalData;
import hero.npc.dict.BoxDataDict.BoxData;
import hero.npc.dict.DoorPlateDataDict.DoorPlateData;
import hero.npc.dict.GearDataDict.GearData;
import hero.npc.dict.GroundTaskGoodsDataDict.GroundTaskGoodsData;
import hero.npc.dict.MonsterDataDict.MonsterData;
import hero.npc.dict.NpcDataDict.NpcData;
import hero.npc.dict.RoadPlateDataDict.RoadPlateData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.FunctionBuilder;
import hero.npc.function.system.auction.AuctionDict;
import hero.npc.function.system.exchange.TraderExchangeContentDict;
import hero.npc.function.system.postbox.MailService;
import hero.npc.function.system.trade.TraderSellContentDict;
import hero.npc.message.GroundTaskGoodsDisappearNofity;
import hero.npc.message.MonsterWalkNotify;
import hero.npc.message.NpcWalkNotify;
import hero.npc.others.Animal;
import hero.npc.others.Box;
import hero.npc.others.DoorPlate;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.RoadInstructPlate;
import hero.npc.others.TaskGear;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.player.service.PlayerServiceImpl;
import hero.share.EMagic;
import hero.share.EObjectType;
import hero.share.ESystemFeature;
import hero.share.EVocation;
import hero.share.message.Warning;
import hero.share.service.DataConvertor;
import hero.share.service.DateTool;
import hero.share.service.LogWriter;
import hero.task.service.TaskServiceImpl;

import java.util.*;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroundTaskGoodsEmergeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-3 下午05:07:33
 * @描述 ：
 */

public class NotPlayerServiceImpl extends AbsServiceAdaptor<NpcConfig> implements
        INotPlayerService
{
	private static Logger log = Logger.getLogger(NotPlayerServiceImpl.class);
	
	private static final int                  REFRESH_NEXT_ANSWER_QUESTION_INTERVAL = 1000;//100000;
    /**
     * 随机数生成器
     */
    private static final Random               random                            = new Random();

    /**
     * 单例
     */
    private static NotPlayerServiceImpl       instance;
    
    /**
     * 答题参与表
     */
    private Hashtable<Integer, ArrayList<Integer>> answerQuestionList; 
    
    /**
     * 按天题套刷新时间
     */
    private Hashtable<Integer, Long> answerQuestionDayRefreshList;
    /**
     * 按时间题套刷新状态
     */
    private Hashtable<Integer, Boolean> answerQuestionTimeRefreshList;

    /**
     * 以怪物ID为key的映射表
     */
    private FastMap<Integer, Monster>         monsterMapOfIDTable;

    /**
     * 以NPC ID为key的映射表
     */
    private FastMap<Integer, Npc>             npcMapOfIDTable;

    /**
     * 以NPC模板编号为key的映射表
     */
    private FastMap<String, Npc>              npcMapOfModelIDTable;

    /**
     * 小动物列表
     */
    private FastList<Animal>                  animalList;

    /**
     * 任务机关列表
     */
    private FastMap<Integer, TaskGear>        taskGearTable;

    /**
     * 地上看见的宝箱列表
     */
    private FastMap<Integer, Box>             boxTable;

    /**
     * 空宝箱列表
     */
    private FastList<Box>                     emptyBoxList;

    /**
     * 地上看见的任务物品列表
     */
    private FastMap<Integer, GroundTaskGoods> groundTaskGoodsTable;

    /**
     * 被拾取走的地上任务物品列表
     */
    private FastList<GroundTaskGoods>         emptyGroundTaskGoodsList;

    /**
     * NPC模板编号前缀
     */
    public static final String                NPC_MODEL_ID_PREFIX               = "n";

    /**
     * 怪物模板编号前缀
     */
    public static final String                MONSTER_MODEL_ID_PREFIX           = "m";

    /**
     * 小动物模板编号前缀
     */
    public static final String                ANIMAL_MODEL_ID_PREFIX            = "a";

    /**
     * 箱子模板编号前缀
     */
    public static final String                BOX_MODEL_ID_PREFIX               = "b";

    /**
     * 路牌模板编号前缀
     */
    public static final String                ROAD_PLATE_MODEL_ID_PREFIX        = "r";

    /**
     * 室内门牌模板编号前缀
     */
    public static final String                DOOR_PLATE_MODEL_ID_PREFIX        = "d";

    /**
     * 机关模板编号前缀
     */
    public static final String                GEAR_MODEL_ID_PREFIX              = "g";

    /**
     * 任务物品模板编号前缀
     */
    public static final String                GROUND_TASK_GOODS_MODEL_ID_PREFIX = "t";
    
    /**
     * 计时器
     */
    private Timer                             timer;

    private NotPlayerServiceImpl()
    {
        config = new NpcConfig();
        monsterMapOfIDTable = new FastMap<Integer, Monster>();
        npcMapOfIDTable = new FastMap<Integer, Npc>();
        npcMapOfModelIDTable = new FastMap<String, Npc>();
        animalList = new FastList<Animal>();
        taskGearTable = new FastMap<Integer, TaskGear>();
        boxTable = new FastMap<Integer, Box>();
        emptyBoxList = new FastList<Box>();
        groundTaskGoodsTable = new FastMap<Integer, GroundTaskGoods>();
        emptyGroundTaskGoodsList = new FastList<GroundTaskGoods>();
        answerQuestionList = new Hashtable<Integer, ArrayList<Integer>>();
        answerQuestionDayRefreshList = new Hashtable<Integer, Long>();
        answerQuestionTimeRefreshList = new Hashtable<Integer, Boolean>();
    }

    public static NotPlayerServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new NotPlayerServiceImpl();
        }
        return instance;
    }

    @Override
    protected void start ()
    {
        MonsterImageConfDict.init();
        MonsterImageDict.getInstance().load(config.MonsterImageHPath,
                config.MonsterImageLPath);
        MonsterDataDict.getInstance().load(config.MONSTER_DATA_PATH);

        NpcHelloContentDict.getInstance().load(config.NPC_HELLO_PATH);
        NpcDataDict.getInstance().load(config.NPC_DATA_PATH);
        NpcImageConfDict.init();
        NpcImageDict.getInstance();

        NpcFunIconDict.getInstance().load(config.npc_fun_icon_path);

        AnimalDataDict.getInstance().load(config.ANIMAL_DATA_PATH);
        AnimalImageDict.getInstance().load(config.ANIMAL_IMAGE_PATH);

        GearDataDict.getInstance().load(config.GEAR_DATA_PATH);
        RoadPlateDataDict.getInstance().load(config.ROAD_PLATE_DATA_PATH);
        DoorPlateDataDict.getInstance().load(config.door_plate_data_path);
        BoxDataDict.getInstance().load(config.BOX_DATA_PATH);

        GroundTaskGoodsDataDict.getInstance().load(
                config.TASK_GOODS_ON_MAP_DATA_PATH);

        AIDataDict.getInstance().load(config.MONSTER_SHOUT_DATA_PATH,
                config.MONSTER_CALL_DATA_PATH,
                config.MONSTER_CHANGES_DATA_PATH,
                config.MONSTER_DISAPPEAR_DATA_PATH,
                config.MONSTER_RUN_AWAY_DATA_PATH,
                config.MONSTER_SPECIAL_AI_DATA_PATH,
                config.MONSTER_SKILL_AI_DATA_PATH, config.MONSTER_AI_DATA_PATH);

        DungeonManagerDict.getInstance().load(config.dungeonManagerDataPath);

        TraderSellContentDict.getInstance().load(
                config.trader_sell_content_data_path);
        TraderExchangeContentDict.getInstance().load(
                config.trader_exchange_content_data_path);
        QuestionDict.getInstance().load(
        		config.npc_function_data_anwser_question, 
        		config.npc_function_data_question, 
        		config.npc_function_data_award);
        EvidenveGiftDict.getInstance().load(config.npc_function_data_evidenve_gift, 
        		config.npc_function_data_evidenve_award);
        AuctionDict.getInstance();

//        new Thread(new AnimalWalkThread()).start();//把小动物行走去掉，以后放到客户端判断 jiaodj 2011-05-06 16:51
        new Thread(new EmptyBoxThread()).start();
        new Thread(new BePickedGroundTaskGoodsThread()).start();
        timer = new Timer();
        timer.schedule(new RefreshQuestionManager(), 1000, REFRESH_NEXT_ANSWER_QUESTION_INTERVAL);
        
        int[] groups = QuestionDict.getInstance().getAnwserQuestionIDs();
        for (int i = 0; i < groups.length; i++) 
        {
        	answerQuestionList.put(groups[i], new ArrayList<Integer>());
        	answerQuestionDayRefreshList.put(groups[i], System.currentTimeMillis());
        	answerQuestionTimeRefreshList.put(groups[i], false);
		}
    }

    @Override
    public void createSession (Session _session)
    {
        MailService.getInstance().loadMail(_session.userID);
    }

    public void clean (int _userID)
    {
        MailService.getInstance().clear(_userID);
    }

    /**
     * 获取NPC实例
     * 
     * @param _npcID NPC编号
     * @return
     */
    public Npc getNpc (int _npcID)
    {
        return npcMapOfIDTable.get(_npcID);
    }

    /**
     * 根据NPC模板编号获取实例
     * 
     * @param _npcModelID NPC模板编号
     * @return
     */
    public Npc getNpc (String _npcModelID)
    {
        return npcMapOfModelIDTable.get(_npcModelID.toLowerCase());
    }
    
    /**
     * 调用传递:ENpcFunctionType.POST_BOX
     * @param _function
     * @return
     */
    public Npc getNpcByFunction (int _function)
    {
    	Npc npc = null;
    	Npc npcTemp = null;
    	for (int i = 0; i < npcMapOfModelIDTable.size(); i++) {
    		npcTemp = npcMapOfModelIDTable.get(i);
    		BaseNpcFunction function = npc.getFunction(_function); 
    		if(function != null) {
    			npc = npcTemp; 
    		}
		}
        return npc;
    }

    /**
     * 获取任务机关实例
     * 
     * @param _taskGearID 任务机关编号
     * @return
     */
    public TaskGear getTaskGear (int _taskGearID)
    {
        return taskGearTable.get(_taskGearID);
    }
    
    /**
     * 玩家进入答题步骤
     * @param _group
     * @param _userID
     */
    public void joinQuestion(int _group, int _userID)
    {
    	if (answerQuestionList.get(_group) != null) 
    	{
    		synchronized (answerQuestionList) 
    		{
    			answerQuestionList.get(_group).add(_userID);
			}
		}
    }
    
    /**
     * 是否处于活动时间内
     * @param _group
     * @return
     */
    public boolean isInTime(int _group)
    {
    	boolean startResult = false;
    	boolean endResult = false;
    	AnswerQuestionData data = QuestionDict.getInstance().getAnswerQuestionData(_group);
    	if(data.refreshType == QuestionDict.REFRESH_TYPE_DAY)
    	{
    		return true; //按天刷新类型不做此验证
    	}
		Date nowDate = new Date(System.currentTimeMillis());
		String startTime = DateTool.formatDate(nowDate) + " ";
		String endTime = startTime;
		for (int i = 0; i < data.refreshTimeSum; i++) 
		{
			startTime += data.startTime[i];
			endTime += data.endTime[i];
			
			Date startDate = DateTool.convertLongDate(startTime);
			if(System.currentTimeMillis() > startDate.getTime())
			{
				//满足第1个条件
				startResult = true;
			}
			Date endDate = DateTool.convertLongDate(endTime);
			if(System.currentTimeMillis() < endDate.getTime())
			{
				//满足第2个条件
				endResult = true;
			}
			if (startResult && endResult) {
				break;
			} else {
				//为下一组时间校验进行重置
				startResult = false;
				endResult = false;
			}
		}
		
		return startResult && endResult;
    }
    
    /**
     * 玩家是否已经在使用过该功能
     * @param _group
     * @param _userID
     * @return
     */
    public boolean isJoinQuestion(int _group, int _userID)
    {
    	boolean result = false;
    	synchronized (answerQuestionList) 
    	{
        	if (answerQuestionList.get(_group) != null) 
        	{
        		//这里的get得到的ArrayList有可能超过10万行.这样还是比较影响效率的.
        		/***考虑将来做优化.*/
        		result = answerQuestionList.get(_group).contains(_userID);
        	}
    	}
    	return result;
    }

    /**
     * 获取地上任务物品
     * 
     * @param _groundTaskGoodsID
     * @return
     */
    public GroundTaskGoods getGroundTaskGoodsTable (int _groundTaskGoodsID)
    {
        return groundTaskGoodsTable.get(_groundTaskGoodsID);
    }

    /**
     * 获取NPC模板数据
     * 
     * @param _npcModelID NPC模板编号
     * @return
     */
    public NpcData getNpcModelData (String _npcModelID)
    {
        return NpcDataDict.getInstance().getNpcData(_npcModelID);
    }

    /**
     * 根据非玩家对象模板编号获取名字
     * 
     * @param _modelID
     * @return
     */
    public String getNotPlayerNameByModelID (String _modelID)
    {
        if (_modelID.toLowerCase().startsWith(NPC_MODEL_ID_PREFIX))
        {
            NpcData npc = NpcDataDict.getInstance().getNpcData(_modelID);

            if (null != npc)
            {
                return npc.name;
            }
        }
        else if (_modelID.toLowerCase().startsWith(MONSTER_MODEL_ID_PREFIX))
        {
            MonsterData monster = MonsterDataDict.getInstance().getMonsterData(
                    _modelID);

            if (null != monster)
            {
                return monster.name;
            }
        }

        return null;
    }

    /**
     * 创建动物实例
     * 
     * @param _animalModelID 动物模型编号
     * @return
     */
    public Animal buildAnimalInstance (String _animalModelID)
    {
        AnimalData data = AnimalDataDict.getInstance().getAnimalData(
                _animalModelID);

        if (null != data)
        {
            Animal animal = new Animal(data);
            animalList.add(animal);

            return animal;
        }

        return null;
    }

    /**
     * 创建宝箱实例
     * 
     * @param _boxModelID 宝箱模型编号
     * @return
     */
    public Box buildBoxInstance (String _boxModelID)
    {
        BoxData data = BoxDataDict.getInstance().getBoxData(_boxModelID);

        if (null != data)
        {
            Box box = new Box(data);
            boxTable.put(box.getID(), box);

            return box;
        }

        return null;
    }

    /**
     * 创建任务机关实例
     * 
     * @param _gearModelID 动物模型编号
     * @return
     */
    public TaskGear buildGearInstance (String _gearModelID)
    {
        GearData data = GearDataDict.getInstance().getGearData(_gearModelID);

        if (null != data)
        {
            TaskGear gear = new TaskGear(data);
            taskGearTable.put(gear.getID(), gear);

            return gear;
        }

        return null;
    }

    /**
     * 创建任务物品容器实例
     * 
     * @param _taskGoodsModelID 模型编号
     * @return
     */
    public GroundTaskGoods buildGroundTaskGood (String _taskGoodsModelID)
    {
        GroundTaskGoodsData data = GroundTaskGoodsDataDict.getInstance()
                .getTaskGoodsData(_taskGoodsModelID);

        if (null != data)
        {
            GroundTaskGoods taskGoods = new GroundTaskGoods(data);
            groundTaskGoodsTable.put(taskGoods.getID(), taskGoods);

            return taskGoods;
        }

        return null;
    }

    /**
     * 创建路牌实例
     * 
     * @param _roadPlateModelID 路牌模板编号
     * @return
     */
    public RoadInstructPlate buildRoadPlate (String _roadPlateModelID)
    {
        RoadPlateData data = RoadPlateDataDict.getInstance().getRoadPlateData(
                _roadPlateModelID);

        if (null != data)
        {
            return new RoadInstructPlate(data);
        }

        return null;
    }

    /**
     * 创建室内门牌实例
     * 
     * @param _roadPlateModelID 室内门牌模板编号
     * @return
     */
    public DoorPlate buildDoorPlate (String _doorPlateModelID)
    {
        DoorPlateData data = DoorPlateDataDict.getInstance().getDoorPlateData(
                _doorPlateModelID);

        if (null != data)
        {
            return new DoorPlate(data);
        }

        return null;
    }

    /**
     * 拾取宝箱
     * 
     * @param _player
     * @param _boxID
     */
    public void pickBox (HeroPlayer _player, int _boxID)
    {
        Box box = boxTable.get(_boxID);

        if (null != box)
        {
            synchronized (box)
            {
                log.debug("box is need gather skill = " + box.isNeedGatherSkill());
                if(box.isNeedGatherSkill()){
                    List<Manufacture> manufactureList =
                            ManufactureServerImpl.getInstance().getManufactureListByUserID(_player.getUserID());
                    if(manufactureList != null){
                        log.debug("manuflist size = " + manufactureList.size());
                        boolean noStudyedGather = true;
                        for(Manufacture manuf : manufactureList){
                            log.debug("manuf type = " +manuf.getManufactureType());
                            if(manuf.getManufactureType() == ManufactureType.GRATHER){
                                noStudyedGather = false;
                            }
                        }
                        if(noStudyedGather){
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("没有学习过采集技能不能拾取"));
                            return;
                        }
                    }
                }
                HashMap<Goods, Integer> goodsListOfBox = box
                        .getActualGoodsTable();

                Iterator<Goods> goodsContent = goodsListOfBox.keySet()
                        .iterator();

                while (goodsContent.hasNext())
                {
                    Goods goods = goodsContent.next();

                    if (null != GoodsServiceImpl.getInstance()
                            .addGoods2Package(_player, goods,
                                    goodsListOfBox.get(goods), CauseLog.DROP))
                    {
                        goodsContent.remove();
                    }
                    else
                    {
                        break;
                    }
                }

                if (0 == goodsListOfBox.size())
                {
                    box.disappear();

                    box.where().getBoxList().remove(box);
                    boxTable.remove(box.getID());
                    emptyBoxList.add(box);
                }
            }
        }
    }

    /**
     * 创建怪物实例
     * 
     * @param _monsterModelID 怪物模型编号
     * @return
     */
    public Monster buildMonsterInstance (String _monsterModelID)
    {
    	log.debug("开始创建怪物实例... modelID  = " + _monsterModelID);
        Monster monster = null;
        
        try
        {
            MonsterData data = MonsterDataDict.getInstance().getMonsterData(
                    _monsterModelID.toLowerCase());
            log.debug("monster data = " + data);
            if (null != data)
            {
            	try {
            		monster = new Monster(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
//                monster = new Monster(data);

                if (data.isActive.equals("主动"))
                {
                    monster.setActiveAttackType(true);
                }
                else
                {
                    monster.setActiveAttackType(false);
                }

                if (null != data.isInDungeon && data.isInDungeon.equals("是"))
                {
                    monster.setInDungeon();
                }

                monster.setAttackRange(Short.parseShort(data.atkRange));
                monster.setActualAttackImmobilityTime((int) (1000 * Float
                        .parseFloat(data.immobilityTime)));

                if (null != data.assistAttackRange)
                {
                    monster.setAttackChain(Integer
                            .parseInt(data.assistAttackRange), Integer
                            .parseInt(data.assistPara));
                }

                int defense = 0;

                if (null != data.strength)
                {
                    monster.getActualProperty().setStrength(
                            Integer.parseInt(data.strength));
                }
                if (null != data.agility)
                {
                    monster.getActualProperty().setAgility(
                            Integer.parseInt(data.agility));
                }
                if (null != data.stamina)
                {
                    monster.getActualProperty().setStamina(
                            Integer.parseInt(data.stamina));
                }
                if (null != data.inte)
                {
                    monster.getActualProperty().setInte(
                            Integer.parseInt(data.inte));
                }
                if (null != data.spirit)
                {
                    monster.getActualProperty().setSpirit(
                            Integer.parseInt(data.spirit));
                }
                if (null != data.lucky)
                {
                    monster.getActualProperty().setLucky(
                            Integer.parseInt(data.lucky));
                }
                if (null != data.defense)
                {
                    defense = Integer.parseInt(data.defense);
                }
                // 添加获得怪物的类型参数代码
                int hp = CEService.hpByStamina(
                		monster.getActualProperty().getStamina(), 
                		monster.getLevel(), 
                		monster.getObjectLevel().getHpCalPara());

                monster.getActualProperty().setHpMax(hp);
                monster.setHp(hp);

                int mp = CEService.mpByInte(monster.getActualProperty()
                        .getInte(), monster.getLevel(), monster
                        .getObjectLevel().getMpCalPara());
                monster.getActualProperty().setMpMax(mp);
                monster.setMp(mp);

                monster.getActualProperty().setDefense(
                        CEService.defenseBySpirit(monster.getActualProperty()
                                .getSpirit(), monster.getVocation()
                                .getPhysicsDefenceSpiritPara())
                                + defense);

                int maxPhysicsAttack = 0, minPhysicsAttack = 0;

                if (null != data.minPhysicsAttack)
                {
                    minPhysicsAttack = Integer.parseInt(data.minPhysicsAttack);
                    monster.getBaseProperty().setMinPhysicsAttack(
                            minPhysicsAttack);
                }

                if (null != data.maxPhysicsAttack)
                {
                    maxPhysicsAttack = Integer.parseInt(data.maxPhysicsAttack);
                    monster.getBaseProperty().setMaxPhysicsAttack(
                            maxPhysicsAttack);
                }

                monster
                        .getActualProperty()
                        .setMinPhysicsAttack(
                                CEService
                                        .minPhysicsAttack(
                                                monster.getActualProperty()
                                                        .getStrength(),
                                                monster.getActualProperty()
                                                        .getAgility(),
                                                monster
                                                        .getVocation()
                                                        .getPhysicsAttackParaA(),
                                                monster
                                                        .getVocation()
                                                        .getPhysicsAttackParaB(),
                                                monster
                                                        .getVocation()
                                                        .getPhysicsAttackParaC(),
                                                minPhysicsAttack,
                                                0,
                                                monster
                                                        .getActualAttackImmobilityTime() / 1000F,
                                                monster
                                                        .getObjectLevel()
                                                        .getPhysicsAttckCalPara()));

                monster
                        .getActualProperty()
                        .setMaxPhysicsAttack(
                                CEService
                                        .maxPhysicsAttack(
                                                monster.getActualProperty()
                                                        .getStrength(),
                                                monster.getActualProperty()
                                                        .getAgility(),
                                                monster
                                                        .getVocation()
                                                        .getPhysicsAttackParaA(),
                                                monster
                                                        .getVocation()
                                                        .getPhysicsAttackParaB(),
                                                monster
                                                        .getVocation()
                                                        .getPhysicsAttackParaC(),
                                                maxPhysicsAttack,
                                                0,
                                                monster
                                                        .getActualAttackImmobilityTime() / 1000F,
                                                monster
                                                        .getObjectLevel()
                                                        .getPhysicsAttckCalPara()));

                int physicsDeathblowLevel = 0, magicDeathblowLevel = 0, hitLevel = 0, duckLevel = 0;
                //edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
                physicsDeathblowLevel = CEService.physicsDeathblowLevel(
                		monster.getActualProperty().getAgility(), 
                		monster.getActualProperty().getLucky());
                magicDeathblowLevel = CEService.magicDeathblowLevel(
                		monster.getActualProperty().getInte(), 
                		monster.getActualProperty().getLucky());
                hitLevel = CEService.hitLevel(monster.getActualProperty().getLucky());
                duckLevel = CEService.duckLevel(
                		monster.getActualProperty().getInte(), 
                		monster.getActualProperty().getLucky());
                
                monster.getActualProperty().setPhysicsDeathblowLevel(
                        (short) physicsDeathblowLevel);
                monster.getActualProperty().setMagicDeathblowLevel(
                        (short) magicDeathblowLevel);
                monster.getActualProperty().setHitLevel((short) hitLevel);
                monster.getActualProperty().setPhysicsDuckLevel(
                        (short) duckLevel);

                if (null != data.sanctity)
                {
                    monster.getActualProperty().getMagicFastnessList().add(
                            EMagic.SANCTITY, Integer.parseInt(data.sanctity));
                }
                if (null != data.umbra)
                {
                    monster.getActualProperty().getMagicFastnessList().add(
                            EMagic.UMBRA, Integer.parseInt(data.umbra));
                }
                if (null != data.fire)
                {
                    monster.getActualProperty().getMagicFastnessList().add(
                            EMagic.FIRE, Integer.parseInt(data.fire));
                }
                if (null != data.water)
                {
                    monster.getActualProperty().getMagicFastnessList().add(
                            EMagic.WATER, Integer.parseInt(data.water));
                }
                if (null != data.soil)
                {
                    monster.getActualProperty().getMagicFastnessList().add(
                            EMagic.SOIL, Integer.parseInt(data.soil));
                }

                int baseMagicHarmByInte = CEService.magicHarmByInte(monster
                        .getActualProperty().getInte());

                monster.getActualProperty().getBaseMagicHarmList().add(
                        EMagic.SANCTITY, baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(
                        EMagic.UMBRA, baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(
                        EMagic.FIRE, baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(
                        EMagic.WATER, baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(
                        EMagic.SOIL, baseMagicHarmByInte);

                if (null != data.magicType)
                {
                    int minDamageValue = Integer.parseInt(data.minDamageValue);
                    int maxDamageValue = Integer.parseInt(data.maxDamageValue);
                    int magicHarmValue = CEService.magicHarm(monster
                            .getActualProperty().getInte(),
                            (minDamageValue + maxDamageValue) / 2);
                    monster.getActualProperty().getBaseMagicHarmList().add(
                            EMagic.getMagic(data.magicType),
                            magicHarmValue - baseMagicHarmByInte);

                }

                if (null != data.money)
                {
                    monster.setMoney(Integer.parseInt(data.money));
                }

                monster.setSoulIDList(data.soulIDList);

                if (null != data.aiID)
                {
                    monster.setAttackAiID(Integer.parseInt(data.aiID));
                }

                if (null != data.legacyTypeNums)
                {
                    monster.setLegacyListInfo(Byte
                            .parseByte(data.legacyTypeMostNums), Byte
                            .parseByte(data.legacyTypeSmallestNums));

                    if (null != data.item1)
                    {
                        monster.addLegacy(
                                        Integer.parseInt(data.item1),
                                        Float.valueOf(data.item1Odds),
//                                        DataConvertor.percentElementsString2Float(data.item1Odds),
                                        Integer.parseInt(data.item1nums));
                    }
                    if (null != data.item2)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item2),
                                        Float.valueOf(data.item2Odds),
//                                        DataConvertor.percentElementsString2Float(data.item2Odds),
                                        Integer.parseInt(data.item2nums));
                    }
                    if (null != data.item3)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item3),
                                        Float.valueOf(data.item3Odds),
//                                        DataConvertor.percentElementsString2Float(data.item3Odds),
                                        Integer.parseInt(data.item3nums));
                    }
                    if (null != data.item4)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item4),
                                        Float.valueOf(data.item4Odds),
//                                        DataConvertor.percentElementsString2Float(data.item4Odds),
                                        Integer.parseInt(data.item4nums));
                    }
                    if (null != data.item5)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item5),
                                        Float.valueOf(data.item5Odds),
//                                        DataConvertor.percentElementsString2Float(data.item5Odds),
                                        Integer.parseInt(data.item5nums));
                    }
                    if (null != data.item6)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item6),
                                        Float.valueOf(data.item6Odds),
//                                        DataConvertor.percentElementsString2Float(data.item6Odds),
                                        Integer.parseInt(data.item6nums));
                    }
                    if (null != data.item7)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item7),
                                        Float.valueOf(data.item7Odds),
//                                        DataConvertor.percentElementsString2Float(data.item7Odds),
                                        Integer.parseInt(data.item7nums));
                    }
                    if (null != data.item8)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item8),
                                        Float.valueOf(data.item8Odds),
//                                        DataConvertor.percentElementsString2Float(data.item8Odds),
                                        Integer.parseInt(data.item8nums));
                    }
                    if (null != data.item9)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item9),
                                        Float.valueOf(data.item9Odds),
//                                        DataConvertor.percentElementsString2Float(data.item9Odds),
                                        Integer.parseInt(data.item9nums));
                    }
                    if (null != data.item10)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item10),
                                        Float.valueOf(data.item10Odds),
//                                        DataConvertor.percentElementsString2Float(data.item10Odds),
                                        Integer.parseInt(data.item10nums));
                    }
                    if (null != data.item11)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item11),
                                        Float.valueOf(data.item11Odds),
//                                        DataConvertor.percentElementsString2Float(data.item11Odds),
                                        Integer.parseInt(data.item11nums));
                    }
                    if (null != data.item12)
                    {
                        monster
                                .addLegacy(
                                        Integer.parseInt(data.item12),
//                                        DataConvertor.percentElementsString2Float(data.item12Odds),
                                        Float.valueOf(data.item12Odds),
                                        Integer.parseInt(data.item12nums));
                    }
                }

                monster.setBaseExp(CEService.monsterBaseExperience(monster
                        .getLevel(), monster.getObjectLevel()));

                if (null != data.retime)
                {
                    monster.setRetime(Integer.parseInt(data.retime));
                }
                else
                {
                    monster.setRetime(5);
                }

                if (null != data.imageID)
                {
                    monster.setImageID(Short.parseShort(data.imageID));
                }
                
                if (null != data.animationID)
                {
                    monster.setAnimationID(Short.parseShort(data.animationID));
                }

                monsterMapOfIDTable.put(monster.getID(), monster);
                log.debug("创建怪物实物实例成功... id = " + monster.getID());
            }
            else
            {
                LogWriter.println("无法创建怪物：" + _monsterModelID);
            }
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
            log.error("创建怪物实例 errors : ",e);
            monster = null;
        }

        return monster;
    }

    /**
     * 从列表中移除怪物
     * 
     * @param _monsterID
     */
    public void removeMonster (int _monsterID)
    {
        monsterMapOfIDTable.remove(_monsterID);
    }

    /**
     * 从列表中移除NPC
     * 
     * @param _npc
     */
    public void removeNpc (Npc _npc)
    {
        npcMapOfIDTable.remove(_npc.getID());
        npcMapOfModelIDTable.remove(_npc.getModelID());
    }

    /**
     * 处理怪物变身
     * 
     * @param _archetype 怪物原型
     * @param _changesData 变身数据
     * @return
     */
    public Monster processMonsterChanges (Monster _archetype,
            Changes _changesData)
    {
        _archetype.getActualProperty().setStrength(_changesData.strength);
        _archetype.getActualProperty().setAgility(_changesData.agility);
        _archetype.getActualProperty().setInte(_changesData.inte);
        _archetype.getActualProperty().setSpirit(_changesData.spirit);
        _archetype.getActualProperty().setLucky(_changesData.lucky);

        if (_changesData.newHp > 0)
        {
            _archetype.getActualProperty().setHpMax(_changesData.newHp);
            _archetype.setHp(_changesData.newHp);
        }

        int mp = CEService.mpByInte(_archetype.getActualProperty().getInte(),
                _archetype.getLevel(), _archetype.getObjectLevel()
                        .getMpCalPara());
        _archetype.getActualProperty().setMpMax(mp);
        _archetype.setMp(mp);

        _archetype.getActualProperty().setDefense(
                CEService.defenseBySpirit(_archetype.getActualProperty()
                        .getSpirit(), _archetype.getVocation()
                        .getPhysicsDefenceSpiritPara())
                        + _changesData.defense);

        _archetype.getActualProperty().setMinPhysicsAttack(
                CEService.maxPhysicsAttack(_archetype.getActualProperty()
                        .getStrength(), _archetype.getActualProperty()
                        .getAgility(), _archetype.getVocation()
                        .getPhysicsAttackParaA(), _archetype.getVocation()
                        .getPhysicsAttackParaB(), _archetype.getVocation()
                        .getPhysicsAttackParaC(), _changesData.minAttack, 0,
                        _archetype.getActualAttackImmobilityTime() / 1000F,
                        _archetype.getObjectLevel().getPhysicsAttckCalPara()));

        _archetype.getActualProperty().setMaxPhysicsAttack(
                CEService.maxPhysicsAttack(_archetype.getActualProperty()
                        .getStrength(), _archetype.getActualProperty()
                        .getAgility(), _archetype.getVocation()
                        .getPhysicsAttackParaA(), _archetype.getVocation()
                        .getPhysicsAttackParaB(), _archetype.getVocation()
                        .getPhysicsAttackParaC(), _changesData.maxAttack, 0,
                        _archetype.getActualAttackImmobilityTime() / 1000F,
                        _archetype.getObjectLevel().getPhysicsAttckCalPara()));

        int physicsDeathblowLevel = 0, magicDeathblowLevel = 0, hitLevel = 0, duckLevel = 0;
        //edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
        physicsDeathblowLevel = CEService.physicsDeathblowLevel(
        		_archetype.getActualProperty().getAgility(), 
        		_archetype.getActualProperty().getLucky());
        magicDeathblowLevel = CEService.magicDeathblowLevel(
        		_archetype.getActualProperty().getInte(), 
        		_archetype.getActualProperty().getLucky());
        hitLevel = CEService.hitLevel(_archetype.getActualProperty().getLucky());
        duckLevel = CEService.duckLevel(
        		_archetype.getActualProperty().getInte(), 
        		_archetype.getActualProperty().getLucky());
        
        _archetype.getActualProperty().setPhysicsDeathblowLevel(
                (short) physicsDeathblowLevel);
        _archetype.getActualProperty().setMagicDeathblowLevel(
                (short) magicDeathblowLevel);
        _archetype.getActualProperty().setHitLevel((short) hitLevel);
        _archetype.getActualProperty().setPhysicsDuckLevel((short) duckLevel);

        _archetype.getActualProperty().getMagicFastnessList().reset(
                EMagic.SANCTITY, _changesData.sanctityFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(
                EMagic.UMBRA, _changesData.umbraFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(
                EMagic.FIRE, _changesData.fireFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(
                EMagic.WATER, _changesData.waterFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(
                EMagic.SOIL, _changesData.soilFastness);

        int baseMagicHarmByInte = CEService.magicHarmByInte(_archetype
                .getActualProperty().getInte());

        _archetype.getActualProperty().getBaseMagicHarmList().reset(
                EMagic.SANCTITY, baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(
                EMagic.UMBRA, baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(
                EMagic.FIRE, baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(
                EMagic.WATER, baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(
                EMagic.SOIL, baseMagicHarmByInte);

        if (null != _changesData.magicType)
        {
            int magicHarmValue = CEService.magicHarm(
            		_archetype.getActualProperty().getInte(), 
            		(_changesData.minDamageValue + _changesData.maxDamageValue) / 2);
            
            _archetype.getActualProperty().getBaseMagicHarmList().add(
            		_changesData.magicType,
                    magicHarmValue - baseMagicHarmByInte);

        }

        _archetype.setImageID(_changesData.imageID);

        return _archetype;
    }

    /**
     * 处理怪物还原
     * 
     * @param _changes 怪物的变身
     * @param _archetypeHp 变身前的HP
     * @param _changesData 变身数据
     * @return
     */
    public Monster processMonsterChangesToArchetype (Monster _changes,
            int _archetypeHp, Changes _changesData)
    {
        MonsterData data = MonsterDataDict.getInstance().getMonsterData(
                _changes.getModelID().toLowerCase());

        if (null != data)
        {
            int defense = 0;

            if (null != data.strength)
            {
                _changes.getActualProperty().setStrength(
                        Integer.parseInt(data.strength));
            }
            if (null != data.agility)
            {
                _changes.getActualProperty().setAgility(
                        Integer.parseInt(data.agility));
            }
            if (null != data.stamina)
            {
                _changes.getActualProperty().setStamina(
                        Integer.parseInt(data.stamina));
            }
            if (null != data.inte)
            {
                _changes.getActualProperty().setInte(
                        Integer.parseInt(data.inte));
            }
            if (null != data.spirit)
            {
                _changes.getActualProperty().setSpirit(
                        Integer.parseInt(data.spirit));
            }
            if (null != data.lucky)
            {
                _changes.getActualProperty().setLucky(
                        Integer.parseInt(data.lucky));
            }
            if (null != data.defense)
            {
                defense = Integer.parseInt(data.defense);
            }

            if (_changesData.newHp > 0)
            {
                _changes.getActualProperty().setHpMax(
                        CEService.hpByStamina(
                        		_changes.getActualProperty().getStamina(), 
                        		_changes.getLevel(), 
                        		_changes.getObjectLevel().getHpCalPara()));
                _changes.setHp(_archetypeHp);
            }

            int mp = CEService.mpByInte(_changes.getActualProperty().getInte(),
                    _changes.getLevel(), _changes.getObjectLevel()
                            .getMpCalPara());
            _changes.getActualProperty().setMpMax(mp);
            _changes.setMp(mp);

            _changes.getActualProperty().setDefense(
                    CEService.defenseBySpirit(_changes.getActualProperty()
                            .getSpirit(), _changes.getVocation()
                            .getPhysicsDefenceSpiritPara())
                            + defense);

            int maxPhysicsAttack = 0, minPhysicsAttack = 0;

            if (null != data.minPhysicsAttack)
            {
                minPhysicsAttack = Integer.parseInt(data.minPhysicsAttack);
            }
            if (null != data.maxPhysicsAttack)
            {
                maxPhysicsAttack = Integer.parseInt(data.maxPhysicsAttack);
            }

            _changes
                    .getActualProperty()
                    .setMinPhysicsAttack(
                            CEService
                                    .maxPhysicsAttack(
                                            _changes.getActualProperty()
                                                    .getStrength(),
                                            _changes.getActualProperty()
                                                    .getAgility(),
                                            _changes.getVocation()
                                                    .getPhysicsAttackParaA(),
                                            _changes.getVocation()
                                                    .getPhysicsAttackParaB(),
                                            _changes.getVocation()
                                                    .getPhysicsAttackParaC(),
                                            minPhysicsAttack,
                                            0,
                                            _changes
                                                    .getActualAttackImmobilityTime() / 1000F,
                                            _changes.getObjectLevel()
                                                    .getPhysicsAttckCalPara()));

            _changes
                    .getActualProperty()
                    .setMaxPhysicsAttack(
                            CEService
                                    .maxPhysicsAttack(
                                            _changes.getActualProperty()
                                                    .getStrength(),
                                            _changes.getActualProperty()
                                                    .getAgility(),
                                            _changes.getVocation()
                                                    .getPhysicsAttackParaA(),
                                            _changes.getVocation()
                                                    .getPhysicsAttackParaB(),
                                            _changes.getVocation()
                                                    .getPhysicsAttackParaC(),
                                            maxPhysicsAttack,
                                            0,
                                            _changes
                                                    .getActualAttackImmobilityTime() / 1000F,
                                            _changes.getObjectLevel()
                                                    .getPhysicsAttckCalPara()));

            int physicsDeathblowLevel = 0, magicDeathblowLevel = 0, hitLevel = 0, duckLevel = 0;
            //edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
            physicsDeathblowLevel = CEService.physicsDeathblowLevel(
            		_changes.getActualProperty().getAgility(), 
            		_changes.getActualProperty().getLucky());
            magicDeathblowLevel = CEService.magicDeathblowLevel(
            		_changes.getActualProperty().getInte(), 
            		_changes.getActualProperty().getLucky());
            hitLevel = CEService.hitLevel(_changes.getActualProperty().getLucky());
            duckLevel = CEService.duckLevel(
            		_changes.getActualProperty().getInte(), 
            		_changes.getActualProperty().getLucky());
            
            _changes.getActualProperty().setPhysicsDeathblowLevel(
                    (short) physicsDeathblowLevel);
            _changes.getActualProperty().setMagicDeathblowLevel(
                    (short) magicDeathblowLevel);
            _changes.getActualProperty().setHitLevel((short) hitLevel);
            _changes.getActualProperty().setPhysicsDuckLevel((short) duckLevel);

            _changes.getActualProperty().getMagicFastnessList().clear();

            if (null != data.sanctity)
            {
                _changes.getActualProperty().getMagicFastnessList().reset(
                        EMagic.SANCTITY, Integer.parseInt(data.sanctity));
            }
            if (null != data.umbra)
            {
                _changes.getActualProperty().getMagicFastnessList().reset(
                        EMagic.UMBRA, Integer.parseInt(data.umbra));
            }
            if (null != data.fire)
            {
                _changes.getActualProperty().getMagicFastnessList().reset(
                        EMagic.FIRE, Integer.parseInt(data.fire));
            }
            if (null != data.water)
            {
                _changes.getActualProperty().getMagicFastnessList().reset(
                        EMagic.WATER, Integer.parseInt(data.water));
            }
            if (null != data.soil)
            {
                _changes.getActualProperty().getMagicFastnessList().reset(
                        EMagic.SOIL, Integer.parseInt(data.soil));
            }

            int baseMagicHarmByInte = CEService.magicHarmByInte(_changes
                    .getActualProperty().getInte());

            _changes.getActualProperty().getBaseMagicHarmList().clear();

            _changes.getActualProperty().getBaseMagicHarmList().reset(
                    EMagic.SANCTITY, baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(
                    EMagic.UMBRA, baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(
                    EMagic.FIRE, baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(
                    EMagic.WATER, baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(
                    EMagic.SOIL, baseMagicHarmByInte);

            if (null != data.magicType)
            {
                int minDamageValue = Integer.parseInt(data.minDamageValue);
                int maxDamageValue = Integer.parseInt(data.maxDamageValue);
                int magicHarmValue = CEService.magicHarm(_changes
                        .getActualProperty().getInte(),
                        (minDamageValue + maxDamageValue) / 2);
                _changes.getActualProperty().getBaseMagicHarmList().add(
                        EMagic.getMagic(data.magicType),
                        magicHarmValue - baseMagicHarmByInte);

            }

            _changes.setImageID(Short.parseShort(data.imageID));

            return _changes;
        }

        return null;
    }

    /**
     * 获取怪物实例
     * 
     * @param _monsterID 怪物ID
     * @return
     */
    public Monster getMonster (int _monsterID)
    {
        return monsterMapOfIDTable.get(_monsterID);
    }

    /**
     * 创建NPC实例
     * 
     * @param _npcModelID NPC模型编号
     * @return
     */
    public Npc buildNpcInstance (String _npcModelID)
    {
        Npc npc = null;
        NpcData data = NpcDataDict.getInstance().getNpcData(
                _npcModelID.toLowerCase());

        if (null != data)
        {
            npc = new Npc(data.helloContent);
            npc.setModelID(data.modelID);
            npc.setName(data.name);
            npc.setTitle(data.title);
            npc.setScreamContent(data.screamContent);
            npc.setImageID(Short.parseShort(data.imageID));
            npc.setImageType(Byte.parseByte(data.imageType));
            npc.setClan(EClan.getClanByDesc(data.clanDesc));
            //add by zhengl ; date:2010-11-18 ; note: 动画ID只通过xml获取并且在此添加
            npc.setAnimationID(Short.parseShort(data.animationID));

            EVocation vocation = EVocation
                    .getVocationByDesc(data.skillEducateVocation);
            ESystemFeature feature = ESystemFeature
                    .getFeatureByDesc(data.skillEducateFeature);

            if (null != data.function1)
            {
                BaseNpcFunction f = FunctionBuilder.build(
                		_npcModelID, 
                		npc.getID(), 
                		Integer.parseInt(data.function1), 
                		vocation,
                        feature, npc.getClan());
                npc.addFunction(f);

                if (null != data.function2)
                {
                    f = FunctionBuilder.build(_npcModelID, npc.getID(), Integer
                            .parseInt(data.function2), vocation, feature, npc.getClan());
                    npc.addFunction(f);

                    if (null != data.function3)
                    {
                        f = FunctionBuilder.build(_npcModelID, npc.getID(),
                                Integer.parseInt(data.function3), vocation,
                                feature, npc.getClan());
                        npc.addFunction(f);

                        if (null != data.function4)
                        {
                            f = FunctionBuilder.build(_npcModelID, npc.getID(),
                                    Integer.parseInt(data.function4), vocation,
                                    feature, npc.getClan());
                            npc.addFunction(f);

                            if (null != data.function5)
                            {
                                f = FunctionBuilder.build(_npcModelID, npc
                                        .getID(), Integer
                                        .parseInt(data.function5), vocation,
                                        feature, npc.getClan());
                                npc.addFunction(f);
                            }
                        }
                    }
                }
            }

            npcMapOfIDTable.put(npc.getID(), npc);
            npcMapOfModelIDTable.put(npc.getModelID(), npc);
        }

        return npc;
    }

    /**
     * 地上的任务物品被拾取
     * 
     * @param _groundTaskGoods
     */
    public void groundTaskGoodsBePicked (GroundTaskGoods _groundTaskGoods)
    {
        _groundTaskGoods.disappear();

        MapSynchronousInfoBroadcast.getInstance().put(
                _groundTaskGoods.where(),
                new GroundTaskGoodsDisappearNofity(_groundTaskGoods.getID(),
                        _groundTaskGoods.getCellY()), false, 0);

        _groundTaskGoods.where().getGroundTaskGoodsList().remove(
                _groundTaskGoods);
        groundTaskGoodsTable.remove(_groundTaskGoods.getID());
        emptyGroundTaskGoodsList.add(_groundTaskGoods);
    }

    /**
     * 处理怪物掉落
     * 
     * @param player
     * @param _monsterID
     * @param _money
     * @param legacyItems
     * @param legacyTaskGoodsList
     * @param _mapID
     * @param _locationX
     * @param _locationY
     */
    public void processMonsterLegacy (Monster _monster,
            ArrayList<int[]> _legacyItems,
            ArrayList<TaskGoodsLegacyInfo> _legacyTaskGoodsList)
    {
        if (_monster.getAttackerAtFirst().getGroupID() > 0)
        {
            Group group = GroupServiceImpl.getInstance().getGroup(
                    _monster.getAttackerAtFirst().getGroupID());

            if (null != group)
            {
                processGroupMonsterLegacy(group, _monster, _legacyItems,
                        _legacyTaskGoodsList);
            }
            else
            {
                processPersonMonsterLegacy(_monster.getAttackerAtFirst(),
                        _monster, _legacyItems, _legacyTaskGoodsList);
            }
        }
        else
        {
            processPersonMonsterLegacy(_monster.getAttackerAtFirst(), _monster,
                    _legacyItems, _legacyTaskGoodsList);
        }
    }

    /**
     * 处理组队下的怪物掉落
     * 
     * @param _group
     * @param _monsterID
     * @param _money
     * @param _legacyItems
     * @param _legacyTaskGoodsList
     * @param _mapID
     * @param _locationX
     * @param _locationY
     */
    private void processGroupMonsterLegacy (Group _group, Monster _monster,
            ArrayList<int[]> _legacyItems,
            ArrayList<TaskGoodsLegacyInfo> _legacyTaskGoodsList)
    {
        if (null != _group)
        {
            ArrayList<HeroPlayer> playerList = _group
                    .getValidatePlayerList(_monster.where().getID());

            int experience = 0;

            int money = (int) (_monster.getMoney() * 1F / playerList.size() + 0.5);

            for (HeroPlayer player : playerList)
            {
                experience = CEService.getExperienceFromMonster(playerList
                        .size(), player.getLevel(), _monster.getLevel(),
                        _monster.getBaseExp());

                if (experience > 0)
                {
                    PlayerServiceImpl.getInstance().addExperience(player,
                            experience, player.getExperienceModulus(),
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_MIDDLE);
                }

                money += TeachService.getMasterAddMoney(player);

                if (money > 0)
                {
                    PlayerServiceImpl.getInstance().addMoney(player, money,
                            player.getMoneyModulus(),
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_MIDDLE,
                            "怪物掉落");
                }
            }

            if ((null != _legacyItems && _legacyItems.size() > 0)
                    || (null != _legacyTaskGoodsList && _legacyTaskGoodsList
                            .size() > 0))
            {
                if (_monster.isInDungeon())
                {
//                    playerList = _monster.getHatredTargetList();   //物品分配玩家列表改为怪物所在地图所有玩家
                }
                log.debug("group player size = " + _group.getMemberList().size()+",pp list size="+playerList.size());
                RaidPickerBox box = new RaidPickerBox(_group.getID(), _group
                        .getGoodsPickerUserID(_monster.where().getID()),
                        playerList, _monster.getID(), _monster.where(),
                        _legacyItems, _legacyTaskGoodsList,
                        _monster.getCellX(), _monster.getCellY());
                MonsterLegacyManager.getInstance().addMonsterLegacyBox(box);

                for (HeroPlayer player : playerList)
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new LegacyBoxEmergeNotify(box, box
                                    .getStateOfPicking(player.getUserID())));
                }

                // 添加怪物掉落日志 ，不记录掉落的任务物品
                if(null != _legacyItems && _legacyItems.size() > 0){
                    int[] goodsIDS = new int[_legacyItems.size()];
                    int[] goodsNums = new int[_legacyItems.size()];
                    String[] goodsNames = new String[_legacyItems.size()];
                    String[] goodsTypes = new String[_legacyItems.size()];
                    Goods goods;
                    for(int i=0; i<_legacyItems.size(); i++){
                        int[] goodsInfo = _legacyItems.get(i);
                        goodsIDS[i] = goodsInfo[0];
                        goodsNums[i] = goodsInfo[1];

                        goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsInfo[0]);

                        goodsNames[i] = goods.getName();
                        goodsTypes[i] = goods.getGoodsType().getDescription();
                    }

                    LogServiceImpl.getInstance().monsterLegacy(_monster.getModelID(),_monster.getName(),playerList.size(),true,
                        money,_legacyItems.size(),goodsIDS,goodsNames,goodsNums,goodsTypes);
                }
            }


        }
    }

    /**
     * 处理个人杀死怪的掉落
     * 
     * @param _player
     * @param _monsterID
     * @param _money
     * @param _legacyItems
     * @param _legacyTaskGoodsList
     * @param _mapID
     * @param _locationX
     * @param _locationY
     */
    private void processPersonMonsterLegacy (HeroPlayer _player,
            Monster _monster, ArrayList<int[]> _legacyItems,
            ArrayList<TaskGoodsLegacyInfo> _legacyTaskGoodsList)
    {
        try
        {
            int experience = CEService.getExperienceFromMonster(1, _player
                    .getLevel(), _monster.getLevel(), _monster.getBaseExp());

            if (experience > 0)
            {
                PlayerServiceImpl.getInstance().addExperience(_player,
                        experience, _player.getExperienceModulus(),
                        PlayerServiceImpl.MONEY_DRAW_LOCATION_MIDDLE);
            }

            int money = _monster.getMoney();

            money += TeachService.getMasterAddMoney(_player);

            if (money > 0)
            {
                PlayerServiceImpl.getInstance().addMoney(_player,
                        money, _player.getMoneyModulus(),
                        PlayerServiceImpl.MONEY_DRAW_LOCATION_MIDDLE, "怪物掉落");
            }

            if (null != _legacyItems || null != _legacyTaskGoodsList)
            {
                MonsterLegacyBox box = new PersonalPickerBox(_player
                        .getUserID(), _monster.getID(), _monster.where(),
                        _legacyItems, _legacyTaskGoodsList,
                        _monster.getCellX(), _monster.getCellY());

                MonsterLegacyManager.getInstance().addMonsterLegacyBox(box);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new LegacyBoxEmergeNotify(box, true));

                // 添加怪物掉落日志，不记录掉落的任务物品
                if(null != _legacyItems && _legacyItems.size() > 0){
                    int[] goodsIDS = new int[_legacyItems.size()];
                    int[] goodsNums = new int[_legacyItems.size()];
                    String[] goodsNames = new String[_legacyItems.size()];
                    String[] goodsTypes = new String[_legacyItems.size()];
                    Goods goods;
                    for(int i=0; i<_legacyItems.size(); i++){
                        int[] goodsInfo = _legacyItems.get(i);
                        goodsIDS[i] = goodsInfo[0];
                        goodsNums[i] = goodsInfo[1];

                        goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsInfo[0]);

                        goodsNames[i] = goods.getName();
                        goodsTypes[i] = goods.getGoodsType().getDescription();
                    }

                    LogServiceImpl.getInstance().monsterLegacy(_monster.getModelID(),_monster.getName(),1,false,
                        money,_legacyItems.size(),goodsIDS,goodsNames,goodsNums,goodsTypes);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 问答活动周期刷新
     */
    @SuppressWarnings("deprecation")
	private void excuteRefreshQuestion ()
    {
    	Enumeration<Integer> keys = answerQuestionList.keys();
    	while (keys.hasMoreElements()) {
			int key =  keys.nextElement();
			
			AnswerQuestionData data = QuestionDict.getInstance().getAnswerQuestionData(key);
			if (data.refreshType == QuestionDict.REFRESH_TYPE_DAY) 
			{
				Date date1 = new Date(answerQuestionDayRefreshList.get(key));
				Date date2 = new Date(System.currentTimeMillis());
				int day = DateTool.getDifference(date1, date2);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date2);
				if (data.refreshDay <= day && calendar.get(Calendar.HOUR_OF_DAY) == 1) 
				{
					answerQuestionList.get(key).clear(); //清除所有参与者
					answerQuestionDayRefreshList.put(key, System.currentTimeMillis()); //最后刷新时间更新
					log.info("已经到了新的一天,对答题活动参与记录进行清零操作.");
				}
			}
			else if (data.refreshType == QuestionDict.REFRESH_TYPE_TIME)
			{
				Date nowDate = new Date(System.currentTimeMillis());
				String startTime = DateTool.formatDate(nowDate) + " ";
				String endTime = startTime;
				for (int i = 0; i < data.refreshTimeSum; i++)
				{
					startTime += data.startTime[i];
					endTime += data.endTime[i];
					Date startDate = DateTool.convertLongDate(startTime);
					Date endDate = DateTool.convertLongDate(endTime);
					if( System.currentTimeMillis() >= startDate.getTime()
							&& (!answerQuestionTimeRefreshList.get(key))
							&& System.currentTimeMillis() <= endDate.getTime() )
					{
						//未刷新,并且已经到了本周期开始时段,而且还未过结束时间
						//那么就刷新,并且记录成已刷新状态
						answerQuestionList.get(key).clear(); //清除所有参与者
						answerQuestionTimeRefreshList.put(key, true);
					}
					else if(System.currentTimeMillis() >= endDate.getTime()
							&& answerQuestionTimeRefreshList.get(key))
					{
						//到了结束时段,且已经刷新就重置回未刷新状态,等待明天再次刷新
						answerQuestionTimeRefreshList.put(key, false);
					}
				}
			}
		} // end while
    }

    class AnimalWalkThread implements Runnable
    {
        public void run ()
        {
            try
            {
                Thread.sleep(30000);

                while (true)
                {
                    for (Animal animal : animalList)
                    {
                        if (random.nextInt(3) > 0)
                        {
                            animal.walk();
                        }
                    }

                    Thread.sleep(10000);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    class RefreshQuestionManager extends TimerTask
    {
        public void run ()
        {
            try
            {
                excuteRefreshQuestion();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    class EmptyBoxThread implements Runnable
    {
        public void run ()
        {
            try
            {
                Thread.sleep(45000);

                while (true)
                {
                    for (int i = 0; i < emptyBoxList.size(); i++)
                    {
                        Box box = emptyBoxList.get(i);

                        if (System.currentTimeMillis() - box.getDisappearTime() >= box
                                .getRebirthInterval())
                        {
                            emptyBoxList.remove(i);
                            boxTable.put(box.getID(), box);
                            box.where().getBoxList().add(box);

                            box.rebirth(false);
                        }
                    }

                    Thread.sleep(60000);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    class BePickedGroundTaskGoodsThread implements Runnable
    {
        public void run ()
        {
            try
            {
                Thread.sleep(30000);

                while (true)
                {
                    for (int i = 0; i < emptyGroundTaskGoodsList.size(); i++)
                    {
                        GroundTaskGoods groundTaskGoods = emptyGroundTaskGoodsList.get(i);
                        //edit by zhengl; date: 2011-04-17; note: 采集任务刷新改为配置型
                        long disappear = 
                        	System.currentTimeMillis() - groundTaskGoods.getDisappearTime();
                        if (disappear >= config.task_gather_rebirth_interval)
                        {
                            TaskServiceImpl.getInstance().groundTaskGoodsRebirth(groundTaskGoods);
                            emptyGroundTaskGoodsList.remove(i);
                            groundTaskGoodsTable.put(groundTaskGoods.getID(), groundTaskGoods);
                            groundTaskGoods.where().getGroundTaskGoodsList().add(groundTaskGoods);
                        }
                    }
                    //edit by zhengl; date: 2011-04-17; note: 确保下一次刷新必定有采集点被刷新出来
                    Thread.sleep(config.task_gather_thread_run_interval * 1000);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 广播NPC或者怪物行走路径
     * 
     * @param _npc
     * @param _path
     */
    public void broadcastNotPlayerWalkPath (ME2NotPlayer _npc, byte[] _path,
            HeroPlayer _attackTarget)
    {
        if (0 < _npc.where().getPlayerList().size())
        {
            AbsResponseMessage msg = null;

            if (EObjectType.MONSTER == _npc.getObjectType())
            {
            	//edit by zhengl; date: 2011-05-07; note: 添加移动的x,y坐标
                msg = new MonsterWalkNotify(_npc.getID(), _npc.getMoveSpeed(),
                        _path, (byte)_npc.getCellX(), (byte)_npc.getCellY());
            }
            else
            {
                msg = new NpcWalkNotify(
                		_npc.getID(), _path, (byte)_npc.getCellX(), (byte)_npc.getCellY());
            }

            if (_npc.where().getPlayerList().size() > 0)
            {
                if (null == _attackTarget)
                {
                    MapSynchronousInfoBroadcast.getInstance().put(_npc.where(),
                            msg, false, 0);
                }
                else
                {
                    MapSynchronousInfoBroadcast.getInstance().put(_npc.where(),
                            msg, false, _attackTarget.getID());
                }
            }
        }
    }
}