package hero.charge;

import hero.item.detail.EGoodsTrait;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MallGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-1-24 下午07:49:56
 * @描述 ：
 */

public class MallGoods
{
    /**
     * 编号
     */
    public int         id;

    /**
     * 名称
     */
    public String      name;

    /**
     * 类型
     */
    public byte        type;

    /**
     * 品质
     */
    public EGoodsTrait trait;

    /**
     * 价格（游戏点数）
     */
    public int         price;

    /**
     * 物品列表
     */
    public int[][]     goodsList;

    /**
     * 图标
     */
    public short       icon;

    /**
     * 图标
     */
    public String      desc;

    /**
     * 是否可以买多个
     */
    public byte        buyNumberPerTime;

    /**
     * 设置每次购买的数量
     */
    public void setBuyNumberPerTime (byte _buyNumberPerTime)
    {
        buyNumberPerTime = _buyNumberPerTime;
    }

    /**
     * 类型-装备
     */
    public static final byte TYPE_EQUIPMENT  = 1;

    /**
     * 类型-药水
     */
    public static final byte TYPE_MEDICAMENT = 2;

    /**
     * 类型-神器
     */
    public static final byte TYPE_MATERIAL   = 3;

    /**
     * 类型-技能书
     */
    public static final byte TYPE_SKILL_BOOK = 4;

    /**
     * 类型-宠物
     */
    public static final byte TYPE_PET        = 5;

    /**
     * 类型-礼包
     */
    public static final byte TYPE_BAG        = 6;
    
    /**
     * 类型-宠物装备
     */
    public static final byte TYPE_PET_EQUIP = 7;
    /**
     * 类型-宠物物品
     */
    public static final byte TYPE_PET_GOODS = 8;

    /**
     * 查找类型
     * 方法弃用.
     * 
     * @param _typeDesc
     * @return
     */
    private static final byte findType (String _typeDesc)
    {
        if (_typeDesc.equals("装备"))
        {
            return TYPE_EQUIPMENT;
        }
        else if (_typeDesc.equals("药水"))
        {
            return TYPE_MEDICAMENT;
        }
        else if (_typeDesc.equals("神器"))
        {
            return TYPE_MATERIAL;
        }
        else if (_typeDesc.equals("技能书"))
        {
            return TYPE_SKILL_BOOK;
        }
        else if (_typeDesc.equals("宠物"))
        {
            return TYPE_PET;
        }
        else if (_typeDesc.equals("礼包"))
        {
            return TYPE_BAG;
        }
        else if(_typeDesc.equals("宠物物品"))
        {
            return  TYPE_PET_GOODS;
        }
        else if(_typeDesc.equals("宠物装备"))
        {
            return  TYPE_PET_EQUIP;
        }

        return 0;
    }
}
