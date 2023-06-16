package hero.group;

import java.util.ArrayList;
import java.util.Iterator;

import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroupMemberProxy.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午02:27:45
 * @描述 ：队伍
 */

public class Group
{
    private static Logger log = Logger.getLogger(Group.class);
    /**
     * 队伍ID
     */
    private int                         ID;

    /**
     * 队长
     */
    private GroupMemberProxy            leader;

    /**
     * 所有成员列表
     */
    private ArrayList<GroupMemberProxy> memberList;

    /**
     * 队伍中最高成员等级
     */
    private int                         memberMaxLevel;

    /**
     * 普通物品轮流拾取者在队伍中的位置
     */
    private int                         goodsPickerGroupIndex;

    /**
     * 是否享受光环
     */
    public byte                         light;

    /**
     * 助手数量
     * 每个队伍最多有2个
     */
    public int assistantNum;

    /**
     * 每个队伍最大助手数量
     */
    public static final int ASSISTANT_MAX_NUM = 2;

    private int subGroupMemberNum = 0;
    private int subGroup2MemberNum = 0;
    private int subGroup3MemberNum = 0;
    private int subGroup4MemberNum = 0;

    /**
     * 构造
     * 
     * @param _ID
     */
    public Group(int _ID, HeroPlayer _leader, HeroPlayer _member)
    {
        ID = _ID;
        memberList = new ArrayList<GroupMemberProxy>();
        leader = new GroupMemberProxy(_leader, 1);
        leader.memberRank = EGroupMemberRank.LEADER;
        memberList.add(leader);
        flushSubGroupMemberNumber(1,1);
        _leader.setGroupID(ID);
        memberList.add(new GroupMemberProxy(_member, 1));
        _member.setGroupID(ID);
        flushSubGroupMemberNumber(1,1);
        memberMaxLevel = _leader.getLevel() >= _member.getLevel() ? _leader
                .getLevel() : _member.getLevel();
    }

    /**
     * 获取成员
     * 
     * @param _userID
     * @return
     */
    public GroupMemberProxy getMember (int _userID)
    {
        synchronized (memberList)
        {
            for (GroupMemberProxy member : memberList)
            {
                if (member.player.getUserID() == _userID)
                {
                    return member;
                }
            }

            return null;
        }
    }

    /**
     * 添加一个玩家入队伍
     * 
     * @param playerProxy
     * @return
     */
    public synchronized GroupMemberProxy add (HeroPlayer _player)
    {
        if (memberList.size() < MAX_NUMBER_OF_MEMBER)
        {
            byte subGroupID = 1;
//            GroupMemberProxy member;
            log.debug("subGroupMemberNum = "+subGroupMemberNum);
            log.debug("subGroup2MemberNum = " + subGroup2MemberNum);
            log.debug("subGroup3MemberNum = " + subGroup3MemberNum);
            log.debug("subGroup4MemberNum = " + subGroup4MemberNum);

            if(subGroupMemberNum == MAX_MEMBER_OF_SUB_GROUP
                    && subGroup2MemberNum < MAX_MEMBER_OF_SUB_GROUP){
                subGroupID = 2;
            }else if(subGroup2MemberNum == MAX_MEMBER_OF_SUB_GROUP
                    && subGroup3MemberNum < MAX_MEMBER_OF_SUB_GROUP){
                subGroupID = 3;
            }else if(subGroup3MemberNum == MAX_MEMBER_OF_SUB_GROUP
                    && subGroup4MemberNum < MAX_MEMBER_OF_SUB_GROUP){
                subGroupID = 4;
            }
            log.debug("add member in subgroupID = " + subGroupID);

            GroupMemberProxy newMember = new GroupMemberProxy(_player, subGroupID);
            memberList.add(newMember);
//            newMember.position = memberList.size()-1;
            _player.setGroupID(getID());
            flushSubGroupMemberNumber(subGroupID,1);

            log.debug("add new member name = " + newMember.player.getName()+", subgroup id = " + newMember.subGroupID);

            for(GroupMemberProxy member : memberList){
                log.debug("add new member after "+member.player.getUserID()+" -- "+member.player.getName() +" -- " + member.subGroupID);
            }

            log.debug("add after subGroupMemberNum = "+subGroupMemberNum);
            log.debug("add after subGroup2MemberNum = " + subGroup2MemberNum);
            log.debug("add after subGroup3MemberNum = " + subGroup3MemberNum);
            log.debug("add after subGroup4MemberNum = " + subGroup4MemberNum);


            /*for (int i = 0; i < memberList.size(); i++)
            {
                member = memberList.get(i);

                if (member.subGroupID == subGroupID)
                {
                    subGroupMemberNum++;

                    continue;
                }
                else
                {
                    if (subGroupMemberNum < MAX_MEMBER_OF_SUB_GROUP)
                    {
                        GroupMemberProxy newMember = new GroupMemberProxy(
                                _player, subGroupID);
                        newMember.position = i;
                        memberList.add(i, newMember);
                        _player.setGroupID(ID);

                        if (_player.getLevel() > memberMaxLevel)
                        {
                            memberMaxLevel = _player.getLevel();
                        }

                        return newMember;
                    }
                    else
                    {
                        subGroupID++;
                        subGroupMemberNum = 1;
                    }
                }
            }

            GroupMemberProxy newMember = new GroupMemberProxy(_player,
                    subGroupMemberNum < MAX_MEMBER_OF_SUB_GROUP ? subGroupID
                            : subGroupID + 1);
            newMember.position = memberList.size();
            memberList.add(newMember);
            _player.setGroupID(ID);*/

            return newMember;
        }

        return null;
    }

