package hero.map.message;

import hero.npc.others.Box;
import hero.npc.others.RoadInstructPlate;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NotifyBoxList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-26 上午10:52:46
 * @描述 ：
 */

public class ResponseBoxList extends AbsResponseMessage
{
    /**
     * 地图上的宝箱列表
     */
    ArrayList<Box> boxList;

    /**
     * 地图上的宝箱
     * 
     * @param _boxList
     */
    public ResponseBoxList(ArrayList<Box> _boxList)
    {
        boxList = _boxList;
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
        yos.writeByte(boxList.size());

        if (boxList.size() > 0)
        {
            for (Box box : boxList)
            {
                yos.writeInt(box.getID());
                yos.writeByte(box.getCellX());
                yos.writeByte(box.getCellY());
            }
        }
    }

}
