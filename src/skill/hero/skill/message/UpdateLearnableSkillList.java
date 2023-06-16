package hero.skill.message;

import hero.skill.Skill;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UpdateLearnableSkillList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-22 下午05:01:03
 * @描述 ：更新可学技能列表
 */

public class UpdateLearnableSkillList extends AbsResponseMessage
{
    /**
     * 刚学会的技能
     */
    private Skill learnedSkill;

    /**
     * 下一级是否可以学习
     */
    private boolean nextSkillLearnable;

    /**
     * 构造
     * 
     * @param _learnedSkill 已经学会的技能，需要从可学列表中消失
     * @param _nextSkillLearnable 消失的技能是否可以学习其下一级技能
     */
    public UpdateLearnableSkillList(Skill _learnedSkill,
            boolean _nextSkillLearnable)
    {
        learnedSkill = _learnedSkill;
        nextSkillLearnable = _nextSkillLearnable;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeInt(learnedSkill.id);
        yos.writeByte(nextSkillLearnable);

        if (nextSkillLearnable && null != learnedSkill.next)
        {
            yos.writeInt(learnedSkill.next.id);
            yos.writeUTF(learnedSkill.next.name);
            yos.writeByte(learnedSkill.next.level);
            yos.writeShort(learnedSkill.next.iconID);
            yos.writeUTF(learnedSkill.next.description);
            yos.writeInt(learnedSkill.next.learnFreight);
        }
    }
}
