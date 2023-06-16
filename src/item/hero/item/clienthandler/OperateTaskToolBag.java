package hero.item.clienthandler;

import hero.item.SingleGoods;
import hero.item.TaskTool;
import hero.item.bag.EBagType;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.TaskGoodsDict;
import hero.item.message.ResponseTaskToolBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.task.service.TaskServiceImpl;
import hero.ui.message.ResponseSinglePackageChange;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateTaskToolBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-15 上午09:33:23
 * @描述 ：操作任务道具背包
 */

public class OperateTaskToolBag extends AbsClientProcess
{
    /**
     * 查看列表
     */
    private static final byte LIST             = 1;

    /**
     * 使用
     */
    private static final byte USE              = 2;

    /**
     * 设置快捷键
     */
    private static final byte SET_SHORTCUT_KEY = 3;

    /**
     * 丢弃
     */
    private static final byte DICE             = 4;

    /**
     * 整理
     */
    private static final byte SORT             = 5;

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
            byte operation = yis.readByte();

            switch (operation)
            {
                case LIST:
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseTaskToolBag(player.getInventory()
                                    .getTaskToolBag(), player
                                    .getShortcutKeyList()));

                    break;
                }
                case USE:
                {
                    byte gridIndex = yis.readByte();
                    int goodsID = yis.readInt();

                    if (player.getInventory().getTaskToolBag().getAllItem()[gridIndex][0] == goodsID)
                    {

                        TaskTool taskTool = TaskGoodsDict.getInstance()
                                .getTaskTool(goodsID);

                        if (taskTool != null)
                        {
                            taskTool.beUse(player, null);
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning("该物品不存在", Warning.UI_STRING_TIP));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning("背包中无此物品", Warning.UI_STRING_TIP));
                    }

                    break;
                }
                case SET_SHORTCUT_KEY:
                {
                    byte shortcutKey = yis.readByte();
                    int goodsID = yis.readInt();

                    PlayerServiceImpl.getInstance().setShortcutKey(player,
                            shortcutKey,
                            PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS,
                            goodsID);

                    break;
                }
                case DICE:
                {
                    byte gridIndex = yis.readByte();
                    int goodsID = yis.readInt();

                    try
                    {
                        if (GoodsServiceImpl.getInstance().diceSingleGoods(
                                player,
                                player.getInventory().getTaskToolBag(),
                                gridIndex, goodsID,CauseLog.DEL))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new ResponseSinglePackageChange(
                                            EBagType.TASK_TOOL_BAG
                                                    .getTypeValue(),
                                            new short[]{gridIndex, 0 }));

                            TaskServiceImpl.getInstance().reduceTaskGoods(
                                    player, goodsID);
                        }
                    }
                    catch (BagException pe)
                    {
                        System.out.print(pe.getMessage());
                    }

                    break;
                }
                case SORT:
                {
                    if (player.getInventory().getTaskToolBag().clearUp())
                    {
                        GoodsDAO.clearUpSingleGoodsPackage(player.getUserID(),
                                player.getInventory().getTaskToolBag(),
                                SingleGoods.TYPE_TASK_TOOL);

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseTaskToolBag(player.getInventory()
                                        .getTaskToolBag(), player
                                        .getShortcutKeyList()));
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
