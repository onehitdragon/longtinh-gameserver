package hero.item.service;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GoodsConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-18 下午05:09:30
 * @描述 ：物品配置类
 */

public class GoodsConfig extends AbsConfig
{
	private SpecialConfig specialConfig;
    /**
     * 武器数据存放路径
     */
    public String weapon_data_path;

    /**
     * 防具数据存放路径
     */
    public String armor_data_path;

    /**
     * 药水数据存放路径
     */
    public String medicament_data_path;

    /**
     * 材料数据存放路径
     */
    public String material_data_path;

    /**
     * 礼包数据存放路径
     */
    public String gift_bag_data_path;
    
    /**
     * 特殊物品数据存放路径
     */
    public String special_goods_data_path;

    /**
     * 普通任务物品数据存放路径
     */
    public String task_goods_data_path;

    /**
     * 交换物品数据存放路径
     */
    public String exchange_goods_data_path;

    /**
     * 世界掉落物品数据存放路径
     */
    public String world_legacy_equip_data_path; //掉落装备
    public String world_legacy_material_data_path; //掉落材料
    public String world_legacy_medicament_data_path;//掉落药水

    /**
     * 转职对应道具
     */
    public String change_vocation_tool_data_path;
    
    /**
     * 强化闪光展示数据组
     */
    public short[][] shine_flash_view;
    
    /**
     * 装备强化发光展示数据组
     */
    public short[][] armor_shine_flash_view;
    
    /**
     * 已镶嵌的宝石在UI上展示数据组
     */
    public short[][] yet_set_jewel;

    /**
     * 套装数据路径
     */
    public String suite_equipment_data_path;
    
    public int[] perforate_money_list;
    
    public int[] enhance_money_list;
    
    public int[] wreck_money_list;
    
    public String describe_string;
    
    public String describe_enhance_string;
    
    public String material_bag_tab_name;
    
    public String medicament_bag_tab_name;
    
    public String task_tool_bag_tab_name;
    
    public String special_bag_tab_name;
    
    public String equipment_bag_tab_name;

    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        Element paraElement = node.element("para");
        
        specialConfig = new SpecialConfig(node.element("specialConfig"));
        
        Element defaultConfig = node.element("defaultConfig");

        weapon_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("weapon_data_path");
        armor_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("armor_data_path");
        medicament_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("medicament_data_path");
        material_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("material_data_path");
        task_goods_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("task_goods_data_path");
        exchange_goods_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("exchange_goods_data_path");
        special_goods_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("special_goods_data_path");
        change_vocation_tool_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("change_vocation_tool_data_path");

        world_legacy_equip_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("world_legacy_equip_data_path");
        world_legacy_material_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("world_legacy_material_data_path");
        world_legacy_medicament_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("world_legacy_medicament_data_path");

        suite_equipment_data_path = YOYOSystem.HOME
                + paraElement.elementTextTrim("suite_equipment_data_path");
        
        gift_bag_data_path = YOYOSystem.HOME
        		+ paraElement.elementTextTrim("gift_bag_data_path");
        
        shine_flash_view = new short[6][2];
        for (int i = 0; i < shine_flash_view.length; i++) {
        	String[] temp = defaultConfig.elementTextTrim("shine_flash_view_" + (i+1)).split(",");
        	shine_flash_view[i][0] = Short.valueOf(temp[0]);
        	shine_flash_view[i][1] = Short.valueOf(temp[1]);
		}
        //armor_shine_flash_view
        armor_shine_flash_view = new short[6][2];
        for (int i = 0; i < armor_shine_flash_view.length; i++) {
        	String[] temp = defaultConfig.elementTextTrim("armor_shine_flash_view_" + (i+1)).split(",");
        	armor_shine_flash_view[i][0] = Short.valueOf(temp[0]);
        	armor_shine_flash_view[i][1] = Short.valueOf(temp[1]);
		}
        
        yet_set_jewel = new short[3][2];
        for (int i = 0; i < yet_set_jewel.length; i++) {
        	String[] temp = defaultConfig.elementTextTrim("yet_set_jewel_" + (i+1)).split(",");
        	yet_set_jewel[i][0] = Short.valueOf(temp[0]);
        	yet_set_jewel[i][1] = Short.valueOf(temp[1]);
		}
        
        //加载强化所需金额.
        perforate_money_list = new int[12];
        String[] temp = defaultConfig.elementTextTrim("perforate_money_list").split(",");
        for (int i = 0; i < perforate_money_list.length; i++) {
        	perforate_money_list[i] = Integer.valueOf(temp[i]);
		}
        
        enhance_money_list = new int[12];
        temp = defaultConfig.elementTextTrim("enhance_money_list").split(",");
        for (int i = 0; i < enhance_money_list.length; i++) {
        	enhance_money_list[i] = Integer.valueOf(temp[i]);
		}
        
        wreck_money_list = new int[12];
        temp = defaultConfig.elementTextTrim("wreck_money_list").split(",");
        for (int i = 0; i < wreck_money_list.length; i++) {
        	wreck_money_list[i] = Integer.valueOf(temp[i]);
		}
        
        describe_string = defaultConfig.elementTextTrim("describe_string");
        
        describe_enhance_string = defaultConfig.elementTextTrim("describe_enhance_string");
        
        material_bag_tab_name = defaultConfig.elementTextTrim("material_bag_tab_name");
        
        medicament_bag_tab_name = defaultConfig.elementTextTrim("medicament_bag_tab_name");
        
        task_tool_bag_tab_name = defaultConfig.elementTextTrim("task_tool_bag_tab_name");
        
        special_bag_tab_name = defaultConfig.elementTextTrim("special_bag_tab_name");
        
        equipment_bag_tab_name = defaultConfig.elementTextTrim("equipment_bag_tab_name");
    }

    public SpecialConfig getSpecialConfig()
    {
    	return specialConfig;
    }
}
