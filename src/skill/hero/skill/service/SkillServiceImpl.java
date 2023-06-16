package hero.skill.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

import hero.duel.service.DuelServiceImpl;
import hero.effect.Effect;
import hero.effect.detail.DynamicEffect;
import hero.effect.detail.StaticEffect;
import hero.effect.detail.TouchEffect;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.fight.service.FightServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.Weapon.EWeaponType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.GuildBuild;
import hero.item.special.RinseSkill;
import hero.log.service.CauseLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.service.MapServiceImpl;
import hero.skill.ActiveSkill;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;
import hero.skill.Skill;
import hero.skill.PassiveSkill;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ESkillType;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.skill.dict.MonsterSkillDict;
import hero.skill.dict.SkillDict;
import hero.skill.dict.SkillUnitDict;
import hero.skill.message.AddSkillNotify;
import hero.skill.message.LearnedSkillListNotify;
import hero.skill.message.RefreshSkillTime;
import hero.skill.message.ReviveConfirm;
import hero.skill.message.SkillAnimationNotify;
import hero.skill.message.SkillUpgradeNotify;
import hero.skill.message.UpdateActiveSkillNotify;
import hero.skill.message.UpdateLearnableSkillList;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.unit.ChangePropertyUnit;
import hero.skill.unit.EnhanceSkillUnit;
import hero.skill.unit.PassiveSkillUnit;
import hero.skill.unit.SkillUnit;
import hero.skill.unit.TouchUnit;
import hero.skill.unit.EnhanceSkillUnit.EnhanceDataType;
import hero.skill.unit.EnhanceSkillUnit.EnhanceUnit;
import hero.skill.unit.EnhanceSkillUnit.SkillDataField;
import hero.novice.service.NoviceServiceImpl;
import hero.npc.Monster;
import hero.npc.dict.MonsterImageConfDict;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.message.ShortcutKeyListNotify;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.EMagic;
import hero.share.EObjectType;
import hero.share.EVocation;
import hero.share.ME2GameObject;
import hero.share.MagicHarmList;
import hero.share.message.RefreshObjectViewValue;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-14 下午02:01:11
 * @描述 ：技能服务
 */

public class SkillServiceImpl extends AbsServiceAdaptor<SkillConfig> {
	private static Logger log = Logger.getLogger(SkillServiceImpl.class);
	/**
	 * 单例
	 */
	private static SkillServiceImpl instance;

	/**
	 * 私有构造
	 */
	private SkillServiceImpl() {
		config = new SkillConfig();
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static SkillServiceImpl getInstance() {
		if (null == instance) {
			instance = new SkillServiceImpl();
		}

		return instance;
	}

	@Override
	protected void start() {
		SkillUnitDict.getInstance().load(config);
		SkillDict.getInstance().load(config);
		MonsterSkillDict.getInstance().load(config);
	}

	@Override
	public void createSession(Session _session) {
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);

		if (null != player) {
			initSkillList(player, SkillDAO.loadPlayerSkill(_session.userID));
		}
	}

	@Override
	public void sessionFree(Session _session) {

	}

	public void clean(int _userID) {
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);

