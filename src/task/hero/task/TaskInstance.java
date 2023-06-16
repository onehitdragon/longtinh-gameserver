package hero.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import hero.share.CompositeInteger;
import hero.task.target.BaseTaskTarget;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskInstance.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午04:28:08
 * @描述 ：
 */

public class TaskInstance
{
    /**
     * 任务模板
     */
    private Task                      task;

    /**
     * 任务目标列表
     */
    private ArrayList<BaseTaskTarget> tastTargetList;

    /**
     * 一个玩家最多可接受的任务数量
     */
    public static final short         MAX_TAST_NUMBER = 20;

    /**
     * 构造
     * 
     * @param _task
     */
    public TaskInstance(Task _task)
    {
        task = _task;

        ArrayList<BaseTaskTarget> targetList = _task.getTargetList();

        if (null != targetList)
        {
            tastTargetList = new ArrayList<BaseTaskTarget>();

            for (int i = 0; i < targetList.size(); i++)
            {
                try
                {
                    tastTargetList.add(targetList.get(i).clone());
                }
                catch (CloneNotSupportedException cnse)
                {
                    cnse.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取任务模板
     * 
     * @return
     */
    public Task getArchetype ()
    {
        return task;
    }

    public void changeTargetState (BaseTaskTarget _taskTarget)
    {
        if (tastTargetList.contains(_taskTarget))
        {

        }
    }

    /**
     * 是否完成
     * 
     * @return
     */
    public boolean isCompleted ()
    {
        boolean completeState = true;

        for (BaseTaskTarget target : tastTargetList)
        {
            completeState = completeState & target.isCompleted();
        }

        return completeState;
    }

    /**
     * 获取目标列表
     * 
     * @return
     */
    public ArrayList<BaseTaskTarget> getTargetList ()
    {
        return tastTargetList;
    }
}
