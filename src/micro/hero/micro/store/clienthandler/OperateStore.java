package hero.micro.store.clienthandler;

import hero.item.detail.EGoodsType;
import hero.map.service.MapServiceImpl;
import hero.micro.service.MicroServiceImpl;
import hero.micro.store.PersionalStore;
import hero.micro.store.StoreService;
import hero.micro.store.message.OtherStoreGoodsList;
import hero.micro.store.message.ResponseBagGoodsList;
import hero.micro.store.message.ResponseStoreGoodsList;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.ui.data.ActiveGoodsBagData;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateStore.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-19 下午05:18:55
 * @描述 ：
 */

public class OperateStore extends AbsClientProcess
{
    private static Logger  log = Logger.getLogger(OperateStore.class);
    /**
     * 查看列表
     */
    public static final byte LIST         = 1;

    /**
     * 打开背包
     */
    public static final byte OPEN_BAG     = 2;

    /**
     * 撤销
     */
    public static final byte REMOVE       = 3;

    /**
     * 改变价格
     */
    public static final byte CHANGE_PRICE = 4;

    /**
     * 开业
     */
    public static final byte START        = 5;

    /**
     * 关闭
     */
    public static final byte CLOSE        = 6;

    /**
     * 查看其他人的商店
     */
    public static final byte VIEW_OTHER   = 7;

    /**
     * 购买
     */
    public static final byte BUY          = 8;
    
    /**
     * 其它人退出商店
     */
    public static final byte OTHER_EXIT	= 9;

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte operation = yis.readByte();
        log.debug("@@@@@2 operate store operation type="+operation);

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_FUN_NOT_OPEN));
        return;

        /*boolean canStore = MapServiceImpl.getInstance().canStroe(player.where().getID());
        if(!canStore){
        	log.debug(player.where().getName() + " 不能摆摊...");
        	OutMsgQ.getInstance().put(player.getMsgQueueIndex(), new Warning(Tip.TIP_MAP_NOT_STORE));
        	return;
        }

        if(player.isSwaping()){
            log.debug("交易状态中不能摆摊。。。");
            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_EXCHANGEING_NOT_STORE));
            return;
        }

        switch (operation)
        {
            case LIST:
            {
                //现在因为直接把背包打开了，所以直接发送 type=2，把列表放到 type=2 里
                OutMsgQ.getInstance().put(
                        player.getMsgQueueIndex(),
                        new ResponseStoreGoodsList(MicroServiceImpl
                                .getInstance().getStore(player.getUserID())));

                break;
            }
            case OPEN_BAG:
            {
                byte goodsTypeValue = input.readByte();
                log.debug("OPEN_BAG goodstype="+goodsTypeValue);
                *//*OutMsgQ.getInstance().put(
                        player.getMsgQueueIndex(),
                        new ResponseStoreGoodsList(MicroServiceImpl
                                .getInstance().getStore(player.getUserID())));*//*

                EGoodsType goodsType = EGoodsType.getGoodsType(goodsTypeValue);

                switch (goodsType)
                {
                    case EQUIPMENT:
                    {
                        OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
                                new ResponseBagGoodsList(ActiveGoodsBagData.getData(
                                		player.getInventory().getEquipmentBag())));

                        break;
                    }
                    case MATERIAL:
                    {
                        OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
                                new ResponseBagGoodsList(ActiveGoodsBagData.getData(
                                		player.getInventory().getMaterialBag(), goodsType)));

                        break;
                    }
                    case MEDICAMENT:
                    {
                        OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
                                        new ResponseBagGoodsList(ActiveGoodsBagData.getData(
                                        		player.getInventory().getMedicamentBag(), goodsType)));

                        break;
                    }
                    case SPECIAL_GOODS:
                    {
                        OutMsgQ.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseBagGoodsList(ActiveGoodsBagData
                                        .getData(player.getInventory()
                                                .getSpecialGoodsBag(),
                                                goodsType)));

                        break;
                    }
                }

                break;
            }
            case REMOVE:
            {
                byte gridIndex = input.readByte();

                StoreService.removeGoods(player, gridIndex);

                break;
            }
            case CHANGE_PRICE:
            {
                byte gridIndex = input.readByte();
                int newPrice = input.readInt();

                StoreService.modifyPrice(player, gridIndex, newPrice);

                break;
            }
            case START:
            {
                String storeName = input.readUTF();
                byte goodsNumber = input.readByte();
                int[][] newGoodsDataList = null;

                if (0 < goodsNumber)
                {
                    newGoodsDataList = new int[goodsNumber][6];

                    for (int i = 0; i < goodsNumber; i++)
                    {
                        newGoodsDataList[i][0] = input.readByte();
                        newGoodsDataList[i][1] = input.readByte();
                        newGoodsDataList[i][2] = input.readInt();
                        newGoodsDataList[i][3] = input.readByte();
                        newGoodsDataList[i][4] = input.readByte();
                        newGoodsDataList[i][5] = input.readInt();

                        if (newGoodsDataList[i][5] <= 0
                                || newGoodsDataList[i][5] >= Constant.INTEGER_MAX_VALUE)
                        {
                            OutMsgQ.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_MICRO_OF_PRICE_ERROR));

                            return;
                        }
                    }
                }

                StoreService.openStore(player, storeName, newGoodsDataList);

                break;
            }
            case CLOSE:
            {
log.info("CLOSE SOTRE ...");
                StoreService.closeStore(player);

                break;
            }
            case VIEW_OTHER:
            {
                int storeOwnerID = input.readInt();

                HeroPlayer other = player.where().getPlayer(storeOwnerID);

                if (null != other)
                {
                    if(other.isSelling()){
                        PersionalStore store = MicroServiceImpl.getInstance()
                                .getStore(other.getUserID());

                        if (null != store && store.goodsNumber > 0)
                        {
                            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
                                    new OtherStoreGoodsList(store));
                            store.addEntrePlayer(player);
                        }else {
                            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("商店已关闭"));
                        }
                    }else {
                        OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("商店已关闭"));
                    }
                }

                break;
            }
            case BUY:
            {
                int storeMasterID = input.readInt();
                byte gridIndex = input.readByte();
                int goodsID = input.readInt();

                HeroPlayer storeMaster = player.where()
                        .getPlayer(storeMasterID);

                if (null != storeMaster)
                {
                    StoreService.buy(player, storeMaster, gridIndex, goodsID);
                }

                break;
            }
            case OTHER_EXIT:
            {
            	log.debug("OPERATE STORE OTHER EXIT ...");
            	int storeOwnerID = input.readInt();

                HeroPlayer other = player.where().getPlayer(storeOwnerID);
                if (null != other)
                {
                    PersionalStore store = MicroServiceImpl.getInstance()
                            .getStore(other.getUserID());
                    
                    if (null != store)
                    {
//                    	log.debug("exit before other size = " + store.getEnterPlayerList().size());
                        store.removeEnterPlayer(player);
//                        log.debug("exit before other size = " + store.getEnterPlayerList().size());
                    }
                }

                break;
            }
        }*/
    }


}
