package hero.pet.message;

import hero.pet.Pet;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 饲养后返回表情和饲养值、攻击力、速度
 * @author jiaodongjie
 *
 */
public class ResponseFeedStatusChange extends AbsResponseMessage
{

	int userID;
	Pet pet;
	
	public ResponseFeedStatusChange(int userID, Pet pet){
		this.userID = userID;
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
		yos.writeInt(userID);
		yos.writeInt(pet.id);
		byte died = (byte)(pet.isDied()?0:1);
		yos.writeByte(died);//是否死掉，和宠物 是否显示有关
		yos.writeByte(pet.getFace());
		yos.writeInt(pet.feeding);
		yos.writeShort(pet.pk.getStage());
		if(pet.pk.getStage() == Pet.PET_STAGE_ADULT){
    		yos.writeInt(pet.getATK());
    		yos.writeInt(pet.getSpeed());
		}
	}

}
