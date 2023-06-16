package hero.map.message;

import hero.item.bag.exception.BagException;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 当前玩家身上装备的宠物信息
 * @author jiaodongjie
 * 0x2a16 地图上显示的宠物
 */
public class ResponsePetInfoList extends AbsResponseMessage
{
	HeroPlayer player;
	
	/**
	 * 当前玩家身上装备的宠物信息
	 * @param player
	 */
	public ResponsePetInfoList(HeroPlayer player)
	{
		this.player = player;
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
		
		try
		{
			Pet[] petlist = player.getBodyWearPetList().getPetList();
			int fullGridNum = player.getBodyWearPetList().getFullGridNumber();
			
			if(fullGridNum > 0){
				yos.writeByte(fullGridNum);
				Pet pet;
				for(int i=0; i<petlist.length; i++){
					pet = petlist[i];
					if(null != pet){
						yos.writeByte(pet.isView);
		            	yos.writeInt(pet.id);
		            	yos.writeShort(pet.iconID);
		            	yos.writeShort(pet.imageID);
		            	//add by zhengl ; date: 2011-01-17 ; note: 添加动画
		            	yos.writeShort(pet.animationID);
		            	yos.writeShort(pet.pk.getType());
		            	yos.writeShort(pet.fun);
					}
				}
			}else{
	        	yos.writeByte(0);
	        }
		}catch(BagException e)
		{
			e.printStackTrace();
		}
       
	}
	

}
