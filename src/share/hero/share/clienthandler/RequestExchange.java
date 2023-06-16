package hero.share.clienthandler;

import hero.chat.service.ChatQueue;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.exchange.Exchange;
import hero.share.exchange.ExchangeDict;
import hero.share.exchange.ExchangeGoodsList;
import hero.share.exchange.ExchangePlayer;
import hero.share.message.AskExchange;
import hero.share.message.ExchangeLockedGoodsList;
import hero.share.message.ResponseExchange;
import hero.share.message.ResponseExchangeGoodsList;
import hero.share.message.Warning;
import hero.share.service.ShareServiceImpl;
import hero.share.service.Tip;
import hero.social.service.SocialServiceImpl;
import hero.ui.UI_InputDigidal;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


public class RequestExchange extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(RequestExchange.class);
    /**
     * 装备
     */
    private static final byte          Z_B                   = 0;
    /**
     * 药水
     */
    private static final byte          X_H_P                 = 1;
    /**
     * 材料
     */
    private static final byte          C_L                   = 2;
    /**
     * 特殊物品
     */
    private static final byte          T_S                   = 3;
    /**
     * 宠物装备
     */
    private static final byte          PET_EQUIP = 4;
    /**
     * 宠物物品
     */
    private static final byte          PET_GOODS = 5;
    /**
     * 宠物
     */
    private static final byte          PET = 6;

    private static final String[]      GOODS_OPERTION_LIST   = {"确　　定" };

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] singleGoodsOptionData = new ArrayList[GOODS_OPERTION_LIST.length];

 

    static
    {
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes(Tip.REQUEST_EXCHANGE_NUM_TIP));
        singleGoodsOptionData[0] = data;
    }

    public void read () throws Exception
    {
        try
        {
            byte exchangeType = yis.readByte();
            HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

            switch (exchangeType)
            {
                case Exchange.BEGIN:
                {
                if(ShareServiceImpl.getInstance().canRequest(player.getUserID())){
                    int objectID = yis.readInt();
                    HeroPlayer other = player.where().getPlayer(objectID);

                    if (other == null || !other.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.REQUEST_EXCHANGE_PLAYER_NOT_EXITS, Warning.UI_STRING_TIP));

                        return;
                    }
                    else
                    {
                        // @ TODO 检查玩家状态
                    	//edit by zhengl; date: 2011-02-27; note: 战斗中不能交易 ，摆摊状态中也不能交易(2011-05-12 jiaodj)
                        if (other.isDead() || other.isInFighting() || other.isSelling())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(Tip.REQUEST_EXCHANGE_PLAYER_BUSY, Warning.UI_STRING_TIP));
                            return;
                        }

                        if (!SocialServiceImpl.getInstance().beBlack(
                                player.getUserID(), other.getUserID()))
                        {
                            /*int exchangeID = ExchangeDict.getInstance()
                                    .addExchange(player, other);

                            OutMsgQ.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new ResponseExchange(exchangeID, other
                                            .getName()));
                            OutMsgQ.getInstance().put(
                                    other.getMsgQueueIndex(),
                                    new ResponseExchange(exchangeID, player
                                            .getName()));*/
                        	ShareServiceImpl.getInstance().addRequestExchangePlayer(player.getUserID(), other.getUserID());
                            ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new AskExchange(player,other));
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning("你已被该玩家屏蔽", Warning.UI_STRING_TIP));
                        }
                    }
                }else{
            		ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new Warning("你不能再次发起交易请求了", Warning.UI_STRING_TIP));
            	}

                    break;
                }
                case Exchange.ADD_MONEY:
                {
                    int exchangeID = yis.readInt();
                    int money = yis.readInt();

                    if(money<=0){
                        LogServiceImpl.getInstance().numberErrorLog(player.getLoginInfo().accountID,player.getLoginInfo().username,
                                player.getUserID(),player.getName(),money,"交易输入的金钱");

                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("系统警告：交易的金钱数量输入错误，系统已记录你的行为！！！",
                                Warning.UI_TOOLTIP_TIP));

                        Exchange exchange = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                        if(exchange != null){
                            ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                            HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                            if(other != null){
                                ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),new Warning("系统警告：对方想骗你钱，系统自行取消交易"));
                            }
                        }

                        ExchangeDict.getInstance().exchangeCancel(exchangeID,player, null);

                        return;
                    }

                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);

                    if (exchange != null)
                    {
                        ExchangePlayer eplayer = exchange
                                            .getPlayerByNickname(player
                                                    .getName());
                        if(eplayer.locked){
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                            		new Warning("你已经锁定交易，不能添加金钱", Warning.UI_STRING_TIP));
                            break;
                        }

                        ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                        HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                        if(oplayer.locked){
                            ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), 
                            		new Warning("对方已经锁定交易，不能添加金钱", Warning.UI_STRING_TIP));
                            break;
                        }

                        HeroPlayer target = PlayerServiceImpl.getInstance()
                                .getPlayerByName(
                                        exchange.getTargetByNickname(player
                                                .getName()).nickname);
                        if (target != null && target.isEnable())
                        {
                            exchange.getPlayerByNickname(player.getName()).money = money;
                            ResponseMessageQueue.getInstance().put(
                                    target.getMsgQueueIndex(),
                                    new ResponseExchange(money));

                            return;
                        }
                    }

                    ExchangeDict.getInstance().exchangeCancel(exchangeID,
                            player, null);

                    break;
                }
                case Exchange.LIST_INVENTORY_GOODS:
                {
                    int exchangeID = yis.readInt();
                    byte goodsType = yis.readByte();
                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);

                    if (exchange == null)
                    {
                        ExchangeDict.getInstance().exchangeCancel(exchangeID,
                                player, null);

                        return;
                    }

                    AbsResponseMessage msg = null;
                    ExchangePlayer eplayer = exchange
                            .getPlayerByNickname(player.getName());

                    switch (goodsType)
                    {
                        case Z_B:
                        {
                            msg = new ResponseExchangeGoodsList(goodsType,
                                    ExchangeGoodsList.getData(player
                                            .getInventory().getEquipmentBag(),
                                            eplayer));

                            break;
                        }
                        case X_H_P:
                        {
                            msg = new ResponseExchangeGoodsList(goodsType,
                                    ExchangeGoodsList.getData(player
                                            .getInventory().getMedicamentBag(),
                                            eplayer));

                            break;
                        }
                        case C_L:
                        {
                            msg = new ResponseExchangeGoodsList(goodsType,
                                    ExchangeGoodsList.getData(player
                                            .getInventory().getMaterialBag(),
                                            eplayer));

                            break;
                        }
                        case T_S:
                        {
                            msg = new ResponseExchangeGoodsList(goodsType,
                                    ExchangeGoodsList.getData(player
                                            .getInventory()
                                            .getSpecialGoodsBag(), eplayer));

                            break;
                        }
                        case PET_EQUIP:
                        {
                            msg = new ResponseExchangeGoodsList(goodsType,
                                    ExchangeGoodsList.getData(player
                                            .getInventory().getPetEquipmentBag(), eplayer));

                            break;
                        }
                        case PET_GOODS:
                        {
                            msg = new ResponseExchangeGoodsList(goodsType,
                                    ExchangeGoodsList.getData(player
                                            .getInventory().getPetGoodsBag(), eplayer));

                            break;
                        }
                        case PET:
                        {
                            msg = new ResponseExchangeGoodsList(goodsType,
                                    ExchangeGoodsList.getData(player
                                            .getInventory().getPetContainer(), eplayer));

                            break;
                        }
                    }

                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);

                    break;
                }
                case Exchange.ADD_GOODS:
                {
                    int exchangeID = yis.readInt();
                    byte goodsType = yis.readByte();
                    int goodsID = yis.readInt();
                    short index = yis.readShort();
                    short goodsNum = yis.readShort();
                    log.debug("ADD_GOODS goodstype="+goodsType+",goodsID="+goodsID+",index="+index+",num="+goodsNum);

                    if(goodsNum<=0){
                        LogServiceImpl.getInstance().numberErrorLog(player.getLoginInfo().accountID,player.getLoginInfo().username,
                                player.getUserID(),player.getName(),goodsNum,"交易输入的物品数量,物品id["+goodsID+"]");

                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("系统警告：交易的物品数量输入错误，系统已记录你的行为！！！",
                                Warning.UI_TOOLTIP_TIP));

                        Exchange exchange = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                        if(exchange != null){
                            ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                            HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                            if(other != null){
                                ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),new Warning("系统警告：对方想骗你钱，系统自行取消交易"));
                            }
                        }

                        ExchangeDict.getInstance().exchangeCancel(exchangeID,player, null);


                        return;
                    }


                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);
                    
                    if (exchange == null)
                    {
                        ExchangeDict.getInstance().exchangeCancel(exchangeID,
                                player, null);

                        return;
                    }

                    ExchangePlayer eplayer = exchange
                                            .getPlayerByNickname(player
                                                    .getName());
                    if(eplayer.locked){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                        		new Warning("你已经锁定交易，不能添加物品", Warning.UI_STRING_TIP));
                        break;
                    }

                    ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                    HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                    if(oplayer.locked){
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), 
                        		new Warning("对方已经锁定交易，不能添加物品", Warning.UI_STRING_TIP));
                        break;
                    }

                    HeroPlayer target = PlayerServiceImpl.getInstance()
                            .getPlayerByName(oplayer.nickname);
                    switch (goodsType)
                    {
                        case Z_B:
                        {
                            EquipmentInstance ei = player.getInventory()
                                    .getEquipmentBag().getEquipmentList()[index];
                            if (null != ei && ei.getInstanceID() == goodsID)
                            {
                                if (ei.getArchetype().exchangeable() || ei.isBind())
                                {
                                    /*ExchangePlayer eplayer = exchange
                                            .getPlayerByNickname(player
                                                    .getName());*/
                                    if (eplayer.addExchangeGoods(index,
                                            goodsID, goodsNum,goodsType))
                                    {
                                        if (target != null && target.isEnable())
                                        {
                                            ResponseMessageQueue.getInstance().put(
                                                    target.getMsgQueueIndex(),
                                                    new ResponseExchange(ei));
                                            return;
                                        }
                                    }
                                }
                                else
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(Tip.REQUEST_EXCHANGE_NO_EXCHANGEABLE, 
                                            		Warning.UI_STRING_TIP));
                                    return;
                                }
                            }

                            break;
                        }
                        case X_H_P:
                        {
                            if (goodsID == player.getInventory()
                                    .getMedicamentBag().getAllItem()[index][0])
                            {
                                short num = (short) player.getInventory()
                                        .getMedicamentBag().getAllItem()[index][1];
                                if (num >= goodsNum)
                                {
                                    Goods goods = GoodsContents
                                            .getGoods(goodsID);
                                    if (goods.exchangeable())
                                    {
                                        /*ExchangePlayer eplayer = exchange
                                                .getPlayerByNickname(player
                                                        .getName());*/
                                        if (eplayer.addExchangeGoods(index,
                                                goodsID, goodsNum,goodsType))
                                        {
                                            if (target != null
                                                    && target.isEnable())
                                            {
                                                ResponseMessageQueue
                                                        .getInstance()
                                                        .put(
                                                                target
                                                                        .getMsgQueueIndex(),
                                                                new ResponseExchange(
                                                                        goods
                                                                                .getName(),
                                                                        goods
                                                                                .getIconID(),
                                                                        goodsNum,
                                                                        goods
                                                                                .getTrait(),
                                                                        goods
                                                                                .getDescription()));
                                                return;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                new Warning(Tip.REQUEST_EXCHANGE_NO_EXCHANGEABLE, 
                                                		Warning.UI_STRING_TIP));
                                        return;
                                    }
                                }
                            }

                            break;
                        }
                        case C_L:
                        {
                            if (goodsID == player.getInventory()
                                    .getMaterialBag().getAllItem()[index][0])
                            {
                                short num = (short) player.getInventory()
                                        .getMaterialBag().getAllItem()[index][1];
                                if (num >= goodsNum)
                                {
                                    Goods goods = GoodsContents
                                            .getGoods(goodsID);
                                    if (goods.exchangeable())
                                    {
                                        /*ExchangePlayer eplayer = exchange
                                                .getPlayerByNickname(player
                                                        .getName());*/
                                        if (eplayer.addExchangeGoods(index,
                                                goodsID, goodsNum,goodsType))
                                        {
                                            if (target != null
                                                    && target.isEnable())
                                            {
                                                ResponseMessageQueue
                                                        .getInstance()
                                                        .put(
                                                                target
                                                                        .getMsgQueueIndex(),
                                                                new ResponseExchange(
                                                                        goods
                                                                                .getName(),
                                                                        goods
                                                                                .getIconID(),
                                                                        goodsNum,
                                                                        goods
                                                                                .getTrait(),
                                                                        goods
                                                                                .getDescription()));
                                                return;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                new Warning(Tip.REQUEST_EXCHANGE_NO_EXCHANGEABLE, 
                                                		Warning.UI_STRING_TIP));
                                        return;
                                    }
                                }
                            }

                            break;
                        }
                        case T_S:
                        {
                            if (goodsID == player.getInventory()
                                    .getSpecialGoodsBag().getAllItem()[index][0])
                            {
                                short num = (short) player.getInventory()
                                        .getSpecialGoodsBag().getAllItem()[index][1];
                                if (num >= goodsNum)
                                {
                                    Goods goods = GoodsContents
                                            .getGoods(goodsID);
                                    if (goods.exchangeable())
                                    {
                                        /*ExchangePlayer eplayer = exchange
                                                .getPlayerByNickname(player
                                                        .getName());*/
                                        if (eplayer.addExchangeGoods(index,
                                                goodsID, goodsNum,goodsType))
                                        {
                                            if (target != null
                                                    && target.isEnable())
                                            {
                                                ResponseMessageQueue
                                                        .getInstance()
                                                        .put(
                                                                target
                                                                        .getMsgQueueIndex(),
                                                                new ResponseExchange(
                                                                        goods
                                                                                .getName(),
                                                                        goods
                                                                                .getIconID(),
                                                                        goodsNum,
                                                                        goods
                                                                                .getTrait(),
                                                                        goods
                                                                                .getDescription()));
                                                return;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                new Warning(Tip.REQUEST_EXCHANGE_NO_EXCHANGEABLE, 
                                                		Warning.UI_STRING_TIP));
                                        return;
                                    }
                                }
                            }
                            break;
                        }
                        case PET:
                        {
                            if(goodsID == player.getInventory().getPetContainer().getPet(index).id){
                                if(goodsNum == 1){ //宠物每次只能交易一只
                                    Pet pet = player.getInventory().getPetContainer().getPet(index);
                                    if(pet.bind == 0){
                                        /*ExchangePlayer eplayer = exchange
                                                .getPlayerByNickname(player
                                                        .getName());*/
                                        if (eplayer.addExchangeGoods(index,
                                                goodsID, goodsNum,goodsType))
                                        {

                                            ResponseMessageQueue.getInstance()
                                                    .put(target.getMsgQueueIndex(),
                                                            new ResponseExchange(
                                                                    pet.name,
                                                                    pet.iconID,
                                                                    (short)1,
                                                                    pet.trait,
                                                                    pet.name));
                                            return;

                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case PET_GOODS:
                        {
                            if (goodsID == player.getInventory()
                                    .getPetGoodsBag().getAllItem()[index][0])
                            {
                                short num = (short) player.getInventory()
                                        .getPetGoodsBag().getAllItem()[index][1];
                                if (num >= goodsNum)
                                {
                                    Goods goods = GoodsContents
                                            .getGoods(goodsID);
                                    if (goods.exchangeable())
                                    {
                                        /*ExchangePlayer eplayer = exchange
                                                .getPlayerByNickname(player
                                                        .getName());*/
                                        if (eplayer.addExchangeGoods(index,
                                                goodsID, goodsNum,goodsType))
                                        {
                                            if (target != null
                                                    && target.isEnable())
                                            {
                                                ResponseMessageQueue
                                                        .getInstance()
                                                        .put(
                                                                target
                                                                        .getMsgQueueIndex(),
                                                                new ResponseExchange(
                                                                        goods
                                                                                .getName(),
                                                                        goods
                                                                                .getIconID(),
                                                                        goodsNum,
                                                                        goods
                                                                                .getTrait(),
                                                                        goods
                                                                                .getDescription()));
                                                return;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                new Warning(Tip.REQUEST_EXCHANGE_NO_EXCHANGEABLE, 
                                                		Warning.UI_STRING_TIP));
                                        return;
                                    }
                                }
                            }
                            break;
                        }
                        case PET_EQUIP:
                        {
                            EquipmentInstance ei = player.getInventory()
                                    .getPetEquipmentBag().getEquipmentList()[index];
                            if (null != ei && ei.getInstanceID() == goodsID)
                            {
                                if (ei.getArchetype().exchangeable())
                                {
                                    /*ExchangePlayer eplayer = exchange
                                            .getPlayerByNickname(player
                                                    .getName());*/
                                    if (eplayer.addExchangeGoods(index,
                                            goodsID, goodsNum,goodsType))
                                    {
                                        if (target != null && target.isEnable())
                                        {
                                            ResponseMessageQueue.getInstance().put(
                                                    target.getMsgQueueIndex(),
                                                    new ResponseExchange(ei));
                                            return;
                                        }
                                    }
                                }
                                else
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(Tip.REQUEST_EXCHANGE_NO_EXCHANGEABLE, 
                                            		Warning.UI_STRING_TIP));
                                    return;
                                }
                            }

                            break;
                        }

                    }

                    ExchangeDict.getInstance().exchangeCancel(exchangeID,
                            player, target);

                    break;
                }
                case Exchange.EXCHANGE_LOCK:
                {
                	log.debug("EXCHANGE_LOCK .... ");
                    int exchangeID = yis.readInt();
                    Exchange exchange = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if(exchange == null){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("获取交易数据错误，取消！", 
                        		Warning.UI_STRING_TIP));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID,
                                player, null);
                        return;
                    }

                    ExchangePlayer eplayer = exchange
                            .getPlayerByNickname(player.getName());
                    eplayer.locked = true;

                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("你已锁定交易！", 
                    		Warning.UI_STRING_TIP));
                    
                    ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                    HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                    ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),new Warning("对方已锁定交易！", 
                    		Warning.UI_STRING_TIP));
                    
                    ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new ExchangeLockedGoodsList(eplayer));
