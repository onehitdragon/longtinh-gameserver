package hero.share.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MailStatusChanges.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-11 下午03:10:53
 * @描述 ：邮箱状态变化通知
 */

public class MailStatusChanges extends AbsResponseMessage
{
    /**
     * 类型
     */
    private byte    type;

    /**
     * 是否标记
     */
    private boolean existsMark;

    /**
     * <p>构造</p>
     * 邮箱状态变化通知
     * 
     * @param _existsMail
     */
    public MailStatusChanges(byte _type, boolean _existsMark)
    {
        type = _type;
        existsMark = _existsMark;
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
        yos.writeByte(type);
        yos.writeByte(existsMark);
    }

    /**
     * 信件
     */
    public static byte TYPE_OF_LETTER   = 1;

    /**
     * 邮箱
     */
    public static byte TYPE_OF_POST_BOX = 2;
}
