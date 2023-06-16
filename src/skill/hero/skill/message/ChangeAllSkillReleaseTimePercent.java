package hero.skill.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeAllSkillReleaseTime.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-23 下午02:46:33
 * @描述 ：改变所有技能施法时间的百分比
 *     <p>
 *     正值延长时间，负值缩短时间
 *     </p>
 */

public class ChangeAllSkillReleaseTimePercent extends AbsResponseMessage
{
    /**
     * 百分比分子
     */
    private byte numerator;

    /**
     * 构造
     * 
     * @param _percent
     */
    public ChangeAllSkillReleaseTimePercent(float _percent)
    {
        numerator = (byte) (_percent * 100);
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
        yos.writeByte(numerator);
    }
}
