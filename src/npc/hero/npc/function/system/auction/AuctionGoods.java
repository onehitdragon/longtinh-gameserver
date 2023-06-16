package hero.npc.function.system.auction;

import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;

/**
 * 装备类拍卖物品 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class AuctionGoods
{
    /**
     * 拍卖ID，拍卖行中物品的唯一标识
     */
    private int               auctionID;

    /**
     * 物品模板ID
     */
    private int               goodsID;

    /**
     * 拥有者UID
     */
    private int               ownerUserID;

    /**
     * 拥有者昵称
     */
    private String            ownerNickname;

    /**
     * 强化等级
     */
    private short             enhanceLevel;

    /**
     * 物品数量
     */
    private short             num;

    /**
     * 拍卖价格
     */
    private int               price;

    /**
     * 拍卖物品类型
     */
    private AuctionType       type;

    /**
     * 装备
     */
    private EquipmentInstance instance;

    /**
     * 拍卖开始时间
     */
    private long              auctionTime;

    /**
     * 构造
     * 
     * @param _auctionID
     * @param _goodsID
     * @param _ownerUserID
     * @param _ownerNickname
     * @param _enhanceLevel
     * @param _num
     * @param _price
     * @param _type
     * @param _instance
     * @param _auctionTime
     */
    public AuctionGoods(int _auctionID, int _goodsID, int _ownerUserID,
            String _ownerNickname, short _enhanceLevel, short _num, int _price,
            AuctionType _type, EquipmentInstance _instance, long _auctionTime)
    {
        auctionID = _auctionID;
        goodsID = _goodsID;
        ownerUserID = _ownerUserID;
        ownerNickname = _ownerNickname;
        enhanceLevel = _enhanceLevel;
        num = _num;
        price = _price;
        type = _type;
        auctionTime = _auctionTime;
        instance = _instance;
    }

    public EquipmentInstance getInstance ()
    {
        return instance;
    }

    public int getAuctionID ()
    {
        return auctionID;
    }

    public int getGoodsID ()
    {
        return goodsID;
    }

    public int getOwnerUserID ()
    {
        return ownerUserID;
    }

    public String getOwnerNickname ()
    {
        return ownerNickname;
    }

    public short getEnhanceLevel ()
    {
        return enhanceLevel;
    }

    public short getNum ()
    {
        return num;
    }

    public int getPrice ()
    {
        return price;
    }

    public AuctionType getAuctionType ()
    {
        return type;
    }

    public long getAuctionTime ()
    {
        return auctionTime;
    }

    public String getName ()
    {
        if (null != instance)
        {
            return instance.getArchetype().getName();
        }
        else
        {
            return GoodsContents.getGoods(goodsID).getName();
        }
    }
}
