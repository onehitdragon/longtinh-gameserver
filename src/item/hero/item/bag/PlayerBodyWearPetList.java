package hero.item.bag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import hero.item.bag.exception.BagException;
import hero.item.detail.EBodyPartOfEquipment;
import hero.pet.Pet;
import hero.pet.PetList;

/**
 * 玩家身上装备的宠物列表
 * 最多两只
 * @author jiaodongjie
 *
 */
public class PlayerBodyWearPetList extends PetContainer
{
	private static Logger log = Logger.getLogger(PlayerBodyWearPetList.class);
	public PlayerBodyWearPetList()
	{
		super(PetList.MAX_SHOW_NUMBER);
	}
	/**
     * 初始化玩家已装备到身上的宠物
     * @param petList
     * @throws BagException
     */
	public void init(HashMap<Integer,Pet> petViewList) throws BagException{
		for(Iterator<Pet> it = petViewList.values().iterator(); it.hasNext();){
			add(it.next());
			log.debug("init player body pet size = " + getFullGridNumber());
		}
	}
	
	public int add(Pet pet) throws BagException{
		int gridnum = EBodyPartOfEquipment.PET_F.value();
		if(null != pet && pet.viewStatus == 1){
			synchronized (petList){
				if(notExists(pet)){
        			//跟随宠物所在格子位置里为 petList[0],实际位置为 8
        	        //坐骑宠物所在的格子位置 petList[1],实际位置为 9
        			if(pet.pk.getStage() == Pet.PET_STAGE_ADULT){
        	    		log.debug("player wear body pet stage adult ..");
        	    		if(pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){//跟随宠物所在格子位置
        	    			petList[0] = pet;
        	    		}else{ //坐骑宠物所在格子位置
        	    			petList[1] = pet;
        	    			gridnum = EBodyPartOfEquipment.PET_S.value();
        	    		}
        	    		emptyGridNumber --;
        	    	}
        	    	else{
        	    		log.debug("player wear body pet stage not adult ..");
        	    		if(null == petList[0]){
        	    			petList[0] = pet;//宠物所在的格子位置
        	    		}else if(null == petList[1]){
        	    			petList[1] = pet;
        	    			gridnum = EBodyPartOfEquipment.PET_S.value();
        	    		}
        	    		emptyGridNumber --;
        	    	}
				}
			}
		}
		return gridnum;
	}

}
