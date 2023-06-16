package hero.dungeon.service;


import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.ServiceManager;
import yoyo.service.base.AbsConfig;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-26 下午01:40:33
 * @描述 ：副本服务配置
 */

public class DungeonConfig extends AbsConfig
{
    /**
     * 副本地图数据路径
     */
    public String dungeon_data_path;

    /**
     * 副本进度重置时间
     */
    public byte   history_refresh_time;

    /**
     * 15人副本进度开始时间，已星期为单位
     */
    public byte   raid_history_refresh_week;

    /**
     * 困难模式副本需要等级
     */
    public byte   difficult_addition_level;

    @Override
    public void init (Element node) throws Exception
    {
        Element element = node.element("config");

        dungeon_data_path = YOYOSystem.HOME
                + element.elementTextTrim("dungeon_data_path");

        history_refresh_time = Byte.parseByte(element
                .elementTextTrim("history_refresh_time"));

        raid_history_refresh_week = Byte.parseByte(element
                .elementTextTrim("raid_history_refresh_week"));

        difficult_addition_level = Byte.parseByte(element
                .elementTextTrim("difficult_addition_level"));
    }
}
