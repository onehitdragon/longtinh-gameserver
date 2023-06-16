package hero.pet.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.pet.Pet;
import hero.pet.message.ResponsePetStage;
import hero.pet.service.PetDictionary;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * 宠物从幼年进化到成年
 * 成年宠物分为草食宠物或肉食宠物
 * 玩家需要到待定的NPC处进化宠物
 * @author jiaodongjie
 *
 */
public class PetEvolveAdult extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(PetEvolveAdult.class);
	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		int petId = yis.readInt();
		byte type = yis.readByte(); //进化类型
		Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
		log.debug("宠物从幼年进化到成年,草食进化点 = "+pet.currHerbPoint +"， 肉食进化点 = "+pet.currCarnPoint);
		if(pet.pk.getStage() == Pet.PET_STAGE_CHILD){
    		if(type == Pet.PET_TYPE_CARNIVORE){
    			if(pet.currCarnPoint >= Pet.MAXCARNPOINT){//可以进化为肉食宠物
    				pet.pk.setType(Pet.PET_TYPE_CARNIVORE);
    				pet.pk.setStage(Pet.PET_STAGE_ADULT);
    				pet.fun = Pet.PET_CARNIVORE_FUN;
    				pet.iconID = PetDictionary.getInstance().getPet(pet.pk).iconID;
    				pet.imageID = PetDictionary.getInstance().getPet(pet.pk).imageID;
    				//add by zhengl ; date: 2011-01-17 ; note: 宠物也得发动画ID
    				pet.animationID = PetDictionary.getInstance().getPet(pet.pk).animationID;
    				pet.feeding = Pet.FEEDING_GREEN_FULL;
    				PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetStage(player.getUserID(),pet));
    			}else{
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning("当前肉食进化点不够进化到肉食战斗宠物，继续努力吧！"));
    			}
    		}else if(type == Pet.PET_TYPE_HERBIVORE){
    			if(pet.currHerbPoint >= Pet.MAXHERBPOINT){//可以进化为草食宠物
    				pet.pk.setType(Pet.PET_TYPE_HERBIVORE);
    				pet.pk.setStage(Pet.PET_STAGE_ADULT);
    				pet.fun = Pet.PET_HERBIVORE_FUN;
    				pet.iconID = PetDictionary.getInstance().getPet(pet.pk).iconID;
    				pet.imageID = PetDictionary.getInstance().getPet(pet.pk).imageID;
    				pet.feeding = Pet.FEEDING_GREEN_FULL;
    				PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetStage(player.getUserID(),pet));
    			}else{
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning("当前草食进化点不够进化到草食坐骑宠物，继续努力吧！"));
    			}
    		}else{
    			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning("当前进化点不够进化到成年宠物，继续努力吧！"));
    		}
		}else{
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new Warning("宠物只有在幼年阶段才会进化到成年！"));
		}
	}

}
