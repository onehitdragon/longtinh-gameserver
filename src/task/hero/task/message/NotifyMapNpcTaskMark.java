package hero.task.message;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NotifyMapNpcTaskMark.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-23 下午04:01:43
 * @描述 ：
 */

public class NotifyMapNpcTaskMark extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(NotifyMapNpcTaskMark.class);
    /**
     * NPC任务标记清单,采用NPC编号后跟任务标记的形式保存,长度的1/2是标记数量
     */
    private ArrayList<Integer> npcTaskMarks;

    public NotifyMapNpcTaskMark(ArrayList<Integer> _npcTaskMarks)
    {
        npcTaskMarks = _npcTaskMarks;
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
        yos.writeByte(npcTaskMarks.size() / 2);
        int id = 0;
        int type = 0;
        for (int i = 0; i < npcTaskMarks.size();)
        {
        	id = npcTaskMarks.get(i++);
        	type = npcTaskMarks.get(i++);
            yos.writeInt(id);
            log.info("npcTaskMarks.get(i++):" + id);
            yos.writeByte(type);
            log.info("npcTaskMarks.get(i++):" + type);
        }
    }
}
