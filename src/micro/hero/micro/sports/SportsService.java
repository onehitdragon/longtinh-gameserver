package hero.micro.sports;

import java.util.TimerTask;

import yoyo.core.queue.ResponseMessageQueue;

import hero.chat.service.ChatServiceImpl;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.npc.Npc;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.Tip;
import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SportsService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-15 下午02:11:14
 * @描述 ：竞技服务
 */

public class SportsService
{
    /**
     * 玩家竞技场等级列表（short[] 代表各个势力的等级）
     */
    private static FastMap<Integer, short[]> sportsPointTable;

    /**
     * 等待队列
     */
    private FastList<SportsTeam>             waitingQueue;

    /**
     * 竞技场单元列表
     */
    private FastList<SportsUnit>             sportsUnitList;

    /**
     * 私有构造
     */
    private SportsService()
    {
        sportsPointTable = new FastMap<Integer, short[]>();
        waitingQueue = new FastList<SportsTeam>();
        sportsUnitList = new FastList<SportsUnit>();
    }

    /**
     * 获取玩家的竞技场等级列表
     * 
     * @param _userID
     * @return
     */
    public short[] getSportsPointList (int _userID)
    {
        return sportsPointTable.get(_userID);
    }

    /**
     * 获取玩家的竞技场等级列表
     * 
     * @param _userID
     * @return
     */
    public short getSportsPoint (int _userID, ESportsClan _sportsClan)
    {
        short[] sportsPointList = sportsPointTable.get(_userID);

        if (null != sportsPointList)
        {
            return sportsPointList[_sportsClan.getID() - 1];
        }

        return INIT_POINT;
    }

    /**
     * 添加等待队列
     * 
     * @param _group
     */
    public void add (HeroPlayer _leader, ESportsClan _sportsClan, byte _type)
    {
        if (_leader.getGroupID() <= 0)
        {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                    new Warning(Tip.TIP_MICRO_OF_NONE_TEAM));

            return;
        }

        Group group = GroupServiceImpl.getInstance().getGroup(
                _leader.getGroupID());

        if (null == group)
        {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                    new Warning(Tip.TIP_MICRO_OF_NONE_TEAM));

