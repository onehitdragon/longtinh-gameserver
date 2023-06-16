package hero.item.bag;

import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.BigTonicBall;
import hero.item.special.ESpecialGoodsType;
import hero.item.special.PetPerCard;
import hero.item.special.SpecialGoodsBuilder;
import hero.player.HeroPlayer;
import hero.player.message.HotKeySumByMedicament;
import hero.player.service.PlayerServiceImpl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Package.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-19 下午04:42:11
 * @描述 ：非装备类型物品容器（可以叠加）
 * ** 待优化.
 * ** 将来需要考虑把特殊物品包内的非一次性道具做一个OO模型,利于编码优雅化,提高复用,降低耦合.
 */

public class SingleGoodsBag
{
    private static Logger log = Logger.getLogger(SingleGoodsBag.class);
	private HeroPlayer master;
    /**
     * 空格子数量
     */
    private short   emptyGridNumber;

    /**
     * 大小
     */
    private int     size;

    /**
     * 数据容器
     */
    private int[][] items;
    
    /**
     * 大补丸容器.
     */
    public Hashtable<Integer, BigTonicBall> tonicList;
    /**
     * 按次宠物卡容器.
     */
    public Hashtable<Integer, PetPerCard> petPerCardList;

    /**
     * 构造
     * 
     * @param _type 类型
     * @param _capacity 尺寸
     */
    public SingleGoodsBag(short _size)
    {
        size = _size;
        emptyGridNumber = _size;
        items = new int[_size][2];
        tonicList = new Hashtable<Integer, BigTonicBall>(500);
        petPerCardList = new Hashtable<Integer, PetPerCard>(500);
    }
    
    /**
     * 使用按次宠物卡
     * @param index
     * @param _player
     * @return
     */
    public boolean usePetPerCard(int index, int _goodsID, HeroPlayer _player)
    {
    	boolean useUp = false;
    	PetPerCard card = petPerCardList.get(index);
    	if(index == -1)
    	{
    		Enumeration<Integer> keys = petPerCardList.keys();
    		while(keys.hasMoreElements()){ 
    			index = keys.nextElement();
    			card = petPerCardList.get(index);
    			break;
    		}
    	}
    	useUp = card.beUse(_player, _player, index);
    	return useUp;
    }
    
    public boolean eatTonicBall(int index, int _goodsID, HeroPlayer _player)
    {
    	boolean useUp = false;
    	BigTonicBall ball = tonicList.get(index);
    	if(index == -1)
    	{
    		BigTonicBall reference = new BigTonicBall(_goodsID, (short)1);
    		if (reference != null) 
    		{
    			Enumeration<Integer> keys = tonicList.keys();
    			while(keys.hasMoreElements()) { 
    				index = keys.nextElement();
    				ball = tonicList.get(index);
    				if(ball.isActivate == BigTonicBall.TONINC_UNAUTO 
    						&& ball.tonincType == reference.tonincType)
    				{
    					break;
    				}
    			}
			}
    	}
    	useUp = ball.beUse(_player, _player, index);
    	return useUp;
    }
    /**
     * 设置自动大补丸到玩家对象里面
     * @param index
     * @param _player
     * @return
     */
    public boolean installTonicBall(int index, HeroPlayer _player)
    {
    	boolean useUp = false;
    	BigTonicBall ball = tonicList.get(index);
    	if(index == -1)
    	{
    		Enumeration<Integer> keys = tonicList.keys();
    		while(keys.hasMoreElements()){ 
    			index = keys.nextElement();
    			ball = tonicList.get(index);
    			if(ball.isActivate != BigTonicBall.TONINC_UNAUTO)
    			{
    				break;
    			}
    		}
    	}
    	useUp = ball.beUse(_player, _player, index);
    	return useUp;
    }

    /**
     * 包大小
     * 
     * @return
     */
    public int getSize ()
    {
        return size;
    }

    /**
     * 获取空格子的数量
     * 
     * @return
     */
    public int getEmptyGridNumber ()
    {
        return emptyGridNumber;
    }

    /**
     * 获取已放置了物品的格子数量
     * 
     * @return
     */
    public int getFullGridNumber ()
    {
        return size - emptyGridNumber;
    }

