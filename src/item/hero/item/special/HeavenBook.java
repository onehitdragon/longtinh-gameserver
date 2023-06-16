package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.message.AskChooseHeavenBookPosition;
import hero.player.message.ResponseInlayHeavenBook;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.skill.message.LearnedSkillListNotify;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-25
 * Time: 下午2:32
 * 天书
 */
public class HeavenBook extends SpecialGoods {

    private short skillPoint;

    private byte position = -1;
    /**
     * 构造
     *
     * @param _stackNums
     */
    public HeavenBook(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {
        boolean succ = false;
        if(position == -1){ //跳转到技能界面选择天书槽位置
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
            		new AskChooseHeavenBookPosition(_player,getID()));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
            		new Warning("请选择天书插槽", Warning.UI_STRING_TIP));
        }else{
            succ = PlayerServiceImpl.getInstance().inlayHeavenBook(_player,position,getID());
            if(!succ){
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                		new Warning("镶嵌天书不成功", Warning.UI_STRING_TIP));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new ResponseInlayHeavenBook(getID(),position,succ,_player.heavenBookSame));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new LearnedSkillListNotify(_player));
        }
        if(succ){
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        }
        return succ;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.HEAVEN_BOOK;
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


    public void setSkillPoint(short point){
        this.skillPoint = point;
    }

    public short getSkillPoint(){
        return skillPoint;
    }

    public void setPosition(byte position){
        this.position = position;
    }

    /**
     * 获取组合奖励
     *(三个天书插槽都是同一种天书)
     * 2的(品质ID+1)次幂
     * @return
     */
    public int getComBonus(){
        return (int)Math.pow(2,getTrait().value());
    }
}
