package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PopHornInputUINotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-30 上午10:02:01
 * @描述 ：通知客户端弹出世界号角输入界面
 */

public class PopHornInputUINotify extends AbsResponseMessage
{
    /**
     * 喇叭在背包中的位置（快捷键使用-1）
     */
    private int hornGridIndex;

    /**
     * 构造
     * 
     * @param _hornGridIndex
     */
    public PopHornInputUINotify(int _hornGridIndex)
    {
        hornGridIndex = _hornGridIndex;
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
        yos.writeByte(hornGridIndex);
    }

}
