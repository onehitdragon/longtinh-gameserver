package hero.chat.service;

import hero.player.define.EClan;
import hero.chat.core.MsgItem;
import hero.chat.message.ChatResponse;
import hero.chat.message.GetGoodsNofity;
//import hero.gm.EResponseType;
//import hero.gm.ResponseToGmTool;
//import hero.gm.service.GmServiceImpl;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.guild.Guild;
import hero.guild.GuildMemberProxy;
import hero.guild.service.GuildServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.ME2ObjectList;
import hero.share.service.Tip;
import hero.social.service.SocialServiceImpl;

import java.util.ArrayList;
import java.util.Iterator;

import yoyo.core.queue.ResponseMessageQueue;

import javolution.util.FastList;

/**
 * 聊天消息管理 Description:
 * 
 * @author Luke
 * @version 0.1
 */
public class ChatQueue
{
    private static ChatQueue queue = null;

    public static ChatQueue getInstance ()
    {
        if (queue == null)
            queue = new ChatQueue();
        return queue;
    }

    private ChatQueue()
    {
        new Task().start();
    }

    ArrayList<MsgItem> messageList = new ArrayList<MsgItem>();

    /**
     * 任务队列
     * 
     * @author Luke 陈路
     * @date Oct 29, 2009
     */
    class Task extends Thread
    {
        public void run ()
        {
            while (true)
            {
                try
                {
                    flush();
                }
                catch (Exception e)
                {
                    LogWriter.error(this, e);
                }

                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void flush ()
    {
        synchronized (messageList)
        {
            Iterator<MsgItem> iterator = messageList.iterator();
            while (iterator.hasNext())
            {
                MsgItem item = iterator.next();
                iterator.remove();

                switch (item.type)
                {
                    /**
                     * 阵营聊天
                     */
                    case ChatServiceImpl.CLAN:
                    {
                        FastList<HeroPlayer> list = PlayerServiceImpl
                                .getInstance().getPlayerList();
                        HeroPlayer player;

                        for (int i = 0; i < list.size(); i++)
                        {
                            try
                            {
                                player = list.get(i);
                            }
                            catch (Exception e)
                            {
                                break;
                            }

                            if (player != null && player.isEnable()
                                    && item.clan == player.getClan().getID() && player.openClanChat)
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ChatResponse(item.type,
                                                item.srcName, item.content));
                            }
                        }

                        /**
                         * 推送聊天信息，插入队列
                         */
//                        ResponseToGmTool rtgt = new ResponseToGmTool(
//                                EResponseType.SEND_CHAT_CONTENT, 0);
//                        rtgt.setChatContent(ChatChanle.WORLD, "[阵营_"
//                                + EClan.getClan(item.clan).getDesc() + "] "
//                                + item.srcName + " : " + item.content);
//                        GmServiceImpl.addGmToolMsg(rtgt);
                    }
                        break;

                    /**
                     * 世界聊天 + 系统世界公告
                     */
                    case ChatServiceImpl.PLAYER_WORLD:
                    {
                        FastList<HeroPlayer> list = PlayerServiceImpl
                                .getInstance().getPlayerList();
                        HeroPlayer player;

                        synchronized (list)
                        {
                            for (int i = 0; i < list.size(); i++)
                            {
                                player = list.get(i);

                                if (player != null && player.isEnable() && player.openWorldChat)
                                {
                                    ResponseMessageQueue.getInstance()
                                            .put(
                                                    player.getMsgQueueIndex(),
                                                    new ChatResponse(item.type,
                                                            item.srcName,
                                                            item.content,item.showMiddle));
                                }
                            }
                        }
                        break;
                    }
                    case ChatServiceImpl.TOP_SYSTEM_WORLD:
                    {
                        FastList<HeroPlayer> list = PlayerServiceImpl
                                .getInstance().getPlayerList();
                        HeroPlayer player;

                        synchronized (list)
                        {
                            for (int i = 0; i < list.size(); i++)
                            {
                                player = list.get(i);

                                if (player != null && player.isEnable())
                                {
                                    ResponseMessageQueue.getInstance()
                                            .put(
                                                    player.getMsgQueueIndex(),
                                                    new ChatResponse(item.type,
                                                            item.srcName,
                                                            item.content,item.showMiddle));
                                }
                            }
                        }

                        if (item.type == ChatServiceImpl.PLAYER_WORLD)
                        {
                            /**
                             * 推送聊天信息，压入队列
                             */
//                            ResponseToGmTool rtgt = new ResponseToGmTool(
//                                    EResponseType.SEND_CHAT_CONTENT, 0);
//                            rtgt.setChatContent(ChatChanle.WORLD, "[世界] "
//                                    + item.srcName + " : " + item.content);
//                            GmServiceImpl.addGmToolMsg(rtgt);
                        }
                        else if (item.type == ChatServiceImpl.TOP_SYSTEM_WORLD)
                        {
                            /**
                             * 推送聊天信息，插入队列
                             */
//                            ResponseToGmTool rtgt = new ResponseToGmTool(
//                                    EResponseType.SEND_CHAT_CONTENT, 0);
//                            rtgt
//                                    .setChatContent(ChatChanle.NOTICE,
//                                            item.content);
//                            GmServiceImpl.addGmToolMsg(rtgt);
                        }
                    }
                        break;

                    /**
                     * 玩家私聊
                     */
                    case ChatServiceImpl.PLAYER_SINGLE:
                    {
                        if (item.target != null && item.target.isEnable() && item.target.openSingleChat)
                        {
                            ResponseMessageQueue.getInstance().put(
                                    item.target.getMsgQueueIndex(),
                                    new ChatResponse(item.type, item.srcName,
                                            item.destName, item.content));

                            if (item.target.getName().equals(item.srcName))
                            {
                                /**
                                 * 推送聊天信息，插入队列
                                 */
//                                ResponseToGmTool rtgt = new ResponseToGmTool(
//                                        EResponseType.SEND_CHAT_CONTENT, 0);
//                                rtgt.setChatContent(ChatChanle.OTHER, "[私聊] "
//                                        + item.srcName + " to " + item.destName
//                                        + " : " + item.content);
//                                GmServiceImpl.addGmToolMsg(rtgt);
                            }
                        }

                        break;
                    }
                        /**
                         * 地图聊天
                         */
                    case ChatServiceImpl.PLAYER_MAP:
                    {
                        HeroPlayer speaker = PlayerServiceImpl.getInstance()
                                .getPlayerByName(item.srcName);

                        if (speaker != null && speaker.isEnable())
                        {
                            ME2ObjectList list = speaker.where()
                                    .getPlayerList();
                            HeroPlayer destPlayer = null;

                            Iterator<ME2GameObject> iteraor = list.iterator();

                            while (iteraor.hasNext())
                            {
                                destPlayer = (HeroPlayer) iteraor.next();

                                if (destPlayer.isEnable() && destPlayer.openMapChat)
                                {
                                    if (!SocialServiceImpl.getInstance()
                                            .beBlack(speaker.getUserID(),
                                                    destPlayer.getUserID()))
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                destPlayer.getMsgQueueIndex(),
                                                new ChatResponse(item.type,
                                                        item.srcName,
                                                        item.content));
                                    }
                                }
                            }

                            /**
                             * 推送聊天信息，插入队列
                             */
//                            ResponseToGmTool rtgt = new ResponseToGmTool(
//                                    EResponseType.SEND_CHAT_CONTENT, 0);
//                            rtgt.setChatContent(ChatChanle.MAP, "[地图] "
//                                    + item.srcName + " : " + item.content);
//                            GmServiceImpl.addGmToolMsg(rtgt);
                        }
                    }
                        break;

                    /**
                     * 队伍聊天
                     */
                    case ChatServiceImpl.PLAYER_GROUP:
                    {
                        HeroPlayer speaker = PlayerServiceImpl.getInstance()
                                .getPlayerByName(item.srcName);

                        if (speaker != null && speaker.isEnable())
                        {
                            Group group = GroupServiceImpl.getInstance()
                                    .getGroup(speaker.getGroupID());

                            if (group != null)
                            {
                                HeroPlayer player = null;
                                ArrayList<HeroPlayer> list = group
                                        .getPlayerList();
                                for (int i = 0; i < list.size(); i++)
                                {
                                    player = list.get(i);

                                    if (player != null && player.isEnable())
                                    {
                                        if (!SocialServiceImpl.getInstance()
                                                .beBlack(speaker.getUserID(),
                                                        player.getUserID()))
                                        {
                                            ResponseMessageQueue.getInstance().put(
                                                    player.getMsgQueueIndex(),
                                                    new ChatResponse(item.type,
                                                            item.srcName,
                                                            item.content));
                                        }
                                    }
                                }

                                /**
                                 * 推送聊天信息，插入队列
                                 */
//                                ResponseToGmTool rtgt = new ResponseToGmTool(
//                                        EResponseType.SEND_CHAT_CONTENT, 0);
//                                rtgt.setChatContent(ChatChanle.OTHER, "[队伍] "
//                                        + item.srcName + " : " + item.content);
//                                GmServiceImpl.addGmToolMsg(rtgt);
                            }
                            else if (group == null)
                            {
                                ResponseMessageQueue.getInstance().put(
                                        speaker.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_CHAT_OF_NOT_TEAM, Warning.UI_STRING_TIP));
                            }

                        }
                    }
                        break;

                    /**
                     * 公会聊天
                     */
                    case ChatServiceImpl.PLAYER_GUILD:
                    {
                        HeroPlayer speaker = PlayerServiceImpl.getInstance()
                                .getPlayerByName(item.srcName);

                        Guild guild = GuildServiceImpl.getInstance().getGuild(
                                speaker.getGuildID());
                        if (guild != null)
                        {
                            ArrayList<GuildMemberProxy> list = guild
                                    .getMemberList();
                            GuildMemberProxy guildMember;
                            HeroPlayer player;

                            for (int i = 0; i < list.size(); i++)
                            {
                                guildMember = list.get(i);

                                if (guildMember != null && guildMember.isOnline)
                                {
                                    player = PlayerServiceImpl.getInstance()
                                            .getPlayerByUserID(
                                                    guildMember.userID);

                                    if (null != player && player.isEnable())
                                    {
                                        if (!SocialServiceImpl.getInstance()
                                                .beBlack(speaker.getUserID(),
                                                        guildMember.userID))
                                        {
                                            ResponseMessageQueue.getInstance().put(
                                                    player.getMsgQueueIndex(),
                                                    new ChatResponse(item.type,
                                                            item.srcName,
                                                            item.content));
                                        }
                                    }
                                }
                            }

                            /**
                             * 推送聊天信息，插入队列
                             */
//                            ResponseToGmTool rtgt = new ResponseToGmTool(
//                                    EResponseType.SEND_CHAT_CONTENT, 0);
//                            rtgt.setChatContent(ChatChanle.OTHER, "[公会] "
//                                    + item.srcName + " : " + item.content);
//                            GmServiceImpl.addGmToolMsg(rtgt);
                        }
                        else if (guild == null)
                        {
                            ResponseMessageQueue.getInstance().put(
                                    speaker.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_CHAT_OF_NOT_HAVE_GUILD, Warning.UI_STRING_TIP));
                        }
                    }
                        break;

