package hero.npc.ai;

import java.util.ArrayList;
import java.util.Random;

import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.skill.Skill;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.ETargetRangeType;
import hero.skill.service.SkillServiceImpl;
import hero.skill.unit.ActiveSkillUnit;
import hero.npc.Monster;
import hero.npc.ai.data.SkillAIData;
import hero.npc.message.MonsterShoutNotify;
import hero.npc.message.RangeSkillWarning;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Direction;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillAI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-18 下午03:47:09
 * @描述 ：
 */

public class SkillAI
{
     private static Logger log = Logger.getLogger(SkillAI.class);
    /**
     * 执行一次的技能是否执行过
     */
    private boolean     hasExecuted;

    /**
     * 上次执行时的剩余生命百分比
     */
    private float       lastTraceHpPercent;

    /**
     * 上次执行时的时间
     */
    private long        timeThatLastExecuted;

    /**
     * 施放技能时提示时间
     */
    private long        releaseWarningTime;

    /**
     * 延时施放的技能预先目标编号
     */
    private int         targetPlayerUserID;

    /**
     * 技能AI数据
     */
    private SkillAIData data;

    /**
     * 构造
     * 
     * @param _data
     */
    public SkillAI(SkillAIData _data, float _traceHpPercent)
    {
        data = _data;
        lastTraceHpPercent = _traceHpPercent;
    }

    /**
     * 执行
     */
    public void execute (Monster _dominator)
    {
        if (USE_TIMES_OF_INTERVAL == data.useTimesType)
        {
            if (data.intervalCondition == INTERVAL_CONDITION_OF_TIME)
            {
                long currentTime = System.currentTimeMillis();
                if (data == null || data.skill == null || data.skill.skillUnit == null) 
                {
					log.warn("怪物数据为NULL.请检查怪物AI表格");
					return;
				}

                if (currentTime - timeThatLastExecuted >= data.intervalTime
                        && currentTime - timeThatLastExecuted 
                        	>= ((ActiveSkillUnit) data.skill.skillUnit).releaseTime)
                {
                    byte releaseResult = releaseSkill(_dominator);

                    if (SkillAI.RELEASE_FAILER_OF_SKILL_CONDITION != releaseResult)
                    {
                        timeThatLastExecuted = currentTime;
                    }

                    if (SkillAI.RELEASE_SUCCESS == releaseResult)
                    {
                        _dominator.refreshLastAttackTime();
                    }
                }
            }
            else if (data.intervalCondition == INTERVAL_CONDITION_OF_HP_CONSUME)
            {
                if (lastTraceHpPercent - _dominator.getHPPercent() >= data.hpConsumePercent)
                {
                    byte releaseResult = releaseSkill(_dominator);

                    if (SkillAI.RELEASE_FAILER_OF_SKILL_CONDITION != releaseResult)
                    {
                        lastTraceHpPercent = _dominator.getHPPercent();
                    }

                    if (SkillAI.RELEASE_SUCCESS == releaseResult)
                    {
                        _dominator.refreshLastAttackTime();
                    }
                }
            }
            else if (data.intervalCondition == INTERVAL_CONDITION_OF_HATRED_TARGET_DIE)
            {
                //
            }
        }
        else
        {
            if (!hasExecuted)
            {
                if (data.onlyReleaseCondition == ONLY_CONDITION_OF_FIGHT_TIME)
                {
                    if (System.currentTimeMillis()
                            - _dominator.getTimeOfEnterFight() >= data.timeOfFighting)
                    {
                        byte releaseResult = releaseSkill(_dominator);

                        if (SkillAI.RELEASE_FAILER_OF_SKILL_CONDITION != releaseResult)
                        {
                            hasExecuted = true;
                        }

                        if (SkillAI.RELEASE_SUCCESS == releaseResult)
                        {
                            _dominator.refreshLastAttackTime();
                        }
                    }
                }
                else if (data.onlyReleaseCondition == ONLY_CONDITION_OF_TRACE_HP)
                {
                    if (_dominator.getHPPercent() <= data.hpTracePercent)
                    {
                        byte releaseResult = releaseSkill(_dominator);

                        if (SkillAI.RELEASE_FAILER_OF_SKILL_CONDITION != releaseResult)
                        {
                            hasExecuted = true;
                        }

                        if (SkillAI.RELEASE_SUCCESS == releaseResult)
                        {
                            _dominator.refreshLastAttackTime();
                        }
                    }
                }
                else if (data.onlyReleaseCondition == ONLY_CONDITION_OF_TRACE_MP)
                {
                    if (_dominator.getMPPercent() <= data.mpTracePercent)
                    {
                        byte releaseResult = releaseSkill(_dominator);

                        if (SkillAI.RELEASE_FAILER_OF_SKILL_CONDITION != releaseResult)
                        {
                            hasExecuted = true;
                        }

                        if (SkillAI.RELEASE_SUCCESS == releaseResult)
                        {
                            _dominator.refreshLastAttackTime();
                        }
                    }
                }
                else
                {
                    hasExecuted = true;
                }
            }
        }
    }

