package hero.item;

import hero.item.detail.EBodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import hero.share.AccessorialOriginalAttribute;
import hero.share.EMagic;
import hero.share.EVocation;
import hero.share.MagicFastnessList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Equipment.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-16 上午01:54:03
 * @描述 ：装备，包括护甲和武器
 */

public abstract class Equipment extends EqGoods
{
    /**
     * 装备的部位
     */
    protected EBodyPartOfEquipment      bodyPart;

    /**
     * 耐久折算率
     */
    protected byte                      durabilityConvertRate;

    /**
     * 最大耐久度
     */
    protected int                       maxDurabilityPoint;

    /**
     * 是否可被修复
     */
    protected boolean                   repairable;

    /**
     * 装备显示图片
     */
    private short                       imageID;
    
    /**
     * 动画文件ID
     */
    private short                       animationID;

    /**
     * 僵直时间(秒，间隔攻击或使用时间)
     */
    protected float                     immobilityTime;

    /**
     * 是否存在封印
     */
    protected boolean                   existSeal;

    /**
     * 是否拾取绑定
     */
    protected byte                      bindType;

    /**
     * 魔法抗性列表
     */
    private MagicFastnessList           magicFastnessList;

    /**
     * 固定属性描述（不包括名称和编号）
     */
    protected byte[]                    fixProperty;

    /**
     * 附带属性
     */
//    public AccessorialOriginalAttribute atribute = new AccessorialOriginalAttribute();

    /**
     * 获取装备图片编号
     * 
     * @return
     */
    public short getImageID ()
    {
        return imageID;
    }

    /**
     * 设置装备显示图片编号
     * 
     * @param _iconID
     */
    public void setImageID (short _imageID)
    {
        imageID = _imageID;
    }
    /**
     * 设置动画ID
     * @param _animationID
     */
    public void setAnimationID (Short _animationID) {
    	animationID = _animationID;
    }
    /**
     * 获取动画ID
     * @return
     */
    public short getAnimationID () {
    	return animationID;
    }

    /**
     * 构造
     */
    public Equipment()
    {
        super((short) 1);
        bindType = BIND_TYPE_OF_NOT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uen.me2.server.model.goods.Goods#getGoodsType() 返回物品类型
     */
    public EGoodsType getGoodsType ()
    {
        return EGoodsType.EQUIPMENT;
    }

    /**
     * 获取耐久折算率
     * 
     * @return
     */
    public short getDurabilityConvertRate ()
    {
        return durabilityConvertRate;
    }

    /**
     * 设置最大耐久度
     * 
     * @param _durabilityPoint 最大耐久度
     */
    public void setMaxDurabilityPoint (int _durabilityPoint)
    {
        maxDurabilityPoint = _durabilityPoint;
    }

    /**
     * 获取最大耐久度
     * 
     * @return
     */
    public int getMaxDurabilityPoint ()
    {
        return maxDurabilityPoint;
    }

    /**
     * 设置是否可修理标记
     * 
     * @param _yesOrNo
     */
    public void setRepairable (boolean _yesOrNo)
    {
        repairable = _yesOrNo;
    }

    /**
     * 是否可修理
     * 
     * @return
     */
    public boolean repairable ()
    {
        return repairable;
    }

    /**
     * 能否交易
     * 
     * @return
     */
    /*
     * (non-Javadoc)
     * 
     * @see hero.item.Goods#exchangeable()
     */
    public boolean exchangeable ()
    {
        return bindType == BIND_TYPE_OF_PICK ? false : true;
    }

    /**
     * 设置装备的使用间隔时间
     * 
     * @param _immobilityTime
     */
    public void setImmobilityTime (float _immobilityTime)
    {
        immobilityTime = _immobilityTime;
    }

    /**
     * 获取装备的间隔使用时间
     * 
     * @return 装备使用间隔时间
     */
    public float getImmobilityTime ()
    {
        return immobilityTime;
    }

    /**
     * 获取装备部位
     * 
     * @return
     */
    public EBodyPartOfEquipment getWearBodyPart ()
    {
        return bodyPart;
    }

    /**
     * 设置装备部位
     * 
     * @param _bodyPart
     */
    public void setWearBodyPart (EBodyPartOfEquipment _bodyPart)
    {
        bodyPart = _bodyPart;
    }

    /**
     * 设置魔法抗性
     * 
     * @param _magic 类型魔法
     * @param _value 魔法抗性值
     */
    public void setMagicFastness (EMagic _magic, int _value)
    {
        if (0 >= _value)
        {
            return;
        }

        if (null == magicFastnessList)
        {
            magicFastnessList = new MagicFastnessList();
        }

        magicFastnessList.add(_magic, _value);
    }

    /**
     * 获取魔法抗性列表
     * 
     * @return 魔法抗性列表
     */
    public MagicFastnessList getMagicFastnessList ()
    {
        return magicFastnessList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uen.me2.server.model.goods.Goods#isIOGoods() 是否是商城出售物品
     */
    public boolean isIOGoods ()
    {
        return false;
    }

    /**
     * 能否被某职业使用
     * 
     * @param _vocation 职业
     * @return
     */
    public abstract boolean canBeUse (EVocation _vocation);

    /**
     * 获取装备类型
     * 
     * @return
     */
    public abstract int getEquipmentType ();

    /**
     * 设置封印
     */
    public void setSeal ()
    {
        existSeal = true;
    }

    /**
     * 是否存在封印
     * 
     * @return
     */
    public boolean existSeal ()
    {
        return existSeal;
    }

    /**
     * 设置绑定类型
     */
    public void setBindType (byte _type)
    {
        bindType = _type;
    }

    /**
     * 获取绑定类型
     * 
     * @return
     */
    public byte getBindType ()
    {
        return bindType;
    }

    /**
     * 设置固定属性字节
     * 
     * @param _fixProperty
     */
    public void setFixPropertyBytes (byte[] _fixProperty)
    {
        fixProperty = _fixProperty;
    }

    /**
     * 获取固定属性字节
     * 
     * @return
     */
    public byte[] getFixPropertyBytes ()
    {
        return fixProperty;
    }

    /**
     * 武器耐久度减少计算参数，当攻击成功次数累计达到此数值时，耐久度下降1点
     */
    public static final int  DURA_REDUCE_PARA_OF_WEAPON = 60;

    /**
     * 防具耐久度减少计算参数，当被攻击次数累计达到此数值时，耐久度下降1点
     */
    public static final int  DURA_REDUCE_PARA_OF_ARMOR  = 40;

    /**
     * 耐久度为0时，装备名称在客户端的显示颜色
     */
    public static final int  BAD_NAME_VIEW_COLOR        = 0X5C5954;

    /**
     * 武器
     */
    public static final int  TYPE_WEAPON                = 1;

    /**
     * 防具
     */
    public static final int  TYPE_ARMOR                 = 2;

    /**
     * 不绑定
     */
    public static final byte BIND_TYPE_OF_NOT           = 1;

    /**
     * 装备绑定
     */
    public static final byte BIND_TYPE_OF_WEAR          = 2;

    /**
     * 拾取绑定
     */
    public static final byte BIND_TYPE_OF_PICK          = 3;
}