    /**
     * 移除队伍成员
     * 
     * @param _userID
     * @return
     */
    public synchronized GroupMemberProxy remove (int _userID)
    {
        GroupMemberProxy member = null;

        int tempMaxLevel = 0;

        Iterator<GroupMemberProxy> it = memberList.iterator();
        while (it.hasNext()){
            member = it.next();

            int tempSubGroupID = member.subGroupID;

            log.debug("member userid = " + member.player.getName());
            if(member.player.getUserID() == _userID){
                log.debug("remove member userID == " + member.player.getUserID());

                it.remove();
                member.player.setGroupID(0);
                if(member.memberRank == EGroupMemberRank.ASSISTANT){
                    assistantNum--;
                }
                tempMaxLevel = member.player.getLevel();
                flushSubGroupMemberNumber(tempSubGroupID,2);

                break;
            }
        }

        GroupMemberProxy tempMember;
        if (memberList.size() > 1
                        && memberMaxLevel == tempMaxLevel)
        {
            int maxLevel = 0;

            it = memberList.iterator();

            while (it.hasNext()){
                tempMember = it.next();
                if(tempMember.player.getLevel() > maxLevel){
                    maxLevel = tempMember.player.getLevel();
                }
            }

            memberMaxLevel = maxLevel;
        }

        for(GroupMemberProxy memberProxy : memberList){
            log.debug("remove member after "+memberProxy.player.getUserID()+" -- " + memberProxy.player.getName());
        }

        /*
        for (int i = 0; i < memberList.size(); i++)
        {
            member = memberList.get(i);

            if (member.player.getUserID() == _userID)
            {
                int tempSubGroupID = member.subGroupID;


                memberList.remove(i);
                member.player.setGroupID(0);

                flushSubGroupMemberNumber(tempSubGroupID,2);

                if (memberList.size() > 1
                        && memberMaxLevel == member.player.getLevel())
                {
                    int maxLevel = 0;

                    for (int j = 0; j < memberList.size(); j++)
                    {
                        member = memberList.get(j);

                        if (member.player.getLevel() > maxLevel)
                        {
                            maxLevel = member.player.getLevel();
                        }
                    }

                    memberMaxLevel = maxLevel;
                }

                break;
            }
        }*/
//        flushMemberPosition();
        return member;
    }

    /*private void flushMemberPosition(){
        GroupMemberProxy memberProxy;
        for(int i=0; i<memberList.size(); i++){
            memberProxy = memberList.get(i);
            memberProxy.position = i;
        }
    }*/

    private void flushSubGroupMemberNumber(int subGroupID,int type){
        if(type == 1){ //加 1
            switch (subGroupID){
                case 1: subGroupMemberNum++;break;
                case 2: subGroup2MemberNum++;break;
                case 3: subGroup3MemberNum++;break;
                case 4: subGroup4MemberNum++;break;
            }
        }
        if(type == 2){ //减 1
            switch (subGroupID){
                case 1: subGroupMemberNum--;break;
                case 2: subGroup2MemberNum--;break;
                case 3: subGroup3MemberNum--;break;
                case 4: subGroup4MemberNum--;break;
            }
        }
    }

