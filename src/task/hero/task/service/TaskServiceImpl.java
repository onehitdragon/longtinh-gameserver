package hero.task.service;

import java.io.File;
import java.util.*;

import hero.log.service.LogServiceImpl;
import hero.log.service.ServiceType;
import hero.manufacture.ManufactureType;
import hero.npc.others.GroundTaskGoods;
import hero.npc.message.ChangeNpcStat;
import hero.npc.message.NpcResetNotify;

import hero.npc.message.GroundTaskGoodsEmergeNotify;

import hero.task.message.*;
import hero.task.target.*;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

import javolution.util.FastList;
import javolution.util.FastMap;
import hero.charge.FeeIni;
import hero.charge.service.ChargeServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.legacy.TaskGoodsLegacyInfo;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.SwitchMapFailNotify;
import hero.map.service.MapServiceImpl;
import hero.npc.Monster;
import hero.npc.Npc;
import hero.npc.message.ChangeTaskGearStat;
import hero.npc.others.TaskGear;
import hero.npc.service.NotPlayerServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.EVocation;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.ME2ObjectList;
import hero.share.service.ShareServiceImpl;
import hero.share.service.Tip;
import hero.task.Award;
import hero.task.Condition;
import hero.task.MonsterTaskGoodsSetting;
import hero.task.Push;
import hero.task.Task;
import hero.task.TaskInstance;
import hero.task.Award.AwardGoodsUnit;
import hero.task.Task.ETaskDifficultyLevel;
import hero.npc.message.ChangeGroundTaskGoodsStat;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-10 上午11:35:58
 * @描述 ：
 */

public class TaskServiceImpl extends AbsServiceAdaptor<TaskConfig>
{
     private static Logger log = Logger.getLogger(TaskServiceImpl.class);
    /**
     * 玩家已完成任务编号映射（key:玩家角色userID value:已完成任务编号列表）
     */
    private FastMap<Integer, ArrayList<Integer>>                playerCompletedTaskListMap;

    /**
     * 玩家接受的任务
     */
    private FastMap<Integer, ArrayList<TaskInstance>>           playerExsitsTaskListMap;
    /**
     * 任务推广数据
     */
    private FastMap<Integer, Push>   							pushDataTable;
    /**
     * 任务推广,玩家待下发物品容器<p>
     * <流水号, [玩家ID, 推广ID, 计费的类型]>
     */
    private FastMap<String, Integer[]>                          pushPlayerGoods;

    /**
     * 任务目录（key:任务编号 vale:任务模板）
     */
    private FastMap<Integer, Task>                              taskDictionary;

    /**
     * 任务目录（key:npc编号 vale:任务模板）
     */
    private FastMap<String, ArrayList<Task>>                    npcTaskDictionary;

    /**
     * 怪物掉落任务物品映射
     */
    private FastMap<String, ArrayList<MonsterTaskGoodsSetting>> monsterTaskGoodsDictory;

    /**
     * 护送NPC任务执行列表
     */
    private FastList<EscortNpcTaskInfo>                         escortTaskExcuteList          = new FastList<EscortNpcTaskInfo>();

    /**
     * 探索地图任务列表
     */
    private FastList<FoundAPathInfo>                            foundAPathTaskList            = new FastList<FoundAPathInfo>();

    /**
     * 单例
     */
    private static TaskServiceImpl                              instance;

    /**
     * 接、交护送任务时的有效距离
     */
    private static final byte                                   ESCORT_TASK_VALIDATE_DISTANCE = 15;

    /**
     * 护送任务的执行判断间隔时间（毫秒）
     */
    private static final int                                    ESCORT_TASK_INTERVAL          = 5000;

    /**
     * 探路任务的执行判断间隔时间（毫秒）
     */
    private static final int                                    FOUND_A_PATH_TASK_INTERVAL    = 10000;

    /**
     * 角色最多可接纳的任务数量
     */
    public static final byte                                    MAX_TASK_NUMBER_AT_TIME       = 20;

    /**
     * 护送和探路任务启动延时
     */
    private static final int                                    THREAD_START_DELAY            = 30000;

    /**
     * 计时器
     */
    private Timer                                               timer;

    /**
     * 获取单例
     * 
     * @return
     */
    public static TaskServiceImpl getInstance ()
    {
        if (null == instance)
        {
            instance = new TaskServiceImpl();
        }

        return instance;
    }

    private TaskServiceImpl()
    {
        config = new TaskConfig();
        timer = new Timer();
        playerCompletedTaskListMap = new FastMap<Integer, ArrayList<Integer>>();
        playerExsitsTaskListMap = new FastMap<Integer, ArrayList<TaskInstance>>();
        taskDictionary = new FastMap<Integer, Task>();
        pushDataTable = new FastMap<Integer, Push>();
        npcTaskDictionary = new FastMap<String, ArrayList<Task>>();
        monsterTaskGoodsDictory = new FastMap<String, ArrayList<MonsterTaskGoodsSetting>>();
        pushPlayerGoods = new FastMap<String, Integer[]>();
    }

    @Override
    public void dbUpdate (int _userID)
    {
    }

