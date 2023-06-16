package hero.task.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.task.message.ResponseTaskView;
import hero.task.service.TaskServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateTaskList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-21 下午02:40:00
 * @描述 ：操作任务列表：查看、放弃
 */

public class OperateTaskList extends AbsClientProcess
{
    /**
     * 查看任务描述
     */
    public static final byte VIEW_DESC  = 1;

    /**
     * 查看奖励物品、奖励技能
     */
    public static final byte VIEW_AWARD = 2;

    /**
     * 放弃任务
     */
    public static final byte CANCEL     = 3;

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte operateMark = yis.readByte();
            int taskID = yis.readInt();

            switch (operateMark)
            {
                case VIEW_DESC:
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseTaskView(TaskServiceImpl.getInstance()
                                    .getPlayerTask(player.getUserID(), taskID),
                                    VIEW_DESC,player.getLevel()));

                    break;
                }
                case VIEW_AWARD:
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseTaskView(TaskServiceImpl.getInstance()
                                    .getPlayerTask(player.getUserID(), taskID),
                                    VIEW_AWARD,player.getLevel()));

                    break;
                }
                case CANCEL:
                {
                    TaskServiceImpl.getInstance().cancelTask(player, taskID);

                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
