package hero.fight.service;

import java.util.ArrayList;
import java.util.Random;

import hero.group.service.GroupServiceImpl;
import hero.player.service.PlayerDAO;
import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;

import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ETouchType;
import hero.skill.service.SkillServiceImpl;
import hero.npc.Monster;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.duel.service.DuelServiceImpl;
import hero.share.EMagic;
import hero.share.ME2GameObject;
import hero.share.EObjectType;
import hero.effect.Effect;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.DynamicEffect;
import hero.effect.detail.StaticEffect;
import hero.effect.message.RemoveEffectNotify;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.fight.broadcast.HpChangeBroadcast;
import hero.fight.message.AttackMissNotify;
import hero.fight.message.GenericAttackViewNotify;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.MpRefreshNotify;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.Weapon.EWeaponType;
import hero.item.enhance.EnhanceService;
import hero.item.service.GoodsServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 FightServiceImp.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 上午11:41:36
 * @描述 ：战斗服务类，提供与战斗相关的接口
 */

public class FightServiceImpl extends AbsServiceAdaptor<FightConfig>
{
	private static Logger log = Logger.getLogger(FightServiceImpl.class);
    /**
     * 随机数生成器
     */
    private static final Random     RANDOM = new Random();

    /**
     * 单例
     */
    private static FightServiceImpl instance;

