package hero.npc.function.system;

import hero.item.Goods;
import hero.item.bag.SingleGoodsBag;
import hero.item.dictionary.ExchangeGoodsDict;
import hero.item.expand.ExchangeGoods;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.ui.UI_ExchangeItemList;
import hero.ui.UI_ExchangeMaterialList;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Exchange.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:33:32
 * @描述 ：兑换（使用材料兑换物品）
 */

public class Exchange extends BaseNpcFunction
{
    /**
     * 顶层操作菜单列表
     */
    private final static String[]    mainMenuList            = {"兑换" };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]     mainMenuMarkImageIDList = {1004 };

    /**
     * 装备交换操作菜单列表
     */
    private final static String[]    menuList                = {"查　　看", "所需材料", "兑　　换" };

    /**
     * 兑换物品列表
     */
    private ArrayList<ExchangeGoods> exchangeGoodsList;

    enum Step
    {
        TOP(1), LIST(2);

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
     * @param _goodsData
     */
    public Exchange(int _npcID, int[] _goodsData)
    {
        super(_npcID);
        initExchangeGoodsList(_goodsData);
    }

    /**
     * 初始化兑换物品
     * 
     * @param _goodsData
     */
    private void initExchangeGoodsList (int[] _goodsIDList)
    {
        exchangeGoodsList = new ArrayList<ExchangeGoods>();

        if (null != _goodsIDList && _goodsIDList.length > 0)
        {
            for (int goodsID : _goodsIDList)
            {
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
                        goodsID);

                if (null != goods)
                {
                    ExchangeGoods exchangeGoods = new ExchangeGoods(goods);

                    ArrayList<int[]> materialList = ExchangeGoodsDict
                            .getInstance().getMaterialList(goodsID);

                    for (int[] materialInfo : materialList)
                    {
                        exchangeGoods.addExchangeMaterial(materialInfo[0],
                                (short) materialInfo[1]);
                    }

                    exchangeGoodsList.add(exchangeGoods);
                }
            }
        }
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.EXCHANGE;
    }

    @Override
    public void initTopLayerOptionList ()
    {
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

    @Override
    public void process (HeroPlayer _player, byte _step, int _selectIndex,
            YOYOInputStream _content) throws Exception
    {
        if (Step.TOP.tag == _step)
        {
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new NpcInteractiveResponse(getHostNpcID(), optionList
                            .get(_selectIndex).functionMark, Step.LIST.tag,
                            UI_ExchangeItemList.getBytes(menuList,
                                    exchangeGoodsList, _player.getInventory()
                                            .getMaterialBag())));
        }
        else if (Step.LIST.tag == _step)
        {
            try
            {
                byte optionIndex = _content.readByte();
                int goodsID = _content.readInt();

                if (optionIndex == 1)
                {
                    ExchangeGoods exchangeGoods = null;

                    for (int i = 0; i < exchangeGoodsList.size(); i++)
                    {
                        if (exchangeGoodsList.get(i).getGoodeModel().getID() == goodsID)
                        {
                            exchangeGoods = exchangeGoodsList.get(i);

                            break;
                        }
                    }

                    if (null != exchangeGoods)
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new NpcInteractiveResponse(
                                                getHostNpcID(),
                                                optionList.get(_selectIndex).functionMark,
                                                Step.LIST.tag,
                                                UI_ExchangeMaterialList
                                                        .getBytes(
                                                                exchangeGoods,
                                                                _player
                                                                        .getInventory()
                                                                        .getMaterialBag())));
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_NONE_GOODS));
                    }
                }
                else if (optionIndex == 2)
                {
                    ExchangeGoods exchangeGoods = null;

                    for (int i = 0; i < exchangeGoodsList.size(); i++)
                    {
                        if (exchangeGoodsList.get(i).getGoodeModel().getID() == goodsID)
                        {
                            exchangeGoods = exchangeGoodsList.get(i);

                            break;
                        }
                    }

                    if (null != exchangeGoods)
                    {
                        if (canExchange(
                                _player.getInventory().getMaterialBag(),
                                exchangeGoods.getMaterialList()))
                        {
                            if (null != GoodsServiceImpl.getInstance()
                                    .addGoods2Package(_player,
                                            exchangeGoods.getGoodeModel(), 1,
                                            CauseLog.EXCHANGE))
                            {
                                removeExchangeMaterial(_player, _player
                                        .getInventory().getMaterialBag(),
                                        exchangeGoods.getMaterialList());

                                ResponseMessageQueue
                                        .getInstance()
                                        .put(
                                                _player.getMsgQueueIndex(),
                                                new NpcInteractiveResponse(
                                                        getHostNpcID(),
                                                        optionList
                                                                .get(_selectIndex).functionMark,
                                                        Step.LIST.tag,
                                                        UI_ExchangeItemList
                                                                .getBytes(
                                                                        menuList,
                                                                        exchangeGoodsList,
                                                                        _player
                                                                                .getInventory()
                                                                                .getMaterialBag())));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_NOT_ENOUGH_MATERIAL));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_NONE_GOODS));
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否满足兑换物品需要的材料
     * 
     * @param _goods
     * @return
     */
    private boolean canExchange (SingleGoodsBag _materialBag,
            ArrayList<int[]> _materialList)
    {
        boolean materialEnough = true;

        for (int[] materialInfo : _materialList)
        {
            if (_materialBag.getGoodsNumber(materialInfo[0]) / materialInfo[1] == 0)
            {
                materialEnough = false;

                break;
            }
        }

        return materialEnough;
    }

    /**
     * 删除玩家背包中兑换物品需要的材料
     * 
     * @param _player
     * @param _materialBag
     * @param _materialList
     */
    private void removeExchangeMaterial (HeroPlayer _player,
            SingleGoodsBag _materialBag, ArrayList<int[]> _materialList)
    {
        try
        {
            for (int[] materialInfo : _materialList)
            {
                GoodsServiceImpl.getInstance().deleteSingleGoods(_player,
                        _materialBag, materialInfo[0], materialInfo[1],
                        CauseLog.CONVERT);
            }
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        return optionList;
    }




}
