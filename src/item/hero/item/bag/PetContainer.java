package hero.item.bag;

import java.util.List;

import org.apache.log4j.Logger;

import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.detail.EGoodsType;
import hero.pet.Pet;
import hero.pet.PetList;

/**
 * 宠物列表容器
 * 用于给玩家装备宠物的界面
 * 只包含没有被玩家装备的宠物
 * @author jiaodongjie
 *
 */
public class PetContainer
{
	private static Logger log = Logger.getLogger(PetContainer.class);
	/**
     * 可用格子数量
     */
    public short  emptyGridNumber;
    /**
     * 宠物列表
     */
    public Pet[]	petList;
    
    /**
     * 
     * @param size
     */
    public PetContainer(short size){
    	petList = new Pet[size];
    	emptyGridNumber = size;
    }
    
    /**
     * 获取容量
     * @return
     */
    public int getSize(){
    	return petList.length;
    }
    
    /**
     * 获取有装备的格子数量
     * 
     * @return
     */
    public int getFullGridNumber ()
    {
        return (petList.length - emptyGridNumber);
    }

    /**
     * 获取空格子数量
     * 
     * @return
     */
    public short getEmptyGridNumber ()
    {
        return emptyGridNumber;
    }
    /**
     * 更新空格子的数量
     */
    public void setEmptyGridNumber(short emptyGridNumber){
    	this.emptyGridNumber = emptyGridNumber;
    } 
    
    /**
     * 增加宠物
     * @param pet
     * @return 位置
     * @throws BagException
     */
    public int add(Pet pet) throws BagException{
    	
    	if(0 == emptyGridNumber){
    		throw PackageExceptionFactory.getInstance().getFullException(
                    EGoodsType.PET);
    	}
    	if(null != pet && pet.viewStatus==0){
//    		log.debug("petContainer add pet id = " + pet.id +", viewstatus = " + pet.viewStatus);
    		synchronized (petList){
        		for(int i=0; i<petList.length; i++){
        			if(null == petList[i] && notExists(pet)){
        				petList[i] = pet;
        				emptyGridNumber --;
        				return i;
        			}
        		}
    		}
    	}

        return -1;
    }
    
    //检查当前petList 里是否已经存在指定的宠物
    protected boolean notExists(Pet pet) throws BagException{
//        synchronized (petList){
            if(null != pet)
                for(int i=0; i<petList.length; i++){
                    if(null != petList[i] && pet.id == petList[i].id){
                        return false;
                    }
                }
            return true;
//        }
    }
    
    /**
     * 初始化玩家已有的但没有装备到身上的宠物
     * @param petList
     * @throws BagException
     */
    public void init(List<Pet> petlist) throws BagException{
        log.debug("init player petContainer petlist size = " + petlist.size()+", emptyGridNumber="+emptyGridNumber);
        if(0 == emptyGridNumber){
            throw PackageExceptionFactory.getInstance().getFullException(
                    EGoodsType.PET);
        }
        synchronized (petList){
            for(Pet pet : petlist){
                log.debug(" init petContainer add pet id = " + pet.id +", viewstatus = " + pet.viewStatus);
                if( pet.viewStatus==0){
                    for(int i=0; i<petList.length; i++){
                        if(null == petList[i] && notExists(pet)){
                            petList[i] = pet;
                            emptyGridNumber --;
                        }
                    }
                }

            }
            log.debug("init petContainer length = " + getFullGridNumber());
        }

    }
    
    /**
     * 根据位置编号移除指定位置的宠物
     * @param gridIndex
     * @return
     */
    public Pet remove(short gridIndex) throws BagException{
    	if(gridIndex >=0 && gridIndex < petList.length){
    		synchronized(petList)
			{
    			log.debug("remove pet, curr pet list size = " + getFullGridNumber());
    			Pet petx = petList[gridIndex];
				petList[gridIndex] = null;
				emptyGridNumber ++;
				
				return petx;
			}
    	}else{
            throw PackageExceptionFactory.getInstance().getException(
                    "无效的宠物位置：" + gridIndex);
        }
    }
    
    /**
     * 根据宠物实例移除
     * @param pet
     * @return 被移除的位置
     * @throws BagException
     */
    public int remove(Pet pet) throws BagException{
    	if(null != pet){
        	synchronized(petList){
        		log.debug("remove pet, curr pet list size = " + getFullGridNumber());
        		for(int i=0; i<petList.length;i++){
        			if(null != petList[i] && petList[i]==pet){
        				petList[i] = null;
        				emptyGridNumber ++;
        				return i;
        			}
        		}
        	}
    	}else{
    		return -1;
    	}
    	throw PackageExceptionFactory.getInstance().getException("不存在的宠物");
    	
    }
    
    /**
     * 根据宠物ID，移除宠物
     * @param petID
     * @return
     * @throws BagException
     */
    public Pet remove(int petID){
    	synchronized(petList){
    		log.debug("remove pet, curr pet list size = " + getFullGridNumber());
    		for(int i=0; i<petList.length; i++){
    			if(null != petList[i] && petList[i].id == petID){
    				Pet petx = petList[i];
    				petList[i] = null;
    				emptyGridNumber ++;
    				log.debug("remove ["+i+"], petid= "+petID);
    				return petx;
    			}
    		}
    	}
    	//throw PackageExceptionFactory.getInstance().getException("不存在的宠物");
        return null;
    }
    
    /**
     * 根据位置获取宠物
     * @param gridIndex
     * @return
     * @throws BagException
     */
    public Pet getPet(int gridIndex) throws BagException{
    	if(gridIndex >=0 && gridIndex<petList.length){
    		return petList[gridIndex];
    	}
    	return null;
    }
    
    /**
     * 获取宠物列表
     * @return
     * @throws BagException
     */
    public Pet[] getPetList() throws BagException{
    	return petList;
    }
    /**
     * 根据宠物获取位置
     * @param pet
     * @return
     * @throws BagException
     */
    public short getGridNumber(Pet pet) throws BagException{
    	if(null != pet){
    		for(int i=0; i<petList.length; i++){
    			if(petList[i] == pet){
    				return (short)i;
    			}
    		}
    	}
    	return -1;
    }

    /**
     * 升级宠物背包
     *
     * @return 是否成功升级（false：已达到顶级）
     */
    public boolean upgrade ()
    {
        if (petList.length == Inventory.BAG_MAX_SIZE)
        {
            return false;
        }

        Pet[] newPetList = new Pet[petList.length + Inventory.STEP_GRID_SIZE];

        System.arraycopy(petList, 0, newPetList, 0, petList.length);

        petList = newPetList;
        emptyGridNumber += Inventory.STEP_GRID_SIZE;

        return true;
    }
}
