package hero.share.service;

import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GameObjectChecker.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-16 上午10:26:31
 * @描述 ：功能类，对游戏对象进行各种条件下的有效性判断
 */

public class GameObjectChecker
{
    /**
     * 私有构造，不可以
     */
    private GameObjectChecker()
    {

    }

    /**
     * 检查对象有效性
     * 
     * @param _object
     * @return
     */
    public static boolean checkValidity (ME2GameObject _object)
    {
        return null != _object && _object.isEnable();
    }

    /**
     * 检查是否死亡状态
     * 
     * @param _object 游戏对象
     * @param _isDead 是否死亡状态
     * @return
     */
    public static boolean checkValidity (ME2GameObject _object, boolean _isDead)
    {
        return null != _object && _object.isEnable()
                && _isDead == _object.isDead();
    }

    /**
     * 检查对象是否是有效活动状态
     * 
     * @param _object
     * @return
     */
    public static boolean checkActiveValidity (ME2GameObject _object)
    {
        return null != _object && _object.isEnable() && !_object.isDead();
    }

    /**
     * 检查双方是否是活动对象
     * 
     * @param _one
     * @param _another
     * @return
     */
    public static boolean checkActiveValidity (ME2GameObject _one,
            ME2GameObject _another)
    {
        return null != _one && _one.isEnable() && !_one.isDead()
                && null != _another && _another.isEnable()
                && !_another.isDead();
    }
}
