package hero.npc.ai;

import hero.expressions.service.CEService;
import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.fight.clienthandler.PhysicsAttack;
import hero.fight.service.FightServiceImpl;
import hero.fight.service.SpecialStatusDefine;
import hero.player.HeroPlayer;
import hero.share.Direction;
import hero.share.service.ME2ObjectList;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;


import hero.map.Decorater;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.service.MapServiceImpl;
import hero.npc.ENotPlayerIntention;
import hero.npc.Monster;
import hero.npc.ai.data.AIDataDict;
import hero.npc.ai.data.Changes;
import hero.npc.ai.data.FightAIData;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.message.MonsterEmergeNotify;
import hero.npc.service.AStar;
import hero.npc.service.NotPlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterAI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-21 上午10:07:17
 * @描述 ：
 */

public class MonsterAI implements Runnable
{
	private static Logger log = Logger.getLogger(MonsterAI.class);
    /**
     * 支配者
     */
    private Monster    dominator;

    /**
     * 在一次战斗中追击时行走的距离（格子数）
     */
    private int        chaseGridNum;

    /**
     * 移动路径
     */
    private byte[][]   movePath;
    
    /**
     * 新地图移动路径
     */
    private Decorater[] newMovePath;

    /**
     * 当前位置在行走路径中的顺序
     */
    private int        currentLocationOfPath;

    /**
     * 上次位置在行走路径中的顺序
     */
    private int        lastLocationOfPath;

    /**
     * 移动路线类型
     */
    private byte       movePathType;

    /**
     * 上次自由走动的时间，怪物每次间隔走动，缓解客户端端怪物动作队列积压过多现象
     */
    private long       lastActiveMoveTime;

    /**
     * 特殊AI列表
     */
    public SpecialAI[] specialAIList;

    /**
     * 原始技能AI列表
     */
    public SkillAI[]   skillAIList;

    /**
     * 当前技能AI列表(变身后将使用与原始技能不一致的技能)
     */
    public SkillAI[]   currentSkillAIList;

    /**
     * 剩余的消失状态保持时间
     */
    public long        traceDisappearTime;

    /**
     * 剩余的逃跑距离
     */
    public int         traceRunAwayGrid;

    /**
     * 当前变身状态下的数据逻辑
     */
    public Changes     currentChangesData;

    /**
     * 变身时间点
     */
    public long        timeClockWhenChanges;

    /**
     * 变身时生命的剩余百分比
     */
    public int         traceHpWhenChanges;

