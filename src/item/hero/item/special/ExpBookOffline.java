package hero.item.special;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;

import hero.log.service.LogServiceImpl;
import hero.charge.message.ExperienceBookTraceTime;
import hero.expressions.service.CEService;
import hero.item.SpecialGoods;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExpBookOffline.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-30 下午03:03:45
 * @描述 ：
 */

public class ExpBookOffline extends SpecialGoods
{
    /**
     * 消耗的离线时间
     */
    private byte  consumeTime;

    /**
     * 当前等级增加的经验百分比
     */
    private float expPercent;

    /**
     * 单、双倍系数
     */
    private byte  parameter;

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public ExpBookOffline(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub

        for (float[] data : TIME_SPAN_LIST)
        {
            if (_id == data[0])
            {
                consumeTime = (byte) data[1];
                expPercent = data[2];
                parameter = (byte) data[3];
            }
        }
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.EXP_BOOK_OFFLINE;
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

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    private static final float[][] TIME_SPAN_LIST = {
            {52041, 4, 1F, 6 }, {52042, 8, 1.03F, 6 }, {52043, 24, 1.1F, 6 },
            {52044, 4, 1F, 3 }, {52045, 8, 1.03F, 3 }, {52046, 24, 1.1F, 3 } };

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        if (MIN_HOUR > _player.getChargeInfo().offLineTimeTotal)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_NOT_ENOGH_TIME, Warning.UI_STRING_TIP));

            return false;
        }

        if (_player.getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_MAX_LEVEL, Warning.UI_STRING_TIP));

            return false;
        }

        long time = (consumeTime * MILL_OF_HOUR) <= _player.getChargeInfo().offLineTimeTotal ? consumeTime
                * MILL_OF_HOUR
                : _player.getChargeInfo().offLineTimeTotal;

        int experience = (int) ((time * 1f / MILL_OF_HOUR) * (_player
                .getUpgradeNeedExp()
                * expPercent / ((parameter
                * (_player.getUpgradeNeedExp()
                        * (2 - 0.01F * (_player.getLevel() - 1)) / CEService
                        .getExperienceFromMonster(1, _player.getLevel(),
                                _player.getLevel(), (40 + 5 * (_player
                                        .getLevel() - 1)))) * 55) / 3600)));
        //del by zhengl; date: 2011-05-03; note: 删除不安全的经验添加点.
        LogWriter.error("严重异常的调用.玩家不应该触发此废弃类." + _player.getName(), 
        		null);
//        PlayerServiceImpl.getInstance().addExperience(_player, experience, 1,
//                PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING);

        _player.getChargeInfo().offLineTimeTotal -= time;

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





    /**
     * 每小时折算的毫秒
     */
    private static final long   MILL_OF_HOUR          = 60 * 60 * 1000;

    /**
     * 最少可使用的离线时间
     */
    private static final long   MIN_HOUR              = 2 * 60 * 60 * 1000;

}
