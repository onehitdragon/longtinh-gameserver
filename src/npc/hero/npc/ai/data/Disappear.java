package hero.npc.ai.data;

import hero.fight.message.SpecialStatusChangeNotify;
import hero.fight.service.SpecialStatusDefine;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Monster;
import hero.npc.message.MonsterDisengageFightNotify;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DissapearData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-12 下午03:11:27
 * @描述 ：消失
 */

public class Disappear extends SpecialWisdom
{
    /**
     * 编号
     */
    public int    id;

    /**
     * 持续时间
     */
    public int    keepTime;

    /**
     * 喊话内容
     */
    public String shoutContent;

    @Override
    public byte getType ()
    {
        // TODO Auto-generated method stub
        return SpecialWisdom.DISAPPEAR;
    }

    @Override
    public void think (Monster _dominator)
    {
        // TODO Auto-generated method stub
        _dominator.disappear();
        _dominator.getAI().traceDisappearTime = keepTime;

        MapSynchronousInfoBroadcast.getInstance().put(
                _dominator.where(),
                new SpecialStatusChangeNotify(_dominator.getObjectType()
                        .value(), _dominator.getID(),
                        SpecialStatusDefine.DISAPPEAR), false, 0);
    }
}
