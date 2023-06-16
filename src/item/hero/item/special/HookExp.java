package hero.item.special;

import hero.item.SpecialGoods;
import hero.item.detail.EGoodsType;
import hero.player.HeroPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-8
 * Time: 下午6:33
 */
public class HookExp extends SpecialGoods {

    public HookExp(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.HOOK_EXP;
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    @Override
    public void initDescription() {

    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }
}
