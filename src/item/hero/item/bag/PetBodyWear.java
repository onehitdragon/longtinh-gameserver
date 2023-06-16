package hero.item.bag;

import hero.item.EquipmentInstance;
import hero.item.PetEquipment;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.detail.EPetBodyPartOfEquipment;

public class PetBodyWear extends EquipmentContainer
{
	/**
     * 身上装备栏格子数量
     */
    private final static short BODY_BAG_GRID_NUMBER = 4;
    
	public PetBodyWear()
	{
		super(BODY_BAG_GRID_NUMBER);
	}

	@Override
	public byte getContainerType ()
	{
		return EquipmentContainer.PET_BODY;
	}
	
	/**
     * 给宠物穿装备
     * 
     * @param _equipmentInstance 要穿的装备
     * @return 卸下的装备
     */
    public EquipmentInstance wear (EquipmentInstance _equipmentInstance)
    {
        if (null != _equipmentInstance && _equipmentInstance.getOwnerType() == 2)
        {
            int bodyPart = ((PetEquipment)_equipmentInstance.getArchetype()).getWearBodyPart().value();

            EquipmentInstance equipmentIns = equipmentList[bodyPart];
            equipmentList[bodyPart] = _equipmentInstance;

            if (null == equipmentIns)
            {
                emptyGridNumber--;
            }
            return equipmentIns;
        }

        return null;
    }
    
    /**
     * 获取宠物武器(爪部)
     * @return
     */
    public EquipmentInstance getPetEqWeapon(){
    	return equipmentList[EPetBodyPartOfEquipment.CLAW.value()];
    }
    /**
     * 获取头部装备
     * @return
     */
    public EquipmentInstance getPetEqHead(){
    	return equipmentList[EPetBodyPartOfEquipment.HEAD.value()];
    }
    /**
     * 获取身躯装备
     * @return
     */
    public EquipmentInstance getPetEqBody(){
    	return equipmentList[EPetBodyPartOfEquipment.BODY.value()];
    }
    /**
     * 获取尾部装备
     * @return
     */
    public EquipmentInstance getPetEqTail(){
    	return equipmentList[EPetBodyPartOfEquipment.TAIL.value()];
    }

}
