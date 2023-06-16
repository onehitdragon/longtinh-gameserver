package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

import hero.npc.Monster;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterDisengageFightNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-10 下午03:03:07
 * @描述 ：
 */

public class MonsterDisengageFightNotify extends AbsResponseMessage
{
    private int   monsterID;

    private short locationX;

    private short locationY;

    public MonsterDisengageFightNotify(int _monsterID, short _locationX,
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
