package hero.pet.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hero.item.dictionary.SpecialGoodsDict;
import hero.item.special.PetFeed;
import hero.log.service.CauseLog;
import hero.log.service.FlowLog;
import hero.log.service.LoctionLog;
import hero.log.service.LogServiceImpl;
import hero.pet.*;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;
import yoyo.tools.YOYOOutputStream;

import hero.expressions.service.CEService;
import hero.item.EquipmentInstance;
import hero.item.PetWeapon;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.PetEquipmentDict;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.pet.clienthandler.OperatePet;
import hero.pet.message.PetChangeNotify;
import hero.pet.message.PetLearnSkillNotify;
import hero.pet.message.ResponseAbilityListChange;
import hero.pet.message.ResponseDiscardPoint;
import hero.pet.message.ResponseFeedStatusChange;
import hero.pet.message.ResponsePetContainer;
import hero.pet.message.ResponsePetEvolveChange;
import hero.pet.message.ResponsePetRevive;
import hero.pet.message.ResponsePetSkillIDList;
import hero.pet.message.ResponsePetStage;
import hero.pet.message.ResponseWearPetGridNumber;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.EMagic;
import hero.share.EObjectLevel;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;
import hero.skill.PetSkill;
import hero.skill.detail.ESkillType;
import hero.skill.dict.PetSkillDict;
import hero.skill.service.SkillServiceImpl;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PetServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-12 下午01:31:48
 * @描述 ：宠物服务
 */

public class PetServiceImpl extends AbsServiceAdaptor<PetConfig>
{
	private static Logger log = Logger.getLogger(PetServiceImpl.class);
    /**
     * 玩家所有宠物列表容器（玩家编号：宠物列表<宠物编号>）
     */
    private FastMap<Integer, PetList> petListContainer;
    
    /**
     * 有效的技能冷却时间，超过此时间的才会入库（秒）
     */
    public static final int VALIDATE_CD_TIME = 300;


    /**
     * 单例
     * 
     * @return
     */
    public static PetServiceImpl getInstance ()
    {
        if (null == instance)
        {
            instance = new PetServiceImpl();
        }

        return instance;
    }

    @Override
    public void createSession (Session _session)
    {
    	/*log.debug("petServiceImpl serssion create ..... " + _session.userID);
    
        if (!petListContainer.containsKey(_session.userID))
        {
            PetList petList = PetDAO.load(_session.userID);
            petListContainer.put(_session.userID, petList);
            log.debug("pet Serviceimpl session create petlist size = " + petList.getPetList().size());
            
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
            
            PetSkillDict.getInstance().load(config);
            
            log.debug("session create load petlist .... ");
            initInfo(petList,player);
        }*/
        
        
        
    }

    @Override
    public void sessionFree (Session _session)
    {

    }

    public void clean (int _userID)
    {
        PetList list = petListContainer.remove(_userID);

        if (null != list)
        {
            if (list.getLastTimesViewPetID().size() == 0)
            {
                if (null != list.getViewPet())
                {
                	for(Iterator<Integer> it = list.getViewPet().keySet().iterator(); it.hasNext();){
                		PetDAO.updateViewStatus(_userID, it.next(),VIEW_STATUS_OF_UPDATE_NOW);
                	}
                }
            }
            else
            {
            	PetDAO.updateViewStatus(_userID, 0,VIEW_STATUS_OF_UPDATE_OLD);
                if (null != list.getViewPet())
                {
                	for(Iterator<Integer> it = list.getViewPet().keySet().iterator(); it.hasNext();){
                        PetDAO.updateViewStatus(_userID, it.next(),VIEW_STATUS_OF_UPDATE_ALL);
                	}
                }
                
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.ServiceAdaptor#startService()
     */
    public void start ()
    {
    	/*try{
    		PetDictionary.getInstance().load(config.pet_data_path,config.feed_data_path);
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}*/
    }
    
    /**
     * 给新手添加一个宠物蛋
     * 默认是不显示的
     * @param _player
     */
    public Pet addPetEgg(HeroPlayer _player){
    	Pet pet = Pet.getRandomPetEgg();
    	log.debug("add pet egg = " + pet);
    	short _color = (short)PetColor.getRandomPetEggColor().getId();
    	log.debug("pet egg color = " + _color);
    	pet.color = _color;
    	pet.bind = 1;
    	pet.bornFrom = 1;
    	pet.viewStatus = 0;
    	pet.isView = false;
    	
    	try{
        	if(_player.getInventory().getPetContainer().add(pet)>=0){
        		PetDAO.addPetForNewPlaye(_player.getUserID(), pet);
            	
            	log.debug("add pet egg after id = " + pet.id);
    
            	pet.totalOnlineTime = 0;
            	pet.startHatchTime = System.currentTimeMillis();
            	
            	PetList list = new PetList();
            	list.add(pet);
            	petListContainer.put(_player.getUserID(), list);
            	
    //        	hatchPet(_player, pet);//孵化 
            	
            	initInfo(list,_player);
        	}
    	}catch(BagException e){
    		log.error("add pet egg error : ", e);
    	}
    	
    	
    	
    	return pet;
    }

    /**
     * 添加新宠物
     * 不能用于玩家之间的交易宠物
     * @param _userID
     * @param _petID
     */
    public Pet addPet (int _userID, int _petAID)
    {
        PetList list = petListContainer.get(_userID);

        if (null == list)
        {
            list = new PetList();
            petListContainer.put(_userID, list);
        }

        //宠物的固定信息从字典里加载
        Pet pet = PetDictionary.getInstance().getPet(_petAID);

        /*if (list.exists(pet))//玩家可以拥有相同的宠物
        {
            return null;
        }*/
        try{
        	HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        	if(player.getInventory().getPetContainer().add(pet) >= 0){
                if(list.add(pet)){
                	PetDAO.add(_userID, pet);
                	
                	initInfo(list,player);
                }else{
                	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        					new Warning("每个玩家最多只能拥有 "+ PetList.MAX_NUMBER+" 只宠物！"));
                }
        	}
        }catch (Exception e){
        	log.error("add pet error : ",e);
        }

        return pet;
    }
    
  //新加或玩家刚进入游戏时，初始化玩家宠物相关信息
    private void initInfo(PetList list,HeroPlayer player){
    	try
		{
    		log.debug("初始化玩家宠物相关信息 : ");
			player.getInventory().getPetContainer().init(list.getPetList());
			if(list.getViewPet().size() > 0){
				player.getBodyWearPetList().init(list.getViewPet());
			}
		}catch(BagException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Pet pet : list.getPetList()){
    		PetServiceImpl.getInstance().initPetSkillList(pet, PetDAO.loadPetSkill(pet));
        	pet.masterID = player.getUserID();
        	
        	//del:	zhengl
        	//date:	2010-10-14
        	//note:	宠物加载的时候暂时默认为跟随状态.
//        	if(pet.isView && !pet.isActivePetAI() && pet.pk.getType() == 2) {
//        		//宠物是展示状态 
//        		pet.startAITask();
//        	}
		}
    }

    /**
     * 
     * @param _player
     * @param _pet 将要孵化的宠物蛋
     */
    public void hatchPet(HeroPlayer _player, Pet _pet){
    	if(_player != null &&_pet != null && _pet.pk.getStage() == Pet.PET_STAGE_EGG){
        	Timer timer = new Timer();
        	timer.schedule(new HatchPetTask(_player,_pet), 0, HATCH_PET_TASK_INTERVAL);
    	}
    }
    
    /**
     * 修改宠物名称
     * @param _player
     * @param petid
     * @param name
     * @return
     */
    public int modifyPetName(HeroPlayer _player, int petid, String name){
    	PetList petList = petListContainer.get(_player.getUserID());
		Pet pet = petList.getPet(petid);
		int succ = PetDAO.updatePetName(_player.getUserID(), petid, name);
		if(succ == 1){
			pet.name = name;
		}
		return succ;
    }
    
    /**
     * 交易宠物
     * @param sellerID 卖方ID
     * @param buyerID 买方ID
     * @param petID
     * @return 交易成功 1;  失败 0
     */
    public int transactPet(int sellerID, int buyerID, int petID){
    	PetList sellerPetList = petListContainer.get(sellerID);
    	Pet pet = sellerPetList.getPet(petID);
    	PetList buyerPetList = petListContainer.get(buyerID);
    	
    	int res = PetDAO.updatePetOwner(buyerID, sellerID, petID);
    	if(res == 1){
    		if(sellerPetList.dicePet(pet)){
    			if(buyerPetList.add(pet)){
                    // 物品变更日志
                    LogServiceImpl.getInstance().petChangeLog(
                            PlayerServiceImpl.getInstance().getPlayerByUserID(buyerID),
                            pet, 1, LoctionLog.BAG, FlowLog.GET,
                            CauseLog.EXCHANGE);
    				return 1;
    			}
    		}
    	}
    	return 0;
    }
    
    /**
     * 显示宠物
     * 把宠物装备到玩家身上才能显示
     * 也可以把宠物蛋装备到玩家身上，开始孵化，但是不显示
     * @param _petID
     * @return 显示的宠物编号
     */
    public void showPetx(HeroPlayer player, int petID){
        if(player.isDead()){
            ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),new Warning("你已经死亡，不能进行此操作！"));
            return;
        }
        HashMap<Integer,Pet> viewPetMap = getViewPetList(player.getUserID());
        if(viewPetMap != null && viewPetMap.size()>0){
            Pet pet = getPet(player.getUserID(), petID);
            log.debug("will show pet id = " + pet.id +",pet stage = " + pet.pk.getStage() +" , had view pet size = " + viewPetMap.size());
            if(viewPetMap.size() == PetList.MAX_SHOW_NUMBER){
                if(pet.pk.getStage() == Pet.PET_STAGE_EGG){
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),new Warning("您已携带了 "+PetList.MAX_SHOW_NUMBER+" 只宠物\n请收起一只后再进行孵化"));
                    return;
                }
                Pet[] hPets = new Pet[2];
                int i=0;
                for(Pet p : viewPetMap.values()){
                    hPets[i] = p;
                    i++;
                }
                if(hPets[0].pk.getStage() == Pet.PET_STAGE_EGG
                        && hPets[1].pk.getStage() == Pet.PET_STAGE_EGG){
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),new Warning("您正在孵化 "+PetList.MAX_SHOW_NUMBER+" 只宠物蛋\n请收起一只后再进行装备"));
                    return;
                }
            }
            log.debug("start show pet id = " + pet.id);
            if(viewPetMap.containsKey(petID)){
//                if(pet.isView){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning("这个宠物已经显示！"));
//                }
                /*else{
                    if(showPety(pet,viewPetMap,player))
                        showPet(player, petID);
                }*/
            }else{
                if(showPety(pet,viewPetMap,player))
                    showPet(player, petID);
            }

        }else{
            showPet(player, petID);
        }
