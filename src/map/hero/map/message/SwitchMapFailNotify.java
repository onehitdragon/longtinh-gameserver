package hero.map.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SwitchMapFailNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-23 下午05:33:09
 * @描述 ：切换地图失败通知
 */

public class SwitchMapFailNotify extends AbsResponseMessage
{
    /**
     * 原因
     */
    private String reason;

    public SwitchMapFailNotify(String _reason)
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