    /**
     * 施放技能
     */
    private byte releaseSkill (Monster _dominator)
    {
        if (data.releaseDelay > 0)
        {
            if (releaseWarningTime == 0)
            {
                if (RANDOM.nextFloat() <= data.odds)
                {
                    releaseWarningTime = System.currentTimeMillis();

                    MapSynchronousInfoBroadcast.getInstance().put(
                            _dominator.where(),
                            new MonsterShoutNotify(_dominator.getID(),
                                    data.shoutContentWhenRelease), false, 0);

                    HeroPlayer target = getTarget(_dominator);

                    if (null != target)
                    {
                        targetPlayerUserID = target.getUserID();

                        if (ETargetRangeType.SOME == ((ActiveSkillUnit) data.skill.skillUnit).targetRangeType)
                        {
                            RangeSkillWarning msg = null;

                            if (EAOERangeType.CENTER == ((ActiveSkillUnit) data.skill.skillUnit).rangeMode)
                            {
                                if (EAOERangeBaseLine.RELEASER == ((ActiveSkillUnit) data.skill.skillUnit).rangeBaseLine)
                                {
                                    msg = new RangeSkillWarning(
                                            ((ActiveSkillUnit) data.skill.skillUnit).rangeX,
                                            ((ActiveSkillUnit) data.skill.skillUnit).rangeY,
                                            true, _dominator.getObjectType()
                                                    .value(), _dominator
                                                    .getID(), 0, 0);
                                }
                                else
                                {
                                    msg = new RangeSkillWarning(
                                            ((ActiveSkillUnit) data.skill.skillUnit).rangeX,
                                            ((ActiveSkillUnit) data.skill.skillUnit).rangeY,
                                            true, target.getObjectType()
                                                    .value(), target.getID(),
                                            0, 0);
                                }
                            }
                            else
                            {
                                int upperLeftX = 0, upperLeftY = 0, xLength = 0, yLength = 0;

                                switch (_dominator.getDirection())
                                {
                                    case Direction.UP:
                                    {
                                        xLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeX;
                                        yLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeY;

                                        int radiu = xLength / 2;

                                        upperLeftX = _dominator.getCellX()
                                                - radiu;
                                        upperLeftY = _dominator.getCellY()
                                                - yLength;

                                        break;
                                    }
                                    case Direction.DOWN:
                                    {
                                        xLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeX;
                                        yLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeY;

                                        int radiu = xLength / 2;

                                        upperLeftX = _dominator.getCellX()
                                                - radiu;
                                        upperLeftY = _dominator.getCellY() + 1;

                                        break;
                                    }
                                    case Direction.LEFT:
                                    {
                                        xLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeY;
                                        yLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeX;

                                        int radiu = yLength / 2;

                                        upperLeftX = _dominator.getCellX()
                                                - xLength;
                                        upperLeftY = _dominator.getCellY()
                                                - radiu;

                                        break;
                                    }
                                    case Direction.RIGHT:
                                    {
                                        xLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeY;
                                        yLength = ((ActiveSkillUnit) data.skill.skillUnit).rangeX;

                                        int radiu = yLength / 2;

                                        upperLeftX = _dominator.getCellX() + 1;
                                        upperLeftY = _dominator.getCellY()
                                                + radiu;

                                        break;
                                    }
                                }

                                msg = new RangeSkillWarning(xLength, yLength,
                                        false, 0, 0, upperLeftX, upperLeftY);
                            }

                            MapSynchronousInfoBroadcast.getInstance().put(
                                    _dominator.where(), msg, false, 0);
                        } else if (ETargetRangeType.SINGLE == ((ActiveSkillUnit) data.skill.skillUnit).targetRangeType) {
							//add by zhengl ; date: 2010-12-10 ; note: 怪物欠缺释放技能的代码,现添上.
                        	SkillServiceImpl.getInstance().monsterReleaseSkill(
                        			_dominator, target, _dominator.getDirection(), data.skill);
                        	log.info("怪物释放:" + data.skill.name);
                        	log.info("技能单元:" + data.skill.skillUnit.name);
                        	if( ((Skill)(data.skill)).addEffectUnit == null ){
                        		log.info("技能附带的效果单元为null");
                        	}
						}

                        return RELEASE_WAIT;
                    }
                    else
                    {
                        return RELEASE_FAILER_OF_ODDS;
                    }
                }
                else
                {
                    return RELEASE_FAILER_OF_ODDS;
                }
            }
            else
            {
                if (System.currentTimeMillis() - releaseWarningTime >= data.releaseDelay)
                {
                    HeroPlayer target = PlayerServiceImpl.getInstance()
                            .getPlayerByUserID(targetPlayerUserID);

                    if (null == target || !target.isEnable() || target.isDead()
                            || target.where() != _dominator.where())
                    {
                        target = getTarget(_dominator);

                        if (null != target)
                        {
                            targetPlayerUserID = target.getUserID();
                        }
                    }

                    if (SkillServiceImpl.getInstance().monsterReleaseSkill(
                            _dominator, target,
                            _dominator.getDirection(), data.skill))
                    {
                        releaseWarningTime = 0;

                        return RELEASE_SUCCESS;
                    }
                    else
                    {
                        return RELEASE_FAILER_OF_SKILL_CONDITION;
                    }
                }
                else
                {
                    return RELEASE_WAIT;
                }
            }
        }
        else
        {
            if (RANDOM.nextFloat() <= data.odds)
            {
                if (SkillServiceImpl.getInstance().monsterReleaseSkill(
                        _dominator, getTarget(_dominator),
                        _dominator.getDirection(), data.skill))
                {
                    return RELEASE_SUCCESS;
                }
                else
                {
                    return RELEASE_FAILER_OF_SKILL_CONDITION;
                }
            }
            else
            {
                return RELEASE_FAILER_OF_ODDS;
            }
        }
    }

