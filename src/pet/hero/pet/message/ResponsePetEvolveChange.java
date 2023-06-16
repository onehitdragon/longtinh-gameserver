package hero.pet.message;

import hero.pet.Pet;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 每次喂养后，肉食宠物的战斗经验点、进化点自动增长
 * 宠物属性界面显示
 * @author jiaodongjie
 *
 */
public class ResponsePetEvolveChange extends AbsResponseMessage
{

	private HeroPlayer player;
	private Pet pet;
	
	public ResponsePetEvolveChange(HeroPlayer player, Pet pet)
	{
		this.player = player;
		this.pet = pet;
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
		yos.writeInt(pet.currEvolvePoint);
		yos.writeInt(pet.currFightPoint);
	}

}
