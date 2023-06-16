package hero.group.message;

import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-12-10
 * Time: 20:35:52
 * 队伍成员HP、MP值通知
 * 0x1f08
 */
public class OthersHpMpNotify extends AbsResponseMessage {
    private HeroPlayer player;

    public OthersHpMpNotify(HeroPlayer player) {
        this.player = player;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(player.getUserID());
        yos.writeInt(player.getHp());
        yos.writeInt(player.getBaseProperty().getHpMax());
        yos.writeInt(player.getMp());
        yos.writeInt(player.getBaseProperty().getMpMax());
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
