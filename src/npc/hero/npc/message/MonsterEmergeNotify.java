package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterEmergeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-24 下午02:22:11
 * @描述 ：怪物现身通知（从隐身状态下返回）
 */

public class MonsterEmergeNotify extends AbsResponseMessage
{
    /**
     * 怪物ID
     */
    private int   monsterID;

    /**
     * 现身位置X坐标
     */
    private short locationX;

    /**
     * 现身位置Y坐标
     */
    private short locationY;

    /**
     * 构造
     * 
     * @param _monsterID
     * @param _locationX
     * @param _locationY
     */
    public MonsterEmergeNotify(int _monsterID, short _locationX,
            short _locationY)
    {
        monsterID = _monsterID;
        locationX = _locationX;
        locationY = _locationY;
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
        yos.writeInt(monsterID);
        yos.writeByte(locationX);
        yos.writeByte(locationY);
    }

}
