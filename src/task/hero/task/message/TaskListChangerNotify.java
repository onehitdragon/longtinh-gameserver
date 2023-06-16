package hero.task.message;

import hero.task.TaskInstance;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskListChangerNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-1 下午04:41:40
 * @描述 ：任务列表变化通知
 */

public class TaskListChangerNotify extends AbsResponseMessage
{
    /**
     * 任务变化类型
     */
    private byte         type;

    /**
     * 任务
     */
    private TaskInstance task;

    /**
     * @param _type
     * @param _taskIns
     */
    public TaskListChangerNotify(byte _type, TaskInstance _task)
    {
        type = _type;
        task = _task;
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
        yos.writeByte(type);
        yos.writeInt(task.getArchetype().getID());
        yos.writeUTF(task.getArchetype().getName());

        if (ADD == type)
        {
            yos.writeShort(task.getArchetype().getLevel());
            yos.writeByte(task.isCompleted());
            yos.writeByte((null != task.getArchetype().getAward()
                    .getOptionalGoodsList() && task.getArchetype().getAward()
                    .getOptionalGoodsList().size() > 0)
                    || (null != task.getArchetype().getAward()
                            .getBoundGoodsList() && task.getArchetype()
                            .getAward().getBoundGoodsList().size() > 0)
                    || task.getArchetype().getAward().skillID > 0);
        }
    }

    /**
     * 新增任务
     */
    public static final byte ADD    = 1;

    /**
     * 完成任务
     */
    public static final byte SUBMIT = 2;

    /**
     * 放弃任务
     */
    public static final byte CANCEL = 3;
}
