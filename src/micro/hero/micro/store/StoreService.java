package hero.micro.store;

import hero.micro.store.message.ResponseStoreGoodsList;
import hero.item.EquipmentInstance;
import hero.item.bag.Inventory;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.micro.service.MicroServiceImpl;
import hero.micro.store.PersionalStore.GoodsForSale;
import hero.micro.store.message.GridGoodsChangesNotify;
import hero.micro.store.message.OtherStoreGoodsList;
import hero.micro.store.message.StoreStatusChanged;
import hero.micro.teach.TeachService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 StoreService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-8 上午11:10:33
 * @描述 ：个人商店服务类，提供静态方法
 */

public class StoreService
{
    private static Logger log = Logger.getLogger(StoreService.class);
    /**
     * 在线玩家的商店数据列表（userID:商店数据）
     */
    private static FastMap<Integer, PersionalStore> storeTable = new FastMap<Integer, PersionalStore>();

    /**
     * 私有构造
     */
    private StoreService()
    {

    }

    /**
     * 登陆
     * 
     * @param _player
     */
    public static void login (HeroPlayer player)
    {
        /*PersionalStore store = StoreDAO.loadStore(player.getUserID());

        if (null != store)
        {
            storeTable.put(player.getUserID(), store);
            log.debug("玩家摆摊数据...");
            storeTable.put(player.getUserID(),store);
            store.setName(player.getName()+"的商店");
            store.opened = true;
            player.setSellStatus(true);
            StoreStatusChanged notify = new StoreStatusChanged(player.getID(),
                    true, store.name);

            OutMsgQ.getInstance().put(player.getMsgQueueIndex(), notify);

            MapSynchronousInfoBroadcast.getInstance().put(player.where(), notify,
                    true, player.getID());
            log.debug("玩家上线加载摆摊信息.end ....");
        }*/
        PersionalStore store = StoreService.get(player.getUserID());
        if(null != store && (store.opened || player.isSelling())){
            log.debug("进入游戏，摆摊状态 = " + store.opened +", player storestatus = " + player.isSelling());

            StoreService.clear(player.getUserID());   //新进入游戏时，清除玩家之前的开店状态
        }
        StoreService.takeOffAll(player);
    }

    /**
     * @param _userID
     */
    public static void clear(int _userID)
    {
        storeTable.remove(_userID);
    }

    /**
     * 获取商店
     * 
     * @param _userID
     * @return
     */
    public static PersionalStore get (int _userID)
    {
        return storeTable.get(_userID);
    }

    /**
     * 商店开业
     * 
     * @param _player
     * @param _storeName
     * @param _newGoodsDataList
     */
    public synchronized static void openStore (HeroPlayer _player,
            String _storeName, int[][] _newGoodsDataList)
    {
        PersionalStore store = storeTable.get(_player.getUserID());

        if (null != store)
        {
            if (store.opened) { return; }
        }
        else
        {
            if (null == _newGoodsDataList)
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_NONE_GOODS));

