package hero.pet.message;

import hero.item.bag.PetContainer;
import hero.item.bag.exception.BagException;
import hero.pet.Pet;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 返回没有被装备的宠物的列表、宠物物品及其所在格子位置
 * 0x5616 
 * @author jiaodongjie
 *
 */
public class ResponsePetContainer extends AbsResponseMessage
{
	private static Logger log = Logger.getLogger(ResponsePetContainer.class);
	private PetContainer petList;
	public ResponsePetContainer(PetContainer petList){
		this.petList = petList;
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
		try{
    		Pet[] petlist = petList.getPetList();
    		yos.writeByte(petlist.length); //格子数
    		log.debug("@@ pet container full grid num = " + (byte)petList.getFullGridNumber());
    		yos.writeByte(petList.getFullGridNumber());//有宠物的格子数,客户端以这个值取宠物数据
    		
    		Pet pet;
    		for(int i=0; i<petlist.length; i++){
    			
    			pet = petlist[i];
    			if(null != pet){
    				yos.writeByte(i);//格子位置
    				log.debug("pet container gridnum = " + i);
    				yos.writeInt(pet.id);
    	            yos.writeShort(pet.pk.getStage());
    	            yos.writeShort(pet.pk.getKind());
    	            yos.writeShort(pet.pk.getType());
    	            yos.writeByte(pet.color);
    	            yos.writeShort(pet.iconID);
    	            yos.writeUTF(pet.name);
    	            yos.writeInt(pet.feeding);
    	            yos.writeShort(pet.imageID);
                	//add by zhengl ; date: 2011-01-17 ; note: 添加动画
                	yos.writeShort(pet.animationID);
    			}
    		}
		}catch(BagException e){
			e.printStackTrace();
		}
	}

}
