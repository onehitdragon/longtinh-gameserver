package hero.dungeon.service;

import hero.dungeon.Dungeon;
import hero.dungeon.DungeonDataModel;
import hero.dungeon.DungeonHistory;
import hero.effect.service.EffectServiceImpl;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.MapModelData;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.detail.Door;
import hero.map.message.DisappearNotify;
import hero.map.message.DoorOpenedNotify;
import hero.map.message.PlayerRefreshNotify;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.SwitchMapFailNotify;
import hero.map.service.MapServiceImpl;
import hero.npc.Monster;
import hero.npc.detail.EMonsterLevel;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.GlobalTimer;
import hero.share.service.LogWriter;
import hero.share.service.Tip;

import java.util.ArrayList;
import java.util.TimerTask;

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
 * @文件 DungeonServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-26 下午01:39:26
 * @描述 ：副本服务
 */

public class DungeonServiceImpl extends AbsServiceAdaptor<DungeonConfig>
{
    private static Logger log = Logger.getLogger(DungeonServiceImpl.class);
    /**
     * 玩家副本进度表（userID：进度编号列表）
     */
    private FastMap<Integer, ArrayList<DungeonHistory>> playerDungeonHistoryTable;

    /**
     * 玩家所在副本表（userID：副本）
     */
    private FastMap<Integer, Dungeon>                   playerDungeonTable;

    /**
     * 单例
     */
    private static DungeonServiceImpl                   instance;

    /**
     * 私有构造
     */
    private DungeonServiceImpl()
    {
        config = new DungeonConfig();
        playerDungeonHistoryTable = new FastMap<Integer, ArrayList<DungeonHistory>>();
        playerDungeonTable = new FastMap<Integer, Dungeon>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static DungeonServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new DungeonServiceImpl();
        }

