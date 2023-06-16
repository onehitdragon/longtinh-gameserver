package hero.task.target;

import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.npc.Npc;
import hero.npc.dict.NpcDataDict.NpcData;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTargetEscortNpc.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午04:20:23
 * @描述 ：护送NPC类型
 */

public class TaskTargetEscortNpc extends BaseTaskTarget
{
    /**
     * 护送的NPC模板编号
     */
    public String               npcModelID;

    /**
     * 计时（毫秒）
     */
    public int                  countTime;

    /**
     * 护送目的地图编号
     */
    public int                  mapID;

    /**
     * 护送目的地X坐标
     */
    public short                x;

    /**
     * 护送目的地Y坐标
     */
    public short                y;

    /**
     * 护送目的地范围误差
     */
    public short                mistakeRang;

    public TaskTargetEscortNpc(int _ID, String _npcModelID, int _countTime,
            int _mapID, short _x, short _y, short _range)
    {
        super(_ID);
        npcModelID = _npcModelID;
        countTime = _countTime;
        mapID = _mapID;
        x = _x;
        y = _y;
        mistakeRang = _range;

        String npcName = NotPlayerServiceImpl.getInstance()
                .getNotPlayerNameByModelID(_npcModelID);

        if (null != npcName)
        {
            description = new StringBuffer(Tip.TIP_TASK_AT).append(countTime / 60000)
                    .append(Tip.TIP_TASK_MINUITE).append(Tip.TIP_TASK_DESC_PREFIX).append(npcName)
                    .append(description).toString();
        }
    }

    @Override
    public ETastTargetType getType ()
    {
        // TODO Auto-generated method stub
        return ETastTargetType.ESCORT_NPC;
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

    /*
     * (non-Javadoc)
     * 
     * @see hero.task.target.BaseTaskTarget#clone()
     */
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
