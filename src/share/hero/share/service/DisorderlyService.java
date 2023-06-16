package hero.share.service;

import hero.item.service.WeaponRankManager;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DisorderlyService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-9 上午09:54:31
 * @描述 ：无序服务，主要用于在游戏服务最后加载、启动那些有服务依赖关系的数据或模块
 */

public class DisorderlyService
{
    /**
     * 单例
     */
    private static DisorderlyService instance;

    /**
     * 私有构造
     */
    private DisorderlyService()
    {

    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static DisorderlyService getInstance ()
    {
        if (null == instance)
        {
            instance = new DisorderlyService();
        }

        return instance;
    }

    /**
     * 加载
     */
    public void start ()
    {
        WeaponRankManager.getInstance();
    }
}
