package hero.guild.service;

import hero.chat.service.ChatServiceImpl;
import hero.guild.EGuildMemberRank;
import hero.guild.Guild;
import hero.guild.GuildMemberProxy;
import hero.guild.message.GuildDisbandNotify;
import hero.guild.message.GuildUpNotify;
import hero.guild.message.RefreshMemberListNotify;
import hero.guild.message.MemberRankChangeNotify;
import hero.guild.message.GuildChangeNotify;
import hero.guild.message.ResponseGuildInfo;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.Crystal;
import hero.item.special.GuildBuild;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.DirtyStringDict;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jaxen.util.DescendantAxisIterator;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-12 下午15:40:21
 * @描述 ：公会服务
 */

public class GuildServiceImpl extends AbsServiceAdaptor<GuildConfig>
{
    /**
     * 单例
     */
    private static GuildServiceImpl instance;

    /**
     * 公会列表容器（编号：公会）
     */
    private HashMap<Integer, Guild> guildTable;

    /**
     * 可用公会编号
     */
    protected int                   USABLE_GUILD_ID;

    /**
     * 获取单例
     * 
     * @return
     */
    public static GuildServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new GuildServiceImpl();
        }

        return instance;
    }

    /**
     * 私有构造
     */
    private GuildServiceImpl()
    {
        USABLE_GUILD_ID = 1000;
        guildTable = new HashMap<Integer, Guild>();
        config = new GuildConfig();
    }

    @Override
    protected void start ()
    {
        GuildDAO.load(guildTable);
    }

    /**
     * 设置公会可用编号
     * 
     * @param _id
     */
    public void setUseableGuildID (int _id)
    {
        USABLE_GUILD_ID = _id;
    }

    /**
     * 发送公会等级，同时通知打开着公会列表的客户度来刷列表，登录时调用，用于加人选项的判断
     * 
     * @param _guildID
     */
    public void sendGuildRank (HeroPlayer _player)
    {
        Guild guild = guildTable.get(_player.getGuildID());

        if (guild != null)
        {
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new MemberRankChangeNotify(guild.getMemberRank(
                            _player.getUserID()).value()));
        }
    }

    /**
     * 创建公会
     * 
     * @param _player1 创建公会玩家对象
     * @param _name 公会名字
     */
    public boolean createGuild (HeroPlayer _player, String _guildName)
    {
		int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(
				GuildBuild.GUILD_BUILD_ID);
		GuildBuild build = (GuildBuild) GoodsContents.getGoods(GuildBuild.GUILD_BUILD_ID);
		if(stoneNum < 1) 
		{
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(
                    		Tip.TIP_GUILD_OF_NOT_HAVE_BUILD_ITEM.replaceAll("%fn", build.getName())));
            return false;
		}
        if (_player.getGuildID() != 0)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_IN_GUILD_TO_YOU));

            return false;
        }

        if (_player.getLevel() < config.level_of_creator)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(config.level_of_creator + Tip.TIP_GUILD_OF_LEVEL_LESS));

            return false;
        }

        if (_player.getMoney() < config.money_of_create)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_MONEY_LESS + config.money_of_create));

            return false;
        }

        if (!DirtyStringDict.getInstance().isCleanString(_guildName))
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_INVALIDATE_GUILD_NAME));

            return false;
        }

        synchronized (guildTable)
        {
            Iterator<Guild> guildIterator = guildTable.values().iterator();

            Guild guild;

            while (guildIterator.hasNext())
            {
                guild = guildIterator.next();

                if (guild.equals(_guildName))
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_GUILD_OF_EXISTS_GUILD_NAME));

                    return false;
                }
            }

            guild = new Guild(USABLE_GUILD_ID++, _guildName, _player, 1);

            _player.setGuildID(guild.getID());
            guildTable.put(guild.getID(), guild);

            GuildDAO.create(guild);

            PlayerServiceImpl.getInstance().addMoney(_player,
                    -config.money_of_create, 1,
                    PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE, "创建帮派");

            GuildChangeNotify guildChangeNotify = new GuildChangeNotify(_player
                    .getID(), _guildName);

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    guildChangeNotify);
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new MemberRankChangeNotify(EGuildMemberRank.PRESIDENT
                            .value()));
            
            //add by zhengl; date: 2011-03-16; note: 创建公会需消耗商城道具.
			try {
				GoodsServiceImpl.getInstance().deleteSingleGoods(
						_player,
				        _player.getInventory().getSpecialGoodsBag(),
				        build, 1,
				        CauseLog.GUILDBUILD);
			} catch (BagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
                    guildChangeNotify, true, _player.getID());

            // 创建公会日志
            LogServiceImpl.getInstance().guildChangeLog(_player.getUserID(),
                    _player.getName(), guild.getID(), guild.getName(), 1,
                    _player.getUserID(), _player.getName(), "创建");

            return true;
        }
    }
    
    public void SeeGuildInfo(HeroPlayer _player)
    {
    	Guild guild = guildTable.get(_player.getGuildID());
        if (null == guild)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_GUILD));
            return;
        }
        
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new ResponseGuildInfo(guild));
    }
    
    public void GuildUp (HeroPlayer _guildManager)
    {
    	Guild guild = guildTable.get(_guildManager.getGuildID());
        if (null == guild)
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_GUILD));
            return;
        }
        GuildMemberProxy managerProxy = guild.getMember(_guildManager.getUserID());
        if (managerProxy.memberRank != EGuildMemberRank.PRESIDENT)
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_RANK));
            return;
        }
        if(guild.getGuildLevel() >= guild.GetMaxLevel())
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_LEVEL_MAX));
            return;
        }
        if(guild.getUpGuildMoney() > _guildManager.getMoney()) 
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NOT_MONEY));
            return;
        }
        int level = guild.GuildLevelUp();
        GuildDAO.guildUpLevel(guild.getID(), level);
        _guildManager.setMoney(-guild.getUpGuildMoney());
        ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                new Warning(Tip.TIP_GUILD_OF_UP_END));
        ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), 
        		new GuildUpNotify(level, guild.GetMaxMemberNumber()));
        
        LogServiceImpl.getInstance().guildChangeLog(_guildManager.getUserID(),
        		_guildManager.getName(), guild.getID(), guild.getName(),
                guild.getMemberNumber(), guild.getPresident().userID, guild.getPresident().name, "升级帮派");
    }

    /**
     * 解散公会
     * 
     * @param _player
     */
    public boolean disbandGuild (HeroPlayer _player)
    {
        Guild guild = guildTable.remove(_player.getGuildID());

        if (guild != null && guild.getPresident().userID == _player.getUserID())
        {
            GuildDAO.distory(guild.getID());
            sendGuildMsg(guild, new GuildDisbandNotify(), 0);

            ArrayList<GuildMemberProxy> list = guild.getMemberList();
            HeroPlayer player;

            for (int i = 0; i < list.size(); i++)
            {
                player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                        list.get(i).userID);

                if (player != null)
                {
                    player.setGuildID(0);

                    if (player.isEnable())
                    {
                        MapSynchronousInfoBroadcast.getInstance().put(
                                player.where(),
                                new GuildChangeNotify(player.getID()), true,
                                player.getID());
                    }
                }
            }

            guild.clear();

            // 创建公会日志
            LogServiceImpl.getInstance().guildChangeLog(_player.getUserID(),
                    _player.getName(), guild.getID(), guild.getName(),
                    guild.getMemberNumber(), _player.getUserID(), _player.getName(), "解散帮派");

            return true;
        }

        return false;
    }

    /**
     * 添加一个玩家入公会
     * 
     * @param _player 玩家对象
     * @param _GuildID 公会ID
     * @return
     */
    public void add (HeroPlayer _invitor, HeroPlayer _target, int _guildID)
    {
        if (_target.getGuildID() > 0)
        {
            ResponseMessageQueue.getInstance().put(_invitor.getMsgQueueIndex(),
                    new Warning(_target.getName() + Tip.TIP_GUILD_OF_IN_GUILD_TO_YOU));

            return;
        }

        if (_invitor.getGuildID() != _guildID)
        {
            return;
        }

        Guild guild = guildTable.get(_guildID);

        if (null != guild)
        {
        	//edit by zhengl; date: 2011-04-29; note:人数上限添加
        	if (guild.getMemberList().size() >= guild.GetMaxMemberNumber()) 
        	{
                ResponseMessageQueue.getInstance().put(_target.getMsgQueueIndex(),
                        new Warning(Tip.TIP_GUILD_OF_MAX_SIZE));

                return;
			}
            if (GuildDAO.add(_guildID, _target.getUserID(), _target.getName()))
            {
                guild.add(_target);
                _target.setGuildID(_guildID);

                sendGuildMsg(guild, new RefreshMemberListNotify(), _target
                        .getID());
                sendGuildMsg(guild, new Warning(_target.getName()
                        + Tip.TIP_GUILD_OF_NEW_MEMBER), _target.getUserID());

                MapSynchronousInfoBroadcast.getInstance()
                        .put(
                                _target.where(),
                                new GuildChangeNotify(_target.getID(), guild
                                        .getName()), false, 0);

                // 公会成员变化日志
                LogServiceImpl.getInstance().guildMemberChangeLog(
                        _invitor.getUserID(), _invitor.getName(),
                        guild.getID(), guild.getName(), _target.getUserID(),
                        _target.getName(), "添加");
            }
        }
    }

    /**
     * 开除公会成员
     * 
     * @param _guildManager 公会管理者（会长、官员）
     * @param _userIDWillBeRemove 被开除的会员编号
     */
    public void removeMember (HeroPlayer _guildManager, int _userIDWillBeRemove)
    {
        Guild guild = guildTable.get(_guildManager.getGuildID());

        if (null == guild)
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_GUILD));

            return;
        }

        GuildMemberProxy managerProxy = guild.getMember(_guildManager
                .getUserID());
        GuildMemberProxy beRemoveMemberProxy = guild
                .getMember(_userIDWillBeRemove);

        if (null == beRemoveMemberProxy)
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NOT_GUILD_MEMBER));

            return;
        }

        if (managerProxy.memberRank.value() <= beRemoveMemberProxy.memberRank
                .value())
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_RANK));

            return;
        }

        guild.remove(_userIDWillBeRemove);
        GuildDAO.removeGuildMember(_userIDWillBeRemove);

        sendGuildMsg(guild, new RefreshMemberListNotify(), 0);

        sendGuildMsg(guild, new Warning(beRemoveMemberProxy.name
                + Tip.TIP_GUILD_OF_NOT_BE_REMOVE), 0);

        if (beRemoveMemberProxy.isOnline)
        {
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(_userIDWillBeRemove);

            if (player != null)
            {
                player.setGuildID(0);

                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_GUILD_OF_YOU + Tip.TIP_GUILD_OF_NOT_BE_REMOVE));
                MapSynchronousInfoBroadcast.getInstance().put(player.where(),
                        new GuildChangeNotify(player.getID()), false, 0);
            }
        }

        // 公会成员变化日志
        LogServiceImpl.getInstance().guildMemberChangeLog(
                _guildManager.getUserID(), _guildManager.getName(),
                guild.getID(), guild.getName(), beRemoveMemberProxy.userID,
                beRemoveMemberProxy.name, "开除");
    }
    
    /**
     * 帮会成员升级
     * @param _guildMember
     */
    public void menberUpgrade (HeroPlayer _guildMember)
    {
        Guild guild = guildTable.get(_guildMember.getGuildID());

        if (null != guild)
        {
        	GuildMemberProxy memberProxy = guild.getMember(_guildMember.getUserID());
        	memberProxy.level = _guildMember.getLevel();
        }

    }

    /**
     * 离开公会
     * 
     * @param _guildMember
     */
    public void leftGuild (HeroPlayer _guildMember)
    {
        Guild guild = guildTable.get(_guildMember.getGuildID());

        if (null == guild)
        {
            ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_GUILD));

            return;
        }

        GuildMemberProxy memberProxy = guild
                .getMember(_guildMember.getUserID());

        if (null == memberProxy)
        {
            ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NOT_GUILD_MEMBER));

            return;
        }

        if (memberProxy.memberRank == EGuildMemberRank.PRESIDENT)
        {
            ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_PRESIDENT_NOT_ALLOW_LEFT));

            return;
        }

        guild.remove(memberProxy.userID);
        GuildDAO.removeGuildMember(memberProxy.userID);

        sendGuildMsg(guild, new RefreshMemberListNotify(), 0);
        sendGuildMsg(guild, new Warning(memberProxy.name + Tip.TIP_GUILD_OF_LEFT),
                memberProxy.userID);

        _guildMember.setGuildID(0);

        ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(),
                new Warning(Tip.TIP_GUILD_OF_YOU + Tip.TIP_GUILD_OF_LEFT));

        GuildChangeNotify guildChangeNotifyMsg = new GuildChangeNotify(
                _guildMember.getID());

        ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(),
                guildChangeNotifyMsg);

        MapSynchronousInfoBroadcast.getInstance().put(_guildMember.where(),
                guildChangeNotifyMsg, true, _guildMember.getID());

        // 公会成员变化日志
        LogServiceImpl.getInstance().guildMemberChangeLog(memberProxy.userID,
                memberProxy.name, guild.getID(), guild.getName(),
                memberProxy.userID, memberProxy.name, "离开");
    }

    /**
     * 在登录服务器删除角色时调用
     * 
     * @param _userID 角色编号
     */
    public void deleteRole (int _userID)
    {
        Iterator<Guild> iterator = guildTable.values().iterator();
        Guild guild;
        GuildMemberProxy guildMemberProxy;

        while (iterator.hasNext())
        {
            guild = iterator.next();

            guildMemberProxy = guild.remove(_userID);

            if (null != guildMemberProxy)
            {
                GuildDAO.removeGuildMember(_userID);

                if (guild.getMemberNumber() == 0)
                {
                    GuildDAO.distory(guild.getID());
                    guildTable.remove(guild.getID());
                }
                else
                {
                    if (guildMemberProxy.memberRank == EGuildMemberRank.PRESIDENT)
                    {
                        GuildMemberProxy newPresident = guild.getMemberList()
                                .get(0);

                        newPresident.memberRank = EGuildMemberRank.PRESIDENT;
                        GuildDAO.changeMemberRank(newPresident.userID,
                                EGuildMemberRank.PRESIDENT);
                        GuildDAO.updatePresident(guild.getID(),
                                newPresident.userID);
                        GuildDAO.changeMemberRank(_userID,
                                EGuildMemberRank.PRESIDENT);

                        sendGuildMsg(guild, new Warning(newPresident.name
                                + Tip.TIP_GUILD_OF_BE
                                + EGuildMemberRank.PRESIDENT.getDesc()),
                                newPresident.userID);

                        if (newPresident.isOnline)
                        {
                            HeroPlayer player = PlayerServiceImpl.getInstance()
                                    .getPlayerByUserID(newPresident.userID);

                            if (null != player && player.isEnable())
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_GUILD_OF_YOU_BE
                                                + EGuildMemberRank.PRESIDENT
                                                        .getDesc()));
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new MemberRankChangeNotify(
                                                EGuildMemberRank.PRESIDENT
                                                        .value()));
                            }
                        }
                    }
                }

                return;
            }
        }
    }
    
    /**
     * 获取玩家的公会职务.
     * @param _player
     * @return
     */
    public String getMemberRank(HeroPlayer _player) {
    	String desc = "";
    	Guild guild = guildTable.get(_player.getGuildID());
    	if(guild != null) {
            GuildMemberProxy managerProxy = guild.getMember(_player.getUserID());
            desc = managerProxy.memberRank.getDesc();
    	}
    	return desc;
    }
    
    /**
     * 玩家1 与玩家2 是否同一工会
     * @param _p1
     * @param _p2
     * @return
     */
    public boolean isAssociate(String _p1, String _p2) {
    	boolean result = false;
    	HeroPlayer p1 = PlayerServiceImpl.getInstance().getPlayerByName(_p1);
    	HeroPlayer p2 = PlayerServiceImpl.getInstance().getPlayerByName(_p2);
    	if(p1.getGuildID() == p2.getGuildID()) {
    		result = true;
    	}
    	return result;
    }
    
    /**
     * 获取工会名称
     * @param _player
     * @return
     */
    public String getGuildName(HeroPlayer _player) {
    	String desc = "";
    	Guild guild = guildTable.get(_player.getGuildID());
    	if(guild != null) {
            desc = guild.getName();
    	}
    	return desc;
    }

    /**
     * 改变公会成员等级
     * 
     * @param _guildManager 公会管理者（会长，官员）
     * @param _memberUserID 成员编号
     * @param _guildRank 公会等级
     */
    public void changeMemberRank (HeroPlayer _guildManager, int _memberUserID,
            EGuildMemberRank _guildRank)
    {
        Guild guild = guildTable.get(_guildManager.getGuildID());

        if (null == guild)
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_GUILD));

            return;
        }

        GuildMemberProxy managerProxy = guild.getMember(_guildManager
                .getUserID());
        GuildMemberProxy beRemoveMemberProxy = guild.getMember(_memberUserID);

        if (null == beRemoveMemberProxy)
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NOT_GUILD_MEMBER));

            return;
        }

        if (managerProxy.memberRank != EGuildMemberRank.PRESIDENT)
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NONE_RANK));

            return;
        }
        if (_guildRank == EGuildMemberRank.OFFICER && 
        		guild.getOfficerSum() >= GuildServiceImpl.getInstance().getConfig().officer_sum) 
        {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_POSITION_SIZE_MAX));
            return;
		}
        guild.changeMemberRank(_memberUserID, _guildRank);
        GuildDAO.changeMemberRank(_memberUserID, _guildRank);

        sendGuildMsg(guild, new Warning(beRemoveMemberProxy.name + Tip.TIP_GUILD_OF_BE
                + _guildRank.getDesc()), _memberUserID);

        if (beRemoveMemberProxy.isOnline)
        {
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(_memberUserID);

            if (player != null)
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new MemberRankChangeNotify(_guildRank.value()));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_GUILD_OF_YOU_BE + _guildRank.getDesc()));
            }
        }
        //add by zhengl; date: 2011-05-14; note: 修复副帮主人数不递增的BUG
        if (_guildRank == EGuildMemberRank.OFFICER) 
        {
        	guild.addOfficer();
		}
        else if (_guildRank == EGuildMemberRank.NORMAL) 
        {
        	guild.reduceOfficer();
		}
        sendGuildMsg(guild, new RefreshMemberListNotify(), 0);
    }

    /**
     * 转让会长
     * 
     * @param _president
     * @param _guild
     * @param _memberUserID
     */
    public boolean transferPresident (Guild _guild, HeroPlayer _president,
            int _memberUserID)
    {
        GuildMemberProxy targetMemberProxy = _guild.getMember(_memberUserID);

        if (null == targetMemberProxy)
        {
            ResponseMessageQueue.getInstance().put(_president.getMsgQueueIndex(),
                    new Warning(Tip.TIP_GUILD_OF_NOT_GUILD_MEMBER));

            return false;
        }

        if (_guild.transferPresidentTo(_memberUserID))
        {
            GuildDAO.transferPresident(_guild.getID(), _president.getUserID(),
                    _memberUserID);
            ResponseMessageQueue.getInstance()
                    .put(
                            _president.getMsgQueueIndex(),
                            new MemberRankChangeNotify(EGuildMemberRank.OFFICER
                                    .value()));
            sendGuildMsg(_guild, new Warning(targetMemberProxy.name + Tip.TIP_GUILD_OF_BE
                    + EGuildMemberRank.PRESIDENT.getDesc()), _memberUserID);

            if (targetMemberProxy.isOnline)
            {
                HeroPlayer player = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(_memberUserID);

                if (player != null)
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new MemberRankChangeNotify(
                                    EGuildMemberRank.PRESIDENT.value()));
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_GUILD_OF_YOU_BE
                                    + EGuildMemberRank.PRESIDENT.getDesc()));
                }
            }

            sendGuildMsg(_guild, new RefreshMemberListNotify(), _president
                    .getUserID());

            return true;
        }

        return false;
    }

    /**
     * 获得公会
     * 
     * @param _guildID 公会编号
     * @return
     */
    public Guild getGuild (int _guildID)
    {
        if (0 < _guildID)
        {
            return guildTable.get(_guildID);
        }
        else
        {
            return null;
        }
    }

    /**
     * 发送公会消息
     * 
     * @param _guild 公会对象
     * @param _msg 下行报文
     * @param _exclude 不接受的公会成员编号
     */
    public void sendGuildMsg (Guild _guild, AbsResponseMessage _msg,
            int _excludeMemberUserID)
    {
        ArrayList<GuildMemberProxy> list = _guild.getMemberList();
        GuildMemberProxy member;
        HeroPlayer player;

        for (int i = 0; i < list.size(); i++)
        {
            member = list.get(i);

            if (_excludeMemberUserID != member.userID && member.isOnline)
            {
                player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                        member.userID);

                if (null != player && player.isEnable())
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), _msg);
            }
        }
    }

    @Override
    public void createSession (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (player != null)
        {
            if (player.getGuildID() == 0)
            {
                if (!guildTable.isEmpty())
                {
                    Iterator<Guild> iterator = guildTable.values().iterator();
                    Guild guild;
                    GuildMemberProxy guildMemberProxy;

                    while (iterator.hasNext())
                    {
                        guild = iterator.next();

                        guildMemberProxy = guild.getMember(_session.userID);

                        if (null != guildMemberProxy)
                        {
                            player.setGuildID(guild.getID());
                            guild.memberLogin(player, guildMemberProxy);

                            // ChatServiceImpl.getInstance().sendGuildBottomSys(
                            // guild,
                            // player.getName() + TIP_OF_MEMBER_LOGIN);

                            return;
                        }
                    }
                }
            }
            else
            {
                Guild guild = guildTable.get(player.getGuildID());

                if (null != guild)
                {
                    GuildMemberProxy guildMemberProxy = guild.getMember(player
                            .getUserID());
                    guild.memberLogin(player, guildMemberProxy);

                    // ChatServiceImpl.getInstance().sendGuildBottomSys(guild,
                    // guildMemberProxy.name + TIP_OF_MEMBER_LOGIN);
                }
            }
        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (player != null)
        {
            Guild guild = guildTable.get(player.getGuildID());

            if (null != guild)
            {
                GuildMemberProxy member = guild.getMember(player.getUserID());
                guild.memberLogout(member);

                // ChatServiceImpl.getInstance().sendGuildBottomSys(guild,
                // member.name + TIP_OF_MEMBER_OFFLINE);
            }
        }
    }

}
