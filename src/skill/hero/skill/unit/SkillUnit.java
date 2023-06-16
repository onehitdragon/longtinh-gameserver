package hero.skill.unit;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 下午02:11:35
 * @描述 ：技能单元，包含直接作用数值、必定产生的持续性效果、几率型效果3部分组成
 *     <p>
 *     其中直接作用数值和必定产生的持续型效果数值可被强化技能改变，几率型效果不可被强化
 *     </p>
 */

public abstract class SkillUnit implements Cloneable
{
    /**
     * 编号
     */
    public int    id;

    /**
     * 名称
     */
    public String name;

    /**
     * 必然携带的连带效果或技能单元编号
     */
    public int    additionalSEID;

    /**
     * @param _id
     * @param _name
     */
    public SkillUnit(int _id)
    {
        id = _id;
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
