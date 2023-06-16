package hero.item.special;

import static hero.item.message.SoulGoodsConfirm.TYPE_OF_CHANNEL;
import static hero.item.message.SoulGoodsConfirm.TYPE_OF_MARK;
import yoyo.core.queue.ResponseMessageQueue;
import hero.effect.service.EffectServiceImpl;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapServiceImpl;
import hero.npc.Npc;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.task.service.TaskServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialGoodsService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-29 上午11:31:42
 * @描述 ：
 */

public class SpecialGoodsService
{
    /**
     * 世界聊天号角
     */
    private static WorldHorn worldHorn = (WorldHorn) GoodsContents
                                               .getGoods(SingleSpecialGoodsIDDefine.WORLD_HORN);

    /**
     * 构造
     */
    private SpecialGoodsService()
    {
    }

    /**
     * 世界聊天
     * 
     * @return
     */
    public static boolean chatInWorld (HeroPlayer _player)
    {
        int firstGoodsGridIndex = _player.getInventory().getSpecialGoodsBag()
                .getFirstGridIndex(worldHorn.getID());

        if (firstGoodsGridIndex >= 0)
        {
            try
            {
                GoodsServiceImpl.getInstance().deleteOne(_player,
                        _player.getInventory().getSpecialGoodsBag(),
                        worldHorn.getID(), CauseLog.WORLDCHAT);

                return true;
            }
            catch (BagException e)
            {

            }
        }
        else
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_ITEM_OF_NONE_HORN, Warning.UI_STRING_TIP));
        }

        return false;
    }

    /**
     * 操作灵魂物品
     * 
     * @param _operationType
     * @param _player
     */
    public static void operateSoulGoods (HeroPlayer _player,
            byte _operationType, byte _locationOfBag)
    {
        try
        {
            switch (_operationType)
            {
                case TYPE_OF_MARK:
                {
                    _player.setHomeID(_player.where().getID());
                    GoodsDAO.updateHome(_player.getUserID(), _player.where()
                            .getID());
                    ((SoulMark) GoodsContents
                            .getGoods(SingleSpecialGoodsIDDefine.SOUL_MARK))
                            .remove(_player, _locationOfBag);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_ITEM_OF_GET_TIIME, Warning.UI_STRING_TIP));

                    break;
                }
                case TYPE_OF_CHANNEL:
                {
                    ((SoulChannel) GoodsContents
                            .getGoods(SingleSpecialGoodsIDDefine.SOUL_CHANNEL))
                            .remove(_player, _locationOfBag);

                    Map targetMap = MapServiceImpl.getInstance()
                            .getNormalMapByID(_player.getHomeID());
                    _player.setCellX(targetMap.getBornX());
                    _player.setCellY(targetMap.getBornY());

                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ResponseMapBottomData(_player, targetMap,
                                    _player.where()));
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ResponseMapGameObjectList(_player
                                    .getLoginInfo().clientType, targetMap));

                    _player.gotoMap(targetMap);
                    //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                    EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                    Npc escortNpc = _player.getEscortTarget();

                    if (null != escortNpc)
                    {
                        TaskServiceImpl.getInstance().endEscortNpcTask(_player,
                                escortNpc);
                    }

                    break;
                }
            }
        }
        catch (BagException e)
        {

        }
    }




}
