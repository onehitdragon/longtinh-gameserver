package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.effect.Effect;
import hero.effect.dictionry.EffectDictionary;
import hero.effect.service.EffectServiceImpl;
import hero.fight.service.FightServiceImpl;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * 按次宠物卡
 * @author zhengl
 * 在以后优化的时候可以考虑用OO方式把这样类型的物品做出一个父类.
 *
 */
public class PetPerCard extends SpecialGoods {
	
	private int[][] PET_CARD_FUNCTION_LIST;
	
	/**
	 * 剩余次数
	 */
	public int surplusPoint;
	
	public int SkillID;
	
	private String oldDescription;
	
	private String nowDescription;
	
	private int location;
	
	private Effect effect;
	
	private boolean inUse;
	
	/**
	 * 从数据库初始化数据
	 * @param _surplusPoint
	 * @param _type
	 * @param _tonicID
	 */
	public void initData(int _surplusPoint, int _index) {
		surplusPoint = _surplusPoint;
		location = _index;
	}
	
	public void copyGoodsData(SpecialGoods _goods)
	{
		setName(_goods.getName());
		setIconID(_goods.getIconID());
		setTrait(_goods.getTrait());
		if (_goods.useable()) {
			setUseable();
		}
		oldDescription = _goods.getDescription();
		nowDescription = "\n剩余:" + surplusPoint + "次";
		replaceDescription(oldDescription, nowDescription);
	}
	
	private void descriptionUpdate(HeroPlayer _player)
	{
		nowDescription = "\n剩余:" + surplusPoint;
		replaceDescription(oldDescription, nowDescription);
		GoodsDAO.updatePetPer(_player.getUserID(), location, getID(), surplusPoint);
        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new ResponseSpecialGoodsBag(
                		_player.getInventory().getSpecialGoodsBag(), 
                		_player.getShortcutKeyList()));
	}

	public PetPerCard(int _id, short nums) {
		super(_id, nums);
		PET_CARD_FUNCTION_LIST = 
			GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().pet_per_function;
		for (int i = 0; i < PET_CARD_FUNCTION_LIST.length; i++) {
			if(_id == PET_CARD_FUNCTION_LIST[i][0]) {
				surplusPoint = PET_CARD_FUNCTION_LIST[i][1];
				SkillID = PET_CARD_FUNCTION_LIST[i][2];
			}
		}
		inUse = false;
		effect = EffectDictionary.getInstance().getEffectRef(SkillID);
	}

	@Override
	public boolean beUse(HeroPlayer _player, Object _target, int _location) {
		boolean remove = false;
		boolean hava = EffectServiceImpl.getInstance().haveAlikeMount(_player, effect);
//		if((!inUse) && hava) {
//			//卡并不在使用中,并且还存在其他相同速度的宠物卡效果则提示这个
//			OutMsgQ.getInstance().put(_player.getMsgQueueIndex(), 
//					new Warning(Tip.TIP_ITEM_PET_CARD_IN_USE.replaceAll("%fname", getName()), 
//							Warning.UI_STRING_TIP));
//			return remove;
//		}
		//激活加速光环的效果.
		if((!inUse) && surplusPoint > 0 && (!hava)) {
			//1,当前卡未使用
			//2,当前卡点数>0
			//3,没有相同的坐骑卡效果
			EffectServiceImpl.getInstance().appendSkillEffect(_player, _player, effect);
			_player.setMount(true);
			surplusPoint -= 1;
			inUse = true;
		} else if(surplusPoint > 0 || _player.getMount()) {
			//1,当前卡点数>0
			//2,或者已经在骑马状态
			EffectServiceImpl.getInstance().appendSkillEffect(_player, _player, effect);
			_player.setMount(false);
			inUse = false;
		}
		location = _location;
		descriptionUpdate(_player);
		if(surplusPoint <= 0 && (!inUse)) {
			remove = true;
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(
							Tip.TIP_ITEM_PET_CARD_END.replaceAll("%fname", getName()), 
							Warning.UI_STRING_TIP));
		}
		if(remove) {
            if (disappearImmediatelyAfterUse())
            {
                try {
					remove(_player, (short)location);
				} catch (BagException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		}
		return remove;
	}

	@Override
	public boolean disappearImmediatelyAfterUse() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ESpecialGoodsType getType() {
		// TODO Auto-generated method stub
		return ESpecialGoodsType.PET_PER;
	}

	@Override
	public void initDescription() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIOGoods() {
		// TODO Auto-generated method stub
		return true;
	}

}
