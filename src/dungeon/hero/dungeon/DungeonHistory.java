package hero.dungeon;

import hero.dungeon.service.DungeonDataModelDictionary;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonHistory.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-7 上午10:04:03
 * @描述 ：
 */

public class DungeonHistory
{
    /**
     * 进度编号
     */
    private int                    historyID;

    /**
     * 副本编号
     */
    private int                    dungeonID;

    /**
     * 副本名称
     */
    private String                 dungeonName;

    /**
     * 模式（简单、困难）
     */
    private byte                   dungeonPattern;

    /**
     * 副本类型（普通队伍、团队）
     */
    private byte                   dungeonType;

    /**
     * 产生时间
     */
    private Date                   buildTime;

    /**
     * 死亡的Boss表（死亡的首领模板编号:地图编号，存储到数据库的形式以 monsterID&mapID|monsterID&mapID）
     */
    private HashMap<String, Short> deathBossTable          = new HashMap<String, Short>();

    /**
     * 包含的玩家角色编号
     */
    private Vector<Integer>        includePlayerUserIDList = new Vector<Integer>();

    /**
     * 构造（来源于BOSS死亡产生进度的内存副本）
     * 
     * @param _historyID
     * @param _dungeonID
     */
    public DungeonHistory(Dungeon _dungeon, short _mapID, String _monsterModelID)
    {
        historyID = _dungeon.getHistoryID();
        dungeonID = _dungeon.getID();
        dungeonName = _dungeon.getName();
        dungeonPattern = _dungeon.getPattern();
        dungeonType = _dungeon.getType();
        buildTime = new Date();
        deathBossTable.put(_monsterModelID, _mapID);
    }

    /**
     * 构造（来源于服务启动时从数据库加载）
     * 
     * @param _historyID
     * @param _dungeonID
     */
    public DungeonHistory(int _historyID, int _dungeonID, byte _pattern,
            byte _dungeonType, Date _buildTime)
    {
        historyID = _historyID;
        dungeonID = _dungeonID;
        dungeonName = DungeonDataModelDictionary.getInsatnce().get(_dungeonID).name;
        dungeonPattern = _pattern;
        dungeonType = _dungeonType;
        buildTime = _buildTime;
    }

    /**
     * 获取进度编号
     * 
     * @return
     */
    public int getID ()
    {
        return historyID;
    }

    /**
     * 获取副本编号
     * 
     * @return
     */
    public int getDungeonID ()
    {
        return dungeonID;
    }

    /**
     * 获取副本名称
     * 
     * @return
     */
    public String getDungeonName ()
    {
        return dungeonName;
    }

    /**
     * 难度模式
     * 
     * @return
     */
    public byte getPattern ()
    {
        return dungeonPattern;
    }

    /**
     * 获取副本类型（普通5人队伍、超过5人的团队）
     * 
     * @return
     */
    public byte getDungeonType ()
    {
        return dungeonType;
    }

    /**
     * 获取创建时间
     * 
     * @return
     */
    public Date getBuildTime ()
    {
        return buildTime;
    }

    /**
     * 是否包含玩家
     * 
     * @param _userID
     * @return
     */
    public boolean containsPlayer (int _userID)
    {
        return includePlayerUserIDList.contains(_userID);
    }

    /**
     * 获取死亡BOSS信息列表
     * 
     * @return
     */
    public HashMap<String, Short> getDeathBossTable ()
    {
        return deathBossTable;
    }

    /**
     * 获取进度中包含的玩家编号列表
     * 
     * @return
     */
    public Vector<Integer> getIncludePlayerUserIDList ()
    {
        return includePlayerUserIDList;
    }

    /**
     * 是否包含某玩家
     * 
     * @param _userID
     * @return
     */
    public boolean includePlayer (int _userID)
    {
        return includePlayerUserIDList.contains(_userID);
    }

    /**
     * 向进度中添加死亡BOSS信息
     * 
     * @param _mapID
     * @param _monsterModelID
     */
    public void addDeathBoss (String _monsterModelID, short _mapID)
    {
        if (!deathBossTable.containsKey(_monsterModelID))
        {
            deathBossTable.put(_monsterModelID, _mapID);
        }
    }

    /**
     * 向进度中添加玩家
     * 
     * @param _userID
     */
    public boolean addPlayer (int _userID)
    {
        if (!includePlayerUserIDList.contains(_userID))
        {
            return includePlayerUserIDList.add(_userID);
        }

        return false;
    }
}
