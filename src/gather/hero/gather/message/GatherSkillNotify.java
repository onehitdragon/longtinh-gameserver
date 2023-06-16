package hero.gather.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GatherSkillNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-16 下午03:34:18
 * @描述 ：
 */

public class GatherSkillNotify extends AbsResponseMessage
{
    /**
     * 采集技能变化（true:增加 false:遗忘）
     */
    private boolean changesOfGatherSkill;

    public GatherSkillNotify(boolean _changesOfGatherSkill)
    {
        changesOfGatherSkill = _changesOfGatherSkill;
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
        yos.writeByte(changesOfGatherSkill);
    }

}
