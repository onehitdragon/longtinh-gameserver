package hero.item.service;

import hero.charge.service.ChargeServiceImpl;
import hero.chat.service.ChatQueue;
import hero.expressions.service.CEService;
import hero.item.*;
import hero.item.bag.EBagType;
import hero.item.bag.EquipmentContainer;
import hero.item.bag.Inventory;
import hero.item.bag.SingleGoodsBag;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsTrait;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.ArmorDict;
import hero.item.dictionary.ChangeVocationToolsDict;
import hero.item.dictionary.ExchangeGoodsDict;
import hero.item.dictionary.GoodsContents;
import hero.item.dictionary.MaterialDict;
import hero.item.dictionary.MedicamentDict;
import hero.item.dictionary.SpecialGoodsDict;
import hero.item.dictionary.SuiteEquipmentDataDict;
import hero.item.dictionary.TaskGoodsDict;
import hero.item.dictionary.WeaponDict;
import hero.item.legacy.MonsterLegacyBox;
import hero.item.legacy.MonsterLegacyManager;
import hero.item.legacy.PersonalPickerBox;
import hero.item.legacy.RaidPickerBox;
import hero.item.legacy.WorldLegacyDict;
import hero.item.message.RefreshEquipmentDurabilityPoint;
import hero.item.message.SendBagSize;
import hero.item.message.SendLegacyBoxList;
import hero.item.message.UpgradeBagAnswer;
import hero.item.special.BagExpan;
import hero.item.special.ESpecialGoodsType;
import hero.item.special.HeavenBook;
import hero.log.service.*;
import hero.map.Map;
import hero.npc.function.system.storage.Warehouse;
import hero.npc.function.system.storage.WarehouseDict;
import hero.npc.message.NpcInteractiveResponse;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.ui.UI_Confirm;
import hero.ui.message.ResponseEuipmentPackageChange;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ItemServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-18 下午01:58:06
 * @描述 ：物品服务
 */

public class GoodsServiceImpl extends AbsServiceAdaptor<GoodsConfig>
{
	private static Logger log = Logger.getLogger(GoodsServiceImpl.class);
    /**
     * 单例
     */
    private static GoodsServiceImpl instance;
    
    /**
     * 仅仅提供给大补丸,消耗次数型宠物卡 等结构的特殊物品使用.
     * 该ID主要是为了进行物品使用的记录
     */
    private int                   USABLE_SPECAIL_GOODS_ID;

    /**
     * 私有构造
     */
    private GoodsServiceImpl()
    {
        config = new GoodsConfig();
        USABLE_SPECAIL_GOODS_ID = 1000;
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static GoodsServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new GoodsServiceImpl();
        }

        return instance;
    }

    @Override
    protected void start ()
    {
        SuiteEquipmentDataDict.getInstance().load(
                config.suite_equipment_data_path);
        WeaponDict.getInstance().load(config.weapon_data_path);
        ArmorDict.getInstance().load(config.armor_data_path);
        MedicamentDict.getInstance().load(config.medicament_data_path);
        MaterialDict.getInstance().load(config.material_data_path);
        SpecialGoodsDict.getInstance().load(config.special_goods_data_path);
        SpecialGoodsDict.getInstance().loadGiftBagData(config.gift_bag_data_path);
        TaskGoodsDict.getInstance().load(config.task_goods_data_path);
        ExchangeGoodsDict.getInstance().load(config.exchange_goods_data_path);
        WorldLegacyDict.getInstance().load(config);
        ChangeVocationToolsDict.getInstance().load(
                config.change_vocation_tool_data_path);
        MonsterLegacyManager.getInstance().startMonitor();
        GoodsDAO.load();
    }
    
    /**
     * 返回指定强化等级的单颗宝石UI展示效果
     * @return
     */
    public short[] getYetSetJewel(byte _level)
    {
    	short[] view = {-1, -1};
    	if(_level == 0)
    	{
    		return view;
    	}
    	view = config.yet_set_jewel[_level -1];
    	return view;
    }
    
    /**
     * 设置特定的特殊物品可用编号
     * 
     * @param _id
     */
    public void setUseableSpecailID (int _id)
    {
    	USABLE_SPECAIL_GOODS_ID = _id;
    }
    
