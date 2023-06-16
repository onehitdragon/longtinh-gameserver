package hero.skill.clienthandler;

import hero.skill.message.RestoreSkillCoolDownNotify;
import hero.skill.service.SkillServiceImpl;
import hero.fight.clienthandler.PhysicsAttack;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.EObjectType;
import hero.share.ME2GameObject;

import java.io.IOException;

import org.apache.log4j.Logger;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UseSkill.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 上午11:46:37
 * @描述 ：使用技能
 */

public class ReleaseSkill extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(ReleaseSkill.class);

    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        try
        {
            int skillID = yis.readInt();
            byte targetType = yis.readByte();
            int targetObjectID = yis.readInt();
            byte direction = yis.readByte();

            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            
            log.info("player:" + player.getName() + "释放技能:" + skillID);

            if (null == player)
            {
            	log.error("null == player; skillid=" + String.valueOf(skillID) 
            			+ " targetType=" + String.valueOf(targetType) 
            			+ " targetObjectID=" + String.valueOf(targetObjectID)
            			+ " directio=" + String.valueOf(direction));
                return;
            }

            player.setDirection(direction);
            ME2GameObject target;

            if (EObjectType.MONSTER.value() == targetType)
            {
                target = player.where().getMonster(targetObjectID);
            }
            else
            {
                target = player.where().getPlayer(targetObjectID);
            }

            if (!SkillServiceImpl.getInstance().playerReleaseSkill(player,
                    skillID, target, direction))
            {
                ResponseMessageQueue.getInstance().put(((HeroPlayer) player).getMsgQueueIndex(), 
                        new RestoreSkillCoolDownNotify(skillID));
            	log.warn("response client : RestoreSkillCoolDownNotify");
            }
        }
        catch (IOException e)
        {
        	log.error("...IOException...");
            e.printStackTrace();
        }
    }
}
