package hero.item.special;

import hero.item.SpecialGoods;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;

public class GuildBuild extends SpecialGoods {
	public static final int GUILD_BUILD_ID = GoodsServiceImpl.getInstance()
				.getConfig().getSpecialConfig().number_guild_build;
	
	public GuildBuild(int _id, short nums) {
		super(_id, nums);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beUse(HeroPlayer _player, Object _target, int _location) {
		// TODO Auto-generated method stub
        boolean res = true;
        if(res){
        //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        }
		return res;
	}

	@Override
	public boolean disappearImmediatelyAfterUse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ESpecialGoodsType getType() {
		// TODO Auto-generated method stub
		return ESpecialGoodsType.GUILD_BUILD;
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
