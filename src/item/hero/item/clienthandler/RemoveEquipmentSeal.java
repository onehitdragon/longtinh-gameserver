package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.service.GoodsServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.message.RemoveEquipmentSealNotify;
import hero.item.service.GoodsDAO;
import hero.item.special.SealPray;
import hero.log.service.CauseLog;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RemoveEquipmentSeal.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-27 下午03:15:54
 * @描述 ：解除封印
 */

public class RemoveEquipmentSeal extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        byte gridIndex = yis.readByte();
        int equipmentInsID = yis.readInt();

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        EquipmentInstance ei = player.getInventory().getEquipmentBag().get(
                gridIndex);

        if (null != ei && ei.getInstanceID() == equipmentInsID)
        {
            if (ei.existSeal())
            {
                int sealPrayID = SealPray.getValidatePrayID(ei.getArchetype()
                        .getNeedLevel());
                int firstSealPrayGridIndex = player.getInventory()
                        .getSpecialGoodsBag().getFirstGridIndex(sealPrayID);

                if (firstSealPrayGridIndex >= 0)
                {
                    ei.setSeal(false);
                    GoodsServiceImpl.getInstance().deleteOne(player,
                            player.getInventory().getSpecialGoodsBag(),
                            sealPrayID, CauseLog.REMOVESEAL);
                    GoodsDAO.removeEquipmentSeal(ei);
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new RemoveEquipmentSealNotify(gridIndex,
                                    equipmentInsID));
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_ITEM_NONE_SEAL_PRAY, Warning.UI_STRING_TIP));
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_ITEM_NONE_SEAL, Warning.UI_STRING_TIP));
            }
        }
    }
}
