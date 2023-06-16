package hero.share;

import hero.effect.Effect;
import hero.effect.Effect.EffectFeature;
import hero.effect.detail.StaticEffect;
import hero.effect.service.EffectServiceImpl;
import hero.map.Map;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.share.cd.CDUnit;
import hero.share.service.IDManager;
import hero.skill.detail.ESpecialStatus;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ME2GameObject implements Cloneable
{
     private static Logger log = Logger.getLogger(ME2GameObject.class);
    /**
     * 游戏内存唯一标识
     */
    private int                     ID;

    /**
     * 名字
     */
    private String                  name;

    /**
     * 当前生命值、当前魔法值
     */
    private int                     hp, mp;

    /**
     * 力槽，与气槽和为100
     */
    private int                     forceQuantity;

    /**
     * 气槽，与力槽和为100
     */
    private int                     gasQuantity;

    /**
     * 携带的游戏货币
     */
    protected int                   money;

    /**
     * 等级
     */
    private short                   level;

    /**
     * 当前所在位置，值指地图格子坐标，从0开始
     */
    private short                   cellX, cellY,cellZ;

    /**
     * 基础攻击间隔时间，普通物理攻击(毫秒)，空手攻击时间或武器攻击时间
     */
    private int                     baseAttackImmobilityTime;

    /**
     * 实际攻击间隔时间，普通物理攻击(毫秒)
     */
    private int                     actualAttackImmobilityTime;

    /**
     * 攻击距离
     */
    private short                   attackRange;

    /**
     * 移动速度
     */
    protected byte                  moveSpeed            = MoveSpeed.GENERIC;
    
    /**
     * 移动状态
     */
    protected byte                  moveSpeedState       = MoveSpeed.SPEED_GENERIC_STATE;
    
    private ArrayList<Boolean>      isSlowSpeed          = new ArrayList<Boolean>();
    
    private ArrayList<Boolean>      isAddSpeed           = new ArrayList<Boolean>();
    
    private boolean                 isMount              = false;

    /**
     * 面向
     */
    private byte                    direction;

    /**
     * 所属阵营
     */
    private EClan                   clan;

    /**
     * 职业
     */
    private EVocation               vocation;

    /** ****************对象状态*************** */
    /**
     * 有效状态
     */
    protected boolean               enabled              = false;

    /**
     * 是否在战斗状态中
     */
    protected boolean               inFighting           = false;

    /**
     * 是否可被看见
     */
    protected boolean               visible              = true;

    /**
     * 是否能施放魔法技能
     */
    protected boolean               canReleaseMagicSkill = true;

    /**
     * 是否死亡
     */
    protected boolean               isDead               = false;

    /**
     * 昏迷的
     */
    protected boolean               insensible           = false;

    /**
     * 沉睡中
     */
    protected boolean               sleeping             = false;

    /**
     * 能否移动
     */
    protected boolean               moveable             = true;

    /**
     * 免疫攻击
     */
    protected boolean               immuneAttack         = false;

    /** ****************对象状态*************** */

    /**
     * 对象类型
     */
    protected EObjectType           objectType;

    /**
     * 对象等级
     */
    private EObjectLevel            objectLevel          = EObjectLevel.NORMAL;

    /**
     * 基础属性（由等级、职业、装备组成）
     */
    private ObjectProperty          baseObjectProperty;

    /**
     * 当前属性（包含基础属性、被动技能、BUFF、DEBUFF）
     */
    private ObjectProperty          actualObjectProperty;

    /**
     * 所在地图
     */
    private Map                     where;

    /**
     * 被攻击时抵抗几率列表
     */
    private ResistOddsList          resistOddsList;
    
    /**
     * 效果列表
     */
    public ArrayList<Effect>             effectList;

    /**
     * CD时间
     */
    public HashMap<Integer, CDUnit> userCDMap            = new HashMap<Integer, CDUnit>();

    /**
     * 构造
     */
    public ME2GameObject()
    {
        setID(IDManager.buildObjectID());
        baseObjectProperty = new ObjectProperty(this);
        actualObjectProperty = new ObjectProperty(this);
        resistOddsList = new ResistOddsList();
    }

    public String getName ()
    {
        return this.name;
    }

    public void setID (int _ID)
    {
        ID = _ID;
    }

    public int getID ()
    {
        return ID;
    }

    /**
     * @return 阵营
     */
    public EClan getClan ()
    {
        return clan;
    }

    /**
     * @param country The country to set.
     */
    public void setClan (EClan _clan)
    {
        clan = _clan;
    }

    /**
     * @param nickName The nickName to set.
     */
    public void setName (String _name)
    {
        name = _name;
    }

    /**
     * @return Returns the x.
     */
    public short getCellX ()
    {
        return cellX;
    }

    /**
     * @param x The x to set.
     */
    public void setCellX (int x)
    {
        cellX = (short) x;
    }

    /**
     * @return Returns the y.
     */
    public short getCellY ()
    {
        return cellY;
    }

    /**
     * @param y The y to set.
     */
    public void setCellY (short y)
    {
        cellY = y;
    }
    
    /**
     * @return Returns the z.
     */
    public short getCellZ ()
    {
        return cellZ;
    }

    /**
     * @param z The z to set.
     */
    public void setCellZ (short z)
    {
        cellZ = z;
    }

    public short getLevel ()
    {
        return level;
    }

    public Map where ()
    {
        return where;
    }

    /**
     * 居住于
     * 
     * @param _player
     */
    public void live (Map _map)
    {
        where = _map;
    }

    /**
     * @param needLevel The level to set.
     */
    public void setLevel (int _level)
    {
        level = 0;
        addLevel(_level);
    }

    public void addLevel (int _level)
    {
        // needRefreshClient = true;
        int newLevel = level;
        newLevel += _level;
        if (newLevel > Constant.INTEGER_MAX_VALUE)
        {
            newLevel = Constant.INTEGER_MAX_VALUE;
        }
        level = (short) newLevel;
    }

    public void informBorn ()
    {
        // SendBornInfo2Other.send(this);
        // SendBornInfo2Other.send(this);
    }

    public void informDead ()
    {
        // SendDeadInfo2Other.send(this);
    }

    /**
     * @return Returns the direction.
     */
    public byte getDirection ()
    {
        return direction;
    }

    /**
     * @param direction The direction to set.
     */
    public void setDirection (byte _direction)
    {
        direction = _direction;
    }

    /**
     * 向某方向行走1格
     * 
     * @param _direction 方向
     */
    public void go (byte _direction)
    {
        switch (_direction)
        {
            case Direction.UP:
            {
                cellY--;
                cellZ--;
                setDirection(_direction);
                
                break;
            }
            case Direction.DOWN:
            {
                cellY++;
                cellZ++;
                setDirection(_direction);

                break;
            }
            case Direction.LEFT:
            {
                cellX--;
                setDirection(_direction);

                break;
            }
            case Direction.RIGHT:
            {
                cellX++;
                setDirection(_direction);

                break;
            }
        }
    }

    /**
     * @return Returns 金钱.
     */
    public int getMoney ()
    {
        return money;
    }

    /**
     * @param money The 金钱 to set.
     */
    public void setMoney (int _money)
    {
        if (_money > 0)
        {
            money = _money;
        }
    }

    /**
     * 获取职业
     * 
     * @return
     */
    public EVocation getVocation ()
    {
        return vocation;
    }

    /**
     * 设置职业
     * 
     * @param _v
     */
    public void setVocation (EVocation _v)
    {
        vocation = _v;

        if (_v.getType() == EVocationType.PHYSICS)
        {
            balanceForceAndGas();
        }
    }

    /**
     * 设置攻击距离
     * 
     * @param _atkRange
     */
    public void setAttackRange (short _atkRange)
    {
        attackRange = _atkRange;
    }

    /**
     * 获取攻击距离
     * 
     * @return
     */
    public short getAttackRange ()
    {
        return attackRange;
    }

    /**
     * 获取基础攻击间隔时间
     * 
     * @return
     */
    public int getBaseAttackImmobilityTime ()
    {
        return baseAttackImmobilityTime;
    }

    /**
     * 设置基础攻击间隔时间
     * 
     * @param _immobilityTime 间隔时间（毫秒）
     */
    public void setBaseAttackImmobilityTime (int _immobilityTime)
    {
        baseAttackImmobilityTime = _immobilityTime;
    }

    /**
     * 获取实际攻击间隔时间
     * 
     * @return
     */
    public int getActualAttackImmobilityTime ()
    {
        return actualAttackImmobilityTime;
    }

    /**
     * 设置实际攻击间隔时间
     * 
     * @param _immobilityTime 间隔时间（毫秒）
     */
    public void setActualAttackImmobilityTime (int _immobilityTime)
    {
        actualAttackImmobilityTime = _immobilityTime;
    }

    /**
     * 增加实际攻击间隔时间
     * 
     * @param _immobilityTime 间隔时间（毫秒）
     */
    public void addActualAttackImmobilityTime (int _immobilityTime)
    {
        actualAttackImmobilityTime += _immobilityTime;
    }
    
    /**
     * 新方法,暂时不用
     */
    public void initMoveSpeed ()
    {
    	for (Effect effect : effectList) {
			if(effect != null && effect instanceof StaticEffect) {
				StaticEffect sEffect = (StaticEffect)effect;
				if(sEffect.specialStatus == ESpecialStatus.MOVE_FAST 
						&& sEffect.feature == EffectFeature.MOUNT) {
					this.isAddSpeed.add(true);
				} else if (sEffect.specialStatus == ESpecialStatus.MOVE_SLOWLY) {
					this.isSlowSpeed.add(true);
				}
			}
		}
    	this.changeMoveSpeedState();
    }

    /**
     * 设置移动速度
     * 
     * @param _speed
     */
    public void setMoveSpeed (byte _speed)
    {
        moveSpeed = _speed;
    }

    /**
     * 改变移动速度
     * 
     * @param _value
     */
    public void changeMoveSpeed (int _value)
    {
        moveSpeed += _value;

        if (moveSpeed <= 0)
        {
            moveSpeed = 1;
        }
    }
    /**
     * 设置骑马状态
     * @param _state
     */
    public void setMount (boolean _state)
    {
        isMount = _state;
        this.changeMoveSpeedState();
    }
    
    public boolean getMount ()
    {
        this.changeMoveSpeedState();
        return isMount;
    }
    /**
     * 设置加速BUFF状态
     * @param _state
     */
    public void addAddSpeedState (boolean _state)
    {
    	isAddSpeed.add(_state);
        this.changeMoveSpeedState();
    }
    
    /**
     * 移除加速状态
     */
    public void removeAddSpeedState ()
    {
    	if(isAddSpeed.size() > 0)
    	{
    		isAddSpeed.remove(0);
    	}
        this.changeMoveSpeedState();
    }
    
    /**
     * 设置减速BUFF状态
     * @param _state
     */
    public void addSlowSpeedState (boolean _state)
    {
    	isSlowSpeed.add(_state);
        this.changeMoveSpeedState();
    }
    /**
     * 移除减速状态
     */
    public void removeSlowSpeedState ()
    {
    	if(isSlowSpeed.size() > 0)
    	{
    		isSlowSpeed.remove(0);
    	}
        this.changeMoveSpeedState();
    }
    
    private void changeMoveSpeedState() {
    	if(isMount && isSlowSpeed.size() < 0) {
    		//骑马,并且没有被减速DEBUFF影响 那么就以坐骑速度前行.
    		moveSpeedState = MoveSpeed.SPEED_ADD_STATE;
    		log.info("!!!!!!!!!!!!!!!!!!!!!!!骑马中,没有被减速");
    	} else if (isAddSpeed.size() > 0 && isSlowSpeed.size() > 0) {
			//同时受增速和减速影响 ( 在马上也按正常步行速度前进
    		moveSpeedState = MoveSpeed.SPEED_GENERIC_STATE;
    		log.info("!!!!!!!!!!!!!!!!!!!!!!!被减速,也被加速");
		} else if (isAddSpeed.size() > 0 && isSlowSpeed.size() == 0) {
			//仅仅受到加速影响
			moveSpeedState = MoveSpeed.SPEED_ADD_STATE;
			log.info("!!!!!!!!!!!!!!!!!!!!!!!被加速");
		} else if (isSlowSpeed.size() > 0 && isAddSpeed.size() == 0) {
			//仅仅受到减速影响
			moveSpeedState = MoveSpeed.SPEED_LESSEN_STATE;
			log.info("!!!!!!!!!!!!!!!!!!!!!!!被减速");
		} else {
			//没有受到任何效果影响
			moveSpeedState = MoveSpeed.SPEED_GENERIC_STATE;
			log.info("!!!!!!!!!!!!!!!!!!!!!!!速度正常");
		}
    }
    
    /**
     * 获取移动状态
     * 
     * @return
     */
    public byte getMoveSpeedState ()
    {
        return moveSpeedState;
    }

    /**
     * 获取移动
     * 
     * @return
     */
    public byte getMoveSpeed ()
    {
        return moveSpeed;
    }

    /**
     * 获取抵抗几率列表
     * 
     * @return
     */
    public ResistOddsList getResistOddsList ()
    {
        return resistOddsList;
    }

    /**
     * 获取生命值
     * 
     * @return
     */
    public int getHp ()
    {
        if (hp < 0)
        {
            return 0;
        }

        return hp;
    }

    /**
     * 设置生命值
     * 
     * @param _hp
     */
    public void setHp (int _hp)
    {
        hp = _hp;
    }

    /**
     * 添加生命值
     * 
     * @param _hp
     */
    public void addHp (int _hp)
    {
    	//add by zhengl; date: 2011-02-15; note: 受到伤害后的伤害吸收暂时放在这.
    	if (this instanceof HeroPlayer && _hp < 0) {
			HeroPlayer player = (HeroPlayer) this;
			for (Effect effect : player.effectList) {
				if ( effect != null && effect instanceof StaticEffect ) {
					StaticEffect sef = (StaticEffect)effect;
					if(sef.isReduceAllHarm) {
						_hp = 0;
					} else if (sef.traceReduceHarmValue > 0) {
						int value = _hp + sef.traceReduceHarmValue;
						if(value > 0) {
							sef.traceReduceHarmValue = value;
							_hp = 0;
						} else {
							log.info("!!!!!!!!!!!!!!!!吸收伤害后盾破裂:" + sef.traceReduceHarmValue);
							sef.traceReduceHarmValue = 0;
							_hp = value;
							effect.setKeepTime((short)0);
						}
					}
				}
			}
		}
    	//end
        hp += _hp;

        if (hp > actualObjectProperty.getHpMax())
        {
            hp = actualObjectProperty.getHpMax();
        }
        else if (hp < 0)
        {
            hp = 0;
        }
    }

    /**
     * 返回当前生命值百分比（分子数值）
     * 
     * @return
     */
    public int getHPPercentElements ()
    {
        return getHp() * 100 / actualObjectProperty.getHpMax();
    }

    /**
     * 返回当前生命值百分比
     * 
     * @return
     */
    public float getHPPercent ()
    {
        return getHp() * 1F / actualObjectProperty.getHpMax();
    }

    /**
     * 获取魔法值
     * 
     * @return
     */
    public int getMp ()
    {
        if (mp < 0)
        {
            return 0;
        }

        return mp;
    }

    /**
     * 设置魔法值
     * 
     * @param _mp
     */
    public void setMp (int _mp)
    {
        mp = _mp;
    }

    /**
     * 添加魔法值
     * 
     * @param _mp
     */
    public void addMp (int _mp)
    {
        mp += _mp;

        if (mp > actualObjectProperty.getMpMax())
        {
            mp = actualObjectProperty.getMpMax();
        }
        else if (mp < 0)
        {
            mp = 0;
        }
    }

    /**
     * 返回当前魔法值百分比（分子数值）
     * 
     * @return
     */
    public int getMPPercentElements ()
    {
        return getMp() * 100 / actualObjectProperty.getMpMax();
    }

    /**
     * 返回当前魔法值百分比
     * 
     * @return
     */
    public float getMPPercent ()
    {
        return getMp() * 1F / actualObjectProperty.getMpMax();
    }

    /**
     * 根据职业初始化起始力槽和气槽
     */
    public void balanceForceAndGas ()
    {
        forceQuantity = 50;
        gasQuantity = 50;
    }

    /**
     * 获得力槽值
     * 
     * @return
     */
    public int getForceQuantity ()
    {
        return forceQuantity;
    }

    /**
     * 设置力槽值
     * 
     * @return
     */
    public void setForceQuantity (int _forceQuantity)
    {
        forceQuantity = _forceQuantity;
        gasQuantity = 100 - forceQuantity;
    }

    /**
     * 获得气槽值
     * 
     * @return
     */
    public int getGasQuantity ()
    {
        return gasQuantity;
    }

    /**
     * 设置气槽值
     * 
     * @return
     */
    public void setGasQuantity (int _gasQuantity)
    {
        gasQuantity = _gasQuantity;
        forceQuantity = 100 - gasQuantity;
    }

    /**
     * 消耗力槽值
     * 
     * @param _forceQuantityVal 需要消耗的力槽数值，可以为负值（增加力槽）
     * @return 是否有足够的力槽值
     */
    public boolean consumeForceQuantity (int _forceQuantityVal)
    {
        if (_forceQuantityVal <= forceQuantity)
        {
            forceQuantity -= _forceQuantityVal;

            if (forceQuantity > 100)
            {
                forceQuantity = 100;
            }

            if (forceQuantity < 0)
            {
                forceQuantity = 0;
            }

            gasQuantity = 100 - forceQuantity;

            return true;
        }

        return false;
    }

    /**
     * 消耗气槽值
     * 
     * @param _forceQuantityVal 需要消耗的气槽数值，可以为负值（增加气槽）
     * @return 是否有足够的气槽值
     */
    public boolean consumeGasQuantity (int _gasQuantityVal)
    {
        if (_gasQuantityVal <= gasQuantity)
        {
            gasQuantity -= _gasQuantityVal;

            if (gasQuantity > 100)
            {
                gasQuantity = 100;
            }

            if (gasQuantity < 0)
            {
                gasQuantity = 0;
            }

            forceQuantity = 100 - gasQuantity;

            return true;
        }

        return false;
    }

    /**
     * 获取对象类型
     * 
     * @return
     */
    public EObjectType getObjectType ()
    {
        return objectType;
    }

    /**
     * 获取对象等级
     * 
     * @return
     */
    public EObjectLevel getObjectLevel ()
    {
        return objectLevel;
    }

    /**
     * 设置对象等级
     * 
     * @param _objectLevel
     */
    public void setObjectLevel (EObjectLevel _objectLevel)
    {
        objectLevel = _objectLevel;
    }

    /**
     * 是否可被攻击
     * 
     * @param _object 攻击方
     * @return
     */
    public abstract boolean canBeAttackBy (ME2GameObject _object);

    /**
     * 获取当前对象属性清单
     * 
     * @return
     */
    public ObjectProperty getActualProperty ()
    {
        return actualObjectProperty;
    }

    /**
     * 获取基础对象属性清单
     * 
     * @return
     */
    public ObjectProperty getBaseProperty ()
    {
        return baseObjectProperty;
    }

    /**
     * 死亡
     * 
     * @param _killer 导致对象死亡的对象
     */
    public void die (ME2GameObject _killer)
    {
        inFighting = false;
        isDead = true;
        sleeping = false;
        insensible = false;
        moveable = true;
        //** 待完善 -- 将来考虑把这个过程放进其他地方:
        //** \src\effect\hero\effect\service\EffectServiceImpl.playerDie
        //add by zhengl ; date: 2010-12-27 ; note: 解决玩家死亡后中毒/DOT效果继续存在的BUG
        ArrayList<Effect> newList = new ArrayList<Effect>();
        log.info("对象死亡,身上有效果:" + effectList.size());
        for (int i = 0; i < effectList.size(); i++) {
        	Effect ef = effectList.get(i);
        	if(!ef.isClearAfterDie) {
        		newList.add(ef);
        	} else {
				//add by zhengl; date: 2011-03-21; note: 因BUFF变更引起的速度变更
				if(ef instanceof StaticEffect){
					EffectServiceImpl.getInstance().removeMove((StaticEffect)ef, this);
				}
			}
		}
        this.getActualProperty().clearNoneBaseProperty();
        effectList = newList;
        log.info("对象死亡,身上保留效果:" + effectList.size());
        //end
    }

    /**
     * 复活
     * 
     * @param _savior 复活对象的对象
     */
    public void revive (ME2GameObject _savior)
    {
        isDead = false;
    }

    /**
     * 是否死亡
     * 
     * @return
     */
    public boolean isDead ()
    {
        return isDead;
    }

    /**
     * 激活对象
     */
    public void active ()
    {
        enabled = true;
    }

    /**
     * 对象失效，不发出任何动作、不接受任何消息
     */
    public void invalid ()
    {
        enabled = false;
    }

    /**
     * 对象是否激活
     * 
     * @return
     */
    public boolean isEnable ()
    {
        return enabled;
    }

    /**
     * 进入战斗
     */
    public void enterFight ()
    {
        inFighting = true;
    }

    /**
     * 脱离战斗
     */
    public void disengageFight ()
    {
        inFighting = false;
    }

    /**
     * 是否在战斗中
     * 
     * @return
     */
    public boolean isInFighting ()
    {
        return inFighting;
    }

    /**
     * 隐身
     */
    public void disappear ()
    {
        visible = false;
    }

    /**
     * 由隐身现身
     */
    public void emerge ()
    {
        visible = true;
    }

    /**
     * 是否可见
     * 
     * @return
     */
    public boolean isVisible ()
    {
        return visible;
    }

    /**
     * 是否能施放魔法技能
     * 
     * @return
     */
    public boolean canReleaseMagicSkill ()
    {
        return canReleaseMagicSkill && !sleeping && !insensible;
    }

    /**
     * 禁止施放魔法技能
     */
    public void forbidReleaseMagicSkill ()
    {
        canReleaseMagicSkill = false;
    }

    /**
     * 解除魔法施放限制
     */
    public void relieveMagicSkillLimit ()
    {
        canReleaseMagicSkill = true;
    }

    /**
     * 使昏迷
     */
    public void beInComa ()
    {
        insensible = true;
    }

    /**
     * 是否昏迷中
     * 
     * @return
     */
    public boolean isInsensible ()
    {
        return insensible;
    }

    /**
     * 解除昏迷
     */
    public void relieveComa ()
    {
        insensible = false;

    }

    /**
     * 沉睡
     */
    public void sleep ()
    {
        sleeping = true;
    }

    /**
     * 是否在沉睡中
     * 
     * @return
     */
    public boolean isSleeping ()
    {
        return sleeping;
    }

    /**
     * 醒来，相对于沉睡
     */
    public void wakeUp ()
    {
        sleeping = false;
    }

    /**
     * 能否移动
     * 
     * @return false=不能移动; true=可以移动
     */
    public boolean moveable ()
    {
        return moveable && !sleeping && !insensible;
    }

    /**
     * 定身
     */
    public void fixBody ()
    {
        moveable = false;
    }

    /**
     * 解除不能移动限制
     */
    public void relieveFixBodyLimit ()
    {
        moveable = true;
    }

    /**
     * 进入免疫攻击状态
     */
    public void enterImmuneAttackStatus ()
    {
        immuneAttack = true;
    }

    /**
     * 解除免疫状态
     */
    public void relieveImmuneAttackStatus ()
    {
        immuneAttack = false;
    }

    /**
     * 是否免疫攻击
     * 
     * @return
     */
    public boolean isImmuneAttack ()
    {
        return immuneAttack;
    }

    /**
     * 发生战斗，被攻击或攻击其他对象，用于怪物
     */
    public abstract void happenFight ();

    /**
     * 获取默认速度
     * 
     * @return
     */
    public abstract byte getDefaultSpeed ();
}
