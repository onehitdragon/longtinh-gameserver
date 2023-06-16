package hero.item.service;

import org.apache.log4j.Logger;

import hero.item.EqGoods;
import hero.item.Equipment;
import hero.item.EquipmentInstance;
import hero.item.dictionary.ArmorDict;
import hero.item.dictionary.PetEquipmentDict;
import hero.item.dictionary.WeaponDict;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EquipmentInstanceFactory.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-4 下午04:42:16
 * @描述 ：装备工厂，提供装备模板获取方法和装备实例构建方法
 */

public class EquipmentFactory
{
	private static Logger log = Logger.getLogger(EquipmentFactory.class);
    /**
     * 单例
     */
    private static EquipmentFactory instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static EquipmentFactory getInstance ()
    {
        if (null == instance)
        {
            instance = new EquipmentFactory();
        }

        return instance;
    }

    /**
     * 私有构造
     */
    private EquipmentFactory()
    {

    }

    /**
     * 根据拥有者userID和装备模板对象创建装备实例
     * 
     * @param _creatorUserID 创建者userID
     * @param _ownerUserID 拥有者userID
     * @param _equipment 装备模板对象
     * @return
     */
    public EquipmentInstance build (int _creatorUserID, int _ownerUserID,
            EqGoods _equipment)
    {
        EquipmentInstance ei = EquipmentInstance.init(_equipment,
                _creatorUserID, _ownerUserID);
        log.debug( " 66: 根据拥有者userID和装备模板对象创建装备实例 id="+ei.getInstanceID() 
		+" ownertype = " + ei.getOwnerType());
        return ei;
    }

    /**
     * 根据创建者userID和装备模板对象创建装备实例
     * 
     * @param _creatorUserID 创建者userID
     * @param _ownerUserID 拥有者userID
     * @param _equipmentID 装备模板编号
     * @return
     */
    public EquipmentInstance build (int _creatorUserID, int _ownerUserID,
            int _equipmentID)
    {
    	log.debug( " 根据创建者userID和装备模板对象创建装备实例 eqid = " + _equipmentID);
    	EqGoods e = getEquipmentArchetype(_equipmentID);
        log.debug(" eqGoods = " + e);
        if (null != e)
        {
            return EquipmentInstance.init(e, _creatorUserID, _ownerUserID);
        }
        else
        {
            LogWriter.println("不存在的装备编号：" + _equipmentID);

            return null;
        }
    }

    /**
     * 根据数据创建装备实例
     * 
     * @param _creatorUserID 创建者userID
     * @param _ownerUserID 拥有者userID
     * @param _instanceID 装备实例ID
     * @param _equipmentID 模板装备ID
     * @param _currentDurabilityPoint 当前耐久度
     * @param _enhanceDesc 强化描述
     * @return
     */
    public EquipmentInstance buildFromDB (int _creatorUserID, int _ownerUserID,
            int _instanceID, int _equipmentID, int _currentDurabilityPoint,
            byte _existSeal, byte _isBind)
    {
    	EqGoods e = getEquipmentArchetype(_equipmentID);

        if (null != e)
        {
            log.debug( " 根据数据创建装备实例 goods id = " + e.getID());
            return EquipmentInstance.init(e, _creatorUserID, _ownerUserID,
                    _instanceID, _currentDurabilityPoint,
                    _existSeal == 1 ? true : false, _isBind == 1 ? true : false);
        }
        else
        {
            LogWriter.println("不存在的装备编号：" + _equipmentID);

            return null;
        }
    }

    /**
     * 根据装备ID获取装备模板对象
     * 
     * @param _equipmentID
     * @return
     */
    public EqGoods getEquipmentArchetype (int _equipmentID)
    {
        if (_equipmentID >= GoodsIDLimitDefine.ARMOR_ID_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.ARMOR_ID_LIMIT[1])
        {
            return ArmorDict.getInstance().getArmor(_equipmentID);
        }
        else if (_equipmentID >= GoodsIDLimitDefine.WEAPON_ID_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.WEAPON_ID_LIMIT[1])
        {
            return WeaponDict.getInstance().getWeapon(_equipmentID);
        }else if (_equipmentID >= GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[1])
        {
            return PetEquipmentDict.getInstance().getPetArmor(_equipmentID);
        }
        else if (_equipmentID >= GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[1])
        {
            return PetEquipmentDict.getInstance().getPetWeapon(_equipmentID);
        }

        return null;
    }
}
