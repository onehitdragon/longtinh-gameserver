package hero.item.bag;

import hero.item.detail.EBodyPartOfEquipment;
import hero.item.Armor;
import hero.item.EquipmentInstance;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 BodyWear.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-21 上午09:56:22
 * @描述 ：身上穿着
 */

public class BodyWear extends EquipmentContainer
{
    /**
     * 构造
     */
    public BodyWear()
    {
        super(BodyWear.BODY_BAG_GRID_NUMBER);
    }

    /**
     * 穿装备
     * 
     * @param _equipmentInstance 要穿的装备
     * @return 卸下的装备
     */
    public EquipmentInstance wear (EquipmentInstance _equipmentInstance)
    {
        if (null != _equipmentInstance && _equipmentInstance.getOwnerType() == 1)
        {
            int bodyPart = _equipmentInstance.getArchetype().getWearBodyPart()
                    .value();

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

    @Override
    public byte getContainerType ()
    {
        // TODO Auto-generated method stub
        return EquipmentContainer.BODY;
    }

    /**
     * 获取胸甲
     * 
     * @return
     */
    public EquipmentInstance getBosom ()
    {
        return equipmentList[EBodyPartOfEquipment.BOSOM.value()];
    }
    /**
     * 获取头盔
     * @return
     */
    public EquipmentInstance getHead ()
    {
        return equipmentList[EBodyPartOfEquipment.HEAD.value()];
    }

    /**
     * 获取武器
     * 
     * @return
     */
    public EquipmentInstance getWeapon ()
    {
        return equipmentList[EBodyPartOfEquipment.WEAPON.value()];
    }
    
    /**
     * 获得当前套装等级.
     * @return
     */
    public int getSuiteLevel()
    {
    	int level = 0;
    	
    	return level;
    }
    

    /**
     * 是否存在套装
     * 
     * @return
     */
    public short hasSuite ()
    {
        if (null != equipmentList[EBodyPartOfEquipment.HEAD.value()]
                && null != equipmentList[EBodyPartOfEquipment.BOSOM.value()]
                && null != equipmentList[EBodyPartOfEquipment.HAND.value()]
                && null != equipmentList[EBodyPartOfEquipment.FOOT.value()])
        {
            short suiteID = ((Armor) equipmentList[EBodyPartOfEquipment.HEAD
                    .value()].getArchetype()).getSuiteID();

            if (0 != suiteID
                    && suiteID == ((Armor) equipmentList[EBodyPartOfEquipment.BOSOM
                            .value()].getArchetype()).getSuiteID())
            {
                if (0 != suiteID
                        && suiteID == ((Armor) equipmentList[EBodyPartOfEquipment.HAND
                                .value()].getArchetype()).getSuiteID())
                {
                    if (0 != suiteID
                            && suiteID == ((Armor) equipmentList[EBodyPartOfEquipment.HAND
                                    .value()].getArchetype()).getSuiteID())
                    {
                        return suiteID;
                    }
                }
            }

        }

        return 0;
    }

    /**
     * 身上装备栏格子数量
     */
    private final static short BODY_BAG_GRID_NUMBER = 8;
}
