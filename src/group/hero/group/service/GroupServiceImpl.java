package hero.group.service;

import hero.dungeon.service.DungeonServiceImpl;
import hero.dungeon.service.TransmitterOfDungeon;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.MpRefreshNotify;
import hero.group.Group;
import hero.group.GroupMemberProxy;
import hero.group.EGroupMemberRank;
import hero.group.message.*;
import hero.lover.service.LoverServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.PlayerRefreshNotify;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.EVocation;
import hero.share.EVocationType;
import hero.share.message.Warning;
import hero.share.service.IDManager;
import hero.share.service.Tip;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroupServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午05:06:22
 * @描述 ：队伍服务
 */

public class GroupServiceImpl extends AbsServiceAdaptor<GroupServerConfig>
{
    private static Logger log = Logger.getLogger(GroupServiceImpl.class);
    /**
     * 单例
     */
    private static GroupServiceImpl instance;

    /**
     * 队伍列表
     */
    private HashMap<Integer, Group> groupTable = new HashMap<Integer, Group>();

    /**
     * 获取单例
     * 
     * @return
     */
    public static GroupServiceImpl getInstance ()
    {
        if (instance == null)
            instance = new GroupServiceImpl();
        return instance;
    }

    /**
     * 私有构造
     */
    private GroupServiceImpl()
    {
        config = new GroupServerConfig();
    }

    /**
     * 刷新成员等级
     * 
     * @param _player
     */
    public void refreshMemberLevel (HeroPlayer _player)
    {
        if (_player.getGroupID() > 0)
        {
            sendGroupMsg(getGroup(_player.getGroupID()),
                    new MemberChangerNotify(
                            MemberChangerNotify.CHANGER_OF_LEVEL, _player
                                    .getUserID(), _player.getLevel()));
        }
    }

    /**
     * 刷新成员职业
     * 
     * @param _player
     */
    public void refreshMemberVocation (HeroPlayer _player)
    {
        if (_player.getGroupID() > 0)
        {
            sendGroupMsg(
                    getGroup(_player.getGroupID()),
                    new MemberChangerNotify(
                            MemberChangerNotify.CHANGER_OF_VOCATION, _player
                                    .getUserID(), _player.getVocation().value()));
        }
    }

    /**
     * 获得队伍对象
     * 
     * @param _groupID 队伍ID
     * @return
     */
    public Group getGroup (int _groupID)
    {
        if (_groupID <= 0)
        {
            return null;
        }

        return groupTable.get(_groupID);
    }

    /**
     * 创建队伍
     * 
     * @param _leader 创建者
     * @param _acceptor 被邀请者
     */
    public void createGroup (HeroPlayer _leader, HeroPlayer _acceptor)
    {
        Group group = new Group(IDManager.buildGroupID(), _leader, _acceptor);

        groupTable.put(group.getID(), group);

        ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                new GroupMemberListNotify(group));
        ResponseMessageQueue.getInstance().put(_acceptor.getMsgQueueIndex(),
                new GroupMemberListNotify(group));

        if ((LoverServiceImpl.getInstance().anotherInTream(_leader.getName(),
                group.getPlayerList())) != null)
        {
            group.light++;
        }

