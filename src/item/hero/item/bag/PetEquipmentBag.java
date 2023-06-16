package hero.item.bag;

import static hero.item.detail.EBodyPartOfEquipment.ADORM;
import static hero.item.detail.EBodyPartOfEquipment.BOSOM;
import static hero.item.detail.EBodyPartOfEquipment.FINGER;
import static hero.item.detail.EBodyPartOfEquipment.FOOT;
import static hero.item.detail.EBodyPartOfEquipment.HAND;
import static hero.item.detail.EBodyPartOfEquipment.HEAD;
import static hero.item.detail.EBodyPartOfEquipment.WRIST;
import hero.item.EquipmentInstance;
import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import hero.item.detail.EPetBodyPartOfEquipment;

/**
 * 
 * @author jiaodongjie
 * 宠物装备背包,在宠物栏里显示
 */
public class PetEquipmentBag extends EquipmentContainer
{

	public PetEquipmentBag(short size)
	{
		super(size);
		
	}

	@Override
	public byte getContainerType ()
	{
		
		return EquipmentContainer.PET_BAG;
	}
	
	/**
     * 向宠物背包中添加装备
     * 
     * @param _equipmentInstance 宠物装备
     * @return 装备在宠物背包中的位置
     */
	public int add (EquipmentInstance _equipmentInstance) throws BagException
    {
        if (0 == emptyGridNumber)
        {
            throw PackageExceptionFactory.getInstance().getFullException(
                    EGoodsType.PET_EQUIQ_GOODS);
        }

        if (null != _equipmentInstance && _equipmentInstance.getOwnerType() == 2)
        {
            synchronized (equipmentList)
            {
                for (int i = 0; i < equipmentList.length; i++)
                {
                    if (null == equipmentList[i])
                    {
                        equipmentList[i] = _equipmentInstance;
                        emptyGridNumber--;

                        return i;
                    }
                }
            }
        }
        else
        {
            throw PackageExceptionFactory.getInstance().getException("无效的宠物装备数据");
        }

        return -1;
    }
	
	/**
     * 向宠物背包中指定位置添加装备
     * 
     * @param _index 包裹里的位置索引
     * @param _equipmentInstance 装备
     * @return 是否添加成功
     * @throws BagException
     */
    public boolean add (int _index, EquipmentInstance _equipmentInstance)
            throws BagException
    {
        if (0 == emptyGridNumber)
        {
            throw PackageExceptionFactory.getInstance().getFullException(
                    EGoodsType.PET_EQUIQ_GOODS);
        }

        if (null != _equipmentInstance && _equipmentInstance.getOwnerType() == 2)
        {
            synchronized (equipmentList)
            {
                if (null != equipmentList[_index])
                {
                    return false;
                }
                else
                {
                    equipmentList[_index] = _equipmentInstance;
                    emptyGridNumber--;

                    return true;
                }
            }
        }
        else
        {
            throw PackageExceptionFactory.getInstance().getException("无效的宠物装备数据");
        }
    }
	
	/**
     * 升级背包
     * 
     * @return 是否成功升级（false：已达到顶级）
     */
    public boolean upgrade ()
    {
        if (getSize() == Inventory.BAG_MAX_SIZE)
        {
            return false;
        }

        EquipmentInstance[] newContainer = new EquipmentInstance[getSize()
                + Inventory.STEP_GRID_SIZE];

        System.arraycopy(equipmentList, 0, newContainer, 0, getSize());

        equipmentList = newContainer;
        emptyGridNumber += Inventory.STEP_GRID_SIZE;

        return true;
    }
    
    /**
     * 整理宠物背包
     * 按 头部、身躯、爪部、尾部 顺序排列
     * @return
     */
    public boolean clearUp(){
    	boolean changed = false;

        EPetBodyPartOfEquipment bodyPart = EPetBodyPartOfEquipment.HEAD;

        int i = 0;
        int j = i + 1;

        for (; i < equipmentList.length - 1;)
        {
            if (null != equipmentList[i]
                    && bodyPart == equipmentList[i].getArchetype().getWearBodyPart())
            {
                i++;
                j = i + 1;

                continue;
            }

            for (; j < equipmentList.length;)
            {
                if (null != equipmentList[j]
                        && bodyPart == equipmentList[j].getArchetype().getWearBodyPart())
                {
                    EquipmentInstance ei = equipmentList[i];
                    equipmentList[i] = equipmentList[j];
                    equipmentList[j] = ei;

                    changed = true;

                    i++;

                    break;
                }
                else
                {
                    j++;
                }
            }

            if (j < equipmentList.length)
            {
                j++;
            }
            else
            {
                j = i + 1;

                switch (bodyPart)
                {
                    case HEAD:
                    {
                        bodyPart = EPetBodyPartOfEquipment.BODY;

                        break;
                    }
                    case BODY:
                    {
                        bodyPart = EPetBodyPartOfEquipment.CLAW;

                        break;
                    }
                    case CLAW:
                    {
                        bodyPart = EPetBodyPartOfEquipment.TAIL;

                        break;
                    }
                    
                    default:
                    {
                        return changed;
                    }
                }
            }
        }

        return changed;
    }

}
