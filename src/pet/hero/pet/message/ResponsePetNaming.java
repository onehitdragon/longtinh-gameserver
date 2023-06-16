package hero.pet.message;

import hero.pet.PetPK;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 宠物命名
 * @author jiaodongjie
 *
 */
public class ResponsePetNaming extends AbsResponseMessage
{

	private HeroPlayer player;
	private int petid;
	private String name;
	
	public ResponsePetNaming(HeroPlayer _player, int _petid, String name){
		this.player = _player;
		this.petid = _petid;
		this.name = name;
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
		
		int succ = PetServiceImpl.getInstance().modifyPetName(player, petid, name);
		yos.writeInt(player.getUserID());
		yos.writeInt(petid);
		yos.writeByte(succ);
		
	}

}
