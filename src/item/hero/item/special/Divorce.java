package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.npc.function.system.MarryNPC;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-11
 * Time: 16:22:42
 * 离婚证明道具
 */
public class Divorce extends SpecialGoods {



    /**
     * 只有在离婚时才能用 MarryNPC.java
     */
    public boolean canUse = false;

    /**
     * 离婚的另一方
     */
    public String otherName;
    /**
     * 构造
     *
     * @param _stackNums
     */
    public Divorce(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {
//        boolean succ = false;
        /*if(isCanUse()){
            HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(getOtherName());
            if(otherPlayer != null){
                succ = MarryNPC.divorce(_player,otherPlayer, (byte)1);
                setCanUse(!succ);
            }else{
                OutMsgQ.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("对方不在线"));
            }
        }*/
        if(_location == -1){
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
            return true;
        }else{
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("只有离婚时才能使用"));
            return false;
        }
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.DIVORCE;
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

     public boolean isCanUse() {
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
    }
}
