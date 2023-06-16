/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 */

package hero.map.service;


import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;

public class MapConfig extends AbsConfig
{
    /**
     * 区域地图图片路径
     */
    private String areaImagePath;

    /**
     * 地图小地图图片路径
     */
    private String microMapImagePath;

    /**
     * 地图切片图片路径
     */
    private String mapTileImagePath;

    /**
     * 地图传输列表路径
     */
    private String transmitMapListPath;

    /**
     * 区域数据路径
     */
    private String areaDataPath;

    /**
     * 地图关系数据数据路径
     */
    private String mapRelationDataPath;

    /**
     * 龙山默认初始地图
     */
    private short  bornMapIDOfLongShan;

    /**
     * 河姆渡默认初始地图
     */
    private short  bornMapIDOfHeMuDu;

    /**
     * 地图模板文件路径
     */
    private String mapModelFilePath;

    /**
     * 地图场景元素图片路径
     */
    private String mapElementImagePath;

    /**
     * 地图音乐配置文件路径
     */
    public String  map_music_config_path;
    
    /**
     * 宠物装备数据路径
     */
    public String pet_equip_data_path;
    
    public short break_lock_default_long_map;
    
    public short break_lock_default_mo_map;
    
    public boolean use_default_map;

    /**
     * 世界地图数据路径
     */
    public String world_maps_shen_long_jie;
    public String world_maps_mo_long_jie;
    public String world_maps_xian_jie;
    public String world_maps;

    /**
     *  世界总地图的PNG和ANU的ID及ANU的宽*高值
     *  以 逗号',' 分隔
     *  顺序：png,anu,宽,高
     */
    public String[] world_map_png_anu;

    /**
     * 各界名称
     * 以 逗号',' 分隔
     * 顺序：神龙界,魔龙界,仙界,世界
     */
    public String[] world_names;

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.AbstractConfig#init(org.dom4j.Element)
     */
    @Override
    public void init (Element _xmlNode) throws Exception
    {
        Element eMap = _xmlNode.element("map");
        
        Element para = _xmlNode.element("para");
        
        mapModelFilePath = YOYOSystem.HOME
                + eMap.elementTextTrim("map_model_path");
        areaImagePath = YOYOSystem.HOME
                + eMap.elementTextTrim("area_image_path");
        microMapImagePath = YOYOSystem.HOME
                + eMap.elementTextTrim("micro_map_image_path");
        mapTileImagePath = YOYOSystem.HOME
                + eMap.elementTextTrim("map_tile_image_path");
        transmitMapListPath = YOYOSystem.HOME
                + eMap.elementTextTrim("transmit_list_path");
        areaDataPath = YOYOSystem.HOME + eMap.elementTextTrim("area_data_path");
        mapRelationDataPath = YOYOSystem.HOME
                + eMap.elementTextTrim("map_relation_path");
        mapElementImagePath = YOYOSystem.HOME
                + eMap.elementTextTrim("map_element_image_path");
        map_music_config_path = YOYOSystem.HOME
                + eMap.elementTextTrim("map_music_config_path");
        pet_equip_data_path = YOYOSystem.HOME
        		+ eMap.elementTextTrim("pet_equipment_data_path");
        //add by zhengl; date: 2011-02-22; note: 添加脱离卡死默认地图,以及其他配置
        break_lock_default_long_map = Short.valueOf(
        		para.elementTextTrim("break_lock_default_long_map"));
        break_lock_default_mo_map = Short.valueOf(
        		para.elementTextTrim("break_lock_default_mo_map"));
        
        use_default_map = Boolean.valueOf(para.elementTextTrim("break_lock_default_mo_map"));

        world_maps = eMap.elementTextTrim("world_maps");
        world_maps_mo_long_jie = eMap.elementTextTrim("world_maps_mo_long");
        world_maps_shen_long_jie = eMap.elementTextTrim("world_maps_shen_long");
        world_maps_xian_jie = eMap.elementTextTrim("world_maps_xian_jie");

        world_map_png_anu = para.elementTextTrim("world_map_png_anu").split(",");

        world_names = para.elementTextTrim("world_names").split(",");
    }
    

    /**
     * 获取地图模板文件路径
     * 
     * @return
     */
    public String getMapModelFilePath ()
    {
        return mapModelFilePath;
    }

    /**
     * 获取区域图片路径
     * 
     * @return
     */
    public String getAreaImagePath ()
    {
        return areaImagePath;
    }

    /**
     * 获取地图小地图图片路径
     * 
     * @return
     */
    public String getMicroMapImagePath ()
    {
        return microMapImagePath;
    }

    /**
     * 获取地图切片路径
     * 
     * @return
     */
    public String getMapTileImagePath ()
    {
        return mapTileImagePath;
    }

    /**
     * 获取传送地图映射列表
     * 
     * @return
     */
    public String getTransmitMapListPath ()
    {
        return transmitMapListPath;
    }

    /**
     * 获取区域数据路径
     * 
     * @return
     */
    public String getAreaDataPath ()
    {
        return areaDataPath;
    }

    /**
     * 获取地图关系数据路径
     * 
     * @return
     */
    public String getMapRelationDataPath ()
    {
        return mapRelationDataPath;
    }

    /**
     * 获取地图场景元素图片路径
     * 
     * @return
     */
    public String getMapElementImagePath ()
    {
        return mapElementImagePath;
    }
}
