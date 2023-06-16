package hero.player.service;

import hero.player.define.EClan;
import hero.player.define.ESex;

import org.dom4j.Element;

/**
 * 
 * @author Administrator
 * @文件 PlayerLimbsConfig.java
 * @创建者 zhengli
 * @版本 1.0
 * @时间 2011-01-17 14:50
 * @描述 ：玩家默认身体部位图片初始值扩展配置
 */
public class PlayerLimbsConfig {
	
    /**龙男发型图标编号*/
    public short init_hair_long_male_icon_id;
    /**龙男发型图片编号*/
    public short init_hair_long_male_image_id;
    /**龙男发型动画编号*/
    public short init_hair_long_male_animation;
    
    /**龙女发型图标编号*/
    public short init_hair_long_female_icon_id;
    /**龙女发型图片编号*/
    public short init_hair_long_female_image_id;
    /**龙女发型动画编号*/
    public short init_hair_long_female_animation_id;
    
    /**魔男发型图标编号*/
    public short init_hair_mo_male_icon_id;
    /**魔男发型图片编号*/
    public short init_hair_mo_male_image_id;
    /**魔男发型动画编号*/
    public short init_hair_mo_male_animation;
    
    /**魔女发型图标编号*/
    public short init_hair_mo_female_icon_id;
    /**魔女发型图片编号*/
    public short init_hair_mo_female_image_id;
    /**魔女发型动画编号*/
    public short init_hair_mo_female_animation_id;
    
    /**男脑袋图片编号*/
    public short init_head_male_image_id;
    /**男脑袋动画编号*/
    public short init_head_male_animation_id;
    /**女脑袋图片编号*/
    public short init_head_female_image_id;
    /**女脑袋动画编号*/
    public short init_head_female_animation_id;
    /**男腿图片编号*/
    public short init_leg_male_image_id;
    /**男腿动画编号*/
    public short init_leg_male_animation_id;
    /**女腿图片编号*/
    public short init_leg_female_image_id;
    /**女腿动画编号*/
    public short init_leg_female_animation_id;
    /**男尾巴图片编号*/
    public short init_tail_male_image_id;
    /**男尾巴动画编号*/
    public short init_tail_male_animation_id;
    /**女尾巴图片编号*/
    public short init_tail_female_image_id;
    /**女尾巴动画编号*/
    public short init_tail_female_animation_id;
    /**龙族死亡图片编号*/
    public short init_long_die_image_id;
    /**龙族死亡动画编号*/
    public short init_long_die_animation_id;
    /**魔族死亡图片编号*/
    public short init_mo_die_image_id;
    /**魔族死亡动画编号*/
    public short init_mo_die_animation_id;
    
    /**
     * 构造
     * <p>玩家默认身体部位图片初始值扩展配置</p>
     * @param _paraElement
     */
    public PlayerLimbsConfig (Element _paraElement) {
    	
    	init_hair_long_male_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_long_male_image_id"));
    	init_hair_long_male_animation = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_long_male_animation"));
    	init_hair_long_female_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_long_female_image_id"));
    	init_hair_long_female_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_long_female_animation_id"));
    	
    	init_hair_mo_male_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_mo_male_image_id"));
    	init_hair_mo_male_animation = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_mo_male_animation"));
    	init_hair_mo_female_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_mo_female_image_id"));
    	init_hair_mo_female_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_mo_female_animation_id"));
    	
    	init_head_male_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_head_male_image_id"));
    	init_head_male_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_head_male_animation_id"));
    	init_head_female_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_head_female_image_id"));
    	init_head_female_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_head_female_animation_id"));
    	
    	init_leg_male_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_leg_male_image_id"));
    	init_leg_male_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_leg_male_animation_id"));
    	init_leg_female_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_leg_female_image_id"));
    	init_leg_female_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_leg_female_animation_id"));
    	
    	init_tail_male_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_tail_male_image_id"));
    	init_tail_male_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_tail_male_animation_id"));
    	init_tail_female_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_tail_female_image_id"));
    	init_tail_female_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_tail_female_animation_id"));
    	
    	init_long_die_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_long_die_image_id"));
    	init_long_die_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_long_die_animation_id"));
    	init_mo_die_image_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_mo_die_image_id"));
    	init_mo_die_animation_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_mo_die_animation_id"));
    	
    	init_hair_long_male_icon_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_long_male_icon_id"));
    	init_hair_long_female_icon_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_long_female_icon_id"));
    	init_hair_mo_male_icon_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_mo_male_icon_id"));
    	init_hair_mo_female_icon_id = Short.parseShort(
        		_paraElement.elementTextTrim("init_hair_mo_female_icon_id"));
    }
    
