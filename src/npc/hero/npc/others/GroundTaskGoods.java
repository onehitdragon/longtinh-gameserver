package hero.npc.others;

import hero.npc.dict.NpcImageDict;
import hero.npc.dict.GroundTaskGoodsDataDict.GroundTaskGoodsData;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroundTaskGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-11 下午07:02:42
 * @描述 ：任务物品，可拾取与任务相关的物品
 */

public class GroundTaskGoods extends ME2OtherGameObject
{
    /**
     * 名称
     */
    private String    name;

    /**
     * 关联的任务编号
     */
    private int       taskIDAbout;

    /**
     * 关联的任务道具编号
     */
    private int       taskToolIDAbout;

    /**
     * 最后一次消失时间
     */
    private long      lastDisappearTime;

    /**
     * 图片字节
     */
    private byte[]    image;

    /**
     * 所有物品的刷新间隔时间
     * edit by zheng; date: 2011-03-02; note: 刷新时间缩短为 15秒.
     */
    public static int REBIRTH_INTERVAL = 15000;

    /**
     * 构造
     * 
     * @param _data
     */
    public GroundTaskGoods(GroundTaskGoodsData _data)
    {
        super(_data.modelID, _data.imageID);
        // TODO Auto-generated constructor stub
        name = _data.name;
        taskIDAbout = _data.taskID;
        taskToolIDAbout = _data.taskToolID;
        image = NpcImageDict.getInstance().getImageBytes(_data.imageID);
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
     * 获取关联的任务道具编号
     * 
     * @return
     */
    public int getTaskToolIDAbout ()
    {
        return taskToolIDAbout;
    }

    /**
     * 消失
     */
    public void disappear ()
    {
        lastDisappearTime = System.currentTimeMillis();
    }

    /**
     * 获取消失时间
     * 
     * @return
     */
    public long getDisappearTime ()
    {
        return lastDisappearTime;
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
