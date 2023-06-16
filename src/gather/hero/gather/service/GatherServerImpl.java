package hero.gather.service;

import hero.chat.service.ChatQueue;
import hero.gather.Gather;
import hero.gather.MonsterSoul;
import hero.gather.dict.Refined;
import hero.gather.dict.RefinedDict;
import hero.gather.dict.SoulInfo;
import hero.gather.dict.SoulInfoDict;
import hero.gather.message.TakeSoulMessage;
import hero.gather.message.UseGourdMessage;
import hero.item.detail.EGoodsTrait;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.Gourd;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Monster;
import hero.player.HeroPlayer;
import hero.share.message.Warning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;


/**
 * 采集服务类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class GatherServerImpl extends AbsServiceAdaptor<GatherServerConfig>
{

    private static GatherServerImpl  instance;

    private HashMap<Integer, Gather> gatherList;

    /**
     * 升级需要的金币数
     */
    public final static int[]        MONEY_OF_UPGRADE     = {
            2000, 10000, 50000, 250000                   };

    /**
     * 每个等级可获得的技能点上限
     */
    public final static int[]        POINT_LIMIT          = {
            1000, 6000, 31000, 156000, 156000            };


    /**
     * 学习一项新技能需要的金钱
     */
    public final static int          FREIFHT_OF_NEW_SKILL = 400;

    /**
     * 吸取灵魂技能ID
     */
    public final static int          SUCK_SOUL_SKILL_ID   = -50;

    private Timer                    mCheckTimer;

    /**
     * 灵魂保存时间
     */
    public static final long         GATHER_SAVE_TIME     = 5 * 60 * 1000;

    private GatherServerImpl()
    {
        config = new GatherServerConfig();
        gatherList = new HashMap<Integer, Gather>();
        mCheckTimer = new Timer();
        mCheckTimer.schedule(new GatherTimerTask(), GATHER_SAVE_TIME,
                GATHER_SAVE_TIME);
    }

    public static GatherServerImpl getInstance ()
    {
        if (instance == null)
            instance = new GatherServerImpl();
        return instance;
    }

    @Override
    protected void start ()
    {
        RefinedDict.getInstance().loadRefineds(config.gatherDataPath);
        SoulInfoDict.getInstance().loadSoulInfos(config.soulsDataPath);
    }

    @Override
    public void createSession (Session _session)
    {
        Gather _gather = GatherDAO.loadGatherByUserID(_session.userID);

        if (_gather != null)
            gatherList.put(_session.userID, _gather);
    }

    @Override
    public void sessionFree (Session _session)
    {
        Gather _gather = gatherList.remove(_session.userID);

        if (_gather != null)
        {
            GatherDAO.saveGahterByUserID(_session.userID, _gather
                    .getMonsterSoul());
        }
    }

    /**
     * 通过玩家ID得到玩家的采集技能
     * 
     * @param _userID
     * @return
     */
    public Gather getGatherByUserID (int _userID)
    {
        return gatherList.get(_userID);
    }

    /**
     * 学习采集辅助技能
     * 
     * @param _userID
     * @return 是否学习成功
     */
    public boolean studyGather (int _userID)
    {
        if (gatherList.get(_userID) != null)
            return false;
        gatherList.put(_userID, new Gather());
        GatherDAO.studyGather(_userID);
        return true;
    }

    /**
     * 得到可炼化ID列表
     * 
     * @param _userID
     * @return
     */
    public ArrayList<Integer> getCanUseManufIDs (int _userID)
    {
        Gather skill = gatherList.get(_userID);
        if (skill != null)
            return skill.getRefinedList();
        return null;
    }

    /**
     * 学习炼制技能条目
     * 
     * @param _player 玩家
     * @param _refined 技能条目
     * @param _getType 获取方式
     * @return
     */
    public boolean addRefinedItem (HeroPlayer _player, Refined _refined,
            GetTypeOfSkillItem _getType)
    {
        Gather skill = gatherList.get(_player.getUserID());

        if (null == skill)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(NEED_GATHER_SKILL));

            return false;
        }

        if (!skill.isStudyedRefinedID(_refined.id))
        {
            skill.addRefinedID(_refined.id);
            GatherDAO.addRefinedID(_player.getUserID(), _refined.id);

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(_getType.toString() + _refined.name));

            return true;
        }
        else
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(STUDYED_MANUF_SKILL));

            return false;
        }
    }

    /**
     * 遗忘辅助技能
     * 
     * @param _userID
     */
    public void forgetGatherByUserID (int _userID)
    {
        Gather skill = gatherList.remove(_userID);
        if (skill != null)
        {
            GatherDAO.forgetGatherByUserID(_userID);
        }
    }

    /**
     * 得到收集的灵魂列表
     * 
     * @param _userID
     * @return
     */
    public ArrayList<MonsterSoul> getMonsterSouls (int _userID)
    {
        Gather skill = gatherList.get(_userID);
        if (skill != null)
            return skill.getMonsterSoul();
        return null;
    }

    /**
     * 战斗系统怪物死亡并且灵魂吸收者ID不为零的时候调用
     * 
     * @param _monster
     */
    public boolean processSoulWhenMonsterDied (Monster _monster)
    {
        if (_monster.getTakeSoulUserID() != 0)
        {
            int soulID = _monster.getSoulID();

            if (soulID != 0)
            {
                HeroPlayer player = _monster.where().getPlayer(
                        _monster.getTakeSoulUserID());
                int gourdID = getGourdID(player);

                if (gourdID > 0
                        && canTakeSoul(player, _monster.getAttackerAtFirst()))
                {
                    Gourd gourd = (Gourd) GoodsServiceImpl.getInstance()
                            .getGoodsByID(gourdID);
                    Gather skill = getGatherByUserID(player.getUserID());

                    if (skill != null)
                    {
                        if (skill.addMosnterSoul(soulID, gourd))
                        {
                            AbsResponseMessage message = new TakeSoulMessage(
                                    _monster.getID(), _monster
                                            .getTakeSoulUserID());

                            SoulInfo soulInfo = SoulInfoDict.getInstance()
                                    .getSoulInfoByID(soulID);

                            ChatQueue.getInstance().addGoodsMsg(player,
                                    TIP_OF_GET_HEADER, soulInfo.soulName,
                                    EGoodsTrait.SHI_QI.getViewRGB(), 1);

                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(), message);

                            MapSynchronousInfoBroadcast.getInstance().put(
                                    _monster.where(), message, true,
                                    player.getID());

                            return true;
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(TIP_GOURD_IS_FULL));
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 玩家对怪物使用葫芦
     * 
     * @param _player
     * @param _monster
     */
    public void useGourd (HeroPlayer _player, Monster _monster)
    {
        if (_monster.getTakeSoulUserID() == 0 && _monster.getSoulID() > 0)
        {
            _monster.beHarmed(_player, 0);
            _monster.setTakeSoulUserID(_player.getID());
            MapSynchronousInfoBroadcast.getInstance().put(_monster.where(),
                    new UseGourdMessage(_monster.getID()), false, 0);
        }
    }

    private boolean canTakeSoul (HeroPlayer _player, HeroPlayer _fristPlayer)
    {
        if (_player == null)
        {
            return false;
        }
        if (_fristPlayer == null)
        {
            return false;
        }
        if (!_player.isEnable())
        {
            return false;
        }
        if (_player.isDead())
        {
            return false;
        }
        if (_player.getUserID() == _fristPlayer.getUserID())
        {
            return true;
        }
        if (_fristPlayer.getGroupID() != 0
                && _player.getGroupID() == _fristPlayer.getGroupID())
        {
            return true;
        }
        return false;
    }

    public int getGourdID (HeroPlayer _player)
    {
        if (null != _player)
        {
            int[][] items = _player.getInventory().getSpecialGoodsBag()
                    .getAllItem();
            for (int i = 0; i < items.length; i++)
            {
                if (items[i][0] >= 50001 && items[i][0] <= 50005)
                    return items[i][0];
            }
        }

        return 0;
    }

    /**
     * 采集技能升级
     * 
     * @param _userID
     * @param _gather
     */
    public void lvlUp (int _userID, Gather _gather)
    {
        if (_gather.lvlUp())
        {
            GatherDAO.updateGather(_userID, _gather);
        }
    }

    /**
     * 添加采集技能点
     * 
     * @param _userID
     * @param _gather
     * @param _addPoint
     */
    public void addPoint (int _userID, Gather _gather, int _addPoint)
    {
        if (_gather.addPoint(_addPoint))
        {
            GatherDAO.updateGather(_userID, _gather);
        }
    }

    private void saveSouls ()
    {
        synchronized (this)
        {
            Iterator<Integer> iter = gatherList.keySet().iterator();

            while (iter.hasNext())
            {
                int _userID = iter.next();
                Gather _gather = gatherList.get(_userID);

                if (_gather.isSave())
                {
                    GatherDAO.saveGahterByUserID(_userID, _gather
                            .getMonsterSoul());
                    _gather.setSave(false);
                }
            }
        }
    }

    class GatherTimerTask extends TimerTask
    {

        @Override
        public void run ()
        {
            saveSouls();
        }

    }

    private static final String TIP_OF_GET_HEADER   = "获得了";

    private static final String TIP_GOURD_IS_FULL   = "葫芦已满";

    private static final String NEED_GATHER_SKILL   = "需要 炼化师";

    private static final String STUDYED_MANUF_SKILL = "已会的技能";

}
