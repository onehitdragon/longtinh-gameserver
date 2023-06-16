package hero.npc.function.system;

import hero.manufacture.Manufacture;
import hero.manufacture.ManufactureType;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.message.ManufNotify;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.manufacture.service.ManufactureServerImpl;
import hero.npc.Npc;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.NpcInteractiveResponse;
import hero.npc.service.NotPlayerServiceImpl;
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
import java.util.List;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


/**
 * 现在所有生活技能都从这个NPC学习，遗忘
 */
public class ManufNpc extends BaseNpcFunction
{
    /**
     * 学习过制造技能顶层操作菜单列表
     */
    private static final String[]      HAS_MANUF_MAIN_MENU_LIST               = {
            "学习技能", "升级", "遗忘"                                               };

    /**
     * 学习过制造技能顶层操作菜单图标列表
     */
    private static final short[]       HAS_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST = {
            1006, 1006, 1007                                                 };

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] hasManufMainMenuListOptionData         = new ArrayList[HAS_MANUF_MAIN_MENU_LIST.length];



    /**
     * 没有学习过制造技能顶层操作菜单列表
     */
    private static final String[]      NO_MANUF_MAIN_MENU_LIST                = {"训练" };

    /**
     * 没有学习过制造技能顶层操作菜单图标列表
     */
    private static final short[]       NO_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST  = {1006 };

    private static final String[]      MENU_LIST                              = {
            "查　　看", "学　　习"                                                   };














    enum Step
    {
        TOP(1), SKILL_LIST(2);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    public ManufNpc(int _hostNpcID)
    {
        super(_hostNpcID);
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        return ENpcFunctionType.MANUF_NPC;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {

        Npc _npc = NotPlayerServiceImpl.getInstance().getNpc(getHostNpcID());
        Manufacture _manuf = ManufactureServerImpl.getInstance()
                .getManufactureByUserIDAndNpcName(_player.getUserID(),_npc.getName());

        if (_manuf != null)
        {
            return optionList;
        }
        else
        {
            ArrayList<NpcHandshakeOptionData> handshakeOptionList = new ArrayList<NpcHandshakeOptionData>();
            NpcHandshakeOptionData optionData = null;

            for (int i = 0; i < NO_MANUF_MAIN_MENU_LIST.length; i++)
            {
                optionData = new NpcHandshakeOptionData();
//                optionData.miniImageID = NO_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST[i];
                optionData.miniImageID = getMinMarkIconID();
                optionData.optionDesc = NO_MANUF_MAIN_MENU_LIST[i];
                optionData.functionMark = getFunctionType().value()
                        * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i
                        + HAS_MANUF_MAIN_MENU_LIST.length;
                handshakeOptionList.add(optionData);
            }
            return handshakeOptionList;
        }
    }

    @Override
    public void initTopLayerOptionList ()
    {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_Confirm.getBytes(Tip.FUNCTION_LOVE_FORGET_SKILL));
        hasManufMainMenuListOptionData[2] = data1;

        for (int i = 0; i < HAS_MANUF_MAIN_MENU_LIST.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = HAS_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = HAS_MANUF_MAIN_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;
            data.followOptionData = hasManufMainMenuListOptionData[i];

            optionList.add(data);
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int _selectIndex,
            YOYOInputStream _content) throws Exception
    {
        Npc _npc = NotPlayerServiceImpl.getInstance().getNpc(getHostNpcID());
        Manufacture manuf = ManufactureServerImpl.getInstance()
                .getManufactureByUserIDAndNpcName(_player.getUserID(),_npc.getName());

        if (_step == Step.TOP.tag)
        {
            switch (_selectIndex)
            {
                case 0:
                {
                    if (manuf == null)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_NOT_STUDY_MANUF));
                        return;
                    }

                    ResponseMessageQueue
                            .getInstance()
                            .put(
                                    _player.getMsgQueueIndex(),
                                    new NpcInteractiveResponse(
                                            getHostNpcID(),
                                            optionList.get(_selectIndex).functionMark,
                                            Step.SKILL_LIST.tag,
                                            UI_AssistSkillList
                                                    .getManufSkillBytes(
                                                            MENU_LIST,
                                                            getCanStudys(manuf,_player),
                                                            manuf)));

                    break;
                }
                case 1:
                {
                    if (manuf == null)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_NOT_STUDY_MANUF));
                        return;
                    }

                   /* if (manuf.getLvl() == Manufacture.MAX_LVL)
                    {
                        OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(ALREADY_HIGH_LVL));
                        return;
                    }

                    if (manuf.getPoint() < ManufactureServerImpl.POINT_LIMIT[manuf
                            .getLvl() - 1])
                    {
                        OutMsgQ
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(
                                                SKILL_POINT_NOT_ENOUGH
                                                        + ManufactureServerImpl.POINT_LIMIT[manuf
                                                                .getLvl() - 1]));

                        return;
                    }
                    if (_player.getMoney() < ManufactureServerImpl.MONEY_OF_UPGRADE[manuf
                            .getLvl() - 1])
                    {
                        OutMsgQ
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(
                                                MONEY_NOT_ENOUGH
                                                        + ManufactureServerImpl.MONEY_OF_UPGRADE[manuf
                                                                .getLvl() - 1]));
                        return;
                    }

                    PlayerServiceImpl.getInstance().addMoney(
                            _player,
                            -ManufactureServerImpl.MONEY_OF_UPGRADE[manuf
                                    .getLvl() - 1], 1,
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                            "升级制造技能花费");
                    ManufactureServerImpl.getInstance().lvlUp(
                            _player.getUserID(), manuf);
                    OutMsgQ.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning(LVL_UP
                                    + ManufactureServerImpl.LEVEL_TITLE[manuf
                                            .getLvl() - 1]
                                    + manuf.getManufactureType().getName()));
                    autoStudy(_player, manuf);
                     */
                    break;
                }
                case 2:
                {
//                    if (manuf == null)
//                    {
//                        OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),
//                                new Warning(NOT_STUDY_MANUF));
//                        return;
//                    }

                    // 遗忘
                    List<Manufacture> oldManufList = ManufactureServerImpl.getInstance()
                            .forgetManufactureByUserID(_player.getUserID());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new ManufNotify(oldManufList));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_FORGET_SUCCESS));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new CloseUIMessage());
                    break;
                }
                default:
                {
                    if (manuf == null)
                    {
//                        Npc _npc = NotPlayerServiceImpl.getInstance().getNpc(
//                                this.getHostNpcID());
                        ManufactureType _type = ManufactureType.get(_npc
                                .getName());

                        /*if (_player.getMoney() < ManufactureServerImpl.FREIFHT_OF_NEW_SKILL)
                        {
                            OutMsgQ
                                    .getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new Warning(
                                                    MONEY_NOT_ENOUGH
                                                            + ManufactureServerImpl.FREIFHT_OF_NEW_SKILL));
                            return;
                        }*/

                        if (ManufactureServerImpl.getInstance()
                                .studyManufacture(_player, _type))
                        {
                            /*PlayerServiceImpl
                                    .getInstance()
                                    .addMoney(
                                            _player,
                                            -ManufactureServerImpl.FREIFHT_OF_NEW_SKILL,
                                            1,
                                            PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                                            "训练制造技能花费");*/

                            List<Manufacture> manufactureList = ManufactureServerImpl.getInstance().getManufactureListByUserID(_player.getUserID());

                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new ManufNotify(manufactureList));
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new CloseUIMessage());

//                            autoStudy(_player, _type, (byte) 1);  //现在不能自动学习技能条目
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning("无法训练两种制造技能"));

                        return;
                    }

                    break;
                }
            }
        }
        else if (_step == Step.SKILL_LIST.tag)
        {
            try
            {
                byte _index = _content.readByte();
                int _manufSkillID = _content.readInt();
                ManufSkill _manufSkill = ManufSkillDict.getInstance()
                        .getManufSkillByID(_manufSkillID);
                if (manuf == null)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NOT_STUDY_MANUF));
                    return;
                }
                if (_index == 0)
                {
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new FullScreenTip(_manufSkill.name,
                                    _manufSkill.desc));
                }
                else
                {
                    if (manuf.isStudyedManufSkillID(_manufSkillID))
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_ALREADY_LEARN));
                        return;
                    }

                    if(manuf.getPoint() < _manufSkill.needSkillPoint){
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(
                                		Tip.TIP_NPC_SKILL_POINT_NOT_ENOUGH[0] + _manufSkill.needSkillPoint+Tip.TIP_NPC_SKILL_POINT_NOT_ENOUGH[1]));
                        return;
                    }

                    if (_player.getMoney() < _manufSkill.money)
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new Warning(Tip.FUNCTION_LOVE_MONEY_NOT_ENOUGH
                                        + _manufSkill.money));
                        return;
                    }

                    PlayerServiceImpl.getInstance().addMoney(_player,
                            -_manufSkill.money, 1,
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,
                            "学习制造技能花费");

                    ManufactureServerImpl.getInstance().addManufSkillItem(
                            _player, _manufSkill, GetTypeOfSkillItem.LEARN);

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new NotifyListItemMessage(_step, false,
                                    _manufSkillID));
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
     * 学习技能条件：该大类技能的熟练度、玩家的等级、金钱
     * 现在不能自动学习技能条目
     * @param _player
     */
    private void autoStudy (HeroPlayer _player, Manufacture manuf)
    {
        Iterator<ManufSkill> iter = ManufSkillDict.getInstance()
                .getManufSkills();
        while (iter.hasNext())
        {
            ManufSkill _manufSkill = iter.next();
            if (_manufSkill.type == manuf.getManufactureType().getID()
                    && _manufSkill.needLevel <= _player.getLevel()
                    && _manufSkill.needSkillPoint <= manuf.getPoint()  && _manufSkill.npcStudy
                    && _manufSkill.money <= _player.getMoney())
            {
                ManufactureServerImpl.getInstance().addManufSkillItem(_player,
                        _manufSkill, GetTypeOfSkillItem.LEARN);
            }
        }
    }

    /**
     * 得到可以学习的制造技能列表
     * 
     * @param _manuf
     * @return
     */
    private ArrayList<ManufSkill> getCanStudys (Manufacture _manuf, HeroPlayer _player)
    {
        ArrayList<ManufSkill> list = new ArrayList<ManufSkill>();
        Iterator<ManufSkill> iter = ManufSkillDict.getInstance()
                .getManufSkills();
        
        while (iter.hasNext())
        {
            ManufSkill _manufSkill = iter.next();
            if (_manuf.getManufactureType().getID() == _manufSkill.type
                    && !_manuf.isStudyedManufSkillID(_manufSkill.id)
                    && _manuf.getPoint() >= _manufSkill.needSkillPoint&& _manufSkill.npcStudy
                    && _manufSkill.needLevel <= _player.getLevel()
                    &&  _manufSkill.money <= _player.getMoney())
            {
                list.add(_manufSkill);
            }
        }
        return list;
    }
}
