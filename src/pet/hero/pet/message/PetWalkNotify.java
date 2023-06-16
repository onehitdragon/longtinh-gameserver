package hero.pet.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class PetWalkNotify extends AbsResponseMessage
{
    /**
     * 内存临时编号
     */
    private int    objectID;

    /**
     * 行走路径
     */
    private byte[] path;

    /**
     * 行走速度
     */
    private byte   speed;

    /**
     * @param _walkerID
     * @param _speed
     * @param _path
     */
    public PetWalkNotify(int _walkerID, byte _speed, byte[] _path)
    {
        objectID = _walkerID;
        speed = _speed;
        path = _path;
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
        yos.writeInt(objectID);
        yos.writeByte(speed);
        yos.writeByte(path.length);
        yos.writeBytes(path);
    }

}