package hero.item.clienthandler;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.effect.service.EffectServiceImpl;
import hero.item.Armor;
import hero.item.EqGoods;
import hero.item.Equipment;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.Weapon.EWeaponType;
import hero.item.bag.EBagType;
import hero.item.bag.EquipmentContainer;
import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import hero.item.message.ClothesOrWeaponChangeNotify;
import hero.item.message.ResponseEquipmentBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.FlowLog;
import hero.log.service.LoctionLog;
import hero.log.service.LogServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.pet.message.ResponseWearPetGridNumber;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.RefreshObjectViewValue;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.ui.message.ResponseEuipmentPackageChange;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateEquipmentBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-5 下午02:58:56
 * @描述 ：操作装备背包
 */

public class OperateEquipmentBag extends AbsClientProcess
{
    /**
     * 查看列表
     */
    private static final byte LIST_BAG            = 1;

    /**
     * 卸下
     */
    private static final byte UNLOAD              = 2;

    /**
     * 丢弃已穿戴装备
     */
    private static final byte DICE_BODY_EQUIPMENT = 3;

    /**
     * 穿戴物品栏装备
     */
    private static final byte WEAR                = 4;

    /**
     * 整理物品栏装备
     */
    private static final byte SORT                = 5;

    /**
     * 丢弃物品栏装备
     */
    private static final byte DICE_BAG_EQUIPMENT  = 6;

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte operation = yis.readByte();

