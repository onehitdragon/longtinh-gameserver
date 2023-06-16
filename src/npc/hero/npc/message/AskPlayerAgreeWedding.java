package hero.npc.message;

import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-11
 * Time: 19:21:30
 * 询问玩家 otherPlayer 是否同意订婚、结婚
 * 0x2d34
 */
public class AskPlayerAgreeWedding extends AbsResponseMessage {
    private HeroPlayer player;
    private HeroPlayer otherPlayer;
    private String content;
    private byte type;  //订婚 1，结婚 2，离婚 3

    public AskPlayerAgreeWedding(HeroPlayer player, HeroPlayer otherPlayer, String content, byte type){
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.content = content;
        this.type = type;
    }
    @Override
    protected void write() throws IOException {
        yos.writeInt(player.getUserID());
        yos.writeInt(otherPlayer.getUserID());
        yos.writeByte(type);
        yos.writeUTF(content);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
