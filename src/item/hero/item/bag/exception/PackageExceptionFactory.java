package hero.item.bag.exception;

import hero.item.detail.EGoodsType;
import hero.pet.PetList;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PackageExceptionFactory.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-27 上午09:59:05
 * @描述 ：背包异常的创建者，针对不同背包
 */

public class PackageExceptionFactory
{
    /**
     * 单例
     */
    private static PackageExceptionFactory instance;

    /**
     * 私有构造
     */
    private PackageExceptionFactory()
    {

    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static PackageExceptionFactory getInstance ()
    {
        if (null == instance)
        {
            instance = new PackageExceptionFactory();
        }

        return instance;
    }

    /**
     * 根据背包类型，创建特定异常实例
     * 
     * @param _exceptionMsg 异常提示信息
     * @return
     */
    public BagException getException (String _exceptionMsg)
    {
        return new BagException(_exceptionMsg);
    }

    /**
     * 创建背包满的异常实例
     * 
     * @param _goodsType 物品类型
     * @return
     */
    public BagException getFullException (EGoodsType _goodsType)
    {
        switch (_goodsType)
        {
            case EQUIPMENT:
            {
                return new BagException(Tip.TIP_ITEM_EQUIPMENT_BAG_IS_FULL);
            }
            case MATERIAL:
            {
                return new BagException(Tip.TIP_ITEM_MATERIAL_BAG_IS_FULL);
            }
            case MEDICAMENT:
            {
                return new BagException(Tip.TIP_ITEM_MEDICAMENT_BAG_IS_FULL);
            }
            case TASK_TOOL:
            {
                return new BagException(Tip.TIP_ITEM_TASK_TOOL_BAG_IS_FULL);
            }
            case SPECIAL_GOODS:
            {
                return new BagException(Tip.TIP_ITEM_SPECIAL_GOODS_BAG_IS_FULL);
            }
            case PET:
            {
            	return new BagException(Tip.TIP_ITEM_PET_CONTAIN_IS_FULL 
            			+ PetList.MAX_SHOW_NUMBER+" 只宠物！");
            }
        }

        return null;
    }
    

}
