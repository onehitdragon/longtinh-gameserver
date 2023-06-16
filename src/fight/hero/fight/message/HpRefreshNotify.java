package hero.fight.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 HpRefreshNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 下午11:40:57
 * @描述 ：生命值刷新通知
 */

public class HpRefreshNotify extends AbsResponseMessage
{
    /**
     * 发生变化的对象类型
     */
    private byte    objectType;

    /**
     * 发生变化的对象编号
     */
    private int     objectID;

    /**
     * 当前生命值
     */
    private int     currentHP;

    /**
     * 变化的生命值
     */
    private int     changeHP;

    /**
     * 是否单独显示数值
     */
    private boolean visible;

    /**
     * 爆击标记
     */
    private boolean isDeathblow;

    /**
     * 构造
     * 
     * @param _objectType 对象类型
     * @param _objectID 对象编号
     * @param _currentHP 当前生命值
     * @param _changeHP 变化的生命值
     * @param _visible 是否可见
     * @param _isDeathblow 是否是爆击
     */
    public HpRefreshNotify(byte _objectType, int _objectID, int _currentHP,
            int _changeHP, boolean _visible, boolean _isDeathblow)
    {
        objectType = _objectType;
        objectID = _objectID;
        currentHP = _currentHP;
        changeHP = _changeHP;
        visible = _visible;
        isDeathblow = _isDeathblow;
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
        yos.writeByte(objectType);
        yos.writeInt(objectID);
        yos.writeInt(currentHP);
        yos.writeInt(changeHP);
        yos.writeByte(visible);
        yos.writeByte(isDeathblow);
    }

}
