package hero.item.bag;

import hero.item.EquipmentInstance;
import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EquipmentContainer.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-8 下午03:10:46
 * @描述 ：装备容器，包括装备背包和身上装备栏
 */

public abstract class EquipmentContainer
{
    /**
     * 可用格子数量
     */
    protected short               emptyGridNumber;

    /**
     * 装备实例列表
     */
    protected EquipmentInstance[] equipmentList;

    /**
     * 构造
     * 
     * @param _size
     */
    public EquipmentContainer(short _size)
    {
        equipmentList = new EquipmentInstance[_size];
        emptyGridNumber = _size;
    }


    /**
     * 获取容量
     * 
     * @return
     */
    public int getSize ()
    {
        return equipmentList.length;
    }

    /**
     * 获取有装备的格子数量
     * 
     * @return
     */
    public int getFullGridNumber ()
    {
        return (equipmentList.length - emptyGridNumber);
    }

    /**
     * 获取空格子数量
     * 
     * @return
     */
    public int getEmptyGridNumber ()
    {
        return emptyGridNumber;
    }
    /**
     * 更新空格子的数量
     */
    public void setEmptyGridNumber(int emptyGridNumber){
    	this.emptyGridNumber = (short)emptyGridNumber;
    }
    

    /**
     * 移除指定位置装备
     * 
     * @param _gridIndex 位置
     * @return 被移除的装备实例
     * @throws BagException
     */
    public EquipmentInstance remove (int _gridIndex) throws BagException
    {
        if (_gridIndex >= 0 && _gridIndex < equipmentList.length)
        {
            synchronized (equipmentList)
            {
                EquipmentInstance ei = equipmentList[_gridIndex];
                equipmentList[_gridIndex] = null;

                emptyGridNumber++;

                return ei;
            }
        }
        else
        {
            throw PackageExceptionFactory.getInstance().getException(
                    "无效的装备位置：" + _gridIndex);
        }
    }

    /**
     * 根据实例编号移除装备
     * 
     * @param _equipmentInsID 装备实例编号
     * @return 被移除的装备实例
     * @throws BagException
     */
    public EquipmentInstance removeByInstanceID (int _equipmentInsID)
            throws BagException
    {
        synchronized (equipmentList)
        {
            for (int i = 0; i < equipmentList.length; i++)
            {
                if (null != equipmentList[i]
                        && _equipmentInsID == equipmentList[i].getInstanceID())
                {
                    EquipmentInstance ei = equipmentList[i];
                    equipmentList[i] = null;
                    emptyGridNumber++;

                    return ei;
                }
            }
        }

        throw PackageExceptionFactory.getInstance().getException("不存在的装备");
    }

    /**
     * 移除装备
     * 
     * @param _equipmentInstance 装备实例
     * @return 被移除装备的位置
     * @throws BagException
     */
    public int remove (EquipmentInstance _equipmentInstance)
            throws BagException
    {
        if (null != _equipmentInstance)
        {
            synchronized (equipmentList)
            {
                for (int i = 0; i < equipmentList.length; i++)
                {
                    if (equipmentList[i] == _equipmentInstance)
                    {
                        equipmentList[i] = null;
                        emptyGridNumber++;

                        return i;
                    }
                }
            }

            throw PackageExceptionFactory.getInstance().getException("不存在的装备");
        }

        return -1;
    }

    /**
     * 根据实例编号获取实例
     * 
     * @param _equipmentInstanceID
     * @return
     */
    public EquipmentInstance getEquipmentByInstanceID (int _equipmentInstanceID)
    {
        for (EquipmentInstance ei : equipmentList)
        {
            if (null != ei && ei.getInstanceID() == _equipmentInstanceID)
            {
                return ei;
            }
        }

        return null;
    }

    /**
     * 根据背包位置获取实例
     * 
     * @param _equipmentInstanceID
     * @return
     */
    public EquipmentInstance get (int _gridIndex)
    {
        if (_gridIndex >= 0 && _gridIndex < equipmentList.length)
        {
            return equipmentList[_gridIndex];
        }

        return null;
    }

    /**
     * 获取装备实例列表
     * 
     * @return
     */
    public EquipmentInstance[] getEquipmentList ()
    {
        return equipmentList;
    }

    /**
     * 装备容器类型
     * 
     * @return
     */
    public abstract byte getContainerType ();

    /**
     * 装备实例在容器中的位置
     * 
     * @param _equipmentIns
     * @return
     */
    public int indexOf (EquipmentInstance _equipmentIns)
    {
        if (null != _equipmentIns)
        {
            for (int i = 0; i < equipmentList.length; i++)
            {
                if (_equipmentIns == equipmentList[i])
                {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 背包
     */
    public static final byte BAG  = 1;

    /**
     * 身上
     */
    public static final byte BODY = 2;
    
    /**
     * 宠物背包
     */
    public static final byte PET_BAG = 3;
    /**
     * 宠物身上
     */
    public static final byte PET_BODY = 4;
}
