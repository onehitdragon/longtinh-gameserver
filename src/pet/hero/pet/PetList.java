package hero.pet;

import hero.pet.service.PetDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PetList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-12 下午03:00:56
 * @描述 ：
 */

public class PetList
{
	private static Logger log = Logger.getLogger(PetList.class);
    /**
     * 上次离线时显示的宠物编号
     * 可以同时显示2只
     */
    private List<Integer> lastTimesViewPetID = new ArrayList<Integer>();

    /**
     * 当前可见宠物
     * 可以同时显示2只
     */
    private HashMap<Integer, Pet>  viewPet = new HashMap<Integer,Pet>();

    /**
     * 宠物列表
     */
    private ArrayList<Pet> petList;

    /**
     * 构造
     */
    public PetList()
    {
        petList = new ArrayList<Pet>(MAX_NUMBER);
    }

    /**
     * 添加宠物
     * 
     * @param _pet
     * @return 成功是否（false:已满）
     */
    public boolean add (Pet _pet)
    {
        if (MAX_NUMBER <= petList.size())
        {
            return false;
        }

        return petList.add(_pet);
    }

    /**
     * 存在相同宠物
     * 
     * @param _pet
     * @return
     */
    public boolean exists (Pet _pet)
    {
        return petList.contains(_pet);
    }

    /**
     * 改变显示宠物
     * 
     * @param _opet 将要被替换显示的宠物
     * @param _npet 将要显示的宠物
     */
    public void changeViewPet (Pet _opet, Pet _npet)
    {
    	if(_opet != null){
    		_opet.viewStatus = 0;
    		_opet.isView = false;
    		viewPet.remove(_opet.id);
    	}
    	if(_npet != null){
    		_npet.viewStatus = 1;
    		_npet.isView = true;
    		viewPet.put(_npet.id, _npet);
    	}
    }
    
    /**
     * 隐藏、收起宠物
     * @param _pet
     */
    public void removeViewPet(Pet _pet){
    	viewPet.remove(_pet.id);
    	_pet.viewStatus = 0;
    	_pet.isView = false;
    }

    /**
     * 设置要显示的宠物
     * 最多同时显示2只
     * 宠物蛋也可以放到viewPet里开始孵化，但设置isView=false,不在界面上显示
     * @param pet
     * @return 当前显示宠物的数量，最多为2
     */
    public int setViewPet(Pet pet){
    	log.debug("set view pet id = " + pet.id);
    	if(pet.pk.getStage() != 0){
    		pet.isView = true;
    	}else
    		pet.isView = false;
    	pet.viewStatus = 1;
    	viewPet.put(pet.id, pet);
    	return viewPet.size();
    }
    
   
    /**
     * 获取所有显示的宠物
     * 目前最多2只
     * @return
     */
    public HashMap<Integer,Pet> getViewPet ()
    {
        return viewPet;
    }

    /**
     * 设置上次离线时显示的宠物编号
     * 
     * @param _petID
     * @return lastTimesViewPetID 里petid的数量,最多为2
     */
    public int setLastTimesViewPetID (int _petID)
    {
    	lastTimesViewPetID.add(_petID);
    	return lastTimesViewPetID.size();
    }

    /**
     * 获取上次离线时显示的宠物
     * 
     * @return
     */
    public List<Integer> getLastTimesViewPetID()
    {
    	return lastTimesViewPetID;
    }

    /**
     * 丢弃宠物
     * 
     * @param _petID
     * @return 丢弃的是否是显示的宠物
     */
    public boolean dicePet (Pet _pet)
    {
        if (petList.remove(_pet))
        {
        	removeLastTimesViewPetID(_pet.id);
            
            viewPet.remove(_pet.id);
            log.debug("dict pet removed pet is null? " + viewPet.get(_pet.id));
            _pet = null;
            return true;
        }

        return false;
    }
    
    public void removeLastTimesViewPetID(Integer petID){
    	for(Iterator<Integer> it = lastTimesViewPetID.iterator(); it.hasNext();){
    		if(it.next() == petID){
    			it.remove();
    		}
    	}
    }

    /**
     * 获取宠物列表
     * 
     * @return
     */
    public ArrayList<Pet> getPetList ()
    {
        return petList;
    }
    
    /**
     * 获取玩家的某个宠物 
     * @param pk
     * @return
     */
    public Pet getPet(int id){
    	for(Pet pet : petList){
    		if(id==pet.id){
    			return pet;
    		}
    	}
    	return null;
    }

    /**
     * 最多数量
     */
    public static final short MAX_NUMBER = 100;
    /**
     * 最多显示的数量
     */
    public static final short MAX_SHOW_NUMBER = 2;
}
