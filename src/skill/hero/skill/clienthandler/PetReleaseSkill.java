package hero.skill.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.skill.message.RestoreSkillCoolDownNotify;
import hero.skill.service.SkillServiceImpl;

/**
 * 宠物使用技能(主动)
 * 
 * 0x1012
 */
public class PetReleaseSkill extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(PetReleaseSkill.class);
	@Override
	public void read () throws Exception
	{
		int petID = yis.readInt();
		int skillID = yis.readInt();
        byte targetType = yis.readByte();
        int targetObjectID = yis.readInt();
        byte direction = yis.readByte();

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        
        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
        
        if(null == player || null == pet){
        	return;
        }
        
        pet.live(player.where());
        pet.setDirection(direction);
        
        ME2GameObject target;//宠物只能攻击怪物或玩家

        if (EObjectType.MONSTER.value() == targetType)
        {
            target = player.where().getMonster(targetObjectID);
        }
        else
        {
            target = player.where().getPlayer(targetObjectID);
        }
        if(null == target){
        	return;
        }
        log.debug("0x1012 target = " + target.getObjectType() +" id = " + target.getID());
        
        pet.live(player.where());
        if (!SkillServiceImpl.getInstance().petReleaseSkill(pet,
                skillID, target, direction))
        {
            ResponseMessageQueue.getInstance().put(
                    player.getMsgQueueIndex(),
                    new RestoreSkillCoolDownNotify(skillID));
        }
	}

}