    /**
     * 返回默认头发图标ID
     * @param _sex
     * @param _clan
     * @return
     */
    public short getHairIcon(ESex _sex, EClan _clan) {
    	short hair = 0;
    	if (_clan == EClan.LONG_SHAN) {
			if (_sex == ESex.Male) {
				hair = init_hair_long_male_icon_id;
			} else {
				hair = init_hair_long_female_icon_id;
			}
		} else {
			if (_sex == ESex.Male) {
				hair = init_hair_mo_male_icon_id;
			} else {
				hair = init_hair_mo_female_icon_id;
			}
		}
    	return hair;
    }
    
    /**
     * 返回头发图片
     * @param _sex
     * @param _clan
     * @return
     */
    public short getHairImage(ESex _sex, EClan _clan) {
    	short hair = 0;
    	if (_clan == EClan.LONG_SHAN) {
			if (_sex == ESex.Male) {
				hair = init_hair_long_male_image_id;
			} else {
				hair = init_hair_long_female_image_id;
			}
		} else {
			if (_sex == ESex.Male) {
				hair = init_hair_mo_male_image_id;
			} else {
				hair = init_hair_mo_female_image_id;
			}
		}
    	return hair;
    }
    
    /**
     * 返回头发动画
     * @param _sex
     * @param _clan
     * @return
     */
    public short getHairAnimation(ESex _sex, EClan _clan) {
    	short hair = 0;
    	if (_clan == EClan.LONG_SHAN) {
			if (_sex == ESex.Male) {
				hair = init_hair_long_male_animation;
			} else {
				hair = init_hair_long_female_animation_id;
			}
		} else {
			if (_sex == ESex.Male) {
				hair = init_hair_mo_male_animation;
			} else {
				hair = init_hair_mo_female_animation_id;
			}
		}
    	return hair;
    }
    /**
     * 脑袋图片
     * @param _sex
     * @return
     */
    public short getHeadImage (ESex _sex) {
    	short head = 0;
		if (_sex == ESex.Male) {
			head = init_head_male_image_id;
		} else {
			head = init_head_female_image_id;
		}
    	return head;
    }
    /**
     * 脑袋动画
     * @param _sex
     * @return
     */
    public short getHeadAnimation (ESex _sex) {
    	short head = 0;
		if (_sex == ESex.Male) {
			head = init_head_male_animation_id;
		} else {
			head = init_head_female_animation_id;
		}
    	return head;
    }
    
    /**
     * 腿图片
     * @param _sex
     * @return
     */
    public short getLegImage(ESex _sex) {
    	short leg = 0;
    	if (_sex == ESex.Male) {
			leg = init_leg_male_image_id;
		} else {
			leg = init_leg_female_image_id;
		}
    	return leg;
    }
    /**
     * 腿动画
     * @param _sex
     * @return
     */
    public short getLegAnimation(ESex _sex) {
    	short leg = 0;
    	if (_sex == ESex.Male) {
			leg = init_leg_male_animation_id;
		} else {
			leg = init_leg_female_animation_id;
		}
    	return leg;
    }
    /**
     * 尾巴图片
     * @param _sex
     * @return
     */
    public short getTailImage (ESex _sex, EClan clan) {
    	short tail = -1;
    	if(clan == EClan.HE_MU_DU) {
	    	if (_sex == ESex.Male) {
	    		tail = init_tail_male_image_id;
			} else {
				tail = init_tail_female_image_id;
			}
    	}
    	return tail;
    }
    /**
     * 尾巴动画
     * @param _sex
     * @return
     */
    public short getTailAnimation (ESex _sex, EClan clan) {
    	short tail = -1;
    	if(clan == EClan.HE_MU_DU) {
	    	if (_sex == ESex.Male) {
	    		tail = init_tail_male_animation_id;
			} else {
				tail = init_tail_female_animation_id;
			}
    	}
    	return tail;
    }
    /**
     * 死亡图片
     * @param _clan
     * @return
     */
    public short getDieImage (EClan _clan) {
    	short die = 0;
    	if (_clan == EClan.LONG_SHAN) {
    		die = init_long_die_image_id;
		} else {
			die = init_mo_die_image_id;
		}
    	return die;
    }
    /**
     * 死亡动画
     * @param _clan
     * @return
     */
    public short getDieAnimation (EClan _clan) {
    	short die = 0;
    	if (_clan == EClan.LONG_SHAN) {
    		die = init_long_die_animation_id;
		} else {
			die = init_mo_die_animation_id;
		}
    	return die;
    }
    
    
}
