package hero.item.special;

import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.log.service.LogServiceImpl;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;

public class PetDicard extends SpecialGoods
{

	public PetDicard(int id, short stackNums)
	{
		super(id, stackNums);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beUse (HeroPlayer _player, Object target, int location)
	{
		Pet pet = (Pet)target;
		if(pet == null) return false;
		PetServiceImpl.getInstance().dicardPoint(_player, pet);
		LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
		return true;
	}

	@Override
	public boolean disappearImmediatelyAfterUse ()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ESpecialGoodsType getType ()
	{
		// TODO Auto-generated method stub
		return ESpecialGoodsType.PET_DICARD;
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
