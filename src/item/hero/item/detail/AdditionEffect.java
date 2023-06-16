package hero.item.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AdditionEffect.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-28 下午02:47:57
 * @描述 ：
 */

public class AdditionEffect
{
    /**
     * 效果单元
     */
    public int   effectUnitID;

    /**
     * 作用次数（包括每次作用后，如果在几率范围内再次作用的次数）
     */
    public byte  activeTimes;

    /**
     * 作用几率（每次发生作用的几率）
     */
    public float activeOdds;
}
