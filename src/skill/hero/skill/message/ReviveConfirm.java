package hero.skill.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ReviveNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-5-28 下午01:52:38
 * @描述 ：被别人复活的确认通知
 */

public class ReviveConfirm extends AbsResponseMessage
{
    /**
     * 释放名字
     */
    private String releaseName;

    /**
     * ｘ坐标
     */
    private short  x;

    /**
     * y坐标
     */
    private short  y;

    /**
     * 恢复血
     */
    private int    hp;

    /**
     * 恢复魔
     */
    private int    mp;
    
    private short  countDown;

    /**
     * 构造
     * 
     * @param _releaseName 释放者名字
     * @param _x ｘ坐标
     * @param _y y坐标
     * @param _hp 恢复血
     * @param _mp 恢复魔
     */
    public ReviveConfirm(String _releaseName, short _x, short _y, int _hp,
            int _mp, short _countDown)
    {
        releaseName = _releaseName;
        x = _x;
        y = _y;
        hp = _hp;
        mp = _mp;
        countDown = _countDown;
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
        // 0x1008
        yos.writeUTF(releaseName);
        yos.writeByte(x);
        yos.writeByte(y);
        yos.writeInt(hp);
        yos.writeInt(mp);
        yos.writeShort(countDown);
    }
}
