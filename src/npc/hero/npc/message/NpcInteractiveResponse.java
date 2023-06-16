package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 InteractiveResponse.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-12 下午04:26:22
 * @描述 ：
 */

public class NpcInteractiveResponse extends AbsResponseMessage
{
    /**
     * NPC编号
     */
    private int    npcID;

    /**
     * 操作步骤标识
     */
    private byte   step;

    /**
     * 绘制UI的数据
     */
    private byte[] uiData;

    /**
     * 功能标记
     */
    private int    functionMark;

    /**
     * 构造
     * 
     * @param _npcID
     * @param _functionMark
     * @param _step
     * @param _uiData
     */
    public NpcInteractiveResponse(int _npcID, int _functionMark, byte _step,
            byte[] _uiData)
    {
        npcID = _npcID;
        step = _step;
        uiData = _uiData;
        functionMark = _functionMark;
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
        yos.writeInt(functionMark);
        yos.writeByte(step);
        yos.writeBytes(uiData);
    }

}