//                    OutMsgQ.getInstance().put(player.getMsgQueueIndex(), new ExchangeLockedGoodsList(eplayer));
                    
                    ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),
                            new ResponseExchange(Exchange.EXCHANGE_LOCK));
                    log.debug("EXCHANGE_LOCK end ....");
                    break;

                }
                case Exchange.CONFIM:
                {
                    int exchangeID = yis.readInt();
                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);

                    if (exchange == null)
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("获取交易数据错误，取消！", 
                        		Warning.UI_STRING_TIP));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID,
                                player, null);
                        return;
                    }

                    ExchangePlayer eplayer = exchange
                            .getPlayerByNickname(player.getName());

                    ExchangePlayer oplayer = exchange
                            .getTargetByNickname(player.getName());

                    HeroPlayer target = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);

                    if( target != null && player.where().getID() != target.where().getID()){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_EXCHANGE_DISTANCE,Warning.UI_TOOLTIP_TIP));
                        break;
                    }

                    if(!eplayer.locked){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("请先锁定交易，然后再确认！", 
                        		Warning.UI_STRING_TIP));
                        break;
                    }


                    if(!oplayer.locked){
                        ResponseMessageQueue.getInstance().put(target.getMsgQueueIndex(),new Warning("请等待对方锁定交易，然后再确认！",
                        		Warning.UI_STRING_TIP));
                        break;
                    }

                    eplayer.state = Exchange.READY;
