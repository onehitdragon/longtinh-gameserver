package hero.npc.ai.data;

import hero.npc.Monster;
import hero.share.MoveSpeed;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RunAwayData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-12 下午03:16:08
 * @描述 ：逃跑
 */

public class RunAway extends SpecialWisdom
{
    /**
     * 编号
     */
    public int    id;

    /**
     * 持续时间
     */
    public short  range;

    /**
     * 喊话内容
     */
    public String shoutContent;

    @Override
    public byte getType ()
    {
        // TODO Auto-generated method stub
        return SpecialWisdom.RUN_AWAY;
    }

    @Override
    public void think (Monster _dominator)
    {
        // TODO Auto-generated method stub
        _dominator.setMoveSpeed(MoveSpeed.FASTER);
        _dominator.getAI().walkWhenRunAway();
    }
}
