package hero.map.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NotifyEnterMap.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-23 下午12:05:18
 * @描述 ：
 */

public class NotifyEnterMap extends AbsResponseMessage
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
