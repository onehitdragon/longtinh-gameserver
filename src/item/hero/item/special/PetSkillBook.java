package hero.item.special;

import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.log.service.LogServiceImpl;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;

/**
 * 宠物技能书类
 * @author jiaodongjie
 *
 */
public class PetSkillBook extends SpecialGoods 
{

	private int skillID;
	
	public PetSkillBook(int id, short stackNums)
	{
		super(id, stackNums);
	}

	@Override
	public boolean beUse (HeroPlayer _player, Object target, int location)
	{
		Pet pet = (Pet)target;
		boolean r = PetServiceImpl.getInstance().petLearnSkillFromSkillBook(_player, pet, skillID);
		if(r){
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        }
		return r;
	}

	@Override
	public boolean disappearImmediatelyAfterUse ()
	{
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
		return ESpecialGoodsType.PET_SKILL_BOOK;
	}

	@Override
	public void initDescription ()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIOGoods ()
	{
		return true;
	}

	public int getSkillID ()
	{
		return skillID;
	}

	public void setSkillID (int skillID)
	{
		this.skillID = skillID;
	}

}
