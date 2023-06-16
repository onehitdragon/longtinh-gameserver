package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.item.SpecialGoods;
import hero.item.detail.EGoodsType;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-17
 * Time: 下午5:09
 * 循环任务扩展道具
 * 每天晚上24点系统将刷新数据，道具功能作废。
 */
public class RepeateTaskExpan extends SpecialGoods{

    /**
     * 可使用次数
     */
    private int usedTimes;

    public RepeateTaskExpan(int _id, short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {

    }

    @Override
    public boolean beUse(HeroPlayer _player, Object _target, int _location) {

        int res = PlayerDAO.insertRepeatTaskGoods(_player.getUserID(),getID(),getUsedTimes());

        if(res == 1){
            _player.setCanReceiveRepeateTaskTimes(_player.getCanReceiveRepeateTaskTimes() + getUsedTimes());

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new NotifyPlayerReciveRepeateTaskTimes(_player));//玩家接收的循环任务次数

            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                        _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());

            return true;
        }
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.REPEATE_TASK_EXPAN;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    public int getUsedTimes() {
        return usedTimes;
    }

    public void setUsedTimes(int usedTimes) {
        this.usedTimes = usedTimes;
    }
}
