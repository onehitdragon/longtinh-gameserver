package hero.npc.function.system;

import hero.dungeon.Dungeon;
import hero.dungeon.service.DungeonServiceImpl;
import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.InviteAttendWedding;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.social.ESocialRelationType;
import hero.social.SocialObjectProxy;
import hero.social.service.SocialServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-17
 * Time: 下午8:06
 *
 * 婚礼司仪(在婚礼副本地图里)
 *
 * 本类代替 WeddingNPC.java
 * 因为不想在原来的代码上改了，就写了一个新的
 *
 */
public class WedEmceeNPC extends BaseNpcFunction {

    private static Logger log = Logger.getLogger(WedEmceeNPC.class);
    /**
     * 顶层操作菜单列表
     */
    private static final String[] mainMenuList            = {"邀请玩家" };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]  mainMenuMarkImageIDList = {1008 };


    public WedEmceeNPC(int npcID){
        super(npcID);
    }

    @Override
    public void process(HeroPlayer _player, byte _step, int _topSelectIndex, YOYOInputStream _content) throws Exception {
        List<SocialObjectProxy> friends = SocialServiceImpl.getInstance()
                .getSocialRelationList(_player.getUserID(), ESocialRelationType.FRIEND);
        if(null != friends){
            log.debug("WedEmcee NPC , player["+_player.getName()+"] friends size = " + friends.size());
            Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(_player.getUserID());
            log.debug("wedemcee player dungeon history id = " + dungeon.getHistoryID());
            for(SocialObjectProxy sop : friends){
                log.debug("friend ["+sop.name+"] isOnline="+sop.isOnline);
                if(!sop.name.equals(_player.spouse) && sop.isOnline){
                    HeroPlayer friend = PlayerServiceImpl.getInstance().getPlayerByUserID(sop.userID);
                    ResponseMessageQueue.getInstance().put(friend.getMsgQueueIndex(),new InviteAttendWedding(_player,dungeon.getEntranceMap(),dungeon.getHistoryID()));
                }
            }
        }

    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.WEDDING;
    }

    @Override
    public void initTopLayerOptionList() {
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
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(HeroPlayer _player) {
        return optionList;
    }
}
