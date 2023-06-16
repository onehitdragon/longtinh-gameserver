package hero.skill;

import hero.skill.detail.ESkillType;
import hero.skill.unit.SkillUnit;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PassiveSkill.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 上午09:11:08
 * @描述 ：被动技能（属性强化型、强化技能型、触发型技能）
 */

public class PassiveSkill extends Skill
{

    public PassiveSkill(int _id, String _name)
    {
        super(_id, _name);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Skill clone () throws CloneNotSupportedException
    {
        return (Skill) super.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.skill.Skill#getType()
     */
    public ESkillType getType ()
    {
        return ESkillType.PASSIVE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.n_skill.N_Skill#setSkillUnit(hero.n_skill.unit.SkillUnit)
     */
    public void setSkillUnit (SkillUnit _passiveSkillUnit)
    {
        super.setSkillUnit(_passiveSkillUnit);
    }
}
