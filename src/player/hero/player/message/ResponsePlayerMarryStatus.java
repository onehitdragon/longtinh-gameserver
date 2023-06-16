package hero.player.message;

import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-17
 * Time: 14:46:24
 * 刷新玩家婚姻状态
 * 0x416
 */
public class ResponsePlayerMarryStatus extends AbsResponseMessage {

    private HeroPlayer player;

    public ResponsePlayerMarryStatus(HeroPlayer _player){
        this.player = _player;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(player.getUserID());
        yos.writeUTF(player.spouse);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