		if (null != player) {
			SkillDAO.updateSkillTraceCD(player);
		}
	}

	/**
	 * 初始化技能列表
	 * 
	 * @param _player
	 *            玩家
	 * @param _skillInfoList
	 *            技能信息列表，信息为长度为2的整形数组，0下标为技能编号，1下标为剩余冷却时间，如果是被动技能则为0
	 */
	private void initSkillList(HeroPlayer _player, ArrayList<int[]> _skillInfoList) {
		_player.activeSkillList.clear();
		_player.activeSkillTable.clear();
		_player.passiveSkillList.clear();

		Skill skill;

		for (int[] skillInfo : _skillInfoList) {
			skill = SkillServiceImpl.getInstance().getSkillIns(skillInfo[0]);

			if (null != skill) {
				if (skill instanceof ActiveSkill) {
					((ActiveSkill) skill).reduceCoolDownTime = skillInfo[1];
					_player.activeSkillList.add((ActiveSkill) skill);
					_player.activeSkillTable.put(skill.id, (ActiveSkill) skill);
				} else {
					_player.passiveSkillList.add((PassiveSkill) skill);
				}
			}
		}

		for (PassiveSkill passiveSkill : _player.passiveSkillList) {
			if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
				enhanceSkill((EnhanceSkillUnit) passiveSkill.skillUnit, _player, true, false);
			}
		}
	}

	/**
	 * 强化玩家技能
	 * 
	 * @param _enhanceSkillPassiveSkill
	 *            强化技能
	 * @param _player
	 *            玩家
	 * @param _isEnhance
	 *            是否是强化
	 * @param _needNotifyClient
	 *            是否通知客户端
	 */
	private void enhanceSkill(EnhanceSkillUnit _enhanceSkillPassiveSkill, HeroPlayer _player,
			boolean _isBuild, boolean _needNotifyClient) {
		boolean enhanced = false;

		for (EnhanceUnit enhanceUnit : _enhanceSkillPassiveSkill.enhanceUnitList) {
			for (ActiveSkill activeSkill : _player.activeSkillList) {
				if (unitEnhance(enhanceUnit, activeSkill, _isBuild, _needNotifyClient, _player)) {
					enhanced = true;

					break;
				}
			}

			if (!enhanced) {
				for (PassiveSkill passiveSkill : _player.passiveSkillList) {
					if (unitEnhance(enhanceUnit, passiveSkill, _isBuild, _needNotifyClient, _player)) {
						break;
					}
				}
			}
		}
	}

	/**
	 * 强化玩家属性
	 * 
	 * @param _propertyPassiveSkill
	 *            强化技能
	 * @param _host
	 *            强化的玩家
	 * @param _isInit
	 *            是否是初始化技能（如果不是，将不对技能施法时间、附加属性、特殊状态抵抗几率进行改变）
	 * @return
	 */
	private void enhanceProperty(ChangePropertyUnit _propertyPassiveSkill, HeroPlayer _host,
			boolean _isInit) {
		float value = 0;
		EMathCaluOperator operator = _propertyPassiveSkill.caluOperator;

		if (_propertyPassiveSkill.strength > 0) {
			value = valueChanged(_host.getBaseProperty().getStrength(),
					_propertyPassiveSkill.strength, operator);

			_host.getBaseProperty().addStrength((int) value);
		}

		if (_propertyPassiveSkill.agility > 0) {
			value = valueChanged(_host.getBaseProperty().getAgility(),
					_propertyPassiveSkill.agility, operator);

			_host.getBaseProperty().addAgility((int) value);
		}

		if (_propertyPassiveSkill.stamina > 0) {
			value = valueChanged(_host.getBaseProperty().getStamina(),
					_propertyPassiveSkill.stamina, operator);

			_host.getBaseProperty().addStamina((int) value);
		}

		if (_propertyPassiveSkill.inte > 0) {
			value = valueChanged(_host.getBaseProperty().getInte(), _propertyPassiveSkill.inte,
					operator);

			_host.getBaseProperty().addInte((int) value);
		}

		if (_propertyPassiveSkill.spirit > 0) {
			value = valueChanged(_host.getBaseProperty().getSpirit(), _propertyPassiveSkill.spirit,
					operator);

			_host.getBaseProperty().addSpirit((int) value);
		}

		if (_propertyPassiveSkill.lucky > 0) {
			value = valueChanged(_host.getBaseProperty().getLucky(), _propertyPassiveSkill.lucky,
					operator);

			_host.getBaseProperty().addLucky((int) value);
		}

		if (_propertyPassiveSkill.defense > 0) {
			value = valueChanged(_host.getBaseProperty().getDefense(),
					_propertyPassiveSkill.defense, operator);

			_host.getBaseProperty().addDefense((int) value);
		}

		if (_propertyPassiveSkill.maxHp > 0) {
			value = valueChanged(_host.getBaseProperty().getHpMax(), _propertyPassiveSkill.maxHp,
					operator);

			_host.getBaseProperty().addHpMax((int) value);
		}

		if (_propertyPassiveSkill.maxMp > 0) {
			value = valueChanged(_host.getBaseProperty().getMpMax(), _propertyPassiveSkill.maxMp,
					operator);

			_host.getBaseProperty().addMpMax((int) value);
		}

		if (_propertyPassiveSkill.hitLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getHitLevel(),
					_propertyPassiveSkill.hitLevel, operator);

			_host.getBaseProperty().addHitLevel((short) value);
		}

		if (_propertyPassiveSkill.physicsDuckLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getPhysicsDuckLevel(),
					_propertyPassiveSkill.physicsDuckLevel, operator);

			_host.getBaseProperty().addPhysicsDuckLevel((short) value);
		}

		if (_propertyPassiveSkill.physicsDeathblowLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getPhysicsDeathblowLevel(),
					_propertyPassiveSkill.physicsDeathblowLevel, operator);

			_host.getBaseProperty().addPhysicsDeathblowLevel((short) value);
		}

		if (_propertyPassiveSkill.magicDeathblowLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getMagicDeathblowLevel(),
					_propertyPassiveSkill.magicDeathblowLevel, operator);

			_host.getBaseProperty().addMagicDeathblowLevel((short) value);
		}

		if (_propertyPassiveSkill.physicsAttackHarmValue > 0 && _isInit) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = valueChanged(0, _propertyPassiveSkill.physicsAttackHarmValue, operator);

				_host.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
			} else {
				value = valueChanged(1, _propertyPassiveSkill.physicsAttackHarmValue, operator);

				_host.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
			}
		}

		if (_propertyPassiveSkill.bePhysicsHarmValue > 0 && _isInit) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = valueChanged(0, _propertyPassiveSkill.bePhysicsHarmValue, operator);

				_host.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
			} else {
				value = valueChanged(1, _propertyPassiveSkill.bePhysicsHarmValue, operator);

				_host.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
			}
		}

		if (_propertyPassiveSkill.magicHarmValue != 0 && _isInit) {
			value = valueChanged(1, _propertyPassiveSkill.magicHarmValue, operator);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_propertyPassiveSkill.magicHarmType != null) {
					_host.getActualProperty().addAdditionalMagicHarm(
							_propertyPassiveSkill.magicHarmType, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarm(list);
				}
			} else {
				if (_propertyPassiveSkill.magicHarmType != null) {
					_host.getActualProperty().addAdditionalMagicHarmScale(
							_propertyPassiveSkill.magicHarmType, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmScale(list);
				}
			}
		}

		if (_propertyPassiveSkill.magicHarmValueBeAttack != 0 && _isInit) {
			value = valueChanged(1, _propertyPassiveSkill.magicHarmValueBeAttack, operator);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_propertyPassiveSkill.magicHarmTypeBeAttack != null) {
					_host.getActualProperty().addAdditionalMagicHarmBeAttack(
							_propertyPassiveSkill.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmBeAttack(list);
				}
			} else {
				if (_propertyPassiveSkill.magicHarmTypeBeAttack != null) {
					_host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(
							_propertyPassiveSkill.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
				}
			}
		}

		if (_propertyPassiveSkill.magicFastnessValue != 0) {
			value = valueChanged(0, _propertyPassiveSkill.magicFastnessValue, operator);

			if (_propertyPassiveSkill.magicFastnessType != null) {
				_host.getBaseProperty().getMagicFastnessList().add(
						_propertyPassiveSkill.magicFastnessType, (int) value);
			} else {
				EMagic[] magic = EMagic.values();

				for (int i = 0; i < magic.length; i++) {
					_host.getBaseProperty().getMagicFastnessList().add(magic[i], (int) value);
				}
			}
		}

		if (_propertyPassiveSkill.hate > 0 && _isInit) {
			if (_host.getObjectType() == EObjectType.PLAYER) {
				value = valueChanged(1, _propertyPassiveSkill.hate, operator);

				((HeroPlayer) _host).changeHatredModulus(value);
			}
		}

		if (_propertyPassiveSkill.physicsAttackInterval > 0) {
			value = valueChanged(_host.getBaseAttackImmobilityTime(),
					_propertyPassiveSkill.physicsAttackInterval, operator);

			_host.addActualAttackImmobilityTime((int) value);
		}

		if (_propertyPassiveSkill.allSkillReleaseTime > 0 && _isInit) {
			ArrayList<ActiveSkill> list = _host.activeSkillList;

			ActiveSkill activeSkill;

			for (int i = 0; i < list.size(); i++) {
				activeSkill = list.get(i);

				value = valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
						.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
						_propertyPassiveSkill.allSkillReleaseTime, operator);

				((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;
			}
		}

		if (_propertyPassiveSkill.specialSkillReleaseTimeIDList != null && _isInit) {
			ActiveSkill activeSkill;

			for (int i = 0; i < _propertyPassiveSkill.specialSkillReleaseTimeIDList.size(); i++) {
				for (int j = 0; j < _host.activeSkillList.size(); j++) {
					activeSkill = _host.activeSkillList.get(j);

					if (activeSkill.id == _propertyPassiveSkill.specialSkillReleaseTimeIDList
							.get(i)) {
						value = valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
								.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
								_propertyPassiveSkill.specialSkillReleaseTime, operator);

						((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;

						break;
					}
				}
			}
		}

		if (_propertyPassiveSkill.resistSpecialStatus != null && _isInit) {
			value = valueChanged(0, _propertyPassiveSkill.resistSpecialStatusOdds, operator);
			value = _isInit ? value : -value;

			if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.DUMB) {
				_host.getResistOddsList().forbidSpellOdds += value;
			} else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.FAINT) {
				_host.getResistOddsList().insensibleOdds += value;
			} else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.SLEEP) {
				_host.getResistOddsList().sleepingOdds += value;
			} else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
				_host.getResistOddsList().physicsDeathblowOdds += value;
			} else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
				_host.getResistOddsList().magicDeathblowOdds += value;
			} else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.STOP) {
				_host.getResistOddsList().fixBodyOdds += value;
			}
		}
	}

	/**
	 * 属性强化技能升级
	 * 
	 * @param _lowLevelSkill
	 *            强化技能
	 * @param _host
	 *            强化的玩家
	 * @param _isBuild
	 *            是强化还是取消
	 * @return 基础属性是否发生变化
	 */
	private boolean enhancePropertySkillUpgrade(ChangePropertyUnit _lowLevelSkill,
			ChangePropertyUnit _highLevelSkill, HeroPlayer _host) {
		EMathCaluOperator operator = _lowLevelSkill.caluOperator;
		boolean basePropertyChanged = false;

		if (_highLevelSkill.strength > 0 
				|| _highLevelSkill.agility > 0
				|| _highLevelSkill.stamina > 0 
				|| _highLevelSkill.inte > 0
				|| _highLevelSkill.spirit > 0 
				|| _highLevelSkill.lucky > 0
				|| _highLevelSkill.defense > 0 
				|| _highLevelSkill.maxHp > 0
				|| _highLevelSkill.maxMp > 0 
				|| _highLevelSkill.hitLevel > 0
				|| _highLevelSkill.physicsDuckLevel > 0
				|| _highLevelSkill.physicsDeathblowLevel > 0
				|| _highLevelSkill.magicDeathblowLevel > 0 
				|| _highLevelSkill.physicsAttackInterval > 0 
				|| _highLevelSkill.physicsAttackHarmValue > 0
				|| _highLevelSkill.bePhysicsHarmValue > 0
				|| _highLevelSkill.magicHarmValue != 0
				|| _highLevelSkill.magicHarmValueBeAttack != 0
				|| _highLevelSkill.hate > 0
				|| _highLevelSkill.resistSpecialStatus != null
				|| _highLevelSkill.allSkillReleaseTime > 0
				|| _highLevelSkill.specialSkillReleaseTimeIDList != null) 
		{
			basePropertyChanged = true;
		}
		float value = 0;
		if (_highLevelSkill.strength > 0) {
			value = valueChanged(_host.getBaseProperty().getStrength(),
					_highLevelSkill.strength, operator);

			_host.getBaseProperty().addStrength((int) value);
		}

		if (_highLevelSkill.agility > 0) {
			value = valueChanged(_host.getBaseProperty().getAgility(),
					_highLevelSkill.agility, operator);

			_host.getBaseProperty().addAgility((int) value);
		}

		if (_highLevelSkill.stamina > 0) {
			value = valueChanged(_host.getBaseProperty().getStamina(),
					_highLevelSkill.stamina, operator);

			_host.getBaseProperty().addStamina((int) value);
		}

		if (_highLevelSkill.inte > 0) {
			value = valueChanged(_host.getBaseProperty().getInte(), _highLevelSkill.inte, operator);

			_host.getBaseProperty().addInte((int) value);
		}

		if (_highLevelSkill.spirit > 0) {
			value = valueChanged(_host.getBaseProperty().getSpirit(), _highLevelSkill.spirit, operator);

			_host.getBaseProperty().addSpirit((int) value);
		}

		if (_highLevelSkill.lucky > 0) {
			value = valueChanged(_host.getBaseProperty().getLucky(), _highLevelSkill.lucky, operator);

			_host.getBaseProperty().addLucky((int) value);
		}

		if (_highLevelSkill.defense > 0) {
			value = valueChanged(_host.getBaseProperty().getDefense(), _highLevelSkill.defense, operator);

			_host.getBaseProperty().addDefense((int) value);
		}

		if (_highLevelSkill.maxHp > 0) {
			value = valueChanged(_host.getBaseProperty().getHpMax(), _highLevelSkill.maxHp, operator);

			_host.getBaseProperty().addHpMax((int) value);
		}

		if (_highLevelSkill.maxMp > 0) {
			value = valueChanged(_host.getBaseProperty().getMpMax(), _highLevelSkill.maxMp, operator);

			_host.getBaseProperty().addMpMax((int) value);
		}

		if (_highLevelSkill.hitLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getHitLevel(), _highLevelSkill.hitLevel, operator);

			_host.getBaseProperty().addHitLevel((short) value);
		}

		if (_highLevelSkill.physicsDuckLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getPhysicsDuckLevel(), 
					_highLevelSkill.physicsDuckLevel, 
					operator);

			_host.getBaseProperty().addPhysicsDuckLevel((short) value);
		}

		_host.getActualProperty().getPhysicsDeathblowOdds();
		_host.getBaseProperty().getPhysicsDeathblowLevel();
		if (_highLevelSkill.physicsDeathblowLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getPhysicsDeathblowLevel(),
					_highLevelSkill.physicsDeathblowLevel, operator);

			_host.getBaseProperty().addPhysicsDeathblowLevel((short) value);
		}

		if (_highLevelSkill.magicDeathblowLevel > 0) {
			value = valueChanged(_host.getBaseProperty().getMagicDeathblowLevel(),
					_highLevelSkill.magicDeathblowLevel, operator);

			_host.getBaseProperty().addMagicDeathblowLevel((short) value);
		}

		if (_highLevelSkill.physicsAttackHarmValue > 0) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = valueChanged(_host.getActualProperty()
						.getAdditionalPhysicsAttackHarmValue(),
						_highLevelSkill.physicsAttackHarmValue, operator)
						- valueChanged(_host.getActualProperty()
								.getAdditionalPhysicsAttackHarmValue(),
								_lowLevelSkill.physicsAttackHarmValue, operator);

				_host.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
			} else {
				value = valueChanged(1, _highLevelSkill.physicsAttackHarmValue, operator)
						- valueChanged(1, _lowLevelSkill.physicsAttackHarmValue, operator);

				_host.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
			}
		}

		if (_highLevelSkill.bePhysicsHarmValue > 0) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = valueChanged(_host.getActualProperty()
						.getAdditionalHarmValueBePhysicsAttack(),
						_highLevelSkill.bePhysicsHarmValue, operator)
						- valueChanged(_host.getActualProperty()
								.getAdditionalHarmValueBePhysicsAttack(),
								_lowLevelSkill.bePhysicsHarmValue, operator);

				_host.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
			} else {
				value = valueChanged(1, _highLevelSkill.bePhysicsHarmValue, operator)
						- valueChanged(1, _lowLevelSkill.bePhysicsHarmValue, operator);

				_host.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
			}
		}

		if (_highLevelSkill.magicHarmValue != 0) {
			value = valueChanged(1, _highLevelSkill.magicHarmValue, operator)
					- valueChanged(1, _lowLevelSkill.magicHarmValue, operator);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_highLevelSkill.magicHarmType != null) {
					_host.getActualProperty().addAdditionalMagicHarm(_highLevelSkill.magicHarmType,
							value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarm(list);
				}
			} else {
				if (_highLevelSkill.magicHarmType != null) {
					_host.getActualProperty().addAdditionalMagicHarmScale(
							_highLevelSkill.magicHarmType, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmScale(list);
				}
			}
		}

		if (_highLevelSkill.magicHarmValueBeAttack != 0) {
			value = valueChanged(1, _highLevelSkill.magicHarmValueBeAttack, operator)
					- valueChanged(1, _lowLevelSkill.magicHarmValueBeAttack, operator);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_highLevelSkill.magicHarmTypeBeAttack != null) {
					_host.getActualProperty().addAdditionalMagicHarmBeAttack(
							_highLevelSkill.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmBeAttack(list);
				}
			} else {
				if (_highLevelSkill.magicHarmTypeBeAttack != null) {
					_host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(
							_highLevelSkill.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
				}
			}
		}

		if (_highLevelSkill.hate > 0) {
			if (_host.getObjectType() == EObjectType.PLAYER) {
				value = valueChanged(1, _highLevelSkill.hate, operator)
						- valueChanged(1, _lowLevelSkill.hate, operator);

				((HeroPlayer) _host).changeHatredModulus(value);
			}
		}

		if (_highLevelSkill.resistSpecialStatus != null) {
			value = valueChanged(0, _highLevelSkill.resistSpecialStatusOdds, operator)
					- valueChanged(0, _lowLevelSkill.resistSpecialStatusOdds, operator);

			if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.DUMB) {
				_host.getResistOddsList().forbidSpellOdds += value;
			} else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.FAINT) {
				_host.getResistOddsList().insensibleOdds += value;
			} else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.SLEEP) {
				_host.getResistOddsList().sleepingOdds += value;
			} else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
				_host.getResistOddsList().physicsDeathblowOdds += value;
			} else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
				_host.getResistOddsList().magicDeathblowOdds += value;
			} else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.STOP) {
				_host.getResistOddsList().fixBodyOdds += value;
			}
		}

		if (_highLevelSkill.allSkillReleaseTime > 0) {
			ArrayList<ActiveSkill> list = _host.activeSkillList;

			ActiveSkill activeSkill;

			for (int i = 0; i < list.size(); i++) {
				activeSkill = list.get(i);

				value = valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
						.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
						_highLevelSkill.allSkillReleaseTime, operator)
						- valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
								.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
								_lowLevelSkill.allSkillReleaseTime, operator);

				((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;
				ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(), new RefreshSkillTime(_host));
			}
		}

		if (_highLevelSkill.specialSkillReleaseTimeIDList != null) {
			ActiveSkill activeSkill;

			for (int i = 0; i < _lowLevelSkill.specialSkillReleaseTimeIDList.size(); i++) {
				for (int j = 0; j < _host.activeSkillList.size(); j++) {
					activeSkill = _host.activeSkillList.get(j);

					if (activeSkill.id == _lowLevelSkill.specialSkillReleaseTimeIDList.get(i)) {
						value = valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
								.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
								_highLevelSkill.specialSkillReleaseTime, operator)
								- valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
										.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
										_lowLevelSkill.specialSkillReleaseTime, operator);

						((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;

						ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(),
								new UpdateActiveSkillNotify(activeSkill));

						break;
					}
				}
			}
		}

		return basePropertyChanged;
	}

	/**
	 * 获得新的属性强化技能
	 * 
	 * @param _newSkill
	 *            强化技能
	 * @param _host
	 *            强化的玩家
	 * @return 基础属性是否发生变化
	 */
	private boolean learnNewEnhancePropertySkill(ChangePropertyUnit _newSkill, HeroPlayer _host) {
		EMathCaluOperator operator = _newSkill.caluOperator;
		boolean basePropertyChanged = false;

		if (_newSkill.strength > 0 || _newSkill.agility > 0 || _newSkill.stamina > 0
				|| _newSkill.inte > 0 || _newSkill.spirit > 0 || _newSkill.lucky > 0
				|| _newSkill.defense > 0 || _newSkill.maxHp > 0 || _newSkill.maxMp > 0
				|| _newSkill.hitLevel > 0 || _newSkill.physicsDuckLevel > 0
				|| _newSkill.physicsDeathblowLevel > 0 || _newSkill.magicDeathblowLevel > 0
				|| _newSkill.stamina > 0 || _newSkill.stamina > 0 || _newSkill.stamina > 0
				|| _newSkill.physicsAttackInterval > 0) {
			basePropertyChanged = true;
		}

		float value = 0;

		if (_newSkill.physicsAttackHarmValue > 0) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = valueChanged(_host.getActualProperty()
						.getAdditionalPhysicsAttackHarmValue(), _newSkill.physicsAttackHarmValue,
						operator);

				_host.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
			} else {
				value = valueChanged(1, _newSkill.physicsAttackHarmValue, operator);

				_host.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
			}
		}

		if (_newSkill.bePhysicsHarmValue > 0) {
			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				value = valueChanged(_host.getActualProperty()
						.getAdditionalHarmValueBePhysicsAttack(), _newSkill.bePhysicsHarmValue,
						operator);

				_host.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
			} else {
				value = valueChanged(1, _newSkill.bePhysicsHarmValue, operator);

				_host.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
			}
		}

		if (_newSkill.magicHarmValue != 0) {
			value = valueChanged(1, _newSkill.magicHarmValue, operator);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_newSkill.magicHarmType != null) {
					_host.getActualProperty()
							.addAdditionalMagicHarm(_newSkill.magicHarmType, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarm(list);
				}
			} else {
				if (_newSkill.magicHarmType != null) {
					_host.getActualProperty().addAdditionalMagicHarmScale(_newSkill.magicHarmType,
							value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmScale(list);
				}
			}
		}

		if (_newSkill.magicHarmValueBeAttack != 0) {
			value = valueChanged(1, _newSkill.magicHarmValueBeAttack, operator);

			if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
				if (_newSkill.magicHarmTypeBeAttack != null) {
					_host.getActualProperty().addAdditionalMagicHarmBeAttack(
							_newSkill.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmBeAttack(list);
				}
			} else {
				if (_newSkill.magicHarmTypeBeAttack != null) {
					_host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(
							_newSkill.magicHarmTypeBeAttack, value);
				} else {
					MagicHarmList list = new MagicHarmList(value);
					_host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
				}
			}
		}

		if (_newSkill.hate > 0) {
			if (_host.getObjectType() == EObjectType.PLAYER) {
				value = valueChanged(1, _newSkill.hate, operator);

				((HeroPlayer) _host).changeHatredModulus(value);
			}
		}

		if (_newSkill.resistSpecialStatus != null) {
			value = valueChanged(0, _newSkill.resistSpecialStatusOdds, operator);

			if (_newSkill.resistSpecialStatus == ESpecialStatus.DUMB) {
				_host.getResistOddsList().forbidSpellOdds += value;
			} else if (_newSkill.resistSpecialStatus == ESpecialStatus.FAINT) {
				_host.getResistOddsList().insensibleOdds += value;
			} else if (_newSkill.resistSpecialStatus == ESpecialStatus.SLEEP) {
				_host.getResistOddsList().sleepingOdds += value;
			} else if (_newSkill.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
				_host.getResistOddsList().physicsDeathblowOdds += value;
			} else if (_newSkill.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
				_host.getResistOddsList().magicDeathblowOdds += value;
			} else if (_newSkill.resistSpecialStatus == ESpecialStatus.STOP) {
				_host.getResistOddsList().fixBodyOdds += value;
			}
		}

		if (_newSkill.allSkillReleaseTime > 0) {
			ArrayList<ActiveSkill> list = _host.activeSkillList;

			ActiveSkill activeSkill;

			for (int i = 0; i < list.size(); i++) {
				activeSkill = list.get(i);

				value = valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
						.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
						_newSkill.allSkillReleaseTime, operator);

				((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;

				ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(),
						new UpdateActiveSkillNotify(activeSkill));
			}
		}

		if (_newSkill.specialSkillReleaseTimeIDList != null) {
			ActiveSkill activeSkill;

			for (int i = 0; i < _newSkill.specialSkillReleaseTimeIDList.size(); i++) {
				for (int j = 0; j < _host.activeSkillList.size(); j++) {
					activeSkill = _host.activeSkillList.get(j);

					if (activeSkill.id == _newSkill.specialSkillReleaseTimeIDList.get(i)) {
						value = valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
								.getSkillUnitRef(activeSkill.skillUnit.id))).releaseTime,
								_newSkill.specialSkillReleaseTime, operator);

						((ActiveSkillUnit) activeSkill.skillUnit).releaseTime += value;

						ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(),
								new UpdateActiveSkillNotify(activeSkill));

						break;
					}
				}
			}
		}

		return basePropertyChanged;
	}

	/**
	 * 获取技能模板
	 * 
	 * @param _skillID
	 * @return
	 */
	public Skill getSkillModel(int _skillID) {
		return SkillDict.getInstance().getSkill(_skillID);
	}

	/**
	 * 获得怪物技能
	 * 
	 * @param _skillID
	 * @return
	 */
	public Skill getMonsterSkillModel(int _skillID) {
		return MonsterSkillDict.getInstance().getSkillUnitRef(_skillID);
	}

	/**
	 * 获取技能实例
	 * 
	 * @param _skillID
	 * @return
	 */
	public Skill getSkillIns(int _skillID) {
		Skill skill = SkillDict.getInstance().getSkill(_skillID);

		if (null != skill)
			try {
				return skill.clone();
			} catch (CloneNotSupportedException e) {

			}

		return null;
	}

	/**
	 * 宠物使用被动技能
	 * 
	 * @param _releaser
	 * @param _skill
	 * @param _target
	 * @param _type
	 *            1:加技能属性 2:清除技能属性
	 * @return
	 */
	public boolean petReleasePassiveSkill(Pet pet, int type) {
		/*if (null == pet) {
			return false;
		}
		List<PetPassiveSkill> skillList = pet.petPassiveSkillList;
		if (type == 1) {// 加持技能属性
			for (PetPassiveSkill skill : skillList) {
				petPassiveSkill(pet, skill);
			}
		}
		if (type == 2) {// 清除技能属性
			for (PetPassiveSkill skill : skillList) {
				petClearPassiveSkill(pet, skill);
			}
		}
		return true;*/
        return false;
	}

	/**
	 * 清除技能属性
	 * 
	 * @param pet
	 * @param _skill
	 */
	private void petClearPassiveSkill(Pet pet, PetPassiveSkill _skill) {
		if (_skill.targetType == ETargetType.MYSELF) {
			pet.getActualProperty().setAgility(
					(int) caluValue(pet.getActualProperty().getAgility(), _skill.agility,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setInte(
					(int) caluValue(pet.getActualProperty().getInte(), _skill.inte,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setLucky(
					(int) caluValue(pet.getActualProperty().getLucky(), _skill.lucky,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setStrength(
					(int) caluValue(pet.getActualProperty().getStrength(), _skill.strength,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setSpirit(
					(int) caluValue(pet.getActualProperty().getSpirit(), _skill.spirit,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setHitLevel(
					(short) caluValue(pet.getActualProperty().getHitLevel(), _skill.hitLevel,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setMpMax(
					(int) caluValue(pet.getActualProperty().getMpMax(), _skill.maxMp,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setMagicDeathblowLevel(
					(short) caluValue(pet.getActualProperty().getMagicDeathblowLevel(),
							_skill.magicDeathblowLevel, EMathCaluOperator
									.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().setPhysicsDeathblowLevel(
					(short) caluValue(pet.getActualProperty().getPhysicsDeathblowLevel(),
							_skill.physicsDeathblowLevel, EMathCaluOperator
									.getReverseCaluOperator(_skill.caluOperator)));
			pet.getActualProperty().addAdditionalHarmValueBePhysicsAttack(
					(int) _skill.physicsAttackHarmValue);
			if (null != _skill.magicHarmType)
				pet.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType,
						_skill.magicHarmValue);
			pet.setBaseAttackImmobilityTime((int) caluValue(pet.getActualAttackImmobilityTime(),
					_skill.physicsAttackInterval, EMathCaluOperator
							.getReverseCaluOperator(_skill.caluOperator)));
		}
		if (_skill.targetType == ETargetType.OWNER) {
			HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(pet.masterID);
			player.getActualProperty().setAgility(
					(int) caluValue(player.getActualProperty().getAgility(), _skill.agility,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setInte(
					(int) caluValue(player.getActualProperty().getInte(), _skill.inte,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setLucky(
					(int) caluValue(player.getActualProperty().getLucky(), _skill.lucky,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setStrength(
					(int) caluValue(player.getActualProperty().getStrength(), _skill.strength,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setSpirit(
					(int) caluValue(player.getActualProperty().getSpirit(), _skill.spirit,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setHitLevel(
					(short) caluValue(player.getActualProperty().getHitLevel(), _skill.hitLevel,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setMpMax(
					(int) caluValue(player.getActualProperty().getMpMax(), _skill.maxMp,
							EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setMagicDeathblowLevel(
					(short) caluValue(player.getActualProperty().getMagicDeathblowLevel(),
							_skill.magicDeathblowLevel, EMathCaluOperator
									.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().setPhysicsDeathblowLevel(
					(short) caluValue(player.getActualProperty().getPhysicsDeathblowLevel(),
							_skill.physicsDeathblowLevel, EMathCaluOperator
									.getReverseCaluOperator(_skill.caluOperator)));
			player.getActualProperty().addAdditionalHarmValueBePhysicsAttack(
					(int) _skill.physicsAttackHarmValue);
			if (null != _skill.magicHarmType)
				player.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType,
						_skill.magicHarmValue);
			player.setBaseAttackImmobilityTime((int) caluValue(player
					.getActualAttackImmobilityTime(), _skill.physicsAttackInterval,
					EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));

		}
	}

	/**
	 * 加持技能属性
	 * 
	 * @param pet
	 * @param _skill
	 */
	private void petPassiveSkill(Pet pet, PetPassiveSkill _skill) {
		if (_skill.targetType == ETargetType.MYSELF) {
			pet.getActualProperty().setAgility(
					(int) caluValue(pet.getActualProperty().getAgility(), _skill.agility,
							_skill.caluOperator));
			pet.getActualProperty().setInte(
					(int) caluValue(pet.getActualProperty().getInte(), _skill.inte,
							_skill.caluOperator));
			pet.getActualProperty().setLucky(
					(int) caluValue(pet.getActualProperty().getLucky(), _skill.lucky,
							_skill.caluOperator));
			pet.getActualProperty().setStrength(
					(int) caluValue(pet.getActualProperty().getStrength(), _skill.strength,
							_skill.caluOperator));
			pet.getActualProperty().setSpirit(
					(int) caluValue(pet.getActualProperty().getSpirit(), _skill.spirit,
							_skill.caluOperator));
			pet.getActualProperty().setHitLevel(
					(short) caluValue(pet.getActualProperty().getHitLevel(), _skill.hitLevel,
							_skill.caluOperator));
			pet.getActualProperty().setMpMax(
					(int) caluValue(pet.getActualProperty().getMpMax(), _skill.maxMp,
							_skill.caluOperator));
			pet.getActualProperty().setMagicDeathblowLevel(
					(short) caluValue(pet.getActualProperty().getMagicDeathblowLevel(),
							_skill.magicDeathblowLevel, _skill.caluOperator));
			pet.getActualProperty().setPhysicsDeathblowLevel(
					(short) caluValue(pet.getActualProperty().getPhysicsDeathblowLevel(),
							_skill.physicsDeathblowLevel, _skill.caluOperator));
			pet.getActualProperty().addAdditionalHarmValueBePhysicsAttack(
					(int) _skill.physicsAttackHarmValue);
			if (null != _skill.magicHarmType)
				pet.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType,
						_skill.magicHarmValue);
			pet.setBaseAttackImmobilityTime((int) caluValue(pet.getActualAttackImmobilityTime(),
					_skill.physicsAttackInterval, _skill.caluOperator));
		}
		if (_skill.targetType == ETargetType.OWNER) {
			HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(pet.masterID);
			player.getActualProperty().setAgility(
					(int) caluValue(player.getActualProperty().getAgility(), _skill.agility,
							_skill.caluOperator));
			player.getActualProperty().setInte(
					(int) caluValue(player.getActualProperty().getInte(), _skill.inte,
							_skill.caluOperator));
			player.getActualProperty().setLucky(
					(int) caluValue(player.getActualProperty().getLucky(), _skill.lucky,
							_skill.caluOperator));
			player.getActualProperty().setStrength(
					(int) caluValue(player.getActualProperty().getStrength(), _skill.strength,
							_skill.caluOperator));
			player.getActualProperty().setSpirit(
					(int) caluValue(player.getActualProperty().getSpirit(), _skill.spirit,
							_skill.caluOperator));
			player.getActualProperty().setHitLevel(
					(short) caluValue(player.getActualProperty().getHitLevel(), _skill.hitLevel,
							_skill.caluOperator));
			player.getActualProperty().setMpMax(
					(int) caluValue(player.getActualProperty().getMpMax(), _skill.maxMp,
							_skill.caluOperator));
			player.getActualProperty().setMagicDeathblowLevel(
					(short) caluValue(player.getActualProperty().getMagicDeathblowLevel(),
							_skill.magicDeathblowLevel, _skill.caluOperator));
			player.getActualProperty().setPhysicsDeathblowLevel(
					(short) caluValue(player.getActualProperty().getPhysicsDeathblowLevel(),
							_skill.physicsDeathblowLevel, _skill.caluOperator));
			player.getActualProperty().addAdditionalHarmValueBePhysicsAttack(
					(int) _skill.physicsAttackHarmValue);
			if (null != _skill.magicHarmType)
				player.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType,
						_skill.magicHarmValue);
			player.setBaseAttackImmobilityTime((int) caluValue(player
					.getActualAttackImmobilityTime(), _skill.physicsAttackInterval,
					_skill.caluOperator));

		}
	}

	private float caluValue(float baseValue, float _caluValue, EMathCaluOperator caluOperator) {
		switch (caluOperator) {
		case ADD: {
			return baseValue + _caluValue;
		}
		case DEC: {
			return baseValue - _caluValue;
		}
		case MUL: {
			return baseValue * _caluValue;
		}
		case DIV: {
			return baseValue / _caluValue;
		}
		default:
			return baseValue + _caluValue;
		}
	}

	/**
	 * 宠物使用主动技能
	 * 
	 * @param _releaser
	 * @param _skillID
	 * @param _target
	 * @param _direction
	 * @return
	 */
	public boolean petReleaseSkill(Pet _releaser, int _skillID, ME2GameObject _target,
			byte _direction) {
		log.debug("petReleaseSkill start ... " + _releaser.isDied() + " -- "
				+ " _target isEnable = " + _target.isEnable());
		if (!_releaser.isEnable() || _releaser.isDied() || null == _target || !_target.isEnable()) {
			return false;
		}

		PetActiveSkill activeSkill = _releaser.getPetActiveSkillByID(_skillID);
		if (activeSkill == null) {
			return false;
		}
		log.debug("activeSkill  = " + activeSkill.name);
		// 冷却时间未到
		if (activeSkill.lastUseTime + activeSkill.coolDownTime * 1000 > System.currentTimeMillis()) {
			log.debug("宠物技能攻击，冷却时间未到。");
			return false;
		}

		log.debug("pet skill attack target type = " + activeSkill.targetType);

		if (activeSkill.targetType == ETargetType.ENEMY) {
			// 暂不考虑宠物群体技能.
			if (ETargetRangeType.SINGLE == activeSkill.targetRangeType) {
				if (activeSkill.skillType == EActiveSkillType.PHYSICS) {
					singlePhysicsAttackSkill(_releaser, _target, activeSkill);
				} else {
					singleMagicAttackSkill(_releaser, _target, activeSkill);
				}
			}
		} else if (activeSkill.targetType == ETargetType.OWNER) {
			singleResumeSkill(_releaser, _target, activeSkill);
		} else if (activeSkill.targetType == ETargetType.MYSELF) {
			singleResumeSkill(_releaser, _releaser, activeSkill);
		} else if (activeSkill.targetType == ETargetType.DIER) {
			ResponseMessageQueue.getInstance().put(
					((HeroPlayer) _target).getMsgQueueIndex(),
					new ReviveConfirm(_releaser.getName(), _releaser.getCellX(), _releaser
							.getCellY(), activeSkill.resumeHp, activeSkill.resumeMp, (short)60));
		}

		activeSkill.lastUseTime = System.currentTimeMillis();

		return true;
	}

	/**
	 * 玩家施放技能
	 * 
	 * @param _releaser
	 *            施放者
	 * @param _skillID
	 *            技能编号
	 * @param _target
	 *            作用目标（怪物、其他玩家）
	 * @param _direction
	 *            施放时的方向
	 * @return 是否施放成功
	 */
	public boolean playerReleaseSkill(HeroPlayer _releaser, int _skillID, ME2GameObject _target,
			byte _direction) {
		if (!_releaser.isEnable() || _releaser.isDead() || null == _target || !_target.isEnable()) {
			return false;
		}

		ActiveSkill activeSkill = _releaser.activeSkillTable.get(_skillID);

		if (null == activeSkill || !canReleaseSkill(_releaser, _target, activeSkill)) {
			return false;
		}

		ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;

		if (skillUnit.targetType == ETargetType.ENEMY) {
			// add: zhengl
			// date: 2010-10-17
			// note: 添加宠物目标
			// PetServiceImpl.getInstance().setAttackTarget(_releaser, _target);
			// //改为客户端控制
			if (ETargetRangeType.SINGLE == skillUnit.targetRangeType) {
				if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
					singlePhysicsAttackSkill(_releaser, _target, activeSkill);
				} else {
					singleMagicAttackSkill(_releaser, _target, activeSkill);
				}
			} else if (ETargetRangeType.SOME == skillUnit.targetRangeType) {
				ArrayList<ME2GameObject> targetList = null;

				if (EAOERangeBaseLine.RELEASER == skillUnit.rangeBaseLine) {
					//以自身为目标.
					_target = _releaser;
				}

				if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
					targetList = MapServiceImpl.getInstance().getAttackableObjectListInForeRange(
							_target.where(), skillUnit.rangeX, skillUnit.rangeY, _releaser,
							skillUnit.rangeTargetNumber);
				} else {
					targetList = MapServiceImpl.getInstance().getAttackableObjectListInRange(
							_target.where(), _target.getCellX(), _target.getCellY(),
							skillUnit.rangeX, _releaser, skillUnit.rangeTargetNumber);
				}

				if (null != targetList && targetList.size() > 0) {
					if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
						groupPhysicsAttackSkill(_releaser, targetList, activeSkill);
					} else {
						groupMagicAttackSkill(_releaser, targetList, activeSkill);
					}
				}
			}
		} else if (skillUnit.targetType == ETargetType.FRIEND) {
			if (ETargetRangeType.SINGLE == skillUnit.targetRangeType) {
				singleResumeSkill(_releaser, _target, activeSkill);
			} else if (ETargetRangeType.SOME == skillUnit.targetRangeType) {
				ArrayList<HeroPlayer> targetList = null;

				HeroPlayer rangeBaseLintTarget = skillUnit.rangeBaseLine == EAOERangeBaseLine.RELEASER ? _releaser
						: (HeroPlayer) _target;

				if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
					targetList = MapServiceImpl.getInstance().getFriendsPlayerListInForeRange(
							rangeBaseLintTarget, skillUnit.rangeX, skillUnit.rangeY,
							skillUnit.rangeTargetNumber);
				} else {
					targetList = MapServiceImpl.getInstance().getFriendsPlayerInRange(
							rangeBaseLintTarget, _target.getCellX(), _target.getCellY(),
							skillUnit.rangeX, skillUnit.rangeTargetNumber);
				}

				if (null != targetList && targetList.size() > 0) {
					groupResumeSkill(_releaser, targetList, activeSkill);
				}
			} else {
				ArrayList<HeroPlayer> targetList = null;

				if (EAOERangeBaseLine.RELEASER == skillUnit.rangeBaseLine) {
					_target = _releaser;
				}

				if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
					targetList = MapServiceImpl.getInstance().getGroupPlayerListInForeRange(
							_releaser, skillUnit.rangeX, skillUnit.rangeY,
							skillUnit.rangeTargetNumber);
				} else {
					targetList = MapServiceImpl.getInstance().getGroupPlayerInRange(_releaser,
							_target.getCellX(), _target.getCellY(), skillUnit.rangeX,
							skillUnit.rangeTargetNumber);
				}

				if (null != targetList && targetList.size() > 0) {
					groupResumeSkill(_releaser, targetList, activeSkill);
				} else {
					// 周围没有团队成员,对自己释放.
					singleResumeSkill(_releaser, _releaser, activeSkill);
				}

			}
		} else if (skillUnit.targetType == ETargetType.MYSELF) {
			singleResumeSkill(_releaser, _releaser, activeSkill);
		} else if (skillUnit.targetType == ETargetType.DIER) {
			ResponseMessageQueue.getInstance().put(
					((HeroPlayer) _target).getMsgQueueIndex(),
					new ReviveConfirm(_releaser.getName(), _releaser.getCellX(), _releaser
							.getCellY(), skillUnit.resumeHp, skillUnit.resumeMp, (short)60));
		}

		activeSkill.reduceCoolDownTime = activeSkill.coolDownTime;

		if (activeSkill.coolDownID > 0
				&& activeSkill.reduceCoolDownTime > SkillServiceImpl.VALIDATE_CD_TIME) {
			for (ActiveSkill otherSkill : _releaser.activeSkillList) {
				if (otherSkill.id != activeSkill.id
						&& otherSkill.coolDownID == activeSkill.coolDownID) {
					otherSkill.reduceCoolDownTime = otherSkill.coolDownTime;
				}
			}
		}

		return true;
	}

	/**
	 * 发送单体技能动画
	 * 
	 * @param _releaser
	 *            施放者
	 * @param _acceptor
	 *            播放目标
	 * @param _releaseAnimation
	 *            施法动画图片编号
	 * @param _accepteAnimation
	 *            被作用动画图片编号
	 */
	public void sendSingleSkillAnimation(ME2GameObject _releaser, ME2GameObject _target,
			int _releaseAnimation, int _releaseImage, int _accepteAnimation, int _accepteImage, 
			byte _actionID, byte _tier, byte _reHeight, byte _accHeight, byte _isDirection) {
		if (_accepteAnimation == 0 && _releaseAnimation == 0)
			return;

		AbsResponseMessage msg = new SkillAnimationNotify(_releaser, _releaseAnimation, _releaseImage, 
				_target, _accepteAnimation, _accepteImage, _actionID, _tier, _reHeight, _accHeight, 
				_isDirection);

		if (EObjectType.PLAYER == _releaser.getObjectType()) {
			ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), msg);

			MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, true,
					((HeroPlayer) _releaser).getID());
		} else {
			MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, false, 0);
		}
	}

	/**
	 * 发送群体攻击技能动画
	 * 
	 * @param _releaser
	 *            施放者
	 * @param _acceptor
	 *            播放目标
	 * @param _releaseAnimation
	 *            施法动画图片编号
	 * @param _accepteAnimation
	 *            被作用动画图片编号
	 */
	public void sendGroupAttackSkillAnimation(ME2GameObject _releaser,
			ArrayList<ME2GameObject> _targetList, int _releaseAnimation, int _releaseImage,
			int _accepteAnimation, int _accepteImage, byte _actionID, byte _tier, 
			byte _reHeight, byte _accHeight, byte _isDirection) {
		if (_accepteAnimation == 0 && _releaseAnimation == 0)
			return;

		AbsResponseMessage msg = new SkillAnimationNotify(_releaser, _targetList, _releaseAnimation,
				_releaseImage, _accepteAnimation, _accepteImage, _actionID, _tier, _reHeight, 
				_accHeight, _isDirection);

		if (EObjectType.PLAYER == _releaser.getObjectType()) {
			ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), msg);

			MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, true,
					((HeroPlayer) _releaser).getID());
		} else {
			MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, false, 0);
		}
	}

	/**
	 * 单体物理攻击技能
	 * 
	 * @param _player
	 *            技能施放者
	 * @param _target
	 *            目标
	 * @param _activeSkillUnit
	 *            施放的技能单元
	 */
	private void singlePhysicsAttackSkill(HeroPlayer _player, ME2GameObject _target,
			ActiveSkill _activeSkill) {
		ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
		;
		FightServiceImpl.getInstance().refreshFightTime(_player, _target);
		sendSingleSkillAnimation(_player, _target, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
				activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
				activeSkillUnit.heightRelation, activeSkillUnit.isDirection);

		if (RANDOM.nextInt(100) > (CEService.attackPhysicsHitOdds(_player.getActualProperty()
				.getLucky(), _player.getActualProperty().getHitLevel(), _player.getLevel(), _target
				.getLevel()) - CEService.attackPhysicsDuckOdds(_player.getLevel(), _target
				.getActualProperty().getAgility(), _target.getActualProperty().getLucky(), _target
				.getActualProperty().getPhysicsDuckLevel(), _target.getLevel()))) {
			EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.ACTIVE,
					true);
			checkTouchSkill(_player, _target, ETouchType.ACTIVE, true);
			FightServiceImpl.getInstance().processMiss(_player, _target);

			return;
		}
		//add by zhengl; date: 2011-05-17; note: 建立嘲讽/添加仇恨类技能的关系系统.
		if (activeSkillUnit.hateValue > 0 && _target instanceof Monster) 
		{
			Monster monster = (Monster)_target;
			if (monster.getAttackerAtFirst() == null) {
				monster.setAttackerAtFirst(_player);
			}
			monster.addTargetHatredValue(_player, activeSkillUnit.hateValue);
		}
		if (activeSkillUnit.physicsHarmValue > 0 || activeSkillUnit.weaponHarmMult > 0) {
			int attack = 0;// 攻击力
			int harmValue = 0;// 伤害值

			if (activeSkillUnit.weaponHarmMult > 0) {
				EquipmentInstance weapon = _player.getBodyWear().getWeapon();

				if (null != weapon) {
					//edit by zhengl; date: 2011-03-07; note: 物理技能算法变更
					attack = CEService.weaponPhysicsAttackBySkill(
							_player.getActualProperty().getActualPhysicsAttack(),
							activeSkillUnit.weaponHarmMult, 
							activeSkillUnit.physicsHarmValue);
				}
			}

			harmValue = CEService.physicsHarm(_player.getLevel(), attack, _target.getLevel(),
					_target.getActualProperty().getDefense());

			boolean isDeathblow = false;

			float physicsDeathblowOdds = CEService.attackPhysicsDeathblowOdds(_player
					.getActualProperty().getAgility(), _player.getActualProperty()
					.getPhysicsDeathblowLevel(), _player.getLevel(), _target.getLevel());

			if (RANDOM.nextInt(100) 
					<= (physicsDeathblowOdds - _target.getResistOddsList().physicsDeathblowOdds)) 
			{
				harmValue = CEService.calculateDeathblowHarm(harmValue, _player.getActualProperty()
						.getLucky());
				isDeathblow = true;
				// add by zhengl ; date: 2010-12-05 ; note: 物理暴击触法效果
				EffectServiceImpl.getInstance().checkTouchEffect(_player, _target,
						ETouchType.TOUCH_DEATHBLOW_BY_PHYSICS, true);
				EffectServiceImpl.getInstance().checkTouchEffect(_target, _player,
						ETouchType.BE_DEATHBLOW_BY_PHYSICS, false);
			}
			//edit by zhengl; date: 2011-03-06; note: 为下面的各种附加伤害做日志监控
			int tempHarm = 0;
			tempHarm += harmValue * _player.getActualProperty().getAdditionalPhysicsAttackHarmScale();
			System.out.println(
					"乘数:" +_player.getActualProperty().getAdditionalPhysicsAttackHarmScale());
			log.info("附加伤害第1次:" + tempHarm);
			tempHarm += _player.getActualProperty().getAdditionalPhysicsAttackHarmValue();
			log.info("附加伤害第2次:" + tempHarm);
			tempHarm += harmValue 
			* _target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack();
			log.info("附加伤害第3次:" + tempHarm);
			tempHarm += _target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();
			log.info("附加伤害第4次:" + tempHarm);
			harmValue += tempHarm;
			if (FightServiceImpl.getInstance().processReduceHp(_player, _target, harmValue, true,
					isDeathblow, null)) 
			{
				EffectServiceImpl.getInstance().checkTouchEffect(_player, _target,
						ETouchType.ATTACK_BY_PHYSICS, true);
				checkTouchSkill(_player, _target, ETouchType.ATTACK_BY_PHYSICS, true);

				return;
			}
		}

//		EffectServiceImpl.getInstance().checkTouchEffect(_player, _target,
//				activeSkillUnit.activeTouchType, true);
//		checkTouchSkill(_player, _target, activeSkillUnit.activeTouchType, true);
//		EffectServiceImpl.getInstance().checkTouchEffect(_target, _player,
//				activeSkillUnit.passiveTouchType, true);
//		checkTouchSkill(_target, _player, activeSkillUnit.passiveTouchType, true);

		if (null != _activeSkill.addEffectUnit) {
			EffectServiceImpl.getInstance().appendSkillEffect(_player, _target,
					_activeSkill.addEffectUnit);
		} else if (null != _activeSkill.addSkillUnit) {
			additionalSkillUnitActive(_player, _target,
					(ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1);
		}

		if (null != activeSkillUnit.additionalOddsActionUnitList) {
			for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
				if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
					additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) SkillUnitDict
							.getInstance()
							.getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID),
							additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
				} else {
					//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
//					if (RANDOM.nextFloat() <= additionalActionUnit.activeOdds) {
//						EffectServiceImpl.getInstance().appendSkillEffect(_player, _target,
//								additionalActionUnit.skillOrEffectUnitID);
//					}
					if(RANDOM.nextInt(100) 
							<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
						log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
						EffectServiceImpl.getInstance().appendSkillEffect(_player, _target,
								additionalActionUnit.skillOrEffectUnitID);
					}
				}
			}
		}
	}

	private void singlePhysicsAttackSkill(Monster _monster, ME2GameObject _target,
			ActiveSkill _activeSkill) {
		ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
		FightServiceImpl.getInstance().refreshFightTime(_monster, _target);
		sendSingleSkillAnimation(_monster, _target, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
				activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
				activeSkillUnit.heightRelation, activeSkillUnit.isDirection);

		float odds = CEService
				.attackPhysicsHitOdds(_monster.getActualProperty().getLucky(), _monster
						.getActualProperty().getHitLevel(), _monster.getLevel(), _target.getLevel())
				- CEService.attackPhysicsDuckOdds(_monster.getLevel(), _target.getActualProperty()
						.getAgility(), _target.getActualProperty().getLucky(), _target
						.getActualProperty().getPhysicsDuckLevel(), _target.getLevel());
		int radom = RANDOM.nextInt(100);
		if (radom > odds) {
			EffectServiceImpl.getInstance().checkTouchEffect(_monster, _target, ETouchType.ACTIVE,
					true);
			checkTouchSkill(_monster, _target, ETouchType.ACTIVE, true);
			FightServiceImpl.getInstance().processMiss(_monster, _target);

			return;
		}

		if (activeSkillUnit.physicsHarmValue > 0) {
			int attack = activeSkillUnit.physicsHarmValue;
			int harmValue = 0;// 伤害值

			harmValue = CEService.physicsHarm(_monster.getLevel(), attack, _target.getLevel(),
					_target.getActualProperty().getDefense());
			boolean isDeathblow = false;

			float physicsDeathblowOdds = CEService.attackPhysicsDeathblowOdds(_monster
					.getActualProperty().getAgility(), _monster.getActualProperty()
					.getPhysicsDeathblowLevel(), _monster.getLevel(), _target.getLevel());

			if (RANDOM.nextInt(100) <= (physicsDeathblowOdds - _target.getResistOddsList().physicsDeathblowOdds)) {
				harmValue = CEService.calculateDeathblowHarm(harmValue, _monster
						.getActualProperty().getLucky());
				isDeathblow = true;
			}

			harmValue += harmValue
					* _monster.getActualProperty().getAdditionalPhysicsAttackHarmScale();
			harmValue += _monster.getActualProperty().getAdditionalPhysicsAttackHarmValue();
			harmValue += harmValue
					* _target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack();
			harmValue += _target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();

			if (FightServiceImpl.getInstance().processReduceHp(_monster, _target, harmValue, true,
					isDeathblow, null)) {
				return;
			}
		}
		EffectServiceImpl.getInstance().checkTouchEffect(_monster, _target,
				activeSkillUnit.activeTouchType, true);
		EffectServiceImpl.getInstance().checkTouchEffect(_target, _monster,
				activeSkillUnit.passiveTouchType, true);

		if (null != _activeSkill.addEffectUnit) {
			EffectServiceImpl.getInstance().appendSkillEffect(_monster, _target,
					_activeSkill.addEffectUnit);
		}

		if (null != activeSkillUnit.additionalOddsActionUnitList) {
			for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
				//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
//				if (RANDOM.nextFloat() <= additionalActionUnit.activeOdds) {
//					EffectServiceImpl.getInstance().appendSkillEffect(_monster, _target,
//							additionalActionUnit.skillOrEffectUnitID);
//				}
				if(RANDOM.nextInt(100) 
						<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
					log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
					EffectServiceImpl.getInstance().appendSkillEffect(_monster, _target,
							additionalActionUnit.skillOrEffectUnitID);
				}
			}
		}
	}

	/**
	 * 宠物物理技能处理
	 * 
	 * @param _pet
	 * @param _target
	 * @param _activeSkill
	 */
	private void singlePhysicsAttackSkill(Pet _pet, ME2GameObject _target,
			PetActiveSkill _activeSkill) {
		/*log.debug("single physics attack skill start ...");
		PetActiveSkill activeSkillUnit = _activeSkill;
		FightServiceImpl.getInstance().refreshFightTime(_pet, _target);
		*//**待完善*//*
		sendSingleSkillAnimation(_pet, _target, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseAnimationID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeAnimationID, (byte)-1, (byte)1, (byte)2, (byte)2, (byte)1);

		float odds = CEService.attackPhysicsHitOdds(_pet.getActualProperty().getLucky(), _pet
				.getActualProperty().getHitLevel(), _pet.getLevel(), _target.getLevel())
				- CEService.attackPhysicsDuckOdds(_pet.getLevel(), _target.getActualProperty()
						.getAgility(), _target.getActualProperty().getLucky(), _target
						.getActualProperty().getPhysicsDuckLevel(), _target.getLevel());
		int radom = RANDOM.nextInt(100);
		log.debug("@ odds = " + odds + " , radom = " + radom);
		if (radom > odds) {
			EffectServiceImpl.getInstance()
					.checkTouchEffect(_pet, _target, ETouchType.ACTIVE, true);
			checkTouchSkill(_pet, _target, ETouchType.ACTIVE, true);
			FightServiceImpl.getInstance().processMiss(_pet, _target);

			return;
		}

		if (activeSkillUnit.physicsHarmValue > 0 || activeSkillUnit.atkMult > 0) {
			int attack = activeSkillUnit.physicsHarmValue; // 宠物技能现在只按直接伤害计算
			int harmValue = 0;// 伤害值

			harmValue = CEService.physicsHarm(_pet.getLevel(), attack, _target.getLevel(), _target
					.getActualProperty().getDefense());
			boolean isDeathblow = false;

			float physicsDeathblowOdds = CEService.attackPhysicsDeathblowOdds(_pet
					.getActualProperty().getAgility(), _pet.getActualProperty()
					.getPhysicsDeathblowLevel(), _pet.getLevel(), _target.getLevel());

			if (RANDOM.nextInt(100) <= (physicsDeathblowOdds - _target.getResistOddsList().physicsDeathblowOdds)) {
				harmValue = CEService.calculateDeathblowHarm(harmValue, _pet.getActualProperty()
						.getLucky());
				isDeathblow = true;
			}

			harmValue += harmValue * _pet.getActualProperty().getAdditionalPhysicsAttackHarmScale();
			harmValue += _pet.getActualProperty().getAdditionalPhysicsAttackHarmValue();
			harmValue += harmValue
					* _target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack();
			harmValue += _target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();

			log.debug("pet skill attack harm value = " + harmValue);

			if (FightServiceImpl.getInstance().processReduceHp(_pet, _target, harmValue, true,
					isDeathblow, null)) {
				return;
			}
		}
		// 宠物技能可能附带抓伤,咬伤等效果
		if (null != _activeSkill.addEffectUnit) {
			EffectServiceImpl.getInstance().appendSkillEffect(_pet, _target,
					_activeSkill.addEffectUnit);
		}*/
	}

	/**
	 * 单体魔法攻击技能
	 * 
	 * @param _player
	 *            技能施放者
	 * @param _target
	 *            目标
	 * @param _activeSkillUnit
	 *            施放的技能单元
	 */
	private void singleMagicAttackSkill(HeroPlayer _player, ME2GameObject _target,
			ActiveSkill _activeSkill) {
		ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
		FightServiceImpl.getInstance().refreshFightTime(_player, _target);
		sendSingleSkillAnimation(_player, _target, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
				activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
				activeSkillUnit.heightRelation, activeSkillUnit.isDirection);

		//edit by zhengl; date: 2011-04-18; note: 使用技能后的特效出发提前,简化代码
		//1, 使用魔法触发
		EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.USE_MAGIC, true);
		checkTouchSkill(_player, _target, ETouchType.USE_MAGIC, true);
		if (activeSkillUnit.needMagicHit) {
			float odds = CEService.attackMagicHitOdds(
					_player.getActualProperty().getLucky(), 
					_player.getActualProperty().getHitLevel(), 
					_player.getLevel(),
					_target.getLevel(), 
					_target.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(
							activeSkillUnit.magicHarmType));
			if (RANDOM.nextInt(100) > odds) {
				FightServiceImpl.getInstance().processMiss(_player, _target);

				return;
			}
		}

		if (activeSkillUnit.magicHarmHpValue > 0) {
			int magicAttack = CEService.magicHarmBySkill(
					(int) _player.getActualProperty().getBaseMagicHarmList()
							.getEMagicHarmValue(activeSkillUnit.magicHarmType),
					activeSkillUnit.magicHarmHpValue, 
					activeSkillUnit.releaseTime, 
					_player.getLevel(), 
					_activeSkill.level);
			int harmValue = CEService.attackMagicHarm(_player.getLevel(), magicAttack, _target
					.getLevel(), _target.getActualProperty().getMagicFastnessList()
					.getEMagicFastnessValue(activeSkillUnit.magicHarmType));

			boolean isDeathblow = false;

			float magicDeathblowOdds = CEService.attackMagicDeathblowOdds(
					_player.getActualProperty().getMagicDeathblowLevel(),
					_player.getLevel(), _target.getLevel());

			if (RANDOM.nextInt(100) 
					<= (magicDeathblowOdds - _target.getResistOddsList().magicDeathblowOdds)) {
				harmValue = CEService.calculateDeathblowHarm(harmValue, 
						_player.getActualProperty().getLucky());
				isDeathblow = true;
				//2, 魔法暴击出发(特殊情况)
				EffectServiceImpl.getInstance().checkTouchEffect(_player, _target,
						ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
				EffectServiceImpl.getInstance().checkTouchEffect(_target, _player,
						ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, false);
			}
//			System.out.println("运算前伤害" + harmValue);
			harmValue += harmValue * _player.getActualProperty().getAdditionalMagicHarmScale(
					activeSkillUnit.magicHarmType);
//			System.out.println("施法者法术加成之后伤害:" + harmValue);
			harmValue += _player.getActualProperty().getAdditionalMagicHarm(
					activeSkillUnit.magicHarmType);
//			System.out.println("施法者法术附加之后伤害:" + harmValue);
			harmValue += harmValue * _target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(
					activeSkillUnit.magicHarmType);
//			System.out.println("被攻击者法术加成之后伤害:" + harmValue);
			harmValue += _target.getActualProperty().getAdditionalMagicHarmBeAttack(
					activeSkillUnit.magicHarmType);
//			System.out.println("被攻击者法术附加之后伤害:" + harmValue);
			//3, 魔法攻击触发
			EffectServiceImpl.getInstance().checkTouchEffect(
					_player, _target, ETouchType.ATTACK_BY_MAGIC, true);
			checkTouchSkill(_player, _target, ETouchType.ATTACK_BY_MAGIC, true);
			if (FightServiceImpl.getInstance().processReduceHp(_player, _target, harmValue, true,
					isDeathblow, activeSkillUnit.magicHarmType)) {
				return;
			}
		}
		
		//add by zhengl; date: 2011-05-17; note: 建立嘲讽/添加仇恨类技能的关系系统.
		if (activeSkillUnit.hateValue > 0 && _target instanceof Monster) 
		{
			Monster monster = (Monster)_target;
			if (monster.getAttackerAtFirst() == null) {
				monster.setAttackerAtFirst(_player);
			}
			monster.addTargetHatredValue(_player, activeSkillUnit.hateValue);
		}

		if (activeSkillUnit.magicHarmMpValue != 0) {
			_player.addMp(-activeSkillUnit.resumeMp);

			FightServiceImpl.getInstance().processSingleTargetMpChange(_player, false);
		}
		//4, 使用魔法后对目标触发
		EffectServiceImpl.getInstance().checkTouchEffect(_target, _player, ETouchType.USE_MAGIC, true);
		checkTouchSkill(_target, _player, ETouchType.USE_MAGIC, true);

		if (null != _activeSkill.addEffectUnit) {
			EffectServiceImpl.getInstance().appendSkillEffect(_player, _target,
					_activeSkill.addEffectUnit);
		} else if (null != _activeSkill.addSkillUnit) {
			additionalSkillUnitActive(_player, _target,
					(ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1);
		}
		//附带在主动技能上的 特效效果触发过程
		if (null != activeSkillUnit.additionalOddsActionUnitList) {
			for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
				if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
					additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) SkillUnitDict
							.getInstance()
							.getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID),
							additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
				} else {
					//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
					if(RANDOM.nextInt(100) 
							<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
						log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
						EffectServiceImpl.getInstance().appendSkillEffect(_player, _target,
								additionalActionUnit.skillOrEffectUnitID);
					}
				}
			}
		}
	}

	/**
	 * 怪物魔法
	 * 
	 * @param _monster
	 * @param _target
	 * @param _activeSkill
	 */
	private void singleMagicAttackSkill(Monster _monster, ME2GameObject _target,
			ActiveSkill _activeSkill) {
		try {
			ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
			FightServiceImpl.getInstance().refreshFightTime(_monster, _target);
			sendSingleSkillAnimation(_monster, _target, activeSkillUnit.releaseAnimationID,
					activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
					activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
					activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
					activeSkillUnit.heightRelation, activeSkillUnit.isDirection);

			float odds = CEService.attackMagicHitOdds(_monster.getActualProperty().getLucky(),
					_monster.getActualProperty().getHitLevel(), _monster.getLevel(), _target
							.getLevel(), _target.getActualProperty().getMagicFastnessList()
							.getEMagicFastnessValue(activeSkillUnit.magicHarmType));
			int radom = RANDOM.nextInt(100);
			if (radom > odds) {
				EffectServiceImpl.getInstance().checkTouchEffect(_monster, _target,
						ETouchType.ATTACK_BY_MAGIC, true);
				checkTouchSkill(_monster, _target, ETouchType.ATTACK_BY_MAGIC, true);
				FightServiceImpl.getInstance().processMiss(_monster, _target);

				return;
			}

			if (activeSkillUnit.magicHarmHpValue > 0) {
				int magicAttack = CEService.magicHarmBySkill((int) _monster.getActualProperty()
						.getBaseMagicHarmList().getEMagicHarmValue(activeSkillUnit.magicHarmType),
						activeSkillUnit.magicHarmHpValue, activeSkillUnit.releaseTime, 
						_monster.getLevel(), _activeSkill.level);

				int harmValue = CEService.attackMagicHarm(_monster.getLevel(), magicAttack, _target
						.getLevel(), _target.getActualProperty().getMagicFastnessList()
						.getEMagicFastnessValue(activeSkillUnit.magicHarmType));

				boolean isDeathblow = false;

				float magicDeathblowOdds = CEService.attackMagicDeathblowOdds(
						_monster.getActualProperty().getMagicDeathblowLevel(), 
						_monster.getLevel(), _target.getLevel());

				if (RANDOM.nextInt(100) <= (magicDeathblowOdds - _target.getResistOddsList().magicDeathblowOdds)) {
					harmValue = CEService.calculateDeathblowHarm(harmValue, _monster
							.getActualProperty().getLucky());
					isDeathblow = true;
				}

				harmValue += harmValue
						* _monster.getActualProperty().getAdditionalMagicHarmScale(
								activeSkillUnit.magicHarmType);
				harmValue += _monster.getActualProperty().getAdditionalMagicHarm(
						activeSkillUnit.magicHarmType);
				harmValue += harmValue
						* _target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(
								activeSkillUnit.magicHarmType);
				harmValue += _target.getActualProperty().getAdditionalMagicHarmBeAttack(
						activeSkillUnit.magicHarmType);

				if (FightServiceImpl.getInstance().processReduceHp(_monster, _target, harmValue,
						true, isDeathblow, activeSkillUnit.magicHarmType)) {
					return;
				}
			}

			if (activeSkillUnit.magicHarmMpValue != 0) {
				_monster.addMp(-activeSkillUnit.magicHarmMpValue);
				// 怪物MP变化暂不做广播通知.
				// FightServiceImpl.getInstance().processSingleTargetMpChange(_pet);
			}

			if (null != _activeSkill.addEffectUnit) {
				EffectServiceImpl.getInstance().appendSkillEffect(_monster, _target,
						_activeSkill.addEffectUnit);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 宠物魔法技能
	 * 
	 * @param _pet
	 * @param _target
	 * @param _activeSkill
	 */
	private void singleMagicAttackSkill(Pet _pet, ME2GameObject _target, PetActiveSkill _activeSkill) {
		/*try {
			log.debug("pet sing magic attack skill start ...");
			PetActiveSkill activeSkillUnit = _activeSkill;
			FightServiceImpl.getInstance().refreshFightTime(_pet, _target);
			*//**待完善*//*
			sendSingleSkillAnimation(_pet, _target, activeSkillUnit.releaseAnimationID,
					activeSkillUnit.releaseAnimationID, activeSkillUnit.activeAnimationID, 
					activeSkillUnit.activeAnimationID, (byte)-1, (byte)1, (byte)2, (byte)2, (byte)1);

			log.debug("next attackMagicHitOdds ...");
			float odds = CEService.attackMagicHitOdds(_pet.getActualProperty().getLucky(), _pet
					.getActualProperty().getHitLevel(), _pet.getLevel(), _target.getLevel(),
					_target.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(
							activeSkillUnit.magicHarmType));
			int radom = RANDOM.nextInt(100);
			log.debug("magic odds = " + odds + ", radom = " + radom);
			if (radom > odds) {
				EffectServiceImpl.getInstance().checkTouchEffect(_pet, _target,
						ETouchType.ATTACK_BY_MAGIC, true);
				checkTouchSkill(_pet, _target, ETouchType.ATTACK_BY_MAGIC, true);
				FightServiceImpl.getInstance().processMiss(_pet, _target);

				return;
			}

			log.debug("next harmValue ...");

			if (activeSkillUnit.magicHarmHpValue > 0) {
				int magicAttack = CEService.magicHarmBySkill((int) _pet.getActualProperty()
						.getBaseMagicHarmList().getEMagicHarmValue(activeSkillUnit.magicHarmType),
						activeSkillUnit.magicHarmHpValue, activeSkillUnit.releaseTime, 
						_pet.getLevel(), _activeSkill.level);

				int harmValue = CEService.attackMagicHarm(_pet.getLevel(), magicAttack, _target
						.getLevel(), _target.getActualProperty().getMagicFastnessList()
						.getEMagicFastnessValue(activeSkillUnit.magicHarmType));

				boolean isDeathblow = false;

				float magicDeathblowOdds = CEService.attackMagicDeathblowOdds(
						_pet.getActualProperty().getMagicDeathblowLevel(), 
						_pet.getLevel(), _target.getLevel());

				if (RANDOM.nextInt(100) <= (magicDeathblowOdds - _target.getResistOddsList().magicDeathblowOdds)) {
					harmValue = CEService.calculateDeathblowHarm(harmValue, _pet
							.getActualProperty().getLucky());
					isDeathblow = true;
				}

				harmValue += harmValue
						* _pet.getActualProperty().getAdditionalMagicHarmScale(
								activeSkillUnit.magicHarmType);
				harmValue += _pet.getActualProperty().getAdditionalMagicHarm(
						activeSkillUnit.magicHarmType);
				harmValue += harmValue
						* _target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(
								activeSkillUnit.magicHarmType);
				harmValue += _target.getActualProperty().getAdditionalMagicHarmBeAttack(
						activeSkillUnit.magicHarmType);

				log.debug("pet skill attack harm value = " + harmValue);

				if (FightServiceImpl.getInstance().processReduceHp(_pet, _target, harmValue, true,
						isDeathblow, activeSkillUnit.magicHarmType)) {
					return;
				}
			}

			if (activeSkillUnit.useMp != 0) {
				_pet.addMp(-activeSkillUnit.useMp);
				// 宠物MP变化暂不做广播通知.
				// FightServiceImpl.getInstance().processSingleTargetMpChange(_pet);
			}

			if (null != _activeSkill.addEffectUnit) {
				EffectServiceImpl.getInstance().appendSkillEffect(_pet, _target,
						_activeSkill.addEffectUnit);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * 群体物理攻击技能
	 * 
	 * @param _player
	 *            技能施放者
	 * @param _targetList
	 *            目标列表
	 * @param _activeSkill
	 *            施放的技能单元
	 */
	private void groupPhysicsAttackSkill(HeroPlayer _player, ArrayList<ME2GameObject> _targetList,
			ActiveSkill _activeSkill) {
		ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
		sendGroupAttackSkillAnimation(_player, _targetList, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
				activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
				activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
		//del by zhengl; date: 2011-03-07; note: 释放的时候均不计算,物理技能只有命中的时候才计算触发
//		EffectServiceImpl.getInstance().checkTouchEffect(_player, null,
//				ETouchType.ATTACK_BY_PHYSICS, true);
//		checkTouchSkill(_player, null, ETouchType.ATTACK_BY_PHYSICS, true);

		if (activeSkillUnit.physicsHarmValue > 0 || activeSkillUnit.hateValue > 0) {
			int harmValue = 0;// 伤害值
			int attack = 0;// 攻击力

			if (activeSkillUnit.weaponHarmMult > 0) {
				EquipmentInstance weapon = _player.getBodyWear().getWeapon();

				if (null != weapon) {
					//edit by zhengl; date: 2011-03-07; note: 物理技能算法变更
					attack += CEService.weaponPhysicsAttackBySkill(
							_player.getActualProperty().getActualPhysicsAttack(),
							activeSkillUnit.weaponHarmMult, 
							activeSkillUnit.physicsHarmValue);
				}
			}

			if (activeSkillUnit.isAverageResult) {
				harmValue = CEService
						.physicsHarm(_player.getLevel(), attack, _player.getLevel(), 0);

				harmValue += harmValue
						* _player.getActualProperty().getAdditionalPhysicsAttackHarmScale();
				harmValue += _player.getActualProperty().getAdditionalPhysicsAttackHarmValue();
				harmValue /= _targetList.size();

				ME2GameObject target;

				for (int i = 0; i < _targetList.size();) {
					target = _targetList.get(i);

					harmValue += harmValue
							* target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack();
					harmValue += target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();

					if (FightServiceImpl.getInstance().processReduceHp(_player, target, harmValue,
							true, false, null)) {
						_targetList.remove(i);

						continue;
					}

					i++;
				}
			} else {
				boolean isDeathblow;
				ME2GameObject target;

				for (int i = 0; i < _targetList.size();) {
					target = _targetList.get(i);

					if (RANDOM.nextInt(100) > (CEService.attackPhysicsHitOdds(_player
							.getActualProperty().getLucky(), _player.getActualProperty()
							.getHitLevel(), _player.getLevel(), target.getLevel()) - CEService
							.attackPhysicsDuckOdds(_player.getLevel(), target.getActualProperty()
									.getAgility(), target.getActualProperty().getLucky(), target
									.getActualProperty().getPhysicsDuckLevel(), target.getLevel()))) {
						FightServiceImpl.getInstance().processMiss(_player, target);

						i++;

						continue;
					}
					//add by zhengl; date: 2011-05-19; note: 建立嘲讽/添加仇恨类技能的关系系统.
					if (activeSkillUnit.hateValue > 0 && target instanceof Monster) 
					{
						Monster monster = (Monster)target;
						if (monster.getAttackerAtFirst() == null) {
							monster.setAttackerAtFirst(_player);
						}
						monster.addTargetHatredValue(_player, activeSkillUnit.hateValue);
					}
					harmValue = CEService.physicsHarm(_player.getLevel(), attack,
							target.getLevel(), target.getActualProperty().getDefense());

					isDeathblow = false;

					if (RANDOM.nextInt(100) <= (CEService.attackPhysicsDeathblowOdds(_player
							.getActualProperty().getAgility(), _player.getActualProperty()
							.getPhysicsDeathblowLevel(), _player.getLevel(), target.getLevel()) - target
							.getResistOddsList().physicsDeathblowOdds)) {
						harmValue = CEService.calculateDeathblowHarm(harmValue, _player
								.getActualProperty().getLucky());
						isDeathblow = true;
						// add by zhengl ; date: 2010-12-05 ; note: 物理暴击触法效果
						EffectServiceImpl.getInstance().checkTouchEffect(_player, target,
								ETouchType.TOUCH_DEATHBLOW_BY_PHYSICS, true);
						EffectServiceImpl.getInstance().checkTouchEffect(target, _player,
								ETouchType.TOUCH_DEATHBLOW_BY_PHYSICS, false);
					}

					harmValue += harmValue
							* _player.getActualProperty().getAdditionalPhysicsAttackHarmScale();
					harmValue += _player.getActualProperty().getAdditionalPhysicsAttackHarmValue();
					harmValue += harmValue
							* target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack();
					harmValue += target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();

					if (FightServiceImpl.getInstance().processReduceHp(_player, target, harmValue,
							true, isDeathblow, null)) {
						_targetList.remove(i);

						continue;
					}

					i++;
				}
			}
		}

		if (null != _activeSkill.addEffectUnit) {
			for (ME2GameObject target : _targetList) {
				EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
						_activeSkill.addEffectUnit);

				if (null != activeSkillUnit.additionalOddsActionUnitList) {
					for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
						if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
							additionalSkillUnitActive(_player, target,
									(ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(
											additionalActionUnit.skillOrEffectUnitID),
									additionalActionUnit.activeTimes,
									additionalActionUnit.activeOdds);
						} else {
							//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
							if(RANDOM.nextInt(100) 
									<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
								log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
								EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
										additionalActionUnit.skillOrEffectUnitID);
							}
						}
					}
				}
			}
		} else if (null != _activeSkill.addSkillUnit) {
			for (ME2GameObject target : _targetList) {
				additionalSkillUnitActive(_player, target,
						(ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1);

				if (null != activeSkillUnit.additionalOddsActionUnitList) {
					for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
						if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
							additionalSkillUnitActive(_player, target,
									(ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(
											additionalActionUnit.skillOrEffectUnitID),
									additionalActionUnit.activeTimes,
									additionalActionUnit.activeOdds);
						} else {
							//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
							if(RANDOM.nextInt(100) 
									<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
								log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
								EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
										additionalActionUnit.skillOrEffectUnitID);
							}
						}
					}
				}
			}
		} else if (null != activeSkillUnit.additionalOddsActionUnitList) {
			for (ME2GameObject target : _targetList) {
				for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
					if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
						additionalSkillUnitActive(_player, target, (ActiveSkillUnit) SkillUnitDict
								.getInstance().getSkillUnitRef(
										additionalActionUnit.skillOrEffectUnitID),
								additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
					} else {
						//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
						if(RANDOM.nextInt(100) 
								<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
							log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
							EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
									additionalActionUnit.skillOrEffectUnitID);
						}
					}
				}
			}
		}

		for (ME2GameObject target : _targetList) {
			EffectServiceImpl.getInstance().checkTouchEffect(target, _player,
					ETouchType.ATTACK_BY_MAGIC, true);
			checkTouchSkill(target, _player, ETouchType.ATTACK_BY_MAGIC, true);

			if (target.getObjectType() == EObjectType.PLAYER) {
				_player.refreshPvPFightTime(((HeroPlayer) target).getUserID());
				((HeroPlayer) target).refreshPvPFightTime(_player.getUserID());
			}
		}
	}

	/**
	 * 群体魔法攻击技能
	 * 
	 * @param _player
	 *            技能施放者
	 * @param _targetList
	 *            目标列表
	 * @param _activeSkill
	 *            施放的技能单元
	 */
	private void groupMagicAttackSkill(HeroPlayer _player, ArrayList<ME2GameObject> _targetList,
			ActiveSkill _activeSkill) {
		ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
		sendGroupAttackSkillAnimation(_player, _targetList, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
				activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
				activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
		//1,使用魔法,开始触发.
		EffectServiceImpl.getInstance().checkTouchEffect(_player, null, ETouchType.USE_MAGIC,
				true);
		checkTouchSkill(_player, null, ETouchType.USE_MAGIC, true);

		if (activeSkillUnit.magicHarmHpValue > 0) {
			int harmValue;
			int magicAttack = CEService.magicHarmBySkill((int) _player.getActualProperty()
					.getBaseMagicHarmList().getEMagicHarmValue(activeSkillUnit.magicHarmType),
					activeSkillUnit.magicHarmHpValue, activeSkillUnit.releaseTime, 
					_player.getLevel(), _activeSkill.level);

			if (activeSkillUnit.isAverageResult) {
				harmValue = CEService.attackMagicHarm(_player.getLevel(), magicAttack, _player
						.getLevel(), 0);

				harmValue += harmValue
						* _player.getActualProperty().getAdditionalMagicHarmScale(
								activeSkillUnit.magicHarmType);
				harmValue += _player.getActualProperty().getAdditionalMagicHarm(
						activeSkillUnit.magicHarmType);
				harmValue /= _targetList.size();

				ME2GameObject target;

				for (int i = 0; i < _targetList.size();) {
					target = _targetList.get(i);

					harmValue += harmValue
							* target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(
									activeSkillUnit.magicHarmType);
					harmValue += target.getActualProperty().getAdditionalMagicHarmBeAttack(
							activeSkillUnit.magicHarmType);

					if (FightServiceImpl.getInstance().processReduceHp(_player, target, harmValue,
							true, false, activeSkillUnit.magicHarmType)) {
						_targetList.remove(i);

						continue;
					}

					i++;
				}
			} else {
				boolean isDeathblow;
				ME2GameObject target;

				for (int i = 0; i < _targetList.size();) {
					target = _targetList.get(i);

					if (activeSkillUnit.needMagicHit) {
						if (RANDOM.nextInt(100) > CEService.attackMagicHitOdds(_player
								.getActualProperty().getLucky(), _player.getActualProperty()
								.getHitLevel(), _player.getLevel(), target.getLevel(), target
								.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(
										activeSkillUnit.magicHarmType))) {
							FightServiceImpl.getInstance().processMiss(_player, target);

							i++;

							continue;
						}
					}

					harmValue = CEService.attackMagicHarm(_player.getLevel(), magicAttack, target
							.getLevel(), target.getActualProperty().getMagicFastnessList()
							.getEMagicFastnessValue(activeSkillUnit.magicHarmType));

					isDeathblow = false;

					if (RANDOM.nextInt(100) <= (CEService.attackPhysicsDeathblowOdds(_player
							.getActualProperty().getAgility(), _player.getActualProperty()
							.getPhysicsDeathblowLevel(), _player.getLevel(), target.getLevel()) - target
							.getResistOddsList().physicsDeathblowOdds)) {
						harmValue = CEService.calculateDeathblowHarm(harmValue, _player
								.getActualProperty().getLucky());
						isDeathblow = true;
						//2, 魔法暴击出发(特殊情况)
						EffectServiceImpl.getInstance().checkTouchEffect(_player, target,
								ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
						EffectServiceImpl.getInstance().checkTouchEffect(target, _player,
								ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, false);
					}

					harmValue += harmValue
							* _player.getActualProperty().getAdditionalMagicHarmScale(
									activeSkillUnit.magicHarmType);
					harmValue += _player.getActualProperty().getAdditionalMagicHarm(
							activeSkillUnit.magicHarmType);
					harmValue += harmValue
							* target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(
									activeSkillUnit.magicHarmType);
					harmValue += target.getActualProperty().getAdditionalMagicHarmBeAttack(
							activeSkillUnit.magicHarmType);

					//3, 魔法攻击触发
					EffectServiceImpl.getInstance().checkTouchEffect(
							_player, target, ETouchType.ATTACK_BY_MAGIC, true);
					checkTouchSkill(_player, target, ETouchType.ATTACK_BY_MAGIC, true);
					if (FightServiceImpl.getInstance().processReduceHp(_player, target, harmValue,
							true, isDeathblow, activeSkillUnit.magicHarmType)) {
						_targetList.remove(i);

						continue;
					}
					//4, 使用魔法后对目标触发
					EffectServiceImpl.getInstance().checkTouchEffect(target, _player, ETouchType.USE_MAGIC, true);
					checkTouchSkill(target, _player, ETouchType.USE_MAGIC, true);
					i++;
				}
			}
		}

		if (null != _activeSkill.addEffectUnit) {
			for (ME2GameObject target : _targetList) {
				EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
						_activeSkill.addEffectUnit);

				if (null != activeSkillUnit.additionalOddsActionUnitList) {
					for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
						if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
							additionalSkillUnitActive(_player, target,
									(ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(
											additionalActionUnit.skillOrEffectUnitID),
									additionalActionUnit.activeTimes,
									additionalActionUnit.activeOdds);
						} else {
							//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
							if(RANDOM.nextInt(100) 
									<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
								log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
								EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
										additionalActionUnit.skillOrEffectUnitID);
							}
						}
					}
				}
			}
		} else if (null != _activeSkill.addSkillUnit) {
			for (ME2GameObject target : _targetList) {
				additionalSkillUnitActive(_player, target,
						(ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1);

				if (null != activeSkillUnit.additionalOddsActionUnitList) {
					for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
						if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
							additionalSkillUnitActive(_player, target,
									(ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(
											additionalActionUnit.skillOrEffectUnitID),
									additionalActionUnit.activeTimes,
									additionalActionUnit.activeOdds);
						} else {
							//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
							if(RANDOM.nextInt(100) 
									<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
								log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
								EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
										additionalActionUnit.skillOrEffectUnitID);
							}
						}
					}
				}
			}
		} else if (null != activeSkillUnit.additionalOddsActionUnitList) {
			for (ME2GameObject target : _targetList) {
				for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
					if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
						additionalSkillUnitActive(_player, target, (ActiveSkillUnit) SkillUnitDict
								.getInstance().getSkillUnitRef(
										additionalActionUnit.skillOrEffectUnitID),
								additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
					} else {
						//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
						if(RANDOM.nextInt(100) 
								<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
							log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
							EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
									additionalActionUnit.skillOrEffectUnitID);
						}
					}
				}
			}
		}

		for (ME2GameObject target : _targetList) {
			//攻击成功,触发魔法攻击触发效果
			EffectServiceImpl.getInstance().checkTouchEffect(target, _player,
					ETouchType.ATTACK_BY_MAGIC, true);
			checkTouchSkill(target, _player, ETouchType.ATTACK_BY_MAGIC, true);

			if (target.getObjectType() == EObjectType.PLAYER) {
				_player.refreshPvPFightTime(((HeroPlayer) target).getUserID());
				((HeroPlayer) target).refreshPvPFightTime(_player.getUserID());
			}
		}

	}

	/**
	 * 附带技能或效果作用
	 * 
	 * @param _player
	 *            施放者
	 * @param _target
	 *            目标
	 * @param _activeSkillUnit
	 *            主动技能单元
	 * @param _totalTimes
	 *            执行的次数
	 * @param _odds
	 *            每次作用的几率（只要一次失败，则剩余的次数将不再执行）
	 */
	public void additionalSkillUnitActive(HeroPlayer _player, ME2GameObject _target,
			ActiveSkillUnit _activeSkillUnit, int _totalTimes, float _odds) {
		if (_activeSkillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
			if (_activeSkillUnit.targetType == ETargetType.ENEMY) {
				if (_activeSkillUnit.targetRangeType == ETargetRangeType.SINGLE) {
					if (_activeSkillUnit.physicsHarmValue > 0
							|| _activeSkillUnit.weaponHarmMult > 0) {
						if (RANDOM.nextFloat() <= _odds) {
							int attack = 0;// 攻击力
							int harmValue = 0;// 伤害值

							if (_activeSkillUnit.weaponHarmMult > 0) {
								EquipmentInstance weapon = _player.getBodyWear().getWeapon();

								if (null != weapon) {
									//edit by zhengl; date: 2011-03-07; note: 物理技能算法变更
									attack = CEService.weaponPhysicsAttackBySkill(
											_player.getActualProperty().getActualPhysicsAttack(), 
											_activeSkillUnit.weaponHarmMult, 
											_activeSkillUnit.physicsHarmValue);
								}
							}
							harmValue = CEService.physicsHarm(_player.getLevel(), attack, _target
									.getLevel(), _target.getActualProperty().getDefense());

							harmValue *= _target.getActualProperty()
									.getAdditionalHarmScaleBePhysicsAttack();
							harmValue += _target.getActualProperty()
									.getAdditionalHarmValueBePhysicsAttack();

							FightServiceImpl.getInstance().processReduceHp(_player, _target,
									harmValue, true, false, null);

							for (int times = 0; times < _totalTimes - 1; times++) {
								if (!_target.isDead()) {
									if (RANDOM.nextFloat() <= _odds) {
										FightServiceImpl.getInstance().processReduceHp(_player,
												_target, harmValue, true, false, null);
									}
								}
							}
						}
					}
				} else if (_activeSkillUnit.targetRangeType == ETargetRangeType.SOME) {
					ME2GameObject rangeBaseLintTarget = _activeSkillUnit.rangeBaseLine == EAOERangeBaseLine.RELEASER ? _player
							: _target;

					ArrayList<ME2GameObject> targetList;

					if (_activeSkillUnit.rangeMode == EAOERangeType.CENTER) {
						targetList = MapServiceImpl.getInstance().getAttackableObjectListInRange(
								rangeBaseLintTarget.where(), rangeBaseLintTarget.getCellX(),
								rangeBaseLintTarget.getCellY(), _activeSkillUnit.rangeX, _player,
								_activeSkillUnit.rangeTargetNumber);
					} else {
						targetList = MapServiceImpl.getInstance()
								.getAttackableObjectListInForeRange(rangeBaseLintTarget.where(),
										_activeSkillUnit.rangeX, _activeSkillUnit.rangeY, _player,
										_activeSkillUnit.rangeTargetNumber);
					}

					if (null != targetList && targetList.size() > 0) {
						for (ME2GameObject target : targetList) {
							if (_activeSkillUnit.physicsHarmValue > 0
									|| _activeSkillUnit.weaponHarmMult > 0) {
								int attack = 0;// 攻击力
								int harmValue = 0;// 伤害值

								if (_activeSkillUnit.weaponHarmMult > 0) {
									EquipmentInstance weapon = _player.getBodyWear().getWeapon();

									if (null != weapon) {
										//edit by zhengl; date: 2011-03-07; note: 物理技能算法变更
										attack += CEService.weaponPhysicsAttackBySkill(
												_player.getActualProperty().getActualPhysicsAttack(),
												_activeSkillUnit.weaponHarmMult, 
												_activeSkillUnit.physicsHarmValue);
									}
								}

								attack += _activeSkillUnit.physicsHarmValue;
								harmValue = CEService.physicsHarm(_player.getLevel(), attack,
										_target.getLevel(), _target.getActualProperty()
												.getDefense());

								harmValue *= target.getActualProperty()
										.getAdditionalHarmScaleBePhysicsAttack();
								harmValue += target.getActualProperty()
										.getAdditionalHarmValueBePhysicsAttack();

								FightServiceImpl.getInstance().processReduceHp(_player, target,
										harmValue, true, false, null);
							}
						}
					}
				}
			} else if (_activeSkillUnit.targetType == ETargetType.MYSELF) {
				if (_activeSkillUnit.resumeHp != 0) {
					FightServiceImpl.getInstance().processHpChange(_player, _player,
							_activeSkillUnit.resumeHp, false, null);
				}

				if (_activeSkillUnit.resumeMp != 0) {
					_player.addMp(_activeSkillUnit.resumeMp);

					FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
				}
			} else if (_activeSkillUnit.targetType == ETargetType.FRIEND) {
				if (_activeSkillUnit.targetRangeType == ETargetRangeType.SINGLE) {
					if (_activeSkillUnit.resumeHp != 0) {
						FightServiceImpl.getInstance().processHpChange(_player, _target,
								_activeSkillUnit.resumeHp, false, null);
					}

					if (_activeSkillUnit.resumeMp != 0) {
						_target.addMp(_activeSkillUnit.resumeMp);

						FightServiceImpl.getInstance().processSingleTargetMpChange(_target, true);
					}
				} else {
					ArrayList<HeroPlayer> targetList;
					ME2GameObject rangeBaseLintTarget = _activeSkillUnit.rangeBaseLine == EAOERangeBaseLine.RELEASER ? _player
							: _target;

					if (_activeSkillUnit.targetRangeType == ETargetRangeType.TEAM) {
						if (_activeSkillUnit.rangeMode == EAOERangeType.CENTER) {
							targetList = MapServiceImpl.getInstance().getGroupPlayerInRange(
									(HeroPlayer) rangeBaseLintTarget,
									rangeBaseLintTarget.getCellX(), rangeBaseLintTarget.getCellY(),
									_activeSkillUnit.rangeX, _activeSkillUnit.rangeTargetNumber);
						} else {
							targetList = MapServiceImpl.getInstance()
									.getGroupPlayerListInForeRange(
											(HeroPlayer) rangeBaseLintTarget,
											_activeSkillUnit.rangeX, _activeSkillUnit.rangeY,
											_activeSkillUnit.rangeTargetNumber);
						}
					} else {
						if (_activeSkillUnit.rangeMode == EAOERangeType.CENTER) {
							targetList = MapServiceImpl.getInstance().getFriendsPlayerInRange(
									(HeroPlayer) rangeBaseLintTarget,
									rangeBaseLintTarget.getCellX(), rangeBaseLintTarget.getCellY(),
									_activeSkillUnit.rangeX, _activeSkillUnit.rangeTargetNumber);
						} else {
							targetList = MapServiceImpl.getInstance()
									.getFriendsPlayerListInForeRange(
											(HeroPlayer) rangeBaseLintTarget,
											_activeSkillUnit.rangeX, _activeSkillUnit.rangeY,
											_activeSkillUnit.rangeTargetNumber);
						}
					}

					if (null != targetList && targetList.size() > 0) {
						for (HeroPlayer player : targetList) {
							if (_activeSkillUnit.resumeHp != 0) {
								FightServiceImpl.getInstance().processHpChange(_player, player,
										_activeSkillUnit.resumeHp, false, null);
							}

							if (_activeSkillUnit.resumeMp != 0) {
								player.addMp(_activeSkillUnit.resumeMp);

								FightServiceImpl.getInstance().processSingleTargetMpChange(player, true);
							}
						}
					}
				}
			}
		} else {

		}
	}

	private void singleResumeSkill(Pet _pet, ME2GameObject _target, PetActiveSkill _activeSkill) {
//		PetActiveSkill activeSkillUnit = _activeSkill;
//		/**待完善*/
//		sendSingleSkillAnimation(_pet, _target, activeSkillUnit.releaseAnimationID,
//				activeSkillUnit.releaseAnimationID, activeSkillUnit.activeAnimationID,
//				activeSkillUnit.activeAnimationID, (byte)-1, (byte)1, (byte)2, (byte)2, (byte)1);
//
//		if (activeSkillUnit.resumeHp > 0) {
//			int resumeHpValue = CEService.magicResume(activeSkillUnit.resumeHp, _pet
//					.getActualProperty().getSpirit(), _pet.getActualProperty().getInte(),
//					_pet.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
//							EMagic.SANCTITY), activeSkillUnit.releaseTime);
//
//			boolean isDeathblow = false;
//
//			if (RANDOM.nextInt(100) <= (_pet.getActualProperty().getMagicDeathblowOdds())) {
//				resumeHpValue = CEService.calculateDeathblowHarm(resumeHpValue, _pet
//						.getActualProperty().getLucky());
//				isDeathblow = true;
//			}
//
//			FightServiceImpl.getInstance().processAddHp(_pet, _target, resumeHpValue, true,
//					isDeathblow);
//		}
//
//		if (activeSkillUnit.resumeMp > 0) {
//			_target.addMp(activeSkillUnit.resumeMp);
//			FightServiceImpl.getInstance().processSingleTargetMpChange(_target, true);
//		}
//
//		if (null != _activeSkill.addEffectUnit) {
//			EffectServiceImpl.getInstance().appendSkillEffect(_pet, _target,
//					_activeSkill.addEffectUnit);
//		}
		// 宠物暂不考虑驱散DEBUFF
		// if (null != activeSkillUnit.cleanEffectFeature)
		// {
		// if (RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds)
		// {
		// EffectServiceImpl.getInstance().cleanEffect(_pet, _target,
		// activeSkillUnit.cleanEffectFeature,
		// activeSkillUnit.cleanEffectMaxLevel,
		// activeSkillUnit.cleanEffectNumberPerTimes);
		// }
		// }
		// else if (activeSkillUnit.cleanDetailEffectLowerID > 0
		// && activeSkillUnit.cleandetailEffectUperID > 0)
		// {
		// if (RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds)
		// {
		// EffectServiceImpl.getInstance().cleanEffect(_pet, _target,
		// activeSkillUnit.cleanDetailEffectLowerID,
		// activeSkillUnit.cleandetailEffectUperID);
		// }
		// }
	}

	/**
	 * 单体辅助技能
	 * 
	 * @param _player
	 *            技能施放者
	 * @param _target
	 *            目标
	 * @param _activeSkillUnit
	 *            施放的技能
	 */
	private void singleResumeSkill(HeroPlayer _player, ME2GameObject _target,
			ActiveSkill _activeSkill) {
		ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
		sendSingleSkillAnimation(_player, _target, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
				activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
				activeSkillUnit.heightRelation, activeSkillUnit.isDirection);

		if (activeSkillUnit.resumeHp > 0) {
			int resumeHpValue = CEService.magicResume(activeSkillUnit.resumeHp, 
					_player.getActualProperty().getSpirit(), _player.getActualProperty().getInte(),
					_player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
							EMagic.SANCTITY), activeSkillUnit.releaseTime);

			boolean isDeathblow = false;

			if (RANDOM.nextInt(100) <= (_player.getActualProperty().getMagicDeathblowOdds())) {
				resumeHpValue = CEService.calculateDeathblowHarm(resumeHpValue, _player
						.getActualProperty().getLucky());
				isDeathblow = true;
				// add by zhengl ; date: 2010-12-05 ; note: 治疗暴击触法效果
				EffectServiceImpl.getInstance().checkTouchEffect(_player, null,
						ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
			}

			FightServiceImpl.getInstance().processAddHp(_player, _target, resumeHpValue, true,
					isDeathblow);
		}

		if (activeSkillUnit.resumeMp > 0) {
			_target.addMp(activeSkillUnit.resumeMp);
			FightServiceImpl.getInstance().processSingleTargetMpChange(_target, true);
		}

		if (null != _activeSkill.addEffectUnit) {
			EffectServiceImpl.getInstance().appendSkillEffect(
					_player, _target, _activeSkill.addEffectUnit);
			//add by zhengl; date: 2011-04-18; note: 对施法时间影响的技能需要刷新施法时间通知.
			if (_activeSkill.addEffectUnit != null) {
				if(_activeSkill.addEffectUnit instanceof StaticEffect) {
					StaticEffect se = (StaticEffect)_activeSkill.addEffectUnit;
					if (se.allSkillReleaseTime > 0) 
					{
						//刷新技能,refresh
						RefreshSkillTime msg = new RefreshSkillTime(_player);
						ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
					}
				}
			}
		} else if (null != _activeSkill.addSkillUnit) {
			additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1);
		}

		if (null != activeSkillUnit.additionalOddsActionUnitList) {
			for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
				if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
					additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) SkillUnitDict
							.getInstance()
							.getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID),
							additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
				} else {
					//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
					if(RANDOM.nextInt(100) 
							<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
						log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
						EffectServiceImpl.getInstance().appendSkillEffect(_player, _target,
								additionalActionUnit.skillOrEffectUnitID);
					}
				}
			}
		}

		EffectServiceImpl.getInstance().checkTouchEffect(_player, _target,
				ETouchType.RESUME_BY_MAGIC, true);
		checkTouchSkill(_player, _target, ETouchType.RESUME_BY_MAGIC, true);

		if (null != activeSkillUnit.cleanEffectFeature) {
			if (RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds) {
				EffectServiceImpl.getInstance().cleanEffect(_player, _target,
						activeSkillUnit.cleanEffectFeature, activeSkillUnit.cleanEffectMaxLevel,
						activeSkillUnit.cleanEffectNumberPerTimes);
			}
		} else if (activeSkillUnit.cleanDetailEffectLowerID > 0
				&& activeSkillUnit.cleandetailEffectUperID > 0) {
			if (RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds) {
				EffectServiceImpl.getInstance().cleanEffect(_player, _target,
						activeSkillUnit.cleanDetailEffectLowerID,
						activeSkillUnit.cleandetailEffectUperID);
			}
		}
	}

	/**
	 * 群体辅助技能
	 * 
	 * @param _player
	 *            技能释放者
	 * @param _targetList
	 *            目标列表
	 * @param _activeSkillUnit
	 *            施放的技能
	 */
	private void groupResumeSkill(HeroPlayer _player, ArrayList<HeroPlayer> _targetList,
			ActiveSkill _activeSkill) {
		ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
		ArrayList<ME2GameObject> targetList = new ArrayList<ME2GameObject>(_targetList.size());

		for (HeroPlayer target : _targetList) {
			targetList.add(target);

			if (activeSkillUnit.resumeHp > 0) {
				int resumeHpValue = CEService.magicResume(activeSkillUnit.resumeHp, _player
						.getActualProperty().getSpirit(), _player.getActualProperty().getInte(),
						_player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
								EMagic.SANCTITY), activeSkillUnit.releaseTime);

				boolean isDeathblow = false;

				if (RANDOM.nextInt(100) <= (_player.getActualProperty().getMagicDeathblowOdds())) {
					resumeHpValue = CEService.calculateDeathblowHarm(resumeHpValue, _player
							.getActualProperty().getLucky());
					isDeathblow = true;
					// add by zhengl ; date: 2010-12-05 ; note: 治疗暴击触法效果
					EffectServiceImpl.getInstance().checkTouchEffect(_player, null,
							ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
				}

				FightServiceImpl.getInstance().processAddHp(_player, target, resumeHpValue, true,
						isDeathblow);
			}

			if (activeSkillUnit.resumeMp > 0) {
				target.addMp(activeSkillUnit.resumeMp);
				FightServiceImpl.getInstance().processSingleTargetMpChange(target, true);
			}

			EffectServiceImpl.getInstance().checkTouchEffect(_player, null,
					ETouchType.RESUME_BY_MAGIC, true);
			checkTouchSkill(_player, null, ETouchType.RESUME_BY_MAGIC, true);

			if (null != activeSkillUnit.cleanEffectFeature) {
				if (RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds) {
					EffectServiceImpl.getInstance().cleanEffect(_player, target,
							activeSkillUnit.cleanEffectFeature,
							activeSkillUnit.cleanEffectMaxLevel,
							activeSkillUnit.cleanEffectNumberPerTimes);
				}
			} else if (activeSkillUnit.cleanDetailEffectLowerID > 0
					&& activeSkillUnit.cleandetailEffectUperID > 0) {
				if (RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds) {
					EffectServiceImpl.getInstance().cleanEffect(_player, target,
							activeSkillUnit.cleanDetailEffectLowerID,
							activeSkillUnit.cleandetailEffectUperID);
				}
			}
		}

		if (null != _activeSkill.addEffectUnit) {
			for (ME2GameObject target : _targetList) {
				EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
						_activeSkill.addEffectUnit);

				if (null != activeSkillUnit.additionalOddsActionUnitList) {
					for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
						if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
							additionalSkillUnitActive(_player, target,
									(ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(
											additionalActionUnit.skillOrEffectUnitID),
									additionalActionUnit.activeTimes,
									additionalActionUnit.activeOdds);
						} else {
							//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
							if(RANDOM.nextInt(100) 
									<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
								log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
								EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
										additionalActionUnit.skillOrEffectUnitID);
							}
						}
					}
				}
			}
		} else if (null != _activeSkill.addSkillUnit) {
			for (ME2GameObject target : _targetList) {
				additionalSkillUnitActive(_player, target,
						(ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1);

				if (null != activeSkillUnit.additionalOddsActionUnitList) {
					for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
						if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
							additionalSkillUnitActive(_player, target,
									(ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(
											additionalActionUnit.skillOrEffectUnitID),
									additionalActionUnit.activeTimes,
									additionalActionUnit.activeOdds);
						} else {
							//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
							if(RANDOM.nextInt(100) 
									<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
								log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
								EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
										additionalActionUnit.skillOrEffectUnitID);
							}
						}
					}
				}
			}
		} else if (null != activeSkillUnit.additionalOddsActionUnitList) {
			for (ME2GameObject target : _targetList) {
				for (AdditionalActionUnit additionalActionUnit : activeSkillUnit.additionalOddsActionUnitList) {
					if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
						additionalSkillUnitActive(_player, target, (ActiveSkillUnit) SkillUnitDict
								.getInstance().getSkillUnitRef(
										additionalActionUnit.skillOrEffectUnitID),
								additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
					} else {
						//edit by zhengl; date: 2011-02-27; note: 发出触发特效几率生效.
						if(RANDOM.nextInt(100) 
								<= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
							log.info("CEService.oddsFormat-->"+CEService.oddsFormat(additionalActionUnit.activeOdds));
							EffectServiceImpl.getInstance().appendSkillEffect(_player, target,
									additionalActionUnit.skillOrEffectUnitID);
						}
					}
				}
			}
		}

		sendGroupAttackSkillAnimation(_player, targetList, activeSkillUnit.releaseAnimationID,
				activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, 
				activeSkillUnit.activeImageID, activeSkillUnit.animationAction, 
				activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, 
				activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
	}

	/**
	 * 执行触发技能
	 * 
	 * @param _host
	 * @param _activeTouchType
	 * @param _target
	 * @param _isSkillTouch
	 */
	public void checkTouchSkill(ME2GameObject _host, ME2GameObject _target,
			ETouchType _activeTouchType, boolean _isSkillTouch) {
		ArrayList<PassiveSkill> passiveList = null;
//		log.info("效果即将被触发");
		if (_host instanceof HeroPlayer) {
//			log.info("玩家触发");
			passiveList = ((HeroPlayer) _host).passiveSkillList;
			if (null != passiveList && passiveList.size() > 0) {
//				log.info("获得被动技能清单不为0");
				PassiveSkillUnit passiveSkillUnit;
				for (int i = 0; i < passiveList.size(); i++) {
					try {
						passiveSkillUnit = (PassiveSkillUnit) passiveList.get(i).skillUnit;
					} catch (Exception e) {
						log.error("技能强转为被动技能失败", e);
						break;
					}
					if ( !(passiveSkillUnit instanceof EnhanceSkillUnit) ) {
						passiveSkillUnit.touch(_host, _target, _activeTouchType, _isSkillTouch);
					}
				}
			}
		}
	}

	/**
	 * 能否施放技能
	 * 
	 * @param _releaser
	 *            施放者
	 * @param _target
	 *            目标
	 * @param _activeSkill
	 *            主动技能
	 * @return
	 */
	private boolean canReleaseSkill(HeroPlayer _releaser, ME2GameObject _target,
			ActiveSkill _activeSkill) {
		if (_activeSkill.reduceCoolDownTime > 0) {
			ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
					new Warning(Tip.TIP_SKILL_OF_SKILL_COOLDOWNING));

			return false;
		}

		ActiveSkillUnit skillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;

		if (ETargetType.DIER == skillUnit.targetType) {
			if (!_target.isDead() || _releaser.getClan() != _target.getClan()) {
				ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_INVALIDATE_TARGET));

				return false;
			}
		} else if (ETargetType.ENEMY == skillUnit.targetType) {
			if (ETargetRangeType.SINGLE == skillUnit.targetRangeType
					|| (ETargetRangeType.SOME == skillUnit.targetRangeType && EAOERangeBaseLine.TARGET == skillUnit.rangeBaseLine)) {
				if (_releaser.getClan() == _target.getClan()) {
					if (_target.getObjectType() == EObjectType.PLAYER) {
						if (!DuelServiceImpl.getInstance().isDueling(_releaser.getUserID(),
								((HeroPlayer) _target).getUserID())) {
							ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
									new Warning(Tip.TIP_SKILL_OF_INVALIDATE_TARGET));

							return false;
						}
					} else if (_target.getObjectType() == EObjectType.MONSTER) {
						ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
								new Warning(Tip.TIP_SKILL_OF_INVALIDATE_TARGET));

						return false;
					}
				}
			}
		}
		//edit by zhengl; date: 2011-02-13; note: 技能距离判定要减去怪物格子/2
		double added = 0.0;
		if(_target instanceof Monster) {
			MonsterImageConfDict.Config monsterConfig = 
				MonsterImageConfDict.get( ((Monster)_target).getImageID() );
			added = monsterConfig.grid/2;
		}
