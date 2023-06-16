package hero.item.legacy;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskGoodsLegacyInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-27 下午09:33:57
 * @描述 ：怪物掉落任务物品信息
 */

public class TaskGoodsLegacyInfo
{
    /**
     * 关联的任务编号
     */
    private int                taskID;

    /**
     * 任务物品编号
     */
    private int                taskGoodsID;

    /**
     * 数量
     */
    private int                number;

    /**
     * 能拾取的角色编号列表
     */
    private ArrayList<Integer> pickerList;

    /**
     * 构造
     * 
     * @param _taskGoodsID
     * @param _number
     */
    public TaskGoodsLegacyInfo(int _taskID, int _taskGoodsID, int _number)
    {
        taskID = _taskID;
        taskGoodsID = _taskGoodsID;
        number = _number;
    }

    /**
     * 添加能拾取者
     * 
     * @param _playerUserID
     */
    public void addPicker (int _playerUserID)
    {
        if (null == pickerList)
        {
            pickerList = new ArrayList<Integer>();
            pickerList.add(_playerUserID);
        }
        else if (!pickerList.contains(_playerUserID))
        {
            pickerList.add(_playerUserID);
        }
    }

    /**
     * 移除拾取者
     * 
     * @param _playerUserID
     */
    public void removePicker (int _playerUserID)
    {
        pickerList.remove(Integer.valueOf(_playerUserID));
    }

    /**
     * 是否被所有人拾取过了
     * 
     * @return
     */
    public boolean pickedByAll ()
    {
        return pickerList.size() == 0 ? true : false;
    }

    /**
     * 获取任务编号
     * 
     * @return
     */
    public int getTaskID ()
    {
        return taskID;
    }

    /**
     * 获取掉落的任务物品编号
     * 
     * @return
     */
    public int getTaskGoodsID ()
    {
        return taskGoodsID;
    }

    /**
     * 获取掉落的任务物品数量
     * 
     * @return
     */
    public int getGoodsNumber ()
    {
        return number;
    }

    /**
     * 是否可以拾取
     * 
     * @param _playerUserID
     * @return
     */
    public boolean canPick (int _playerUserID)
    {
        return pickerList.contains(_playerUserID);
    }
}
