package hero.micro.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hero.item.EqGoods;
import hero.item.EquipmentInstance;
import hero.item.SingleGoods;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.EquipmentFactory;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PersionalStore.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-19 上午10:15:13
 * @描述 ：
 */

public class PersionalStore
{
    private static Logger log = Logger.getLogger(PersionalStore.class);
    /**
     * 物品列表
     */
    public GoodsForSale[] goodsList;

    /**
     * 商品数量
     */
    public int            goodsNumber;

    /**
     * 名称
     */
    public String         name;

    /**
     * 是否营业
     */
    public boolean        opened;

    /**
     * 是否下架所有物品
     */
    private boolean isTakeOff = false;
    
    /**
     * 进入本店的其它玩家
     */
    private List<HeroPlayer> enterPlayerList;

    /**
     * 构造
     * 
     * @param _name
     */
    public PersionalStore()
    {
        goodsList = new GoodsForSale[MAX_SIZE];
        name = DEFAULT_NAME;
        enterPlayerList = new ArrayList<HeroPlayer>();
    }
    
    /**
     * 获取进入商店的其它玩家列表
     * @return
     */
    public List<HeroPlayer> getEnterPlayerList(){
    	return enterPlayerList;
    }
    
    /**
     * 其它玩家进入本店时，加到此列表里
     * @param other
     */
    public void addEntrePlayer(HeroPlayer other){
    	enterPlayerList.add(other);
    }
    
    /**
     * 移除退出商店的玩家
     * @param other
     */
    public void removeEnterPlayer(HeroPlayer other){
    	for(Iterator<HeroPlayer> it= enterPlayerList.iterator();it.hasNext();){
    		if(it.next().getUserID() == other.getUserID()){
    			it.remove();
    			break;
    		}
    	}
    }
    /**
     * 移除所有玩家
     */
    public void removeAllEnterPlayer(){
/*    	for(Iterator<HeroPlayer> it = enterPlayerList.iterator(); it.hasNext();){
    		it.remove();
    	}*/
    	enterPlayerList = null;
    }

    /**
     * 设置商店名字
     * 
     * @param _name
     */
    public void setName (String _name)
    {
        name = _name;
    }

    /**
     * 添加商品
     * 
     * @param _goodsType 物品类型
     * @param _bagGrid 在原先背包中的位置
     * @param _singleGoodsID 非装备物品编号
     * @param _number 非装备物品数量
     * @param _equipment 装备
     */
    public boolean add (byte _goodsType, byte _bagGrid, int _singleGoodsID,
            short _number, EquipmentInstance _equipment, int _salePrice)
    {
        if (null == goodsList[_bagGrid])
        {
            if (_singleGoodsID > 0)
            {

                goodsList[_bagGrid] = new GoodsForSale(_goodsType, _bagGrid,
                        (SingleGoods) GoodsContents.getGoods(_singleGoodsID),
                        _number, _equipment, _salePrice);
            }
            else
            {
                goodsList[_bagGrid] = new GoodsForSale(_goodsType, _bagGrid,
                        null, (short) 0, _equipment, _salePrice);
            }

            goodsNumber++;

            return true;
        }

        return false;
    }

    /**
     * 关闭商店时把所有物品都放回玩家背包
     * @param _player
     */
    public void takeOffAll(HeroPlayer _player){
        log.debug("is take Off  = " + isTakeOff);
        if(!isTakeOff){
            for (GoodsForSale goods : goodsList) {
                if (goods != null) {
                    remove(goods.gridIndex, goods);
                    if(goods.goodsType == EGoodsType.EQUIPMENT.value()){//装备
                        log.debug("take off equipment ...");

                        GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(_player,goods.equipment,CauseLog.TAKEOFF);

    //                        OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),new Warning("下架 "+goods.singleGoods.getName()+" 失败"));

                    }else{
                        log.debug("take off not equipment ...");
                        GoodsServiceImpl.getInstance().addGoods2Package(_player, goods.singleGoods, goods.number, CauseLog.TAKEOFF);
                    }
                    StoreDAO.removeFromStore(_player.getUserID(), goods.gridIndex);
                    log.debug("takeOff end ...");
                }
            }
            isTakeOff = true;
        }
    }

    /**
     * 从商店中移除商品
     * 
     * @param _gridIndex
     * @param _goodsForSale
     * @return
     */
    public boolean remove (byte _gridIndex, GoodsForSale _goodsForSale)
    {
        GoodsForSale goods = goodsList[_gridIndex];

        if (null != goods && _goodsForSale == goods)
        {
            goodsList[_gridIndex] = null;

            goodsNumber--;

            return true;
        }

        return false;
    }

    /**
     * @author DC 销售的物品
     */
    public class GoodsForSale
    {
        /**
         * 销售物品
         * 
         * @param _goodsType 物品类型
         * @param _gridIndex 在原先背包中的位置
         * @param _singleGoodsID 非装备物品编号
         * @param _number 非装备物品数量
         * @param _equipment 装备
         */
        public GoodsForSale(byte _goodsType, byte _gridIndex,
                SingleGoods _singleGoods, short _number,
                EquipmentInstance _equipment, int _salePrice)
        {
            goodsType = _goodsType;
            gridIndex = _gridIndex;
            singleGoods = _singleGoods;
            number = _number;
            equipment = _equipment;
            salePrice = _salePrice;
        }

        /**
         * 物品类型
         */
        public byte              goodsType;

        /**
         * 在包裹中的格子
         */
        public byte              gridIndex;

        /**
         * 非装备物品
         */
        public SingleGoods       singleGoods;

        /**
         * 非装备物品数量
         */
        public short             number;

        /**
         * 装备
         */
        public EquipmentInstance equipment;

        /**
         * 销售价格
         */
        public int               salePrice;
    }

    /**
     * 最大尺寸
     */
    public static final byte    MAX_SIZE     = 16;

    /**
     * 默认名称
     */
    private static final String DEFAULT_NAME = "";
}
