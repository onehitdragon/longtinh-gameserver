package hero.item.legacy;

import hero.chat.service.ChatQueue;
import hero.chat.service.ChatServiceImpl;
import hero.item.message.GoodsDistributeNotify;
import hero.item.message.LegacyBoxStatusDisappearNotify;
import hero.item.message.LegacyBoxDisappearNotify;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterLegacyManager.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-27 下午01:20:13
 * @描述 ：怪物掉落物品管理器，负责掉落物品的分配、存储
 */

public class MonsterLegacyManager
{
    private static Logger log = Logger.getLogger(MonsterLegacyManager.class);
    /**
     * 分配物品字典
     */
    private FastMap<Short, DistributeGoods> distributeGoodsTable;

    /**
     * 怪物掉落的箱子列表
     */
    private ArrayList<MonsterLegacyBox>     boxList;

    /**
     * 箱子消失的检查间隔时间
     */
    private static final int                BOX_CHECK_INTERVAL        = 30000;

    /**
     * 物品分配检查间隔时间
     */
    private static final int                GOODS_DISTRIBUTE_INTERVAL = 5000;

    /**
     * 单例
     */
    private static MonsterLegacyManager     instance;

    /**
     * 任务管理器
     */
    private Timer                           timer;

    /**
     * 私有构造
     */
    private MonsterLegacyManager()
    {
        boxList = new ArrayList<MonsterLegacyBox>();
        distributeGoodsTable = new FastMap<Short, DistributeGoods>();
    }

