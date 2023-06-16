package hero.item.bag;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PackageTypeDefine.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-18 下午02:54:47
 * @描述 背包类型定义
 */

public enum EBagType
{
    /**
     * 身上穿着
     */
    BODY_WEAR(10, "身上"),

    /**
     * 药水包
     */
    MEDICAMENT_BAG(36, "药水背包"),

    /**
     * 材料包
     */
    MATERIAL_BAG(37, "材料背包"),

    /**
     * 任务物品包
     */
    TASK_TOOL_BAG(38, "任务物品背包"),

    /**
     * 特殊物品包
     */
    SPECIAL_GOODS_BAG(39, "特殊物品"),

    /**
     * 装备背包
     */
    EQUIPMENT_BAG(56, "装备背包"),
    
    /**
     * 宠物装备背包
     */
    PET_EQUIPMENT_BAG(57, "宠物装备背包"),
    
    /**
     * 宠物身上
     */
    PET_BODY_WEAR(58,"宠物身上"),
    /**
     *  宠物物品背包
     */
    PET_GOODS_BAG(59,"宠物物品背包"),
    /**
     * 宠物背包
     */
    PET_BAG(60,"宠物背包"),
    /**
     * 仓库
     */
    STORAGE_BAG(99,"仓库");

    /**
     * 类型值
     */
    private byte   typeValue;

    /**
     * 描述
     */
    private String description;

    /**
     * 构造
     * 
     * @param _typeValue
     * @param _description
     */
    EBagType(int _typeValue, String _description)
    {
        typeValue = (byte) _typeValue;
        description = _description;
    }

    /**
     * 获取类型值
     * 
     * @return
     */
    public byte getTypeValue ()
    {
        return typeValue;
    }

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDescription ()
    {
        return description;
    }

    /**
     * 根据类型值获取背包类型
     * 
     * @param _typeValue
     * @return
     */
    public static EBagType getBagType (int _typeValue)
    {
        for (EBagType type : EBagType.values())
        {
            if (type.typeValue == _typeValue)
            {
                return type;
            }
        }

        return null;
    }
}
