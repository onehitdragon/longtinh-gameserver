package hero.micro.teach.message;

import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-17
 * Time: 下午12:56
 * 如果客户端接收到这个报文，则把确认框清除掉
 *
 * 0x6024
 */
public class ClearConfirmDialog extends AbsResponseMessage{

    public ClearConfirmDialog() {
    }

    @Override
    protected void write() throws IOException {
        yos.writeByte(0);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
