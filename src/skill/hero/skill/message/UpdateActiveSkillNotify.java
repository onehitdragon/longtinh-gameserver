package hero.skill.message;

import hero.skill.ActiveSkill;
import hero.skill.unit.ActiveSkillUnit;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UpdateActiveSkillNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-5-11 下午01:28:37
 * @描述 ：更新主动技能通知，当技能被强化或者受BUFF影响某些属性发生变化，被动技能的变化不发送
 */

public class UpdateActiveSkillNotify extends AbsResponseMessage
{
    /**
     * 更新的技能
     */
    private ActiveSkill activeSkill;

    /**
     * 构造
     * 
     * @param _skill
     */
    public UpdateActiveSkillNotify(ActiveSkill _activeSkill)
    {
        activeSkill = _activeSkill;
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
        ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;

        yos.writeInt(activeSkill.id);
        yos.writeInt(activeSkill.coolDownTime);
        yos.writeInt(activeSkill.reduceCoolDownTime);
        yos.writeShort(skillUnit.releaseTime * 1000);
        yos.writeByte(skillUnit.targetDistance);
        yos.writeShort(skillUnit.consumeHp);
        yos.writeShort(skillUnit.consumeMp);
        yos.writeByte(skillUnit.consumeFp);
        yos.writeByte(skillUnit.consumeGp);
    }
}