    /**
     * 队伍成员离开
     * 
     * @param _memberUserID
     * @return
     */
    public synchronized GroupMemberProxy memberLeft (int _memberUserID)
    {
        GroupMemberProxy leftMember = null;

        for (int i = 0; i < memberList.size(); i++)
        {
            leftMember = memberList.get(i);

            if (leftMember.player.getUserID() == _memberUserID)
            {
                try
                {
                    int tempSubGroupID = leftMember.subGroupID;
                    memberList.remove(i);
                    leftMember.player.setGroupID(0);
                    if(leftMember.memberRank == EGroupMemberRank.ASSISTANT){
                        assistantNum--;
                    }

                    flushSubGroupMemberNumber(tempSubGroupID,2);

                    if (memberList.size() > 1)
                    {
                        if (leftMember.memberRank == EGroupMemberRank.LEADER)
                        {
                            int j = 0;
                            GroupMemberProxy member;

                            for (; j < memberList.size(); j++)
                            {
                                member = memberList.get(j);

                                if (member.memberRank == EGroupMemberRank.ASSISTANT)
                                {
                                    leader = member;
                                    leader.memberRank = EGroupMemberRank.LEADER;

                                    break;
                                }
                            }

                            if (j == memberList.size())
                            {
                                leader = memberList.get(0);
                                leader.memberRank = EGroupMemberRank.LEADER;
                            }
                        }

                        int maxLevel = 0;
                        GroupMemberProxy member;

                        for (int j = 0; j < memberList.size(); j++)
                        {
                            member = memberList.get(j);

                            if (member.player.getLevel() > maxLevel)
                            {
                                maxLevel = member.player.getLevel();
                            }
                        }

                        memberMaxLevel = maxLevel;
                    }

                    break;
                }
                catch (Exception e)
                {
                }
            }
        }
//        flushMemberPosition();
        return leftMember;
    }

    /**
     * 转让队长
     * 
     * @param _member 新队长
     */
    public GroupMemberProxy transferLeader (GroupMemberProxy _member)
    {
        synchronized (leader)
        {
            if (null != _member)
            {
                leader.memberRank = EGroupMemberRank.NORMAL; //转让队长后，旧队长变为普通队员 jiaodongjie
                leader = _member;
                if(_member.memberRank == EGroupMemberRank.ASSISTANT){
                    assistantNum--;
                }
                leader.memberRank = EGroupMemberRank.LEADER;
            }

            return _member;
        }
    }

    /**
     * 任命助手（针对普通队员）
     * 
     * @param _normalMemberUserID
     */
    public GroupMemberProxy addAssistant (int _normalMemberUserID)
    {

        GroupMemberProxy member = getMember(_normalMemberUserID);

        if (null != member)
        {
            if (member.memberRank == EGroupMemberRank.NORMAL)
            {
                member.memberRank = EGroupMemberRank.ASSISTANT;
                assistantNum++;
                return member;
            }
        }

        return null;
    }

    /**
     * 解除助手
     * 
     * @param _assistantUserID
     */
    public GroupMemberProxy removeAssistant (int _assistantUserID)
    {
        GroupMemberProxy member = getMember(_assistantUserID);

        if (null != member)
        {
            if (member.memberRank == EGroupMemberRank.ASSISTANT)
            {
                member.memberRank = EGroupMemberRank.NORMAL;
                assistantNum--;
                return member;
            }
        }

        return null;
    }

    /**
     * 改变队员位置
     * 
     * @param _memberUserID
     * @param _subGroupID
     */
    public synchronized GroupMemberProxy changeMemberSubGroup (
            int _memberUserID, byte _subGroupID)
    {
        boolean canChange = false;
        switch (_subGroupID){
            case 1:
                canChange = subGroupMemberNum < MAX_MEMBER_OF_SUB_GROUP;
                break;
            case 2:
                canChange = subGroup2MemberNum < MAX_MEMBER_OF_SUB_GROUP;
                break;
            case 3:
                canChange = subGroup3MemberNum < MAX_MEMBER_OF_SUB_GROUP;
                break;
            case 4:
                canChange = subGroup4MemberNum < MAX_MEMBER_OF_SUB_GROUP;
                break;
        }

        GroupMemberProxy member = getMember(_memberUserID);
        log.debug("change member sub group member= " +member);
        if (null != member)
        {
            if(!canChange){
                ResponseMessageQueue.getInstance().put(member.player.getMsgQueueIndex(), 
                		new Warning(Tip.TIP_GROUP_OF_TEAM_FULL_NOT_POSITION));
                return null;
            }

            if(member.subGroupID == _subGroupID){
                ResponseMessageQueue.getInstance().put(member.player.getMsgQueueIndex(), 
                		new Warning(Tip.TIP_GROUP_OF_IN_THIS_TEAM));
                return null;
            }

            int tempSubGroupID = member.subGroupID;

            member.subGroupID = _subGroupID;

            flushSubGroupMemberNumber(tempSubGroupID,2);
            flushSubGroupMemberNumber(_subGroupID,1);

            /*int temposition = -1;
            GroupMemberProxy tempMember;
            log.debug("membr subgroup = " + member.subGroupID);

            if (member.subGroupID != _subGroupID && _subGroupID >= 1
                    && _subGroupID <= MAX_SUB_GROUP)
            {
                int subGroupIDMemberNum = 0;
                int tempSubGroupID;

                for (int i = 0; i < memberList.size(); i++)
                {
                    try
                    {
                        tempMember = memberList.get(i);
                        tempSubGroupID = tempMember.subGroupID;
                        log.debug("temp sub group id = " + tempSubGroupID);


                        if (tempSubGroupID > _subGroupID)
                        {
                            if (subGroupIDMemberNum < MAX_MEMBER_OF_SUB_GROUP)
                            {
                                log.debug(i + "---- tempSubGroupID > _subGroupID");

                                memberList.set(i, member);
                                member.subGroupID = _subGroupID;


                                return member;
                            }
                            else
                            {
                                return null;
                            }
                        }
                        else if (tempSubGroupID == _subGroupID)
                        {
                            subGroupIDMemberNum++;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        return null;
                    }
                }

                tempMember = memberList.get(memberList.size() - 1);
                temposition = member.position;

                memberList.set(memberList.size() - 1, member);
                member.subGroupID = _subGroupID;
                member.position = tempMember.position;

                memberList.set(temposition,tempMember);
                tempMember.position = temposition;*//*



                return member;

            }*/
            return member;
        }

        return null;
    }

