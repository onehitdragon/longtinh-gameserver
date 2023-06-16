package hero.dungeon;

import java.util.ArrayList;

import hero.dungeon.service.DungeonHistoryManager;
import hero.dungeon.service.DungeonInstanceManager;
import hero.map.Map;
import hero.map.service.WeatherManager;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Dungeon.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-2 上午09:30:59
 * @描述 ：地下城
 */

public class Dungeon
{
    /**
     * 婚礼礼堂人数上限
     */
    public static final int MEMBER_NUMBER_OF_MARRY_MAP = 100;
    /**
     * 编号
     */
    private int               id;

    /**
     * 名称
     */
    private String            name;

    /**
     * 模式（简单、困难）
     */
    private byte              pattern;

    /**
     * 副本类型，直接影响能进入的人数和进度的处理
     */
    private byte              type;

    /**
     * 是否在首领战斗中
     */
    private boolean           isInFightingBoss;

    /**
     * 归属的队伍编号
     */
    private int               groupID;

    /**
     * 进度编号（唯一，在定期内重置）
     */
    private int               historyID;

    /**
     * 入口地图
     */
    private Map               entranceMap;

    /**
     * 无人时的时间点
     */
    private long              timeOfNobody;

    /**
     * 地下城中的地图列表
     */
    private ArrayList<Map>    mapList;

    /**
     * 死亡首领模板编号列表
     */
    private ArrayList<String> deathBossModelIDList;
    
    /**
     * 构造一个婚姻地图副本
     * 该副本只有一张地图，无人员上限,固定为简单模式，不用组队，无怪物，不保存历史，婚礼完后清除
     * @param _dungeonDataModel
     */
    public Dungeon(DungeonDataModel _dungeonDataModel){
        id = _dungeonDataModel.id;
        name = _dungeonDataModel.name;
        pattern = PATTERN_OF_EASY;
        type = TYPE_NORMAL_GROUP;
        mapList = new ArrayList<Map>();
        historyID = DungeonHistoryManager.getInstance().buildHistoryID(); // 这里用historyID来区分婚礼地图副本id
    }

    /**
     * 构造（新进度副本）
     */
    public Dungeon(DungeonDataModel _dungeonDataModel, byte _pattern,
            int _groupID)
    {
        id = _dungeonDataModel.id;
        name = _dungeonDataModel.name;
        pattern = _pattern;

        if (MEMBER_NUMBER_OF_RAID == _dungeonDataModel.playerNumberLimit)
        {
            type = TYPE_RAID;
        }
        else
        {
            type = TYPE_NORMAL_GROUP;
        }

        historyID = DungeonHistoryManager.getInstance().buildHistoryID();
        groupID = _groupID;

        mapList = new ArrayList<Map>();
        deathBossModelIDList = new ArrayList<String>();
    }

    /**
     * 构造（进度副本）
     */
    public Dungeon(DungeonHistory _history, DungeonDataModel _dungeonDataModel,
            int _groupID)
    {
        id = _history.getDungeonID();
        name = _dungeonDataModel.name;
        pattern = _history.getPattern();
        historyID = _history.getID();
        groupID = _groupID;

        if (MEMBER_NUMBER_OF_RAID == _dungeonDataModel.playerNumberLimit)
        {
            type = TYPE_RAID;
        }
        else
        {
            type = TYPE_NORMAL_GROUP;
        }

        mapList = new ArrayList<Map>();
        deathBossModelIDList = new ArrayList<String>();
    }

    /**
     * 获取副本编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    /**
     * 获取进度编号
     * 
     * @return
     */
    public int getHistoryID ()
    {
        return historyID;
    }

    /**
     * 是否需要保存进度
     * 
     * @return
     */
    public boolean needSaveHistory ()
    {
        return type == TYPE_RAID || pattern == PATTERN_OF_DIFFICULT;
    }

    /**
     * 获取副本名称
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 获取难度模式
     * 
     * @return
     */
    public byte getPattern ()
    {
        return pattern;
    }

    /**
     * 获取类型
     * 
     * @return
     */
    public byte getType ()
    {
        return type;
    }

    /**
     * 获取归属的队伍编号
     * 
     * @return
     */
    public int getGroupID ()
    {
        return groupID;
    }
    
    /**
     * 玩家进来婚礼副本
     * @param _userID
     */
    public void playerEntryMarryDungeon(int _userID){
        int playerNumber = getPlayerNumber();

        if (playerNumber < MEMBER_NUMBER_OF_MARRY_MAP)
        {
            if (1 == playerNumber && 0 != timeOfNobody)
            {
                DungeonInstanceManager.getInstance().removeDungeonFromMonitor(
                        this);
            }
        }
    }

    /**
     * 设置归属队伍编号
     * 
     * @param _groupID
     */
    public void setGroupID (int _groupID)
    {
        groupID = _groupID;
    }

    /**
     * 进入首领战斗
     */
    public void enterFightingOfBoss ()
    {
        isInFightingBoss = true;
    }

