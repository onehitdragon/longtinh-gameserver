package hero.pet;

import hero.expressions.service.CEService;
import hero.item.bag.PetBodyWear;
import hero.item.detail.EGoodsTrait;
import hero.npc.ME2NotPlayer;
import hero.npc.service.NotPlayerServiceImpl;
import hero.pet.service.PetDictionary;
import hero.player.HeroPlayer;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.MoveSpeed;
import hero.share.service.ThreadPoolFactory;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Pet.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-13 上午09:54:59
 * @描述 ：
 */

public class Pet extends ME2NotPlayer
{
	private static Logger log = Logger.getLogger(Pet.class);
	/**
	 * 宠物种类总数
	 */
	private static final int PET_TOTAL_KIND = 8;
	
	/**
	 * 宠物蛋的孵化时间(分钟)
	 */
	public static final long PET_HATCH_TIME	= 1;//测试用，正式为 60
	
    /**
     * 编号
     * 数据库ID
     */
    public int  id;
    /**
     * 宠物原型ID，
     */
    public int aid;
    
    /**
     * 备份一份自己主人的ID以便在需要的时候能联系到主人
     */
    public int  masterID;
    
    /**
     * 在宠物字典里区分宠物
     */
    public PetPK pk;
    /**
     * 宠物阶段-蛋
     */
    public static final short PET_STAGE_EGG = 0;
    /**
     * 宠物阶段-幼年
     */
    public static final short PET_STAGE_CHILD = 1;
    /**
     * 宠物阶段-成年
     */
    public static final short PET_STAGE_ADULT = 2;
    /**
     * 草食类型宠物
     */
    public static final short PET_TYPE_HERBIVORE = 1;
    /**
     * 肉食类型宠物
     */
    public static final short PET_TYPE_CARNIVORE = 2;

    /**
     * 是否是宠物蛋
     * @return
     */
    public boolean isEgg(){
    	return pk.getStage() == PET_STAGE_EGG;
    }
    /**
     * 图标编号
     */
    public short  iconID;

    /**
     * 图片编号
     */
    public short  imageID;
    
    /**
     * 动画编号
     */
    public short  animationID;

    /**
     * 名称
     */
    public String name;
    
    /**
     * 颜色
     */
    public short color;
    
    /**
     * 当前成长点(成年后进化点)
     */
    public int currEvolvePoint;
    /**
     * 每个宠物最多进化点
     */
    public static final int MAXEVOLVEPOINT = 15;
    /**
     * 当前草食进化点(幼年)
     */
    public int currHerbPoint;
    public static final int MAXHERBPOINT = 4;
    /**
     * 当前肉食进化点(幼年)
     */
    public int currCarnPoint;
    public static final int MAXCARNPOINT = 3;
    
    /**
     * 产生途径
     * 1:自己饲养  2:交易  3:任务产出    4:竞赛产出    5:商城产出 
     */
    public short bornFrom;
    
    /**
     * 宠物等级
     */
    public int level;
    /**
     * 当前等级所持续时间
     */
    public int currLevelTime;
    
    
    /**
     * 对象类型
     */
//    protected EObjectType           objectType;
    
    /**
     * zhengl add
     * 宠物构造函数
     * 
     */
    public Pet () {
    	objectType = EObjectType.PET;
    	initPet();
    }
    
    public int getID ()
    {
        return id;
    }
    
