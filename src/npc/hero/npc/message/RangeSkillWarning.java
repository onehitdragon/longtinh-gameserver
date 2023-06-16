package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RangeSkillWarning.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-25 上午11:03:17
 * @描述 ：
 */

public class RangeSkillWarning extends AbsResponseMessage
{
    /**
     * 区域的X方向长度
     */
    private int     xLength;

    /**
     * 区域的Y方向长度
     */
    private int     yLength;

    /**
     * 是否是移动的范围
     */
    private boolean mobile;

    /**
     * 固定区域左上X坐标
     */
    private int     fixedRangeUpperLeftX;

    /**
     * 固定区域左上Y坐标
     */
    private int     fixedRangeUpperLeftY;

    /**
     * 移动区域中心目标的编号
     */
    private int     mobielRangeCenterTargetID;

    /**
     * 移动区域中心目标对象的类型
     */
    private int     mobielRangeCenterTargetType;

    /**
     * 构造
     * 
     * @param _xLength 区域X长度
     * @param _yLength 区域Y长度
     * @param _mobile 是否是跟随目标移动的区域
     * @param centerTargetType 移动区域中心目标对象的类型
     * @param centerTargetID 移动区域中心目标的编号
     * @param rangeUpperLeftX 固定区域左上X坐标
     * @param rangeUpperLeftY 固定区域左上Y坐标
     */
    public RangeSkillWarning(int _xLength, int _yLength, boolean _mobile,
            int _mobielRangeCenterTargetType, int _mobielRangeCenterTargetID,
            int _fixedRangeUpperLeftX, int _fixedRangeUpperLeftY)
    {

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
        yos.writeByte(xLength);
        yos.writeByte(yLength);
        yos.writeByte(mobile);

        if (mobile)
        {
            yos.writeByte(mobielRangeCenterTargetType);
            yos.writeByte(mobielRangeCenterTargetID);
        }
        else
        {
            yos.writeByte(fixedRangeUpperLeftX);
            yos.writeByte(fixedRangeUpperLeftY);
        }
    }

}