    /**
     * 结束首领战斗
     */
    public void endFightingOfBoss ()
    {
        isInFightingBoss = false;
    }

    /**
     * 是否处于首领战斗中
     * 
     * @return
     */
    public boolean isInFightingBoss ()
    {
        return isInFightingBoss;
    }

    /**
     * 添加死亡BOSS
     * 
     * @param _monsterModelID
     */
    public void addDeathBoss (String _monsterModelID)
    {
        if (!deathBossModelIDList.contains(_monsterModelID))
        {
            deathBossModelIDList.add(_monsterModelID);
        }
    }

    /**
     * BOSS是否死亡
     * 
     * @param _monsterModelID
     * @return
     */
    public boolean bossIsDead (String _monsterModelID)
    {
        return deathBossModelIDList.contains(_monsterModelID);
    }

    /**
     * 获取副本中的玩家数量
     * 
     * @return
     */
    public int getPlayerNumber ()
    {
        int playerNumber = 0;

        for (Map map : mapList)
        {
            playerNumber += map.getPlayerList().size();
        }

        return playerNumber;
    }

    /**
     * 获取副本中的玩家列表
     * 
     * @return
     */
    public ArrayList<HeroPlayer> getPlayerList ()
    {
        ArrayList<HeroPlayer> playList = new ArrayList<HeroPlayer>();

        for (Map map : mapList)
        {
            for (ME2GameObject object : map.getPlayerList())
            {
                playList.add((HeroPlayer) object);
            }
        }

        return playList;
    }

    /**
     * 设置玩家数量上限
     * 
     * @return
     */
    public int getPlayerNumberLimit ()
    {
        return type == TYPE_NORMAL_GROUP ? MEMBER_NUMBER_OF_NORMAL
                : MEMBER_NUMBER_OF_RAID;
    }

    /**
     * 玩家进来
     */
    public void playerComeIn (int _userID)
    {
        int playerNumber = getPlayerNumber();

        if (playerNumber < getPlayerNumberLimit())
        {
            if (1 == playerNumber && 0 != timeOfNobody)
            {
                DungeonInstanceManager.getInstance().removeDungeonFromMonitor(
                        this);
            }
        }
    }

    /**
     * 玩家离开
     */
    public void playerLeft (HeroPlayer _player)
    {
        if (0 == getPlayerNumber())
        {
            timeOfNobody = System.currentTimeMillis();
            DungeonInstanceManager.getInstance().addDungeonToMonitor(this);
        }
    }

    /**
     * 设置入口地图
     * 
     * @param _map
     */
    public void setEntranceMap (Map _map)
    {
        entranceMap = _map;
    }

    /**
     * 获取入口地图
     * 
     * @return
     */
    public Map getEntranceMap ()
    {
        return entranceMap;
    }

    /**
     * 根据编号获取地图
     * 
     * @param _mapID
     * @return
     */
    public Map getMap (int _mapID)
    {
        for (Map map : mapList)
        {
            if (map.getID() == _mapID)
            {
                return map;
            }
        }

        return null;
    }

    /**
     * 获取内部地图列表
     * 
     * @return
     */
    public ArrayList<Map> getInternalMapList ()
    {
        return mapList;
    }

    /**
     * 获取在无人时的时间点
     * 
     * @return
     */
    public long getTimeOfNobody ()
    {
        return timeOfNobody;
    }

    /**
     * 添加内部地图
     * 
     * @param _map
     */
    public void initInternalMap (Map _map)
    {
        mapList.add(_map);
        _map.setDungeon(this);
    }

    /**
     * 副本销毁，主要停止、清除怪物AI线程、心跳任务、NPC跟随线程
     */
    public void destroy ()
    {
        synchronized (mapList)
        {
            for (Map map : mapList)
            {
                map.destroy();
                WeatherManager.getInstance().remove(map);
            }
        }
    }

    /**
     * 简单模式
     */
    public static final byte   PATTERN_OF_EASY                = 1;

    /**
     * 困难模式
     */
    public static final byte   PATTERN_OF_DIFFICULT           = 2;

    /**
     * 困难模式下的怪物编号后缀
     */
    public static final String SUFFIX_OF_HERO_MONSTER_ID      = "h";

    /**
     * 副本地图名称分隔符
     */
    public static final String DUNGEON_MAP_NAME_SEPARATE_CHAR = " . ";

    /**
     * 团队副本容纳的人数上限
     */
    public static final byte   MEMBER_NUMBER_OF_RAID          = 15;

    /**
     * 普通副本容纳的人数上限
     */
    public static final byte   MEMBER_NUMBER_OF_NORMAL        = 5;

    /**
     * 普通队伍
     */
    public static final byte   TYPE_NORMAL_GROUP              = 1;

    /**
     * 团体类型
     */
    public static final byte   TYPE_RAID                      = 2;

    /**
     * 简单模式描述
     */
    public static final String PATTERN_DESC_OF_EASY           = "（简单）";

    /**
     * 困难模式描述
     */
    public static final String PATTERN_DESC_OF_DIFFICULT      = "（困难）";
}
