/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 */

package hero.player.service;

import java.sql.Timestamp;
import java.util.ArrayList;

import hero.effect.detail.StaticEffect;
import hero.item.Armor;
import hero.item.Weapon;
import hero.item.service.EquipmentFactory;
import hero.player.define.EClan;
import hero.player.define.ESex;
import hero.share.EVocation;
import hero.share.EVocationType;

import org.dom4j.Element;

import yoyo.service.base.AbsConfig;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Dungeon.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-2 上午09:30:59
 * @描述 ：玩家服务配置
 */

public class PlayerConfig extends AbsConfig
{
	private PlayerLimbsConfig limbsConfig;
	
	public String countdown_gift_data_path;
	
	
	public boolean open_countdown_gift;
	/**
	 * 升级获得的技能点数
	 */
	private int     upgradeSkillPoint;
    /**
     * 玩家名字最大长度
     */
    public short    max_length_of_name;

    /**
     * 玩家基本信息表刷新间隔时间（毫秒）
     */
    public int      db_update_interval;

    /**
     * 初始金币
     */
    public int      init_money;
    
    /**
     * 力士能获得的技能
     */
    private int     init_li_shi_skill;
    /**
     * 斥候能获得的技能
     */
    private int     init_chi_hou_skill;
    /**
     * 法师能获得的技能
     */
    private int     init_fa_shi_skill;
    /**
     * 巫医能获得的技能
     */
    private int     init_wu_yi_skill;

    /**
     * 最高等级
     */
    public short    max_level;

    /**
     * 空手普通攻击间隔时间（毫秒）
     */
    public int      free_attack_interval;

    /**
     * 战士系初始武器编号
     */
    private int     init_weapon_id_of_wu_zhe;

    /**
     * 战士系初始化第二件武器，加背包里
     */
    private int init_weapon_id_of_wu_zhe_second;

    /**
     * 法师系初始武器编号
     */
    private int     init_weapon_id_of_magic;
    /**
     * 法师系初始化第二件武器，加背包里
     */
    private int init_weapon_id_of_magic_second;
    
    /**
     * 牧师系初始武器编号
     */
    private int     init_weapon_id_of_mu_shi;
    /**
     * 牧师系初始化第二件武器，加背包里
     */
    private int init_weapon_id_of_mu_shi_second;
    
    /**
     * 游侠系初始武器编号
     */
    private int     init_weapon_id_of_you_xia;
    /**
     *  游侠系初始化第二件武器，加背包里
     */
    private int init_weapon_id_of_you_xia_second;
    
    /**
     * 战士系初始头盔图片编号
     */
    private short   init_hat_image_id_of_wu_zhe;
    
    /**
     * 法师系初始头盔图片编号
     */
    private short   init_hat_image_id_of_magic;
    
    /**
     * 游侠系初始头盔图片编号
     */
    private short   init_hat_image_id_of_you_xia;
    
    /**
     * 牧师系初始头盔图片编号
     */
    private short   init_hat_image_id_of_mu_shi;

    /**
     * 战士系初始衣服图片编号
     */
    private short   init_chothes_image_id_of_wu_zhe;
    
    /**
     * 法师系初始衣服图片编号
     */
    private short   init_chothes_image_id_of_magic;
    
    /**
     * 牧师系初始衣服图片编号
     */
    private short   init_chothes_image_id_of_mu_shi;
    
    /**
     * 游侠系初始衣服图片编号
     */
    private short   init_chothes_image_id_of_you_xia;

    /**
     * 战士系初始防具编号列表
     */
    private int[]   init_armor_id_list_of_wu_zhe;
    
    /**
     * 游侠系初始防具编号列表
     */
    private int[]   init_armor_id_list_of_you_xia;
    
    /**
     * 法师系初始防具编号列表
     */
    private int[]   init_armor_id_list_of_magic;
    
    /**
     * 牧师系初始防具编号列表
     */
    private int[]   init_armor_id_list_of_mu_shi;
    
    //项链,腰带,戒子
    private int[]   init_jewelry_id_list_of_wu_zhe;
    
    private int[]   init_jewelry_id_list_of_you_xia;
    
    private int[]   init_jewelry_id_list_of_magic;
    
    private int[]   init_jewelry_id_list_of_mu_shi;
    
    /**
     * 男性缺省衣服显示图片编号（不穿衣服）
     */
    private short   default_clothes_image_id_of_male;
    
    private short   default_clothes_animation_id_of_male;

