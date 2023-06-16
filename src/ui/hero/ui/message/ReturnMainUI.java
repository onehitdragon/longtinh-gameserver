package hero.ui.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ReturnMainUI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-6 下午04:43:27
 * @描述 ：
 */

public class ReturnMainUI extends AbsResponseMessage
{
    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub

    }
}
