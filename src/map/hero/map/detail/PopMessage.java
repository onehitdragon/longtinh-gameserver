package hero.map.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PopMessage.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-30 下午03:20:07
 * @描述 ：弹出消息
 */

public class PopMessage
{
    /**
     * 消息位置X、Y坐标
     */
    public byte   x, y;

    /**
     * 消息内容
     */
    public String msgContent;

    /**
     * 最小间隔弹出时间（毫秒）
     */
    public long   minIntervalTime;
}