    /**
     * 获取条件攻击目标
     * 
     * @param _condition
     * @param _sequence
     * @return
     */
    private HeroPlayer getTarget (Monster _dominator)
    {
        HeroPlayer target = null;

        switch (data.targetSettingCondition)
        {
            case SkillAI.TARGET_SET_CONDITION_OF_HATRED:
            {
                target = _dominator
                        .getHatredTargetByHatredSequence(data.sequenceOfSettingCondition);

                break;
            }
            case SkillAI.TARGET_SET_CONDITION_OF_DISTANCE:
            {
                ArrayList<HeroPlayer> targetList = _dominator
                        .getHatredTargetList();

                if (null != targetList)
                {
                    ArrayList<HeroPlayer> templeteList = new ArrayList<HeroPlayer>();

                    for (HeroPlayer hatredTarget : targetList)
                    {
                        if (hatredTarget.where() == _dominator.where()
                                && !hatredTarget.isDead())
                        {
                            HeroPlayer player = null;
                            int j = 0;

                            for (; j < templeteList.size(); j++)
                            {
                                player = templeteList.get(j);

                                /*if (Math.sqrt(Math.pow(hatredTarget.getCellX()
                                        - _dominator.getCellX(), 2)
                                        + Math.pow(hatredTarget.getCellY()
                                                - _dominator.getCellY(), 2)) <= Math
                                        .sqrt(Math.pow(player.getCellX()
                                                - _dominator.getCellX(), 2)
                                                + Math
                                                        .pow(
                                                                player
                                                                        .getCellY()
                                                                        - _dominator
                                                                                .getCellY(),
                                                                2)))*/
                                boolean inDistance = (hatredTarget.getCellX()-_dominator.getCellX())*(hatredTarget.getCellX()-_dominator.getCellX())+(hatredTarget.getCellY()-_dominator.getCellY())*(hatredTarget.getCellY()-_dominator.getCellY())
                                        <= (player.getCellX()-_dominator.getCellX())*(player.getCellX()-_dominator.getCellX())+(player.getCellY()-_dominator.getCellY())*(player.getCellY()-_dominator.getCellY());
                                if(inDistance)
                                {
                                    break;
                                }
                            }

                            templeteList.add(j, player);
                        }
                    }

                    if (data.sequenceOfSettingCondition <= templeteList.size())
                    {
                        target = templeteList
                                .get(data.sequenceOfSettingCondition - 1);
                    }
                    else
                    {
                        target = _dominator.getTopHatredTarget();
                    }
                }

                break;
            }
            case SkillAI.TARGET_SET_CONDITION_OF_SELF_CENTER:
            {
                break;
            }
            case SkillAI.TARGET_SET_CONDITION_OF_HP:
            {
                ArrayList<HeroPlayer> targetList = _dominator
                        .getHatredTargetList();

                if (null != targetList)
                {
                    ArrayList<HeroPlayer> templeteList = new ArrayList<HeroPlayer>();

                    for (HeroPlayer hatredTarget : targetList)
                    {
                        if (hatredTarget.where() == _dominator.where()
                                && !hatredTarget.isDead())
                        {
                            HeroPlayer player = null;
                            int j = 0;

                            for (; j < templeteList.size(); j++)
                            {
                                player = templeteList.get(j);

                                if (hatredTarget.getHp() >= player.getHp())
                                {
                                    break;
                                }
                            }

                            templeteList.add(j, player);
                        }
                    }

                    if (data.sequenceOfSettingCondition <= templeteList.size())
                    {
                        target = templeteList
                                .get(data.sequenceOfSettingCondition - 1);
                    }
                    else
                    {
                        target = _dominator.getTopHatredTarget();
                    }
                }

                break;
            }
            case SkillAI.TARGET_SET_CONDITION_OF_MP:
            {
                ArrayList<HeroPlayer> targetList = _dominator
                        .getHatredTargetList();

                if (null != targetList)
                {
                    ArrayList<HeroPlayer> templeteList = new ArrayList<HeroPlayer>();

                    for (HeroPlayer hatredTarget : targetList)
                    {
                        if (hatredTarget.where() == _dominator.where()
                                && !hatredTarget.isDead())
                        {
                            HeroPlayer player = null;
                            int j = 0;

                            for (; j < templeteList.size(); j++)
                            {
                                player = templeteList.get(j);

                                if (hatredTarget.getMp() >= player.getMp())
                                {
                                    break;
                                }
                            }

                            templeteList.add(j, player);
                        }
                    }

                    if (data.sequenceOfSettingCondition <= templeteList.size())
                    {
                        target = templeteList
                                .get(data.sequenceOfSettingCondition - 1);
                    }
                    else
                    {
                        target = _dominator.getTopHatredTarget();
                    }
                }

                break;
            }
        }

        return target;
    }

