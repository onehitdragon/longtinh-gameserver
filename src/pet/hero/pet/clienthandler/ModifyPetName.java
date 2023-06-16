package hero.pet.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.PetPK;
import hero.pet.message.ResponsePetNaming;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * 宠物命名
 * @author jiaodongjie
 *
 */
public class ModifyPetName extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		
		int petid = yis.readInt();
		String name = yis.readUTF();
		
		ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponsePetNaming(player,petid,name));
		
	}

}
