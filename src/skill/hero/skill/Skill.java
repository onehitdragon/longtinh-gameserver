package hero.skill;

import java.util.ArrayList;

import hero.effect.Effect;

import hero.item.Weapon.EWeaponType;
import hero.skill.detail.ESkillType;
import hero.skill.unit.SkillUnit;
import hero.share.ESystemFeature;
import hero.share.EVocation;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Skill.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-31 上午08:58:56
 * @描述 ：
 */

public abstract class Skill implements Cloneable
{
    /**
     * 编号
     */
    public int            id;

    /**
     * 名称
     */
    public String         name;

    /**
     * 等级
     */
    public short          level;
    
    /**
     * 该技能最高等级
     */
    public short          maxLevel;
    
    /**
     * 技能阶级(几转技能).
     */
    public byte           skillRank;

    /**
     * **old**
     * 系统特性（英雄、亡者） // 该特性暂时废弃 以后以**old**作为老旧游戏标记
     */
    public ESystemFeature feature;

    /**
     * 是否从技能书中领悟
     */
    public boolean        getFromSkillBook;

    /**
     * 一级技能时，需要的学习者等级
     */
    public short          learnerLevelOfOneLevel;

    /**
     * 学习者等级
     */
    public short          learnerLevel;

    /**
     * **需要修改的地方
     * 学习者职业
     */
    public EVocation[]      learnerVocation;

    /**
     * 升级该技能需要的费用
     */
    public int            learnFreight;

    /**
     * 需要装备的武器类型
     */
    public ArrayList<EWeaponType>    needWeaponType;

    /**
     * 图标编号
     */
    public short          iconID;

    /**
     * 描述
     */
    public String         description;

    /**
     * 技能单元
     */
    public SkillUnit     skillUnit;

    /**
     * 附带的必然效果单元（与附带的必然技能单元只会同时存在一个）
     */
    public Effect         addEffectUnit;

    /**
     * 附带的必然技能单元（与附带的必然效果单元只会同时存在一个）
     */
    public SkillUnit      addSkillUnit;

    /**
     * 下一级技能
     */
    public Skill        next;

    /**
     * 上一级技能
     */
    public Skill        prev;
    
    /**
     * 升级技能所需要的技能点
     */
    public short         skillPoints;
    
    /**
     * 技能是否激活.
     *   **即便未激活的技能,我们也会下发给客户端,但是会附带此标记.
     *   **这样客户端就可以展示所有已经学会或还未学会的技能了.
     */
//    public byte         isActive;

    /**
     * 构造
     * 
     * @param _id
     * @param _name
     */
    public Skill(int _id, String _name)
    {
        id = _id;
        name = _name;
    }

    /**
     * 是否同名技能
     * 
     * @param _otherSkill
     * @return
     */
    public boolean isSameName (Skill _otherSkill)
    {
        return name.equals(_otherSkill.name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Skill clone () throws CloneNotSupportedException
    {
        Skill skill = (Skill) super.clone();

        skill.skillUnit = skill.skillUnit.clone();

        if (null != skill.addEffectUnit)
        {
            skill.addEffectUnit = skill.addEffectUnit.clone();
        }
        else if (null != skill.addSkillUnit)
        {
            skill.addSkillUnit = skill.addSkillUnit.clone();
        }

        return skill;
    }

    /**
     * @param _skillUnit
     */
    public void setSkillUnit (SkillUnit _skillUnit)
    {
        skillUnit = _skillUnit;
    }

    public abstract ESkillType getType();
}
