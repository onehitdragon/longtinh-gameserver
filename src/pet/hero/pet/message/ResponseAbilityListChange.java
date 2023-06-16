package hero.pet.message;

import hero.pet.Pet;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 给能力槽分配进化点
 * @author jiaodongjie
 *
 */
public class ResponseAbilityListChange extends AbsResponseMessage
{

	private HeroPlayer player;
	private Pet pet;
	private byte code;
	private int point;
	
	public ResponseAbilityListChange(HeroPlayer player, Pet pet, byte code, int point)
	{
		this.player = player;
		this.pet = pet;
		this.code = code;
		this.point = point;
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
		yos.writeInt(player.getUserID());
		yos.writeInt(pet.id);
		yos.writeByte(code);
		yos.writeInt(point);//分配后的点数
	}

}
