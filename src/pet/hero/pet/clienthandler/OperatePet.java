package hero.pet.clienthandler;


import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.effect.Effect;
import hero.effect.dictionry.EffectDictionary;
import hero.effect.service.EffectServiceImpl;
import hero.item.detail.EGoodsTrait;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.item.service.GoodsServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.pet.Pet;
import hero.pet.message.PetChangeNotify;
import hero.pet.message.ResponsePetContainer;
import hero.pet.message.ResponsePetList;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 HidePet.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-12 上午10:35:42
 * @描述 ：操作宠物(显示、收起、丢弃)
 */

public class OperatePet extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(OperatePet.class);
    /**
     * 查看所有宠物的列表
     */
    public static final byte LIST           = 1;

    /**
     * 显示
     */
    public static final byte SHOW           = 2;

    /**
     * 收起
     */
    public static final byte HIDE           = 3;

    /**
     * 丢弃
     */
    public static final byte DICE           = 4;

    /**
     * 设置自动出售品质
     */
    public static final byte SET_SELL_TRAIT = 5;

    /**
     * 自动贩卖
     */
    public static final byte AUTO_SELL      = 6;

    /**
     * 自动贩卖后现身
     */
    public static final byte COMPLETE_SELL  = 7;
    
    /**
     * 已死亡宠物列表
     */
    public static final byte DIED_LIST	= 8;
    
    /**
     * 孵化宠物蛋
     */
//    public static final byte HATCH_EGG = 10;
    
    /**
     * 查看没有被装备的宠物的列表
     */
    public static final byte PET_CONTAINER = 10;
    
    /**
     * 宠物物品列表
     */
    public static final byte PET_GOODS_LIST = 11;

    /**
     * 查看玩家身上的宠物列表
     **/
    public static final byte PLAYER_BODY_PETS = 12;
    
    //删除原因:与宠物show功能的功能重叠.
//    /**
//     * 战斗宠物跟随
//     * ...come on baby.
//     */
//    public static final byte COME_ON	= 9;
    
    /**
     * 战斗宠物战斗待命
     */
    public static final byte ATTACK_AWAIT	= 9;
    /**
     * 坐骑待命状态
     */
    public static final byte MOUNT_AWAIT = 13;

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte operation = yis.readByte();
        int petID = yis.readInt();
        log.debug("operate type = " + operation);
        log.debug("operate pet id = " + petID);

//        if(petID >= 0)
        switch (operation)
        {
            case LIST:
            {
                ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),
                        new ResponsePetContainer(player.getInventory().getPetContainer()));

                break;
            }
            case SHOW:
            {
            	log.debug("要显示的宠物： id = " + petID);

                PetServiceImpl.getInstance().showPetx(player, petID);
                
                //同步上坐骑之后的移动速度变更通知.
                Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                if(pet.pk.getType() == Pet.PET_TYPE_HERBIVORE) {
                	//上坐骑
                    player.setMount(true);
    				//触发光环开启,关闭特效
    				Effect effect = EffectDictionary.getInstance().getEffectRef(pet.mountFunction);
    				EffectServiceImpl.getInstance().appendSkillEffect(player, player, effect);
                }


            	//add:	zhengl
            	//date:	2010-10-14
            	//note:	战斗宠物跟随状态
            	//跟随状态,移除宠物战斗AI
//            	PetServiceImpl.getInstance().stopAttackAI(player, petID);
            	//发送tip提示成功

                break;
            }
            case HIDE:
            {
            	if(petID != 0)
            	{
                    PetServiceImpl.getInstance().hidePet(player, petID);
                    
                    Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                    if(pet.pk.getType() == Pet.PET_TYPE_HERBIVORE) {
                    	//下坐骑
                        player.setMount(false);
        				//触发光环开启,关闭特效
        				Effect effect = EffectDictionary.getInstance().getEffectRef(pet.mountFunction);
        				EffectServiceImpl.getInstance().appendSkillEffect(player, player, effect);
                    }
            	}
            	else
            	{
            		//坐骑卡,下马.
            		EffectServiceImpl.getInstance().downMountEffect(player);
            	}

                break;
            }
            case DICE:
            {
                PetServiceImpl.getInstance().dicePet(player, petID);
                
                break;
            }
            case SET_SELL_TRAIT:
            {
                byte traitValue = yis.readByte();

                PlayerServiceImpl.getInstance().updateAutoSellTrait(player,
                        EGoodsTrait.getTrait(traitValue));

                break;
            }
            case AUTO_SELL:
            {
            	Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                MapSynchronousInfoBroadcast.getInstance().put(
                        player.where(),
                        new PetChangeNotify(player.getID(), OperatePet.HIDE,
                                pet.imageID,pet.pk.getType()), true, player.getID());

                int money = GoodsServiceImpl.getInstance().autoSellMAE(player);

                if (0 != money)
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning("宠物完成了贩卖"));
                }

                break;
            }
            case COMPLETE_SELL:
            {
                Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                MapSynchronousInfoBroadcast.getInstance().put(
                        player.where(),
                        new PetChangeNotify(player.getID(), OperatePet.SHOW,
                                pet.imageID,pet.pk.getType()), true,
                        player.getID());

                break;
            }
            case DIED_LIST:
            {
            	ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),
                        new ResponsePetList(PetServiceImpl.getInstance()
                                .getDiedPetList(player.getUserID()), (byte) player
                                .getAutoSellTrait().value()));

                break;
            }
//            case HATCH_EGG:{
            	/*Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
        		if(null != pet){
        			OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
                            new Warning("现在开始孵化宠物蛋 \""+pet.name+"\"！"));
        			PetServiceImpl.getInstance().hatchPet(player, pet);
        		}else{
        			OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
                            new Warning("获取宠物蛋失败，无法孵化！"));
        		}*/
//            }
            case ATTACK_AWAIT:
            {
            	//add:	zhengl
            	//date:	2010-10-14
            	//note:	战斗宠物战斗状态
            	//战斗待命状态,添加宠物战斗AI
            	/*short x = input.readShort();
            	short y = input.readShort();
            	short z = input.readShort();
            	PetServiceImpl.getInstance().startAttackAI(player, petID, x, y, z);*/
            	//发送tip提示成功
                break;
            }
            case PET_CONTAINER:
            {
            	log.debug("查看没有被装备的宠物的列表....");
            	ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),
                        new ResponsePetContainer(player.getInventory().getPetContainer()));
            	break;
            }
            case PET_GOODS_LIST:
            {
            	log.debug("查看宠物物品列表");
            	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            			new ResponseSpecialGoodsBag(player.getInventory().getPetGoodsBag(),null));
            	break;
            }
            case PLAYER_BODY_PETS:
            {
                log.debug("查看已经被装备的宠物的列表....");
            	ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),
                        new ResponsePetContainer(player.getBodyWearPetList()));
            	break;
            }
            case MOUNT_AWAIT:
            {
//            	log.info("进入战斗,主动下马");
//                player.setMount(false);
//				MapSynchronousInfoBroadcast.getInstance().put(
//						player.where(),
//						new MoveSpeedChangerNotify(player.getObjectType().value(), player.getID(),
//								player.getMoveSpeed()), false, 0);
            	break;
            }
        }
    }
}
