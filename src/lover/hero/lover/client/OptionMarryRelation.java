package hero.lover.client;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.group.service.GroupServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.item.special.SpouseTransport;
import hero.lover.service.LoverServiceImpl;
import hero.npc.function.system.MarryGoods;
import hero.npc.function.system.MarryNPC;
import hero.npc.message.AskPlayerAgreeWedding;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-2-11
 * Time: 上午10:43
 * 0x5a01
 */
public class OptionMarryRelation extends AbsClientProcess{
    private static final byte SHOW = 1;//进入伴侣界面
    private static final byte DIVORCE = 2;//离婚
    private static final byte FORCE_DIVORCE = 3;//强制离婚
    private static final byte TRANSPORT = 4; //传送到对方身边
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte type = yis.readByte();
        switch (type){
            case SHOW:
                LoverServiceImpl.getInstance().showMarryRelation(player);
                break;
            case DIVORCE:
                String othername = player.spouse;
                HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(othername);
                if(otherMarryPlayer != null){
                    String myLover = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                    if(myLover.equals(othername)){
//                        MarryNPC.divorce(player, otherMarryPlayer, (byte)0);
                        if(player.getGroupID() > 0){
                                if(otherMarryPlayer.getGroupID() > 0 && player.getGroupID() == otherMarryPlayer.getGroupID()){
                                     if(GroupServiceImpl.getInstance().getGroup(player.getGroupID()).getMemberNumber() == 2){
                                         ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(),
                                                        new AskPlayerAgreeWedding(player,otherMarryPlayer,player.getName()+"要和你离婚，\n你同意吗？",(byte)3));
                                     }else{
                                          ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning("夫妻队伍里必须只有双方两个人！"));
                                     }
                                }else{
                                     ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning("夫妻双方必须要单独组成一个队伍！"));
                                }
                            }else{
                                ResponseMessageQueue.getInstance().put(
                                            player.getMsgQueueIndex(),
                                            new Warning("夫妻双方必须要单独组成一个队伍！"));
                            }
                    }else{
                        ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(othername + "和你不是夫妻！"));
                    }
                }else{
                    ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("对方不在线！"));
                }
                break;
            case FORCE_DIVORCE:
                othername = player.spouse;
                otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(othername);
                if(otherMarryPlayer == null){
                    otherMarryPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(othername);
                    otherMarryPlayer = PlayerServiceImpl.getInstance().load(otherMarryPlayer.getUserID());
                }
                if(otherMarryPlayer != null){
                    String myLover = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                    if(myLover.equals(othername)){
                        int num = player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
                        if(num == 0){

                            //没有离婚证明，提示发起者购买离婚证明
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("你没有强制离婚证明，是否去商城购买！",Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                            return;

                        }
                        MarryNPC.divorce(player, otherMarryPlayer, (byte)1);
                    }else{
                        ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(othername + "和你不是夫妻！"));
                    }
                }else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("找不到对方玩家，强制离婚失败"));
                }
                break;
            case TRANSPORT:
                int goodsID = MarryGoods.TRANSPORT.getId();
                int rangnum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID);
                if(rangnum == 0){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("需要\"夫妻传送符\"才可以传送"));
                }else {
                    SpouseTransport stgoods = (SpouseTransport) GoodsContents.getGoods(goodsID);
                    if(stgoods != null){
                        if(stgoods.beUse(player,null,-1)){
                            if (stgoods.disappearImmediatelyAfterUse())
                            {
                                stgoods.remove(player, (short)-1);
                            }
                        }
                    }
                }
                break;
        }
    }
}
