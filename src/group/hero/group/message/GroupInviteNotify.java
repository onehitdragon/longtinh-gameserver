package hero.group.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroupInviteNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午05:34:44
 * @描述 ：加入队伍的邀请通知
 */

public class GroupInviteNotify extends AbsResponseMessage
{
    /**
     * 邀请者的名字
     */
    private String invitorName;

    /**
     * 构造
     * 
     * @param _invitorName 邀请者的名字
     */
    public GroupInviteNotify(String _invitorName)
    {
        invitorName = _invitorName;
    }

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
        yos.writeUTF(invitorName);
    }
}
