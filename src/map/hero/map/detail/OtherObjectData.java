package hero.map.detail;

import hero.map.Decorater;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OtherObjectData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-30 下午04:00:42
 * @描述 ：非玩家信息
 */

public class OtherObjectData
{
    /**
     * 非玩家对象模板编号
     */
    public String   modelID;

    /**
     * 非玩家对象在地图上的原始X、Y、Z坐标
     */
    public byte     x, y,z;

    /**
     * 移动路径
     */
    public byte[][] movePath;
    
    /**
     * 动画id
     */
    public short animationID;
    
    /**
     * add by zhengl
     * 可选字段.
     * 该对象图片编号
     */
    public short pngId;
    
    /**
     * 装饰层上动画
     */
    public Decorater[] decorater;
}
