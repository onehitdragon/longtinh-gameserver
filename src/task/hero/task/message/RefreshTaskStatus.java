package hero.task.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RefreshTaskStatus.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-21 下午02:43:21
 * @描述 ：刷新任务状态
 */

public class RefreshTaskStatus extends AbsResponseMessage
{
    /**
     * 任务编号
     */
    private int     taskID;

    /**
     * 目标编号
     */
    private int     targetID;

    /**
     * 目标是否完成
     */
    private boolean targetIsComplted;

    /**
     * 任务是否完成
     */
    private boolean taskIsComplted;

    /**
     * 目标描述
     */
    private String  targetDesc;

    /**
     * @param _taskID
     * @param _targetID
     * @param _targetIsComplted
     * @param _targetDesc
     * @param _taskIsComplted
     */
    public RefreshTaskStatus(int _taskID, int _targetID,
            boolean _targetIsComplted, String _targetDesc,
            boolean _taskIsComplted)
    {
        taskID = _taskID;
        targetID = _targetID;
        targetIsComplted = _targetIsComplted;
        targetDesc = _targetDesc;
        taskIsComplted = _taskIsComplted;
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
        yos.writeInt(taskID);
        yos.writeInt(targetID);
        yos.writeByte(targetIsComplted);
        yos.writeUTF(targetDesc);
        yos.writeByte(taskIsComplted);
    }
}
