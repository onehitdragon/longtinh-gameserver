package hero.skill.unit;

import java.util.ArrayList;
import java.util.Random;

import hero.effect.service.EffectServiceImpl;
import hero.fight.service.FightServiceImpl;
import hero.player.HeroPlayer;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.skill.dict.SkillUnitDict;
import hero.skill.service.SkillServiceImpl;
import hero.share.Constant;
import hero.share.EMagic;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangePropertyUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 下午06:05:39
 * @描述 ：改变宿主属性的被动技能，同时在某种情况下会被触发产生额外的作用
 */

public class ChangePropertyUnit extends PassiveSkillUnit
{
    /**
     * @param _id
     * @param _name
     */
    public ChangePropertyUnit(int _id)
    {
        super(_id);
        // TODO Auto-generated constructor stub
    }

    /**
     * 运算符
     */
    public EMathCaluOperator      caluOperator;

    /**
     * 力量
     */
    public float                  strength;

    /**
     * 敏捷
     */
    public float                  agility;

    /**
     * 耐力
     */
    public float                  stamina;

    /**
     * 智力
     */
    public float                  inte;

    /**
     * 精神
     */
    public float                  spirit;

    /**
     * 幸运
     */
    public float                  lucky;

    /**
     * 物理防御
     */
    public float                  defense;

    /**
     * 命中等级
     */
    public float                  hitLevel;

    /**
     * 物理闪避等级
     */
    public float                  physicsDuckLevel;

    /**
     * 物理暴击等级
     */
    public float                  physicsDeathblowLevel;

    /**
     * 魔法暴击等级
     */
    public float                  magicDeathblowLevel;

    /**
     * 生命值（上限）
     */
    public float                  maxHp;

    /**
     * 魔法值（上限）
     */
    public float                  maxMp;

    /**
     * 仇恨值
     */
    public float                  hate;

    /**
     * 魔法抗性类型
     */
    public EMagic                 magicFastnessType;

    /**
     * 魔法抗性改变数值
     */
    public float                  magicFastnessValue;

    /**
     * 物理攻击伤害
     */
    public float                  physicsAttackHarmValue;

    /**
     * 受的物理攻击伤害
     */
    public float                  bePhysicsHarmValue;

    /**
     * 魔法伤害类型
     */
    public EMagic                 magicHarmType;

    /**
     * 魔法伤害改变数值
     */
    public float                  magicHarmValue;

    /**
     * 承受的魔法伤害类型
     */
    public EMagic                 magicHarmTypeBeAttack;

    /**
     * 承受的魔法伤害改变数值
     */
    public float                  magicHarmValueBeAttack;

    /**
     * 普通物理攻击间隔时间
     */
    public float                  physicsAttackInterval;

    /**
     * 所有技能施放受影响的时间（秒）
     */
    public float                  allSkillReleaseTime;

    /**
     * 影响施放时间的特殊技能编号列表，可以是多个
     */
    public ArrayList<Integer>     specialSkillReleaseTimeIDList;

    /**
     * 特殊技能施放受影响的时间（秒）
     */
    public float                  specialSkillReleaseTime;

    /**
     * 抵抗属性类型
     */
    public ESpecialStatus         resistSpecialStatus;

    /**
     * 抵抗属性几率
     */
    public float                  resistSpecialStatusOdds;

    /**
     * 所有附带效果或技能单元的触发方式
     */
    public ETouchType             touchType;

    /**
     * 附带的技能或效果单元信息列表（这部分数据不可以被强化）
     */
    public AdditionalActionUnit[] additionalOddsActionUnitList;

    /*
     * (non-Javadoc)
     * 
     * @see hero.n_skill.unit.PassiveSkillUnit#getPassiveSkillType()
     */
    public PassiveSkillType getPassiveSkillType ()
    {
        return PassiveSkillType.CHANGE_PROPERTY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public PassiveSkillUnit clone () throws CloneNotSupportedException
    {
        return (PassiveSkillUnit) super.clone();
    }

    @Override
    public void touch (ME2GameObject _toucher, ME2GameObject _other,
            ETouchType _touchType, boolean _isSkillTouch)
    {
        // TODO Auto-generated method stub

        if (null == touchType)
        {
            return;
        }

        if (touchType.canTouch(_touchType, _isSkillTouch))
        {
            if (additionalSEID > 0)
            {
                if (Constant.isSkillUnit(additionalSEID))
                {
                    SkillServiceImpl.getInstance().additionalSkillUnitActive(
                            (HeroPlayer) _toucher,
                            _other,
                            (ActiveSkillUnit) SkillUnitDict.getInstance()
                                    .getSkillUnitRef(additionalSEID), 1, 1);
                }
                else
                {
                    EffectServiceImpl.getInstance().appendSkillEffect(_toucher,
                            _other, additionalSEID);
                }
            }

            if (null != additionalOddsActionUnitList)
            {
                for (AdditionalActionUnit additionalActionUnit : additionalOddsActionUnitList)
                {
                    if (Constant
                            .isSkillUnit(additionalActionUnit.skillOrEffectUnitID))
                    {
                        SkillServiceImpl
                                .getInstance()
                                .additionalSkillUnitActive(
                                        (HeroPlayer) _toucher,
                                        _other,
                                        (ActiveSkillUnit) SkillUnitDict
                                                .getInstance()
                                                .getSkillUnitRef(
                                                        additionalActionUnit.skillOrEffectUnitID),
                                        additionalActionUnit.activeTimes,
                                        additionalActionUnit.activeOdds);
                    }
                    else
                    {
                        EffectServiceImpl.getInstance().appendSkillEffect(
                                _toucher, _other,
                                additionalActionUnit.skillOrEffectUnitID);
                    }
                }
            }
        }
    }

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();
}