//    /**
//     * 特定的特殊物品可用编号 +1
//     */
//    public void useableSpecailIDAdd ()
//    {
//    	USABLE_SPECAIL_GOODS_ID += 1;
//    }
    /**
     * 获得特定的特殊物品编号
     * @return
     */
    public int getUseableSpecailID()
    {
    	return USABLE_SPECAIL_GOODS_ID += 1;
    }
    
    /**
     * 返回指定发光等级的展示效果
     * @return
     */
    public short[] getFlashView(byte _flashLevel)
    {
    	short[] view = new short[2];
    	if(_flashLevel == 0)
    	{
    		return view;
    	}
    	view = config.shine_flash_view[_flashLevel -1];
    	return view;
    }
    /**
     * 返回指定发光等级的非武器装备展示效果
     * @param _flashLevel
     * @return
     */
    public short[] getArmorFlashView(byte _flashLevel)
    {
    	short[] view = new short[2];
    	if(_flashLevel == 0)
    	{
    		return view;
    	}
    	view = config.armor_shine_flash_view[_flashLevel -1];
    	return view;
    }

    @Override
    public void createSession (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);
        log.debug("userid= "+_session.userID+" ,goodsserviceImpl player = " + player);
        if (null == player.getInventory())
        {
            player.setInventory(new Inventory(player.getUserID(),
                    player.bagSizes));
            GoodsDAO.loadPlayerGoods(player);
        }
    }

    @Override
    public void sessionFree (Session _session)
    {
        if (null != _session.nickName)
            WarehouseDict.getInstance().releaseWarehouseByNickname(
                    _session.nickName);
    }

    /**
     * 销毁角色随身携带装备
     * 
     * @param _player 玩家
     * @param _package 装备包
     * @param _ei 装备实例
     * @param _loction 位置
     * @param _cause 原因
     * @return 是否销毁成功
     * @throws BagException
     */
    public boolean diceEquipmentOfBag (HeroPlayer _player,
            EquipmentContainer _package, EquipmentInstance _ei,
            LoctionLog _loction, CauseLog _cause) throws BagException
    {
        boolean result = -1 != _package.remove(_ei)
                && GoodsDAO.diceEquipment(_ei.getInstanceID());
        if (result)
        {
            LogServiceImpl.getInstance().goodsChangeLog(_player,
                    _ei.getArchetype(), 1, _loction, FlowLog.LOSE, _cause);
        }
        return result;
    }
    /**
     * 销毁宠物随身携带装备
     * @param pet 宠物
     * @param _player 玩家
     * @param _package 装备包
     * @param _ei 装备实例
     * @param _loction 位置
     * @param _cause 原因
     * @return 是否销毁成功
     * @throws BagException
     */
    public boolean diceEquipmentOfBag (Pet pet,HeroPlayer player,
            EquipmentContainer _package, EquipmentInstance _ei,
            LoctionLog _loction, CauseLog _cause) throws BagException
    {
        boolean result = -1 != _package.remove(_ei)
                && GoodsDAO.diceEquipment(_ei.getInstanceID());
        if (result)
        {
            LogServiceImpl.getInstance().goodsChangeLog(player,
                    _ei.getArchetype(), 1, _loction, FlowLog.LOSE, _cause);
        }
        return result;
    }

    /**
     * 从背包中移除装备
     * 
     * @param _package 装备包
     * @param _ei 装备实例
     * @return 移除的装备在背包中的位置
     * @throws BagException
     */
    public int removeEquipmentOfBag (HeroPlayer _player,
            EquipmentContainer _package, EquipmentInstance _ei, CauseLog _cause)
            throws BagException
    {
        if (GoodsDAO.removeEquipmentOfBag(_ei.getInstanceID()))
        {
            int index = _package.remove(_ei);
            if (index >= 0)
            {
                LogServiceImpl.getInstance().goodsChangeLog(_player,
                        _ei.getArchetype(), 1, LoctionLog.BAG, FlowLog.LOSE,
                        _cause);
            }
            return index;
        }
        return -1;
    }

    /**
     * 从背包中移除装备
     * 
     * @param _package 装备包
     * @param _ei 装备实例
     * @param _bagGrid 装备在背包中的位置
     * @return 移除的装备
     * @throws BagException
     */
    public EquipmentInstance removeEquipmentOfBag (HeroPlayer _player,
            EquipmentContainer _package, int _bagGrid, CauseLog _cause)
            throws BagException
    {
        EquipmentInstance equipment = _package.remove(_bagGrid);

        if (null != equipment)
        {
            if (GoodsDAO.removeEquipmentOfBag(equipment.getInstanceID()))
            {
                LogServiceImpl.getInstance().goodsChangeLog(_player,
                        equipment.getArchetype(), 1, LoctionLog.BAG,
                        FlowLog.LOSE, _cause);
                return equipment;
            }
        }
        return null;
    }

    /**
     * 减少非装备物品
     * 
     * @param _player 玩家
     * @param _package 背包
     * @param _gridIndex 格子位置
     * @param _goods 物品
     * @param _number 数量
     * @param _cause
     * @return
     * @throws BagException
     */
    public boolean reduceSingleGoods (HeroPlayer _player,
            SingleGoodsBag _package, int _gridIndex, Goods _goods, int _number,
            CauseLog _cause) throws BagException
    {
        short[] change = _package.remove(_gridIndex, _goods.getID(), _number);

        boolean result = false;
        if (null != change)
        {
            if (0 == change[1])
            {
                result = GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(),
                        (short) _gridIndex, _goods.getID());
            }
            else
            {
                result = GoodsDAO.updateGridSingleGoodsNumberOfBag(_player
                        .getUserID(), _goods.getID(), change[1],
                        (short) _gridIndex);
            }
        }

        if (result)
        {
            // 玩家物品变更日志
            LogServiceImpl.getInstance().goodsChangeLog(_player, _goods,
                    _number, LoctionLog.BAG, FlowLog.LOSE, _cause);
        }

        return result;

    }

    /**
     * 减少非装备物品
     * 
     * @param _player 玩家
     * @param _package 背包
     * @param _gridIndex 格子位置
     * @param _goodsID 物品ID
     * @param _number 数量
     * @param _cause 原因
     * @return
     * @throws BagException
     */
    public boolean reduceSingleGoods (HeroPlayer _player,
            SingleGoodsBag _package, int _gridIndex, int _goodsID, int _number,
            CauseLog _cause) throws BagException
    {
        Goods goods = GoodsContents.getGoods(_goodsID);
        return reduceSingleGoods(_player, _package, _gridIndex, goods, _number,
                _cause);
    }

    /**
     * 删除非装备物品
     * 
     * @param _player 玩家
     * @param _package 背包
     * @param _goods 物品
     * @param _number 数量
     * @param _cause 原因
     * @return 是否删除成功
     * @throws BagException
     */
    public boolean deleteSingleGoods (HeroPlayer _player,
            SingleGoodsBag _package, Goods _goods, int _number, CauseLog _cause)
            throws BagException
    {
        ArrayList<int[]> result = _package.remove(_goods.getID(),
                (short) _number);
        boolean[] flag = new boolean[result.size()];

        for (int i = 0; i < result.size(); i++)
        {
            int[] change = result.get(i);
            if (change[1] == 0)
            {
                flag[i] = GoodsDAO.removeSingleGoodsFromBag(
                        _player.getUserID(), (short) change[0], _goods.getID());
            }
            else
            {
                flag[i] = GoodsDAO.updateGridSingleGoodsNumberOfBag(_player
                        .getUserID(), _goods.getID(), change[1],
                        (short) change[0]);
            }
        }
        for (boolean f : flag)
        {
            if (!f)
                return false;
        }

        // 玩家物品变更日志
        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number,
                LoctionLog.BAG, FlowLog.LOSE, _cause);

        return true;
    }

    /**
     * 删除非装备物品
     * 
     * @param _player 玩家
     * @param _package 背包
     * @param _goods 物品
     * @param _number 数量
     * @param _cause 原因
     * @return 是否删除成功
     * @throws BagException
     */
    public boolean deleteSingleGoods (HeroPlayer _player,
            SingleGoodsBag _package, int _goodsID, int _number, CauseLog _cause)
            throws BagException
    {
        Goods goods = GoodsContents.getGoods(_goodsID);
        return deleteSingleGoods(_player, _package, goods, _number, _cause);
    }

    /**
     * 删除非装备物品
     * 
     * @param _player 玩家
     * @param _package 背包
     * @param _goodsID 物品编号
     * @param _cause 原因
     * @return 是否删除成功
     * @throws BagException
     */
    public boolean deleteSingleGoods (HeroPlayer _player,
            SingleGoodsBag _package, int _goodsID, CauseLog _cause)
            throws BagException
    {
        Goods goods = GoodsContents.getGoods(_goodsID);
        return deleteSingleGoods(_player, _package, goods, _cause);
    }

    /**
     * 删除一个非装备物品
     * 
     * @param _player
     * @param _package
     * @param _goodsID
     * @return 是否删除成功
     * @throws BagException
     */
    public boolean deleteOne (HeroPlayer _player, SingleGoodsBag _package,
            int _goodsID, CauseLog _cause) throws BagException
    {
        short[] change = _package.removeOne(_goodsID);

        if (null != change)
        {

            Goods goods = GoodsContents.getGoods(_goodsID);
            // 物品变更日志
            LogServiceImpl.getInstance().goodsChangeLog(_player, goods, 1,
                    LoctionLog.BAG, FlowLog.LOSE, _cause);
            if (0 == change[1])
            {
                return GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(),
                        change[0], _goodsID);
            }
            else
            {
                return GoodsDAO.updateGridSingleGoodsNumberOfBag(_player
                        .getUserID(), _goodsID, change[1], change[0]);
            }
        }

        return false;
    }

    /**
     * 向实例表中插入装备实例
     * 
     * @param _ei 装备实例
     */
    public static EquipmentInstance buildEquipmentInstance (int _userID,
            int _equipmentID)
    {
        EquipmentInstance ei = EquipmentFactory.getInstance().build(_userID,
                _userID, _equipmentID);

        if (null != ei)
        {
            GoodsDAO.buildEquipmentInstance(ei);
        }

        return ei;
    }

    /**
	 * 删除非装备物品
	 * 
	 * @param _player 玩家
	 * @param _package 背包
	 * @param _goods 物品
	 * @param _cause 原因
	 * @return 是否删除成功
	 * @throws BagException
	 */
	public boolean deleteSingleGoods (HeroPlayer _player,
	        SingleGoodsBag _package, Goods _goods, CauseLog _cause)
	        throws BagException
	{
	    int num = _package.remove(_goods.getID());
	
	    if (GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), _goods
	            .getID()))
	    {
	        // 玩家物品变更日志
	        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, num,
	                LoctionLog.BAG, FlowLog.LOSE, _cause);
	        return true;
	    }
	
	    return false;
	}

	/**
     * 丢弃非装备物品
     * 
     * @param _player 玩家
     * @param _package 背包
     * @param _gridIndex 格子位置
     * @param _goodsID 物品编号
     * @param _cause 原因
     * @return 是否丢弃成功
     * @throws BagException
     */
    public boolean diceSingleGoods (HeroPlayer _player,
            SingleGoodsBag _package, int _gridIndex, int _goodsID,
            CauseLog _cause) throws BagException
    {
        if (GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(),
                (short) _gridIndex, _goodsID))
        {
            Goods goods = GoodsContents.getGoods(_goodsID);
            // 玩家物品变更日志
            int[] items = _package.getItemData(_gridIndex);
            int _num = 0;
            if (items != null)
                _num = items[1];
            LogServiceImpl.getInstance().goodsChangeLog(_player, goods, _num,
                    LoctionLog.BAG, FlowLog.LOSE, _cause);
            return _package.remove(_gridIndex, _goodsID);
        }

        return false;
    }

    /**
     * 因任务奖励添加物品
     * 
     * @param _player 玩家
     * @param _goods 物品
     * @param _number 数量
     * @param _cause 原因(日志用)
     * @return 在背包中的位置以及现有数量
     */
    public short[] addGoods2PackageByTask (HeroPlayer _player, Goods _goods,
            int _number, CauseLog _cause)
    {
        try
        {
            if (null != _goods && _number > 0)
            {
            	log.debug(" 添加物品 addGoods2Package  goodstype= " + _goods.getGoodsType());
                if (_goods.getGoodsType() == EGoodsType.EQUIPMENT)
                {
                	log.debug(" 给玩家添加装备");
                    Equipment e = (Equipment) _goods;

                    EquipmentInstance ei = EquipmentFactory.getInstance()
                            .build(_player.getUserID(), _player.getUserID(), e);
                    //add by zhengl; date: 2011-02-21; note: 任务奖励都需要绑定
                    if(e.getBindType() == Equipment.BIND_TYPE_OF_WEAR) {
                    	ei.bind();
                    }
                    int gridInex = _player.getInventory().addEquipmentIns(ei);

                    if (-1 != gridInex)
                    {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei,
                                gridInex);

                        ChatQueue.getInstance().addGoodsMsg(_player, "获得了",
                                _goods.getName(),
                                _goods.getTrait().getViewRGB(), 1);

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                _goods, _number, LoctionLog.BAG, FlowLog.GET,
                                _cause);
                        return new short[]{(short) gridInex, 1 };
                    }
                } else if (_goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS){
                	log.debug(" 给宠物添加装备");
                	PetEquipment e = (PetEquipment) _goods;

                    EquipmentInstance ei = EquipmentFactory.getInstance()
                            .build(_player.getUserID(), _player.getUserID(), e);

                    int gridInex = _player.getInventory().addEquipmentIns(ei);

                    if (-1 != gridInex)
                    {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei,
                                gridInex);

                        ChatQueue.getInstance().addGoodsMsg(_player, "获得了",
                                _goods.getName(),
                                _goods.getTrait().getViewRGB(), 1);

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                _goods, _number, LoctionLog.BAG, FlowLog.GET,
                                _cause);
                        return new short[]{(short) gridInex, 1 };
                    }
                }
                else
                {
                    SingleGoods sg = (SingleGoods) _goods;

                    short[] change = _player.getInventory().addSingleGoods(sg,
                            _number);

                    if (null != change)
                    {
                        if (change[1] > _number)
                        {
                            GoodsDAO.updateGridSingleGoodsNumberOfBag(_player
                                    .getUserID(), _goods.getID(), change[1],
                                    change[0]);
                        }
                        else if (change[1] == _number)
                        {
                            GoodsDAO
                                    .addSingleGoods(_player.getUserID(),
                                            ((SingleGoods) _goods)
                                                    .getSingleGoodsType(),
                                            _goods.getID(), change[1],
                                            change[0]);
                        }

                        ChatQueue.getInstance().addGoodsMsg(_player, "获得了",
                                _goods.getName(),
                                _goods.getTrait().getViewRGB(), _number);

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                _goods, _number, LoctionLog.BAG, FlowLog.GET,
                                _cause);
                        return change;
                    }
                }
            }
        }
        catch (BagException e)
        {
            // TODO Auto-generated catch block
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(e.getMessage(), Warning.UI_STRING_TIP));
        }

        return null;
    }
    
    /**
     * 添加物品
     * 
     * @param _player 玩家
     * @param _goods 物品
     * @param _number 数量
     * @param _cause 原因(日志用)
     * @return 在背包中的位置以及现有数量
     */
    public short[] addGoods2Package (HeroPlayer _player, Goods _goods,
            int _number, CauseLog _cause)
    {
        try
        {
            if (null != _goods && _number > 0)
            {
            	log.debug(" 添加物品 addGoods2Package  goodstype= " + _goods.getGoodsType());
                if (_goods.getGoodsType() == EGoodsType.EQUIPMENT)
                {
                	log.debug(" 给玩家添加装备");
                    Equipment e = (Equipment) _goods;

                    EquipmentInstance ei = EquipmentFactory.getInstance()
                            .build(_player.getUserID(), _player.getUserID(), e);
                    
                    int gridInex = _player.getInventory().addEquipmentIns(ei);

                    if (-1 != gridInex)
                    {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei,
                                gridInex);

                        ChatQueue.getInstance().addGoodsMsg(_player, "获得了",
                                _goods.getName(),
                                _goods.getTrait().getViewRGB(), 1);

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                _goods, _number, LoctionLog.BAG, FlowLog.GET,
                                _cause);
                        return new short[]{(short) gridInex, 1 };
                    }
                } else if (_goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS){
                	log.debug(" 给宠物添加装备");
                	PetEquipment e = (PetEquipment) _goods;

                    EquipmentInstance ei = EquipmentFactory.getInstance()
                            .build(_player.getUserID(), _player.getUserID(), e);

                    int gridInex = _player.getInventory().addEquipmentIns(ei);

                    if (-1 != gridInex)
                    {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei,
                                gridInex);

                        ChatQueue.getInstance().addGoodsMsg(_player, "获得了",
                                _goods.getName(),
                                _goods.getTrait().getViewRGB(), 1);

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                _goods, _number, LoctionLog.BAG, FlowLog.GET,
                                _cause);
                        return new short[]{(short) gridInex, 1 };
                    }
                }
                else
                {
                    SingleGoods sg = (SingleGoods) _goods;

                    short[] change = _player.getInventory().addSingleGoods(sg,
                            _number);

                    if (null != change)
                    {
                        if (change[1] > _number)
                        {
                            GoodsDAO.updateGridSingleGoodsNumberOfBag(_player
                                    .getUserID(), _goods.getID(), change[1],
                                    change[0]);
                        }
                        else if (change[1] == _number)
                        {
                            GoodsDAO
                                    .addSingleGoods(_player.getUserID(),
                                            ((SingleGoods) _goods)
                                                    .getSingleGoodsType(),
                                            _goods.getID(), change[1],
                                            change[0]);
                        }
                        log.debug("add special goods name = " + sg.getName());
                        ChatQueue.getInstance().addGoodsMsg(_player, "获得了",
                                _goods.getName(),
                                _goods.getTrait().getViewRGB(), _number);

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                _goods, _number, LoctionLog.BAG, FlowLog.GET,
                                _cause);
                        return change;
                    }
                }
            }
        }
        catch (BagException e)
        {
            // TODO Auto-generated catch block
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(e.getMessage(), Warning.UI_STRING_TIP));
            log.debug("addGoods2Package error : ",e);
        }

        return null;
    }

    /**
     * 添加物品
     * 
     * @param _player 玩家
     * @param _goodsID 物品编号
     * @param _number 物品数量
     * @param _cause 原因(日志用)
     * @return 在背包中的位置以及现有数量
     */
    public short[] addGoods2Package (HeroPlayer _player, int _goodsID,
            int _number, CauseLog _cause)
    {
        Goods goods = GoodsContents.getGoods(_goodsID);

        return addGoods2Package(_player, goods, _number, _cause);
    }

    /**
     * 改变非装备物品所有者
     * 
     * @param _goods 物品
     * @param _master 拥有者
     * @param _bag 背包
     * @param _bagGridIndex 物品所在的背包位置
     * @param _number 改变的数量
     * @param _newMaster 新拥有者
     * @param _cause 原因(日志用)
     */
    public void changeSingleGoodsOwner (SingleGoods _goods, HeroPlayer _master,
            SingleGoodsBag _bag, int _bagGridIndex, int _number,
            HeroPlayer _newMaster, CauseLog _cause)
    {
        try
        {
            reduceSingleGoods(_master, _bag, _bagGridIndex, _goods, _number,
                    _cause);
            addGoods2Package(_newMaster, _goods, _number, _cause);

            ChatQueue.getInstance().addGoodsMsg(_newMaster, "获得了",
                    _goods.getName(), _goods.getTrait().getViewRGB(), _number);
        }
        catch (BagException pe)
        {

        }
    }

    /**
     * 改变装备所有者
     * 
     * @param _equipmentIns 装备实例
     * @param _master 原拥有者
     * @param _newMaster 新拥有者
     * @param _cause 原因
     * @return 是否操作成功
     */
    public boolean changeGoodsOwner (EquipmentInstance _equipmentIns,
            HeroPlayer _master, HeroPlayer _newMaster, CauseLog _cause)
    {
        try
        {
            if (-1 != _master.getInventory().getEquipmentBag().remove(
                    _equipmentIns))
            {
                int gridIndex = _newMaster.getInventory().addEquipmentIns(
                        _equipmentIns);

                ChatQueue.getInstance()
                        .addGoodsMsg(
                                _newMaster,
                                "获得了",
                                _equipmentIns.getArchetype().getName(),
                                _equipmentIns.getArchetype().getTrait()
                                        .getViewRGB(), 1);

                LogServiceImpl.getInstance().goodsChangeLog(_master,
                        _equipmentIns.getArchetype(), 1, LoctionLog.BAG,
                        FlowLog.LOSE, _cause);
                LogServiceImpl.getInstance().goodsChangeLog(_newMaster,
                        _equipmentIns.getArchetype(), 1, LoctionLog.BAG,
                        FlowLog.GET, _cause);

                return GoodsDAO.changeEquipmentOwner(_newMaster.getUserID(),
                        _equipmentIns.getInstanceID(), gridIndex);
            }
        }
        catch (BagException pe)
        {
            pe.printStackTrace();
        }

        return false;
    }

    /**
     * 添加装备实例
     * 
     * @param _player 玩家
     * @param _ei 装备实例
     * @param _cause 原因(日志用)
     * @return 是否添加成功
     */
    public short[] addEquipmentInstance2Bag (HeroPlayer _player,
            EquipmentInstance _ei, CauseLog _cause)
    {
        try
        {
            if (null != _ei)
            {
                int gridIndex = _player.getInventory().addEquipmentIns(_ei);

                if (-1 != gridIndex)
                {
                    GoodsDAO.addEquipment2Bag(_player.getUserID(), _ei,
                            gridIndex);

                    ChatQueue.getInstance().addGoodsMsg(_player, "你获得了",
                            _ei.getArchetype().getName(),
                            _ei.getArchetype().getTrait().getViewRGB(), 1);

                    // 物品变更日志
                    LogServiceImpl.getInstance().goodsChangeLog(_player,
                            _ei.getArchetype(), 1, LoctionLog.BAG, FlowLog.GET,
                            _cause);

                    return new short[]{(short) gridIndex, 1 };
                }
            }
        }
        catch (BagException e)
        {
            // TODO Auto-generated catch block
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(e.getMessage(), Warning.UI_STRING_TIP));
        }

        return null;
    }

    /**
     * 添加装备实例到玩家身上（原相应部位必须没有装备）
     * 
     * @param _player 玩家
     * @param _ei 装备实例
     * @param _cause 原因
     * @return 是否添加成功
     */
    public void addEquipmentInstance2Body (HeroPlayer _player,
            EquipmentInstance _ei, CauseLog _cause)
    {
        try
        {
            if (null != _ei)
            {
                _player.getBodyWear().wear(_ei);
                GoodsDAO.buildEquipment2Body(_player.getUserID(), _ei);

                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new ResponseEuipmentPackageChange(EBagType.BODY_WEAR
                                .getTypeValue(), _player.getBodyWear().indexOf(
                                _ei), _ei));
                ChatQueue.getInstance().addGoodsMsg(_player, "你获得了",
                        _ei.getArchetype().getName(),
                        _ei.getArchetype().getTrait().getViewRGB(), 1);

                LogServiceImpl.getInstance().goodsChangeLog(_player,
                        _ei.getArchetype(), 1, LoctionLog.BODY, FlowLog.GET,
                        _cause);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            LogWriter.error(this, e);
        }
    }

    /**
     * 根据物品编号获取物品模板
     * 
     * @param _goodsID
     * @return
     */
    public Goods getGoodsByID (int _goodsID)
    {
        return GoodsContents.getGoods(_goodsID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.ServiceAdaptor#clear(int)
     */
    public void clean (int _userID)
    {

    }
    
    public void searchUpgradeBag (HeroPlayer _player, byte _bagType)
    {
        int upgradeTimes = 0;
        String bagName = "";
        Inventory inventory = _player.getInventory();
        EBagType bagType = EBagType.getBagType(_bagType);
        switch (bagType)
        {
	        case TASK_TOOL_BAG:
	        {
	            upgradeTimes = (inventory.getTaskToolBag().getSize() - Inventory.GENERIC_BAG_GRID_SIZE) / 8 + 1;
	            bagName = GoodsServiceImpl.getInstance().getConfig().task_tool_bag_tab_name;
	            break;
	        }
            case EQUIPMENT_BAG:
            {
                upgradeTimes = (inventory.getEquipmentBag().getSize() - Inventory.GENERIC_BAG_GRID_SIZE) / 8 + 1;
                bagName = GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name;
                break;
            }
            case MEDICAMENT_BAG:
            {
                upgradeTimes = (inventory.getMedicamentBag().getSize() - Inventory.GENERIC_BAG_GRID_SIZE) / 8 + 1;
                bagName = GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name;
                break;
            }
            case MATERIAL_BAG:
            {
                upgradeTimes = (inventory.getMaterialBag().getSize() - Inventory.MATERIAL_BAG_GRID_SIZE) / 8 + 1;
                bagName = GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name;
                break;
            }
            case SPECIAL_GOODS_BAG:
            {
                upgradeTimes = (inventory.getSpecialGoodsBag().getSize() - Inventory.GENERIC_BAG_GRID_SIZE) / 8 + 1;
                bagName = GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name;
                break;
            }
            case PET_EQUIPMENT_BAG:
            {
            	upgradeTimes = (inventory.getPetEquipmentBag().getSize() - Inventory.STEP_GRID_SIZE) / 8 + 1;
            	bagName = "宠物";
                break;
            }
            case PET_GOODS_BAG:
            {
                upgradeTimes = (inventory.getPetGoodsBag().getSize() - Inventory.STEP_GRID_SIZE) / 8 + 1;
                bagName = "宠物";
                break;
            }
            case PET_BAG:
            {
                upgradeTimes = (inventory.getPetContainer().getSize() - Inventory.STEP_GRID_SIZE) / 8 + 1;
                bagName = "宠物";
                break;
            }
            case STORAGE_BAG:
            {
                Warehouse warehouse = WarehouseDict.getInstance().getWarehouseByNickname(
                		_player.getName());
		        byte level = warehouse.getLevel();
                upgradeTimes = level + 1;
                bagName = "仓库";
            	break;
            }
            default:
            {
            	log.info("获得无法匹配的类型");
            	break;
            }
        }
        if (upgradeTimes <= 3) {
//        	upgradeTimes += 1;
//        	int fee = upgradeTimes * upgradeTimes * BASE_UPGRADE_POINT;
            int fee = upgradeTimes * BASE_UPGRADE_POINT;
        	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
        			new UpgradeBagAnswer(
        					Tip.TIP_UPGRADE_BAG_FEE
        						.replaceAll("%fn", bagName)
        						.replaceAll("%fx", String.valueOf(upgradeTimes))
        						.replaceAll("%fy", String.valueOf(fee)), 
        					_bagType));
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_BAG_IS_MAX, Warning.UI_STRING_TIP));
		}
    }

    /**
     * 升级背包
     * 
     * @param _player
     * @param _bagType
     */
    public void upgradeBag (HeroPlayer _player, byte _bagType)
    {
        int upgradeTimes = 0;
        boolean upgradeSuccessful = false;
        Inventory inventory = _player.getInventory();
        EBagType bagType = EBagType.getBagType(_bagType);

        switch (bagType)
        {
            case EQUIPMENT_BAG:
            {
                upgradeTimes = (inventory.getEquipmentBag().getSize() - Inventory.GENERIC_BAG_GRID_SIZE) / 8 + 1;

                break;
            }
            case MEDICAMENT_BAG:
            {
                upgradeTimes = (inventory.getMedicamentBag().getSize() - Inventory.GENERIC_BAG_GRID_SIZE) / 8 + 1;

                break;
            }
            case MATERIAL_BAG:
            {
                upgradeTimes = (inventory.getMaterialBag().getSize() - Inventory.MATERIAL_BAG_GRID_SIZE) / 8 + 1;

                break;
            }
            case SPECIAL_GOODS_BAG:
            {
                upgradeTimes = (inventory.getSpecialGoodsBag().getSize() - Inventory.GENERIC_BAG_GRID_SIZE) / 8 + 1;

                break;
            }
            case PET_EQUIPMENT_BAG:
            {
            	upgradeTimes = (inventory.getPetEquipmentBag().getSize() - Inventory.STEP_GRID_SIZE) / 8 + 1;

                break;
            }
            case PET_GOODS_BAG:
            {
                upgradeTimes = (inventory.getPetGoodsBag().getSize() - Inventory.STEP_GRID_SIZE) / 8 + 1;

                break;
            }
            case PET_BAG:
            {
                upgradeTimes = (inventory.getPetContainer().getSize() - Inventory.STEP_GRID_SIZE) / 8 + 1;

                break;
            }
            case STORAGE_BAG:
            {
                Warehouse warehouse = WarehouseDict.getInstance().getWarehouseByNickname(
                		_player.getName());
                upgradeTimes = warehouse.getLevel() + 1;
            }
        }

        if (0 < upgradeTimes && upgradeTimes <= 3)
        {
            BagExpan bagExpan = (BagExpan)GoodsContents.getGoods(config.getSpecialConfig().bag_expan_goods_id);
            if(bagExpan == null){//如果没找到这个物品，因为这个道具是不在玩家背包里显示的，所以只提示玩家扩展失败
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning(Tip.TIP_BAG_EXPAN_ERROR,Warning.UI_TOOLTIP_TIP));
                return;
            }
//            if(ChargeServiceImpl.getInstance().reducePoint(_player,upgradeTimes * upgradeTimes * BASE_UPGRADE_POINT,
//                    config.getSpecialConfig().bag_expan_goods_id,
//                    bagExpan.getName(),1, ServiceType.BAG_EXPAN))
            if(ChargeServiceImpl.getInstance().reducePoint(_player,upgradeTimes * BASE_UPGRADE_POINT,
                    config.getSpecialConfig().bag_expan_goods_id,
                    bagExpan.getName(),1, ServiceType.BAG_EXPAN))
            {
                switch (bagType)
                {
                    case EQUIPMENT_BAG:
                    {
                        if (!inventory.getEquipmentBag().upgrade())
                        {
                            return;
                        }

                        upgradeSuccessful = true;

                        break;
                    }
                    case MEDICAMENT_BAG:
                    {
                        if (!inventory.getMedicamentBag().upgrade())
                        {
                            return;
                        }

                        upgradeSuccessful = true;

                        break;
                    }
                    case MATERIAL_BAG:
                    {
                        if (!inventory.getMaterialBag().upgrade())
                        {
                            return;
                        }

                        upgradeSuccessful = true;

                        break;
                    }
                    case SPECIAL_GOODS_BAG:
                    {
                        if (!inventory.getSpecialGoodsBag().upgrade())
                        {
                            return;
                        }

                        upgradeSuccessful = true;

                        break;
                    }
                    case PET_EQUIPMENT_BAG:
                    {
                    	if(!inventory.getPetEquipmentBag().upgrade())
                    	{
                    		return;
                    	}
                    	upgradeSuccessful = true;
                    	break;
                    }
                    case PET_GOODS_BAG:
                    {
                        if(!inventory.getPetGoodsBag().upgrade())
                        {
                            return;
                        }
                        upgradeSuccessful = true;
                    	break;
                    }
                    case PET_BAG:
                    {
                        if(!inventory.getPetContainer().upgrade())
                        {
                            return;
                        }
                        upgradeSuccessful = true;
                    	break;
                    }
                    case STORAGE_BAG:
                    {
                        Warehouse warehouse = WarehouseDict.getInstance().getWarehouseByNickname(
                        		_player.getName());
                        byte level = warehouse.getLevel();
        		        if (level >= Warehouse.MAX_LEVEL)
        		        {
        		            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
        		                    new Warning(Tip.TIP_NPC_ALREADY_HIGH_LVL));
        		        }
                    	upgradeSuccessful = true;
                    	warehouse.upLevel();
                    	break;
                    }
                }

                if (upgradeSuccessful)
                {
                    GoodsDAO.updatePlayerBagSize(_player);
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new SendBagSize(inventory.getEquipmentBag()
                                    .getSize(), inventory.getMedicamentBag()
                                    .getSize(), inventory.getMaterialBag()
                                    .getSize(), inventory.getSpecialGoodsBag()
                                    .getSize(),inventory.getPetEquipmentBag().getSize(),
                                    inventory.getPetContainer().getSize(),
                                    inventory.getPetGoodsBag().getSize()));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(bagType.getDescription() + " 成功扩容，请回相应包裹查看", 
                            		Warning.UI_STRING_TIP));
                }
            }
            else
            {
            	//eidt by zhengl; date: 2011-03-16; note: 提示不走TIP
            	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
            			new UpgradeBagAnswer(
            					"游戏点数不够：" + upgradeTimes * upgradeTimes * BASE_UPGRADE_POINT + ",请到商城充值", 
            					UpgradeBagAnswer.SEND_TO_CHARGE));
            }
        }
        else
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning("背包已是最大容量", Warning.UI_STRING_TIP));
        }
    }

    /**
     * 处理战斗中装备耐久度的变化
     * 
     * @param _attacker 攻击者
     * @param _target 被攻击者
     */
    public void processEquipmentDurabilityInFighting (ME2GameObject _attacker,
            ME2GameObject _target)
    {
        HeroPlayer player;
        ArrayList<EquipmentInstance> equipmentListThatNeedUpdate = null;

        if (_attacker.getObjectType() == EObjectType.PLAYER)
        {
            player = (HeroPlayer) _attacker;

            EquipmentInstance weapon = player.getBodyWear().getWeapon();

            if (null != weapon)
            {
                int durabilityPointBeforeChange = 0, durabilityPointNow = 0;

                durabilityPointBeforeChange = weapon
                        .getCurrentDurabilityPoint();

                if (durabilityPointBeforeChange > 0)
                {
                    weapon.reduceCurrentDurabilityPoint(1);
                    durabilityPointNow = weapon.getCurrentDurabilityPoint();

                    if (durabilityPointBeforeChange > durabilityPointNow)
                    {
                        equipmentListThatNeedUpdate = new ArrayList<EquipmentInstance>();
                        equipmentListThatNeedUpdate.add(weapon);

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new RefreshEquipmentDurabilityPoint(player
                                        .getBodyWear()));

                        if (durabilityPointNow == 0)
                        {
                            PlayerServiceImpl.getInstance()
                                    .reCalculateRoleProperty(player);
                            PlayerServiceImpl.getInstance()
                                    .refreshRoleProperty(player);
                        }
                    }
                }
            }
        }

        if (_target.getObjectType() == EObjectType.PLAYER)
        {
            player = (HeroPlayer) _target;

            boolean durabilityChanged = false;
            boolean needRecalculteProperty = false;
            int durabilityPointBeforeChange = 0, durabilityPointNow = 0;

            for (EquipmentInstance ei : player.getBodyWear().getEquipmentList())
            {
                if (null != ei
                        && ei.getArchetype().getEquipmentType() == Equipment.TYPE_ARMOR)
                {
                    durabilityPointBeforeChange = ei
                            .getCurrentDurabilityPoint();

                    if (durabilityPointBeforeChange > 0)
                    {
                        ei.reduceCurrentDurabilityPoint(1);
                        durabilityPointNow = ei.getCurrentDurabilityPoint();

                        if (durabilityPointBeforeChange > durabilityPointNow)
                        {
                            if (null == equipmentListThatNeedUpdate)
                            {
                                equipmentListThatNeedUpdate = new ArrayList<EquipmentInstance>();
                            }

                            equipmentListThatNeedUpdate.add(ei);
                            durabilityChanged = true;

                            if (durabilityPointNow == 0)
                            {
                                needRecalculteProperty = true;
                            }
                        }
                    }
                }
            }

            if (durabilityChanged)
            {
                ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),
                        new RefreshEquipmentDurabilityPoint(player
                                .getBodyWear()));
                GoodsDAO.updateEquipmentDurability(equipmentListThatNeedUpdate);

            }

            if (needRecalculteProperty)
            {
                PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
                PlayerServiceImpl.getInstance().refreshRoleProperty(player);
            }
        }
    }

    /**
     * 修复装备耐久度
     * 
     * @param _equipmentInsList
     */
    public void restoreEquipmentDurability (
            ArrayList<EquipmentInstance> _equipmentInsList)
    {
        for (EquipmentInstance ei : _equipmentInsList)
        {
            ei.beRepaired();
        }

        GoodsDAO.updateEquipmentDurability(_equipmentInsList);
    }

    /**
     * 自动出售劣质物品（仅限装备和材料）
     * 
     * @param _player
     */
    public int autoSellMAE (HeroPlayer _player)
    {
        EGoodsTrait sellGoodsTrait = _player.getAutoSellTrait();

        if (null != sellGoodsTrait)
        {
            int money = 0;

            if (_player.getInventory().getEquipmentBag().getFullGridNumber() > 0)
            {
                EquipmentInstance[] equipmentList = _player.getInventory()
                        .getEquipmentBag().getEquipmentList();

                EquipmentInstance ei;

                for (int i = 0; i < equipmentList.length; i++)
                {
                    ei = equipmentList[i];

                    if (null != ei
                            && ei.getArchetype().getTrait().value() <= sellGoodsTrait
                                    .value())
                    {
                        try
                        {
                            if (ei == removeEquipmentOfBag(_player, _player
                                    .getInventory().getEquipmentBag(), i,
                                    CauseLog.SALE))
                            {
                                money += CEService
                                        .sellPriceOfEquipment(
                                                ei.getArchetype()
                                                        .getSellPrice(),
                                                ei.getCurrentDurabilityPoint(),
                                                ei
                                                        .getArchetype()
                                                        .getMaxDurabilityPoint());

                            }
                        }
                        catch (BagException be)
                        {
                            be.printStackTrace();
                        }
                    }
                }
            }

            if (_player.getInventory().getMaterialBag().getFullGridNumber() > 0)
            {
                int[][] materialDataList = _player.getInventory()
                        .getMaterialBag().getAllItem();

                Goods material;

                for (int i = 0; i < materialDataList.length; i++)
                {
                    if (0 != materialDataList[i][0])
                    {
                        material = GoodsContents
                                .getGoods(materialDataList[i][0]);

                        if (null != material
                                && material.getTrait().value() <= sellGoodsTrait
                                        .value())
                        {
                            try
                            {
                                int number = materialDataList[i][1];

                                if (GoodsServiceImpl
                                        .getInstance()
                                        .diceSingleGoods(
                                                _player,
                                                _player.getInventory()
                                                        .getMaterialBag(), i,
                                                material.getID(), CauseLog.SALE))
                                {
                                    money += material.getRetrievePrice()
                                            * number;
                                }
                            }
                            catch (BagException be)
                            {
                                be.printStackTrace();
                            }
                        }
                    }
                }
            }

            PlayerServiceImpl.getInstance().addMoney(_player, money, 1,
                    PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING, "自动出售劣质物品");

            return money;
        }

        return 0;
    }

    /**
     * 处理死亡后装备耐久度的变化
     * 
     * @param _dier 死者
     */
    public void processEquipmentDurabilityAfterDie (HeroPlayer _dier)
    {
        boolean durabilityChanged = false;
        boolean needRecalculteProperty = false;
        int durabilityPointBeforeChange = 0, durabilityPointNow = 0;
        ArrayList<EquipmentInstance> equipmentListThatNeedUpdate = null;

        for (EquipmentInstance ei : _dier.getBodyWear().getEquipmentList())
        {
            if (null != ei)
            {
                durabilityPointBeforeChange = ei.getCurrentDurabilityPoint();

                if (durabilityPointBeforeChange > 0)
                {
                    ei.reduceCurrentDurabilityPercent(5);

                    durabilityPointNow = ei.getCurrentDurabilityPoint();

                    if (durabilityPointBeforeChange > durabilityPointNow)
                    {
                        durabilityChanged = true;

                        if (null == equipmentListThatNeedUpdate)
                        {
                            equipmentListThatNeedUpdate = new ArrayList<EquipmentInstance>();
                        }

                        equipmentListThatNeedUpdate.add(ei);

                        if (durabilityPointNow == 0)
                        {
                            needRecalculteProperty = true;
                        }
                    }
                }
            }
        }

        if (durabilityChanged)
        {
            ResponseMessageQueue.getInstance().put(_dier.getMsgQueueIndex(),
                    new RefreshEquipmentDurabilityPoint(_dier.getBodyWear()));
            GoodsDAO.updateEquipmentDurability(equipmentListThatNeedUpdate);
        }

        if (needRecalculteProperty)
        {
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_dier);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_dier);
        }
    }

    /**
     * 发送地图上可见怪物掉落的箱子列表
     * 
     * @param _map
     * @param _playerUserID
     */
    public void sendLegacyBoxList (Map _map, HeroPlayer _player)
    {
        ArrayList<MonsterLegacyBox> legacyBoxList = new ArrayList<MonsterLegacyBox>();

        MonsterLegacyBox monsterLegacyBox;

        for (int i = 0; i < _map.getLegacyBoxList().size(); i++)
        {
            monsterLegacyBox = _map.getLegacyBoxList().get(i);

            if (monsterLegacyBox instanceof PersonalPickerBox)
            {
                if (monsterLegacyBox.getPickerUserID() == _player.getUserID())
                {
                    legacyBoxList.add(monsterLegacyBox);
                }
            }
            else
            {
                if (((RaidPickerBox) monsterLegacyBox).containsVisibler(_player
                        .getUserID()))
                {
                    legacyBoxList.add(monsterLegacyBox);
                }
            }
        }

        if (legacyBoxList.size() > 0)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new SendLegacyBoxList(legacyBoxList, _player.getUserID()));
        }
    }

    /**
     * 背包物品原型
     * 
     * @param _inventory
     * @param _goodsType
     * @param _bagIndex
     * @return
     */
    public Goods bagGoodsModel (Object _container, EGoodsType _goodsType,
            int _bagIndex)
    {
        switch (_goodsType)
        {
            case EQUIPMENT:
            {
                EquipmentContainer ec = (EquipmentContainer) _container;

                EquipmentInstance ei = ec.get(_bagIndex);

                if (null != ei)
                {
                    return ei.getArchetype();
                }

                break;
            }
            default:
            {
                SingleGoodsBag sb = (SingleGoodsBag) _container;

                int[] goodsInfo = sb.getItemData(_bagIndex);

                if (null != goodsInfo && 0 != goodsInfo[0])
                {
                    return GoodsContents.getGoods(goodsInfo[0]);
                }

                break;
            }
        }

        return null;
    }
    
    /**
     * 对比穿戴装备时候穿上和脱下的2件装备是否有外观区别
     * @param _uei
     * @param _ei
     * @return  true=有区别;false=没区别
     */
    public boolean changeEquimentViewDifference(EquipmentInstance _uei, EquipmentInstance _ei)
    {
    	boolean result = false;
    	boolean pngDifference = true;
    	boolean flashDifference = true;
    	if (_uei != null) 
    	{
			if(_uei.getGeneralEnhance().getFlashLevel() == _ei.getGeneralEnhance().getFlashLevel())
			{
				pngDifference = false;//没区别
			}
			if (_uei.getArchetype().getImageID() == _ei.getArchetype().getImageID()) 
			{
				flashDifference = false;//没区别
			}
			if(pngDifference || flashDifference)
			{
				result = true;
			}
		}
    	else 
    	{
    		result = true;
		}
    	
    	return result;
    }

    /**
     * 获取玩家特殊背包里的天书
     * @param _player
     * @return
     */
    public List<HeavenBook> getPlayerSepcialBagHeavenBooks(HeroPlayer _player){
        List<HeavenBook> heavenBookList = new ArrayList<HeavenBook>();
        SingleGoodsBag singleGoodsBag = _player.getInventory().getSpecialGoodsBag();
        for(int i=0; i<singleGoodsBag.getAllItem().length; i++){
            if(singleGoodsBag.getAllItem()[i][0] > 0){
                SpecialGoods goods = (SpecialGoods)getGoodsByID(singleGoodsBag.getAllItem()[i][0]);
                if(goods.getType() == ESpecialGoodsType.HEAVEN_BOOK){
                    heavenBookList.add((HeavenBook)goods);
                }
            }
        }
        return heavenBookList;
    }

    /**
     * 基础升级需要的点数
     */
    private static final short BASE_UPGRADE_POINT = 20;
}