    /**
     * 最大攻击力
	 * ATT_max = [（STR * a + AGI * b）/ 3+（B_ATT _max + ATT_mw * c ）/ TIME ] * TIME * ATB
     * 最小攻击力
     * ATT_min = [（STR * a + AGI * b）/ 3 +（ B_ATT _min + ATT_mw * c ）/ TIME ] * TIME * ATB
     */
    private void initPet() {
    	int minAtk = CEService.minPhysicsAttack(str, 
    			agi, 
    			0.3F, //参数a先写死
    			0.3F, //b先写死
    			0.3F, //c先写死
    			minAtkHarm,   //宠物无武器
    			0,   //基础最小攻击参数(怪物一般为0)
    			getActualAttackImmobilityTime(), 
    			1);
    	int maxAtk = CEService.maxPhysicsAttack(str, 
    			agi, 
    			0.3F, //参数a先写死
    			0.3F, //b先写死
    			0.3F, //c先写死
    			maxAtkHarm,   //宠物无武器
    			0,   //基础最大攻击参数(怪物一般为0)
    			getActualAttackImmobilityTime(), 
    			1);
    	this.setLevel(level);
    	this.getBaseProperty().setMinPhysicsAttack(minAtk); //添加攻击实际值
    	this.getBaseProperty().setMaxPhysicsAttack(maxAtk);
//    	this.setVocation(EVocation.WU_ZHE);
    	this.getBaseProperty().setAgility(agi);
    	this.getBaseProperty().setStrength(str);
    	this.getBaseProperty().setInte(intel);
    	this.getBaseProperty().setSpirit(spi);
    	
    }
    
    
    /**
     * 计算升到下一级所需时间(分钟)
     * @return
     */
    public int getToNextLevelNeedTime(){
//    	return level*level + 4*level - ((level-1)*(level-1) + 4*(level-1));
    	return 3;  //用于测试
    }
    
    /**
     * 当前战斗点
     */
    public int currFightPoint;
    
    /**
     * 累计在线时间
     */
    public long totalOnlineTime = 0;
    /**
     * 宠物开始在健康状态的时间
     */
//    public long startHealthTime = -1;
    /**
     * 宠物在健康状态下持续的时间
     */
    public int healthTime = 0;
    
    /**
     * 开始孵化时间
     */
    public long startHatchTime;
    
    /**
     * 本次登录时间(从显示开始)
     */
    public long loginTime;
    
    /**
     * 是否绑定 0:未绑定   1:绑定
     */
    public short bind;
    
    /**
     * 宠物显示状态 1:显示  0:不显示
     */
    public short viewStatus;
    public boolean isView;
    
       
    /**
     * 当前饲养条值(1--100红色,101--200黄色,201--300绿色,0:空(即死亡) )
     */
    public int feeding;
    public static final int FEEDING_GREEN_FULL = 300;
    public static final int FEEDING_GREEN_HALF = 250;
    public static final int FEEDING_YELLOW_FULL = 200;
    public static final int FEEDING_YELLOW_HALF = 150;
    public static final int FEEDING_RED_FULL = 100;
    public static final int FEEDING_RED_HALF = 50;
    public static final int FEEDING_NULL = 0;
    
    /**
     * 宠物是死是活
     * 蛋不会死
     * @return true 死,  false 活
     */
    public boolean isDied(){
    	if(pk.getStage() != PET_STAGE_EGG && feeding <= FEEDING_NULL){
        	viewStatus = 0;
        	isView = false;
        	return true;
    	}
    	return false;
    }
    
    public void refreshLastAttackTime ()
    {
        lastAttackTime = System.currentTimeMillis();
    }
    
    /**
     * 当前魔法值
     */
    public int mp;
    /**
     * 当前等级力量值
     */
    public int str;
    /**
     * 肉食宠物力量值基数，保存在宠物字典里
     */
    public int a_str;
    /**
     * 当前等级敏捷值
     */
    public int agi;
    /**
     * 肉食宠物敏捷值基数，保存在宠物字典里
     */
    public int a_agi;
    /**
     * 当前等级智力值
     */
    public int intel;
    /**
     * 肉食宠物智力值基数，保存在宠物字典里
     */
    public int a_intel;
    /**
     * 当前等级精神值
     */
    public int spi;
    /**
     * 肉食宠物精神值基数，保存在宠物字典里
     */
    public int a_spi;
    /**
     * 当前等级幸运值
     */
    public int luck;
    /**
     * 肉食宠物幸运值基数，保存在宠物字典里
     */
    public int a_luck;
  
    
    /**
     * 当前愤怒槽值(食肉宠)
     */
    public int rage;
    /**
     * 愤怒槽编号
     */
    public static final byte RAGECODE = 1; 
    
    /**
     * 当前智慧槽值(食肉宠)
     */
    public int wit;
    /**
     * 智慧槽编号
     */
    public static final byte WITCODE = 2;
    /**
     * 将要被洗点的 能力槽编号
     */
    public byte dicard_code;
    
    /**
     * 当前敏捷槽值(食肉宠)
     */
    public int agile;
    /**
     * 敏捷槽编号
     */
    public static final byte AGILECODE = 3;
    /**
     * 每个能力槽最多能分配的点数
     */
    public static final int MAXPERPOINT = 10;
    