        return instance;
    }

    @Override
    protected void start ()
    {
        DungeonDataModelDictionary.getInsatnce().loadDungeonModelData(
                config.dungeon_data_path);
        DungeonHistoryManager.getInstance().init();
        DungeonInstanceManager.getInstance();
        GlobalTimer.getInstance().registe(TransmitterOfDungeon.getInstance(),
                TransmitterOfDungeon.START_RELAY,
                TransmitterOfDungeon.CHECK_PERIOD);
        GlobalTimer.getInstance().registe(DungeonInstanceManager.getInstance(),
                DungeonInstanceManager.START_RELAY,
                DungeonInstanceManager.CHECK_PERIOD);
    }

    @Override
    public void createSession (Session _session)
    {
        ArrayList<DungeonHistory> historyList = DungeonHistoryManager
                .getInstance().getPlayerHistoryList(_session.userID);

        if (null != historyList)
        {
            playerDungeonHistoryTable.put(_session.userID, historyList);
        }
        else
        {
            playerDungeonHistoryTable.put(_session.userID,
                    new ArrayList<DungeonHistory>());
        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        
    }

    public void clean (int _userID)
    {
        playerDungeonHistoryTable.remove(_userID);
        Dungeon dungeon = playerDungeonTable.remove(_userID);

        if (null != dungeon)
        {
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(_userID);

            if (null != player
                    && player.where().getMapType() == EMapType.DUNGEON
                    && player.getGroupID() > 0)
            {
                dungeon.playerLeft(player);
            }
        }
    }

    /**
     * 玩家进入副本
     * 
     * @param _userID
     * @param _dungeon
     */
    public void playerEnterDungeon (int _userID, Dungeon _dungeon)
    {
        playerDungeonTable.put(_userID, _dungeon);
        _dungeon.playerComeIn(_userID);
    }

    /**
    /**
     * 结婚双方进入礼堂副本
     * 第一个进去，创建副本,另一个则进入第一个创建好的副本
     * @param firster  第一个进入的玩家
     * @param seconder   第二个进入的玩家
     * @param marryMapID
     */
    public void marryerGotoMarryDungeon(HeroPlayer firster, HeroPlayer seconder, short marryMapID){
        Dungeon dungeon = getWhereDungeon(firster.getUserID());
        log.debug("marryerGotoMarryDungeon = " + dungeon);
        if(dungeon == null){
            log.debug("第一个进去");
            gotoMarryDungeonMap(0,firster,marryMapID);
        }else{
            gotoMarryDungeonMap(dungeon.getHistoryID(),seconder,marryMapID);
        }
    }
    
    /**
     * 创建婚礼副本地图
     * 该副本里只有一张地图
     * @param _dugeonDataModel
     * @param _marryer 结婚中的男方 userID
     * @return
     */
    private Dungeon buildMarryDungeon(DungeonDataModel _dungeonDataModel){
        Dungeon dungeon = new Dungeon(_dungeonDataModel);
        Map map;

        for (MapModelData mapModeldata : _dungeonDataModel.mapModelList)
        {
            map = MapServiceImpl.getInstance().buildDungeonMap(Dungeon.PATTERN_OF_EASY,
                    mapModeldata, _dungeonDataModel.name, false);

            dungeon.initInternalMap(map);

            if (mapModeldata.id == _dungeonDataModel.entranceMapID)
            {
                dungeon.setEntranceMap(map);
            }
        }

        DungeonInstanceManager.getInstance().add(dungeon);

        return dungeon;
    }

    /**
     * 进入婚礼礼堂副本地图
     * 无人员数量限制，无等级限制，无怪物
     * @param _inviterID 结婚一方的userID
     * @param player
     * @param mapID
     * @param targetX
     * @param targetY
     */
    public boolean gotoMarryDungeonMap(int _dungeonID, HeroPlayer _player, short mapID){
        DungeonDataModel dungeonData = DungeonDataModelDictionary.getInsatnce()
                .getDungeonDataModelByMapid(mapID);
        log.debug("进入婚礼礼堂副本地图 gotoMarryDungeonMap = " + dungeonData);
        if (null != dungeonData)
        {
            Dungeon dungeon = DungeonInstanceManager.getInstance().getMarryDungeon(dungeonData.id,_dungeonID);
            log.debug("goto marry dungeon = " + dungeon);
            if(null == dungeon){
                dungeon = buildMarryDungeon(dungeonData);
            }

            if (null != dungeon)
            {
                if(dungeon.getPlayerNumber() < Dungeon.MEMBER_NUMBER_OF_MARRY_MAP){
                    Map entranceMap = dungeon.getEntranceMap();

                    _player.setCellX(entranceMap.getBornX());
                    _player.setCellY(entranceMap.getBornY());

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ResponseMapBottomData(_player, entranceMap, _player.where()));

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ResponseMapGameObjectList(_player
                                    .getLoginInfo().clientType, entranceMap));
                    //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                    EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);

                    _player.where().getPlayerList().remove(_player);
                    if (_player.where() != entranceMap)
                    {
                        MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
                                new DisappearNotify(_player.getObjectType().value(), _player.getID(),
                                        _player.getHp(),_player.getBaseProperty().getHpMax(),
                                        _player.getMp(),_player.getBaseProperty().getMpMax()),
                                false, 0);

                    }
                    _player.live(entranceMap);
                    entranceMap.getPlayerList().add(_player);
                    MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
                        new PlayerRefreshNotify(_player), true, _player.getID());
                    _player.needUpdateDB = true;

                    playerEntryMarryDungeon(_player.getUserID(), dungeon);

                    return true;
                }else{
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                    		new Warning(Tip.TIP_DUNGEON_OF_MAP_FULL));
                    return false;
                }
            }

        }
        else
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_DUNGEON_OF_WEDDING_MAP_WRONG));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new SwitchMapFailNotify(Tip.TIP_DUNGEON_OF_WEDDING_MAP_WRONG));
        }
        return false;
    }
    
    /**
     * 玩家进入婚礼副本地图
     * @param _userID
     * @param _dungeon
     */
    public void playerEntryMarryDungeon(int _userID, Dungeon _dungeon){
        playerDungeonTable.put(_userID, _dungeon);
        _dungeon.playerEntryMarryDungeon(_userID);
    }
    /**
     * 玩家离开副本
     * 
     * @param _userID
     * @param _dungeon
     */
    public void playerLeftDungeon (HeroPlayer _player)
    {
        Dungeon dungeon = playerDungeonTable.remove(_player.getUserID());

        if (null != dungeon)
        {
            dungeon.playerLeft(_player);
        }
    }

    /**
     * 根据玩家编号获取所在副本
     * 
     * @param _userID
     * @return
     */
    public Dungeon getWhereDungeon (int _userID)
    {
        return playerDungeonTable.get(_userID);
    }

    /**
     * 处理副本怪物死亡
     * 
     * @param _monster
     * @param _dungeon
     */
    public void processMonsterDie (Monster _monster, Dungeon _dungeon)
    {
        try
        {
            if (null != _dungeon)
            {
                if (EMonsterLevel.NORMAL == _monster.getMonsterLevel())
                {
                    if (null != _monster.where().getMonsterModelIDAbout())
                    {
                        if (_dungeon.bossIsDead(_monster.where()
                                .getMonsterModelIDAbout()))
                        {
                            _monster.destroy();
                        }
                    }
                    else
                    {
                        _monster.destroy();
                    }
                }
                else
                {
                    _dungeon.addDeathBoss(_monster.getModelID());

                    for (Door door : _monster.where().doorList)
                    {
                        if (null != door.monsterIDAbout
                                && -1 != _monster.getModelID().indexOf(
                                        door.monsterIDAbout))
                        {
                            door.visible = true;
                            DoorOpenedNotify msg = new DoorOpenedNotify(
                                    _monster.where().getID(), door.targetMapID);

                            MapSynchronousInfoBroadcast.getInstance().put(
                                    _monster.where(), msg, false, 0);
                        }
                    }

                    if (_dungeon.needSaveHistory())
                    {
                        DungeonHistory history = DungeonHistoryManager
                                .getInstance().getHistory(
                                        _dungeon.getHistoryID());

                        if (null != history)
                        {
                            history.addDeathBoss(_monster.getModelID(),
                                    _monster.where().getID());

                            DungeonDAO.changeDungeonHistoryContent(history);
                        }
                        else
                        {
                            history = new DungeonHistory(_dungeon, _monster
                                    .where().getID(), _monster.getModelID());

                            ArrayList<HeroPlayer> playerList = _monster
                                    .getHatredTargetList();

                            AbsResponseMessage msg = new Warning(_dungeon.getName()
                                    + TIP_OF_HISTORY_HAPPEND);

                            for (HeroPlayer player : playerList)
                            {
                                history.addPlayer(player.getUserID());
                                playerDungeonHistoryTable.get(
                                        player.getUserID()).add(history);
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(), msg);
                            }

                            DungeonHistoryManager.getInstance()
                                    .addDungeonHistory(history);

                            DungeonDAO.buildDungeonHistory(history);
                        }
                    }

                    _monster.destroy();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取玩家的副本进度列表
     * 
     * @param _userID
     * @return
     */
    public ArrayList<DungeonHistory> getHistoryList (int _userID)
    {
        return playerDungeonHistoryTable.get(_userID);
    }

    /**
     * 进入副本前的逻辑
     * 
     * @param _dungeonID 副本编号
     * @param _leaderUserID 队长角色编号
     * @return 副本入口地图
     */
    public Dungeon tryEnterDungeon (HeroPlayer _player,
            DungeonDataModel _dungeonData, byte _pattern, int _groupID,
            int _leaderUserID)
    {
        try
        {
            Dungeon dungeon;

            if (Dungeon.PATTERN_OF_DIFFICULT == _pattern)
            {
                dungeon = DungeonInstanceManager.getInstance()
                        .getNoneHistoryDungeon(_groupID, _dungeonData.id);

                if (null != dungeon && dungeon.getPlayerNumber() > 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(TIP_OF_MEMBER_IN_EASY_PATTERN));

                    return null;
                }
            }
            else
            {
                dungeon = DungeonInstanceManager.getInstance()
                        .getHistoryDungeon(_groupID, _dungeonData.id,
                                Dungeon.PATTERN_OF_DIFFICULT);

                if (null != dungeon && dungeon.getPlayerNumber() > 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(TIP_OF_MEMBER_IN_DIFFICULT_PATTERN));

                    return null;
                }
            }

            if (Dungeon.PATTERN_OF_DIFFICULT == _pattern
                    || Dungeon.MEMBER_NUMBER_OF_RAID == _dungeonData.playerNumberLimit)
            {
                DungeonHistory dungeonHistory = getDungeonHistory(_player
                        .getUserID(), _dungeonData.id, _pattern);

                if (null != dungeonHistory)
                {
                    if (dungeonHistory.containsPlayer(_leaderUserID))
                    {
                        dungeon = DungeonInstanceManager.getInstance()
                                .getHistoryDungeon(dungeonHistory.getID());

                        if (null != dungeon)
                        {
                            if (dungeon.getGroupID() == _groupID)
                            {
                                if (dungeon.getPlayerNumber() >= dungeon
                                        .getPlayerNumberLimit())
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning(TIP_OF_MEMBER_FULL));

                                    return null;
                                }
                                else
                                {
                                    return dungeon;
                                }
                            }
                            else
                            {
                                if (dungeon.getPlayerNumber() == 0)
                                {
                                    dungeon.setGroupID(_groupID);

                                    return dungeon;
                                }
                                else
                                {
                                    ResponseMessageQueue
                                            .getInstance()
                                            .put(
                                                    _player.getMsgQueueIndex(),
                                                    new Warning(
                                                            TIP_OF_OTHER_GROUP_USEING_HISTORY));

                                    return null;
                                }
                            }
                        }
                        else
                        {
                            return buildDungeonByHistory(dungeonHistory,
                                    _groupID);
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(TIP_OF_DIFFERENT_HISTORY));

                        return null;
                    }
                }
                else
                {
                    dungeonHistory = getDungeonHistory(_leaderUserID,
                            _dungeonData.id, _pattern);

                    if (null != dungeonHistory)
                    {
                        dungeon = DungeonInstanceManager.getInstance()
                                .getHistoryDungeon(dungeonHistory.getID());

                        if (null != dungeon)
                        {
                            if (dungeon.getGroupID() == _groupID)
                            {
                                if (dungeon.getPlayerNumber() >= dungeon
                                        .getPlayerNumberLimit())
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning(TIP_OF_MEMBER_FULL));

                                    return null;
                                }
                                else
                                {
                                    if (dungeonHistory.addPlayer(_player
                                            .getUserID()))
                                    {
                                        DungeonDAO
                                                .changeDungeonHistoryContent(dungeonHistory);
                                    }

                                    return dungeon;
                                }
                            }
                            else
                            {
                                if (dungeon.getPlayerNumber() == 0)
                                {
                                    dungeon.setGroupID(_groupID);

                                    return dungeon;
                                }
                                else
                                {
                                    ResponseMessageQueue
                                            .getInstance()
                                            .put(
                                                    _player.getMsgQueueIndex(),
                                                    new Warning(
                                                            TIP_OF_OTHER_GROUP_USEING_HISTORY));

                                    return null;
                                }
                            }
                        }
                        else
                        {
                            dungeon = buildDungeonByHistory(dungeonHistory,
                                    _groupID);

                            if (dungeonHistory.addPlayer(_player.getUserID()))
                            {
                                DungeonDAO
                                        .changeDungeonHistoryContent(dungeonHistory);
                            }

                            return dungeon;
                        }
                    }
                    else
                    {
                        dungeon = DungeonInstanceManager.getInstance()
                                .getHistoryDungeon(_groupID, _dungeonData.id,
                                        _pattern);

                        if (null != dungeon)
                        {
                            if (dungeon.getPlayerNumber() >= dungeon
                                    .getPlayerNumberLimit())
                            {
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(TIP_OF_MEMBER_FULL));

                                return null;
                            }
                            else
                            {
                                dungeonHistory = DungeonHistoryManager
                                        .getInstance().getHistory(
                                                dungeon.getHistoryID());

                                if (null != dungeonHistory
                                        && dungeonHistory.addPlayer(_player
                                                .getUserID()))
                                {
                                    DungeonDAO
                                            .changeDungeonHistoryContent(dungeonHistory);
                                }

                                return dungeon;
                            }
                        }
                        else
                        {
                            return buildNewDungeon(_dungeonData, _pattern,
                                    _groupID);
                        }
                    }
                }
            }
            else
            {
                dungeon = DungeonInstanceManager.getInstance()
                        .getNoneHistoryDungeon(_groupID, _dungeonData.id);

                if (null != dungeon)
                {
                    if (dungeon.getPlayerNumber() >= dungeon
                            .getPlayerNumberLimit())
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(TIP_OF_MEMBER_FULL));

                        return null;
                    }
                    else
                    {
                        return dungeon;
                    }
                }
                else
                {
                    return buildNewDungeon(_dungeonData, _pattern, _groupID);
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }

        return null;
    }

    /**
     * 根据玩家编号、副本编号、难度模式获取副本进度
     * 
     * @param _playerUserID
     * @param _dungeonID
     * @param _pattern
     * @return
     */
    private DungeonHistory getDungeonHistory (int _playerUserID,
            int _dungeonID, byte _pattern)
    {
        ArrayList<DungeonHistory> dungeonHistoryList = playerDungeonHistoryTable
                .get(_playerUserID);

        if (null != dungeonHistoryList)
        {
            for (DungeonHistory history : dungeonHistoryList)
            {
                if (history.getDungeonID() == _dungeonID
                        && history.getPattern() == _pattern)
                {
                    return history;
                }
            }
        }

        return null;
    }

    /**
     * 根据进度创建副本实例
     * 
     * @param _history 进度
     * @param _groupID 队伍编号
     * @return
     */
    private Dungeon buildDungeonByHistory (DungeonHistory _history, int _groupID)
    {
        DungeonDataModel dungeonDataModel = DungeonDataModelDictionary
                .getInsatnce().get(_history.getDungeonID());

        Dungeon dungeon = new Dungeon(_history, dungeonDataModel, _groupID);

        Map map;

        for (MapModelData mapModeldata : dungeonDataModel.mapModelList)
        {
            boolean monsterAboutExists = true;

            Short monsterAboutWhereID;

            if (_history.getPattern() == Dungeon.PATTERN_OF_DIFFICULT)
            {
                monsterAboutWhereID = _history.getDeathBossTable().get(
                        mapModeldata.monsterModelIDAbout
                                + Dungeon.SUFFIX_OF_HERO_MONSTER_ID);
            }
            else
            {
                monsterAboutWhereID = _history.getDeathBossTable().get(
                        mapModeldata.monsterModelIDAbout);
            }

            if (null != monsterAboutWhereID
                    && monsterAboutWhereID == mapModeldata.id)
            {
                monsterAboutExists = false;
            }

            map = MapServiceImpl.getInstance().buildDungeonMap(
                    _history.getPattern(), mapModeldata, dungeonDataModel.name,
                    monsterAboutExists);

            dungeon.initInternalMap(map);

            if (mapModeldata.id == dungeonDataModel.entranceMapID)
            {
                dungeon.setEntranceMap(map);
            }
        }

        DungeonInstanceManager.getInstance().add(dungeon);

        return dungeon;
    }

    /**
     * 创建新副本实例
     * 
     * @param _dungeonDataModel 副本数据模板
     * @param _pattern 难度模式
     * @param _groupID 队伍编号
     * @return
     */
    private Dungeon buildNewDungeon (DungeonDataModel _dungeonDataModel,
            byte _pattern, int _groupID)
    {
        Dungeon dungeon = new Dungeon(_dungeonDataModel, _pattern, _groupID);

        Map map;

        for (MapModelData mapModeldata : _dungeonDataModel.mapModelList)
        {
            map = MapServiceImpl.getInstance().buildDungeonMap(_pattern,
                    mapModeldata, _dungeonDataModel.name, true);

            dungeon.initInternalMap(map);

            if (mapModeldata.id == _dungeonDataModel.entranceMapID)
            {
                dungeon.setEntranceMap(map);
            }
        }

        DungeonInstanceManager.getInstance().add(dungeon);

        return dungeon;
    }

    private static final String TIP_OF_MEMBER_IN_DIFFICULT_PATTERN = "已有队员在困难难度中";

    private static final String TIP_OF_MEMBER_IN_EASY_PATTERN      = "已有队员在简单难度中";

    private static final String TIP_OF_DIFFERENT_HISTORY           = "与队长进度不同";

    private static final String TIP_OF_OTHER_GROUP_USEING_HISTORY  = "另一个队伍在你的进度中";

    private static final String TIP_OF_MEMBER_FULL                 = "副本中人数已满";

    private static final String TIP_OF_HISTORY_HAPPEND             = "进度已产生";
}
