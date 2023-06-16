package hero.task.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.item.special.TaskTransportItem;
import hero.map.message.SwitchMapFailNotify;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.task.message.ResponseTaskView;
import hero.task.service.TaskServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTransmint.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-7 下午03:13:21
 * @描述 ：
 */

public class TaskTransmint extends AbsClientProcess
{
    /**
     * 提交任务传送
     */
    public static final byte TYPE_OF_COMPLETE = 1;

    /**
     * 传送到任务目标目的地
     */
    public static final byte TYPE_OF_TARGET   = 2;

    /**
     * 可接任务的接受NPC目的地
     */
    public static final byte TYPE_OF_RECEIVE  = 3;
    

    


    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {

            if(player.isSelling()){
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_EXCHANGEING_NOT_USE_FUN));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new SwitchMapFailNotify(Tip.TIP_EXCHANGEING_NOT_USE_FUN));
                return;
            }

        	//add by zhengl; date: 2011-02-11; note: 任务传送需要消耗传送道具
            int itemNum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(
            		TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
            if(itemNum <= 0)
            {
            	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            			new Warning(Tip.TIP_TASK_NOT_HAVE, 
            					Warning.UI_TOOLTIP_AND_EVENT_TIP, 
            					Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
//            	//SwitchMapFailNotify
            	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            			new SwitchMapFailNotify(Tip.TIP_TASK_NOT_HAVE));
            	return;
            }
            //end

            byte operateMark = yis.readByte();
            int taskID = yis.readInt();
            boolean transmitSuccessfully = false;

            switch (operateMark)
            {
                case TYPE_OF_COMPLETE:
                {
                    transmitSuccessfully = TaskServiceImpl.getInstance()
                            .transmitToTaskNpc(player, taskID, false);

                    break;
                }
                case TYPE_OF_TARGET:
                {
                    int targetID = yis.readInt();

                    transmitSuccessfully = TaskServiceImpl.getInstance()
                            .transmitToTaskTarget(player, taskID, targetID);
                    break;
                }
                case TYPE_OF_RECEIVE:
                {
                    transmitSuccessfully = TaskServiceImpl.getInstance()
                            .transmitToTaskNpc(player, taskID, true);

                    break;
                }
            }

            if (transmitSuccessfully)
            {
            	Goods goods = GoodsContents.getGoods(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
            	((TaskTransportItem)goods).remove(player, (short)-1);
                 ((TaskTransportItem)goods).beUse(player,null,-1);//这里要调用一下，要记录使用日志
            	//暂时这样写,先不用beUse方法来做这样的提示.
            	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            			new Warning(Tip.TIP_TASK_USE + goods.getName(), Warning.UI_STRING_TIP));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
