package hero.task.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeNpcTaskMark.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-21 下午02:42:24
 * @描述 ：
 */

public class ChangeNpcTaskMark extends AbsResponseMessage
{
    /**
     * 更新的NPC编号
     */
    private int npcID;

    /**
     * 头顶任务标记（0没有，1有可接任务，2有可提交任务, 3有未完成任务）
     */
    private int npcTaskMark;

    public ChangeNpcTaskMark(int _npcID, int _npcTaskMark)
    {
        npcID = _npcID;
        npcTaskMark = _npcTaskMark;
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
        yos.writeByte(npcTaskMark);
    }

}
