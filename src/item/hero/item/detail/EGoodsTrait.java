package hero.item.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EGoodsTrait.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-21 上午09:45:20
 * @描述 ：物品的品质（石器、兵制、将制、御制、圣器5种）
 */

public enum EGoodsTrait
{
    /**
     * 普通（灰色）
     */
    SHI_QI(1, "普通", 0x000000),
    /**
     * 优秀（绿色）
     */
    BING_ZHI(2, "优秀", 0x09830C),

    /**
     * 精良（蓝色）
     */
    JIANG_ZHI(3, "精良", 0x0070c0),

    /**
     * 史诗（紫色）
     */
    YU_ZHI(4, "史诗", 0xc800c8),

    /**
     * 传说（红色）
     */
    SHENG_QI(5, "传说", 0xd30e17);

    /**
     * 类型编号
     */
    private int    value;

    /**
     * 品质名称
     */
    private String description;

    /**
     * 品质对应的颜色
     */
    private int    viewRGB;

    /**
     * 构造
     * 
     * @param _value 类型值
     * @param _desc 描述
     * @param _viewRGB 品质颜色RGB值
     */
    private EGoodsTrait(int _value, String _desc, int _viewRGB)
    {
        value = _value;
        description = _desc;
        viewRGB = _viewRGB;
    }

    /**
     * 获取类型值
     * 
     * @return
     */
    public int value ()
    {
        return value;
    }

    /**
     * 获取品质描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return description;
    }

    /**
     * 获取品质对应的客户端显示颜色
     * 
     * @return
     */
    public int getViewRGB ()
    {
        return viewRGB;
    }

    /**
     * 根据编号获取品质枚举
     * 
     * @param _value 品质类型值
     * @return 品质枚举
     */
    public static EGoodsTrait getTrait (int _value)
    {
        for (EGoodsTrait trait : EGoodsTrait.values())
        {
            if (trait.value() == _value)
            {
                return trait;
            }
        }

        return null;
    }

    /**
     * 根据品质描述获取品质枚举
     * 
     * @param _traitDesc 品质描述
     * @return 品质枚举
     */
    public static EGoodsTrait getTrait (String _traitDesc)
    {
        for (EGoodsTrait trait : EGoodsTrait.values())
        {
            if (trait.getDesc().equals(_traitDesc))
            {
                return trait;
            }
        }

        return null;
    }
}