                return;
            }

            store = new PersionalStore();
            storeTable.put(_player.getUserID(), store);
        }

        store.setName(_storeName);

        if (null != _newGoodsDataList)
        {
            byte goodsType, bagGrid, number, storeBagGrid;
            int goodsID, salePrice;

            try
            {
                Inventory inventory = _player.getInventory();
                EquipmentInstance equipment;

                for (int[] goodsForSaleData : _newGoodsDataList)
                {
                    goodsType = (byte) goodsForSaleData[0];
                    bagGrid = (byte) goodsForSaleData[1];
                    goodsID = goodsForSaleData[2];
                    number = (byte) goodsForSaleData[3];
                    storeBagGrid = (byte) goodsForSaleData[4];
                    salePrice = goodsForSaleData[5];

                    log.debug("open store add goods type="+goodsType+",bagGrid="+bagGrid+",goodsID="+goodsID+",number="+number
                            +",storeBagGrid="+storeBagGrid+",salePrice="+salePrice);

                    switch (EGoodsType.getGoodsType((byte) goodsForSaleData[0]))
                    {
                        case EQUIPMENT:
                        {
                            equipment = GoodsServiceImpl.getInstance()
                                    .removeEquipmentOfBag(_player,
                                            inventory.getEquipmentBag(),
                                            bagGrid,CauseLog.STORE);

                            if (null != equipment
                                    && equipment.getInstanceID() == goodsID)
                            {
                                store.add(goodsType, storeBagGrid, 0, number,
                                        equipment, salePrice);
                            }

                            break;
                        }
                        case MATERIAL:
                        {
                            if (GoodsServiceImpl.getInstance()
                                    .reduceSingleGoods(_player,
                                            inventory.getMaterialBag(),
                                            bagGrid, goodsID, number,
                                            CauseLog.STORE))
                            {
                                store.add(goodsType, storeBagGrid, goodsID,
                                        number, null, salePrice);
                            }

                            break;
                        }
                        case MEDICAMENT:
                        {
                            if (GoodsServiceImpl.getInstance()
                                    .reduceSingleGoods(_player,
                                            inventory.getMedicamentBag(),
                                            bagGrid, goodsID, number,
                                            CauseLog.STORE))
                            {
                                store.add(goodsType, storeBagGrid, goodsID,
                                        number, null, salePrice);
                            }

                            break;
                        }
                        case SPECIAL_GOODS:
                        {
                            if (GoodsServiceImpl.getInstance()
                                    .reduceSingleGoods(_player,
                                            inventory.getSpecialGoodsBag(),
                                            bagGrid, goodsID, number,
                                            CauseLog.STORE))
                            {
                                store.add(goodsType, storeBagGrid, goodsID,
                                        number, null, salePrice);
                            }

                            break;
                        }
                    }
                }
            }
            catch (Exception e)
            {

            }

            StoreDAO.insertGoods2Store(_player.getUserID(), _newGoodsDataList);
        }
        else
        {
            if (store.goodsNumber == 0)
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_NONE_GOODS));

                return;
            }
        }

        store.opened = true;
        _player.setSellStatus(true);
        StoreStatusChanged notify = new StoreStatusChanged(_player.getID(),
                true, _storeName);

        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), notify);

        MapSynchronousInfoBroadcast.getInstance().put(_player.where(), notify,
                true, _player.getID());
    }

    /**
     * 关闭商店
     * 
     * @param _player
     */
    public static void closeStore (HeroPlayer _player)
    {
log.info("@@ closeStore ...");
        PersionalStore store = storeTable.get(_player.getUserID());

        if (null != store && store.opened)
        {
log.info("#@#@ strat close store....");
            store.opened = false;
            _player.setSellStatus(false);

            store.takeOffAll(_player);
            store.removeAllEnterPlayer();
            storeTable.remove(_player.getUserID());
            
            StoreStatusChanged notify = new StoreStatusChanged(_player.getID(),
                    false);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), notify);
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
                    notify, true, _player.getID());

            
        }
    }

    /**
     * 下架所有物品
     * @param _player
     */
    public static void takeOffAll(HeroPlayer _player){
        _player.setSellStatus(false);
        PersionalStore store = storeTable.get(_player.getUserID());
        if(null != store){
            store.opened = false;

            log.debug("take off all before goodsnumber = " + store.goodsNumber);
            if(store.goodsNumber > 0)
                store.takeOffAll(_player);

            StoreStatusChanged notify = new StoreStatusChanged(_player.getID(),
                    false);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), notify);
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
                    notify, true, _player.getID());

        }else {
            store = StoreDAO.loadStore(_player.getUserID());
            if(null != store){
                store.opened = false;

                log.debug("take off all before goodsnumber = " + store.goodsNumber);
                if(store.goodsNumber > 0)
                    store.takeOffAll(_player);
            }
        }
    }

    /**
     * 从商店里移除商品
     * 
     * @param _userID
     * @param _gridIndex
     */
    public static void removeGoods (HeroPlayer _player, byte _gridIndex)
    {
        PersionalStore store = storeTable.get(_player.getUserID());

        if (null != store && !store.opened)
        {
            GoodsForSale goodsForSale = store.goodsList[_gridIndex];

            if (null != goodsForSale)
            {
                if (goodsForSale.goodsType == EGoodsType.EQUIPMENT.value())
                {
                    if (null != GoodsServiceImpl.getInstance()
                            .addEquipmentInstance2Bag(_player,
                                    goodsForSale.equipment, CauseLog.STORE))
                    {
                        if (store.remove(_gridIndex, goodsForSale)
                                && StoreDAO.removeFromStore(
                                        _player.getUserID(), _gridIndex))
                        {
                            ResponseMessageQueue
                                    .getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new GridGoodsChangesNotify(
                                                    true,
                                                    _gridIndex,
                                                    GridGoodsChangesNotify.CONTENT_OF_REMOVE));
                        }
                    }
                }
                else
                {
                    if (null != GoodsServiceImpl.getInstance()
                            .addGoods2Package(_player,
                                    goodsForSale.singleGoods,
                                    goodsForSale.number, CauseLog.STORE))
                    {
                        if (store.remove(_gridIndex, goodsForSale)
                                && StoreDAO.removeFromStore(
                                        _player.getUserID(), _gridIndex))
                        {
                            ResponseMessageQueue
                                    .getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new GridGoodsChangesNotify(
                                                    true,
                                                    _gridIndex,
                                                    GridGoodsChangesNotify.CONTENT_OF_REMOVE));
                        }
                    }
                }

                if (0 == store.goodsNumber)
                {
                    storeTable.remove(_player.getUserID());
                }
            }
        }
    }

    /**
     * 修改商品价格
     * 
     * @param _userID
     * @param _gridIndex
     */
    public static void modifyPrice (HeroPlayer _player, byte _gridIndex,
            int _newPrice)
    {
        if (_newPrice > 0 && _newPrice < 1000000000)
        {
            PersionalStore store = storeTable.get(_player.getUserID());

            if (null != store && !store.opened)
            {
                GoodsForSale goodsForSale = store.goodsList[_gridIndex];

                if (null != goodsForSale)
                {
                    int goodsID;

                    if (goodsForSale.goodsType == EGoodsType.EQUIPMENT
                            .value())
                    {
                        goodsID = goodsForSale.equipment.getInstanceID();
                    }
                    else
                    {
                        goodsID = goodsForSale.singleGoods.getID();
                    }

                    if (StoreDAO.changePrice(_player.getUserID(), _gridIndex,
                            goodsID, _newPrice))
                    {
                        goodsForSale.salePrice = _newPrice;

                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new GridGoodsChangesNotify(
                                                true,
                                                _gridIndex,
                                                GridGoodsChangesNotify.CONTENT_OF_PRICE,
                                                _newPrice));
                    }
                }
            }
        }
    }

    /**
     * 购买商店物品
     * 
     * @param _buyer 买方
     * @param _seller 店主
     * @param _gridIndex 商品位置
     * @param _goodsID 商品编号
     */
    public static void buy (HeroPlayer _buyer, HeroPlayer _seller,
            byte _gridIndex, int _goodsID)
    {
        PersionalStore store = MicroServiceImpl.getInstance().getStore(
                _seller.getUserID());

        if (null == store)
        {
            ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(),
                    new Warning(Tip.TIP_MICRO_OF_STORE_EMPTY));
        }
        else
        {
            GoodsForSale goods = store.goodsList[_gridIndex];

            if (null != goods)
            {
                if (_buyer.getMoney() < goods.salePrice)
                {
                    ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_MONEY_NOTENOUGH));

                    return;
                }

                if (goods.goodsType == EGoodsType.EQUIPMENT.value() 
                		|| goods.goodsType == EGoodsType.PET_EQUIQ_GOODS.value())
                {
                    if (_goodsID == goods.equipment.getInstanceID())
                    {
                        if (null == GoodsServiceImpl.getInstance()
                                .addEquipmentInstance2Bag(_buyer,
                                        goods.equipment, CauseLog.STORE))
                        {
                            return;
                        }
                        else
                        {
                            if (!store.remove(_gridIndex, goods)
                                    && StoreDAO.removeFromStore(_seller
                                            .getUserID(), _gridIndex)) {

                                return;
                            }
                        }
                        for(HeroPlayer other : store.getEnterPlayerList()){
                        	ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), 
                        			new OtherStoreGoodsList(MicroServiceImpl.getInstance().getStore(_seller.getUserID())));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(),
                                new Warning(Tip.TIP_MICRO_OF_GOODS_EMPTY));

                        return;
                    }

                }
                else
                {
                    if (_goodsID == goods.singleGoods.getID())
                    {
                        if (null == GoodsServiceImpl.getInstance()
                                .addGoods2Package(_buyer, goods.singleGoods,
                                        goods.number, CauseLog.STORE))
                        {
                            return;
                        }
                        else
                        {
                            if (!store.remove(_gridIndex, goods)
                                    && StoreDAO.removeFromStore(_seller
                                            .getUserID(), _gridIndex)) {

                                return;
                            }
                        }
                        for(HeroPlayer other : store.getEnterPlayerList()){
                        	ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), 
                        			new OtherStoreGoodsList(MicroServiceImpl.getInstance().getStore(_seller.getUserID())));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(),
                                new Warning(Tip.TIP_MICRO_OF_GOODS_EMPTY));

                        return;
                    }
                }

                ResponseMessageQueue.getInstance().put(
                        _buyer.getMsgQueueIndex(),
                        new GridGoodsChangesNotify(false, _gridIndex,
                                GridGoodsChangesNotify.CONTENT_OF_REMOVE));
                ResponseMessageQueue.getInstance().put(
                        _seller.getMsgQueueIndex(),
                        new GridGoodsChangesNotify(true, _gridIndex,
                                GridGoodsChangesNotify.CONTENT_OF_REMOVE));

                if (!PlayerServiceImpl.getInstance()
                        .addMoney(_seller, goods.salePrice, 1,
                                PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                "个人商店出售")) { return; }

                PlayerServiceImpl.getInstance()
                        .addMoney(_buyer, -goods.salePrice, 1,
                                PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                "个人商店购买");

                if (0 == store.goodsNumber)
                {
                	store.removeAllEnterPlayer();
                    storeTable.remove(_seller.getUserID());
                    _seller.setSellStatus(false);
                    ResponseMessageQueue.getInstance().put(_seller.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_CLOSE_THAT_GOODS_EMPTY));
                    StoreStatusChanged msg = new StoreStatusChanged(_seller
                            .getID(), false);
                    ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), msg);
                    MapSynchronousInfoBroadcast.getInstance().put(
                            _seller.where(), msg, true, _buyer.getID());
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_GOODS_EMPTY));
            }
        }
    }

}
