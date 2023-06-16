package hero.duel.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.duel.Duel;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.duel.message.ResponseDuel;
import hero.duel.service.DuelServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.social.SocialRelationList;
import hero.social.service.SocialServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Duel.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2008-12-30 上午11:41:36
 * @描述 ：决斗请求，包括：邀请、确认、拒绝、其他原因不接受
 */

public class DuleRequest extends AbsClientProcess
{
    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte requestType = yis.readByte();

        switch (requestType)
        {
            case TYPE_OF_INVITE:
            {
                if (null == player || !player.isEnable() || player.isDead())
                {
                    return;
                }

                if (DuelServiceImpl.getInstance().isDueling(player.getUserID()))
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_DUEL_OF_NOT_MORE));

                    return;
                }

                int targetObjectID = yis.readInt();

                HeroPlayer target = (HeroPlayer) player.where().getPlayer(
                        targetObjectID);

                if (target == null || !target.isEnable() || target.isDead())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_DUEL_OF_INVALIDATE_TARGET));

                    return;
                }
                else
                {
                    //add by zhengl; date: 2011-02-27; note: 玩家正忙
                    if (target.isInFighting())
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_PUBLIC_BUSY));
                        return;
                    }
                    if (DuelServiceImpl.getInstance().isDueling(
                            target.getUserID()))
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_DUEL_OF_INVALIDATE_TARGET));

                        return;
                    }

                    if (!SocialServiceImpl.getInstance().beBlack(
                            player.getUserID(), target.getUserID()))
                    {
                        if (DuelServiceImpl.getInstance().inviteDuel(player,
                                target))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    target.getMsgQueueIndex(),
                                    new ResponseDuel(player.getID(), player
                                            .getName()));
                        }
                        else
                        {
                            ResponseMessageQueue
                                    .getInstance()
                                    .put(
                                            player.getMsgQueueIndex(),
                                            new Warning(
                                                    Tip.TIP_DUEL_OF_TWO_SIDE_LOCATION_INVALIDATE));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_DUEL_OF_BE_BLACK));
                    }
                }

                break;
            }
            case TYPE_OF_ACCEPT:
            {
                if (null == player)
                {
                    return;
                }

                if (!player.isEnable() || player.isDead())
                {
                    DuelServiceImpl.getInstance().removeByOneSide(
                            player.getUserID());

                    return;
                }

                int invitorObjectID = yis.readInt();

                HeroPlayer invitor = player.where().getPlayer(invitorObjectID);

                if (invitor == null || !invitor.isEnable() || invitor.isDead())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_DUEL_OF_INVALIDATE_TARGET));

                    DuelServiceImpl.getInstance().removeByOneSide(
                            player.getUserID());

                    return;
                }
                else
                {
                    Duel duel = DuelServiceImpl.getInstance().getDuelByOneSide(
                            player.getUserID());

                    if (null != duel)
                    {
                        if (player.where().getID() != duel.duleMapID)
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_DUEL_OF_MYSELF_LEFT_SCENE));
                            ResponseMessageQueue.getInstance().put(
                                    invitor.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_DUEL_OF_OTHER_SIDE_LEFT_SCENE));
                        }
                        else if (invitor.where().getID() != duel.duleMapID)
                        {
                            DuelServiceImpl.getInstance().removeByOneSide(
                                    player.getUserID());

                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_DUEL_OF_OTHER_SIDE_LEFT_SCENE));
                            ResponseMessageQueue.getInstance().put(
                                    invitor.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_DUEL_OF_MYSELF_LEFT_SCENE));
                        }
                        else
                        {
                            duel.isConfirming = false;
                            player.startDuel(invitor.getUserID());
                            invitor.startDuel(player.getUserID());
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new ResponseDuel(invitor.getID()));
                            ResponseMessageQueue.getInstance().put(
                                    invitor.getMsgQueueIndex(),
                                    new ResponseDuel(player.getID()));
                        }
                    }
                }

                break;
            }
            case TYPE_OF_REFUSE:
            {
                DuelServiceImpl.getInstance().removeByOneSide(
                        player.getUserID());

                int invitorObjectID = yis.readInt();

                HeroPlayer invitor = (HeroPlayer) player.where().getPlayer(
                        invitorObjectID);

                if (null != invitor && invitor.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(
                            invitor.getMsgQueueIndex(),
                            new Warning(player.getName()
                                    + Tip.TIP_DUEL_OF_OTHER_SIDE_REFUSE));
                }

                break;
            }
            case TYPE_OF_BUSY:
            {
                DuelServiceImpl.getInstance().removeByOneSide(
                        player.getUserID());

                int invitorObjectID = yis.readInt();
                HeroPlayer invitor = (HeroPlayer) player.where().getPlayer(
                        invitorObjectID);

                if (null != invitor && invitor.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(
                            invitor.getMsgQueueIndex(),
                            new Warning(player.getName()
                                    + Tip.TIP_DUEL_OF_OTHER_SIDE_IS_BUSY));
                }

                break;
            }
        }
    }

    /**
     * 请求类型-邀请
     */
    private static final byte   TYPE_OF_INVITE                      = 1;

    /**
     * 请求类型-接受
     */
    private static final byte   TYPE_OF_ACCEPT                      = 2;

    /**
     * 请求类型-拒绝
     */
    private static final byte   TYPE_OF_REFUSE                      = 3;

    /**
     * 请求类型-忙
     */
    private static final byte   TYPE_OF_BUSY                        = 4;




}
