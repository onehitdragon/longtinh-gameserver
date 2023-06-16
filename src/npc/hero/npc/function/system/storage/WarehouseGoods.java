package hero.npc.function.system.storage;

import hero.item.EquipmentInstance;

/**
 * 仓库物品，仓库中存放的物品 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class WarehouseGoods
{
    /**
     * 存放物品的id，如果是装备这里则是instanceID
     */
    public int               goodsID;

    /**
     * 存放物品的数量,如果物品为装备的时候这里的值为物品的强化等级
     */
    public short             goodsNum;

    /**
     * 存放的物品类型(0-装备，1-普通物品)
     */
    public short             goodsType;
    
    /**
     * 如果是马符或者大补丸的时候.该ID指向对应物品信息表的索引ID
     */
    public int				 indexID;

    public EquipmentInstance instance;

    public WarehouseGoods(int _goodsID, short _goodsNum, short _goodsType,
            EquipmentInstance _instance, int _indexID)
    {
        goodsID = _goodsID;
        goodsNum = _goodsNum;
        goodsType = _goodsType;
        instance = _instance;
        indexID = _indexID;
    }
}