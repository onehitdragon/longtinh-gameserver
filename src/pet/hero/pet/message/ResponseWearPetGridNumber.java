package hero.pet.message;

import hero.item.bag.PlayerBodyWearPetList;
import hero.item.bag.exception.BagException;
import hero.item.detail.EBodyPartOfEquipment;
import hero.pet.Pet;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 玩家把宠物装备到身上后，身上宠物格子的情况
 * 0x3a17
 * @author jiaodongjie
 *
 */
public class ResponseWearPetGridNumber extends AbsResponseMessage
{
	private static Logger log = Logger.getLogger(ResponseWearPetGridNumber.class);
	private PlayerBodyWearPetList petlist;
	
	public ResponseWearPetGridNumber(PlayerBodyWearPetList petlist){
		this.petlist = petlist;
	}
	@Override
	public int getPriority ()
	{
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
		try{
    		Pet[] pets = petlist.getPetList();
    		yos.writeByte(pets.length);//格子数
    		log.debug("@@ player body petlist full num = " + (byte)petlist.getFullGridNumber());
    		yos.writeByte(petlist.getFullGridNumber());//有宠物的格子数
    		for(int i=8; i<=9; i++){
    			Pet pet = pets[i-8];
    			if(null != pet){
                	log.debug("player wear body pet id = " + pet.id);
                	yos.writeByte(i);//格子位置
                	log.debug("player body pet gridnumber = " + i);
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
			log.error("response player body pet error ：", e);
		}
        log.info("output size = " + String.valueOf(yos.size()));
	}

}