    /**
     * 加载背包物品（从数据库获取）
     * 
     * @param _goodsID
     * @param _number
     * @param pakcage_index
     */
    public void load (int _goodsID, int _number, int _bagIndex)
    {
        if (0 == items[_bagIndex][0])
        {
            emptyGridNumber--;
        }

        items[_bagIndex][0] = _goodsID;
        items[_bagIndex][1] = _number;
        
    }
    /**
     * 加载背包的大补丸(从数据库获取)
     * @param _goodsID
     * @param _number
     * @param _bagIndex
     * @param _surplus
     * @param _type
     */
    public void loadBigTonicBall (int _goodsID, int _number, int _bagIndex, int _surplus, int _type, 
    		SpecialGoods _specialGoods)
    {
        //add by zhengl
        if(_specialGoods != null) 
        {
        	BigTonicBall ball = new BigTonicBall(_specialGoods.getID(), (short)1);
        	ball.initData(_surplus, _type, _bagIndex);
        	ball.copyGoodsData(_specialGoods, master);
        	tonicList.put(_bagIndex, ball);
        }
    }
    
    /**
     * 加载背包中的宠物卡
     * @param _goodsID
     * @param _number
     * @param _bagIndex
     * @param _surplus
     * @param _specialGoods
     */
    public void loadBigPetPerCard (int _goodsID, int _number, int _bagIndex, int _surplus, 
    		SpecialGoods _specialGoods)
    {
        //add by zhengl
        if(_specialGoods != null) 
        {
        	PetPerCard card = new PetPerCard(_specialGoods.getID(), (short)1);
        	card.initData(_surplus, _bagIndex);
        	card.copyGoodsData(_specialGoods);
        	petPerCardList.put(_bagIndex, card);
        }
    }
    

    /**
     * 添加物品
     * 
     * @param _goodsID 物品编号
     * @param _number 添加的数量
     * @return
     */
    protected short[] add (int _goodsID, int _number) throws BagException
    {
        return add((SingleGoods) GoodsContents.getGoods(_goodsID), _number);
    }
    
    /**
     * 获得第1个空格子的index
     * @return
     */
    public int getFirstEmptyGridIndex()
    {
    	int result = 0;
    	int emptyGridIndex = -1;
    	synchronized (items)
    	{
            for (int i = 0; i < size; i++)
            {
                if (items[i][0] == 0)
                {
                	emptyGridIndex = i;
                    break;
                }
             }
    	}
    	result = emptyGridIndex;
    	return result;
    }

    /**
     * 添加物品
     * 
     * @param _goods 物品
     * @param _number 添加的数量
     * @return
     */
    public short[] add (SingleGoods _goods, int _number) throws BagException
    {
        if (null == _goods || 0 >= _number)
        {
            throw PackageExceptionFactory.getInstance()
                    .getException("添加无效物品数据");
        }

        if (_number > _goods.getMaxStackNums())
        {
            throw PackageExceptionFactory.getInstance().getException(
                    "一次性添加的物品数量不能超过可叠加的最大数量");
        }

        int emptyGridIndex = -1;

        synchronized (items)
        {
            for (int i = 0; i < size; i++)
            {
                if (items[i][0] == 0)
                {
                    if (emptyGridIndex == -1)

                    {
                        emptyGridIndex = i;
                    }

                    continue;
                }

                if (items[i][0] == _goods.getID()
                        && _number <= (_goods.getMaxStackNums() - items[i][1]))
                {
                    items[i][1] += _number;

                    return new short[]{(short) i, (short) items[i][1] };
                }
            }

            if (-1 == emptyGridIndex)
            {
                throw PackageExceptionFactory.getInstance().getFullException(
                        _goods.getGoodsType());
            }

            items[emptyGridIndex][0] = _goods.getID();
            items[emptyGridIndex][1] = _number;

            if(_goods instanceof BigTonicBall)
            {
            	BigTonicBall ball = new BigTonicBall(_goods.getID(), (short)1);
            	ball.copyGoodsData((BigTonicBall)_goods, master);
            	tonicList.put(emptyGridIndex, ball);
            }
            if(_goods instanceof PetPerCard)
            {
            	PetPerCard card = new PetPerCard(_goods.getID(), (short)1);
            	card.copyGoodsData((PetPerCard)_goods);
            	petPerCardList.put(emptyGridIndex, card);
            }
            emptyGridNumber--;
        }

        medicamentChange(_goods.getID(), _goods.getGoodsType());
        return new short[]{(short) emptyGridIndex, (short) _number };
    }

