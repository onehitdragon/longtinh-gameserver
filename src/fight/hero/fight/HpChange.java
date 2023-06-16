package hero.fight;

import hero.map.Map;
import hero.share.EVocationType;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 HpChange.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-6 下午01:54:03
 * @描述 ：生命值变化信息
 */

public class HpChange
{
    /**
     * 触发者对象编号
     */
    public int           triggerID;

    /**
     * 变化者对象编号
     */
    public int           changerID;

    /**
     * 变化者对象类型
     */
    public byte          changerObjectType;

    /**
     * 变化者职业类型
     */
    public EVocationType changerVocationType;

    /**
     * 当前生命值
     */
    public int           currentHp;

    /**
     * 变化的生命值
     */
    public int           changeHpValue;

    /**
     * 所在地图
     */
    public Map           where;
}
