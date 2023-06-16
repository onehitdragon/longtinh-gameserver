package hero.skill.unit;

import hero.share.ME2GameObject;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.ETouchType;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PassiveSkillUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 下午02:30:29
 * @描述 ：被动技能单元（被动触发型、强化技能型、属性改变型）
 */

public abstract class PassiveSkillUnit extends SkillUnit
{
    /**
     * @param _id
     * @param _name
     */
    public PassiveSkillUnit(int _id)
    {
        super(_id);
        // TODO Auto-generated constructor stub
    }

    /**
     * 被动技能类型
     * 
     * @author Administrator
     */
    enum PassiveSkillType
    {
        /**
         * 触发型
         */
        TOUCH,
        /**
         * 强化技能型
         */
        ENHANCE_SKILL,
        /**
         * 改变宿主属性
         */
        CHANGE_PROPERTY;
    }

    /**
     * 获取被动技能类型
     * 
     * @return
     */
    public abstract PassiveSkillType getPassiveSkillType ();

    /**
     * 能否触发其他效果
     * 
     * @param _toucher
     * @param _other
     * @param _touchType
     * @param _isSkillTouch
     */
    public abstract void touch (ME2GameObject _toucher,
            ME2GameObject _other, ETouchType _touchType, boolean _isSkillTouch);

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
