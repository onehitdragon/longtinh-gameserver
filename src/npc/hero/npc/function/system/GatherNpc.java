package hero.npc.function.system;

import hero.gather.Gather;
import hero.gather.dict.Refined;
import hero.gather.dict.RefinedDict;
import hero.gather.message.GatherSkillNotify;
import hero.gather.message.GourdNotify;
import hero.gather.service.GatherServerImpl;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.FullScreenTip;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.ui.UI_AssistSkillList;
import hero.ui.UI_Confirm;
import hero.ui.message.CloseUIMessage;
import hero.ui.message.NotifyListItemMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


public class GatherNpc extends BaseNpcFunction
{
    /**
     * 学习过采集技能顶层操作菜单列表
     */
    private static final String[]      HAS_GATHER_MAIN_MENU_LIST               = {"学习技能", "升级", "遗忘" };

    /**
     * 学习过采集技能顶层操作菜单图标列表
     */
    private static final short[]       HAS_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST = {1006, 1006, 1007 };

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] hasGatherMainMenuListOptionData         = new ArrayList[HAS_GATHER_MAIN_MENU_LIST.length];

    /**
     * 没有学习过采集技能顶层操作菜单列表
     */
    private static final String[]      NO_GATHER_MAIN_MENU_LIST                = {"训练采集" };

    /**
     * 没有学习过采集技能顶层操作菜单图标列表
     */
    private static final byte[]        NO_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST  = {1 };

    private static final String[]      MENU_LIST                               = {"查　　看", "学　　习" };

    enum Step
    {
        TOP(1), SKILL_LIST(2);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    public GatherNpc(int _hostNpcID)
    {
        super(_hostNpcID);
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        return ENpcFunctionType.GATHER_NPC;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        Gather _gather = GatherServerImpl.getInstance().getGatherByUserID(
                _player.getUserID());
        if (_gather != null) return optionList;
        else
        {
            ArrayList<NpcHandshakeOptionData> handshakeOptionList = new ArrayList<NpcHandshakeOptionData>();
            NpcHandshakeOptionData optionData = null;
            for (int i = 0; i < NO_GATHER_MAIN_MENU_LIST.length; i++)
            {
                optionData = new NpcHandshakeOptionData();
//                optionData.miniImageID = NO_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST[i];
                optionData.miniImageID = getMinMarkIconID();
                optionData.optionDesc = NO_GATHER_MAIN_MENU_LIST[i];
                optionData.functionMark = getFunctionType().value()
                        * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i
                        + HAS_GATHER_MAIN_MENU_LIST.length;
                handshakeOptionList.add(optionData);
            }
            return handshakeOptionList;
        }
    }

    @Override
    public void initTopLayerOptionList ()
    {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_Confirm.getBytes(Tip.TIP_GATHER_GORGET_SKILL));
        hasGatherMainMenuListOptionData[2] = data1;
        for (int i = 0; i < HAS_GATHER_MAIN_MENU_LIST.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = HAS_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = HAS_GATHER_MAIN_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;
            data.followOptionData = hasGatherMainMenuListOptionData[i];
            optionList.add(data);
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int _selectIndex,
            YOYOInputStream _content) throws Exception
    {
        if (_step == Step.TOP.tag)
        {
            switch (_selectIndex)
            {
                case 0:
                {
                    Gather _gather = GatherServerImpl.getInstance()
                            .getGatherByUserID(_player.getUserID());
                    if (_gather == null)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GATHER_NOT_STUDY_GATHER));
                        return;
                    }
                    // 可学技能列表
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new NpcInteractiveResponse(getHostNpcID(),
                                    optionList.get(_selectIndex).functionMark,
                                    Step.SKILL_LIST.tag, UI_AssistSkillList
                                            .getRefinedBytes(MENU_LIST,
                                                    getCanStudys(_gather))));
                    break;
                }
                case 1:
                {
                    // 升级
                    Gather _gather = GatherServerImpl.getInstance()
                            .getGatherByUserID(_player.getUserID());
                    if (_gather == null)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GATHER_NOT_STUDY_GATHER));
                        return;
                    }
                    if (_gather.getLvl() >= Gather.MAX_LVL)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GATHER_ALREADY_HIGH_LVL));
                        return;
                    }

                    if (_gather.getPoint() < GatherServerImpl.POINT_LIMIT[_gather
                            .getLvl() - 1])
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GATHER_SKILL_POINT_NOT_ENOUGH
                                        + GatherServerImpl.POINT_LIMIT[_gather
                                                .getLvl() - 1]));

                        return;
                    }

                    if (_player.getMoney() < GatherServerImpl.MONEY_OF_UPGRADE[_gather
                            .getLvl() - 1])
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(
                                                Tip.TIP_GATHER_MONEY_NOT_ENOUGH
                                                        + GatherServerImpl.MONEY_OF_UPGRADE[_gather
                                                                .getLvl() - 1]));
                        return;
                    }

                    PlayerServiceImpl.getInstance()
                            .addMoney(
                                    _player,
                                    -GatherServerImpl.MONEY_OF_UPGRADE[_gather
                                            .getLvl() - 1], 1,
                                    PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                                    "升级采集技能花费");
                    GatherServerImpl.getInstance().lvlUp(_player.getUserID(),
                            _gather);
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_GATHER_LVL_UP + Tip.GATHER_LEVEL_TITLE[_gather
                                            .getLvl() - 1] + Tip.TIP_GATHER_SKILL));
                    autoStudy(_player, _gather.getLvl());
                    break;
                }
                case 2:
                {
                    // 遗忘
                    GatherServerImpl.getInstance().forgetGatherByUserID(
                            _player.getUserID());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new GatherSkillNotify(false));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new GourdNotify(false));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_GATHER_FORGET_SUCCESS));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new CloseUIMessage());
                    int gourdID = GatherServerImpl.getInstance().getGourdID(
                            _player);
                    if (gourdID > 0)
                    {
                        try
                        {
                            _player.getInventory().getSpecialGoodsBag().remove(
                                    gourdID);
                        }
                        catch (BagException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    // 删除快捷键
                    PlayerServiceImpl.getInstance().deleteShortcutKey(_player,
                            PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SKILL,
                            GatherServerImpl.SUCK_SOUL_SKILL_ID);
                    break;
                }
                case 3:
                {
                    // 训练
                    if (_player.getMoney() < GatherServerImpl.FREIFHT_OF_NEW_SKILL)
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(
                                                Tip.TIP_GATHER_MONEY_NOT_ENOUGH
                                                        + GatherServerImpl.FREIFHT_OF_NEW_SKILL));
                        return;
                    }

                    if (GatherServerImpl.getInstance().getGatherByUserID(
                            _player.getUserID()) == null)
                    {
                        if (GatherServerImpl.getInstance().studyGather(
                                _player.getUserID()))
                        {
                            PlayerServiceImpl.getInstance().addMoney(_player,
                                    -GatherServerImpl.FREIFHT_OF_NEW_SKILL, 1,
                                    PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                                    "训练采集技能花费");
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new GatherSkillNotify(true));
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new GourdNotify(true));
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new CloseUIMessage());
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_GATHER_GET_BASIC_SKILL));
                            autoStudy(_player, (byte) 1);
                            GoodsServiceImpl.getInstance().addGoods2Package(
                                    _player, GoodsContents.getGoods(50001), 1,
                                    CauseLog.GATHER);
                        }
                    }

                    break;
                }
            }
        }
        else if (_step == Step.SKILL_LIST.tag)
        {
            // 玩家学习技能
            try
            {
                byte _index = _content.readByte();
                int _refinedID = _content.readInt();
                Gather _gather = GatherServerImpl.getInstance()
                        .getGatherByUserID(_player.getUserID());
                Refined refined = RefinedDict.getInstance().getRefinedByID(
                        _refinedID);
                if (_gather == null)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_GATHER_NOT_STUDY_GATHER));
                    return;
                }
                if (_index == 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new FullScreenTip(refined.name, refined.desc));
                }
                else
                {
                    if (_gather.isStudyedRefinedID(_refinedID))
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GATHER_ALREADY_LEARN));
                        return;
                    }
                    if (_player.getMoney() < refined.money)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_GATHER_MONEY_NOT_ENOUGH + refined.money));
                        return;
                    }
                    PlayerServiceImpl.getInstance().addMoney(_player,
                            -refined.money, 1,
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                            "学习采集技能花费");
                    GatherServerImpl.getInstance().addRefinedItem(_player,
                            refined, GetTypeOfSkillItem.LEARN);

                    ResponseMessageQueue.getInstance()
                            .put(
                                    _player.getMsgQueueIndex(),
                                    new NotifyListItemMessage(_step, false,
                                            _refinedID));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 自动学习技能
     * 
     * @param _player
     */
    private void autoStudy (HeroPlayer _player, byte _lvl)
    {
        Iterator<Refined> iter = RefinedDict.getInstance().getRefineds();

        while (iter.hasNext())
        {
            Refined _refined = iter.next();

            if (_refined.needLvl == _lvl && _refined.npcStudy
                    && _refined.money == 0)
            {
                GatherServerImpl.getInstance().addRefinedItem(_player,
                        _refined, GetTypeOfSkillItem.LEARN);
            }
        }
    }

    /**
     * 得到可以学习的炼化技能列表
     * 
     * @param _gather
     * @return
     */
    private ArrayList<Refined> getCanStudys (Gather _gather)
    {
        ArrayList<Refined> list = new ArrayList<Refined>();
        Iterator<Refined> iter = RefinedDict.getInstance().getRefineds();
        while (iter.hasNext())
        {
            Refined _refined = iter.next();
            if (!_gather.isStudyedRefinedID(_refined.id)
                    && _gather.getLvl() >= _refined.needLvl)
            {
                list.add(_refined);
            }
        }
        return list;
    }

}
