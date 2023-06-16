package hero.item.bag;

import hero.item.dictionary.GoodsContents;
import org.apache.log4j.Logger;

import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.pet.PetList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Inventory.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-21 下午04:58:48
 * @描述 ：玩家背包，里面包含装备背包、材料背包、药水背包、任务道具背包、特殊物品背包、宠物装备包、宠物列表容器
 */

public class Inventory
{
	private static Logger log = Logger.getLogger(Inventory.class);
    /**
     * 归属玩家userID
     */
    private int            masterUserID;

    /**
     * 装备包
     */
    private EquipmentBag   equpmentBag;

    /**
     * 药水包
     */
    private SingleGoodsBag medicamentBag;

    /**
     * 材料包
     */
    private SingleGoodsBag materialBag;

    /**
     * 任务道具包
     */
    private SingleGoodsBag taskToolBag;

    /**
     * 特殊物品包、其他物品包
     */
    private SingleGoodsBag specialGoodsBag;
    
    /**
     * 宠物装备包
     */
    private PetEquipmentBag petEquipmentBag;
    
    /**
     * 宠物列表包
     */
    private PetContainer petContainer;
    
    /**
     * 宠物物品背包
     */
    private SingleGoodsBag petGoodsBag;

    /**
     * 构造（新建角色）
     * 
     * @param _masterUserID 所属玩家userID
     */
    public Inventory(int _masterUserID)
    {
        masterUserID = _masterUserID;
        equpmentBag = new EquipmentBag(GENERIC_BAG_GRID_SIZE);
        medicamentBag = new SingleGoodsBag(GENERIC_BAG_GRID_SIZE);
        materialBag = new SingleGoodsBag(MATERIAL_BAG_GRID_SIZE);
        specialGoodsBag = new SingleGoodsBag(GENERIC_BAG_GRID_SIZE);
        taskToolBag = new SingleGoodsBag(TOOL_GOODS_BAG_GRID_SIZE);
        petEquipmentBag = new PetEquipmentBag(GENERIC_BAG_GRID_SIZE);
        petContainer = new PetContainer(GENERIC_BAG_GRID_SIZE);
        petGoodsBag = new SingleGoodsBag(GENERIC_BAG_GRID_SIZE);
    }

    /**
     * 构造（原有角色登陆）
     * 
     * @param _masterUserID 所属玩家userID
     * @param _equpmentBagSize 装备背包大小
     * @param _medicamentBagSize 药水背包大小
     * @param _materialBagSize 材料背包大小
     * @param _specialGoodsBagSize 特殊物品背包大小
     */
    public Inventory(int _masterUserID, byte[] _bagSizeList)
    {
        masterUserID = _masterUserID;
        equpmentBag = new EquipmentBag(_bagSizeList[0]);
        medicamentBag = new SingleGoodsBag(_bagSizeList[1]);
        medicamentBag.initMaster(masterUserID);
        materialBag = new SingleGoodsBag(_bagSizeList[2]);
        specialGoodsBag = new SingleGoodsBag(_bagSizeList[3]);
        specialGoodsBag.initMaster(masterUserID); //特殊物品现在也需要master
        taskToolBag = new SingleGoodsBag(TOOL_GOODS_BAG_GRID_SIZE);
        petEquipmentBag = new PetEquipmentBag(_bagSizeList[4]);
        petContainer = new PetContainer(_bagSizeList[5]);
        petGoodsBag = new SingleGoodsBag(_bagSizeList[6]);
    }

    /**
     * 获取包裹归属玩家
     * 
     * @return
     */
    public int getOwnerUserID ()
    {
        return masterUserID;
    }

