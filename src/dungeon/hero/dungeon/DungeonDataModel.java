package hero.dungeon;

import hero.map.MapModelData;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonDataModel.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-8 下午01:50:33
 * @描述 ：
 */

public class DungeonDataModel
{
    /**
     * 副本编号
     */
    public int            id;

    /**
     * 副本名称
     */
    public String         name;

    /**
     * 等级
     */
    public short          level;

    /**
     * 玩家数量上限
     */
    public byte           playerNumberLimit;

    /**
     * 入口地图编号
     */
    public int            entranceMapID;

    /**
     * 副本中地图模板列表
     */
    public MapModelData[] mapModelList;
}
