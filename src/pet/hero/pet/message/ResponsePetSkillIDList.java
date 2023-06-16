package hero.pet.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;

/**
 * 给客户端宠物的技能列表，只有技能ID和技能间隔时间
 * 在显示宠物时用
 * 0x5618
 * @author jiaodongjie
 *
 */
public class ResponsePetSkillIDList extends AbsResponseMessage
{
	private Pet pet;
	
	public ResponsePetSkillIDList(Pet pet){
		this.pet = pet;
	}

	@Override
	public int getPriority ()
	{
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
		yos.writeInt(pet.id);
		
		PetServiceImpl.getInstance().writePetSkillID(pet, yos);
		
	}
	
}
