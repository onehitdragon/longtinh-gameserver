package hero.guild;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;

import hero.player.HeroPlayer;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Guild.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 上午11:00:45
 * @描述 ：公会
 */

public class Guild
{
    /**
     * 公会ID
     */
    private int                         id;

    /**
     * 公会名称
     */
    private String                      name;
    /**
     * 公会等级
     */
    private int                         guildLevel;

    /**
     * 会长
     */
    private GuildMemberProxy            president;

    /**
     * 公会成员
     */
    private ArrayList<GuildMemberProxy> memberList;
    
    private int							officerSum;

    /**
     * 构造（创建公会时调用）
     * 
     * @param _id
     * @param _str
     * @param _player
     */
    public Guild(int _id, String _name, HeroPlayer _president, int _guildLevel)
    {
        id = _id;
        name = _name;
        guildLevel = _guildLevel;
        GuildMemberProxy member = new GuildMemberProxy(_president.getUserID(),
                _president, EGuildMemberRank.PRESIDENT);
        president = member;
        memberList = new ArrayList<GuildMemberProxy>();
        memberList.add(member);
    }

    /**
     * 构造（游戏启动时，从数据库加载公会时调用）
     * 
     * @param _id
     * @param _name
     */
    public Guild(int _id, String _name, int _guildLevel)
    {
        id = _id;
        name = _name;
        guildLevel = _guildLevel;
        //容错操作
        if(guildLevel > this.GetMaxLevel())
        {
        	guildLevel = this.GetMaxLevel();
        	System.out.println("");
        }
        memberList = new ArrayList<GuildMemberProxy>();
    }

    /**
     * 添加（在线添加人进公会）
     * 
     * @param _player
     * @return
     */
    public void add (HeroPlayer _newMember)
    {
        GuildMemberProxy newMember = new GuildMemberProxy(_newMember
                .getUserID(), _newMember, EGuildMemberRank.NORMAL);

        GuildMemberProxy member;
        int i = 0;

        for (; i < memberList.size(); i++)
        {
            member = memberList.get(i);

            if (!member.isOnline)
            {
                break;
            }
            else
            {
                if (member.memberRank.value == newMember.memberRank.value)
                {
                    if (member.level < newMember.level)
                    {
                        break;
                    }
                }
            }
        }

        memberList.add(i, newMember);
    }

    /**
     * 添加（游戏启动时，从数据库加载公会时调用）
     * 
     * @param _player
     * @return
     */
    public void add (int _memberUserID, String __memberName, byte _rankValue)
    {
        GuildMemberProxy newMember = new GuildMemberProxy(_memberUserID,
                __memberName, _rankValue);

        if (newMember.memberRank == EGuildMemberRank.PRESIDENT)
        {
            president = newMember;
            memberList.add(0, newMember);
        }
        else
        {
            int i = 0;

            for (; i < memberList.size(); i++)
            {
                if (memberList.get(i).memberRank.value < newMember.memberRank.value)
                {
                    break;
                }
            }
        	if (newMember.memberRank == EGuildMemberRank.OFFICER) 
        	{
        		officerSum += 1;
			}

            memberList.add(i, newMember);
        }
    }

    public int GuildLevelUp()
    {
    	if(this.guildLevel < this.GetMaxLevel())
    	{
    		this.guildLevel += 1;
    	}
    	return guildLevel;
    }
    /**
     * 移除公会成员
     * 
     * @param _userID 玩家编号
     * @return
     */
    public GuildMemberProxy remove (int _userID)
    {
        GuildMemberProxy member;

        for (int i = 0; i < memberList.size(); i++)
        {
            member = memberList.get(i);

            if (member.userID == _userID)
            {
                memberList.remove(i);

                return member;
            }
        }

        return null;
    }

    /**
     * 返回副会长人数.
     * @return
     */
    public int getOfficerSum ()
    {
    	return officerSum;
    }
    
    /**
     * 副会长人数加1
     */
    public void addOfficer ()
    {
    	officerSum += 1;
    }
    
    /**
     * 副会长人数减1
     */
    public void reduceOfficer ()
    {
    	officerSum -= 1;
    	if (officerSum < 0) 
    	{
    		officerSum = 0;
		}
    }
    
