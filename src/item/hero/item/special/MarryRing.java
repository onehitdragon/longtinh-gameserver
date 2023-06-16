package hero.item.special;

import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.npc.function.system.MarryNPC;
import hero.player.HeroPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-11
 * Time: 16:16:53
 * 结婚戒指   2011-01-19
 */
public class MarryRing extends SpecialGoods {

    /*
     */
//    public boolean canUse = false;
    /**
     *
     */
//    public String otherName;
    /**
     * 构造
     *
     * @param _stackNums
     */
    public MarryRing(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {
//        boolean succ = false;
        /*if(isCanUse()){
            succ = MarryNPC.propose(_player,otherName);
            setCanUse(!succ);
        }*/
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.MARRY_RING;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {

    }

    /*public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }*/
}
