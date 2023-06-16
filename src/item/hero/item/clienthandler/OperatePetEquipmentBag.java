package hero.item.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.item.Equipment;
import hero.item.EquipmentInstance;
import hero.item.PetArmor;
import hero.item.PetEquipment;
import hero.item.PetWeapon;
import hero.item.bag.EBagType;
import hero.item.bag.EquipmentContainer;
import hero.item.message.ClothesOrWeaponChangeNotify;
import hero.item.message.PetClothesOrWeaponChangeNotify;
import hero.item.message.ResponseEquipmentBag;
import hero.item.message.ResponsePetEquipmentBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.FlowLog;
import hero.log.service.LoctionLog;
import hero.log.service.LogServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.pet.Pet;
import hero.pet.message.ResponseRefreshPetProperty;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.ui.message.ResponseEuipmentPackageChange;

/**
 * 操作宠物背包
 * @author jiaodongjie
 *
 */
public class OperatePetEquipmentBag extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(OperatePetEquipmentBag.class);
	/**
     * 查看所有宠物装备列表
     */
    private static final byte LIST_BAG            = 1;

    /**
     * 卸下某个食肉宠物身上的装备
     */
    private static final byte UNLOAD              = 2;

    /**
     * 丢弃某个食肉宠物已穿戴的装备
     */
    private static final byte DICE_BODY_EQUIPMENT = 3;

    /**
     * 给某个食肉宠物穿戴物品栏装备
     */
    private static final byte WEAR                = 4;

    /**
     * 整理物品栏所有装备
     */
    private static final byte SORT                = 5;

    /**
     * 丢弃物品栏装备
     */
    private static final byte DICE_BAG_EQUIPMENT  = 6;

	@Override
	public void read () throws Exception
	{
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		try{
    		byte operation = yis.readByte();
    		
    		
    		switch(operation){
    			case LIST_BAG:
    			{
    				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    						new ResponsePetEquipmentBag(player.getInventory().getPetEquipmentBag()));
    				break;
    			}
    			case UNLOAD:
    			{
    				int petID = yis.readInt(); //宠物id
    				byte gridIndex = yis.readByte();
                    int equipmentInsID = yis.readInt();
                    log.debug("pet unload equip gridindex = " + gridIndex + "  equipmentInsid="+equipmentInsID);
                    Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                    if(null != pet){
                    	EquipmentInstance unei = pet.getPetBodyWear().get(gridIndex);
                    	
                    	if(null != unei && unei.getInstanceID() == equipmentInsID){
                    		log.debug("pet unload unei id= " + unei.getInstanceID());
                    		int bagGridIndex = player.getInventory().getPetEquipmentBag().add(unei);
                    		log.debug("bag grid index  = " + bagGridIndex);
                    		if(bagGridIndex != -1){
                    			pet.getPetBodyWear().remove(unei);
                    			
                    			GoodsDAO.changeEquipmentLocation(unei
                                        .getInstanceID(),
                                        EquipmentContainer.PET_BAG, bagGridIndex);

                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ResponseEuipmentPackageChange(
                                                EBagType.PET_EQUIPMENT_BAG.getTypeValue(),
                                                bagGridIndex, unei));

                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ResponseEuipmentPackageChange(
                                                EBagType.PET_BODY_WEAR.getTypeValue(),
                                                gridIndex, null));
                                 
                                AbsResponseMessage msg = new PetClothesOrWeaponChangeNotify(
                                        pet.id,
                                        (byte)unei.getArchetype().getWearBodyPart().value(),
                                        (short) unei.getArchetype().getIconID());
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(), msg);
                                MapSynchronousInfoBroadcast
                                        .getInstance().put(
                                                player.where(), msg,
                                                true, player.getID());
                             
                                PetServiceImpl.getInstance().updatePetEquipment(pet);
	                            GoodsDAO.changeEquipmentLocation(
	                                    unei.getInstanceID(),
	                                    EquipmentContainer.PET_BODY, (short) -1);
	                            //刷新宠物属性
	                            PetServiceImpl.getInstance().reCalculatePetProperty(pet);
	                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
                    		}
                    	}
                    }
    				break;
    			}
    			case DICE_BODY_EQUIPMENT:
    			{
    				int petID = yis.readInt(); //宠物id
    				byte gridIndex = yis.readByte();
                    int equipmentInsID = yis.readInt();
                    
                    Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                    if(null != pet){
                    	EquipmentInstance unei = pet.getPetBodyWear().get(gridIndex);
                    	if(null != unei && unei.getInstanceID() == equipmentInsID){
                    		if (null != unei && unei.getInstanceID() == equipmentInsID)
                            {
                                GoodsServiceImpl.getInstance().diceEquipmentOfBag(
                                        pet,player, pet.getPetBodyWear(), unei,
                                        LoctionLog.PET_BAG, CauseLog.DEL);
                                                                                               
                                AbsResponseMessage msg = new PetClothesOrWeaponChangeNotify(
                                        player.getID(),
                                        (byte)unei.getArchetype().getWearBodyPart().value(),
                                        (short) unei.getArchetype().getIconID());
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(), msg);
                                MapSynchronousInfoBroadcast
                                        .getInstance().put(
                                                player.where(), msg,
                                                true, player.getID());
                             
                                PetServiceImpl.getInstance().updatePetEquipment(pet);
	                            GoodsDAO.changeEquipmentLocation(
	                                    unei.getInstanceID(),
	                                    EquipmentContainer.PET_BODY, (short) -1);
	                            //刷新宠物属性
	                            PetServiceImpl.getInstance().reCalculatePetProperty(pet);
	                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
                    		
                            }
                    	}
                    }
                    break;
    			}
    			case WEAR:
    			{
    				int petID = yis.readInt(); //宠物id
    				byte gridIndex = yis.readByte();
                    int equipmentInsID = yis.readInt();
                    
    				Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
    				if(null != pet){
    					if(pet.pk.getStage() == Pet.PET_STAGE_ADULT 
    							&& pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
    						EquipmentInstance ei = player.getInventory().getPetEquipmentBag().get(gridIndex);
    						if(null != ei && ei.getInstanceID() == equipmentInsID){
    							if(pet.level >= ei.getArchetype().getNeedLevel()){
    								player.getInventory().getPetEquipmentBag().remove(gridIndex);
    								EquipmentInstance uei = pet.getPetBodyWear().wear(ei);//给宠物穿上该装备并卸下原来的装备
    								
    								
    								if (!ei.isBind()
    	                                    && ei.getArchetype().getBindType() == PetEquipment.BIND_TYPE_OF_WEAR)
    	                            {
    	                                ei.bind();
    	                                GoodsDAO.bindEquipment(ei);
    	                            }
    								
    								if(null != uei){//卸下的装备
    									player.getInventory().getPetEquipmentBag().add(gridIndex,uei);
    									ResponseMessageQueue.getInstance().put(
    	                                        player.getMsgQueueIndex(),
    	                                        new ResponseEuipmentPackageChange(
    	                                                EBagType.PET_EQUIPMENT_BAG.getTypeValue(),
    	                                                gridIndex, uei));

    	                                GoodsDAO.changeEquipmentLocation(uei.getInstanceID(),
    	                                        EquipmentContainer.PET_BAG, gridIndex);

    	                                LogServiceImpl.getInstance().goodsChangeLog(
    	                                        player, uei.getArchetype(), 1,
    	                                        LoctionLog.PET_BODY, FlowLog.LOSE,
    	                                        CauseLog.UNLOAD);
    	                                LogServiceImpl.getInstance().goodsChangeLog(
    	                                        player, uei.getArchetype(), 1,
    	                                        LoctionLog.PET_BAG, FlowLog.GET,
    	                                        CauseLog.UNLOAD);
    	                            }
    	                            else
    	                            {
    	                                ResponseMessageQueue.getInstance().put(
    	                                        player.getMsgQueueIndex(),
    	                                        new ResponseEuipmentPackageChange(
    	                                                EBagType.PET_EQUIPMENT_BAG.getTypeValue(),
    	                                                gridIndex, null));
    	                            }
    								
    								LogServiceImpl.getInstance().goodsChangeLog(player,
    	                                    ei.getArchetype(), 1, LoctionLog.PET_BODY,
    	                                    FlowLog.GET, CauseLog.WEAR);
    	                            LogServiceImpl.getInstance().goodsChangeLog(player,
    	                                    ei.getArchetype(), 1, LoctionLog.PET_BAG,
    	                                    FlowLog.LOSE, CauseLog.WEAR);

    	                            ResponseMessageQueue.getInstance().put(
    	                                    player.getMsgQueueIndex(),
    	                                    new ResponseEuipmentPackageChange(
    	                                            EBagType.PET_BODY_WEAR.getTypeValue(),
    	                                            pet.getPetBodyWear().indexOf(ei),ei));
    	                            
    	                            
	                            	if (null == uei
                                            || uei.getArchetype().getIconID() != ei.getArchetype().getIconID())
                                    {
	                            		log.debug("pet wear equipment body part = " + ei.getArchetype().getWearBodyPart());
                                        AbsResponseMessage msg = new PetClothesOrWeaponChangeNotify(
                                                pet.id,
                                                (byte)ei.getArchetype().getWearBodyPart().value(),
                                                (short) ei.getArchetype().getIconID());
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(), msg);
                                        MapSynchronousInfoBroadcast
                                                .getInstance().put(
                                                        player.where(), msg,
                                                        true, player.getID());
                                    }
    	                            
    	                            
    	                            PetServiceImpl.getInstance().updatePetEquipment(pet);
    	                            GoodsDAO.changeEquipmentLocation(
    	                                    ei.getInstanceID(),
    	                                    EquipmentContainer.PET_BODY, (short) -1);
    	                            //刷新宠物属性
    	                            PetServiceImpl.getInstance().reCalculatePetProperty(pet);
    	                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
    								
    							}else{
    								ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    										new Warning("操作失败！宠物等级不够！", 
    												Warning.UI_STRING_TIP));
    							}
    						}
    					}else{
    						ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    								new Warning("操作失败！只有成年肉食宠物才能装备！", 
    										Warning.UI_STRING_TIP));
    					}
    				}else{
    					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
    							new Warning("操作失败！", Warning.UI_STRING_TIP));
    				}
    				break;
    			}
    			case SORT:
    			{
    				if (player.getInventory().getPetEquipmentBag().clearUp())
                    {
                        GoodsDAO.clearUpEquipmentList(player.getInventory()
                                .getPetEquipmentBag().getEquipmentList());

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponsePetEquipmentBag(player.getInventory().getPetEquipmentBag()));
                    }

    				break;
    			}
    			case DICE_BAG_EQUIPMENT:
    			{
    				byte gridIndex = yis.readByte();
                    int equipmentInsID = yis.readInt();
                    log.debug("dict pet bag equip id = " + equipmentInsID);
                    EquipmentInstance ei = player.getInventory().getPetEquipmentBag().get(gridIndex);

                    if(null != ei && ei.getInstanceID() == equipmentInsID){
                        log.debug("dict pet bag equip ei id = " + ei.getInstanceID());
                    	GoodsServiceImpl.getInstance().diceEquipmentOfBag(
                                player,
                                player.getInventory().getPetEquipmentBag(), ei,
                                LoctionLog.PET_BAG, CauseLog.DEL);

                        break;
                    }
    			}
    		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
