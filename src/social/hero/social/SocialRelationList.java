package hero.social;

import static hero.social.ESocialRelationType.BLACK;
import static hero.social.ESocialRelationType.ENEMY;
import static hero.social.ESocialRelationType.FRIEND;

import hero.player.HeroPlayer;
import hero.social.service.SocialServiceImpl;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SocialRelationList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-9 上午10:08:23
 * @描述 ：社交关系列表（包括友好、仇人、屏蔽三种关系）
 */

public class SocialRelationList
{
    /**
     * 容器
     */
    private ArrayList<SocialObjectProxy> list = new ArrayList<SocialObjectProxy>();

    /**
     * 好友数量
     */
    private short                        friendNumber;

    /**
     * 仇人数量
     */
    private short                        enemyNumber;

    /**
     * 屏蔽数量
     */
    private short                        blackNumber;

    /**
     * 获得个人社交关系列表
     *
     * @param _socialRelationType
     * @return
     */
    public ArrayList<SocialObjectProxy> getSocialRelationList (
            ESocialRelationType _socialRelationType)
    {
        ArrayList<SocialObjectProxy> socialRelationList = new ArrayList<SocialObjectProxy>();

        for (SocialObjectProxy socialObjectProxy : list)
        {
            if (socialObjectProxy.socialRelationType == _socialRelationType)
            {
                socialRelationList.add(socialObjectProxy);
            }
        }

        if (socialRelationList.size() > 0)
            return socialRelationList;
        else
            return null;
    }

    /**
     * 获得关系
     *
     * @param _userID 要查找的玩家编号
     * @return
     */
    public SocialObjectProxy getSocialObjectProxy (int _userID)
    {
        for (SocialObjectProxy socialObjectProxy : list)
        {
            if (socialObjectProxy.userID == _userID)
            {
                return socialObjectProxy;
            }
        }

        return null;
    }

    /**
     * 获得关系
     *
     * @param _nickname 要查找的玩家名字
     * @return
     */
    public SocialObjectProxy getSocialObjectProxy (String _name)
    {
        for (SocialObjectProxy socialObjectProxy : list)
        {
            if (socialObjectProxy.name.equals(_name))
            {
                return socialObjectProxy;
            }
        }

        return null;
    }

    /**
     * 是否被屏蔽
     *
     * @param _userID
     * @return
     */
    public boolean isBlack (int _userID)
    {
        if (blackNumber > 0)
        {
            for (SocialObjectProxy socialObjectProxy : list)
            {
                if (socialObjectProxy.socialRelationType == ESocialRelationType.BLACK
                        && socialObjectProxy.userID == _userID)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 是否是仇人
     *
     * @param _userID
     * @return
     */
    public boolean isEnemy (int _userID)
    {
        if (enemyNumber > 0)
        {
            for (SocialObjectProxy socialObjectProxy : list)
            {
                if (socialObjectProxy.socialRelationType == ESocialRelationType.ENEMY
                        && socialObjectProxy.userID == _userID)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 是否是朋友
     * @param _userID
     * @return
     */
    public boolean isFriend(int _userID)
    {
        if(friendNumber > 0){
            for (SocialObjectProxy socialObjectProxy : list)
            {
                if (socialObjectProxy.socialRelationType == ESocialRelationType.FRIEND
                        && socialObjectProxy.userID == _userID)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 向关系列表中添加社交关系
     *
     * @param _socialRelationType
     * @param _playerUserID
     * @param _playerName
     * @param _isOnline
     * @return
     */
    public SocialObjectProxy add (ESocialRelationType _socialRelationType,
            HeroPlayer _target)
    {
        SocialObjectProxy socialObjectProxy = null;

        switch (_socialRelationType)
        {
            case FRIEND:
            {
                if (friendNumber < SocialServiceImpl.MAX_NUMBER_OF_FRIEND)
                {
                    socialObjectProxy = new SocialObjectProxy(FRIEND, _target
                            .getUserID(), _target.getName());

                    list.add(socialObjectProxy);
                    socialObjectProxy.setOnlineStatus(_target.getLevel(),
                            _target.getVocation(), _target.getSex());
                    friendNumber++;
                }

                break;
            }
            case ENEMY:
            {
                if (enemyNumber < SocialServiceImpl.MAX_NUMBER_OF_ENEMY)
                {
                    socialObjectProxy = new SocialObjectProxy(ENEMY, _target
                            .getUserID(), _target.getName());

                    list.add(socialObjectProxy);
                    socialObjectProxy.setOnlineStatus(_target.getLevel(),
                            _target.getVocation(), _target.getSex());
                    enemyNumber++;
                }

                break;
            }
            case BLACK:
            {
                if (blackNumber < SocialServiceImpl.MAX_NUMBER_OF_BLACK)
                {
                    socialObjectProxy = new SocialObjectProxy(BLACK, _target
                            .getUserID(), _target.getName());

                    list.add(socialObjectProxy);
                    socialObjectProxy.setOnlineStatus(_target.getLevel(),
                            _target.getVocation(), _target.getSex());
                    blackNumber++;
                }

                break;
            }
            default:
            {
                return null;
            }
        }

        return socialObjectProxy;
    }

    /**
     * 从数据库中加载社交关系
     *
     * @param _socialRelationType
     * @param _playerUserID
     * @param _playerName
     * @param _isOnline
     * @return
     */
    public void add (byte _socialRelationTypeValue, int _playerUserID,
            String _playerName)
    {
        SocialObjectProxy socialObjectProxy = new SocialObjectProxy(
                _socialRelationTypeValue, _playerUserID, _playerName);
        list.add(socialObjectProxy);

        switch (socialObjectProxy.socialRelationType)
        {
            case FRIEND:
            {
                friendNumber++;

                break;
            }
            case ENEMY:
            {
                enemyNumber++;

                break;
            }
            case BLACK:
            {
                blackNumber++;

                break;
            }
        }
    }

    /**
     * 删除社交关系
     *
     * @param _name
     * @return
     */
    public SocialObjectProxy remove (String _name, byte _type)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).name.equals(_name))
            {
                SocialObjectProxy socialObjectProxy = list.get(i);
                if(socialObjectProxy.socialRelationType.value != _type) {
                	continue;
                }
                socialObjectProxy = list.remove(i);
                switch (socialObjectProxy.socialRelationType)
                {
                    case FRIEND:
                    {
                        friendNumber--;

                        break;
                    }
                    case ENEMY:
                    {
                        enemyNumber--;

                        break;
                    }
                    case BLACK:
                    {
                        blackNumber--;

                        break;
                    }
                }

                return socialObjectProxy;
            }
        }

        return null;
    }

    /**
     * 删除社交关系
     *
     * @param _userID
     * @return
     */
    public SocialObjectProxy remove (int _userID)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).userID == _userID)
            {
                SocialObjectProxy socialObjectProxy = list.remove(i);

                switch (socialObjectProxy.socialRelationType)
                {
                    case FRIEND:
                    {
                        friendNumber--;

                        break;
                    }
                    case ENEMY:
                    {
                        enemyNumber--;

                        break;
                    }
                    case BLACK:
                    {
                        blackNumber--;

                        break;
                    }
                }

                return socialObjectProxy;
            }
        }

        return null;
    }
}
