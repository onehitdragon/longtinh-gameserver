package hero.map;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Area.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-20 下午05:49:39
 * @描述 ：区域，内包含地图若干
 */

public class Area
{
    /**
     * 编号
     */
    private int                 id;

    /**
     * 名称
     */
    private String              name;

    /**
     * 图片字节数据
     */
    private byte[]              imageBytes;

    /**
     * 内辖可查看的地图信息(地图编号：地图在区域中的位置X、Y坐标)
     */
    private FastMap<Map, int[]> innerVisibleMapTable;

    /**
     * 内辖可查看的地图
     */
    private FastList<Map>       innerVisibleList;

    /**
     * 内辖不可查看的地图
     */
    private FastList<Map>       innerUnvisibleList;

    /**
     * 构造
     * 
     * @param _id 编号
     * @param _name 名称
     * @param _imageBytes 微缩图片字节数组
     */
    public Area(int _id, String _name, byte[] _imageBytes)
    {
        id = _id;
        name = _name;
        imageBytes = _imageBytes;
        innerVisibleMapTable = new FastMap<Map, int[]>();
        innerVisibleList = new FastList<Map>();
        innerUnvisibleList = new FastList<Map>();
    }

    /**
     * 向区域中添加地图
     * 
     * @param _map 地图
     * @param _visible 在区域微缩地图中是否可见
     * @param _locationX 在区域图片中的X坐标
     * @param _locationY 在区域图片中的Y坐标
     */
    public void add (Map _map, boolean _visible, int _locationX, int _locationY)
    {
        if (_visible)
        {
            int insertIndex = 0;

            int[] location;

            for (; insertIndex < innerVisibleList.size(); insertIndex++)
            {
                location = innerVisibleMapTable.get(innerVisibleList
                        .get(insertIndex));

                if (_locationY < location[1])
                {
                    break;
                }
                else if (_locationY == location[1])
                {
                    if (_locationX < location[0])
                    {
                        break;
                    }
                }
            }

            innerVisibleList.add(insertIndex, _map);
            innerVisibleMapTable.put(_map, new int[]{_locationX, _locationY });
        }
        else
        {
            innerUnvisibleList.add(_map);

        }

        _map.setArea(this);
    }

    /**
     * 获取区域可见地图列表
     * 
     * @return
     */
    public FastMap<Map, int[]> getVisibleMapTable ()
    {
        return innerVisibleMapTable;
    }

    /**
     * 获取区域可见地图信息
     * 
     * @return
     */
    public FastList<Map> getVisibleMapList ()
    {
        return innerVisibleList;
    }

    /**
     * 获取区域编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    /**
     * 获取区域名
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 获取区域图片字节数组
     * 
     * @return
     */
    public byte[] getImageBytes ()
    {
        return imageBytes;
    }
}
