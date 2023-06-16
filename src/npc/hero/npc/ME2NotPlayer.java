package hero.npc;

import hero.map.Map;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.share.Direction;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ME2NotPlayer.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-10 下午04:38:34
 * @描述 ：
 */

public abstract class ME2NotPlayer extends ME2GameObject
{
    /**
     * NPC模板编号
     */
    private String    modelID;

    /**
     * 图片编号
     */
    private short     imageID;
    
    /**
     * 动画id
     */
    private short     animationID;

    /**
     * 刷新时所在地图
     */
    private Map       orgMap;

    /**
     * 原始坐标
     */
    private short     orgX, orgY,orgZ;

    /**
     * 刷新出来的时间
     */
    private long      refreshTime;

    /**
     * 是否是召唤出来的
     */
    protected boolean isCalled;

    /**
     * 存在时间
     */
    private int       existsTime;

    /**
     * 构造
     */
    public ME2NotPlayer()
    {
        super();
        refreshTime = System.currentTimeMillis();
    }

    /**
     * 获取原始所在地图
     * 
     * @return
     */
    public Map getOrgMap ()
    {
        return orgMap;
    }

    /**
     * 设置原始所在地图
     * 
     * @param _orgMap
     */
    public void setOrgMap (Map _orgMap)
    {
        orgMap = _orgMap;
    }

    /**
     * 获取原始坐标X
     * 
     * @return
     */
    public short getOrgX ()
    {
        return orgX;
    }

    /**
     * 设置原始坐标X
     * 
     * @param orgX 坐标X
     */
    public void setOrgX (short _orgX)
    {
        orgX = _orgX;
    }

    /**
     * 获取原始坐标Y
     * 
     * @return
     */
    public short getOrgY ()
    {
        return orgY;
    }

    /**
     * 设置原始坐标Y
     * 
     * @param orgY 原始坐标Y
     */
    public void setOrgY (short _orgY)
    {
        orgY = _orgY;
    }
    
    /**
     * 获取原始坐标Z
     * 
     * @return
     */
    public short getOrgZ ()
    {
        return orgZ;
    }

    /**
     * 设置原始坐标Z
     * 
     * @param orgY 原始坐标Z
     */
    public void setOrgZ (short _orgZ)
    {
        orgZ = _orgZ;
    }

    /**
     * 是否是召唤怪
     * 
     * @return
     */
    public boolean isCalled ()
    {
        return isCalled;
    }

    /**
     * 获取刷新出来的时间
     * 
     * @return
     */
    public long getRefreshTime ()
    {
        return refreshTime;
    }

    /**
     * 获取召唤类型怪的总存在时间
     * 
     * @return
     */
    public int getExistsTime ()
    {
        return existsTime;
    }

    /**
     * 设置因某种原因刷新出来的总存在时间
     * 
     * @param _time
     */
    public void setExistsTime (int _time)
    {
        isCalled = true;
        existsTime = _time;
    }

    /**
     * 设置图片编号
     * 
     * @param _imageID
     */
    public void setImageID (short _imageID)
    {
        imageID = _imageID;
    }

    /**
     * 获取图片编号
     * 
     * @return
     */
    public short getImageID ()
    {
        return imageID;
    }
    
    /**
     * 获取动画ID
     * @return
     */
    public short getAnimationID ()
    {
        return animationID;
    }
    
    /**
     * 设置动画ID
     * @param _animationID
     */
    public void setAnimationID (short _animationID)
    {
    	animationID = _animationID;
    }

    /**
     * 行走
     * 
     * @param _movePath 路径
     */
    public void goAlone (byte[] _movePath, HeroPlayer _attackTarget)
    {
        for (byte direction : _movePath)
        {
            go(direction);
        }

        NotPlayerServiceImpl.getInstance().broadcastNotPlayerWalkPath(this,
                _movePath, _attackTarget);
    }

    /**
     * 位置是否在合法的行走范围内
     * 
     * @param _targetX
     * @param _targetY
     * @return
     */
    private boolean inMoveRange (int _targetX, int _targetY)
    {
//        double distanc = Math.sqrt(Math.pow((orgX - _targetX) * 16, 2)
//                + Math.pow((orgY - _targetY) * 16, 2));

        int monster_move_most_fast_grid = NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_MOST_FAST_GRID * 16;

        boolean inDistance = ((orgX - _targetX) * 16)*((orgX - _targetX) * 16)+((orgY - _targetY) * 16)*((orgY - _targetY) * 16)
                                <= monster_move_most_fast_grid * monster_move_most_fast_grid;

        return inDistance ;
    }

    /**
     * 能否穿过
     * 
     * @param _direction
     * @return
     */
    public boolean passable (byte _direction)
    {
        switch (_direction)
        {
            case Direction.UP:
            {
                if (inMoveRange(getCellX(), getCellY() - 1))
                {
                    return where().isRoad(getCellX(), getCellY() - 1);
                }

                break;
            }
            case Direction.DOWN:
            {
                if (inMoveRange(getCellX(), getCellY() + 1))
                {
                    return where().isRoad(getCellX(), getCellY() + 1);
                }

                break;
            }
            case Direction.LEFT:
            {
                if (inMoveRange(getCellX() - 1, getCellY()))
                {
                    return where().isRoad(getCellX() - 1, getCellY());
                }

                break;
            }
            case Direction.RIGHT:
            {
                if (inMoveRange(getCellX() + 1, getCellY()))
                {
                    return where().isRoad(getCellX() + 1, getCellY());
                }

                break;
            }
        }

        return false;
    }

    /**
     * NPC模板编号
     * 
     * @param _modelID
     */
    public void setModelID (String _modelID)
    {
        modelID = _modelID;
    }

    /**
     * 获取NPC模板编号
     * 
     * @return
     */
    public String getModelID ()
    {
        return modelID;
    }

    /**
     * 销毁
     */
    public abstract void destroy ();
}
