package hero.item;

import yoyo.core.queue.ResponseMessageQueue;
import hero.item.bag.EBagType;
import hero.item.detail.EGoodsTrait;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.npc.ME2NotPlayer;
import hero.npc.Monster;
import hero.npc.Npc;
import hero.npc.message.MonsterRefreshNotify;
import hero.npc.message.NpcRefreshNotify;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.share.Direction;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.task.service.TaskServiceImpl;
import hero.ui.message.NotifyAddGoods2SinglePackage;
import hero.ui.message.ResponseSinglePackageChange;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTool.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-17 下午03:07:46
 * @描述 ：任务道具
 */

public class TaskTool extends SingleGoods
{
    /**
     * 是否为共享类型物品（用于在团队中是否大家都可以拾取）
     */
    private boolean       isShare;

    /**
     * 使用后是否消失
     */
    private boolean       disappearAfterUse;

    /**
     * 使用后获得的物品编号
     */
    private TaskTool      getGoodsAfterUse;

    /**
     * 使用的对象模型编号（NPC、怪物）
     */
    private String        targetModelID;

    /**
     * 目标剩余生命值比例
     */
    private float         targetTraceHpPercent;

    /**
     * 使用后目标是否消失
     */
    private boolean       targetDisappearAfterUse;

    /**
     * 使用后刷新出的NPC模型编号（NPC、怪物）
     */
    private String        refreshNpcModelIDAfterUse;

    /**
     * 使用后刷新出的NPC数量
     */
    private short         refreshNpcNumberAfterUse;

    /**
     * 是否显示使用地点
     */
    private boolean       isLimitLocation;

    /**
     * 使用的位置
     */
    private LocationOfUse locationOfUse;

    /**
     * 上次成功使用的时间
     */
    private long          timeOfLastUse;

    /**
     * 构造
     * 
     * @param _stackNumber
     * @param _isShare
     * @param _useable
     */
    public TaskTool(short _stackNumber, boolean _isShare, boolean _useable)
    {
        super(_stackNumber);
        // TODO Auto-generated constructor stub
        isShare = _isShare;
        useable = _useable;
        setTrait(EGoodsTrait.BING_ZHI);
    }

    /**
     * 是否是共享类型
     * 
     * @return
     */
    public boolean isShare ()
    {
        return isShare;
    }

    /**
     * 时候可使用
     * 
     * @return
     */
    public boolean useable ()
    {
        return useable;
    }

    /*
     * (non-Javadoc)
     * @see hero.item.IItemCanBeUsed#beUse(hero.player.HeroPlayer,
     * hero.share.ME2GameObject)
     */
    public boolean beUse (HeroPlayer _player, Object _target)
    {
        ME2GameObject target = (ME2GameObject) _target;

        if (canBeUse(_player, target))
        {
            try
            {
                if (null != getGoodsAfterUse)
                {
                    short[] gridChange = GoodsServiceImpl.getInstance()
                            .addGoods2Package(_player, getGoodsAfterUse, 1,
                                    CauseLog.TASKTOOL);

                    if (null != gridChange)
                    {
                        TaskServiceImpl.getInstance().addTaskGoods(_player,
                                getGoodsAfterUse.getID(), 1);

                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new NotifyAddGoods2SinglePackage(
                                        EBagType.TASK_TOOL_BAG.getTypeValue(),
                                        gridChange, getGoodsAfterUse, _player
                                                .getShortcutKeyList()));
                    }
                    else
                    {
                        return false;
                    }
                }

                if (disappearAfterUse)
                {
                    short[] gridChange = _player.getInventory()
                            .getTaskToolBag().removeOne(getID());

                    if (null != gridChange)
                    {
                        if (0 == gridChange[1])
                        {
                            GoodsDAO.removeSingleGoodsFromBag(_player
                                    .getUserID(), gridChange[0], getID());
                        }
                        else
                        {
                            GoodsDAO.updateGridSingleGoodsNumberOfBag(_player
                                    .getUserID(), getID(), gridChange[1],
                                    gridChange[0]);
                        }

                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ResponseSinglePackageChange(
                                        EBagType.TASK_TOOL_BAG.getTypeValue(),
                                        gridChange));
                    }
                    else
                    {
                        return false;
                    }
                }

                if (targetDisappearAfterUse)
                {
                    target.invalid();

                    if (_target instanceof Monster)
                    {
                        ((Monster) _target).clearFightInfo();
                        ((Monster) _target).setDieTime(System
                                .currentTimeMillis());
                        target.where().getMonsterList().remove(_target);
                    }
                    else
                    {
                        ((Npc) _target).stopFollowTask();
                        target.where().getNpcList().remove(_target);
                    }

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new DisappearNotify(target.getObjectType()
                                    .value(), target.getID(),target.getHp(),target.getBaseProperty().getHpMax(),
                                    target.getMp(),target.getBaseProperty().getMpMax()));
                }
                else
                {
                    if (null != targetModelID && _target instanceof Monster)
                    {
                        ((Monster) _target).beHarmed(_player, 0);
                    }
                }

