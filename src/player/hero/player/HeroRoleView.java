package hero.player;

import hero.item.Armor;
import hero.item.Equipment;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.Weapon.EWeaponType;
import hero.item.bag.EquipmentContainer;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.dictionary.WeaponDict;
import hero.item.enhance.GenericEnhance;
import hero.item.service.EquipmentFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import yoyo.service.tools.database.DBServiceImpl;
import yoyo.tools.YOYOOutputStream;

import hero.pet.Pet;
import hero.pet.service.PetDAO;
import hero.pet.service.PetServiceImpl;
import hero.player.define.EClan;
import hero.player.define.ESex;
import hero.player.service.PlayerConfig;
import hero.player.service.PlayerServiceImpl;
import hero.share.EVocation;
import hero.share.service.LogWriter;



/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file NRoleView.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-9-19 上午11:04:42
 * 
 *       <pre>
 *      Description:
 * </pre>
 */
public class HeroRoleView {
	private static HeroRoleView instance;

	/**
	 * 获取玩家身上穿戴装备SQL脚本 edit by zhengl ; date: 2010-11-25 ; note: 添加展示
	 * generic_enhance_desc
	 */
	private static final String SELECT_PLAYER_BODY_EQUIPMENT_SQL = "SELECT equipment_id,generic_enhance_desc "
			+ "FROM equipment_instance JOIN player_carry_equipment "
			+ "ON user_id=? AND container_type=? "
			+ "AND player_carry_equipment.instance_id="
			+ "equipment_instance.instance_id LIMIT 8";

	public static HeroRoleView getInstance() {
		if (instance == null) {
			instance = new HeroRoleView();
		}
		return instance;
	}

	private HeroRoleView() {
	}

