/**
 * Copyright: Copyright (c) 2008
 * <br>
 * Company: Digifun
 * <br>
 * Date: 2008-11-6
 */

package hero.npc.service;


import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;

/**
 * Description:<br>
 * NPC图片路径等的存放路径
 * 
 * @author Johnny
 * @version 0.1
 */

public class NpcConfig extends AbsConfig
{
    /**
     * 高端怪物图片所在目录
     */
    public String MonsterImageHPath;

    /**
     * 低端怪物图片所在目录
     */
    public String MonsterImageLPath;

    /**
     * 怪物图片配置文件
     */
    public String MonsterImageCfgPath;

    /**
     * NPC图片
     */
    public String NPCImagePath;

    /**
     * NPC图片配置文件
     */
    public String NPCImageCfgPath;

    public int    NPC_NAME_LENGTH;

    public String MONSTER_DATA_PATH;

    public String NPC_DATA_PATH;

    public String NPC_HELLO_PATH;

    public int    MONSTER_AI_INTERVAL;

    public short  MONSTER_MOVE_MOST_FAST_GRID;

    public short  MONSTER_MOVE_GRID_NUM_PER_TIME;

    public int    MONSTER_ACTIVE_MOVE_INTERVAL;

    public short  NPC_FOLLOW_GRID_DISTANCE_OF_TARGET;

    public short  NPC_FOLLOW_MOST_FAST_GRID;

    public short  ANIMAL_WALK_GRID_NUM_PER_TIME;

    public String ANIMAL_DATA_PATH;

    public String ANIMAL_IMAGE_PATH;

    public String GEAR_DATA_PATH;

    public String other_task_object_image_path;

    public String ROAD_PLATE_DATA_PATH;

    public String door_plate_data_path;

    public String BOX_DATA_PATH;

    public String TASK_GOODS_ON_MAP_DATA_PATH;

    public String MONSTER_AI_DATA_PATH;

    public String MONSTER_SKILL_AI_DATA_PATH;

    public String MONSTER_SPECIAL_AI_DATA_PATH;

    public String MONSTER_CALL_DATA_PATH;

    public String MONSTER_CHANGES_DATA_PATH;

    public String MONSTER_DISAPPEAR_DATA_PATH;

    public String MONSTER_RUN_AWAY_DATA_PATH;

    public String MONSTER_SHOUT_DATA_PATH;
    
    /**
     * NPC功能--答题数据路径 (答题 -- 问题表)
     */
    public String npc_function_data_question;
    /**
     * NPC功能--答题数据路径 (答题 -- 奖励表)
     */
    public String npc_function_data_award;
    /**
     * NPC功能--答题数据路径 (答题 -- 答题主表)
     */
    public String npc_function_data_anwser_question;
    
    /**
     * NPC功能--凭证领奖数据路径(领取 -- 奖励)
     */
    public String npc_function_data_evidenve_award;
    /**
     * NPC功能--凭证领奖数据路径(领取 -- 主表)
     */
    public String npc_function_data_evidenve_gift;

    public String dungeonManagerDataPath;

    /**
     * npc 功能图标
     */
    public String npc_fun_icon_path;

    /**
     * 商人出售物品数据存放路径
     */
    public String trader_sell_content_data_path;

    /**
     * 交换商人交换内容数据路径
     */
    public String trader_exchange_content_data_path;

    /**
     * 任务道具召唤出的怪物存在时间
     */
    public int    task_call_monster_exist_time;

    /**
     * AI召唤出的怪物存在时间
     */
    public int    ai_call_monster_exist_time;
    
    /**
     * AI追击距离
     */
    public int    ai_follow_distance;
    
    /**
     * AI追击总格子数
     */
    public int    ai_follow_grid;
    
    /**
     * 地上任务物品刷新周期. 秒为单位
     */
    public int    task_gather_rebirth_interval;
    
