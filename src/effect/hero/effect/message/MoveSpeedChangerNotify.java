package hero.effect.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MoveSpeedChangerNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-24 下午02:47:18
 * @描述 ：移动速度改变通知
 */

public class MoveSpeedChangerNotify extends AbsResponseMessage
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
     * 移动速度
     */
    private byte moveSpeed;

    /**
     * 构造
     * 
     * @param _objectType
     * @param _objectID
     * @param _moveSpeed
     */
    public MoveSpeedChangerNotify(byte _objectType, int _objectID,
            byte _moveSpeed)
    {
        objectType = _objectType;
        objectID = _objectID;
        moveSpeed = _moveSpeed;
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
        yos.writeByte(moveSpeed);
    }

}
