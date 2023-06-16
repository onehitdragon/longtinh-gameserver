package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.item.special.SpecialGoodsService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AnswerSoulGoodsOperation.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-11 上午10:12:09
 * @描述 ：
 */

public class AnswerSoulGoodsOperation extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        byte operateType = yis.readByte();
        byte locationOfBag = yis.readByte();

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        SpecialGoodsService.operateSoulGoods(player, operateType, locationOfBag);
    }
}
