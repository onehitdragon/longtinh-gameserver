package hero.pet.message;

import hero.pet.Pet;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 宠物升级后通知 0x5612
 * @author jiaodongjie
 *
 */
public class ResponsePetUpgrade extends AbsResponseMessage
{
	private Pet pet;
	
	public ResponsePetUpgrade(Pet pet){
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
		yos.writeInt(pet.id);
		yos.writeInt(pet.level);
		yos.writeInt(pet.mp);
		yos.writeInt(pet.str);
		yos.writeInt(pet.agi);
		yos.writeInt(pet.spi);
		yos.writeInt(pet.intel);
		yos.writeInt(pet.luck);
	}

}