                    /**
                     * 队伍公告
                     */
                    case ChatServiceImpl.BOTTOM_SYSTEM_GROUP:
                    {
                        Group group = GroupServiceImpl.getInstance().getGroup(
                                item.groupID);

                        if (group != null)
                        {
                            HeroPlayer player = null;
                            ArrayList<HeroPlayer> list = group.getPlayerList();
                            for (int i = 0; i < list.size(); i++)
                            {
                                player = list.get(i);
                                if (player != null && player.isEnable())
                                    ResponseMessageQueue.getInstance()
                                            .put(
                                                    player.getMsgQueueIndex(),
                                                    new ChatResponse(item.type,
                                                            item.srcName,
                                                            item.content));
                            }

                            /**
                             * 推送聊天信息，插入队列
                             */
//                            ResponseToGmTool rtgt = new ResponseToGmTool(
//                                    EResponseType.SEND_CHAT_CONTENT, 0);
//                            rtgt.setChatContent(ChatChanle.NOTICE, "[队伍公告]  : "
//                                    + item.content);
//                            GmServiceImpl.addGmToolMsg(rtgt);
                        }
                    }
                        break;

                    /**
                     * 公会公告
                     */
                    case ChatServiceImpl.BOTTOM_SYSTEM_GUILD:
                    {
                        Guild guild = GuildServiceImpl.getInstance().getGuild(
                                item.guildID);

                        if (guild != null)
                        {
                            ArrayList<GuildMemberProxy> list = guild
                                    .getMemberList();
                            GuildMemberProxy guildMember;
                            HeroPlayer player;

                            for (int i = 0; i < list.size(); i++)
                            {
                                guildMember = list.get(i);

                                player = PlayerServiceImpl.getInstance()
                                        .getPlayerByUserID(guildMember.userID);

                                if (null != player && player.isEnable())
                                {
                                    ResponseMessageQueue.getInstance()
                                            .put(
                                                    player.getMsgQueueIndex(),
                                                    new ChatResponse(item.type,
                                                            item.srcName,
                                                            item.content));
                                }
                            }

                            /**
                             * 推送聊天信息，插入队列
                             */
//                            ResponseToGmTool rtgt = new ResponseToGmTool(
//                                    EResponseType.SEND_CHAT_CONTENT, 0);
//                            rtgt.setChatContent(ChatChanle.NOTICE, "[公会公告]  : "
//                                    + item.content);
//                            GmServiceImpl.addGmToolMsg(rtgt);
                        }
                    }
                        break;
                    case ChatServiceImpl.TOP_SYSTEM_SINGLE:
                    {
                        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(item.destName);
                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ChatResponse(item.type,
                                        item.srcName,
                                        item.content,item.showMiddle));
                    }
                    	break;
                }
            }
        }
    }

    /**
     * 添加一条聊天信息 阵营
     * 
     * @param _type
     * @param _srcName
     * @param _destName
     * @param _target
     * @param _content
     */
    public void add (byte _type, HeroPlayer _speaker, String _destName,
            HeroPlayer _target, String _content, short _clan)
    {
        MsgItem item = new MsgItem();
        item.type = _type;
        item.srcName = _speaker.getName();
        item.destName = _destName;
        item.target = _target;
        item.content = _content;
        item.clan = _clan;

        synchronized (messageList)
        {
            messageList.add(item);
        }
    }

    /**
     * 添加一条聊天信息 普通
     * 
     * @param _type
     * @param _speaker
     * @param _destName
     * @param _target
     * @param _content
     */
    public void add (byte _type, HeroPlayer _speaker, String _destName,
            HeroPlayer _target, String _content){
    	add(_type,_speaker,_destName,_target,_content,false);
    }
    public void add (byte _type, HeroPlayer _speaker, String _destName,
            HeroPlayer _target, String _content, boolean showMiddle)
    {
        MsgItem item = new MsgItem();
        item.type = _type;
        if (null != _speaker)
            item.srcName = _speaker.getName();
        else
            item.srcName = "";

        item.destName = _destName;
        item.target = _target;
        item.content = _content;
        item.clan = -1;
        item.showMiddle = showMiddle;

        synchronized (messageList)
        {
            messageList.add(item);
        }
    }
    
    /**
     * GM 发公告
     * @param _type
     * @param _speakerName
     * @param _destName
     * @param _target
     * @param _content
     * @param showMiddle
     */
    public void add (byte _type, String _speakerName,String _content, boolean showMiddle)
    {
        MsgItem item = new MsgItem();
        item.type = _type;
        item.srcName = _speakerName;

        item.content = _content;
        item.clan = -1;
        item.showMiddle = showMiddle;

        synchronized (messageList)
        {
            messageList.add(item);
        }
    }

    /**
     * 添加一条聊天信息 GM工具世界发话
     * 
     * @param _type
     * @param _srcName
     * @param _content
     */
    public void add (byte _type, String _srcName, String _content)
    {
        MsgItem item = new MsgItem();
        item.type = _type;
        item.srcName = _srcName;
        item.destName = "";
        item.target = null;
        item.content = _content;
        item.clan = -1;

        synchronized (messageList)
        {
            messageList.add(item);
        }
    }

    /**
     * 添加获得物品信息
     * 
     * @param _target
     * @param _content
     * @param _name
     * @param _rbg
     * @param _num
     */
    public void addGoodsMsg (HeroPlayer _target, String _content, String _name,
            int _traitRGB, int _num)
    {
        if (_target.isEnable())
        {
            ResponseMessageQueue.getInstance().put(_target.getMsgQueueIndex(),
                    new GetGoodsNofity(_content, _name, _traitRGB, _num));
        }
    }

    /**
     * 添加获得物品信息
     * 
     * @param _target
     * @param _msg
     */
    public void addGoodsMsg (HeroPlayer _target, GetGoodsNofity _msg)
    {
        if (_target.isEnable())
        {
            ResponseMessageQueue.getInstance().put(_target.getMsgQueueIndex(), _msg);
        }
    }

    /**
     * 添加队伍公告
     * 
     * @param _groupID
     * @param _content
     */
    public void addGroupSys (int _groupID, String _content)
    {
        MsgItem item = new MsgItem();
        item.type = ChatServiceImpl.BOTTOM_SYSTEM_GROUP;
        item.groupID = _groupID;
        item.content = _content;

        synchronized (messageList)
        {
            messageList.add(item);
        }
    }

    /**
     * 添加公会公告
     * 
     * @param _guildID
     * @param _content
     */
    public void addGuildSys (int _guildID, String _content)
    {
        MsgItem item = new MsgItem();
        item.type = ChatServiceImpl.BOTTOM_SYSTEM_GUILD;
        item.guildID = _guildID;
        item.content = _content;

        synchronized (messageList)
        {
            messageList.add(item);
        }
    }
}
