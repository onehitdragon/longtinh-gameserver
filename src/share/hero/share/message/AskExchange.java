package hero.share.message;

import hero.micro.teach.TeachService;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-12-9
 * Time: 17:01:42
 * 问玩家是否同意交易
 * 0x3015
 */
public class AskExchange extends AbsResponseMessage {
    private HeroPlayer player;
    private HeroPlayer other;

    public AskExchange(HeroPlayer player, HeroPlayer other) {
        this.player = player;
        this.other = other;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(player.getUserID());
        yos.writeInt(other.getUserID());
        yos.writeUTF(player.getName() + " 请求和你交易，是否同意？");

//        TeachService.waitingReply(other);//开始等待回复计时
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
