package hero.effect.service;

import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 N_EffectServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-13 下午05:54:55
 * @描述 ：
 */

public class ObjectCheckor
{
    /**
     * 检查两个对象的有效性
     * 
     * @param _one
     * @param _other
     * @return
     */
    public static boolean isValidate (ME2GameObject _one, ME2GameObject _other)
    {
        return ((null != _other && _other.isEnable() && !_other.isDead()) && (null != _one
                && _one.isEnable() && !_one.isDead())) ? true : false;
    }

    /**
     * 检查单个目标的合法性
     * 
     * @param _object 游戏对象
     * @return
     */
    public static boolean isValidate (ME2GameObject _object)
    {
        return (null != _object && _object.isEnable() && !_object.isDead());
    }
}
