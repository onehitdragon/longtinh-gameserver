package hero.item.clienthandler;

import hero.item.SingleGoods;
import hero.item.bag.exception.BagException;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.BigTonicBall;
import hero.item.special.ESpecialGoodsType;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.item.SpecialGoods;
import hero.item.dictionary.GoodsContents;
import hero.log.service.CauseLog;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateSpecialGoodsBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-9 下午01:51:32
 * @描述 ：操作特殊物品背包
 */

public class OperateSpecialGoodsBag extends AbsClientProcess
{
	
	private static Logger log = Logger.getLogger(OperateSpecialGoodsBag.class);
    /**
     * 查看列表
     */
    private static final byte LIST             = 1;

    /**
     * 使用
     */
    private static final byte USE              = 2;

    /**
     * 设置快捷键
     */
    private static final byte SET_SHORTCUT_KEY = 3;

    /**
     * 丢弃
     */
    private static final byte DICE             = 4;

    /**
     * 整理
     */
    private static final byte SORT             = 5;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
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
                case LIST:
                {
                    log.debug("operation special goods bag size="+player.getInventory().getSpecialGoodsBag().getFullGridNumber());
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseSpecialGoodsBag(player.getInventory()
                                    .getSpecialGoodsBag(), player
                                    .getShortcutKeyList()));

                    break;
                }
                case USE:
                {
                    byte gridIndex = yis.readByte();
                    int goodsID = yis.readInt();
                    
                    log.debug("use special goods index = " + gridIndex + ", goodsid="+goodsID);

                    boolean hadGoods = player.getInventory().getSpecialGoodsBag().getAllItem()[gridIndex][0] == goodsID
                                            || player.getInventory().getPetGoodsBag().getAllItem()[gridIndex][0] == goodsID;
                    if (hadGoods)
                    {
                        SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(goodsID);
                        if (specialGoods.getNeedLevel() > player.getLevel()) 
                        {
                    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_LEVEL_NOT_ENOUGH, Warning.UI_STRING_TIP));
                    		break;
						}
                        
                        log.debug("special goods = " + specialGoods.getType());
                        if(specialGoods.getType() == ESpecialGoodsType.PET_FEED){
                        	int petId = yis.readInt();
                        	Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
                        	if(null != pet){
                            	if (specialGoods.beUse(player, pet, goodsID))
                                {
                                    if (specialGoods.disappearImmediatelyAfterUse())
                                    {
                                        specialGoods.remove(player, gridIndex);
                                    }
                                }
                        	}else{
                        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                        new Warning("没有找到这个宠物！", Warning.UI_STRING_TIP));
                        	}
                        }else if(specialGoods.getType() == ESpecialGoodsType.PET_REVIVE){
                        	int petId = yis.readInt();
                        	if (specialGoods.beUse(player, petId, gridIndex))
                            {
                                if (specialGoods.disappearImmediatelyAfterUse())
                                {
                                    specialGoods.remove(player, gridIndex);
                                }
                            }
                        }else if(specialGoods.getType() == ESpecialGoodsType.PET_DICARD){
                        	int petId = yis.readInt();
                        	byte code = yis.readByte();
                        	Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
                        	if(null != pet){
                            	pet.dicard_code = code;
                            	if (specialGoods.beUse(player, pet, gridIndex))
                                {
                                    if (specialGoods.disappearImmediatelyAfterUse())
                                    {
                                        specialGoods.remove(player, gridIndex);
                                    }
                                }
                            }else{
                        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                        new Warning("没有找到这个宠物！", Warning.UI_STRING_TIP));
                        	}
                        }else if(specialGoods.getType() == ESpecialGoodsType.PET_SKILL_BOOK){
                        	int petId = yis.readInt();
                        	log.debug("pet use skillbook , petid=" + petId);
                        	Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
                        	if(null != pet){
                        		log.debug("start skill from skillbook , pet.level="+pet.level);
                            	if (specialGoods.beUse(player, pet, gridIndex))
                                {
                                    if (specialGoods.disappearImmediatelyAfterUse())
                                    {
                                        specialGoods.remove(player, gridIndex);
                                    }
                                }
                        	}else{
                        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                        new Warning("没有找到这个宠物！", Warning.UI_STRING_TIP));
                        	}
                        } 
                        else if (specialGoods.getType() == ESpecialGoodsType.BIG_TONIC) 
                        {
                        	//add by zhengl; date: 2011-03-15; note: 吃丸子.
                        	if(player.getInventory().getSpecialGoodsBag().tonicList.size() > 0) 
                        	{
                        		if(((BigTonicBall)specialGoods).isActivate == BigTonicBall.TONINC_UNAUTO)
                        		{
                        			player.getInventory().getSpecialGoodsBag().eatTonicBall(
                        					gridIndex, specialGoods.getID(), player);
                        		}
                        		else 
                        		{
                        			player.getInventory().getSpecialGoodsBag().installTonicBall(
                        					gridIndex, player);
								}
                        	} 
                        	else 
                        	{
                        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                        new Warning("没有找到这个物品！", Warning.UI_STRING_TIP));
							}
						}
                        else if (specialGoods.getType() == ESpecialGoodsType.PET_PER) 
                        {
                        	//add by zhengl; date: 2011-03-20; note: 用宠物卡.
                        	if(player.getInventory().getSpecialGoodsBag().petPerCardList.size() > 0) 
                        	{
                    			player.getInventory().getSpecialGoodsBag().usePetPerCard(
                    					gridIndex, specialGoods.getID(), player);
                        	} 
                        	else 
                        	{
                        		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                        new Warning("没有找到这个物品！", Warning.UI_STRING_TIP));
							}
						}
                        else
                        {
                            if (specialGoods.beUse(player, null, gridIndex))
                            {
                                if (specialGoods.disappearImmediatelyAfterUse())
                                {
                                    specialGoods.remove(player, gridIndex);
                                }
                            }
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning("背包中无此物品", Warning.UI_STRING_TIP));
                    }

                    break;
                }
                case SET_SHORTCUT_KEY:
                {
                    byte shortcutKey = yis.readByte();
                    int goodsID = yis.readInt();

                    PlayerServiceImpl.getInstance().setShortcutKey(player,
                            shortcutKey,
                            PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS,
                            goodsID);

                    break;
                }
                case DICE:
                {
                    byte gridIndex = yis.readByte();
                    int goodsID = yis.readInt();

                    try
                    {
                        GoodsServiceImpl.getInstance().diceSingleGoods(
                                player,
                                player.getInventory().getSpecialGoodsBag(),
                                gridIndex, goodsID,CauseLog.DEL);
                    }
                    catch (BagException pe)
                    {
                        log.error("特殊物品丢弃 error:",pe);
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
                    log.debug("special goods bag clearup size="+player.getInventory().getSpecialGoodsBag().getFullGridNumber());
                    if (player.getInventory().getSpecialGoodsBag().clearUp())
                    {
                        GoodsDAO.clearUpSingleGoodsPackage(player.getUserID(),
                                player.getInventory().getSpecialGoodsBag(),
                                SingleGoods.TYPE_SPECIAL_GOODS);
                        log.debug("after clearup special goods bag size="+player.getInventory().getSpecialGoodsBag().getFullGridNumber());
                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseSpecialGoodsBag(player
                                        .getInventory().getSpecialGoodsBag(),
                                        player.getShortcutKeyList()));
                    }

                    break;
                }
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
