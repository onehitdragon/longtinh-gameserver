package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.lover.service.LoverServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-20
 * Time: 下午1:08
 * 巧克力：增加爱情值
 * 双方增加 2000 爱情值
 */
public class Chocolate extends SpecialGoods{

    public Chocolate(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {
        if(_player.spouse.trim().length()>0){
            HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(_player.spouse);
            if(other != null && other.isEnable()){

                _player.addLoverValue(LoverServiceImpl.ADD_LOVER_VALUE_FOR_CHOCOLATE);
                other.addLoverValue(LoverServiceImpl.ADD_LOVER_VALUE_FOR_CHOCOLATE);

                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
                return true;
            }else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("对方不在线，不能使用"));
            }
        }else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("你还没有配偶"));
        }
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.CHOCOLATE;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {

    }
}
