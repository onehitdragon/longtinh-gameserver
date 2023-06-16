package hero.npc.others;

import hero.map.Map;
import hero.share.service.IDManager;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ME2OtherGameObject.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-11 下午03:13:00
 * @描述 ：
 */

public abstract class ME2OtherGameObject
{
    /**
     * 动态编号
     */
    private int    id;

    /**
     * 模型编号
     */
    private String modelID;

    /**
     * 所在地图
     */
    protected Map  where;

    /**
     * 所在位置
     */
    private short  cellX, cellY;

    /**
     * 图片编号
     */
    private short  imageID;

    /**
     * 构造
     */
    public ME2OtherGameObject(String _modelID, short _imageID)
    {
        id = IDManager.buildObjectID();
        modelID = _modelID;
        imageID = _imageID;
    }

    /**
     * 构造
     */
    public ME2OtherGameObject(String _modelID)
    {
        id = IDManager.buildObjectID();
        modelID = _modelID;
    }

    /**
     * 获取编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    /**
     * 获取模型编号
     * 
     * @return
     */
    public String getModelID ()
    {
        return modelID;
    }

    /**
     * 在哪里
     * 
     * @return
     */
    public Map where ()
    {
        return where;
    }

    /**
     * 居住在
     * 
     * @param _map
     */
    public void live (Map _map)
    {
        where = _map;
    }

    /**
     * 获取当前位置X坐标
     * 
     * @return
     */
    public short getCellX ()
    {
        return cellX;
    }

    /**
     * 设置当前位置X坐标
     * 
     * @param _x
     */
    public void setCellX (short _x)
    {
        cellX = _x;
    }

    /**
     * 设置当前位置Y坐标
     * 
     * @return
     */
    public short getCellY ()
    {
        return cellY;
    }

    /**
     * 设置当前位置X坐标
     * 
     * @param _y
     */
    public void setCellY (short _y)
    {
        cellY = _y;
    }

    /**
     * 获取图片编号
     * 
     * @return
     */
    public short getImageID ()
    {
        return imageID;
    }
}
