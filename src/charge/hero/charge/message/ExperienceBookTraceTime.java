package hero.charge.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExperienceBookTraceTime.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-10 上午11:32:58
 * @描述 ：改变经验书剩余时间（包括在线经验书、上次离线累计剩余时间）
 */

public class ExperienceBookTraceTime extends AbsResponseMessage
{
    /**
     * 剩余上次离线时间
     */
    private long traceTimeOfOffline;

    /**
     * 剩余经验书时间
     */
    private long traceExperienceBookTime;

    /**
     * 剩余狩猎经验书时间
     */
    private long traceHuntExperienceBookTime;

    /**
     * 构造
     * 
     * @param _traceTimeOfOffline
     * @param _traceExperienceBookTime
     */
    public ExperienceBookTraceTime(long _traceTimeOfOffline,
            long _traceExperienceBookTime, long _traceHuntExperienceBookTime)
    {
        traceTimeOfOffline = _traceTimeOfOffline;
        traceExperienceBookTime = _traceExperienceBookTime;
        traceHuntExperienceBookTime = _traceHuntExperienceBookTime;
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
        yos.writeLong(traceTimeOfOffline);
        yos.writeLong(traceExperienceBookTime);
        yos.writeLong(traceHuntExperienceBookTime);
//        output.writeUTF("双倍经验");//暂时写死
    }
}