                if (null != refreshNpcModelIDAfterUse
                        && 0 < refreshNpcNumberAfterUse)
                {
                    short x, y;

                    if (null != targetModelID)
                    {
                        x = target.getCellX();
                        y = target.getCellY();
                    }
                    else
                    {
                        if (isLimitLocation)
                        {
                            x = locationOfUse.mapX;
                            y = locationOfUse.mapY;
                        }
                        else
                        {
                            x = _player.getCellX();
                            y = _player.getCellY();
                        }
                    }

                    if (refreshNpcModelIDAfterUse
                            .startsWith(NotPlayerServiceImpl.MONSTER_MODEL_ID_PREFIX))
                    {
                        Monster monster;

                        for (int i = 1; i <= refreshNpcNumberAfterUse; i++)
                        {
                            monster = NotPlayerServiceImpl.getInstance()
                                    .buildMonsterInstance(
                                            refreshNpcModelIDAfterUse);

                            monster.setOrgMap(_player.where());
                            monster.setOrgX(x);
                            monster.setOrgY(y);
                            monster.setCellX(x);
                            monster.setCellY(y);
                            monster.setDirection(Direction.LEFT);
                            monster
                                    .setExistsTime(NotPlayerServiceImpl
                                            .getInstance().getConfig().task_call_monster_exist_time);

                            monster.live(_player.where());

                            MapSynchronousInfoBroadcast.getInstance()
                                    .put(
                                            monster.where(),
                                            new MonsterRefreshNotify(_player
                                                    .getLoginInfo().clientType,
                                                    monster), false, 0);

                            monster.where().getMonsterList().add(monster);
                            monster.active();
                        }

                        monster = null;
                    }
                    else
                    {
                        Npc npc;

                        for (int i = 1; i <= refreshNpcNumberAfterUse; i++)
                        {
                            npc = NotPlayerServiceImpl
                                    .getInstance()
                                    .buildNpcInstance(refreshNpcModelIDAfterUse);

                            npc.setOrgMap(_player.where());
                            npc.setOrgX(x);
                            npc.setOrgY(y);
                            npc.setCellX(x);
                            npc.setCellY(y);
                            npc.setDirection(Direction.LEFT);
                            npc.live(_player.where());

                            MapSynchronousInfoBroadcast.getInstance().put(
                                    npc.where(),
                                    new NpcRefreshNotify(
                                            _player.getLoginInfo().clientType,
                                            npc), false, 0);

                            npc.where().getNpcList().add(npc);
                            npc.active();
                        }

                        npc = null;
                    }

                    timeOfLastUse = System.currentTimeMillis();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * 是否可使用
     * 
     * @param _player 使用者
     * @param _target 目标
     * @return
     */
    private boolean canBeUse (HeroPlayer _player, ME2GameObject _target)
    {
        if (useable)
        {
            if (isLimitLocation)
            {
                if (_player.where().getID() != locationOfUse.mapID)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_ITEM_OF_INVALID_MAP, Warning.UI_STRING_TIP));

                    return false;
                }
                else
                {
                    if (locationOfUse.pointRange < Math.abs(locationOfUse.mapX
                            - _player.getCellX())
                            || locationOfUse.pointRange < Math
                                    .abs(locationOfUse.mapY
                                            - _player.getCellY()))
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_ITEM_OF_INVALID_POINT, Warning.UI_STRING_TIP));

