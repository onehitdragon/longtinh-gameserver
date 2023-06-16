package hero.npc.function.system;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.lover.service.LoverServiceImpl;
import hero.lover.service.LoverServiceImpl.LoverStatus;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.player.HeroPlayer;
import hero.player.define.ESex;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.share.message.MailStatusChanges;
import hero.share.message.Warning;
import hero.share.service.DateFormatter;
import hero.share.service.Tip;
import hero.ui.UI_Confirm;
import hero.ui.UI_InputString;

/**
 * 大榕树
 * 
 * @author Luke 陈路
 * @date Jul 28, 2009
 *
 * 现在不用大榕树了 --- 2010-11-10
 */
public class LoverTree extends BaseNpcFunction
{
    /**
     * 与客户端约定代替所签的名字
     */
    private static final String        SPName                  = "#FNAME";

    /**
     * 任务ID
     */
    private static final int[]         taskID                  = {
            0, 60188, 60194                                   };                                   // 龙山,河姆渡



    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]       mainMenuMarkImageIDList = {1008 };



    private static ArrayList<byte[]>[] loverMenuOptionData     = new ArrayList[Tip.FUNCTION_LOVE_MAIN_MENU_LIST.length];

    public LoverTree(int npcID)
    {
        super(npcID);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.LOVER_TREE;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        ArrayList<NpcHandshakeOptionData> temp = new ArrayList<NpcHandshakeOptionData>();
        short index = _player.getClan().getID();
        // if(TaskServiceImpl.getInstance().hasCompleteTask(_player.getUserID(),
        // taskID[index]))
        // {
        temp.add(optionList.get(0));
        // }
        return temp;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_InputString.getBytes(Tip.FUNCTION_INPUT));
        loverMenuOptionData[0] = data1;

        for (int i = 0; i < Tip.FUNCTION_LOVE_MAIN_MENU_LIST.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = Tip.FUNCTION_LOVE_MAIN_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);

            if (null != loverMenuOptionData[i])
            {
                data.followOptionData = new ArrayList<byte[]>(
                        loverMenuOptionData[i].size());

                for (byte[] b : loverMenuOptionData[i])
                {
                    data.followOptionData.add(loverMenuOptionData[i].get(0));
                    data.followOptionData.add(UI_Confirm.getBytes("确认与"
                            + SPName + "签名吗？签名后7天内无法修改"));
                }
            }
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        if (_step == 1)
        {
            switch (selectIndex)
            {
                case 0:
                {
                    String otherName = _content.readUTF();

                    if (otherName.equals(_player.getName()))
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning("名称不能为自己"));

                        return;
                    }
                    else
                    {
                        HeroPlayer other = PlayerServiceImpl.getInstance()
                                .getPlayerByName(otherName);

                        if (null == other || other.isEnable())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("找不到在线玩家‘" + otherName + "’"));

                            return;
                        }

                        if (other.getSex() == _player.getSex())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("对方性别相同"));

                            return;
                        }

                        if (other.getClan() != _player.getClan())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("对方阵营不同"));

                            return;
                        }

                        LoverStatus status = LoverServiceImpl
                                .getInstance()
                                .registerLoverTree(_player.getName(), otherName);

                        if (status == LoverStatus.SUCCESS)
                        {
                            Letter letterOfRegistor = new Letter(Letter.SYSTEM_TYPE,LetterService
                                    .getInstance().getUseableLetterID(),
                                    "纯真的爱情", "大榕树", _player.getUserID(),
                                    _player.getName(),
                                    "这是一份最简单，最直接的爱情，你们几乎同时向我表达了与对方的感情，这感动了我，作为神域世界中爱情的守护者，我宣布你和‘"
                                            + otherName + "’就在这一刻订婚");
                            LetterService.getInstance().addNewLetter(
                                    letterOfRegistor);

                            Letter letterOfOther = new Letter(Letter.SYSTEM_TYPE,LetterService
                                    .getInstance().getUseableLetterID(),
                                    "纯真的爱情", "大榕树", other.getUserID(),
                                    otherName,
                                    "这是一份最简单，最直接的爱情，你们几乎同时向我表达了与对方的感情，这感动了我，作为神域世界中爱情的守护者，我宣布你和‘"
                                            + _player.getName() + "’就在这一刻订婚");
                            LetterService.getInstance().addNewLetter(
                                    letterOfOther);

                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("你已与‘" + otherName + "’订婚"));

                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new MailStatusChanges(
                                            MailStatusChanges.TYPE_OF_LETTER,
                                            true));

                            ResponseMessageQueue.getInstance().put(
                                    other.getMsgQueueIndex(),
                                    new Warning("你已与‘" + _player.getName()
                                            + "’订婚"));

                            ResponseMessageQueue.getInstance().put(
                                    other.getMsgQueueIndex(),
                                    new MailStatusChanges(
                                            MailStatusChanges.TYPE_OF_LETTER,
                                            true));
                        }
                        else if (status == LoverStatus.ME_SUCCESSED)
                        {
                            // 已有恋人
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("你已订婚"));
                        }
                        else if (status == LoverStatus.THEM_SUCCESSED)
                        {
                            // 已有恋人
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(otherName + "已订婚"));
                        }
                        else if (status == LoverStatus.REGISTERED)
                        {
                            // 已登记过
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("你已登记过了"));
                        }
                        else if (status == LoverStatus.REGISTER)
                        {
                            // 登记成功
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("登记成功"));

                            Letter letter = new Letter(Letter.SYSTEM_TYPE,
                                    LetterService.getInstance()
                                            .getUseableLetterID(),
                                    "心灵的传递",
                                    "大榕树",
                                    other.getUserID(),
                                    otherName,
                                    "就在"
                                            + DateFormatter.currentTime()
                                            + "，"
                                            + _player.getName()
                                            + "在我的身旁默念着你的名字，"
                                            + (_player.getSex() == ESex.Male ? "他"
                                                    : "她")
                                            + "向我倾述着对你的爱慕，如果你对他也有同样 的感觉请来绿野广场在我的身旁呼唤他"
                                            + (_player.getSex() == ESex.Male ? "他"
                                                    : "她") + "的名字");

                            LetterService.getInstance().addNewLetter(letter);
                            ResponseMessageQueue.getInstance().put(
                                    other.getMsgQueueIndex(),
                                    new MailStatusChanges(
                                            MailStatusChanges.TYPE_OF_LETTER,
                                            true));
                        }
                    }
                    break;
                }
            }
        }
    }
}
