package hero.task.target;

import hero.npc.dict.GearDataDict;
import hero.npc.dict.GearDataDict.GearData;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TastTargetOpenBox.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午04:25:54
 * @描述 ：打开机关类型任务
 */

public class TaskTargetOpenGear extends BaseTaskTarget
{
    /**
     * 目标机关模板编号
     */
    public String               gearModelID;

    public TaskTargetOpenGear(int _ID, String _gearNpcModelID)
    {
        super(_ID);

        gearModelID = _gearNpcModelID;

        GearData data = GearDataDict.getInstance().getGearData(_gearNpcModelID);

        if (null != data)
        {
            String name = data.name;
            String optionDesc = data.optionDesc;

            if (null != optionDesc)
            {
                description = new StringBuffer(optionDesc).append(name)
                        .toString();
            }
        }
    }

    @Override
    public ETastTargetType getType ()
    {
        // TODO Auto-generated method stub
        return ETastTargetType.OPEN_GEAR;
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
        return new StringBuffer(description).append(Tip.SPACE)
                .append(currentNumber).append(Tip.SEPATATOR).append(1).toString();
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
        return null == transmitMapInfo ? false : true;
    }
}
