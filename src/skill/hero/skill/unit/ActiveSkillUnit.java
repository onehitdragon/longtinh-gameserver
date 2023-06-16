package hero.skill.unit;

import hero.effect.Effect.EffectFeature;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.EHarmType;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.share.EMagic;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ActiveSkillUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 下午02:29:33
 * @描述 ：主动技能单元，是玩家或怪物主动使用而产生动作，包括伤害和恢复型、清除效果型
 */

public class ActiveSkillUnit extends SkillUnit
{
    /**
     * @param _id
     * @param _name
     */
    public ActiveSkillUnit(int _id)
    {
        super(_id);
        // TODO Auto-generated constructor stub
    }

    /**
     * 技能类型
     */
    public EActiveSkillType       activeSkillType;

    /**
     * 消耗生命值
     */
    public int                    consumeHp;

    /**
     * 消耗魔法值
     */
    public int                    consumeMp;

    /**
     * 消耗力值
     */
    public short                  consumeFp;

    /**
     * 消耗气值
     */
    public short                  consumeGp;

    /**
     * 目标类型
     */
    public ETargetType            targetType;

    /**
     * 目标范围类型
     */
    public ETargetRangeType       targetRangeType;

    /**
     * 目标范围数量
     */
    public byte                   rangeTargetNumber;

    /**
     * 秒
     */
    public float                  releaseTime;

    /**
     * 目标距离（格子数）
     */
    public byte                   targetDistance;

    /**
     * 范围性法术范围基准
     */
    public EAOERangeBaseLine      rangeBaseLine;

    /**
     * 范围性法术范围模式（中心模式、前方矩形模式）
     */
    public EAOERangeType          rangeMode;

    /**
     * 范围法术范围宽度
     */
    public byte                   rangeX;

    /**
     * 范围性法术范围高度
     */
    public byte                   rangeY;
    
    /**
     * <p>动画作用形式</p>
     * 1=单独一个大动画效果; 2=对目标逐个施展小效果
     */
    public byte                   animationModel;

    /**
     * 是否分摊技能结果
     */
    public boolean                isAverageResult;

    /**
     * 物理伤害类型
     */
    public EHarmType              physicsHarmType;

    /**
     * 直接物理伤害
     */
    public int                    physicsHarmValue;

    /**
     * 武器伤害倍数
     */
    public float                  weaponHarmMult;

    /**
     * 需要魔法命中s
     */
    public boolean                needMagicHit;

    /**
     * 法术伤害属性
     */
    public EMagic                 magicHarmType;

    /**
     * 法术伤害生命值值
     */
    public int                    magicHarmHpValue;

    /**
     * 法术伤害魔法值
     */
    public int                    magicHarmMpValue;

    /**
     * 恢复生命值
     */
    public int                    resumeHp;

    /**
     * 恢复魔法值
     */
    public int                    resumeMp;

    /**
     * 产生的仇恨值
     */
    public int                    hateValue;

    /**
     * 驱散目标身上效果的几率
     */
    public float                  cleanEffectOdds;

    /**
     * 驱散的效果特质
     */
    public EffectFeature          cleanEffectFeature;

    /**
     * 驱散的效果的最高等级
     */
    public byte                   cleanEffectMaxLevel;

    /**
     * 每次驱散的效果数量
     */
    public short                  cleanEffectNumberPerTimes;

    /**
     * 可驱散的具体效果编号下限（针对具体效果驱散）
     */
    public int                    cleanDetailEffectLowerID;

    /**
     * 可驱散的具体效果编号上限（针对具体效果驱散）
     */
    public int                    cleandetailEffectUperID;

    /**
     * 附带的可能产生的技能或效果单元信息列表（这部分数据不可以被强化）
     */
    public AdditionalActionUnit[] additionalOddsActionUnitList;
    
    /**
     * 施放动作编号
     */
    public byte                   animationAction;

    /**
     * 施放动画编号
     */
    public short                  releaseAnimationID;
    
    /**
     * 施法图片ID
     */
    public short                  releaseImageID;
    
    /**
     * 施放动画特效层级
     */
    public byte                   tierRelation;
    
    /**
     * 承受技能播放高度关系
     * 1=脚下播放 (力量光环,闪电召唤等)
     * 2=中心位置播放 (火球术等)
     * 3=头顶播放 (小天使光圈等)
     */
    public byte                   heightRelation;
    
    /**
     * 释放技能播放高度关系
     * 1=脚下播放 (力量光环,闪电召唤等)
     * 2=中心位置播放 (火球术等)
     * 3=头顶播放 (小天使光圈等)
     */
    public byte                   releaseHeightRelation;
    
    /**
     * 技能是否分方向
     */
    public byte			     	  isDirection;

    /**
     * 作用动画编号
     */
    public short                  activeAnimationID;
    
    /**
     * 作用动画图片ID
     */
    public short                  activeImageID;

    /**
     * 引发的主动触发方式，用于施放者身上的被动触发技能和触发效果
     */
    public ETouchType             activeTouchType;

    /**
     * 引发的被动触发方式，用于目标的身上的被动触发技能和触发效果
     */
    public ETouchType             passiveTouchType;

    /**
     * 是否需要目标
     * 
     * @return
     */
    public boolean isNeedTarget ()
    {
        return targetRangeType == ETargetRangeType.SINGLE
                || rangeBaseLine == EAOERangeBaseLine.TARGET;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public SkillUnit clone () throws CloneNotSupportedException
    {
        return (SkillUnit) super.clone();
    }

}
