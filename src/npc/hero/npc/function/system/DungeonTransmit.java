package hero.npc.function.system;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import hero.dungeon.Dungeon;
import hero.dungeon.DungeonDataModel;
import hero.dungeon.service.DungeonDataModelDictionary;
import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.item.service.GoodsServiceImpl;
import hero.map.Map;
import hero.map.message.ResponseAnimalInfoList;
import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapElementList;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapMiniImage;
import hero.map.message.ResponseSceneElement;
import hero.map.service.MiniMapImageDict;
import hero.npc.Npc;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.task.service.TaskServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DungeonTransmit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午05:45:38
 * @描述 ：传送玩家进入副本
 */

public class DungeonTransmit extends BaseNpcFunction
{
    /**
     * 顶层操作菜单列表
     */
    private static final String[] mainMenuList            = {
            "进入（难度－简单）", "进入（难度－困难）"                     };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]  mainMenuMarkImageIDList = {1011, 1011 };

    /**
     * 关联的副本数据
     */
    private int                   dungeonDataID;

    public DungeonTransmit(int _npcID, int _dungeonDataID)
    {
        super(_npcID);
        // TODO Auto-generated constructor stub
        dungeonDataID = _dungeonDataID;
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.DUNGEON_TRANSMIT;
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

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        if (_player.getGroupID() <= 0)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning("在队伍中才可以进入"));

            return;
        }

        DungeonDataModel dungeonData = DungeonDataModelDictionary.getInsatnce()
                .get(dungeonDataID);

        if (null != dungeonData)
        {
            if (0 == selectIndex)
            {
                if (_player.getLevel() < dungeonData.level)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning("你的等级不够"));

                    return;
                }
            }
            else
            {
            	int heroDungeon = dungeonData.level 
            		+ DungeonServiceImpl.getInstance().getConfig().difficult_addition_level;
                if (_player.getLevel() < heroDungeon)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning("你的等级不够"));

                    return;
                }
            }

            Group group = GroupServiceImpl.getInstance().getGroup(
                    _player.getGroupID());

            if (null != group)
            {
                Dungeon dungeon;

                if (0 == selectIndex)
                {
                    dungeon = DungeonServiceImpl.getInstance()
                            .tryEnterDungeon(_player, dungeonData,
                                    Dungeon.PATTERN_OF_EASY, group.getID(),
                                    group.getLeader().player.getUserID());
                }
                else
                {
                    dungeon = DungeonServiceImpl.getInstance()
                            .tryEnterDungeon(_player, dungeonData,
                                    Dungeon.PATTERN_OF_DIFFICULT,
                                    group.getID(),
                                    group.getLeader().player.getUserID());
                }

                if (null != dungeon)
                {
                    if (dungeon.isInFightingBoss())
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning("首领战中无法进入"));

                        return;
                    }

                    Map entranceMap = dungeon.getEntranceMap();

                    _player.setCellX(entranceMap.getBornX());
                    _player.setCellY(entranceMap.getBornY());

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ResponseMapBottomData(_player, entranceMap,
                                    _player.where()));

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ResponseMapGameObjectList(_player
                                    .getLoginInfo().clientType, entranceMap));

                    _player.gotoMap(entranceMap);
                    //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                    EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);
                    DungeonServiceImpl.getInstance().playerEnterDungeon(
                            _player.getUserID(), dungeon);

                    Npc escortNpc = _player.getEscortTarget();

                    if (null != escortNpc)
                    {
                        TaskServiceImpl.getInstance().endEscortNpcTask(_player,
                                escortNpc);
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning("队伍信息错误"));
            }
        }
        else
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning("副本数据错误"));
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        return optionList;
    }
}
