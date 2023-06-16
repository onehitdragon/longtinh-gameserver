package hero.group.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ReduceMemberNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午06:15:48
 * @描述 ：成员减少通知
 */

public class ReduceMemberNotify extends AbsResponseMessage
{
    /**
     * 成员编号
     */
    private int memberUserID;

    /**
     * @param _memberUserID
     */
    public ReduceMemberNotify(int _memberUserID)
    {
        memberUserID = _memberUserID;
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
        yos.writeInt(memberUserID);
    }

}
