package hero.item.special;

import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.charge.message.ExperienceBookTraceTime;
import hero.charge.service.ExperienceBookService;
import hero.effect.Effect;
import hero.effect.Effect.EKeepTimeType;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.StaticEffect;
import hero.effect.message.AddEffectNotify;
import hero.item.SpecialGoods;
import hero.item.detail.EGoodsTrait;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 HuntExperienceBook.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-10 下午03:04:55
 * @描述 ：
 */

public class HuntExperienceBook extends SpecialGoods
{

    public HuntExperienceBook(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        int[][] list = 
        	GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_time;
        for (int i = 0; i < list.length; i++) 
        {
			if (list[i][0] == _id) 
			{
				KEEP_TIME = list[i][1] * 60 * 1000;
				break;
			}
		}
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
    	long time = KEEP_TIME;
    	//add by zhengl; date: 2011-05-19; note: 超过short最大值的时间就不再添加了
    	int now = (int)_player.getChargeInfo().huntBookTimeTotal;
    	if (now + time >= 32766000) {
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(
							Tip.TIP_ITEM_OF_EXPERIENCE_FULL
									.replaceAll("%t", String.valueOf(now))
									.replaceAll("%n", getName()), Warning.UI_TOOLTIP_TIP));
			return false;
		}
        ExperienceBookService.getInstance().addHuntExpBookTime(_player, time);

        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new Warning(Tip.TIP_ITEM_OF_GET_TIME.replaceAll("%fx", getName()), 
                		Warning.UI_STRING_TIP));
//        OutMsgQ.getInstance().put(
//                _player.getMsgQueueIndex(),
//                new ExperienceBookTraceTime(
//                        _player.getChargeInfo().offLineTimeTotal, _player
//                                .getChargeInfo().expBookTimeTotal, _player
//                                .getChargeInfo().huntBookTimeTotal));
        /*
         * 
        output.writeInt(effect.ID);
        output.writeInt(effect.releaser.getID());`
        output.writeByte(effect.trait.value());`
        output.writeByte(effect.keepTimeType.value());`
        output.writeShort(effect.traceTime);`
        output.writeShort(effect.iconID);`
        //add by zhengl; date: 2011-03-22; note: 宠物卡坐骑的外观
        output.writeByte(effect.viewType); //添加BUFF外观类型`
        output.writeShort(effect.imageID); //添加BUFF外观图片`
        output.writeShort(effect.animationID); //添加BUFF外观动画`
         */
        StaticEffect sef = new StaticEffect(1, "双倍经验");
        sef.desc = "双倍经验";
        sef.releaser = (ME2GameObject)_player;
        sef.trait = EffectTrait.BUFF;
        sef.keepTimeType = EKeepTimeType.LIMITED;
        sef.traceTime = (short)(_player.getChargeInfo().huntBookTimeTotal/1000);
        sef.iconID = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_icon;
        sef.viewType = 0;
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
        		new AddEffectNotify(_player, (Effect)sef));
        LogServiceImpl.getInstance().goodsUsedLog(
        		_player.getLoginInfo().accountID, _player.getLoginInfo().username,
                _player.getUserID(), _player.getName(), getID(), 
                getName(), getTrait().getDesc(), getType().getDescription());
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
        return ESpecialGoodsType.HUNT_EXP_BOOK;
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
    private long   KEEP_TIME       = 61*1000;



    /**
     * 狩猎经验书加成系数
     */
    public static final float   EXP_MODULUS     = 1f;
}
