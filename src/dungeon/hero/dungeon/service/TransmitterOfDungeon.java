package hero.dungeon.service;

import java.util.TimerTask;

import yoyo.core.queue.ResponseMessageQueue;

import hero.dungeon.Dungeon;
import hero.expressions.service.CEService;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.MpRefreshNotify;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.fight.service.SpecialStatusDefine;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.share.EObjectLevel;
import hero.share.EVocationType;
import hero.share.message.Warning;


import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TransmitterOfDungeon.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-16 上午09:37:42
 * @描述 ：当某种条件满足时，将玩家传送出副本，会产生一个提示等待，时间结束时将玩家传送到附件的地图，等待时间30秒
 */

public class TransmitterOfDungeon extends TimerTask
{
    /**
     * 即将要被传送出副本的玩家等待队列（玩家userID：剩余时间）
     */
    private FastList<ObjectWhillBeTransmit> waitingQueue;

    /**
     * 单例
     */
    private static TransmitterOfDungeon     instance;

    /**
     * 私有构造
     */
    private TransmitterOfDungeon()
    {
        waitingQueue = new FastList<ObjectWhillBeTransmit>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static TransmitterOfDungeon getInstance ()
    {
        if (null == instance)
        {
            instance = new TransmitterOfDungeon();
        }

        return instance;
    }

    /**
     * 添加需要传送出副本的玩家
     * 
     * @param _player
     */
    public void add (HeroPlayer _player)
    {
        if (null != _player)
        {
            Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(
                    _player.getUserID());

            if (null != dungeon)
            {
                waitingQueue.add(new ObjectWhillBeTransmit(_player, dungeon));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run ()
    {
        ObjectWhillBeTransmit info;
        HeroPlayer player;

        for (int i = 0; i < waitingQueue.size();)
        {
            info = waitingQueue.get(i);

            player = info.player;

            if (!player.isEnable())
            {
                waitingQueue.remove(i);
                DungeonServiceImpl.getInstance().playerLeftDungeon(player);

                continue;
            }
            else
            {
                if (player.getGroupID() > 0)
                {
                    waitingQueue.remove(i);

                    continue;
                }

                Map where = player.where();

                if (where.getMapType() == EMapType.GENERIC
                        || info.dungeon.getGroupID() == player.getGroupID())
                {
                    waitingQueue.remove(i);

                    continue;
                }

                info.traceTime -= CHECK_PERIOD;

                if (info.traceTime <= 0)
                {
                    waitingQueue.remove(i);

                    Map destinationMap = MapServiceImpl.getInstance()
                            .getNormalMapByID(
                                    where.getTargetMapIDAfterUseGoods());

                    //如果玩家是魔族，则使用魔族地图 --add by jiaodj 2011-05-16
                    if(player.getClan() == EClan.HE_MU_DU){
                        destinationMap = MapServiceImpl.getInstance().getNormalMapByID(where.getMozuTargetMapIDAfterUseGoods());
                    }

                    if (null != destinationMap
                            && destinationMap.getID() != where.getID())
                    {
                        player.setCellX(destinationMap.getBornX());
                        player.setCellY(destinationMap.getBornY());

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMapBottomData(player,
                                        destinationMap, where));
                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMapGameObjectList(player
                                        .getLoginInfo().clientType,
                                        destinationMap));

                        DungeonServiceImpl.getInstance().playerLeftDungeon(
                                player);

                        player.gotoMap(destinationMap);

                        if (player.isDead())
                        {
                    		int stamina = CEService.hpByStamina(
                    				CEService.playerBaseAttribute(
                    						player.getLevel(), player.getVocation().getStaminaCalPara() ), 
                    				player.getLevel(), 
                    				player.getObjectLevel().getHpCalPara());
                            player.setHp(stamina);

                            player.setMp(
                            		CEService.mpByInte(
                            				CEService.playerBaseAttribute(player.getLevel(),
                                            player.getVocation().getInteCalcPara()),
                                    player.getLevel(), EObjectLevel.NORMAL.getMpCalPara()));

                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new SpecialStatusChangeNotify(player
                                            .getObjectType().value(), player
                                            .getID(),
                                            SpecialStatusDefine.REVIVAL));
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new HpRefreshNotify(player.getObjectType()
                                            .value(), player.getID(), player
                                            .getHp(), player.getHp(), false,
                                            false));
                            MpRefreshNotify notify = new MpRefreshNotify(
                            		player.getObjectType().value(),
                                    player.getID(),
                                    player.getMp(), 
                                    false);
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), notify);

                            player.revive(null);
                        }
                    }

                    continue;
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(info.traceTime / 1000 + TRANSMIT_TIP));

                    i++;
                }
            }
        }
    }

    /**
     * 从检查队列中移除
     * 
     * @param _playerUserID
     */
    public void remove (int _playerUserID)
    {
        for (int i = 0; i < waitingQueue.size(); i++)
        {
            if (waitingQueue.get(i).player.getUserID() == _playerUserID)
            {
                waitingQueue.remove(i);

                return;
            }
        }
    }

    class ObjectWhillBeTransmit
    {
        /**
         * 玩家userID
         */
        HeroPlayer player;

        /**
         * 剩余时间（秒）
         */
        int        traceTime;

        /**
         * 所在的副本
         */
        Dungeon    dungeon;

        ObjectWhillBeTransmit(HeroPlayer _player, Dungeon _dungeon)
        {
            player = _player;
            dungeon = _dungeon;
            traceTime = MAX_TIME_OF_WAITING;
        }
    }

    /**
     * 传送提示
     */
    private static final String TRANSMIT_TIP        = "秒后被移出副本";

    /**
     * 最长等待时间
     */
    private static final int    MAX_TIME_OF_WAITING = 30 * 1000;

    /**
     * 启动延时
     */
    public static final int     START_RELAY         = 15 * 1000;

    /**
     * 检查间隔
     */
    public static final short   CHECK_PERIOD        = 5 * 1000;

}