    /**
     * 构造
     * 
     * @param _monster AI支配者
     */
    public MonsterAI(Monster _monster)
    {
        dominator = _monster;
        movePathType = MOVE_TYPE_OF_NONE;
        initFightAI();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run ()
    {
        try
        {
        	//edit by zhengl; date: 2011-03-27; note: 怪物激活且没死亡的情况下.
            if ( dominator.isEnable() && (!dominator.isDead()) )
            {
                if (dominator.isCalled())
                {
                    if (System.currentTimeMillis() - dominator.getRefreshTime() >= dominator
                            .getExistsTime())
                    {
                        dominator.clearFightInfo();
                        dominator.destroy();
                        dominator.where().getMonsterList().remove(dominator);
                        SpecialViewStatusBroadcast.send(dominator,
                                SpecialStatusDefine.DIE);

                        return;
                    }
                }

                if (dominator.getIntention() == ENotPlayerIntention.AI_INTENTION_ACTIVE)
                {
                    onIntentionActive();
                }
                else if (dominator.getIntention() == ENotPlayerIntention.AI_INTENTION_ATTACK)
                {
                    onIntentionAttack();
                }
            }
            else
            {
            	if(dominator.isEnable() == true && dominator.isDead() == true)
            	{
            		log.error("it enable is true but it dead state is true");
            		log.error("monter's name=" + dominator.getName() + ";modeid=" 
            				+ dominator.getModelID());
            	}
                if (System.currentTimeMillis() - dominator.getDieTime() >= dominator
                        .getRetime() * 1000)
                {
                    if (dominator.isInDungeon())
                    {
                        if (null != dominator.where().getMonsterModelIDAbout())
                        {
                            ME2ObjectList monsterList = dominator.where()
                                    .getMonsterList();

                            boolean aboutMonsterIsDeath = true;

                            for (int i = 0; i < monsterList.size(); i++)
                            {
                                if ( ((Monster) monsterList.get(i)).getModelID().equals(
                                		dominator.where().getMonsterModelIDAbout()) )
                                {
                                    aboutMonsterIsDeath = false;

                                    break;
                                }
                            }

                            if (aboutMonsterIsDeath)
                            {
                                dominator.destroy();

                                return;
                            }
                        }
                        else
                        {
                            dominator.destroy();

                            return;
                        }
                    }

                    dominator.revive(null);
                    dominator.setIntention(ENotPlayerIntention.AI_INTENTION_ACTIVE);
                }
            }
        }
        catch (Exception e)
        {
            log.error("怪物AI error : ",e);
        }
    }

    /**
     * 处于随机行走或扫描可攻击目标时的逻辑
     */
    private void onIntentionActive ()
    {
        if (dominator.isActiveAttackType())
        {
            HeroPlayer player = scanAttackableTarget();

            if (null != player)
            {
                player.enterFight();
                dominator.addHattedTarget(player);
                dominator.changeInvention(
                        ENotPlayerIntention.AI_INTENTION_ATTACK, player);

                return;
            }
        }

        if (dominator.where().getPlayerList().size() > 0)
            if (RANDOM_BUILDER.nextBoolean())
            {
                walk();
            }
    }

    /**
     * 初始化战斗AI
     */
    private void initFightAI ()
    {
        FightAIData data = AIDataDict.getInstance().getFightAIData(
                dominator.getFightAIID());

        if (null != data)
        {
            if (null != data.specialAIList)
            {
                specialAIList = new SpecialAI[data.specialAIList.length];

                for (int i = 0; i < data.specialAIList.length; i++)
                {
                    specialAIList[i] = new SpecialAI(dominator,
                            data.specialAIList[i]);
                }
            }

            if (null != data.skillAIList)
            {
                skillAIList = AIDataDict.getInstance().buildSkillAIList(
                        data.skillAIList, dominator.getHPPercent());
                currentSkillAIList = skillAIList;
            }
        }
    }

    /**
     * 清除AI动态数据
     */
    public void clearDynamicData ()
    {
        chaseGridNum = 0;
        currentLocationOfPath = 0;
        lastLocationOfPath = 0;
    }

    private void ai ()
    {
    	try {
            if (dominator.isVisible())
            {
                if (!dominator.isRunningAway())
                {
                    if (!dominator.isChangesStatus())
                    {
                        executeSpecialAI();
                    }
                    else
                    {
                        if (currentChangesData.endChanges(dominator,
                                timeClockWhenChanges, traceHpWhenChanges))
                        {
                            executeSpecialAI();
                        }
                    }
                }
                else
                {
                    if (traceRunAwayGrid > 0)
                    {
                        walkWhenRunAway();

                        if (traceRunAwayGrid == 0)
                        {
                            executeSpecialAI();
                        }
                        else
                        {
                            return;
                        }
                    }
                }
            }
            else
            {
                traceDisappearTime -= 1000;

                if (traceDisappearTime <= 0)
                {
                    gotoValideEmergeVocation();

                    MapSynchronousInfoBroadcast.getInstance().put(
                            dominator.where(),
                            new MonsterEmergeNotify(dominator.getID(), dominator
                                    .getCellX(), dominator.getCellY()), false, 0);

                    dominator.emerge();

                    executeSpecialAI();
                }
                else
                {
                    return;
                }
            }

            if (dominator.isVisible() && !dominator.isRunningAway())
            {
                executeSkillAI();
            }
		} catch (Exception e) {
			if (dominator != null) 
			{
				log.error("怪物="+dominator.getName()+";id="+dominator.getModelID());
			}
			else 
			{
				log.error("怪物为NULL ");
			}
            log.error("怪物AI出现严重异常:",e);
		}

    }

    /**
     * 设置移动路径
     * 
     * @param _movePath
     */
    public void setMovePath (byte[][] _movePath)
    {
        if (null != _movePath)
        {
            movePath = _movePath;

            if (movePath[movePath.length - 1][0] == movePath[0][0]
                    && movePath[movePath.length - 1][1] == movePath[0][1])
            {
                movePathType = MOVE_TYPE_OF_AROUND;
            }
            else
            {
                movePathType = MOVE_TYPE_OF_LINE;
            }
        }
    }
    
    /**
     * 新地图设置移动路径
     * @param _movePath
     */
    public void setMovePath(Decorater[] _movePath){
    	if(null != _movePath){
    		newMovePath = _movePath;
    		if(newMovePath[newMovePath.length-1].x == newMovePath[0].x
    				&& newMovePath[newMovePath.length-1].y == newMovePath[0].y){
    			movePathType = MOVE_TYPE_OF_AROUND;
            }
            else
            {
                movePathType = MOVE_TYPE_OF_LINE;
            }
    	}
    }

    private void executeSpecialAI ()
    {
        if (null != specialAIList)
        {
            for (SpecialAI ai : specialAIList)
            {
                if (!dominator.isVisible() || dominator.isRunningAway()
                        || dominator.isChangesStatus())
                {
                    break;
                }

                ai.execute();
            }
        }
    }

    private void executeSkillAI ()
    {
        if (null != currentSkillAIList)
        {
            for (SkillAI ai : currentSkillAIList)
            {
                if (!dominator.isVisible() || dominator.isRunningAway())
                {
                    break;
                }

                ai.execute(dominator);
            }
        }
    }

    /**
     * 战斗状态下的逻辑
     * 
     * @param target
     * @追踪距离 攻击型NPC追击距离：间隔10格（与原始坐标比较），行走30格
     */
    private void onIntentionAttack ()
    {
        if (!dominator.inValidateFightTime()
                || dominator.getHatredTargetNums() == 0)
        {
            disengageFight();

            return;
        }

        normalAttack();
    }

    /**
     * 战斗逻辑
     */
    public void normalAttack ()
    {
        HeroPlayer target = dominator.getAttackTarget();

        if (null == target)
        {
            disengageFight();

            return;
        }

        if (0 == dominator.getLastAttackTime())
        {
            dominator.setFirstAttackTime();
        }

        if (dominator.moveable() && !dominator.isInsensible() && !dominator.isSleeping())
        {
//            double distance = Math.sqrt(Math.pow((dominator.getCellX() - target.getCellX()), 2)
//                    + Math.pow((dominator.getCellY() - target.getCellY()), 2));
            //edit by zhengl ; date: 2011-01-07 ; note: 怪物攻击距离加上怪物体积,这样攻击显得合理
			MonsterImageConfDict.Config monsterConfig = 
				MonsterImageConfDict.get( ((Monster)dominator).getImageID() );
//			attackIsRoad
//			boolean isRoad = dominator.where().attackIsRoad(
//					dominator.getCellX(), dominator.getCellY(), 
//					target.getCellX(), target.getCellY());
            boolean inDistance = (dominator.getCellX() - target.getCellX())*(dominator.getCellX() - target.getCellX())
                        + (dominator.getCellY() - target.getCellY())*(dominator.getCellY() - target.getCellY())
                            <= (dominator.getAttackRange() + monsterConfig.grid/2)*(dominator.getAttackRange() + monsterConfig.grid/2);
//            if (distance <= dominator.getAttackRange() + monsterConfig.grid/2)
            if(inDistance)
            {
                if (System.currentTimeMillis() - dominator.getLastAttackTime() >= dominator
                        .getActualAttackImmobilityTime())
                {
                	this.ai();
                    FightServiceImpl.getInstance().processPhysicsAttack(
                            dominator, target);

                    dominator.refreshLastAttackTime();
                }
            }
            else
            {
                byte[] activeDirection = AStar.getPath(dominator.getCellX(),
                        dominator.getCellY(), target.getCellX(), target
                                .getCellY(), NotPlayerServiceImpl.getInstance()
                                .getConfig().MONSTER_MOVE_MOST_FAST_GRID,
                        dominator.getAttackRange(), dominator.where());

                if (null != activeDirection)
                {
                	this.ai();
                	//edit by zhengl; date: 2011-05-07; note: 添加x,y坐标
//                    OutMsgQ.getInstance().put(target.getMsgQueueIndex(),
//                            new MonsterWalkNotify(dominator.getID(), 
//                            		dominator.getMoveSpeed(), 
//                            		activeDirection, 
//                            		(byte)dominator.getCellX(), 
//                            		(byte)dominator.getCellY()));

                    dominator.goAlone(activeDirection, target);

                    chase(activeDirection.length);

                    /*distance = Math.sqrt(Math.pow(
                            (dominator.getCellX() - target.getCellX()), 2)
                            + Math.pow((dominator.getCellY() - target
                                    .getCellY()), 2));*/
//                    if (distance <= dominator.getAttackRange())

                    inDistance = (dominator.getCellX() - target.getCellX())*(dominator.getCellY() - target.getCellY())
                                <= dominator.getAttackRange()*dominator.getAttackRange();

                    if(inDistance)
                    {
                        if (System.currentTimeMillis()
                                - dominator.getLastAttackTime() >= dominator
                                .getActualAttackImmobilityTime())
                        {
                            FightServiceImpl.getInstance()
                                    .processPhysicsAttack(dominator, target);

                            dominator.refreshLastAttackTime();
                        }
                    }
/*
                    distance = Math.sqrt(
                    		Math.pow((dominator.getOrgX() - dominator.getCellX()), 2) 
                    				+ Math.pow((dominator.getOrgY() - dominator.getCellY()), 2));

                    //edit by zhengl; date: 2011-03-22; note: 固定数值写进配置
                    if (distance > NotPlayerServiceImpl.getInstance().getConfig().ai_follow_distance 
                    		&& chaseGridNum > NotPlayerServiceImpl.getInstance().getConfig().ai_follow_grid)*/

                    inDistance = (dominator.getOrgX() - dominator.getCellX())*(dominator.getOrgX() - dominator.getCellX())
                                + (dominator.getOrgY() - dominator.getCellY()) * (dominator.getOrgY() - dominator.getCellY())
                                > NotPlayerServiceImpl.getInstance().getConfig().ai_follow_distance * NotPlayerServiceImpl.getInstance().getConfig().ai_follow_distance
                                && chaseGridNum > NotPlayerServiceImpl.getInstance().getConfig().ai_follow_grid;
                    if(inDistance)
                    {
                        dominator.hatredTargetAway(target);
                    }
                }
                else
                {
                	//hahaha... this monster not find what is north
                    disengageFight();
                }
            }
        }
    }

    /**
     * 脱离战斗
     */
    private void disengageFight ()
    {
        clearDynamicData();
        dominator.disengageFight();
    }

    /**
     * 追击
     * 
     * @param _gridNum 追击时行走的格子数
     */
    public void chase (int _gridNum)
    {
        chaseGridNum += _gridNum;
    }

    /**
     * 根据视野计算公式，获取可攻击目标（玩家）
     * 
     * @return 可被攻击的玩家
     */
    private HeroPlayer scanAttackableTarget ()
    {
        ArrayList<HeroPlayer> knownPlayerList = MapServiceImpl.getInstance()
                .getMonsterValidateTargetListInCircle(dominator.where(),
                        dominator, FAST_DISTANCE);

        if (null == knownPlayerList || knownPlayerList.size() == 0)
        {
            return null;
        }

        for (HeroPlayer player : knownPlayerList)
        {
            if (CEService.inAttackRange(dominator.getObjectLevel().value(),
                    dominator.getLevel(), dominator.getCellX(), dominator
                            .getCellY(), player.getLevel(), player.getCellX(),
                    player.getCellY()))
            {
                return player;
            }
        }

        return null;
    }

    /**
     * 设置怪物消失出现后的有效随机位置
     * 
     * @return
     */
    private void gotoValideEmergeVocation ()
    {
        byte newDirection = getRandomDirection();

        for (byte step = 0; step < 5; step++)
        {
            newDirection = trackNext(true, 1, newDirection);

            if (0 < newDirection)
            {
                dominator.go(newDirection);
            }
            else
            {
                break;
            }
        }
    }

    /**
     * 怪物逃跑过程中的移动
     * 
     * @return
     */
    public void walkWhenRunAway ()
    {
        int walkGridNum = traceRunAwayGrid >= GRID_NUMBER_OF_PER_WHEN_RUNAWAY ? GRID_NUMBER_OF_PER_WHEN_RUNAWAY
                : traceRunAwayGrid;

        byte[] movePath = new byte[walkGridNum];

        byte newDirection = getRandomDirection();

        for (byte step = 0; step < walkGridNum; step++)
        {
            newDirection = trackNext(true, 1, newDirection);

            if (0 < newDirection)
            {
                dominator.go(newDirection);
                movePath[step] = newDirection;
            }
            else
            {
                break;
            }
        }

        NotPlayerServiceImpl.getInstance().broadcastNotPlayerWalkPath(
                dominator, movePath, null);

        traceRunAwayGrid -= walkGridNum;
    }

    /**
     * 走动并返回行走路径
     * 
     * @return
     */
    private void walk ()
    {
        if (MOVE_TYPE_OF_NONE != movePathType)
        {
            if (dominator.moveable()
                    && System.currentTimeMillis() - lastActiveMoveTime >= NotPlayerServiceImpl
                            .getInstance().getConfig().MONSTER_ACTIVE_MOVE_INTERVAL)
            {
                int stepNums = 0;

                if (MOVE_TYPE_OF_AROUND == movePathType)
                {
                    stepNums = movePath.length - 1 > NotPlayerServiceImpl
                            .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME ? NotPlayerServiceImpl
                            .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME
                            : movePath.length - 1;
                }
                else
                {
                    if (currentLocationOfPath == movePath.length - 1)
                    {
                        stepNums = movePath.length - 1 > NotPlayerServiceImpl
                                .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME ? NotPlayerServiceImpl
                                .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME
                                : (movePath.length - 1);
                    }
                    else
                    {
                        if (0 == currentLocationOfPath
                                || currentLocationOfPath > lastLocationOfPath)
                        {
                            stepNums = movePath.length - 1
                                    - currentLocationOfPath > NotPlayerServiceImpl
                                    .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME ? NotPlayerServiceImpl
                                    .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME
                                    : (movePath.length - 1 - currentLocationOfPath);
                        }
                        else
                        {
                            stepNums = currentLocationOfPath > NotPlayerServiceImpl
                                    .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME ? NotPlayerServiceImpl
                                    .getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME
                                    : currentLocationOfPath;
                        }
                    }
                }

                byte[] movePath = new byte[stepNums];

                byte newDirection;

                for (byte step = 0; step < stepNums; step++)
                {
                    newDirection = trackFixedPath();

                    if (0 < newDirection)
                    {
                        dominator.go(newDirection);
                        movePath[step] = newDirection;
                    }
                    else
                    {
                        break;
                    }
                }

                lastActiveMoveTime = System.currentTimeMillis();

                NotPlayerServiceImpl.getInstance().broadcastNotPlayerWalkPath(
                        dominator, movePath, null);
            }
        }
    }

    private byte trackFixedPath ()
    {
        if (MOVE_TYPE_OF_AROUND == movePathType)
        {
            if (currentLocationOfPath < movePath.length - 2)
            {
                currentLocationOfPath++;
            }
            else
            {
                currentLocationOfPath = 0;
            }
        }
        else if (MOVE_TYPE_OF_LINE == movePathType)
        {
            if (currentLocationOfPath == 0)
            {
                lastLocationOfPath = currentLocationOfPath;
                currentLocationOfPath++;
            }
            else if (currentLocationOfPath == movePath.length - 1)
            {
                lastLocationOfPath = currentLocationOfPath;
                currentLocationOfPath--;
            }
            else
            {
                if (currentLocationOfPath > lastLocationOfPath)
                {
                    lastLocationOfPath = currentLocationOfPath;
                    currentLocationOfPath++;
                }
                else
                {
                    lastLocationOfPath = currentLocationOfPath;
                    currentLocationOfPath--;
                }
            }
        }

        int x = movePath[currentLocationOfPath][0], y = movePath[currentLocationOfPath][1];

        if (x == dominator.getCellX())
        {
            if (y < dominator.getCellY())
            {
                return Direction.UP;
            }
            else
            {
                return Direction.DOWN;
            }
        }
        else if (y == dominator.getCellY())
        {
            if (x < dominator.getCellX())
            {
                return Direction.LEFT;
            }
            else
            {
                return Direction.RIGHT;
            }
        }

        return -1;
    }

    /**
     * 循路递归
     * 
     * @param _trackTimes 当前是第几次循路
     * @return 已经做过的循路次数
     */
    private byte trackNext (boolean _first, int _trackTimes, byte _direction)
    {
        if (_first)
        {
            if (dominator.passable(_direction))
            {
                return _direction;
            }
            else
            {
                return trackNext(false, ++_trackTimes, _direction);
            }
        }
        else
        {
            if (_trackTimes <= TRACK_TIMES)
            {
                switch (_direction)
                {
                    case Direction.UP:
                    {
                        if (dominator.passable(Direction.RIGHT))
                        {
                            return Direction.RIGHT;
                        }
                        else
                        {
                            return trackNext(false, ++_trackTimes,
                                    Direction.RIGHT);
                        }
                    }
                    case Direction.DOWN:
                    {
                        if (dominator.passable(Direction.LEFT))
                        {
                            return Direction.LEFT;
                        }
                        else
                        {
                            return trackNext(false, ++_trackTimes,
                                    Direction.LEFT);
                        }
                    }
                    case Direction.LEFT:
                    {
                        if (dominator.passable(Direction.UP))
                        {
                            return Direction.UP;
                        }
                        else
                        {
                            return trackNext(false, ++_trackTimes, Direction.UP);
                        }
                    }
                    case Direction.RIGHT:
                    {
                        if (dominator.passable(Direction.DOWN))
                        {
                            return Direction.DOWN;
                        }
                        else
                        {
                            return trackNext(false, ++_trackTimes,
                                    Direction.DOWN);
                        }
                    }
                    default:
                    {
                        return 0;
                    }
                }
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * 获取随机方向
     * 
     * @return
     */
    private byte getRandomDirection ()
    {
        return (byte) (RANDOM_BUILDER.nextInt(4) + 1);
    }

    /**
     * 随机数生成器
     */
    private static final Random RANDOM_BUILDER                  = new Random();

    /**
     * 怪物巡逻最远距离（格子）
     */
    private static final int    FAST_DISTANCE                   = 9;

    /**
     * 对堵塞方向的重定向次数
     */
    private final static byte   TRACK_TIMES                     = 4;

    /**
     * 怪物逃跑时每次AI间隔移动的距离
     */
    private final static byte   GRID_NUMBER_OF_PER_WHEN_RUNAWAY = 3;

    /**
     * 不走动
     */
    private final static byte   MOVE_TYPE_OF_NONE               = 0;

    /**
     * 环绕方式路线
     */
    private final static byte   MOVE_TYPE_OF_AROUND             = 1;

    /**
     * 线形方式路线
     */
    private final static byte   MOVE_TYPE_OF_LINE               = 2;
}
