package hero.npc.others;

import java.util.Random;

import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.dict.AnimalImageDict;
import hero.npc.dict.AnimalDataDict.AnimalData;
import hero.npc.message.AnimalWalkNotify;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.Direction;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Animal.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-8 下午12:57:30
 * @描述 ：自由行走的动物
 */

public class Animal extends ME2OtherGameObject
{
    /**
     * 随机数生成器
     */
    private static final Random RANDOM_BUILDER = new Random();

    /**
     * 当遇到不可通行的格子时，反复尝试新方向的最多次数
     */
    private static byte         TRACK_TIMES    = 4;

    /**
     * 原始位置x,y坐标
     */
    private short               orgX, orgY;

    /**
     * 面向
     */
    private byte                direction;

    /**
     * 行走时离原始位置的最远范围
     */
    private byte                fastestWalkRange;
    
    private short                animationID;

    /**
     * 图片字节
     */
    private byte[]              image;

    /**
     * 构造
     * 
     * @param _data
     */
    public Animal(AnimalData _data)
    {
        super(_data.modelID, _data.imageID);
        fastestWalkRange = _data.fastestWalkRange;
        image = AnimalImageDict.getInstance().getAnimalImageBytes(getImageID());
        direction = getRandomDirection();
        animationID = _data.animationID;
    }

    /**
     * 获取面向
     * 
     * @return
     */
    public byte getDirection ()
    {
        return direction;
    }

    /**
     * 设置面向
     * 
     * @param _direction
     */
    public void setDirection (byte _direction)
    {
        direction = _direction;
    }

    /**
     * 设置原始位置X坐标
     * 
     * @param _x
     */
    public void setOrgX (int _x)
    {
        orgX = (short) _x;
    }

    /**
     * 获得原始位置X坐标
     * 
     * @return
     */
    public short getOrgX ()
    {
        return orgX;
    }

    /**
     * 设置原始位置Y坐标
     * 
     * @param _y
     */
    public void setOrgY (int _y)
    {
        orgY = (short) _y;
    }

    /**
     * 获取原始位置Y坐标
     * 
     * @return
     */
    public short getOrgY ()
    {
        return orgY;
    }
    
    /**
     * 动物动画
     * @return
     */
    public short getAnimationID ()
    {
        return animationID;
    }

    /**
     * 获取图片
     * 
     * @return
     */
    public byte[] getImage ()
    {
        return image;
    }

    /**
     * 向某方向行走1格
     * 
     * @param _direction 方向
     */
    public void go (byte _direction)
    {
        switch (_direction)
        {
            case Direction.UP:
            {
                setCellY((short) (getCellY() - 1));
                setDirection(_direction);

                break;
            }
            case Direction.DOWN:
            {
                setCellY((short) (getCellY() + 1));
                setDirection(_direction);

                break;
            }
            case Direction.LEFT:
            {
                setCellX((short) (getCellX() - 1));
                setDirection(_direction);

                break;
            }
            case Direction.RIGHT:
            {
                setCellX((short) (getCellX() + 1));
                setDirection(_direction);

                break;
            }
        }
    }

    /**
     * 按照路径行走
     * 
     * @param _movePath
     */
    public void goAlone (byte[] _movePath)
    {
        for (byte direction : _movePath)
        {
            go(direction);
        }
    }

    /**
     * 走动
     */
    public void walk ()
    {
        byte[] moveActions = new byte[NotPlayerServiceImpl.getInstance()
                .getConfig().ANIMAL_WALK_GRID_NUM_PER_TIME];
        direction = getRandomDirection();

        byte newDirection = 0;

        byte step = 0;

        for (; step < NotPlayerServiceImpl.getInstance().getConfig().ANIMAL_WALK_GRID_NUM_PER_TIME; step++)
        {
            newDirection = trackNext(true, 1);

            if (0 < newDirection)
            {
                go(newDirection);
                moveActions[step] = newDirection;
            }
            else
            {
                break;
            }
        }

        MapSynchronousInfoBroadcast.getInstance().put(
        		where,
                new AnimalWalkNotify(
                		getID(), moveActions, animationID, (byte)getCellX(), (byte)getCellY()), 
                false, 0);
    }

    /**
     * 循路递归
     * 
     * @param _trackTimes 当前是第几次循路
     * @return 已经做过的循路次数
     */
    private byte trackNext (boolean _first, int _trackTimes)
    {
        if (_first)
        {
            if (!passable(direction))
            {
                return trackNext(false, ++_trackTimes);
            }
            else
            {
                return direction;
            }
        }
        else
        {
            if (_trackTimes <= TRACK_TIMES)
            {
                switch (direction)
                {
                    case Direction.UP:
                    {
                        direction = Direction.RIGHT;

                        if (!passable(Direction.RIGHT))
                        {
                            return trackNext(false, ++_trackTimes);
                        }
                        else
                        {
                            return Direction.RIGHT;
                        }
                    }
                    case Direction.DOWN:
                    {
                        direction = Direction.LEFT;

                        if (!passable(Direction.LEFT))
                        {
                            return trackNext(false, ++_trackTimes);
                        }
                        else
                        {
                            return Direction.LEFT;
                        }
                    }
                    case Direction.LEFT:
                    {
                        direction = Direction.UP;

                        if (!passable(Direction.UP))
                        {
                            return trackNext(false, ++_trackTimes);
                        }
                        else
                        {
                            return Direction.UP;
                        }
                    }
                    case Direction.RIGHT:
                    {
                        direction = Direction.DOWN;

                        if (!passable(Direction.DOWN))
                        {
                            return trackNext(false, ++_trackTimes);
                        }
                        else
                        {
                            return Direction.DOWN;
                        }
                    }
                    default:
                    {
                        return 0;
                    }
                }
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * 能否穿过
     * 
     * @param _direction
     * @return
     */
    private boolean passable (byte _direction)
    {
        switch (_direction)
        {
            case Direction.UP:
            {
                if (inMoveRange(getCellX(), getCellY() - 1))
                {
                    return where.isRoad(getCellX(), getCellY() - 1);
                }

                break;
            }
            case Direction.DOWN:
            {
                if (inMoveRange(getCellX(), getCellY() + 1))
                {
                    return where.isRoad(getCellX(), getCellY() + 1);
                }

                break;
            }
            case Direction.LEFT:
            {
                if (inMoveRange(getCellX() - 1, getCellY()))
                {
                    return where.isRoad(getCellX() - 1, getCellY());
                }

                break;
            }
            case Direction.RIGHT:
            {
                if (inMoveRange(getCellX() + 1, getCellY()))
                {
                    return where.isRoad(getCellX() + 1, getCellY());
                }

                break;
            }
        }

        return false;
    }

    /**
     * 是否在可移动的范围内
     * 
     * @param _targetX
     * @param _targetY
     * @return
     */
    private boolean inMoveRange (int _targetX, int _targetY)
    {
//        double distanc = Math.sqrt(Math.pow((orgX - _targetX) * 16, 2)
//                + Math.pow((orgY - _targetY) * 16, 2));

        boolean inDistance = ((orgX - _targetX) * 16)*((orgX - _targetX) * 16)+((orgY - _targetY) * 16)*((orgY - _targetY) * 16)
                    <= (fastestWalkRange * 16)*(fastestWalkRange * 16);

//        return distanc <= fastestWalkRange * 16;
        return inDistance;
    }

    /**
     * 获取随机方向
     * 
     * @return
     */
    private byte getRandomDirection ()
    {
        return (byte) (RANDOM_BUILDER.nextInt(4) + 1);
    }
}
