package hero.fight.broadcast;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.group.service.GroupServiceImpl;
import hero.fight.HpChange;
import hero.fight.message.HpRefreshNotify;
import hero.map.broadcast.IBroadcastThread;
import hero.map.broadcast.ME2ArrayList;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.share.service.ME2ObjectList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 HpChangeBroadcast.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-6 下午01:46:25
 * @描述 ：生命值变化广播
 */

public class HpChangeBroadcast extends IBroadcastThread
{
    /**
     * 生命值变化信息对象容器
     */
    private static ME2ArrayList list = new ME2ArrayList();

    @Override
    public void broadcast ()
    {
        // TODO Auto-generated method stub
        int nums = list.size();

        HpChange hpOrMpChange;

        for (int j = 0; j < nums; j++)
        {
            try
            {
                hpOrMpChange = (HpChange) list.get(j);
                ME2ObjectList mapPlayerList = hpOrMpChange.where
                        .getPlayerList();
                AbsResponseMessage message = new HpRefreshNotify(
                        hpOrMpChange.changerObjectType, hpOrMpChange.changerID,
                        hpOrMpChange.currentHp, hpOrMpChange.changeHpValue,
                        false, false);

                HeroPlayer player = null;

                for (int i = 0; i < mapPlayerList.size(); i++)
                {
                    player = (HeroPlayer) mapPlayerList.get(i);

                    if (player.getID() == hpOrMpChange.changerID
                            || player.getID() == hpOrMpChange.triggerID)
                    {
                        continue;
                    }

                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            message);

                    GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);
                }

                player = null;
                hpOrMpChange = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        list.remove(0, nums);
    }

    @Override
    public long getExcuteInterval ()
    {
        // TODO Auto-generated method stub
        return 0X000096;
    }

    @Override
    public long getStartTime ()
    {
        // TODO Auto-generated method stub
        return 0X002710;
    }

    /**
     * 向容器中添加变化涉及到的元素
     * 
     * @param _s 引起生命值变化的对象
     * @param _t 生命值变化对象
     * @param _change 生命值变化数值
     */
    public static void put (ME2GameObject _s, ME2GameObject _t, int _change)
    {
        HpChange change = new HpChange();

        change.triggerID = _s.getID();
        change.changerID = _t.getID();
        change.changerObjectType = _t.getObjectType().value();
        change.changerVocationType = _t.getVocation().getType();
        change.currentHp = _t.getHp();
        change.changeHpValue = _change;
        change.where = _t.where();

        list.add(change);
    }
}
