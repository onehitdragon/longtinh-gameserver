package hero.effect.service;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javolution.util.FastList;
import hero.effect.Effect;
import hero.effect.Effect.AureoleRadiationRange;
import hero.effect.Effect.EKeepTimeType;
import hero.effect.Effect.EffectFeature;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.DynamicEffect;
import hero.effect.detail.StaticEffect;
import hero.effect.detail.TouchEffect;
import hero.effect.dictionry.EffectDictionary;
import hero.effect.message.AddEffectNotify;
import hero.effect.message.MoveSpeedChangerNotify;
import hero.effect.message.RefreshEffectNotify;
import hero.effect.message.RemoveEffectNotify;
import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.fight.service.FightServiceImpl;
import hero.fight.service.SpecialStatusDefine;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.service.MapServiceImpl;
import hero.skill.ActiveSkill;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.skill.dict.SkillUnitDict;
import hero.skill.unit.ActiveSkillUnit;
import hero.npc.Monster;
import hero.npc.detail.EMonsterLevel;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.EMagic;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.MagicHarmList;
import hero.share.MoveSpeed;
import hero.share.message.RefreshObjectViewValue;
import hero.share.message.Warning;
import hero.share.service.ME2ObjectList;
import hero.share.service.Tip;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 N_EffectServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-13 下午05:54:55
 * @描述 ：
 */

public class EffectServiceImpl extends AbsServiceAdaptor<EffectConfig> {
	private static Logger log = Logger.getLogger(EffectServiceImpl.class);
	/**
	 * 单例
	 */
	private static EffectServiceImpl instance;

	/**
	 * 存在效果的怪物列表
	 */
	private FastList<Monster> existsEffectMonsterList;

	/**
	 * 怪物效果计时器
	 */
	private Timer timer;

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static EffectServiceImpl getInstance() {
		if (null == instance) {
			instance = new EffectServiceImpl();
		}

		return instance;
	}

	/**
	 * 私有构造
	 */
	private EffectServiceImpl() {
		config = new EffectConfig();
		existsEffectMonsterList = new FastList<Monster>();
		timer = new Timer();
		timer.schedule(new MonsterEffectCheckTask(), 10000, 3000);
	}

	@Override
	protected void start() {
		EffectDictionary.getInstance().load(config);
	}

	@Override
	public void createSession(Session _session) {

	}

	@Override
	public void sessionFree(Session _session) {

	}

	/**
	 * 计算静态效果对玩家的属性的修改
	 * 
	 * @param _player
	 */
	public void staticEffectAction(HeroPlayer _player) {
		Effect effect;

		for (int i = 0; i < _player.effectList.size(); i++) {
			effect = _player.effectList.get(i);

			if (effect instanceof StaticEffect) {
				changePropertyValue(_player, (StaticEffect) effect, true);
			}
		}
	}

	/**
	 * 进入地图之后交换各自身上BUFF信息
	 * 
	 * @param _player
	 */
	public void sendEffectList(HeroPlayer _player, Map _where) {
		if (_where == null) {
			return;
		}
		ME2ObjectList monsterList = _where.getMonsterList();
		ME2ObjectList otherPlayerList = _where.getPlayerList();
		ME2GameObject _target = null;
		Effect ef = null;
		// 把自己的BUFF告诉大家.
		for (int j = 0; j < _player.effectList.size(); j++) {
			ef = _player.effectList.get(j);
			AddEffectNotify msg = new AddEffectNotify(_player, ef);
			MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, false, 0);
			if (ef != null && ef instanceof StaticEffect) {
				notifyMoveToOther((StaticEffect) _player.effectList.get(j), _player);
			}
		}

