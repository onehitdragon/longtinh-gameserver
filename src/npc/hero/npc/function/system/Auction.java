package hero.npc.function.system;

import hero.item.Armor;
import hero.item.EqGoods;
import hero.item.Equipment;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.Armor.ArmorType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.system.auction.AuctionDict;
import hero.npc.function.system.auction.AuctionGoods;
import hero.npc.function.system.auction.AuctionType;
import hero.npc.function.system.postbox.Mail;
import hero.npc.function.system.postbox.MailService;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.ui.UI_AuctionGoodsList;
import hero.ui.UI_GoodsListWithOperation;
import hero.ui.UI_GridGoodsNumsChanged;
import hero.ui.UI_InputDigidal;
import hero.ui.UI_InputString;
import hero.ui.UI_SelectOperation;
import hero.ui.message.NotifyListItemMessage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.player.IPlayer;
import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Auction.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:31:45
 * @描述 ：拍卖（拍、卖、取回）
 */

public class Auction extends BaseNpcFunction
{
    /**
     * 顶层操作菜单列表
     */
    private static final String[]      MAIN_MENU_LIST               = {
            "竞拍物品", "拍卖物品", "查询物品"                                 };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]       MAIN_MENU_MARK_IMAGE_ID_LIST = {
            1015, 1014, 1013                                       };



    private static final String[]      BUY_MENU_LIST                = {
            "武器", "布甲", "轻甲", "重甲", "配饰", "药水", "材料", "特殊物品"       };



    private static final String[]      SALE_MENU_LIST               = {
            "装备", "药水", "材料", "特殊物品"                               };



    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] searchOptionData             = new ArrayList[BUY_MENU_LIST.length];

    /**
     * 出售操作菜单列表
     */
    private static final String[]      SALE_OPERTION_LIST           = {"拍　　卖" };

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] saleSingleGoodsOptionData    = new ArrayList[SALE_OPERTION_LIST.length];

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] saleEquipmentOptionData      = new ArrayList[SALE_OPERTION_LIST.length];

    private static final String[]      BUY_OPERTION_LIST            = {
            "竞　　拍", "上　　页", "下　　页", "查看属性", "强化细节"                 };

    private static final String[]      BUY1_OPERTION_LIST           = {
            "竞　　拍", "上　　页", "下　　页", "查看属性"                         };

    private static final String[]      BUY2_OPERTION_LIST           = {
            "竞　　拍", "查看属性", "强化细节"                                 };

    private static final String[]      BUY3_OPERTION_LIST           = {
            "竞　　拍", "查看属性"                                         };

















    private static final String        AUCTION_TITLE                = "拍卖行";
















    private static final AuctionType[] AUCTION_TYPES                = {
            AuctionType.WEAPON, AuctionType.BU_JIA, AuctionType.QING_JIA,
            AuctionType.ZHONG_JIA, AuctionType.PEI_SHI, AuctionType.MEDICAMENT,
            AuctionType.MATERIAL, AuctionType.SPECIAL              };

    private static final byte[]        STEP_IDS                     = {
            Step.BUY_WEAPNS.tag, Step.BUY_BJ.tag, Step.BUY_QJ.tag,
            Step.BUY_ZJ.tag, Step.BUY_PS.tag, Step.BUY_XHDJ.tag,
            Step.BUY_CL.tag, Step.BUY_TSDJ.tag                     };

    enum Step
    {
        TOP(1), BUY_CATEGORY(2), SALE_CATEGORY(3), BUY_WEAPNS(21), BUY_BJ(22), BUY_QJ(
                23), BUY_ZJ(24), BUY_PS(25), BUY_XHDJ(26), BUY_CL(27), BUY_TSDJ(
                28), SALE_Equipment(31), SALE_XHDJ(32), SALE_CL(33), SALE_TSDJ(
                34), SEARCH_CATEGORY(40);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    public Auction(int _npcID)
    {
        super(_npcID);
        // TODO Auto-generated constructor stub
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        byte[] inputDigidal = UI_InputDigidal.getBytes(Tip.TIP_NPC_SALE_MONEY, 0,
                1000000000);
        data.add(UI_InputDigidal.getBytes(Tip.TIP_NPC_SALE_NUM));
        data.add(inputDigidal);
        saleSingleGoodsOptionData[0] = data;

        data = new ArrayList<byte[]>();
        data.add(inputDigidal);
        saleEquipmentOptionData[0] = data;

        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_InputString.getBytes(Tip.TIP_NPC_SEL_GOODS_NAME));
        for (int i = 0; i < searchOptionData.length; i++)
        {
            searchOptionData[i] = data1;
        }
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.AUCTION;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub

        for (int i = 0; i < MAIN_MENU_LIST.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = MAIN_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;
            optionList.add(data);
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int _selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        if (Step.TOP.tag == _step)
        {
            if (0 == _selectIndex)// 购买物品分类列表
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getHostNpcID(), optionList
                                .get(_selectIndex).functionMark,
                                Step.BUY_CATEGORY.tag, UI_SelectOperation
                                        .getBytes(Tip.TIP_NPC_BUY, BUY_MENU_LIST)));
            }
            else if (1 == _selectIndex) // 出售物品分类列表
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getHostNpcID(), optionList
                                .get(_selectIndex).functionMark,
                                Step.SALE_CATEGORY.tag, UI_SelectOperation
                                        .getBytes(Tip.TIP_NPC_SALE, SALE_MENU_LIST)));
            }
            else if (2 == _selectIndex) // 查询物品
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getHostNpcID(), optionList
                                .get(_selectIndex).functionMark,
                                Step.SEARCH_CATEGORY.tag, UI_SelectOperation
                                        .getBytes(Tip.TIP_NPC_SEARCH, BUY_MENU_LIST,
                                                searchOptionData)));

            }
        }
        else if (Step.BUY_CATEGORY.tag == _step)
        {
            byte index = _content.readByte();
            NpcInteractiveResponse msg = null;
            short page = 0;
            ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
            AuctionDict.getInstance().getAuctionGoods(page, goodsList,
                    AUCTION_TYPES[index]);
            if (index <= 4)
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(0).functionMark, STEP_IDS[index],
                        UI_AuctionGoodsList.getBytes((short) page, goodsList,
                                BUY_OPERTION_LIST));
            }
            else
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(0).functionMark, STEP_IDS[index],
                        UI_AuctionGoodsList.getBytes((short) page, goodsList,
                                BUY1_OPERTION_LIST));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        }
        else if (Step.SALE_CATEGORY.tag == _step)
        {
            byte index = _content.readByte();
            NpcInteractiveResponse msg = null;

            if (0 == index)
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(1).functionMark, Step.SALE_Equipment.tag,
                        UI_GoodsListWithOperation.getData(SALE_OPERTION_LIST,
                                saleEquipmentOptionData, _player.getInventory().getEquipmentBag(), 
                                GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
            }
            else if (1 == index)
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(1).functionMark, Step.SALE_XHDJ.tag,
                        UI_GoodsListWithOperation.getBytes(
                        		SALE_OPERTION_LIST,
                                saleSingleGoodsOptionData, 
                                _player.getInventory().getMedicamentBag(), 
                                GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
            }
            else if (2 == index)
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(1).functionMark, Step.SALE_CL.tag,
                        UI_GoodsListWithOperation.getBytes(
                        		SALE_OPERTION_LIST,
                                saleSingleGoodsOptionData, 
                                _player.getInventory().getMaterialBag(), 
                                GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
            }
            else if (3 == index)
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(1).functionMark, Step.SALE_TSDJ.tag,
                        UI_GoodsListWithOperation.getBytes(
                        		SALE_OPERTION_LIST,
                                saleSingleGoodsOptionData, 
                                _player.getInventory().getSpecialGoodsBag(), 
                                GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
            }

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        }
        else if (Step.BUY_WEAPNS.tag <= _step && Step.BUY_TSDJ.tag >= _step)
        {
            byte optionIndex = _content.readByte();
            int auctionID = _content.readInt();
            short page = _content.readShort();
            if (optionIndex == 0)
            {
                AuctionGoods _auctionGoods = AuctionDict.getInstance()
                        .getAuctionGoods(auctionID,
                                AUCTION_TYPES[_step - Step.BUY_WEAPNS.tag]);
                if (_auctionGoods == null)
                {
                    // 移除选择的物品
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new NotifyListItemMessage(_step, false, auctionID));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_AUCTION_OVER, Warning.UI_STRING_TIP));
                }
                else
                {
                    if (_auctionGoods.getPrice() <= _player.getMoney())
                    {
                        AuctionType type = _auctionGoods.getAuctionType();

                        short[] addResult = null;

                        if (type == AuctionType.MATERIAL
                                || type == AuctionType.MEDICAMENT
                                || type == AuctionType.SPECIAL)
                        {
                            addResult = GoodsServiceImpl.getInstance()
                                    .addGoods2Package(_player,
                                            _auctionGoods.getGoodsID(),
                                            _auctionGoods.getNum(),
                                            CauseLog.AUCTION);
                        }
                        else
                        {
                            addResult = GoodsServiceImpl.getInstance()
                                    .addEquipmentInstance2Bag(_player,
                                            _auctionGoods.getInstance(),
                                            CauseLog.AUCTION);
                        }
                        // @ TODO 给拍卖玩家发送金钱
                        if (null != addResult)
                        {
                            Mail mail = new Mail(
                            		MailService.getInstance().getUseableMailID(), 
                            		_auctionGoods.getOwnerUserID(), 
                            		_auctionGoods.getOwnerNickname(), 
                            		AUCTION_TITLE,
                                    Mail.TYPE_OF_MONTY, 
                                    _auctionGoods.getPrice(), "", 
                                    "金币", new Date(System.currentTimeMillis()), (byte)2);
                            MailService.getInstance().addMail(mail, true);

                            // 邮件发送日志
                            LogServiceImpl.getInstance().mailLog(0, 0,
                                    AUCTION_TITLE, "", mail.getID(), 0,
                                    _auctionGoods.getOwnerNickname(),
                                    _auctionGoods.getPrice(), 0, "");

                            // 发送信件提示玩家获得金钱
                            Letter letter = new Letter(Letter.SYSTEM_TYPE,
                                    LetterService.getInstance()
                                            .getUseableLetterID(),
                                    Tip.TIP_NPC_MONEY_NOTIFY,
                                    AUCTION_TITLE,
                                    _auctionGoods.getOwnerUserID(),
                                    _auctionGoods.getOwnerNickname(),
                                    MessageFormat.format(
                                                    Tip.TIP_NPC_LETTER_NOTIFY,
                                                    new Object[]{
                                                    		_auctionGoods.getName(),
                                                    		String.valueOf(_auctionGoods.getPrice()) 
                                                    		}));
                            LetterService.getInstance().addNewLetter(letter);

                            HeroPlayer player = PlayerServiceImpl.getInstance()
                                    .getPlayerByName(
                                            _auctionGoods.getOwnerNickname());
                            if (player != null && player.isEnable())
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_NPC_NEW_MAIL, Warning.UI_STRING_TIP));
                            }

                            PlayerServiceImpl
                                    .getInstance()
                                    .addMoney(
                                            _player,
                                            -_auctionGoods.getPrice(),
                                            1,
                                            PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                            "购买拍卖行物品");

                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new NotifyListItemMessage(_step, false,
                                            auctionID));

                            AuctionDict.getInstance().removeAuctionGoods(
                                    auctionID, _auctionGoods.getAuctionType());
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_MONEY_NOT_ENOUGH, Warning.UI_STRING_TIP));
                    }
                }
            }
            else if (optionIndex == 1)
            {
                if (page == 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NOT_PREC_PAGE, Warning.UI_STRING_TIP));
                    return;
                }
                page--;
                ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
                AuctionDict.getInstance().getAuctionGoods(page, goodsList,
                        AUCTION_TYPES[_step - Step.BUY_WEAPNS.tag]);
                if (goodsList.size() == 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NOT_PREC_PAGE, Warning.UI_STRING_TIP));
                }
                else
                {
                    NpcInteractiveResponse msg = null;
                    if (_step - Step.BUY_WEAPNS.tag <= 4)
                    {

                        msg = new NpcInteractiveResponse(getHostNpcID(),
                                optionList.get(0).functionMark, STEP_IDS[_step
                                        - Step.BUY_WEAPNS.tag],
                                UI_AuctionGoodsList.getBytes((short) page,
                                        goodsList, BUY_OPERTION_LIST));
                    }
                    else
                    {
                        msg = new NpcInteractiveResponse(getHostNpcID(),
                                optionList.get(0).functionMark, STEP_IDS[_step
                                        - Step.BUY_WEAPNS.tag],
                                UI_AuctionGoodsList.getBytes((short) page,
                                        goodsList, BUY1_OPERTION_LIST));
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            }
            else if (optionIndex == 2)
            {
                page++;
                ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
                AuctionDict.getInstance().getAuctionGoods(page, goodsList,
                        AUCTION_TYPES[_step - Step.BUY_WEAPNS.tag]);
                if (goodsList.size() == 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NOT_NEXT_PAGE, Warning.UI_STRING_TIP));
                }
                else
                {
                    NpcInteractiveResponse msg = null;
                    if (_step - Step.BUY_WEAPNS.tag <= 4)
                    {
                        msg = new NpcInteractiveResponse(getHostNpcID(),
                                optionList.get(0).functionMark, STEP_IDS[_step
                                        - Step.BUY_WEAPNS.tag],
                                UI_AuctionGoodsList.getBytes((short) page,
                                        goodsList, BUY_OPERTION_LIST));
                    }
                    else
                    {
                        msg = new NpcInteractiveResponse(getHostNpcID(),
                                optionList.get(0).functionMark, STEP_IDS[_step
                                        - Step.BUY_WEAPNS.tag],
                                UI_AuctionGoodsList.getBytes((short) page,
                                        goodsList, BUY1_OPERTION_LIST));
                    }
                    
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            }
        }
        else if (Step.SALE_Equipment.tag == _step)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int num = 1;// 装备数量默认1
            int money = _content.readInt();
            EquipmentInstance ei = _player.getInventory().getEquipmentBag()
                    .getEquipmentList()[gridIndex];
            if (null != ei && ei.getInstanceID() == goodsID)
            {
                if (ei.getArchetype().exchangeable())
                {
                    if (ei.getCurrentDurabilityPoint() == ei.getArchetype()
                            .getMaxDurabilityPoint())
                    {
                        int _price = ei.getArchetype().getSellPrice()
                                / AuctionDict.AUCTION_PRICE;
                        if (_player.getMoney() >= _price)
                        {
                            if (-1 != GoodsServiceImpl.getInstance()
                                    .removeEquipmentOfBag(
                                            _player,
                                            _player.getInventory()
                                                    .getEquipmentBag(), ei,
                                            CauseLog.AUCTION))
                            {
                                PlayerServiceImpl
                                        .getInstance()
                                        .addMoney(
                                                _player,
                                                -_price,
                                                1,
                                                PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                                                "拍卖行手续费");
                                AuctionDict.getInstance().addAuctionGoods(
                                        new AuctionGoods(AuctionDict
                                                .getInstance().getAuctionID(),
                                                ei.getInstanceID(), _player
                                                        .getUserID(), _player
                                                        .getName(), ei
                                                        .getGeneralEnhance()
                                                        .getLevel(),
                                                (short) num, money,
                                                getAuctionType(ei
                                                        .getArchetype()), ei,
                                                System.currentTimeMillis()),
                                        true);
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_NPC_AUCTION_SUCCESS, Warning.UI_STRING_TIP));
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new NpcInteractiveResponse(
                                                getHostNpcID(), optionList
                                                        .get(1).functionMark,
                                                Step.SALE_Equipment.tag,
                                                UI_GridGoodsNumsChanged
                                                        .getBytes(gridIndex,
                                                                goodsID, 0)));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_MONEY_ERROR, Warning.UI_STRING_TIP));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_DURABILITY_ERROR, Warning.UI_STRING_TIP));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NO_EXCHANGEABLE, Warning.UI_STRING_TIP));
                    return;
                }
            }
        }
        else if (Step.SALE_XHDJ.tag == _step)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            int money = _content.readInt();
            Goods _goods = GoodsContents.getGoods(goodsID);
            if (_goods.exchangeable())
            {
                if (goodsID == _player.getInventory().getMedicamentBag()
                        .getAllItem()[gridIndex][0])
                {
                    // @ TODO 修改数量以及数量检查
                    short num = (short) _player.getInventory()
                            .getMedicamentBag().getAllItem()[gridIndex][1];
                    if (num >= goodsNum)
                    {
                        Goods goods = GoodsContents.getGoods(goodsID);
                        int _price = goods.getSellPrice()
                                / AuctionDict.AUCTION_PRICE;
                        if (_player.getMoney() >= _price)
                        {
                            if (GoodsServiceImpl.getInstance()
                                    .reduceSingleGoods(
                                            _player,
                                            _player.getInventory()
                                                    .getMedicamentBag(),
                                            gridIndex, (SingleGoods) goods,
                                            goodsNum, CauseLog.AUCTION))
                            {
                                PlayerServiceImpl
                                        .getInstance()
                                        .addMoney(
                                                _player,
                                                -_price,
                                                1,
                                                PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                                                "拍卖行手续费");
                                AuctionDict.getInstance().addAuctionGoods(
                                        new AuctionGoods(AuctionDict
                                                .getInstance().getAuctionID(),
                                                goodsID, _player.getUserID(),
                                                _player.getName(), (short) 0,
                                                (short) goodsNum, money,
                                                AuctionType.MEDICAMENT, null,
                                                System.currentTimeMillis()),
                                        true);
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_NPC_AUCTION_SUCCESS, Warning.UI_STRING_TIP));
                                ResponseMessageQueue
                                        .getInstance()
                                        .put(
                                                _player.getMsgQueueIndex(),
                                                new NpcInteractiveResponse(
                                                        getHostNpcID(),
                                                        optionList.get(1).functionMark,
                                                        Step.SALE_XHDJ.tag,
                                                        UI_GridGoodsNumsChanged
                                                                .getBytes(
                                                                        gridIndex,
                                                                        goodsID,
                                                                        num
                                                                                - goodsNum)));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_MONEY_ERROR, Warning.UI_STRING_TIP));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_NUM_ERROR, Warning.UI_STRING_TIP));
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_NO_EXCHANGEABLE, Warning.UI_STRING_TIP));
                return;
            }
        }
        else if (Step.SALE_CL.tag == _step)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            int money = _content.readInt();
            Goods _goods = GoodsContents.getGoods(goodsID);
            if (_goods.exchangeable())
            {
                if (goodsID == _player.getInventory().getMaterialBag()
                        .getAllItem()[gridIndex][0])
                {
                    short num = (short) _player.getInventory().getMaterialBag()
                            .getAllItem()[gridIndex][1];
                    if (num >= goodsNum)
                    {
                        Goods goods = GoodsContents.getGoods(goodsID);
                        int _price = goods.getSellPrice()
                                / AuctionDict.AUCTION_PRICE;
                        if (_player.getMoney() >= _price)
                        {
                            if (GoodsServiceImpl.getInstance()
                                    .reduceSingleGoods(
                                            _player,
                                            _player.getInventory()
                                                    .getMaterialBag(),
                                            gridIndex, (SingleGoods) goods,
                                            goodsNum, CauseLog.AUCTION))
                            {
                                PlayerServiceImpl
                                        .getInstance()
                                        .addMoney(
                                                _player,
                                                -_price,
                                                1,
                                                PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                                                "拍卖行手续费");
                                AuctionDict.getInstance().addAuctionGoods(
                                        new AuctionGoods(AuctionDict
                                                .getInstance().getAuctionID(),
                                                goodsID, _player.getUserID(),
                                                _player.getName(), (short) 0,
                                                (short) goodsNum, money,
                                                AuctionType.MATERIAL, null,
                                                System.currentTimeMillis()),
                                        true);
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_NPC_AUCTION_SUCCESS, Warning.UI_STRING_TIP));
                                ResponseMessageQueue
                                        .getInstance()
                                        .put(
                                                _player.getMsgQueueIndex(),
                                                new NpcInteractiveResponse(
                                                        getHostNpcID(),
                                                        optionList.get(1).functionMark,
                                                        Step.SALE_CL.tag,
                                                        UI_GridGoodsNumsChanged
                                                                .getBytes(
                                                                        gridIndex,
                                                                        goodsID,
                                                                        num
                                                                                - goodsNum)));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_MONEY_ERROR, Warning.UI_STRING_TIP));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_NUM_ERROR, Warning.UI_STRING_TIP));
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_NO_EXCHANGEABLE, Warning.UI_STRING_TIP));
                return;
            }
        }
        else if (Step.SALE_TSDJ.tag == _step)
        {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            int money = _content.readInt();
            Goods _goods = GoodsContents.getGoods(goodsID);
            if (_goods.exchangeable())
            {
                if (goodsID == _player.getInventory().getSpecialGoodsBag()
                        .getAllItem()[gridIndex][0])
                {
                    short num = (short) _player.getInventory()
                            .getSpecialGoodsBag().getAllItem()[gridIndex][1];
                    if (num >= goodsNum)
                    {
                        Goods goods = GoodsContents.getGoods(goodsID);
                        int _price = goods.getSellPrice()
                                / AuctionDict.AUCTION_PRICE;
                        if (_player.getMoney() >= _price)
                        {
                            if (GoodsServiceImpl.getInstance()
                                    .reduceSingleGoods(
                                            _player,
                                            _player.getInventory()
                                                    .getSpecialGoodsBag(),
                                            gridIndex, (SingleGoods) goods,
                                            goodsNum, CauseLog.AUCTION))
                            {
                                PlayerServiceImpl
                                        .getInstance()
                                        .addMoney(
                                                _player,
                                                -_price,
                                                1,
                                                PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                                                "拍卖行手续费");
                                AuctionDict.getInstance().addAuctionGoods(
                                        new AuctionGoods(AuctionDict
                                                .getInstance().getAuctionID(),
                                                goodsID, _player.getUserID(),
                                                _player.getName(), (short) 0,
                                                (short) goodsNum, money,
                                                AuctionType.SPECIAL, null,
                                                System.currentTimeMillis()),
                                        true);
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_NPC_AUCTION_SUCCESS, Warning.UI_STRING_TIP));
                                ResponseMessageQueue
                                        .getInstance()
                                        .put(
                                                _player.getMsgQueueIndex(),
                                                new NpcInteractiveResponse(
                                                        getHostNpcID(),
                                                        optionList.get(1).functionMark,
                                                        Step.SALE_TSDJ.tag,
                                                        UI_GridGoodsNumsChanged
                                                                .getBytes(
                                                                        gridIndex,
                                                                        goodsID,
                                                                        num
                                                                                - goodsNum)));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_MONEY_ERROR, Warning.UI_STRING_TIP));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_NUM_ERROR, Warning.UI_STRING_TIP));
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_NO_EXCHANGEABLE, Warning.UI_STRING_TIP));
                return;
            }
        }
        else if (_step == Step.SEARCH_CATEGORY.tag)
        {
            byte index = _content.readByte();
            String searchName = _content.readUTF();
            NpcInteractiveResponse msg = null;
            ArrayList<AuctionGoods> goodsList = AuctionDict.getInstance()
                    .sreachAuctionGoods(AUCTION_TYPES[index], searchName);
            if (index <= 4)
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(2).functionMark, STEP_IDS[index],
                        UI_AuctionGoodsList.getBytes((short) 1, goodsList,
                                BUY2_OPERTION_LIST));
            }
            else
            {
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(2).functionMark, STEP_IDS[index],
                        UI_AuctionGoodsList.getBytes((short) 1, goodsList,
                                BUY3_OPERTION_LIST));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        }
    }

    /**
     * 根据装备类型得到拍卖类型
     * 
     * @param _equipment
     * @return
     */
    private AuctionType getAuctionType (EqGoods _equipment)
    {
        if (_equipment instanceof Armor)
        {
            ArmorType armorType = ((Armor) _equipment).getArmorType();
            if (armorType == ArmorType.BU_JIA)
            {
                // 布甲
                return AuctionType.BU_JIA;
            }
            else if (armorType == ArmorType.QING_JIA)
            {
                // 轻甲
                return AuctionType.QING_JIA;
            }
            else if (armorType == ArmorType.ZHONG_JIA)
            {
                // 重甲
                return AuctionType.ZHONG_JIA;
            }
            else if (armorType == ArmorType.RING
                    || armorType == ArmorType.NECKLACE
                    || armorType == ArmorType.BRACELETE)
            {
                // 配饰
                return AuctionType.PEI_SHI;
            }
        }
        return AuctionType.WEAPON;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        return optionList;
    }
}