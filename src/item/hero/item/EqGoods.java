package hero.item;

import hero.item.detail.BodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import hero.share.AccessorialOriginalAttribute;
import hero.share.EVocation;
import hero.share.MagicFastnessList;

public abstract class EqGoods extends Goods
{
	
	/**
     * 附带属性
     */
    public AccessorialOriginalAttribute atribute = new AccessorialOriginalAttribute();

	public EqGoods(short stackNums)
	{
		super(stackNums);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public EGoodsType getGoodsType ()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void initDescription ()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isIOGoods ()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public abstract short getDurabilityConvertRate();
	    
    public abstract int getMaxDurabilityPoint();
    
    public abstract BodyPartOfEquipment getWearBodyPart();
    
    /**
     * 获得物品属性数据组
     * @return
     */
    public abstract byte[] getFixPropertyBytes();
    
    public abstract boolean existSeal();
    
    public abstract MagicFastnessList getMagicFastnessList();
    
    public abstract short getImageID();
    
    public abstract short getAnimationID();
    
    public abstract boolean canBeUse(EVocation evocation);
    
    public abstract byte getBindType();
    
    public abstract int getEquipmentType();

}
