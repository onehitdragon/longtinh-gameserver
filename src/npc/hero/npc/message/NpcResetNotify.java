package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NpcResetNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-6 下午04:37:34
 * @描述 ：
 */

public class NpcResetNotify extends AbsResponseMessage
{
    /**
     * NPC编号
     */
    private int   npcID;

    /**
     * X坐标
     */
    private short x;

    /**
     * Y坐标
     */
    private short y;

    public NpcResetNotify(int _npcID, short _x, short _y)
    {
        npcID = _npcID;
        x = _x;
        y = _y;
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
        yos.writeInt(npcID);
        yos.writeByte(x);
        yos.writeByte(y);
    }

}