    @Override
    public void createSession (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (null != player)
        {
            playerCompletedTaskListMap.put(_session.userID,
                    new ArrayList<Integer>());
            playerExsitsTaskListMap.put(_session.userID,
                    new ArrayList<TaskInstance>());
            TaskDAO.loadTask(player, playerExsitsTaskListMap
                    .get(_session.userID), playerCompletedTaskListMap
                    .get(_session.userID));
        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        try
        {
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(_session.userID);

            if (null != player && null != player.getEscortTarget())
            {
                synchronized (escortTaskExcuteList)
                {
                    EscortNpcTaskInfo info;

                    for (int i = 0; i < escortTaskExcuteList.size(); i++)
                    {
                        info = escortTaskExcuteList.get(i);

                        if (info.trigger == player)
                        {
                            info = escortTaskExcuteList.remove(i);

                            endEscort(info);

                            if (null != info.spareTaskMemberList)
                            {
                                for (HeroPlayer other : info.spareTaskMemberList)
                                {
                                    if (other.isEnable())
                                        ResponseMessageQueue.getInstance().put(
                                                other.getMsgQueueIndex(),
                                                new Warning("‘" + info.task.getName() + "’任务失败", 
                                                		Warning.UI_STRING_TIP));
                                }
                            }

                            info.trigger = null;
                            info.spareTaskMemberList.clear();

                            break;
                        }

                        if (info.spareTaskMemberList.contains(player))
                        {
                            info.spareTaskMemberList.remove(player);
                            player.setEscortTarget(null);

                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.println("释放任务会话出错");
        }
    }

    public void clean (int _userID)
    {
        playerExsitsTaskListMap.remove(_userID);
        playerCompletedTaskListMap.remove(_userID);
    }

    @Override
    protected void start ()
    {
        load();
        timer.schedule(new EscortNpcTaskManager(), THREAD_START_DELAY,
                ESCORT_TASK_INTERVAL);
        timer.schedule(new FoundAPathTaskManager(), THREAD_START_DELAY,
                FOUND_A_PATH_TASK_INTERVAL);
    }

    /**
     * 根据任务编号获取任务模型
     * 
     * @param _taskID
     * @return
     */
    public Task getTask (int _taskID)
    {
        return taskDictionary.get(_taskID);
    }

    /**
     * 获取玩家任务
     * 
     * @param _taskID
     * @return
     */
    public TaskInstance getPlayerTask (int _userID, int _taskID)
    {
        ArrayList<TaskInstance> taskList = playerExsitsTaskListMap.get(_userID);

        for (TaskInstance task : taskList)
        {
            if (task.getArchetype().getID() == _taskID)
            {
                return task;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private void load ()
    {
        File dataPath = null;
        String mapID, mapX, mapY;

        FastMap<Integer, BaseTaskTarget> killMonsterTargetList = new FastMap<Integer, BaseTaskTarget>();

        try
        {
            dataPath = new File(config.getKillMonsterTaskTargetDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                TaskTargetKillMonster tastTarget = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                    	//杀怪目标
                        tastTarget = new TaskTargetKillMonster(Integer
                                .parseInt(subE.elementTextTrim("id")), subE
                                .elementTextTrim("monsterID").toLowerCase(),
                                Short
                                        .parseShort(subE
                                                .elementTextTrim("number")));

                        if (null != (mapID = subE.elementTextTrim("mapID")))
                        {
                            if (null != (mapX = subE.elementTextTrim("mapX")))
                            {
                                if (null != (mapY = subE.elementTextTrim("mapY")))
                                {
                                    tastTarget.setTransmitMapInfo(new short[]{
                                            Short.parseShort(mapID),
                                            Short.parseShort(mapX),
                                            Short.parseShort(mapY) });
                                }
                            }
                        }

                        killMonsterTargetList.put(tastTarget.getID(),
                                tastTarget);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        FastMap<Integer, BaseTaskTarget> goodsTargetList = new FastMap<Integer, BaseTaskTarget>();

        try
        {
            dataPath = new File(config.getGoodsTaskTargetDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                TaskTargetGoods tastTarget = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        log.debug("load task target goods id == " + subE.elementTextTrim("id"));
                        //物品目标
                        try {
                            tastTarget = new TaskTargetGoods(
                                    Integer.parseInt(subE.elementTextTrim("id")),
                                    (SingleGoods) GoodsServiceImpl.getInstance().getGoodsByID(
                                    		Integer.parseInt(subE.elementTextTrim("goodsID"))),
                                    Short.parseShort(subE.elementTextTrim("number"))
                                    );
						} catch (Exception e) {
							e.printStackTrace();
						}


                        if (null != (mapID = subE.elementTextTrim("mapID")))
                        {
                            if (null != (mapX = subE.elementTextTrim("mapX")))
                            {
                                if (null != (mapY = subE
                                        .elementTextTrim("mapY")))
                                {
                                    tastTarget.setTransmitMapInfo(new short[]{
                                            Short.parseShort(mapID),
                                            Short.parseShort(mapX),
                                            Short.parseShort(mapY) });
                                }
                            }
                        }

                        goodsTargetList.put(tastTarget.getID(), tastTarget);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        FastMap<Integer, BaseTaskTarget> escortNpcTargetList = new FastMap<Integer, BaseTaskTarget>();

        try
        {
            dataPath = new File(config.getEscortNpcTaskTargetDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                TaskTargetEscortNpc tastTarget = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        log.debug("load task target escort npc = " + subE.elementTextTrim("id"));
                        tastTarget = new TaskTargetEscortNpc(
                                Integer.parseInt(subE.elementTextTrim("id")),
                                subE.elementTextTrim("npcID").toLowerCase(),
                                Integer.parseInt(subE.elementTextTrim("totalTime")) * 60 * 1000,
                                Integer.parseInt(subE.elementTextTrim("mapID")),
                                Short.parseShort(subE.elementTextTrim("mapX")),
                                Short.parseShort(subE.elementTextTrim("mapY")),
                                Short.parseShort(subE.elementTextTrim("range")));

                        escortNpcTargetList.put(tastTarget.getID(), tastTarget);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        FastMap<Integer, BaseTaskTarget> foundPathTargetList = new FastMap<Integer, BaseTaskTarget>();

        try
        {
            dataPath = new File(config.getFoundPathTaskTargetDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                TaskTargetFoundAPath tastTarget = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        log.debug("load task target found path = " + subE.elementTextTrim("id"));
                        tastTarget = new TaskTargetFoundAPath(Integer
                                .parseInt(subE.elementTextTrim("id")), Short
                                .parseShort(subE.elementTextTrim("mapID")),
                                Short.parseShort(subE.elementTextTrim("mapX")),
                                Short.parseShort(subE.elementTextTrim("mapY")),
                                Short.parseShort(subE.elementTextTrim("range")));

                        foundPathTargetList.put(tastTarget.getID(), tastTarget);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        FastMap<Integer, BaseTaskTarget> openGearTargetList = new FastMap<Integer, BaseTaskTarget>();

        try
        {
            dataPath = new File(config.getOpenGearTaskTargetDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                TaskTargetOpenGear tastTarget = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                    	//机关目标
                        tastTarget = new TaskTargetOpenGear(Integer
                                .parseInt(subE.elementTextTrim("id")), subE
                                .elementTextTrim("npcID").toLowerCase());

                        if (null != (mapID = subE.elementTextTrim("mapID")))
                        {
                            if (null != (mapX = subE.elementTextTrim("mapX")))
                            {
                                if (null != (mapY = subE
                                        .elementTextTrim("mapY")))
                                {
                                    tastTarget.setTransmitMapInfo(new short[]{
                                            Short.parseShort(mapID),
                                            Short.parseShort(mapX),
                                            Short.parseShort(mapY) });
                                }
                            }
                        }

                        openGearTargetList.put(tastTarget.getID(), tastTarget);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        FastMap<Integer, String[]> descriptionList = new FastMap<Integer, String[]>();

        try
        {
            dataPath = new File(config.getDescDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                String[] descList = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        descList = new String[]{
                                subE.elementTextTrim("receiveContent"),
                                subE.elementTextTrim("viewContent"),
                                subE.elementTextTrim("submitContent") };

                        descriptionList.put(Integer.parseInt(subE
                                .elementTextTrim("id")), descList);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //add by zhengl; date: 2011-03-28; note: 添加任务推广配置
        try
        {
            dataPath = new File(config.getTaskPushPath());

            File[] dataFileList = dataPath.listFiles();

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

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        Push push = new Push();
                        push.id = Integer.parseInt(subE.elementTextTrim("id"));
                        push.pushNum = Integer.parseInt(subE.elementTextTrim("pushNum"));
                        push.commPushContent = String.valueOf(subE.elementTextTrim("commPushContent"));
                        push.commConfirmContent = String.valueOf(
                        		subE.elementTextTrim("commConfirmContent"));
                        push.point = Integer.parseInt(subE.elementTextTrim("point"));
                        push.goodsID = Integer.parseInt(subE.elementTextTrim("goods"));
                        push.time = Integer.parseInt(subE.elementTextTrim("countDown"));
                        
                        push.limitContent = new String[push.pushNum];
                        push.pushContent = new String[push.pushNum];
                        push.pushType = new int[push.pushNum];
                        int index = 0;
                        for (int i = 0; i < push.pushNum; i++) 
                        {
                        	index = i + 1;
                            push.limitContent[i] = String.valueOf(
                            		subE.elementTextTrim("limit"+index+"Content"));
                            push.pushContent[i] = String.valueOf(
                            		subE.elementTextTrim("push"+index+"Content"));
                            push.pushType[i] = Integer.valueOf(
                            		subE.elementTextTrim("push"+index+"Type"));
						}
                        pushDataTable.put(push.id, push);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            dataPath = new File(config.getTaskDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                Task task = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        task = new Task(Integer.parseInt(subE
                                .elementTextTrim("id")), subE
                                .elementTextTrim("name"), Short.parseShort(subE
                                .elementTextTrim("level")), subE
                                .elementTextTrim("isRepeated").equals("是"));

                        task.setDifficultyLevel(ETaskDifficultyLevel.get(subE
                                .elementTextTrim("difficultyLevel")));
                        //add by zhengl; date: 2011-03-28; note: 添加任务推广信息
                        String taskPushID = subE.elementTextTrim("taskFeeMode");
                        if(taskPushID != null && !taskPushID.equals(""))
                        {
                        	int pushID = Integer.parseInt(taskPushID);
                        	task.setTaskPush(this.pushDataTable.get(pushID));
                        }
                        //end

                        Condition condition = new Condition();

                        condition.vocation = EVocation.getVocationByDesc(subE
                                .elementTextTrim("vocation"));
                        condition.clan = EClan.getClanByDesc(subE
                                .elementTextTrim("clan"));
                        condition.level = Short.parseShort(subE
                                .elementTextTrim("needLevel"));
                        log.debug("task["+task.getName()+"], vovation=" +condition.vocation +" -- clan="+condition.clan 
                        		+ " -- needlevel= " + condition.level);
                        String completedTaskID = subE
                                .elementTextTrim("completedTaskID");
                        if (null != completedTaskID)
                        {
                            condition.completeTaskID = Integer
                                    .parseInt(completedTaskID);
                        }
                        //add by zhengl; date: 2011-03-25; note: 添加后续任务
                        String taskNext = subE.elementTextTrim("taskNext");
                        if (null != taskNext)
                        {
                        	condition.taskNext = Integer.parseInt(taskNext);
                        }
                        //add by zhengl; date: 2011-04-29; note: 主线任务标记
                        String data = subE.elementTextTrim("isMainLine");
                        if (null != data)
                        {
                        	if (data.equals("是")) 
                        	{
                        		task.setMainLine();
							}
                        }
                        
                        String needManufSkillType = subE.elementTextTrim("needManufSkill");
                        if(null != needManufSkillType){
                            condition.manufactureType = ManufactureType.get(Byte.parseByte(needManufSkillType));
                        }
                        String taskType = subE.elementTextTrim("taskType");
                        if(null != taskType){
                            condition.taskType = TaskType.getTaskTypeByType(taskType);
                        }else{
                            condition.taskType = TaskType.SINGLE;
                        }
                        log.debug("task ["+task.getName()+"] -- type="+condition.taskType);
                        task.setCondition(condition);
                        task.setDescList(descriptionList.get(Integer
                                .parseInt(subE.elementTextTrim("descListID"))));

                        for (int i = 1; i <= 5; i++)
                        {
                            ETastTargetType targetType = ETastTargetType
                                    .getTastTargetTypeByDesc(subE
                                            .elementTextTrim("target" + i
                                                    + "Type"));

                            if (null != targetType)
                            {
                                int targetID = Integer.parseInt(subE
                                        .elementTextTrim("target" + i + "ID"));

                                BaseTaskTarget taskTarget = null;

                                switch (targetType)
                                {
                                    case KILL_MONSTER:
                                    {
                                        taskTarget = killMonsterTargetList
                                                .get(targetID);

                                        break;
                                    }
                                    case GOODS:
                                    {
                                        taskTarget = goodsTargetList
                                                .get(targetID);

                                        break;
                                    }
                                    case ESCORT_NPC:
                                    {
                                        taskTarget = escortNpcTargetList
                                                .get(targetID);

                                        break;
                                    }
                                    case FOUND_A_PATH:
                                    {
                                        taskTarget = foundPathTargetList
                                                .get(targetID);

                                        if (null != taskTarget)
                                        {
                                            addFoundPathTask(
                                                    task.getID(),
                                                    (TaskTargetFoundAPath) taskTarget);
                                        }

                                        break;
                                    }
                                    case OPEN_GEAR:
                                    {
                                        taskTarget = openGearTargetList
                                                .get(targetID);

                                        break;
                                    }
                                }

                                if (null != taskTarget)
                                {
                                    task.addTarget(taskTarget);
                                }
                            }
                            else
                            {
                                break;
                            }
                        }

                        task.setDistributeNpcModelID(subE.elementTextTrim(
                                "distributeNpcID").toLowerCase());

                        for (int i = 1; i <= 3; i++)
                        {
                            String goodsID = subE
                                    .elementTextTrim("receiveGoods" + i + "ID");

                            if (null != goodsID)
                            {
                                short number = Short.parseShort(subE
                                        .elementTextTrim("receiveGoods" + i
                                                + "Nums"));

                                task.addReceiveGoods(Integer.parseInt(goodsID),
                                        number);
                            }
                            else
                            {
                                break;
                            }
                        }

                        String effectID = subE
                                .elementTextTrim("receiveEffectID");
                        if (null != effectID)
                        {
                            task.setReceiveEffectID(Integer.parseInt(effectID));
                        }

                        task.setSubmitNpcID(subE.elementTextTrim("submitNpcID")
                                .toLowerCase());

                        Award award = new Award();

                        String temp = subE.elementTextTrim("awardExp");

                        if (null != temp)
                        {
                            award.experience = Integer.parseInt(temp);
                        }

                        temp = subE.elementTextTrim("awardMoney");
                        if (null != temp)
                        {
                            award.money = Integer.parseInt(temp);
                        }
                        //add by zhengl; date: 2011-02-22; note: 奖励传送至某地图
                        temp = subE.elementTextTrim("endToMapID");
                        if (null != temp)
                        {
                            award.mapID = Short.parseShort(temp);
                        }
                        else 
                        {
                        	award.mapID = -1;
						}
                        temp = subE.elementTextTrim("endToMapX");
                        if (null != temp)
                        {
                            award.mapX = Short.parseShort(temp);
                        }
                        else 
                        {
                        	award.mapX = -1;
						}
                        temp = subE.elementTextTrim("endToMapY");
                        if (null != temp)
                        {
                            award.mapY = Short.parseShort(temp);
                        }
                        else 
                        {
                        	award.mapY = -1;
						}
                        //end
                        

                        temp = subE.elementTextTrim("awardSkillID");
                        if (null != temp)
                        {
                            award.skillID = Integer.parseInt(temp);
                        }

                        temp = subE.elementTextTrim("awardEffectID");
                        if (null != temp)
                        {
                            award.effectID = Integer.parseInt(temp);
                        }

                        Goods goods;

                        for (int i = 1; i <= 5; i++)
                        {
                            String goodsID = subE.elementTextTrim("sAwardGood"
                                    + i + "ID");

                            if (null != goodsID)
                            {
                                goods = GoodsServiceImpl
                                        .getInstance()
                                        .getGoodsByID(Integer.parseInt(goodsID));

                                if (null != goods)
                                {
                                    short number = Short.parseShort(subE
                                            .elementTextTrim("sAwardGood" + i
                                                    + "Nums"));
                                    award.addOptionalGoods(goods, number);
                                }
                                else
                                {
                                    LogWriter.println("加载任务可选奖励物品错误："
                                            + task.getName() + "  物品编号："
                                            + goodsID);
                                }
                            }
                            else
                            {
                                break;
                            }
                        }

                        for (int i = 1; i <= 5; i++)
                        {
                            String goodsID = subE.elementTextTrim("gAwardGood"
                                    + i + "ID");

                            if (null != goodsID)
                            {
                                goods = GoodsServiceImpl
                                        .getInstance()
                                        .getGoodsByID(Integer.parseInt(goodsID));

                                if (null != goods)
                                {
                                    short number = 0;
                                    try 
                                    {
										number = Short.parseShort(
												subE.elementTextTrim("gAwardGood" + i + "Nums"));
									} catch (Exception e) {
										System.out.println("加载物品"+goodsID+"的数量失败");
										e.printStackTrace();
										break;
									}

                                    award.addBoundGoods(GoodsServiceImpl
                                            .getInstance().getGoodsByID(
                                                    Integer.parseInt(goodsID)),
                                            number);
                                }
                                else
                                {
                                    LogWriter.println("加载任务必得奖励物品错误："
                                            + task.getName() + "  物品编号："
                                            + goodsID);
                                }
                            }
                            else
                            {
                                break;
                            }
                        }

                        task.setAward(award);

                        taskDictionary.put(task.getID(), task);

                        ArrayList<Task> list = npcTaskDictionary.get(task
                                .getDistributeNpcModelID());

                        if (null == list)
                        {
                            list = new ArrayList<Task>();
                            npcTaskDictionary.put(task
                                    .getDistributeNpcModelID(), list);
                        }

                        list.add(task);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            dataPath = new File(config.getMonsterTaskGoodsDataPath());

            File[] dataFileList = dataPath.listFiles();

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

                ArrayList<MonsterTaskGoodsSetting> list = null;
                MonsterTaskGoodsSetting set = null;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        String monsterModelID = subE
                                .elementTextTrim("monsterID");

                        list = monsterTaskGoodsDictory.get(monsterModelID);

                        if (null == list)
                        {
                            list = new ArrayList<MonsterTaskGoodsSetting>();
                            monsterTaskGoodsDictory.put(monsterModelID, list);
                        }

                        set = new MonsterTaskGoodsSetting(
                                Integer.parseInt(subE
                                        .elementTextTrim("goodsID")),
                                Integer
                                        .parseInt(subE
                                                .elementTextTrim("taskID")),
                                Integer.parseInt(subE.elementTextTrim("odds")) / 100F,
                                Short.parseShort(subE
                                        .elementTextTrim("maxNumber")));

                        list.add(set);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 根据怪物模板编号获取任务物品掉落
     * 
     * @param _monsterModelID
     * @return
     */
    public ArrayList<MonsterTaskGoodsSetting> getTaskGoodsDropList (
            String _monsterModelID)
    {
        return monsterTaskGoodsDictory.get(_monsterModelID);
    }

    /**
     * 能否接受任务
     * 
     * @param _player
     * @param _task
     * @return
     */
    private boolean canReceiveTask (HeroPlayer _player, Task _task)
    {
        ArrayList<TaskInstance> taskList = playerExsitsTaskListMap.get(_player
                .getUserID());
        log.debug("exists task list ...");
        for (TaskInstance task : taskList)
        {
            log.debug("exists task id = " + task.getArchetype().getID() +" ;; _taks id =" + _task.getID());
            if (task.getArchetype().getID() == _task.getID())
            {
                return false;
            }
        }
        log.debug("next completed task list ....");
        ArrayList<Integer> completedTaskList = playerCompletedTaskListMap
                .get(_player.getUserID());

        if (!_task.isRepeated())
        {
            if (completedTaskList.contains(_task.getID()))
            {
                return false;
            }
        }
        log.debug("next condition task : "+ _task.getName());
        return _task.getCondition().check(_player.getUserID(),_player.getVocation(),
                _player.getClan(), _player.getLevel(), completedTaskList);
    }

    /**
     * 是否已经完成某任务
     * 
     * @param _userID 角色userID
     * @param _taskID 任务编号
     * @return
     */
    public boolean hasCompleteTask (int _userID, int _taskID)
    {
        ArrayList<Integer> completedTaskList = playerCompletedTaskListMap
                .get(_userID);

        if (null != completedTaskList && completedTaskList.contains(_taskID))
        {
            return true;
        }

        return false;
    }

    /**
     * 处理怪物死亡后与任务相关的处理(杀怪类型任务目标)
     * 
     * @param _monster 死亡的怪物
     * @param _killer 怪物死亡所属玩家
     */
    public ArrayList<TaskGoodsLegacyInfo> processTaskAboutMonster (
            Monster _monster, HeroPlayer _killer)
    {
        if (_killer.getGroupID() > 0)
        {
            Group group = GroupServiceImpl.getInstance().getGroup(
                    _killer.getGroupID());

            if (null != group)
            {
                return processTaskAboutMonsterInGroup(_monster, group);
            }
        }
        else
        {
            return processTaskAboutMonsterPersonally(_monster, _killer);
        }

        return null;
    }

    /**
     * 处理团队中的杀怪类型任务
     * 
     * @param _monster 被杀死的怪物
     * @param _player 角色
     * @param _group 角色所在的团队
     */
    private ArrayList<TaskGoodsLegacyInfo> processTaskAboutMonsterInGroup (
            Monster _monster, Group _group)
    {
        ArrayList<TaskGoodsLegacyInfo> legacyTaskGoodsInfoList = null;

        ArrayList<HeroPlayer> playerList = _group
                .getValidatePlayerList(_monster.where().getID());

        ArrayList<TaskInstance> memberTaskList = null;

        for (HeroPlayer player : playerList)
        {
            memberTaskList = playerExsitsTaskListMap.get(player.getUserID());

            for (TaskInstance task : memberTaskList)
            {
                if (null != _group)
                {
                    ETaskDifficultyLevel difficultyLevel = task.getArchetype()
                            .getDifficultyLevel();

                    if (ETaskDifficultyLevel.EASY == difficultyLevel
                            || ETaskDifficultyLevel.DIFFICULT == difficultyLevel)
                    {
                        if (_group.getMemberNumber() > Group.MAX_NUMBER_OF_MEMBER)
                        {
                            continue;
                        }
                    }
                }

                if (!task.isCompleted())
                {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();

                    for (BaseTaskTarget target : targetList)
                    {
                        if (!target.isCompleted())
                        {
                            if (ETastTargetType.KILL_MONSTER == target
                                    .getType())
                            {
                                if (((TaskTargetKillMonster) target).monsterModelID
                                        .equals(_monster.getModelID()))
                                {
                                    ((TaskTargetKillMonster) target)
                                            .numberChanged(1);

                                    TaskDAO.updateTaskProgress(player
                                            .getUserID(), task);

                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new RefreshTaskStatus(task
                                                    .getArchetype().getID(),
                                                    target.getID(), target
                                                            .isCompleted(),
                                                    target.getDescripiton(),
                                                    task.isCompleted()));

                                    if (task.isCompleted())
                                    {
                                        Npc taskNpc = NotPlayerServiceImpl
                                                .getInstance()
                                                .getNpc(
                                                        task
                                                                .getArchetype()
                                                                .getSubmitNpcID());

                                        if (null != taskNpc
                                                && taskNpc.where() == player
                                                        .where())
                                        {
                                            ResponseMessageQueue
                                                    .getInstance()
                                                    .put(
                                                            player
                                                                    .getMsgQueueIndex(),
                                                            new ChangeNpcTaskMark(
                                                                    taskNpc
                                                                            .getID(),
                                                                    Npc.SUBMIT_TASK_MARK));
                                        }
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList<MonsterTaskGoodsSetting> monsterTaskGoodsList = monsterTaskGoodsDictory
                .get(_monster.getModelID());

        if (null != monsterTaskGoodsList)
        {
            for (MonsterTaskGoodsSetting monsterTaskGoods : monsterTaskGoodsList)
            {
                TaskGoodsLegacyInfo legacyTaskGoodsInfo = null;

                boolean goodsChecked = false;

                Task taskModel = getTask(monsterTaskGoods.taskID);

                if (null != _group && _group.getMemberNumber() > Group.MAX_NUMBER_OF_MEMBER)
                {
                    if (taskModel.getDifficultyLevel() != ETaskDifficultyLevel.NIGHTMARE)
                    {
                        continue;
                    }
                }

                for (HeroPlayer player : playerList)
                {
                    boolean playerChecked = false;

                    if (player.isEnable() && player.where() == _monster.where())
                    {
                        memberTaskList = playerExsitsTaskListMap.get(player
                                .getUserID());

                        for (TaskInstance task : memberTaskList)
                        {
                            if (playerChecked)
                            {
                                break;
                            }

                            if (task.getArchetype() == taskModel
                                    && !task.isCompleted())
                            {
                                ArrayList<BaseTaskTarget> targetList = task
                                        .getTargetList();

                                for (BaseTaskTarget target : targetList)
                                {
                                    if (ETastTargetType.GOODS == target
                                            .getType()
                                            && !target.isCompleted()
                                            && ((TaskTargetGoods) target).goods
                                                    .getID() == monsterTaskGoods.goodsID)
                                    {
                                        if (goodsChecked)
                                        {
                                            if (null != legacyTaskGoodsInfo)
                                            {
                                                legacyTaskGoodsInfo
                                                        .addPicker(player
                                                                .getUserID());
                                            }
                                        }
                                        else
                                        {
                                            int number = monsterTaskGoods
                                                    .getDropNumber();

                                            if (number > 0)
                                            {
                                                legacyTaskGoodsInfo = new TaskGoodsLegacyInfo(
                                                        task.getArchetype()
                                                                .getID(),
                                                        monsterTaskGoods.goodsID,
                                                        number);

                                                if (null == legacyTaskGoodsInfoList)
                                                {
                                                    legacyTaskGoodsInfoList = new ArrayList<TaskGoodsLegacyInfo>();
                                                }

                                                legacyTaskGoodsInfo
                                                        .addPicker(player
                                                                .getUserID());

                                                legacyTaskGoodsInfoList
                                                        .add(legacyTaskGoodsInfo);
                                            }

                                            goodsChecked = true;
                                        }

                                        playerChecked = true;

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return legacyTaskGoodsInfoList;
    }

    /**
     * 处理非组队下的杀怪和获取怪物掉落物品类型任务
     * 
     * @param _monster 被杀死的怪物
     * @param _player 角色
     */
    private ArrayList<TaskGoodsLegacyInfo> processTaskAboutMonsterPersonally (
            Monster _monster, HeroPlayer _player)
    {
        ArrayList<TaskGoodsLegacyInfo> legacyTaskGoodsInfoList = null;

        ArrayList<TaskInstance> taskList = playerExsitsTaskListMap.get(_player
                .getUserID());

        if (null != taskList)
        {
            for (TaskInstance task : taskList)
            {
                if (!task.isCompleted())
                {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();

                    for (BaseTaskTarget target : targetList)
                    {
                        if (!target.isCompleted())
                        {
                            if (ETastTargetType.KILL_MONSTER == target
                                    .getType())
                            {
                                if (((TaskTargetKillMonster) target).monsterModelID
                                        .equals(_monster.getModelID()))
                                {
                                    ((TaskTargetKillMonster) target)
                                            .numberChanged(1);

                                    TaskDAO.updateTaskProgress(_player
                                            .getUserID(), task);

                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new RefreshTaskStatus(task
                                                    .getArchetype().getID(),
                                                    target.getID(), target
                                                            .isCompleted(),
                                                    target.getDescripiton(),
                                                    task.isCompleted()));

                                    if (task.isCompleted())
                                    {
                                        Npc taskNpc = NotPlayerServiceImpl
                                                .getInstance()
                                                .getNpc(
                                                        task
                                                                .getArchetype()
                                                                .getSubmitNpcID());

                                        if (null != taskNpc
                                                && taskNpc.where() == _player
                                                        .where())
                                        {
                                            ResponseMessageQueue
                                                    .getInstance()
                                                    .put(
                                                            _player
                                                                    .getMsgQueueIndex(),
                                                            new ChangeNpcTaskMark(
                                                                    taskNpc
                                                                            .getID(),
                                                                    Npc.SUBMIT_TASK_MARK));
                                        }
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }

            ArrayList<MonsterTaskGoodsSetting> monsterTaskGoodsList = monsterTaskGoodsDictory
                    .get(_monster.getModelID());

            if (null != monsterTaskGoodsList)
            {
                for (MonsterTaskGoodsSetting monsterTaskGoods : monsterTaskGoodsList)
                {
                    TaskGoodsLegacyInfo legacyTaskGoodsInfo = null;

                    boolean goodsCheck = false;

                    for (TaskInstance task : taskList)
                    {
                        if (goodsCheck)
                        {
                            break;
                        }

                        if (task.getArchetype().getID() == monsterTaskGoods.taskID
                                && !task.isCompleted())
                        {
                            ArrayList<BaseTaskTarget> targetList = task
                                    .getTargetList();

                            for (BaseTaskTarget target : targetList)
                            {
                                if (ETastTargetType.GOODS == target.getType()
                                        && !target.isCompleted())
                                {
                                    if (((TaskTargetGoods) target).goods
                                            .getID() == monsterTaskGoods.goodsID)
                                    {
                                        int number = monsterTaskGoods
                                                .getDropNumber();

                                        if (number > 0)
                                        {
                                            legacyTaskGoodsInfo = new TaskGoodsLegacyInfo(
                                                    task.getArchetype().getID(),
                                                    monsterTaskGoods.goodsID,
                                                    number);

                                            if (null == legacyTaskGoodsInfoList)
                                            {
                                                legacyTaskGoodsInfoList = new ArrayList<TaskGoodsLegacyInfo>();
                                            }

                                            legacyTaskGoodsInfoList
                                                    .add(legacyTaskGoodsInfo);
                                        }

                                        goodsCheck = true;

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return legacyTaskGoodsInfoList;
    }
    
    /**
     * 返回该Npc的该玩家无法接收的任务
     * @param _npcModelID
     * @param _player
     * @return
     */
    public ArrayList<Task> getNotReceTaskList (String _npcModelID, HeroPlayer _player)
    {
        ArrayList<Task> npcTaskList = npcTaskDictionary.get(_npcModelID);

        if (null != npcTaskList)
        {
            ArrayList<Task> notReceive = new ArrayList<Task>();

            for (Task task : npcTaskList)
            {
            	if (!canReceiveTask(_player, task))
            	{
            		notReceive.add(task);
            	}
            }

            if (notReceive.size() != 0)
            {
                return notReceive;
            }
            else
            {
                return null;
            }
        }

        return null;
    }

    /**
     * 获取玩家在NPC处可接受的
     * 
     * @param _npcModelID
     * @param _player
     * @return
     */
    public ArrayList<Task> getReceiveableTaskList (String _npcModelID,
            HeroPlayer _player)
    {
        ArrayList<Task> npcTaskList = npcTaskDictionary.get(_npcModelID);

        if (null != npcTaskList)
        {
            ArrayList<Task> canReceiveTaskList = new ArrayList<Task>();

            for (Task task : npcTaskList)
            {
                log.debug("task["+ task.getName() +"] type="+ task.getCondition().taskType);
                if (canReceiveTask(_player, task))
                {
                    canReceiveTaskList.add(task);
                }
            }

            if (canReceiveTaskList.size() != 0)
            {
                return canReceiveTaskList;
            }
            else
            {
                return null;
            }
        }

        return null;
    }

    /**
     * 获取玩家在NPC处可接受的高效任务，数量最多20个
     * 
     * @param _npcModelID
     * @param _player
     * @return
     */
    public ArrayList<Task> getReceiveableTaskList (HeroPlayer _player)
    {
        ArrayList<TaskInstance> existsTaskList = playerExsitsTaskListMap
                .get(_player.getUserID());
        ArrayList<Integer> completedTaskIDList = playerCompletedTaskListMap
                .get(_player.getUserID());

        Iterator<Task> taskList = taskDictionary.values().iterator();
        Task task;
        ArrayList<Task> canReceiveableTaskList = null;
        boolean validateTask;

        while (taskList.hasNext())
        {
            validateTask = true;
            task = taskList.next();

            if (!task.isRepeated())
            {
                if (completedTaskIDList.contains(task.getID()))
                {
                    continue;
                }
            }

            if (task.getCondition().check(_player.getUserID(),_player.getVocation(),
                    _player.getClan(), _player.getLevel(), completedTaskIDList))
            {
            	//edit by zhengl; date: 2011-02-20; note: 
//                if (_player.getLevel() - task.getCondition().level <= 3)
            	if (_player.getLevel() - task.getCondition().level > -1)
                {
                    for (TaskInstance taskIns : existsTaskList)
                    {
                        if (taskIns.getArchetype().getID() == task.getID())
                        {
                            validateTask = false;

                            break;
                        }
                    }

                    if (validateTask)
                    {
                        if (null == canReceiveableTaskList)
                        {
                            canReceiveableTaskList = new ArrayList<Task>();
                        }

                        canReceiveableTaskList.add(task);
                        //del by zhengl; date: 2011-02-20; note: 所有任务均需要
//                        if (canReceiveableTaskList.size() == 20)
//                        {
//                            break;
//                        }
                    }
                }
            }
        }//while end

        if(canReceiveableTaskList != null && canReceiveableTaskList.size()>1){
            Collections.sort(canReceiveableTaskList,new TaskComparator());
        }

        //add by zhengl; date: 2011-02-20; note: 大于20个任务则取等级最高的前20个任务
        ArrayList<Task> returnTasks = new ArrayList<Task>();
        if(canReceiveableTaskList != null && canReceiveableTaskList.size() 
        		> config.can_receive_task_number)
        {
//        	canReceiveableTaskList = this.getSortTasks(canReceiveableTaskList);
        	/*Task addTask = null;
        	int size = canReceiveableTaskList.size();
        	for (int i = 0; i < size; i++) {
        		addTask = canReceiveableTaskList.get(i);
        		if (i < 20) 
        		{
        			returnTasks.add(addTask);
				}
        		else if (addTask.getMainLine()) 
        		{
        			returnTasks.add(0, addTask);
				}
			}*/
            for(int i=0; i<20; i++){
                Task t = canReceiveableTaskList.get(i);
                returnTasks.add(t);
            }
//            returnTasks = canReceiveableTaskList;
        }
        else 
        {
        	returnTasks = canReceiveableTaskList;
		}

        return returnTasks;
    }
    
    private ArrayList<Task> getSortTasks (ArrayList<Task> _list)
    {
    	//数组化
    	Task[] tasks = new Task[_list.size()];
    	for (int i = 0; i < _list.size(); i++) {
    		tasks[i] = _list.get(i);
		}
    	//排序
    	Task k = null;
		for (int i = 0; i < tasks.length; i++) {
			for (int j = i + 1; j < tasks.length; j++)
				if( tasks[i].getLevel() > tasks[j].getLevel() ) {
					k = tasks[i];
					tasks[i] = tasks[j];
					tasks[j] = k;
				}
		}
		//再次填充
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (int i = tasks.length-1; i > -1; i--) {
			taskList.add(tasks[i]);
		}
    	return taskList;
    }

    /**
     * 接受任务
     * 
     * @param _player 玩家
     * @param _taskID 任务编号
     */
    public void receiveTask (HeroPlayer _player, int _taskID)
    {
        Task task = taskDictionary.get(_taskID);

        if (null != task)
        {
            if(task.isRepeated()){
                log.debug(_player.getName()+" 接收循环任务["+task.getName()+"],receivedRepeateTaskTimes="+_player.receivedRepeateTaskTimes);
                if(_player.canReceiveRepeateTask()){
                    _player.receivedRepeateTaskTimes++;
                    //add by zhengl; date: 2011-05-15; note: 在更新玩家数据之后即时也更新数据库
                    PlayerDAO.updateRepeateTask(_player);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new NotifyPlayerReciveRepeateTaskTimes(_player));
                }else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning("对不起，今日您的\"循环任务\"已接收 " + _player.receivedRepeateTaskTimes+
                                                " 次，如果您想继续接收\"循环任务\",请到商城购买\"任务刷新卷轴\"道具。",
                                    Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                    return;
                }
            }

            ArrayList<TaskInstance> taskList = playerExsitsTaskListMap
                    .get(_player.getUserID());

            synchronized (taskList)
            {
                for (TaskInstance existsTask : taskList)
                {
                    if (existsTask.getArchetype().getID() == _taskID)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_TASK_SERVICE_OF_HAS, Warning.UI_STRING_TIP));

                        return;
                    }
                }

                if (playerCompletedTaskListMap.get(_player.getUserID())
                        .contains(_taskID))
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_TASK_SERVICE_OF_COMPETED, Warning.UI_STRING_TIP));

                    return;
                }

                if (null != task.getReceiveGoodsList()
                        && task.getReceiveGoodsList().size() < _player
                                .getInventory().getTaskToolBag()
                                .getEmptyGridNumber())
                {
                    ArrayList<int[]> receiveGoodsList = task
                            .getReceiveGoodsList();
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();

                    for (int[] goods : receiveGoodsList)
                    {
                        GoodsServiceImpl.getInstance()
                                .addGoods2Package(
                                        _player,
                                        GoodsServiceImpl.getInstance()
                                                .getGoodsByID(goods[0]),
                                        goods[1], CauseLog.TASKAWARD);

                        for (BaseTaskTarget target : targetList)
                        {
                            if (ETastTargetType.GOODS == target.getType()
                                    && ((TaskTargetGoods) target).goods.getID() == goods[0])
                            {
                                ((TaskTargetGoods) target)
                                        .numberChanged(goods[1]);

                                break;
                            }
                        }
                    }
                }

                TaskDAO.insertNewTask(_player.getUserID(), task);
                //add by zhengl; date: 2011-03-28; note: 进行任务推广
                //暂时删除
//                Push push = task.getTaskPush();
//                if (push != null) 
//                {
//                	//任务推广第1步骤,这个事件将引导客户端触发
//                    OutMsgQ.getInstance().put(
//                            _player.getMsgQueueIndex(),
//                            new Warning(push.commPushContent, 
//                            		Warning.UI_COMPLEX_TIP, 
//                            		Warning.SUBFUNCTION_UI_TASK_PUSH_COMM, 
//                            		push.id));
//				}
                TaskInstance taskInstance = new TaskInstance(task);
                taskList.add(taskInstance);

                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new TaskListChangerNotify(TaskListChangerNotify.ADD,
                                taskInstance));

                Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(
                        task.getDistributeNpcModelID());
                //edit by zhengl; date: 2011-04-17; note: 解决副本中NPC的叹号无法刷新的问题
                if (null != taskNpc && taskNpc.where().getID() == _player.where().getID())
                {
                    byte npcTaskMarks = getTaskMark(task
                            .getDistributeNpcModelID(), _player);

                    ResponseMessageQueue.getInstance()
                            .put(
                                    _player.getMsgQueueIndex(),
                                    new ChangeNpcTaskMark(taskNpc.getID(),
                                            npcTaskMarks));
                }

                taskNpc = NotPlayerServiceImpl.getInstance().getNpc(
                        task.getSubmitNpcID());

                if (taskInstance.isCompleted() && null != taskNpc
                        && taskNpc.where() == _player.where())
                {
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ChangeNpcTaskMark(taskNpc.getID(),
                                    Npc.SUBMIT_TASK_MARK));
                }

                if (null != _player.where().getTaskGearList())
                {
                    ArrayList<TaskGear> gearList = _player.where()
                            .getTaskGearList();

                    if (gearList.size() > 0)
                    {
                        for (TaskGear gear : gearList)
                        {
                            if (task.getID() == gear.getTaskIDAbout())
                            {
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new ChangeTaskGearStat(gear.getID(),
                                                gear.getCellY(), true));
                            }
                        }
                    }
                }

                if (null != _player.where().getGroundTaskGoodsList())
                {
                    ArrayList<GroundTaskGoods> groundTaskGoodsList = _player
                            .where().getGroundTaskGoodsList();

                    if (groundTaskGoodsList.size() > 0)
                    {
                        for (GroundTaskGoods taskGoods : groundTaskGoodsList)
                        {
                            if (task.getID() == taskGoods.getTaskIDAbout())
                            {
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new ChangeTaskGearStat(taskGoods
                                                .getID(), taskGoods.getCellY(),
                                                true));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 放弃任务
     * 
     * @param _player
     * @param _taskID
     */
    public void cancelTask (HeroPlayer _player, int _taskID)
    {
        ArrayList<TaskInstance> existsTaskList = playerExsitsTaskListMap
                .get(_player.getUserID());;

        for (TaskInstance existsTask : existsTaskList)
        {
            if (existsTask.getArchetype().getID() == _taskID)
            {
                existsTaskList.remove(existsTask);

                TaskDAO.deleteTask(_player.getUserID(), _taskID);

                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new TaskListChangerNotify(TaskListChangerNotify.CANCEL,
                                existsTask));

                if (null != existsTask.getArchetype().getReceiveGoodsList())
                {
                    ArrayList<int[]> receiveGoodsList = existsTask
                            .getArchetype().getReceiveGoodsList();

                    for (int[] goods : receiveGoodsList)
                    {
                        try
                        {
                            GoodsServiceImpl.getInstance().deleteSingleGoods(
                                    _player,
                                    _player.getInventory().getTaskToolBag(),
                                    GoodsServiceImpl.getInstance()
                                            .getGoodsByID(goods[0]),
                                    CauseLog.CANCELTASK);
                        }
                        catch (BagException pe)
                        {
                            pe.printStackTrace();
                        }
                    }
                }

                if (null != existsTask.getTargetList())
                {
                    ArrayList<BaseTaskTarget> targetList = existsTask
                            .getTargetList();

                    for (BaseTaskTarget target : targetList)
                    {
                        if (ETastTargetType.GOODS == target.getType())
                        {
                            try
                            {
                                GoodsServiceImpl
                                        .getInstance()
                                        .deleteSingleGoods(
                                                _player,
                                                _player.getInventory()
                                                        .getTaskToolBag(),
                                                ((TaskTargetGoods) target).goods,
                                                CauseLog.CANCELTASK);

                                if (_player.where().getGroundTaskGoodsList()
                                        .size() > 0)
                                {
                                    for (GroundTaskGoods taskGoods : _player
                                            .where().getGroundTaskGoodsList())
                                    {
                                        if (taskGoods.getTaskToolIDAbout() == ((TaskTargetGoods) target).goods
                                                .getID())
                                        {
                                            ResponseMessageQueue
                                                    .getInstance()
                                                    .put(
                                                            _player
                                                                    .getMsgQueueIndex(),
                                                            new ChangeGroundTaskGoodsStat(
                                                                    taskGoods
                                                                            .getID(),
                                                                    taskGoods
                                                                            .getCellY(),
                                                                    false));
                                        }
                                    }
                                }
                            }
                            catch (BagException pe)
                            {
                                pe.printStackTrace();
                            }
                        }
                        else if (ETastTargetType.OPEN_GEAR == target.getType())
                        {
                            if (_player.where().getTaskGearList().size() > 0)
                            {
                                for (TaskGear gear : _player.where()
                                        .getTaskGearList())
                                {
                                    if (gear
                                            .getModelID()
                                            .equals(
                                                    ((TaskTargetOpenGear) target).gearModelID))
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new ChangeTaskGearStat(gear
                                                        .getID(), gear
                                                        .getCellY(), false));

                                        break;
                                    }
                                }
                            }
                        }
                        else if (ETastTargetType.ESCORT_NPC == target.getType())
                        {
                            synchronized (escortTaskExcuteList)
                            {
                                EscortNpcTaskInfo info;

                                for (int i = 0; i < escortTaskExcuteList.size(); i++)
                                {
                                    info = escortTaskExcuteList.get(i);

                                    if (info.trigger == _player
                                            && null != _player
                                                    .getEscortTarget()
                                            && _player
                                                    .getEscortTarget()
                                                    .getModelID()
                                                    .equals(
                                                            ((TaskTargetEscortNpc) target).npcModelID))
                                    {
                                        escortTaskExcuteList.remove(i);
                                        info.npc.stopFollowTask();
                                        _player.setEscortTarget(null);

                                        info.trigger = null;
                                        info.npc.setCellX(info.npc.getOrgX());
                                        info.npc.setCellY(info.npc.getOrgY());

                                        if (info.npc.where() != info.npc
                                                .getOrgMap())
                                        {
                                            info.npc.gotoMap(info.npc
                                                    .getOrgMap());
                                        }
                                        else
                                        {
                                            MapSynchronousInfoBroadcast
                                                    .getInstance()
                                                    .put(
                                                            info.npc.where(),
                                                            new NpcResetNotify(
                                                                    info.npc
                                                                            .getID(),
                                                                    info.npc
                                                                            .getCellX(),
                                                                    info.npc
                                                                            .getCellY()),
                                                            false, 0);
                                        }

                                        info.npc = null;

                                        if (null != info.spareTaskMemberList)
                                        {
                                            for (HeroPlayer other : info.spareTaskMemberList)
                                            {
                                                ResponseMessageQueue.getInstance().put(
                                                		other.getMsgQueueIndex(),
                                                		new Warning(
                                                				"‘"+ existsTask.getArchetype().getName()+ "’" + "任务失败", 
                                                				Warning.UI_STRING_TIP));
                                            }

                                            info.spareTaskMemberList.clear();
                                            info.spareTaskMemberList = null;
                                        }

                                        break;
                                    }

                                    if (null != info.spareTaskMemberList)
                                    {
                                        if (info.spareTaskMemberList
                                                .contains(_player))
                                        {
                                            info.spareTaskMemberList
                                                    .remove(_player);
                                            _player.setEscortTarget(null);

                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(
                        existsTask.getArchetype().getDistributeNpcModelID());

                if (null != taskNpc && taskNpc.where() == _player.where())
                {
                    byte npcTaskMarks = getTaskMark(taskNpc.getModelID(),
                            _player);

                    ResponseMessageQueue.getInstance()
                            .put(
                                    _player.getMsgQueueIndex(),
                                    new ChangeNpcTaskMark(taskNpc.getID(),
                                            npcTaskMarks));
                }

                if (existsTask.isCompleted())
                {
                    taskNpc = NotPlayerServiceImpl.getInstance().getNpc(
                            existsTask.getArchetype().getSubmitNpcID());

                    if (null != taskNpc && taskNpc.where() == _player.where())
                    {
                        byte npcTaskMarks = getTaskMark(taskNpc.getModelID(),
                                _player);

                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ChangeNpcTaskMark(taskNpc.getID(),
                                        npcTaskMarks));
                    }
                }

                return;
            }
        }

        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new Warning("不存在的任务", Warning.UI_STRING_TIP));
    }

    /**
     * 传送到任务目标的目的地
     * 
     * @param _player
     * @param _taskID
     * @param _targetID
     */
    public boolean transmitToTaskTarget (HeroPlayer _player, int _taskID,
            int _targetID)
    {
        ArrayList<TaskInstance> existsTaskList = playerExsitsTaskListMap
                .get(_player.getUserID());;

        for (TaskInstance existsTask : existsTaskList)
        {
            if (existsTask.getArchetype().getID() == _taskID)
            {
                if (null != existsTask.getTargetList())
                {
                    ArrayList<BaseTaskTarget> targetList = existsTask
                            .getTargetList();

                    for (BaseTaskTarget target : targetList)
                    {
                        if (target.getID() == _targetID)
                        {
                            short[] transmitMapInfo = target.getTransmitMapInfo();
                            //add by zhengl; date: 2011-02-24; note: 探路与护送无法传送
                            if(target.getType() == ETastTargetType.FOUND_A_PATH 
                            		|| target.getType() == ETastTargetType.ESCORT_NPC)
                            {
                            	//edit by zhengl; date: 2011-03-21; note: 适配客户端切图前置
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(
                                        		Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT_BY_TYPE, 
                                        		Warning.UI_STRING_TIP));
                            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                            			new SwitchMapFailNotify(
                            					Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT_BY_TYPE));
                                return false;
                            }
                            //end

                            if (null != transmitMapInfo)
                            {
                                Map currentMap = _player.where();
                                Map targetMap = MapServiceImpl.getInstance()
                                        .getNormalMapByID(transmitMapInfo[0]);

                                if (null == targetMap
                                        || currentMap.getID() == targetMap
                                                .getID())
                                {
                                	//edit by zhengl; date: 2011-03-21; note: 适配客户端切图前置
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning(Tip.TIP_TASK_SERVICE_OF_HERE_ALREADY, 
                                            		Warning.UI_STRING_TIP));
                                	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                                			new SwitchMapFailNotify(
                                					Tip.TIP_TASK_SERVICE_OF_HERE_ALREADY));

                                    return false;
                                }
                                else
                                {
                                    _player.setCellX(transmitMapInfo[1]);
                                    _player.setCellY(transmitMapInfo[2]);

                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new ResponseMapBottomData(_player,
                                                    targetMap, currentMap));
                                    ResponseMessageQueue
                                            .getInstance()
                                            .put(
                                                    _player.getMsgQueueIndex(),
                                                    new ResponseMapGameObjectList(
                                                            _player
                                                                    .getLoginInfo().clientType,
                                                            targetMap));

                                    _player.gotoMap(targetMap);
                                    //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                                    EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                                    Npc escortNpc = _player.getEscortTarget();

                                    if (null != escortNpc)
                                    {
                                        TaskServiceImpl.getInstance()
                                                .endEscortNpcTask(_player,
                                                        escortNpc);
                                    }

                                    return true;
                                }
                            }
                            else
                            {
                            	//edit by zhengl; date: 2011-03-21; note: 适配客户端切图前置
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT, 
                                        		Warning.UI_STRING_TIP));
                            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                            			new SwitchMapFailNotify(Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT));
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 传送到目标NPC（位置定为NPC下方2格）
     * 
     * @param _player
     * @param _taskID
     * @param _isReceiveTask 是否是接受任务（true:接受，false:提交）
     */
    public boolean transmitToTaskNpc (HeroPlayer _player, int _taskID,
            boolean _isReceiveTask)
    {
        Npc taskNpc = null;

        if (!_isReceiveTask)
        {
            ArrayList<TaskInstance> existsTaskList = playerExsitsTaskListMap
                    .get(_player.getUserID());;

            for (TaskInstance existsTask : existsTaskList)
            {
                if (existsTask.getArchetype().getID() == _taskID)
                {
                    taskNpc = NotPlayerServiceImpl.getInstance().getNpc(
                            existsTask.getArchetype().getSubmitNpcID());
                }
            }
        }
        else
        {
            taskNpc = NotPlayerServiceImpl.getInstance().getNpc(
                    taskDictionary.get(_taskID).getDistributeNpcModelID());
        }

        if (null != taskNpc)
        {
            if (EMapType.DUNGEON == taskNpc.getOrgMap().getMapType())
            {
            	//edit by zhengl; date: 2011-03-21; note: 适配客户端切图前置
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT_TO_DUNGEON, 
                        		Warning.UI_STRING_TIP));
            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
            			new SwitchMapFailNotify(Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT_TO_DUNGEON));

                return false;
            }

            Map currentMap = _player.where();
            Map targetMap = taskNpc.where();

            if (null == targetMap || currentMap.getID() == targetMap.getID())
            {
            	//edit by zhengl; date: 2011-03-21; note: 适配客户端切图前置
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_TASK_SERVICE_OF_HERE_ALREADY, 
                        		Warning.UI_STRING_TIP));
            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
            			new SwitchMapFailNotify(Tip.TIP_TASK_SERVICE_OF_HERE_ALREADY));

                return false;
            }
            else
            {
                short x = 0, y = 0;

                if (taskNpc.getOrgY() + 1 <= targetMap.getHeight()
                        && targetMap.isRoad(taskNpc.getOrgX(), taskNpc
                                .getOrgY() + 1))
                {
                    x = taskNpc.getOrgX();
                    y = (short) (taskNpc.getOrgY() + 1);
                }
                else if (taskNpc.getOrgY() - 1 >= 0
                        && targetMap.isRoad(taskNpc.getOrgX(), taskNpc
                                .getOrgY() - 1))
                {
                    x = taskNpc.getOrgX();
                    y = (short) (taskNpc.getOrgY() - 1);
                }
                else if (taskNpc.getOrgX() + 1 <= targetMap.getWidth()
                        && targetMap.isRoad(taskNpc.getOrgX() + 1, taskNpc
                                .getOrgY()))
                {
                    x = (short) (taskNpc.getOrgX() + 1);
                    y = taskNpc.getOrgY();
                }

                _player.setCellX(x);
                _player.setCellY(y);

                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new ResponseMapBottomData(_player, targetMap,
                                currentMap));
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new ResponseMapGameObjectList(
                                _player.getLoginInfo().clientType, targetMap));

                _player.gotoMap(targetMap);
                //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                Npc escortNpc = _player.getEscortTarget();

                if (null != escortNpc)
                {
                    TaskServiceImpl.getInstance().endEscortNpcTask(_player,
                            escortNpc);
                }

                return true;
            }
        }
        else
        {
        	//edit by zhengl; date: 2011-03-21; note: 适配客户端切图前置
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT, Warning.UI_STRING_TIP));
        	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
        			new SwitchMapFailNotify(Tip.TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT));

            return false;
        }
    }

    /**
     * 地上的任务物品刷新
     * 
     * @param _groundTaskGoods
     */
    public void groundTaskGoodsRebirth (GroundTaskGoods _groundTaskGoods)
    {
        ME2ObjectList mapPlayerList = _groundTaskGoods.where().getPlayerList();

        if (mapPlayerList.size() > 0)
        {
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new GroundTaskGoodsEmergeNotify(player
                                    .getLoginInfo().clientType,
                                    _groundTaskGoods, TaskServiceImpl
                                            .getInstance()
                                            .getGroundTaskGoodsOperateMark(
                                                    player, _groundTaskGoods)));
                }
            }

            player = null;
        }
    }

    /**
     * 打开任务机关
     * 
     * @param _player 玩家
     * @param _gearID 认为机关编号
     */
    public void openGear (HeroPlayer _player, int _gearID)
    {
        ArrayList<TaskInstance> taskList = playerExsitsTaskListMap.get(_player
                .getUserID());

        if (null != taskList && taskList.size() > 0)
        {
            TaskGear gear = NotPlayerServiceImpl.getInstance().getTaskGear(
                    _gearID);

            for (TaskInstance task : taskList)
            {
                if (!task.isCompleted())
                {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();

                    for (BaseTaskTarget target : targetList)
                    {
                        if (!target.isCompleted()
                                && ETastTargetType.OPEN_GEAR == target
                                        .getType())
                        {
                            if (((TaskTargetOpenGear) target).gearModelID
                                    .equals(gear.getModelID()))

                            {
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new ChangeTaskGearStat(_gearID, gear
                                                .getCellY(), false));

                                ((TaskTargetOpenGear) target).complete();

                                TaskDAO.updateTaskProgress(_player.getUserID(),
                                        task);

                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new RefreshTaskStatus(task
                                                .getArchetype().getID(), target
                                                .getID(), target.isCompleted(),
                                                target.getDescripiton(), task
                                                        .isCompleted()));

                                if (task.isCompleted())
                                {
                                    Npc taskNpc = NotPlayerServiceImpl
                                            .getInstance().getNpc(
                                                    task.getArchetype()
                                                            .getSubmitNpcID());

                                    if (null != taskNpc
                                            && taskNpc.where() == _player
                                                    .where())
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new ChangeNpcTaskMark(taskNpc
                                                        .getID(),
                                                        Npc.SUBMIT_TASK_MARK));
                                    }
                                }

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 拾取任务物品
     * 
     * @param _player 玩家
     * @param _groundTaskGoodsID 任务物品编号
     */
    public void pickGroundTaskGoods (HeroPlayer _player, int _groundTaskGoodsID)
    {
        ArrayList<TaskInstance> taskList = playerExsitsTaskListMap.get(_player
                .getUserID());

        if (null != taskList && taskList.size() > 0)
        {
            GroundTaskGoods groundTaskGoods = NotPlayerServiceImpl
                    .getInstance().getGroundTaskGoodsTable(_groundTaskGoodsID);

            if (null != groundTaskGoods)
            {
                for (TaskInstance task : taskList)
                {
                    if (!task.isCompleted()
                            && task.getArchetype().getID() == groundTaskGoods
                                    .getTaskIDAbout())
                    {
                        ArrayList<BaseTaskTarget> targetList = task
                                .getTargetList();

                        for (BaseTaskTarget target : targetList)
                        {
                            if (!target.isCompleted()
                                    && ETastTargetType.GOODS == target
                                            .getType())
                            {
                                if (((TaskTargetGoods) target).goods.getID() == groundTaskGoods
                                        .getTaskToolIDAbout())
                                {
                                    if (null != GoodsServiceImpl
                                            .getInstance()
                                            .addGoods2Package(
                                                    _player,
                                                    ((TaskTargetGoods) target).goods,
                                                    1, CauseLog.TASKAWARD))
                                    {
                                        ((TaskTargetGoods) target)
                                                .numberChanged(1);

                                        NotPlayerServiceImpl.getInstance()
                                                .groundTaskGoodsBePicked(
                                                        groundTaskGoods);

                                        TaskDAO.updateTaskProgress(_player
                                                .getUserID(), task);

                                        ResponseMessageQueue
                                                .getInstance()
                                                .put(
                                                        _player
                                                                .getMsgQueueIndex(),
                                                        new RefreshTaskStatus(
                                                                task
                                                                        .getArchetype()
                                                                        .getID(),
                                                                target.getID(),
                                                                target
                                                                        .isCompleted(),
                                                                target
                                                                        .getDescripiton(),
                                                                task
                                                                        .isCompleted()));

                                        if (target.isCompleted())
                                        {
                                            ArrayList<GroundTaskGoods> groundTaskGoodsList = _player
                                                    .where()
                                                    .getGroundTaskGoodsList();

                                            if (0 < groundTaskGoodsList.size())
                                            {
                                                for (int i = 0; i < groundTaskGoodsList
                                                        .size(); i++)
                                                {
                                                    GroundTaskGoods other = groundTaskGoodsList
                                                            .get(i);

                                                    if (other
                                                            .getModelID()
                                                            .equals(
                                                                    groundTaskGoods
                                                                            .getModelID()))
                                                    {
                                                        ResponseMessageQueue
                                                                .getInstance()
                                                                .put(
                                                                        _player
                                                                                .getMsgQueueIndex(),
                                                                        new ChangeGroundTaskGoodsStat(
                                                                                other
                                                                                        .getID(),
                                                                                other
                                                                                        .getCellY(),
                                                                                false));
                                                    }
                                                }
                                            }

                                            if (task.isCompleted())
                                            {
                                                Npc taskNpc = NotPlayerServiceImpl
                                                        .getInstance()
                                                        .getNpc(
                                                                task
                                                                        .getArchetype()
                                                                        .getSubmitNpcID());

                                                if (null != taskNpc
                                                        && taskNpc.where() == _player
                                                                .where())
                                                {
                                                    ResponseMessageQueue
                                                            .getInstance()
                                                            .put(
                                                                    _player
                                                                            .getMsgQueueIndex(),
                                                                    new ChangeNpcTaskMark(
                                                                            taskNpc
                                                                                    .getID(),
                                                                            Npc.SUBMIT_TASK_MARK));
                                                }
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 提交任务
     * 
     * @param _player 玩家
     * @param _taskID 任务编号
     */
    public void submitTask (HeroPlayer _player, int _taskID, int _goodsIDByUser)
    {
        if (playerCompletedTaskListMap.get( _player.getUserID()).contains(_taskID) )
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_TASK_SERVICE_OF_COMPETED, Warning.UI_STRING_TIP));

            return;
        }

        ArrayList<TaskInstance> existsTaskList = playerExsitsTaskListMap.get(_player.getUserID());

        synchronized (existsTaskList)
        {
            for (int i = 0; i < existsTaskList.size(); i++)
            {
                TaskInstance existsTask = existsTaskList.get(i);
                if (existsTask.getArchetype().getID() == _taskID && existsTask.isCompleted())
                {
                    if (null != existsTask.getTargetList())
                    {
                        ArrayList<BaseTaskTarget> targetList = existsTask.getTargetList();

                        for (BaseTaskTarget target : targetList)
                        {
                            if (ETastTargetType.GOODS == target.getType())
                            {
                                try
                                {
                                    GoodsServiceImpl.getInstance().deleteSingleGoods(
                                    		_player, _player.getInventory().getTaskToolBag(), 
                                    		((TaskTargetGoods) target).goods, CauseLog.SUBMITTASK);
                                }
                                catch (BagException pe)
                                {
                                    pe.printStackTrace();
                                }
                            }
                        }
                    }

                    Award award = existsTask.getArchetype().getAward();
                    //add by zhengl; date: 2011-05-09; note: 校验物品ID防止恶意篡改
                    int goodsID = award.selectGoodsVerify(_goodsIDByUser);
                    Goods awardGoods;

                    if (0 < goodsID)
                    {
                        awardGoods = GoodsServiceImpl.getInstance().getGoodsByID(goodsID);

                        if (null != awardGoods)
                        {
                            int goodsNumber = 1;

                            if (EGoodsType.EQUIPMENT != awardGoods.getGoodsType())
                            {
                                goodsNumber = award.getOptionalGoodsNumber(awardGoods);
                            }

                            if (0 != goodsNumber)
                            {
                            	//edit by zhengl; date: 2011-02-21; note: 任务奖励专用方法
                                GoodsServiceImpl
                                        .getInstance()
                                        .addGoods2PackageByTask(_player, awardGoods,
                                                goodsNumber, CauseLog.TASKAWARD);
                            }
                        }
                    }

                    if (null != award.getBoundGoodsList())
                    {
                        ArrayList<AwardGoodsUnit> boundBoundGoodsList = award
                                .getBoundGoodsList();

                        for (AwardGoodsUnit awardGoodsUnit : boundBoundGoodsList)
                        {
                        	//edit by zhengl; date: 2011-02-21; note: 任务奖励专用方法
                            GoodsServiceImpl.getInstance().addGoods2PackageByTask(
                                    _player, awardGoodsUnit.goods,
                                    awardGoodsUnit.number, CauseLog.TASKAWARD);
                        }
                    }

                    existsTaskList.remove(i);

                    if (!existsTask.getArchetype().isRepeated())
                    {
                        playerCompletedTaskListMap.get(_player.getUserID())
                                .add(_taskID);
                    }

                    TaskDAO.completeTask(_player.getUserID(), _taskID,
                            existsTask.getArchetype().isRepeated());

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new TaskListChangerNotify(
                                    TaskListChangerNotify.SUBMIT, existsTask));

                    PlayerServiceImpl.getInstance().addMoney(_player,
                            award.money, 1,
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                            "任务奖励");
                    //edit by zhengl
                    if (config.is_use_push) 
                    {
                    	Push push = existsTask.getArchetype().getTaskPush();
                        if (push != null && 
                        		!config.confine_publisher_list.contains(_player.getLoginInfo().publisher)) 
                        {
                        	//任务推广第1步骤,这个事件将引导客户端触发
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(push.commPushContent, 
                                    		Warning.UI_COMPLEX_TIP, 
                                    		Warning.SUBFUNCTION_UI_TASK_PUSH_COMM, 
                                    		push.id, push.time));
        				}
					}


                    //任务完成日志
                    LogServiceImpl.getInstance().taskFinished(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                            _player.getUserID(),_player.getName(),_taskID,existsTask.getArchetype().getName());

                    //add by zhengl; date: 2011-02-22; note: 添加完成任务时传送至某地图
                    if(award.mapID != -1 && award.mapX != -1 && award.mapY != -1) 
                    {
                    	Map currentMap = _player.where();
                    	Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(award.mapID);
                    	_player.setCellX(award.mapX);
                    	_player.setCellY(award.mapY);
                    	
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ResponseMapBottomData(_player, targetMap, currentMap));
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ResponseMapGameObjectList(
                                        _player.getLoginInfo().clientType, targetMap));
                        _player.gotoMap(targetMap);
                        //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                        EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                    }
                    //end
                    PlayerServiceImpl.getInstance().addExperience(
                    		_player,
                            CEService.taskExperience(_player.getLevel(),
                            		existsTask.getArchetype().getLevel(), 
                            		award.experience), 
                            1, 
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING);
                    
                    //add by zhengl; date: 2011-03-25; note: 解决BUG
//                    int taskNext = existsTask.getArchetype().getCondition().taskNext;
//                    Task next = getTask(taskNext);
//                    Npc npc = NotPlayerServiceImpl.getInstance().getNpc(next.getDistributeNpcModelID());
//                    if(taskNext != 0 && npc != null && npc.where() == _player.where())
//                    {
//                    	ChangeNpcTaskMark mark = new ChangeNpcTaskMark(npc.getID(), 1);
//                    	OutMsgQ.getInstance().put(_player.getMsgQueueIndex(), mark);
//                    }
                    notifyMapNpcTaskMark(_player, _player.where());

                    return;
                }
            }
        }
    }
    


    /**
     * 添加任务物品
     * 
     * @param _player
     * @param _taskID
     * @param _taskGoods
     * @param _number
     */
    public void addTaskGoods (HeroPlayer _player, int _taskID,
            Goods _taskGoods, int _number)
    {
        ArrayList<TaskInstance> playerList = playerExsitsTaskListMap
                .get(_player.getUserID());

        for (TaskInstance taskInstance : playerList)
        {
            if (!taskInstance.isCompleted()
                    && taskInstance.getArchetype().getID() == _taskID)
            {
                ArrayList<BaseTaskTarget> targetList = taskInstance
                        .getTargetList();

                for (BaseTaskTarget target : targetList)
                {
                    if (!target.isCompleted())
                    {
                        if (ETastTargetType.GOODS == target.getType())
                        {
                            if (((TaskTargetGoods) target).goods == _taskGoods)
                            {
                                ((TaskTargetGoods) target)
                                        .numberChanged(_number);

                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new RefreshTaskStatus(taskInstance
                                                .getArchetype().getID(), target
                                                .getID(), target.isCompleted(),
                                                target.getDescripiton(),
                                                taskInstance.isCompleted()));

                                if (taskInstance.isCompleted())
                                {
                                    Npc taskNpc = NotPlayerServiceImpl
                                            .getInstance().getNpc(
                                                    taskInstance.getArchetype()
                                                            .getSubmitNpcID());

                                    if (null != taskNpc
                                            && taskNpc.where() == _player
                                                    .where())
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new ChangeNpcTaskMark(taskNpc
                                                        .getID(),
                                                        Npc.SUBMIT_TASK_MARK));
                                    }
                                }

                                break;
                            }
                        }
                    }
                }

                break;
            }
        }
    }

    /**
     * 添加任务物品
     * 
     * @param _player
     * @param _taskGoodsID
     * @param _number
     */
    public void addTaskGoods (HeroPlayer _player, int _taskGoodsID, int _number)
    {
        ArrayList<TaskInstance> playerList = playerExsitsTaskListMap
                .get(_player.getUserID());

        for (TaskInstance taskInstance : playerList)
        {
            if (!taskInstance.isCompleted())
            {
                ArrayList<BaseTaskTarget> targetList = taskInstance
                        .getTargetList();

                for (BaseTaskTarget target : targetList)
                {
                    if (!target.isCompleted())
                    {
                        if (ETastTargetType.GOODS == target.getType()
                                && ((TaskTargetGoods) target).goods.getID() == _taskGoodsID)
                        {
                            ((TaskTargetGoods) target).numberChanged(_number);

                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new RefreshTaskStatus(taskInstance
                                            .getArchetype().getID(), target
                                            .getID(), target.isCompleted(),
                                            target.getDescripiton(),
                                            taskInstance.isCompleted()));

                            if (taskInstance.isCompleted())
                            {
                                Npc taskNpc = NotPlayerServiceImpl
                                        .getInstance().getNpc(
                                                taskInstance.getArchetype()
                                                        .getSubmitNpcID());

                                if (null != taskNpc
                                        && taskNpc.where() == _player.where())
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new ChangeNpcTaskMark(taskNpc
                                                    .getID(),
                                                    Npc.SUBMIT_TASK_MARK));
                                }
                            }

                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 减少任务物品
     * 
     * @param _player
     * @param _taskGoods
     * @param _number
     */
    public void reduceTaskGoods (HeroPlayer _player, int _taskToolID)
    {
        ArrayList<TaskInstance> playerList = playerExsitsTaskListMap
                .get(_player.getUserID());

        for (TaskInstance taskInstance : playerList)
        {
            ArrayList<BaseTaskTarget> targetList = taskInstance.getTargetList();

            for (BaseTaskTarget target : targetList)
            {
                if (ETastTargetType.GOODS == target.getType()
                        && ((TaskTargetGoods) target).goods.getID() == _taskToolID)
                {
                    if (target.isCompleted())
                    {
                        ArrayList<GroundTaskGoods> groundTaskGoodsList = _player
                                .where().getGroundTaskGoodsList();

                        if (0 < groundTaskGoodsList.size())
                        {
                            for (int i = 0; i < groundTaskGoodsList.size(); i++)
                            {
                                GroundTaskGoods other = groundTaskGoodsList
                                        .get(i);

                                if (other.getTaskToolIDAbout() == _taskToolID)
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new ChangeGroundTaskGoodsStat(other
                                                    .getID(), other.getCellY(),
                                                    true));
                                }
                            }
                        }
                    }

                    if (taskInstance.isCompleted())
                    {
                        Npc taskNpc = NotPlayerServiceImpl.getInstance()
                                .getNpc(
                                        taskInstance.getArchetype()
                                                .getSubmitNpcID());

                        if (null != taskNpc
                                && taskNpc.where() == _player.where())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new ChangeNpcTaskMark(taskNpc.getID(), 0));
                        }
                    };

                    ((TaskTargetGoods) target)
                            .numberChanged(-((TaskTargetGoods) target).number);

                    break;
                }
            }
        }
    }

    /**
     * 获取NPC关于任务的头顶标记
     * 
     * @param _npcModelID
     * @param _player
     * @return
     */
    private byte getTaskMark (String _npcModelID, HeroPlayer _player)
    {
        ArrayList<TaskInstance> playerList = playerExsitsTaskListMap
                .get(_player.getUserID());
        //优先显示可完成通知(黄问号)
        for (TaskInstance taskInstance : playerList)
        {
            if (taskInstance.isCompleted()
                    && taskInstance.getArchetype().getSubmitNpcID().equals(
                            _npcModelID))
            {
                return Npc.SUBMIT_TASK_MARK;
            }
        }
        //再显示可接通知(黄叹号)
        ArrayList<Task> taskList = TaskServiceImpl.getInstance()
                .getReceiveableTaskList(_npcModelID, _player);

        if (null != taskList)
        {
            return Npc.RECEIVE_TASK_MARK;
        }
        //del by zhengl; date: 2011-01-28; note: 暂不需要该种显示模式.
        //再显示未满足接任务条件的任务(灰叹号)
//        taskList = getNotReceTaskList(_npcModelID, _player);
//        if (null != taskList)
//        {
//            return Npc.RECEIVE_TASK_NOT_MARK;
//        }
        //未完成通知(灰问号)
        for (TaskInstance taskInstance : playerList)
        {
            if (!taskInstance.isCompleted()
                    && taskInstance.getArchetype().getSubmitNpcID().equals(_npcModelID))
            {
                return Npc.SUBMIT_TASK_NOT_MARK;
            }
        }

        return Npc.NOT_MARK;
    }

    /**
     * 在角色进入地图后,通知NPC头顶的任务标记
     * 
     * @param _player
     */
    public void notifyMapNpcTaskMark (HeroPlayer _player, Map _map)
    {
        if (_map.getNpcList().size() > 0)
        {
            ArrayList<Integer> taskMarkList = new ArrayList<Integer>();

            for (int i = 0; i < _map.getNpcList().size(); i++)
            {
                Npc npc = (Npc) _map.getNpcList().get(i);
                byte npcTaskMarks = getTaskMark(npc.getModelID(), _player);
                taskMarkList.add(npc.getID());
                taskMarkList.add(Integer.valueOf(npcTaskMarks));
            }

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new NotifyMapNpcTaskMark(taskMarkList));
        }
    }

    /**
     * 在角色进入地图后,更改任务机关的是否可操作标记
     * 
     * @param _player
     * @param _map
     */
    public void notifyMapGearOperateMark (HeroPlayer _player, Map _map)
    {
        ArrayList<TaskGear> gearList = _map.getTaskGearList();

        if (gearList.size() > 0)
        {
            ArrayList<TaskInstance> taskList = TaskServiceImpl.getInstance()
                    .getTaskList(_player.getUserID());

            if (taskList.size() > 0)
            {
                for (TaskGear gear : gearList)
                {
                    for (TaskInstance task : taskList)
                    {
                        if (task.getArchetype().getID() == gear
                                .getTaskIDAbout()
                                && !task.isCompleted())
                        {
                            ArrayList<BaseTaskTarget> targetList = task
                                    .getTargetList();

                            for (BaseTaskTarget target : targetList)
                            {
                                if (ETastTargetType.OPEN_GEAR == target
                                        .getType()
                                        && ((TaskTargetOpenGear) target).gearModelID
                                                .equals(gear.getModelID()))
                                {
                                    if (!target.isCompleted())
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new ChangeTaskGearStat(gear
                                                        .getID(), gear
                                                        .getCellY(), true));
                                    }
                                    else
                                    {
                                        return;
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 在角色进入地图后,更改地上任务物品的是否可操作标记
     * 
     * @param _player
     * @param _map
     */
    public void notifyGroundTaskGoodsOperateMark (HeroPlayer _player, Map _map)
    {
        ArrayList<GroundTaskGoods> groundTaskGoodsList = _map
                .getGroundTaskGoodsList();

        if (groundTaskGoodsList.size() > 0)
        {
            ArrayList<TaskInstance> taskList = TaskServiceImpl.getInstance()
                    .getTaskList(_player.getUserID());

            if (taskList.size() > 0)
            {
                for (GroundTaskGoods taskGoods : groundTaskGoodsList)
                {
                    for (TaskInstance task : taskList)
                    {
                        if (task.getArchetype().getID() == taskGoods
                                .getTaskIDAbout()
                                && !task.isCompleted())
                        {
                            ArrayList<BaseTaskTarget> targetList = task
                                    .getTargetList();

                            for (BaseTaskTarget target : targetList)
                            {
                                if (ETastTargetType.GOODS == target.getType()
                                        && ((TaskTargetGoods) target).goods
                                                .getID() == taskGoods
                                                .getTaskToolIDAbout())
                                {
                                    if (!target.isCompleted())
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new ChangeGroundTaskGoodsStat(
                                                        taskGoods.getID(),
                                                        taskGoods.getCellY(),
                                                        true));
                                    }
                                    else
                                    {
                                        return;
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取地上任务物品的拾取状态
     * 
     * @param _player
     * @param _groundTaskGoods
     */
    public boolean getGroundTaskGoodsOperateMark (HeroPlayer _player,
            GroundTaskGoods _groundTaskGoods)
    {
        ArrayList<TaskInstance> taskList = TaskServiceImpl.getInstance()
                .getTaskList(_player.getUserID());

        if (taskList.size() > 0)
        {
            for (TaskInstance task : taskList)
            {
                if (!task.isCompleted())
                {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();

                    for (BaseTaskTarget target : targetList)
                    {
                        if (ETastTargetType.GOODS == target.getType()
                                && ((TaskTargetGoods) target).goods.getID() == _groundTaskGoods
                                        .getTaskToolIDAbout())
                        {
                            if (!target.isCompleted())
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 获取角色接受的任务
     * 
     * @param _userID
     * @return
     */
    public ArrayList<TaskInstance> getTaskList (int _userID)
    {
        return playerExsitsTaskListMap.get(_userID);
    }
    
    /**
     * 根据推广ID获得推广信息
     * @param _pushID
     * @return
     */
    public Push getTaskPush(int _pushID)
    {
    	return this.pushDataTable.get(_pushID);
    }
    
    /**
     * 异步任务计费装备下发
     * <P>
     * 当玩家使用异步任务计费流程之后提供给计费模块调用的方法
     * @param _userID
     * @param _pushID
     * @param _pushType
     * @param _result
     */
    public void asynTaskPushItem(String _transID, boolean _result)
    {
    	int userID = pushPlayerGoods.get(_transID)[0];
    	int pushID = pushPlayerGoods.get(_transID)[1];
    	int pushType = pushPlayerGoods.get(_transID)[2];
    	Push push = this.pushDataTable.get(pushID);
    	HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
    	boolean isCpmpel = false;
    	if (push != null && player != null && getEmptyGrid(push.goodsID, player))
    	{
    		int price = push.point/ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
    		//是否强制给装备
    		if (TaskServiceImpl.getInstance().getConfig().is_proxy_compel_give 
    				&& pushType == Push.PUSH_TYPE_MOBILE_PROXY) {
    			isCpmpel = true;
			}
    		if (TaskServiceImpl.getInstance().getConfig().is_sms_compel_give 
    				&& pushType == Push.PUSH_TYPE_SMS) {
    			isCpmpel = true;
			}
			//加装备
			if(_result || isCpmpel)
			{
				if (player.getInventory().getSpecialGoodsBag().getEmptyGridNumber() > 0) 
				{
                    GoodsServiceImpl.getInstance().addGoods2Package(
                    		player, push.goodsID, 1, CauseLog.TASKPUSH);
				}
				else 
				{
    		        LogServiceImpl.getInstance().taskPushOption(
    		        		player.getLoginInfo().accountID, 
    		        		player.getLoginInfo().username,
    		        		player.getUserID(),
    		        		player.getName(), 
    		        		pushID, 
    		        		9, 
    		        		"用户点数充足,但是扣点失败.任务计费未成功", 
    		        		price);
				}
            	if (_result) 
            	{
                    LogServiceImpl.getInstance().taskPushOption(
                    		player.getLoginInfo().accountID, 
                    		player.getLoginInfo().username,
                    		player.getUserID(),
                    		player.getName(), 
                    		pushID, 
                    		5, 
                    		"计费成功,下发装备", 
                    		price);
				}
            	else if (isCpmpel) 
            	{
                    LogServiceImpl.getInstance().taskPushOption(
                    		player.getLoginInfo().accountID, 
                    		player.getLoginInfo().username,
                    		player.getUserID(),
                    		player.getName(), 
                    		pushID, 
                    		7, 
                    		"计费失败,强制下发装备", 
                    		price);
				}
			}
			else 
			{
				if (pushType == Push.PUSH_TYPE_SMS) {
					//短信失败tip
//					OutMsgQ.getInstance().put(player.getMsgQueueIndex(), 
//							new Warning(Tip.TIP_TASK_CONNECTOR, Warning.UI_TOOLTIP_TIP));
					
				} else if (pushType == Push.PUSH_TYPE_MOBILE_PROXY) {
					
					//网游计费平台失败tip
				}
		        LogServiceImpl.getInstance().taskPushOption(
		        		player.getLoginInfo().accountID, 
		        		player.getLoginInfo().username,
		        		player.getUserID(),
		        		player.getName(), 
		        		pushID, 
		        		8, 
		        		"计费失败,且未下发装备", 
		        		price);
			}
    	}
    	pushPlayerGoods.remove(_transID);
    	//暂时不处理玩家不在线的情况,等邮件系统起来发放进邮件系统里面
    }
    
    /**
     * 是否还有位置
     * @param _goodsID
     * @param _player
     * @return
     */
    private boolean getEmptyGrid(int _goodsID, HeroPlayer _player)
    {
    	boolean result = false;
		String bagNameString = "";
		if(GoodsContents.getGoodsType(_goodsID) == EGoodsType.EQUIPMENT)
		{
			int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
			bagNameString = EGoodsType.EQUIPMENT.getDescription();
			if (empty > 0) 
			{
				result = true;
			}
		}
		else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.MATERIAL) 
		{
			int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
			bagNameString = EGoodsType.MATERIAL.getDescription();
			if (empty > 0) 
			{
				result = true;
			}
		}
		else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.MEDICAMENT) 
		{
			int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
			bagNameString = EGoodsType.MEDICAMENT.getDescription();
			if (empty > 0) 
			{
				result = true;
			}
		}
		else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.PET) 
		{
			int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
			bagNameString = EGoodsType.PET.getDescription();
			if (empty > 0) 
			{
				result = true;
			}
		}
		else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.PET_GOODS) 
		{
			int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
			bagNameString = EGoodsType.PET.getDescription();
			if (empty > 0) 
			{
				result = true;
			}
		}
		else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.SPECIAL_GOODS) 
		{
			int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
			bagNameString = EGoodsType.SPECIAL_GOODS.getDescription();
			if (empty > 0) 
			{
				result = true;
			}
		}
		else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.TASK_TOOL) 
		{
			int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
			bagNameString = EGoodsType.SPECIAL_GOODS.getDescription();
			if (empty > 0) 
			{
				result = true;
			}
		}
		if (!result) 
		{
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_TASK_PUSH_BAG_FULL.replaceAll("%fn", bagNameString)));
		}
    	return result;
    }
    
    /**
     * 最后一步
     * @param _player
     * @param _pushID
     * @param _pushType
     */
    public void confirmTaskPush(HeroPlayer _player, int _pushID, int _pushType, 
    		String _productID, int _proxyID, String _tranID, String _mobileUserID)
    {
    	Push push = this.pushDataTable.get(_pushID);
    	if (push != null && getEmptyGrid(push.goodsID, _player)) 
    	{
    		int price = push.point/ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
    		if (_pushType == Push.PUSH_TYPE_COMM) 
    		{
    			//按点数计费的普通模式
    			if(push.point <= _player.getChargeInfo().pointAmount)
    			{
	    			Goods goods = GoodsContents.getGoods(push.goodsID);
	    			//扣点
	    			boolean reduce = ChargeServiceImpl.getInstance().reducePoint(
	    					_player, push.point, push.goodsID, goods.getName(),1, 
	    					ServiceType.BUY_TOOLS);
	    			//加装备
	    			if(reduce)
	    			{
	    				if (_player.getInventory().getSpecialGoodsBag().getEmptyGridNumber() > 0) 
	    				{
	                        GoodsServiceImpl.getInstance().addGoods2Package(
	                        		_player, push.goodsID, 1, CauseLog.TASKPUSH);
		    		        LogServiceImpl.getInstance().taskPushOption(
		    		        		_player.getLoginInfo().accountID, 
		    		        		_player.getLoginInfo().username,
		    		        		_player.getUserID(),
		    		        		_player.getName(), 
		    		        		_pushID, 
		    		        		5, 
		    		        		"用户点数充足,已扣除点数.装备已下发", 
		    		        		price);
						}
	    				else 
	    				{
		    		        LogServiceImpl.getInstance().taskPushOption(
		    		        		_player.getLoginInfo().accountID, 
		    		        		_player.getLoginInfo().username,
		    		        		_player.getUserID(),
		    		        		_player.getName(), 
		    		        		_pushID, 
		    		        		9, 
		    		        		"用户点数充足,已扣除点数.但是包裹已满,添加装备失败", 
		    		        		price);
						}
	    			}
	    			else 
	    			{
	    				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
	    						new Warning(Tip.TIP_TASK_PUSH_FEE_FAIL));
	    		        LogServiceImpl.getInstance().taskPushOption(
	    		        		_player.getLoginInfo().accountID, 
	    		        		_player.getLoginInfo().username,
	    		        		_player.getUserID(),
	    		        		_player.getName(), 
	    		        		_pushID, 
	    		        		6, 
	    		        		"用户点数充足,但扣点失败.任务计费未成功", 
	    		        		price);
					}
    			}
    			else 
    			{
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_NOT_POINT));
				}
    		}
    		else if (_pushType == Push.PUSH_TYPE_MOBILE_PROXY) 
    		{
    			//记录计费信息,等待焦栋杰回调之后下发装备.
    			Integer[] data = new Integer[3];
    			data[0] = _player.getUserID();
    			data[1] = _pushID;
    			data[2] = _pushType;
    			pushPlayerGoods.put(_tranID, data);
    			//调用焦栋杰计费模块方法 _productID,_proxyID
    			String ngUrlID = "new type, please call gameserver developer";
    			if (_proxyID == Push.PROXY_HERO_ID) 
    			{
    				ngUrlID = FeeIni.FEE_URL_ID_HERO;
				} 
    			else if (_proxyID == Push.PROXY_JTCQ_ID) 
    			{
    				ngUrlID = FeeIni.FEE_URL_ID_JIUTIAN;
				}
    			//String ngUrlID, int _accountID, String _toolsID,String mobileUserID,
                //int userID,int publisher,ServiceType serviceType
    			boolean[] result = ChargeServiceImpl.getInstance().ngBuyMallTools(
    					ngUrlID, 
    					_player.getLoginInfo().accountID, 
    					_productID,
    					_mobileUserID, 
    					_player.getUserID(),
    					_player.getLoginInfo().publisher, 
    					ServiceType.FEE, 1, price);
    			/**
    			 * 网游代收费模式暂时作为同步进行调用
    			 */
    			this.asynTaskPushItem(_tranID, result[0]);

			}
    		else if (_pushType == Push.PUSH_TYPE_SMS) 
    		{
    			//记录计费信息,等待焦栋杰回调之后下发装备.
    			Integer[] data = new Integer[3];
    			data[0] = _player.getUserID();
    			data[1] = _pushID;
    			data[2] = _pushType;
    			pushPlayerGoods.put(_tranID, data);
			}
    	}
    }
    
    /**
     * 进入推广步骤.
     * @param _player
     * @param _pushID
     * @param _pushType
     * @param _isNext
     */
    public void enterTaskPush(HeroPlayer _player, int _pushID, int _pushType, boolean _isNext)
    {
    	Push push = this.pushDataTable.get(_pushID);
    	if (push != null && getEmptyGrid(push.goodsID, _player)) 
    	{
    		//背包未满的情况:
    		String confirmContent = "";
    		
    		if (_pushType == Push.PUSH_TYPE_COMM) 
    		{
    			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
    					new Warning(push.commConfirmContent, 
    							Warning.UI_COMPLEX_TIP, 
    							Warning.SUBFUNCTION_UI_TASK_PUSH_COMM_CONFIRM, 
    							push.id, 0));
			}
    		else
    		{
    			/**
    			 * 非常规任务推广计费模式用 push.pushContent[_pushType -1] 方式不好
    			 * 有时间了改成 _pushType 匹配 push 的类型的模式.
    			 */
    			int index = 0;
    			for (int i = 0; i < push.pushType.length; i++) 
    			{
    				if(push.pushType[i] == _pushType)
    				{
    					index = i;
    					break;
    				}
				}
        		if(_isNext)
        		{
        			confirmContent = push.pushContent[index];
        			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
        					new Warning(confirmContent, 
        							Warning.UI_COMPLEX_TIP, 
        							Warning.SUBFUNCTION_UI_TASK_PUSH_COMM_SPECIAL, 
        							push.id, 0));
        		}
        		else 
        		{
        			confirmContent = push.limitContent[index];
        			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
        					new Warning(confirmContent, Warning.UI_TOOLTIP_TIP));
				}
			}
		}
    }

    /**
     * 加载玩家任务列表,包括未提交任务和已完成并提交的任务
     * 
     * @param _userID
     */
    public void loadPlayerTaskList (HeroPlayer _player)
    {
        TaskDAO.loadTask(_player, playerExsitsTaskListMap.get(_player
                .getUserID()), playerCompletedTaskListMap.get(_player
                .getUserID()));
    }

    /**
     * 加载玩家任务列表,并将已接任务列表目录发送到客户端
     * 
     * @param _userID
     */
    public void sendPlayerTaskList (HeroPlayer _player)
    {
        ArrayList<TaskInstance> taskList = playerExsitsTaskListMap.get(_player
                .getUserID());

        if (null != taskList && taskList.size() > 0)
        {
            Collections.sort(taskList,new TaskInstanceComparator());
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new ResponseTaskItems(taskList));
        }
    }

    /**
     * @param _userID
     * @return
     */
    public ArrayList<Integer> getCompltedTaskIDList (int _userID)
    {
        return playerCompletedTaskListMap.get(_userID);
    }

    /**
     * 开始护送
     * 
     * @param _player 触发开始行走的玩家
     * @param _taskID 任务编号
     * @param _npc 被护送的NPC
     */
    public void beginEscortNpcTask (HeroPlayer _player, Task _task,
            TaskTargetEscortNpc _escortTarget, Npc _npc)
    {
        synchronized (escortTaskExcuteList)
        {
            _npc.beginFollow(_player);
            _player.setEscortTarget(_npc);

            MapSynchronousInfoBroadcast.getInstance().put(_npc.where(),
                    new ChangeNpcStat(_npc.getID(), _npc.canInteract()), false,
                    0);

            EscortNpcTaskInfo info = new EscortNpcTaskInfo();
            info.task = _task;
            info.escortTarget = _escortTarget;
            info.trigger = _player;
            info.npc = _npc;
            info.traceTime = _escortTarget.countTime;

            escortTaskExcuteList.add(info);

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning("‘" + info.task.getName() + "’任务开始", Warning.UI_STRING_TIP));

            int groupID = _player.getGroupID();

            if (groupID > 0)
            {
                Group group = GroupServiceImpl.getInstance().getGroup(groupID);

                ArrayList<HeroPlayer> playerList = group.getPlayerList();

                for (HeroPlayer other : playerList)
                {
                    if (other != _player)
                    {
                        ArrayList<TaskInstance> taskList = getTaskList(other
                                .getUserID());

                        for (TaskInstance task : taskList)
                        {
                            if (task.getArchetype() == _task)
                            {
                                if (!task.isCompleted())
                                {
                                    if (_npc.where() == other.where())
                                    {
                                        /*double distance = Math
                                                .sqrt(Math.pow((other
                                                        .getCellX() - _npc
                                                        .getCellX()), 2)
                                                        + Math
                                                                .pow(
                                                                        (other
                                                                                .getCellY() - _npc
                                                                                .getCellY()),
                                                                        2));*/

                                        boolean inDistance = ESCORT_TASK_VALIDATE_DISTANCE*ESCORT_TASK_VALIDATE_DISTANCE
                                                    >= (other.getCellX()-_npc.getCellX())*(other.getCellX()-_npc.getCellX())+(other.getCellY()-_npc.getCellY())*(other.getCellY()-_npc.getCellY());

//                                        if (ESCORT_TASK_VALIDATE_DISTANCE >= distance)
                                        if(inDistance)
                                        {
                                            info.addSpareTaskMember(other);

                                            ResponseMessageQueue.getInstance().put(
                                                    other.getMsgQueueIndex(),
                                                    new Warning("‘"
                                                            + info.task
                                                                    .getName()
                                                            + "’任务开始", Warning.UI_STRING_TIP));
                                        }
                                    }
                                }

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 结束护送任务（当玩家下线、死亡、被传送到其他地图上时）
     * 
     * @param _player
     * @param _targetNpc
     */
    public void endEscortNpcTask (HeroPlayer _player, Npc _targetNpc)
    {
        synchronized (escortTaskExcuteList)
        {
            EscortNpcTaskInfo info;

            for (int i = 0; i < escortTaskExcuteList.size(); i++)
            {
                info = escortTaskExcuteList.get(i);

                if (info.npc == _targetNpc)
                {
                    if (_player == info.trigger)
                    {
                        escortTaskExcuteList.remove(i);

                        ResponseMessageQueue.getInstance()
                                .put(
                                        info.trigger.getMsgQueueIndex(),
                                        new Warning("‘" + info.task.getName()
                                                + "’任务失败", Warning.UI_STRING_TIP));

                        if (null != info.getSpareTaskMemberList()
                                && info.getSpareTaskMemberList().size() > 0)
                        {
                            for (HeroPlayer member : info
                                    .getSpareTaskMemberList())
                            {
                                ResponseMessageQueue.getInstance().put(
                                        member.getMsgQueueIndex(),
                                        new Warning("‘" + info.task.getName()
                                                + "’任务失败", Warning.UI_STRING_TIP));
                            }
                        }

                        info.npc.stopFollowTask();
                        info.trigger.setEscortTarget(null);

                        info.npc.setCellX(info.npc.getOrgX());
                        info.npc.setCellY(info.npc.getOrgY());

                        if (info.npc.where() != info.npc.getOrgMap())
                        {
                            info.npc.gotoMap(info.npc.getOrgMap());
                        }
                        else
                        {
                            MapSynchronousInfoBroadcast.getInstance().put(
                                    info.npc.where(),
                                    new NpcResetNotify(info.npc.getID(),
                                            info.npc.getCellX(), info.npc
                                                    .getCellY()), false, 0);
                        }

                        info.trigger = null;
                        info.spareTaskMemberList.clear();
                    }
                    else
                    {
                        if (null != info.spareTaskMemberList
                                && info.spareTaskMemberList.contains(_player))
                        {
                            info.spareTaskMemberList.remove(_player);
                            _player.setEscortTarget(null);
                        }
                    }

                    break;
                }
            }
        }
    }

    /**
     * 护送任务有效性判断逻辑
     */
    private void excuteEscortNpcTask ()
    {
        EscortNpcTaskInfo info;

        for (int i = 0; i < escortTaskExcuteList.size();)
        {
            info = escortTaskExcuteList.get(i);

            short npcLocationX = info.npc.getCellX();
            short npcLocationY = info.npc.getCellY();
            int npcWhereMapID = info.npc.where().getID();

            if (!info.trigger.isEnable() || info.trigger.isDead())
            {
                if (endEscort(info))
                {
                    escortTaskExcuteList.remove(i);

                    ResponseMessageQueue.getInstance().put(info.trigger.getMsgQueueIndex(),
                            new Warning("‘" + info.task.getName() + "’任务失败", 
                            		Warning.UI_STRING_TIP));

                    if (null != info.getSpareTaskMemberList()
                            && info.getSpareTaskMemberList().size() > 0)
                    {
                        for (HeroPlayer member : info.getSpareTaskMemberList())
                        {
                            if (member.isEnable())
                                ResponseMessageQueue.getInstance().put(
                                        member.getMsgQueueIndex(),
                                        new Warning("‘" + info.task.getName()+ "’任务失败", 
                                                Warning.UI_STRING_TIP));
                        }
                    }

                    info.trigger = null;
                    info.spareTaskMemberList.clear();

                    continue;
                }
            }

            if (npcWhereMapID == info.escortTarget.mapID
                    && (npcLocationX >= info.escortTarget.x
                            - info.escortTarget.mistakeRang
                            && npcLocationX <= info.escortTarget.x
                                    + info.escortTarget.mistakeRang
                            && npcLocationY >= info.escortTarget.y
                                    - info.escortTarget.mistakeRang && npcLocationY <= info.escortTarget.y
                            + info.escortTarget.mistakeRang))
            {
                reachDestination(info);

                if (endEscort(info))
                {
                    escortTaskExcuteList.remove(i);

                    info.trigger = null;
                    info.spareTaskMemberList.clear();

                    continue;
                }
            }

            if (info.traceTime <= 0)
            {
                if (endEscort(info))
                {
                    escortTaskExcuteList.remove(i);

                    ResponseMessageQueue.getInstance().put(info.trigger.getMsgQueueIndex(),
                            new Warning("‘" + info.task.getName() + "’任务失败", 
                            		Warning.UI_STRING_TIP));

                    if (null != info.getSpareTaskMemberList()
                            && info.getSpareTaskMemberList().size() > 0)
                    {
                        for (HeroPlayer member : info.getSpareTaskMemberList())
                        {
                            if (member.isEnable())
                                ResponseMessageQueue.getInstance().put(
                                        member.getMsgQueueIndex(),
                                        new Warning("‘" + info.task.getName()
                                                + "’任务失败", Warning.UI_STRING_TIP));
                        }
                    }

                    info.trigger = null;
                    info.spareTaskMemberList.clear();

                    continue;
                }
            }

            boolean inDistance = ESCORT_TASK_VALIDATE_DISTANCE*ESCORT_TASK_VALIDATE_DISTANCE
                        < (info.trigger.getCellX() - npcLocationX)*(info.trigger.getCellX() - npcLocationX)+(info.trigger.getCellY() - npcLocationY)*(info.trigger.getCellY() - npcLocationY);

           /* if (npcWhereMapID != info.trigger.where().getID()
                    || ESCORT_TASK_VALIDATE_DISTANCE < Math
                            .sqrt(Math
                                    .pow(
                                            (info.trigger.getCellX() - npcLocationX),
                                            2)
                                    + Math
                                            .pow(
                                                    (info.trigger.getCellY() - npcLocationY),
                                                    2)))*/
            if(npcWhereMapID != info.trigger.where().getID() || inDistance)
            {
                if (endEscort(info))
                {
                    escortTaskExcuteList.remove(i);

                    ResponseMessageQueue.getInstance().put(info.trigger.getMsgQueueIndex(),
                            new Warning("‘" + info.task.getName() + "’任务失败", 
                            		Warning.UI_STRING_TIP));

                    if (null != info.getSpareTaskMemberList()
                            && info.getSpareTaskMemberList().size() > 0)
                    {
                        for (HeroPlayer member : info.getSpareTaskMemberList())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    member.getMsgQueueIndex(),
                                    new Warning("‘" + info.task.getName()
                                            + "’任务失败", Warning.UI_STRING_TIP));
                        }
                    }

                    info.trigger = null;
                    info.spareTaskMemberList.clear();

                    continue;
                }
            }

            info.traceTime -= ESCORT_TASK_INTERVAL;
            i++;
        }
    }

    private boolean endEscort (EscortNpcTaskInfo _taskInfo)
    {
        _taskInfo.npc.stopFollowTask();
        _taskInfo.trigger.setEscortTarget(null);
        _taskInfo.npc.setCellX(_taskInfo.npc.getOrgX());
        _taskInfo.npc.setCellY(_taskInfo.npc.getOrgY());

        if (_taskInfo.npc.where() != _taskInfo.npc.getOrgMap())
        {
            _taskInfo.npc.gotoMap(_taskInfo.npc.getOrgMap());
        }
        else
        {
            MapSynchronousInfoBroadcast.getInstance().put(
                    _taskInfo.npc.where(),
                    new NpcResetNotify(_taskInfo.npc.getID(), _taskInfo.npc
                            .getCellX(), _taskInfo.npc.getCellY()), false, 0);
        }

        return true;
    }

    /**
     * 探路任务判断逻辑
     */
    private void excuteFoundAPathTask ()
    {
        ArrayList<HeroPlayer> playerList;

        for (FoundAPathInfo info : foundAPathTaskList)
        {
            playerList = MapServiceImpl.getInstance().getAllPlayerListInCircle(
                    info.map, info.x, info.y, info.mistakeRang);

            if (playerList != null && 0 < playerList.size())
            {
                for (HeroPlayer player : playerList)
                {
                    ArrayList<TaskInstance> taskList = playerExsitsTaskListMap
                            .get(player.getUserID());

                    for (TaskInstance task : taskList)
                    {
                        if (task.getArchetype().getID() == info.taskID)
                        {
                            if (!task.isCompleted())
                            {
                                ArrayList<BaseTaskTarget> targetList = task
                                        .getTargetList();

                                for (BaseTaskTarget target : targetList)
                                {
                                    if (ETastTargetType.FOUND_A_PATH == target
                                            .getType()
                                            && !target.isCompleted()
                                            && target.getID() == info.tastTargetID)
                                    {
                                        ((TaskTargetFoundAPath) target)
                                                .complete();

                                        TaskDAO.updateTaskProgress(player
                                                .getUserID(), task);

                                        ResponseMessageQueue
                                                .getInstance()
                                                .put(
                                                        player
                                                                .getMsgQueueIndex(),
                                                        new RefreshTaskStatus(
                                                                task
                                                                        .getArchetype()
                                                                        .getID(),
                                                                target.getID(),
                                                                target
                                                                        .isCompleted(),
                                                                target
                                                                        .getDescripiton(),
                                                                task
                                                                        .isCompleted()));

                                        if (task.isCompleted())
                                        {
                                            Npc taskNpc = NotPlayerServiceImpl
                                                    .getInstance()
                                                    .getNpc(
                                                            task
                                                                    .getArchetype()
                                                                    .getSubmitNpcID());

                                            if (null != taskNpc
                                                    && taskNpc.where() == player
                                                            .where())
                                            {
                                                byte npcTaskMarks = getTaskMark(
                                                        taskNpc.getModelID(),
                                                        player);

                                                ResponseMessageQueue
                                                        .getInstance()
                                                        .put(
                                                                player
                                                                        .getMsgQueueIndex(),
                                                                new ChangeNpcTaskMark(
                                                                        taskNpc
                                                                                .getID(),
                                                                        npcTaskMarks));
                                            }
                                        }

                                        break;
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加探索目标
     * 
     * @param _taskID 任务编号
     * @param _foundAPathTarget 探索目标
     */
    private void addFoundPathTask (int _taskID,
            TaskTargetFoundAPath _foundAPathTarget)
    {
        FoundAPathInfo foundAPathInfo = new FoundAPathInfo();

        foundAPathInfo.taskID = _taskID;
        foundAPathInfo.tastTargetID = _foundAPathTarget.getID();
        foundAPathInfo.map = MapServiceImpl.getInstance().getNormalMapByID(
                _foundAPathTarget.mapID);
        foundAPathInfo.x = _foundAPathTarget.x;
        foundAPathInfo.y = _foundAPathTarget.y;
        foundAPathInfo.mistakeRang = _foundAPathTarget.mistakeRang;

        foundAPathTaskList.add(foundAPathInfo);
    }

    /**
     * 被护送的NPC到达目的地
     * 
     * @param _npc
     */
    private void reachDestination (EscortNpcTaskInfo _info)
    {
        synchronized (escortTaskExcuteList)
        {
            ArrayList<TaskInstance> taskList = playerExsitsTaskListMap
                    .get(_info.trigger.getUserID());

            boolean refreshTaskSuccessful = false;

            for (TaskInstance task : taskList)
            {
                if (refreshTaskSuccessful)
                {
                    break;
                }

                if (task.getArchetype() == _info.task)
                {
                    if (!task.isCompleted())
                    {
                        ArrayList<BaseTaskTarget> targetList = task
                                .getTargetList();

                        for (BaseTaskTarget target : targetList)
                        {
                            if (ETastTargetType.ESCORT_NPC == target.getType()
                                    && !target.isCompleted())
                            {
                                if (((TaskTargetEscortNpc) target).getID() == _info.escortTarget
                                        .getID())
                                {
                                    ((TaskTargetEscortNpc) target).complete();

                                    TaskDAO.updateTaskProgress(_info.trigger
                                            .getUserID(), task);

                                    ResponseMessageQueue.getInstance().put(
                                            _info.trigger.getMsgQueueIndex(),
                                            new RefreshTaskStatus(task
                                                    .getArchetype().getID(),
                                                    target.getID(), target
                                                            .isCompleted(),
                                                    target.getDescripiton(),
                                                    task.isCompleted()));

                                    if (task.isCompleted())
                                    {
                                        Npc taskNpc = NotPlayerServiceImpl
                                                .getInstance()
                                                .getNpc(
                                                        task
                                                                .getArchetype()
                                                                .getSubmitNpcID());

                                        if (null != taskNpc
                                                && taskNpc.where() == _info.trigger
                                                        .where())
                                        {
                                            byte npcTaskMarks = getTaskMark(
                                                    taskNpc.getModelID(),
                                                    _info.trigger);

                                            ResponseMessageQueue
                                                    .getInstance()
                                                    .put(
                                                            _info.trigger
                                                                    .getMsgQueueIndex(),
                                                            new ChangeNpcTaskMark(
                                                                    taskNpc
                                                                            .getID(),
                                                                    npcTaskMarks));
                                        }
                                    }

                                    refreshTaskSuccessful = true;

                                    break;
                                }
                            }
                        }
                    }

                    break;
                }
            }

            if (null != _info.spareTaskMemberList)
            {
                for (HeroPlayer other : _info.spareTaskMemberList)
                {
                    if (_info.npc.where() == other.where())
                    {
//                        double distance = Math.sqrt(Math.pow(
//                                (other.getCellX() - _info.npc.getCellX()), 2)
//                                + Math.pow((other.getCellY() - _info.npc
//                                        .getCellY()), 2));
                        boolean inDistance = ESCORT_TASK_VALIDATE_DISTANCE*ESCORT_TASK_VALIDATE_DISTANCE
                                    >= (other.getCellX() - _info.npc.getCellX())*(other.getCellX() - _info.npc.getCellX())
                                        +(other.getCellY() - _info.npc.getCellY())*(other.getCellY() - _info.npc.getCellY());

//                        if (ESCORT_TASK_VALIDATE_DISTANCE >= distance)
                        if(inDistance)
                        {
                            taskList = playerExsitsTaskListMap.get(other
                                    .getUserID());

                            refreshTaskSuccessful = false;

                            for (TaskInstance task : taskList)
                            {
                                if (refreshTaskSuccessful)
                                {
                                    break;
                                }

                                if (task.getArchetype() == _info.task)
                                {
                                    if (!task.isCompleted())
                                    {
                                        ArrayList<BaseTaskTarget> targetList = task
                                                .getTargetList();

                                        for (BaseTaskTarget target : targetList)
                                        {
                                            if (ETastTargetType.ESCORT_NPC == target
                                                    .getType()
                                                    && !target.isCompleted())
                                            {
                                                if (((TaskTargetEscortNpc) target)
                                                        .getID() == _info.escortTarget
                                                        .getID())
                                                {
                                                    ((TaskTargetEscortNpc) target)
                                                            .complete();

                                                    TaskDAO.updateTaskProgress(
                                                            other.getUserID(),
                                                            task);

                                                    ResponseMessageQueue
                                                            .getInstance()
                                                            .put(
                                                                    other
                                                                            .getMsgQueueIndex(),
                                                                    new RefreshTaskStatus(
                                                                            task
                                                                                    .getArchetype()
                                                                                    .getID(),
                                                                            target
                                                                                    .getID(),
                                                                            target
                                                                                    .isCompleted(),
                                                                            target
                                                                                    .getDescripiton(),
                                                                            task
                                                                                    .isCompleted()));

                                                    if (task.isCompleted())
                                                    {
                                                        Npc taskNpc = NotPlayerServiceImpl
                                                                .getInstance()
                                                                .getNpc(
                                                                        task
                                                                                .getArchetype()
                                                                                .getSubmitNpcID());

                                                        if (task.isCompleted()
                                                                && null != taskNpc
                                                                && taskNpc
                                                                        .where() == other
                                                                        .where())
                                                        {
                                                            byte npcTaskMarks = getTaskMark(
                                                                    taskNpc
                                                                            .getModelID(),
                                                                    other);

                                                            ResponseMessageQueue
                                                                    .getInstance()
                                                                    .put(
                                                                            other
                                                                                    .getMsgQueueIndex(),
                                                                            new ChangeNpcTaskMark(
                                                                                    taskNpc
                                                                                            .getID(),
                                                                                    npcTaskMarks));
                                                        }
                                                    }

                                                    refreshTaskSuccessful = true;

                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 探索地图任务管理器
     * 
     * @author DC
     */
    class FoundAPathTaskManager extends TimerTask
    {
        public void run ()
        {
            try
            {
                excuteFoundAPathTask();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 护送NPC任务管理器
     * 
     * @author DC
     */
    class EscortNpcTaskManager extends TimerTask
    {
        public void run ()
        {
            try
            {
                excuteEscortNpcTask();
            }
            catch (Exception e)
            {
                log.error("护送任务线程出错");
            }
        }
    }

    class EscortNpcTaskInfo
    {
        Task                          task;

        TaskTargetEscortNpc           escortTarget;

        HeroPlayer                    trigger;

        private ArrayList<HeroPlayer> spareTaskMemberList = new ArrayList<HeroPlayer>();

        Npc                           npc;

        int                           traceTime;

        /**
         * 获取共享任务成员列表
         * 
         * @return
         */
        public ArrayList<HeroPlayer> getSpareTaskMemberList ()
        {
            return spareTaskMemberList;
        }

        /**
         * 获取共享任务成员列表
         * 
         * @return
         */
        public void addSpareTaskMember (HeroPlayer _member)
        {
            if (null != _member)
            {
                spareTaskMemberList.add(_member);
            }
        }
    }

    class FoundAPathInfo
    {
        /**
         * 任务编号
         */
        int   taskID;

        /**
         * 任务目标编号
         */
        int   tastTargetID;

        /**
         * 探索的目的地图
         */
        Map   map;

        /**
         * 探索的目的地图位置
         */
        short x, y;

        /**
         * 探索目的范围的误差
         */
        short mistakeRang;
    }

	class TaskComparator implements Comparator<Task> {
	        @Override
	        public int compare(Task o1, Task o2) {
                Integer o1Level = Integer.valueOf(o1.getLevel());
                Integer o2Level = Integer.valueOf(o2.getLevel());

                Boolean o1MainLine = Boolean.valueOf(o1.getMainLine());
                Boolean o2MainLine = Boolean.valueOf(o2.getMainLine());

                Boolean o1IsRepeated = Boolean.valueOf(o1.isRepeated());
                Boolean o2IsRepeated = Boolean.valueOf(o2.isRepeated());

                if(o1MainLine && !o2MainLine){
                    return -1;
                }
                if(!o1MainLine && o2MainLine){
                    return 1;
                }
                if(o1MainLine && o2MainLine){
                    return 0;
                }
                if(o1Level.compareTo(o2Level) > 0){
                    return -1;
                }
                if(o1Level.compareTo(o2Level) == 0){
                    if(o1IsRepeated){
                        return 1;
                    }
                    return 0;
                }
                if(o1Level.compareTo(o2Level) < 0){
                    return 1;
                }

	            return 0;
	        }
	    }

    class TaskInstanceComparator implements Comparator<TaskInstance>{
        @Override
        public int compare(TaskInstance o1, TaskInstance o2) {
            Integer o1Level = Integer.valueOf(o1.getArchetype().getLevel());
                Integer o2Level = Integer.valueOf(o2.getArchetype().getLevel());

                Boolean o1MainLine = Boolean.valueOf(o1.getArchetype().getMainLine());
                Boolean o2MainLine = Boolean.valueOf(o2.getArchetype().getMainLine());

                Boolean o1IsRepeated = Boolean.valueOf(o1.getArchetype().isRepeated());
                Boolean o2IsRepeated = Boolean.valueOf(o2.getArchetype().isRepeated());

                if(o1MainLine && !o2MainLine){
                    return -1;
                }
                if(!o1MainLine && o2MainLine){
                    return 1;
                }
                if(o1MainLine && o2MainLine){
                    return 0;
                }
                if(o1Level.compareTo(o2Level) > 0){
                    return -1;
                }
                if(o1Level.compareTo(o2Level) == 0){
                    if(o1IsRepeated){
                        return 1;
                    }
                    return 0;
                }
                if(o1Level.compareTo(o2Level) < 0){
                    return 1;
                }

	            return 0;
        }
    }
}