    /**
     * 当前成长经验(食肉宠)
     */
    public int grow_exp;
    
    /**
     * 当前战斗经验(食肉宠)
     */
    public int fight_exp;
    /**
     * 更新战斗经验点
     * 在健康状态下持续一个小时，战斗点加 1，健康持续时间清0
     */
    public void updFightPoint(){
    	if(healthTime == 60){
    		fight_exp += 1;
    		currFightPoint += 1;
    		healthTime = 0;
    	}
    }
    /**
     * 更新进化点
     */
    public void updEvolvePoint(){
    	if(currEvolvePoint < MAXEVOLVEPOINT)
    		currEvolvePoint = currFightPoint/15;
    }
    
    public void updFEPoint(){
    	updFightPoint();
    	updEvolvePoint();
    }
    
    /**
     * 当前功能
     * 成年食肉宠有两种
     */
    public short fun; 
    
    /**
     * 成年草食宠附带的坐骑光环编号
     */
    public int mountFunction; 
    /**
     * 攻击力(物理伤害)
     */
    public int atk;
    /**
     * 最大物理伤害
     */
    public int maxAtkHarm;
    /**
     * 最小物理伤害
     */
    public int minAtkHarm;
    /**
     * 获取当前物理攻击力
     * @return
     */
    public int getATK(){
    	if(isDied()){
    		return 0;
    	}else{ 
    		if(pk.getType() == Pet.PET_TYPE_CARNIVORE){
    			atk += (atk * rage * 10/100);
        		if(feeding > FEEDING_YELLOW_FULL)
            		return atk;
            	else if(feeding > FEEDING_YELLOW_HALF)
            		return atk * 80/100;
            	else if(feeding > FEEDING_RED_FULL)
            		return atk * 60/100;
            	else if(feeding > FEEDING_RED_HALF)
            		return atk * 40/100;
            	else if(feeding > FEEDING_NULL)
            		return atk * 20/100;
    		}
    	}
    	return 0;
    }
    
    /**
     * 魔法伤害
     */
    public int magicHarm;
    /**
     * 最大魔法伤害
     */
    public int maxMagicHarm;
    /**
     * 最小魔法伤害
     */
    public int minMagicHarm;
    /**
     * 获取魔法伤害值
     * @return
     */
    public int getMagicHarm(){
    	return magicHarm;
    }
    
    /**
     * 攻击距离
     */
    public short attackRange;
//    public float baseAttackImmobilityTime;
    /**
     * 获取攻击距离
     * @return
     */
    public short getAttackRange ()
	{
		return attackRange;
	}
    /**
     * 设置攻击距离
     * @param attackRange
     */
	public void setAttackRange (short attackRange)
	{
		this.attackRange = attackRange;
	}

	/**
	 * 获取攻击间隔
	 * @return
	 */
	/*public int getBaseAttackImmobilityTime ()
	{
		return (int)baseAttackImmobilityTime;
	}*/
	/**
	 * 设置攻击间隔
	 * @param baseAttackImmobilityTime
	 */
	/*public void setBaseAttackImmobilityTime (int baseAttackImmobilityTime)
	{
		this.baseAttackImmobilityTime = baseAttackImmobilityTime;
	}*/
	
    /**
     * 上次普通攻击时间
     */
    public long lastAttackTime;

	/**
     * 速度
     */
    public int speed;
    /**
     * 获取当前速度
     * @return
     */
    public int getSpeed(){
    	if(isDied()){
    		return 0;
    	}else if(feeding > FEEDING_YELLOW_FULL)
    		return speed;
    	else if(feeding > FEEDING_YELLOW_HALF)
    		return speed * 80/100;
    	else if(feeding > FEEDING_RED_FULL)
    		return speed * 60/100;
    	else if(feeding > FEEDING_RED_HALF)
    		return speed * 40/100;
    	else if(feeding > FEEDING_NULL)
    		return speed * 20/100;
    	return 0;
    }
    
