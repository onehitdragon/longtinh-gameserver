package hero.task.message;

import hero.npc.Npc;
import hero.npc.service.NotPlayerServiceImpl;
import hero.task.Task;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ReceiveableTaskList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-7 下午04:23:39
 * @描述 ：可接任务列表信息（包含任务名称、编号、任务接受NPC所在地图名称）
 */

public class ReceiveableTaskList extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ReceiveableTaskList.class);
    /**
     * 任务列表
     */
    private ArrayList<Task> taskList;

    /**
     * 构造
     * 
     * @param _taskList
     */
    public ReceiveableTaskList(ArrayList<Task> _taskList)
    {
        taskList = _taskList;
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
        yos.writeByte(taskList.size());
        Npc npc;

        for (Task task : taskList)
        {
            yos.writeInt(task.getID());
            yos.writeUTF(task.getName()+" ( "+task.getLevel()+" )");

            npc = NotPlayerServiceImpl.getInstance().getNpc(
                    task.getDistributeNpcModelID());

            if (null != npc)
            {
                yos.writeUTF(npc.where().getName());
            }
            else
            {
            	log.info("根据任务ID=" + task.getID() + ";名字="
                        + task.getName() + "NPCID=" + task.getDistributeNpcModelID());
            	log.info("获得NPC为NULL");
            	
                yos.writeUTF(EMPTY_MAP_NAME);
            }
        }
    }

    private static final String EMPTY_MAP_NAME = "";
}
