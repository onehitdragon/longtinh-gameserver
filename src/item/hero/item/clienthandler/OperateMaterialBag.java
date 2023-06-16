package hero.item.clienthandler;

import hero.item.SingleGoods;
import hero.item.bag.exception.BagException;
import hero.item.message.ResponseMaterialBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateMaterialBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-15 上午09:33:23
 * @描述 ：操作材料背包
 */

public class OperateMaterialBag extends AbsClientProcess
{
    /**
     * 查看列表
     */
    private static final byte LIST = 1;

    /**
     * 丢弃
     */
    private static final byte DICE = 2;

    /**
     * 整理
     */
    private static final byte SORT = 3;

    /*
     * (non-Javadoc)
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte operation = yis.readByte();

            switch (operation)
            {
                case LIST:
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseMaterialBag(player.getInventory()
                                    .getMaterialBag()));

                    break;
                }
                case DICE:
                {
                    byte gridIndex = yis.readByte();
                    int goodsID = yis.readInt();

                    try
                    {
                        GoodsServiceImpl.getInstance().diceSingleGoods(player,
                                player.getInventory().getMaterialBag(),
                                gridIndex, goodsID, CauseLog.DEL);
                    }
                    catch (BagException pe)
                    {
                        System.out.print(pe.getMessage());
                    }

                    break;
                }
                case SORT:
                {
                	if (true) 
                	{
                		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                				new Warning("该功能暂不开放"));
                		break;
					}
                    if (player.getInventory().getMaterialBag().clearUp())
                    {
                        GoodsDAO.clearUpSingleGoodsPackage(player.getUserID(),
                                player.getInventory().getMaterialBag(),
                                SingleGoods.TYPE_MATERIAL);

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMaterialBag(player.getInventory()
                                        .getMaterialBag()));
                    }

                    break;
                }
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
