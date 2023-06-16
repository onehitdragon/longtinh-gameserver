package hero.npc.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.item.SpecialGoods;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.Divorce;
import hero.log.service.CauseLog;
import hero.npc.function.system.MarryGoods;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-20
 * Time: 下午3:06
 * 回复是否同意购买离婚证书
 * 0x609
 */
public class ReplyPlayerBuyDivorce extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        byte type = yis.readByte(); //回复类型 1:协议离婚证书  2:强制离婚证书
        byte reply = yis.readByte(); //是否同意 0:不同意  1:同意
        if(reply == 1){
            // todo 去商城购买
            SpecialGoods divorce = null;
            String desc = "";
            int price = 0;
            if(type == 2){
                divorce = (SpecialGoods)GoodsContents.getGoods(MarryGoods.FORCE_DIVORCE.getId());
                price = divorce.getSellPrice();
                desc = "买强制离婚证明";
            }
            if(type == 1){
                divorce = (SpecialGoods)GoodsContents.getGoods(MarryGoods.DIVORCE.getId());
                price = divorce.getSellPrice();
                desc = "买离婚协议";
            }
            GoodsServiceImpl.getInstance().addGoods2Package(player,divorce,1, CauseLog.BUY);
            PlayerServiceImpl.getInstance().addMoney(player,price,1,
                        PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,desc);
        }
    }
}
