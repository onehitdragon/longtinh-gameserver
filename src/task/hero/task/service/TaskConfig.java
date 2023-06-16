package hero.task.service;

import java.util.ArrayList;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-10 上午11:35:25
 * @描述 ：
 */

public class TaskConfig extends AbsConfig
{
    /**
     * 任务数据路径
     */
    private String taskDataPath;

    /**
     * 护送NPC任务目标数据路径
     */
    private String escortNpcTaskTargetDataPath;

    /**
     * 探索地图任务目标数据路径
     */
    private String foundPathTaskTargetDataPath;

    /**
     * 物品任务目标数据路径
     */
    private String goodsTaskTargetDataPath;

    /**
     * 杀怪任务目标数据路径
     */
    private String killMonsterTaskTargetDataPath;

    /**
     * 开箱子任务目标数据路径
     */
    private String openGearTaskTargetDataPath;

    /**
     * 任务描述数据路径
     */
    private String descriptionDataPath;
    /**
     * 能够接受的任务的上限
     */
    public int can_receive_task_number;
    
    /**
     * 移动代收费失败后时候还给予装备
     */
    public boolean is_proxy_compel_give;
    /**
     * 短信收费失败后时候还给予装备
     */
    public boolean is_sms_compel_give;

    /**
     * 怪物掉落的任务物品数据路径
     */
    private String monsterTaskGoodsDataPath;
    
    /**
     * 任务推广数据路径
     */
    private String pushDataPuth;
    /**
     * 被限制不弹任务推广的渠道ID 列表
     */
    public ArrayList<Integer>  confine_publisher_list;
    
    public boolean is_use_push;

    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        Element dataPathE = node.element("para");
        Element config = node.element("taskConfig");
        taskDataPath = YOYOSystem.HOME + dataPathE.elementTextTrim("task_path");
        escortNpcTaskTargetDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("escort_npc_target_data_path");
        foundPathTaskTargetDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("found_path_target_data_path");
        goodsTaskTargetDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("goods_target_data_path");
        killMonsterTaskTargetDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("kill_monster_target_data_path");
        openGearTaskTargetDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("open_gear_target_data_path");
        descriptionDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("task_description_path");
        monsterTaskGoodsDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("monster_task_goods_path");
        pushDataPuth = YOYOSystem.HOME
        		+ dataPathE.elementTextTrim("task_push_path");
        
        can_receive_task_number = Integer.valueOf( config.elementTextTrim("can_receive_task_number") );
        //confine_publisher_list
        String[] temp = config.elementTextTrim("confine_publisher_list").split(",");
        confine_publisher_list = new ArrayList<Integer>();
        if (temp.length > 0 && (!temp[0].equals(""))) 
        {
            for (int i = 0; i < temp.length; i++) 
            {
            	confine_publisher_list.add(Integer.valueOf(temp[i]));
    		}
		}
        is_use_push = Boolean.valueOf(config.elementTextTrim("is_use_push"));
        
        is_proxy_compel_give = Boolean.valueOf( config.elementTextTrim("is_proxy_compel_give") );
        
        is_sms_compel_give = Boolean.valueOf( config.elementTextTrim("is_sms_compel_give") );
        
        
    }

    /**
     * 获取任务数据路径
     * 
     * @return
     */
    public String getTaskDataPath ()
    {
        return taskDataPath;
    }
    
    /**
     * 获得任务推广数据路径
     * @return
     */
    public String getTaskPushPath ()
    {
        return pushDataPuth;
    }

    /**
     * 获取护送npc任务目标数据路径
     * 
     * @return
     */
    public String getEscortNpcTaskTargetDataPath ()
    {
        return escortNpcTaskTargetDataPath;
    }

    /**
     * 获取探索地图任务目标数据路径
     * 
     * @return
     */
    public String getFoundPathTaskTargetDataPath ()
    {
        return foundPathTaskTargetDataPath;
    }

    /**
     * 获取物品任务目标数据路径
     * 
     * @return
     */
    public String getGoodsTaskTargetDataPath ()
    {
        return goodsTaskTargetDataPath;
    }

    /**
     * 获取杀怪任务目标数据路径
     * 
     * @return
     */
    public String getKillMonsterTaskTargetDataPath ()
    {
        return killMonsterTaskTargetDataPath;
    }

    /**
     * 获取开机关子任务目标数据路径
     * 
     * @return
     */
    public String getOpenGearTaskTargetDataPath ()
    {
        return openGearTaskTargetDataPath;
    }

    /**
     * 获取描述数据路径
     * 
     * @return
     */
    public String getDescDataPath ()
    {
        return descriptionDataPath;
    }

    /**
     * 获取怪物掉落物品数据路径
     * 
     * @return
     */
    public String getMonsterTaskGoodsDataPath ()
    {
        return monsterTaskGoodsDataPath;
    }
}
