package hero.group.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroupDisbandNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午05:34:44
 * @描述 ：队伍解散通知
 */

public class GroupDisbandNotify extends AbsResponseMessage
{
    /**
     * 原因
     */
    public String reason;

    /**
     * 构造
     * 
     * @param _reason
     */
    public GroupDisbandNotify(String _reason)
    {
        reason = _reason;
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
        yos.writeUTF(reason);
    }

}
