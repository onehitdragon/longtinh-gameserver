package hero.social.service;

import static hero.social.ESocialRelationType.BLACK;
import static hero.social.ESocialRelationType.ENEMY;
import static hero.social.ESocialRelationType.FRIEND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hero.chat.service.ChatServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.social.ESocialRelationType;
import hero.social.SocialObjectProxy;
import hero.social.SocialRelationList;
import hero.social.message.AddSocialMemberNotify;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SocialServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-9 上午10:08:23
 * @描述 ：社交关系服务（包括友好、仇人、屏蔽三种关系，其他关系在独有模块中处理）
 */

public class SocialServiceImpl extends AbsServiceAdaptor<SocialServerConfig>
{
    private static Logger log = Logger.getLogger(SocialServiceImpl.class);
    /**
     * 社交关系容器（玩家编号：社交关系列表）
     */
    protected Map<Integer, SocialRelationList> container = Collections
                                                                 .synchronizedMap(new HashMap<Integer, SocialRelationList>());

    /**
     * 单例
     */
    private static SocialServiceImpl           instance;

    /**
     * 构造
     */
    private SocialServiceImpl()
    {
        config = new SocialServerConfig();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static SocialServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new SocialServiceImpl();
        }

        return instance;
    }

    /**
     * 玩家直接操作，添加社交关系
     * 
     * @param _player 操作者
     * @param _target 目标
     * @param _type 加入类型
     */
    public void add (HeroPlayer _player, HeroPlayer _target, byte _type)
    {
    	//1,好友反目 = 成仇人 = 删除好友关系,添加仇人关系// 现在的设定不会出现这样的情况
    	//2,好友反目 = 成黑名单 = 删除好友关系,添加成为黑名单
    	//3,仇人化解 = 成好友 = 删除仇人关系,添加成为好友// 现在的设定不会出现这样的情况
    	//4,仇人憎恶 = 加黑名单 = 保留仇人关系,添加黑名单
    	//5,黑名单化解 = 成好友 = 删除黑名单关系,添加称为好友
    	//6,黑名单作恶 = 加仇人 = 保留黑名单关系,添加仇人
    	byte complexRelation = 0; //关系单纯
        SocialRelationList socialRelationList = container.get(_player.getUserID());

        /**当前关系*/
        SocialObjectProxy nowSocial = socialRelationList
        					.getSocialObjectProxy(_target.getUserID());
        SocialObjectProxy oldSocial = null;
        /**将要 成为什么关系*/
        ESocialRelationType newSocialType = ESocialRelationType.getSocialRelationType(_type);

        if (null != nowSocial)
        {
        	if(ESocialRelationType.FRIEND == newSocialType 
        			&& ESocialRelationType.FRIEND == nowSocial.socialRelationType) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(_target.getName()+"已在你的好友名单中"));
                return;
        	} else if(ESocialRelationType.BLACK == newSocialType 
        			&& ESocialRelationType.BLACK == nowSocial.socialRelationType) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_SOCIAL_SERVICE_OF_EXISTS_BLACK_LIST));
                return;
			} else if(ESocialRelationType.ENEMY == newSocialType 
        			&& ESocialRelationType.ENEMY == nowSocial.socialRelationType) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_SOCIAL_SERVICE_OF_EXISTS_ENEMY_LIST));
                return;
			}
        	//处理复杂关系
        	if(ESocialRelationType.ENEMY == newSocialType 
        			&& ESocialRelationType.FRIEND == nowSocial.socialRelationType) {
        		//好友反目 (该关系现阶段版本不会触发,因为互为好友关系的玩家只能是同一阵营)
        		complexRelation = 1;
        	} else if(ESocialRelationType.BLACK == newSocialType 
        			&& ESocialRelationType.FRIEND == nowSocial.socialRelationType) {
        		//好友反目
        		complexRelation = 2;
			} else if(ESocialRelationType.FRIEND == newSocialType 
        			&& ESocialRelationType.ENEMY == nowSocial.socialRelationType) {
                //仇人化解 (该关系现阶段版本不会触发,因为互为仇敌关系的玩家只能是非同一阵营)
				complexRelation = 3;
			} else if(ESocialRelationType.BLACK == newSocialType 
        			&& ESocialRelationType.ENEMY == nowSocial.socialRelationType) {
                //仇人憎恶
				complexRelation = 4;
			} else if(ESocialRelationType.FRIEND == newSocialType 
        			&& ESocialRelationType.BLACK == nowSocial.socialRelationType) {
                //黑名单化解
				complexRelation = 5;
			} else if(ESocialRelationType.ENEMY == newSocialType 
        			&& ESocialRelationType.BLACK == nowSocial.socialRelationType) {
                //黑名单作恶
				complexRelation = 6;
			}
        }
        switch (newSocialType)
        {
            case FRIEND:
            {
                if (_player.getClan() != _target.getClan())
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SOCIAL_SERVICE_OF_DIFFERENT_CLAN));

                    return;
                }

                nowSocial = socialRelationList.add(FRIEND, _target);

                if (null == nowSocial)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SOCIAL_SERVICE_OF_FIREND_FULL));

                    return;
                }

                break;
            }
            case BLACK:
            {
            	nowSocial = socialRelationList.add(BLACK, _target);

                if (null == nowSocial)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SOCIAL_SERVICE_OF_BLACK_FULL));

                    return;
                }

                break;
            }
            case ENEMY:
            {
                if (_player.getClan() == _target.getClan())
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SOCIAL_SERVICE_OF_SAME_CLAN));

                    return;
                }

                nowSocial = socialRelationList.add(ENEMY, _target);

                if (null == nowSocial)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SOCIAL_SERVICE_OF_FIREND_FULL));

                    return;
                }

                break;
            }
        }
        if (null != nowSocial)
        {
        	log.info("他们的关系:" + complexRelation);
        	if (complexRelation == 1) {
        		remove(_player, _target.getName(), (byte)1, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), 
                		_target.getName(), _type, _target.getVocation().value(), _target.getLevel());
			} else if (complexRelation == 2) {
        		remove(_player, _target.getName(), (byte)1, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), 
                		_target.getName(), _type, _target.getVocation().value(), _target.getLevel());
			} else if (complexRelation == 3) {
        		remove(_player, _target.getName(), (byte)3, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), 
                		_target.getName(), _type, _target.getVocation().value(), _target.getLevel());
			} else if (complexRelation == 5) {
        		remove(_player, _target.getName(), (byte)2, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), 
                		_target.getName(), _type, _target.getVocation().value(), _target.getLevel());
			} else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), 
                		_target.getName(), _type, _target.getVocation().value(), _target.getLevel());
			}
        }
    }

    /**
     * 删除社交关系
     * 
     * @param _player 操作者
     * @param _targetName 目标名称
     * @param _type 加入类型
     * @param _real 是否在线
     */
    public void remove (HeroPlayer _player, String _targetName, byte _type,
            boolean _real)
    {
        SocialRelationList socialRelationList = container.get(_player
                .getUserID());

        if (null != socialRelationList)
        {
            SocialObjectProxy socialObjectProxy = socialRelationList.remove(_targetName, _type);

            if (null != socialObjectProxy)
            {
                SocialDAO.removeOne(_player.getUserID(),
                        socialObjectProxy.userID);
            }
        }
    }

    /**
     * 获得指定类型的社交关系列表
     * 
     * @param _userID 玩家编号
     * @param _socialRelationType 个人社交关系类型
     * @return
     */
    public ArrayList<SocialObjectProxy> getSocialRelationList (int _userID,
            ESocialRelationType _socialRelationType)
    {
        SocialRelationList socialRelationList = container.get(_userID);

        if (null != socialRelationList)
        {
            ArrayList<SocialObjectProxy> socialObjectProxyList = socialRelationList
                    .getSocialRelationList(_socialRelationType);

            if (null != socialObjectProxyList
                    && socialObjectProxyList.size() > 0)
            {
                HeroPlayer player;

                for (SocialObjectProxy socialObjectProxy : socialObjectProxyList)
                {
                    player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                            socialObjectProxy.userID);

                    if (null != player && player.isEnable())
                    {
                        socialObjectProxy.setOnlineStatus(player.getLevel(),
                                player.getVocation(), player.getSex());
                    }
                    else
                    {
                        socialObjectProxy.isOnline = false;
                    }
                }

                return socialObjectProxyList;
            }
        }

        return null;
    }

    /**
     * 是否被屏蔽
     * 
     * @param _beUserID
     * @param _hostUserID
     * @return
     */
    public boolean beBlack (int _beUserID, int _hostUserID)
    {
        SocialRelationList socialRelationList = container.get(_hostUserID);

        if (null != socialRelationList)
            return socialRelationList.isBlack(_beUserID);

        return false;
    }

    /**
     * 是否是朋友
     * @param _beUserID
     * @param _hostUserID
     * @return
     */
    public boolean beFriend(int _beUserID, int _hostUserID){
        SocialRelationList socialRelationList = container.get(_hostUserID);

        if (null != socialRelationList)
            return socialRelationList.isFriend(_beUserID);

        return false;
    }
    
    /**
     * <p>刨根问底的查询,如果内存没有,要去数据库查询<p>
     * 用于_hostUser为离线状态的时候.比如_hostUser未登录
     * 但是其他玩家给他发了邮件我们也需要知道其他玩家是他什么人
     * @param _beUserID 当前这个家伙到底是不是我朋友
     * @param _hostUserID  查询者
     * @param _bySql
     * @return
     */
    public boolean beFriend(String _beUser, String _hostUser, boolean _bySql)
    {
    	int hostUserID = PlayerServiceImpl.getInstance().getPlayerByName(_hostUser).getUserID();
    	int beUserID = PlayerServiceImpl.getInstance().getPlayerByName(_beUser).getUserID();
        SocialRelationList socialRelationList = container.get(hostUserID);

        if (null != socialRelationList)
        {
        	return socialRelationList.isFriend(beUserID);
        }
        else
        {
        	return SocialDAO.beFriend(_beUser, hostUserID);
        }
    }

    /**
     * 在登陆界面删除角色
     * 
     * @param _userID
     */
    public void deleteRole (int _userID)
    {
        SocialDAO.removeAll(_userID);

        Iterator<SocialRelationList> iterator = container.values().iterator();
        SocialRelationList socialRelationList;

        while (iterator.hasNext())
        {
            socialRelationList = iterator.next();

            socialRelationList.remove(_userID);
        }
    }

    @Override
    public void createSession (Session _session)
    {
        SocialRelationList socialRelationList = container.get(_session.userID);

        if (socialRelationList == null)
        {
            socialRelationList = new SocialRelationList();
            SocialDAO.load(_session.userID, socialRelationList);
            container.put(_session.userID, socialRelationList);
        }

        Iterator iterator = container.entrySet().iterator();
        
        
        while (iterator.hasNext())
        {
        	Map.Entry pairs = (Map.Entry)iterator.next();
        	int userID = Integer.valueOf(pairs.getKey().toString());
        	HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
        	if(player == null) {
        		continue;
        	}
            socialRelationList = (SocialRelationList)pairs.getValue();

            if (socialRelationList.isEnemy(_session.userID))
            {
                HeroPlayer beNotifier = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);

                if (null != beNotifier && beNotifier.isEnable())
                {
//                    OutMsgQ.getInstance().put(beNotifier.getMsgQueueIndex(),
//                            new Warning(TIP_OF_LOGIN + _session.nickName));
                	ChatServiceImpl.getInstance().sendSinglePlayer(beNotifier.getName(), "请注意" 
                			+ _session.nickName + "已经上线，请速去追杀！");
                }
            }
        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        Iterator<SocialRelationList> iterator = container.values().iterator();

        SocialRelationList socialRelationList;

        while (iterator.hasNext())
        {
            socialRelationList = iterator.next();

            if (socialRelationList.isEnemy(_session.userID))
            {
                HeroPlayer beNotifier = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(_session.userID);

                if (null != beNotifier && beNotifier.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(beNotifier.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SOCIAL_SERVICE_OF_LOGOUT + _session.nickName));
                }
            }
        }
    }

    @Override
    public void clean (int _userID)
    {
        synchronized (container)
        {
            container.remove(_userID);
        }
    }

    /**
     * 好友人数上限
     */
    public static final int    MAX_NUMBER_OF_FRIEND      = 200;

    /**
     * 屏蔽人数上限
     */
    public static final int    MAX_NUMBER_OF_BLACK       = 50;
    
    /**
     * 仇人数量上限
     */
    public static final int    MAX_NUMBER_OF_ENEMY       = 50;



}
