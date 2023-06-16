package hero.fight.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialStatusChangeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 上午11:11:08
 * @描述 ：游戏对象特殊状态变化通知
 */

public class SpecialStatusChangeNotify extends AbsResponseMessage
{
    /**
     * 对象类型
     */
    private byte objectType;

    /**
     * 对象编号
     */
    private int  objectID;

    /**
     * 特殊状态
     */
    private byte status;

    /**
     * 构造(游戏对象特殊状态变化通知)
     * 
     * @param _objectType 对象类型
     * @param _objectID 对象编号
     * @param _status 状态标识
     */
    public SpecialStatusChangeNotify(byte _objectType, int _objectID,
            byte _status)
    {
        objectType = _objectType;
        objectID = _objectID;
        status = _status;
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
        yos.writeByte(status);
    }
}
