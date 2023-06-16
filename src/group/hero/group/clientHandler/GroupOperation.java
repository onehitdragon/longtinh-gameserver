package hero.group.clientHandler;

import java.io.IOException;

import hero.group.Group;
import hero.group.message.GroupInviteNotify;
import hero.group.service.GroupServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.social.service.SocialServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroupOperation.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午05:07:13
 * @描述 ：队伍操作
 */

public class GroupOperation extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(GroupOperation.class);

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        if (null != player)
        {
            try
            {
                byte operateType = yis.readByte();

                switch (operateType)
                {
                    case OPERATION_OF_ADD_MEMBER:
                    {
                        String name = yis.readUTF();
                        log.debug("加人入队伍： " + name);

                        if(player.getName().equals(name)){
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_GROUP_NOT_MYSELF));
                            return;
                        }

                        boolean canInvite = false;

                        HeroPlayer target = PlayerServiceImpl.getInstance()
                                .getPlayerByName(name);

                        if (target != null)
                        {

                            if (!SocialServiceImpl.getInstance().beBlack(
                                    player.getUserID(), target.getUserID()))
                            {
                                log.debug(name + " groupid = " + target.getGroupID());
                                if (target.getGroupID() == 0)
                                {
                                    if (target.getClan() != player.getClan())
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                new Warning(
                                                        Tip.TIP_GROUP_OF_DIFFERENT_CLAN));
                                    }
                                    else
                                    {
                                        if (player.isDead())
                                        {
                                            ResponseMessageQueue
                                                    .getInstance()
                                                    .put(
                                                            player
                                                                    .getMsgQueueIndex(),
                                                            new Warning(
                                                                    Tip.TIP_GROUP_OF_TARGET_IS_DEAD));
                                        }
                                        else
                                        {
                                            canInvite = true;

                                        }
                                    }
                                }
                                else
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(name
                                                    + Tip.TIP_GROUP_OF_TARGET_IN_GROUP));
                                }
                            }
                            else
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_GROUP_OF_BE_BLACK));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_GROUP_OF_TARGET_OFFLINE));
                        }

                        if (player.getGroupID() > 0)
                        {
                            Group group = GroupServiceImpl.getInstance()
                                    .getGroup(player.getGroupID());

                            if (null != group
                                    && group.getMemberNumber() >= Group.MAX_NUMBER_OF_MEMBER)
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_GROUP_OF_GROUP_FULL));

                            }

                            if(null != group && !GroupServiceImpl.getInstance().canInvite(group,player.getUserID())){
                                 ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_GROUP_OF_NOT_GRANT));

                            }

                        }

                        if(canInvite){
                            ResponseMessageQueue.getInstance().put(
                                                    target.getMsgQueueIndex(),
                                                    new GroupInviteNotify(
                                                            player.getName()));
                        }else {
                            return;
                        }

                        break;
                    }
                    case OPERATION_OF_REMOVE_MEMBER:
                    {
                        int memberUserID = yis.readInt();

                        GroupServiceImpl.getInstance().removeMember(player,
                                memberUserID);

                        break;
                    }
                    case OPERATION_OF_UP_TO_ASSISTANT:
                    {
                        int memberUserID = yis.readInt();

                        GroupServiceImpl.getInstance().addAssistant(player,
                                memberUserID);

                        break;
                    }
                    case OPERATION_OF_DOWN_TO_NORMAL:
                    {
                        int memberUserID = yis.readInt();

                        GroupServiceImpl.getInstance().removeAssistant(player,
                                memberUserID);

                        break;
                    }
                    case OPERATION_OF_CHANGE_SUB_GROUP:
                    {
                        int memberUserID = yis.readInt();
                        byte subGroupID = yis.readByte();
                        log.debug("改变队员位置： "  + subGroupID);
                        GroupServiceImpl.getInstance().changeSubGroup(player,
                                memberUserID, subGroupID);

                        break;
                    }
                    case OPERATION_OF_LEFT_GROUP:
                    {
                        GroupServiceImpl.getInstance().leftGroup(player);

                        break;
                    }
                    case OPERATION_OF_TRANSFER_LEADER:
                    {
                        int newLeaderUserID = yis.readInt();

                        GroupServiceImpl.getInstance().changeGroupLeader(
                                player, newLeaderUserID);

                        break;
                    }
                    case OPERATION_OF_CONFIRM_INVITOR:
                    {
                        byte answer = yis.readByte();
                        String invitorName = yis.readUTF();

                        HeroPlayer invitor = PlayerServiceImpl.getInstance()
                                .getPlayerByName(invitorName);

                        if (null == invitor || !invitor.isEnable())
                        {
                            if (answer != 0)
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_GROUP_OF_INVITOR_OFFLINE));
                            }

                            return;
                        }

                        if (invitor.getGroupID() > 0)
                        {
                            Group group = GroupServiceImpl.getInstance()
                                    .getGroup(invitor.getGroupID());

                            if (null != group){
                                if(group.getMemberNumber() >= Group.MAX_NUMBER_OF_MEMBER)
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(Tip.TIP_GROUP_OF_GROUP_FULL));

                                    return;
                                }

                               /* if(!GroupServiceImpl.getInstance().canInvite(group,player.getUserID())){
                                     OutMsgQ.getInstance().put(
                                            invitor.getMsgQueueIndex(),
                                            new Warning(TIP_OF_NOT_GRANT));

                                    return;
                                }*/
                            }
                        }

                        if (0 != player.getGroupID())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_GROUP_OF_MYSELF_IN_GROUP));

                            /*if (null != invitor)
                                OutMsgQ.getInstance().put(
                                        invitor.getMsgQueueIndex(),
                                        new Warning(player.getName()
                                                + TIP_OF_TARGET_IN_GROUP));*/
                        }

                        if (answer != 0)
                        {
                            if (player.getGroupID() == 0)
                            {
                                if (invitor.getClan() != player.getClan())
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(Tip.TIP_GROUP_OF_DIFFERENT_CLAN));
                                }
                                else
                                {
                                    if (invitor.getGroupID() != 0)
                                    {
                                        GroupServiceImpl.getInstance().add(
                                                player, invitor.getGroupID());
                                    }
                                    else
                                    {
                                        GroupServiceImpl.getInstance()
                                                .createGroup(invitor, player);
                                    }
                                }
                            }
                            else
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_GROUP_OF_MYSELF_IN_GROUP));

                                ResponseMessageQueue.getInstance().put(
                                        invitor.getMsgQueueIndex(),
                                        new Warning(player.getName()
                                                + Tip.TIP_GROUP_OF_TARGET_IN_GROUP));
                            }
                        }
                        else
                        {
                            if (invitor != null && player != null)
                                ResponseMessageQueue.getInstance().put(
                                        invitor.getMsgQueueIndex(),
                                        new Warning(player.getName()
                                                + Tip.TIP_GROUP_OF_REFUSE_INVITE));
                        }

                        break;
                    }
                }
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 操作-加人入队伍
     */
    private static final byte   OPERATION_OF_ADD_MEMBER       = 1;

    /**
     * 操作-回去队伍邀请
     */
    private static final byte   OPERATION_OF_CONFIRM_INVITOR  = 2;

    /**
     * 操作-提升助手
     */
    private static final byte   OPERATION_OF_UP_TO_ASSISTANT  = 3;

    /**
     * 操作-降为普通成员
     */
    private static final byte   OPERATION_OF_DOWN_TO_NORMAL   = 4;

    /**
     * 操作-转让队长
     */
    private static final byte   OPERATION_OF_TRANSFER_LEADER  = 5;

    /**
     * 操作-改变成员小队
     */
    private static final byte   OPERATION_OF_CHANGE_SUB_GROUP = 6;

    /**
     * 操作-移除队员
     */
    private static final byte   OPERATION_OF_REMOVE_MEMBER    = 7;

    /**
     * 操作-离开队伍
     */
    private static final byte   OPERATION_OF_LEFT_GROUP       = 8;


}
