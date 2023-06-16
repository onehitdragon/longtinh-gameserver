package hero.npc.function.system.storage;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 仓库集合类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class WarehouseDict
{

    private static WarehouseDict       instance;

    private HashMap<String, Warehouse> warehouses = null;

    private ReentrantLock              lock       = new ReentrantLock();

    private WarehouseDict()
    {
        warehouses = new HashMap<String, Warehouse>();
    }

    public static WarehouseDict getInstance ()
    {
        if (instance == null)
            instance = new WarehouseDict();
        return instance;
    }

    /**
     * 得到指定昵称的玩家仓库
     * 
     * @param _nickname
     * @return
     */
    public Warehouse getWarehouseByNickname (String _nickname)
    {
        try
        {
            lock.lock();
            Warehouse w = warehouses.get(_nickname);
            if (w == null)
            {
                byte lvl = WarehouseDB.selLvl(_nickname);
                w = new Warehouse(_nickname, lvl);
                WarehouseDB.selGoods(_nickname, w);
                warehouses.put(_nickname, w);
            }
            return w;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 玩家登陆时调用，加载玩家的仓库
     * 
     * @param _nickname
     */
    public void loadWarehouseByNickname (String _nickname)
    {
        try
        {
            lock.lock();
            Warehouse w = warehouses.get(_nickname);
            if (w == null)
            {
                byte lvl = WarehouseDB.selLvl(_nickname);
                w = new Warehouse(_nickname, lvl);
                WarehouseDB.selGoods(_nickname, w);
                warehouses.put(_nickname, w);
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 玩家离线时调用，将内存中玩家仓库数据清除
     * 
     * @param _nickname
     */
    public void releaseWarehouseByNickname (String _nickname)
    {
        try
        {
            lock.lock();

            Warehouse warehouse = warehouses.remove(_nickname);
            
            if (null != warehouse)
                warehouse.clear();
        }
        finally
        {
            lock.unlock();
        }
    }
}
