package hero.task.message;

import hero.task.Task;
import hero.task.TaskInstance;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseTaskItems.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-21 下午02:42:11
 * @描述 ：玩家已接任务条目
 */

public class ResponseTaskItems extends AbsResponseMessage
{
    /**
     * 已接任务列表
     */
    private ArrayList<TaskInstance> taskList;

    /**
     * @param _existsTaskList
     */
    public ResponseTaskItems(ArrayList<TaskInstance> _existsTaskList)
    {
        taskList = _existsTaskList;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeByte(taskList.size());

        for (TaskInstance existsTask : taskList)
        {
            Task task = existsTask.getArchetype();

            yos.writeInt(task.getID());
            yos.writeShort(task.getLevel());
            yos.writeUTF(task.getName() + " ( "+ task.getLevel()+" )");
            yos.writeByte(existsTask.isCompleted());
            yos
                    .writeByte((null != task.getAward().getOptionalGoodsList() && task
                            .getAward().getOptionalGoodsList().size() > 0)
                            || (null != task.getAward().getBoundGoodsList() && task
                                    .getAward().getBoundGoodsList().size() > 0)
                            || task.getAward().skillID > 0);
        }
    }
}
