package hero.skill.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeSkillReleaseTime.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-23 下午03:21:11
 * @描述 ：改变特定技能的施法时间
 *     <p>
 *     正值延长时间，负值缩短时间
 *     该报文由于定义上的问题弃用.转而采用RefreshSkillTime.java
 *     </p>
 */

public class ChangeSkillReleaseTime extends AbsResponseMessage
{
    /**
     * 技能编号
     */
    private int   skillID;

    /**
     * 变化的时间（秒）
     */
    private float time;

    /**
     * 构造
     * 
     * @param _skillID 技能编号
     * @param _timeChanged 变化的时间
     */
    public ChangeSkillReleaseTime(int _skillID, float _time)
    {
        skillID = _skillID;
        time = _time;
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
        yos.writeInt(skillID);
        yos.writeInt((int) (time * 1000));
    }

}