//                    HeroPlayer target = PlayerServiceImpl.getInstance()
//                            .getPlayerByName(oplayer.nickname);

                    if (target != null && target.isEnable())
                    {
                        if (oplayer.state == Exchange.READY)
                        {
                            if (canExchangeMoney(player, eplayer.money, target,
                                    oplayer.money)
                                    && canExchangeGoods(player, target,
                                            exchange))
                            {
                                StringBuffer _items = new StringBuffer();
                                StringBuffer _receiveItems = new StringBuffer();
                                for (int i = 0; i < eplayer.goodsID.length; i++)
                                {
                                    if (eplayer.goodsID[i] != 0)
                                    {
                                        if(eplayer.goodsType[i] != PET){
                                            Goods goods = GoodsContents
                                                    .getGoods(eplayer.goodsID[i]);
                                            if (goods == null)
                                            {
                                                EquipmentInstance ei = player
                                                        .getInventory()
                                                        .getEquipmentBag()
                                                        .getEquipmentList()[eplayer.gridIndex[i]];
                                                GoodsServiceImpl.getInstance()
                                                        .changeGoodsOwner(ei,
                                                                player, target,
                                                                CauseLog.EXCHANGE);
                                                _items.append(ei.getArchetype()
                                                        .getID());
                                                _items.append(",");
                                                _items.append(ei.getArchetype()
                                                        .getName());
                                                _items.append(",");
                                                _items.append(1);
                                                _items.append(";");
                                            }
                                            else
                                            {
                                                _items.append(goods.getID());
                                                _items.append(",");
                                                _items.append(goods.getName());
                                                _items.append(",");
                                                _items.append(1);
                                                _items.append(";");
                                                if (goods.getGoodsType() == EGoodsType.MATERIAL)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    player,
                                                                    player
                                                                            .getInventory()
                                                                            .getMaterialBag(),
                                                                    eplayer.gridIndex[i],
                                                                    eplayer.goodsNum[i],
                                                                    target,
                                                                    CauseLog.EXCHANGE);
                                                }
                                                else if (goods.getGoodsType() == EGoodsType.MEDICAMENT)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    player,
                                                                    player
                                                                            .getInventory()
                                                                            .getMedicamentBag(),
                                                                    eplayer.gridIndex[i],
                                                                    eplayer.goodsNum[i],
                                                                    target,
                                                                    CauseLog.EXCHANGE);
                                                }
                                                else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    player,
                                                                    player
                                                                            .getInventory()
                                                                            .getSpecialGoodsBag(),
                                                                    eplayer.gridIndex[i],
                                                                    eplayer.goodsNum[i],
                                                                    target,
                                                                    CauseLog.EXCHANGE);
                                                }
                                                else if(goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS)
                                                {
                                                    EquipmentInstance ei = player
                                                            .getInventory()
                                                            .getPetEquipmentBag()
                                                            .getEquipmentList()[eplayer.gridIndex[i]];
                                                    GoodsServiceImpl.getInstance()
                                                            .changeGoodsOwner(ei,
                                                                    player, target,
                                                                    CauseLog.EXCHANGE);
                                                    _items.append(ei.getArchetype()
                                                            .getID());
                                                    _items.append(",");
                                                    _items.append(ei.getArchetype()
                                                            .getName());
                                                    _items.append(",");
                                                    _items.append(1);
                                                    _items.append(";");
                                                }
                                                else if(goods.getGoodsType() == EGoodsType.PET_GOODS)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    player,
                                                                    player
                                                                            .getInventory()
                                                                            .getPetGoodsBag(),
                                                                    eplayer.gridIndex[i],
                                                                    eplayer.goodsNum[i],
                                                                    target,
                                                                    CauseLog.EXCHANGE);
                                                }
                                            }
                                        }else{
                                            // target 买了 player 的宠物 eplayer.goodsID[i]
                                            int res =PetServiceImpl.getInstance().transactPet(player.getUserID(), target.getUserID(), eplayer.goodsID[i]);
                                            if(res == 1){
                                                Pet pet = PetServiceImpl.getInstance().getPet(target.getUserID(),eplayer.goodsID[i]);
                                                ChatQueue.getInstance().addGoodsMsg(target, "获得了",
                                                        pet.name, pet.trait.getViewRGB(), 1);
                                            }
                                        }
                                    }
                                }

                                for (int i = 0; i < oplayer.goodsID.length; i++)
                                {
                                    if (oplayer.goodsID[i] != 0)
                                    {
                                        if(oplayer.goodsType[i] != PET){
                                            Goods goods = GoodsContents
                                                    .getGoods(oplayer.goodsID[i]);
                                            if (goods == null)
                                            {
                                                EquipmentInstance ei = target
                                                        .getInventory()
                                                        .getEquipmentBag()
                                                        .getEquipmentList()[oplayer.gridIndex[i]];
                                                GoodsServiceImpl.getInstance()
                                                        .changeGoodsOwner(ei,
                                                                target, player,
                                                                CauseLog.EXCHANGE);
                                                _receiveItems.append(ei
                                                        .getArchetype().getID());
                                                _receiveItems.append(",");
                                                _receiveItems.append(ei
                                                        .getArchetype().getName());
                                                _receiveItems.append(",");
                                                _receiveItems.append(1);
                                                _receiveItems.append(";");
                                            }
                                            else
                                            {
                                                _receiveItems.append(goods.getID());
                                                _receiveItems.append(",");
                                                _receiveItems.append(goods
                                                        .getName());
                                                _receiveItems.append(",");
                                                _receiveItems.append(1);
                                                _receiveItems.append(";");
                                                if (goods.getGoodsType() == EGoodsType.MATERIAL)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    target,
                                                                    target
                                                                            .getInventory()
                                                                            .getMaterialBag(),
                                                                    oplayer.gridIndex[i],
                                                                    oplayer.goodsNum[i],
                                                                    player,
                                                                    CauseLog.EXCHANGE);
                                                }
                                                else if (goods.getGoodsType() == EGoodsType.MEDICAMENT)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    target,
                                                                    target
                                                                            .getInventory()
                                                                            .getMedicamentBag(),
                                                                    oplayer.gridIndex[i],
                                                                    oplayer.goodsNum[i],
                                                                    player,
                                                                    CauseLog.EXCHANGE);
                                                }
                                                else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    target,
                                                                    target
                                                                            .getInventory()
                                                                            .getSpecialGoodsBag(),
                                                                    oplayer.gridIndex[i],
                                                                    oplayer.goodsNum[i],
                                                                    player,
                                                                    CauseLog.EXCHANGE);
                                                }
                                                else if(goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS)
                                                {
                                                    EquipmentInstance ei = target
                                                            .getInventory()
                                                            .getPetEquipmentBag()
                                                            .getEquipmentList()[oplayer.gridIndex[i]];
                                                    GoodsServiceImpl.getInstance()
                                                            .changeGoodsOwner(ei,
                                                                    target, player,
                                                                    CauseLog.EXCHANGE);
                                                    _receiveItems.append(ei
                                                            .getArchetype().getID());
                                                    _receiveItems.append(",");
                                                    _receiveItems.append(ei
                                                            .getArchetype().getName());
                                                    _receiveItems.append(",");
                                                    _receiveItems.append(1);
                                                    _receiveItems.append(";");
                                                }
                                                else if(goods.getGoodsType() == EGoodsType.PET_GOODS)
                                                {
                                                    GoodsServiceImpl
                                                            .getInstance()
                                                            .changeSingleGoodsOwner(
                                                                    (SingleGoods) goods,
                                                                    target,
                                                                    target
                                                                            .getInventory()
                                                                            .getPetGoodsBag(),
                                                                    oplayer.gridIndex[i],
                                                                    oplayer.goodsNum[i],
                                                                    player,
                                                                    CauseLog.EXCHANGE);
                                                }
                                            }
                                        }else{
                                            // player 买了 target 的宠物 oplayer.goodsID[i]
                                            int res =PetServiceImpl.getInstance().transactPet(target.getUserID(), player.getUserID(), oplayer.goodsID[i]);
                                            if(res == 1){
                                                Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(),oplayer.goodsID[i]);
                                                ChatQueue.getInstance().addGoodsMsg(player, "获得了",
                                                        pet.name, pet.trait.getViewRGB(), 1);
                                            }
                                        }
                                    }
                                }

                                if (0 != oplayer.money || 0 != eplayer.money)
                                {
                                    PlayerServiceImpl
                                            .getInstance()
                                            .addMoney(
                                                    player,
                                                    oplayer.money
                                                            - eplayer.money,
                                                    1,
                                                    PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                                    "交易");

                                    PlayerServiceImpl
                                            .getInstance()
                                            .addMoney(
                                                    target,
                                                    eplayer.money
                                                            - oplayer.money,
                                                    1,
                                                    PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
                                                    "交易");
                                }

                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ResponseExchange(
                                                Exchange.EXCHANGE_FINISH));
                                ResponseMessageQueue.getInstance().put(
                                        target.getMsgQueueIndex(),
                                        new ResponseExchange(
                                                Exchange.EXCHANGE_FINISH));
                                ExchangeDict.getInstance().removeExchangeByID(
                                        exchangeID);
                                ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(exchange.getRequestExchangeUserID());

                                player.swapOver();
                                target.swapOver();

                                // 交易日志
                                int _money = eplayer.money;
                                int _receiveMoney = oplayer.money;
                                LogServiceImpl.getInstance().tradeLog(
                                        player.getLoginInfo().accountID,
                                        player.getUserID(), player.getName(),
                                        player.getLoginInfo().loginMsisdn,
                                        _money, _items.toString(),
                                        target.getLoginInfo().accountID,
                                        target.getUserID(), target.getName(),
                                        target.getLoginInfo().loginMsisdn,
                                        _receiveMoney,
                                        _receiveItems.toString(),
                                        player.where().getName());
                            }
                            else
                            {
                                ExchangeDict.getInstance().exchangeCancel(
                                        exchangeID, player, target);
                            }

                            return;
                        }
                        else
                        {
                            player.swapOver();
                            target.swapOver();

                            ResponseMessageQueue.getInstance().put(
                                    target.getMsgQueueIndex(),
                                    new ResponseExchange(Exchange.CONFIM));

                            return;
                        }
                    }

                    ExchangeDict.getInstance().exchangeCancel(exchangeID,
                            player, target);

                    break;
                }
                case Exchange.EXCHANGE_CANCEL:
                {
                    int exchangeID = yis.readInt();
                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);
                    ExchangeDict.getInstance().removeExchangeByID(exchangeID);
                    if (exchange != null)
                    {
                        player.swapOver();

                    	ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(exchange.getRequestExchangeUserID());
                        HeroPlayer target = PlayerServiceImpl.getInstance()
                                .getPlayerByName(
                                        exchange.getTargetByNickname(player
                                                .getName()).nickname);
                        if (target != null && target.isEnable())
                        {
                            player.swapOver();
                            ResponseMessageQueue.getInstance().put(
                                    target.getMsgQueueIndex(),
                                    new ResponseExchange(
                                            Exchange.EXCHANGE_CANCEL));
                            return;
                        }
                    }

                    break;
                }
                case Exchange.EXCHANGE_BUSY:
                {
                    int exchangeID = yis.readInt();
                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);
                    ExchangeDict.getInstance().removeExchangeByID(exchangeID);
                    if (exchange != null)
                    {
                        player.swapOver();

                        HeroPlayer target = PlayerServiceImpl.getInstance()
                                .getPlayerByName(
                                        exchange.getTargetByNickname(player
                                                .getName()).nickname);
                        if (target != null && target.isEnable())
                        {
                            target.swapOver();

                            ResponseMessageQueue.getInstance().put(
                                    target.getMsgQueueIndex(),
                                    new ResponseExchange(
                                            Exchange.EXCHANGE_CANCEL));
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(Tip.REQUEST_EXCHANGE_PLAYER_BUSY, 
                                    		Warning.UI_STRING_TIP));
                            return;
                        }
                    }

                    break;
                }
                case Exchange.REMOVE_GOODS:
                {
                    int exchangeID = yis.readInt();
                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);

                    if (exchange == null)
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("交易数据错误，取消", 
                        		Warning.UI_STRING_TIP));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID,
                                player, null);

                        return;
                    }

                    ExchangePlayer eplayer = exchange
                                            .getPlayerByNickname(player
                                                    .getName());
                    if(eplayer.locked){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("你已经锁定交易，不能撤消物品", 
                        		Warning.UI_STRING_TIP));
                        break;
                    }

                    ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                    HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                    if(oplayer.locked){
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),new Warning("对方已经锁定交易，不能撤消物品", 
                        		Warning.UI_STRING_TIP));
                        break;
                    }

                    eplayer.removeExchangeGoods();
                    ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),
                            new ResponseExchange(Exchange.REMOVE_GOODS));
                    break;
                }
                case Exchange.REMOVE_SINGLE_GOODS:
                {
                	log.debug("REMOVE_SINGLE_GOODS.......");
                	int exchangeID = yis.readInt();
//                	byte goodsType = input.readByte();
                	short gridIndex = yis.readShort();
                	int goodsid = yis.readInt();
                	
                	log.debug(exchangeID + " -- " + gridIndex+" -- " + goodsid);
                	
                    Exchange exchange = ExchangeDict.getInstance()
                            .getExchangeByID(exchangeID);

                    if (exchange == null)
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("交易数据错误，取消", 
                        		Warning.UI_STRING_TIP));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID,
                                player, null);

                        return;
                    }

                    ExchangePlayer eplayer = exchange
                                            .getPlayerByNickname(player
                                                    .getName());
                    if(eplayer.locked){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("你已经锁定交易，不能撤消物品", 
                        		Warning.UI_STRING_TIP));
                        break;
                    }

                    ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                    HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                    if(oplayer.locked){
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),new Warning("对方已经锁定交易，不能撤消物品", 
                        		Warning.UI_STRING_TIP));
                        break;
                    }

                    eplayer.removeSingleExchangeGoods(gridIndex, goodsid);
                    ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),
                            new ResponseExchange(Exchange.REMOVE_SINGLE_GOODS,gridIndex));
                    break;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 能否交换金钱
     * 
     * @return
     */
    private boolean canExchangeMoney (HeroPlayer _player1, int _exchangeMoney1,
            HeroPlayer _player2, int _exchangeMoney2)
    {
        int moneyChange = _exchangeMoney2 - _exchangeMoney1;

        if (0 != moneyChange)
        {
            if (_player1.getMoney() + moneyChange > Constant.INTEGER_MAX_VALUE)
            {
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_MONEY_FULL, Warning.UI_STRING_TIP));
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_OTHER_MONEY_FULL, Warning.UI_STRING_TIP));

                return false;
            }
            else if (_player1.getMoney() + moneyChange < 0)
            {
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_MONEY_ENOUGH, Warning.UI_STRING_TIP));
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_OTHER_MONEY_ENOUGH, Warning.UI_STRING_TIP));

                return false;
            }

            moneyChange = _exchangeMoney1 - _exchangeMoney2;

            if (_player2.getMoney() + moneyChange > Constant.INTEGER_MAX_VALUE)
            {
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_MONEY_FULL, Warning.UI_STRING_TIP));
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_OTHER_MONEY_FULL, Warning.UI_STRING_TIP));

                return false;
            }
            else if (_player2.getMoney() + moneyChange < 0)
            {
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_MONEY_ENOUGH, Warning.UI_STRING_TIP));
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(),
                        new Warning(Tip.REQUEST_EXCHANGE_OTHER_MONEY_ENOUGH, Warning.UI_STRING_TIP));

                return false;
            }
        }

        return true;
    }

    /**
     * 能否交换物品
     * 
     * @param player1
     * @param player2
     * @param exchange
     * @return
     */
    private boolean canExchangeGoods (HeroPlayer player1, HeroPlayer player2,
            Exchange exchange)
    {
        int player1EquipmentNum = 0;
        int player1XhpNum = 0;
        int player1ClNum = 0;
        int player1TsNum = 0;
        int player1PetNum = 0;
        int player1PetGoodsNum =0;
        int player1PetEquipmentNum = 0;

        int player2EquipmentNum = 0;
        int player2XhpNum = 0;
        int player2ClNum = 0;
        int player2TsNum = 0;
        int player2PetNum = 0;
        int player2PetGoodsNum =0;
        int player2PetEquipmentNum = 0;

        ExchangePlayer eplayer1 = exchange.getPlayerByNickname(player1
                .getName());
        ExchangePlayer eplayer2 = exchange.getPlayerByNickname(player2
                .getName());
        for (int i = 0; i < eplayer1.goodsID.length; i++)
        {
            if (eplayer1.goodsID[i] != 0)
            {
                Goods goods = GoodsContents.getGoods(eplayer1.goodsID[i]);
                if (goods == null)
                {
                    // 装备
                    player1EquipmentNum++;
                }
                else if (goods.getGoodsType() == EGoodsType.MEDICAMENT)
                {
                    player1XhpNum++;
                }
                else if (goods.getGoodsType() == EGoodsType.MATERIAL)
                {
                    player1ClNum++;
                }
                else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
                {
                    player1TsNum++;
                }
                else if(goods.getGoodsType() == EGoodsType.PET){
                    player1PetNum ++;
                }
                else if(goods.getGoodsType() == EGoodsType.PET_GOODS){
                    player1PetGoodsNum ++;
                }
                else if(goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS){
                    player1PetEquipmentNum ++;
                }
            }
        }
        for (int i = 0; i < eplayer2.goodsID.length; i++)
        {
            if (eplayer2.goodsID[i] != 0)
            {
                Goods goods = GoodsContents.getGoods(eplayer2.goodsID[i]);
                if (goods == null)
                {
                    // 装备
                    player2EquipmentNum++;
                }
                else if (goods.getGoodsType() == EGoodsType.MEDICAMENT)
                {
                    player2XhpNum++;
                }
                else if (goods.getGoodsType() == EGoodsType.MATERIAL)
                {
                    player2ClNum++;
                }
                else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
                {
                    player2TsNum++;
                }
                else if(goods.getGoodsType() == EGoodsType.PET){
                    player2PetNum ++;
                }
                else if(goods.getGoodsType() == EGoodsType.PET_GOODS){
                    player2PetGoodsNum ++;
                }
                else if(goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS){
                    player2PetEquipmentNum ++;
                }
            }
        }
        if (player1.getInventory().getEquipmentBag().getEmptyGridNumber() < player2EquipmentNum)
        {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_EQUIPEMNT_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_EQUIPMENT_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if (player1.getInventory().getMaterialBag().getEmptyGridNumber() < player2ClNum)
        {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_MATERIAL_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_MATERIAL_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if (player1.getInventory().getMedicamentBag().getEmptyGridNumber() < player2XhpNum)
        {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_MEDICAMENT_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_MEDICAMENT_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if (player1.getInventory().getSpecialGoodsBag().getEmptyGridNumber() < player2TsNum)
        {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_SPECIAL_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_SPECIAL_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if(player1.getInventory().getPetContainer().getEmptyGridNumber() < player2PetNum){
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_PET_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_PET_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if(player1.getInventory().getPetGoodsBag().getEmptyGridNumber() < player2PetGoodsNum){
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_PET_GOODS_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_PET_GOODS_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if(player1.getInventory().getPetEquipmentBag().getEmptyGridNumber() < player2PetEquipmentNum){
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_PET_EQUIPMENT_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_PET_EQUIPMENT_FULL, Warning.UI_STRING_TIP));
            return false;
        }

        if (player2.getInventory().getEquipmentBag().getEmptyGridNumber() < player1EquipmentNum)
        {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_EQUIPEMNT_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_EQUIPMENT_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if (player2.getInventory().getMaterialBag().getEmptyGridNumber() < player1ClNum)
        {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_MATERIAL_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_MATERIAL_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if (player2.getInventory().getMedicamentBag().getEmptyGridNumber() < player1XhpNum)
        {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_MEDICAMENT_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_MEDICAMENT_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if (player2.getInventory().getSpecialGoodsBag().getEmptyGridNumber() < player1TsNum)
        {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_SPECIAL_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_SPECIAL_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if(player2.getInventory().getPetContainer().getEmptyGridNumber() < player1PetNum){
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_PET_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_PET_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if(player2.getInventory().getPetGoodsBag().getEmptyGridNumber() < player1PetGoodsNum){
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_PET_GOODS_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_PET_GOODS_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        if(player2.getInventory().getPetEquipmentBag().getEmptyGridNumber() < player1PetEquipmentNum){
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_PET_EQUIPMENT_FULL, Warning.UI_STRING_TIP));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(),
                    new Warning(Tip.REQUEST_EXCHANGE_OTHER_PET_EQUIPMENT_FULL, Warning.UI_STRING_TIP));
            return false;
        }
        return true;
    }
}
