package hero.player.clienthandler;

import hero.item.SpecialGoods;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.ESpecialGoodsType;
import hero.item.special.HeavenBook;
import hero.player.HeroPlayer;
import hero.player.message.ResponseHeavenBookList;
import hero.player.service.PlayerConfig;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.util.List;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-26
 * Time: 下午2:54
 * 请求镶嵌天书
 * 0x418
 */
public class RequestInlayHeavenBook extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

        int bookID = yis.readInt();
        byte position = yis.readByte();

        if(bookID == 0){ //要选择天书
            List<HeavenBook> heavenBookList = GoodsServiceImpl.getInstance().getPlayerSepcialBagHeavenBooks(player);
            if(heavenBookList == null || heavenBookList.size()==0){
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_NOT_HEAVEN_BOOK,
                        Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                return;
            }
            ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseHeavenBookList(heavenBookList));
        }else{
            int booknum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(bookID);
            if(booknum == 0){
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("没有找到这本天书"));
            }else{
                int currPositionBookID = player.heaven_book_ids[position];
                if(currPositionBookID > 0){
                    player.currInlayHeavenBookID = bookID;
                    player.currInlayHeavenBookPosition = position;
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_OVERRIDE_HEAVEN_BOOK,
                            Warning.UI_TOOLTIP_CONFIM_CANCEL_TIP, PlayerServiceImpl.REPLY_INLAY_HEAVEN_BOOK_COMMAND_CODE));
                }else{
                    PlayerServiceImpl.getInstance().startInlayHeavenBook(player,position,bookID);
                }

            }

        }

    }
}
