package hero.pet.message;

import hero.pet.Pet;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 用洗点道具洗能力槽点
 * 洗完某个能力槽的点数后，玩家的总进化点恢复相应点数
 * @author jiaodongjie
 *
 */
public class ResponseDiscardPoint extends AbsResponseMessage
{

	private HeroPlayer player;
	private Pet pet;
	private byte code;
	
	public ResponseDiscardPoint(HeroPlayer player, Pet pet, byte code)
	{
		this.player = player;
		this.pet = pet;
		this.code = code;
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
		yos.writeByte(code);//被洗的能力槽编号
		yos.writeInt(0);//被洗后该能力槽的值为0
		yos.writeInt(pet.currEvolvePoint); //当前宠物进化点(已恢复)
		
	}

}