    /**
     * 启动监视器
     */
    public void startMonitor ()
    {
        if (null == timer)
        {
            timer = new Timer();

            timer.schedule(new BoxBecomeDueCheckTask(), BOX_CHECK_INTERVAL,
                    BOX_CHECK_INTERVAL);
            timer.schedule(new GoodsDistributeCheck(),
                    GOODS_DISTRIBUTE_INTERVAL, GOODS_DISTRIBUTE_INTERVAL);
        }
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MonsterLegacyManager getInstance ()
    {
        if (null == instance)
        {
            instance = new MonsterLegacyManager();
        }

        return instance;
    }

    /**
     * 添加掉落箱子
     * 
     * @param _box
     */
    public void addMonsterLegacyBox (MonsterLegacyBox _box)
    {
        boxList.add(_box);
    }

    /**
     * 拾取箱子
     * 
     * @param _player
     * @param _boxID
     */
    public void playerPickBox (HeroPlayer _player, int _boxID)
    {
        try
        {
            for (int i = 0; i < boxList.size(); i++)
            {
                MonsterLegacyBox box = boxList.get(i);

                if (box.getID() == _boxID)
                {
                    if (box.bePicked(_player))
                    {
                        if (box.isEmpty())
                        {
                            boxList.remove(box);
                            box.where().getLegacyBoxList().remove(box);

                            AbsResponseMessage msg = new LegacyBoxDisappearNotify(
                                    box.getID(), box.getLocationY());

                            if (MonsterLegacyBox.PICKER_TYPE_RAID == box
                                    .getPickerType())
                            {
                                RaidPickerBox raidBox = (RaidPickerBox) box;

                                for (HeroPlayer player : raidBox
                                        .getVisitorList())
                                {
                                    if (player.isEnable()
                                            && player.where() == raidBox
                                                    .where())
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(), msg);
                                }
                            }
                            else
                            {
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(), msg);
                            }
                        }
                        else
                        {
                            if (MonsterLegacyBox.PICKER_TYPE_RAID == box
                                    .getPickerType())
                            {
                                RaidPickerBox raidBox = (RaidPickerBox) box;

                                for (HeroPlayer player : raidBox
                                        .getVisitorList())
                                {
                                    if (raidBox.statusIsChanged(player
                                            .getUserID()))
                                    {
                                        ResponseMessageQueue
                                                .getInstance()
                                                .put(
                                                        player
                                                                .getMsgQueueIndex(),
                                                        new LegacyBoxStatusDisappearNotify(
                                                                box.getID(),
                                                                box
                                                                        .getLocationY()));
                                    }
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogWriter.error(null, e);
        }
    }

    /**
     * 通知客户端弹出物品分配框
     * 
     * @param _distributeGoods
     */
    public synchronized void notifyGoodsDistributeUI (
            DistributeGoods _distributeGoods)
    {
        _distributeGoods.distributeTime = System.currentTimeMillis();
        distributeGoodsTable.put(_distributeGoods.id, _distributeGoods);

        AbsResponseMessage msg = new GoodsDistributeNotify(_distributeGoods.id,
                _distributeGoods.goods, _distributeGoods.number,
                MonsterLegacyBox.KEEP_TIME_OF_DISTRIBUTE_OPERATION);

        for (HeroPlayer player : _distributeGoods.box.getVisitorList())
        {
            log.debug("通知客户端弹出物品分配框 player name = " + player.getName());
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
        }
    }

    class BoxBecomeDueCheckTask extends TimerTask
    {
        public void run ()
        {
            for (int i = 0; i < boxList.size(); i++)
            {
                MonsterLegacyBox box = boxList.get(i);

                if (box.becomeDue())
                {
                    boxList.remove(box);
                    box.where().getLegacyBoxList().remove(box);

                    if (MonsterLegacyBox.PICKER_TYPE_PERSION == box
                            .getPickerType())
                    {
                        HeroPlayer player = PlayerServiceImpl.getInstance()
                                .getPlayerByUserID(box.getPickerUserID());

                        if (null != player && player.isEnable()
                                && player.where() == box.where())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new LegacyBoxDisappearNotify(box.getID(),
                                            box.getLocationY()));
                        }
                    }
                    else
                    {
                        MapSynchronousInfoBroadcast.getInstance().put(
                                box.where(),
                                new LegacyBoxDisappearNotify(box.getID(), box
                                        .getLocationY()), false, 0);
                    }
                }
            }

        }
    }

    class GoodsDistributeCheck extends TimerTask
    {
        public void run ()
        {
            synchronized (distributeGoodsTable)
            {
                Iterator<DistributeGoods> distributeGoodsSet = distributeGoodsTable
                        .values().iterator();

                DistributeGoods distributeGoods;

                long currentTime = System.currentTimeMillis();

                while (distributeGoodsSet.hasNext())
                {
                    distributeGoods = distributeGoodsSet.next();

                    if (currentTime - distributeGoods.distributeTime >= MonsterLegacyBox.DELAY_CORRECT_TIME_OF_DISTRIBUTE_OPERATION)
                    {
                        distributeGoodsTable.remove(distributeGoods.id);
                        distributeGoods.hasOperated = true;

                        if (0 != distributeGoods.pickerUserID)
                        {
                            HeroPlayer gettor = PlayerServiceImpl.getInstance()
                                    .getPlayerByUserID(
                                            distributeGoods.pickerUserID);

                            if (null != GoodsServiceImpl.getInstance()
                                    .addGoods2Package(gettor,
                                            distributeGoods.goods,
                                            distributeGoods.number,CauseLog.DROP))
                            {
                                distributeGoods.box
                                        .removeNormalGoods(distributeGoods);

                                ChatQueue.getInstance().addGoodsMsg(
                                        gettor,
                                        "你以" + distributeGoods.maxRandom
                                                + "点赢得了",
                                        distributeGoods.goods.getName(),
                                        distributeGoods.goods.getTrait()
                                                .getViewRGB(),
                                        distributeGoods.number);

                                if (distributeGoods.box.isEmpty())
                                {
                                    distributeGoods.box.where()
                                            .getLegacyBoxList().remove(
                                                    distributeGoods.box);
                                    AbsResponseMessage msg = new LegacyBoxDisappearNotify(
                                            distributeGoods.box.getID(),
                                            distributeGoods.box.getLocationY());

                                    for (HeroPlayer player : distributeGoods.box
                                            .getVisitorList())
                                    {
                                        if (player.isEnable()
                                                && player.where() == distributeGoods.box
                                                        .where())
                                            ResponseMessageQueue.getInstance().put(
                                                    player.getMsgQueueIndex(),
                                                    msg);
                                    }
                                }
                                else
                                {
                                    for (HeroPlayer player : distributeGoods.box
                                            .getVisitorList())
                                    {
                                        if (distributeGoods.box
                                                .statusIsChanged(player
                                                        .getUserID()))
                                        {
                                            ResponseMessageQueue
                                                    .getInstance()
                                                    .put(
                                                            player
                                                                    .getMsgQueueIndex(),
                                                            new LegacyBoxStatusDisappearNotify(
                                                                    distributeGoods.box
                                                                            .getID(),
                                                                    distributeGoods.box
                                                                            .getLocationY()));
                                        }
                                    }
                                }
                            }

                            ChatServiceImpl.getInstance().sendGroupGoods(
                                    gettor.getGroupID(),
                                    gettor.getName()
                                            + distributeGoods.maxRandom
                                            + "点赢得了", distributeGoods.goods,
                                    distributeGoods.number, true,
                                    gettor.getUserID());
                        }
                        else
                        {
                            ChatServiceImpl.getInstance().sendGroupGoods(
                                    distributeGoods.box.getGroupID(),
                                    "所有人都放弃了", distributeGoods.goods,
                                    distributeGoods.number, false, 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 选择分配物品
     * 
     * @param _player
     * @param _needOrCancel 需求还是放弃
     */
    public void selectDistributeGoods (short _distributeGoodsID,
            HeroPlayer _player, boolean _needOrCancel)
    {
        DistributeGoods distributeGoods = distributeGoodsTable
                .get(_distributeGoodsID);

        if (null != distributeGoods)
        {
            if (_needOrCancel)
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new Warning("你掷出了"
                                + distributeGoods.distribute(_player
                                        .getUserID(), RANDOM.nextInt(100) + 1)
                                + "点", Warning.UI_STRING_TIP));
            }
            else
            {
                distributeGoods.distribute(_player.getUserID(), 0);
            }

            if (distributeGoods.hasOperated)
            {
                distributeGoodsTable.remove(_distributeGoodsID);

                if (0 != distributeGoods.pickerUserID)
                {
                    HeroPlayer gettor = PlayerServiceImpl.getInstance()
                            .getPlayerByUserID(distributeGoods.pickerUserID);

                    ChatQueue.getInstance().addGoodsMsg(gettor,
                            "你以" + distributeGoods.maxRandom + "点赢得了",
                            distributeGoods.goods.getName(),
                            distributeGoods.goods.getTrait().getViewRGB(),
                            distributeGoods.number);

                    if (null != GoodsServiceImpl.getInstance()
                            .addGoods2Package(gettor, distributeGoods.goods,
                                    distributeGoods.number,CauseLog.DROP))
                    {
                        distributeGoods.box.removeNormalGoods(distributeGoods);

                        if (distributeGoods.box.isEmpty())
                        {
                            distributeGoods.box.where().getLegacyBoxList()
                                    .remove(distributeGoods.box);
                            AbsResponseMessage msg = new LegacyBoxDisappearNotify(
                                    distributeGoods.box.getID(),
                                    distributeGoods.box.getLocationY());

                            for (HeroPlayer player : distributeGoods.box
                                    .getVisitorList())
                            {
                                if (player.isEnable()
                                        && player.where() == distributeGoods.box
                                                .where())
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(), msg);
                            }
                        }
                        else
                        {
                            for (HeroPlayer player : distributeGoods.box
                                    .getVisitorList())
                            {
                                if (distributeGoods.box.statusIsChanged(player
                                        .getUserID()))
                                {
                                    ResponseMessageQueue
                                            .getInstance()
                                            .put(
                                                    player.getMsgQueueIndex(),
                                                    new LegacyBoxStatusDisappearNotify(
                                                            distributeGoods.box
                                                                    .getID(),
                                                            distributeGoods.box
                                                                    .getLocationY()));
                                }
                            }
                        }
                    }

                    ChatServiceImpl.getInstance().sendGroupGoods(
                            _player.getGroupID(),
                            gettor.getName() + distributeGoods.maxRandom
                                    + "点赢得了", distributeGoods.goods,
                            distributeGoods.number, true, gettor.getID());
                }
                else
                {
                    ChatServiceImpl.getInstance().sendGroupGoods(
                            _player.getGroupID(), "所有人都放弃了",
                            distributeGoods.goods, distributeGoods.number,
                            false, 0);
                }
            }
        }
    }

    /**
     * 从监视器中移除分配物品
     * 
     * @param _distributeGoodsID
     */
    public void removeDistributeFromMonitor (short _distributeGoodsID)
    {
        distributeGoodsTable.remove(_distributeGoodsID);
    }

    private static final Random RANDOM = new Random();
}
