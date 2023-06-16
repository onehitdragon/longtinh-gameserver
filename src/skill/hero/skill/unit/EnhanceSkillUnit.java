package hero.skill.unit;

import hero.share.ME2GameObject;
import hero.skill.ActiveSkill;
import hero.skill.Skill;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ETouchType;
import hero.skill.service.SkillServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EnhanceSkillUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 下午06:02:31
 * @描述 ：强化技能型被动技能，该类型技能单位不会被多例化
 */

public class EnhanceSkillUnit extends PassiveSkillUnit
{
    /**
     * @param _id
     * @param _name
     */
    public EnhanceSkillUnit(int _id)
    {
        super(_id);
        // TODO Auto-generated constructor stub
    }

    /**
     * 强化单位列表
     */
    public EnhanceUnit[] enhanceUnitList;

    /*
     * (non-Javadoc)
     * 
     * @see hero.n_skill.unit.PassiveSkillUnit#getPassiveSkillType()
     */
    public PassiveSkillType getPassiveSkillType ()
    {
        return PassiveSkillType.ENHANCE_SKILL;
    }

    /**
     * @author Administrator 强化单元
     */
    public static class EnhanceUnit
    {
        /**
         * 技能数据类型名（技能表、技能单元、技能单元必定引发的效果）
         */
        public EnhanceDataType   skillDataType;

        /**
         * 强化的技能或效果名，不分等级
         */
        public String            skillName;

        /**
         * 强化的数据字段名
         */
        public SkillDataField    dataField;

        /**
         * 运算符号
         */
        public EMathCaluOperator caluOperator;

        /**
         * 改变的系数
         */
        public float             changeMulti;
        
        /**
         * 改变的字符串(主要用于武器类型添加;比如强化一个技能,让该技能多一种可以使用的武器)
         */
        public String[]          changeString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public PassiveSkillUnit clone () throws CloneNotSupportedException
    {
        return this;
    }

    public static enum EnhanceDataType
    {
        SKILL("总表"), SKILL_UNIT("技能单元"), EFFECT_UNIT("效果单元");

        String desc;

        EnhanceDataType(String _desc)
        {
            desc = _desc;
        }

        public static EnhanceDataType get (String _desc)
        {
            for (EnhanceDataType dataType : EnhanceDataType.values())
            {
                if (dataType.desc.equals(_desc))
                {
                    return dataType;
                }
            }

            return null;
        }
    }

    public static enum SkillDataField
    {
        COOL_DOWN("延迟时间"), PHYSICS_HARM("物理伤害值"), MAGIC_HARM("魔法伤害值"), HATE(
                "仇恨值"), MAGIC_REDUCE("魔法值减少量"), HP_RESUME("生命恢复量"), RELEASE_TIME(
                "施法时间"), TARGET_DISTANCE("目标距离"), MP_CONSUME("消耗数值"), HP_CONSUME(
                "消耗生命值"), RANGE_X("生效范围X"), WEAPON_HARM_MULT("武器伤害倍数"), STRENGTH(
                "力量"), INTE("智力"), AGILITY("敏捷"), SPIRIT("精神"), DEFENSE("防御"), FASTNESS_VALUE(
                "抗性值"), DUCK_LEVEL("闪躲等级"), HIT_LEVEL("命中等级"), PHISICS_DEATHBLOW_LEVEL(
                "物理暴击等级"), WEAPON_IMMO("普通攻击间隔"), EFFECT_KEEP_TIME("持续时间"), HP_MAX(
                "生命值"), NEED_WEAPON("需求武器类型");

        String desc;

        SkillDataField(String _desc)
        {
            desc = _desc;
        }

        public static SkillDataField get (String _desc)
        {
            for (SkillDataField dataType : SkillDataField.values())
            {
                if (dataType.desc.equals(_desc))
                {
                    return dataType;
                }
            }

            return null;
        }
    }

    @Override
    public void touch (ME2GameObject _toucher, ME2GameObject _other,
            ETouchType _touchType, boolean _isSkillTouch)
    {
        // TODO Auto-generated method stub
    }
}
