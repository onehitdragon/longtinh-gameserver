package hero.item.bag;

import static hero.item.detail.EBodyPartOfEquipment.ADORM;
import static hero.item.detail.EBodyPartOfEquipment.BOSOM;
import static hero.item.detail.EBodyPartOfEquipment.FINGER;
import static hero.item.detail.EBodyPartOfEquipment.FOOT;
import static hero.item.detail.EBodyPartOfEquipment.HAND;
import static hero.item.detail.EBodyPartOfEquipment.HEAD;
import static hero.item.detail.EBodyPartOfEquipment.WRIST;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import hero.item.EquipmentInstance;
import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import hero.pet.Pet;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EquipmentPackage.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-20 下午10:43:29
 * @描述 ：装备背包
 */

public class EquipmentBag extends EquipmentContainer
{
	private static Logger log = Logger.getLogger(EquipmentBag.class);
    /**
     * 构造
     * 
     * @param _size 初始容量
     */
    public EquipmentBag(short _size)
    {
        super(_size);
    }
    
    /**
     * 背包里宠物的数量
     */
//    private int petnum; 
    /**
     * 玩家背包里放入宠物后，更新背包的空格子数量
     * @param newPetNum
     */
    /*public void setPetNum(int newPetNum){
//    	log.debug("玩家背包里已经放入宠物的数量 = "+petnum+"，重新放入的数量 = " + newPetNum +", 当前空格子数量="+emptyGridNumber);
    	if(petnum == 0){
    		emptyGridNumber = (short)(emptyGridNumber - (newPetNum-petnum));
    		petnum = newPetNum;
    	}else{
    		if(petnum > newPetNum){
    			emptyGridNumber = (short)(emptyGridNumber + (petnum-newPetNum));
    		}else if(petnum < newPetNum){
    			emptyGridNumber = (short)(emptyGridNumber - (newPetNum-petnum));
    		}
    		petnum = newPetNum;
    	}
//    	log.debug("玩家背包里放入宠物后, 背包的 空格子数量 = " + emptyGridNumber);
    }*/

    /**
     * 向背包中添加装备
     * 
     * @param _equipmentInstance 装备
     * @return 装备在背包中的位置
     */
    public int add (EquipmentInstance _equipmentInstance) throws BagException
    {
        if (0 == emptyGridNumber)
        {
            throw PackageExceptionFactory.getInstance().getFullException(
                    EGoodsType.EQUIPMENT);
        }
        log.debug("向背包中添加装备 id = " + _equipmentInstance.getInstanceID());
        if (null != _equipmentInstance && _equipmentInstance.getOwnerType() == 1)
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
            throw PackageExceptionFactory.getInstance().getException("无效的装备数据");
        }

        return -1;
    }

    /**
     * 向背包中指定位置添加装备
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
                    EGoodsType.EQUIPMENT);
        }

        if (null != _equipmentInstance && _equipmentInstance.getOwnerType() == 1)
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
            throw PackageExceptionFactory.getInstance().getException("无效的装备数据");
        }
    }

    @Override
    public byte getContainerType ()
    {
        // TODO Auto-generated method stub
        return EquipmentContainer.BAG;
    }

    /**
     * 整理背包（按照武器、头部、胸部、手套、鞋子、项链、戒指的顺序）
     */
    public boolean clearUp ()
    {
        boolean changed = false;

        EBodyPartOfEquipment bodyPart = EBodyPartOfEquipment.WEAPON;

        int i = 0;
        int j = i + 1;

        for (; i < equipmentList.length - 1;)
        {
            if (null != equipmentList[i]
                    && bodyPart == equipmentList[i].getArchetype()
                            .getWearBodyPart())
            {
                i++;
                j = i + 1;

                continue;
            }

            for (; j < equipmentList.length;)
            {
                if (null != equipmentList[j]
                        && bodyPart == equipmentList[j].getArchetype()
                                .getWearBodyPart())
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
                    case WEAPON:
                    {
                        bodyPart = HEAD;

                        break;
                    }
                    case HEAD:
                    {
                        bodyPart = BOSOM;

                        break;
                    }
                    case BOSOM:
                    {
                        bodyPart = HAND;

                        break;
                    }
                    case HAND:
                    {
                        bodyPart = FOOT;

                        break;
                    }
                    case FOOT:
                    {
                        bodyPart = FINGER;

                        break;
                    }
                    case FINGER:
                    {
                        bodyPart = ADORM;

                        break;
                    }
                    case ADORM:
                    {
                        bodyPart = WRIST;

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
}
