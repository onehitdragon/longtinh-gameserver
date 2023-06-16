package hero.player.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MoneyChange.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-22 下午02:50:37
 * @描述 ：
 */

public class MoneyChangeNofity extends AbsResponseMessage
{
    /**
     * 游戏钱币数量，负数为减少
     */
    private int money;

    /**
     * 客户端提示绘制位置0：不绘制，1：主界面飘出，2：中间红字
     */
    private int drawLocation;

    /**
     * @param _money 金钱
     * @param _drawLocation 客户端提示绘制位置0：不绘制，1：主界面飘出，2：中间红字
     */
    public MoneyChangeNofity(int _money, int _drawLocation)
    {
        money = _money;
        drawLocation = _drawLocation;
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
        yos.writeByte(drawLocation);
        yos.writeInt(money);
    }

}
