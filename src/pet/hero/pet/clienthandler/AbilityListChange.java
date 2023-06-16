package hero.pet.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * 给能力槽分配进化点
 * @author jiaodongjie
 *
 */
public class AbilityListChange extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		
		int petId = yis.readInt();
		byte code = yis.readByte(); //能力槽编号
		int points = yis.readInt(); //要分配的点数
		
		PetServiceImpl.getInstance().addAbilityPoint(player, petId, code, points);
		
	}

}
