package hero.ui.service;

import yoyo.service.base.AbsServiceAdaptor;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UIService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-11 上午11:28:47
 * @描述 ：
 */

public class UIServiceImpl extends AbsServiceAdaptor<UIConfig>
{
    private static UIServiceImpl instance = null;

    private UIServiceImpl()
    {
        config = new UIConfig();
    }

    public static UIServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new UIServiceImpl();
        }

        return instance;
    }
}
