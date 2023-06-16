package hero.item.clienthandler;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.item.Medicament;
import hero.item.SingleGoods;
import hero.item.bag.EBagType;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.MedicamentDict;
import hero.item.message.ResponseMedicamentBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.player.HeroPlayer;
import hero.player.message.HotKeySumByMedicament;
import hero.player.service.PlayerServiceImpl;
import hero.share.cd.CDTimer;
import hero.share.cd.CDTimerTask;
import hero.share.cd.CDUnit;
import hero.share.message.Warning;
import hero.ui.message.ResponseSinglePackageChange;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateMedicamentBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-15 上午09:33:23
 * @描述 ：操作药品背包
 */

public class OperateMedicamentBag extends AbsClientProcess
{
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
                case LIST:
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseMedicamentBag(player.getInventory()
                                    .getMedicamentBag(), player
                                    .getShortcutKeyList()));

                    break;
                }
                case USE:
                {
                    byte gridIndex = yis.readByte();
                    int goodsID = yis.readInt();
                    short[] gridChange = null;

                    Medicament medicament = MedicamentDict.getInstance()
                            .getMedicament(goodsID);

                    if (player.getLevel() >= medicament.getNeedLevel())
                    {
                        if (player.isInFighting()
                                && !medicament.canUseInFight())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning("该药品在战斗中不能使用", Warning.UI_STRING_TIP));

                            return;
                        }
                        else
                        {
                            CDUnit cd = null;

                            if (medicament.getMaxCdTime() > 0)
                            {
                                cd = player.userCDMap.get(medicament
                                        .getPublicCdVariable());

                                if (cd != null && cd.isRunTD())
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning("还有"
                                                    + cd.getTimeBySec()
                                                    + "秒时间冷却", Warning.UI_STRING_TIP));

                                    return;
                                }
                            }

                            if (medicament.beUse(player, null))
                            {
                                try
                                {
                                    gridChange = player.getInventory()
                                            .getMedicamentBag().remove(
                                                    gridIndex,
                                                    medicament.getID(), 1);

                                    if (null != gridChange)
                                    {
                                        if (0 == gridChange[1])
                                        {
                                            GoodsDAO.removeSingleGoodsFromBag(
                                                    player.getUserID(),
                                                    (short) gridChange[0],
                                                    goodsID);
                                        }
                                        else
                                        {
                                            GoodsDAO
                                                    .updateGridSingleGoodsNumberOfBag(
                                                            player.getUserID(),
                                                            goodsID,
                                                            gridChange[1],
                                                            (short) gridChange[0]);
                                        }

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

                                        ResponseMessageQueue
                                                .getInstance()
                                                .put(
                                                        player
                                                                .getMsgQueueIndex(),
                                                        new ResponseSinglePackageChange(
                                                                EBagType.MEDICAMENT_BAG
                                                                        .getTypeValue(),
                                                                new short[]{gridIndex, (short) gridChange[1] }));

                                        return;
                                    }
                                }
                                catch (BagException pe)
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(pe.getMessage(), Warning.UI_STRING_TIP));
                                }
                            }
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning("你的等级不够", Warning.UI_STRING_TIP));
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
                        GoodsServiceImpl.getInstance().diceSingleGoods(player,
                                player.getInventory().getMedicamentBag(),
                                gridIndex, goodsID, CauseLog.DEL);
//                        //add by zhengl; date: 2011-03-07; note: 丢弃药水的时候变化引起的快捷键变化
//                    	HotKeySumByMedicament keyMsg = new HotKeySumByMedicament(player);
//                    	if(keyMsg.haveRelation(goodsID)) {
//                    		OutMsgQ.getInstance().put(player.getMsgQueueIndex(), keyMsg);
//                    	}
//                    	//end
                    }
                    catch (BagException pe)
                    {
                        System.out.print(pe.getMessage());
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
                    if (player.getInventory().getMedicamentBag().clearUp())
                    {
                        GoodsDAO.clearUpSingleGoodsPackage(player.getUserID(),
                                player.getInventory().getMedicamentBag(),
                                SingleGoods.TYPE_MEDICAMENT);

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMedicamentBag(player.getInventory()
                                        .getMedicamentBag(), player
                                        .getShortcutKeyList()));
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