    /**
     * 女性缺省衣服显示图片编号（不穿衣服）
     */
    private short   default_clothes_image_id_of_female;
    
    private short   default_clothes_animation_id_of_female;


    /**
     * 龙之传人(原龙山)出生地图
     */
    private short   born_map_id_of_long_shan;

    /**
     * 龙之传人(原龙山)出生地图坐标（x,y）
     */
    private byte[]  born_point_of_long_shan;

    /**
     * 恶魔之子(原河姆渡)出生地图
     */
    private short   born_map_id_of_he_mu_du;

    /**
     * 恶魔之子(原河姆渡)出生地图坐标（x,y）
     */
    private byte[]  born_point_of_he_mu_du;

    /**
     * 战士系初始生命值
     */
    private int     init_hp_of_wu_zhe;

    /**
     * 战士系初始力值
     */
    private int     init_fore_of_wu_zhe;
    
    /**
     * 游侠系初始生命值
     */
    private int     init_hp_of_you_xia;

    /**
     * 游侠系初始魔法值
     */
    private int     init_mp_of_you_xia;

    /**
     * 法师系初始生命值
     */
    private int     init_hp_of_magic;

    /**
     * 法师系初始魔法值
     */
    private int     init_mp_of_magic;
    
    /**
     * 牧师系初始生命值
     */
    private int     init_hp_of_mu_shi;

    /**
     * 牧师系初始魔法值
     */
    private int     init_mp_of_mu_shi;

    /**
     * 用力槽的职业初始药水信息（物品编号：数量）
     */
    private int[][] init_medicament_of_wu_zhe;

    /**
     * 有魔法值的职业初始药水信息（物品编号：数量）
     */
    private int[][] init_medicament_of_magic;

    /**
     * 战士初始技能
     */
    private int[]   init_skill_of_wu_zhe;
    
    /**
     * 游侠初始技能
     */
    private int[]   init_skill_of_you_xia;

    /**
     * 法师初始技能
     */
    private int[]   init_skill_of_magic;
    
    /**
     * 牧师初始技能
     */
    private int[]   init_skill_of_mu_shi;
    
    /**
     * 初始技能点
     */
    public int   init_surplus_skill_point;
    
    /**
     * 洗点后返还的技能点
     */
    public int   forget_skill_back_point;
    
    public int[]  init_new_male_role_armor_view_wu_zhe;
    
    public int[]  init_new_male_role_armor_view_you_xia;
    
    public int[]  init_new_male_role_armor_view_fa_shi;
    
    public int[]  init_new_male_role_armor_view_mu_shi;
    
    
    public short[][] default_armor_list;
    
    public short[][] default_weapon_list;

    /**
     * 是否使用新手教程
     * 1:使用  0:不使用
     */
    public byte use_novice;

    /**
     * 经验倍数
     */
    public float expModulus = 1.0f;
    /**
     * 此经验倍数开始时间
     */
    public String expModulusStartTime;
    /**
     * 此经验倍数结束时间
     */
    public String expModulusEndTime;
    
    public int    default_red_medicament;
    
