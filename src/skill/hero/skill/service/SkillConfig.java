package hero.skill.service;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-14 下午02:00:59
 * @描述 ：技能配置
 */

public class SkillConfig extends AbsConfig
{
    /**
     * 主动技能单元数据文件路径
     */
    public String active_skill_data_path;

    /**
     * 被动触发技能单元数据文件路径
     */
    public String touch_skill_data_path;

    /**
     * 强化技能或效果数据文件路径
     */
    public String enhance_skill_data_path;

    /**
     * 强化属性技能数据文件路径
     */
    public String change_property_skill_data_path;

    /**
     * 玩家技能数据文件路径
     */
    public String player_skill_data_path;

    /**
     * 怪物技能数据文件路径
     */
    public String monster_skill_data_path;

    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        Element dataPathE = node.element("para");

        active_skill_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("active_skill_data_path");
        touch_skill_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("touch_skill_data_path");
        enhance_skill_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("enhance_skill_data_path");
        change_property_skill_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("change_property_skill_data_path");
        player_skill_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("player_skill_data_path");
        monster_skill_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("monster_skill_data_path");
    }

}
