package hero.effect.service;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 N_EffectConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-13 下午05:54:40
 * @描述 ：
 */

public class EffectConfig extends AbsConfig
{
    /**
     * 静态效果数据文件路径
     */
    public String static_effect_data_path;

    /**
     * 动态效果数据文件路径
     */
    public String dynamic_effect_data_path;

    /**
     * 触发效果数据文件路径
     */
    public String touch_effect_data_path;

    /**
     * 武器强化（杀戮和屠魔）效果数据文件路径
     */
    public String weapon_enhance_effect_data_path;

    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        Element dataPathE = node.element("para");
        static_effect_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("static_effect_data_path");

        dynamic_effect_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("dynamic_effect_data_path");
        touch_effect_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("touch_effect_data_path");

        weapon_enhance_effect_data_path = YOYOSystem.HOME
                + dataPathE.elementTextTrim("weapon_enhance_effect_data_path");
    }
}
