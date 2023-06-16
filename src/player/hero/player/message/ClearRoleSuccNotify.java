package hero.player.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ClearRoleSuccNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-1 上午10:04:45
 * @描述 ：
 */

public class ClearRoleSuccNotify extends AbsResponseMessage
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
