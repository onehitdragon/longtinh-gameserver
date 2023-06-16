package hero.npc.function.system.transmit;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TransmitTargetMapInfo.java
 * @创建者 Liu Jie
 * @版本 1.0
 * @时间 2009-2-17 下午02:33:23
 * @描述 ：传送到的目的地图信息（包括编号、名称、传送费用）
 */

public class TransmitTargetMapInfo
{
    /**
     * 地图编号
     */
    private short  mapID;

    /**
     * 地图名称
     */
    private String mapName;

    /**
     * 需要的等级
     */
    private short  needLevel;

    /**
     * 目标地图X坐标
     */
    private byte   mapX;

    /**
     * 目标地图Y坐标
     */
    private byte   mapY;

    /**
     * 传送费用
     */
    private int    freight;

    /**
     * 描述
     */
    private String freightDescription;

    /**
     * 构造
     * 
     * @param _mapID
     * @param _mapName
     * @param _freight
     */
    public TransmitTargetMapInfo(short _mapID, String _mapName,
            String _areaName, short _needLevel, byte _mapX, byte _mapY,
            int _freight)
    {
        mapID = _mapID;
//        mapName = _areaName + NAME_CONNECTOR + _mapName;
        mapName = _mapName;
        needLevel = _needLevel;
        mapX = _mapX;
        mapY = _mapY;
        freight = _freight;
        freightDescription = DESC_HEADER + _freight + DESC_ENDER;
    }

    /**
     * 获取地图编号
     * 
     * @return
     */
    public short getMapID ()
    {
        return mapID;
    }

    /**
     * 获取地图名称
     * 
     * @return
     */
    public String getMapName ()
    {
        return mapName;
    }

    /**
     * 获取传送需要的等级
     * 
     * @return
     */
    public short getNeedLevel ()
    {
        return needLevel;
    }

    /**
     * 获取地图出现位置X坐标
     * 
     * @return
     */
    public byte getMapX ()
    {
        return mapX;
    }

    /**
     * 获取地图出现位置Y坐标
     * 
     * @return
     */
    public byte getMapY ()
    {
        return mapY;
    }

    /**
     * 获取传送费用
     * 
     * @return
     */
    public int getFreight ()
    {
        return freight;
    }

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDescription ()
    {
        return freightDescription;
    }

    /**
     * **需后续工作
     * 描述头文字
     */
    private static final String DESC_HEADER    = "费用：";

    /**
     * 描述尾文字
     */
    private static final String DESC_ENDER     = "金";

    /**
     * 地图名连接符
     */
    private static final String NAME_CONNECTOR = "--";
}