    /**
     * 添加非装备物品
     * 
     * @param _goods
     */
    public short[] addSingleGoods (SingleGoods _goods, int _nums)
            throws BagException
    {
        short[] addResult = null;
        EGoodsType goodsType = GoodsContents.getGoodsType(_goods.getID());//这里要重新获取物品类型，因为宠物物品虽然也是特殊物品，但是是分开存放的
        log.debug("add singleGoods type = " + goodsType);
        switch (goodsType)
        {
            case MATERIAL:
            {
                addResult = materialBag.add(_goods, _nums);

                break;
            }
            case MEDICAMENT:
            {
                addResult = medicamentBag.add(_goods, _nums);

                break;
            }
            case TASK_TOOL:
            {
                addResult = taskToolBag.add(_goods, _nums);

                break;
            }
            case SPECIAL_GOODS:
            {
                addResult = specialGoodsBag.add(_goods, _nums);

                break;
            }
            case PET_GOODS:
            {
            	addResult = petGoodsBag.add(_goods, _nums);
            	break;
            }
        }

        return addResult;
    }

    /**
     * 添加装备实例
     * 
     * @param _equipIns
     * @throws BagException
     */
    public int addEquipmentIns (EquipmentInstance _equipmentInstance)
            throws BagException
    {
        _equipmentInstance.changeOwnerUserID(masterUserID);
        
        log.debug("添加装备实例 equipment owner type = " + _equipmentInstance.getOwnerType());

        if(_equipmentInstance.getOwnerType() == 2){
        	return addPetEquipment(_equipmentInstance);
        }

        return equpmentBag.add(_equipmentInstance);
    }
    
    /**
     * 添加宠物装备实例
     * @param _equipmentInstance
     * @return
     * @throws BagException
     */
    public int addPetEquipment(EquipmentInstance _equipmentInstance) throws BagException{
    	_equipmentInstance.changeOwnerUserID(masterUserID);

        return petEquipmentBag.add(_equipmentInstance);
    }

    /**
     * 获得装备物品背包
     * 
     * @return
     */
    public EquipmentBag getEquipmentBag ()
    {
        return equpmentBag;
    }

    /**
     * 添加物品
     * 
     * @param _goodsID 物品模型编号
     * @param _nums 添加的数量
     * @return
     * @throws BagException
     */
    public short[] addSingleGoods (int _goodsID, int _nums) throws BagException
    {
        Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(_goodsID);

        if (EGoodsType.EQUIPMENT != goods.getGoodsType() 
        		&& EGoodsType.PET_EQUIQ_GOODS != goods.getGoodsType())
        {
            return addSingleGoods((SingleGoods) goods, _nums);
        }

        return null;
    }

    /**
     * 获取药水包裹
     * 
     * @return
     */
    public SingleGoodsBag getMedicamentBag ()
    {
        return medicamentBag;
    }

    /**
     * 获取材料包裹
     * 
     * @return
     */
    public SingleGoodsBag getMaterialBag ()
    {
        return materialBag;
    }

    /**
     * 获取任务物品包裹
     * 
     * @return
     */
    public SingleGoodsBag getTaskToolBag ()
    {
        return taskToolBag;
    }

    /**
     * 获取特殊物品包裹
     * 
     * @return
     */
    public SingleGoodsBag getSpecialGoodsBag ()
    {
        return specialGoodsBag;
    }
    
    /**
     * 获取宠物装备背包
     * @return
     */
    public PetEquipmentBag getPetEquipmentBag(){
    	return petEquipmentBag;
    }
    
    /**
     * 获取宠物列表包
     * @return
     */
    public PetContainer getPetContainer(){
    	return petContainer;
    }
    /**
     * 获取宠物物品背包
     * @return
     */
    public SingleGoodsBag getPetGoodsBag(){
    	return petGoodsBag;
    }

    /**
     * 初始化背包格子数量
     */
    public static final byte GENERIC_BAG_GRID_SIZE    = 16;

    /**
     * 材料背包初始容量
     */
    public static final byte MATERIAL_BAG_GRID_SIZE   = 16;

    /**
     * 任务道具背包格子数量
     */
    public static final byte TOOL_GOODS_BAG_GRID_SIZE = 16;

    /**
     * 升级递增的格子数量
     */
    public static final byte STEP_GRID_SIZE           = 8;

    /**
     * 背包最大格子数量
     */
    public static final byte BAG_MAX_SIZE             = 40;
   
}
