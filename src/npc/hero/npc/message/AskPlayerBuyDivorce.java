package hero.npc.message;


import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-20
 * Time: 下午1:41
 * 询问玩家购买 离婚证明
 * 0x636
 */
public class AskPlayerBuyDivorce extends AbsResponseMessage{

    private byte type;  // 1:协议离婚证书   2:强制离婚证书
    private String content;

    public AskPlayerBuyDivorce(byte _type,String _content) {
        this.content = _content;
        this.type = _type;
    }

    @Override
    protected void write() throws IOException {
        yos.writeByte(type);
        yos.writeUTF(content);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
