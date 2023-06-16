package hero.pet.message;

import hero.pet.Pet;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 宠物复活
 * @author jiaodongjie
 *
 */
public class ResponsePetRevive extends AbsResponseMessage
{

	private HeroPlayer player;
	private Pet pet;
	
	
	public ResponsePetRevive(HeroPlayer player, Pet pet)
	{
		super();
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
		yos.writeShort(pet.imageID);
    	//add by zhengl ; date: 2011-01-17 ; note: 添加动画
    	yos.writeShort(pet.animationID);
		yos.writeByte(pet.getFace());
		yos.writeInt(pet.feeding);
	}

}
