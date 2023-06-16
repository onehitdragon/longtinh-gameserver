package hero.npc.ai.data;

import hero.fight.message.SpecialStatusChangeNotify;
import hero.fight.service.SpecialStatusDefine;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Monster;
import hero.npc.message.MonsterShoutNotify;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ShoutData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-16 下午05:03:24
 * @描述 ：喊话
 */

public class Shout extends SpecialWisdom
{
    /**
     * 编号
     */
    public int    id;

    /**
     * 喊话的内容
     */
    public String shoutContent;

    @Override
    public byte getType ()
    {
        // TODO Auto-generated method stub
        return SpecialWisdom.SHOUT;
    }

    @Override
    public void think (Monster _dominator)
    {
        // TODO Auto-generated method stub
        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(),
                new MonsterShoutNotify(_dominator.getID(), shoutContent),
                false, 0);
    }
}