    public int    default_blue_medicament;

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.AbstractConfig#init(org.dom4j.Element)
     */
    @Override
    public void init (Element _xmlNode) throws Exception
    {
        Element paraElement = _xmlNode.element("para");
        Element other = _xmlNode.element("other");
        //add by zhengl; date: 2011-05-16; note: 默认蓝红药剂
        default_red_medicament = Integer.valueOf(other.elementTextTrim("default_red_medicament"));
        default_blue_medicament = Integer.valueOf(other.elementTextTrim("default_blue_medicament"));
        //add by zhengl; date: 2011-04-01; note: 玩家基本配置将来也建议慢慢转移到配置表中.
        //path read
        countdown_gift_data_path = paraElement.elementTextTrim("countdown_gift_data_path");
        
        open_countdown_gift = Boolean.valueOf(paraElement.elementTextTrim("open_countdown_gift"));
        
        limbsConfig = new PlayerLimbsConfig(paraElement);
        //add by zhengl; date: 2011-02-25; note: 升级获得的技能点从配置获得.
        upgradeSkillPoint = Integer.parseInt(paraElement.elementTextTrim("upgradeSkillPoint"));
        
        //
        default_armor_list = new short[9][8];
        for (int i = 0; i < 9; i++) {
        	String[] temp = other.elementTextTrim("default_armor_" + (i+1)).split(",");
        	for (int j = 0; j < temp.length; j++) {
        		default_armor_list[i][j] = Short.valueOf(temp[j]);
			}
		}
        
        default_weapon_list = new short[9][12];
        for (int i = 0; i < 9; i++) {
        	String[] temp = other.elementTextTrim("default_weapon_" + (i+1)).split(",");
        	for (int j = 0; j < temp.length; j++) {
        		default_weapon_list[i][j] = Short.valueOf(temp[j]);
			}
		}
        //

        use_novice = Byte.parseByte(paraElement.elementTextTrim("use_novice"));

        expModulus = Float.parseFloat(paraElement.elementTextTrim("exp_modulus"));
        expModulusStartTime = paraElement.elementTextTrim("exp_modulus_start_time");
        expModulusEndTime = paraElement.elementTextTrim("exp_modulus_end_time");

        max_length_of_name = Short.parseShort(paraElement
                .elementTextTrim("max_length_of_name"));
        max_level = Short.parseShort(paraElement.elementTextTrim("max_level"));
        db_update_interval = Integer.parseInt(paraElement
                .elementTextTrim("db_update_interval"));
        default_clothes_image_id_of_male = Short.parseShort(paraElement
                .elementTextTrim("default_clothes_image_id_of_male"));
        default_clothes_image_id_of_female = Short.parseShort(paraElement
                .elementTextTrim("default_clothes_image_id_of_female"));
        born_map_id_of_long_shan = Short.parseShort(paraElement
                .elementTextTrim("born_map_id_of_long_shan"));

        born_point_of_long_shan = new byte[2];

        String point = paraElement.elementTextTrim("born_point_of_long_shan");
        String[] pointXY = point.split(",");

        for (int i = 0; i < 2; i++)
        {
            born_point_of_long_shan[i] = Byte.parseByte(pointXY[i]);
        }

        born_point_of_he_mu_du = new byte[2];

        point = paraElement.elementTextTrim("born_point_of_he_mu_du");
        pointXY = point.split(",");

        for (int i = 0; i < 2; i++)
        {
            born_point_of_he_mu_du[i] = Byte.parseByte(pointXY[i]);
        }

        born_map_id_of_he_mu_du = Short.parseShort(paraElement
                .elementTextTrim("born_map_id_of_he_mu_du"));
        init_money = Integer.parseInt(paraElement.elementTextTrim("init_money"));
        //add by zhengl; date: 2011-04-18; note: 创建角色的时候为各职业提升1级技能等级
        init_li_shi_skill = Integer.parseInt(paraElement.elementTextTrim("init_li_shi_skill"));
        init_chi_hou_skill = Integer.parseInt(paraElement.elementTextTrim("init_chi_hou_skill"));
        init_fa_shi_skill = Integer.parseInt(paraElement.elementTextTrim("init_fa_shi_skill"));
        init_wu_yi_skill = Integer.parseInt(paraElement.elementTextTrim("init_wu_yi_skill"));
        
        free_attack_interval = Integer.parseInt(paraElement
                .elementTextTrim("free_attack_interval"));
        init_weapon_id_of_wu_zhe = Integer.parseInt(paraElement
                .elementTextTrim("init_weapon_id_of_wu_zhe"));
        init_chothes_image_id_of_wu_zhe = Short.parseShort(paraElement
                .elementTextTrim("init_chothes_image_id_of_wu_zhe"));
        init_weapon_id_of_you_xia = Integer.parseInt(paraElement
                .elementTextTrim("init_weapon_id_of_you_xia"));
        init_chothes_image_id_of_you_xia = Short.parseShort(paraElement
                .elementTextTrim("init_chothes_image_id_of_you_xia"));
        init_weapon_id_of_magic = Integer.parseInt(paraElement
                .elementTextTrim("init_weapon_id_of_magic"));
        init_chothes_image_id_of_magic = Short.parseShort(paraElement
                .elementTextTrim("init_chothes_image_id_of_magic"));
        init_weapon_id_of_mu_shi = Integer.parseInt(paraElement
                .elementTextTrim("init_weapon_id_of_mu_shi"));
        init_chothes_image_id_of_mu_shi = Short.parseShort(paraElement
                .elementTextTrim("init_chothes_image_id_of_mu_shi"));

        init_weapon_id_of_you_xia_second = Integer.parseInt(
                paraElement.elementTextTrim("init_weapon_id_of_you_xia_second"));
        init_weapon_id_of_mu_shi_second = Integer.parseInt(
                paraElement.elementTextTrim("init_weapon_id_of_mu_shi_second"));
        init_weapon_id_of_magic_second = Integer.parseInt(
                paraElement.elementTextTrim("init_weapon_id_of_magic_second"));
        init_weapon_id_of_wu_zhe_second = Integer.parseInt(
                paraElement.elementTextTrim("init_weapon_id_of_wu_zhe_second"));

        init_armor_id_list_of_wu_zhe = new int[4];

        String ids = paraElement
                .elementTextTrim("init_armor_id_list_of_wu_zhe");
        String[] idList = ids.split(",");

        for (int i = 0; i < 4; i++)
        {
            init_armor_id_list_of_wu_zhe[i] = Integer.parseInt(idList[i]);
        }

        init_armor_id_list_of_magic = new int[4];
        ids = paraElement.elementTextTrim("init_armor_id_list_of_magic");
        idList = ids.split(",");

        for (int i = 0; i < 4; i++)
        {
            init_armor_id_list_of_magic[i] = Integer.parseInt(idList[i]);
        }
        
        init_armor_id_list_of_you_xia = new int[4];
        ids = paraElement.elementTextTrim("init_armor_id_list_of_you_xia");
        idList = ids.split(",");

        for (int i = 0; i < 4; i++)
        {
        	init_armor_id_list_of_you_xia[i] = Integer.parseInt(idList[i]);
        }
        
        init_armor_id_list_of_mu_shi = new int[4];
        ids = paraElement.elementTextTrim("init_armor_id_list_of_mu_shi");
        idList = ids.split(",");

        for (int i = 0; i < 4; i++)
        {
        	init_armor_id_list_of_mu_shi[i] = Integer.parseInt(idList[i]);
        }
        
        //首饰
        init_jewelry_id_list_of_wu_zhe = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_wu_zhe");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_jewelry_id_list_of_wu_zhe[i] = Integer.parseInt(idList[i]);
        }
        