//		double distance = Math.sqrt(Math.pow(_releaser.getCellX() - _target.getCellX(), 2)
//				+ Math.pow(_releaser.getCellY() - _target.getCellY(), 2)) - added;
		//end

        boolean outDistance = (_releaser.getCellX() - _target.getCellX())*(_releaser.getCellX() - _target.getCellX())
                    + (_releaser.getCellY() - _target.getCellY())*(_releaser.getCellY() - _target.getCellY())
                    > (skillUnit.targetDistance + added)*(skillUnit.targetDistance + added);

		if (ETargetType.ENEMY == skillUnit.targetType) {
			if (ETargetRangeType.SINGLE == skillUnit.targetRangeType
					|| (ETargetRangeType.SOME == skillUnit.targetRangeType && EAOERangeBaseLine.TARGET == skillUnit.rangeBaseLine)) {
//				if (distance > skillUnit.targetDistance)
                if(outDistance)
                {
					ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
							new Warning(Tip.TIP_SKILL_OF_FARAWAY));

					return false;
				}
			}
		} else if (ETargetType.FRIEND == skillUnit.targetType) {
			if (EObjectType.PLAYER == _target.getObjectType()
					&& !DuelServiceImpl.getInstance().isDueling(_releaser.getUserID(),
							((HeroPlayer) _target).getUserID())) {
				if (ETargetRangeType.SINGLE == skillUnit.targetRangeType
						|| (ETargetRangeType.SOME == skillUnit.targetRangeType && EAOERangeBaseLine.TARGET == skillUnit.rangeBaseLine)) {
//					if (distance > skillUnit.targetDistance)
                    if(outDistance)
                    {
						ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
								new Warning(Tip.TIP_SKILL_OF_FARAWAY));

						return false;
					}
				}
			}
		}

		if (_activeSkill.onlyNotFightingStatus) {
			if (_releaser.isInFighting()) {
				ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_UNUSABLE_IN_FIGHTING));

				return false;
			}
		}

		if (!_releaser.canReleaseMagicSkill()) {
			ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
					new Warning(Tip.TIP_SKILL_OF_CANNOT_RELEASE));

			return false;
		}
		// _activeSkill.needWeaponType为NULL的话证明该技能没有武器限制
		if (null != _activeSkill.needWeaponType) {
			EquipmentInstance weaponInstance = _releaser.getBodyWear().getWeapon();
			if (null == weaponInstance) {
				return false;
			}
			// edit by zhengl
			boolean weaponType = false;
			for (int i = 0; i < _activeSkill.needWeaponType.size(); i++) {
				if( ((Weapon) weaponInstance.getArchetype()).getWeaponType() 
						== _activeSkill.needWeaponType.get(i) ) {
					weaponType = true;
					break;
				}
			}
			if( !weaponType ) {
				ResponseMessageQueue.getInstance().put( _releaser.getMsgQueueIndex(), 
						new Warning(Tip.TIP_SKILL_OF_NEED_WEAPON) );
				return false;
			}
			// end for
		}

		if (null != _activeSkill.hpConditionTargetType) {
			if (ETargetType.MYSELF == _activeSkill.hpConditionTargetType) {
				if (_activeSkill.hpConditionCompareLine == ActiveSkill.HP_COND_LINE_OF_GREATER_AND_EQUAL) {
					if (_releaser.getHPPercent() < _activeSkill.hpConditionPercent) {
						ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
								new Warning(Tip.TIP_SKILL_OF_CANNOT_RELEASE));

						return false;
					}
				} else if (_releaser.getHPPercent() > _activeSkill.hpConditionPercent) {
					ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
							new Warning(Tip.TIP_SKILL_OF_CANNOT_RELEASE));

					return false;
				}
			} else if (ETargetType.TARGET == _activeSkill.hpConditionTargetType) {
				if (_activeSkill.hpConditionCompareLine == ActiveSkill.HP_COND_LINE_OF_GREATER_AND_EQUAL) {
					if (_target.getHPPercent() < _activeSkill.hpConditionPercent) {
						ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
								new Warning(Tip.TIP_SKILL_OF_CANNOT_RELEASE));

						return false;
					}
				} else if (_target.getHPPercent() > _activeSkill.hpConditionPercent) {
					ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
							new Warning(Tip.TIP_SKILL_OF_CANNOT_RELEASE));

					return false;
				}
			} else {
				return false;
			}
		}

		boolean confirmEffectCondition = false;

		if (null != _activeSkill.releaserExistsEffectName) {
			for (Effect effect : _releaser.effectList) {
				if (effect.name.equals(_activeSkill.releaserExistsEffectName)) {
					confirmEffectCondition = true;

					break;
				}
			}

			if (!confirmEffectCondition) {
				ResponseMessageQueue.getInstance()
						.put(
								_releaser.getMsgQueueIndex(),
								new Warning(Tip.TIP_SKILL_OF_NEED_SELF_EFFECT
										+ _activeSkill.releaserExistsEffectName));

				return false;
			}
		} else if (null != _activeSkill.releaserUnexistsEffectName) {
			confirmEffectCondition = true;

			for (Effect effect : _releaser.effectList) {
				if (effect.name.equals(_activeSkill.releaserUnexistsEffectName)) {
					confirmEffectCondition = false;

					break;
				}
			}

			if (!confirmEffectCondition) {
				ResponseMessageQueue.getInstance().put(
						_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NEED_SELF_NONE_EFFECT
								+ _activeSkill.releaserUnexistsEffectName));

				return false;
			}
		} else if (null != _activeSkill.targetExistsEffectName) {
			confirmEffectCondition = false;

			if (_target.effectList.size() > 0) {
				for (int i = 0; i < _target.effectList.size(); i++) {
					try {
						if (_target.effectList.get(i).name
								.equals(_activeSkill.targetExistsEffectName)) {
							confirmEffectCondition = true;

							break;
						}
					} catch (Exception e) {
						confirmEffectCondition = true;

						break;
					}
				}
			}

			if (!confirmEffectCondition) {
				ResponseMessageQueue.getInstance()
						.put(
								_releaser.getMsgQueueIndex(),
								new Warning(Tip.TIP_SKILL_OF_NEED_TARGET_EFFECT
										+ _activeSkill.targetExistsEffectName));

				return false;
			}
		} else if (null != _activeSkill.targetUnexistsEffectName) {
			confirmEffectCondition = true;

			if (_target.effectList.size() > 0) {
				for (int i = 0; i < _target.effectList.size(); i++) {
					try {
						if (_target.effectList.get(i).name
								.equals(_activeSkill.targetExistsEffectName)) {
							confirmEffectCondition = false;

							break;
						}
					} catch (Exception e) {
						confirmEffectCondition = true;

						break;
					}
				}
			}

			if (!confirmEffectCondition) {
				ResponseMessageQueue.getInstance().put(
						_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NEED_TARGET_NONE_EFFECT
								+ _activeSkill.targetUnexistsEffectName));

				return false;
			}
		}

		if (skillUnit.consumeMp > 0) {
			if (_releaser.getMp() >= skillUnit.consumeMp) {
				_releaser.addMp(-skillUnit.consumeMp);
				FightServiceImpl.getInstance().processSingleTargetMpChange(_releaser, false);
			} else {
				ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NOT_ENOUGH_MP));

				return false;
			}
		} else if (skillUnit.consumeFp > 0) {
			if (_releaser.getForceQuantity() >= skillUnit.consumeFp) {
				_releaser.consumeForceQuantity(skillUnit.consumeFp);
				FightServiceImpl.getInstance().processPersionalForceQuantityChange(_releaser);
			} else {
				ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NOT_ENOUGH_FP));

				return false;
			}
		} else if (skillUnit.consumeGp > 0) {
			if (_releaser.getGasQuantity() >= skillUnit.consumeGp) {
				_releaser.consumeGasQuantity(skillUnit.consumeGp);
				FightServiceImpl.getInstance().processPersionalForceQuantityChange(_releaser);
			} else {
				ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NOT_ENOUGH_GP));

				return false;
			}
		}

		if (skillUnit.consumeHp > 0) {
			if (_releaser.getHp() >= skillUnit.consumeHp) {
				_releaser.addHp(-skillUnit.consumeHp);
				FightServiceImpl.getInstance().processPersionalHpChange(_releaser,
						-skillUnit.consumeHp);
			} else {
				ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NOT_ENOUGH_HP));

				return false;
			}
		}

		return true;
	}

	/**
	 * 怪物施放技能
	 * 
	 * @param _releaser
	 *            施放者
	 * @param _skillID
	 *            技能编号
	 * @param _target
	 *            作用目标（玩家）
	 * @param _direction
	 *            施放时的方向
	 * @return
	 */
	public boolean monsterReleaseSkill(Monster _releaser, HeroPlayer _target, byte _direction,
			ActiveSkill _activeSkill) {

		if (!_releaser.isEnable() || _releaser.isDead() || null == _target || !_target.isEnable()) {
			return false;
		}

		ActiveSkill activeSkill = _activeSkill;

		if (null == activeSkill) {
			return false;
		}

		ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
		if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
			singlePhysicsAttackSkill(_releaser, (ME2GameObject) _target, activeSkill);
		} else {
			singleMagicAttackSkill(_releaser, (ME2GameObject) _target, activeSkill);
		}
		// if (skillUnit.targetType == ETargetType.ENEMY) {
		// if (ETargetRangeType.SINGLE == skillUnit.targetRangeType) {
		// if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
		// singlePhysicsAttackSkill(_releaser, (ME2GameObject)_target,
		// activeSkill);
		// } else {
		// singleMagicAttackSkill(_releaser, (ME2GameObject)_target,
		// activeSkill);
		// }
		// } else if (ETargetRangeType.SOME == skillUnit.targetRangeType) {
		// ArrayList<ME2GameObject> targetList = null;
		//
		// if (EAOERangeBaseLine.RELEASER == skillUnit.rangeBaseLine) {
		// _target = _releaser;
		// }
		//
		// if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
		// targetList =
		// MapServiceImpl.getInstance().getAttackableObjectListInForeRange(
		// _target.where(), skillUnit.rangeX, skillUnit.rangeY, _releaser,
		// skillUnit.rangeTargetNumber);
		// } else {
		// targetList =
		// MapServiceImpl.getInstance().getAttackableObjectListInRange(
		// _target.where(), _target.getCellX(), _target.getCellY(),
		// skillUnit.rangeX, _releaser, skillUnit.rangeTargetNumber);
		// }
		//
		// if (null != targetList && targetList.size() > 0) {
		// if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
		// groupPhysicsAttackSkill(_releaser, targetList, activeSkill);
		// } else {
		// groupMagicAttackSkill(_releaser, targetList, activeSkill);
		// }
		// }
		// }
		// } else if (skillUnit.targetType == ETargetType.FRIEND) {
		// if (ETargetRangeType.SINGLE == skillUnit.targetRangeType) {
		// singleResumeSkill(_releaser, _target, activeSkill);
		// } else if (ETargetRangeType.SOME == skillUnit.targetRangeType) {
		// ArrayList<HeroPlayer> targetList = null;
		//
		// HeroPlayer rangeBaseLintTarget = skillUnit.rangeBaseLine ==
		// EAOERangeBaseLine.RELEASER ? _releaser
		// : (HeroPlayer) _target;
		//
		// if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
		// targetList =
		// MapServiceImpl.getInstance().getFriendsPlayerListInForeRange(
		// rangeBaseLintTarget, skillUnit.rangeX, skillUnit.rangeY,
		// skillUnit.rangeTargetNumber);
		// } else {
		// targetList = MapServiceImpl.getInstance().getFriendsPlayerInRange(
		// rangeBaseLintTarget, _target.getCellX(), _target.getCellY(),
		// skillUnit.rangeX, skillUnit.rangeTargetNumber);
		// }
		//
		// if (null != targetList && targetList.size() > 0) {
		// groupResumeSkill(_releaser, targetList, activeSkill);
		// }
		// } else {
		// ArrayList<HeroPlayer> targetList = null;
		//
		// if (EAOERangeBaseLine.RELEASER == skillUnit.rangeBaseLine) {
		// _target = _releaser;
		// }
		//
		// if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
		// targetList =
		// MapServiceImpl.getInstance().getGroupPlayerListInForeRange(
		// _releaser, skillUnit.rangeX, skillUnit.rangeY,
		// skillUnit.rangeTargetNumber);
		// } else {
		// targetList =
		// MapServiceImpl.getInstance().getGroupPlayerInRange(_releaser,
		// _target.getCellX(), _target.getCellY(), skillUnit.rangeX,
		// skillUnit.rangeTargetNumber);
		// }
		//
		// if (null != targetList && targetList.size() > 0) {
		// groupResumeSkill(_releaser, targetList, activeSkill);
		// }
		//
		// }
		// } else if (skillUnit.targetType == ETargetType.MYSELF) {
		// singleResumeSkill(_releaser, _releaser, activeSkill);
		// } else if (skillUnit.targetType == ETargetType.DIER) {
		// OutMsgQ.getInstance().put(
		// ((HeroPlayer) _target).getMsgQueueIndex(),
		// new ReviveConfirm(_releaser.getName(), _releaser.getCellX(),
		// _releaser
		// .getCellY(), skillUnit.resumeHp, skillUnit.resumeMp));
		// }
		//
		// activeSkill.reduceCoolDownTime = activeSkill.coolDownTime;
		//
		// if (activeSkill.coolDownID > 0
		// && activeSkill.reduceCoolDownTime >
		// SkillServiceImpl.VALIDATE_CD_TIME) {
		// for (ActiveSkill otherSkill : _releaser.activeSkillList) {
		// if (otherSkill.id != activeSkill.id
		// && otherSkill.coolDownID == activeSkill.coolDownID) {
		// otherSkill.reduceCoolDownTime = otherSkill.coolDownTime;
		// }
		// }
		// }

		return true;
	}

	/**
	 * 改变属性类型被动技能产生作用
	 * 
	 * @param _player
	 *            玩家
	 * @param _isInit
	 *            是否初始化
	 */
	public void changePropertySkillAction(HeroPlayer _player, boolean _isInit) {
		for (PassiveSkill passiveSkill : _player.passiveSkillList) {
			if (passiveSkill.skillUnit instanceof ChangePropertyUnit && passiveSkill.level > 0) 
			{
				enhanceProperty((ChangePropertyUnit) passiveSkill.skillUnit, _player, _isInit);
			}
		}
	}

	/**
	 * 领悟
	 * 
	 * @param _player
	 * @param _skillID
	 * @return
	 */
	public boolean comprehendSkill(HeroPlayer _player, int _skillID) {

		return true;
	}

	/**
	 * 获取可学习技能列表
	 * 
	 * @param _player
	 * @return
	 */
	public ArrayList<Skill> getLearnableSkillList(ArrayList<Skill> _vocationSkillList,
			HeroPlayer _player) {
		ArrayList<Skill> list = null;

		Skill existsSkill;
		boolean isNewSkill;

		for (Skill skill : _vocationSkillList) {
			isNewSkill = true;

			if (_player.getLevel() >= skill.learnerLevel) {
				if (ESkillType.ACTIVE == skill.getType()) {
					for (int i = 0; i < _player.activeSkillList.size(); i++) {
						existsSkill = _player.activeSkillList.get(i);

						if (existsSkill.isSameName(skill)) {
							if (existsSkill.feature == skill.feature
									&& skill.level - existsSkill.level == 1) {
								if (null == list) {
									list = new ArrayList<Skill>();
								}

								list.add(skill);
							}

							isNewSkill = false;

							break;
						}
					}
				} else {
					for (int i = 0; i < _player.passiveSkillList.size(); i++) {
						existsSkill = _player.passiveSkillList.get(i);

						if (existsSkill.isSameName(skill)) {
							if (existsSkill.feature == skill.feature
									&& skill.level - existsSkill.level == 1) {
								if (null == list) {
									list = new ArrayList<Skill>();
								}

								list.add(skill);
							}

							isNewSkill = false;

							break;
						}
					}

				}

				if (isNewSkill && skill.level == 1) {
					if (null == list) {
						list = new ArrayList<Skill>();
					}

					list.add(skill);
				}
			}
		}

		return list;
	}

	/**
	 * 转职之后新技能列表下发
	 * 
	 * @param _player
	 */
	public void changeVocationProcess(HeroPlayer _player) {
		ArrayList<Skill> skills = SkillDict.getInstance().getChangeVocationSkills(
				_player.getVocation());
		for (Skill skill : skills) {
			if (null != skill) {
				if (skill instanceof ActiveSkill) {
					_player.activeSkillList.add((ActiveSkill) skill);
					_player.activeSkillTable.put(skill.id, (ActiveSkill) skill);
				} else {
					_player.passiveSkillList.add((PassiveSkill) skill);
				}
			}
		}

		for (PassiveSkill passiveSkill : _player.passiveSkillList) {
			if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
				enhanceSkill((EnhanceSkillUnit) passiveSkill.skillUnit, _player, true, false);
			}
		}
		SkillDAO.changeCovation(_player.getUserID(), skills);
		ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new LearnedSkillListNotify(_player));
	}

	/**
	 * 学习技能 **未考虑通过技能书学习的情况.将来如果要这样学习的话得考虑兼容性.
	 * 
	 * @param _player
	 * @param _skillID
	 */
	public boolean learnSkill(HeroPlayer _player, int _skillID) {
		Skill oldSkill = instance.getSkillIns(_skillID);

		if (null != oldSkill) {
			if (oldSkill.next == null) {
				log.info("wrong:技能已达到最高级,或者该技能等级不连贯.请检查xml");
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_SKILL_OF_HIGHEST));
				return false;
			}
			//edit by zhengl; date: 2011-02-10; note: 学习技能需要的金钱需要加上
			if (_player.getMoney() < oldSkill.learnFreight) {
				 ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						 new Warning(Tip.TIP_SKILL_OF_NOT_ENOUGH_MONEY));
				 return false;
			}
			if (_player.getLevel() < oldSkill.learnerLevel) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NOT_ENOUGH_LEVEL));
				return false;
			}
			if (_player.surplusSkillPoint < oldSkill.skillPoints) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NOT_ENOUGH_POINT));
				return false;
			}

			if (oldSkill instanceof ActiveSkill) {
				if ( !(oldSkill.next instanceof ActiveSkill) ) {
					ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
							new Warning(Tip.TIP_PUBLIC_OF_FAIL_CONTINU));
					log.info("技能的下一级不是主动技能,请检查EXCEL表配置:" + oldSkill.name);
					return false;
				}
				Iterator<ActiveSkill> it = _player.activeSkillList.iterator();
				while (it.hasNext()) {
					if (oldSkill.id == it.next().id) {
						it.remove();
					}
				}
				_player.activeSkillTable.remove(oldSkill.id); // 删除低级的该技能
				if (oldSkill.next == null) {
					log.info("");
				}
				_player.activeSkillList.add((ActiveSkill) oldSkill.next); // 添加高级技能
				_player.activeSkillTable.put(oldSkill.next.id, (ActiveSkill) oldSkill.next); // 添加高级技能
				// 重新设置被动技能对主动技能的强化影响.
				for (PassiveSkill passiveSkill : _player.passiveSkillList) {
					if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
						EnhanceSkillUnit enhanceSkillUnit = (EnhanceSkillUnit) passiveSkill.skillUnit;

						for (EnhanceUnit enhanceUnit : enhanceSkillUnit.enhanceUnitList) {
							unitEnhance(enhanceUnit, oldSkill.next, true, false, null);
						}
					}
				}
			} else {
				Iterator<PassiveSkill> it = _player.passiveSkillList.iterator();
				while (it.hasNext()) {
					if (oldSkill.id == it.next().id) {
						it.remove();
					}
				}
				//add by zhengl; date: 2011-04-17; note: 刷新主动技能刷新吟唱时间
				Skill passive = oldSkill.next;
				if (passive.addEffectUnit != null) {
					if(passive.addEffectUnit instanceof StaticEffect) {
						StaticEffect se = (StaticEffect)passive.addEffectUnit;
						if (se.allSkillReleaseTime > 0) 
						{
							//刷新技能,refresh
							RefreshSkillTime msg = new RefreshSkillTime(_player);
							ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
						}
					}
				}
				_player.passiveSkillList.add((PassiveSkill) oldSkill.next);

				if (oldSkill.next.skillUnit instanceof EnhanceSkillUnit) {
					if (oldSkill.next.level > 1) {
						// 老技能先去除老效果
						enhanceSkill((EnhanceSkillUnit) oldSkill.skillUnit, _player, false, false);
					}
					// 再附加新技能该 有的效果
					enhanceSkill((EnhanceSkillUnit) oldSkill.next.skillUnit, _player, true, true);
				} else if (oldSkill.next.skillUnit instanceof ChangePropertyUnit) {
					if (enhancePropertySkillUpgrade((ChangePropertyUnit) oldSkill.skillUnit,
							(ChangePropertyUnit) oldSkill.next.skillUnit, _player)) {
						PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
						PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
						MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
								new RefreshObjectViewValue(_player), true, _player.getID());
					}
				}
			}
			// 升级技能对数据库的修改
			if (SkillDAO.LearnSkill(false, _player.getUserID(), oldSkill.next.id, oldSkill.id)) {
				if (oldSkill.next instanceof ActiveSkill) {
					PlayerServiceImpl.getInstance().upgradeShortcutKeySkill(_player, oldSkill.id,
							oldSkill.next.id);
				}
			} else {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_DB_OPERATE));

				return false;
			}
			_player.surplusSkillPoint -= oldSkill.skillPoints;
			PlayerServiceImpl.getInstance().addMoney(_player, 
					-oldSkill.learnFreight, 1, 
					PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING, "升级技能");
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
					new SkillUpgradeNotify(oldSkill.next, _player));
		} else {
			log.info("根据ID获得技能为null.ID=" + _skillID);
			return false;
		}
		return true;

	}

	// 遗忘技能（保留最基础职业的技能，道者和武者）**old**
	/**
	 * 遗忘技能
	 * 
	 * @param _player
	 */
	public void forgetSkill(HeroPlayer _player) {
		try 
		{
			int forgetGoods = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(
					RinseSkill.RINSE_SKILL_ID);
			RinseSkill rinse = (RinseSkill) GoodsContents.getGoods(RinseSkill.RINSE_SKILL_ID);
			if (forgetGoods <= 0) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(
								Tip.TIP_SKILL_OF_NOT_HAVE_RINSE_SKILL.replaceAll("%fn", rinse.getName()), 
								Warning.UI_TOOLTIP_AND_EVENT_TIP, 
								Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
				return;
			}
			//add by zhengl; date: 2011-04-21; note: 没有学习技能的时候不能洗点
			boolean isHaveSkill = false;
			for (int i = 0; i < _player.activeSkillList.size(); i++) {
				Skill skill = _player.activeSkillList.get(i);
				if (skill.level > 0) {
					isHaveSkill = true;
					break;
				}
			}
			if (!isHaveSkill) {
				for (int i = 0; i < _player.passiveSkillList.size(); i++) {
					Skill skill = _player.passiveSkillList.get(i);
					if (skill.level > 0) {
						isHaveSkill = true;
						break;
					}
				}
			}
			if (!isHaveSkill) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_SKILL_OF_NOT_HAVE_SKILL, Warning.UI_TOOLTIP_TIP));
				return;
			}

			ArrayList<Integer> forgetSkillIDList = new ArrayList<Integer>();
			ActiveSkill activeSkill;
			PassiveSkill passiveSkill;
			for (int i = 0; i < _player.activeSkillList.size(); i++) {
				activeSkill = _player.activeSkillList.get(i);
				forgetSkillIDList.add(activeSkill.id);
			}

			for (int i = 0; i < _player.passiveSkillList.size(); i++) {
				passiveSkill = _player.passiveSkillList.get(i);
				forgetSkillIDList.add(passiveSkill.id);
			}
			if (forgetSkillIDList.size() == 0) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_SKILL_OF_NONE_SKILL_BE_FORGET));
				return;
			}

			// edit by zhengl ; date: 2010-11-09 ; note: 遗忘技能可以一次性忘记全部
			SkillDAO.forgetSkill(_player);
			//x级只能获得 x-1个技能点.
			short point = (short)(_player.getLevel() 
					* PlayerServiceImpl.getInstance().getConfig().getUpgradeSkillPoint() -1);
			point += (short)PlayerServiceImpl.getInstance().getPlayerHeavenBookSkillPoint(_player);
			point += NoviceServiceImpl.getInstance().getConfig().novice_award_skill_point;
			point += PlayerServiceImpl.getInstance().getConfig().init_surplus_skill_point;
			point += PlayerServiceImpl.getInstance().getConfig().forget_skill_back_point;
			_player.surplusSkillPoint = point; // 重置技能点
			reloadSkillList(_player, null);
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
					new LearnedSkillListNotify(_player));
			PlayerServiceImpl.getInstance().resetSkillShortcutKey(_player);
			PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
			PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
			//add by zhengl; date: 2011-03-30; note: 洗点后快捷键下发
	        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
	                new ShortcutKeyListNotify(_player));
			MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
					new RefreshObjectViewValue(_player), true, _player.getID());

			GoodsServiceImpl.getInstance().deleteSingleGoods(
					_player,
                    _player.getInventory().getSpecialGoodsBag(),
                    rinse, 1,
                    CauseLog.RINSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重新初始化技能列表
	 * 
	 * @param _player
	 *            玩家
	 * @param _skillInfoList
	 *            技能信息列表，信息为长度为2的整形数组，0下标为技能编号，1下标为剩余冷却时间，如果是被动技能则为0
	 */
	private void reloadSkillList(HeroPlayer _player, ArrayList<int[]> _skillInfoList) {
		_player.activeSkillList.clear();
		_player.activeSkillTable.clear();
		_player.passiveSkillList.clear();

		ArrayList<Skill> skills = SkillDict.getInstance()
				.getSkillsByVocation(_player.getVocation());

		for (Skill skill : skills) {
			if (null != skill) {
				if (skill instanceof ActiveSkill) {
					_player.activeSkillList.add((ActiveSkill) skill);
					_player.activeSkillTable.put(skill.id, (ActiveSkill) skill);
				} else {
					_player.passiveSkillList.add((PassiveSkill) skill);
				}
			}
		}

		for (PassiveSkill passiveSkill : _player.passiveSkillList) {
			if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
				enhanceSkill((EnhanceSkillUnit) passiveSkill.skillUnit, _player, true, false);
			}
		}
		// end
	}

	/**
	 * 强化单元技能对特定技能的影响
	 * 
	 * @param enhanceUnit
	 *            强化单元
	 * @param _skill
	 *            受 影响的技能
	 * @param _isEnhance
	 *            强化还是取消
	 * @return
	 */
	private boolean unitEnhance(EnhanceUnit enhanceUnit, Skill _skill, boolean _isEnhance,
			boolean _needNotifyClient, HeroPlayer _player) {
		float value;
		boolean enhanced = false;

		if (_skill.skillUnit instanceof EnhanceSkillUnit) {
			return false;
		}

		// 强化总表
		if (EnhanceDataType.SKILL == enhanceUnit.skillDataType) {
			log.info("enhanceUnit.skillName == "+enhanceUnit.skillName);
			log.info("_skill.name == "+_skill.name);
			if (enhanceUnit.skillName.equals(_skill.name)) {
				if (enhanceUnit.dataField == SkillDataField.COOL_DOWN) {
					value = valueChanged(((ActiveSkill) getSkillModel(_skill.id)).coolDownTime,
							enhanceUnit.changeMulti, enhanceUnit.caluOperator);

					switch (enhanceUnit.dataField) {
					case COOL_DOWN:
						if (!_isEnhance)
							value = -value;
						((ActiveSkill) _skill).coolDownTime += value;
						enhanced = true;
						break;
					case NEED_WEAPON:
						String temp = "";
						for (int i = 0; i < enhanceUnit.changeString.length; i++) {
							temp = enhanceUnit.changeString[i];
							((ActiveSkill) _skill).needWeaponType.add(EWeaponType.getType(temp));
						}
						enhanced = true;
						break;
					}
					if (_needNotifyClient)
						ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
								new UpdateActiveSkillNotify((ActiveSkill) _skill));
				}
			}
		} else if (EnhanceDataType.SKILL_UNIT == enhanceUnit.skillDataType) {
			SkillUnit skillUnit = null;

			if (enhanceUnit.skillName.equals(_skill.skillUnit.name)) {
				skillUnit = _skill.skillUnit;
			} else if (null != _skill.addSkillUnit
					&& enhanceUnit.skillName.equals(_skill.addSkillUnit.name)) {
				skillUnit = _skill.addSkillUnit;
			}

			if (null != skillUnit) {
				switch (enhanceUnit.dataField) {
				case PHYSICS_HARM: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
						log.info("被强化技能:" + activeSkillUnit.name);
						value = valueChanged(activeSkillUnit.physicsHarmValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.physicsHarmValue += value;
					} else if (skillUnit instanceof TouchUnit) {
						TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;

						value = valueChanged(touchPassiveSkill.physicsHarmValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						touchPassiveSkill.physicsHarmValue += value;
					} else if (skillUnit instanceof ChangePropertyUnit) {
						ChangePropertyUnit changePropertyPassiveSkill = (ChangePropertyUnit) skillUnit;

						value = valueChanged(changePropertyPassiveSkill.physicsAttackHarmValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						changePropertyPassiveSkill.physicsAttackHarmValue += value;
					}

					break;
				}
				case MAGIC_HARM: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.magicHarmHpValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.magicHarmHpValue += value;
					} else if (skillUnit instanceof TouchUnit) {
						TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;

						value = valueChanged(touchPassiveSkill.magicHarmHpValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						touchPassiveSkill.magicHarmHpValue += value;
					} else if (skillUnit instanceof ChangePropertyUnit) {
						ChangePropertyUnit changePropertyPassiveSkill = (ChangePropertyUnit) skillUnit;

						value = valueChanged(changePropertyPassiveSkill.magicHarmValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						changePropertyPassiveSkill.magicHarmValue += value;
					}

					break;
				}
				case HATE: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.hateValue, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.hateValue += value;
					} else if (skillUnit instanceof TouchUnit) {
						TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;

						value = valueChanged(touchPassiveSkill.hateValue, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						touchPassiveSkill.hateValue += value;
					} else if (skillUnit instanceof ChangePropertyUnit) {
						ChangePropertyUnit changePropertyPassiveSkill = (ChangePropertyUnit) skillUnit;

						value = valueChanged(changePropertyPassiveSkill.hate,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						changePropertyPassiveSkill.hate += value;
					}

					break;
				}
				case MAGIC_REDUCE: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.magicHarmMpValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.magicHarmMpValue += value;
					} else if (skillUnit instanceof TouchUnit) {
						TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;

						value = valueChanged(touchPassiveSkill.magicHarmMpValue,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						touchPassiveSkill.magicHarmMpValue += value;
					}

					break;
				}
				case HP_RESUME: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.resumeHp, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.resumeHp += value;
					} else if (skillUnit instanceof TouchUnit) {
						TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;

						value = valueChanged(touchPassiveSkill.resumeHp, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						touchPassiveSkill.resumeHp += value;
					}

					break;
				}
				case RELEASE_TIME: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(((ActiveSkillUnit) (SkillUnitDict.getInstance()
								.getSkillUnitRef(activeSkillUnit.id))).releaseTime,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.releaseTime += value;

						if (_needNotifyClient)
							ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
									new UpdateActiveSkillNotify((ActiveSkill) _skill));
					}

					break;
				}
				case TARGET_DISTANCE: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.targetDistance,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.targetDistance += value;

						if (_needNotifyClient)
							ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
									new UpdateActiveSkillNotify((ActiveSkill) _skill));
					}

					break;
				}
				case MP_CONSUME: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						if (activeSkillUnit.consumeMp > 0) {
							value = valueChanged(activeSkillUnit.consumeMp,
									enhanceUnit.changeMulti, enhanceUnit.caluOperator);

							if (!_isEnhance)
								value = -value;

							activeSkillUnit.consumeMp += value;
						} else if (activeSkillUnit.consumeFp > 0) {
							value = valueChanged(activeSkillUnit.consumeFp,
									enhanceUnit.changeMulti, enhanceUnit.caluOperator);

							if (!_isEnhance)
								value = -value;

							activeSkillUnit.consumeFp += value;
						} else if (activeSkillUnit.consumeGp > 0) {
							value = valueChanged(activeSkillUnit.consumeGp,
									enhanceUnit.changeMulti, enhanceUnit.caluOperator);

							if (!_isEnhance)
								value = -value;

							activeSkillUnit.consumeGp += value;
						}

						if (_needNotifyClient)
							ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
									new UpdateActiveSkillNotify((ActiveSkill) _skill));
					}

					break;
				}
				case HP_CONSUME: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.consumeHp, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.consumeHp += value;

						if (_needNotifyClient)
							ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
									new UpdateActiveSkillNotify((ActiveSkill) _skill));
					}

					break;
				}
				case RANGE_X: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.rangeX, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.rangeX += value;
					} else if (skillUnit instanceof TouchUnit) {
						TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;

						value = valueChanged(touchPassiveSkill.rangeX, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						touchPassiveSkill.rangeX += value;
					}

					break;
				}
				case WEAPON_HARM_MULT: {
					if (skillUnit instanceof ActiveSkillUnit) {
						ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;

						value = valueChanged(activeSkillUnit.weaponHarmMult,
								enhanceUnit.changeMulti, enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						activeSkillUnit.weaponHarmMult += value;
					}

					break;
				}
				}

				enhanced = true;
			}
		} else if (EnhanceDataType.EFFECT_UNIT == enhanceUnit.skillDataType) {
			if (null != _skill.addEffectUnit
					&& enhanceUnit.skillName.equals(_skill.addEffectUnit.name)) {
				Effect effect = _skill.addEffectUnit;

				switch (enhanceUnit.dataField) {
				case MAGIC_HARM: {
					if (effect instanceof DynamicEffect) {
						DynamicEffect dynamicEffect = (DynamicEffect) effect;

						value = valueChanged(dynamicEffect.hpHarmTotal, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						dynamicEffect.hpHarmTotal += value;
					} else if (effect instanceof StaticEffect) {
						StaticEffect staticEffect = (StaticEffect) effect;

						value = valueChanged(staticEffect.magicHarmValue, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						staticEffect.magicHarmValue += value;
					} else if (effect instanceof TouchEffect) {
						TouchEffect touchEffect = (TouchEffect) effect;

						value = valueChanged(touchEffect.hpHarmValue, enhanceUnit.changeMulti,
								enhanceUnit.caluOperator);

						if (!_isEnhance)
							value = -value;

						touchEffect.hpHarmValue += value;
					}

					break;
				}
				case STRENGTH: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.strength, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.strength += value;

					break;
				}
				case INTE: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.inte, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.inte += value;

					break;
				}
				case AGILITY: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.agility, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.agility += value;

					break;
				}
				case SPIRIT: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.spirit, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.spirit += value;

					break;
				}
				case DEFENSE: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.defense, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.defense += value;

					break;
				}
				case FASTNESS_VALUE: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.magicFastnessValue, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.magicFastnessValue += value;

					break;
				}
				case DUCK_LEVEL: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.physicsDuckLevel, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.physicsDuckLevel += value;

					break;
				}
				case HIT_LEVEL: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.hitLevel, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.hitLevel += value;

					break;
				}
				case PHISICS_DEATHBLOW_LEVEL: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.physicsDeathblowLevel,
							enhanceUnit.changeMulti, enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.physicsDeathblowLevel += value;

					break;
				}
				case WEAPON_IMMO: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.physicsAttackInterval,
							enhanceUnit.changeMulti, enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.physicsAttackInterval += value;

					break;
				}
				case EFFECT_KEEP_TIME: {
					value = valueChanged(effect.keepTime, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					effect.addKeepTime((short) value);

					break;
				}
				case HP_MAX: {
					StaticEffect staticEffect = (StaticEffect) effect;

					value = valueChanged(staticEffect.maxHp, enhanceUnit.changeMulti,
							enhanceUnit.caluOperator);

					if (!_isEnhance)
						value = -value;

					staticEffect.maxHp += value;

					break;
				}
				}

				enhanced = true;
			}
		}

		return enhanced;
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
	private float valueChanged(float _baseValue, float _caluModulus, EMathCaluOperator _operator) {
		if (_caluModulus > 0) {
			switch (_operator) {
			case ADD: {
				return _caluModulus;
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
	 * 随机数生成器
	 */
	private static final Random RANDOM = new Random();

	/**
	 * 有效的技能冷却时间，超过此时间的才会入库（秒）
	 */
	public static final int VALIDATE_CD_TIME = 300;

}
