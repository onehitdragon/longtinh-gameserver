package hero.pet.message;

import hero.pet.Pet;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * 宠物阶段变化
 * @author jiaodongjie
 *
 */
public class ResponsePetStage extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ResponsePetStage.class);
	private int userID;
	private Pet pet;
	public ResponsePetStage(int _userID, Pet _pet){
		this.userID = _userID;
		this.pet = _pet;
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
        log.debug("response pet ID = "+pet.id+", stage = " + pet.pk.getStage() +", iconID="+pet.iconID+" ,imageID="+pet.imageID);
		yos.writeInt(userID);
		yos.writeInt(pet.id);
		yos.writeByte(pet.color);
		yos.writeShort(pet.fun);
        yos.writeShort(pet.iconID);
        yos.writeShort(pet.imageID);
    	//add by zhengl ; date: 2011-01-17 ; note: 添加动画
    	yos.writeShort(pet.animationID);
        yos.writeByte(pet.getFace());
        yos.writeUTF(pet.name);
        yos.writeInt(pet.feeding);
        yos.writeShort(pet.pk.getType());
        yos.writeShort(pet.pk.getStage());//当从蛋孵化到幼年时，客户端可以以阶段值可以用来判断是否收起幼年宠物
	}

}
