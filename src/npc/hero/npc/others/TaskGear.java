package hero.npc.others;

import hero.npc.dict.NpcImageDict;
import hero.npc.dict.GearDataDict.GearData;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskGear.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-8 下午12:59:57
 * @描述 ：任务机关
 */

public class TaskGear extends ME2OtherGameObject
{
    /**
     * 名称
     */
    private String name;

    /**
     * 对话描述
     */
    private String description;

    /**
     * 操作描述
     */
    private String optionDesc;

    /**
     * 关联的任务编号
     */
    private int    taskIDAbout;

    /**
     * 图片字节
     */
    private byte[] image;

    /**
     * 构造
     * 
     * @param _gearData
     */
    public TaskGear(GearData _gearData)
    {
        super(_gearData.modelID, _gearData.imageID);
        name = _gearData.name;
        description = _gearData.description;
        optionDesc = _gearData.optionDesc;
        taskIDAbout = _gearData.taskID;

        image = NpcImageDict.getInstance().getImageBytes(getImageID());
    }

    /**
     * 获取名称
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 获取关联的任务编号
     * 
     * @return
     */
    public int getTaskIDAbout ()
    {
        return taskIDAbout;
    }

    /**
     * 获取机关对话描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return description;
    }

    /**
     * 获取机关操作描述
     * 
     * @return
     */
    public String getOptionDesc ()
    {
        return optionDesc;
    }

    /**
     * 获取图片
     * 
     * @return
     */
    public byte[] getImage ()
    {
        return image;
    }
}
