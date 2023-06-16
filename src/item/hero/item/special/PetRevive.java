package hero.item.special;

import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.log.service.LogServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;

/**
 * 宠物复活卷轴
 * @author jiaodongjie
 *
 */
public class PetRevive extends SpecialGoods 
{

	public PetRevive(int id, short stackNums)
	{
		super(id, stackNums);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beUse (HeroPlayer _player, Object target, int location)
	{
		int petId = (Integer)target;
		boolean r = PetServiceImpl.getInstance().petRevive(_player, petId);
		if(r){
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        }
		return r;
		
	}

	@Override
	public boolean disappearImmediatelyAfterUse ()
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
    public byte getSingleGoodsType ()
    {
        // TODO Auto-generated method stub
        return SingleGoods.TYPE_PET_GOODS;
    }

	@Override
	public ESpecialGoodsType getType ()
	{
		// TODO Auto-generated method stub
		return ESpecialGoodsType.PET_REVIVE;
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
