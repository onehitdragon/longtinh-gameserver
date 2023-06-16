package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.item.SpecialGoods;
import hero.item.message.PopHornInputUINotify;
import hero.player.HeroPlayer;
/**
 * 集结号角类
 * @author jiaodongjie
 *
 */
public class MassHorn extends SpecialGoods
{

	public MassHorn(int id, short stackNums)
	{
		super(id, stackNums);
	}

	@Override
	public boolean beUse (HeroPlayer _player, Object target, int _location)
	{
		ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new PopHornInputUINotify(_location));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
	}

	@Override
	public boolean disappearImmediatelyAfterUse ()
	{
		return false;
	}

	@Override
	public ESpecialGoodsType getType ()
	{
		return ESpecialGoodsType.MASS_HORN;
	}

	@Override
	public void initDescription ()
	{
		
	}

	@Override
	public boolean isIOGoods ()
	{
		return true;
	}

}
