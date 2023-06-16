package hero.guild.clientHandler;

import hero.guild.message.GuildInviteNotify;
import hero.guild.EGuildMemberRank;
import hero.guild.Guild;
import hero.guild.GuildMemberProxy;
import hero.guild.message.ResponseMemberList;
import hero.guild.service.GuildServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.social.service.SocialServiceImpl;
import java.io.IOException;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildOperation.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 下午16:40:33
 * @描述 ：
 */

public class GuildOperation extends AbsClientProcess
{
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte operation = yis.readByte();

            switch (operation)
            {
                case OPERATION_OF_VIEW_LIST:
                {
                    Guild guild = GuildServiceImpl.getInstance().getGuild(
                            player.getGuildID());

                    if (guild != null)
                    {
                        GuildMemberProxy guildMemberProxy = guild
                                .getMember(player.getUserID());

                        if (null != guildMemberProxy)
                        {
                            byte page = yis.readByte();

                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new ResponseMemberList(
                                    		guild.getMemberList(page),
                                            guildMemberProxy.memberRank, 
                                            page,
                                            guild.getViewPageNumber(), 
                                            guild.getMemberNumber(),
                                            guild.GetMaxMemberNumber(), guild));
                        }
                    }

                    break;
                }
                case OPERATION_OF_ADD_MEMBER:
                {
                    Guild guild = GuildServiceImpl.getInstance().getGuild(
                            player.getGuildID());

                    if (guild == null)
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_NONE_GUILD));

                        return;
                    }

                    GuildMemberProxy guildMemberProxy = guild.getMember(player
                            .getUserID());

                    if (null == guildMemberProxy
                            || guildMemberProxy.memberRank == EGuildMemberRank.NORMAL)
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_NONE_RANK));

                        return;
                    }

                    String name = yis.readUTF();

                    HeroPlayer otherPlayer = PlayerServiceImpl.getInstance()
                            .getPlayerByName(name);
                    //add by zhengl; date: 2011-02-27; note: 战斗状态忽略其他玩家打扰.
                    if (otherPlayer.isInFighting())
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_PUBLIC_BUSY));

                        return;
                    }
                    //edit by zhengl; date: 2011-04-29; note:人数上限添加
                	if (guild.getMemberList().size() >= guild.GetMaxMemberNumber()) 
                	{
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_MAX_SIZE));

                        return;
        			}
                    if (otherPlayer.getGuildID() > 0)
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_IN_GUILD));

                        return;
                    }
                    if (otherPlayer == null || !otherPlayer.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_TARGET_OFFLINE));

                        return;
                    }

                    if (otherPlayer.getClan() != player.getClan())
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_DEFFERENT_CLAN));

                        return;
                    }

                    if (SocialServiceImpl.getInstance().beBlack(
                            player.getUserID(), otherPlayer.getUserID()))
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_IN_BLACK));

                        return;
                    }

                    if (otherPlayer.getGuildID() > 0)
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_IN_GUILD));

                        return;
                    }

                    ResponseMessageQueue.getInstance()
                            .put(
                                    otherPlayer.getMsgQueueIndex(),
                                    new GuildInviteNotify(player.getUserID(),
                                            player.getName(), guild.getID(),
                                            guild.getName()));

                    break;
                }
                case OPERATION_OF_REMOVE_MEMBER:
                {
                    int userIDWillBeRemove = yis.readInt();

                    GuildServiceImpl.getInstance().removeMember(player,
                            userIDWillBeRemove);

                    break;
                }
                case OPERATION_OF_CHANGE_RANK:
                {
                    byte rankValue = yis.readByte();
                    int memberUserID = yis.readInt();

                    GuildServiceImpl.getInstance().changeMemberRank(player,
                            memberUserID, EGuildMemberRank.getRank(rankValue));

                    break;
                }
                case OPERATION_OF_LEFT_GUILD:
                {
                    GuildServiceImpl.getInstance().leftGuild(player);

                    break;
                }
                case OPERATION_OF_CONFIRM_INVITE:
                {
                    byte receiveOrRefuse = yis.readByte();
                    int invitorUserID = yis.readInt();
                    int guildID = yis.readInt();

                    HeroPlayer invitor = PlayerServiceImpl.getInstance()
                            .getPlayerByUserID(invitorUserID);

                    if (receiveOrRefuse == 0)
                    {
                        if (null != invitor && invitor.isEnable())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    invitor.getMsgQueueIndex(),
                                    new Warning(player.getName()
                                            + Tip.TIP_GUILD_OF_REFUCE_JOIN_GUILD));
                        }

                        return;
                    }

                    if (null == invitor || !invitor.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GUILD_OF_INVITOR_OFFLINE));

                        return;
                    }

                    GuildServiceImpl.getInstance()
                            .add(invitor, player, guildID);

                    break;
                }
                case OPERATION_OF_GUILD_UP:
                {
                    GuildServiceImpl.getInstance().GuildUp(player);

                    break;
                }
                case OPERATION_OF_GUILD_SEE:
                {
                	GuildServiceImpl.getInstance().SeeGuildInfo(player);
                	
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

    /**
     * 操作-查看列表
     */
    private static final byte   OPERATION_OF_VIEW_LIST      = 1;

    /**
     * 加人入会
     */
    private static final byte   OPERATION_OF_ADD_MEMBER     = 2;

    /**
     * 移除会员
     */
    private static final byte   OPERATION_OF_REMOVE_MEMBER  = 3;

    /**
     * 改变成员等级
     */
    private static final byte   OPERATION_OF_CHANGE_RANK    = 4;

    /**
     * 离开公会
     */
    private static final byte   OPERATION_OF_LEFT_GUILD     = 5;

    /**
     * 回复公会邀请
     */
    private static final byte   OPERATION_OF_CONFIRM_INVITE = 6;
    /**
     * 公会升级请求
     */
    private static final byte   OPERATION_OF_GUILD_UP       = 7;
    /**
     * 公会信息查看请求
     */
    private static final byte   OPERATION_OF_GUILD_SEE      = 8;








}
