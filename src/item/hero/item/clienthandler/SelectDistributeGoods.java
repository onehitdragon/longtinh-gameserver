package hero.item.clienthandler;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;

import hero.item.legacy.MonsterLegacyManager;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SelectDistributeGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-15 下午08:07:30
 * @描述 ：选择分配物品框
 */

public class SelectDistributeGoods extends AbsClientProcess
{

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            short didtributeGoodsID = yis.readShort();
            boolean needOrCancel = yis.readByte() == 1 ? true : false;

            MonsterLegacyManager.getInstance().selectDistributeGoods(
                    didtributeGoodsID, player, needOrCancel);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
