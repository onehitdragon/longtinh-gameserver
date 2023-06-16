package hero.task.target;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTargetBuilder.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-10 上午10:59:27
 * @描述 ：
 */

public class TaskTargetBuilder
{
    /**
     * 单例
     */
    private static TaskTargetBuilder instance;

    /**
     * 构造
     */
    private TaskTargetBuilder()
    {

    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static TaskTargetBuilder getInstance ()
    {
        if (null == instance)
        {
            instance = new TaskTargetBuilder();
        }

        return instance;
    }

    public BaseTaskTarget create ()
    {
        BaseTaskTarget taskTarget = null;

        return taskTarget;
    }
}
