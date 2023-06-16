package hero.fight.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 FightStatusChangeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-3 下午07:16:31
 * @描述 ：战斗状态变化通知
 */

public class FightStatusChangeNotify extends AbsResponseMessage
{
    /**
     * 在战斗中
     */
    private boolean isInFightStatus;

    /**
     * 构造
     * 
     * @param _inFightStatus 战斗状态
     */
    public FightStatusChangeNotify(boolean _inFightStatus)
    {
        isInFightStatus = _inFightStatus;
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
        yos.writeByte(isInFightStatus);
    }
}