    /**
     * 间隔施放类型
     */
    public static final byte    USE_TIMES_OF_INTERVAL                   = 1;

    /**
     * 只施放一次类型
     */
    public static final byte    USE_TIMES_OF_ONLY                       = 2;

    /**
     * 间隔施放类型的时间条件类型
     */
    public static final byte    INTERVAL_CONDITION_OF_TIME              = 1;

    /**
     * 间隔施放类型的生命消耗百分比类型
     */
    public static final byte    INTERVAL_CONDITION_OF_HP_CONSUME        = 2;

    /**
     * 间隔施放类型的仇恨目标死亡时
     */
    public static final byte    INTERVAL_CONDITION_OF_HATRED_TARGET_DIE = 3;

    /**
     * 施放一次类型的进入战斗后多久
     */
    public static final byte    ONLY_CONDITION_OF_FIGHT_TIME            = 1;

    /**
     * 施放一次类型的剩余生命值比例
     */
    public static final byte    ONLY_CONDITION_OF_TRACE_HP              = 2;

    /**
     * 施放一次类型的剩余魔法值比例
     */
    public static final byte    ONLY_CONDITION_OF_TRACE_MP              = 3;

    /**
     * 目标判断条件－仇恨值
     */
    public static final byte    TARGET_SET_CONDITION_OF_HATRED          = 1;

    /**
     * 目标判断条件－生命值
     */
    public static final byte    TARGET_SET_CONDITION_OF_HP              = 2;

    /**
     * 目标判断条件－魔法值
     */
    public static final byte    TARGET_SET_CONDITION_OF_MP              = 3;

    /**
     * 目标判断条件－距离
     */
    public static final byte    TARGET_SET_CONDITION_OF_DISTANCE        = 4;

    /**
     * 目标判断条件－以自己为中心
     */
    public static final byte    TARGET_SET_CONDITION_OF_SELF_CENTER     = 5;

    /**
     * 施放失败原因因为几率
     */
    private static final byte   RELEASE_FAILER_OF_ODDS                  = -1;

    /**
     * 施放失败原因因为技能条件
     */
    private static final byte   RELEASE_FAILER_OF_SKILL_CONDITION       = 0;

    /**
     * 施放等待中
     */
    private static final byte   RELEASE_WAIT                            = 2;

    /**
     * 施放成功
     */
    private static final byte   RELEASE_SUCCESS                         = 1;

    /**
     * 随机数生成器
     */
    private static final Random RANDOM                                  = new Random();
}