    /**
     * 得到队伍中所有玩家
     * 
     * @return
     */
    public ArrayList<HeroPlayer> getPlayerList ()
    {
        ArrayList<HeroPlayer> list = new ArrayList<HeroPlayer>();

        GroupMemberProxy member;

        for (int i = 0; i < memberList.size(); i++)
        {
            try
            {
                member = memberList.get(i);

                if (member.isOnline())
                {
                    list.add(member.player);
                }
            }
            catch (Exception e)
            {

            }
        }

        return list;
    }

    /**
     * 获取队员数量
     * 
     * @return
     */
    public int getMemberNumber ()
    {
        return memberList.size();
    }

    /**
     * 获取队伍编号
     * 
     * @return
     */
    public int getID ()
    {
        return ID;
    }

    /**
     * 获取队长
     * 
     * @return
     */
    public GroupMemberProxy getLeader ()
    {
        return leader;
    }

    /**
     * 获取与指定地图相关的有效角色列表
     * 
     * @param _mapID
     * @return
     */
    public ArrayList<HeroPlayer> getValidatePlayerList (int _mapID)
    {
        ArrayList<HeroPlayer> list = new ArrayList<HeroPlayer>();

        GroupMemberProxy member;

        for (int i = 0; i < memberList.size(); i++)
        {
            try
            {
                member = memberList.get(i);

                if (member.isOnline()
                        && member.player.where().getID() == _mapID)
                {
                    list.add(member.player);
                }
            }
            catch (Exception e)
            {

            }
        }

        return list;
    }

    /**
     * 获取成员列表
     * 
     * @return
     */
    public ArrayList<GroupMemberProxy> getMemberList ()
    {
        return memberList;
    }

    /**
     * 获取队伍中拾取怪物掉落的角色
     * 
     * @param _mapOfLegacy 怪物掉落所在的地图
     * @return
     */
    public int getGoodsPickerUserID (int _mapID)
    {
        int lookTimes = 1;

        GroupMemberProxy member;

        while (lookTimes <= memberList.size())
        {
            if (goodsPickerGroupIndex >= memberList.size())
            {
                goodsPickerGroupIndex = 0;
            }

            try
            {
                member = memberList.get(goodsPickerGroupIndex++);

                if (member.isOnline()
                        && member.player.where().getID() == _mapID)
                {
                    return member.player.getUserID();
                }
            }
            catch (Exception e)
            {
                memberList.get(0).player.getUserID();
            }

            lookTimes++;
        }

        return 0;
    }

    /**
     * 队伍解散
     */
    public synchronized void destory ()
    {
        for (GroupMemberProxy member : memberList)
        {
            member.player.setGroupID(0);
        }

        memberList.clear();
        leader = null;
    }

    /**
     * 获取队伍中成员最高等级
     * 
     * @return
     */
    public int getMemberMaxLevel ()
    {
        return memberMaxLevel;
    }

    /**
     * 根据USERID 判断是否是队长
     * @param userID
     * @return
     */
    public boolean isLeader(int userID){
         return (null != leader && leader.player.getUserID()==userID);
    }
    /**
     * 队伍中能容纳的队员数量
     */
    public static final int MAX_NUMBER_OF_MEMBER    = 10;

    /**
     * 队伍中最多小队数量
     */
    public static final int MAX_SUB_GROUP           = 2;

    /**
     * 单个子队最大队员数量
     */
    public static final int MAX_MEMBER_OF_SUB_GROUP = 5;
}
