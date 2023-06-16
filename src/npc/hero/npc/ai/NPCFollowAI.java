package hero.npc.ai;

import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Npc;
import hero.npc.message.NpcResetNotify;
import hero.npc.service.AStar;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.share.service.ThreadPoolFactory;

import java.util.concurrent.Future;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NPCFollowAI.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-23 下午03:20:46
 * @描述 ：
 */

public class NPCFollowAI implements Runnable
{
    private Future     followAITask;

    /**
     * AI拥有者
     */
    private Npc        actor;

    /**
     * 跟随目标
     */
    private HeroPlayer followTarget;

    /**
     * 构造
     * 
     * @param _master
     */
    public NPCFollowAI(Npc _actor)
    {
        actor = _actor;
    }

    /**
     * 开始跟随
     */
    public void startFollow (HeroPlayer _followTarget)
    {
        if (followAITask == null)
        {
            followTarget = _followTarget;
            followAITask = ThreadPoolFactory.getInstance().excuteAI(this, 100,
                    1000);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run ()
    {
        if (null != followTarget && followTarget.isEnable())
        {
            if (actor.where() == followTarget.where())
            {
                short targetLocationX = followTarget.getCellX();
                short targetLocationY = followTarget.getCellY();

                /*int distance = (int) Math.sqrt(Math.pow(
                        (actor.getCellX() - targetLocationX) * 16, 2)
                        + Math
                                .pow((actor.getCellY() - targetLocationY) * 16,
                                        2));*/
//                if (distance > NotPlayerServiceImpl.getInstance().getConfig().NPC_FOLLOW_GRID_DISTANCE_OF_TARGET * 16)
                int npc_follow_grid_distance_of_target = NotPlayerServiceImpl.getInstance().getConfig().NPC_FOLLOW_GRID_DISTANCE_OF_TARGET * 16;
//                            * (NotPlayerServiceImpl.getInstance().getConfig().NPC_FOLLOW_GRID_DISTANCE_OF_TARGET * 16);

                boolean inDistance = ((actor.getCellX() - targetLocationX) * 16)*((actor.getCellX() - targetLocationX) * 16)
                            + ((actor.getCellY() - targetLocationY) * 16)*((actor.getCellY() - targetLocationY) * 16)
                                > npc_follow_grid_distance_of_target * npc_follow_grid_distance_of_target;


                if(inDistance)
                {
                    byte[] path = AStar
                            .getPath(
                                    actor.getCellX(),
                                    actor.getCellY(),
                                    targetLocationX,
                                    targetLocationY,	
                                    NotPlayerServiceImpl.getInstance()
                                            .getConfig().NPC_FOLLOW_MOST_FAST_GRID,
                                    NotPlayerServiceImpl.getInstance()
                                            .getConfig().NPC_FOLLOW_GRID_DISTANCE_OF_TARGET,
                                    actor.where());

                    if (null != path)
                    {
                        if (path.length > 1
                                || (path.length == 1 && path[0] > 0))
                        {
                            actor.goAlone(path, null);
                        }
                    }
                }
            }
        }
        else
        {
            actor.stopFollowTask();

            actor.setCellX(actor.getOrgX());
            actor.setCellY(actor.getOrgY());

            if (actor.where() != actor.getOrgMap())
            {
                actor.gotoMap(actor.getOrgMap());
            }
            else
            {
                MapSynchronousInfoBroadcast.getInstance().put(
                        actor.where(),
                        new NpcResetNotify(actor.getID(), actor.getCellX(),
                                actor.getCellY()), false, 0);
            }
        }
    }

    /**
     * 停止AI
     */
    public void stopFollow ()
    {
        if (null != followAITask)
        {
            followAITask.cancel(true);
            followAITask = null;
        }

        followTarget = null;
    }

    /**
     * 获取跟随目标
     * 
     * @return 跟随目标
     */
    public HeroPlayer getFollowTarget ()
    {
        return followTarget;
    }
}
