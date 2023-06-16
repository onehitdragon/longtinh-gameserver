package hero.npc.function.system;

import hero.expressions.service.CEService;
import hero.item.Armor;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.Material;
import hero.item.Medicament;
import hero.item.SpecialGoods;
import hero.item.Weapon;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.expand.ExpandGoods;
import hero.item.expand.SellGoods;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.ESpecialGoodsType;
import hero.log.service.CauseLog;
import hero.log.service.LoctionLog;
import hero.log.service.LogServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.player.message.HotKeySumByMedicament;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.ui.UI_GoodsListWithOperation;
import hero.ui.UI_GridGoodsNumsChanged;
import hero.ui.UI_InputDigidal;

import java.util.ArrayList;
import java.util.Hashtable;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Trade.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:33:23
 * @描述 ：商人（出售物品、回收物品、修理装备）
 */

public class Trade extends BaseNpcFunction
{
	private static Logger log = Logger.getLogger(Trade.class);
    /**
     * 顶层操作菜单列表
     */
	private static final String[]      mainMenuList            = {"交易"};
//    private static final String[]      mainMenuList            = {"购买", "出售" };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]       mainMenuMarkImageIDList = {1004, 1004 };

    /**
     * 购买装备操作菜单列表
     */
    private static final String[]      buyEquipmentMenuList    = {"购买单个" };

    /**
     * 出售操作菜单列表
     */
    private static final String[]      sellMenuList            = {"卖　　出" };

