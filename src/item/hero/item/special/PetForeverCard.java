package hero.item.special;

import hero.effect.Effect;
import hero.effect.dictionry.EffectDictionary;
import hero.effect.service.EffectServiceImpl;
import hero.item.SpecialGoods;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;

public class PetForeverCard extends SpecialGoods {
	
	private Effect effect;
	
	private static final int[][] PET_CARD_FUNCTION_LIST = 
		GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().pet_forever_function;
	
	public PetForeverCard(int _id, short nums) {
		super(_id, nums);
		

	}

	@Override
	public boolean beUse(HeroPlayer _player, Object _target, int _location) {
		int SkillID = 0;
		for (int i = 0; i < PET_CARD_FUNCTION_LIST.length; i++) {
			if(getID() == PET_CARD_FUNCTION_LIST[i][0]) {
				SkillID = PET_CARD_FUNCTION_LIST[i][1];
			}
		}
		effect = EffectDictionary.getInstance().getEffectRef(SkillID);
		EffectServiceImpl.getInstance().appendSkillEffect(_player, _player, effect);
		return false;
	}

	@Override
	public boolean disappearImmediatelyAfterUse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ESpecialGoodsType getType() {
		// TODO Auto-generated method stub
		return ESpecialGoodsType.PET_FOREVER;
	}

	@Override
	public void initDescription() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIOGoods() {
		// TODO Auto-generated method stub
		return false;
	}

}
