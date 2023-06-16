package hero.task.target;

import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTargetFoundAPath.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午04:15:37
 * @描述 ：探路类型任务
 */

public class TaskTargetFoundAPath extends BaseTaskTarget
{
    /**
     * 地图编号
     */
    public short                  mapID;

    /**
     * 目标地点X坐标
     */
    public short                  x;

    /**
     * 目标地点Y坐标
     */
    public short                  y;

    /**
     * 目标地点范围误差
     */
    public short                  mistakeRang;

    /**
     * 构造
     * 
     * @param _ID
     */
    public TaskTargetFoundAPath(int _ID, short _mapID, short _x, short _y,
            short _range)
    {
        super(_ID);
        mapID = _mapID;
        x = _x;
        y = _y;
        mistakeRang = _range;

        Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);

        if (null != map)
        {
            int centerX = map.getWidth() / 2;
            int centerY = map.getHeight() / 2;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    description = Tip.TIP_TASK_LOCATION_LIST[0];
                }
                else
                {
                    description = Tip.TIP_TASK_LOCATION_LIST[1];
                }

            }
            else
            {
                if (y < centerY)
                {
                    description = Tip.TIP_TASK_LOCATION_LIST[2];
                }
                else
                {
                    description = Tip.TIP_TASK_LOCATION_LIST[3];
                }
            }

            description = new StringBuffer(Tip.TIP_TASK_DESC_PREFIX_EXPLORE).append(map.getName())
                    .append(Tip.TIP_TASK_CONNECTOR).append(description).toString();
        }
    }

    @Override
    public ETastTargetType getType ()
    {
        // TODO Auto-generated method stub
        return ETastTargetType.FOUND_A_PATH;
    }

    @Override
    public boolean isCompleted ()
    {
        // TODO Auto-generated method stub
        return currentNumber == 1 ? true : false;
    }

    /**
     * 完成
     */
    public void complete ()
    {
        currentNumber = 1;
    }

    @Override
    public String getDescripiton ()
    {
        // TODO Auto-generated method stub
        return description;
    }

    @Override
    public BaseTaskTarget clone () throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
    }

    @Override
    public void setCurrentNumber (short _number)
    {
        // TODO Auto-generated method stub
        currentNumber = _number;

        if (currentNumber > 1)
        {
            currentNumber = 1;
        }
    }

    @Override
    public boolean canTransmit ()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
