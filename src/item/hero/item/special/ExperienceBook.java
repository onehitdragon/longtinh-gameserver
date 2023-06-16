package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.charge.message.ExperienceBookTraceTime;
import hero.charge.service.ExperienceBookService;
import hero.item.SpecialGoods;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExperienceBook.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-6 下午02:00:45
 * @描述 ：经验书（在线时，间隔获得经验，持续4小时）
 */

public class ExperienceBook extends SpecialGoods
{

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public ExperienceBook(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        ExperienceBookService.getInstance().addExpBookTime(_player, KEEP_TIME);

        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new Warning(Tip.TIP_ITEM_OF_GET_TIIME, Warning.UI_STRING_TIP));
        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new ExperienceBookTraceTime(
                        _player.getChargeInfo().offLineTimeTotal, _player
                                .getChargeInfo().expBookTimeTotal, _player
                                .getChargeInfo().huntBookTimeTotal));

        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.EXPERIENCE_BOOK;
    }

    @Override
    public void initDescription ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isIOGoods ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * 持续时间
     */
    private static final long   KEEP_TIME        = 4 * 60 * 60 * 1000;


}
