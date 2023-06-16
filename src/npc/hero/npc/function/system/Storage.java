package hero.npc.function.system;

import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.BigTonicBall;
import hero.item.special.PetPerCard;
import hero.log.service.CauseLog;
import hero.log.service.FlowLog;
import hero.log.service.LoctionLog;
import hero.log.service.LogServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.system.storage.Warehouse;
import hero.npc.function.system.storage.WarehouseDict;
import hero.npc.function.system.storage.WarehouseGoods;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.player.message.HotKeySumByMedicament;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.ui.UI_Confirm;
import hero.ui.UI_GoodsListWithOperation;
import hero.ui.UI_GridGoodsNumsChanged;
import hero.ui.UI_InputDigidal;
import hero.ui.UI_SelectOperation;
import hero.ui.UI_StorageGoodsList;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Storage.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:26:55
 * @描述 ：仓库（存取物品）
 */

public class Storage extends BaseNpcFunction
{
    /**
     * 顶层操作菜单列表
     */
//    private static final String[]      MAIN_MENU_LIST               = {"取出", "存入", "升级空间" };
//	private static final String[]      MAIN_MENU_LIST               = {"取出", "存入"};
	private static final String[]      MAIN_MENU_LIST               = {"打开仓库"};

    private static final String[]      SEL_OPERTION_LIST            = {"取　　出" };

//    private static final String        STORAGE_TIP                  = "请选择存放物品的种类";
//
//    private static final String[]      STORAGE_MENU_LIST            = {"装备", "药品", "材料", "任务", "宝物" };

