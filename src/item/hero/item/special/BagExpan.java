package hero.item.special;

import hero.item.SpecialGoods;
import hero.player.HeroPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-22
 * Time: 下午5:15
 * 背包扩展道具，只是在玩家扩展背包时当做扣点使用，并不显示在玩家的背包里，也不会在商城或NPC处出现
 */
public class BagExpan extends SpecialGoods{

    public BagExpan(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.BAG_EXPAN;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    @Override
    public void initDescription() {

    }
}