        init_jewelry_id_list_of_you_xia = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_you_xia");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_jewelry_id_list_of_you_xia[i] = Integer.parseInt(idList[i]);
        }
        
        init_jewelry_id_list_of_magic = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_magic");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_jewelry_id_list_of_magic[i] = Integer.parseInt(idList[i]);
        }
        
        init_jewelry_id_list_of_mu_shi = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_mu_shi");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_jewelry_id_list_of_mu_shi[i] = Integer.parseInt(idList[i]);
        }
        
        //add by zhengl; date: 2011-02-27; note: 默认角色展示用新模式读取
        init_new_male_role_armor_view_wu_zhe = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_wu_zhe");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_new_male_role_armor_view_wu_zhe[i] = Integer.parseInt(idList[i]);
        }
        
        init_new_male_role_armor_view_you_xia = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_you_xia");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_new_male_role_armor_view_you_xia[i] = Integer.parseInt(idList[i]);
        }
        
        init_new_male_role_armor_view_fa_shi = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_fa_shi");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_new_male_role_armor_view_fa_shi[i] = Integer.parseInt(idList[i]);
        }
        
        init_new_male_role_armor_view_mu_shi = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_mu_shi");
        idList = ids.split(",");
        for (int i = 0; i < 3; i++)
        {
        	init_new_male_role_armor_view_mu_shi[i] = Integer.parseInt(idList[i]);
        }
        //end

        init_hp_of_wu_zhe = Integer.parseInt(paraElement
                .elementTextTrim("init_hp_of_wu_zhe"));
        init_hp_of_magic = Integer.parseInt(paraElement
                .elementTextTrim("init_hp_of_magic"));
        init_hp_of_you_xia = Integer.parseInt(paraElement
                .elementTextTrim("init_hp_of_you_xia"));
        init_hp_of_mu_shi = Integer.parseInt(paraElement
                .elementTextTrim("init_hp_of_mu_shi"));
        
        init_fore_of_wu_zhe = Integer.parseInt(paraElement
                .elementTextTrim("init_fore_of_wu_zhe"));
        init_mp_of_you_xia = Integer.parseInt(paraElement
                .elementTextTrim("init_mp_of_you_xia"));
        init_mp_of_magic = Integer.parseInt(paraElement
                .elementTextTrim("init_hp_of_magic"));
        init_mp_of_mu_shi = Integer.parseInt(paraElement
                .elementTextTrim("init_mp_of_mu_shi"));

        String infoDesc = paraElement
                .elementTextTrim("init_medicament_of_wu_zhe");
        String[] infoList = infoDesc.split(";");
        if(infoList != null && infoList.length > 0 && !infoList[0].equals(""))
        {
            init_medicament_of_wu_zhe = new int[infoList.length][2];
            for (int i = 0; i < infoList.length; i++)
            {
                String[] info = infoList[i].split(",");
                init_medicament_of_wu_zhe[i][1] = Integer.parseInt(info[1]);
                init_medicament_of_wu_zhe[i][0] = Integer.parseInt(info[0]);
            }
        }


        infoDesc = paraElement.elementTextTrim("init_medicament_of_magic");
        infoList = infoDesc.split(";");
        if(infoList != null && infoList.length > 0 && !infoList[0].equals(""))
        {
            init_medicament_of_magic = new int[infoList.length][2];
            for (int i = 0; i < infoList.length; i++)
            {
                String[] info = infoList[i].split(",");
                init_medicament_of_magic[i][0] = Integer.parseInt(info[0]);
                init_medicament_of_magic[i][1] = Integer.parseInt(info[1]);
            }
        }


        ids = paraElement.elementTextTrim("init_skill_of_wu_zhe");
        idList = ids.split(",");
        init_skill_of_wu_zhe = new int[idList.length];
        for (int i = 0; i < idList.length; i++)
        {
            init_skill_of_wu_zhe[i] = Integer.parseInt(idList[i]);
        }

        ids = paraElement.elementTextTrim("init_skill_of_magic");
        idList = ids.split(",");
        init_skill_of_magic = new int[idList.length];
        for (int i = 0; i < idList.length; i++)
        {
            init_skill_of_magic[i] = Integer.parseInt(idList[i]);
        }
        
        ids = paraElement.elementTextTrim("init_skill_of_you_xia");
        idList = ids.split(",");
        init_skill_of_you_xia = new int[idList.length];
        for (int i = 0; i < idList.length; i++)
        {
            init_skill_of_you_xia[i] = Integer.parseInt(idList[i]);
        }
        
        ids = paraElement.elementTextTrim("init_skill_of_mu_shi");
        idList = ids.split(",");
        init_skill_of_mu_shi = new int[idList.length];
        for (int i = 0; i < idList.length; i++)
        {
            init_skill_of_mu_shi[i] = Integer.parseInt(idList[i]);
        }
        
        init_surplus_skill_point = Integer.valueOf(
        		paraElement.elementTextTrim("init_surplus_skill_point"));
        
        forget_skill_back_point = Integer.valueOf(
        		paraElement.elementTextTrim("forget_skill_back_point"));
        
        //add by zhengl ; date: 2010-11-26 ; note: 新手也有头盔
        init_hat_image_id_of_mu_shi = Short.valueOf(
        		paraElement.elementTextTrim("init_hat_image_id_of_mu_shi"));
        init_hat_image_id_of_magic = Short.valueOf(
        		paraElement.elementTextTrim("init_hat_image_id_of_magic"));
        init_hat_image_id_of_wu_zhe = Short.valueOf(
        		paraElement.elementTextTrim("init_hat_image_id_of_wu_zhe"));
        init_hat_image_id_of_you_xia = Short.valueOf(
        		paraElement.elementTextTrim("init_hat_image_id_of_you_xia"));
        //end
        
        //add by zhengl ; date: 2011-01-17 ; note: 加上动画ID
        default_clothes_animation_id_of_male = Short.valueOf(
        		paraElement.elementTextTrim("default_clothes_animation_id_of_male"));
        default_clothes_animation_id_of_female = Short.valueOf(
        		paraElement.elementTextTrim("default_clothes_animation_id_of_female"));
        //end
    }
    
    /**
     * 返回默认等级的武器
     * @param _level
     * @return
     */
    public short[] getWeaponViewByLevel(short _level) {
    	short[] view = new short[10];
    	for (int i = 0; i < default_weapon_list.length; i++) {
			short start = default_weapon_list[i][0];
			short end = default_weapon_list[i][1];
			if(_level >= start && start <= end) {
				for (int j = 2; j < default_weapon_list.length; j++) {
					view = default_weapon_list[j];
				}
			}
		}
    	return view;
    }
    
    /**
     * 返回对应等级的默认装备
     * @param _level
     * @return
     */
    public short[] getArmorViewByLevel(short _level) {
    	short[] view = new short[6];
    	for (int i = 0; i < default_armor_list.length; i++) {
			short start = default_armor_list[i][0];
			short end = default_armor_list[i][1];
			if(_level >= start && start <= end) {
				for (int j = 2; j < default_armor_list.length; j++) {
					view = default_armor_list[j];
				}
			}
		}
    	return view;
    }

    /**
     * 玩家默认身体部位图片初始值扩展配置
     * @return
     */
    public PlayerLimbsConfig getLimbsConfig() {
    	return limbsConfig;
    }
    
    /**
     * 升级获得的技能点
     * @return
     */
    public int getUpgradeSkillPoint() {
    	return upgradeSkillPoint;
    }
    
    /**
     * <p>是否使用新手教程</p>
     * 仙境游戏已经方式使用本地化新手教程
     * @return
     */
    public boolean  useNovice(){
        return use_novice == 1;
    }

    /**
     * 获取职业相关的初始化防具ID列表
     * 
     * @param _vocation
     * @return
     */
    public int[] getInitArmorIDs (EVocationType _vocation)
    {
        if (EVocationType.PHYSICS == _vocation )
        {
            return init_armor_id_list_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation)
        {
            return init_armor_id_list_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation)
        {
            return init_armor_id_list_of_magic;
        }
        else
        {
        	return init_armor_id_list_of_mu_shi;
		}
    }
    /**
     * 获取职业相关的初始化首饰ID列表
     * @param _vocation
     * @return
     */
    public int[] getInitJewelryIDs (EVocationType _vocation)
    {
        if (EVocationType.PHYSICS == _vocation )
        {
            return init_jewelry_id_list_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation)
        {
            return init_jewelry_id_list_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation)
        {
            return init_jewelry_id_list_of_magic;
        }
        else
        {
        	return init_jewelry_id_list_of_mu_shi;
		}
    }

    /**
     * 获得职业相关初始化可展示防具的图像信息
     * [0]=头盔图片;[1]=头盔动画;[3]=头盔是否分种族展示;[4]=胸甲图片;[5]=胸甲动画;[6]=胸甲是否分种族展示
     * @param _vocation
     * @return
     */
    public int[] getInitArmorImageGroup (EVocationType _vocation) {
    	Armor armor = null;
    	int[] result = {-1, -1, 0, -1, -1, 0};
    	//edit by zhengl; date: 2011-02-25; note: 头盔不再展示在新建角色页面
        if (EVocationType.PHYSICS == _vocation)
        {
        	if(init_new_male_role_armor_view_wu_zhe[0] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
            	.getEquipmentArchetype(init_new_male_role_armor_view_wu_zhe[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
        	}
        	if(init_new_male_role_armor_view_wu_zhe[1] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
        		.getEquipmentArchetype(init_new_male_role_armor_view_wu_zhe[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
        	}
        }
        else if (EVocationType.RANGER == _vocation)
        {
        	if(init_new_male_role_armor_view_you_xia[0] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
            	.getEquipmentArchetype(init_new_male_role_armor_view_you_xia[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
        	}
        	if(init_new_male_role_armor_view_you_xia[1] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
        		.getEquipmentArchetype(init_new_male_role_armor_view_you_xia[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
        	}
        }
        else if (EVocationType.MAGIC == _vocation)
        {
        	if(init_new_male_role_armor_view_fa_shi[0] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
            	.getEquipmentArchetype(init_new_male_role_armor_view_fa_shi[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
        	}
        	if(init_new_male_role_armor_view_fa_shi[1] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
        		.getEquipmentArchetype(init_new_male_role_armor_view_fa_shi[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
        	}
        }
        else
        {
        	if(init_new_male_role_armor_view_mu_shi[0] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
            	.getEquipmentArchetype(init_new_male_role_armor_view_mu_shi[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
        	}
        	if(init_new_male_role_armor_view_mu_shi[1] != 0) {
            	armor = (Armor)EquipmentFactory.getInstance()
        		.getEquipmentArchetype(init_new_male_role_armor_view_mu_shi[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
        	}
		}

        return result;
    }
    
    /**
     * 获取职业相关的初始化防具图像信息
     * [0]=武器图片;[1]=武器动画;[2]=武器攻击光效图片;[3]=武器攻击光效动画
     * @param _vocation
     * @return
     */
    public int[] getInitWeaponImageGroup (EVocationType _vocation)
    {
    	Weapon weapon = null;
    	int[] result = {-1, -1, -1, -1};
        if (EVocationType.PHYSICS == _vocation)
        {
        	weapon = (Weapon)EquipmentFactory.getInstance()
        		.getEquipmentArchetype(init_new_male_role_armor_view_wu_zhe[2]);
            result[0] = weapon.getImageID();
            result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
        }
        else if (EVocationType.RANGER == _vocation)
        {
        	weapon = (Weapon)EquipmentFactory.getInstance()
    		.getEquipmentArchetype(init_new_male_role_armor_view_you_xia[2]);
        	result[0] = weapon.getImageID();
        	result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
        }
        else if (EVocationType.MAGIC == _vocation)
        {
        	weapon = (Weapon)EquipmentFactory.getInstance()
    		.getEquipmentArchetype(init_new_male_role_armor_view_fa_shi[2]);
        	result[0] = weapon.getImageID();
        	result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
        }
        else
        {
        	weapon = (Weapon)EquipmentFactory.getInstance()
    		.getEquipmentArchetype(init_new_male_role_armor_view_mu_shi[2]);
        	result[0] = weapon.getImageID();
        	result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
		}
        return result;
    }
    
    /**
     * 获取职业相关的初始化武器ID
     * 
     * @param _vocation
     * @return
     */
    public int getInitWeaponID (EVocation _vocation)
    {
        if (EVocationType.PHYSICS == _vocation.getType() )
        {
            return init_weapon_id_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation.getType())
        {
            return init_weapon_id_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation.getType())
        {
            return init_weapon_id_of_magic;
        }
        else
        {
        	return init_weapon_id_of_mu_shi;
		}
    }

    /**
     * 获取职业相关的初始化衣服图片编号
     * 
     * @param _vocation
     * @return
     */
    public short getInitClothesImageID (EVocationType _vocation, ESex _sex)
    {
        return this.getInitMaleClothesImageID(_vocation);
    }

    /**
     * 获取男性职业相关的初始化衣服图片编号
     * 
     * @param _vocation
     * @return
     */
    public short getInitMaleClothesImageID (EVocationType _vType)
    {
        if (EVocationType.PHYSICS == _vType )
        {
            return init_chothes_image_id_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vType)
        {
            return init_chothes_image_id_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vType)
        {
            return init_chothes_image_id_of_magic;
        }
        else
        {
        	return init_chothes_image_id_of_mu_shi;
		}
    }
    
    public short getInitMaleHatImageID (EVocationType _vocation)
    {
        if (EVocationType.PHYSICS == _vocation )
        {
            return init_hat_image_id_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation)
        {
            return init_hat_image_id_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation)
        {
            return init_hat_image_id_of_magic;
        }
        else
        {
        	return init_hat_image_id_of_mu_shi;
		}
    }

    /**
     * 获取女性职业相关的初始化衣服图片编号
     * 
     * @param _vocation
     * @return
     */
    public short getInitFemaleClothesImageID (EVocation _vocation)
    {
        if (EVocationType.PHYSICS == _vocation.getType() )
        {
            return init_chothes_image_id_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation.getType())
        {
            return init_chothes_image_id_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation.getType())
        {
            return init_chothes_image_id_of_magic;
        }
        else
        {
        	return init_chothes_image_id_of_mu_shi;
		}
    }

    /**
     * 根据性别获取无衣服时角色的衣服图片ID
     * 
     * @param _sex
     * @return
     */
    public short getDefaultClothesImageID (ESex _sex)
    {
        switch (_sex)
        {
            case Male:
            {
                return default_clothes_image_id_of_male;
            }
            case Female:
            {
                return default_clothes_image_id_of_female;
            }
            default:
            {
                return 0;
            }
        }
    }
    
    /**
     * 根据性别获取无衣服时角色的衣服动画
     * 
     * @param _sex
     * @return
     */
    public short getDefaultClothesAnimation (ESex _sex)
    {
        switch (_sex)
        {
            case Male:
            {
                return default_clothes_animation_id_of_male;
            }
            case Female:
            {
                return default_clothes_animation_id_of_female;
            }
            default:
            {
                return 0;
            }
        }
    }

    /**
     * 获取男性无衣服时角色的衣服图片ID
     * 
     * @return
     */
    public short getDefaultMaleClothesImageID ()
    {
        return default_clothes_image_id_of_male;
    }
    /**
     * 获取男性无衣服时角色的衣服动画
     * 
     * @return
     */
    public short getDefaultMaleClothesAnimation ()
    {
        return default_clothes_animation_id_of_male;
    }

    /**
     * 获取女性无衣服时角色的衣服图片ID
     * 
     * @return
     */
    public short getDefaultFemaleClothesImageID ()
    {
        return default_clothes_image_id_of_female;
    }
    /**
     * 获取女性无衣服时角色的衣服动画
     * 
     * @return
     */
    public short getDefaultFemaleClothesAnimation ()
    {
        return default_clothes_animation_id_of_female;
    }

    /**
     * 获取氏族出生地图编号
     * 
     * @param _clan 氏族
     * @return
     */
    public short getBornMapID (EClan _clan)
    {
        switch (_clan)
        {
            case LONG_SHAN:
            {
                return born_map_id_of_long_shan;
            }
            case HE_MU_DU:
            {
                return born_map_id_of_he_mu_du;
            }
            default:
            {
                return 0;
            }
        }
    }
    /**
     * 获得各职业赠送的初始技能编号
     * @param _vocation
     * @return
     */
    public int getInitSkill (EVocation _vocation)
    {
    	int skillID = 0;
    	if (_vocation == EVocation.LI_SHI) 
    	{
    		skillID = init_li_shi_skill;
		}
    	else if (_vocation == EVocation.CHI_HOU) 
    	{
    		skillID = init_chi_hou_skill;
		}
    	else if (_vocation == EVocation.FA_SHI) 
    	{
    		skillID = init_fa_shi_skill;
		}
    	else if (_vocation == EVocation.WU_YI) 
    	{
    		skillID = init_wu_yi_skill;
		}
    	return skillID;
    }

    /**
     * 获取氏族出生坐标
     * 
     * @param _sex
     * @return
     */
    public byte[] getBornPoint (EClan _clan)
    {
        switch (_clan)
        {
            case LONG_SHAN:
            {
                return born_point_of_long_shan;
            }
            case HE_MU_DU:
            {
                return born_point_of_he_mu_du;
            }
            default:
            {
                return new byte[]{20, 20 };
            }
        }
    }

    /**
     * 根据职业获取初始生命值
     * 
     * @param _vocation
     * @return
     */
    public int getInitHp (EVocation _vocation)
    {
        if (EVocationType.PHYSICS == _vocation.getType() )
        {
            return init_hp_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation.getType())
        {
            return init_hp_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation.getType())
        {
            return init_hp_of_magic;
        }
        else
        {
        	return init_hp_of_mu_shi;
		}
    }

    /**
     * 根据职业获取初始魔法值或力值
     * 
     * @param _vocation
     * @return
     */
    public int getInitMp (EVocation _vocation)
    {
        if (EVocationType.PHYSICS == _vocation.getType() )
        {
            return init_fore_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation.getType())
        {
            return init_mp_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation.getType())
        {
            return init_mp_of_magic;
        }
        else
        {
        	return init_mp_of_mu_shi;
		}
    }

    /**
     * 根据职业获取初始职业药水
     * 
     * @param _vocation
     * @return
     */
    public int[][] getInitMedicamentData (EVocation _vocation)
    {
        if (EVocationType.PHYSICS == _vocation.getType() )
        {
            return init_medicament_of_wu_zhe;
        }
        else
        {
        	return init_medicament_of_magic;
		}
    }

    /**
     * 根据职业获取初始技能列表
     * 
     * @param _vocation
     * @return
     */
    public int[] getInitSkillList (EVocation _vocation)
    {
        if (EVocationType.PHYSICS == _vocation.getType() )
        {
            return init_skill_of_wu_zhe;
        }
        else if (EVocationType.RANGER == _vocation.getType())
        {
            return init_skill_of_you_xia;
        }
        else if (EVocationType.MAGIC == _vocation.getType())
        {
            return init_skill_of_magic;
        }
        else
        {
        	return init_skill_of_mu_shi;
		}
    }

    /**
     * DEMO 版，初始化角色的第二件武器
     * @param _vocation
     * @return
     */
    public int getInitSecondWeapon(EVocation _vocation){
        if (EVocationType.PHYSICS == _vocation.getType() )
        {
            return init_weapon_id_of_wu_zhe_second;
        }
        else if (EVocationType.RANGER == _vocation.getType())
        {
            return init_weapon_id_of_you_xia_second;
        }
        else if (EVocationType.MAGIC == _vocation.getType())
        {
            return init_weapon_id_of_magic_second;
        }
        else
        {
        	return init_weapon_id_of_mu_shi_second;
		}
    }

    /**
     * 获取当前经验系数
     * @return
     */
    public float getCurrExpModulus(){
        float currModulus = 1.0f;

        if(expModulus != 1.0f){
            Timestamp startTime = Timestamp.valueOf(expModulusStartTime);
            Timestamp endTime = Timestamp.valueOf(expModulusEndTime);
            long currTime = System.currentTimeMillis();
            if(startTime.getTime() <= currTime && endTime.getTime() >= currTime){
                currModulus = expModulus;
            }
        }
        return currModulus;
    }
    
}
