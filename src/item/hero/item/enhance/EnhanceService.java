package hero.item.enhance;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import hero.log.service.ServiceType;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;

import hero.charge.message.ResponseRecharge;
import hero.charge.service.ChargeServiceImpl;
import hero.chat.service.ChatQueue;
import hero.chat.service.ChatServiceImpl;
import hero.item.message.AskBuyJewel;
import hero.effect.detail.TouchEffect;
import hero.effect.dictionry.EffectDictionary;
import hero.effect.service.WeaponPvpAndPveEffectDict;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.item.Armor;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.Weapon;
import hero.item.bag.EquipmentContainer;
import hero.item.bag.exception.BagException;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.detail.EGoodsTrait;
import hero.item.dictionary.GoodsContents;
import hero.item.message.AddWeaponBloodyEnhanceNotify;
import hero.item.message.ClothesOrWeaponChangeNotify;
import hero.item.message.EnhanceAnswer;
import hero.item.message.EquipmentEnhanceChangeNotify;
import hero.item.message.ResponseSingleHoleEnhanceProperty;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.Crystal;
import hero.log.service.CauseLog;
import hero.log.service.LoctionLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Monster;
import hero.npc.detail.EMonsterLevel;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EnhanceService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-12 下午04:51:09
 * @描述 ：装备强化服务，包括提升屠魔、杀戮、
 */

public class EnhanceService {
	private static Logger log = Logger.getLogger(EnhanceService.class);
	/**
	 * 单例
	 */
	private static EnhanceService instance;

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static EnhanceService getInstance() {
		if (null == instance) {
			instance = new EnhanceService();
		}

		return instance;
	}

	/**
	 * 私有构造
	 */
	private EnhanceService() {

	}

	/**
	 * 获取血腥强化效果（包括屠魔和杀戮）
	 * 
	 * @param _enhanceDataID
	 *            强化效果数据编号
	 * @param _enhanceLevel
	 *            强化等级
	 * @return
	 */
	public TouchEffect getEffect(int _enhanceDataID, byte _enhanceLevel) {
		TouchEffect effect = (TouchEffect) EffectDictionary.getInstance().getEffectInstance(
				WeaponPvpAndPveEffectDict.getInstance().getEffectID(_enhanceDataID, _enhanceLevel));

		if (null == effect) {
			LogWriter.println("错误的杀戮、屠魔效果编号：" + _enhanceDataID + "--等级：" + _enhanceLevel);
		}
		return effect;
	}