    /**
     * 私有构造
     */
    private FightServiceImpl()
    {
        config = new FightConfig();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static FightServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new FightServiceImpl();
        }
        return instance;
    }

    @Override
    protected void start ()
    {

    }

    /**
     * 处理普通物理攻击
     * 
     * @param _attacker 攻击者
     * @param _target 被攻击者
     */
    public void processPhysicsAttack (ME2GameObject _attacker,
            ME2GameObject _target)
    {
        refreshFightTime(_attacker, _target);

        if (!_attacker.isVisible()
                && _attacker.getObjectType() == EObjectType.PLAYER)
        {
            EffectServiceImpl.getInstance().clearHideEffect(_target);
        }

        processGenericPhysicsAttackView(_attacker, _target);

        float attackerPhysicsHitOdds = CEService.attackPhysicsHitOdds(_attacker
                .getActualProperty().getLucky(), _attacker.getActualProperty()
                .getHitLevel(), _attacker.getLevel(), _target.getLevel());

        float targetPhysicsDuckOdds = CEService.attackPhysicsDuckOdds(_attacker
                .getLevel(), _target.getActualProperty().getAgility(), _target
                .getActualProperty().getLucky(), _target.getActualProperty()
                .getPhysicsDuckLevel(), _target.getLevel());
        				
        int value = 0;

        if (RANDOM.nextInt(100) <= (attackerPhysicsHitOdds - targetPhysicsDuckOdds))
        {
            value = CEService.physicsHarm(_attacker.getLevel(), _attacker
                    .getActualProperty().getActualPhysicsAttack(), _target
                    .getLevel(), _target.getActualProperty().getDefense());
//            log.info("attacker name:"+_attacker.getName()+",_target id="+_target.getID()+",physicsHarm="+value);
            boolean isDeathblow = false;

            float physicsDeathblowOdds = CEService.attackPhysicsDeathblowOdds(
                    _attacker.getActualProperty().getAgility(), _attacker
                            .getActualProperty().getPhysicsDeathblowLevel(),
                    _attacker.getLevel(), _target.getLevel());

            if (RANDOM.nextInt(100) <= (physicsDeathblowOdds - _target
                    .getResistOddsList().physicsDeathblowOdds))
            {
                value = CEService.calculateDeathblowHarm(value, _attacker
                        .getActualProperty().getLucky());
                isDeathblow = true;
//                log.info("attacker name:"+_attacker.getName()+",_target id="+_target.getID()+",calculateDeathblowHarm="+value);
            }

            processReduceHp(_attacker, _target, value, true, isDeathblow, null);
            GoodsServiceImpl.getInstance()
                    .processEquipmentDurabilityInFighting(_attacker, _target);

            if (_attacker.getObjectType() == EObjectType.PLAYER)
            {
                //add by zhengl ; date: 2010-12-05 ; note:添加物理攻击暴击触发的持续效果
            	if (isDeathblow) 
            	{
                    EffectServiceImpl.getInstance().checkTouchEffect(_attacker, 
                    		_target, ETouchType.TOUCH_DEATHBLOW, false);
                    SkillServiceImpl.getInstance().checkTouchSkill(
                    		_attacker, _target, ETouchType.TOUCH_DEATHBLOW, false);
				}
            	//物理攻击触发
                EffectServiceImpl.getInstance().checkTouchEffect(_attacker, 
                		_target, ETouchType.ATTACK_BY_PHYSICS, false);
                SkillServiceImpl.getInstance().checkTouchSkill(
                		_attacker, _target, ETouchType.ATTACK_BY_PHYSICS, false);
                
                //因普通物理攻击引发的触发效果 (近远效果)
                EffectServiceImpl.getInstance().checkTouchEffect(
                                _attacker, _target,
                                ((HeroPlayer) _attacker).isRemotePhysicsAttack 
                                		? ETouchType.ATTACK_BY_DISTANCE_PHYSICS
                                        : ETouchType.ATTACK_BY_NEAR_PHYSICS,
                                false);
                SkillServiceImpl.getInstance().checkTouchSkill(
                                _attacker, _target,
                                ((HeroPlayer) _attacker).isRemotePhysicsAttack 
                                		? ETouchType.ATTACK_BY_DISTANCE_PHYSICS
                                        : ETouchType.ATTACK_BY_NEAR_PHYSICS,
                                false);

                if (_target.getObjectType() == EObjectType.PLAYER)
                {
                	if (isDeathblow) 
                	{
                        EffectServiceImpl.getInstance().checkTouchEffect(
                        		_target, _attacker, ETouchType.BE_DEATHBLOW_BY_PHYSICS, false);
                        SkillServiceImpl.getInstance().checkTouchSkill(
                        		_target, _attacker, ETouchType.BE_DEATHBLOW_BY_PHYSICS, false);
    				}
                    EffectServiceImpl.getInstance().checkTouchEffect(
                                    _target, _attacker,
                                    ((HeroPlayer) _attacker).isRemotePhysicsAttack 
                                    		? ETouchType.BE_ATTACKED_BY_DISTANCE_PHYSICS
                                            : ETouchType.BE_ATTACKED_BY_NEAR_PHYSICS,
                                    false);
                    SkillServiceImpl.getInstance().checkTouchSkill(
                                    _target, _attacker,
                                    ((HeroPlayer) _attacker).isRemotePhysicsAttack 
                                    		? ETouchType.ATTACK_BY_DISTANCE_PHYSICS
                                            : ETouchType.ATTACK_BY_NEAR_PHYSICS,
                                    false);
                }
            }
        }
        else
        {
            processMiss(_attacker, _target);
        }

        if (_attacker.getObjectType() == EObjectType.PLAYER)
        {
            ((HeroPlayer) _attacker).lastAttackTime = System.currentTimeMillis();
        }
        if(_attacker.getObjectType() == EObjectType.PET){
        	((Pet) _attacker).lastAttackTime = System.currentTimeMillis();
        }
    }
    
    public short[] getFightTarget(EWeaponType _eType)
    {
    	short[] result = new short[2];
    	result[0] = -1;
    	result[1] = -1;
    	if(EWeaponType.TTYPE_ZHANG == _eType) {
//    		log.info("持法杖攻击");
    		result = config.staff_attack_target_animation;
    	} else if (EWeaponType.TYPE_BISHOU == _eType) {
//    		log.info("持匕首攻击");
    		result = config.dagger_attack_target_animation;
		} else if (EWeaponType.TYPE_CHUI == _eType) {
//			log.info("持铁锤攻击");
			result = config.hammer_attack_target_animation;
		} else if (EWeaponType.TYPE_GONG == _eType) {
//			log.info("持长弓攻击");
			result = config.bow_attack_target_animation;
		} else if (EWeaponType.TYPE_JIAN == _eType) {
//			log.info("持利剑攻击");
			result = config.sword_attack_target_animation;
		}
    	return result;
    }

    /**
     * 处理普通攻击动作
     * 
     * @param _attacker 攻击者
     * @param _target 被攻击者
     */
    public void processGenericPhysicsAttackView (ME2GameObject _attacker,
            ME2GameObject _target)
    {
    	log.debug("processGenericPhysicsAttackView : attacker = " + _attacker.getObjectType() +" ,id="+ _attacker.getID()  
    			+ " , _target = " + _target.getObjectType().value()+" , _tartget id="+ _target.getID());
        AbsResponseMessage msg = new GenericAttackViewNotify(_attacker
                .getObjectType().value(), _attacker.getID(), _target
                .getObjectType().value(), _target.getID());

        if (EObjectType.MONSTER == _attacker.getObjectType()
                && EObjectType.PLAYER == _target.getObjectType())
        {
            ResponseMessageQueue.getInstance().put(
                    ((HeroPlayer) _target).getMsgQueueIndex(), msg);

            MapSynchronousInfoBroadcast.getInstance().put(
                    _attacker.where(),
                    new GenericAttackViewNotify(_attacker.getObjectType()
                            .value(), _attacker.getID(), _target
                            .getObjectType().value(), _target.getID()), true,
                    _target.getID());
        }
        else
        {
        	if(EObjectType.PLAYER == _attacker.getObjectType()) {
                ResponseMessageQueue.getInstance().put(((HeroPlayer) _attacker).getMsgQueueIndex(), 
                		new GenericAttackViewNotify(
                				_attacker.getObjectType().value(), 
                				_attacker.getID(), _target.getObjectType().value(), 
                				_target.getID(), _attacker));
        	}
            MapSynchronousInfoBroadcast.getInstance().put(
                    _attacker.where(),
                    new GenericAttackViewNotify(_attacker.getObjectType()
                            .value(), _attacker.getID(), _target
                            .getObjectType().value(), _target.getID(), _attacker), true,
                    _attacker.getID());
        }
    }

    public void processTargetDie (ME2GameObject _attacker, ME2GameObject _target)
    {
        if (_attacker.getObjectType() == EObjectType.PLAYER)
        {
            EnhanceService.getInstance().processWeaponEnhance(
                    (HeroPlayer) _attacker, _target);
        }

        _target.die(_attacker);
    }

    /**
     * 处理非自动生命值增加
     * 
     * @param _trigger 触发者
     * @param _target 目标
     * @param _value 增加的数值
     * @param _isDeathblow 是否爆击标记
     */
    public void processAddHp (ME2GameObject _trigger, ME2GameObject _target,
            int _value, boolean _visible, boolean _isDeathblow)
    {
        if (_value > 0)
        {
            int validHp = _target.getActualProperty().getHpMax()
                    - _target.getHp();

            if (validHp > 0)
            {
                validHp = (validHp >= _value) ? _value : validHp;
                _target.addHp(validHp);

                if (_target.getObjectType() == EObjectType.PLAYER)
                {
                    ((HeroPlayer) _target).beResumeHpByOthers(
                            (HeroPlayer) _trigger, validHp);
                }

                AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_target
                        .getObjectType().value(), _target.getID(), _target
                        .getHp(), _value, _visible, _isDeathblow);

                if (null != _trigger
                        && _trigger.getObjectType() == EObjectType.PLAYER)
                {
                    ResponseMessageQueue.getInstance().put(
                            ((HeroPlayer) _trigger).getMsgQueueIndex(),
                            refreshHpMsg);
                }

                if (_target != _trigger && null != _target
                        && _target.getObjectType() == EObjectType.PLAYER)
                {
                    ResponseMessageQueue.getInstance().put(
                            ((HeroPlayer) _target).getMsgQueueIndex(),
                            refreshHpMsg);
                }

                HpChangeBroadcast.put(_trigger, _target, _value);
            }
        }
    }
    
    /**
     * 从特效特效列表中寻找伤害加成
     * @param _effectList
     * @param _value
     * @param _accMagic
     * @return
     */
    private float changeValueByEffect(ArrayList<Effect> _effectList, float _value, EMagic _accMagic)
    {
    	float result = 0F;
        if (_effectList.size() > 0) 
        {
        	float multiplier = 0F;
        	Effect effect = null;
        	EMathCaluOperator operator = null;
			for (int i = 0; i < _effectList.size(); i++) 
			{
				effect = _effectList.get(i);
				if (effect instanceof StaticEffect) 
				{
					StaticEffect sEffect = (StaticEffect)effect;
					operator = sEffect.caluOperator;
					if (_accMagic != null) 
					{
						if (sEffect.magicHarmValueBeAttack > 0 
								&& (sEffect.magicHarmTypeBeAttack == EMagic.ALL 
										|| sEffect.magicHarmTypeBeAttack == _accMagic)) 
						{
							multiplier = sEffect.magicHarmValueBeAttack;
							log.info("攻击类型:" + _accMagic.getName() 
									+ " 伤害加成:" + String.valueOf(multiplier) 
									+ " 特效类型:" + sEffect.magicHarmTypeBeAttack.getName());
							result += (int)changeValue((float)_value, multiplier, operator);
						}
					} 
					else 
					{
						if (sEffect.bePhysicsHarmValue > 0 ) 
						{
							multiplier = sEffect.bePhysicsHarmValue;
							log.info("攻击类型:物理;伤害加成:" + String.valueOf(multiplier));
							result += (int)changeValue((float)_value, multiplier, operator);
						}
					}
				}
			}//end for
		}
        result += _value;
        
    	return result;
    }
    
	/**
	 * 改变的值
	 * 
	 * @param _baseValue
	 *            基础值
	 * @param _caluModulus
	 *            计算系数
	 * @param _operator
	 *            运算符号
	 * @return
	 */
	private float changeValue(float _baseValue, float _caluModulus, EMathCaluOperator _operator) 
	{
		float result = 0;
		if (_caluModulus > 0 && _operator != null) 
		{
			if (_caluModulus > 1) 
			{
				_caluModulus -= 1;
			}
			else 
			{
				//传入0.7的话是表示降低30%伤害,那么直接扣除30%伤害即可 所以会返回 负数的30%伤害
				_caluModulus = -(1 - _caluModulus);
			}
			switch (_operator) 
			{
				case ADD: 
				{
					result = _baseValue + _caluModulus;
					break;
				}
				case DEC: 
				{
					result = _baseValue - _caluModulus;
					break;
				}
				case MUL:
				{
					result = _baseValue * _caluModulus;
					break;
				}
				case DIV: 
				{
					result = _baseValue * ((_caluModulus - 1) / _caluModulus);
					break;
				}
			}
		}
		return result;
	}

    /**
     * 处理非自动生命值减少
     * 
     * @param _trigger 触发者
     * @param _target 目标
     * @param _value 减少的数值
     * @param _isDeathblow 是否爆击标记
     * @param _accMagic 是否魔法攻击(非魔法攻击_magic传入null即可)
     * @return 目标是否死亡（决斗失败）
     */
    public boolean processReduceHp (ME2GameObject _trigger,
            ME2GameObject _target, int _value, boolean _visible,
            boolean _isDeathblow, EMagic _accMagic)
    {
        if (_target.isDead())
        {
        	log.warn("target.isDead! hp porcess cancel");
        	return true;
        }
        if (_trigger.getObjectType() == EObjectType.PLAYER 
        		&& _target.getObjectType() == EObjectType.MONSTER)
        {
            ((Monster) _target).setAttackerAtFirst((HeroPlayer) _trigger);
        }
        
        if (_value >= 0)
        { 
            if (_target.getObjectType() == EObjectType.MONSTER
                    && _trigger.getObjectType() == EObjectType.PLAYER
                    && !_target.isDead())
            {

                ((Monster) _target).beHarmed((HeroPlayer) _trigger, _target
                        .getHp() >= _value ? _value : _target.getHp());
            }
            
            
        }
        
        if (_value > 0)
        {
            if (!_target.isVisible() && _target.getObjectType() == EObjectType.PLAYER)
            {
                EffectServiceImpl.getInstance().clearHideEffect(_target);
            }
//            if (_target.getObjectType() == EObjectType.PLAYER) 
//            {
//			}
            //add by zhengl; date: 2011-04-21; note: 从特效特效列表中寻找伤害加成
            _value = (int)changeValueByEffect(_target.effectList, _value, _accMagic);
            
            _value = (int)changeValueByEffect(_trigger.effectList, _value, _accMagic);

            _target.addHp(-_value);

            AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_target
                    .getObjectType().value(), _target.getID(), _target.getHp(),
                    -_value, _visible, _isDeathblow);

            if (null != _trigger
                    && _trigger.getObjectType() == EObjectType.PLAYER)
            {
                ResponseMessageQueue.getInstance().put(
                        ((HeroPlayer) _trigger).getMsgQueueIndex(),
                        refreshHpMsg);
            }

            if (_target != _trigger && null != _target
                    && _target.getObjectType() == EObjectType.PLAYER)
            {
                ResponseMessageQueue.getInstance()
                        .put(((HeroPlayer) _target).getMsgQueueIndex(),
                                refreshHpMsg);
            }

            HpChangeBroadcast.put(_trigger, _target, -_value);

            if(_trigger.getHp() == 0){
                if(_target instanceof HeroPlayer && _trigger instanceof  HeroPlayer){
                    HeroPlayer target = (HeroPlayer)_target;
                    HeroPlayer trigger = (HeroPlayer)_trigger;

                    if(target.getClan() != trigger.getClan()){
                        //保存敌对阵营PK玩家USERID
                        PlayerDAO.insertPvpInfo(target.getUserID(), target.getVocation().value(), trigger.getUserID(), trigger.getVocation().value());

                    }

                }
            }
            
            if (_target.getHp() == 0)
            {
                if(_target instanceof HeroPlayer && _trigger instanceof  HeroPlayer){
                    HeroPlayer target = (HeroPlayer)_target;
                    HeroPlayer trigger = (HeroPlayer)_trigger;

                    if(target.getClan() != trigger.getClan()){
                        //保存敌对阵营PK玩家USERID
                        PlayerDAO.insertPvpInfo(trigger.getUserID(), trigger.getVocation().value(), target.getUserID(), target.getVocation().value());

                    }

                }

                if (_target instanceof HeroPlayer
                        && _trigger instanceof HeroPlayer)
                {
                	HeroPlayer targetPlayer = (HeroPlayer)_target;
                    if (DuelServiceImpl.getInstance().isDueling(targetPlayer.getUserID(),
                    		((HeroPlayer)_trigger).getUserID()))
                    {
                    	//add by zhengl; date: 2011-03-10; note: 修改决斗胜利后残留在决斗目标身上的DEBUFF会导致玩家死亡的BUG
                        ArrayList<Effect> newList = new ArrayList<Effect>();
                        log.info("对象决斗失败,身上有效果:"+targetPlayer.effectList.size());
                        for (int i = 0; i < targetPlayer.effectList.size(); i++) {
                        	Effect ef = targetPlayer.effectList.get(i);
                        	//edit by zhengl; date: 2011-04-23; note: 只强行移除可以导致玩家死亡的DEBUFF
                        	if (ef instanceof DynamicEffect 
                        			&& ((DynamicEffect)ef).hpHarmTotal > 0) 
                        	{
        						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
    							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
    									false, 0);
							}
                        	else 
                        	{
                        		newList.add(ef);
							}
//                        	if(ef.trait == EffectTrait.BUFF) {
//                        		newList.add(ef);
//                        	} else {
//        						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
//    							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
//    									false, 0);
//							}
                        	//edit end
                		}
                        targetPlayer.effectList = newList;

                        log.info("对象决斗失败,身上保留效果:"+targetPlayer.effectList.size());
                        _target.setHp(1);
                        DuelServiceImpl.getInstance().wonDuel(
                                (HeroPlayer) _trigger);

                        return true;
                    } else if(targetPlayer.getClan() == _trigger.getClan()) {
                    	//add by zhengl; date: 2011-03-10; note: 为容错而添加,在少数情况下,结束决斗后仍可制造伤害的情况屏蔽掉
                        log.info("!!受到非决斗模式下的同阵营致命攻击伤害,身上有效果:"
                        		+targetPlayer.effectList.size());
                        ArrayList<Effect> newList = new ArrayList<Effect>();
                        for (int i = 0; i < targetPlayer.effectList.size(); i++) {
                        	Effect ef = targetPlayer.effectList.get(i);
                        	if (ef instanceof DynamicEffect 
                        			&& ((DynamicEffect)ef).hpHarmTotal > 0) 
                        	{
        						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
    							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
    									false, 0);
							}
                        	else 
                        	{
                        		newList.add(ef);
							}
//                        	if(ef.trait == EffectTrait.BUFF) {
//                        		newList.add(ef);
//                        	} else {
//        						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
//    							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
//    									false, 0);
//    						}
                		}
                        targetPlayer.effectList = newList;
                        log.info("!!受到非决斗模式下的同阵营致命攻击伤害,身上有效果:"
                        		+targetPlayer.effectList.size());
                        _target.setHp(1);
                        //需要在这里return,否则还是会处理为死亡
                        return true;
                    }
                }

                processTargetDie(_trigger, _target);
                
                return true;
            }
        }

        return false;
    }

    /**
     * 处理非直接技能引起的生命值变化(dot,hot)
     * 
     * @param _trigger 触发者
     * @param _target 目标
     * @param _value 生命值（大于0增加，小于0掉血）
     * @param _accMagic 攻击魔法类型(null为物理类型)
     * @param _isDeathblow 是否是暴击
     */
    public void processHpChange (ME2GameObject _trigger, ME2GameObject _target,
            int _value, boolean _isDeathblow, EMagic _accMagic)
    {
        if (_target.isDead())
            return;
        //add by zhengl; date: 2011-04-18; note: 自动掉血也会添加第1受益人.
        if (_trigger.getObjectType() == EObjectType.PLAYER 
        		&& _target.getObjectType() == EObjectType.MONSTER)
        {
            ((Monster) _target).setAttackerAtFirst((HeroPlayer) _trigger);
        }

        if (_trigger != null && _value < 0)
        {
            if (_target.getObjectType() == EObjectType.MONSTER
                    && _trigger.getObjectType() == EObjectType.PLAYER)
            {
                ((Monster) _target).beHarmed((HeroPlayer) _trigger, _target
                        .getHp() >= -_value ? -_value : _target.getHp());
            }
        }
        
        if (_target.getObjectType() == EObjectType.PLAYER && _value < 0) {
            //add by zhengl; date: 2011-04-22; note: 从特效特效列表中寻找伤害加成
            _value = (int)changeValueByEffect(_target.effectList, _value, _accMagic);
		}

        if (!_target.isVisible() && _value < 0)
        {
            EffectServiceImpl.getInstance().clearHideEffect(_target);
        }

        _target.addHp(_value);

        AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_target
                .getObjectType().value(), _target.getID(), _target.getHp(),
                _value, true, _isDeathblow);

        if (null != _trigger && _trigger.getObjectType() == EObjectType.PLAYER)
        {
            ResponseMessageQueue.getInstance().put(
                    ((HeroPlayer) _trigger).getMsgQueueIndex(), refreshHpMsg);
        }

        if (_target != _trigger && null != _target
                && _target.getObjectType() == EObjectType.PLAYER)
        {
            ResponseMessageQueue.getInstance().put(
                    ((HeroPlayer) _target).getMsgQueueIndex(), refreshHpMsg);
        }

        HpChangeBroadcast.put(_trigger, _target, _value);

        if(_trigger.getHp() == 0){
            if(_target instanceof HeroPlayer && _trigger instanceof  HeroPlayer){
                HeroPlayer target = (HeroPlayer)_target;
                HeroPlayer trigger = (HeroPlayer)_trigger;

                if(target.getClan() != trigger.getClan()){
                    //保存敌对阵营PK玩家USERID
                    PlayerDAO.insertPvpInfo(target.getUserID(), target.getVocation().value(), trigger.getUserID(), trigger.getVocation().value());

                }

            }
        }

        if (_target.getHp() == 0)
        {
            if(_target instanceof HeroPlayer && _trigger instanceof  HeroPlayer){
                HeroPlayer target = (HeroPlayer)_target;
                HeroPlayer trigger = (HeroPlayer)_trigger;

                if(target.getClan() != trigger.getClan()){
                    //保存敌对阵营PK玩家USERID
                    PlayerDAO.insertPvpInfo(trigger.getUserID(), trigger.getVocation().value(), target.getUserID(), target.getVocation().value());

                }

            }

            if (_target instanceof HeroPlayer && _trigger instanceof HeroPlayer)
            {
            	HeroPlayer targetPlayer = (HeroPlayer)_target;
                if (DuelServiceImpl.getInstance().isDueling(targetPlayer.getUserID(), 
                		((HeroPlayer)_trigger).getUserID()))
                {
                	//add by zhengl; date: 2011-03-10; note: 修改决斗胜利后残留在决斗目标身上的DEBUFF会导致玩家死亡的BUG
                    ArrayList<Effect> newList = new ArrayList<Effect>();
                    log.info("对象决斗失败,身上有效果:"+targetPlayer.effectList.size());
                    for (int i = 0; i < targetPlayer.effectList.size(); i++) {
                    	Effect ef = targetPlayer.effectList.get(i);
                    	//edit by zhengl; date: 2011-04-23; note: 只强行移除可以导致玩家死亡的DEBUFF
                    	if (ef instanceof DynamicEffect 
                    			&& ((DynamicEffect)ef).hpHarmTotal > 0) 
                    	{
    						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
									false, 0);
						}
                    	else 
                    	{
                    		newList.add(ef);
						}
//                    	if(ef.trait == EffectTrait.BUFF) {
//                    		newList.add(ef);
//                    	} else {
//    						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
//							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
//									false, 0);
//						}
            		}
                    targetPlayer.effectList = newList;
                    log.info("对象决斗失败,身上保留效果:"+targetPlayer.effectList.size());
                    _target.setHp(1);
                    DuelServiceImpl.getInstance()
                            .wonDuel((HeroPlayer) _trigger);

                    return;
                } else if(targetPlayer.getClan() == _trigger.getClan()) {
                	//add by zhengl; date: 2011-03-10; note: 为容错而添加,在少数情况下,结束决斗后仍可制造伤害的情况屏蔽掉
                    log.info("!!受到非决斗模式下的同阵营致命攻击伤害,身上有效果:"+targetPlayer.effectList.size());
                    ArrayList<Effect> newList = new ArrayList<Effect>();
                    for (int i = 0; i < targetPlayer.effectList.size(); i++) {
                    	Effect ef = targetPlayer.effectList.get(i);
                    	if (ef instanceof DynamicEffect 
                    			&& ((DynamicEffect)ef).hpHarmTotal > 0) 
                    	{
    						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
									false, 0);
						}
                    	else 
                    	{
                    		newList.add(ef);
						}
//                    	if(ef.trait == EffectTrait.BUFF) {
//                    		newList.add(ef);
//                    	} else {
//    						RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
//							MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg,
//									false, 0);
//						}
            		}
                    targetPlayer.effectList = newList;
                    log.info("!!受到非决斗模式下的同阵营致命攻击伤害,身上有效果:"+targetPlayer.effectList.size());
                    _target.setHp(1);
                    //需要在这里return,否则还是会处理为死亡
                    return;
                }
            }

            processTargetDie(_trigger, _target);
        }
    }

    /**
     * 处理攻击未命中
     * 
     * @param _trigger 触发者
     * @param _target 目标
     */
    public void processMiss (ME2GameObject _trigger, ME2GameObject _target)
    {
        if (_target.getObjectType() == EObjectType.MONSTER
                && _target.isEnable() && !_target.isDead() 
                && _trigger.getObjectType() == EObjectType.PLAYER)
        {
            ((Monster) _target).beHarmed((HeroPlayer) _trigger, 0);
        }

        AbsResponseMessage hpChangeViewMsg = new AttackMissNotify(_target
                .getObjectType().value(), _target.getID());

        if (null != _trigger && _trigger.getObjectType() == EObjectType.PLAYER)
        {
            ResponseMessageQueue.getInstance()
                    .put(((HeroPlayer) _trigger).getMsgQueueIndex(),
                            hpChangeViewMsg);
        }

        if (_target != _trigger && null != _target
                && _target.getObjectType() == EObjectType.PLAYER)
        {
            ResponseMessageQueue.getInstance().put(
                    ((HeroPlayer) _target).getMsgQueueIndex(), hpChangeViewMsg);
        }
        
        if(_target != _trigger && null != _target
                && _trigger.getObjectType() == EObjectType.PET){
        	Pet pet = (Pet)_trigger;
        	HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(pet.masterID);
        	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),hpChangeViewMsg);
        }
    }

    /**
     * 处理单目标MP变化
     * MP恢复也在这处理
     * 
     * @param _target 目标
     */
    public void processSingleTargetMpChange (ME2GameObject _target, boolean _visible)
    {
        AbsResponseMessage refreshMpMsg = new MpRefreshNotify(_target
                .getObjectType().value(), _target.getID(), _target.getMp(), _visible);

        if (null != _target && _target.getObjectType() == EObjectType.PLAYER)
        {
            ResponseMessageQueue.getInstance().put(
                    ((HeroPlayer) _target).getMsgQueueIndex(), refreshMpMsg);
        }

        MapSynchronousInfoBroadcast.getInstance().put(_target.where(),
                refreshMpMsg, true, _target.getID());
    }

    /**
     * 处理个人力值变化
     * 
     * @param _object 变化的对象
     */
    public void processPersionalForceQuantityChange (ME2GameObject _object)
    {
        AbsResponseMessage refreshMpMsg = new MpRefreshNotify(_object
                .getObjectType().value(), _object.getID(), _object
                .getForceQuantity(), false);

        if (null != _object && _object.getObjectType() == EObjectType.PLAYER)
        {
            ResponseMessageQueue.getInstance().put(
                    ((HeroPlayer) _object).getMsgQueueIndex(), refreshMpMsg);
        }

        MapSynchronousInfoBroadcast.getInstance().put(_object.where(),
                refreshMpMsg, true, _object.getID());
    }

    /**
     * 处理个人生命值变化
     * 
     * @param _object 变化的对象
     */
    public void processPersionalHpChange (ME2GameObject _object, int _value)
    {
        AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_object
                .getObjectType().value(), _object.getID(), _object.getHp(),
                _value, false, false);

        if (null != _object && _object.getObjectType() == EObjectType.PLAYER)
        {
            ResponseMessageQueue.getInstance().put(
                    ((HeroPlayer) _object).getMsgQueueIndex(), refreshHpMsg);

            GroupServiceImpl.getInstance().groupMemberListHpMpNotify((HeroPlayer) _object);
        }

        MapSynchronousInfoBroadcast.getInstance().put(_object.where(),
                refreshHpMsg, true, _object.getID());
    }

    /**
     * 更新战斗时间
     * 
     * @param _attacker 一方
     * @param _target 另一方
     */
    public void refreshFightTime (ME2GameObject _attacker, ME2GameObject _target)
    {
        if (_attacker.getObjectType() == EObjectType.MONSTER)
        {
            _attacker.happenFight();
        }
        else
        {
            if (_target.getObjectType() == EObjectType.PLAYER)
            {
                ((HeroPlayer) _attacker)
                        .refreshPvPFightTime(((HeroPlayer) _target).getUserID());
                ((HeroPlayer) _target)
                        .refreshPvPFightTime(((HeroPlayer) _attacker)
                                .getUserID());
            }
            else
            {
                _target.happenFight();
            }
        }
    }
}
