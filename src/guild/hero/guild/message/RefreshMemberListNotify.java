package hero.guild.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RefreshMemberListNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-12 上午11:54:34
 * @描述 ：通知打开着公会列表的客户端来刷新会员列表
 */

public class RefreshMemberListNotify extends AbsResponseMessage
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
