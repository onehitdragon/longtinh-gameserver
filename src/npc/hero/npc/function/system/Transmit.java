package hero.npc.function.system;

import hero.effect.service.EffectServiceImpl;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapServiceImpl;
import hero.npc.Npc;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.system.transmit.TransmitTargetMapInfo;
import hero.npc.function.system.transmit.MapTransmitInfoDict;
import hero.npc.message.NpcInteractiveResponse;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.task.service.TaskServiceImpl;
import hero.ui.UI_SelectOperationWithTip;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Transmit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:32:32
 * @描述 ：传送（提供地图列表并传送）
 */

public class Transmit extends BaseNpcFunction
{
    /**
     * 顶层操作菜单
     */
//    private final static String[] mainMenuList            = {"地图传送" };
	private final static String[] mainMenuList            = {"仙镜之门" };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]  mainMenuMarkImageIDList = {1010 };

    /**
     * NPC模板编号
     */
    private String                npcModelID;

    /**
     * @author DC
     */
    private static enum Step
    {
        TOP(1), TRANSMIT(2);

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
    public Transmit(int _npcID, String _npcModelID)
    {
        super(_npcID);
        // TODO Auto-generated constructor stub
        npcModelID = _npcModelID;
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.TRANSMIT;
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
        ArrayList<TransmitTargetMapInfo> list = MapTransmitInfoDict
                .getInstance().getTargetMapInfoList(npcModelID);

        if (_step == Step.TOP.tag)
        {
            if (list != null)
            {
                String[][] menuList = new String[list.size()][2];

                for (int i = 0; i < list.size(); i++)
                {
                    menuList[i][0] = list.get(i).getMapName();
                    menuList[i][1] = list.get(i).getDescription();
                }

                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getHostNpcID(), optionList
                                .get(_selectIndex).functionMark,
                                Step.TRANSMIT.tag, UI_SelectOperationWithTip
                                        .getBytes(Tip.TIP_NPC_OF_SELECT_DIST_MAP,
                                                menuList)));
            }
        }
        else if (_step == Step.TRANSMIT.tag)
        {
            if (!_player.isDead())
            {
                byte index = _content.readByte();

                if (list != null && index < list.size())
                {
                    TransmitTargetMapInfo targetMapInfo = list.get(index);

                    if (_player.getLevel() >= targetMapInfo.getNeedLevel())
                    {
                        if (_player.getMoney() >= targetMapInfo.getFreight())
                        {
                            Map currentMap = _player.where();
                            Map targetMap = MapServiceImpl.getInstance()
                                    .getNormalMapByID(targetMapInfo.getMapID());

                            if (null == targetMap
                                    || currentMap.getID() == targetMap.getID())
                            {
                                return;
                            }
                            else
                            {
                                PlayerServiceImpl
                                        .getInstance()
                                        .addMoney(
                                                _player,
                                                -targetMapInfo.getFreight(),
                                                1,
                                                PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,"传送花费");
                                _player.setCellX(targetMapInfo.getMapX());
                                _player.setCellY(targetMapInfo.getMapY());

                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new ResponseMapBottomData(_player,
                                                targetMap, currentMap));
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new ResponseMapGameObjectList(_player
                                                .getLoginInfo().clientType,
                                                targetMap));

                                _player.gotoMap(targetMap);
                                //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                                EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                                Npc escortNpc = _player.getEscortTarget();

                                if (null != escortNpc)
                                {
                                    TaskServiceImpl.getInstance()
                                            .endEscortNpcTask(_player,
                                                    escortNpc);
                                }
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_OF_NOT_ENOUGH_MONEY));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_OF_NEED_LEVEL
                                        + targetMapInfo.getNeedLevel()));
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_OF_DIE));
            }
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        return optionList;
    }
}
