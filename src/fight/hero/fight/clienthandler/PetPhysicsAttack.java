package hero.fight.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.fight.service.FightServiceImpl;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * 宠物普通物理攻击
 * @author jiaodongjie
 * 0xf02
 */
public class PetPhysicsAttack extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(PetPhysicsAttack.class);
	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		
		if (null == player || !player.isEnable() || player.isDead())
        {
            return;
        }
		
		int petID = yis.readInt();
		
		Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
		
        if(null == pet || pet.isDied() 
        		|| System.currentTimeMillis() - pet.lastAttackTime < pet.getActualAttackImmobilityTime()){
        	log.debug("pet attack ActualAttackImmobilityTime = " + pet.getActualAttackImmobilityTime());
        	return;
        }
        
        
        byte targetType = yis.readByte();
        int targetObjectID = yis.readInt();

        log.debug("pet physics attack targetType = " + targetType + ", targetObjectID = " +targetObjectID);
        
        ME2GameObject target;

        if (EObjectType.MONSTER.value() == targetType)
        {
            target = player.where().getMonster(targetObjectID);

            if (null == target)
            {
                return;
            }
        }
        else
        {
            target = player.where().getPlayer(targetObjectID);

            if (null == target
                    || (player.getClan() == target.getClan() && player
                            .getDuelTargetUserID() != ((HeroPlayer) target)
                            .getUserID()))
            {
                return;
            }
        }

        if (target.isEnable() && !target.isDead())
        {
//            if (Math.sqrt(Math
//                    .pow(player.getCellX() - target.getCellX(), 2)
//                    + Math.pow(player.getCellY() - target.getCellY(), 2)) <= (player
//                    .getAttackRange() + Constant.BALANCE_ATTACK_DISTANCE))
            boolean inRange = (player.getCellX() - target.getCellX())*(player.getCellX() - target.getCellX())
                        +(player.getCellY() - target.getCellY())*(player.getCellY() - target.getCellY())
                        <= (player.getAttackRange()+Constant.BALANCE_ATTACK_DISTANCE)*(player.getAttackRange()+Constant.BALANCE_ATTACK_DISTANCE);
            if(inRange)
            {
            	log.debug("####### pet physics attack ########## ");
            	pet.masterID = player.getUserID();
            	pet.live(player.where());
                FightServiceImpl.getInstance().processPhysicsAttack(pet,target);
            }
            else
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_FIGHT_OF_ATTACK_RANGE_ENOUGH, Warning.UI_STRING_TIP));
            }
        }
		
	}
        


}
