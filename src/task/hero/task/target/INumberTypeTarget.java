package hero.task.target;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 INumberTypeTarget.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-13 下午03:06:06
 * @描述 ：
 */

public interface INumberTypeTarget
{
    /**
     * 任务物品数量发生变化
     * 
     * @param _currentNumber 当前数量
     */
    public void numberChanged (int _changeNumber);
}