            switch (operation)
            {
                case LIST_BAG:
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseEquipmentBag(player.getInventory().getEquipmentBag(),player));
                    //在显示玩家装备列表界面，要再加一个玩家身上宠物列表的报文
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                    		new ResponseWearPetGridNumber(player.getBodyWearPetList()));
                    break;
                }
                case UNLOAD:
                {
                    try
                    {
                        byte gridIndex = yis.readByte();
                        int equipmentInsID = yis.readInt();

                        EquipmentInstance ei = player.getBodyWear().get(gridIndex);

                        if (null != ei && ei.getInstanceID() == equipmentInsID)
                        {
                            int bagGridIndex = player.getInventory()
                                    .getEquipmentBag().add(ei);

                            if (-1 != bagGridIndex)
                            {
                                ei = player.getBodyWear().remove(gridIndex);

                                // 卸载装备，玩家物品变更日志
                                GoodsDAO.changeEquipmentLocation(ei.getInstanceID(),
                                        EquipmentContainer.BAG, bagGridIndex);

                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ResponseEuipmentPackageChange(
                                                EBagType.EQUIPMENT_BAG
                                                        .getTypeValue(),
                                                bagGridIndex, ei));

                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                                        new ResponseEuipmentPackageChange(
                                                EBagType.BODY_WEAR.getTypeValue(),
                                                gridIndex, null));
                                //edit by zhengl ; date: 2010-11-24 ; note: 大量修改装备卸下,删除时展示信息
                                if (ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON
                                		|| ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM)
                                {
                                	EBodyPartOfEquipment equipmentType;
                                	EqGoods eqGood;
                                	short png = -1, anu = -1;
                                	if(ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON)
                                	{
                                		//如果是武器的话,同时改为无法进行远程攻击(因为武器没了)
                                		player.isRemotePhysicsAttack = false;
                                        equipmentType = ((Weapon) ei.getArchetype()).getWearBodyPart();
                                        eqGood = (Weapon) ei.getArchetype();
                                		png = ei.getGeneralEnhance().getFlashView()[0];
                                		anu = ei.getGeneralEnhance().getFlashView()[1];
                                	} else {
                                		equipmentType = ((Armor) ei.getArchetype()).getWearBodyPart();
                                		eqGood = (Armor) ei.getArchetype();
                                		png = ei.getGeneralEnhance().getArmorFlashView()[0];
                                		anu = ei.getGeneralEnhance().getArmorFlashView()[1];
									}
                                    AbsResponseMessage msg = new ClothesOrWeaponChangeNotify(
                                            player,
                                            equipmentType,
                                            ei.getArchetype().getImageID(),
                                            ei.getArchetype().getAnimationID(),
                                            ei.getGeneralEnhance().getLevel(), 
                                            eqGood, true, png, anu);

                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

                                    MapSynchronousInfoBroadcast.getInstance().put(
                                                    player.where(), msg,
                                                    true, player.getID());
                                }
                                //end
                                PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
                                PlayerServiceImpl.getInstance().refreshRoleProperty(player);
                                MapSynchronousInfoBroadcast.getInstance().put(
                                        player.where(),
                                        new RefreshObjectViewValue(player),
                                        true, player.getID());
                            }
                        }
                    }
                    catch (BagException pe)
                    {
                        throw PackageExceptionFactory.getInstance()
                                .getFullException(EGoodsType.EQUIPMENT);
                    }

                    break;
                }
                case DICE_BODY_EQUIPMENT:
                {
                    try
                    {
                        byte gridIndex = yis.readByte();
                        int equipmentInsID = yis.readInt();

                        EquipmentInstance ei = player.getBodyWear().get(
                                gridIndex);

                        if (null != ei && ei.getInstanceID() == equipmentInsID)
                        {
                            GoodsServiceImpl.getInstance().diceEquipmentOfBag(
                                    player, player.getBodyWear(), ei,
                                    LoctionLog.BAG, CauseLog.DEL);
                            short png = -1, anu = -1;
                            //edit by zhengl ; date: 2010-11-24 ; note: 大量修改装备卸下,删除时展示信息
                            if (ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON
                            		|| ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM)
                            {
                            	EBodyPartOfEquipment equipmentType;
                            	EqGoods eqGood;
                            	if(ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON) {
                            		//如果是武器的话,同时改为无法进行远程攻击(因为武器没了)
                            		player.isRemotePhysicsAttack = false;
                                    equipmentType = ((Weapon) ei.getArchetype()).getWearBodyPart();
                                    eqGood = (Weapon) ei.getArchetype();
                            		png = ei.getGeneralEnhance().getFlashView()[0];
                            		anu = ei.getGeneralEnhance().getFlashView()[1]; 
                            	} else {
                            		equipmentType = ((Armor) ei.getArchetype()).getWearBodyPart();
                            		eqGood = (Armor) ei.getArchetype();
                            		png = ei.getGeneralEnhance().getArmorFlashView()[0];
                            		anu = ei.getGeneralEnhance().getArmorFlashView()[1];
								}
                                AbsResponseMessage msg = new ClothesOrWeaponChangeNotify(
                                        player,
                                        equipmentType,
                                        ei.getArchetype().getImageID(),
                                        ei.getArchetype().getAnimationID(),
                                        ei.getGeneralEnhance().getLevel(), 
                                        eqGood, true, png, anu);

                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

                                MapSynchronousInfoBroadcast.getInstance().put(
                                                player.where(), msg,
                                                true, player.getID());
                            }
                            //end

                            PlayerServiceImpl.getInstance()
                                    .reCalculateRoleProperty(player);
                            PlayerServiceImpl.getInstance()
                                    .refreshRoleProperty(player);
                            MapSynchronousInfoBroadcast.getInstance().put(
                                    player.where(),
                                    new RefreshObjectViewValue(player), true,
                                    player.getID());
                        }
                    }
                    catch (Exception pe)
                    {
                        pe.printStackTrace();
                    }

                    break;
                }
                case WEAR:
                {
                    byte gridIndex = yis.readByte();
                    int equipmentInsID = yis.readInt();

                    EquipmentInstance ei = player.getInventory()
                            .getEquipmentBag().get(gridIndex);

                    if (null != ei && ei.getInstanceID() == equipmentInsID)
                    {
                        if (player.getLevel() >= ei.getArchetype().getNeedLevel()
                                && ei.getArchetype().canBeUse( player.getVocation() )
                                && !ei.existSeal())
                        {
                            player.getInventory().getEquipmentBag().remove(gridIndex);
                            EquipmentInstance uei = player.getBodyWear().wear(ei);

                            if (!ei.isBind()
                                    && ei.getArchetype().getBindType() == Equipment.BIND_TYPE_OF_WEAR)
                            {
                                ei.bind();
                                GoodsDAO.bindEquipment(ei);
                            }

                            if (null != uei)
                            {
                                player.getInventory().getEquipmentBag().add(gridIndex, uei);

                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ResponseEuipmentPackageChange(
                                                EBagType.EQUIPMENT_BAG.getTypeValue(),
                                                gridIndex, uei));

                                GoodsDAO.changeEquipmentLocation(uei.getInstanceID(),
                                        EquipmentContainer.BAG, gridIndex);

                                LogServiceImpl.getInstance().goodsChangeLog(
                                        player, uei.getArchetype(), 1,
                                        LoctionLog.BODY, FlowLog.LOSE,
                                        CauseLog.UNLOAD);
                                LogServiceImpl.getInstance().goodsChangeLog(
                                        player, uei.getArchetype(), 1,
                                        LoctionLog.BAG, FlowLog.GET,
                                        CauseLog.UNLOAD);
                            }
                            else
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ResponseEuipmentPackageChange(
                                                EBagType.EQUIPMENT_BAG.getTypeValue(),
                                                gridIndex, null));
                            }

                            LogServiceImpl.getInstance().goodsChangeLog(player,
                                    ei.getArchetype(), 1, LoctionLog.BODY,
                                    FlowLog.GET, CauseLog.WEAR);
                            LogServiceImpl.getInstance().goodsChangeLog(player,
                                    ei.getArchetype(), 1, LoctionLog.BAG,
                                    FlowLog.LOSE, CauseLog.WEAR);

                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new ResponseEuipmentPackageChange(
                                            EBagType.BODY_WEAR.getTypeValue(),
                                            player.getBodyWear().indexOf(ei),
                                            ei));
                            
                            //edit by zhengl ; date: 2010-11-24 ; note: 大量修改装备卸下,删除时展示信息
                            if (ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON
                            		|| ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM)
                            {
                            	//edit by zhengl; date: 2011-03-30; note: 替换装备外观效果下发逻辑修正
                            	//当替换下的装备为空.
                            	//或者替换下的装备和将要装备的装备有区别的时候才下发外观替换报文
                                if (GoodsServiceImpl.getInstance().changeEquimentViewDifference(uei, ei))
                                {
                                	EBodyPartOfEquipment equipmentType;
                                	EqGoods eqGood;
                                	short png = -1, anu = -1;
                                	if(ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON) {
                                        player.isRemotePhysicsAttack = 
                                        	((Weapon) ei.getArchetype()).getWeaponType() 
                                        		== EWeaponType.TYPE_GONG ? true : false;
                                        equipmentType = ((Weapon) ei.getArchetype()).getWearBodyPart();
                                        eqGood = (Weapon) ei.getArchetype();
                                		png = ei.getGeneralEnhance().getFlashView()[0];
                                		anu = ei.getGeneralEnhance().getFlashView()[1]; 
                                	} else {
                                		equipmentType = ((Armor) ei.getArchetype()).getWearBodyPart();
                                		eqGood = (Armor) ei.getArchetype();
                                		png = ei.getGeneralEnhance().getArmorFlashView()[0];
                                		anu = ei.getGeneralEnhance().getArmorFlashView()[1]; 
									}
                                    AbsResponseMessage msg = new ClothesOrWeaponChangeNotify(
                                            player,
                                            equipmentType,
                                            ei.getArchetype().getImageID(),
                                            ei.getArchetype().getAnimationID(),
                                            ei.getGeneralEnhance().getLevel(), 
                                            eqGood, false, png, anu);
                                    
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                                    MapSynchronousInfoBroadcast.getInstance().put(
                                                    player.where(), 
                                                    msg,
                                                    true, player.getID());
                                }
                            }
                            //end

                            GoodsDAO.changeEquipmentLocation(
                                    ei.getInstanceID(),
                                    EquipmentContainer.BODY, (short) -1);

                            PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
                            PlayerServiceImpl.getInstance().refreshRoleProperty(player);
                            MapSynchronousInfoBroadcast.getInstance().put(
                                    player.where(),
                                    new RefreshObjectViewValue(player), true, player.getID());
                        }
                        else {
                            ResponseMessageQueue.getInstance().put(
                            		player.getMsgQueueIndex(), new Warning(Tip.TIP_ITEM_NOT_UES, 
                            				Warning.UI_STRING_TIP));
						}
                    }

                    break;
                }
                case SORT:
                {
                	if (true) 
                	{
                		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                				new Warning("该功能暂不开放"));
                		break;
					}
                    if (player.getInventory().getEquipmentBag().clearUp())
                    {
                        GoodsDAO.clearUpEquipmentList(player.getInventory()
                                .getEquipmentBag().getEquipmentList());

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseEquipmentBag(player.getInventory()
                                        .getEquipmentBag(),player));
                    }

                    break;
                }
                case DICE_BAG_EQUIPMENT:
                {
                    byte gridIndex = yis.readByte();
                    int equipmentInsID = yis.readInt();

                    EquipmentInstance ei = player.getInventory()
                            .getEquipmentBag().get(gridIndex);

                    if (null != ei && ei.getInstanceID() == equipmentInsID)
                    {
                        GoodsServiceImpl.getInstance().diceEquipmentOfBag(
                                player,
                                player.getInventory().getEquipmentBag(), ei,
                                LoctionLog.BAG, CauseLog.DEL);

                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