    private static final String[]      STORAGE_OPERTION_LIST        = {"存　　入" };

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] storageSingleGoodsOptionData = new ArrayList[STORAGE_OPERTION_LIST.length];

    private static final String        STORAGE_NUM_TIP              = "请输入存入数量";

    enum Step
    {
        TOP(1), SEL_STORAGE(10), STORAGE_CATEGORY(20), STORAGE_EQUIPMENT(21), STORAGE_XHDJ(
                22), STORAGE_CL(23), STORAGE_RWDJ(24), STORAGE_TSDJ(25), UP_LEVEL(30);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    public Storage(int npcID)
    {
        super(npcID);
        // TODO Auto-generated constructor stub
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes(STORAGE_NUM_TIP));
        storageSingleGoodsOptionData[0] = data;
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.STORAGE;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
        for (int i = 0; i < MAIN_MENU_LIST.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = MAIN_MENU_MARK_IMAGE_ID_LIST[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = MAIN_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;
            optionList.add(data);
        }
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
        // TODO Auto-generated method stub
        if (_step == Step.TOP.tag)
        {
        	//打开仓库后玩家5个背包以及个人仓库背包集体下发
            if (_selectIndex == 0)
            {
                //下发仓库
            	Warehouse warehouse = 
            		WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
            	NpcInteractiveResponse msg = null;
                ArrayList<NpcInteractiveResponse> msgList = new ArrayList<NpcInteractiveResponse>();

            	msg = new NpcInteractiveResponse(getHostNpcID(), 
                		optionList.get(_selectIndex).functionMark,
                        Step.SEL_STORAGE.tag,
                        UI_StorageGoodsList.getBytes(SEL_OPERTION_LIST, warehouse));
            	if (msg != null) {
            		msgList.add(msg);
				} else {
					System.out.println("获得仓库为null,这样的情况不应该发生");
				}
                //下发包裹
                msg = new NpcInteractiveResponse(getHostNpcID(), 
                		optionList.get(_selectIndex).functionMark, Step.STORAGE_EQUIPMENT.tag,
                        UI_GoodsListWithOperation.getStorageData(STORAGE_OPERTION_LIST, 
                        		_player.getInventory().getEquipmentBag(), 
                        		GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
            	if (msg != null) {
            		msgList.add(msg);
				} else {
					System.out.println("获得装备包为null,这样的情况不应该发生");
				}
                
                msg = new NpcInteractiveResponse(getHostNpcID(), 
                		optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag,
                        UI_GoodsListWithOperation.getStorageBytes(
                        		STORAGE_OPERTION_LIST, 
                        		_player.getInventory().getMedicamentBag(), 
                        		GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
            	if (msg != null) {
            		msgList.add(msg);
				} else {
					System.out.println("获得药品包为null,这样的情况不应该发生");
				}
                
                msg = new NpcInteractiveResponse(getHostNpcID(), 
                		optionList.get(_selectIndex).functionMark, Step.STORAGE_CL.tag,
                        UI_GoodsListWithOperation.getStorageBytes(STORAGE_OPERTION_LIST, 
                        		_player.getInventory().getMaterialBag(), 
                        		GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
            	if (msg != null) {
            		msgList.add(msg);
				} else {
					System.out.println("获得材料包为null,这样的情况不应该发生");
				}
                
                msg = new NpcInteractiveResponse(getHostNpcID(), 
                		optionList.get(_selectIndex).functionMark, Step.STORAGE_RWDJ.tag,
                        UI_GoodsListWithOperation.getStorageBytes(STORAGE_OPERTION_LIST, 
                        		_player.getInventory().getTaskToolBag(), 
                        		GoodsServiceImpl.getInstance().getConfig().task_tool_bag_tab_name));
            	if (msg != null) {
            		msgList.add(msg);
				} else {
					System.out.println("获得任务包为null,这样的情况不应该发生");
				}
                
                msg = new NpcInteractiveResponse(getHostNpcID(), 
                		optionList.get(_selectIndex).functionMark, Step.STORAGE_TSDJ.tag,
                        UI_GoodsListWithOperation.getStorageBytes(STORAGE_OPERTION_LIST, 
                        		_player.getInventory().getSpecialGoodsBag(), 
                        		GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
            	if (msg != null) {
            		msgList.add(msg);
				} else {
					System.out.println("获得宝物包为null,这样的情况不应该发生");
				}
                if(msgList.size() > 0)
                {
                	for (NpcInteractiveResponse bagData : msgList) {
                		ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), bagData);
					}
                }
            }
        }
        else if (_step == Step.SEL_STORAGE.tag)
        {
            // 提取物品
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int number = _content.readByte();
            Warehouse warehouse = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
            WarehouseGoods wgoods = warehouse.getWarehouseGoods(gridIndex);
            if (wgoods != null && wgoods.goodsID == goodsID)
            {
                short[] addSuccessful = null;
                Goods goods = null;
                int num = 1;
                if (wgoods.goodsType == 0)
                {
                    addSuccessful = GoodsServiceImpl.getInstance()
                            .addEquipmentInstance2Bag(_player, wgoods.instance,
                                    CauseLog.STORAGE);
                    goods = wgoods.instance.getArchetype();
                }
                else
                {
                    goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsID);
                    //add by zhengl; date: 2011-05-12; note: 添加大补丸/马符等物品
                    if (goods instanceof BigTonicBall || goods instanceof PetPerCard) 
                    {
                    	int singleGoodsIndex = wgoods.indexID;
                    	int packageIndex = 
                    		_player.getInventory().getSpecialGoodsBag().getFirstEmptyGridIndex();
                    	_player.getInventory().getSpecialGoodsBag().load(goodsID, 1, packageIndex);
						GoodsDAO.getStorageSpecialGoods(
								_player, goodsID, singleGoodsIndex, 1, packageIndex);
						addSuccessful = new short[]{(short)packageIndex, (short) 1};
						
						//仓库操作不计数所以要进行强制刷新
			    		HotKeySumByMedicament keyMsg = new HotKeySumByMedicament(_player, goodsID);
			    		if(keyMsg.haveRelation(goodsID)) {
			    			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), keyMsg);
			    		}
					}
                    else 
                    {
                        addSuccessful = GoodsServiceImpl.getInstance().addGoods2Package(
                        		_player, goods, wgoods.goodsNum, CauseLog.STORAGE);
					}
                    num = wgoods.goodsNum;
                }
                if (null != addSuccessful)
                {
                    warehouse.removeWarehouseGoods(gridIndex);
//                    NpcInteractiveResponse msg = new NpcInteractiveResponse(
//                            getHostNpcID(), optionList.get(0).functionMark,
//                            Step.SEL_STORAGE.tag, UI_GridGoodsNumsChanged
//                                    .getBytes(gridIndex, goodsID, 0));
                    NpcInteractiveResponse msg = new NpcInteractiveResponse(getHostNpcID(), 
                    		optionList.get(_selectIndex).functionMark,
                            Step.SEL_STORAGE.tag,
                            UI_StorageGoodsList.getBytes(SEL_OPERTION_LIST, warehouse));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                    
                    if (goods.getGoodsType() == EGoodsType.EQUIPMENT) {
                        msg = new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark, Step.STORAGE_EQUIPMENT.tag,
                                UI_GoodsListWithOperation.getStorageData(STORAGE_OPERTION_LIST, 
                                		_player.getInventory().getEquipmentBag(), 
                                		GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                        
                        
					} else if (goods.getGoodsType() == EGoodsType.MATERIAL) {
						msg = new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark, Step.STORAGE_CL.tag,
                                UI_GoodsListWithOperation.getStorageBytes(STORAGE_OPERTION_LIST, 
                                		_player.getInventory().getMaterialBag(), 
                                		GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                        
                        
					} else if (goods.getGoodsType() == EGoodsType.MEDICAMENT) {
						msg = new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag,
                                UI_GoodsListWithOperation.getStorageBytes(
                                		STORAGE_OPERTION_LIST, 
                                		_player.getInventory().getMedicamentBag(), 
                                		GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                        
                        
					} else if (goods.getGoodsType() == EGoodsType.TASK_TOOL) {
						msg = new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark, Step.STORAGE_RWDJ.tag,
                                UI_GoodsListWithOperation.getStorageBytes(STORAGE_OPERTION_LIST, 
                                		_player.getInventory().getTaskToolBag(), 
                                		GoodsServiceImpl.getInstance().getConfig().task_tool_bag_tab_name));
                        
                        
					} else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
						msg = new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark, Step.STORAGE_TSDJ.tag,
                                UI_GoodsListWithOperation.getStorageBytes(STORAGE_OPERTION_LIST, 
                                		_player.getInventory().getSpecialGoodsBag(), 
                                		GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
					} else if (goods.getGoodsType() == EGoodsType.PET) {
						//暂不启用
					}
                    //再发一次
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                    

                    // 物品变更日志
                    LogServiceImpl.getInstance().goodsChangeLog(_player, goods,
                            num, LoctionLog.STORAGE, FlowLog.GET,
                            CauseLog.STORAGE);
                    LogServiceImpl.getInstance().goodsChangeLog(_player, goods, num,
                                    LoctionLog.BAG, FlowLog.LOSE,
                                    CauseLog.STORAGE);

                    // 仓库变化日志
                    LogServiceImpl.getInstance().depotChangeLog(
                            _player.getLoginInfo().accountID,
                            _player.getUserID(), _player.getName(),
                            _player.getLoginInfo().loginMsisdn, goodsID,
                            goods.getName(), num, "取出",
                            _player.where().getName());
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_STORAGE_NOT_EXITS));
            }
        }
        else if (_step == Step.STORAGE_CATEGORY.tag)
        {
            byte index = _content.readByte();
            //原本逻辑弃用
        }
        else if (_step == Step.STORAGE_EQUIPMENT.tag)
        {
        	//存放
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            EquipmentInstance ei = _player.getInventory().getEquipmentBag().getEquipmentList()[gridIndex];
            if (null != ei && ei.getInstanceID() == goodsID)
            {
                Warehouse warehouse = WarehouseDict.getInstance()
                        .getWarehouseByNickname(_player.getName());
                if (warehouse.addWarehouseGoods(ei))
                {
                    if (-1 != GoodsServiceImpl.getInstance()
                            .removeEquipmentOfBag(_player,
                                    _player.getInventory().getEquipmentBag(),
                                    ei, CauseLog.STORAGE))
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_STORAGE_SUCCESS));
                        NpcInteractiveResponse msg = new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark,
                                Step.SEL_STORAGE.tag,
                                UI_StorageGoodsList.getBytes(SEL_OPERTION_LIST, warehouse));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        
                        msg = new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark, Step.STORAGE_EQUIPMENT.tag,
                                UI_GoodsListWithOperation.getStorageData(STORAGE_OPERTION_LIST, 
                                		_player.getInventory().getEquipmentBag(), 
                                		GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                ei.getArchetype(), 1, LoctionLog.STORAGE,
                                FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                ei.getArchetype(), 1, LoctionLog.BAG,
                                FlowLog.LOSE, CauseLog.STORAGE);
                        // 仓库变化日志
                        LogServiceImpl.getInstance().depotChangeLog(
                                _player.getLoginInfo().accountID,
                                _player.getUserID(), _player.getName(),
                                _player.getLoginInfo().loginMsisdn, goodsID,
                                ei.getArchetype().getName(), 1, "存入",
                                _player.where().getName());
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_STORAGE_FULL));
                }
            }
        }
        else if (_step == Step.STORAGE_XHDJ.tag)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getMedicamentBag()
                    .getAllItem()[gridIndex][0])
            {
                // @ TODO 修改数量以及数量检查
                short num = (short) _player.getInventory().getMedicamentBag()
                        .getAllItem()[gridIndex][1];
                if (num >= goodsNum)
                {
                    Warehouse warehouse = WarehouseDict.getInstance()
                            .getWarehouseByNickname(_player.getName());
                    if (warehouse.addWarehouseGoods(goodsID, (short) goodsNum,
                            (short) 1, _player.getUserID(), gridIndex, false))
                    {
                        Goods goods = GoodsContents.getGoods(goodsID);
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(
                                _player,
                                _player.getInventory().getMedicamentBag(),
                                gridIndex, (SingleGoods) goods, goodsNum,
                                CauseLog.STORAGE))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_STORAGE_SUCCESS));
                            //
                            NpcInteractiveResponse msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark,
                                    Step.SEL_STORAGE.tag,
                                    UI_StorageGoodsList.getBytes(SEL_OPERTION_LIST, warehouse));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                            
    						msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag,
                                    UI_GoodsListWithOperation.getStorageBytes(
                                    		STORAGE_OPERTION_LIST, 
                                    		_player.getInventory().getMedicamentBag(), 
                                    		GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        }
                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.STORAGE,
                                FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.BAG, FlowLog.LOSE,
                                CauseLog.STORAGE);

                        // 仓库变化日志
                        LogServiceImpl.getInstance().depotChangeLog(
                                _player.getLoginInfo().accountID,
                                _player.getUserID(), _player.getName(),
                                _player.getLoginInfo().loginMsisdn, goodsID,
                                goods.getName(), goodsNum, "存入",
                                _player.where().getName());
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_STORAGE_FULL));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_STORAGE_NUM_ERROR));
                }
            }
        }
        else if (_step == Step.STORAGE_CL.tag)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getMaterialBag().getAllItem()[gridIndex][0])
            {
                // @ TODO 修改数量以及数量检查
                short num = (short) _player.getInventory().getMaterialBag()
                        .getAllItem()[gridIndex][1];
                if (num >= goodsNum)
                {
                    Warehouse warehouse = WarehouseDict.getInstance()
                            .getWarehouseByNickname(_player.getName());
                    if (warehouse.addWarehouseGoods(goodsID, (short) goodsNum,
                            (short) 1, _player.getUserID(), gridIndex, false))
                    {
                        Goods goods = GoodsContents.getGoods(goodsID);
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(
                                _player,
                                _player.getInventory().getMaterialBag(),
                                gridIndex, (SingleGoods) goods, goodsNum,
                                CauseLog.STORAGE))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_STORAGE_SUCCESS));
                            NpcInteractiveResponse msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark,
                                    Step.SEL_STORAGE.tag,
                                    UI_StorageGoodsList.getBytes(SEL_OPERTION_LIST, warehouse));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                            
    						msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag,
                                    UI_GoodsListWithOperation.getStorageBytes(
                                    		STORAGE_OPERTION_LIST, 
                                    		_player.getInventory().getMaterialBag(), 
                                    		GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        }

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.STORAGE,
                                FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.BAG, FlowLog.LOSE,
                                CauseLog.STORAGE);

                        // 仓库变化日志
                        LogServiceImpl.getInstance().depotChangeLog(
                                _player.getLoginInfo().accountID,
                                _player.getUserID(), _player.getName(),
                                _player.getLoginInfo().loginMsisdn, goodsID,
                                goods.getName(), goodsNum, "存入",
                                _player.where().getName());
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_STORAGE_FULL));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_STORAGE_NUM_ERROR));
                }
            }
        }
        else if (_step == Step.STORAGE_TSDJ.tag)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getSpecialGoodsBag()
                    .getAllItem()[gridIndex][0])
            {
                // @ TODO 修改数量以及数量检查
                short num = (short) _player.getInventory().getSpecialGoodsBag()
                        .getAllItem()[gridIndex][1];
                if (num >= goodsNum)
                {
                    Warehouse warehouse = WarehouseDict.getInstance()
                            .getWarehouseByNickname(_player.getName());
                    Goods goods = GoodsContents.getGoods(goodsID);
                    boolean isAutoBall = false;
                    //add by zhengl; date: 2011-05-13; note: 大补丸注销状态
                	if (goods instanceof BigTonicBall) 
                	{
                		BigTonicBall ball = (BigTonicBall)goods;
                		if (ball.isActivate != BigTonicBall.TONINC_UNAUTO) 
                		{
                			if (ball.tonincType == BigTonicBall.TONINC_RED) 
                			{
                				if (_player.getRedTonicBall() != null) 
                				{
                					_player.setRedTonicBall(null);
								}
							}
                			else 
                			{
                				if (_player.getBuleTonicBall() != null) 
                				{
                					_player.setBuleTonicBall(null);
								}
							}
                			isAutoBall = true;
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
                                    new Warning(Tip.TIP_ITEM_TONIC_STOP.replaceAll("%fname", 
                                    		ball.getName())));
						}
					}
                    if (warehouse.addWarehouseGoods(goodsID, (short) goodsNum,
                            (short) 1, _player.getUserID(), gridIndex, isAutoBall))
                    {

                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(
                                _player,
                                _player.getInventory().getSpecialGoodsBag(),
                                gridIndex, (SingleGoods) goods, goodsNum,
                                CauseLog.STORAGE))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_STORAGE_SUCCESS));
                            NpcInteractiveResponse msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark,
                                    Step.SEL_STORAGE.tag,
                                    UI_StorageGoodsList.getBytes(SEL_OPERTION_LIST, warehouse));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                            
    						msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag,
                                    UI_GoodsListWithOperation.getStorageBytes(
                                    		STORAGE_OPERTION_LIST, 
                                    		_player.getInventory().getSpecialGoodsBag(), 
                                    		GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        }

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.STORAGE,
                                FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.BAG, FlowLog.LOSE,
                                CauseLog.STORAGE);

                        // 仓库变化日志
                        LogServiceImpl.getInstance().depotChangeLog(
                                _player.getLoginInfo().accountID,
                                _player.getUserID(), _player.getName(),
                                _player.getLoginInfo().loginMsisdn, goodsID,
                                goods.getName(), goodsNum, "存入",
                                _player.where().getName());
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_STORAGE_FULL));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_STORAGE_NUM_ERROR));
                }
            }
        }
        else if (_step == Step.STORAGE_RWDJ.tag)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getMaterialBag().getAllItem()[gridIndex][0])
            {
                // @ TODO 修改数量以及数量检查
                short num = (short) _player.getInventory().getTaskToolBag().getAllItem()[gridIndex][1];
                if (num >= goodsNum)
                {
                    Warehouse warehouse = WarehouseDict.getInstance()
                            .getWarehouseByNickname(_player.getName());
                    if (warehouse.addWarehouseGoods(goodsID, (short) goodsNum,
                            (short) 1, _player.getUserID(), gridIndex, false))
                    {
                        Goods goods = GoodsContents.getGoods(goodsID);
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(
                                _player,
                                _player.getInventory().getMaterialBag(),
                                gridIndex, (SingleGoods) goods, goodsNum,
                                CauseLog.STORAGE))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_STORAGE_SUCCESS));
                            NpcInteractiveResponse msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark,
                                    Step.SEL_STORAGE.tag,
                                    UI_StorageGoodsList.getBytes(SEL_OPERTION_LIST, warehouse));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                            
    						msg = new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag,
                                    UI_GoodsListWithOperation.getStorageBytes(
                                    		STORAGE_OPERTION_LIST, 
                                    		_player.getInventory().getTaskToolBag(), 
                                    		GoodsServiceImpl.getInstance().getConfig().task_tool_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        }

                        // 物品变更日志
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.STORAGE,
                                FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player,
                                goods, goodsNum, LoctionLog.BAG, FlowLog.LOSE,
                                CauseLog.STORAGE);

                        // 仓库变化日志
                        LogServiceImpl.getInstance().depotChangeLog(
                                _player.getLoginInfo().accountID,
                                _player.getUserID(), _player.getName(),
                                _player.getLoginInfo().loginMsisdn, goodsID,
                                goods.getName(), goodsNum, "存入",
                                _player.where().getName());
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_STORAGE_FULL));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_STORAGE_NUM_ERROR));
                }
            }
        }
        else if (_step == Step.UP_LEVEL.tag)
        {

        }
    }
}
