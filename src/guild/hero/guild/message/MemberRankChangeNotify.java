package hero.guild.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MemberRankChangeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-12 下午16:23:33
 * @描述 ：公会成员等级变化通知
 */

public class MemberRankChangeNotify extends AbsResponseMessage
{
    /**
     * 等级值
     */
    private byte rankValue;

    /**
     * 构造
     * 
     * @param _rankValue
     */
    public MemberRankChangeNotify(byte _rankValue)
    {
        rankValue = _rankValue;
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
        yos.writeByte(rankValue);
    }
}
