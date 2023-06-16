package hero.guild.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildDisbandNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-12 下午04:41:28
 * @描述 ：公会解散通知
 */

public class GuildDisbandNotify extends AbsResponseMessage
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
