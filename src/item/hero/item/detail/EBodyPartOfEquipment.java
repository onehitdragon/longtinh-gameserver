package hero.item.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EBodyPartOfEquipment.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-21 上午10:16:10
 * @描述 ：装备身体部位
 */

public enum EBodyPartOfEquipment implements BodyPartOfEquipment
{
    /**
     * 头部
     */
    HEAD(0, "头部"),
    /**
     * 胸部
     */
    BOSOM(1, "胸部"),
    /**
     * 手部
     */
    HAND(2, "手部"),
    /**
     * 脚部
     */
    FOOT(3, "脚部"),

    /**
     * 武器
     */
    WEAPON(4, "武器"),

    /**
     * 颈部
     */
    FINGER(5, "颈部"),
    
    /**
     * 腰部
     */
    WAIST(6,"腰部"),
    /**
     * 手指
     */
    ADORM(7, "手指"),

    /**
     * 手腕
     */
    WRIST(8, "手腕"),
    /**
     * 跟随宠物
     */
    PET_F(9,"跟随宠物"),
    /**
     * 坐骑宠物
     */
    PET_S(10,"坐骑宠物"),
    /**
     * 扩展
     */
    EXTEND(11,"扩展");

    /**
     * 身体部位标识
     */
    private int    bodyPartValue;

    /**
     * 身体部位描述
     */
    private String desc;

    /**
     * 构造
     * 
     * @param _bodyPartValue
     * @param _desc
     */
    EBodyPartOfEquipment(int _bodyPartValue, String _desc)
    {
        bodyPartValue = _bodyPartValue;
        desc = _desc;
    }

    /**
     * 获取部位标识
     * 
     * @return
     */
    public int value ()
    {
        return bodyPartValue;
    }

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return desc;
    }

    /**
     * 根据身体部位标识获取部位枚举
     * 
     * @param _bodyPartValue
     * @return
     */
    public static final EBodyPartOfEquipment getBodyPart (int _bodyPartValue)
    {
        for (EBodyPartOfEquipment part : EBodyPartOfEquipment.values())
        {
            if (part.value() == _bodyPartValue)
            {
                return part;
            }
        }

        return null;
    }

    /**
     * 根据身体部位描述获取部位枚举
     * 
     * @param _desc
     * @return
     */
    public static final EBodyPartOfEquipment getBodyPart (String _desc)
    {
        for (EBodyPartOfEquipment part : EBodyPartOfEquipment.values())
        {
            if (part.getDesc().equals(_desc))
            {
                return part;
            }
        }

        return null;
    }
}