    /**
     * 获取公会成员
     * 
     * @param _userID 玩家编号
     * @return
     */
    public GuildMemberProxy getMember (int _userID)
    {
        GuildMemberProxy member;

        for (int i = 0; i < memberList.size(); i++)
        {
            member = memberList.get(i);

            if (member.userID == _userID)
            {
                return member;
            }
        }

        return null;
    }

    /**
     * 获取公会成员
     * 
     * @param _name 玩家名字
     * @return
     */
    public GuildMemberProxy getMember (String _name)
    {
        GuildMemberProxy member;

        for (int i = 0; i < memberList.size(); i++)
        {
            member = memberList.get(i);

            if (member.name.equals(_name))
            {
                return member;
            }
        }
        return null;
    }

    /**
     * 转让会长
     * 
     * @param _newLeader
     */
    public boolean transferPresidentTo (int _userID)
    {
        GuildMemberProxy newPresident = getMember(_userID);

        if (null != newPresident
                && newPresident.memberRank != EGuildMemberRank.PRESIDENT)
        {
            newPresident.memberRank = EGuildMemberRank.PRESIDENT;
            president.memberRank = EGuildMemberRank.OFFICER;

            memberList.remove(newPresident);

            if (newPresident.isOnline)
            {
                memberList.add(0, newPresident);
            }
            else
            {
                int i = 0;

                for (; i < memberList.size(); i++)
                {
                    if (!memberList.get(i).isOnline)
                    {
                        break;
                    }
                }

                memberList.add(i, newPresident);
            }

            GuildMemberProxy member;
            int i = 0;

            memberList.remove(president);

            for (; i < memberList.size(); i++)
            {
                member = memberList.get(i);

                if (member.isOnline)
                {
                    if (president.memberRank.value > member.memberRank.value())
                    {
                        break;
                    }
                    else if (member.memberRank == president.memberRank)
                    {
                        if (president.level >= member.level)
                        {
                            break;
                        }
                    }
                }
                else
                {
                    break;
                }
            }

            memberList.add(i, president);

            president = newPresident;

            return true;
        }

        return false;
    }

    /**
     * 会员上线
     * 
     * @param _player
     * @param _member
     */
    public void memberLogin (HeroPlayer _player, GuildMemberProxy _member)
    {
        if (null != _member)
        {
            _member.level = _player.getLevel();
            _member.sex = _player.getSex();
            _member.vocation = _player.getVocation();
            _member.isOnline = true;

            memberList.remove(_member);

            GuildMemberProxy otherMember;
            int i = 0;

            for (; i < memberList.size(); i++)
            {
                otherMember = memberList.get(i);

                if (!otherMember.isOnline)
                {
                    break;
                }
                else
                {
                    if (_member.memberRank.value > otherMember.memberRank.value)
                    {
                        break;
                    }
                    else if (_member.memberRank == otherMember.memberRank)
                    {
                        if (_member.level > otherMember.level)
                        {
                            break;
                        }
                    }
                }
            }

            memberList.add(i, _member);
        }
    }

    /**
     * 会员下线
     * 
     * @param _member
     */
    public void memberLogout (GuildMemberProxy _member)
    {
        if (null != _member)
        {
            _member.isOnline = false;
            memberList.remove(_member);

            GuildMemberProxy otherMember;
            int i = 0;

            for (; i < memberList.size(); i++)
            {
                otherMember = memberList.get(i);

                if (!otherMember.isOnline)
                {
                    if (_member.memberRank.value >= otherMember.memberRank.value)
                    {
                        break;
                    }
                }
            }

            memberList.add(i, _member);
        }
    }

    /**
     * 调整会员等级（由会长操作）
     * 
     * @param _userID 玩家编号
     * @return
     */
    public boolean changeMemberRank (int _userID, EGuildMemberRank _memberRank)
    {
        GuildMemberProxy member = getMember(_userID);

        if (null != member)
        {
            if (member.memberRank == _memberRank)
            {
                return false;
            }

            memberList.remove(member);
            member.memberRank = _memberRank;

            if (member.isOnline)
            {
                GuildMemberProxy otherMember;
                int i = 0;

                for (; i < memberList.size(); i++)
                {
                    otherMember = memberList.get(i);

                    if (!otherMember.isOnline)
                    {
                        break;
                    }
                    else
                    {
                        if (member.memberRank == otherMember.memberRank)
                        {
                            if (otherMember.level < member.level)
                            {
                                break;
                            }
                        }
                    }
                }

                memberList.add(i, member);
            }
            else
            {
                GuildMemberProxy otherMember;
                int i = 0;

                for (; i < memberList.size(); i++)
                {
                    otherMember = memberList.get(i);

                    if (!otherMember.isOnline)
                    {
                        if (member.memberRank == otherMember.memberRank)
                        {
                            break;
                        }
                    }
                }

                memberList.add(i, member);
            }

            return true;
        }

        return false;
    }

