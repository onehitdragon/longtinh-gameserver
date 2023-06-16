package hero.pet.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.Pet;
import hero.pet.message.ResponseShowPetSkillList;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * 查看宠物技能列表   0x560f
 * @author jiaodongjie
 *
 */
public class ShowPetSkillList extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		int petID = yis.readInt();
		Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
		if(null != pet){
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseShowPetSkillList(pet));
		}else{
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("没有找到此宠物的技能信息！"));
		}
	}

}
