package hero.pet.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.pet.Pet;
import hero.pet.message.ResponseFeedStatusChange;
import hero.pet.message.ResponsePetUpgrade;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * 宠物饲养条变化
 * 客户端每分钟发次请求
 * 在这里也要根据时间来判断肉食宠物是否可以升级
 * @author jiaodongjie
 *
 */
public class PetFeedStatusChange extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(PetFeedStatusChange.class);
	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		
		int petId = yis.readInt();
		int feeding = yis.readInt();
		log.debug("PetFeedStatusChange ... petid=" + petId +",  feeding="+feeding);
		Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
		if(pet != null){
    		pet.feeding = feeding;
    		if(pet.isDied()){
    			PetServiceImpl.getInstance().hidePet(player,petId);
    			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("宠物已经死亡！"));
    			return;
    		}else{
    			log.debug("pet.healthtime = " + pet.healthTime);
    			log.debug("pet.face = " + pet.getFace());
    			if(pet.getFace() < 5){
    				pet.healthTime = 0;
    			}else{
    				/*if(pet.startHealthTime == -1){
    					log.debug("pet.startHealthTime = -1..");
//    					pet.startHealthTime = System.currentTimeMillis();
    					pet.healthTime = 1;
    				}else{
    					log.debug("pet.startHealthTime = " + pet.startHealthTime);
    					long currtime = System.currentTimeMillis();
        				log.debug("currtime = " + currtime);
        				int htimex = (int)((currtime - pet.startHealthTime)/(60*1000));
        				log.debug("htimex == " + htimex);
        				pet.healthTime = pet.healthTime + htimex;
    					pet.healthTime++;
    				}*/
    				pet.healthTime++;
    				log.debug("pet health time = " + pet.healthTime);
    				pet.updFEPoint();
    			}
    			log.debug("healthtime = " + pet.healthTime);
    		}
    	
    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseFeedStatusChange(player.getUserID(),pet));
    		
    		if(pet.pk.getStage() == Pet.PET_STAGE_ADULT && pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
    			int cLevel = pet.level;
    			if(cLevel < player.getLevel()){//宠物的等级不能大于玩家的等级
        			if(PetServiceImpl.getInstance().petUpgrade(player.getUserID(), pet) == (cLevel+1)){
        				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetUpgrade(pet));
        				//每次升级后，查看是否有可学习的技能
        				PetServiceImpl.getInstance().petLearnSkill(player,pet);
        				
        				PetServiceImpl.getInstance().reCalculatePetProperty(pet);
        			}
    			}
    		}
		}else{
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("没有这个宠物，操作失败！"));
		}
	}

}