    /**
     * 获取会员在公会中的等级
     * 
     * @param _userID
     * @return
     */
    public EGuildMemberRank getMemberRank (int _userID)
    {
        GuildMemberProxy member;

        for (int i = 0; i < memberList.size(); i++)
        {
            member = memberList.get(i);

            if (member.userID == _userID)
            {
                return member.memberRank;
            }
        }

        return null;
    }

    /**
     * 根据页数获得公会成员列表
     * 
     * @param _pageNumber 页码
     * @return
     */
    public List<GuildMemberProxy> getMemberList (int _pageNumber)
    {
        if (_pageNumber < 1)
        {
            return memberList
                    .subList(
                            0,
                            memberList.size() >= MAX_MEMBER_PER_PAGE ? MAX_MEMBER_PER_PAGE
                                    : memberList.size());
        }

        int totalPageNumer = getViewPageNumber();

        if (totalPageNumer >= _pageNumber)
        {
            return memberList
                    .subList(MAX_MEMBER_PER_PAGE * (_pageNumber - 1),
                            memberList.size() >= MAX_MEMBER_PER_PAGE
                                    * _pageNumber ? MAX_MEMBER_PER_PAGE
                                    * _pageNumber : memberList.size());
        }
        else
        {
            return memberList.subList(MAX_MEMBER_PER_PAGE
                    * (totalPageNumer - 1), memberList.size());
        }
    }

    /**
     * 获得公会所有成员列表
     * 
     * @return
     */
    public ArrayList<GuildMemberProxy> getMemberList ()
    {
        return memberList;
    }

    /**
     * 获取成员数量
     * 
     * @return
     */
    public int getMemberNumber ()
    {
        return memberList.size();
    }

    /**
     * 获取公会编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    /**
     * 获取公会名称
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 获取会长
     * 
     * @return
     */
    public GuildMemberProxy getPresident ()
    {
        return president;
    }

    /**
     * 获取显示页数
     * 
     * @return
     */
    public int getViewPageNumber ()
    {
        return memberList.size() % MAX_MEMBER_PER_PAGE > 0 ? memberList.size()
                / MAX_MEMBER_PER_PAGE + 1 : memberList.size()
                / MAX_MEMBER_PER_PAGE;
    }

    /**
     * 清除，公会解散时调用
     */
    public void clear ()
    {
        memberList.clear();
    }
    /**
     * 获得公会可容纳最大成员数量
     * @return
     */
    public int GetMaxMemberNumber()
    {
    	return MAX_MEMBER_NUMBER[guildLevel -1][0];
    }
    
    public int GetMaxLevel()
    {
    	return MAX_MEMBER_NUMBER.length;
    }
    /**
     * 获得帮派等级
     * @return
     */
    public int getGuildLevel()
    {
    	return this.guildLevel;
    }
    
    public int getUpGuildMoney()
    {
    	return MAX_MEMBER_NUMBER[guildLevel -1][1];
    }
    
    public int[] getUpGuildMoneyList()
    {
    	int[] list = new int[MAX_MEMBER_NUMBER.length -1];
    	for (int i = 1; i < MAX_MEMBER_NUMBER.length; i++) {
    		list[i -1] = MAX_MEMBER_NUMBER[i][1];
		}
    	return list;
    }
    
    public int[] getUpGuildNumberList()
    {
    	int[] list = new int[MAX_MEMBER_NUMBER.length -1];
    	for (int i = 1; i < MAX_MEMBER_NUMBER.length; i++) {
    		list[i -1] = MAX_MEMBER_NUMBER[i][0];
		}
    	return list;
    }

    /**
     * 公会最多人数以及每个等级升级需要的花费
     */
    public static final int[][]   MAX_MEMBER_NUMBER   = {{20, 0}, {50, 100000}, {100, 500000}};

    /**
     * 发送给客户端的会员信息每次最多数量
     */
    private static final byte MAX_MEMBER_PER_PAGE = 100;
}