        if (group.light > 0)
        {
            /*
             * try { BufDebuf bufdebuf = null;
             * EffectServiceImpl.getInstance().addPlayerEffect(_leader, bufdebuf =
             * new LoveBuf(group.getID())); bufdebuf.action((ME2GameObject)
             * _leader, null);
             * EffectServiceImpl.getInstance().addPlayerEffect(_acceptor,
             * bufdebuf = new LoveBuf(group.getID()));
             * bufdebuf.action((ME2GameObject) _acceptor, null);
             * EffectServiceImpl.getInstance().addPlayerEffect(_leader, bufdebuf =
             * new HomeBuf(group.getID())); bufdebuf.action((ME2GameObject)
             * _leader, null);
             * EffectServiceImpl.getInstance().addPlayerEffect(_acceptor,
             * bufdebuf = new HomeBuf(group.getID()));
             * bufdebuf.action((ME2GameObject) _acceptor, null); } catch
             * (EffectException e) { e.printStackTrace(); }
             */
        }
    }

    /**
     * 添加一个玩家入队伍
     * 
     * @param _player 添加的一个玩家
     * @param _groupID 加入队伍的ID
     * @return
     */
    public void add (HeroPlayer _player, int _groupID)
    {
        Group group = groupTable.get(_groupID);

        if (null != group)
        {
            if (group.getMemberNumber() < Group.MAX_NUMBER_OF_MEMBER)
            {
                GroupMemberProxy member = group.add(_player);

                if (null != member)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new GroupMemberListNotify(group));

                    sendGroupMsg(group, new AddMemberNotify(member), _player
                            .getUserID());

                    String[] family = null;

                    if ((family = LoverServiceImpl.getInstance()
                            .anotherInTream(_player.getName(),
                                    group.getPlayerList())) != null)
                    {
                        group.light++;
                    }

                    if (group.light > 0)
                    {
                        /*
                         * try { BufDebuf bufdebuf = null; List<Effect>
                         * effectList = EffectServiceImpl
                         * .getInstance().getEffect(_player); if (null !=
                         * effectList) { effectList .add(bufdebuf = new
                         * LoveBuf(group.getID()));
                         * bufdebuf.action((ME2GameObject) _player, null); if
                         * (family != null) { for (int i = 0; i < family.length;
                         * i++) { HeroPlayer p = PlayerServiceImpl
                         * .getInstance().getPlayerByName( family[i]);
                         * EffectServiceImpl.getInstance()
                         * .getPlayerEffect(p.getUserID());
                         * bufdebuf.action((ME2GameObject) p, null); } } } }
                         * catch (EffectException e) { e.printStackTrace(); }
                         */
                    }
                }
            }
        }
    }

    /**
     * 是否可以邀请玩家加入队伍
     * 只有队长或助手可以邀请
     * @param group
     * @param _invitorID
     * @return
     */
    public boolean canInvite(Group group, int _invitorID){
        GroupMemberProxy member = group.getMember(_invitorID);
        if(null != member){
            return member.memberRank == EGroupMemberRank.ASSISTANT || member.memberRank == EGroupMemberRank.LEADER;
        }
        return false;
    }

    /**
     * 移除队伍中的一个玩家
     * 
     * @param _player 删除一个玩家
     * @param _groupID 队伍的ID
     * @return
     */
    public void removeMember (HeroPlayer _groupManager, int _memberUserID)
    {
        try{
        Group group = groupTable.get(_groupManager.getGroupID());

        if (null != group)
        {
            GroupMemberProxy member = group.remove(_memberUserID);
            log.debug("removed member after groupid = " + member.player.getGroupID());
            if (null == member)
            {
                return;
            }
            log.debug("remove member username = " + member.player.getName());
            if (LoverServiceImpl.getInstance().anotherInTream(
                    member.player.getName(), group.getPlayerList()) != null)
            {
                group.light--;
            }

            if (member.isOnline())
            {
                ResponseMessageQueue.getInstance().put(member.player.getMsgQueueIndex(),
                        new GroupDisbandNotify("你被移出队伍"));
                TransmitterOfDungeon.getInstance().add(member.player);
            }

            if (group.getMemberNumber() == 1)
            {
                GroupMemberProxy lastMember = group.getMemberList().get(0);
                TransmitterOfDungeon.getInstance().add(lastMember.player);

                if (lastMember.isOnline())
                {
                    ResponseMessageQueue.getInstance().put(
                            lastMember.player.getMsgQueueIndex(),
                            new GroupDisbandNotify("队伍已解散"));
                }

                group.destory();
                groupTable.remove(group.getID());
            }
            else
            {
                sendGroupMsg(group, new ReduceMemberNotify(_memberUserID));
            }
        }
        }catch (Exception e){
            log.error("移除队伍里的玩家 error: ",e);
        }
    }

    /**
     * 玩家离开队伍
     * 
     * @param _player 离开队伍的玩家
     * @return
     */
    public void leftGroup (HeroPlayer _player)
    {
        try{
        Group group = groupTable.get(_player.getGroupID());

        if (null != group)
        {
            if (LoverServiceImpl.getInstance().anotherInTream(
                    _player.getName(), group.getPlayerList()) != null)
            {
                group.light--;
            }

            GroupMemberProxy member = group.memberLeft(_player.getUserID());

            if (member != null)
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new GroupDisbandNotify("你离开了队伍"));

                TransmitterOfDungeon.getInstance().add(_player);

                if (group.getMemberNumber() == 1)
                {
                    GroupMemberProxy lastMember = group.getMemberList().get(0);
                    TransmitterOfDungeon.getInstance().add(lastMember.player);

                    if (lastMember.isOnline())
                    {
                        ResponseMessageQueue.getInstance().put(
                                lastMember.player.getMsgQueueIndex(),
                                new GroupDisbandNotify("队伍已解散"));
                    }

                    group.destory();
                    groupTable.remove(group.getID());
                }
                else
                {
                    sendGroupMsg(group, new ReduceMemberNotify(_player
                            .getUserID()));

                    if (member.memberRank == EGroupMemberRank.LEADER)
                    {
                        sendGroupMsg(group, new MemberChangerNotify(
                                MemberChangerNotify.CHANGER_OF_RANK, group
                                        .getLeader().player.getUserID(),
                                EGroupMemberRank.LEADER.value()));
                    }
                }
            }
        }
        }catch (Exception e){
            log.error("玩家离开队伍 error: ",e);
        }
    }

    /**
     * 玩家上线
     * 
     * @param _player 上线玩家
     */
    public void login (HeroPlayer _player)
    {
        if (_player.getGroupID() != 0)
        {
            Group group = groupTable.get(_player.getGroupID());

            if (group != null)
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new GroupMemberListNotify(group));
            }
            else
            {
                _player.setGroupID(0);
            }
        }
    }

    /**
     * 转让队长
     * 
     * @param _leader
     * @param _newLeaderUserID
     * @param _groupID
     */
    public void changeGroupLeader (HeroPlayer _leader, int _newLeaderUserID)
    {
        Group group = groupTable.get(_leader.getGroupID());

        GroupMemberProxy newLeader = group.getMember(_newLeaderUserID);

        if (null != newLeader)
        {
            if (!newLeader.isOnline())
            {
                ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(),
                        new Warning(Tip.TIP_GROUP_OF_HER_LEAVE));

                return;
            }

            group.transferLeader(newLeader);

            sendGroupMsg(group, new MemberChangerNotify(
                    MemberChangerNotify.CHANGER_OF_RANK, _newLeaderUserID,
                    EGroupMemberRank.LEADER.value()));

            sendGroupMsg(group, new MemberChangerNotify(
                    MemberChangerNotify.CHANGER_OF_RANK, _leader.getUserID(),
                    EGroupMemberRank.NORMAL.value()));
        }
    }

    /**
     * 任命助手（针对普通队员）
     * 
     * @param _leader
     * @param _memberUserID
     */
    public void addAssistant (HeroPlayer _leader, int _memberUserID)
    {
        Group group = groupTable.get(_leader.getGroupID());

        if (null != group)
        {
            log.debug("任命助手"+_memberUserID+"，current assistantnum = " + group.assistantNum);
            if(group.assistantNum < Group.ASSISTANT_MAX_NUM){
                GroupMemberProxy member = group.addAssistant(_memberUserID);

                if (null != member){
                    log.debug("任命助手:"+member.player.getName());
                    sendGroupMsg(group, new MemberChangerNotify(
                            MemberChangerNotify.CHANGER_OF_RANK, member.player
                                    .getUserID(), EGroupMemberRank.ASSISTANT
                                    .value()));
                }
            }else{
                ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), 
                		new Warning(Tip.TIP_GROUP_OF_AIDE_LIST_FULL));
            }
        }
    }

    /**
     * 解除助手
     * 
     * @param _assistantUserID
     * @param _groupID
     */
    public void removeAssistant (HeroPlayer _leader, int _assistantUserID)
    {
        Group group = groupTable.get(_leader.getGroupID());

        if (null != group)
        {
            GroupMemberProxy member = group.removeAssistant(_assistantUserID);

            if (null != member)
                sendGroupMsg(group, new MemberChangerNotify(
                        MemberChangerNotify.CHANGER_OF_RANK, _assistantUserID,
                        EGroupMemberRank.NORMAL.value()));
        }
    }

    /**
     * 改变队员位置
     * 
     * @param _memberUserID
     * @param _subGroupID
     * @param _groupID
     */
    public void changeSubGroup (HeroPlayer _player, int _memberUserID,
            byte _subGroupID)
    {
        Group group = groupTable.get(_player.getGroupID());

        if (null != group)
        {
//            for( GroupMemberProxy m : group.getMemberList()){
//                log.debug("@@@@@@@@@@@@@@ "+m.player.getName() +" - subgroupID: "+m.subGroupID);
//            }

            GroupMemberProxy member = group.changeMemberSubGroup(_memberUserID,
                    _subGroupID);

            if (null != member)
            {
                log.debug("changeSubGroup after member: " + member.player.getName()+", sub groupid= " + member.subGroupID);
                sendGroupMsg(group, new MemberChangerNotify(
                        MemberChangerNotify.CHANGER_OF_SUB_GROUP, member.player
                                .getUserID(), _subGroupID));
            }

//            for( GroupMemberProxy m : group.getMemberList()){
//                log.debug("============ "+m.player.getName()+ " - subgroupID: "+m.subGroupID);
//            }

        }
    }

    /**
     * 队伍提示消息群发
     * 
     * @param _group 队伍对象
     * @param _msg 报文
     * @param _exclude 此次一个无法接受报文的玩家
     */
    public void sendGroupMsg (Group _group, AbsResponseMessage _msg, int _excludeUID)
    {
        ArrayList<HeroPlayer> list = _group.getPlayerList();
        HeroPlayer player;

        for (int i = 0; i < list.size(); i++)
        {
            player = list.get(i);

            if (player.getUserID() != _excludeUID)
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), _msg);
            }
        }
    }

    /**
     * 队伍提示消息群发
     * 
     * @param _group 队伍对象
     * @param _response 报文
     */
    public void sendGroupMsg (Group _group, AbsResponseMessage _response)
    {
        ArrayList<HeroPlayer> list = _group.getPlayerList();

        for (int i = 0; i < list.size(); i++)
        {
            ResponseMessageQueue.getInstance()
                    .put(list.get(i).getMsgQueueIndex(), _response);
        }
    }

    @Override
    public void createSession (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (player.getGroupID() > 0)
        {
            Group group = groupTable.get(player.getGroupID());

            if (null != group)
            {
                sendGroupMsg(group, new MemberChangerNotify(
                        MemberChangerNotify.CHANGER_OF_ONLINE_STATUS, player
                                .getUserID(), true), _session.userID);
            }
        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (player != null && player.getGroupID() > 0)
        {
            Group group = groupTable.get(player.getGroupID());

            if (null != group)
            {
                sendGroupMsg(group, new MemberChangerNotify(
                        MemberChangerNotify.CHANGER_OF_ONLINE_STATUS, player
                                .getUserID(), false), _session.userID);
            }
        }
    }

    public void clean (int _userID)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _userID);

        if (player != null && player.getGroupID() > 0)
        {
            Group group = groupTable.get(player.getGroupID());

            if (group != null)
            {
                if (LoverServiceImpl.getInstance().anotherInTream(
                        player.getName(), group.getPlayerList()) != null)
                {
                    group.light--;
                }

                GroupMemberProxy groupMember = group.memberLeft(_userID);

                if (group.getMemberNumber() == 1)
                {
                    GroupMemberProxy lastMember = group.getMemberList().get(0);
                    TransmitterOfDungeon.getInstance().add(lastMember.player);

                    if (lastMember.isOnline())
                    {
                        ResponseMessageQueue.getInstance().put(
                                lastMember.player.getMsgQueueIndex(),
                                new GroupDisbandNotify("队伍已解散"));
                    }

                    group.destory();
                    groupTable.remove(group.getID());
                }
                else
                {
                    sendGroupMsg(group, new ReduceMemberNotify(_userID));

                    if (groupMember.memberRank == EGroupMemberRank.LEADER)
                    {
                        sendGroupMsg(group, new MemberChangerNotify(
                                MemberChangerNotify.CHANGER_OF_RANK, group
                                        .getLeader().player.getUserID(),
                                EGroupMemberRank.LEADER.value()));
                    }
                }
            }
        }
    }

    /**
     * 通知队伍成员 player 的 hp mp 变化
     * @param player
     */
    public void groupMemberListHpMpNotify(HeroPlayer player){
        if(player.getGroupID() > 0){
            Group group = GroupServiceImpl.getInstance().getGroup(player.getGroupID());
            if(null != group){
                ArrayList<HeroPlayer> playerList = group.getPlayerList();
                for(HeroPlayer mem : playerList){
                    if(mem.getUserID() != player.getUserID()){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new OthersHpMpNotify(mem));
                        /*OutMsgQ.getInstance().put(
                            mem.getMsgQueueIndex(),
                            new HpRefreshNotify(mem.getObjectType().value(),
                                    mem.getID(), mem.getHp(),
                                    mem.getHp(), false,
                                    false));
                        OutMsgQ.getInstance().put(
                            mem.getMsgQueueIndex(),
                            new MpRefreshNotify(
                                    mem.getObjectType().value(),
                                    mem.getID(),
                                    mem.getVocation().getType() == EVocationType.PHYSICS ? mem.getForceQuantity(): mem.getMp()));*/
                    }
                }

            }
        }
    }
}