		// 把别人的BUFF收录下来
		for (int i = 0; i < otherPlayerList.size(); i++) {
			_target = otherPlayerList.get(i);
			for (Effect effect : _target.effectList) {
				if (effect == null) {
					continue;
				}
				AddEffectNotify msg = new AddEffectNotify(_target, effect);
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
				if (effect instanceof StaticEffect) {
					notifyMoveToPlayer((StaticEffect) effect, _player, _target);
				}
			}
		}
		// 把怪物身上的BUFF记录下来
		for (int i = 0; i < monsterList.size(); i++) {
			_target = monsterList.get(i);
			for (Effect effect : _target.effectList) {
				if (effect == null) {
					continue;
				}
				AddEffectNotify msg = new AddEffectNotify(_target, effect);
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
			}
		}

	}

	/**
	 * 玩家死亡，移除所有死亡后消失的效果，包括光环
	 * 
	 * @param _player
	 */
	public void playerDie(HeroPlayer _player) {

	}

	/**
	 * 移除决斗双方产生的效果
	 * 
	 * @param _one
	 * @param _other
	 */
	public void removeDuelEffect(HeroPlayer _one, HeroPlayer _other) {

	}

	/**
	 * 主动下马.
	 * 
	 * @param _player
	 */
	public void downMountEffect(ME2GameObject _player) {
		ArrayList<Effect> targetEffectList = _player.effectList;
		synchronized (targetEffectList) {
			Effect existsEffect = null;
			for (int i = 0; i < targetEffectList.size(); i++) {
				existsEffect = targetEffectList.get(i);
				if (existsEffect.feature == EffectFeature.MOUNT) {
					removeEffect(_player, existsEffect, false, false);
					break;
				}
			}// for end
		}
	}

	/**
	 * 
	 * @param _player
	 */
	public boolean haveAlikeMount(ME2GameObject _player, Effect _effect) {
		boolean result = false;
		ArrayList<Effect> targetEffectList = _player.effectList;
		synchronized (targetEffectList) {
			Effect existsEffect = null;
			for (int i = 0; i < targetEffectList.size(); i++) {
				existsEffect = targetEffectList.get(i);
				if (existsEffect.ID == _effect.ID) {
					result = true;
					break;
				}
			}// for end
		}
		return result;
	}

	/**
	 * 技能产生的效果
	 * **edit by zhengl; date: 2011-05-14; note: 修改了不同效果同时作用时候技能叠加的BUG
	 * 
	 * @param _src
	 *            技能施放者
	 * @param _dest
	 *            施放目标
	 * @param _effectModel
	 *            效果模板
	 */
	public void appendSkillEffect(ME2GameObject _skillReleaser, ME2GameObject _target,
			Effect _effectModel) {
		if (null == _target) {
			ResponseMessageQueue.getInstance().put(((HeroPlayer) _skillReleaser).getMsgQueueIndex(),
					new Warning(Tip.TIP_EFFECT_OF_VALIDATE_TARGET));

			return;
		}

		ArrayList<Effect> targetEffectList = _target.effectList;

		synchronized (targetEffectList) {
			if (targetEffectList.size() > 0) {
				Effect existsEffect;
				//第1个for,检测是否重复效果
				for (int i = 0; i < targetEffectList.size(); i++) 
				{
					if (_effectModel.keepTimeType == EKeepTimeType.N_A) {
						//光环不进行检测.
						continue;
					}
					existsEffect = targetEffectList.get(i);
					if (existsEffect.releaser == _skillReleaser) {
						if (existsEffect.name.equals(_effectModel.name)) {
							if (existsEffect.ID == _effectModel.ID) {
								// 相同效果
								existsEffect.resetTraceTime();

								if (existsEffect.totalTimes > 1) {
									existsEffect.addCurrentCountTimes(_target);
								}

								RefreshEffectNotify msg = new RefreshEffectNotify(existsEffect,
										existsEffect.releaser.getID());

								if (_target instanceof HeroPlayer) {
									ResponseMessageQueue.getInstance().put(
											((HeroPlayer) _target).getMsgQueueIndex(), msg);

									MapSynchronousInfoBroadcast.getInstance().put(_target.where(),
											msg, true, _target.getID());
								} else {
									MapSynchronousInfoBroadcast.getInstance().put(_target.where(),
											msg, false, 0);
								}

								log.info("受到相同的效果,重置效果持续时间.");
								return;
							}
						}
					} else {
						if (existsEffect.name.equals(_effectModel.name)) {
							if (existsEffect.level == _effectModel.level) {
								// 相同效果
								existsEffect.resetTraceTime();
								existsEffect.releaser = _skillReleaser;

								RefreshEffectNotify msg = new RefreshEffectNotify(existsEffect,
										existsEffect.releaser.getID());

								if (_target instanceof HeroPlayer) {
									ResponseMessageQueue.getInstance().put(
											((HeroPlayer) _target).getMsgQueueIndex(), msg);

									MapSynchronousInfoBroadcast.getInstance().put(_target.where(),
											msg, true, _target.getID());
								} else {
									MapSynchronousInfoBroadcast.getInstance().put(_target.where(),
											msg, false, 0);
								}
								log.info("受到相同的效果,重置效果持续时间.");
								return;
							}
						}
					}
				} // for end

				for (int i = 0; i < targetEffectList.size(); i++) 
				{
					existsEffect = targetEffectList.get(i);
					if (_effectModel.keepTimeType == existsEffect.keepTimeType
							&& _effectModel.trait == existsEffect.trait) 
					{
						//1,排重之后处理光环效果
						if (_effectModel.keepTimeType == EKeepTimeType.N_A) {
							if (existsEffect.releaser == _skillReleaser) {
								// 1个人只能施展一种光环
								removeEffect(_skillReleaser, existsEffect, false, true);
								// if(_effectModel.feature !=
								// EffectFeature.MOUNT) {
								//
								// }
								/**
								 * 光环的定义是同一个光环释放即会生效,再次释放就会取消.
								 */
								if (existsEffect.ID == _effectModel.ID) {
									return;
								}
								break;
							}
						}
						//2,处理非光环效果
						if (existsEffect.name.equals(_effectModel.name) && 
								_effectModel.level >= existsEffect.level) 
						{
							// 移除原有低等级效果，用更高等级的效果代替
							removeEffect(_target, existsEffect, false, true);
							break;
						} 
						else if(existsEffect.name.equals(_effectModel.name)) 
						{
							//提示有更高级效果
							if (_skillReleaser instanceof HeroPlayer) {
								ResponseMessageQueue.getInstance().put(
										((HeroPlayer) _skillReleaser).getMsgQueueIndex(),
										new Warning(Tip.TIP_EFFECT_OF_LOWER_LEVEL));

								return;
							}
						}
					}
				}
			}
			//
			//
			//

			try {
				Effect newEffect = _effectModel.clone();

				if (newEffect.build(_skillReleaser, _target)) {
					targetEffectList.add(newEffect);
					log.info("给目标[" + _target.getName() + "]" + "施加" + newEffect.name);

					AddEffectNotify msg = new AddEffectNotify(_target, newEffect);

					if (_target instanceof HeroPlayer) {
						ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), msg);

						MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, true,
								_target.getID());
					} else {
						MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, false,
								0);
					}

					if (_target instanceof Monster && !existsEffectMonsterList.contains(_target)) {
						existsEffectMonsterList.add((Monster) _target);
					}
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加武器强化BUFF
	 * 
	 * @param _src
	 *            武器拥有者
	 * @param _effectModel
	 *            效果模板
	 */
	public void addWeaponEnhanceEffect(HeroPlayer _player, Effect _effectModel) {
		if (null == _effectModel)
			return;

		ArrayList<Effect> playerEffectList = _player.effectList;

		synchronized (playerEffectList) {
			if (playerEffectList.size() > 0) {
				Effect existsEffect;

				for (int i = 0; i < playerEffectList.size(); i++) {
					existsEffect = playerEffectList.get(i);

					if (_effectModel.ID == existsEffect.ID) {
						return;
					}
				}
			}

			playerEffectList.add(_effectModel);
		}
	}

	/**
	 * 移除武器强化BUFF
	 * 
	 * @param _src
	 *            武器拥有者
	 * @param _effectModel
	 *            效果模板
	 */
	public void removeWeaponEnhanceEffect(HeroPlayer _player, Effect _effectModel) {
		if (null == _effectModel)
			return;

		ArrayList<Effect> playerEffectList = _player.effectList;

		synchronized (playerEffectList) {
			if (playerEffectList.size() > 0) {
				Effect existsEffect;

				for (int i = 0; i < playerEffectList.size(); i++) {
					existsEffect = playerEffectList.get(i);

					if (_effectModel.ID == existsEffect.ID) {
						playerEffectList.remove(i);

						return;
					}
				}
			}
		}
	}

	/**
	 * 技能产生的效果
	 * 
	 * @param _src
	 *            技能施放者
	 * @param _dest
	 *            施放目标
	 * @param _effectModel
	 *            效果模板
	 */
	public void appendSkillEffect(ME2GameObject _skillReleaser, ME2GameObject _target, int _effectID) {
		Effect effectModel = EffectDictionary.getInstance().getEffectRef(_effectID);

		if (null != effectModel) {
			appendSkillEffect(_skillReleaser, _target, effectModel);
		}
	}

	/**
	 * 改变属性值，最终的运算全部通过加进行，这样确保多层和相互影响的属性在还原时的精准
	 * 
	 * @param _object
	 *            对象
	 * @param _staticEffect
	 *            静态效果
	 * @param _
	 *            _isBuild 是否是产生还是消失
	 * @return
	 */
	public boolean changePropertyValue(ME2GameObject _object, StaticEffect _staticEffect,
			boolean _isBuild) {
		boolean propertyIsChanged = false;
		boolean skillReleaseTimeChanged = false;
		float value = 0;
		EMathCaluOperator operator = _staticEffect.caluOperator;

		if (_staticEffect.strength > 0) {
			value = changeValue(_object.getBaseProperty().getStrength(), _staticEffect.strength,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addStrength((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.agility > 0) {
			value = changeValue(_object.getBaseProperty().getAgility(), _staticEffect.agility,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addAgility((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.stamina > 0) {
			value = changeValue(_object.getBaseProperty().getStamina(), _staticEffect.stamina,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addStamina((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.inte > 0) {
			value = changeValue(_object.getBaseProperty().getInte(), _staticEffect.inte, operator,
					false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addInte((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.spirit > 0) {
			value = changeValue(_object.getBaseProperty().getSpirit(), _staticEffect.spirit,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addSpirit((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.lucky > 0) {
			value = changeValue(_object.getBaseProperty().getLucky(), _staticEffect.lucky,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addLucky((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.defense > 0) {
			value = changeValue(_object.getBaseProperty().getDefense(), _staticEffect.defense,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addDefense((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.maxHp > 0) {
			value = changeValue(_object.getBaseProperty().getHpMax(), _staticEffect.maxHp,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addHpMax((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.maxMp > 0) {
			value = changeValue(_object.getBaseProperty().getMpMax(), _staticEffect.maxMp,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addMpMax((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.hitLevel > 0) {
			value = changeValue(_object.getBaseProperty().getHitLevel(), _staticEffect.hitLevel,
					operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addHitLevel((short) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.physicsDuckLevel > 0) {
			value = changeValue(_object.getBaseProperty().getPhysicsDuckLevel(),
					_staticEffect.physicsDuckLevel, operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addPhysicsDuckLevel((short) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.physicsDeathblowLevel > 0) {
			value = changeValue(_object.getBaseProperty().getPhysicsDeathblowLevel(),
					_staticEffect.physicsDeathblowLevel, operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addPhysicsDeathblowLevel((short) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.magicDeathblowLevel > 0) {
			value = changeValue(_object.getBaseProperty().getMagicDeathblowLevel(),
					_staticEffect.magicDeathblowLevel, operator, false);

			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.getActualProperty().addMagicDeathblowLevel((short) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.physicsAttackHarmValue > 0) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = changeValue(
						_object.getBaseProperty().getAdditionalPhysicsAttackHarmValue(),
						_staticEffect.physicsAttackHarmValue, operator, false);

				value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
				_object.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
			} else {
				// 乘号运算符代表增加该比率相关倍数,接下来使用加法加上这个增加率即可
				value = changeValue(
						_object.getBaseProperty().getAdditionalPhysicsAttackHarmScale(),
						_staticEffect.physicsAttackHarmValue, EMathCaluOperator.ADD, true);

				value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
				System.out.println("准备add:" + value);
				float f = _object.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
				System.out.println("添加中的变化值" + f);
				System.out.println("增加后:"
						+ _object.getActualProperty().getAdditionalPhysicsAttackHarmScale());
			}

			propertyIsChanged = true;
		}

		if (_staticEffect.bePhysicsHarmValue > 0) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = changeValue(_object.getBaseProperty()
						.getAdditionalHarmValueBePhysicsAttack(), _staticEffect.bePhysicsHarmValue,
						operator, false);

				value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
				_object.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
			} else {
				value = changeValue(_object.getBaseProperty()
						.getAdditionalHarmScaleBePhysicsAttack(), _staticEffect.bePhysicsHarmValue,
						operator, false);

				value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
				_object.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
			}

			propertyIsChanged = true;
		}

		if (_staticEffect.magicHarmValue != 0) {
			value = changeValue(1, _staticEffect.magicHarmValue, operator, false);
			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_staticEffect.magicHarmType != null) {
					_object.getActualProperty().addAdditionalMagicHarm(_staticEffect.magicHarmType,
							value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_object.getActualProperty().addAdditionalMagicHarm(list);
				}
			} else {
				if (_staticEffect.magicHarmType != null) {
					_object.getActualProperty().addAdditionalMagicHarmScale(
							_staticEffect.magicHarmType, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_object.getActualProperty().addAdditionalMagicHarmScale(list);
				}
			}

			propertyIsChanged = true;
		}

		if (_staticEffect.magicHarmValueBeAttack != 0) {
			value = changeValue(1, _staticEffect.magicHarmValueBeAttack, operator, false);
			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_staticEffect.magicHarmTypeBeAttack != null) {
					_object.getActualProperty().addAdditionalMagicHarmBeAttack(
							_staticEffect.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_object.getActualProperty().addAdditionalMagicHarmBeAttack(list);
				}
			} else {
				if (_staticEffect.magicHarmTypeBeAttack != null) {
					_object.getActualProperty().addAdditionalMagicHarmScaleBeAttack(
							_staticEffect.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_object.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
				}
			}

			propertyIsChanged = true;
		}

		if (_staticEffect.magicFastnessValue != 0) {
			if (_staticEffect.magicFastnessType != null) {
				value = changeValue(_object.getBaseProperty().getMagicFastnessList()
						.getEMagicFastnessValue(_staticEffect.magicFastnessType),
						_staticEffect.magicFastnessValue, operator, false);
			} else {
				EMagic[] magic = EMagic.values();

				for (int i = 0; i < magic.length; i++) {
					value = changeValue(_object.getBaseProperty().getMagicFastnessList()
							.getEMagicFastnessValue(magic[i]), _staticEffect.magicFastnessValue,
							operator, false);

					if (value != 0) {
						value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
						_object.getActualProperty().getMagicFastnessList().add(magic[i],
								(int) value);
					}
				}
			}

			propertyIsChanged = true;
		}

		if (_staticEffect.hate > 0) {
			if (_object.getObjectType() == EObjectType.PLAYER) {
				value = changeValue(1, _staticEffect.hate, operator, false);

				value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
				((HeroPlayer) _object).changeHatredModulus(value);
			}

			propertyIsChanged = true;
		}

		if (_staticEffect.physicsAttackInterval > 0) {
			value = changeValue(_object.getBaseAttackImmobilityTime(),
					_staticEffect.physicsAttackInterval, operator, false);
			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
			_object.addActualAttackImmobilityTime((int) value);

			propertyIsChanged = true;
		}

		if (_staticEffect.allSkillReleaseTime > 0) {
			ArrayList<ActiveSkill> list = null;
			if (_object instanceof HeroPlayer) {
				list = ((HeroPlayer) _object).activeSkillList;
				skillReleaseTimeChanged = true;

				ActiveSkill activeSkill;

				for (int i = 0; i < list.size(); i++) {
					activeSkill = list.get(i);

					value = changeValue(((ActiveSkillUnit) (SkillUnitDict.getInstance()
							.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
							_staticEffect.allSkillReleaseTime, operator, false);
					value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
					((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;
				}
			}

		}

		if (_staticEffect.specialSkillReleaseTimeIDList != null) {
			ArrayList<ActiveSkill> skillListBeRefresh = new ArrayList<ActiveSkill>();
			ArrayList<ActiveSkill> objectSkillList = ((HeroPlayer) _object).activeSkillList;

			ActiveSkill activeSkill;

			for (int i = 0; i < _staticEffect.specialSkillReleaseTimeIDList.size(); i++) {
				for (int j = 0; j < objectSkillList.size(); j++) {
					activeSkill = objectSkillList.get(j);

					if (activeSkill.id == _staticEffect.specialSkillReleaseTimeIDList.get(i)) {
						value = changeValue(((ActiveSkillUnit) (SkillUnitDict.getInstance()
								.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
								_staticEffect.specialSkillReleaseTime, operator, false);

						value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);
						((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;

						skillListBeRefresh.add(objectSkillList.get(j));

						break;
					}
				}
			}

			if (skillListBeRefresh.size() > 0 && _object instanceof HeroPlayer) {
				// OutMsgQ.getInstance().put(
				// ((HeroPlayer) _object).getMsgQueueIndex(),
				// new UpdateSkillTimeByInt(skillListBeRefresh));

				skillReleaseTimeChanged = true;
			}
		}

		if (_staticEffect.resistSpecialStatus != null) {
			value = changeValue(0, _staticEffect.resistSpecialStatusOdds, operator, false);
			value = _isBuild ? value : -(_staticEffect.currentCountTimes * value);

			if (_staticEffect.resistSpecialStatus == ESpecialStatus.DUMB) {
				_object.getResistOddsList().forbidSpellOdds += value;
			} else if (_staticEffect.resistSpecialStatus == ESpecialStatus.FAINT) {
				_object.getResistOddsList().insensibleOdds += value;
			} else if (_staticEffect.resistSpecialStatus == ESpecialStatus.SLEEP) {
				_object.getResistOddsList().sleepingOdds += value;
			} else if (_staticEffect.resistSpecialStatus == ESpecialStatus.LAUGH) {
				if (_object.getObjectType() == EObjectType.MONSTER)
					((Monster) _object).getResistOddsList().provokeOdds += value;
			} else if (_staticEffect.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
				_object.getResistOddsList().physicsDeathblowOdds += value;
			} else if (_staticEffect.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
				_object.getResistOddsList().magicDeathblowOdds += value;
			} else if (_staticEffect.resistSpecialStatus == ESpecialStatus.STOP) {
				_object.getResistOddsList().fixBodyOdds += value;
			}

			if (value != 0) {
				propertyIsChanged = true;
			}
		}

		if (propertyIsChanged) {
			if (_object.getObjectType() == EObjectType.PLAYER) {
				PlayerServiceImpl.getInstance().refreshRoleProperty((HeroPlayer) _object);
			}

			MapSynchronousInfoBroadcast.getInstance().put(_object.where(),
					new RefreshObjectViewValue(_object), true, _object.getID());
		}

		return propertyIsChanged || skillReleaseTimeChanged;
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
	 * @param _isOdd
	 *            是否为比率做加减运算
	 * @return
	 */
	private float changeValue(float _baseValue, float _caluModulus, EMathCaluOperator _operator,
			boolean _isOdd) {
		if (_caluModulus > 0) {
			switch (_operator) {
			case ADD: {
				// 例1:
				// 传入 1.5 ; 期望提升50%攻击力
				// 我们返回 附加的0.5;这个0.5会附加到人物基础属性的基础攻击比率上
				// 例2:
				// 传入0.5; 期望降低敌方50%攻击力
				// 我们返回 -0.5;这个-0.5会附加到人物基础属性的基础攻击比率上
				// 例3:
				// 先传入1.6;期望提升60%攻击, 人物攻击附加60%
				// 再被传入0.3;敌方期望降低你30%攻击. 人物攻击附加 -0.3
				// 最终算出来的结果为 0.6+(-0.3) = 0.3
				// 最终只被附加了 30%的伤害
				if (_isOdd && _caluModulus > 1)
					return _caluModulus - 1;
				else if (_caluModulus > 1) {
					return _caluModulus;
				} else if (_caluModulus < 1)
					return -(1 - _caluModulus);
			}
			case DEC: {
				return -_caluModulus;
			}
			case MUL: {
				if (_caluModulus > 1)
					return _baseValue * (_caluModulus - 1);
				else if (_caluModulus < 1)
					return -(_baseValue * (1 - _caluModulus));
			}
			case DIV: {
				return -(_baseValue * ((_caluModulus - 1) / _caluModulus));
			}
			}
		}

		return 0;
	}

	/**
	 * 扫描队伍光环辐射目标
	 * 
	 * @param _releaser
	 *            施放者
	 * @param _aureoleEffect
	 *            光环效果
	 */
	public void scanAureoleRadiationTarget(ME2GameObject _releaser, Effect _aureoleEffect) {
		if (_aureoleEffect.keepTimeType == EKeepTimeType.N_A) {
			AureoleRadiationRange range = _aureoleEffect.aureoleRadiationRange;

			if (_releaser instanceof HeroPlayer) {
				HeroPlayer player = (HeroPlayer) _releaser;

				if (_aureoleEffect.aureoleRadiationRange.targetType == ETargetType.ENEMY) {
					ArrayList<ME2GameObject> radiationTargetList = MapServiceImpl.getInstance()
							.getAttackableObjectListInRange(_releaser.where(),
									_releaser.getCellX(), _releaser.getCellY(),
									_aureoleEffect.aureoleRadiationRange.rangeRadiu, _releaser, -1);

					if (radiationTargetList != null && radiationTargetList.size() > 0) {
						ME2GameObject target;
						ArrayList<Effect> targetEffectList = null;
						Effect existsEffect = null;
						boolean beRadiation = false;

						for (int i = 0; i < radiationTargetList.size(); i++) {
							target = radiationTargetList.get(i);

							if (target.getID() == _releaser.getID()) {
								continue;
							}

							targetEffectList = target.effectList;

							if (targetEffectList.contains(_aureoleEffect)) {
								return;
							}

							beRadiation = true;

							for (int j = 0; j < targetEffectList.size(); j++) {
								existsEffect = targetEffectList.get(j);

								if (EKeepTimeType.N_A == existsEffect.keepTimeType) {
									if (existsEffect.ID == _aureoleEffect.ID) {
										return;
									}

									if (existsEffect.name.equals(_aureoleEffect.name)) {
										if (existsEffect.level >= _aureoleEffect.level) {
											beRadiation = false;

											break;
										} else {
											targetEffectList.remove(existsEffect);
											existsEffect.destory(target);
											// add by zhengl; date: 2011-03-21;
											// note: 因BUFF变更引起的速度变更
											if (existsEffect instanceof StaticEffect) {
												removeMove((StaticEffect) existsEffect, target);
											}
											MapSynchronousInfoBroadcast.getInstance().put(
													target.where(),
													new RemoveEffectNotify(target, existsEffect),
													false, 0);

											break;
										}
									}
								}
							}

							if (beRadiation) {
								targetEffectList.add(_aureoleEffect);
								_aureoleEffect.radiationTarget(_releaser, target);
								MapSynchronousInfoBroadcast.getInstance().put(target.where(),
										new AddEffectNotify(target, _aureoleEffect), false, 0);

								if (target instanceof Monster
										&& !existsEffectMonsterList.contains(target)) {
									existsEffectMonsterList.add((Monster) target);
								}
							}
						}
					}
				} else if (_aureoleEffect.aureoleRadiationRange.targetType == ETargetType.FRIEND) {
					if (player.getGroupID() > 0) {
						ArrayList<HeroPlayer> radiationTargetList = MapServiceImpl.getInstance()
								.getGroupPlayerInRange((HeroPlayer) _releaser,
										_releaser.getCellX(), _releaser.getCellY(),
										range.rangeRadiu, -1);

						if (radiationTargetList != null && radiationTargetList.size() > 0) {
							ME2GameObject target;
							ArrayList<Effect> targetEffectList = null;
							Effect existsEffect = null;
							boolean beRadiation = false;

							for (int i = 0; i < radiationTargetList.size(); i++) {
								target = radiationTargetList.get(i);

								if (target.getID() == _releaser.getID()) {
									continue;
								}

								targetEffectList = target.effectList;

								if (targetEffectList.contains(_aureoleEffect)) {
									return;
								}

								beRadiation = true;

								for (int j = 0; j < targetEffectList.size(); j++) {
									existsEffect = targetEffectList.get(j);

									if (EKeepTimeType.N_A == existsEffect.keepTimeType) {
										if (existsEffect.ID == _aureoleEffect.ID) {
											return;
										}

										if (existsEffect.name.equals(_aureoleEffect.name)) {
											if (existsEffect.level >= _aureoleEffect.level) {
												return;
											} else {
												targetEffectList.remove(existsEffect);
												existsEffect.destory(target);

												MapSynchronousInfoBroadcast.getInstance()
														.put(
																target.where(),
																new RemoveEffectNotify(target,
																		existsEffect), false, 0);

												break;
											}
										}
									}
								}

								if (beRadiation) {
									targetEffectList.add(_aureoleEffect);
									_aureoleEffect.radiationTarget(_releaser, target);
									MapSynchronousInfoBroadcast.getInstance().put(target.where(),
											new AddEffectNotify(target, _aureoleEffect), false, 0);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 光环有效性检查
	 * 
	 * @param _host
	 *            效果作用目标
	 * @param _aureoleEffect
	 *            光环效果
	 */
	public boolean checkAureoleValidity(ME2GameObject _host, Effect _aureoleEffect) {
		if (_aureoleEffect.keepTimeType == EKeepTimeType.N_A) {
			ME2GameObject releaser = _aureoleEffect.releaser;

			if (releaser == _host) {
				return true;
			}

			AureoleRadiationRange range = _aureoleEffect.aureoleRadiationRange;

			if (Math.abs(_host.getCellX() - releaser.getCellX()) > range.rangeRadiu
					|| Math.abs(_host.getCellY() - releaser.getCellY()) > range.rangeRadiu) {
				removeEffect(_host, _aureoleEffect, false, false);

				return false;
			}

			if (!ObjectCheckor.isValidate(releaser)) {
				removeEffect(_host, _aureoleEffect, false, false);

				return false;
			}

			if (releaser instanceof HeroPlayer && _host instanceof HeroPlayer) {
				if (((HeroPlayer) releaser).getGroupID() == 0
						|| ((HeroPlayer) releaser).getGroupID() != ((HeroPlayer) _host)
								.getGroupID()) {
					removeEffect(_host, _aureoleEffect, false, false);

					return false;
				}
			}

			if (_host.where() != releaser.where()) {
				removeEffect(_host, _aureoleEffect, false, false);

				return false;
			}
		}

		return true;
	}

	/**
	 * 追加特殊效果
	 * 
	 * @param _releaser
	 * @param _host
	 * @param _effect
	 */
	public boolean appendSpecialStatus(ME2GameObject _releaser, ME2GameObject _host,
			StaticEffect _effect) {
		log.info("添加特殊效果：" + _effect.name);
		ESpecialStatus specialStatus = _effect.specialStatus;
		byte existsMaxSpeedLevel = 0, existsSpeedLevel = 0;
		if (specialStatus != null) {
			ArrayList<Effect> effectList = _host.effectList;
			if (null != effectList && effectList.size() > 0) {
				Effect existsEffect;
				for (int i = 0; i < effectList.size(); i++) {
					existsEffect = effectList.get(i);
					if (_effect != existsEffect && existsEffect instanceof StaticEffect) {
						if (((StaticEffect) existsEffect).specialStatus == specialStatus) {
							existsSpeedLevel = ((StaticEffect) existsEffect)
									.getSpecialStatusLevel();
							if (existsMaxSpeedLevel < existsSpeedLevel) {
								existsMaxSpeedLevel = existsSpeedLevel;
							}
						}
					}
				}
			}
			switch (specialStatus) {
			case HIDE: {
				_host.disappear();
				SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.DISAPPEAR);
				return true;
			}
			case DUMB: {
				if (_host instanceof Monster
						&& ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
					ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(),
							new Warning(Tip.TIP_EFFECT_OF_IMMUNITY));
				} else {
					if (_host.canReleaseMagicSkill()) {
						_host.forbidReleaseMagicSkill();
						SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.MUTE);
					}
					return true;
				}
				break;
			}
			case FAINT: {
				if (_host instanceof Monster
						&& ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
					ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(),
							new Warning(Tip.TIP_EFFECT_OF_IMMUNITY));
				} else {
					if (!_host.isInsensible()) {
						_host.beInComa();
						SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.FAINT);
					}
					return true;
				}
				break;
			}
			case SLEEP: {
				if (_host instanceof Monster
						&& ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
					ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(),
							new Warning(Tip.TIP_EFFECT_OF_IMMUNITY));
				} else {
					if (!_host.isSleeping()) {
						_host.sleep();
						SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.SLEEP);
					}
					return true;
				}
				break;
			}
			case LAUGH: {
				if (_host instanceof Monster && _releaser instanceof HeroPlayer) {
					((Monster) _host).beProvoke((HeroPlayer) _releaser);
					return true;
				}
				break;
			}
			case STOP: {
				if (_host instanceof Monster
						&& ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
					ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(),
							new Warning(Tip.TIP_EFFECT_OF_IMMUNITY));
				} else {
					if (_host.moveable()) {
						_host.fixBody();
						SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.STOP);
					}
					return true;
				}
				break;
			}
			case MOVE_FAST: {
				// edit by zhengl; date: 2011-03-21; note: 不在直接用玩家速度作为参考值.
				_host.addAddSpeedState(true);
				byte speedLevel = _effect.getSpecialStatusLevel();
				byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
				MapSynchronousInfoBroadcast.getInstance().put(
						_host.where(),
						new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(),
								speed), false, 0);
				return true;
			}
			case MOVE_SLOWLY: {
				if (_host instanceof Monster
						&& ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
					ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(),
							new Warning(Tip.TIP_EFFECT_OF_IMMUNITY));
				} else {
					// edit by zhengl; date: 2011-03-21; note: 不在直接用玩家速度作为参考值.
					_host.addSlowSpeedState(true);
					byte speedLevel = _effect.getSpecialStatusLevel();
					byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
					MapSynchronousInfoBroadcast.getInstance().put(
							_host.where(),
							new MoveSpeedChangerNotify(_host.getObjectType().value(),
									_host.getID(), speed), false, 0);
					return true;
				}
				break;
			}
			}// end switch
		}

		// eturn false;

		return true;
	}

	/**
	 * 消除特殊效果
	 * 
	 * @param _host
	 * @param _effect
	 */
	public void clearSpecialStatus(ME2GameObject _host, StaticEffect _effect) {
		ESpecialStatus specialStatus = _effect.specialStatus;
		if (specialStatus != null) {
			switch (specialStatus) {
			case HIDE: {// 潜行
				_host.emerge();
				SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.NORMAL);
				break;
			}
			case DUMB: {// 禁魔
				if (!_host.canReleaseMagicSkill()) {
					_host.relieveMagicSkillLimit();
					SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.CAN_MAGIC);
				}
				break;
			}
			case FAINT: {// 晕厥
				if (_host.isInsensible()) {
					_host.relieveComa();
					SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.SOBER);
				}
				break;
			}
			case SLEEP: {// 昏睡
				if (_host.isSleeping()) {
					_host.wakeUp();
					SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.WAKE_UP);
				}
				break;
			}
			case STOP: {// 定身
				if (!_host.moveable()) {
					_host.relieveFixBodyLimit();
					SpecialViewStatusBroadcast.send(_host, SpecialStatusDefine.RUN);
				}
				break;
			}
			case MOVE_FAST: {// 加速
				// edit by zhengl; date: 2011-03-21; note: 不在直接用玩家速度作为参考值.
				_host.removeAddSpeedState();
				byte speedLevel = _effect.getSpecialStatusLevel();
				byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
				MapSynchronousInfoBroadcast.getInstance().put(
						_host.where(),
						new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(),
								speed), false, 0);
				RemoveEffectNotify msg = new RemoveEffectNotify(_host, _effect);
				if (_host instanceof HeroPlayer) {
					HeroPlayer player = (HeroPlayer) _host;
					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

					MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true,
							player.getID());
				} else {
					MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
				}
				break;
			}
			case MOVE_SLOWLY: {// 减速
				// edit by zhengl; date: 2011-03-21; note: 不在直接用玩家速度作为参考值.
				_host.removeSlowSpeedState();
				byte speedLevel = _effect.getSpecialStatusLevel();
				byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
				MapSynchronousInfoBroadcast.getInstance().put(
						_host.where(),
						new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(),
								speed), false, 0);
				RemoveEffectNotify msg = new RemoveEffectNotify(_host, _effect);
				if (_host instanceof HeroPlayer) {
					HeroPlayer player = (HeroPlayer) _host;
					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

					MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true,
							player.getID());
				} else {
					MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
				}
				break;
			}
			} // end switch

		}
	}

	/**
	 * 消除隐形效果
	 * 
	 * @param _host
	 */
	public void clearHideEffect(ME2GameObject _host) {
		if (_host != null) {
			ArrayList<Effect> effectList = _host.effectList;

			if (null != effectList && effectList.size() > 0) {
				Effect existsEffect;

				for (int i = 0; i < effectList.size(); i++) {
					existsEffect = effectList.get(i);

					if (existsEffect instanceof StaticEffect) {
						if (((StaticEffect) existsEffect).specialStatus == ESpecialStatus.HIDE) {
							existsEffect.destory(_host);
							effectList.remove(existsEffect);
						}
					}
				}
			}
		}
	}

	/**
	 * 执行动态效果
	 * 
	 * @param _effect
	 *            效果
	 * @param _host
	 *            效果宿主
	 */
	public void executeDynamicEffect(DynamicEffect _effect, ME2GameObject _host) {
		if (_effect.trait == EffectTrait.BUFF) {
			if (_effect.hpResumeValue != 0) {
				FightServiceImpl.getInstance().processHpChange(_effect.releaser, _host,
						_effect.hpResumeValue, _effect.isDeathblow, _effect.harmMagicType);
			}

			if (_effect.mpResumeValue != 0) {
				_host.addMp(_effect.mpResumeValue);

				FightServiceImpl.getInstance().processSingleTargetMpChange(_host, true);
			}
		} else {
			if (_effect.hpHarmValue != 0) {
				FightServiceImpl.getInstance().processHpChange(_effect.releaser, _host,
						-_effect.hpHarmValue, _effect.isDeathblow, _effect.harmMagicType);
			}

			if (_effect.mpHarmValue != 0) {
				_host.addMp(-_effect.mpHarmValue);

				FightServiceImpl.getInstance().processSingleTargetMpChange(_host, true);
			}
		}
	}

	/**
	 * 触发效果
	 * <p>
	 * 举个例子:比如光环类技能附带50%几率触发伤害反弹.
	 * 
	 * @param _host
	 *            主动方
	 * @param _activeTouchType
	 *            引发的主动方的触发方式
	 * @param _target
	 *            涉及到的另一方
	 * @param _passiveTouchType
	 *            引发的目标的触发方式
	 * @param _isSkillTouch
	 *            是否是技能触发，如果是技能触发，则也可算作使用魔法的触发条件
	 */
	public void checkTouchEffect(ME2GameObject _host, ME2GameObject _target,
			ETouchType _activeTouchType, boolean _isSkillTouch) {
		if (null != _host && _host.isEnable() && !_host.isDead()) {
			ArrayList<Effect> effectList = null;
			if (_host instanceof HeroPlayer) {
				effectList = ((HeroPlayer) _host).effectList;
				if (null != effectList && effectList.size() > 0) {
					Effect effect;
					for (int i = 0; i < effectList.size(); i++) {
						try {
							effect = effectList.get(i);
						} catch (Exception e) {
							break;
						}
						if (effect instanceof TouchEffect
								&& ((TouchEffect) effect).touchType == _activeTouchType) {
							((TouchEffect) effect).touch((HeroPlayer) _host, _target,
									_activeTouchType, _isSkillTouch,
									((TouchEffect) effect).harmMagicType);
						}
					}
				}
			}
		}
	}

	/**
	 * 移除对象身上的效果
	 * 
	 * @param _host
	 *            对象
	 * @param _effect
	 *            效果
	 * @param _isTimeEnd
	 *            是否是时间结束导致的
	 */
	public void removeEffect(ME2GameObject _host, Effect _effect, boolean _isTimeEnd,
			boolean _isCompelRemoveIcon) {
		ArrayList<Effect> targetEffectList = _host.effectList;

		if (_effect.keepTimeType == EKeepTimeType.N_A) {
			if (_host == _effect.releaser) {
				if (_effect.aureoleRadiationTargetList.size() > 0) {
					ME2GameObject target;

					synchronized (_effect.aureoleRadiationTargetList) {
						for (int i = 0; i < _effect.aureoleRadiationTargetList.size(); i++) {
							target = _effect.aureoleRadiationTargetList.get(i);

							if (target.effectList.remove(_effect)) {
								_effect.destory(target);
								// //add by zhengl; date: 2011-03-21; note: 暂不需要
								// if(_effect instanceof StaticEffect){
								// removeMove((StaticEffect)_effect, _host);
								// }
								if (target.isEnable() && !target.isDead()) {
									MapSynchronousInfoBroadcast.getInstance().put(target.where(),
											new RemoveEffectNotify(target, _effect), false, 0);
								}
							}
						}

						_effect.aureoleRadiationTargetList.clear();
					}
				}
			} else {
				_effect.aureoleRadiationTargetList.remove(_host);
			}
		}

		if (targetEffectList.remove(_effect)) {
			// add by zhengl; date: 2011-03-21; note: 因BUFF变更引起的速度变更
			_effect.destory(_host);
			if ((!_host.isDead() && _isTimeEnd) || _isCompelRemoveIcon) {
				RemoveEffectNotify msg = new RemoveEffectNotify(_host, _effect);

				if (_host instanceof HeroPlayer) {
					ResponseMessageQueue.getInstance().put(((HeroPlayer) _host).getMsgQueueIndex(), msg);

					MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, true,
							_host.getID());
				} else {
					MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
				}
			}
		}
	}

	/**
	 * 把别人的速度通知给这个玩家 (用于切换地图)
	 * 
	 * @param _effect
	 * @param _player
	 */
	private void notifyMoveToPlayer(StaticEffect _effect, HeroPlayer _player, ME2GameObject _other) {
		if (_effect.specialStatus == ESpecialStatus.MOVE_FAST
				|| _effect.specialStatus == ESpecialStatus.MOVE_SLOWLY) {
			byte speedLevel = _effect.getSpecialStatusLevel();
			byte speed = MoveSpeed.getNowSpeed(_other.getMoveSpeedState(), speedLevel);

			MoveSpeedChangerNotify msg = new MoveSpeedChangerNotify(_other.getObjectType().value(),
					_other.getID(), speed);
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
		}
	}

	/**
	 * 通知当前玩家的速度给别人(用于切换地图)
	 * 
	 * @param _effect
	 * @param _host
	 */
	private void notifyMoveToOther(StaticEffect _effect, ME2GameObject _host) {
		ESpecialStatus specialStatus = _effect.specialStatus;
		if (specialStatus != null) {
			switch (specialStatus) {
			case MOVE_FAST: {
				log.info("该玩家有被加速!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				byte speedLevel = _effect.getSpecialStatusLevel();
				byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
				MapSynchronousInfoBroadcast.getInstance().put(
						_host.where(),
						new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(),
								speed), false, 0);
				break;
			}
			case MOVE_SLOWLY: {
				log.info("该玩家有被减速!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				byte speedLevel = _effect.getSpecialStatusLevel();
				byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
				MapSynchronousInfoBroadcast.getInstance().put(
						_host.where(),
						new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(),
								speed), false, 0);
				break;
			}
			default: {
				break;
			}
			}
		}
	}

	/**
	 * 移除当前1个速度效果
	 * 
	 * @param _effect
	 * @param _host
	 */
	public void removeMove(StaticEffect _effect, ME2GameObject _host) {
		if (_effect != null && _effect.specialStatus != null) {
			ESpecialStatus specialStatus = _effect.specialStatus;
			if (specialStatus != null) {
				switch (specialStatus) {
				case MOVE_FAST: {
					log.info("移除加速度!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					_host.removeAddSpeedState();
					byte speedLevel = _effect.getSpecialStatusLevel();
					byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
					MapSynchronousInfoBroadcast.getInstance().put(
							_host.where(),
							new MoveSpeedChangerNotify(_host.getObjectType().value(),
									_host.getID(), speed), false, 0);
					break;
				}
				case MOVE_SLOWLY: {
					log.info("移除减速度!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					_host.removeSlowSpeedState();
					byte speedLevel = _effect.getSpecialStatusLevel();
					byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
					MapSynchronousInfoBroadcast.getInstance().put(
							_host.where(),
							new MoveSpeedChangerNotify(_host.getObjectType().value(),
									_host.getID(), speed), false, 0);
					break;
				}
				default: {
					break;
				}
				}
			}// end if

		}
	}

	/**
	 * 移除对象身上符合条件的效果
	 * 
	 * @param _player
	 *            驱散技能施放者
	 * @param _host
	 *            目标
	 * @param _effectFeature
	 *            效果特质
	 * @param _effectMaxLevel
	 *            可驱散的最高效果等级
	 * @param _number
	 *            每次最多可驱散的效果数量
	 */
	public void cleanEffect(HeroPlayer _player, ME2GameObject _host, EffectFeature _effectFeature,
			byte _effectMaxLevel, short _number) {
		if (_host.effectList.size() > 0) {
			Effect effect;

			int totalCleaned = 0;

			for (int i = 0; i < _host.effectList.size();) {
				effect = _host.effectList.get(i);
				// edit by zhengl; date: 2011-04-17; note: 添加ALL驱散类型可以驱散所有属性法术.
				if (EKeepTimeType.LIMITED == effect.keepTimeType
						&& EffectTrait.DEBUFFF == effect.trait) {
					if ((effect.feature == _effectFeature || _effectFeature == EffectFeature.ALL)
							&& effect.featureLevel <= _effectMaxLevel) {
						_host.effectList.remove(i);
						effect.destory(_host);

						totalCleaned++;

						RemoveEffectNotify msg = new RemoveEffectNotify(_host, effect);
						// add by zhengl; date: 2011-03-21; note: 因BUFF变更引起的速度变更
						if (effect instanceof StaticEffect) {
							removeMove((StaticEffect) effect, _host);
						}

						if (_host.getID() == _player.getID()) {
							ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);

							MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg,
									true, _player.getID());
						} else {
							MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg,
									false, 0);
						}

						if (totalCleaned >= _number) {
							return;
						}

						continue;
					}
				}

				i++;
			}
		}
	}

	/**
	 * 移除对象身上指定编号的效果
	 * 
	 * @param _player
	 *            驱散技能施放者
	 * @param _host
	 *            目标
	 * @param _effectIDLowLimit
	 *            具体效果编号区间下限
	 * @param _effectIDUperLimit
	 *            具体效果编号区间上限
	 */
	public void cleanEffect(HeroPlayer _player, ME2GameObject _host, int _effectIDLowLimit,
			int _effectIDUperLimit) {
		if (_host.effectList.size() > 0) {
			Effect effect;

			for (int i = 0; i < _host.effectList.size(); i++) {
				effect = _host.effectList.get(i);

				if (effect.ID >= _effectIDLowLimit && effect.ID <= _effectIDLowLimit) {
					_host.effectList.remove(i);
					effect.destory(_host);

					RemoveEffectNotify msg = new RemoveEffectNotify(_host, effect);
					// add by zhengl; date: 2011-03-21; note: 因BUFF变更引起的速度变更
					if (effect instanceof StaticEffect) {
						removeMove((StaticEffect) effect, _host);
					}

					if (_host.getID() == _player.getID()) {
						ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);

						MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, true,
								_player.getID());
					} else {
						MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
					}

					return;
				}
			}
		}
	}

	/**
	 * @author Administrator 怪物效果列表检索任务
	 */
	public class MonsterEffectCheckTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (existsEffectMonsterList.size() > 0) {
				Monster monster;
				Effect effect;

				for (int i = 0; i < existsEffectMonsterList.size();) {
					monster = existsEffectMonsterList.get(i);

					if (monster.effectList.size() > 0) {
						for (int j = 0; j < monster.effectList.size();) {
							try {
								effect = monster.effectList.get(j);

								if (effect.heartbeat(monster)) {
									j++;
								}
							} catch (Exception e) {
								e.printStackTrace();

								break;
							}
						}
					}

					if (monster.effectList.size() == 0) {
						existsEffectMonsterList.remove(i);
					} else {
						i++;
					}
				}
			}
		}
	}

}
