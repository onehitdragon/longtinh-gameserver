package hero.map.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DisappearNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-30 下午03:20:07
 * @描述 ：弹出消息
 */

public class DisappearNotify extends AbsResponseMessage
{
    /**
     * 消失的对象类型
     */
    private byte objectType;

    /**
     * 消失的对象编号
     */
    private int  objectID;
    /**
     * 消失对象的hp
     */
    private int hp;
    /**
     * 消失对象的maxhp
     */
    private int maxHp;
    /**
     * 消失对象的mp
     */
    private int mp;
    /**
     * 消失对象的maxMp
     */
    private int maxMp;


    /**
     * 构造
     * 
     * @param _objectType
     * @param _objectID
     */
    public DisappearNotify(byte _objectType, int _objectID, int _hp, int _maxHp, int _mp, int _maxMp)
    {
        objectType = _objectType;
        objectID = _objectID;
        hp = _hp;
        maxHp = _maxHp;
        mp = _mp;
        maxMp = _maxMp;
    }

    public DisappearNotify(byte _objectType, int _objectID){
        objectType = _objectType;
        objectID = _objectID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.data.ResultMessage#getPriority()
     */
    @Override
    public int getPriority ()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.data.ResultMessage#write()
     */
    @Override
    protected void write () throws IOException
    {
        yos.writeByte(objectType);
        yos.writeInt(objectID);
        yos.writeInt(hp);
        yos.writeInt(maxHp);
        yos.writeInt(mp);
        yos.writeInt(maxMp);
    }
}