            return;
        }

        if (_leader.getLevel() < MIN_LEVEL)
        {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                    new Warning(Tip.TIP_MICRO_OF_LESS_LEVEL));

            return;
        }

        if (group.getMemberNumber() > _type)
        {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                    new Warning(Tip.TIP_MICRO_OF_MORE_MEMBER));

            return;
        }

        short levelZoon = (short) (_leader.getLevel() / 10 * 10);

        synchronized (group.getPlayerList())
        {
            SportsTeam sportsTeam = new SportsTeam(group.getID(), _type,
                    _leader.getUserID());

            int teamPointTotal = 0;
            short memberPoint;

            for (HeroPlayer member : group.getPlayerList())
            {
                if (!member.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_MEMBER_OFFLINE));

                    return;
                }
                else if (levelZoon != _leader.getLevel() / 10 * 10)
                {
                    ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_MEMBER_DIFFICULT));

                    return;
                }

                memberPoint = getSportsPoint(member.getUserID(), _sportsClan);
                sportsTeam.addMemberInfo(member.getUserID(), memberPoint);
                teamPointTotal += memberPoint;
            }

            sportsTeam.levelZoon = levelZoon;
            sportsTeam.teamPointTotal = teamPointTotal;
            waitingQueue.add(sportsTeam);
            ChatServiceImpl.getInstance().sendGroupBottomSys(group,
                    Tip.TIP_MICRO_OF_JOIN_QUEUE);
        }
    }

    /**
     * @author DC 竞技场等待队列匹配任务
     */
    public class MatchTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
            if (waitingQueue.size() >= 2)
            {
                SportsTeam sportsTeamActive, sportsTeamPassive;

                for (int i = 0; i < waitingQueue.size(); i++)
                {
                    sportsTeamActive = waitingQueue.get(i);

                    for (int j = i + 1; j < waitingQueue.size();)
                    {
                        sportsTeamPassive = waitingQueue.get(j);

                        if (sportsTeamActive.queueType == sportsTeamPassive.queueType
                                && sportsTeamActive.levelZoon == sportsTeamPassive.levelZoon)
                        {
                            if (Math.abs(sportsTeamActive.teamPointTotal
                                    / sportsTeamActive.queueType
                                    - sportsTeamActive.teamPointTotal
                                    / sportsTeamActive.queueType) <= DIFFERENCE)
                            {
                                sportsUnitList.add(new SportsUnit(
                                        sportsTeamActive, sportsTeamPassive,
                                        null));

                                waitingQueue.remove(j);
                                waitingQueue.remove(i);

                                j++;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @author DC 竞技场监控任务
     */
    public class MonitorTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
            // 竞技场中比赛数量>0
            if (sportsUnitList.size() > 0)
            {
                SportsUnit sportsUnit;

                for (int i = 0; i < sportsUnitList.size();)
                {
                    sportsUnit = sportsUnitList.get(i);

                    // 处于准备阶段的比赛
                    if (STATUS_OF_READY == sportsUnit.status)
                    {
                        if (sportsUnit.readyKeepTime >= READY_TIME)
                        {
                            sportsUnit.start();

                            Map map = sportsUnit.site;

                            for (ME2GameObject npc : map.getNpcList())
                            {
                                if (((Npc) npc).getModelID().equalsIgnoreCase(
                                        DOOR_MODEL_ID))
                                {
                                    MapSynchronousInfoBroadcast
                                            .getInstance()
                                            .put(
                                                    map,
                                                    new DisappearNotify(npc
                                                            .getObjectType()
                                                            .value(), npc
                                                            .getID()), false, 0);
                                }
                            }

                            sportsUnitList.remove(i);
                        }
                        else
                        {
                            sportsUnit.readyKeepTime += COUNT_TIME_INVERVAL;

                            i++;
                        }
                    }// 处于战斗阶段的比赛
                    else
                    {
                        if (sportsUnit.fightKeepTime >= SPORTS_KEEP_MAX_TIME)
                        {
                            if (sportsUnit.team1.getLiveMemberNumber() > 0
                                    && sportsUnit.team2.getLiveMemberNumber() > 0)
                            {
                                if (sportsUnit.team1.getLiveMemberNumber() > sportsUnit.team2
                                        .getLiveMemberNumber())
                                {
                                    ChatServiceImpl.getInstance()
                                            .sendGroupBottomSys(
                                                    sportsUnit.team1.teamID,
                                                    Tip.TIP_MICRO_OF_WIN);
                                    ChatServiceImpl.getInstance()
                                            .sendGroupBottomSys(
                                                    sportsUnit.team2.teamID,
                                                    Tip.TIP_MICRO_OF_LOSE);
                                }
                                else if (sportsUnit.team1.getLiveMemberNumber() == sportsUnit.team2
                                        .getLiveMemberNumber())
                                {
                                    ChatServiceImpl.getInstance()
                                            .sendGroupBottomSys(
                                                    sportsUnit.team1.teamID,
                                                    Tip.TIP_MICRO_OF_END);
                                    ChatServiceImpl.getInstance()
                                            .sendGroupBottomSys(
                                                    sportsUnit.team2.teamID,
                                                    Tip.TIP_MICRO_OF_END);
                                }
                                else
                                {
                                    ChatServiceImpl.getInstance()
                                            .sendGroupBottomSys(
                                                    sportsUnit.team1.teamID,
                                                    Tip.TIP_MICRO_OF_LOSE);
                                    ChatServiceImpl.getInstance()
                                            .sendGroupBottomSys(
                                                    sportsUnit.team2.teamID,
                                                    Tip.TIP_MICRO_OF_WIN);
                                }
                            }
                            else if (sportsUnit.team1.getLiveMemberNumber() == 0)
                            {
                                ChatServiceImpl.getInstance()
                                        .sendGroupBottomSys(
                                                sportsUnit.team1.teamID,
                                                Tip.TIP_MICRO_OF_LOSE);
                                ChatServiceImpl.getInstance()
                                        .sendGroupBottomSys(
                                                sportsUnit.team2.teamID,
                                                Tip.TIP_MICRO_OF_WIN);
                            }
                            else
                            {
                                ChatServiceImpl.getInstance()
                                        .sendGroupBottomSys(
                                                sportsUnit.team1.teamID,
                                                Tip.TIP_MICRO_OF_WIN);
                                ChatServiceImpl.getInstance()
                                        .sendGroupBottomSys(
                                                sportsUnit.team2.teamID,
                                                Tip.TIP_MICRO_OF_LOSE);
                            }

                            sportsUnitList.remove(i);
                            sportsUnit.site.destroy();
                        }
                        else
                        {
                            sportsUnit.fightKeepTime += COUNT_TIME_INVERVAL;

                            i++;
                        }
                    }
                }
            }
        }
    }













    /**
     * 进入竞技场的最低等级20
     */
    private static final short  MIN_LEVEL               = 20;

    /**
     * 准备状态－－进入了竞技场，但没有开始战斗
     */
    public static final byte    STATUS_OF_READY         = 1;

    /**
     * 战斗状态－－竞技中
     */
    public static final byte    STATUS_OF_FIGHTING      = 2;

    /**
     * 等待时间
     */
    public static final long    READY_TIME              = 2 * 60 * 1000;

    /**
     * 竞技计时任务执行间隔时间
     */
    public static final long    COUNT_TIME_INVERVAL     = 15 * 1000;

    /**
     * 竞技最多持续时间
     */
    public static final long    SPORTS_KEEP_MAX_TIME    = 30 * 60 * 1000;


    /**
     * 门NPC编号
     */
    private static final String DOOR_MODEL_ID           = "n300";

    /**
     * 各势力初始积分
     */
    private static final short  INIT_POINT              = 1000;

    /**
     * 队伍匹配允许的最大积分差
     */
    private static final short  DIFFERENCE              = 500;
}
