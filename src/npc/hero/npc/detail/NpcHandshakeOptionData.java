package hero.npc.detail;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NpcHandshakeOptionData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-19 上午10:45:24
 * @描述 ：NPC第一次交互的选项数据信息
 */

public class NpcHandshakeOptionData
{
    /**
     * 操作功能图标
     */
    public short             miniImageID;

    /**
     * 操作功能标记
     */
    public int               functionMark;

    /**
     * 操作功能描述
     */
    public String            optionDesc;

    /**
     * 后续操作
     */
    public ArrayList<byte[]> followOptionData;
}
