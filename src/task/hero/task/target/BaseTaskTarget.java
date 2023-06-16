package hero.task.target;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 BaseTaskTarget.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午01:54:10
 * @描述 ：
 */

public abstract class BaseTaskTarget implements Cloneable
{
    /**
     * 编号
     */
    private int     ID;

    /**
     * 传送信息（目标地图编号、X坐标、Y坐标）
     */
    protected short[] transmitMapInfo;

    /**
     * 任务目标当前数量
     */
    protected short currentNumber;

    /**
     * 构造
     * 
     * @param _ID
     */
    public BaseTaskTarget(int _ID)
    {
        ID = _ID;
    }

    /**
     * 描述
     */
    protected String description = "";

    /**
     * 获取编号
     * 
     * @return
     */
    public int getID ()
    {
        return ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public BaseTaskTarget clone () throws CloneNotSupportedException
    {
        return (BaseTaskTarget) super.clone();
    }

    /**
     * 获取当前数量
     * 
     * @return
     */
    public short getCurrentNumber ()
    {
        return currentNumber;
    }

    /**
     * 目标类型
     * 
     * @return
     */
    public abstract ETastTargetType getType ();

    /**
     * 是否已完成
     * 
     * @return
     */
    public abstract boolean isCompleted ();

    /**
     * 完成
     */
    public abstract void complete ();

    /**
     * 设置传送点信息
     * 
     * @param _transmitInfo
     */
    public void setTransmitMapInfo (short[] _transmitMapInfo)
    {
        transmitMapInfo = _transmitMapInfo;
    }

    /**
     * 获取传送点信息
     * 
     * @return
     */
    public short[] getTransmitMapInfo ()
    {
        return transmitMapInfo;
    }

    /**
     * 能否传送
     */
    public abstract boolean canTransmit ();

    /**
     * 设置当前数量
     * 
     * @param _currentNumber
     */
    public abstract void setCurrentNumber (short _currentNumber);

    /**
     * 描述
     * 
     * @return
     */
    public abstract String getDescripiton ();
}
