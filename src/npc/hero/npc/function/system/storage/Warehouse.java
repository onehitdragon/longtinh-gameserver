package hero.npc.function.system.storage;

import hero.item.EquipmentInstance;
import hero.item.service.GoodsDAO;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:玩家仓库类<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class Warehouse
{
    /**
     * 仓库最大等级
     */
    public static final byte          MAX_LEVEL    = 8;

    /**
     * 没提升一级增加的格子数
     */
    public static final byte          UP_SIZE      = 8;

    /**
     * 仓库默认大小
     */
    public static final short         DEFAULT_SIZE = 16;

    /**
     * 拥有者昵称
     */
    private String                    ownerNickname;

    /**
     * 仓库现在等级
     */
    private byte                      level;

    /**
     * 仓库中的物品列表
     */
    private ArrayList<WarehouseGoods> goodsList;

    private ReentrantLock             lock         = new ReentrantLock();

    public Warehouse(String _ownerNickname, byte _level)
    {
        ownerNickname = _ownerNickname;
        level = _level;
        goodsList = new ArrayList<WarehouseGoods>();
        int maxSize = DEFAULT_SIZE + UP_SIZE * _level;
        for (int i = 0; i < maxSize; i++)
        {
            goodsList.add(null);
        }
    }

    /**
     * 清除玩家仓库内存数据时调用
     */
    protected void clear ()
    {
        goodsList.clear();
    }

    /**
     * 得到仓库所有者昵称
     * 
     * @return
     */
    public String getOwnerNickname ()
    {
        return ownerNickname;
    }

    /**
     * 得到仓库当前等级
     * 
     * @return
     */
    public byte getLevel ()
    {
        return level;
    }

    /**
     * 得到升级需要的金钱
     * 
     * @return
     */
    public int getUpLevelMoney ()
    {
        return (level + 1) * (level + 1) * 5000;
    }

    /**
     * 得到仓库最大数
     * 
     * @return
     */
    public int getMaxSize ()
    {
        return goodsList.size();
    }

    /**
     * 得到仓库存放物品的数量
     * 
     * @return
     */
    public int getGoodsNum ()
    {
        try
        {
            lock.lock();
            int num = 0;
            for (WarehouseGoods goods : goodsList)
            {
                if (goods != null)
                    num++;
            }
            return num;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 将仓库升到下一级
     */
    public void upLevel ()
    {
        try
        {
            lock.lock();
            level++;
            for (int i = 0; i < UP_SIZE; i++)
            {
                goodsList.add(null);
            }
            WarehouseDB.updateLvl(level, ownerNickname);
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 添加一个物品到仓库中
     * 
     * @param _goodsID
     * @param _goodsNum
     * @param _goodsType
     * @return
     */
    public boolean addWarehouseGoods (int _goodsID, short _goodsNum,
            short _goodsType, int _userID, int _bagGridIndex, boolean _isAutoBall)
    {
        try
        {
            lock.lock();
            for (int i = 0; i < goodsList.size(); i++)
            {
                if (goodsList.get(i) == null)
                {
                    //add by zhengl; date: 2011-05-12; note: 附加存储非装备类型物品索引ID
                    int indexID = 0;
                    if (_goodsType == 1) 
                    {
                    	indexID = GoodsDAO.selectTonic(_userID, _bagGridIndex, _goodsID);
					}
                    goodsList.set(i, new WarehouseGoods(_goodsID, _goodsNum,
                    		_goodsType, null, indexID));
                    WarehouseDB.insertGoods(ownerNickname, (byte) i, _goodsID,
                            _goodsNum, _goodsType, indexID, _isAutoBall);
                    return true;
                }
            }
            return false;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 添加一个装备实例到仓库中
     * 
     * @param _instance
     * @return
     */
    public boolean addWarehouseGoods (EquipmentInstance _instance)
    {
        try
        {
            lock.lock();
            for (int i = 0; i < goodsList.size(); i++)
            {
                if (goodsList.get(i) == null)
                {
                    int _goodsID = _instance.getInstanceID();
                    short _goodsNum = 1;
                    short _goodsType = 0;
                    goodsList.set(i, new WarehouseGoods(_goodsID, _goodsNum,
                            _goodsType, _instance, 0));
                    //edit by zhengl; date: 2011-05-12; note: 为适配大补丸,马符这些东西而统一添加
                    WarehouseDB.insertGoods(ownerNickname, (byte) i, _goodsID,
                            _goodsNum, _goodsType, 0, false);
                    return true;
                }
            }
            return false;
        }
        finally
        {
            lock.unlock();
        }
    }

    protected void addWarehouseGoods (byte _index, int _goodsID,
            short _goodsNum, short _goodsType, EquipmentInstance _instance, int single_goods_id)
    {
        try
        {
            lock.lock();
            goodsList.set(_index, new WarehouseGoods(_goodsID, _goodsNum,
                    _goodsType, _instance, single_goods_id));
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 取出指定位置的仓库物品
     * 
     * @param _index
     * @return
     */
    public WarehouseGoods getWarehouseGoods (int _index)
    {
        try
        {
            lock.lock();
            WarehouseGoods goods = goodsList.get(_index);
            return goods;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 移除指定位置的物品
     * 
     * @param _index
     */
    public void removeWarehouseGoods (int _index)
    {
        try
        {
            lock.lock();
            goodsList.set(_index, null);
            WarehouseDB.delGoods(ownerNickname, (byte) _index);
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 得到仓库中所有物品
     * 
     * @return
     */
    public ArrayList<WarehouseGoods> getGoodsList ()
    {
        return goodsList;
    }
}
