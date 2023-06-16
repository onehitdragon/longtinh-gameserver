package hero.map.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Door.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-30 下午03:14:58
 * @描述 ：去往其他地图的切换门
 */

public class Door implements Cloneable
{
    /**
     * 是否出现
     */
    public boolean visible;

    /**
     * 地图切换点X、Y坐标
     */
    public short   x, y;

    /**
     * 目的地图相对方向
     */
    public byte    direction;

    /**
     * 目的地图编号
     */
    public short   targetMapID;

    /**
     * 目的地图名称
     */
    public String  targetMapName;

    /**
     * 出现在目的地图上的X、Y坐标
     */
    public short   targetMapX, targetMapY;

    /**
     * 关联的怪物模板编号（如果地图上存在此编号的怪物，则此跳转点不出现在客户端，反之）
     */
    public String  monsterIDAbout;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Door clone () throws CloneNotSupportedException
    {
        return (Door) super.clone();
    }
}
