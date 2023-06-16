package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeNpcStat.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-6 下午04:23:27
 * @描述 ：
 */

public class ChangeNpcStat extends AbsResponseMessage
{
    /**
     * npc编号
     */
    private int     npcID;

    /**
     * 能否交互
     */
    private boolean canInteract;

    /**
     * 构造
     * 
     * @param _npcID
     * @param _canInteract
     */
    public ChangeNpcStat(int _npcID, boolean _canInteract)
    {
        npcID = _npcID;
        canInteract = _canInteract;
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
        yos.writeByte(canInteract);
    }

}