//        changePetStatus(hidePetList,showPetList);
    }

    /**
     *  * 显示逻辑：
     *  1、装备跟随宠物或坐骑宠物时判断一下是否已经装备着跟随或坐骑宠物，如果已经有装备着的，则自动将之前装备着的那只收入背包
        2、装备完成后，给予玩家提示语：您的宠物“XXX“已出战！
        3. 蛋可以和跟随宠或者骑宠同时装备在装备格里,在游戏世界里只显示跟随宠，蛋不显示，蛋是在孵，孵完后自动从装备栏上退回到背包里
        4. 可以是两只蛋
        5. 当玩家已经装备了两只非蛋的宠物时，宠物蛋不能再装备孵化，要提示玩家收起一只才能孵化
        6. 当玩家已装备的是两只蛋，再装备其它宠物时，提示玩家收起一只
     *  1蛋1跟
        1蛋1骑
        1跟1骑
        2蛋
     * @param pet
     * @param viewPetMap
     * @param player
     */
    private boolean showPety(Pet pet,HashMap<Integer,Pet> viewPetMap,HeroPlayer player){
        List<Pet> petlistx = new ArrayList<Pet>();
        for(Pet p : viewPetMap.values()){ //因为在收起或显示宠物时会修改viewPetMap里的Pet对象的状态，所以这里先用一个List把Pet对象保存
            petlistx.add(p);              //否则会抛出java.util.ConcurrentModificationException错误
        }
        if(pet.pk.getStage() != Pet.PET_STAGE_EGG){//有可能从蛋进化到幼年，只能显示一只跟随宠物(包含幼年宠物、成年肉食宠物)
            if(pet.pk.getStage() == Pet.PET_STAGE_CHILD){//此时将要显示的宠物是跟随宠物
                for(Pet vpet : petlistx){
                    log.debug("vpet fun = " + vpet.fun );
                    if(vpet.fun != Pet.PET_HERBIVORE_FUN
                            && vpet.pk.getStage() != Pet.PET_STAGE_EGG){ //已装备上的跟随宠物要收起，蛋继续孵化
                        log.debug("已装备上的跟随宠物要收起，蛋继续孵化");
                        hidePet(player,vpet.id);
                    }
//                                if(vpet.fun == Pet.PET_HERBIVORE_FUN){
//                                    //继续显示，1幼年1坐骑
//                                }
                }
            }else{ //此时将要显示的宠物是成年战斗或坐骑宠物
                for(Pet vpet : petlistx){
                    log.debug("vpet 2 fun = " + vpet.fun );
                    if(pet.fun == Pet.PET_CARNIVORE_FUN
                            && vpet.fun != Pet.PET_HERBIVORE_FUN
                                && vpet.pk.getStage() != Pet.PET_STAGE_EGG){//已装备上的跟随宠物要收起，蛋继续孵化
                        hidePet(player,vpet.id);
                    }
                    else if(pet.fun == Pet.PET_HERBIVORE_FUN
                            && vpet.fun == Pet.PET_HERBIVORE_FUN){ //已装备上的坐骑要收起，蛋继续孵化
                        hidePet(player,vpet.id);
                    }
                }
            }

        }else{
            if(viewPetMap.size() == PetList.MAX_SHOW_NUMBER){
                ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),new Warning("您已携带了 "+PetList.MAX_SHOW_NUMBER+" 只宠物\n请收起一只后再进行孵化"));
                return false;
            }
        }
        return true;
    }

    private boolean showPet (HeroPlayer _player, int _petID)
    {
        PetList list = petListContainer.get(_player.getUserID());

        if (null != list)
        {
//            Pet pet = PetDictionary.getInstance().getPet(_petID);
            Pet pet = list.getPet(_petID);
            if (null != pet)
            {
                if (list.exists(pet))
                {
                	log.debug("要显示的宠物所在阶段： stage = " + pet.pk.getStage());
                	if(pet.pk.getStage() != 0){
                    	if(!pet.isDied()){
                            HashMap<Integer,Pet> viewPetMap = getViewPetList(_player.getUserID());

                    		Pet currViewPet =null;
                            if(null != viewPetMap){
                                log.debug("had view pet size = " + viewPetMap.size());
                                for(Pet pet_ : viewPetMap.values()){ //玩家只能显示两只宠物，已显示的只能有一只
                                    currViewPet = pet_;
                                }
                            }
                    		if(pet.pk.getStage() == 1){
                                if(currViewPet != null && currViewPet.pk.getStage() == 1){
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("只能显示一只幼年宠物！"));
                                    return false;
                                }

                    			list.setViewPet(pet);

                    			try{
                    				_player.getInventory().getPetContainer().remove(pet.id);
                    				_player.getBodyWearPetList().add(pet);
                    				
                    				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                    						new ResponsePetContainer(_player.getInventory().getPetContainer()));
                    				
                    				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                    						new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                    			}catch(BagException e){
                    				e.printStackTrace();
                                    return false;
                    			}
                    			
                    			
                                MapSynchronousInfoBroadcast.getInstance().put(
                                        _player.where(),
                                        new PetChangeNotify(_player.getID(),
                                                OperatePet.SHOW, pet.imageID,pet.pk.getType()), true,
                                        _player.getID());
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
            								new Warning("您的宠物\""+pet.name+"\"已出战！"));

                    		}else if(pet.pk.getStage() == 2){
                        		if(currViewPet == null || currViewPet.pk.getType() != pet.pk.getType()){//成年宠物，最多只能显示一只坐骑和一只战斗宠物，不能显示两种同类型宠物
                                    list.setViewPet(pet);
                                    try{
                        				_player.getInventory().getPetContainer().remove(pet.id);
                        				_player.getBodyWearPetList().add(pet);
                        				
                        				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                        						new ResponsePetContainer(_player.getInventory().getPetContainer()));
                        				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                        						new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                        			}catch(BagException e){
                        				e.printStackTrace();
                                        return false;
                        			}
                        			
                        			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetSkillIDList(pet));
                        			
                        			PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                        			
                        			log.debug("add skill property before pet maxMp = " + pet.getActualProperty().getMpMax());
                        			
                        			// 使用被动技能给自身或主人
                                	SkillServiceImpl.getInstance().petReleasePassiveSkill(pet,1);
                                	
                                	log.debug("add skill property after pet maxMp = " + pet.getActualProperty().getMpMax());
                                    
                                    MapSynchronousInfoBroadcast.getInstance().put(
                                            _player.where(),
                                            new PetChangeNotify(_player.getID(),
                                                    OperatePet.SHOW, pet.imageID,pet.pk.getType()), true,
                                            _player.getID());
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
            								new Warning("您的宠物\""+pet.name+"\"已出战！"));

                        		}else if(currViewPet.pk.getType() == pet.pk.getType()){
                        			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
            								new Warning("不能同时显示两只坐骑宠物或两只战斗宠物"));
                                    return false;
                        		}
                    		}
                    	}else{
                    		ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
    								new Warning("这个宠物已经死亡，需要复活才能显示！"));
                            return false;
                    	}
                	}else{//装备宠物蛋到玩家身上,开始孵化，但是不显示 
                		list.setViewPet(pet);
                		hatchPet(_player,pet);
                		
                		
                		try{
            				_player.getInventory().getPetContainer().remove(pet.id);
            				_player.getBodyWearPetList().add(pet);
            				
            				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
            						new ResponsePetContainer(_player.getInventory().getPetContainer()));
            				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                    		
            			}catch(BagException e){
            				e.printStackTrace();
                            return false;
            			}
            			
                		/*
                        MapSynchronousInfoBroadcast.getInstance().put(
                                _player.where(),
                                new PetChangeNotify(_player.getID(),
                                        OperatePet.SHOW, pet.imageID), true,
                                _player.getID());*/
                		ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
								new Warning("宠物蛋\""+pet.name+"\"开始孵化！"));
                	}
                	int currEmptyGridNum = _player.getBodyWear().getEmptyGridNumber();
                	log.debug("显示宠物后，玩家身上  currEmptyGridNum="+currEmptyGridNum);
                	currEmptyGridNum = _player.getBodyWearPetList().getEmptyGridNumber();
                	log.debug("显示宠物后，玩家身上宠物格子 currEmptyGridNum = " + currEmptyGridNum);
                    return true;
                }
                log.debug("@@@ pet isview="+pet.isView+"   pet viewstatus = " + pet.viewStatus);
            }
        }
        return false;
    }

    public void writePetSkillID(Pet pet, YOYOOutputStream output){
    	//宠物技能
    	try
		{
			output.writeByte(pet.petActiveSkillList.size());
			for(PetActiveSkill skill : pet.petActiveSkillList){
	    		output.writeInt(skill.id);
	    		output.writeInt(skill.coolDownTime);//因为宠物自动使用技能是客户端处理的，所以要把技能的冷却时间给客户端
	    	}
	    	output.writeByte(pet.petPassiveSkillList.size());
	    	for(PetPassiveSkill skill : pet.petPassiveSkillList){
				output.writeInt(skill.id);
	    	}
		}catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    /**
     * 隐藏、收起宠物
     * 
     * @param _petID
     */
    public boolean hidePet (HeroPlayer _player, int petID)
    {
        boolean noViewPet = getViewPetList(_player.getUserID())==null || getViewPetList(_player.getUserID()).size()==0;
        if(noViewPet){
            //没有显示的宠物
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning("没有要收起的宠物！"));
            return false;
        }

    	log.debug("@@@@  hide pet id = " + petID);
        PetList list = petListContainer.get(_player.getUserID());

        if (null != list)
        {
        	Pet pet = list.getPet(petID);
        	
            list.removeViewPet(list.getPet(petID));
            if(pet.pk.getStage() == Pet.PET_STAGE_EGG){
            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
						new Warning("宠物蛋\""+pet.name+"\"已经停止孵化！"));
            }else{
                MapSynchronousInfoBroadcast.getInstance().put(
                        _player.where(),
                        new PetChangeNotify(_player.getID(), OperatePet.HIDE,
                                pet.imageID,pet.pk.getType()), true, _player.getID());
            }
        	
        	try{
            	_player.getInventory().getPetContainer().add(pet);
            	_player.getBodyWearPetList().remove(pet.id);
            	
            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
    					new ResponsePetContainer(_player.getInventory().getPetContainer()));
            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
            	
            	log.debug("del skill property before pet maxMp = " + pet.getActualProperty().getMpMax());
            	// 清除被动技能给自身或主人的属性
            	SkillServiceImpl.getInstance().petReleasePassiveSkill(pet,2);
            	log.debug("del skill property after pet maxMp = " + pet.getActualProperty().getMpMax());
            	
            	log.debug("hide pet end...");
        	}catch(BagException e){
        		log.error("hide pet error : ",e);
        		e.printStackTrace();
                return false;
        	}
        	
        	int currEmptyGridNum = _player.getBodyWear().getEmptyGridNumber();
        	log.debug("收起宠物后，玩家身上格子 currEmptyGridNum = " + currEmptyGridNum);
        	currEmptyGridNum = _player.getBodyWearPetList().getEmptyGridNumber();
        	log.debug("收起宠物后，玩家身上宠物格子 currEmptyGridNum = " + currEmptyGridNum);
            return true;
        }
        return false;
    }

    /**
     * 获取宠物列表
     * 
     * @param _userID
     * @return
     */
    public ArrayList<Pet> getPetList (int _userID)
    {
        PetList list = petListContainer.get(_userID);
        
        if (null == list || 0 == list.getPetList().size())
        {
            return null;
        }
        log.debug(" pet serviceimpl get petlist size = " + list.getPetList().size());
        return list.getPetList();
    }
    
    /**
     * 获取已死亡的宠物列表
     * @param _userID
     * @return
     */
    public List<Pet> getDiedPetList(int _userID){
    	PetList list = petListContainer.get(_userID);
    	log.debug(" pet serviceimpl get died pet list size = " + list.getPetList().size());
        if (null == list || 0 == list.getPetList().size())
        {
            return null;
        }
    	List<Pet> petlist = list.getPetList();
    	List<Pet> diedPetList = new ArrayList<Pet>();
    	for(Pet pet : petlist){
    		if(pet.isDied()){
    			diedPetList.add(pet);
    		}
    	}
    	return diedPetList;
    }
    /**
     * 获取显示宠物列表
     * @param _userID
     * @return
     */
    public HashMap<Integer,Pet> getViewPetList(int _userID){
    	PetList list = petListContainer.get(_userID);
    	if(list == null || list.getViewPet().size() == 0){
    		log.debug("get view pet list = null ########33");
    		return null;
    	}
    	return list.getViewPet();
    }
    
    /**
     * 获取某个宠物
     * @param _userID
     * @param _petID 数据库里的petID
     * @return
     */
    public Pet getPet(int _userID, int _petID){
    	PetList list = petListContainer.get(_userID);
    	//edit by zhengl ; date: 2011-01-20 ; note: 这个地方需要加上NULL判断否则会异常.
    	if(list == null) {
    		return null;
    	}
    	return list.getPet(_petID);
    }

    /**
     * 获取玩家的显示宠物编号图片
     * 
     * @param _userID
     * @param _petID 数据库里的petID
     * @return
     */
    public short getViewPetImage (int _userID, int _petID)
    {
        PetList list = petListContainer.get(_userID);

        if (null == list || null == list.getViewPet())
        {
            return 0;
        }

        return list.getViewPet().get(_petID).imageID;
    }

    /**
     * 获取玩家的显示宠物
     * 
     * @param _userID
     * @param _petID
     * @return
     */
    public Pet getViewPet (int _userID, int _petID)
    {
        PetList list = petListContainer.get(_userID);

        if (null == list || null == list.getViewPet())
        {
            return null;
        }

        return list.getViewPet().get(_petID);
    }
    
    /**
     * 修改宠物属性
     * @param _userID
     * @param _pet
     * @param _type 草食或肉食
     */
    public void updatePet(int _userID, Pet _pet){
    	log.debug("DB save update pet id = " + _pet.id);
    	PetDAO.updatePet(_userID, _pet);
    }

    /**
     * 给能力槽分配进化点
     * @param _player
     * @param petID
     * @param code
     * @param points
     */
    public void addAbilityPoint(HeroPlayer player, int petID, byte code, int points){
    	Pet pet = this.getPet(player.getUserID(), petID);
    	if(points > Pet.MAXPERPOINT){
    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    				new Warning("最多只能增加到 " + Pet.MAXHERBPOINT + " 点！"));
    	}else{
    		int npoint;
    		if(code == Pet.AGILECODE){
    			npoint = pet.agile + points;
    			if(npoint > Pet.MAXPERPOINT){
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    	    				new Warning("敏捷槽最多还能分配 "+(Pet.MAXPERPOINT-pet.agile)+" 点！"));
    			}else{
    				pet.agile = npoint;
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    	    				new ResponseAbilityListChange(player,pet,code,pet.agile));
    			}
    		}
    		if(code == Pet.RAGECODE){
    			npoint = pet.rage + points;
    			if(npoint > Pet.MAXPERPOINT){
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    	    				new Warning("愤怒槽最多还能分配 "+(Pet.MAXPERPOINT-pet.rage)+" 点！"));
    			}else{
    				pet.rage = npoint;
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    	    				new ResponseAbilityListChange(player,pet,code,pet.rage));
    			}
    		}
    		if(code == Pet.WITCODE){
    			npoint = pet.wit + points;
    			if(npoint > Pet.MAXPERPOINT){
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    	    				new Warning("智慧槽最多还能分配 "+(Pet.MAXPERPOINT-pet.rage)+" 点！"));
    			}else{
    				pet.wit = npoint;
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    	    				new ResponseAbilityListChange(player,pet,code,pet.wit));
    			}
    		}
    		updatePet(player.getUserID(),pet);
    	}
    }
    
    /**
     * 用洗点道具给能力槽洗点
     * @param _player
     * @param pet
     */
    public void dicardPoint(HeroPlayer _player, Pet pet){
    	if(pet.dicard_code == Pet.AGILECODE){
    		pet.currEvolvePoint += pet.agile;
    		pet.agile = 0;
    	}
    	if(pet.dicard_code == Pet.RAGECODE){
    		pet.currEvolvePoint += pet.rage;
    		pet.rage = 0;
    	}
    	if(pet.dicard_code == Pet.WITCODE){
    		pet.currEvolvePoint += pet.wit;
    		pet.wit = 0;
    	}
    	updatePet(_player.getUserID(),pet);
    	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseDiscardPoint(_player, pet, pet.dicard_code));
    }
    
    /**
     * 丢弃宠物
     * 
     * @param _petID
     * @return 丢弃的是否是显示的宠物
     */
    public void dicePet (HeroPlayer _player,int _petID)
    {
        PetList list = petListContainer.get(_player.getUserID());

        if (null != list)
        {
//            Pet pet = PetDictionary.getInstance().getPet(_pk);
        	Pet pet = list.getPet(_petID);

            if (null != pet)
            {
            	
            	
                if (list.exists(pet))
                {
                	
                    if (list.dicePet(pet))
                    {
                    	try{
            				_player.getInventory().getPetContainer().remove(pet);
            				_player.getBodyWearPetList().remove(pet);
            				
            				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
            						new ResponsePetContainer(_player.getInventory().getPetContainer()));
            				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                    		
            				// 清除被动技能给自身或主人的属性
                        	SkillServiceImpl.getInstance().petReleasePassiveSkill(pet,2);
            			}catch(BagException e){
            				e.printStackTrace();
            			}
                    	
                        MapSynchronousInfoBroadcast.getInstance().put(
                                _player.where(),
                                new PetChangeNotify(_player.getID(),
                                        OperatePet.HIDE, (short)0,pet.pk.getType()), true,
                                _player.getID());
                    }
                }

                PetDAO.dice(_player.getUserID(), _petID);
            }
        }
    }
    
    /**
     * 宠物复活
     * @param userID
     * @param petID
     */
    public boolean petRevive(HeroPlayer player, int petID){
    	try{
    		Pet pet = this.getPet(player.getUserID(), petID);
        	if(pet != null){
        		pet.feeding = Pet.FEEDING_GREEN_HALF;
        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetRevive(player,pet));
        		return true;
        	}else{
        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
    					new Warning("获取宠物失败！"));
        		return false;
        	}
    	}catch(Exception e){
    		log.error("pet revice error : ", e);
    	}
    	return false;
    }
    
    /**
     * 宠物升级
     * 客户端会每分钟发一次报文
     * @param pet
     * @return 下一级数
     */
    public int petUpgrade(int userID, Pet pet){
    	pet.currLevelTime += 1;
    	log.debug("pet level = "+pet.level+" ,curr level time = " + pet.currLevelTime);
    	if(pet.currLevelTime == pet.getToNextLevelNeedTime()){
    		pet.level += 1;
    		pet.currLevelTime = 0;
    		if(pet.pk.getStage() == Pet.PET_STAGE_ADULT && pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
    			// TODO pet.mp = ;
    			Pet pet_x = PetDictionary.getInstance().getPet(pet.pk);
    			pet.str = CEService.playerBaseAttribute(pet.level,pet_x.a_str);
    			pet.agi = CEService.playerBaseAttribute(pet.level,pet_x.a_agi);
    			pet.intel = CEService.playerBaseAttribute(pet.level,pet_x.a_intel);
    			pet.spi = CEService.playerBaseAttribute(pet.level,pet_x.a_spi);
    			pet.luck = CEService.playerBaseAttribute(pet.level,pet_x.a_luck);
    		}
    		PetDAO.upgradePet(userID, pet);
    		log.debug("upet upgrade after level = " + pet.level);
    	}
    	
    	return pet.level;
    }
    
    /**
     * 饲料喂养宠物
     * @param pet
     * @param feedID
     */
    public boolean feedPet(int _userID, Pet pet, int feedID){
    	HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
    	if(!pet.isDied()){
    		PetFeed feed = (PetFeed)SpecialGoodsDict.getInstance().getSpecailGoods(feedID);
    		if(null != feed){  
    			/*
    			if(pet.pk.getStage() == Pet.PET_STAGE_EGG){    				
    				OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
							new Warning("宠物未到幼年，不能喂养！"));
    				return;
        			
    			}*/
    			
    			//宠物幼年可以喂养草食或肉食成长饲料，绿色状态下对应进化点+1;
    			if(pet.pk.getStage() == Pet.PET_STAGE_CHILD 
    					&& feed.getFeedType().getTypeID() < FeedType.DADIJH.getTypeID()){
    				if(pet.feeding > Pet.FEEDING_YELLOW_FULL){
    					// 此状态下不能喂养
						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
								new Warning("宠物在当前状态下喂养无效"));
						return false;
    				}else if(pet.feeding > Pet.FEEDING_YELLOW_HALF && pet.feeding <= Pet.FEEDING_YELLOW_FULL){
    					if(feed.getFeedType() == FeedType.NORMAL){
    						// 无效
    						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
    								new Warning("宠物在当前状态下喂养普通饲料无效，要喂养成长饲料"));
    						return false;
    					}else{ 
    						pet.feeding = Pet.FEEDING_GREEN_FULL;
    						if(feed.getFeedType() == FeedType.HERBIVORE){
        						pet.currHerbPoint = pet.currHerbPoint + 1;
        					}else if(feed.getFeedType() == FeedType.CARNIVORE){
        						pet.currCarnPoint = pet.currCarnPoint + 1;
        					}
    					}
    					if(pet.currCarnPoint >= Pet.MAXCARNPOINT){
    						
    						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
    								new Warning("肉食进化点已足够，可以进化到肉食宠物！"));
    						return false;
    					}
    					if(pet.currHerbPoint >= Pet.MAXHERBPOINT){
    						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
    								new Warning("草食进化点已足够，可以进化到草食宠物！"));
    						return false;
    					}
//    					log.debug("幼年喂养后，当前草食进化点");
    				}else if(pet.feeding <= Pet.FEEDING_YELLOW_HALF && pet.feeding > Pet.FEEDING_RED_FULL){
    					pet.feeding = Pet.FEEDING_YELLOW_FULL;
    				}else if(pet.feeding <= Pet.FEEDING_RED_FULL && pet.feeding > Pet.FEEDING_RED_HALF){
    					if(feed.getFeedType() == FeedType.NORMAL)
    						pet.feeding = Pet.FEEDING_RED_FULL;
    					else{//成长饲料
    						pet.feeding = Pet.FEEDING_YELLOW_FULL;
    					}
    				}else if(pet.feeding <= Pet.FEEDING_RED_HALF){
    					if(feed.getFeedType() == FeedType.NORMAL)
    						pet.feeding = Pet.FEEDING_RED_FULL;
    					else{//成长饲料
    						pet.feeding = Pet.FEEDING_YELLOW_HALF;
    					}
        			}
    			}else if(pet.pk.getStage() == Pet.PET_STAGE_ADULT 
    					&& feed.getFeedType().getTypeID() >= FeedType.DADIJH.getTypeID()){//成年喂养
    				if(pet.feeding > Pet.FEEDING_YELLOW_FULL){
    					// 此状态下不能喂养
						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
								new Warning("宠物在当前状态下喂养无效"));
						return false;
    				}else{
    					if(pet.feeding > Pet.FEEDING_YELLOW_HALF && pet.feeding <= Pet.FEEDING_YELLOW_FULL){
    				
        					if(pet.pk.getType() == Pet.PET_TYPE_HERBIVORE){
        						pet.feeding = Pet.FEEDING_GREEN_FULL;
//        						pet.startHealthTime = System.currentTimeMillis();
        					}else if(pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
        						if(feed.getFeedType() == FeedType.LYCZ){
        							ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        									new Warning("龙涎草汁只能喂养草食宠物！"));
        							return false;
        						}else if(feed.getFeedType() == FeedType.DADIJH){
        							pet.feeding = Pet.FEEDING_GREEN_FULL;
        							pet.fight_exp += 1;
        							pet.currFightPoint += 1;
//        							pet.startHealthTime = System.currentTimeMillis();
        						}
        					}
        				}else if(pet.feeding <= Pet.FEEDING_YELLOW_HALF && pet.feeding > Pet.FEEDING_RED_FULL){
        					if(pet.pk.getType() == Pet.PET_TYPE_HERBIVORE){
        						pet.feeding = Pet.FEEDING_YELLOW_FULL;
        						
        					}else if(pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
        						if(feed.getFeedType() == FeedType.LYCZ){
        							ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        									new Warning("龙涎草汁只能喂养草食宠物！"));
        							return false;
        						}else if(feed.getFeedType() == FeedType.DADIJH){
        							pet.feeding = Pet.FEEDING_YELLOW_FULL;
        							pet.fight_exp = pet.fight_exp + 1;
        							pet.currFightPoint += 1;
        						}
        					}
        				}else if(pet.feeding <= Pet.FEEDING_RED_FULL && pet.feeding > Pet.FEEDING_RED_HALF){
        					if(pet.pk.getType() == Pet.PET_TYPE_HERBIVORE){
        						pet.feeding = Pet.FEEDING_YELLOW_HALF;
        					}else if(pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
        						if(feed.getFeedType() == FeedType.LYCZ){
        							ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        									new Warning("龙涎草汁只能喂养草食宠物！"));
        							return false;
        						}else if(feed.getFeedType() == FeedType.DADIJH){
        							pet.feeding = Pet.FEEDING_YELLOW_HALF;
        							pet.fight_exp = pet.fight_exp + 1;
        							pet.currFightPoint += 1;
        						}
        					}
        				}else if(pet.feeding <= Pet.FEEDING_RED_HALF){
        					if(pet.pk.getType() == Pet.PET_TYPE_HERBIVORE){
        						pet.feeding = Pet.FEEDING_RED_FULL;
        					}else if(pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
        						if(feed.getFeedType() == FeedType.LYCZ){
        							ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
        									new Warning("龙涎草汁只能喂养草食宠物！"));
        							return false;
        						}else if(feed.getFeedType() == FeedType.DADIJH){
        							pet.feeding = Pet.FEEDING_RED_FULL;
        							pet.fight_exp = pet.fight_exp + 1;
        							pet.currFightPoint += 1;
        						}
        					}
        				}
    					// TODO 宠物战斗经验、成长点自动递增
    					pet.updFEPoint();
    					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetEvolveChange(player,pet));
    				}
    			}
    			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseFeedStatusChange(player.getUserID(),pet));
    			updatePet(_userID, pet);
    		}
    		return true;
    	}else{
    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
					new Warning("这个宠物已经死亡，不能喂养，请用复活道具复活！"));
			return false;
    	}
    }
    
    /**
     * 给宠物换装备后，更新
     * @param pet
     */
    public void updatePetEquipment(Pet pet){
    	PetDAO.updPetEquipment(pet);
    }

    /**
     * 私有构造
     */
    private PetServiceImpl()
    {
        config = new PetConfig();
        petListContainer = new FastMap<Integer, PetList>();
    }

    /**
     * 单例
     */
    private static PetServiceImpl instance;
    
    /**
     * 刷新宠物属性
     * @param pet
     */
    public void reCalculatePetProperty(Pet pet){
       try{
    	int maxHP = 0, maxMP = 0, inte = 0, strength = 0, spirit = 0, lucky = 0, defense = 0, agility = 0;
        short physicsDeathblowLevel = 0, magicDeathblowLevel = 0, hitLevel = 0, duckLevel = 0;
        
        int level = pet.level;

        //edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
        physicsDeathblowLevel = CEService.physicsDeathblowLevel(pet.a_agi, pet.a_luck);
        magicDeathblowLevel = CEService.magicDeathblowLevel(pet.a_intel, pet.a_luck);
        hitLevel = CEService.hitLevel(pet.a_luck);
        duckLevel = CEService.duckLevel(pet.a_agi, pet.a_luck);

        inte = CEService.playerBaseAttribute(level, pet.a_intel);
        strength = CEService.playerBaseAttribute(level, pet.a_str);
        spirit = CEService.playerBaseAttribute(level, pet.a_spi);
        lucky = CEService.playerBaseAttribute(level, pet.a_luck);
        agility = CEService.playerBaseAttribute(level, pet.a_agi);
        
        EquipmentInstance[] equipmentList = pet.getPetBodyWear().getEquipmentList();
        log.debug("pet equipmentlist size = " + equipmentList.length);
        for(EquipmentInstance ei : equipmentList){
//        	maxHP += ei.getArchetype().atribute.hp;
        	log.debug("ei  == " +ei);
        	if(null != ei){
            	maxMP += ei.getArchetype().atribute.mp;
            	inte += ei.getArchetype().atribute.inte;
            	strength += ei.getArchetype().atribute.strength;
            	spirit += ei.getArchetype().atribute.spirit;
            	lucky += ei.getArchetype().atribute.lucky;
            	defense += ei.getArchetype().atribute.defense;
            	agility += ei.getArchetype().atribute.agility;
            	
            	physicsDeathblowLevel += ei.getArchetype().atribute.physicsDeathblowLevel;
            	hitLevel += ei.getArchetype().atribute.hitLevel;
            	magicDeathblowLevel += ei.getArchetype().atribute.magicDeathblowLevel;
            	duckLevel += ei.getArchetype().atribute.duckLevel;
        	}
        }
        maxMP += CEService.mpByInte(inte, level, EObjectLevel.NORMAL.getMpCalPara());
        
        pet.mp = maxMP;
        pet.intel += inte;
        pet.str += strength;
        pet.spi += spirit;
        pet.luck += lucky;
        pet.agi += agility;
        pet.physicsDeathblowLevel = physicsDeathblowLevel;
        pet.magicDeathblowLevel = magicDeathblowLevel;
        pet.hitLevel = hitLevel;
        pet.duckLevel = duckLevel;
        
        pet.getBaseProperty().setMpMax(pet.mp);
        pet.getBaseProperty().setInte(pet.intel);
        pet.getBaseProperty().setStrength(pet.str);
        pet.getBaseProperty().setSpirit(pet.spi);
        pet.getBaseProperty().setLucky(pet.luck);
        pet.getBaseProperty().setPhysicsDeathblowLevel(pet.physicsDeathblowLevel);
        pet.getBaseProperty().setMagicDeathblowLevel(pet.magicDeathblowLevel);
        pet.getBaseProperty().setHitLevel(pet.hitLevel);
        pet.getBaseProperty().setPhysicsDuckLevel(pet.duckLevel);
        
        PetWeapon weapon = null;
        
        EquipmentInstance ei = pet.getPetBodyWear().getPetEqWeapon();
        
        if(null != ei){
        	weapon = (PetWeapon)ei.getArchetype();
        	
        	int weaponMinMagicHarm = 0, weaponMaxMagicHarm = 0;
            
            pet.setAttackRange(weapon.getAttackDistance());
            pet.setBaseAttackImmobilityTime((int)weapon.getImmobilityTime());
            
            if (null != ei.getMagicDamage())
            {
                weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                
                pet.maxMagicHarm = pet.magicHarm + weaponMaxMagicHarm;
                pet.minMagicHarm = pet.magicHarm + weaponMinMagicHarm;
            }
            
            pet.maxAtkHarm = pet.getATK() + weapon.getMaxPhysicsAttack();
            pet.minAtkHarm = pet.getATK() + weapon.getMinPhysicsAttack();
        }
        
        pet.getBaseProperty().setMaxPhysicsAttack(pet.maxAtkHarm);
        pet.getBaseProperty().setMinPhysicsAttack(pet.minAtkHarm);

        /*int baseMagicHarmByInte = CEService.magicHarmByInte(inte);

        pet.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SANCTITY,
                baseMagicHarmByInte);
        pet.getBaseProperty().getBaseMagicHarmList().reset(EMagic.UMBRA,
                baseMagicHarmByInte);
        pet.getBaseProperty().getBaseMagicHarmList().reset(EMagic.FIRE,
                baseMagicHarmByInte);
        pet.getBaseProperty().getBaseMagicHarmList().reset(EMagic.WATER,
                baseMagicHarmByInte);
        pet.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SOIL,
                baseMagicHarmByInte);*/

        pet.getActualProperty().clearNoneBaseProperty();
//        pet.getResistOddsList().clear();
        
        pet.getActualProperty().setMaxPhysicsAttack(pet.getBaseProperty().getMaxPhysicsAttack());
        pet.getActualProperty().setMinPhysicsAttack(pet.getBaseProperty().getMinPhysicsAttack());
        pet.setActualAttackImmobilityTime(pet.getBaseAttackImmobilityTime());
        
        /*pet.getActualProperty().setStamina(
        		pet.getBaseProperty().getStamina());*/
        pet.getActualProperty()
                .setInte(pet.getBaseProperty().getInte());
        pet.getActualProperty().setStrength(
        		pet.getBaseProperty().getStrength());
        pet.getActualProperty().setSpirit(
        		pet.getBaseProperty().getSpirit());
        pet.getActualProperty().setLucky(
        		pet.getBaseProperty().getLucky());
        pet.getActualProperty().setAgility(
        		pet.getBaseProperty().getAgility());
        pet.getActualProperty().setDefense(
        		pet.getBaseProperty().getDefense());
        pet.getActualProperty().setPhysicsDeathblowLevel(
        		pet.getBaseProperty().getPhysicsDeathblowLevel());
        pet.getActualProperty().setMagicDeathblowLevel(
        		pet.getBaseProperty().getMagicDeathblowLevel());
        pet.getActualProperty().setHitLevel(
        		pet.getBaseProperty().getHitLevel());
        pet.getActualProperty().setPhysicsDuckLevel(
        		pet.getBaseProperty().getPhysicsDuckLevel());

        /*pet.getActualProperty().setHpMax(
        		pet.getBaseProperty().getHpMax());*/
        pet.getActualProperty().setMpMax(
        		pet.getBaseProperty().getMpMax());
       }catch (Exception e){
           log.error("刷新宠物属性 error：",e);
       }
    }
    
    /**
     * 获取宠物技能实例
     */
    public PetSkill getPetSkillIns(int id){
    	PetSkill petSkill = PetSkillDict.getInstance().getPetSkill(id);
    	if(null != petSkill){
    		try
			{
				return petSkill.clone();
			}catch(CloneNotSupportedException e)
			{
				e.printStackTrace();
				log.error("获取宠物技能实例 error : ",e);
			}
    	}
    	return null;
    }
    
    /**
     * 初始化宠物技能列表
     */
    private void initPetSkillList(Pet pet, ArrayList<int[]> _skillInfoList){
    	
    	pet.petActiveSkillList.clear();
    	pet.petPassiveSkillList.clear();
    	
    	PetSkill petSkill;
    	for(int[] skillInfo : _skillInfoList){
    		petSkill = PetServiceImpl.getInstance().getPetSkillIns(skillInfo[0]);
    		if(null != petSkill){
    			if(petSkill instanceof PetActiveSkill){
    				((PetActiveSkill)petSkill).reduceCoolDownTime = skillInfo[1];
    				pet.petActiveSkillList.add((PetActiveSkill)petSkill);
    			}else{
    				pet.petPassiveSkillList.add((PetPassiveSkill)petSkill);
    			}
    		}
    	}
    	
    }
    
    /**
     * 宠物自动学习技能
     * @param pet
     */
    public boolean petLearnSkill(HeroPlayer player, Pet pet){
    	List<PetSkill> canLearnSkillList = PetSkillDict.getInstance().getPetCanLearnSkillList(pet);
    	log.debug("pet can learn skill size = " + canLearnSkillList.size());
    	List<PetSkill> newLearnSkillList = new ArrayList<PetSkill>();
    	List<PetSkill> oldLearnSkillList = new ArrayList<PetSkill>();
    	for(PetSkill skill : canLearnSkillList){
    		if(skill instanceof PetActiveSkill){
    			if(skill.getFrom == 1){
        			for(PetActiveSkill actSkill : pet.petActiveSkillList){
        				if(skill.isSameName(actSkill)){
        					if(skill.level - actSkill.level == 1){
        						skill.isNewSkill = false;
        						skill._lowLevelSkillID = actSkill.id;
        						oldLearnSkillList.add(actSkill);
            					pet.petActiveSkillList.remove(actSkill);
            					pet.petActiveSkillList.add((PetActiveSkill)skill);
            					newLearnSkillList.add(skill);
            					break;
        					}else
        						return false;
        				}
        			}
        			if(skill.level == 1){
        				pet.petActiveSkillList.add((PetActiveSkill)skill);
        				newLearnSkillList.add(skill);
        			}
    			}
    		}else if(skill instanceof PetPassiveSkill) {
    			if(skill.getFrom == 1){
        			for(PetPassiveSkill paSkill : pet.petPassiveSkillList){
        				if(skill.isSameName(paSkill)){
        					if(skill.level - paSkill.level == 1){
        						skill.isNewSkill = false;
        						skill._lowLevelSkillID = paSkill.id;
        						oldLearnSkillList.add(paSkill);
            					pet.petPassiveSkillList.remove(paSkill);
            					pet.petPassiveSkillList.add((PetPassiveSkill)skill);
            					newLearnSkillList.add(skill);
            					break;
        					}else
        						return false;
        				}
        			}
        			if(skill.level == 1){
        				pet.petPassiveSkillList.add((PetPassiveSkill)skill);
        				newLearnSkillList.add(skill);
        			}
    			}
    		}
    	}
    	
    	if(PetDAO.addSkill(pet.id, newLearnSkillList)){
    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new PetLearnSkillNotify(pet,newLearnSkillList));
    	}else{//如果插入数据库失败，则还原宠物原来的技能
    		for(PetSkill skillx : newLearnSkillList){
    			if(skillx.getType() == ESkillType.ACTIVE){
    				pet.petActiveSkillList.remove(skillx);
    			}else{
    				pet.petPassiveSkillList.remove(skillx);
    			}
    		}
    		for(PetSkill skillx : oldLearnSkillList){
    			if(skillx.getType() == ESkillType.ACTIVE){
    				pet.petActiveSkillList.add((PetActiveSkill)skillx);
    			}else{
    				pet.petPassiveSkillList.add((PetPassiveSkill)skillx);
    			}
    		}
    		return false;
    	}
    	return true;
    }
    
    /**
     * TODO 宠物从技能书学习技能
     * @param pet
     * @param skillID
     */
    public boolean petLearnSkillFromSkillBook(HeroPlayer player, Pet pet, int skillID){
    	log.debug("@@ petLearnSkillFromSkillBook .....");
    	PetSkill skill = PetSkillDict.getInstance().getPetSkill(skillID);   	
    	if(null != skill){
    		log.debug("skill book skillID= " + skill.id);
    		List<PetSkill> canLearnSkillList = PetSkillDict.getInstance().getPetCanLearnSkillList(pet);
        	if(canLearnSkillList.contains(skill) && skill.getFrom == 2){
        		log.debug("宠物从技能书学习技能 学习技能 ....");
        		List<PetSkill> newLearnSkillList = new ArrayList<PetSkill>();
        		List<PetSkill> oldLearnSkillList = new ArrayList<PetSkill>();
        		if(skill.getType() == ESkillType.ACTIVE){
                	for(PetActiveSkill petSkill : pet.petActiveSkillList){
                		if(petSkill.isSameName(skill)){
                			if(skill.level - petSkill.level == 1){
                    			skill.isNewSkill = false;
                    			skill._lowLevelSkillID = petSkill.id;
                    			oldLearnSkillList.add(petSkill);
                    			pet.petActiveSkillList.remove(petSkill);
                    			pet.petActiveSkillList.add((PetActiveSkill)skill);
                    			newLearnSkillList.add(skill);
                    			break;
                			}else 
                				return false;
                		}
                	}
                	if(skill.level == 1){
        				pet.petActiveSkillList.add((PetActiveSkill)skill);
        				newLearnSkillList.add(skill);
        			}
                	
        		}else{
        			for(PetPassiveSkill petSkill : pet.petPassiveSkillList){
                		if(petSkill.isSameName(skill)){
                			if(skill.level - petSkill.level == 1){
                    			skill.isNewSkill = false;
                    			skill._lowLevelSkillID = petSkill.id;
                    			oldLearnSkillList.add(petSkill);
                    			pet.petPassiveSkillList.remove(petSkill);
                    			pet.petPassiveSkillList.add((PetPassiveSkill)skill);
                    			newLearnSkillList.add(skill);
                    			break;
                			}else 
                				return false;
                		}
                	}
        			if(skill.level == 1){
        				pet.petPassiveSkillList.add((PetPassiveSkill)skill);
        				newLearnSkillList.add(skill);
        			}
        		}
        		if(PetDAO.addSkill(pet.id, newLearnSkillList)){
            		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new PetLearnSkillNotify(pet,newLearnSkillList));
            	}else{//如果插入数据库失败，则还原宠物原来的技能
            		for(PetSkill skillx : newLearnSkillList){
            			if(skillx.getType() == ESkillType.ACTIVE){
            				pet.petActiveSkillList.remove(skillx);
            			}else{
            				pet.petPassiveSkillList.remove(skillx);
            			}
            		}
            		for(PetSkill skillx : oldLearnSkillList){
            			if(skillx.getType() == ESkillType.ACTIVE){
            				pet.petActiveSkillList.add((PetActiveSkill)skillx);
            			}else{
            				pet.petPassiveSkillList.add((PetPassiveSkill)skillx);
            			}
            		}
            		return false;
            	}
        	}else{
        		log.debug("宠物不能学习此技能书上的技能...");
        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("宠物不能学习此技能！"));
        		return false;
        	}
        	return true;
    	}else{
    		log.debug("没有找到这个技能...");
    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("没有找到这个技能！"));
    	}
    	return false;
    }
    

    /**
     * 更新上次显示状态
     */
    public static final byte      VIEW_STATUS_OF_UPDATE_OLD = 1;

    /**
     * 更新当前宠物显示状态
     */
    public static final byte      VIEW_STATUS_OF_UPDATE_NOW = 2;

    /**
     * 更新上次和当前宠物显示状态
     */
    public static final byte      VIEW_STATUS_OF_UPDATE_ALL = 3;
    
    /**
     * 孵化宠物蛋任务执行间隔(毫秒)
     */
    public static final int	      HATCH_PET_TASK_INTERVAL = 1*60*1000; 
    
    /**
     * 孵化宠物蛋的任务
     * @author jiaodongjie
     *
     */
    private class HatchPetTask extends TimerTask{

    	HeroPlayer player;
    	Pet petegg; //宠物蛋
    	public HatchPetTask(HeroPlayer player, Pet petegg){
    		this.player = player;
    		this.petegg = petegg;
    	}
    	@Override
    	public void run ()
    	{
    		FastList<HeroPlayer> playerList = PlayerServiceImpl.getInstance().getPlayerList();
    		if(playerList.contains(player)){//如果玩家在线
    			log.debug("@@@@@@@ pet egg hatching .... @@@@@@@@");
        		PetList petList = petListContainer.get(player.getUserID());
        		if(petList != null){
            		Pet pet = petList.getPet(petegg.id);
            
            		if(pet != null){
            			log.debug("pet egg id = " + pet.id);
            			log.debug("玩家 " + player.getName() + " 正在孵化宠物蛋 " + pet.name);
            			if(pet.viewStatus == 1){
                    		long needTime = Pet.PET_HATCH_TIME - pet.totalOnlineTime;
                    		log.debug("还需时间： " + needTime);
                    		if(needTime == 0){   
                    			log.debug("时间到，进化到幼年 。。。。");
                    			pet.pk.setStage((short)1); //幼年   
                    			pet.feeding = Pet.FEEDING_GREEN_HALF;
                    			pet.fun = Pet.PET_CHILD_FUN;
                                pet.iconID = PetDictionary.getInstance().getPet(pet.pk).iconID;
    				            pet.imageID = PetDictionary.getInstance().getPet(pet.pk).imageID;
                                //add by zhengl ; date: 2011-01-17 ; note: 动画
                                pet.animationID = PetDictionary.getInstance().getPet(pet.pk).animationID;
                                log.debug("pet pk = "+pet.pk.intValue()+", iconID = " + pet.iconID +" -- imageID = " + pet.imageID);
                    			PetDAO.updatePet(player.getUserID(), pet);//更新宠物数据库数据
                    			//通知玩家宠物蛋已经孵化到幼年阶段
                    			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponsePetStage(player.getUserID(),pet));
                    			this.cancel();
                    			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("恭喜！您的宠物\""+pet.name+"\"已经成长到幼年！"));

                                hidePet(player,pet.id);  //进化到幼年后收回背包

                    			log.debug("孵化成功 ，终止此任务。");
                    		}else{
                                log.debug("时间未到.... 目前总在线时间 为 ： " + pet.totalOnlineTime);
                    			pet.totalOnlineTime = pet.totalOnlineTime + HATCH_PET_TASK_INTERVAL/(60*1000);  

                    		}
            			}else{
            				log.debug("宠物蛋被收起，停止孵化..");
            				PetDAO.updatePet(player.getUserID(), pet);//更新宠物数据库数据
            				this.cancel();
            			}
        		}else{
        			log.debug("获取宠物蛋失败，无法孵化！");
            			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning("获取宠物蛋失败，无法孵化！"));
            			this.cancel();
            		}
        		}
    		}else{
    			log.debug("玩家下线....");
//        			pet.totalOnlineTime = pet.totalOnlineTime+(System.currentTimeMillis() - pet.startHatchTime)/60000;
//        			PetDAO.updatePet(player.getUserID(), pet);
    			log.debug("取消此孵化任务....");
    			this.cancel();
    		}
    	}
    	 
     }
}

 
