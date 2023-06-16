package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AnimalWalkNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-12 下午06:03:49
 * @描述 ：
 */

public class AnimalWalkNotify extends AbsResponseMessage
{
    private int    animalID;

    private byte[] path;
    
    private short  animationID;
    
    private byte   x;
    
    private byte   y;

    public AnimalWalkNotify(int _animalID, byte[] _path, short _animationID, byte _x, byte _y)
    {
        animalID = _animalID;
        path = _path;
        animationID = _animationID;
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
        yos.writeInt(animalID);
        yos.writeShort(animationID);
        yos.writeByte((byte) path.length);
        yos.writeBytes(path);
        //add by zhengl; date: 2011-05-10; note: 移动添加x,y校正
        yos.writeByte(x);
        yos.writeByte(y);
    }

}
