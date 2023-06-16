package hero.novice.service;

import hero.player.define.EClan;
import hero.share.EVocation;
import hero.share.EVocationType;

import org.dom4j.Element;

import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NoviceConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-27 上午10:11:59
 * @描述 ：
 */

public class NoviceConfig extends AbsConfig
{
    /**
     * 新手地图编号(401)
     */
    public short novice_map_id;

    /**
     * 新手地图出生X坐标(8)
     */
    public short novice_map_born_x;

    /**
     * 新手地图出生Y坐标(8)
     */
    public short novice_map_born_y;

    /**
     * 新手任务经验奖励（80）
     */
    public short novice_task_experience;

    /**
     * 新手任务金钱奖励（100）
     */
    public short novice_task_money;

    /**
     * 新手杀怪获得经验（20）
     */
    public short novice_monster_experience;

    /**
     * 新手杀怪金钱掉落（100）
     */
    public short novice_monster_money;

    /**
     * 完成新手教学后的等级
     */
    public short level_when_complete_novice_teaching;
    
    public int[] novice_li_shi_init_award_list;
    
    public int[] novice_chi_hou_init_award_list;
    
    public int[] novice_fa_shi_init_award_list;
    
    public int[] novice_wu_yi_init_award_list;
    
    public boolean is_novice_award;
    
    public short novice_award_skill_point;
    
    public short novice_award_level;
    
    public int novice_award_money;
    
    public int[][] novice_award_item;

    /**
     * 奖励装备编号
     */
    public int   award_equipment_id;

    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        Element element = node.element("config");

        novice_map_id = Short.parseShort(element
                .elementTextTrim("novice_map_id"));
        novice_map_born_x = Short.parseShort(element
                .elementTextTrim("novice_map_born_x"));
        novice_map_born_y = Short.parseShort(element
                .elementTextTrim("novice_map_born_y"));
        novice_task_experience = Short.parseShort(element
                .elementTextTrim("novice_task_experience"));
        novice_task_money = Short.parseShort(element
                .elementTextTrim("novice_task_money"));
        novice_monster_experience = Short.parseShort(element
                .elementTextTrim("novice_monster_experience"));
        novice_monster_money = Short.parseShort(element
                .elementTextTrim("novice_monster_money"));
        level_when_complete_novice_teaching = Short.parseShort(element
                .elementTextTrim("level_when_complete_novice_teaching"));
        award_equipment_id = Short.parseShort(element
                .elementTextTrim("award_equipment_id"));
        
        String tempString = "";
        
        //add by zhengl; date: 2011-03-04; note: 活动的时候新角色创建的奖励
        is_novice_award = Boolean.valueOf( element.elementTextTrim("is_novice_award") );
        String[] temp = null;
        tempString = element.elementTextTrim("novice_li_shi_init_award_list");
        if(tempString != null && (!tempString.equals(""))) {
            temp = element.elementTextTrim("novice_li_shi_init_award_list").split(",");
            novice_li_shi_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
            	novice_li_shi_init_award_list[i] = Integer.valueOf(temp[i]);
    		}
        }

        tempString = element.elementTextTrim("novice_chi_hou_init_award_list");
        if(tempString != null && (!tempString.equals(""))) {
        	temp = tempString.split(",");
            novice_chi_hou_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
            	novice_chi_hou_init_award_list[i] = Integer.valueOf(temp[i]);
    		}
        }

        tempString = element.elementTextTrim("novice_fa_shi_init_award_list");
        if(tempString != null && (!tempString.equals(""))) {
        	temp = tempString.split(",");
            novice_fa_shi_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
            	novice_fa_shi_init_award_list[i] = Integer.valueOf(temp[i]);
    		}
        }

        tempString = element.elementTextTrim("novice_wu_yi_init_award_list");
        if(tempString != null && (!tempString.equals(""))) {
        	temp = tempString.split(",");
            novice_wu_yi_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
            	novice_wu_yi_init_award_list[i] = Integer.valueOf(temp[i]);
    		}
        }

        
        novice_award_skill_point = Short.valueOf(element.elementTextTrim("novice_award_skill_point"));
        novice_award_level = Short.valueOf(element.elementTextTrim("novice_award_level"));
        novice_award_money = Integer.valueOf(element.elementTextTrim("novice_award_money"));
        //end
        tempString = element.elementTextTrim("novice_award_item");
        if(tempString != null && (!tempString.equals(""))) {
        	temp = tempString.split(";");
            novice_award_item = new int[temp.length][2];
            for (int i = 0; i < temp.length; i++) {
            	novice_award_item[i][0] = Integer.valueOf(temp[i].split(",")[0]);
            	novice_award_item[i][1] = Integer.valueOf(temp[i].split(",")[1]);
    		}
        }

    }
    
    /**
     * 获得活动奖励物品
     * @param _vocation
     * @return
     */
    public int[] getInitAwardList(EVocation _vocation)
    {
    	int[] goodsList = null;
    	if(is_novice_award)
    	{
            if (EVocationType.PHYSICS == _vocation.getType() )
            {
            	goodsList = novice_li_shi_init_award_list;
            }
            else if (EVocationType.RANGER == _vocation.getType())
            {
            	goodsList = novice_chi_hou_init_award_list;
            }
            else if (EVocationType.MAGIC == _vocation.getType())
            {
            	goodsList = novice_fa_shi_init_award_list;
            }
            else
            {
            	goodsList = novice_wu_yi_init_award_list;
    		}
    	}
    	
    	return goodsList;
    }

}
