package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.chat.service.WorldHornService;
import hero.item.SpecialGoods;
import hero.item.dictionary.GoodsContents;
import hero.item.special.MassHorn;
import hero.item.special.SingleSpecialGoodsIDDefine;
import hero.item.special.WorldHorn;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 CompleteHornInput.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-30 上午09:57:51
 * @描述 ：完成世界号角或集结号角内容输入
 */

public class CompleteHornInput extends AbsClientProcess
{

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        byte hornLocation = yis.readByte();
        String content = yis.readUTF();

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        int[] ginfo = player.getInventory().getSpecialGoodsBag().getItemData(hornLocation);
        int hornType = 1;
        if(ginfo[0] == SingleSpecialGoodsIDDefine.WORLD_HORN){
            ((WorldHorn) GoodsContents
                    .getGoods(SingleSpecialGoodsIDDefine.WORLD_HORN)).remove(
                    player, hornLocation);
        }
        if(ginfo[0] == SingleSpecialGoodsIDDefine.MASS_HORN){
        	((MassHorn) GoodsContents
                    .getGoods(SingleSpecialGoodsIDDefine.MASS_HORN)).remove(
                    player, hornLocation);
        	hornType = 2;
        }
        WorldHornService.getInstance().put(player.getName(), content,hornType);
    }

}
