package hero.fight.service;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 FightConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 上午11:39:43
 * @描述 ：战斗服务配置类
 */

public class FightConfig extends AbsConfig
{
    /**
     * 脱离战斗的判断间隔时间
     */
    public static int DISENGAGE_FIGHT_TIME = 15 * 1000;
    
    /**
     * 剑打击目标的打击效果
     */
    public short[] sword_attack_target_animation;
    
    /**
     * 匕首打击目标的打击效果
     */
    public short[] dagger_attack_target_animation;
    
    /**
     * 弓打击目标的打击效果
     */
    public short[] bow_attack_target_animation;
    
    /**
     * 锤打击目标的打击效果
     */
    public short[] hammer_attack_target_animation;
    
    /**
     * 法杖打击目标的打击效果
     */
    public short[] staff_attack_target_animation;

    @Override
    public void init (Element node) throws Exception
    {
        Element paraElement = node.element("para");

        String[] sword = paraElement.elementTextTrim("sword_attack_target_animation").split(",");
        
        String[] dagger = paraElement.elementTextTrim("dagger_attack_target_animation").split(",");
        
        String[] bow = paraElement.elementTextTrim("bow_attack_target_animation").split(",");
        
        String[] hammer = paraElement.elementTextTrim("hammer_attack_target_animation").split(",");
        
        String[] staff = paraElement.elementTextTrim("staff_attack_target_animation").split(",");
        
        //add by zhengl; date: 2011-03-01; note: 静态变量也使用init初始化.
        DISENGAGE_FIGHT_TIME = Integer.valueOf(
        		paraElement.elementTextTrim("disengage_fight_seconds")) * 1000;
        
        sword_attack_target_animation = new short[2];
        sword_attack_target_animation[0] = Short.valueOf(sword[0]);
        sword_attack_target_animation[1] = Short.valueOf(sword[1]);
        
        dagger_attack_target_animation = new short[2];
        dagger_attack_target_animation[0] = Short.valueOf(dagger[0]);
        dagger_attack_target_animation[1] = Short.valueOf(dagger[1]);
        
        bow_attack_target_animation = new short[2];
        bow_attack_target_animation[0] = Short.valueOf(bow[0]);
        bow_attack_target_animation[1] = Short.valueOf(bow[1]);
        
        hammer_attack_target_animation = new short[2];
        hammer_attack_target_animation[0] = Short.valueOf(hammer[0]);
        hammer_attack_target_animation[1] = Short.valueOf(hammer[1]);
        
        staff_attack_target_animation = new short[2];
        staff_attack_target_animation[0] = Short.valueOf(staff[0]);
        staff_attack_target_animation[1] = Short.valueOf(staff[1]);

    }
}
