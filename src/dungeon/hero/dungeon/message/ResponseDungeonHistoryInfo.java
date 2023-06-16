package hero.dungeon.message;

import hero.dungeon.Dungeon;
import hero.dungeon.DungeonHistory;
import hero.dungeon.service.DungeonHistoryManager;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseDungeonHistoryInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-22 上午10:01:47
 * @描述 ：响应副本进度信息
 */

public class ResponseDungeonHistoryInfo extends AbsResponseMessage
{
    /**
     * 副本进度列表
     */
    private ArrayList<DungeonHistory> historyList;

    /**
     * 构造
     * 
     * @param _historyList
     */
    public ResponseDungeonHistoryInfo(ArrayList<DungeonHistory> _historyList)
    {
        historyList = _historyList;
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
        if (null != historyList)
        {
            yos.writeShort(historyList.size());

            if (historyList.size() > 0)
            {
                String[] dungeonRefreshTimeInfo = DungeonHistoryManager
                        .getInstance().getDungeonRefreshTimeInfo();

                for (DungeonHistory history : historyList)
                {
                    if (Dungeon.PATTERN_OF_EASY == history.getPattern())
                    {
                        yos.writeUTF(history.getDungeonName()
                                + Dungeon.PATTERN_DESC_OF_EASY);
                    }
                    else
                    {
                        yos.writeUTF(history.getDungeonName()
                                + Dungeon.PATTERN_DESC_OF_DIFFICULT);
                    }

                    yos.writeInt(history.getID());

                    if (Dungeon.TYPE_RAID == history.getDungeonType())
                    {
                        yos.writeUTF(dungeonRefreshTimeInfo[0]);
                    }
                    else
                    {
                        yos.writeUTF(dungeonRefreshTimeInfo[1]);
                    }
                }
            }
        }
        else
        {
            yos.writeShort(0);
        }
    }
}
