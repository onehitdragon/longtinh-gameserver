package hero.gather.clientHandler;

import hero.gather.Gather;
import hero.gather.dict.Refined;
import hero.gather.dict.RefinedDict;
import hero.gather.message.RefinedListMessage;
import hero.gather.message.RefinedNeedGoodsMessage;
import hero.gather.service.GatherServerImpl;
import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.message.UpgradeSkillPoint;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.FullScreenTip;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.io.IOException;
import java.util.Random;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * 炼制列表上行 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class RefinedClientHandler extends AbsClientProcess
{
    private static final String NOT_GATHER_SKILL = "你还没有学习采集技能";

    private static final String SUCCESS          = "制作成功";

    private static final String FAIL             = "制作失败";

    private static final String EXPECTED         = "意外的成功";

    private static final String LVL              = "葫芦等级不够";

    private static final String SOUL_ENOUGH      = "收集的灵魂数不够";

    private static final String BOX_NOT_ENOUGH   = "材料背包已满";

    public void read () throws Exception
    {
        try
        {
            byte _type = yis.readByte();
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            if (player == null) return;
            Gather gather = GatherServerImpl.getInstance().getGatherByUserID(
                    player.getUserID());
            if (gather == null)
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning(NOT_GATHER_SKILL));
                return;
            }
            if (_type == 0)
            {
                // 查看炼化列表
                RefinedListMessage msg = new RefinedListMessage(
                        Tip.GATHER_LEVEL_TITLE[gather.getLvl() - 1],
                        gather);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
            }
            else
            {
                int _refinedID = yis.readInt();
                Refined _refined = RefinedDict.getInstance().getRefinedByID(
                        _refinedID);
                if (_refined == null) { return; }
                if (_type == 1)
                {
                    if (player.getInventory().getMaterialBag()
                            .getEmptyGridNumber() > 0)
                    {
                        // 炼化
                        if (canRefined(gather, _refined, player))
                        {
                            // 是否有需要的葫芦
                            if (hasGourd(player, _refined))
                            {
                                // 计算生成的物品
                                int random = getRandom();
                                int goodsID = _refined.getGoodsID[random];
                                short num = _refined.getGoodsNum[random];
                                // 移除物品
                                removeGoods(player, gather, _refined);

                                if (random == 0)
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(FAIL));

                                    return;
                                }
                                else if (random == 1)
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(SUCCESS));
                                }
                                else
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning(EXPECTED));

                                    if (_refined.abruptID > 0)
                                    {
                                        Refined abrupt = RefinedDict
                                                .getInstance().getRefinedByID(
                                                        _refined.abruptID);
                                        if (abrupt != null)
                                        {
                                            random = new Random().nextInt(100);
                                            if (random < 10)
                                            {
                                                GatherServerImpl
                                                        .getInstance()
                                                        .addRefinedItem(
                                                                player,
                                                                abrupt,
                                                                GetTypeOfSkillItem.COMPREHEND);
                                            }
                                        }
                                    }
                                }
                                // 添加物品
                                GoodsServiceImpl
                                        .getInstance()
                                        .addGoods2Package(
                                                player,
                                                GoodsContents
                                                        .getGoods(_refined.getGoodsID[1]),
                                                num, CauseLog.REFINED);
                                // 添加技能点
                                if (_refined.needLvl == gather.getLvl())
                                {
                                    GatherServerImpl.getInstance().addPoint(
                                            player.getUserID(), gather,
                                            _refined.point);
                                    ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new UpgradeSkillPoint(gather
                                                    .getPoint()));
                                }
                            }
                            else
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(LVL));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    player.getMsgQueueIndex(),
                                    new Warning(SOUL_ENOUGH));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(BOX_NOT_ENOUGH));
                    }
                }
                else if (_type == 2)
                {
                    // 查看需要的材料
                    RefinedNeedGoodsMessage msg = new RefinedNeedGoodsMessage(
                            _refinedID, _refined.desc, gather);
                    for (int i = 0; i < _refined.needSoulID.length; i++)
                    {
                        if (_refined.needSoulID[i] > 0)
                            msg.addNeedSoul(_refined.needSoulID[i],
                                    _refined.needSoulNum[i]);
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                }
            }
        }
        catch (IOException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
    }

    private int getRandom ()
    {
        int _random = new Random().nextInt(100);
        if (_random < 10) return 0;
        if (_random < 80) return 1;
        return 2;
    }

    /**
     * 移除背包中制造原材料
     * 
     * @param _player
     * @param _refined
     */
    private void removeGoods (HeroPlayer _player, Gather _gather,
            Refined _refined)
    {
        for (int i = 0; i < _refined.needSoulID.length; i++)
        {
            if (_refined.needSoulID[i] > 0)
            {
                _gather.releaseMonsterSoul(_refined.needSoulID[i],
                        _refined.needSoulNum[i]);
            }
        }
    }

    private boolean hasGourd (HeroPlayer _player, Refined _refined)
    {
        return true;
    }

    /**
     * 判断玩家是否有空间存放制造的物品
     * 
     * @param _player
     * @param _refined
     * @return
     */
    private static boolean hasPackage (HeroPlayer _player, Refined _refined)
    {
        Goods goods = GoodsContents.getGoods(_refined.getGoodsID[1]);

        if (goods != null)
        {
            EGoodsType type = goods.getGoodsType();
            if (type == EGoodsType.EQUIPMENT)
            {
                if (_player.getInventory().getEquipmentBag()
                        .getEmptyGridNumber() < 1) return false;
            }
            else if (type == EGoodsType.MATERIAL)
            {

            }
            else if (type == EGoodsType.MEDICAMENT)
            {
                if (_player.getInventory().getMedicamentBag()
                        .getEmptyGridNumber() < 1) return false;
            }
            else if (type == EGoodsType.SPECIAL_GOODS)
            {
                if (_player.getInventory().getSpecialGoodsBag()
                        .getEmptyGridNumber() < 1) return false;
            }
            else if (type == EGoodsType.TASK_TOOL)
            {
                if (_player.getInventory().getTaskToolBag()
                        .getEmptyGridNumber() < 1) return false;
            }
        }
        return true;
    }

    /**
     * 是否有足够的灵魂炼化
     * 
     * @param _gather
     * @param _refined
     * @param _player
     * @return
     */
    private static boolean canRefined (Gather _gather, Refined _refined,
            HeroPlayer _player)
    {
        for (int i = 0; i < _refined.needSoulID.length; i++)
        {
            if (_refined.needSoulID[i] > 0)
            {
                boolean flag = _gather.enough(_refined.needSoulID[i],
                        _refined.needSoulNum[i]);
                if (!flag) return false;
            }
        }
        return true;
    }
}
