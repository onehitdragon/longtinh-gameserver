package hero.task.target;

import hero.npc.Monster;
import hero.npc.dict.MonsterDataDict;
import hero.npc.dict.MonsterDataDict.MonsterData;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTargetKillMonster.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午04:23:20
 * @描述 ：杀怪类型任务
 */

public class TaskTargetKillMonster extends BaseTaskTarget implements
        INumberTypeTarget
{

    /**
     * 需要杀的怪物编号
     */
    public String               monsterModelID;

    /**
     * 需要杀的怪的数量
     */
    public short                number;

    public TaskTargetKillMonster(int _ID, String _monsterModelID, short _number)
    {
        super(_ID);
        monsterModelID = _monsterModelID;
        number = _number;

        String monsterName = NotPlayerServiceImpl.getInstance()
                .getNotPlayerNameByModelID(_monsterModelID);

        if (null != monsterName)
        {
            description = new StringBuffer(Tip.DESC_PREFIX).append(monsterName)
                    .toString();
        }
    }

    @Override
    public ETastTargetType getType ()
    {
        // TODO Auto-generated method stub
        return ETastTargetType.KILL_MONSTER;
    }

    @Override
    public boolean isCompleted ()
    {
        // TODO Auto-generated method stub
        return currentNumber >= number ? true : false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.task.target.INumberTypeTarget#numberChanged(int)
     */
    public void numberChanged (int _changeNumber)
    {
        currentNumber += _changeNumber;

        if (currentNumber >= number)
        {
            currentNumber = number;
        }
    }

    @Override
    public String getDescripiton ()
    {
        // TODO Auto-generated method stub
        return new StringBuffer(description).append(Tip.SPACE)
                .append(currentNumber).append(Tip.SEPATATOR).append(number)
                .toString();
    }

    @Override
    public BaseTaskTarget clone () throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
    }

    /**
     * @param _number
     */
    public void setCurrentNumber (short _number)
    {
        // TODO Auto-generated method stub
        currentNumber = _number;

        if (currentNumber > number)
        {
            currentNumber = number;
        }
    }

    @Override
    public void complete ()
    {
        // TODO Auto-generated method stub
        currentNumber = number;
    }
    
    @Override
    public boolean canTransmit ()
    {
        // TODO Auto-generated method stub
        return null == transmitMapInfo ? false : true;
    }
}