    /**
     * 购买非装备物品操作菜单列表
     */
    private static final String[]      buySingleGoodsMenuList  = {"购买单个", "购买多个" };

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] optionListData          = new ArrayList[2];

    /**
     * 购买物品输入数量的提示
     */
    private static final String        buyGoodsNumberTip       = "请输入购买数量";

    /**
     * 出售的物品类型
     */
    private EGoodsType                 sellGoodsType;

    /**
     * 出售物品列表
     */
    private ArrayList<ExpandGoods>     sellGoodsList;
    
    private Hashtable<String, ArrayList<ExpandGoods>> 	 sellList;

    /**
     * @author DC
     */
    private static enum Step
    {
        TOP(1), BUY(2), SELL(3);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    /**
     * 构造
     * 
     * @param _npcID
     */
    public Trade(int _npcID, int[][] _goodsData)
    {
        super(_npcID);
        log.debug("trade NPC id  = " + _npcID);
        // TODO Auto-generated constructor stub
        sellGoodsType = EGoodsType.EQUIPMENT;
        initSellGoodsList(_goodsData);
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes(buyGoodsNumberTip));
        optionListData[0] = null;
        optionListData[1] = data;
    }

    /**
     * 初始化出售物品
     * 
     * @param _goodsData
     */
    private void initSellGoodsList (int[][] _goodsData)
    {
        sellGoodsList = new ArrayList<ExpandGoods>();
        sellList = new Hashtable<String, ArrayList<ExpandGoods>>();
        log.debug("initSellGoodsList  _goodsData[][] = " +  _goodsData);
        if (null != _goodsData && _goodsData.length > 0)
        {
            log.debug("initSellGoodsList _goodsData[0][0] = " + _goodsData[0][0]);
            Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
                    _goodsData[0][0]);
            
            if (null != goods)
            {
                sellGoodsType = goods.getGoodsType();
                log.debug("initSellGoodsList sellGoodsType = " + sellGoodsType);
            }
            else
            {
                sellGoodsList = null;

                return;
            }

            for (int i = 0; i < _goodsData.length; i++)
            {
                goods = GoodsServiceImpl.getInstance().getGoodsByID(
                        _goodsData[i][0]);
                if (null != goods)
                {
                    SellGoods sellGoods = new SellGoods(goods);

                    sellGoods.setOriginalSellGoodsNums(_goodsData[i][1]);
                    sellGoods.setTraceSellGoodsNums(_goodsData[i][1]);
                    String tab = "";
                    if (goods instanceof Weapon) {
                    	Weapon weapon = (Weapon)goods;
                    	tab = weapon.getWeaponType().getDesc();
					} else if (goods instanceof Armor) {
						Armor armor = (Armor)goods;
//                    	sell = new SellListStructure(
//                    			armor.getArmorType().getDesc(), 
//                    			sellGoods);
						tab = armor.getArmorType().getDesc();
					} else if (goods instanceof Medicament) {
                    	tab = GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name;
					} else if (goods instanceof Material) {
                    	tab = GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name;
					} else if (goods instanceof SpecialGoods) {
                    	tab = GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name;
					}
                    if(sellList.containsKey(tab)) {
                    	sellList.get(tab).add(sellGoods);
                    } else {
                    	sellList.put(tab, new ArrayList<ExpandGoods>());
                    	sellList.get(tab).add(sellGoods);
					}
                    sellGoodsList.add(sellGoods);
                }
                else
                {
                    sellGoodsList.clear();
                    sellGoodsList = null;

                    return;
                }
            }
            log.debug("initSellGoodsList size = " + sellGoodsList.size());
        }
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.TRADE;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
        for (int i = 0; i < mainMenuList.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = mainMenuList[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);
        }
    }

    /**
     * 获取剩余物品种类
     * 
     * @return
     */
    private int getTraceGoodsType ()
    {
        int types = 0;

        for (ExpandGoods sellGoods : sellGoodsList)
        {
            if (((SellGoods) sellGoods).getTraceSellGoodsNums() != 0)
            {
                types++;
            }
        }

        return types;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        return optionList;
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int _selectIndex,
            YOYOInputStream _content) throws Exception
    {
        if (Step.TOP.tag == _step)
        {
            AbsResponseMessage msg = null;
            
            log.debug("trade sellgoodstype = " + sellGoodsType);
            if (0 == _selectIndex)// 商人出售物品列表  TOP(1), BUY(2), SELL(3);
            {
                if(sellGoodsList == null || sellGoodsList.size() == 0){
                    log.debug("该商人没有要卖的物品");
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                    		new Warning(Tip.TIP_NPC_NOT_HAVE_ITEM));
                    return;
                }
                if (EGoodsType.EQUIPMENT == sellGoodsType)
                {
                    byte[] goodsDatas = UI_GoodsListWithOperation.getBytes(
                                    buyEquipmentMenuList, 
                                    sellList,
                                    getTraceGoodsType());
                    msg = new NpcInteractiveResponse(getHostNpcID(), 
                    		optionList.get(_selectIndex).functionMark, 
                    		Step.BUY.tag,
                            goodsDatas);
                }
                else
                {
                    byte[] goodsDatas = UI_GoodsListWithOperation.getBytes(
                            buySingleGoodsMenuList, 
                            optionListData,
                            sellList, 
                            getTraceGoodsType());
                    msg = new NpcInteractiveResponse(getHostNpcID(), 
                    		optionList.get(_selectIndex).functionMark, 
                    		Step.BUY.tag,
                            goodsDatas);
                }
                if (null != msg)
                {
                	//NPC出售列表发送
                	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
                ArrayList<NpcInteractiveResponse> msgList = new ArrayList<NpcInteractiveResponse>();
                NpcInteractiveResponse data;
                //自身包裹数据列表
                data = new NpcInteractiveResponse(getHostNpcID(), 
                		optionList.get(_selectIndex).functionMark, 
                		Step.SELL.tag,
                        UI_GoodsListWithOperation.getData(sellMenuList,
                                _player.getInventory().getEquipmentBag(), 
                                GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                msgList.add(data);
                data = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(_selectIndex).functionMark, Step.SELL.tag,
                        UI_GoodsListWithOperation.getBytes(sellMenuList,
                                null, _player.getInventory().getMedicamentBag(), 
                                GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                msgList.add(data);
                data = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(_selectIndex).functionMark, Step.SELL.tag,
                        UI_GoodsListWithOperation.getBytes(sellMenuList,
                                null, _player.getInventory().getMaterialBag(), 
                                GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                msgList.add(data);
                data = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(_selectIndex).functionMark, Step.SELL.tag,
                        UI_GoodsListWithOperation.getBytes(sellMenuList,
                                null, _player.getInventory().getSpecialGoodsBag(), 
                                GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
                msgList.add(data);
                //发送多个包裹数据
                if(msgList.size() > 0)
                {
                	for (NpcInteractiveResponse bagData : msgList) {
                		ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), bagData);
					}
                }
            }

        }
        else if (Step.BUY.tag == _step)
        {
            byte optionIndex = _content.readByte(); //操作类型
            byte gridIndex = _content.readByte(); //格子位置
            int goodsID = _content.readInt(); //物品ID
            int nums = 1;

            if (1 == optionIndex)// 购买多个
            {
                nums = _content.readInt();

                if(nums<=0){
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("系统警告：交易的物品数量输入错误，系统已记录你的行为！！！",
                                Warning.UI_TOOLTIP_TIP));
                    LogServiceImpl.getInstance().numberErrorLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                            _player.getUserID(),_player.getName(),nums,"交易NPC购买物品,物品id["+goodsID+"]");

                    return;
                }
            }

            Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsID);

            int charge = goods.getSellPrice() * nums;

            if (charge <= _player.getMoney())
            {
                int spareGoodsNums = getGoodsTraceNums(goods);

                if (spareGoodsNums != -1)
                {
                    if (spareGoodsNums == -2 || spareGoodsNums == 0
                            || spareGoodsNums < nums)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning("该物品不存在或数量不够"));

                        return;
                    }
                }

                if (null != GoodsServiceImpl.getInstance().addGoods2Package(
                        _player, goods, (short) nums, CauseLog.BUY))
                {
                    boolean spareGoodsNumsChanged = sellGoods(goods, (short) nums);

                    if (spareGoodsNumsChanged)
                    {
                        AbsResponseMessage msg = null;

                        if (spareGoodsNums - nums > 0)
                        {
                            msg = new NpcInteractiveResponse(getHostNpcID(),
                                    optionList.get(_selectIndex).functionMark,
                                    Step.BUY.tag, UI_GridGoodsNumsChanged
                                            .getBytes(gridIndex, goodsID,
                                                    spareGoodsNums - nums));
                        }
                        else
                        {
                            if (EGoodsType.EQUIPMENT == sellGoodsType || EGoodsType.PET_EQUIQ_GOODS == sellGoodsType)
                            {
                                msg = new NpcInteractiveResponse(
                                        getHostNpcID(),
                                        optionList.get(_selectIndex).functionMark,
                                        Step.BUY.tag, UI_GoodsListWithOperation
                                                .getBytes(buyEquipmentMenuList,
                                                        sellList,
                                                        getTraceGoodsType()));
                            }
                            else
                            {
                                msg = new NpcInteractiveResponse(
                                        getHostNpcID(),
                                        optionList.get(_selectIndex).functionMark,
                                        Step.BUY.tag, UI_GoodsListWithOperation
                                                .getBytes(
                                                        buySingleGoodsMenuList,
                                                        optionListData,
                                                        sellList,
                                                        getTraceGoodsType()));
                            }
                        }

                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                msg);
                    }

                    String cause = "购买物品";
                    if(goods.getGoodsType() == EGoodsType.PET_GOODS
                            || goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
                    {
                        SpecialGoods specialGoods = (SpecialGoods)goods;
                        if(specialGoods.getType()  == ESpecialGoodsType.PET_FEED
                                || specialGoods.getType() == ESpecialGoodsType.CRYSTAL)
                        {
                        	cause = "购买"+specialGoods.getType();
                        }
                    }
                    //add by zhengl; date: 2011-03-24; note: 购买成功后要下发包裹更新通知.
                    NpcInteractiveResponse data = null;
                    if(goods.getGoodsType() == EGoodsType.EQUIPMENT) {
                    	data = new NpcInteractiveResponse(getHostNpcID(), 
                    			optionList.get(_selectIndex).functionMark, 
                    			Step.SELL.tag,
                    			UI_GoodsListWithOperation.getData(sellMenuList,
                    					_player.getInventory().getEquipmentBag(), 
                    					GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                    } else if(goods.getGoodsType() == EGoodsType.MEDICAMENT) {
		                data = new NpcInteractiveResponse(getHostNpcID(), optionList
		                        .get(_selectIndex).functionMark, Step.SELL.tag,
		                        UI_GoodsListWithOperation.getBytes(sellMenuList,
		                                null, _player.getInventory().getMedicamentBag(), 
		                                GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
					} else if(goods.getGoodsType() == EGoodsType.MATERIAL) {
		                data = new NpcInteractiveResponse(getHostNpcID(), optionList
		                        .get(_selectIndex).functionMark, Step.SELL.tag,
		                        UI_GoodsListWithOperation.getBytes(sellMenuList,
		                                null, _player.getInventory().getMaterialBag(), 
		                                GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
					} else if(goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
		                data = new NpcInteractiveResponse(getHostNpcID(), optionList
		                        .get(_selectIndex).functionMark, Step.SELL.tag,
		                        UI_GoodsListWithOperation.getBytes(sellMenuList,
		                                null, _player.getInventory().getSpecialGoodsBag(), 
		                                GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
					}
                    if (data != null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data);
					}
                    PlayerServiceImpl.getInstance().addMoney(_player, -charge,
                            1, PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                            cause);
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning("你的金钱不够"));
            }
        }
        else if (Step.SELL.tag == _step)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            Goods goods = GoodsContents.getGoods(goodsID);
            //edit by zhengl; date: 2011-04-02; note: 装备发的是实例ID,非装备发的是模板ID.为此容错
            EquipmentInstance ei = null;
        	ei = _player.getInventory().getEquipmentBag().getEquipmentList()[gridIndex];
            if(ei != null && ei.getInstanceID() == goodsID)
            {
            	goods = ei.getArchetype();
            }
            if (goods == null) 
            {
				LogWriter.error("error:由goodsID="+String.valueOf(goodsID)
						+ ",optionIndex=" + optionIndex 
						+ ",gridIndex=" + gridIndex + "获得物品为null", 
						null);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning("操作失败"));
				return;
			}
            
            if (0 == optionIndex)
            {
            	//edit by zhengl; date: 2011-03-22; note: 出售物品不再限制.
//                if (EGoodsType.EQUIPMENT == sellGoodsType)
            	if(EGoodsType.EQUIPMENT == goods.getGoodsType())
                {
                    if (null != ei && ei.getInstanceID() == goodsID)
                    {
                        if (GoodsServiceImpl.getInstance().diceEquipmentOfBag(
                                _player,
                                _player.getInventory().getEquipmentBag(), ei,
                                LoctionLog.BAG, CauseLog.SALE))
                        {
                            int money = CEService.sellPriceOfEquipment(ei
                                    .getArchetype().getSellPrice(), ei
                                    .getCurrentDurabilityPoint(), ei
                                    .getArchetype().getMaxDurabilityPoint());

                            PlayerServiceImpl
                                    .getInstance()
                                    .addMoney(
                                            _player,
                                            money,
                                            1,
                                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                            "出售物品");
                            NpcInteractiveResponse data = new NpcInteractiveResponse(getHostNpcID(), 
                        			optionList.get(_selectIndex).functionMark, 
                        			Step.SELL.tag,
                        			UI_GoodsListWithOperation.getData(sellMenuList,
                        					_player.getInventory().getEquipmentBag(), 
                        					GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data);
                        }

                    }
                }
                else if (EGoodsType.PET_EQUIQ_GOODS == goods.getGoodsType())
                {
                    ei = _player.getInventory().getPetEquipmentBag().getEquipmentList()[gridIndex];

                    if (null != ei && ei.getInstanceID() == goodsID)
                    {
                        if (GoodsServiceImpl.getInstance().diceEquipmentOfBag(
                                _player,
                                _player.getInventory().getPetEquipmentBag(), ei,
                                LoctionLog.BAG, CauseLog.SALE))
                        {
                            int money = CEService.sellPriceOfEquipment(ei
                                    .getArchetype().getSellPrice(), ei
                                    .getCurrentDurabilityPoint(), ei
                                    .getArchetype().getMaxDurabilityPoint());

                            PlayerServiceImpl
                                    .getInstance()
                                    .addMoney(
                                            _player,
                                            money,
                                            1,
                                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                            "出售物品");

                            ResponseMessageQueue
                                    .getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new NpcInteractiveResponse(
                                                    getHostNpcID(),
                                                    optionList
                                                            .get(_selectIndex).functionMark,
                                                    Step.SELL.tag,
                                                    UI_GridGoodsNumsChanged
                                                            .getBytes(
                                                                    gridIndex,
                                                                    goodsID, 0)));
                        }

                    }
                }
                else if (EGoodsType.MEDICAMENT == goods.getGoodsType())
                {
                    if (goodsID == _player.getInventory().getMedicamentBag()
                            .getAllItem()[gridIndex][0])
                    {
                        int money = GoodsServiceImpl.getInstance()
                                .getGoodsByID(goodsID).getRetrievePrice()
                                * _player.getInventory().getMedicamentBag()
                                        .getAllItem()[gridIndex][1];

                        if (GoodsServiceImpl.getInstance().diceSingleGoods(
                                _player,
                                _player.getInventory().getMedicamentBag(),
                                gridIndex, goodsID, CauseLog.SALE))
                        {
                            PlayerServiceImpl
                                    .getInstance()
                                    .addMoney(
                                            _player,
                                            money,
                                            1,
                                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                            "出售物品");

                            NpcInteractiveResponse data = new NpcInteractiveResponse(getHostNpcID(), optionList
    		                        .get(_selectIndex).functionMark, Step.SELL.tag,
    		                        UI_GoodsListWithOperation.getBytes(sellMenuList,
    		                                null, _player.getInventory().getMedicamentBag(), 
    		                                GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data);
                        }
                    }
                }
                else if (EGoodsType.MATERIAL == goods.getGoodsType())
                {
                    if (goodsID == _player.getInventory().getMaterialBag()
                            .getAllItem()[gridIndex][0])
                    {
                        int money = GoodsContents.getGoods(goodsID)
                                .getRetrievePrice()
                                * _player.getInventory().getMaterialBag()
                                        .getAllItem()[gridIndex][1];

                        if (GoodsServiceImpl.getInstance().diceSingleGoods(
                                _player,
                                _player.getInventory().getMaterialBag(),
                                gridIndex, goodsID, CauseLog.SALE))
                        {
                            PlayerServiceImpl
                                    .getInstance()
                                    .addMoney(
                                            _player,
                                            money,
                                            1,
                                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                            "出售物品");
                            NpcInteractiveResponse data = new NpcInteractiveResponse(getHostNpcID(), optionList
    		                        .get(_selectIndex).functionMark, Step.SELL.tag,
    		                        UI_GoodsListWithOperation.getBytes(sellMenuList,
    		                                null, _player.getInventory().getMaterialBag(), 
    		                                GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data);
                        }
                    }
                }
                else if (EGoodsType.SPECIAL_GOODS == goods.getGoodsType()) 
                {
                	ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                			new Warning(Tip.TIP_NPC_ITEM_NOT_TRADE));
				}
            }
        }
    }

    /**
     * 获取剩余物品数量
     * 
     * @param _goods
     * @return
     */
    private synchronized int getGoodsTraceNums (Goods _goods)
    {
        for (ExpandGoods goods : sellGoodsList)
        {
            SellGoods sellGoods = (SellGoods) goods;

            if (sellGoods.getGoodeModel() == _goods) { return sellGoods
                    .getTraceSellGoodsNums(); }
        }

        return -2;
    }

    /**
     * 售出物品
     * 
     * @param _goods 售出的物品
     * @param _nums 售出物品的数量
     * @return 剩余数量是否发生变化
     */
    private synchronized boolean sellGoods (Goods _goods, short _nums)
    {
        for (ExpandGoods goods : sellGoodsList)
        {
            SellGoods sellGoods = (SellGoods) goods;

            if (sellGoods.getGoodeModel() == _goods)
            {
                if (sellGoods.getTraceSellGoodsNums() != -1)
                {
                    sellGoods.setTraceSellGoodsNums(sellGoods
                            .getTraceSellGoodsNums()
                            - _nums);

                    if (sellGoods.getTraceSellGoodsNums() < 0)
                    {
                        sellGoods.setTraceSellGoodsNums(0);
                    }

                    return true;
                }

                break;
            }
        }

        return false;
    }
}
