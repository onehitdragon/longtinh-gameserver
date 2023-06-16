package hero.skill.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RestoreSkillCoolDownNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-23 下午02:19:10
 * @描述 ：恢复技能使用冷却时间
 *     <p>
 *     有冷却时间的技能施放后在客户端直接处于未冷却状态，当施放失败时，通知客户端还原到冷却状态
 *     </p>
 */

public class RestoreSkillCoolDownNotify extends AbsResponseMessage
{
    /**
     * 恢复冷却状态的技能编号
     */
    private int skillID;

    /**
     * 构造
     * 
     * @param _skillID
     */
    public RestoreSkillCoolDownNotify(int _skillID)
    {
        skillID = _skillID;
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
        yos.writeInt(skillID);
    }
}
