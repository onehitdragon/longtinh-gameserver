package hero.npc.message;

import hero.map.Map;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-17
 * Time: 下午8:30
 * 邀请玩家参加婚礼
 * 0x2d35
 */
public class InviteAttendWedding extends AbsResponseMessage{

    private HeroPlayer inviter;//邀请者
    private Map marryMap; //婚礼地图
    private int dungeonID;//副本ID，(实例化副本时，生成的historyID)

    public InviteAttendWedding(HeroPlayer _inviter, Map _map, int _dungeonID){
        this.inviter = _inviter;
        this.marryMap = _map;
        this.dungeonID = _dungeonID;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(dungeonID);
        yos.writeShort(marryMap.getID());
        yos.writeUTF(inviter.getName()+" 邀请您参加他的婚礼，\n您要去吗？");
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