    /**
     * 获取当前表情
     * 默认表情(绿色笑脸)
     * 表情为 [0,1,2,3,4,5]
     * @return
     */
    public byte getFace(){
    	if(isDied()){
    		return 0;
    	}else if(feeding == FEEDING_NULL)
    		return 0;
    	else if(feeding > FEEDING_YELLOW_FULL)
    		return 5;
    	else if(feeding > FEEDING_YELLOW_HALF)
    		return 4;
    	else if(feeding > FEEDING_RED_FULL)
    		return 3;
    	else if(feeding > FEEDING_RED_HALF)
    		return 2;
    	else if(feeding > FEEDING_NULL)
    		return 1;
    	return 5;
    }
    
    /**
     * 蛋 功能 id
     */
    public static final short PET_EGG_FUN = 0; 
    /**
     * 幼年 功能 id
     */
    public static final short PET_CHILD_FUN = 1; 
    /**
     * 草食 功能 id
     */
    public static final short PET_HERBIVORE_FUN = 2;
    /**
     * 肉食功能 id
     */
    public static final short PET_CARNIVORE_FUN = 3;
    
    /**
     * 主动技能列表
     */
    public List<PetActiveSkill> petActiveSkillList = new ArrayList<PetActiveSkill>();
    /**
     * 被动技能列表
     */
    public List<PetPassiveSkill> petPassiveSkillList = new ArrayList<PetPassiveSkill>();
    
    /**
     * 根据技能ID ，获取主动技能
     * @param skillID
     * @return
     */
    public PetActiveSkill getPetActiveSkillByID(int skillID){
    	for(PetActiveSkill skill : petActiveSkillList){
    		if(skill.id == skillID){
    			return skill;
    		}
    	}
    	return null;
    }
    /**
     * 根据技能ID，获取被动技能
     * @param skillID
     * @return
     */
    public PetPassiveSkill getPetPassiveSkillByID(int skillID){
    	for(PetPassiveSkill skill : petPassiveSkillList){
    		if(skill.id == skillID){
    			return skill;
    		}
    	}
    	return null;
    }
    
    /**
     * 装备列表
     */
    public List<Integer> petEquList = new ArrayList<Integer>();
    
    private PetBodyWear bodyWear = new PetBodyWear();
    
    public PetBodyWear getPetBodyWear(){
    	return bodyWear;
    }
    
    /**
     * 随机得到一种宠物蛋
     * @return
     */
    public static Pet getRandomPetEgg(){
    	List<Pet> eggList = new ArrayList<Pet>();
    	FastMap<Integer, Pet>  petMap = PetDictionary.getInstance().getPetDict();
        log.debug("petMap size = " + petMap.size());
        for (Pet pet : petMap.values()) {
            if (pet.pk.getStage() == Pet.PET_STAGE_EGG) {
                log.debug("pet pk stage = " + pet.pk.getStage());
                eggList.add(pet);
            }
        }
    	
    	log.debug("egglist size = " + eggList.size());
    	Random random = new Random();
    	if(eggList.size()>0){ 
    		int r = random.nextInt(eggList.size());
    		return eggList.get(r);
    	}
    	
    	return null;
    }

    /**
     * 面向
     */
    private byte direction;
    
    @Override
    public void setDirection (byte direction)
    {
    	this.direction = direction;
    }
    @Override
    public byte getDirection ()
    {
    	return direction;
    }

    /**
     * 品质，在交易时使用，暂时是固定不变的
     */
    public EGoodsTrait trait = EGoodsTrait.YU_ZHI;
	
    public short physicsDeathblowLevel;
    public short magicDeathblowLevel;
    public short hitLevel;
    public short duckLevel;
    private static final Random RANDOM = new Random();
    
    
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean canBeAttackBy(ME2GameObject object) {
		return false;
	}
	
	@Override
	public byte getDefaultSpeed() {
		return MoveSpeed.GENERIC;
	}
	@Override
	public void happenFight() {
		//lastFightTime = System.currentTimeMillis();
		
	}

	/*@Override
	public EGoodsType getIfType ()
	{
		return EGoodsType.PET;
	}
	*//**
	 * 获取格子里宠物的叠放数量
	 * 因为宠物是每只放一个格子里所以固定为 1
	 *//*
    @Override
    public int getGoodsNum ()
    {
    	return 1;
    }
    *//**
     * 设置格子里宠物的叠放数量
     * 
     *//*
    @Override
    public void setGoodsNum (int n)
    {
    	// nothing
    }*/
}
