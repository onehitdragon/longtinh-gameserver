package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.effect.service.EffectServiceImpl;
import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.SwitchMapFailNotify;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-21
 * Time: 上午10:28
 * 夫妻传送符
 * 把自己传送到配偶身边
 */
public class SpouseTransport extends SpecialGoods{

    public SpouseTransport(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {
        boolean res = false;
        if(!_player.isSelling()){
            if(_player.marryed && _player.spouse.trim().length()>0){
                HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(_player.spouse);
                if(!other.isSelling()){
                    if(other.isEnable()){
                        if(other.where().getMapType() == EMapType.GENERIC ){
                            Map entranceMap = MapServiceImpl.getInstance().getNormalMapByID(other.where().getID());

                            _player.setCellX(other.getCellX());
                            _player.setCellY(other.getCellY());

                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new ResponseMapBottomData(_player, entranceMap,
                                            _player.where()));

                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new ResponseMapGameObjectList(_player
                                            .getLoginInfo().clientType, entranceMap));

                            _player.gotoMap(entranceMap);
                            //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                            EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);
                            res = true;
                        }else{
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("副本不能传送",Warning.UI_TOOLTIP_TIP));
                        }
                    }else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("对方不在线，不能传送",Warning.UI_TOOLTIP_TIP));
                    }
                }else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("对方正在摆摊，不能传送！"));
//                    OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),new SwitchMapFailNotify("对方正在摆摊，不能传送！"));
                }
            }else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("你还没有结婚，不能传送",Warning.UI_TOOLTIP_TIP));
            }


            if(res){
                //如果使用成功，则记录使用日志
                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                        _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getGoodsType().getDescription());
            }
        }else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning(Tip.TIP_EXCHANGEING_NOT_USE_FUN));
//            OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),new SwitchMapFailNotify(Tip.TIP_EXCHANGEING_NOT_USE_FUN));
        }
        return res;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.SPOUSE_TRANSPORT;
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
