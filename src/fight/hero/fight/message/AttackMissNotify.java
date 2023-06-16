package hero.fight.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AttackMissNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-11 下午05:11:49
 * @描述 ：攻击未击中通知
 */

public class AttackMissNotify extends AbsResponseMessage
{
    /**
     * 目标的对象类型
     */
    private byte targetObjectType;

    /**
     * 对象ObjectID
     */
    private int  targetObjectID;

    /**
     * 构造
     * 
     * @param _objectType 对象类型
     * @param _objectID 对象编号
     */
    public AttackMissNotify(byte _objectType, int _objectID)
    {
        targetObjectType = _objectType;
        targetObjectID = _objectID;
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
        yos.writeByte(targetObjectType);
        yos.writeInt(targetObjectID);
    }
}
