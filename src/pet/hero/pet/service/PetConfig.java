package hero.pet.service;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PetConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-12 下午01:32:23
 * @描述 ：
 */

public class PetConfig extends AbsConfig
{
    /**
     * 困难模式副本需要等级
     */
    public String pet_data_path;
    
    /**
     * 饲料数据路径 
     */
    public String feed_data_path;
    
    /**
     * 宠物技能数据路径
     */
    public String pet_skill_data_path;
    public String pet_skill_effect_data_path;
    

    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        Element element = node.element("config");

        pet_data_path = YOYOSystem.HOME + element.elementTextTrim("pet_data_path");
        feed_data_path = YOYOSystem.HOME + element.elementTextTrim("feed_data_path");
        pet_skill_data_path = YOYOSystem.HOME + element.elementTextTrim("pet_skill_data_path");
        pet_skill_effect_data_path = YOYOSystem.HOME + element.elementTextTrim("pet_skill_effect_data_path");
    }
}
