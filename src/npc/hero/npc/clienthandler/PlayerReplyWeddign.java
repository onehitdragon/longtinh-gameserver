package hero.npc.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.dictionary.GoodsContents;
import hero.item.special.MarryRing;
import hero.npc.function.system.MarryGoods;
import hero.npc.function.system.MarryNPC;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-11
 * Time: 19:33:42
 * 玩家回复是否同意 求婚 1，结婚 2
 * 0x2d06
 */
public class PlayerReplyWeddign extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        int askUserID = yis.readInt();
        int replyUserID = yis.readInt();
        byte type = yis.readByte(); //求婚 1，结婚 2 ,协议离婚 3
        byte result = yis.readByte(); //回复的结果，0不同意，1同意

        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(askUserID);
        if(result == 0){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
            		new Warning("对方不同意！", Warning.UI_STRING_TIP));
        }else{
            HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByUserID(replyUserID);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
            		new Warning("恭喜你，对方同意了！", Warning.UI_STRING_TIP));
            if(type == 1){
                /*MarryRing ring = (MarryRing) GoodsContents.getGoods(MarryGoods.RANG.getId());
                if(ring != null){
//                    ring.setCanUse(true);
//                    ring.setOtherName(otherPlayer.getName());
                    if(ring.beUse(player,null,-1)){
                        if (ring.disappearImmediatelyAfterUse())
                            {
                                ring.remove(player, (short)-1);
                            }
                    }

                }*/
                MarryNPC.propose(player,otherPlayer);
            }
            if(type == 2){
                MarryNPC.married(player,otherPlayer.getName());
            }

            if(type == 3){
                MarryNPC.divorce(player,otherPlayer,(byte)0);
            }

        }
    }
}
