package hero.item;

import java.util.Random;

import org.apache.log4j.Logger;

import hero.item.detail.EGoodsTrait;
import hero.item.enhance.GenericEnhance;
import hero.item.enhance.WeaponBloodyEnhance;
import hero.share.service.IDManager;
import hero.share.service.MagicDamage;

/**
 * @文件 EquipmentInstance.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-14 下午08:34:28
 * @描述 ：装备实例，引用装备原型，对可变属性进行单独定义，包括：耐久度、拥有者、创建者、强化
 */

public class EquipmentInstance
{
	private static Logger log = Logger.getLogger(EquipmentInstance.class);
    /**
     * 实例编号
     */
    private int                 instanceID;

    /**
     * 创建者编号
     */
    private int                 creatorUserID;

    /**
     * 拥有者编号
     */
    private int                 ownerUserID;

    /**
     * 当前耐久度折算
     */
    private int                 currentDurability;

    /**
     * 是否存在封印
     */
    private boolean             existSeal;

    /**
     * 是否绑定
     */
    private boolean             isBind;

    /**
     * 普通强化属性（12次宝石镶嵌）
     */
    private GenericEnhance      genericEnhance;

    /**
     * 武器血腥强化
     */
    private WeaponBloodyEnhance weaponBloodyEnhance;

    /**
     * 原型对象
     */
    private EqGoods           archetype;

    /**
     * 最小物理攻击力
     */
    private int                 weaponMinPhysicsAttack;

    /**
     * 最大物理攻击力
     */
    private int                 weaponMaxPhysicsAttack;

    /**
     * 武器携带的魔法伤害，设定为只会携带一种魔法效果
     */
    private MagicDamage         weaponMagicMamage;
    
    /**
     * 拥有者类型 1:玩家    2:宠物
     */
    private short ownerType;
    
