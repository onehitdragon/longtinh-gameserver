package hero.task.clienthandler;

import java.util.ArrayList;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.task.Task;
import hero.task.message.ReceiveableTaskList;
import hero.task.service.TaskServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SearchReceivableTaskList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-7 下午03:42:28
 * @描述 ：搜索可接任务列表
 */

public class SearchReceivableTaskList extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            ArrayList<Task> taskList = TaskServiceImpl.getInstance()
                    .getReceiveableTaskList(player);

            if (null != taskList)
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new ReceiveableTaskList(taskList));
            }
            else
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_TASK_OF_NONE, Warning.UI_STRING_TIP));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
