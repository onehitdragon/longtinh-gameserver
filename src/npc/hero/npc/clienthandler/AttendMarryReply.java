package hero.npc.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.dungeon.service.DungeonServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-17
 * Time: 下午8:52
 * 玩家回复是否受邀请参加婚礼
 * 0x2d07
 */
public class AttendMarryReply extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);

        int dungeonID = yis.readInt(); //举行婚礼的副本ID(实例化副本时生成的historyID)
        short mapID = yis.readShort(); //婚礼礼堂地图ID
        byte reply = yis.readByte(); //是否同意，1:同意　　0:不同意

        if(reply == 1){
            // 跳转到婚礼礼堂地图副本
            DungeonServiceImpl.getInstance().gotoMarryDungeonMap(dungeonID,player,mapID);
        }
    }
}
