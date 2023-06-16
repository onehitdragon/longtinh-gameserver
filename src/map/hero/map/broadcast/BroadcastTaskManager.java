package hero.map.broadcast;

import hero.fight.broadcast.HpChangeBroadcast;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 BroadcastTaskManager.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-9-24 上午09:34:24
 * @描述 ：地图模型数据
 */

public class BroadcastTaskManager
{
    /**
     * 单例
     */
    private static BroadcastTaskManager instance;

    /**
     * 构造
     */
    private BroadcastTaskManager()
    {
        new Thread(new HpChangeBroadcast()).start();
        new Thread(MapSynchronousInfoBroadcast.getInstance()).start();

        LogWriter.println("广播任务管理器已启动");
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static BroadcastTaskManager getInstance ()
    {
        if (null == instance)
        {
            instance = new BroadcastTaskManager();
        }

        return instance;
    }
}