    /**
     * 删除指定格子索引处的所有物品
     * 
     * @param _gridIndex 物品位置
     * @param _goodsID 物品编号
     * @return 是否成功删除
     */
    public boolean remove (int _gridIndex, int _goodsID)
    {
        synchronized (items)
        {
            if (_gridIndex >= 0 && _gridIndex < size)
            {
                if (items[_gridIndex][0] == _goodsID
                        && items[_gridIndex][1] > 0)
                {
                	//add by zhengl; date: 2011-03-15; note: 同时也把tonic列表的物品移除.
                	tonicList.remove(_gridIndex);
                	petPerCardList.remove(_gridIndex);
                    items[_gridIndex][0] = 0;
                    items[_gridIndex][1] = 0;

                    emptyGridNumber++;
                    
                    medicamentChange(_goodsID, 
                    		GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 删除指定物品指定的数量
     * 
     * @param _goodsID 要删除的物品编号
     * @param _number 删除的数量
     * @return 删除结果（int[] 0下标：位置 1下标：剩余数量）
     */
    public ArrayList<int[]> remove (int _goodsID, short _number)
            throws BagException
    {
        synchronized (items)
        {
            if (_number <= 0)
            {
                throw PackageExceptionFactory.getInstance().getException(
                        "删除的数量非法");
            }

            if (getGoodsNumber(_goodsID) < _number)
            {
                throw PackageExceptionFactory.getInstance().getException(
                        "删除的数量超过背包中数量");
            }

            int reduceDeleteNumber = _number;
            ArrayList<int[]> result = new ArrayList<int[]>();

            for (int i = 0; i < size; i++)
            {
                if (items[i][0] == _goodsID)
                {
                    if (items[i][1] > reduceDeleteNumber)
                    {
                        items[i][1] -= reduceDeleteNumber;
                        result.add(new int[]{i, items[i][1] });

                        medicamentChange(_goodsID, 
                        		GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                        return result;
                    }
                    else
                    {
                        reduceDeleteNumber -= items[i][1];
                        items[i][0] = 0;
                        items[i][1] = 0;
                        result.add(new int[]{i, 0 });

                        emptyGridNumber++;

                        if (0 == reduceDeleteNumber)
                        {
                            medicamentChange(
                            		_goodsID, 
                            		GoodsServiceImpl.getInstance().getGoodsByID(
                            				_goodsID).getGoodsType());
                            return result;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 删除指定物品
     * 
     * @param _goods 要删除的物品
     * @return 删除的数量
     */
    public int remove (SingleGoods _goods) throws BagException
    {
        return remove(_goods.getID());
    }

    /**
     * 删除指定物品
     * 
     * @param _goodsID 要删除的物品编号
     * @return 删除的数量
     */
    public int remove (int _goodsID) throws BagException
    {
        int deletedNumber = 0;

        synchronized (items)
        {
            for (int i = 0; i < size; i++)
            {
                if (items[i][0] == _goodsID)
                {
                    deletedNumber += items[i][1];
                    items[i][0] = 0;
                    items[i][1] = 0;
                    emptyGridNumber++;
                    medicamentChange(_goodsID, 
                    		GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                }
            }
        }

        return deletedNumber;
    }

    /**
     * 删除一个指定物品（应用于快捷键）
     * 
     * @param _goodsID 要删除的物品编号
     * @return 删除的数量
     */
    public short[] removeOne (int _goodsID) throws BagException
    {
        synchronized (items)
        {
            for (int i = 0; i < size; i++)
            {
                if (items[i][0] == _goodsID && items[i][1] > 0)
                {
                    items[i][1]--;

                    if (0 == items[i][1])
                    {
                        items[i][0] = 0;

                        emptyGridNumber++;
                    }
                    medicamentChange(_goodsID, 
                    		GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                    return new short[]{(short) i, (short) (items[i][1]) };
                }
            }
        }

        return null;
    }

    /**
     * 删除指定格子的指定数量物品（应用于分离格子内的物品）
     * 
     * @param _gridIndex 背包位置
     * @param _goods 删除的物品
     * @param _number 删除的数量
     * @return
     */
    public short[] remove (int _gridIndex, int _goodsID, int _number)
            throws BagException
    {
        synchronized (items)
        {
        	//add by zhengl; date: 2011-03-15; note: 同时也把tonic列表的物品移除.
        	tonicList.remove(_gridIndex);
        	petPerCardList.remove(_gridIndex);
            if (0 >= _number || items[_gridIndex][0] != _goodsID
                    || items[_gridIndex][1] < _number)
            {
                throw PackageExceptionFactory.getInstance().getException(
                        "删除数据无效");
            }

            if (items[_gridIndex][1] > _number)
            {
                items[_gridIndex][1] -= _number;
                medicamentChange(_goodsID, 
                		GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                return new short[]{
                        (short) _gridIndex, (short) items[_gridIndex][1] };
            }
            else
            {
                items[_gridIndex][0] = 0;
                items[_gridIndex][1] = 0;

                emptyGridNumber++;
                medicamentChange(_goodsID, 
                		GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                return new short[]{(short) _gridIndex, (short) 0 };
            }
        }
    }

    /**
     * 获取指定物品在背包中第一个位置
     * 
     * @param _goodsID 物品编号
     * @return
     */
    public int getFirstGridIndex (int _goodsID)
    {
        if (_goodsID <= 0)
        {
            return -1;
        }

        for (int i = 0; i < size; i++)
        {
            if (items[i][0] == _goodsID)
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * 获取包中指定物品的数量
     * 
     * @param _goodsID 物品编号
     * @return
     */
    public int getGoodsNumber (int _goodsID)
    {
        int number = 0;

        for (int[] goodsDesc : items)
        {
            if (goodsDesc[0] == _goodsID)
            {
                number += goodsDesc[1];
            }
        }

        return number;
    }

    /**
     * 获取背包中指定位置的物品
     * 
     * @param _gridIndex
     * @return
     */
    public int[] getItemData (int _gridIndex)
    {
        if (_gridIndex >= 0 && _gridIndex < items.length)
        {
            return items[_gridIndex];
        }

        return null;
    }

    /**
     * 获取物品数据
     * 
     * @return
     */
    public int[][] getAllItem ()
    {
        return items;
    }

    /**
     * 根据物品编号从小到大排序
     */
    public boolean clearUp ()
    {
        boolean changed = false;

        for (int i = 0; i < size; i++)
        {
            for (int j = i + 1; j < size; j++)
            {
                if (0 == items[j][0])
                {
                    continue;
                }

                if (0 == items[i][0] || items[i][0] > items[j][0])
                {
                    int[] temp = items[i];
                    items[i] = items[j];
                    items[j] = temp;

                    changed = true;
                }
                else if (items[i][0] == items[j][0])
                {
                    int stackNums = GoodsServiceImpl.getInstance()
                            .getGoodsByID(items[i][0]).getMaxStackNums();

                    if (items[i][1] + items[j][1] > stackNums)
                    {
                        items[j][1] -= stackNums - items[i][1];
                        items[i][1] = stackNums;

                        changed = true;
                    }
                    else
                    {
                        items[i][1] += items[j][1];
                        items[j][0] = 0;
                        items[j][1] = 0;

                        emptyGridNumber++;

                        changed = true;
                    }
                }
            }
        }

        return changed;
    }

    /**
     * 升级背包
     * 
     * @return 是否成功升级（false：已达到顶级）
     */
    public boolean upgrade ()
    {
        if (size == Inventory.BAG_MAX_SIZE)
        {
            return false;
        }

        int[][] newContainer = new int[size + Inventory.STEP_GRID_SIZE][2];

        System.arraycopy(items, 0, newContainer, 0, size);

        items = newContainer;
        emptyGridNumber += Inventory.STEP_GRID_SIZE;
        size += Inventory.STEP_GRID_SIZE;

        return true;
    }
    
    public void initMaster (int _masterID)
    {
    	master = PlayerServiceImpl.getInstance().getPlayerByUserID(_masterID);
    }
    
    /**
     * 药水变化引起的快捷键变化
     * **add by zhengl; date: 2011-03-07; note: 药水的变化引起的快捷键变化
     * @param _goodsID
     */
    private void medicamentChange (int _goodsID, EGoodsType _eType)
    {
        //add by zhengl; date: 2011-03-07; note: 使用药水的时候变化引起的快捷键变化
    	if((EGoodsType.MEDICAMENT == _eType || EGoodsType.SPECIAL_GOODS == _eType) 
    			&& master != null)
    	{
    		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!药水/特殊物品数量发生变化");
    		HotKeySumByMedicament keyMsg = new HotKeySumByMedicament(master, _goodsID);
    		if(keyMsg.haveRelation(_goodsID)) {
    			ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), keyMsg);
    		}
    	}
    	//end
    }

}
