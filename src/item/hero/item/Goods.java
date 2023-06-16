package hero.item;

import hero.item.detail.EGoodsTrait;
import hero.item.detail.EGoodsType;
import hero.share.CharacterDefine;

/**
 * @文件 Goods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-12 下午04:04:56
 * @描述 ：所有物品的父类，子类包括：装备、药水、材料、任务道具、特殊物品五大类型
 */

public abstract class Goods
{
    /**
     * 编号
     */
    private int         id;

    /**
     * 名称
     */
    private String      name;

    /**
     * 出售价格（系统出售的价格）
     */
    private int         sellPrice;

    /**
     * 系统回收价格（玩家出售给商人的物品价格）
     */
    private int         retrievePrice;

    /**
     * 描述
     */
    protected String    description;

    /**
     * 是否可交易
     */
    protected boolean   exchangeable;

    /**
     * 能否出售
     */
    private boolean     canBeSell;

    /**
     * 使用需要的的角色等级
     */
    private int         needLevel;

    /**
     * 物品品质
     */
    private EGoodsTrait trait;

    /**
     * 物品框显示图标
     */
    private short       iconID;

    /**
     * 叠放数量
     */
    private short       stackNums;

    /**
     * 构造
     * 
     * @param _stackNums
     */
    public Goods(short _stackNums)
    {
        stackNums = _stackNums;
        setTrait(EGoodsTrait.SHI_QI);
        description = "";
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
     * 获取物品编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    /**
     * 设置物品编号
     * 
     * @param _id 编号
     */
    public void setID (int _id)
    {
        id = _id;
    }

    /**
     * 获取物品名称
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 设置物品名称
     * 
     * @param _name 名称
     */
    public void setName (String _name)
    {
        name = _name;
    }

    /**
     * 设置能出售
     */
    public void setCanBeSell ()
    {
        canBeSell = true;
    }

    /**
     * 能否出售
     * 
     * @return
     */
    public boolean canBeSell ()
    {
        return canBeSell;
    }

    /**
     * 获取物品出售价格
     * 
     * @return
     */
    public int getSellPrice ()
    {
        return sellPrice;
    }

    /**
     * 设置物品回收价格
     * 
     * @param _price 价格
     */
    public void setPrice (int _price)
    {
        if (_price <= 0)
        {
            sellPrice = 0;
        }
        else
        {
            sellPrice = _price;
        }

        retrievePrice = (int) (sellPrice / 3F + 0.5);

        if (retrievePrice == 0)
        {
            retrievePrice = 1;
        }
    }

    /**
     * 获取物品回收价格
     * 
     * @return
     */
    public int getRetrievePrice ()
    {
        return retrievePrice;
    }

    /**
     * 设置物品图标
     * 
     * @param _iconID 物品图标
     */
    public void setIconID (short _iconID)
    {
        iconID = _iconID;
    }

    /**
     * 获取物品图标
     * 
     * @return 物品图标
     */
    public short getIconID ()
    {
        return iconID;
    }

    /**
     * 设置使用需要的等级
     * 
     * @param _level
     */
    public void setNeedLevel (int _level)
    {
        needLevel = _level;
    }

    /**
     * 获取使用需要的等级
     * 
     * @return
     */
    public int getNeedLevel ()
    {
        return needLevel;
    }

    /**
     * 根据品质描述设置物品品质
     * 
     * @param _traitDesc 品质描述
     */
    public void setTrait (String _traitDesc)
    {
        trait = EGoodsTrait.getTrait(_traitDesc);
    }

    /**
     * 根据品质编号设置物品品质
     * 
     * @param _traitID 品质编号
     */
    public void setTrait (int _traitID)
    {
        trait = EGoodsTrait.getTrait(_traitID);
    }

    /**
     * 设置物品品质
     * 
     * @param _trait 品质
     */
    public void setTrait (EGoodsTrait _trait)
    {
        trait = _trait;
    }

    /**
     * 设置能否交易
     */
    public void setExchangeable ()
    {
        exchangeable = true;
    }

    /**
     * 能否交易
     * 
     * @return
     */
    public boolean exchangeable ()
    {
        return exchangeable;
    }

    /**
     * 获取物品品质
     * 
     * @return
     */
    public EGoodsTrait getTrait ()
    {
        return trait;
    }

    /**
     * 返回背包中一个格子可叠放的数量
     * 
     * @return
     */
    public short getMaxStackNums ()
    {
        return stackNums;
    }
    
    /**
     * 重设描述信息
     * @param _description
     */
    public void replaceDescription (String oldDesc, String _description)
    {
    	description = oldDesc + _description;
    }

    /**
     * 追加物品描述
     * 
     * @param _description 描述
     */
    public void appendDescription (String _description)
    {
        if (null != _description)
        {
            String additionalDescription = _description.trim();

            if (!additionalDescription.equals(""))
            {
                if (!description.equals(""))
                {
                    description = description
                            + CharacterDefine.DESC_NEW_LINE_CHAR
                            + additionalDescription;
                }
                else
                {
                    description = additionalDescription;
                }
            }
        }
    }

    /**
     * 是否是商城物品（与计费相关）
     * 
     * @return
     */
    public abstract boolean isIOGoods ();

    /**
     * 获取物品类型
     * 
     * @return
     */
    public abstract EGoodsType getGoodsType ();

    /**
     * 初始化描述
     */
    public abstract void initDescription ();
}