	/**
	 * 获取新建角色的描述信息 **待修改**
	 * 
	 * @param _userID
	 * @param _clientType
	 * @return
	 */
	public byte[] getNewRoleDesc(int _userID, String _nickname, EClan _clan, EVocation _vocation,
			ESex _sex, int _clientType) {
		YOYOOutputStream outPipe = null;
		byte[] rtnValue = null;

		try {
			outPipe = new YOYOOutputStream();
			outPipe.writeInt(_userID);
			setSingleRoleView(outPipe, _nickname, _vocation, (short)1, _sex, _clan);

			outPipe.flush();
			rtnValue = outPipe.getBytes();

			return rtnValue;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != outPipe) {
					outPipe.close();
					outPipe = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取角色列表的描述信息
	 * 
	 * @param _userIDs
	 * @param _clientType
	 * @return
	 */
	public byte[] get(int[] _userIDs) {
		YOYOOutputStream outPipe = new YOYOOutputStream();
		byte[] rtnValue = null;

		try {
			for (int i = 0; i < _userIDs.length; i++) {
				outPipe.writeInt(_userIDs[i]);
				setRoleViewFromDB(outPipe, _userIDs[i]);
			}

			outPipe.flush();
			rtnValue = outPipe.getBytes();
			outPipe.close();
			outPipe = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtnValue;
	}
	/**
	 * 初始化角色的时候
	 * @param _outPipe
	 * @param _nickname
	 * @param _vocation
	 * @param _level
	 * @param _sex
	 * @param _clan
	 */
	private void setSingleRoleView(YOYOOutputStream _outPipe, String _nickname, EVocation _vocation,
			short _level, ESex _sex, EClan _clan) {
		try {
			_outPipe.writeUTF(_nickname);
			_outPipe.writeByte(_vocation.value());
			_outPipe.writeByte(_clan.getID());
			_outPipe.writeByte(_sex.value());
			_outPipe.writeShort(_level);
			PlayerConfig config = PlayerServiceImpl.getInstance().getConfig();
			
//			//改变报文序的报文:
			_outPipe.writeShort(config.getLimbsConfig().getHeadImage(_sex)); //头
			_outPipe.writeShort(config.getLimbsConfig().getHeadAnimation(_sex)); //头
			_outPipe.writeShort(config.getLimbsConfig().getHairImage(_sex, _clan)); //头发
			_outPipe.writeShort(config.getLimbsConfig().getHairAnimation(_sex, _clan)); //头发
			
			_outPipe.writeShort( config.getInitArmorImageGroup(_vocation.getType())[0] );
			_outPipe.writeShort( config.getInitArmorImageGroup(_vocation.getType())[1]);
			_outPipe.writeByte( config.getInitArmorImageGroup(_vocation.getType())[2] );
			_outPipe.writeShort( config.getInitArmorImageGroup(_vocation.getType())[3] );
			_outPipe.writeShort( config.getInitArmorImageGroup(_vocation.getType())[4]);
			_outPipe.writeByte( config.getInitArmorImageGroup(_vocation.getType())[5] );
			_outPipe.writeShort(-1); //初始化角色的时候不可能有衣服强化光效所以下发-1
			_outPipe.writeShort(-1); //初始化角色的时候不可能有衣服强化光效所以下发-1
			
			_outPipe.writeShort(config.getLimbsConfig().getLegImage(_sex)); //腿
			_outPipe.writeShort(config.getLimbsConfig().getLegAnimation(_sex)); //腿
			
			_outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[0]); //武器
			_outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[1]);
			_outPipe.writeShort(-1); //宠物
			_outPipe.writeShort(-1);
			_outPipe.writeShort(-1); //套装
			_outPipe.writeShort(-1);
			_outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[2]); //武器攻击
			_outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[3]);
			_outPipe.writeShort(-1); //武器强化发光
			_outPipe.writeShort(-1);
			_outPipe.writeShort(config.getLimbsConfig().getDieImage(_clan)); //死亡
			_outPipe.writeShort(config.getLimbsConfig().getDieAnimation(_clan)); //死亡
			_outPipe.writeShort(config.getLimbsConfig().getTailImage(_sex, _clan)); //尾巴
			_outPipe.writeShort(config.getLimbsConfig().getTailAnimation(_sex, _clan)); //尾巴
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSingleRoleView(YOYOOutputStream _outPipe, String _nickname, EVocation _vocation,
			short _level, ESex _sex, EClan _clan, short _clothesImageID, short _clothesAnimation, 
			byte _isDistinguish, Weapon _weapon, short _hatImageID, short _hatAnimation, 
			byte _hatDistinguish, int _userID, short _enhanceClothesPNG, short _enhanceClothesANU, 
			short _enhanceWeaponPNG, short _enhanceWeaponANU, int _weaponType) {
		try {
			_outPipe.writeUTF(_nickname);
			_outPipe.writeByte(_vocation.value());
			_outPipe.writeByte(_clan.getID());
			_outPipe.writeByte(_sex.value());
			_outPipe.writeShort(_level);
			
//			_outPipe.writeShort(_clothesImageID);
//			_outPipe.writeByte(_isDistinguish);
//			_outPipe.writeShort(_hatImageID);
//			_outPipe.writeByte(_hatDistinguish);
//			_outPipe.writeShort(_weaponImageID);
//			_outPipe.writeByte(_weaponEnhanceLevel);
			PlayerConfig config = PlayerServiceImpl.getInstance().getConfig();
			
//			//改变报文序的报文:
			_outPipe.writeShort(config.getLimbsConfig().getHeadImage(_sex)); //头
			_outPipe.writeShort(config.getLimbsConfig().getHeadAnimation(_sex)); //头
			_outPipe.writeShort(config.getLimbsConfig().getHairImage(_sex, _clan)); //头发
			_outPipe.writeShort(config.getLimbsConfig().getHairAnimation(_sex, _clan)); //头发
			
			_outPipe.writeShort(_hatImageID);
			_outPipe.writeShort(_hatAnimation);
			_outPipe.writeByte(_hatDistinguish);
			_outPipe.writeShort(_clothesImageID);
			_outPipe.writeShort(_clothesAnimation);
			_outPipe.writeByte(_isDistinguish);
			//add by zhengl; date: 2011-03-17; note: 添加衣服强化效果
			_outPipe.writeShort(_enhanceClothesPNG); //衣服强化光效图片
			_outPipe.writeShort(_enhanceClothesANU); //衣服强化光效动画
			
			_outPipe.writeShort(config.getLimbsConfig().getLegImage(_sex)); //腿
			_outPipe.writeShort(config.getLimbsConfig().getLegAnimation(_sex)); //腿
			_outPipe.writeShort(_weaponType);
			if(_weapon != null) {
				_outPipe.writeShort(_weapon.getImageID()); //武器
				_outPipe.writeShort(_weapon.getAnimationID());
			} else {
				_outPipe.writeShort(-1); //武器
				_outPipe.writeShort(-1);
			}
			
			Pet pet = PetServiceImpl.getInstance().getPet(_userID, PetDAO.selectMountPet(_userID));
			_outPipe.writeShort(pet != null ? pet.getImageID() : -1); //宠物
			_outPipe.writeShort(pet != null ? pet.getAnimationID() : -1);
			_outPipe.writeShort(-1); //套装
			_outPipe.writeShort(-1);
			if(_weapon != null) {
				_outPipe.writeShort(_weapon.getLightID()); //武器攻击
				_outPipe.writeShort(_weapon.getLightAnimation());
				_outPipe.writeShort(_enhanceWeaponPNG); //武器强化发光 暂时先只发-1
				_outPipe.writeShort(_enhanceWeaponANU);
			} else {
				_outPipe.writeShort(-1); //武器攻击
				_outPipe.writeShort(-1);
				_outPipe.writeShort(-1); //武器为空,发光-1
				_outPipe.writeShort(-1);
			}
			_outPipe.writeShort(config.getLimbsConfig().getDieImage(_clan)); //死亡
			_outPipe.writeShort(config.getLimbsConfig().getDieAnimation(_clan)); //死亡
			_outPipe.writeShort(config.getLimbsConfig().getTailImage(_sex, _clan)); //尾巴
			_outPipe.writeShort(config.getLimbsConfig().getTailAnimation(_sex, _clan)); //尾巴
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得角色列表中其中一个角色
	 * @param _outPipe
	 * @param _userID
	 * @return
	 */
	private byte[] setRoleViewFromDB(YOYOOutputStream _outPipe, int _userID) {
		// edit: zhengl
		// date: 2010-11-25
		// note: 大量修改了本方法,添加功能:武器强化等级下发,装备展示的时候是否区分种族下发
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DBServiceImpl.getInstance().getConnection();
			pstm = conn
					.prepareStatement("SELECT nickname,clan,vocation,sex,lvl FROM player WHERE user_id=?");
			pstm.setInt(1, _userID);
			rs = pstm.executeQuery();

			if (rs.next()) {
				String nickname = rs.getString("nickname");
				EClan clan = EClan.getClan(rs.getShort("clan"));// 阵营－1：龙山，2：河姆渡
				EVocation vocation = EVocation.getVocationByID(rs.getShort("vocation"));// 职业－1：武者，2：道者
				ESex sex = ESex.getSex(rs.getShort("sex"));// 性别－1：男，2：女
				short level = rs.getShort("lvl");

				if (level > PlayerServiceImpl.getInstance().getConfig().max_level) {
					level = PlayerServiceImpl.getInstance().getConfig().max_level;
				}

				rs.close();
				rs = null;
				pstm.close();
				pstm = null;

				pstm = conn.prepareStatement(SELECT_PLAYER_BODY_EQUIPMENT_SQL);

				pstm.setInt(1, _userID);
				pstm.setShort(2, EquipmentContainer.BODY);

				rs = pstm.executeQuery();

				Weapon weapon = null;
				short clothesImageID = 0, clothesAnimation = 0;
				short enhanceClothesPNG = 0, enhanceClothesANU = 0;
				short enhanceWeaponPNG = 0, enhanceWeaponANU = 0;
				short hatImageID = -1;
				short hatAnimation = -1;
				byte weaponLevel = 0;
				byte isDistinguish = 0;
				byte isDistinguishHat = 0;

				while (rs.next()) {
					int equipmentID = rs.getInt("equipment_id");
					String goodsDesc = rs.getString("generic_enhance_desc");

					Equipment e = (Equipment) EquipmentFactory.getInstance().getEquipmentArchetype(
							equipmentID);
					if (null != e) {
						GenericEnhance ge = new GenericEnhance(e.getEquipmentType());
						if (e instanceof Armor) {
							if ( ((Armor) e).getWearBodyPart() == EBodyPartOfEquipment.BOSOM ) {
								clothesImageID = e.getImageID();
								clothesAnimation = e.getAnimationID();
								isDistinguish = ((Armor) e).getDistinguish();
								enhanceClothesPNG = 
									ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.BOSOM)[0];
								enhanceClothesANU = 
									ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.BOSOM)[1];
							} else if (((Armor) e).getWearBodyPart() == EBodyPartOfEquipment.HEAD) {
								hatImageID = e.getImageID();
								hatAnimation = e.getAnimationID();
								isDistinguishHat = ((Armor) e).getDistinguish();
							}
						} else {
							weapon = (Weapon)e;
							enhanceWeaponPNG = 
								ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.WEAPON)[0];
							enhanceWeaponANU = 
								ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.WEAPON)[1];
						}
					}
				}
				if (clothesImageID == 0) {
					clothesImageID = PlayerServiceImpl.getInstance().getConfig()
					.getDefaultClothesImageID(sex);
					clothesAnimation = PlayerServiceImpl.getInstance().getConfig()
					.getDefaultClothesAnimation(sex);
				}
				int type = -1;
				if (weapon != null) 
				{
					type = weapon.getWeaponType().getID();
				}
				setSingleRoleView(_outPipe, nickname, vocation, level, sex, clan, clothesImageID, 
						clothesAnimation, isDistinguish, weapon, hatImageID, hatAnimation, 
						isDistinguishHat, _userID, enhanceClothesPNG, enhanceClothesANU, 
						enhanceWeaponPNG, enhanceWeaponANU, type);
			}
			
			
		} catch (Exception e) {
			LogWriter.error(this, e);
			e.printStackTrace();
		} finally {
			try {
				if (null != rs) {
					rs.close();
					rs = null;
				}
				if (null != pstm) {
					pstm.close();
					pstm = null;
				}
				if (null != conn) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
			}
		}
		return null;
	}
}
