package hero.task;

import hero.task.target.BaseTaskTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Task.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 上午09:59:38
 * @描述 ：
 */

public class Task
{
    public enum ETaskDifficultyLevel
    {
        EASY("Độ khó: Dễ"), DIFFICULT("Độ khó: Khó"), NIGHTMARE("Độ khó: Ác mộng");

        /**
         * 难度标识
         */
        private static String[] difficultyMark = {"简单", "困难", "噩梦" };

        /**
         * 描述
         */
        private String          description;

        private ETaskDifficultyLevel(String _description)
        {
            description = _description;
        }

        public String getDescription ()
        {
            return description;
        }

        public static ETaskDifficultyLevel get (String _mark)
        {
            if (_mark.equals(difficultyMark[0]))
            {
                return EASY;
            }
            else if (_mark.equals(difficultyMark[1]))
            {
                return DIFFICULT;
            }
            else if (_mark.equals(difficultyMark[2]))
            {
                return NIGHTMARE;
            }

            return EASY;
        }
    }

    /**
     * 任务名称
     */
    private String                    name;

    /**
     * 任务编号
     */
    private int                       ID;

    /**
     * 任务等级
     */
    private short                     level;
    
    /**
     * 是否主线任务
     */
    private boolean					  isMainLine;

    /**
     * 是否可反复完成
     */
    private boolean                   repeated;

    /**
     * 任务目标列表
     */
    private ArrayList<BaseTaskTarget> targetList;

    /**
     * 接任务、查看列表、提交任务时候的描述
     */
    private final String[]            descriptionList = new String[3];

    /**
     * 任务接受条件
     */
    private Condition                 condition;
    
    /**
     * 任务推广信息
     */
    private Push					  taskPush;

    /**
     * 任务奖励
     */
    private Award                     award;

    /**
     * 接受任务时获得的物品列表
     */
    private ArrayList<int[]>          receiveGoodsList;

    /**
     * 接受任务时获得的效果编号
     */
    private int                       receiveEffectID;

    /**
     * 派发任务的NPC编号
     */
    private String                    distributeNpcModelID;

    /**
     * 任务提交NCP编号
     */
    private String                    submitNpcModelID;

    /**
     * 难度等级
     */
    private ETaskDifficultyLevel      difficultyLevel;

    public Task(int _ID, String _name, short _level, boolean _isRepeated)
    {
        ID = _ID;
        name = _name;
        level = _level;
        repeated = _isRepeated;
        targetList = new ArrayList<BaseTaskTarget>();
    }

    /**
     * 获取任务编号
     * 
     * @return
     */
    public int getID ()
    {
        return ID;
    }

    /**
     * 获取任务名称
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 获取任务等级
     * 
     * @return
     */
    public short getLevel ()
    {
        return level;
    }
    
    /**
     * 是否主线
     * @return
     */
    public boolean getMainLine ()
    {
    	return isMainLine;
    }
    
    /**
     * 设置为主线任务
     * @param _isMainLine
     */
    public void setMainLine ()
    {
    	isMainLine = true;
    }

    /**
     * 是否为可反复完成的任务
     * 
     * @return
     */
    public boolean isRepeated ()
    {
        return repeated;
    }

    /**
     * 添加任务目标
     * 
     * @param _taskTarget
     */
    public void addTarget (BaseTaskTarget _taskTarget)
    {
        targetList.add(_taskTarget);
    }

    /**
     * 获取任务目标列表
     * 
     * @return
     */
    public ArrayList<BaseTaskTarget> getTargetList ()
    {
        return targetList;
    }

    /**
     * 设置任务接受条件
     * 
     * @param _condition
     */
    public void setCondition (Condition _condition)
    {
        condition = _condition;
    }

    /**
     * 获取任务接受条件
     * 
     * @return
     */
    public Condition getCondition ()
    {
        return condition;
    }
    
    /**
     * 设置任务信息
     * @param _taskPush
     */
    public void setTaskPush (Push _taskPush)
    {
    	taskPush = _taskPush;
    }
    
    /**
     * 返回任务推广信息
     * @return
     */
    public Push getTaskPush()
    {
    	return taskPush;
    }

    /**
     * 设置任务奖励
     * 
     * @param _award
     */
    public void setAward (Award _award)
    {
        award = _award;
    }

    /**
     * 获取任务奖励
     * 
     * @return
     */
    public Award getAward ()
    {
        return award;
    }

    /**
     * 设置任务描述
     * 
     * @param _descList
     */
    public void setDescList (String[] _descList)
    {
        if (_descList.length != 3)
        {
            return;
        }

        descriptionList[0] = difficultyLevel.getDescription() + "\n"
                + _descList[0];

        descriptionList[1] = difficultyLevel.getDescription() + "\n"
                + _descList[1];

        descriptionList[2] = _descList[2];
    }

    /**
     * 获取接受任务时的描述
     * 
     * @return
     */
    public String getReceiveDesc ()
    {
        if (null != descriptionList)
        {
            return descriptionList[0];
        }

        return "";
    }

    /**
     * 获取查看任务列表时的描述
     * 
     * @return
     */
    public String getViewDesc ()
    {
        if (null != descriptionList)
        {
            return descriptionList[1];
        }

        return "";
    }

    /**
     * 获取完成任务时的描述
     * 
     * @return
     */
    public String getSubmitDesc ()
    {
        if (null != descriptionList)
        {
            return descriptionList[2];
        }

        return "";
    }

    /**
     * 添加接受时获得的物品
     * 
     * @param _goodsID 物品编号
     * @param _number 物品数量
     */
    public void addReceiveGoods (int _goodsID, short _number)
    {
        if (null == receiveGoodsList)
        {
            receiveGoodsList = new ArrayList<int[]>();
        }

        receiveGoodsList.add(new int[]{_goodsID, _number });
    }

    /**
     * 获取接受任务时得到的道具
     * 
     * @return
     */
    public ArrayList<int[]> getReceiveGoodsList ()
    {
        return receiveGoodsList;
    }

    /**
     * 设置接受任务时获得的效果编号
     * 
     * @param _effectID
     */
    public void setReceiveEffectID (int _effectID)
    {
        receiveEffectID = _effectID;
    }

    /**
     * 获取接受任务时获得的效果编号
     * 
     * @return
     */
    public int getReceiveEffectID ()
    {
        return receiveEffectID;
    }

    /**
     * 设置任务提交NPC模板编号
     */
    public void setSubmitNpcID (String _npcModelID)
    {
        submitNpcModelID = _npcModelID;
    }

    /**
     * 获取提交NPC模板编号
     * 
     * @return
     */
    public String getSubmitNpcID ()
    {
        return submitNpcModelID;
    }

    /**
     * 设置接受任务NPC模板编号
     */
    public void setDistributeNpcModelID (String _npcModelID)
    {
        distributeNpcModelID = _npcModelID;
    }

    /**
     * 获取接受NPC模板编号
     * 
     * @return
     */
    public String getDistributeNpcModelID ()
    {
        return distributeNpcModelID;
    }

    public void setDifficultyLevel (ETaskDifficultyLevel _difficultyLevel)
    {
        difficultyLevel = _difficultyLevel;
    }

    /**
     * 获取难度等级
     * 
     * @return
     */
    public ETaskDifficultyLevel getDifficultyLevel ()
    {
        return difficultyLevel;
    }
}
