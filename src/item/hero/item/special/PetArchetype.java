package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.pet.Pet;
import hero.pet.PetPK;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PetArchetype.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-12 上午10:10:45
 * @描述 ：宠物原型，使用后可以获得宠物，可以有相同的宠物
 */


public class PetArchetype extends SpecialGoods
{
    

	/**
     * 宠物编号
     */
    private int petAID;

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public PetArchetype(int id, short stackNums)
	{
		super(id, stackNums);
	}

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        Pet pet = PetServiceImpl.getInstance().addPet(_player.getUserID(),
        		petAID);//可以有相同的宠物
       
//        if (null != pet)
//        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_GET_PET + pet.name, Warning.UI_STRING_TIP));

        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
            return true;
//        }
//        else
//        { 
//            OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),
//                    new Warning(TIP_OF_EXIST));
//
//            return false;
//        }
    }

    /**
     * 设置宠物编号
     * 
     * @param _petID
     */
    public void setPetAID (int petAID)
    {
    	this.petAID = petAID;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        return true;
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        return ESpecialGoodsType.PET_ARCHETYPE;
    }
    
    @Override
    public byte getSingleGoodsType ()
    {
        // TODO Auto-generated method stub
        return SingleGoods.TYPE_PET_GOODS;
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
        return true;
    }


}
