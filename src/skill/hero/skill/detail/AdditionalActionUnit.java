package hero.skill.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AdditionalActionUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 下午04:33:47
 * @描述 ：额外的作用，可能触发其他技能单元或产生其他效果
 */

public class AdditionalActionUnit
{
    /**
     * 技能或效果单元的编号
     */
    public int   skillOrEffectUnitID;

    /**
     * 作用次数（包括每次作用后，如果在几率范围内再次作用的次数）
     */
    public byte  activeTimes;

    /**
     * 作用几率（每次发生作用的几率）
     */
    public float activeOdds;
}
