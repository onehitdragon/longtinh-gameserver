package hero.npc.function.system;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import hero.guild.Guild;
import hero.guild.service.GuildServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.share.service.Tip;
import hero.ui.UI_Confirm;
import hero.ui.UI_GuildMemberList;
import hero.ui.UI_InputString;
import hero.ui.message.CloseUIMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildManager.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-4 下午01:39:13
 * @描述 ：
 */

public class GuildManager extends BaseNpcFunction
{


    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]       mainMenuMarkImageIDList = {
            1008, 1008, 1008                                  };







    private static final String[]      menuList                = new String[]{
            "下　　页", "上　　页", "确　　定"                            };

    private static ArrayList<byte[]>[] guildMenuOptionData     = new ArrayList[Tip.FUNCTION_MAIN_MENU_LIST.length];

    enum Step
    {
        TOP(1), VIEW_LIST(2), TRANSFER(3);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    public GuildManager(int _hostNpcID)
    {
        super(_hostNpcID);
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.GUILD_MANAGE;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        ArrayList<NpcHandshakeOptionData> playerOptionList = new ArrayList<NpcHandshakeOptionData>();

        if (_player.getGuildID() == 0)
        {
            playerOptionList.add(optionList.get(0));
        }
        else
        {
            Guild guild = GuildServiceImpl.getInstance().getGuild(
                    _player.getGuildID());

            if (null != guild
                    && guild.getPresident().userID == _player.getUserID())
            {
                playerOptionList.add(optionList.get(1));
//                playerOptionList.add(optionList.get(2));//删除帮派转让功能
            }
        }

        return playerOptionList;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_InputString.getBytes(Tip.TIP_NPC_GUILD_NAME, 2, 6));
        guildMenuOptionData[0] = data1;

        data1 = new ArrayList<byte[]>();
        data1.add(UI_Confirm.getBytes(Tip.TIP_NPC_DROP_GUILD));
        guildMenuOptionData[1] = data1;

        for (int i = 0; i < Tip.FUNCTION_MAIN_MENU_LIST.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = Tip.FUNCTION_MAIN_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);

            if (null != guildMenuOptionData[i])
            {
                data.followOptionData = new ArrayList<byte[]>(
                        guildMenuOptionData[i].size());

                for (byte[] b : guildMenuOptionData[i])
                {
                    data.followOptionData.add(b);
                }
            }
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content)
    {
        // TODO Auto-generated method stub
        try
        {
            if (_step == Step.TOP.tag)
            {
                switch (selectIndex)
                {
                    case OPERATION_OF_CREATE_GUILD:
                    {
                        String name = _content.readUTF();

                        if (GuildServiceImpl.getInstance().createGuild(_player,
                                name))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new CloseUIMessage());
                        }

                        break;
                    }
                    case OPERATION_OF_DISBAND_GUILD:
                    {
                        if (GuildServiceImpl.getInstance()
                                .disbandGuild(_player))
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new CloseUIMessage());
                        }

                        break;
                    }
                    case OPERATION_OF_TRANSFER_PRESIDENT:
                    {
                        Guild guild = GuildServiceImpl.getInstance().getGuild(
                                _player.getGuildID());

                        if (guild != null
                                && guild.getPresident().userID == _player
                                        .getUserID())
                        {
                            ResponseMessageQueue
                                    .getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new NpcInteractiveResponse(
                                                    getHostNpcID(),
                                                    optionList.get(selectIndex).functionMark,
                                                    (byte) 2,
                                                    UI_GuildMemberList
                                                            .getBytes(
                                                                    menuList,
                                                                    guild.getMemberList(1),
                                                                    guild.getMemberNumber(),
                                                                    guild.GetMaxMemberNumber(),
                                                                    1,
                                                                    guild.getViewPageNumber())));
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new CloseUIMessage());
                        }

                        break;
                    }
                }
            }
            else if (_step == Step.VIEW_LIST.tag)
            {
                byte menuIndex = _content.readByte();

                switch (menuIndex)
                {
                    case 0:
                    case 1:
                    {
                        byte page = _content.readByte();

                        Guild guild = GuildServiceImpl.getInstance().getGuild(
                                _player.getGuildID());

                        if (guild != null
                                && guild.getPresident().userID == _player
                                        .getUserID())
                        {
                            ResponseMessageQueue
                                    .getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new NpcInteractiveResponse(
                                                    getHostNpcID(),
                                                    optionList.get(selectIndex).functionMark,
                                                    (byte) 2,
                                                    UI_GuildMemberList
                                                            .getBytes(
                                                                    menuList,
                                                                    guild.getMemberList(page),
                                                                    guild.getMemberNumber(),
                                                                    guild.GetMaxMemberNumber(),
                                                                    page,
                                                                    guild.getViewPageNumber())));
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new CloseUIMessage());
                        }

                        break;
                    }
                    case 2:
                    {
                        int targetMemberUserID = _content.readInt();

                        Guild guild = GuildServiceImpl.getInstance().getGuild(
                                _player.getGuildID());

                        if (null != guild)
                        {
                            if (guild.getPresident().userID != _player
                                    .getUserID())
                            {
                                GuildServiceImpl.getInstance()
                                        .transferPresident(guild, _player,
                                                targetMemberUserID);

                                return;
                            }

                            if (targetMemberUserID == _player.getUserID())
                            {
                                return;
                            }

                            GuildServiceImpl.getInstance().transferPresident(
                                    guild, _player, targetMemberUserID);
                        }

                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new CloseUIMessage());

                        break;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 操作-创建公会
     */
    private static final byte OPERATION_OF_CREATE_GUILD       = 0;

    /**
     * 操作-解散公会
     */
    private static final byte OPERATION_OF_DISBAND_GUILD      = 1;

    /**
     * 操作-转让会长
     */
    private static final byte OPERATION_OF_TRANSFER_PRESIDENT = 2;
}