    /**
     * 地上任务采集物品线程sleep周期 (单位:秒)
     */
    public int    task_gather_thread_run_interval;

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.AbstractConfig#init(org.dom4j.Element)
     */
    @Override
    public void init (Element _xmlNode) throws Exception
    {
        Element eMap = _xmlNode.element("npc");
        //add by zhengl; date: 2011-03-01; note: 固定数值尽量放进配置文件
        ai_follow_distance = Integer.valueOf( eMap.elementTextTrim("ai_follow_distance") );
        ai_follow_grid = Integer.valueOf( eMap.elementTextTrim("ai_follow_grid") );
        //end
        task_gather_rebirth_interval = Integer.valueOf(
        		eMap.elementTextTrim("task_gather_rebirth_interval"));
        task_gather_thread_run_interval = Integer.valueOf(
        		eMap.elementTextTrim("task_gather_thread_run_interval"));

        MonsterImageHPath = YOYOSystem.HOME
                + eMap.elementTextTrim("monsterImageH");

        MonsterImageLPath = YOYOSystem.HOME
                + eMap.elementTextTrim("monsterImageL");

        MonsterImageCfgPath = YOYOSystem.HOME
                + eMap.elementTextTrim("monsterImageCfg");

        NPCImagePath = YOYOSystem.HOME + eMap.elementTextTrim("NPCImage");

        NPCImageCfgPath = YOYOSystem.HOME + eMap.elementTextTrim("NPCImageCfg");

        MONSTER_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_DATA_PATH");

        NPC_DATA_PATH = YOYOSystem.HOME + eMap.elementTextTrim("NPC_DATA_PATH");

        NPC_HELLO_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("NPC_HELLO_PATH");

        NPC_NAME_LENGTH = Integer.parseInt(eMap
                .elementTextTrim("NPC_NAME_LENGTH"));

        MONSTER_AI_INTERVAL = Integer.parseInt(eMap
                .elementTextTrim("MONSTER_AI_INTERVAL"));

        MONSTER_MOVE_MOST_FAST_GRID = Short.parseShort(eMap
                .elementTextTrim("MONSTER_MOVE_MOST_FAST_GRID"));

        MONSTER_MOVE_GRID_NUM_PER_TIME = Short.parseShort(eMap
                .elementTextTrim("MONSTER_MOVE_GRID_NUM_PER_TIME"));

        MONSTER_ACTIVE_MOVE_INTERVAL = Integer.parseInt(eMap
                .elementTextTrim("MONSTER_ACTIVE_MOVE_INTERVAL"));

        NPC_FOLLOW_GRID_DISTANCE_OF_TARGET = Short.parseShort(eMap
                .elementTextTrim("NPC_FOLLOW_GRID_DISTANCE_OF_TARGET"));

        NPC_FOLLOW_MOST_FAST_GRID = Short.parseShort(eMap
                .elementTextTrim("NPC_FOLLOW_MOST_FAST_GRID"));

        ANIMAL_WALK_GRID_NUM_PER_TIME = Short.parseShort(eMap
                .elementTextTrim("ANIMAL_WALK_GRID_NUM_PER_TIME"));

        ANIMAL_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("ANIMAL_DATA_PATH");

        ANIMAL_IMAGE_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("ANIMAL_IMAGE_PATH");

        GEAR_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("GEAR_DATA_PATH");

        other_task_object_image_path = YOYOSystem.HOME
                + eMap.elementTextTrim("other_task_object_image_path");

        ROAD_PLATE_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("ROAD_PLATE_DATA_PATH");

        door_plate_data_path = YOYOSystem.HOME
                + eMap.elementTextTrim("door_plate_data_path");
        
        npc_function_data_question = YOYOSystem.HOME + eMap.elementTextTrim("npc_function_data_question");
        
        npc_function_data_award = YOYOSystem.HOME + eMap.elementTextTrim("npc_function_data_award");
        
        npc_function_data_anwser_question = 
        	YOYOSystem.HOME + eMap.elementTextTrim("npc_function_data_anwser_question");
        
        npc_function_data_evidenve_award = 
        	YOYOSystem.HOME + eMap.elementTextTrim("npc_function_data_evidenve_award");
        
        npc_function_data_evidenve_gift = 
        	YOYOSystem.HOME + eMap.elementTextTrim("npc_function_data_evidenve_gift");

        BOX_DATA_PATH = YOYOSystem.HOME + eMap.elementTextTrim("BOX_DATA_PATH");

        TASK_GOODS_ON_MAP_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("TASK_GOODS_ON_MAP_DATA_PATH");

        MONSTER_AI_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_AI_DATA_PATH");

        MONSTER_SKILL_AI_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_SKILL_AI_DATA_PATH");

        MONSTER_SPECIAL_AI_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_SPECIAL_AI_DATA_PATH");

        MONSTER_CALL_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_CALL_DATA_PATH");

        MONSTER_CHANGES_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_CHANGES_DATA_PATH");

        MONSTER_DISAPPEAR_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_DISAPPEAR_DATA_PATH");

        MONSTER_RUN_AWAY_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_RUN_AWAY_DATA_PATH");

        MONSTER_SHOUT_DATA_PATH = YOYOSystem.HOME
                + eMap.elementTextTrim("MONSTER_SHOUT_DATA_PATH");

        dungeonManagerDataPath = YOYOSystem.HOME
                + eMap.elementTextTrim("DUNGEON_MANAGER_DATA_PATH");

        trader_sell_content_data_path = YOYOSystem.HOME
                + eMap.elementTextTrim("trader_sell_content_data_path");

        trader_exchange_content_data_path = YOYOSystem.HOME
                + eMap.elementTextTrim("trader_exchange_content_data_path");

        task_call_monster_exist_time = Integer.parseInt(eMap
                .elementTextTrim("task_call_monster_exist_time"));

        ai_call_monster_exist_time = Integer.parseInt(eMap
                .elementTextTrim("ai_call_monster_exist_time"));

        npc_fun_icon_path = YOYOSystem.HOME + eMap.elementTextTrim("npc_fun_icon_path");
    }
}
