package hero.pet.message;

import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * 交易宠物 
 * @author jiaodongjie
 * 改由交易物品报文处理交易宠物
 * TODO 此报文作废
 */
public class ResponseTransactPet extends AbsResponseMessage
{
	int userID;
	long petID;
	int res;

	public ResponseTransactPet(int userID, long petID, int res)
	{
		this.userID = userID;
		this.petID = petID;
		this.res = res;
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
		
		String msg = "宠物交易成功";
		if(res != 1){
			msg = "宠物交易失败";
		}
		yos.writeInt(userID);
		yos.writeInt(petID);
		yos.writeByte(res);
		yos.writeUTF(msg);
	}

}
