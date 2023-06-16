package hero.pet.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.pet.Pet;
import hero.pet.message.ResponseRefreshPetProperty;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * 显示宠物属性信息
 * @author jiaodongjie
 *
 */
public class ShowPetInfo extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(ShowPetInfo.class);
	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		int petID = yis.readInt();
		log.debug("show pet info petid = " + petID);
		Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
		if(null != pet){
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
		}else{
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("没有找到此宠物的属性信息！"));
		}
	}

}
