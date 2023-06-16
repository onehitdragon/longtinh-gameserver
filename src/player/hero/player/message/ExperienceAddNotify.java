package hero.player.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExperienceAddNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-22 下午02:52:18
 * @描述 ：
 */

public class ExperienceAddNotify extends AbsResponseMessage
{
    /**
     * 获得的经验值
     */
    private int experience;

    /**
     * 客户端提示绘制位置0：不绘制，1：主界面飘出，2：中间红字
     */
    private int drawLocation;

    /**
     * 当前经验
     */
    private int currentExp;
    /**
     * 当前用于UI展示的经验
     */
    private int currentExpShow;

    /**
     * @param _experience 经验
     * @param _drawLocation 客户端提示绘制位置0：不绘制，1：主界面飘出，2：中间红字
     */
    public ExperienceAddNotify(int _experience, int _drawLocation,
            int _currentExp, int _currentExpShow)
    {
        experience = _experience;
        drawLocation = _drawLocation;
        currentExp = _currentExp;
        currentExpShow = _currentExpShow;
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
        yos.writeInt(experience);
        yos.writeInt(currentExp);
        yos.writeInt(currentExpShow);
    }

}
