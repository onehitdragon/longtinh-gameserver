package hero.item.service;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GoodsIDDefine.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-4 下午05:05:02
 * @描述 ：物品编号区间定义
 */

public class GoodsIDLimitDefine
{
    /**
     * 材料编号区间
     */
    public static final int[] MATERIAL_ID_LIMIT   = new int[]{320000,329999 };

    /**
     * 药水编号区间
     */
    public static final int[] MEDICAMENT_ID_LIMIT = new int[]{310000,319999 };

    /**
     * 任务道具编号区间
     */
    public static final int[] TASK_GOODS_ID_LIMIT = new int[]{330000,339999 };

    /**
     * 武器编号区间
     */
    public static final int[] WEAPON_ID_LIMIT     = new int[]{1108000,1112999 };

    /**
     * 防具编号区间
     */
    public static final int[] ARMOR_ID_LIMIT      = new int[]{1200000,1499999 };

    /**
     * 特殊物品编号区间
     */
    public static final int[] SPECIAL_GOODS_LIMIT = new int[]{340000,349999 };
    
    /**
     * 宠物技能书区间
     */
    public static final int[] PET_SKILL_BOOK	 = new int[]{351000,354999};
    
    /**
     * 宠物饲料编号区间
     */
    public static final int[] PET_FEED_GOODS_LIMIT = new int[]{355000,355999}; 
    
    /**
     * 宠物复活卷轴区间
     */
    public static final int[] PET_REVIVE_LIMIT = new int[]{356000,356999};
    /**
     * 宠物洗点道具区间
     */
    public static final int[] PET_DICARD_LIMIT = new int[]{357000,357999};
    /**
     * 宠物装备---防具区间
     */
    public static final int[] PET_EQUIP_ARMOT_LIMIT = new int[]{1514000,1516999};
    /**
     * 宠物装备---武器区间
     */
    public static final int[] PET_EQUIP_WEAPON_LIMIT = new int[]{1513000,1513999};
    /**
     * 商城销售宠物区间
     */
    public static final int[] PET_LIMIT = new int[]{9200,9299};
}
