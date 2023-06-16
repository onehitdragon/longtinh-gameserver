package hero.item.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EGoodsType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-25 下午01:57:18
 * @描述 ：物品类型
 */

public enum EGoodsType
{
    /**
     * 装备
     */
    EQUIPMENT("装备", 1),
    /**
     * 药水
     */
    MEDICAMENT("药水", 2),
    /**
     * 材料
     */
    MATERIAL("材料", 3),
    /**
     * 任务物品
     */
    TASK_TOOL("任务物品", 4),
    /**
     * 特殊物品
     */
    SPECIAL_GOODS("特殊物品", 5),
    
    
    /**
     * 宠物装备
     */
    PET_EQUIQ_GOODS("宠物装备",7),
    /**
     * 宠物
     */
    PET("宠物",8),
    
    /**
     * 宠物物品
     */
    PET_GOODS("宠物物品",9);

    /**
     * 描述
     */
    private String description;

    /**
     * 标识值
     */
    private byte   value;

    /**
     * 构造
     * 
     * @param _desc 描述
     */
    EGoodsType(String _desc, int _value)
    {
        description = _desc;
        value = (byte) _value;
    }

    /**
     * 获取描述
     * 
     * @return 类型描述
     */
    public String getDescription ()
    {
        return description;
    }

    /**
     * 获取标识值
     * 
     * @return
     */
    public byte value ()
    {
        return value;
    }

    /**
     * 获取物品类型
     * 
     * @param _value
     * @return
     */
    public static EGoodsType getGoodsType (byte _value)
    {
        for (EGoodsType goodsType : EGoodsType.values())
        {
            if (goodsType.value == _value)
            {
                return goodsType;
            }
        }

        return null;
    }
}
