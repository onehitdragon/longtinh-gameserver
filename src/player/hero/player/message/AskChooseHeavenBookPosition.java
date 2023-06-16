package hero.player.message;

import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-26
 * Time: 下午2:45
 * 询问要镶嵌天书的位置
 * 0x420
 */
public class AskChooseHeavenBookPosition extends AbsResponseMessage{
    private HeroPlayer player;
    private int bookID;

    public AskChooseHeavenBookPosition(HeroPlayer _player, int bookID) {
        this.player = _player;
        this.bookID = bookID;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(bookID);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