    public static EquipmentInstance init(EqGoods _e, int _creatorUserID, int _ownerUserID){
    	try{
        	if(_e instanceof Equipment){
        		log.debug(" 初始化装备实例类型为玩家武器");
        		Equipment _equipment = (Equipment)_e;
        		return new EquipmentInstance(_equipment, _creatorUserID, _ownerUserID);
        	}
        	if(_e instanceof PetEquipment){
        		log.debug(" 初始化装备实例类型为宠物武器");
        		PetEquipment _equipment = (PetEquipment)_e;
        		return new EquipmentInstance(_equipment, _creatorUserID, _ownerUserID);
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return null;
    }

    /**
     * 构造，产生的新玩家装备
     * 
     * @param _equipment 装备原型
     * @param _creatorUserID 创建者userID
     * @param _ownerUserID 拥有者userID
     * @param _existSeal 是否存在封印
     * @param _isBind 是否已绑定
     */
    public EquipmentInstance(Equipment _equipment, int _creatorUserID,
            int _ownerUserID)
    {
        instanceID = IDManager.buildEquipmentInsID();
        log.debug(" 构造，产生的新玩家装备 id = " + instanceID);
        creatorUserID = _creatorUserID;
        ownerUserID = _ownerUserID;
        archetype = _equipment;
        currentDurability = _equipment.getMaxDurabilityPoint()
                * _equipment.getDurabilityConvertRate();
        existSeal = _equipment.existSeal();
        setOwnerType((short)1);

        if (Equipment.BIND_TYPE_OF_PICK == _equipment.getBindType())
            isBind = true;

        if (_equipment instanceof Weapon)
        {
            Weapon weapon = (Weapon) _equipment;

            weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();

            if (null != weapon.getMagicDamage())
            {
                weaponMagicMamage = new MagicDamage();
                weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }

            if (EGoodsTrait.YU_ZHI == _equipment.getTrait()
                    || EGoodsTrait.SHENG_QI == _equipment.getTrait())
            {
                weaponBloodyEnhance = new WeaponBloodyEnhance(weapon
                        .getPveEnhanceID(), weapon.getPvpEnhanceID());
            }
        }
        log.debug(" getOwnerType= "+getOwnerType()); 
        genericEnhance = new GenericEnhance(((Equipment)archetype).getEquipmentType());
    }
    
    public static EquipmentInstance init(EqGoods _e, int _creatorUserID,
            int _ownerUserID, int _instanceID, int _currentDurabilityPoint,
            boolean _existSeal, boolean _isBind){
    	if(_e instanceof Equipment){
    		log.debug("根据数据创建装备实例是 玩家装备");
    		Equipment _equipment = (Equipment)_e;
    		return new EquipmentInstance(_equipment, _creatorUserID, _ownerUserID, _instanceID,
    				_currentDurabilityPoint,_existSeal,_isBind);
    	}
    	if(_e instanceof PetEquipment){
    		log.debug(" 根据数据创建装备实例是 宠物装备");
    		PetEquipment _equipment = (PetEquipment)_e;
    		return new EquipmentInstance(_equipment, _creatorUserID, _ownerUserID, _instanceID,
    				_currentDurabilityPoint,_existSeal,_isBind);
    	}
    	return null;
    }

    /**
     * 构造，依据数据库中的信息创建
     * 玩家装备
     * @param _equipment 装备原型
     * @param _id 装备实例编号
     * @param _currentDurabilityPoint 当前耐久度
     * @param _existSeal 是否存在封印
     * @param _isBind 是否已绑定
     */
    public EquipmentInstance(Equipment _equipment, int _creatorUserID,
            int _ownerUserID, int _instanceID, int _currentDurabilityPoint,
            boolean _existSeal, boolean _isBind)
    {
        archetype = _equipment;
        creatorUserID = _creatorUserID;
        ownerUserID = _ownerUserID;
        instanceID = _instanceID;
        currentDurability = _currentDurabilityPoint
                * _equipment.getDurabilityConvertRate();
        existSeal = _existSeal;
        isBind = _isBind;
        setOwnerType((short)1);

        if (_equipment instanceof Weapon)
        {
            Weapon weapon = (Weapon) _equipment;

            weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();

            if (null != weapon.getMagicDamage())
            {
                weaponMagicMamage = new MagicDamage();
                weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }

            if (EGoodsTrait.YU_ZHI == _equipment.getTrait()
                    || EGoodsTrait.SHENG_QI == _equipment.getTrait())
            {
                weaponBloodyEnhance = new WeaponBloodyEnhance(
                        ((Weapon) _equipment).getPveEnhanceID(),
                        ((Weapon) _equipment).getPvpEnhanceID());
            }
        }

        genericEnhance = new GenericEnhance(((Equipment)archetype).getEquipmentType());
    }
    
    /**
     * 构造，产生的新宠物装备
     * 
     * @param _equipment 装备原型
     * @param _creatorUserID 创建者userID
     * @param _ownerUserID 拥有者userID
     * @param _existSeal 是否存在封印
     * @param _isBind 是否已绑定
     */
    public EquipmentInstance(PetEquipment _equipment, int _creatorUserID,
            int _ownerUserID)
    {
        instanceID = IDManager.buildEquipmentInsID();
        creatorUserID = _creatorUserID;
        ownerUserID = _ownerUserID;
        archetype = _equipment;
        currentDurability = _equipment.getMaxDurabilityPoint()
                * _equipment.getDurabilityConvertRate();
        setOwnerType((short)2);
        
        if (_equipment instanceof PetWeapon)
        {
            PetWeapon weapon = (PetWeapon) _equipment;

            weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();

            if (null != weapon.getMagicDamage())
            {
                weaponMagicMamage = new MagicDamage();
                weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }

        }
    }
    /**
    * 构造，产生的新宠物装备
    */
    public EquipmentInstance(PetEquipment _equipment, int _creatorUserID,
            int _ownerUserID, int _instanceID, int _currentDurabilityPoint,
            boolean _existSeal, boolean _isBind){
    	instanceID = _instanceID;
        creatorUserID = _creatorUserID;
        ownerUserID = _ownerUserID;
        archetype = _equipment;
        currentDurability = _equipment.getMaxDurabilityPoint()
                * _equipment.getDurabilityConvertRate();
        setOwnerType((short)2);
        
        if (_equipment instanceof PetWeapon)
        {
            PetWeapon weapon = (PetWeapon) _equipment;

            weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();

            if (null != weapon.getMagicDamage())
            {
                weaponMagicMamage = new MagicDamage();
                weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }

        }
    }
    

    /**
     * 获取物品原型
     * 
     * @return
     */
    public EqGoods getArchetype ()
    {
    	if(getOwnerType() == 1)
    		return (Equipment)archetype;
    	else 
    		return (PetEquipment)archetype;
    }
    
    
    /**
     * 获取拥有者类型
     * 1:玩家   2:宠物 
     * @return
     */
    public short getOwnerType(){
    	return ownerType;
    }
    public void setOwnerType(short ownerType){
    	this.ownerType = ownerType;
    }

    /**
     * 改变拥有者编号
     * 
     * @param _ownerUserID
     */
    public void changeOwnerUserID (int _ownerUserID)
    {
        ownerUserID = _ownerUserID;
    }

    /**
     * 获取创建者编号
     * 
     * @return
     */
    public int getCreatorUserID ()
    {
        return creatorUserID;
    }

    /**
     * 获取拥有者编号
     * 
     * @return
     */
    public int getOwnerUserID ()
    {
        return ownerUserID;
    }

    /**
     * 返回装备实例编号
     * 
     * @return
     */
    public int getInstanceID ()
    {
        return instanceID;
    }

    /**
     * 获取普通强化属性
     * 
     * @return
     */
    public GenericEnhance getGeneralEnhance ()
    {
        return genericEnhance;
    }

    /**
     * 设置武器血腥强化属性
     */
    public void setWeaponBloodyEnhance (WeaponBloodyEnhance _bloodyEnhance)
    {
        weaponBloodyEnhance = _bloodyEnhance;
    }

    /**
     * 获取武器血腥强化属性
     * 
     * @return 武器血腥强化属性类
     */
    public WeaponBloodyEnhance getWeaponBloodyEnhance ()
    {
        return weaponBloodyEnhance;
    }

    /**
     * 获取当前耐久度
     * 
     * @return 装备当前耐久度
     */
    public int getCurrentDurabilityPoint ()
    {
        int currentDurabilityPoint = currentDurability
                / archetype.getDurabilityConvertRate();

        if (currentDurability % archetype.getDurabilityConvertRate() > 0)
        {
            currentDurabilityPoint += 1;
        }

        return currentDurabilityPoint;
    }

    /**
     * 降低耐久折算
     * 
     * @return 返回
     */
    public int reduceCurrentDurabilityPoint (int _point)
    {
        if (currentDurability > 0)
        {
            currentDurability -= _point;

            if (currentDurability < 0)
            {
                currentDurability = 0;
            }
        }

        return currentDurability;
    }

    /**
     * 降低耐久折算
     * 
     * @return 返回
     */
    public int reduceCurrentDurabilityPercent (int _percent)
    {
        if (currentDurability > 0)
        {
            currentDurability -= _percent
                    * getArchetype().getMaxDurabilityPoint()
                    * archetype.getDurabilityConvertRate() / 100F;

            if (currentDurability < 0)
            {
                currentDurability = 0;
            }
        }

        return currentDurability;
    }

    /**
     * 被修复
     */
    public void beRepaired ()
    {
        currentDurability = archetype.getMaxDurabilityPoint()
                * archetype.getDurabilityConvertRate();
    }

    /**
     * 设置封印状态（初始化、从数据库加载、解除3种情况下调用）
     * 
     * @param _isBeSealed
     */
    public void setSeal (boolean _isBeSealed)
    {
        existSeal = _isBeSealed;
    }

    /**
     * 是否被封印
     * 
     * @return
     */
    public boolean existSeal ()
    {
        return existSeal;
    }

    /**
     * 绑定
     * 
     * @param _isBeSealed
     */
    public void bind ()
    {
        isBind = true;
    }

    /**
     * 是否已绑定
     * 
     * @return
     */
    public boolean isBind ()
    {
        return isBind;
    }

    /**
     * 计算修理费用
     * 
     * @return
     */
    public int getRepairCharge ()
    {
        int currentDurabilityPoint = getCurrentDurabilityPoint();

        if (currentDurabilityPoint < archetype.getMaxDurabilityPoint())
        {
            int cost = (int) (archetype.getSellPrice()
                    * 1.0
                    * (archetype.getMaxDurabilityPoint() - currentDurabilityPoint)
                    / (4 * archetype.getMaxDurabilityPoint()) + .5);

            return cost < 1 ? 1 : cost;
        }
        else
        {
            return 0;
        }
    }

    /**
     * 重置武器最小物理攻击力
     * 
     * @param _modulus 加成系数
     */
    public void resetWeaponMinPhysicsAttack (float _modulus)
    {
        if (archetype instanceof Weapon && _modulus > 0)
        {
            Weapon weapon = (Weapon) archetype;

            weaponMinPhysicsAttack = weapon.getMinPhysicsAttack()
                    + (int) ((weapon.getMaxPhysicsAttack() + weapon
                            .getMinPhysicsAttack()) / 2 * _modulus);
        }
        if(archetype instanceof PetWeapon && _modulus > 0){
        	PetWeapon weapon = (PetWeapon) archetype;

            weaponMinPhysicsAttack = weapon.getMinPhysicsAttack()
                    + (int) ((weapon.getMaxPhysicsAttack() + weapon
                            .getMinPhysicsAttack()) / 2 * _modulus);
        }
    }

    /**
     * 获取武器最小物理攻击力
     * 
     * @return 最小物理攻击力
     */
    public int getWeaponMinPhysicsAttack ()
    {
        return weaponMinPhysicsAttack;
    }

    /**
     * 重置武器最大物理攻击力
     * 
     * @param _modulus 加成系数
     */
    public void resetWeaponMaxPhysicsAttack (float _modulus)
    {
        if (archetype instanceof Weapon && _modulus > 0)
        {
            Weapon weapon = (Weapon) archetype;

            weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack()
                    + (int) ((weapon.getMaxPhysicsAttack() + weapon
                            .getMinPhysicsAttack()) / 2 * _modulus);
        }
        if (archetype instanceof PetWeapon && _modulus > 0)
        {
        	PetWeapon weapon = (PetWeapon) archetype;

            weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack()
                    + (int) ((weapon.getMaxPhysicsAttack() + weapon
                            .getMinPhysicsAttack()) / 2 * _modulus);
        }
    }

    /**
     * 获取武器最大物理攻击力
     * 
     * @return 最大物理攻击力
     */
    public int getWeaponMaxPhysicsAttack ()
    {
        return weaponMaxPhysicsAttack;
    }

    /**
     * 武器物理攻击力，用于计算技能携带的攻击力
     * 
     * @return
     */
    public int getPhysicsAttack ()
    {
        return weaponMinPhysicsAttack
                + RANDOM.nextInt(weaponMaxPhysicsAttack
                        - weaponMinPhysicsAttack + 1);
    }

    /**
     * 获取武器魔法伤害属性
     * 
     * @return
     */
    public MagicDamage getMagicDamage ()
    {
        return weaponMagicMamage;
    }

    /**
     * 重置武器最大魔法伤害
     * 
     * @param _modulus 加成系数
     */
    public void resetWeaponMaxMagicAttack (float _modulus)
    {
        if (archetype instanceof Weapon && null != weaponMagicMamage
                && _modulus > 0)
        {
            Weapon weapon = (Weapon) archetype;

            weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue
                    + (int) (weapon.getMagicDamage().maxDamageValue * _modulus);
        }
        if (archetype instanceof PetWeapon && null != weaponMagicMamage
                && _modulus > 0)
        {
        	PetWeapon weapon = (PetWeapon) archetype;

            weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue
                    + (int) (weapon.getMagicDamage().maxDamageValue * _modulus);
        }
    }

    /**
     * 重置武器最小物理伤害
     * 
     * @param _modulus 加成系数
     */
    public void resetWeaponMinMagicAttack (float _modulus)
    {
        if (archetype instanceof Weapon && null != weaponMagicMamage && _modulus > 0)
        {
            Weapon weapon = (Weapon) archetype;

            weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue
                    + (int) (weapon.getMagicDamage().minDamageValue * _modulus);
        }
        if (archetype instanceof PetWeapon && null != weaponMagicMamage && _modulus > 0)
        {
        	PetWeapon weapon = (PetWeapon) archetype;

            weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue
                    + (int) (weapon.getMagicDamage().minDamageValue * _modulus);
        }
    }

    /**
     * 随机数生成器
     */
    private final static Random RANDOM = new Random();
}
