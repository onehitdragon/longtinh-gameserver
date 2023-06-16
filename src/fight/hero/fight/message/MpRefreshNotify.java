package hero.fight.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MpRefreshNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-8 下午05:41:13
 * @描述 ：魔法值或力值变化通知
 */

public class MpRefreshNotify extends AbsResponseMessage
{
    /**
     * 发生变化的对象类型
     */
    private byte objectType;

    /**
     * 发生变化的对象编号
     */
    private int  objectID;

    /**
     * 变化的数值
     */
    private int  changeValue;
    
    private boolean visible;

    /**
     * 构造
     * 
     * @param _objectType 对象类型
     * @param _objectID 对象编号
     * @param _changeValue 变化的数值
     * @param _visible 是否显示
     */
    public MpRefreshNotify(byte _objectType, int _objectID, int _changeValue, boolean _visible)
    {
        objectType = _objectType;
        objectID = _objectID;
        changeValue = _changeValue;
        visible = _visible;
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
        yos.writeInt(changeValue);
        yos.writeByte(visible);/*add by zhengl; date: 2011-04-08; note: 添加是否可见*/
    }
}
