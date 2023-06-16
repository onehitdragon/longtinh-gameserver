package hero.map.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Cartoon.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-30 下午03:11:30
 * @描述 ：动画数据
 */

public class Cartoon
{
    /**
     * 动画X、Y坐标
     */
    public byte   x, y;

    /**
     * 第一帧切片索引
     */
    public byte   firstTileIndex;

    /**
     * 后续帧切片索引列表
     */
    public byte[] followTileIndexList;
}
