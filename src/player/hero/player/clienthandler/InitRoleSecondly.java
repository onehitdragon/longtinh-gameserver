package hero.player.clienthandler;

import hero.charge.message.ExperienceBookTraceTime;

import hero.charge.message.PointAmountNotify;
import hero.effect.Effect;
import hero.effect.Effect.EKeepTimeType;
import hero.effect.Effect.EffectTrait;
import hero.effect.detail.StaticEffect;
import hero.effect.message.AddEffectNotify;
import hero.effect.service.EffectServiceImpl;
import hero.fight.service.FightServiceImpl;
import hero.gather.message.GatherSkillNotify;
import hero.gather.message.GourdNotify;
import hero.gather.service.GatherServerImpl;
import hero.group.service.GroupServiceImpl;
import hero.guild.service.GuildServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.bag.Inventory;
import hero.item.message.SendBagSize;
import hero.item.service.GoodsServiceImpl;
import hero.manufacture.Manufacture;
import hero.manufacture.message.ManufNotify;
import hero.manufacture.service.ManufactureServerImpl;
import hero.map.Map;
import hero.map.message.ResponseAnimalInfoList;
import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseMapDecorateData;
import hero.map.message.ResponseMapElementList;
import hero.map.message.ResponsePetInfoList;
import hero.map.message.ResponseSceneElement;
import hero.skill.message.LearnedSkillListNotify;
import hero.npc.function.system.postbox.MailService;
import hero.player.HeroPlayer;
import hero.player.message.HotKeySumByMedicament;
import hero.player.message.ShortcutKeyListNotify;
import hero.player.service.PlayerServiceImpl;
import hero.share.ME2GameObject;
import hero.share.letter.LetterService;
import hero.share.message.MailStatusChanges;
import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;
import hero.task.service.TaskServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import java.util.List;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 InitRoleSecondly.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-22 上午09:43:29
 * @描述 ：对非基本信息的初始化通知
 */

public class InitRoleSecondly extends AbsClientProcess
{
     private static Logger log = Logger.getLogger(InitRoleSecondly.class);
    public void read () throws Exception
    {
    	log.info("@@@@@@@@ InitRoleSecondly ..............");
        // TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        TaskServiceImpl.getInstance().sendPlayerTaskList(player);

        EquipmentInstance weapon = player.getBodyWear().getWeapon();
        GuildServiceImpl.getInstance().sendGuildRank(player);

        Map where = player.where();

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseSceneElement(player.getLoginInfo().clientType,
                        where));
        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseMapElementList(player.getLoginInfo().clientType,
                        where));

//        OutMsgQ.getInstance().put(
//                player.getMsgQueueIndex(),
//                new ResponseMapMiniImage(player.getLoginInfo().clientType,
//                        where.getMiniImageID(), MiniMapImageDict.getInstance()
//                                .getImageBytes(where.getMiniImageID())));
        //ResponseAreaImage

        //add:	zhengl
        //date:	2010-11-03
        //note:	添加装饰层数据
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
        		new ResponseMapDecorateData(where, player.getLoginInfo().clientType));
        //end

        if (where.getAnimalList().size() > 0)
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResponseAnimalInfoList(where));
            
        }

        if (where.getBoxList().size() > 0)
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResponseBoxList(where.getBoxList()));
        }

        if (LetterService.getInstance()
                .existsUnreadedLetter(player.getUserID()))
        {
            ResponseMessageQueue.getInstance().put(
                    player.getMsgQueueIndex(),
                    new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER,
                            true));
        }

        //edit by zhengl; date: 2011-02-18; note: 获得未读邮件
        if (MailService.getInstance().getUnreadMailNumber(player.getUserID()) > 0)
        {
            ResponseMessageQueue.getInstance().put(
                    player.getMsgQueueIndex(),
                    new MailStatusChanges(MailStatusChanges.TYPE_OF_POST_BOX,
                            true));
        }

        GoodsServiceImpl.getInstance().sendLegacyBoxList(where, player);
        TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player, where);
        TaskServiceImpl.getInstance().notifyMapGearOperateMark(player, where);
        TaskServiceImpl.getInstance().notifyGroundTaskGoodsOperateMark(player,
                where);

        if (null != GatherServerImpl.getInstance().getGatherByUserID(
                player.getUserID()))
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new GatherSkillNotify(true));
            int gourdID = GatherServerImpl.getInstance().getGourdID(player);

            if (gourdID > 0)
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new GourdNotify(true));
        }

        Inventory inventory = player.getInventory();

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new SendBagSize(inventory.getEquipmentBag()
                                    .getSize(), inventory.getMedicamentBag()
                                    .getSize(), inventory.getMaterialBag()
                                    .getSize(), inventory.getSpecialGoodsBag()
                                    .getSize(),inventory.getPetEquipmentBag().getSize(),
                                    inventory.getPetContainer().getSize(),
                                    inventory.getPetGoodsBag().getSize()));

        List<Manufacture> _manufList = ManufactureServerImpl.getInstance()
                .getManufactureListByUserID(player.getUserID());

        if (null != _manufList)
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ManufNotify(_manufList));
        }

        GroupServiceImpl.getInstance().login(player);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new LearnedSkillListNotify(player));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                new ShortcutKeyListNotify(player));
        //add by zhengl; date: 2011-03-07; note: 快捷键上绑定的消耗品的数量下发
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new HotKeySumByMedicament(player));
//        OutMsgQ.getInstance().put(
//                player.getMsgQueueIndex(),
//                new ExperienceBookTraceTime(
//                        player.getChargeInfo().offLineTimeTotal, player
//                                .getChargeInfo().expBookTimeTotal, player
//                                .getChargeInfo().huntBookTimeTotal));
        if (player.getChargeInfo().huntBookTimeTotal > 0) 
        {
            StaticEffect sef = new StaticEffect(1, "双倍经验");
            sef.desc = "双倍经验";
            sef.releaser = (ME2GameObject)player;
            sef.trait = EffectTrait.BUFF;
            sef.keepTimeType = EKeepTimeType.LIMITED;
            sef.traceTime = (short)(player.getChargeInfo().huntBookTimeTotal/1000);
            sef.iconID = 
            	GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_icon;
            sef.viewType = 0;
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            		new AddEffectNotify(player, (Effect)sef));
		}

        if (player.getChargeInfo().pointAmount > 0)
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new PointAmountNotify(player.getChargeInfo().pointAmount));
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetInfoList(player));
        
        player.init();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new NotifyPlayerReciveRepeateTaskTimes(player));//玩家接收的循环任务次数

    }
}