                        return false;
                    }
                }
            }

            if (null != targetModelID)
            {
                if (!(_target instanceof ME2NotPlayer)
                        || !((ME2NotPlayer) _target).getModelID().equals(
                                targetModelID))
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_ITEM_OF_INVALID_TARGET, Warning.UI_STRING_TIP));

                    return false;
                }

                if (_target.getHPPercent() > targetTraceHpPercent)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_ITEM_OF_INVALID_TARGET_STATUS, Warning.UI_STRING_TIP));

                    return false;
                }
            }
            else
            {
                if (null != refreshNpcModelIDAfterUse)
                {
                    if (System.currentTimeMillis() - timeOfLastUse < INTERVAL_TIME)
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(
                                                ((INTERVAL_TIME - (System
                                                        .currentTimeMillis() - timeOfLastUse)) / 1000)
                                                        + Tip.TIP_ITEM_OF_USE_WAITING, Warning.UI_STRING_TIP));

                        return false;
                    }
                }
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    /**
     * 设置使用地点信息
     * 
     * @param _mapID 地图编号
     * @param _mapX 坐标X
     * @param _mapY 坐标Y
     * @param _pointRange 范围
     */
    public void setLocationOfUse (int _mapID, short _mapX, short _mapY,
            short _pointRange)
    {
        locationOfUse = new LocationOfUse();
        locationOfUse.mapID = _mapID;
        locationOfUse.mapX = _mapX;
        locationOfUse.mapY = _mapY;
        locationOfUse.pointRange = _pointRange;
    }

    /**
     * 限定使用地点
     */
    public void limitLocation ()
    {
        isLimitLocation = true;
    }

    /**
     * 设置在使用后消失
     */
    public void disappearAfterUse ()
    {
        disappearAfterUse = true;
    }

    /**
     * 使用后是否消失
     * 
     * @return
     */
    public boolean isDisappearAfterUse ()
    {
        return disappearAfterUse;
    }

    /**
     * 设置使用后获得物品编号
     * 
     * @param _goodsID
     */
    public void setGetGoodsAfterUse (int _goodsID)
    {
        getGoodsAfterUse = (TaskTool) GoodsContents.getGoods(_goodsID);
    }

    /**
     * 使用后获得物品编号
     * 
     * @return
     */
    public TaskTool getGoodsIDAfterUse ()
    {
        return getGoodsAfterUse;
    }

    /**
     * 设置目标NPC模板编号
     * 
     * @param _npcModelID
     */
    public void setTargetNpcModelID (String _npcModelID)
    {
        targetModelID = _npcModelID;
    }

    /**
     * 设置有效目标剩余生命值比例
     * 
     * @param _hpPercent
     */
    public void setTargetTraceHpPercent (float _hpPercent)
    {
        targetTraceHpPercent = _hpPercent;
    }

    /**
     * 获取目标NPC模板编号
     * 
     * @return
     */
    public String getTargetNpcModelID ()
    {
        return targetModelID;
    }

    /**
     * 使用后目标消失
     */
    public void targetDisappearAfterUse ()
    {
        targetDisappearAfterUse = true;
    }

    /**
     * 使用后目标是否消失
     * 
     * @return
     */
    public boolean targetIsDisappearAfterUse ()
    {
        return targetDisappearAfterUse;
    }

    /**
     * 设置使用后刷新出来的NPC模板编号
     * 
     * @param _npcModelID
     */
    public void setRefreshNpcModelIDAfterUse (String _npcModelID)
    {
        refreshNpcModelIDAfterUse = _npcModelID;
    }

    /**
     * 获取使用后刷新出来的NPC模板编号
     * 
     * @return
     */
    public String getRefreshNpcModelIDAfterUse ()
    {
        return refreshNpcModelIDAfterUse;
    }

    /**
     * 设置使用后刷新出来的NPC数量
     * 
     * @param _number
     */
    public void setRefreshNpcNumsAfterUse (short _number)
    {
        refreshNpcNumberAfterUse = _number;

        if (refreshNpcNumberAfterUse <= 0)
        {
            refreshNpcNumberAfterUse = 1;
        }
    }

    /**
     * 获取使用后刷新出来的NPC数量
     * 
     * @return
     */
    public short getRefreshNpcNumsAfterUse ()
    {
        return refreshNpcNumberAfterUse;
    }

    @Override
    public byte getSingleGoodsType ()
    {
        // TODO Auto-generated method stub
        return SingleGoods.TYPE_TASK_TOOL;
    }

    @Override
    public EGoodsType getGoodsType ()
    {
        // TODO Auto-generated method stub
        return EGoodsType.TASK_TOOL;
    }

    @Override
    public void initDescription ()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isIOGoods ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 使用限制的地点
     * 
     * @author DC
     */
    class LocationOfUse
    {
        /**
         * 限制使用的地图编号
         */
        public int   mapID;

        /**
         * 限制使用的地图X坐标
         */
        public short mapX;

        /**
         * 限制使用的地图Y坐标
         */
        public short mapY;

        /**
         * 限制使用的地图坐标误差范围
         */
        public short pointRange;
    }

    /**
     * 间隔使用时间（针对无目标且会召唤怪物出来的道具）
     */
    private static final int    INTERVAL_TIME                = 120000;


}
