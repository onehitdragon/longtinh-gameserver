package hero.manufacture.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UpgradeSkillPoint.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-10 下午03:33:07
 * @描述 ：
 */

public class UpgradeSkillPoint extends AbsResponseMessage
{
    /**
     * 当前技能熟练点
     */
    private int skillPoint;

    /**
     * 构造
     * 
     * @param _skillPoint
     */
    public UpgradeSkillPoint(int _skillPoint)
    {
        skillPoint = _skillPoint;
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
        yos.writeInt(skillPoint);
    }

}