	/**
	 * 添加屠魔数
	 */
	public void addPve(HeroPlayer _player, EquipmentInstance _weapon) {
		if (null != _weapon.getWeaponBloodyEnhance()) {
			_weapon.getWeaponBloodyEnhance().addPveNumber();
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
					new AddWeaponBloodyEnhanceNotify(_weapon));
			GoodsDAO.updateWeaponBloodyEnhance(_weapon.getInstanceID(),
					buildBloodyEnhanceDesc(_weapon));
		}
	}

	/**
	 * 添加杀戮数
	 */
	public void addPvp(HeroPlayer _player, EquipmentInstance _weapon) {
		if (null != _weapon.getWeaponBloodyEnhance()) {
			_weapon.getWeaponBloodyEnhance().addPvpNumber();
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
					new AddWeaponBloodyEnhanceNotify(_weapon));
			GoodsDAO.updateWeaponBloodyEnhance(_weapon.getInstanceID(),
					buildBloodyEnhanceDesc(_weapon));
		}
	}

	/**
	 * 处理玩家杀死一个目标后武器的屠魔和杀戮逻辑
	 * 
	 * @param _player
	 *            胜利玩家
	 * @param _dier
	 *            死亡对象
	 */
	public void processWeaponEnhance(HeroPlayer _player, ME2GameObject _dier) {
//		try {
//			EquipmentInstance weapon;
//
//			if (_dier instanceof HeroPlayer) {
//				Group group = GroupServiceImpl.getInstance().getGroup(_player.getGroupID());
//
//				if (null != group) {
//					ArrayList<HeroPlayer> playerList = group.getValidatePlayerList(_dier.where()
//							.getID());
//
//					for (HeroPlayer player : playerList) {
//						weapon = player.getBodyWear().getWeapon();
//
//						if (weapon == null) {
//							continue;
//						}
//
//						if (player.getLevel() - _dier.getLevel() <= 5) {
//							addPvp(player, weapon);
//						}
//					}
//				} else {
//					weapon = _player.getBodyWear().getWeapon();
//
//					if (weapon == null) {
//						return;
//					}
//
//					if (_player.getLevel() - _dier.getLevel() <= 5) {
//						addPvp(_player, weapon);
//					}
//				}
//			} else if (_dier instanceof Monster) {
//				if (EMonsterLevel.BOSS == ((Monster) _dier).getMonsterLevel()) {
//					ArrayList<HeroPlayer> hatredTargetList = ((Monster) _dier)
//							.getHatredTargetList();
//
//					if (null != hatredTargetList) {
//						for (HeroPlayer player : hatredTargetList) {
//							weapon = player.getBodyWear().getWeapon();
//
//							if (weapon == null) {
//								continue;
//							}
//
//							if (player.getLevel() - _dier.getLevel() <= 2) {
//								addPve(player, weapon);
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public void enhanceQuest(HeroPlayer _player, int _crystalID, 
			byte _jewelIndex, EquipmentInstance _ei)
	{
		boolean result = false;
		int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(_crystalID);
		Goods goods = GoodsContents.getGoods(_crystalID);
		
		if(goods == null || !(goods instanceof Crystal) ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_WRONG_TYPE, Warning.UI_STRING_TIP));
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new EnhanceAnswer(EnhanceAnswer.ANSWER_TYPE_WRONG));
			result = true;
		}
		if (stoneNum <= 0) {
			//edit by zhengl ; date: 2010-12-16 ; note: 暂时先这样提示
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_NOT_HAVE_NEW_JEWEL, Warning.UI_STRING_TIP));
			result = true;
		}
		//edit by zhengl; date: 2011-03-31; note: 添加一段防止异常的容错代码
		Crystal crystal = null;
		if (goods instanceof Crystal) {
			crystal = (Crystal)goods;
		}
		if (crystal == null || crystal.getUseType() != 1) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_WRONG_TYPE, Warning.UI_STRING_TIP));
			result = true;
		}
		if (_ei.getGeneralEnhance().getLevel() == EnhanceService.MAX_LEVEL_OF_ENHANCE) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_TOP_LEVEL, Warning.UI_STRING_TIP));
			result = true;
		}
		if( !_ei.getGeneralEnhance().haveHole(_jewelIndex) ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_HAVE_HOLE, Warning.UI_STRING_TIP));
			result = true;
		}
		byte index = _ei.getGeneralEnhance().getJewelLevel(_jewelIndex);
		if (index > 0) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_HAVE_OCCUPIED, Warning.UI_STRING_TIP));
			result = true;
		}
		if(_jewelIndex < GenericEnhance.HIGH_SIDE && crystal != null && crystal.getIsUltimaNeed() ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_NOT_HAVE_ULTIMATE, Warning.UI_STRING_TIP));
			result = true;
		}
		if(_jewelIndex >= GenericEnhance.HIGH_SIDE && crystal != null 
				&& (!crystal.getIsUltimaNeed()) ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_NOT_ULTIMATE, Warning.UI_STRING_TIP));
			result = true;
		}
		if(!crystal.conformLevel(_ei.getArchetype().getNeedLevel())) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_NOT_CONFORM, Warning.UI_STRING_TIP));
			result = true;
		}
		if(_player.getMoney() 
				< GoodsServiceImpl.getInstance().getConfig().enhance_money_list[_jewelIndex]) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_MONEY_LACK_ENHANCE, Warning.UI_STRING_TIP));
			result = true;
		}
		if(result) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new EnhanceAnswer(EnhanceAnswer.ANSWER_TYPE_WRONG));
		} else {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new EnhanceAnswer(EnhanceAnswer.ANSWER_TYPE_RIGHT));
		}
	}

	/**
	 * 强化装备
	 * <p>
	 * 如果成功的话,必然需要执行的4个操作 1,装备实际值的强化 2,数据库的修改 3,回复报文通知玩家 4,消耗宝石的删除.
	 * 
	 * @param _player
	 * @param _crystalID
	 * @param _gridIndex
	 * @param _ei
	 */
	public void enhanceEquipment(HeroPlayer _player, int _crystalID, 
			byte _jewelIndex, EquipmentInstance _ei) {
		int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(_crystalID);
		Goods goods = GoodsContents.getGoods(_crystalID);
		boolean isFlash = false;
		
		if(goods == null || !(goods instanceof Crystal) ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_WRONG_TYPE, Warning.UI_STRING_TIP));
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new EnhanceAnswer(EnhanceAnswer.ANSWER_TYPE_WRONG));
			return;
		}
		if (stoneNum <= 0) {
			//edit by zhengl ; date: 2010-12-16 ; note: 暂时先这样提示
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_NOT_HAVE_NEW_JEWEL, Warning.UI_STRING_TIP));
			return;
		}
		//edit by zhengl; date: 2011-03-31; note: 添加一段防止异常的容错代码
		Crystal crystal = null;
		if (goods instanceof Crystal) {
			crystal = (Crystal)goods;
		}
		if (crystal == null || crystal.getUseType() != 1) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_WRONG_TYPE, Warning.UI_STRING_TIP));
			return;
		}
		if(_player.getMoney() 
				< GoodsServiceImpl.getInstance().getConfig().enhance_money_list[_jewelIndex]) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_MONEY_LACK_ENHANCE, Warning.UI_STRING_TIP));
			return;
		}
		if (_ei.getGeneralEnhance().getLevel() == EnhanceService.MAX_LEVEL_OF_ENHANCE) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_TOP_LEVEL, Warning.UI_STRING_TIP));
			return;
		}
		if( !_ei.getGeneralEnhance().haveHole(_jewelIndex) ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_HAVE_HOLE, Warning.UI_STRING_TIP));
			return;
		}
		byte index = _ei.getGeneralEnhance().getJewelLevel(_jewelIndex);
		if (index > 0) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_HAVE_OCCUPIED, Warning.UI_STRING_TIP));
			return;
		}
		if(_jewelIndex < GenericEnhance.HIGH_SIDE && crystal.getIsUltimaNeed() ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_NOT_HAVE_ULTIMATE, Warning.UI_STRING_TIP));
			return;
		}
		if(_jewelIndex >= GenericEnhance.HIGH_SIDE && (!crystal.getIsUltimaNeed()) ) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_NOT_ULTIMATE, Warning.UI_STRING_TIP));
			return;
		}
		if(!crystal.conformLevel(_ei.getArchetype().getNeedLevel())) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_NOT_CONFORM, Warning.UI_STRING_TIP));
			return;
		}
		
		if (null != crystal) {
			//add by zhengl; date: 2011-05-18; note: 添加+1 确保选值是1-100
			int random = RANDOM_BUILDER.nextInt(100) + 1;
//			byte level = EnhanceService.ENHANCE_ODDS_LIST[crystal.getCrystalLevel()][random];
			byte level = (byte)crystal.getEnhanceOdds(random);
			if (level == 1) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_ITEM_OF_CHIP_COMPLETE, Warning.UI_STRING_TIP));
			} else if (level == 2) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_ITEM_OF_SUCCESS_COMPLETE, Warning.UI_STRING_TIP));
			} else {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_ITEM_OF_FLASH_COMPLETE, Warning.UI_STRING_TIP));
				isFlash = true;
			}
			_ei.getGeneralEnhance().addDetailEnhance(_jewelIndex, level);
			//--------start修改标记--------
			//add by zhengl; date: 2011-05-20; note: 闪光全服通知
			int flash = _ei.getGeneralEnhance().getFlash();
			int lvl = _ei.getGeneralEnhance().getLevel();
			if (flash == 1 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_ONE_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 2 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_TWO_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 3 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_THREE_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 4 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_FOUR_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 5 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_FIVE_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 6 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_SIX_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 7 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_SEVEN_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null, content);
			} else if (flash == 8 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_EIGHT_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null, content);
			} else if (flash == 9 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_NINE_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 10 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_TEN_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 11 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_ELEVEN_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (flash == 12 && isFlash) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_TWELVE_FLASH_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%s", String.valueOf(lvl))
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(
	        			ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			}
			//add by zhengl; date: 2011-03-08; note: 获得装备发光效果
			short pngID = -1;
			short aunID = -1;
			if(_ei.getArchetype() instanceof Weapon) {
				pngID = _ei.getGeneralEnhance().getFlashView()[0];
				aunID = _ei.getGeneralEnhance().getFlashView()[1];
			} else if( _ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM ) {
				pngID = _ei.getGeneralEnhance().getArmorFlashView()[0];
				aunID = _ei.getGeneralEnhance().getArmorFlashView()[1];
			}
			String changeValue = _ei.getGeneralEnhance().getUpEndString();
			ResponseMessageQueue.getInstance().put(
					_player.getMsgQueueIndex(),
					new EquipmentEnhanceChangeNotify(
							_ei.getInstanceID(), 
							_ei.getGeneralEnhance().detail, 
							pngID, 
							aunID, 
							_ei.getArchetype().getWearBodyPart().value(), 
							changeValue)
					);
			GoodsDAO.updateEquipmentEnhance(_ei.getInstanceID(), buildGenericEnhanceDesc(_ei));
	        PlayerServiceImpl.getInstance().addMoney(_player, 
	        		-GoodsServiceImpl.getInstance().getConfig().enhance_money_list[_jewelIndex],
	                1, PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING, "镶嵌金钱消耗");

			// 从装备的强化属性上重新刷新属性
			if (_ei.getArchetype() instanceof Weapon) {
				_ei.resetWeaponMaxMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
				_ei.resetWeaponMinMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
				_ei.resetWeaponMaxPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());
				_ei.resetWeaponMinPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());

				ClothesOrWeaponChangeNotify msg = new ClothesOrWeaponChangeNotify(
						_player,
						((Weapon) _ei.getArchetype()).getWearBodyPart(), 
						_ei.getArchetype().getImageID(), 
						_ei.getArchetype().getAnimationID(),
						_ei.getGeneralEnhance().getLevel(),
						((Weapon) _ei.getArchetype()), false, pngID, aunID );
				MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, true, _player.getID());
			}

			PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
			PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
			try {
				//edit by zhengl; date: 2011-03-08; note:删除物品使用新的方式,可以记录日志.
//				crystal.remove(_player, _crystalLocationOfBag);
				GoodsServiceImpl.getInstance().deleteSingleGoods(
						_player,
	                    _player.getInventory().getSpecialGoodsBag(),
	                    crystal, 1,
	                    CauseLog.ENHANCE);
			} catch (BagException e) {
				return;
			}
		}
	}

	/**
	 * 装备打孔.
	 * <p>
	 * 如果成功的话,必然需要执行的4个操作 1,装备实际值的强化 2,数据库的修改 3,回复报文通知玩家 4,消耗宝石的删除.
	 * 
	 * @param _player
	 * @param _locationOfEquipment
	 * @param _ei
	 * @throws BagException
	 */
	public void perforateEquipment(HeroPlayer _player, EquipmentInstance _ei) throws BagException {
		int stoneNum = _player.getInventory().getSpecialGoodsBag()
				.getGoodsNumber(Crystal.STONE_PWEDOEATE_LIST[0]);
		Crystal crystal = (Crystal) GoodsContents.getGoods(Crystal.STONE_PWEDOEATE_LIST[0]);
		if (stoneNum <= 0) {
			//edit by zhengl ; date: 2010-12-16 ; note: 暂时先这样提示
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning("没有打孔石", Warning.UI_STRING_TIP));
			return;
		}
		if(crystal == null) {
			log.error("出现严重错误,获得打孔石为NULL");
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning("打孔失败,请重试", Warning.UI_STRING_TIP));
			return;
		}
		byte hole = _ei.getGeneralEnhance().getHole();
		if (crystal.getUseType() != 0) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
					new Warning(
							Tip.TIP_ITEM_OF_WRONG_TYPE + " " + crystal.getUseType(), 
							Warning.UI_STRING_TIP));
			return;
		}
		if (hole >= MAX_LEVEL_OF_ENHANCE) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_MAX_POSITION, Warning.UI_STRING_TIP));
			return;
		}
		if(_player.getMoney() 
				< GoodsServiceImpl.getInstance().getConfig().perforate_money_list[hole]) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_MONEY_LACK, Warning.UI_STRING_TIP));
			return;
		}

		int random = RANDOM_BUILDER.nextInt(10);
		byte isDamage = 0;//默认为未被摧毁状态
		log.info("打孔,取到随机数:"+random);
		//指定孔位打孔成功率
		if (PERFORATE_ODDS_LIST[hole][random] == 1) {
			if (_ei.getGeneralEnhance().addPerforate()) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning(Tip.TIP_ITEM_OF_PWEDOEATE_COMPLETE, Warning.UI_STRING_TIP));
			}
			//edit by zhengl; date: 2011-05-20; note: 强化装备的结果世界通知.
			if (hole == 10) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_ELEVEN_PERFORATE_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			} else if (hole == 11) {
	        	String content = Tip.TIP_ITEM_NOTICE_BY_TWELVE_PERFORATE_END
										.replaceAll("%p", _player.getName())
										.replaceAll("%e", _ei.getArchetype().getName());
	        	ChatQueue.getInstance().add(ChatServiceImpl.TOP_SYSTEM_WORLD, null, null, null,content);
			}
		} else {
            GoodsServiceImpl.getInstance().diceEquipmentOfBag(
                    _player,
                    _player.getInventory().getEquipmentBag(), _ei,
                    LoctionLog.BAG, CauseLog.DICEEQUIP);
            isDamage = 1;
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_BIG_LOSE, Warning.UI_STRING_TIP));
		}
		//add by zhengl; date: 2011-03-08; note: 获得装备发光效果
		short pngID = -1;
		short aunID = -1;
		if(_ei.getArchetype() instanceof Weapon) {
			pngID = _ei.getGeneralEnhance().getFlashView()[0];
			aunID = _ei.getGeneralEnhance().getFlashView()[1];
		} else if( _ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM ) {
			pngID = _ei.getGeneralEnhance().getArmorFlashView()[0];
			aunID = _ei.getGeneralEnhance().getArmorFlashView()[1];
		}
        PlayerServiceImpl.getInstance().addMoney(_player, 
        		-GoodsServiceImpl.getInstance().getConfig().perforate_money_list[hole],
                1, PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING, "打孔金钱消耗");
		// -----装备数值变化-----
		String changeValue = _ei.getGeneralEnhance().getUpEndString();
		ResponseMessageQueue.getInstance().put(
				_player.getMsgQueueIndex(),
				new EquipmentEnhanceChangeNotify(
						_ei.getInstanceID(), _ei.getGeneralEnhance().detail, pngID, aunID, 
						_ei.getArchetype().getWearBodyPart().value(), changeValue, isDamage));
		GoodsDAO.updateEquipmentEnhance(_ei.getInstanceID(), buildGenericEnhanceDesc(_ei));
		// -----装备数值变化-----
		try {
			//edit by zhengl; date: 2011-03-08; note: 删除物品使用新的方式,可以记录日志.
//			crystal.remove(_player, _stoneLocationOfBag);
			GoodsServiceImpl.getInstance().deleteSingleGoods(
					_player,
                    _player.getInventory().getSpecialGoodsBag(),
                    crystal, 1,
                    CauseLog.PERFORATE);
		} catch (BagException e) {
			return;
		}

	}

    /**
     * 给玩家添加一个剥离宝石，并扣除玩家相应的点数
     * @param _player
     */
    public void addJewelForPlayer(HeroPlayer _player){
    	log.debug("add jewel for palyer ... 玩家添加一个剥离宝石");
        Crystal crystal = (Crystal) GoodsContents.getGoods(Crystal.STONE_WRECK_LIST[0]);
        if(ChargeServiceImpl.getInstance().reducePoint(_player,BUY_JEWEL_NEED_POINT,crystal.getID(),crystal.getName(),1, ServiceType.BUY_TOOLS)){
//        if(ChargeServiceImpl.getInstance().updatePointAmount(_player,-BUY_JEWEL_NEED_POINT)){
        	log.debug("扣除点数成功....");
            GoodsServiceImpl.getInstance().addGoods2Package(_player, Crystal.STONE_WRECK_LIST[0], 1,CauseLog.BUY);
        }
    }

    /**
     * 宝石剥离
     * <p>
     * 是否有剥离宝石，如果没有，则检查点数是否够买剥离宝石
     * @param _player
     * @param _stoneLocationOfBag
     * @param _locationOfEquipment
     * @param _ei
     * @param _jewelIndex
     */
    public void jewelWreck(HeroPlayer _player, EquipmentInstance _ei, int _jewelIndex) {
    	log.debug("宝石剥离 start ....");
        int stoneID = Crystal.STONE_WRECK_LIST[0];//这里的剥离宝石ID是写死的
        int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(stoneID);
        Crystal crystal = (Crystal) GoodsContents.getGoods(stoneID);
        if(stoneNum == 0) {
            int player_point = _player.getChargeInfo().pointAmount; //玩家当前的点数
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
            		new Warning(
							Tip.TIP_PUBLIC_OF_NOT_HAVE_ITEM_TO_CHARGE.replaceAll(
									"%fn", crystal.getName()), 
							Warning.UI_TOOLTIP_AND_EVENT_TIP, 
							Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
        } else {
            if(null == crystal){
            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
            			new Warning("剥离宝石失败,请重试", Warning.UI_STRING_TIP));
            	log.error("出现严重错误,获取剥离宝石失败.");
            	return;
            }
        	log.debug("使用剥离宝石...");
            jewelWreck(_player, crystal, _ei, _jewelIndex);
        }
    }

	/**
	 * 摧毁宝石 
	 * 如果成功的话,必然需要执行的4个操作 1,装备实际值的强化 2,数据库的修改 3,回复报文通知玩家 4,消耗宝石的删除.
	 * 
	 * @param _player
	 *            玩家
	 * @param _locationOfEquipment
	 *            装备所在格子
	 * @param _ei
	 *            装备实体
	 * @param _jewelIndex
	 *            宝石位置
	 */
	private void jewelWreck(HeroPlayer _player, Crystal crystal, 
			EquipmentInstance _ei, int _jewelIndex) {
//		Crystal crystal = (Crystal) GoodsContents.getGoods(_stoneID);
		if (crystal.getUseType() != 2) {
			log.debug(Tip.TIP_ITEM_OF_WRONG_TYPE);
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_WRONG_TYPE, Warning.UI_STRING_TIP));
			return;
		}
		if(!_ei.getGeneralEnhance().haveJewel(_jewelIndex)){
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_NO_COMPLETE_WRECK, Warning.UI_STRING_TIP));
			return;
		}
		
		if(_ei.getGeneralEnhance().getJewelLevel(_jewelIndex) == 3){
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_NOT_WRECK, Warning.UI_STRING_TIP));
			return;
		}
		if(_player.getMoney() 
				< GoodsServiceImpl.getInstance().getConfig().wreck_money_list[_jewelIndex]) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(
					Tip.TIP_ITEM_OF_MONEY_LACK_WRECK, Warning.UI_STRING_TIP));
			return;
		}
		boolean result = _ei.getGeneralEnhance().resetEnhanceLevel(_jewelIndex);
		if (result) {
			log.info("摧毁宝石成功..");
			//add by zhengl; date: 2011-03-08; note: 获得装备发光效果
			short pngID = -1;
			short aunID = -1;
			if(_ei.getArchetype() instanceof Weapon) {
				pngID = _ei.getGeneralEnhance().getFlashView()[0];
				aunID = _ei.getGeneralEnhance().getFlashView()[1];
			} else if( _ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM ) {
				pngID = _ei.getGeneralEnhance().getArmorFlashView()[0];
				aunID = _ei.getGeneralEnhance().getArmorFlashView()[1];
			}
	        PlayerServiceImpl.getInstance().addMoney(_player, 
	        		-GoodsServiceImpl.getInstance().getConfig().wreck_money_list[_jewelIndex],
	                1, PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING, "剥离金钱消耗");
			// -----装备数值变化-----
			String changeValue = _ei.getGeneralEnhance().getUpEndString();
			ResponseMessageQueue.getInstance().put(
					_player.getMsgQueueIndex(),
					new EquipmentEnhanceChangeNotify(_ei.getInstanceID(), _ei
							.getGeneralEnhance().detail, pngID, aunID, 
							_ei.getArchetype().getWearBodyPart().value(), changeValue));
			GoodsDAO.updateEquipmentEnhance(_ei.getInstanceID(), buildGenericEnhanceDesc(_ei));
			// -----装备数值变化-----
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
					new Warning(Tip.TIP_ITEM_OF_WRECK_COMPLETE, Warning.UI_STRING_TIP));

			try {
				//edit by zhengl; date: 2011-03-08; note:删除物品使用新的方式,可以记录日志.
//				log.debug("移除玩家背包里的剥离宝石...");
//				crystal.remove(_player, _crystalLocationOfBag);
				GoodsServiceImpl.getInstance().deleteSingleGoods(
						_player,
	                    _player.getInventory().getSpecialGoodsBag(),
	                    crystal, 1,
	                    CauseLog.PULLOUT);
			} catch (BagException e) {
				return;
			}
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new ResponseSingleHoleEnhanceProperty(_ei));
		} else {
			log.debug(Tip.TIP_ITEM_OF_WRONG);
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_OF_WRONG, Warning.UI_STRING_TIP));
		}
	}

	/**
	 * 解析强化属性在数据库中的描述，并赋值
	 * 
	 * @return
	 */
	public void parseEnhanceDesc(EquipmentInstance _ei, String _genericEnhanceDesc,
			String _bloodyEnhanceDesc) {
		try {
			if (!_genericEnhanceDesc.equals("")) {
				String[] genericEnhanceDataDesc = _genericEnhanceDesc.split("#");
				// add: zhengl
				// date: 2010-09-17
				// note: 打个日志看看
				/*for (int c = 0; c < genericEnhanceDataDesc.length; c++) {
					log.info("genericEnhanceDataDesc," + c + ";value="
							+ genericEnhanceDataDesc[c]);
				}*/
				// add end
				//调整数据库中,强化字段的规则;
				//-1=无孔,0=空孔,1=碎裂型强化,2=成功型强化,3=闪光型强化
				for (int i = 0; i < genericEnhanceDataDesc.length; i++) {
					byte enhance = Byte.parseByte(genericEnhanceDataDesc[i]);
					if(enhance == -1) {
						_ei.getGeneralEnhance().initAllGrid(i,false,(byte)0);
					} else {
						_ei.getGeneralEnhance().initAllGrid(i,true,enhance);
					}
				}
				_ei.getGeneralEnhance().initModulus();

				_ei.resetWeaponMaxMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
				_ei.resetWeaponMinMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
				_ei.resetWeaponMaxPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());
				_ei.resetWeaponMinPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());
			}

			if (!_bloodyEnhanceDesc.equals("")) {
				String[] bloodyEnhanceDataDesc = _bloodyEnhanceDesc.split("#");
				Weapon weapon = (Weapon) _ei.getArchetype();

				if (weapon.getTrait() == EGoodsTrait.YU_ZHI
						|| weapon.getTrait() == EGoodsTrait.SHENG_QI) {
					// _ei.getWeaponBloodyEnhance().setPveNumber(
					// Integer.parseInt(bloodyEnhanceDataDesc[0]));
					// _ei.getWeaponBloodyEnhance().setPvpNumber(
					// Integer.parseInt(bloodyEnhanceDataDesc[1]));
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 获取普通强化属性在数据库中的描述格式<p>
	 * 保存到数据库时，保存generalEnhance[i][1]
	 * -1=无孔,0=空孔,1=碎裂型强化,2=成功型强化,3=闪光型强化
	 * @return
	 */
	public String buildGenericEnhanceDesc(EquipmentInstance _ei) {
		StringBuffer desc = new StringBuffer();

		byte[][] generalEnhance = _ei.getGeneralEnhance().detail;
		for (int i = 0; i < generalEnhance.length; i++) {
			if (generalEnhance[i][0] == 0) {
				desc.append("-1#");
			} else {
				desc.append(generalEnhance[i][1] + "#");
			}
		}
		return desc.toString();
	}

	/**
	 * 获取血腥强化属性在数据库中的描述格式
	 * 
	 * @return
	 */
	public String buildBloodyEnhanceDesc(EquipmentInstance _ei) {
		StringBuffer desc = new StringBuffer();

		if (_ei.getArchetype() instanceof Weapon && null != _ei.getWeaponBloodyEnhance()) {
			desc.append(_ei.getWeaponBloodyEnhance().getPveNumber()).append("#").append(
					_ei.getWeaponBloodyEnhance().getPvpNumber()).append("#");
		}

		return desc.toString();
	}

	/**
	 * 强化几率列表  3=闪光;2=成功;1=碎裂
	 * （低级几率：10%闪光、30%碎裂、60%成功）
	 * （中级几率：10%闪光、30%碎裂、60%成功）
	 * （高级几率：10%闪光、30%碎裂、60%成功）
	 */
//	private static final byte[][] ENHANCE_ODDS_LIST = { { 3, 2, 2, 2, 1, 1, 1, 1, 1, 1 }, 
//		{ 3, 2, 2, 2, 1, 1, 1, 1, 1, 1 }, 
//		{ 3, 2, 2, 2, 1, 1, 1, 1, 1, 1 },
//		{ 3, 2, 2, 2, 1, 1, 1, 1, 1, 1 }  };
	/** 打孔几率列表; 0= */
	private static final byte[][] PERFORATE_ODDS_LIST = { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 0, 1, 1, 1, 0, 1, 1, 1, 1, 1 }, { 0, 0, 1, 0, 1, 0, 1, 0, 1, 0 },
			{ 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 } };

	/**
	 * 闪光
	 */
	public static final int ENHANCE_RESULT_OF_FLASH = 2;

	/**
	 * 碎裂
	 */
	public static final int ENHANCE_RESULT_OF_CHIP = 0;

	/**
	 * 成功
	 */
	public static final int ENHANCE_RESULT_OF_SUCCESS = 1;

	/**
	 * 已经无法强化
	 */
	public static final int ENHANCE_RESULT_OF_HAS_FULL = 2;

	/**
	 * 杀戮和屠魔强化最高等级
	 */
	public static final int MAX_LEVEL_OF_PVE_AND_PVP = 12;

	/**
	 * 普通强化最高等级
	 */
	public static final int MAX_LEVEL_OF_ENHANCE = 12;

	/**
	 * 随机数生成器
	 */
	private Random RANDOM_BUILDER = new Random();

    /**
     * 购买剥离宝石所需要的点数
     */
    private static final int BUY_JEWEL_NEED_POINT = 100;
}
