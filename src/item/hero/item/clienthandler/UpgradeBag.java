package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UpgradeBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-3 下午05:18:12
 * @描述 ：升级背包（任务道具包除外）
 */

public class UpgradeBag extends AbsClientProcess
{
	private static final byte OPERATION_SEE = 0;
	
	private static final byte OPERATION_UPGRADE = 1;
    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
        byte iperation = yis.readByte();
        byte bagType = yis.readByte();
        if(OPERATION_SEE == iperation) {
        	GoodsServiceImpl.getInstance().searchUpgradeBag(player, bagType);
        } else if (OPERATION_UPGRADE == iperation) {
        	GoodsServiceImpl.getInstance().upgradeBag(player, bagType);
		}

    }
}
