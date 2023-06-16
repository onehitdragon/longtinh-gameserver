package hero.duel.service;

import hero.duel.Duel;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.duel.message.ResponseDuel;
import hero.effect.service.EffectServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.skill.service.SkillConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DuelServiceImpl.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2008-12-30 上午11:41:36
 * @描述 ：同阵营决斗服务类
 */

public class DuelServiceImpl extends AbsServiceAdaptor<PvpServerConfig>
{
    /**
     * 单例
     */
    private static DuelServiceImpl instance;

    /**
     * 决斗管理列表
     */
    private List<Duel>             duelList;

    /**
     * 同步锁
     */
    private ReentrantLock          lock = new ReentrantLock();

    /**
     * 超时检查计时器
     */
    private Timer                  timeOutChecker;

    /**
     * 私有构造
     */
    private DuelServiceImpl()
    {
        duelList = new ArrayList<Duel>();
        timeOutChecker = new Timer();
        timeOutChecker.schedule(new TimeOutCheckTask(), DELAY_OF_CHECK,
                INTERVAL_OF_CHECK);
        config = new PvpServerConfig();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static DuelServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new DuelServiceImpl();
        }

        return instance;
    }

    /**
     * 指定userID的玩家是否在PK中
     * 
     * @param _userID
     * @return
     */
    public boolean isDueling (int _userID)
    {
        return getDuelByOneSide(_userID) == null ? false : true;
    }

    /**
     * 2个玩家是否在相互决斗
     * 
     * @param _p1UserID 一方userID
     * @param _p2UserID 另一方userID
     * @return
     */
    public boolean isDueling (int _p1UserID, int _p2UserID)
    {
        try
        {
            lock.lock();

            for (Duel d : duelList)
            {
                if (d.player1UserID == _p1UserID
                        && d.player2UserID == _p2UserID)
                    return true;
                if (d.player1UserID == _p2UserID
                        && d.player2UserID == _p1UserID)
                    return true;
            }
        }
        finally
        {
            lock.unlock();
        }

        return false;
    }

    /**
     * 根据玩家的userID获取决斗状态管理对象
     * 
     * @param _userID
     * @return
     */
    public Duel removeByOneSide (int _userID)
    {
        try
        {
            lock.lock();

            Duel d;

            for (int i = 0; i < duelList.size(); i++)
            {
                d = duelList.get(i);

                if (d.player1UserID == _userID || d.player2UserID == _userID)
                {
                    duelList.remove(i);

                    return d;
                }
            }
        }
        finally
        {
            lock.unlock();
        }

        return null;
    }

    /**
     * 根据玩家的userID获取决斗状态管理对象
     * 
     * @param _userID
     * @return
     */
    public Duel getDuelByOneSide (int _userID)
    {
        try
        {
            lock.lock();

            for (Duel d : duelList)
            {
                if (d.player1UserID == _userID || d.player2UserID == _userID)
                {
                    return d;
                }
            }
        }
        finally
        {
            lock.unlock();
        }

        return null;
    }

    /**
     * 邀请决斗
     * 
     * @param _player1
     * @param _player2
     * @param _duelMapID
     */
    public boolean inviteDuel (HeroPlayer _player1, HeroPlayer _player2)
    {
        if (_player1.where() == _player2.where())
        {
            duelList.add(new Duel(_player1.getUserID(), _player2.getUserID(),
                    _player1.where().getID()));

            return true;
        }

        return false;
    }

    /**
     * 赢得决斗（当一方生命值为0时）
     * 
     * @param _winner 赢家
     */
    public void wonDuel (HeroPlayer _winner)
    {
        Duel dule = removeByOneSide(_winner.getUserID());

        if (dule != null)
        {
            ResponseMessageQueue.getInstance().put(_winner.getMsgQueueIndex(),
                    new ResponseDuel(ResponseDuel.END_TYPE_OF_GENERIC));
            _winner.endDuel();
            _winner
                    .removePvpTarget(dule.player1UserID == _winner.getUserID() ? dule.player2UserID
                            : dule.player1UserID);

            HeroPlayer failer = PlayerServiceImpl
                    .getInstance()
                    .getPlayerByUserID(
                            dule.player1UserID == _winner.getUserID() ? dule.player2UserID
                                    : dule.player1UserID);

            if (null != failer)
            {
                EffectServiceImpl.getInstance().removeDuelEffect(failer,
                        _winner);

                ResponseMessageQueue.getInstance().put(failer.getMsgQueueIndex(),
                        new ResponseDuel(ResponseDuel.END_TYPE_OF_GENERIC));
                failer.endDuel();
                failer.removePvpTarget(_winner.getUserID());

                MapSynchronousInfoBroadcast.getInstance().put(
                        _winner.where(),
                        new Warning(_winner.getName() + Tip.TIP_DUEL_OF_WIN
                                + failer.getName()), false, 0);
            }else {
                failer = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(
                        dule.player1UserID == _winner.getUserID() ? dule.player2UserID
                                    : dule.player1UserID);
            }


        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        Duel dule = removeByOneSide(_session.userID);

        if (dule != null)
        {
            HeroPlayer another = PlayerServiceImpl
                    .getInstance()
                    .getPlayerByUserID(
                            dule.player1UserID == _session.userID ? dule.player2UserID
                                    : dule.player1UserID);

            if (dule.isConfirming)
            {
                ResponseMessageQueue.getInstance().put(another.getMsgQueueIndex(),
                        new Warning(Tip.TIP_DUEL_OF_REFUSE));
            }
            else
            {
                ResponseMessageQueue.getInstance().put(another.getMsgQueueIndex(),
                        new ResponseDuel(ResponseDuel.END_TYPE_OF_OFFLINE));
                another.endDuel();
                another.removePvpTarget(_session.userID);
            }
        }
    }

    /**
     * 决斗超时检查逻辑
     */
    private void check ()
    {
        long nowTime = System.currentTimeMillis();

        Duel duel;

        for (int i = 0; i < duelList.size();)
        {
            duel = duelList.get(i);

            if (nowTime - duel.startTime >= DUEL_KEEP_TIME)
            {
                duelList.remove(i);

                HeroPlayer oneSide = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(duel.player1UserID);

                AbsResponseMessage msg = new ResponseDuel(
                        ResponseDuel.END_TYPE_OF_TIMEOUT);

                if (null != oneSide)
                {
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);

                    if (oneSide.isEnable())
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(),
                                msg);
                }

                HeroPlayer anotherSide = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(duel.player2UserID);

                if (null != anotherSide)
                {
                    anotherSide.endDuel();
                    anotherSide.removePvpTarget(duel.player1UserID);

                    if (anotherSide.isEnable())
                        ResponseMessageQueue.getInstance().put(
                                anotherSide.getMsgQueueIndex(), msg);
                }

                continue;
            }
            else
            {
                HeroPlayer oneSide = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(duel.player1UserID);

                HeroPlayer anotherSide = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(duel.player2UserID);

                if (null == oneSide)
                {
                    AbsResponseMessage msg = new ResponseDuel(
                            ResponseDuel.END_TYPE_OF_OFFLINE);

                    if (null != anotherSide)
                    {
                        anotherSide.endDuel();
                        anotherSide.removePvpTarget(duel.player1UserID);

                        if (anotherSide.isEnable())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    anotherSide.getMsgQueueIndex(), msg);
                        }
                    }

                    duelList.remove(i);

                    continue;
                }
                else if (!oneSide.isEnable())
                {
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);

                    AbsResponseMessage msg = new ResponseDuel(
                            ResponseDuel.END_TYPE_OF_OFFLINE);

                    if (null != anotherSide)
                    {
                        anotherSide.endDuel();
                        anotherSide.removePvpTarget(duel.player1UserID);

                        if (anotherSide.isEnable())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    anotherSide.getMsgQueueIndex(), msg);
                        }
                    }

                    duelList.remove(i);

                    continue;
                }

                if (null == anotherSide)
                {
                    AbsResponseMessage msg = new ResponseDuel(
                            ResponseDuel.END_TYPE_OF_OFFLINE);

                    if (null != oneSide)
                    {
                        oneSide.endDuel();
                        oneSide.removePvpTarget(duel.player2UserID);

                        if (oneSide.isEnable())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    oneSide.getMsgQueueIndex(), msg);
                        }
                    }

                    duelList.remove(i);

                    continue;
                }
                else if (!anotherSide.isEnable())
                {
                    anotherSide.endDuel();
                    anotherSide.removePvpTarget(duel.player1UserID);

                    AbsResponseMessage msg = new ResponseDuel(
                            ResponseDuel.END_TYPE_OF_OFFLINE);

                    if (null != oneSide)
                    {
                        oneSide.endDuel();
                        oneSide.removePvpTarget(duel.player2UserID);

                        if (oneSide.isEnable())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    oneSide.getMsgQueueIndex(), msg);
                        }
                    }

                    duelList.remove(i);

                    continue;
                }

                if (oneSide.where().getID() != duel.duleMapID)
                {
                    AbsResponseMessage msg = new ResponseDuel(
                            ResponseDuel.END_TYPE_OF_GENERIC);

                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    anotherSide.endDuel();
                    anotherSide.removePvpTarget(duel.player1UserID);

                    if (oneSide.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(),
                                msg);
                    }

                    if (anotherSide.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(
                                anotherSide.getMsgQueueIndex(), msg);
                    }

                    duelList.remove(i);

                    continue;
                }

                if (anotherSide.where().getID() != duel.duleMapID)
                {
                    AbsResponseMessage msg = new ResponseDuel(
                            ResponseDuel.END_TYPE_OF_GENERIC);

                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    anotherSide.endDuel();
                    anotherSide.removePvpTarget(duel.player1UserID);

                    if (oneSide.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(),
                                msg);
                    }

                    if (anotherSide.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(
                                anotherSide.getMsgQueueIndex(), msg);
                    }

                    duelList.remove(i);

                    continue;
                }

                i++;
            }
        }
    }

    /**
     * @author DC 超时检查任务
     */
    class TimeOutCheckTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
            try
            {
                check();
            }
            catch (Exception e)
            {

            }
        }
    }





    /**
     * 决斗状态持续时间
     */
    public static final long    DUEL_KEEP_TIME    = 5 * 60 * 1000;

    /**
     * 双方状态、因素等检查间隔时间
     */
    public static final long    INTERVAL_OF_CHECK = 10 * 1000;

    /**
     * 检查任务启动延时
     */
    public static final long    DELAY_OF_CHECK    = 30 * 1000;
}
