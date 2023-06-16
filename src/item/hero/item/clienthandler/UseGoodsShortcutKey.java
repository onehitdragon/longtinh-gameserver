package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.Goods;
import hero.item.Medicament;
import hero.item.SpecialGoods;
import hero.item.TaskTool;
import hero.item.bag.EBagType;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.message.NodifyMedicamentCDTime;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.BigTonicBall;
import hero.item.special.ESpecialGoodsType;
import hero.log.service.CauseLog;
import hero.player.HeroPlayer;
import hero.player.message.HotKeySumByMedicament;
import hero.player.service.PlayerServiceImpl;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.cd.CDTimer;
import hero.share.cd.CDTimerTask;
import hero.share.cd.CDUnit;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.task.service.TaskServiceImpl;
import hero.ui.message.ResponseSinglePackageChange;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UseGoodsShortcutKey.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-10 上午09:20:31
 * @描述 ：使用物品快捷键
 */

public class UseGoodsShortcutKey extends AbsClientProcess
{
    /*
     * (non-Javadoc)
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            int goodsID = yis.readInt();
            byte targetType = yis.readByte();
            int targetID = yis.readInt();

            int goodsNumber = player.getInventory().getMedicamentBag()
                    .getGoodsNumber(goodsID);

            if (0 == goodsNumber)
            {
                goodsNumber = player.getInventory().getTaskToolBag()
                        .getGoodsNumber(goodsID);

                if (0 == goodsNumber)
                {
                    goodsNumber = player.getInventory().getSpecialGoodsBag()
                            .getGoodsNumber(goodsID);
                }
            }

            if (0 != goodsNumber)
            {
                Goods goods = GoodsContents.getGoods(goodsID);

                ME2GameObject target = null;

                if (goods.getGoodsType() != EGoodsType.MEDICAMENT
                        && 0 < targetID)
                {
                    if (EObjectType.MONSTER.value() == targetType)
                    {
                        target = player.where().getMonster(targetID);
                    }
                    else if (EObjectType.PLAYER.value() == targetType)
                    {
                        target = player.where().getPlayer(targetID);
                    }
                    else
                    {
                        target = player.where().getNpc(targetID);
                    }
                }

                switch (goods.getGoodsType())
                {
                    case MEDICAMENT:
                    {
                        Medicament medicament = (Medicament) goods;

                        if (player.getLevel() >= medicament.getNeedLevel())
                        {
                            if (player.isInFighting() && !medicament.canUseInFight())
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("战斗中无法使用", Warning.UI_STRING_TIP));

                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new NodifyMedicamentCDTime(goodsID));

                                return;
                            }
                            else
                            {
                                CDUnit cd = null;

                                if (medicament.getMaxCdTime() > 0)
                                {
                                    cd = player.userCDMap.get(medicament.getPublicCdVariable());

                                    if (cd != null && cd.isRunTD())
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                new Warning("还有"
                                                        + cd.getTimeBySec()
                                                        + "秒时间冷却", Warning.UI_STRING_TIP));

                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                new NodifyMedicamentCDTime(
                                                        goodsID));

                                        return;
                                    }
                                }

                                if (medicament.beUse(player, null))
                                {
                                    if (GoodsServiceImpl
                                            .getInstance()
                                            .deleteSingleGoods(
                                                    player,
                                                    player.getInventory()
                                                            .getMedicamentBag(),
                                                    medicament, 1,
                                                    CauseLog.SHORTCUTKEY))
                                    {
                                        if (cd == null)
                                        {
                                            cd = new CDUnit(medicament
                                                    .getPublicCdVariable(),
                                                    medicament.getMaxCdTime(),
                                                    medicament.getMaxCdTime());

                                            player.userCDMap.put(cd.getKey(),
                                                    cd);
                                        }
                                        CDTimer.getInsctance().addTask(
                                                new CDTimerTask(cd));
                                        return;
                                    }
                                }
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning("你的等级不够", Warning.UI_STRING_TIP));
                        }

                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new NodifyMedicamentCDTime(goodsID));

                        break;
                    }
                    case SPECIAL_GOODS:
                    {
                    	SpecialGoods specialGoods = (SpecialGoods) goods;
                        if (specialGoods.getNeedLevel() > player.getLevel()) 
                        {
                    		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_LEVEL_NOT_ENOUGH, Warning.UI_STRING_TIP));
                    		break;
						}
                    	 if ( specialGoods.getType() == ESpecialGoodsType.BIG_TONIC) 
                    	 {
                         	//add by zhengl; date: 2011-03-15; note: 吃丸子.
                         	if(player.getInventory().getSpecialGoodsBag().tonicList.size() > 0) 
                         	{
                        		if(((BigTonicBall)specialGoods).isActivate == BigTonicBall.TONINC_UNAUTO)
                        		{
                        			player.getInventory().getSpecialGoodsBag().eatTonicBall(
                        					-1, specialGoods.getID(), player);
                        		}
                        		else 
                        		{
                        			player.getInventory().getSpecialGoodsBag().installTonicBall(
                        					-1, player);
								}
                         	}
                         	else 
                         	{
                         		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                         new Warning("没有找到这个物品！", Warning.UI_STRING_TIP));
 							}
 						} else if (specialGoods.getType() == ESpecialGoodsType.PET_PER) {
                         	//add by zhengl; date: 2011-03-20; note: 用宠物卡.
                         	if(player.getInventory().getSpecialGoodsBag().petPerCardList.size() > 0) 
                         	{
                    			player.getInventory().getSpecialGoodsBag().usePetPerCard(-1, 
                    					specialGoods.getID(), player);
                         	}
                         	else 
                         	{
                         		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                         new Warning("没有找到这个物品！", Warning.UI_STRING_TIP));
 							}
						} else {
 							if (specialGoods.beUse(player, target, -1))
 							{
 								if (specialGoods.disappearImmediatelyAfterUse())
 								{
 									specialGoods.remove(player, (short) -1);
 								}
 							}
						}


                        break;
                    }
                    case TASK_TOOL:
                    {
                        TaskTool taskTool = (TaskTool) goods;

                        taskTool.beUse(player, target);

                        break;
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning("背包中无此物品", Warning.UI_STRING_TIP));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new NodifyMedicamentCDTime(goodsID));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
