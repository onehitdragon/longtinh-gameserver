package hero.share.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 FullScreenTip.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-27 下午12:34:04
 * @描述 ：全屏提示内容通知
 */

public class FullScreenTip extends AbsResponseMessage
{
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 构造
     * 
     * @param content
     */
    public FullScreenTip(String _title, String _content)
    {
        title = _title;
        content = _content;
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
        yos.writeUTF(title);
        yos.writeUTF(content);
    }
}
