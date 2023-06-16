package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.pet.FeedType;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.share.message.Warning;

public class PetFeed extends SpecialGoods 
{
	public PetFeed(int id, short stackNums)
	{
		super(id, stackNums);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beUse (HeroPlayer _player, Object target, int location)
	{
		// 使用饲料，心情变化，攻击力，速度变化情况
		Pet pet = (Pet)target;
		if(pet.pk.getStage() > Pet.PET_STAGE_EGG){
			boolean r = PetServiceImpl.getInstance().feedPet(_player.getUserID(), pet, getID());

            if(r){
                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                        _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
            }
			
			return r;
		}else{
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
					new Warning("宠物未到幼年，不能喂养！", Warning.UI_STRING_TIP));
			return false;
		}
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
		return ESpecialGoodsType.PET_FEED;
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


    /**
	 * 普通饲料
	 */
	public static final short FEED_TYPE_NORMAL = 1;
	/**
	 * 草食成长饲料
	 */
	public static final short FEED_TYPE_HERBIVORE_GROW = 2;
	/**
	 * 肉食成长饲料
	 */
	public static final short FEED_TYPE_CARNIVORE_GROW = 3;
	/**
	 * 大地精华
	 */
	public static final short FEED_TYPE_DADIJH = 4;
	/**
	 * 龙涎草汁
	 */
	public static final short FEED_TYPE_LYCZ = 5;

    /**
     * 饲料类型
     */
    private FeedType feedType;
    public FeedType getFeedType ()
	{
		return feedType;
	}
	public void setFeedType (FeedType feedType)
	{
		this.feedType = feedType;
	}
}
